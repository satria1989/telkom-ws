/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ListKorektifTicketModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class KorektifProcessOpenRunning extends DefaultApplicationPlugin {

    String pluginName = "Telkom New OSS - Korektif Open.Running";
    LogInfo logInfo = new LogInfo();

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "7.0.0";
    }

    @Override
    public String getDescription() {
        return pluginName;
    }

    @Override
    public String getLabel() {
        return pluginName;
    }

    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return null;
    }

    @Override
    public Object execute(Map map) {

        try {
            ApplicationContext ac = AppUtil.getApplicationContext();
            WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
            String processDefId = "ticketIncidentService:latest:flowIncidentTicket";
            int count = 0;
            List<ListKorektifTicketModel> listTicket = getTicketOpenRunning();
            if (!listTicket.isEmpty()) {
                for (ListKorektifTicketModel ticket : listTicket) {
                    workflowManager.processAbort(ticket.getProcessId());
                    UpdateStatus(ticket.getStatus(), ticket.getTicketStatus(), "EDOTENSEI", ticket.getParentId());
                    WorkflowProcessResult startProcess = workflowManager.processStartWithLinking(processDefId, null, "Automation", ticket.getParentId());
                    count++;
                    if (count == 20) {
                        count = 0;
                        TimeUnit.SECONDS.sleep(10);
                    }
                }
            }

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        }

        return null;
    }

    public List<ListKorektifTicketModel> getTicketOpenRunning() throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;

        List<ListKorektifTicketModel> r = new ArrayList<>();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();

        query.append(" select c.id, c.c_id_ticket, c.c_parent_id, c.c_status, c.c_ticket_status, d.PROCESSID, ")
                .append(" e.id as activity_id, e.ACTIVITYDEFINITIONID as activity_name, e.PDEFNAME as process_def_id, e.STATE ")
                .append(" from app_fd_ticket c ")
                .append(" JOIN wf_process_link d ON c.c_parent_id = d.originProcessId ")
                .append(" join SHKACTIVITIES e on e.PROCESSID = d.PROCESSID ")
                .append(" WHERE e.STATE = '1000001' ")
                .append(" and round((SYSDATE -(FROM_TZ( CAST(DATE '1970-01-01' + (1/24/60/60/1000) * e.ACTIVATED AS TIMESTAMP ), 'Asia/Jakarta') + 7/24)) * 24 * 60) > 3 ");
//                .append(" and rownum <= 20 ");

        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());

            result = ps.executeQuery();
            ListKorektifTicketModel tc = null;
            while (result.next()) {
                tc = new ListKorektifTicketModel();
                tc.setIdTableTicket(result.getString("id"));
                tc.setIdTicket(result.getString("c_id_ticket"));
                tc.setParentId(result.getString("c_parent_id"));
                tc.setStatus(result.getString("c_status"));
                tc.setTicketStatus(result.getString("c_ticket_status"));
                tc.setProcessId(result.getString("PROCESSID"));
                tc.setActivityId(result.getString("activity_id"));
                tc.setActivityName(result.getString("activity_name"));
                tc.setProcessDefId(result.getString("process_def_id"));
                tc.setState(result.getString("STATE"));

                r.add(tc);
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            try {
                if (result != null) {
                    result.close();
                }
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }

        }
        return r;
    }

    private void UpdateStatus(String status, String ticketStatus, String saveStatus, String originProcessId) throws SQLException, Exception {

        boolean result = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        StringBuilder query;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try {
            con = ds.getConnection();
            query = new StringBuilder();
            query
                    .append(" UPDATE app_fd_ticket ")
                    .append(" SET c_status = ? ")
                    .append(" , c_ticket_status = ? ")
                    .append(" , c_save_status = ? ")
                    .append(" WHERE c_parent_id = ? ");
            ps = con.prepareStatement(query.toString());
            ps.setString(1, status);
            ps.setString(2, ticketStatus);
            ps.setString(3, saveStatus);
            ps.setString(4, originProcessId);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            query = null;
        }

//        return result;
    }

}