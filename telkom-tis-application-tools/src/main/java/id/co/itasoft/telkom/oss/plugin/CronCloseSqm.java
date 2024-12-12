/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateAttributeTicketDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.service.WorkflowManager;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class CronCloseSqm extends DefaultApplicationPlugin {

    String pluginName = "Telkom New OSS - Cron Close SQM";
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
            CloseSqm cs = new CloseSqm();
            InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
            UpdateAttributeTicketDao tDao = new UpdateAttributeTicketDao();

            List<Ticket> listTicket = getTicketSqm();
            if (!listTicket.isEmpty()) {
                for (Ticket ticket : listTicket) {
                    if ("1000003".equals(ticket.getState())) {

                        HashMap<String, String> paramCkWo = new HashMap<String, String>();
                        paramCkWo.put("externalID1", ticket.getTicketId());
                        irdao.getStatusWo(paramCkWo, ticket.getTicketId(), "checkWorkORder - Close SQM");

                        cs.updateStatusTikcet(ticket.getParentTicketInc(), "", "");
                        workflowManager.assignmentForceComplete(ticket.getProcessDefId().replace("#", ":"), ticket.getProcessId(), ticket.getActivityId(), "000000");
                    }
                }
            }

        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        }
        return null;
    }

    public List<Ticket> getTicketSqm() throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;

        List<Ticket> r = new ArrayList<>();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append(" SELECT c.c_id_ticket, d.processId, c.c_parent_id AS parent_id_ticket, e.id as activity_id, e.PDEFNAME as process_def_id, e.STATE ")
                .append(" from app_fd_ticket c ")
                .append(" JOIN wf_process_link d ON c.c_parent_id = d.originProcessId ")
                .append(" join SHKACTIVITIES e on e.PROCESSID = d.PROCESSID ")
                .append(" WHERE c.c_channel = '50' ")
                .append(" and c.c_ticket_status != 'CLOSED' ")
                .append(" and extract ( day from ( numtodsinterval(sysdate - trunc(c.dateCreated), 'DAY'))) = 3 ")
                .append(" and e.state = '1000003' ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());

            result = ps.executeQuery();
            Ticket tc = null;
            while (result.next()) {
                tc = new Ticket();
                tc.setTicketId(result.getString("c_id_ticket"));
                tc.setProcessId(result.getString("processId"));
                tc.setParentTicketInc(result.getString("parent_id_ticket"));
                tc.setActivityId(result.getString("activity_id"));
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

}
