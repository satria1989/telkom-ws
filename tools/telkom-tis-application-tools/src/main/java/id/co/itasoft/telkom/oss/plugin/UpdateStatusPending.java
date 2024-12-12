/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateQueryCollectionDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateStatusPendingDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateTicketStatusDeadlineDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

/**
 *
 * @author Mujib, asani
 */
public class UpdateStatusPending extends DefaultApplicationPlugin {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Update Status Pending";
    TicketStatus ticketStatus;
    UpdateStatusPendingDao dao;
    UpdateTicketStatusDeadlineDao dd;
    InsertTicketStatusLogsDao itslog;
    UpdateQueryCollectionDao updateQueryCollectiondao;
    LogInfo logInfo = new LogInfo();

    @Override
    public Object execute(Map map) {

        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());

        dao = new UpdateStatusPendingDao();
        dd = new UpdateTicketStatusDeadlineDao();
        itslog = new InsertTicketStatusLogsDao();
        ticketStatus = new TicketStatus();
        updateQueryCollectiondao = new UpdateQueryCollectionDao();

        try {

            ticketStatus = itslog.getTicketId(processId);
            String TICKETID = (ticketStatus.getTicketId() == null) ?
                "" : ticketStatus.getTicketId();
            String actStatus = (ticketStatus.getActionStatus() == null) ?
                "" : ticketStatus.getActionStatus();
            String pendStatus = (ticketStatus.getPendingStatus()== null) ?
                "" : ticketStatus.getPendingStatus();
            String status = (ticketStatus.getStatus()== null) ?
                "" : ticketStatus.getStatus();
            
            ticketStatus.setStatusTracking(TICKETID);
            if ("REQUEST_PENDING".equalsIgnoreCase(actStatus)
                    && !"APPROVED".equalsIgnoreCase(pendStatus)) {
                ticketStatus.setStatus("REQUEST_PENDING_" + status);
            } else if ("APPROVED".equalsIgnoreCase(pendStatus)) {
                ticketStatus.setStatus("PENDING");
            } else {
                ticketStatus.setStatus(status);
            }

            ticketStatus.setOwner("");
            ticketStatus.setChangeBy("SYSTEM");
            ticketStatus.setMemo("Take Ownership");
            ticketStatus.setStatusTracking("false");
            updateQueryCollectiondao.UpdateTimeonLastTicketSta(ticketStatus);
            
//            dd.UpdateTicketStatus(processId, ticketStatus.getStatusCurrent()); // UPDATE STATUS DAN TICKET STATUS
//            dd.UpdateOwnerBy(processId, "SYSTEM"); // UPDATE OWNER

            // INSERT TICKET STATUS LOG
            itslog.insertTicketStatus(ticketStatus);
            dao.clearField(processId, status); // CLEAR STATUS BY PROCESS ID

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            ticketStatus = null;
            dao = null;
            dd = null;
            itslog = null;
            updateQueryCollectiondao = null;
        }

        return null;
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

    //Fungsi SAVE / SET Workflow Variable/
    public void saveWorkFlowVariable(Map map, String wVariableName, String wVariableValue) {
        PluginManager pluginManager = (PluginManager) map.get("pluginManager");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
        wm.activityVariable(workflowAssignment.getActivityId(), wVariableName, wVariableValue);
    }

}
