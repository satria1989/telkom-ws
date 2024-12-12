/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

/**
 *
 * @author mtaup
 */
public class CompleteActivity extends DefaultApplicationPlugin {

    private final String pluginName = "Telkom New OSS - Ticket Incident Services - Complete Activity";
    private final String pluginClassName = this.getClass().getName();
    LogInfo logInfo = new LogInfo();
    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "7.0";
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
        return pluginClassName;
    }

    @Override
    public String getPropertyOptions() {
        return null;
//        return AppUtil.readPluginResource(getClass().getName(), "/properties/CompleteActivity.json", null, true, null);
    }

    String ticketStatus = "";
    String actionStatus = "";
    String pending_status = "";
    String save_status = "";

    @Override
    public Object execute(Map map) {

        try {

            String activityId = "";
            boolean status = false;
            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
            String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());

            String data = getSaveStatus(processId);
            String[] datas = data.split(";");

            ticketStatus = datas[0];
            actionStatus = datas[1];
            pending_status = datas[2];
            save_status = datas[3];

            WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                if ("false".equalsIgnoreCase(save_status)) {
                    try {
                        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
                        con = ds.getConnection();
                        if (!con.isClosed()) {
                            String query = "SELECT a.ACTIVITYID AS ACTIVITY_ID , b.PROCESSID " +
                                     "FROM SHKASSIGNMENTSTABLE a " +
                                     "JOIN WF_PROCESS_LINK b ON a.ACTIVITYPROCESSID = b.PROCESSID " +
                                     "WHERE ORIGINPROCESSID = ? " +
                                     "and ISACCEPTED  = 0 " +
                                     "and rownum = 1 ";

                            ps = con.prepareStatement(query);
                            ps.setString(1, processId);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                activityId = rs.getString("ACTIVITY_ID");
                                processId = rs.getString("PROCESSID");
                                status = true;
                            } else {
                                updateStatus(ticketStatus, ticketStatus, processId);
                            }
                        }

                    } catch (Exception e) {
                      logInfo.Log(getClass().getName(), e.getMessage());
                    } finally {

                        try {
                            if (con != null) {
                                con.close();
                            }
                        } catch (SQLException ex) {
                            logInfo.Log(getClass().getName(), ex.getMessage());
                        }
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                        } catch (SQLException ex) {
                            logInfo.Log(getClass().getName(), ex.getMessage());
                        }
                        try {
                            if (ps != null) {
                                ps.close();
                            }
                        } catch (SQLException ex) {
                            logInfo.Log(getClass().getName(), ex.getMessage());
                        }
                    }

                    String finalProcessId = processId;
                    String finalActivityId = activityId;
                    if (status) {
                        workflowManager.assignmentForceComplete("ticketIncidentService:latest:flowIncidentTicket", finalProcessId, finalActivityId, "000000");
                    }

                } else if ("new".equalsIgnoreCase(save_status)) {
                    String processDefId = "ticketIncidentService:latest:flowIncidentTicket";
                    try {
                        workflowManager.processStart(processDefId, null, null, "system", processId, false);
                    } catch (Exception e) {
                      logInfo.Log(getClass().getName(), e.getMessage());
                    }
                } else {
                    try {
                        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
                        con = ds.getConnection();
                        if (!con.isClosed()) {
                            String query = "update app_fd_ticket set c_save_status = 'false' " +
                                     "WHERE c_parent_id = ? ";

                            ps = con.prepareStatement(query);
                            ps.setString(1, processId);
                            ps.executeUpdate();
                        }

                    } catch (Exception e) {
                        logInfo.Log(getClass().getName(), e.getMessage());
                    } finally {

                        try {
                            if (con != null) {
                                con.close();
                            }
                        } catch (SQLException ex) {
                            logInfo.Log(getClass().getName(), ex.getMessage());
                        }
                        try {
                            if (ps != null) {
                                ps.close();
                            }
                        } catch (SQLException ex) {
                            logInfo.Log(getClass().getName(), ex.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }

        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        }

        return null;
    }

    public String getSaveStatus(String processId) throws SQLException, Exception {

        PreparedStatement ps = null;
        ResultSet rs = null;
        DataSource ds = null;
        Connection con = null;
        String saveStatus = "";

        ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        con = ds.getConnection();

        String query = "select get_status_ticket(?) as status_ticket from dual";

        try {
            ps = con.prepareStatement(query);
            ps.setString(1, processId);

            rs = ps.executeQuery();
            if (rs.next()) {
                saveStatus = rs.getString("status_ticket");
            }
        } catch (SQLException e) {
            logInfo.Log(getClass().getName(), e.getMessage());
        } finally {
            query = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }
        }

        return saveStatus;
    }

    public void updateStatus(String status, String ticketStatus, String processId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        con = ds.getConnection();

        query.append("UPDATE app_fd_ticket SET ")
                .append("c_status = ? ")
                .append(", c_ticket_status = ? ")
                .append("WHERE c_parent_id = ? ");

        try {
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());

                ps.setString(1, status);
                ps.setString(2, ticketStatus);
                ps.setString(3, processId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            logInfo.Log(getClass().getName(), e.getMessage());
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                logInfo.Log(getClass().getName(), ex.getMessage());
            }

            query = null;
        }
    }

}
