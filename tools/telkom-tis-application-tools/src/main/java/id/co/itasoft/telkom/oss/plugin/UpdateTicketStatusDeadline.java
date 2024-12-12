/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GlobalQuerySelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateQueryCollectionDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ListEnum;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;

/**
 *
 * @author tarkiman, rizki
 */
public class UpdateTicketStatusDeadline extends DefaultApplicationPlugin {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Update Ticket Status Deadline";
    TicketStatus ticketStatus;
    InsertTicketStatusLogsDao daoTicket;
    GlobalQuerySelectCollections selectCollection;
    UpdateQueryCollectionDao updateQueryCollectionDao;
    LogInfo logInfo = new LogInfo();

    public UpdateTicketStatusDeadline() {
        updateQueryCollectionDao = new UpdateQueryCollectionDao();
        selectCollection = new GlobalQuerySelectCollections();
        daoTicket = new InsertTicketStatusLogsDao();
        ticketStatus = new TicketStatus();
    }

    @Override
    public Object execute(Map map) {

        //dapatkan process ID
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        final String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
        final String status = getPropertyString("status");

        String DRAFT = ListEnum.DRAFT.toString();
        String MEDIACARE = ListEnum.MEDIACARE.toString();
        String SALAMSIM = ListEnum.SALAMSIM.toString();
                
        String ACT_DEADLINETOSALAMSIM = ListEnum.DEADLINETOSALAMSIM.toString();
        String ACT_DEADLINETOANALYSIS = ListEnum.DEADLINETOANALYSIS.toString();
        String ACT_DEADLINETOCLOSED = ListEnum.DEADLINETOCLOSED.toString();
        
        try {
            if (!"".equalsIgnoreCase(processId) && !"".equalsIgnoreCase(status)) {
                ticketStatus = selectCollection.getTicketId(processId);
                String STATUSCURRENT = (ticketStatus.getStatus() == null) ? 
                        "" : ticketStatus.getStatus().toUpperCase().trim();
                String FLAG_FCR = (ticketStatus.getFlagFcr() == null) ?
                        "" : ticketStatus.getFlagFcr().toUpperCase().trim();
                
                String ACTIONSTATUS = "";
                if ("DRAFT".equals(STATUSCURRENT)) {
                    // UPDATE ACTION STATUS JADI DEADLINE
                    ACTIONSTATUS = ACT_DEADLINETOANALYSIS;
//                    dao.UpdateActionStatus(processId, "SENDTOANALYSIS");
                } else if(MEDIACARE.equalsIgnoreCase(STATUSCURRENT)) {
                    ACTIONSTATUS = ACT_DEADLINETOSALAMSIM;
                } else if(SALAMSIM.equalsIgnoreCase(STATUSCURRENT)) {
                    ACTIONSTATUS = ACT_DEADLINETOCLOSED;
                }

                // UPDATE STATUS AND WORKFLOW VARIABLE
//                dao.UpdateTicketStatus(processId, status);
                /**
                 * @param PROCESSID
                 * @param actionStatus
                 */
                updateQueryCollectionDao.updateDeadlineLogic(processId, ACTIONSTATUS);
//
//                ticketStatus.setStatusTracking(ticketStatus.getTicketId());
//                ticketStatus.setOwner("");
//                ticketStatus.setChangeBy("SYSTEM");
//                daoTicket.insertTicketStatus(ticketStatus);

                if ("CLOSED".equalsIgnoreCase(status)) {
                    if (!"FCR".equalsIgnoreCase(FLAG_FCR)) {
                        // UPDATE CLOSED/REOPEN BY
                        String CLOSEDBYCODE = "8";
                        updateQueryCollectionDao.UpdateClosedReopenByParentId(processId, CLOSEDBYCODE);
                    }
                }
            } 
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            daoTicket = null;
            ticketStatus = null;
            updateQueryCollectionDao = null;
            selectCollection = null;
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
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/UpdateTicketStatusDeadlineProperties.json");
    }

//    public void saveWorkFlowVariable(Map map, String wVariableName, String wVariableValue) {
//        PluginManager pluginManager = (PluginManager) map.get("pluginManager");
//        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
//        WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
//        wm.activityVariable(workflowAssignment.getActivityId(), wVariableName, wVariableValue);
//    }
}
