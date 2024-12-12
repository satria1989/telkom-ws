/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.KorektifTicketModel;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class KorektifTicketStuck extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Duplicate Process";
    LogInfo info = new LogInfo();

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        return "";
    }

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
        return "";
    }

    @Override
    //        hsr1.getWriter().print(response);
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {

        ApplicationContext ac = AppUtil.getApplicationContext();
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");

        try {
            JSONObject headMainObj = new JSONObject();
            JSONObject mainObj = new JSONObject();
            KorektifTicketModel ktm = new KorektifTicketModel();

            String paramTicketId = hsr.getParameter("ticketId");
            String[] listTicketId = paramTicketId.split(";");
            for (String ticketId : listTicketId) {
//                ktm = getDataTicket(ticketId);

                /**
                 *
                 * jika process open.running
                 */
                if ("1000001".equalsIgnoreCase(ktm.getState())) {
                    workflowManager.processAbort(ktm.getProcessId());
                    UpdateStatus(ktm.getStatus(), ktm.getTicketStatus(), "EDOTENSEI", ktm.getParentId());

                    WorkflowProcessResult startProcess = workflowManager.processStartWithLinking(ktm.getProcessDefId().replace("#", ":"), null, "Automation", ktm.getParentId());

                }
            }

        } catch (Exception ex) {
          info.Log(getClassName(), ex.getMessage());
        }

    }

   
    
    
    public List<KorektifTicketModel> getDataTicket(String idParent) throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;

        List<KorektifTicketModel> r = new ArrayList<>();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append(" select c.id, c.c_id_ticket, c.c_parent_id, c.c_status, c.c_ticket_status, d.PROCESSID,  ")
                    .append(" e.id as activity_id, e.ACTIVITYDEFINITIONID as activity_name, e.PDEFNAME as process_def_id, e.STATE  ")
                    .append(" from app_fd_ticket c  ")
                    .append(" LEFT JOIN wf_process_link d ON c.c_parent_id = d.originProcessId  ")
                    .append(" LEFT join SHKACTIVITIES e on e.PROCESSID = d.PROCESSID  ")
                    .append(" WHERE 1=1  ")
                    .append(" and e.STATE = '1000001' ")
                    .append(" and c.datecreated BETWEEN TO_DATE ('2023/04/09 00:00', 'yyyy/mm/dd HH24:MI:SS') ")
                    .append(" AND TO_DATE ('2023/04/09 11:00:00', 'yyyy/mm/dd HH24:MI:SS') ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, idParent);

            result = ps.executeQuery();
            KorektifTicketModel tc = null;
            while (result.next()) {
                tc = new KorektifTicketModel();
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
        } catch (SQLException e) {
          info.Log(getClassName(), e.getMessage());
        } finally {
            query = null;
            try {
                if (result != null) {
                    result.close();
                }
            } catch (Exception e) {
                info.Log(getClassName(), e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                info.Log(getClassName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                info.Log(getClassName(), e.getMessage());
            }

        }
        return r;
    }

    private boolean UpdateStatus(String status, String ticketStatus, String saveStatus, String originProcessId) throws SQLException, Exception {

        boolean result = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        GetConnections gc = new GetConnections();
        StringBuilder query;
        try {
            con = gc.getJogetConnection();
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
            } catch (Exception e) {
                info.Log(getClassName(), e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                info.Log(getClassName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                info.Log(getClassName(), e.getMessage());
            }
            query = null;
        }

        return result;

    }
}
