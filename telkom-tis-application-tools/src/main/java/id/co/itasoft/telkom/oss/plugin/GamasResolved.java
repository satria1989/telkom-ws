/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.dao.MeasureIboosterGamasDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
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
public class GamasResolved extends DefaultApplicationPlugin {

    private final String pluginName = "Telkom New OSS - Ticket Incident Services - Gamas Resolved";
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
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        ApplicationContext ac = AppUtil.getApplicationContext();
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
        String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
//        String processId = "f28fdf43-3ea3-4235-a845-bf2db3838baf";
        MeasureIboosterGamasDao dao = new MeasureIboosterGamasDao();
        TicketStatus r = new TicketStatus();
        InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();

        try {
            r = dao.getDataTicket(processId);

            if ("GAMAS".equalsIgnoreCase(r.getSourceTicket())) {
                List<Ticket> listTicket = dao.getProcessIdTicketWithShk(processId);
                for (Ticket ticket : listTicket) {
                    if ("1000003".equals(ticket.getState())) {
                        String activityDefId = ticket.getActivityName();
                        String processDefId = ticket.getProcessDefId().replace("#", ":");
                        String ChildProcessId = ticket.getProcessId();
                        ListIbooster ibooster = new ListIbooster();
                        String nd = ticket.getService_no();
                        String realm = "telkom.net";

//                        String response = "";
//                        String status = "";
//                        String ticketStatus = "";
                        String actionStatus = "";

                        if ("DCS".equalsIgnoreCase(ticket.getCust_segment()) || "PL-TSEL".equalsIgnoreCase(ticket.getCust_segment())) {
                            if ("backend".equalsIgnoreCase(activityDefId) ||
                                     "analysis".equalsIgnoreCase(activityDefId) ||
                                     "mediacare".equalsIgnoreCase(activityDefId) ||
                                     "resolved".equalsIgnoreCase(activityDefId) ||
                                     "salamsim".equalsIgnoreCase(activityDefId) ||
                                     "finalcheck".equalsIgnoreCase(activityDefId) ||
                                     "draft".equalsIgnoreCase(activityDefId)) {
                                actionStatus = "RESOLVE";
                            }

                            irdao.updateStatusChildGamas2("TRUE", "SYSTEM", r.getTicketId(), "Change of Status by Gamas", ticket.getTicketId(), actionStatus, "1");
                            workflowManager.assignmentForceComplete(processDefId, ChildProcessId, ticket.getActivityId(), "000000");

//                            if ("draft".equalsIgnoreCase(activityDefId)) {
//                                status = "MEDIACARE";
//                                ticketStatus = "MEDIACARE";
//                            }
//                            if ("mediacare".equalsIgnoreCase(activityDefId) || "resolved".equalsIgnoreCase(activityDefId) || "salamsim".equalsIgnoreCase(activityDefId)) {
//                                status = "CLOSED";
//                                ticketStatus = "CLOSED";
//                            }
//
//                            if ("finalcheck".equalsIgnoreCase(activityDefId)) {
//                                if (!"".equals(nd)) {
//                                    ibooster = dao.getIbooster(nd, realm, ticket.getTicketId());
//                                    dao.updateIbooster(ibooster, ticket.getParentTicketInc());
//                                    dao.insertWorkLogs(ticket.getIdTicketInc(), ticket.getTicketId(), ticket.getOwnerGroup(), ibooster.getMeasurementCategory());
//                                } else {
//                                    ibooster.setMeasurementCategory("UNSPEC");
//                                }
//
//                                if ("SPEC".equalsIgnoreCase(ibooster.getMeasurementCategory())) {
//                                    status = "MEDIACARE";
//                                    ticketStatus = "MEDIACARE";
//                                    dao.updateStatusTicket(ticketStatus, ticket.getParentTicketInc());
//                                } else {
//                                    status = "FINALCHECK";
//                                    ticketStatus = ticket.getLast_state();
//                                    dao.updateStatusAndAtionTicket("REOPEN", ticketStatus, ticket.getParentTicketInc());
//                                    irdao.updateStatusChildGamas("FALSE", "SYSTEM", "", "Change of Status by Gamas", ticket.getTicketId());
//                                }
//                                workflowManager.assignmentForceComplete(processDefId, ChildProcessId, ticket.getActivityId(), "000000");
//
//                            }
//                            if (!"finalcheck".equalsIgnoreCase(activityDefId)) {
//                                dao.updateStatusTicket(ticketStatus, ticket.getParentTicketInc());
//                                workflowManager.assignmentForceComplete(processDefId, ChildProcessId, ticket.getActivityId(), "000000");
//                            }
                        }
                    }
                }
                listTicket.clear();
            }
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            dao = null;
            r = null;
            irdao = null;
        }

        return null;
    }

//    public Object execute(Map map) {
//        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
//        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
//        ApplicationContext ac = AppUtil.getApplicationContext();
//        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
//        String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
////        String processId = "f28fdf43-3ea3-4235-a845-bf2db3838baf";
//        MeasureIboosterGamasDao dao = new MeasureIboosterGamasDao();
//        TicketStatus r = new TicketStatus();
//        InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
//
//        try {
//            r = dao.getDataTicket(processId);
//
//            if ("GAMAS".equalsIgnoreCase(r.getSourceTicket())) {
//                List<Ticket> listTicket = dao.getProcessIdTicket(processId);
//                // START
//                 for (Ticket ticket : listTicket) {
////
//                    List<WorkflowActivity> activityList = (List<WorkflowActivity>) workflowManager.getActivityList(ticket.getProcessId(), 0, -1, "id", false);
//                    for (WorkflowActivity wa : activityList) {
//                        if (wa.getState().equals("open.not_running.not_started")) {
//                            String activityDefId = wa.getActivityDefId();
//                            String processDefId = wa.getProcessDefId().replace("#", ":");
//                            String ChildProcessId = wa.getProcessId();
//                            ListIbooster ibooster = new ListIbooster();
//                            String nd = ticket.getService_no();
//                            String realm = "telkom.net";
//
//                            String response = "";
//                            String status = "";
//                            String ticketStatus = "";
//
//                            if ("DCS".equalsIgnoreCase(ticket.getCust_segment())) {
//                                irdao.updateStatusChildGamas("TRUE", "SYSTEM", r.getTicketId(), "Change of Status by Gamas", ticket.getTicketId());
//                                if ("backend".equalsIgnoreCase(activityDefId) || "analysis".equalsIgnoreCase(activityDefId)) {
//                                    if ("FISIK".equalsIgnoreCase(ticket.getClassificationType())) {
//                                        status = "FINALCHECK";
//                                        ticketStatus = "FINALCHECK";
//                                    } else {
//                                        status = "MEDIACARE";
//                                        ticketStatus = "MEDIACARE";
//                                    }
//
//                                }
//
//                                if ("draft".equalsIgnoreCase(activityDefId)) {
//                                    status = "MEDIACARE";
//                                    ticketStatus = "MEDIACARE";
//                                }
//
//                                if ("mediacare".equalsIgnoreCase(activityDefId) || "resolved".equalsIgnoreCase(activityDefId) || "salamsim".equalsIgnoreCase(activityDefId)) {
//                                    status = "CLOSED";
//                                    ticketStatus = "CLOSED";
//                                }
//
//                                if ("finalcheck".equalsIgnoreCase(activityDefId)) {
//                                    if (!"".equals(nd)) {
//                                        ibooster = dao.getIbooster(nd, realm, ticket.getTicketId());
//                                        dao.updateIbooster(ibooster, ticket.getParentTicketInc());
//                                        dao.insertWorkLogs(ticket.getIdTicketInc(), ticket.getTicketId(), ticket.getOwnerGroup(), ibooster.getMeasurementCategory());
//                                    } else {
//                                        ibooster.setMeasurementCategory("UNSPEC");
//                                    }
//
//                                    if ("SPEC".equalsIgnoreCase(ibooster.getMeasurementCategory())) {
//                                        status = "MEDIACARE";
//                                        ticketStatus = "MEDIACARE";
//                                        dao.updateStatusTicket(ticketStatus, ticket.getParentTicketInc());
//                                    } else {
//                                        status = "FINALCHECK";
//                                        ticketStatus = ticket.getLast_state();
//                                        dao.updateStatusAndAtionTicket("REOPEN", ticketStatus, ticket.getParentTicketInc());
//                                        irdao.updateStatusChildGamas("FALSE", "SYSTEM", "", "Change of Status by Gamas", ticket.getTicketId());
//                                    }
//                                    workflowManager.assignmentForceComplete(processDefId, ChildProcessId, wa.getId(), "000000");
//
//                                }
//
//                                if (!"finalcheck".equalsIgnoreCase(activityDefId)) {
//                                    dao.updateStatusTicket(ticketStatus, ticket.getParentTicketInc());
//                                    workflowManager.assignmentForceComplete(processDefId, ChildProcessId, wa.getId(), "000000");
//                                }
//                            }
//                        }
//                    }
//                }
//                // END
//
//                listTicket.clear();
//
//            }
//        } catch (Exception ex) {
//            LogUtil.error(getClassName(), ex, "eex:" + ex.getMessage());
//        } finally {
//            dao = null;
//            r = null;
//            irdao = null;
//        }
//
//        return null;
//    }
}

// START
//               
//for (Ticket ticket : listTicket) {
//
//                    if ("1000003".equals(ticket.getState())) {
//                        String activityDefId = ticket.getActivityName();
//                        String processDefId = ticket.getProcessDefId().replace("#", ":");
//                        String ChildProcessId = ticket.getProcessId();
//                        ListIbooster ibooster = new ListIbooster();
//                        String nd = ticket.getService_no();
//                        String realm = "telkom.net";
//
//                        String response = "";
//                        String status = "";
//                        String ticketStatus = "";
//
//                        if ("DCS".equalsIgnoreCase(ticket.getCust_segment())) {
//                            irdao.updateStatusChildGamas("TRUE", "SYSTEM", r.getTicketId(), "Change of Status by Gamas", ticket.getTicketId());
//                            if ("backend".equalsIgnoreCase(activityDefId) || "analysis".equalsIgnoreCase(activityDefId)) {
//                                if ("FISIK".equalsIgnoreCase(ticket.getClassificationType())) {
//                                    status = "FINALCHECK";
//                                    ticketStatus = "FINALCHECK";
//                                } else {
//                                    status = "MEDIACARE";
//                                    ticketStatus = "MEDIACARE";
//                                }
//
//                            }
//
//                            if ("draft".equalsIgnoreCase(activityDefId)) {
//                                status = "MEDIACARE";
//                                ticketStatus = "MEDIACARE";
//                            }
//
//                            if ("mediacare".equalsIgnoreCase(activityDefId) || "resolved".equalsIgnoreCase(activityDefId) || "salamsim".equalsIgnoreCase(activityDefId)) {
//                                status = "CLOSED";
//                                ticketStatus = "CLOSED";
//                            }
//
//                            if ("finalcheck".equalsIgnoreCase(activityDefId)) {
//                                if (!"".equals(nd)) {
//                                    ibooster = dao.getIbooster(nd, realm, ticket.getTicketId());
//                                    dao.updateIbooster(ibooster, ticket.getParentTicketInc());
//                                    dao.insertWorkLogs(ticket.getIdTicketInc(), ticket.getTicketId(), ticket.getOwnerGroup(), ibooster.getMeasurementCategory());
//                                } else {
//                                    ibooster.setMeasurementCategory("UNSPEC");
//                                }
//
//                                if ("SPEC".equalsIgnoreCase(ibooster.getMeasurementCategory())) {
//                                    status = "MEDIACARE";
//                                    ticketStatus = "MEDIACARE";
//                                    dao.updateStatusTicket(ticketStatus, ticket.getParentTicketInc());
//                                } else {
//                                    status = "FINALCHECK";
//                                    ticketStatus = ticket.getLast_state();
//                                    dao.updateStatusAndAtionTicket("REOPEN", ticketStatus, ticket.getParentTicketInc());
//                                    irdao.updateStatusChildGamas("FALSE", "SYSTEM", "", "Change of Status by Gamas", ticket.getTicketId());
//                                }
//                                workflowManager.assignmentForceComplete(processDefId, ChildProcessId, ticket.getActivityId(), "000000");
//
//                            }
//
//                            if (!"finalcheck".equalsIgnoreCase(activityDefId)) {
//                                dao.updateStatusTicket(ticketStatus, ticket.getParentTicketInc());
//                                workflowManager.assignmentForceComplete(processDefId, ChildProcessId, ticket.getActivityId(), "000000");
//                            }
//                        }
//                    }
//                }
// END

