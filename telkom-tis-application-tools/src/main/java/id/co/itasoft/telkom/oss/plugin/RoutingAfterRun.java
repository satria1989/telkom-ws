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
import org.joget.workflow.model.DecisionResult;
import org.joget.workflow.model.WorkflowAssignment;

/**
 * UNUSED
 * @author mtaup
 */
public class RoutingAfterRun extends DefaultApplicationPlugin {

    private final String pluginName = "Telkom New OSS - Ticket Incident Services - Routing After Run";
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
//        return AppUtil.readPluginResource(getClass().getName(), "/properties/CompleteActivity.json", null, true, null);
    return null;
    }

    @Override
    public Object execute(Map map) {

        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());

        String status = "";
        String action_status = "";
        String pending_status = "";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            if (!con.isClosed()) {
                String query = "select c_ticket_status, c_action_status, c_pending_status " +
                        "from app_fd_ticket " +
                        "where c_parent_id = ? ";

                ps = con.prepareStatement(query);
                ps.setString(1, processId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    status = rs.getString("c_ticket_status");
                    action_status = rs.getString("c_action_status");
                    pending_status = rs.getString("c_pending_status");
                } else {
//                    LogUtil.info(this.getClassName(), "ACTIVITY NOT FOUND");
                }
            }

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
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

        if ("REQUEST_PENDING".equalsIgnoreCase(action_status) &&
                !"APPROVED".equalsIgnoreCase(pending_status)) {
            status = "REQUEST_PENDING";
        } else if ("APPROVED".equalsIgnoreCase(pending_status)) {
            status = "PENDING";
        }
        

        DecisionResult result = new DecisionResult();
//        LogUtil.info("BEANSELL CLASS ROUTE ", processId+"  :: " + status);
        if (!"".equalsIgnoreCase(status)) {
            result.addTransition(status);
        } else {
            status = "else";
            result.addTransition(status);
        }

        /* For use cases requiring multiple node routing, do set boolean below as true. */
//result.setIsAndSplit(true);
        return result;

    }
}
