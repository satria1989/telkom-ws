/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import java.util.Map;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

/**
 *
 * @author asani
 */
public class UpdateLastOwnerGroupReopen extends DefaultApplicationPlugin {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Update Last Owner Group (REOPEN)";
//    MasterParam param;
//    MasterParamDao paramDao;
//    SendStatusToMycx mycx;
//    MasterParam myihxParam;
//    SendStatusToMYIHX myihx;
//    InsertTicketStatusLogsDao itslog;
//    TicketStatus r, ts;
//    UpdateLastOwnerGroupDao updtLastDao;
//    InsertTicketStatusLogsDao dao;

    @Override
    public Object execute(Map map) {
//        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
//        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
//        String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
//        try {
//            param = new MasterParam();
//            paramDao = new MasterParamDao();
//            mycx = new SendStatusToMycx();
//            myihxParam = new MasterParam();
//            myihx = new SendStatusToMYIHX();
//            itslog = new InsertTicketStatusLogsDao();
//
//            //dapatkan process ID
//            String ownerGroup = "";
//            r = new TicketStatus();
//
//            if (!"".equalsIgnoreCase(processId)) {
//
//                updtLastDao = new UpdateLastOwnerGroupDao();
//                dao = new InsertTicketStatusLogsDao();
//
//                r = dao.getTicketId(processId); // 1
//                String owner = r.getOwner();
//
//                if (!"".equalsIgnoreCase(r.getTicketId())) {
//
//                    // reject pending
//                    ts = new TicketStatus();
//                    if ("REOPEN".equalsIgnoreCase(r.getActionStatus())
//                            || "REQUEST_REJECTED".equalsIgnoreCase(r.getPendingStatus())) {
//
//                        if ("REQUEST_REJECTED".equalsIgnoreCase(r.getPendingStatus())) {
//                            ts = updtLastDao.getLastOwnerGroupByTicketID(r.getTicketId(), "1"); //2
//                        } else {
//                            ts = updtLastDao.getLastOwnerGroupByTicketID(r.getTicketId(), "3"); //2
//                        }
//
//                        ownerGroup = ts.getOwnerGroup();
//                        dao.UpdateOwnergroup(processId, ownerGroup); //3
//                        dao.UpdateOwnerTicket(processId); // CLEAR OWNER //4
//
//                        if ("APPROVED".equalsIgnoreCase(r.getPendingStatus())) {
//                            r.setStatus("PENDING");
//                        } else {
//                            r.setStatus(r.getStatusCurrent());
//
//                            //CLEAR PENDING STATUS IF REJECTED
//                            if (!"REOPEN".equalsIgnoreCase(r.getActionStatus())) {
//                                dao.ClearPendingStatus(processId);
//                            }
//
//                        }
//
//                        r.setOwnerGroup(r.getAssignedOwnerGroup());
//                        r.setAssignedOwnerGroup(ownerGroup);
//
//                        if ("".equals(owner) || owner == null) {
//                            r.setChangeBy("SYSTEM");
//                        } else {
//                            r.setChangeBy(owner);
//                        }
//                        r.setOwner("");
//                        r.setStatusTracking(r.getTicketId());
//
//                        Thread thread = new Thread(() -> {
//                            MasterParam paramF = new MasterParam();
//                            try {
//                                paramF = paramDao.getUrl("update_status_ticket_to_mycx");
//                                mycx.updateStatusTicket(r, paramF);
//                            } catch (Exception ex) {
//                                LogUtil.error(getClassName(), ex, "ERROR SEND TO MYCX :" + ex.getMessage());
//                            } finally {
//                                paramF = null;
//                            }
//                        });
//                        thread.setDaemon(true);
//                        thread.start();
//
//                        itslog.insertTicketStatus(r);
//                        itslog.UpdateDateModifiedByProcess(processId);
//
//                    }
//
//                } else {
//                    LogUtil.error(this.getClass().getName(), null, "TICKET ID kosong");
//                }
//
//            } else {
//                LogUtil.error(this.getClass().getName(), null, "PROCESS ID kosong");
//            }
//        } catch (Exception ex) {
//            LogUtil.error(getClassName(), ex, "ERROR : " + ex.getMessage());
//        } finally {
//            param = null;
//            paramDao = null;
//            mycx = null;
//            myihxParam = null;
//            myihx = null;
//            itslog = null;
//            r = null;
//            ts = null;
//            updtLastDao = null;
//            dao = null;
//        }

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
        return null;
//        return AppUtil.readPluginResource(getClass().getName(), "/properties/InsertTicketStatusLogsProperties.json");
    }

    public void saveWorkFlowVariable(Map map, String wVariableName, String wVariableValue) {
        PluginManager pluginManager = (PluginManager) map.get("pluginManager");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
        wm.activityVariable(workflowAssignment.getActivityId(), wVariableName, wVariableValue);
    }

}
