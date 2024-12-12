/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.FinalcheckHandlerAuto;
import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.MeasureIboosterGamasDao;
import id.co.itasoft.telkom.oss.plugin.function.SendStatusToMycx;
import id.co.itasoft.telkom.oss.plugin.function.SendWANotification;
import id.co.itasoft.telkom.oss.plugin.model.FinalcheckActModel;
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Request;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

/**
 * UNUSED
 * @author asani
 */
public class FinalcheckFromDraft extends DefaultApplicationPlugin {

    private final String pluginName = "Telkom New OSS - Ticket Incident Services - Process Finalcheck From Draft";

    @Override
    public Object execute(Map map) {
//
//        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
//        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
//        ApplicationContext ac = AppUtil.getApplicationContext();
//        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
//        String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
//        ListIbooster listIbooster = new ListIbooster();
//        MeasureIboosterGamasDao iboosterDao = new MeasureIboosterGamasDao();
//        FinalcheckActModel fam = new FinalcheckActModel();
//        InsertTicketStatusLogsDao dao = new InsertTicketStatusLogsDao();
//        InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
//        TicketStatus r = new TicketStatus();
//        TicketStatus ts = new TicketStatus();
//        Timestamp lastTimestamp = new Timestamp(System.currentTimeMillis());
//
//        InsertTicketStatusLogsNewAfterRunProcess newAfterRunProcess = new InsertTicketStatusLogsNewAfterRunProcess();
//        MasterParam param = new MasterParam();
//        FinalcheckHandlerAuto fha = new FinalcheckHandlerAuto();
//        JSONObject jsonObj = new JSONObject();
//        JSONArray jsonArr = new JSONArray();
//        try {
//            SendStatusToMycx mycx = new SendStatusToMycx();
//            SendWANotification swa = new SendWANotification();
//            MasterParamDao paramDao = new MasterParamDao();
//            boolean obj = false;
//            int totalWO = 0;
//            Request request;
//            Timestamp currentTime = null;
//
//            //get mycx url
//            param = paramDao.getUrl("update_status_ticket_to_mycx");
//
//            // GET DATA TICKET BY PROCESS ID
//            r = dao.getTicketId(processId);
//            currentTime = r.getDateModified();
//            boolean isrtTktRelvBool = false;
//            boolean updtOwnergroupBool = false;
//            boolean finalcheckBool = true;
//            boolean checkActualSolution = true;
//            boolean checkIbooster = true;
//            boolean isTechnical = true;
//            boolean technicalCheckIbooster = false;
//            boolean isSalamsimNotif = false;
//            boolean isReopen = false;
//            String owner = r.getOwner();
//
//            fam.setIbooster(r.getIbooster());
//            fam.setScc_internet(r.getScc_internet());
//            fam.setServiceType(r.getServiceType());
//            fam.setScc_voice(r.getScc_voice());
//            fam.setServiceNo(r.getServiceNo());
////            fam.setOwnergroup(r.getOwnerGroup());
////            LogUtil.info(getClassName(), "TICKET ID: " + r.getTicketId());
//            if ("TECHNICAL".equalsIgnoreCase(r.getClassificationFlag())
//                    && "DCS".equalsIgnoreCase(r.getCustomerSegment())
//                    && ("VOICE".equalsIgnoreCase(r.getServiceType())
//                    || "INTERNET".equalsIgnoreCase(r.getServiceType())
//                    || "IPTV".equalsIgnoreCase(r.getServiceType()))) {
//                technicalCheckIbooster = true;
////                LogUtil.info(getClassName(), "TECHNICAL DATA TRUE");
//
//                //JIKA IBOOSTER !SPEC => ukur ulang ibooster
//                if (!"SPEC".equalsIgnoreCase(fam.getIbooster())) {
//                    listIbooster = iboosterDao.getIbooster(fam.getServiceNo(), fam.getRealm(), r.getTicketId());
//                    fam.setIbooster(listIbooster.getMeasurementCategory());
//                    iboosterDao.updateIbooster(listIbooster, processId);
//                    iboosterDao.insertWorkLogs(processId, r.getTicketId(), r.getOwnerGroup(), listIbooster.getMeasurementCategory());
//                }
//            }
//
//            String duration = "";
//
//            // CHECK CLASSFICATION FLAG
//            if (!"TECHNICAL".equalsIgnoreCase(r.getClassificationFlag())) {
//                isTechnical = false;
//            }
//
//            if ("false".equalsIgnoreCase(r.getChild_gamas())) {
//                // khusus yg TIDAK di resolve oleh induk gamas
//
//                // CHECK ACTUAL SOLUTION
//                if (r.getActualSolution() == null || "".equals(r.getActualSolution())) {
//                    // JIKA ACTUAL SOLUTIN KOSONG MASUK KE FORM FINALCHECK
//                    fam.setAction_status("TO_ACTIVITY");
//                    fam.setTicket_status("FINALCHECK");
//                    finalcheckBool = false;
//                } else {
//                    checkActualSolution = false;
//
//                    // UPDATE FINALCHECK JADI BY SYSTEM
//                    String idFn = fha.GetIDFinalcheckTS(r.getTicketId());
//                    boolean updateFnSystem = fha.UpdateTs("SYSTEM", idFn);
//                }
//
//            } else {
//                /**
//                 * Di resolve oleh induk gamas mengabaikan actual solution
//                 *
//                 */
//                checkActualSolution = false;
//
//                // UPDATE FINALCHECK JADI BY SYSTEM
//                String idFn = fha.GetIDFinalcheckTS(r.getTicketId());
//                boolean updateFnSystem = fha.UpdateTs("SYSTEM", idFn);
//            }
//
//            if (!checkActualSolution) {
//
//                if (technicalCheckIbooster) {
////                    LogUtil.info(getClassName(), "TECHNICAL");
//
//                    // UNSPEC REOPEN TO BEFORE PROCESS
//                    if ("UNSPEC".equalsIgnoreCase(fam.getIbooster())) {
//                        fam.setAction_status("REOPEN");
//                        fam.setTicket_status(r.getLast_state());
//                        isReopen = true;
//                        irdao.updateStatusChildGamas("FALSE", "SYSTEM", "", processId);
//                        finalcheckBool = false;
//                    } else {
//                        if ("GAMAS".equalsIgnoreCase(r.getSourceTicket())
//                                || "FALLOUT".equalsIgnoreCase(r.getSourceTicket())
//                                || "PROACTIVE".equalsIgnoreCase(r.getSourceTicket())) {
//                            fam.setAction_status("RESOLVE");
//                            fam.setTicket_status("RESOLVED");
//
//                            isrtTktRelvBool = false;
//                            finalcheckBool = true;
////                            r.setOwnerGroup(r.getAssignedOwnerGroup());
//                            fam.setOwnergroup(r.getAssignedOwnerGroup());
//                        } else {
//                            if ("VALID".equalsIgnoreCase(r.getScc_code_validation())) {
//                                fam.setAction_status("RESOLVE");
//                                fam.setTicket_status("CLOSED");
//
//                                //HARUS INSERT STATUS RESOLVED
//                                isrtTktRelvBool = true;
//                                isSalamsimNotif = true;
//                                updtOwnergroupBool = true;
//                                fam.setOwnergroup("TIER1-BDG-SLMSPTK");
//                            } else {
//                                fam.setAction_status("RESOLVE");
//                                fam.setTicket_status("MEDIACARE");
//
//                                //HARUS INSERT STATUS RESOLVED
//                                isrtTktRelvBool = true;
//
//                                updtOwnergroupBool = true;
//                                fam.setOwnergroup("TIER1-BDG-SLMSPTK");
//                            }
//                        }
//
//                    }
//                } else {
//                    /**
//                     * NON TECHNICAL
//                     *
//                     */
//                    if ("GAMAS".equalsIgnoreCase(r.getSourceTicket())
//                            || "FALLOUT".equalsIgnoreCase(r.getSourceTicket())
//                            || "PROACTIVE".equalsIgnoreCase(r.getSourceTicket())) {
//                        fam.setAction_status("RESOLVE");
//                        fam.setTicket_status("RESOLVED");
//
//                        isrtTktRelvBool = false;
//                        finalcheckBool = true;
//                        r.setOwnerGroup(r.getAssignedOwnerGroup());
//                    } else {
//                        fam.setAction_status("RESOLVE");
//                        //HARUS INSERT STATUS RESOLVED
//                        isrtTktRelvBool = true;
//
//                        if ("DCS".equalsIgnoreCase(r.getCustomerSegment())) {
//                            fam.setTicket_status("MEDIACARE");
//                            fam.setOwnergroup("TIER1-BDG-SLMSPTK");
//                            updtOwnergroupBool = true;
//                        } else if ("DES".equalsIgnoreCase(r.getCustomerSegment())
//                                || "DGS".equalsIgnoreCase(r.getCustomerSegment())
//                                || "DBS".equalsIgnoreCase(r.getCustomerSegment())) {
//                            fam.setTicket_status("SALAMSIM");
//                            fam.setOwnergroup("TIER1_TENESA");
//                            updtOwnergroupBool = true;
//                            isSalamsimNotif = true;
//                        } else if ("DWS".equalsIgnoreCase(r.getCustomerSegment())) {
//                            fam.setTicket_status("SALAMSIM");
//                            updtOwnergroupBool = true;
//                            isSalamsimNotif = true;
//                            if (r.getCustomerId().equalsIgnoreCase("C3700008")) {
//                                fam.setOwnergroup("TIER1TELKOMSEL");
//                            } else {
//                                fam.setOwnergroup("TIER1DWS");
//                            }
//
//                        } else {
//                            fam.setTicket_status("MEDIACARE");
//                        }
//                    }
//
//                }
//            }
//
//            if (isrtTktRelvBool) {
////                LogUtil.info(getClassName(), "INSERT TICKET STATUS RESOLVED");
//                ts = dao.getTicketStatusByTicketID(r.getTicketId());
//                lastTimestamp = ts.getDateCreated();
//                duration = newAfterRunProcess.getDuration(lastTimestamp, currentTime);
//                r.setStatusTracking(duration);
//
//                //INSERT STATUS RESOLVED
//                r.setStatus("RESOLVED");
//                r.setStatusCurrent("RESOLVED");
//                if ("".equals(owner) || owner == null) {
//                    owner = "SYSTEM";
//                }
//                r.setChangeBy(owner);
//                r.setOwner("");
//                boolean statusResolved = dao.insertNewTicketStatus(r);
//                final TicketStatus tsF = r;
//                final MasterParam paramF = param;
//                Thread thread = new Thread(() -> {
//                    try {
//                        mycx.updateStatusTicket(tsF, paramF);
//                    } catch (SQLException ex) {
//                        LogUtil.error(getClassName(), ex, "ERROR SEND TO MYCX :" + ex.getMessage());
//                    }
//                });
//                thread.setDaemon(true);
//                thread.start();
//            }
//
//            TimeUnit.SECONDS.sleep(2);
//            //INSERT TICKET STATUS TARGET
//            if (finalcheckBool) {
//                ts = dao.getTicketStatusByTicketID(r.getTicketId());
//                lastTimestamp = ts.getDateCreated();
//                duration = newAfterRunProcess.getDuration(lastTimestamp, currentTime);
//                r.setStatusTracking(duration);
//                r.setStatus(fam.getTicket_status());
//                r.setStatusCurrent(fam.getTicket_status());
//                r.setAssignedOwnerGroup(fam.getOwnergroup());
//                r.setChangeBy("SYSTEM");
//                r.setOwner("");
//                boolean statusResolved = dao.insertNewTicketStatus(r);
//                final TicketStatus tsF = r;
//                final MasterParam paramF = param;
//                Thread thread = new Thread(() -> {
//                    try {
////                        LogUtil.info(getClassName(), "SEND STATUS " + tsF.getStatusCurrent());
//                        mycx.updateStatusTicket(tsF, paramF);
////                        LogUtil.info(getClassName(), "UPDATE STATUS Mycx :" + sendStatusToMycxNew);
//                    } catch (SQLException ex) {
//                        LogUtil.error(getClassName(), ex, "ERROR SEND TO MYCX :" + ex.getMessage());
//                    }
//                });
//                thread.setDaemon(true);
//                thread.start();
//
//                if ("MEDIACARE".equalsIgnoreCase(r.getStatusCurrent())) {
//                    /**
//                     * ADD WA NOTIFY
//                     */
//                    Thread threadWA = new Thread(() -> {
//                        try {
//                            String msg = swa.sendNotifyResolve(tsF);
//                        } catch (Exception ex) {
//                            LogUtil.error(getClassName(), ex, "ERROR SEND TO MYCX :" + ex.getMessage());
//                        }
//                    });
//                    threadWA.setDaemon(true);
//                    threadWA.start();
//                }
//            }
//
//            // UPDATE OWNERGROUP
//            if (updtOwnergroupBool) {
//                dao.UpdateOwnergroup(processId, fam.getOwnergroup());
//            }
//
//            if (isSalamsimNotif) {
//                final TicketStatus tsx = r;
//                Thread threadClosed = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            MasterParam paramcx = paramDao.getUrl("update_salamsim_to_mycx");
//                            mycx.sendStatusSalamsim(tsx, paramcx, "RESOLVE");
//                        } catch (Exception ex) {
//                            LogUtil.error(getClassName(), ex, "ex:" + ex.getMessage());
//                        }
//
//                    }
//                });
//                threadClosed.setDaemon(true);
//                threadClosed.start();
//            }
//
//            //UPDATE STATUS DB & WORKFLOW VARIABLED 
//            boolean updateDb = dao.UpdateStatusAndActStat(fam, processId);
////            saveWorkFlowVariable(map, "action_status", fam.getAction_status());
////            saveWorkFlowVariable(map, "ticket_status", fam.getTicket_status());
//
//            dao.UpdateOwnerTicket(processId);
//
//        } catch (Exception ex) {
//            LogUtil.info(getClassName(), "ERROR FINALCHECK FROM DRAFT :" + ex.getMessage());
//        } finally {
//            listIbooster = null;
//            iboosterDao = null;
//            fam = null;
//            dao = null;
//            irdao = null;
//            r = null;
//            ts = null;
//            newAfterRunProcess = null;
//            param = null;
//            fha = null;
//            jsonObj = null;
//            jsonArr = null;
//            System.gc();
//        }

        return null;
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "1.0";
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

//    private void saveWorkFlowVariable(Map map, String wVariableName, String wVariableValue) {
//        PluginManager pluginManager = (PluginManager) map.get("pluginManager");
//        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
//        WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
//        wm.activityVariable(workflowAssignment.getActivityId(), wVariableName, wVariableValue);
//    }
}
