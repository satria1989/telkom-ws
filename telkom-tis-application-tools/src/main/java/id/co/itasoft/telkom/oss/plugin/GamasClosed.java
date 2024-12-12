/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.dao.MeasureIboosterGamasDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.List;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class GamasClosed extends DefaultApplicationPlugin {

    private final String pluginName = "Telkom New OSS - Ticket Incident Services - Gamas Closed";
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
    }

    @Override
    public Object execute(Map map) {

//        LogUtil.info(pluginName, "**gamas_resolved");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        ApplicationContext ac = AppUtil.getApplicationContext();
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
        String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
//        String processId = "701cc7c9-bc33-48b2-8e06-4b835e5b5864";
//        LogUtil.info(pluginName, "process_id_gms : " + processId);
        TicketStatus r;
        MeasureIboosterGamasDao dao = new MeasureIboosterGamasDao();
        InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
        try {
            r = new TicketStatus();
            r = dao.getDataTicket(processId);

            if ("GAMAS".equalsIgnoreCase(r.getSourceTicket())) {

                List<Ticket> listTicket = dao.getProcessIdTicketWithShk(processId);
                for (Ticket ticket : listTicket) {
                    if ("1000003".equals(ticket.getState())) {
                        String activityDefId = ticket.getActivityName();
                        String processDefId = ticket.getProcessDefId().replace("#", ":");
                        String ChildProcessId = ticket.getProcessId();
                        String status = "";
                        String ticketStatus = "";
                        String actionStatus = "";

                        if ("DCS".equalsIgnoreCase(ticket.getCust_segment())) {
//                            if ("backend".equalsIgnoreCase(activityDefId)) {
//                                status = "FINALCHECK";
//                                ticketStatus = "FINALCHECK";
//                            }
//                            if ("finalcheck".equalsIgnoreCase(activityDefId)) {
//                                status = "MEDIACARE";
//                                ticketStatus = "MEDIACARE";
//                            }
                            if ("mediacare".equalsIgnoreCase(activityDefId) ||
                                     "resolved".equalsIgnoreCase(activityDefId) ||
                                     "finalcheck".equalsIgnoreCase(activityDefId) ||
                                     "backend".equalsIgnoreCase(activityDefId) ||
                                     "salamsim".equalsIgnoreCase(activityDefId)) {
//                                status = "CLOSED";
//                                ticketStatus = "CLOSED";
                                actionStatus = "RESOLVE";
                            }
//                            dao.updateStatusTicket(ticketStatus, ticket.getParentTicketInc());
                            irdao.updateStatusChildGamas2("FALSE", "SYSTEM", "", "Change of Status by Gamas", ticket.getTicketId(), actionStatus, "1");
                            workflowManager.assignmentForceComplete(processDefId, ChildProcessId, ticket.getActivityId(), "000000");
                        }

                    }
                }

                listTicket.clear();
            }

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            r = null;
            dao = null;
            irdao = null;
        }

        return null;
    }

//    @Override
//    public Object execute(Map map) {
//
////        LogUtil.info(pluginName, "**gamas_resolved");
//
//        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
//        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
//        ApplicationContext ac = AppUtil.getApplicationContext();
//        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
//        String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
////        String processId = "701cc7c9-bc33-48b2-8e06-4b835e5b5864";
////        LogUtil.info(pluginName, "process_id_gms : " + processId);
//        TicketStatus r;
//        MeasureIboosterGamasDao dao = new MeasureIboosterGamasDao();
//        InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
//        try {
//            r = new TicketStatus();
//            r = dao.getDataTicket(processId);
//
//            if ("GAMAS".equalsIgnoreCase(r.getSourceTicket())) {
//
//                List<Ticket> listTicket = dao.getProcessIdTicket(processId);
//                for (Ticket ticket : listTicket) {
//                    List<WorkflowActivity> activityList = (List<WorkflowActivity>) workflowManager.getActivityList(ticket.getProcessId(), 0, -1, "id", false);
//                    for (WorkflowActivity wa : activityList) {
//                        if (wa.getState().equals("open.not_running.not_started")) {
//                                String activityDefId = wa.getActivityDefId();
//                                String processDefId = wa.getProcessDefId().replace("#", ":");
//                                String ChildProcessId = wa.getProcessId();
////                                LogUtil.info(this.getClassName(), "ChildProcessId : " + ChildProcessId +" | "+activityDefId);
//                                String status = "";
//                                String ticketStatus = "";
//
//                                if ("DCS".equalsIgnoreCase(ticket.getCust_segment())) {
//                                    irdao.updateStatusChildGamas("FALSE", "SYSTEM", "", "Change of Status by Gamas", ticket.getTicketId());
//                                    if ("backend".equalsIgnoreCase(activityDefId)) {
//                                        status = "FINALCHECK";
//                                        ticketStatus = "FINALCHECK";
//                                    }
//                                    if ("finalcheck".equalsIgnoreCase(activityDefId)) {
//                                        status = "MEDIACARE";
//                                        ticketStatus = "MEDIACARE";
//                                    }
//                                    if ("mediacare".equalsIgnoreCase(activityDefId) || "resolved".equalsIgnoreCase(activityDefId) || "salamsim".equalsIgnoreCase(activityDefId)) {
//                                        status = "CLOSED";
//                                        ticketStatus = "CLOSED";
//                                    }
//                                    dao.updateStatusTicket(ticketStatus, ticket.getParentTicketInc());
//                                    workflowManager.assignmentForceComplete(processDefId, ChildProcessId, wa.getId(), "000000");
//                                }
//
//                        }
//                    }
//                    activityList.clear();
//                }
//                
//                listTicket.clear();
//            }
//
//        } catch (Exception ex) {
//             LogUtil.error(getClassName(), ex, "GAMAS CLOSED ERROR :: "+ ex.getMessage());
//        } finally {
//            r = null;
//            dao = null;
//            irdao = null;
//        }
//
//        return null;
//    }
}
