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
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.util.NullLogger;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class KorektifTiket extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - korektif Process";
    LogInfo logInfo = new LogInfo();

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

//        ApplicationContext ac = AppUtil.getApplicationContext();
//        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");

        JSONObject mainObj = new JSONObject();
        JSONObject jObj = new JSONObject();
        try {
//            JSONObject headMainObj = new JSONObject();
//            KorektifTicketModel ktm = new KorektifTicketModel();

//            String paramTicketId = hsr.getParameter("ticketId").trim();
//            String[] listTicketId = paramTicketId.split(";");
//            String processDefId = "ticketIncidentService:latest:flowIncidentTicket";
            int i = 0;
//            for (String ticketId : listTicketId) {
////                LogUtil.info(this.getClassName(), "ticket_id : "+ticketId);
//                ktm = getDataTicket(ticketId);
////                LogUtil.info(this.getClassName(), "processId : "+ktm.getProcessId());
//                /**
//                 *
//                 * jika process open.running
//                 */
//                if ("1000001".equalsIgnoreCase(ktm.getState())) {
//                    workflowManager.processAbort(ktm.getProcessId());
//                    UpdateStatus(ktm.getStatus(), ktm.getTicketStatus(), "EDOTENSEI", ktm.getParentId());
//
//                    WorkflowProcessResult startProcess = workflowManager.processStartWithLinking(ktm.getProcessDefId().replace("#", ":"), null, "Correction", ktm.getParentId());
//                    if (startProcess.getParentProcessId() != null) {
//                        i++;
//                    }
//                } else if ("NEW".equalsIgnoreCase(ktm.getTicketStatus()) && ktm.getState() == null && ktm.getProcessId() != null && ktm.getActivityName() == null) {
//                    workflowManager.processAbort(ktm.getProcessId());
//                    UpdateStatus(ktm.getStatus(), ktm.getTicketStatus(), "FALSE", ktm.getParentId());
//
//                    WorkflowProcessResult startProcess = workflowManager.processStartWithLinking(ktm.getProcessDefId().replace("#", ":"), null, "Correction", ktm.getParentId());
//                    if (startProcess.getParentProcessId() != null) {
//                        i++;
//                    }
//                } else if ("NEW".equalsIgnoreCase(ktm.getTicketStatus()) && ktm.getProcessId() == null && ktm.getActivityName() == null) {
//                    UpdateStatus(ktm.getStatus(), ktm.getTicketStatus(), "FALSE", ktm.getParentId());
//
//                    WorkflowProcessResult startProcess = workflowManager.processStartWithLinking(processDefId, null, "Correction", ktm.getParentId());
//                    if (startProcess.getParentProcessId() != null) {
//                        i++;
//                    }
//                } else if (ktm.getProcessId() == null) {
////                    LogUtil.info(this.getClassName(), "kondisi processId null : "+ktm.getProcessId());
//                    UpdateStatus(ktm.getStatus(), ktm.getTicketStatus(), "FALSE", ktm.getParentId());
//
//                    WorkflowProcessResult startProcess = workflowManager.processStartWithLinking(processDefId, null, "Correction", ktm.getParentId());
//                    if (startProcess.getParentProcessId() != null) {
//                        i++;
//                    }
//                } else if (ktm.getProcessId() != null && ktm.getState() == null) {
//                    UpdateStatus(ktm.getStatus(), ktm.getTicketStatus(), "FALSE", ktm.getParentId());
//
//                    WorkflowProcessResult startProcess = workflowManager.processStartWithLinking(processDefId, null, "Correction", ktm.getParentId());
//                    if (startProcess.getParentProcessId() != null) {
//                        i++;
//                    }
//                } else if ("NEW".equalsIgnoreCase(ktm.getTicketStatus()) && "1000003".equals(ktm.getState()) && ktm.getProcessId() != null && "new".equals(ktm.getActivityName())) {
//                    workflowManager.processAbort(ktm.getProcessId());
//                    UpdateStatus(ktm.getStatus(), ktm.getTicketStatus(), "FALSE", ktm.getParentId());
//
//                    WorkflowProcessResult startProcess = workflowManager.processStartWithLinking(ktm.getProcessDefId().replace("#", ":"), null, "Correction", ktm.getParentId());
//                    if (startProcess.getParentProcessId() != null) {
//                        i++;
//                    }
//                } else if ("1000011".equalsIgnoreCase(ktm.getState())) {
//                    UpdateStatus(ktm.getStatus(), ktm.getTicketStatus(), "EDOTENSEI", ktm.getParentId());
//
//                    WorkflowProcessResult startProcess = workflowManager.processStartWithLinking(ktm.getProcessDefId().replace("#", ":"), null, "Correction", ktm.getParentId());
//                    if (startProcess.getParentProcessId() != null) {
//                        i++;
//                    }
//                } else if (ktm.getResourceId() == null &&
//                        "1000007".equals(ktm.getState()) &&
//                        ("new".equalsIgnoreCase(ktm.getActivityName()) ||
//                         "analysis".equalsIgnoreCase(ktm.getActivityName()) ||
//                         "backend".equalsIgnoreCase(ktm.getActivityName()) ||
//                         "draft".equalsIgnoreCase(ktm.getActivityName()) ||
//                         "pending".equalsIgnoreCase(ktm.getActivityName()) ||
//                         "approvalPending".equalsIgnoreCase(ktm.getActivityName()) ||
//                         "resolved".equalsIgnoreCase(ktm.getActivityName()) ||
//                         "finalcheck".equalsIgnoreCase(ktm.getActivityName()) ||
//                         "mediacare".equalsIgnoreCase(ktm.getActivityName()) ||
//                         "salamsim".equalsIgnoreCase(ktm.getActivityName()))) {
//                    workflowManager.reevaluateAssignmentsForActivity(ktm.getActivityId());
//                    i++;
//                } else if ("1000007".equals(ktm.getState()) && !"closed".equalsIgnoreCase(ktm.getActivityName())) {
//                    workflowManager.processAbort(ktm.getProcessId());
//                    UpdateStatus(ktm.getStatus(), ktm.getTicketStatus(), "EDOTENSEI", ktm.getParentId());
//                    WorkflowProcessResult startProcess = workflowManager.processStartWithLinking(ktm.getProcessDefId().replace("#", ":"), null, "Correction", ktm.getParentId());
//                    if (startProcess.getParentProcessId() != null) {
//                        i++;
//                    }
//                } else if (ktm.getIdParentTicket() == null) {
//                    insertToParentTicket(ktm.getParentId(), ticketId, ktm.getIdTableTicket());
//                    i++;
//                }
//
//                // state open.running.started && activity not in (activity status)
//            }

//            JSONObject jObj;
            
            mainObj.put("code", 200);
            mainObj.put("message", i + " data successfully updated");
            mainObj.write(hsr1.getWriter());

//            ktm = getDataTicket(ticketId);
//            boolean updateStatus = UpdateStatus(status, ticketStatus, saveStatus, originProcessId);
//            LogUtil.info(this.getClassName(), "updateStatus : "+updateStatus);
//            WorkflowProcessResult startProcess = null;
//            if (true) {
//                ApplicationContext ac = AppUtil.getApplicationContext();
//                WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
//                try {
////                    LogUtil.info(this.getClassName(), "start process");
//                    startProcess = workflowManager.processStartWithLinking(processDefId, null, "Automation", originProcessId);
//                    String process_id = startProcess.getParentProcessId();
//
//                    JSONObject jObj;
//                    jObj = new JSONObject();
//                    mainObj.put("code", 200);
//                    mainObj.put("message", "data update successful");
//                    mainObj.put("processId", process_id);
//                    mainObj.write(hsr1.getWriter());
//
//                } catch (Exception ex) {
//                    JSONObject jObj;
//                    jObj = new JSONObject();
//                    mainObj.put("code", 500);
//                    mainObj.put("message", "failed");
//                    headMainObj.put("error", mainObj);
//                    headMainObj.write(hsr1.getWriter());
//                    LogUtil.error(this.getClassName(), ex, "errror : " + ex.getMessage());
//                }
//
//            }
        } catch (Exception ex) {
            logInfo.Log(getClassName(), ex.getMessage());
        } finally {
            mainObj = null;
            jObj = null;
        }

    }

    private KorektifTicketModel getDataTicket(String idTicket) throws Exception {
        KorektifTicketModel ktm = new KorektifTicketModel();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();

        try {
            con = gc.getJogetConnection();
            query.append(" select c.id, c.c_id_ticket, c.c_parent_id, c.c_status, c.c_ticket_status, d.PROCESSID, ")
                    .append(" e.id as activity_id, e.ACTIVITYDEFINITIONID as activity_name, e.PDEFNAME as process_def_id, e.STATE, F.RESOURCEID, g.id as id_parent_ticket ")
                    .append(" from app_fd_ticket c ")
                    .append(" LEFT JOIN wf_process_link d ON c.c_parent_id = d.originProcessId ")
                    .append(" LEFT join SHKACTIVITIES e on e.PROCESSID = d.PROCESSID ")
                    .append(" LEFT JOIN SHKASSIGNMENTSTABLE F ON e.id = F.ACTIVITYID ")
                    .append(" LEFT JOIN app_fd_parent_ticket g on c.c_parent_id = g.id ")
                    .append(" WHERE 1=1 ")
                    .append(" AND C.C_ID_TICKET = ? ")
                    .append(" ORDER BY E.ACTIVATED DESC fetch first 1 row only  ");
            ps = con.prepareStatement(query.toString());
            ps.setString(1, idTicket);
            rs = ps.executeQuery();
            if (rs.next()) {
                ktm.setIdTableTicket(rs.getString("id"));
                ktm.setIdTicket(rs.getString("c_id_ticket"));
                ktm.setParentId(rs.getString("c_parent_id"));
                ktm.setStatus(rs.getString("c_status"));
                ktm.setTicketStatus(rs.getString("c_ticket_status"));
                ktm.setProcessId(rs.getString("PROCESSID"));
                ktm.setActivityId(rs.getString("activity_id"));
                ktm.setActivityName(rs.getString("activity_name"));
                ktm.setProcessDefId(rs.getString("process_def_id"));
                ktm.setState(rs.getString("STATE"));
                ktm.setResourceId(rs.getString("RESOURCEID"));
                ktm.setIdParentTicket(rs.getString("id_parent_ticket"));
            }

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClassName(), e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClassName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClassName(), e.getMessage());
            }
            query = null;
        }

        return ktm;
    }

//    public List<KorektifTicketModel> getDataTicket(String idTicket) throws SQLException {
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//
//        List<KorektifTicketModel> lktm = new ArrayList<>();
//
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        StringBuilder query = new StringBuilder();
//        query.append(" select c.id, c.c_id_ticket, c.c_parent_id, c.c_status, c.c_ticket_status, d.PROCESSID, ")
//                .append(" e.id as activity_id, e.ACTIVITYDEFINITIONID as activity_name, e.PDEFNAME as process_def_id, e.STATE ")
//                .append(" from app_fd_ticket c ")
//                .append(" LEFT JOIN wf_process_link d ON c.c_parent_id = d.originProcessId ")
//                .append(" LEFT join SHKACTIVITIES e on e.PROCESSID = d.PROCESSID ")
//                .append(" WHERE 1=1 ")
//                .append(" AND C.C_ID_TICKET = ? ")
//                .append(" ORDER BY E.ACTIVATED DESC fetch first 1 row only  ");
//        Connection con = ds.getConnection();
//        try {
//            ps = con.prepareStatement(query.toString());
//            ps.setString(1, idTicket);
//
//            rs = ps.executeQuery();
//            KorektifTicketModel ktm = null;
//            while (rs.next()) {
//                ktm = new KorektifTicketModel();
//                ktm.setIdTableTicket(rs.getString("id"));
//                ktm.setIdTicket(rs.getString("c_id_ticket"));
//                ktm.setParentId(rs.getString("c_parent_id"));
//                ktm.setStatus(rs.getString("c_status"));
//                ktm.setTicketStatus(rs.getString("c_ticket_status"));
//                ktm.setProcessId(rs.getString("PROCESSID"));
//                ktm.setActivityId(rs.getString("activity_id"));
//                ktm.setActivityName(rs.getString("activity_name"));
//                ktm.setProcessDefId(rs.getString("process_def_id"));
//                ktm.setState(rs.getString("STATE"));
//
//                lktm.add(ktm);
//            }
//        } catch (SQLException e) {
//            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
//        } finally {
//            query = null;
//            try {
//                if (rs != null) {
//                    rs.close();
//                }
//            } catch (Exception e) {
//                LogUtil.error(this.getClass().getName(), e, "Error message rs : " + e.getMessage());
//            }
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//            } catch (Exception e) {
//                LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
//            }
//            try {
//                if (con != null) {
//                    con.close();
//                }
//            } catch (Exception e) {
//                LogUtil.error(this.getClass().getName(), e, "Error message con : " + e.getMessage());
//            }
//
//        }
//        return lktm;
//    }
//                    JSONObject jObj;
//                    jObj = new JSONObject();
//                    mainObj.put("code", 500);
//                    mainObj.put("message", "failed");
//                    headMainObj.put("error", mainObj);
//                    headMainObj.write(hsr1.getWriter());
//                    LogUtil.error(this.getClassName(), ex, "errror : " + ex.getMessage());
    private boolean insertToParentTicket(String parentId, String ticketId, String idTableTicket) throws SQLException, Exception {

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
                    .append(" INSERT INTO app_fd_parent_ticket (id, dateCreated, c_child_id) ")
                    .append(" VALUES (?, ")
                    .append(" (select DATECREATED from APP_FD_TICKET where c_id_ticket = ? ), ")
                    .append(" ? ) ");
            ps = con.prepareStatement(query.toString());
            ps.setString(1, parentId);
            ps.setString(2, ticketId);
            ps.setString(3, idTableTicket);

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
                logInfo.Log(getClassName(), e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClassName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClassName(), e.getMessage());
            }
            query = null;
        }

        return result;

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
                logInfo.Log(getClassName(), e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClassName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClassName(), e.getMessage());
            }
            query = null;
        }

        return result;

    }

}
