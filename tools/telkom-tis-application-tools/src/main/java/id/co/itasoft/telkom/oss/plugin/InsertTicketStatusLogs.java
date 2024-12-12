/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editoticketSts.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GlobalQueryInsertCollection;
import id.co.itasoft.telkom.oss.plugin.dao.GlobalQuerySelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogicToOtherAPI;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateQueryCollectionDao;
import id.co.itasoft.telkom.oss.plugin.function.ActionStatusTicket;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.function.SendStatusToMYIHX;
import id.co.itasoft.telkom.oss.plugin.function.SendStatusToMycx;
import id.co.itasoft.telkom.oss.plugin.model.ListEnum;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author tarkiman, rizki eka
 */
public class InsertTicketStatusLogs extends DefaultApplicationPlugin {

  final String pluginName = "Telkom New OSS - Ticket Incident Services - Insert Ticket Status Logs";

  LogInfo logInfo = new LogInfo();
  RESTAPI _RESTAPI;
  InsertTicketStatusLogsDao dao;
  ActionStatusTicket actionStatusTicket;
  UpdateQueryCollectionDao updateQueryCollectionDao;
  TicketStatus ticketSts, ticketStatusReopen;
  ArrayManipulation arrayManipulation;
  GlobalQuerySelectCollections selectCollections;
  String TOKEN = "";
  MasterParam mstParamMycx, mstParamMyIhx, mstParamSalamsim;
  MasterParamDao mstParamDao;
  SendStatusToMycx statusToMycx;
  SendStatusToMYIHX myihx;
  GlobalQueryInsertCollection insertCollection;
  LogicToOtherAPI logicToOtherAPI;
  TtrEndToEnd ttrE2E;
  
  String ANALYSIS = ListEnum.ANALYSIS.toString();
  String BACKEND = ListEnum.BACKEND.toString();
  String DRAFT = ListEnum.DRAFT.toString();
  String PENDING = ListEnum.PENDING.toString();
  String FINALCHECK = ListEnum.FINALCHECK.toString();
  String RESOLVED = ListEnum.RESOLVED.toString();
  String MEDIACARE = ListEnum.MEDIACARE.toString();
  String SALAMSIM = ListEnum.SALAMSIM.toString();
  String CLOSED = ListEnum.CLOSED.toString();
  String REQUESTPENDING = ListEnum.REQUESTPENDING.toString();
  String ownerGroup = "";

  String ACT_REDISPATCH_BY_SELECT_SOLUTION = ListEnum.REDISPATCH_BY_SELECT_SOLUTION.toString();
  String ACT_RESOLVE = ListEnum.RESOLVE.toString();
  String ACT_REASSIGN_OWNERGROUP = ListEnum.REASSIGN_OWNERGROUP.toString();
  String ACT_REQUEST_PENDING = ListEnum.REQUEST_PENDING.toString();
  String ACT_REOPEN = ListEnum.REOPEN.toString();
  String ACT_SENDTOANALYSIS = ListEnum.SENDTOANALYSIS.toString();
  String ACT_AFTERPENDING = ListEnum.AFTERPENDING.toString();
  String ACT_AFTER_REQUEST = ListEnum.AFTER_REQUEST.toString();
  String ACT_DEADLINETOSALAMSIM = ListEnum.DEADLINETOSALAMSIM.toString();
  String ACT_DEADLINETOCLOSED = ListEnum.DEADLINETOCLOSED.toString();
  String ACT_DEADLINETOANALYSIS = ListEnum.DEADLINETOANALYSIS.toString();
  String ACT_DEADLINETOMEDIACARE = ListEnum.DEADLINETOMEDIACARE.toString();
  String ACT_TOSALAMSIM = ListEnum.TOSALAMSIM.toString();
  
  String custSegmentHSIList[] = {"DES", "DGS", "DBS"};
  

  public InsertTicketStatusLogs() {
    logicToOtherAPI = new LogicToOtherAPI();
    insertCollection = new GlobalQueryInsertCollection();
    mstParamMycx = new MasterParam();
    mstParamMyIhx = new MasterParam();
    mstParamSalamsim = new MasterParam();
    mstParamDao = new MasterParamDao();
    statusToMycx = new SendStatusToMycx();
    myihx = new SendStatusToMYIHX();
    selectCollections = new GlobalQuerySelectCollections();
    actionStatusTicket = new ActionStatusTicket();
    _RESTAPI = new RESTAPI();
    updateQueryCollectionDao = new UpdateQueryCollectionDao();
    dao = new InsertTicketStatusLogsDao();
    ticketSts = new TicketStatus();
    ticketStatusReopen = new TicketStatus();
    arrayManipulation = new ArrayManipulation();
    TOKEN = _RESTAPI.getToken();
  }

  @Override
  public Object execute(Map map) {
    AppService appService = (AppService) AppUtil
        .getApplicationContext()
        .getBean("appService");
    
    WorkflowAssignment workflowAssignment = (WorkflowAssignment) map
        .get("workflowAssignment");
    
    WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil
        .getApplicationContext()
        .getBean("workflowUserManager");
    
    final String processId = appService
        .getOriginProcessId(workflowAssignment
            .getProcessId()
        );
    
    String username = workflowUserManager.getCurrentUsername();

    boolean sendStatusResolve = false;

    try {
      if (!"".equalsIgnoreCase(processId)) {
        //  Number of threads = Number of Available Cores * (1 + Wait time / Service time)
//        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(135);
//        executor.setKeepAliveTime(30, TimeUnit.SECONDS);
//        executor.allowCoreThreadTimeOut(true);
        
//        int numOfCores = Runtime.getRuntime().availableProcessors(); //
        
        ticketSts = selectCollections.getTicketId(processId); // GET DATA TICKET BY PROCESS
        String runProcessStr = (ticketSts.getRunProcess() == null) ? "" : ticketSts.getRunProcess();
        boolean runProcess = (runProcessStr.equals("1")) ? true : false;

        // JIKA RUN PROCESS 1 MAKA LOGIC AKAN BERJALAN
        if (runProcess) {
          // CHECK NEXT STATUS
          String STATUS_TARGET = actionStatusTicket.getStatus(ticketSts, processId);
          
          // CREATE VARIABLE BASE ON TICKET
          String LAST_STATUS = (ticketSts.getStatus() == null) ? 
              "" : ticketSts.getStatus().toUpperCase().trim(); // CURRENT STATUS BEFORE CHECK NEXT STATUS
          String actionStatus = (ticketSts.getActionStatus() == null) ? 
              "" : ticketSts.getActionStatus().toUpperCase().trim();
          String finalcheck = (ticketSts.getFinalCheck() == null) ? 
              "" : ticketSts.getFinalCheck();
          String sourceTicket = (ticketSts.getSourceTicket() == null) ? 
              "" : ticketSts.getSourceTicket().toUpperCase().trim();
          String technicalStatus = (ticketSts.getClassificationFlag() == null) ? 
              "" : ticketSts.getClassificationFlag();
          String classificationType = (ticketSts.getClassification_type() == null) ? 
              "" : ticketSts.getClassification_type();
          String customerSegment = (ticketSts.getCustomerSegment() == null) ? 
              "" : ticketSts.getCustomerSegment().toUpperCase().trim();
          String pendingStatus = (ticketSts.getPendingStatus() == null) ? 
              "" : ticketSts.getPendingStatus().toUpperCase().trim();
          String channel = (ticketSts.getChannel() == null) ? 
              "" : ticketSts.getChannel();
          String statusSCCValidation = (ticketSts.getScc_code_validation() == null) ? 
              "" : ticketSts.getScc_code_validation().toUpperCase().trim();
          String ticketId = (ticketSts.getTicketId() == null) ? 
              "" : ticketSts.getTicketId();
          String owner = (ticketSts.getOwner() == null) ? 
              "" : ticketSts.getOwner();
          String idTicket = (ticketSts.getId() == null) ? 
              "" : ticketSts.getId(); // record id on ticket
          String symptomp = (ticketSts.getSymptomId()== null) ? 
              "" : ticketSts.getSymptomId(); // record id on ticket
          String workzone = (ticketSts.getWorkZone()== null) ? 
              "" : ticketSts.getWorkZone(); // record id on ticket
          String lastState = (ticketSts.getLast_state()== null) ? 
              "" : ticketSts.getLast_state(); // record id on ticket
          
          // SET LAST  ASSIGNED
          // OWNERGROUP
          String ASSIGNEDOWNERGROUP = ticketSts.getAssignedOwnerGroup();
          ownerGroup = ticketSts.getAssignedOwnerGroup();
          ticketSts.setStatusTracking(ticketSts.getTicketId()); // FOR FUNCTION GETDURATION
          
          String ownergroupReopen = ownerGroup;
          
          String LISTDEADLINEACTSTAT[] = {
            ACT_DEADLINETOANALYSIS,
            ACT_DEADLINETOCLOSED,
            ACT_DEADLINETOSALAMSIM,
            ACT_DEADLINETOMEDIACARE
          };
          String LISTACTIONSSTATUS[] = {
            ACT_REDISPATCH_BY_SELECT_SOLUTION,
            ACT_REASSIGN_OWNERGROUP,
            ACT_REQUEST_PENDING,
            ACT_SENDTOANALYSIS,
            ACT_RESOLVE
          };
          
          boolean checkStatusInList = false;
          boolean checkStatusCurrentNotAfterResolved = false;
          boolean checkActionDeadline = false;
          
          
          if (!"".equalsIgnoreCase(ticketId)) {
            boolean checkOwnerGroupHSI = false;
//            
            String TICKET35[] = {ANALYSIS, BACKEND, MEDIACARE, SALAMSIM, CLOSED};
            String SOURCETICKETLIST[] = {"GAMAS", "FALLOUT", "PROACTIVE"};
            String LISTSTATUSAFTERRESOLVED[] = {MEDIACARE, SALAMSIM, CLOSED};
            String POSITION_STATUS_TARGET = STATUS_TARGET; // SET POSITION TARGET FOR SWITCH-CASE
            
            if (actionStatus.equals(ACT_REQUEST_PENDING) && "APPROVED".equals(pendingStatus)) {
              POSITION_STATUS_TARGET = PENDING;
            } else if (actionStatus.equals(ACT_REQUEST_PENDING) && !"APPROVED".equals(pendingStatus)) {
              POSITION_STATUS_TARGET = REQUESTPENDING;
            }
            
            ticketSts.setStatusCurrent(STATUS_TARGET); // set status target
            ticketSts.setChangeBy(owner);
            String runProccess = "";
            
            switch(POSITION_STATUS_TARGET) {
              case "DRAFT" :
                ticketSts.setOwner("");
                ticketSts.setStatusCurrent(STATUS_TARGET);
                ticketSts.setStatus(STATUS_TARGET);
                ticketSts.setChangeBy(owner);
                ticketSts.setAssignedOwnerGroup(ownerGroup);
                
//                executor.submit(() -> {
//                  try {
                    // send mycx & mihx
                    sendToMycxAndMyihx(channel, STATUS_TARGET, ticketSts);
//                  } catch (Exception ex) {
//                    logInfo.Log(getClass().getName(), ex.getMessage());
//                  } 
//                  return null;
//                });
                
                // INSERT STATUS LOG, SEND OT MYCX & MYIHX
                updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                ticketSts.setStatusTracking("false");
                dao.insertTicketStatus(ticketSts);
                
                // CLEAR OWNER & MEMO, UPDATE OWNERGROUP TERBARU, UPDATE STATUS TARGET
                updateQueryCollectionDao.updateDataAfterRunProcess(
                    processId,
                    ownerGroup,
                    STATUS_TARGET, 
                    statusSCCValidation, 
                    runProccess,
                    false,
                    ANALYSIS
                );
                break;
                
              case "ANALYSIS" :
                // KONDISI JIKA REOPEN GET OWNERGROUP
                if (ACT_REOPEN.equals(actionStatus)) {
                  ticketStatusReopen = selectCollections.getLastOwnerGroupByTicketID(ticketId, "3");
                  ownergroupReopen = (ticketStatusReopen.getOwnerGroup() == null) ? 
                    "" : ticketStatusReopen.getOwnerGroup();
                }

                // KONDISI REQUEST PENDING DI REJECT BALIK KE OWNERGROUP LAST
                if ("REQUEST_REJECTED".equalsIgnoreCase(pendingStatus)
                    && ACT_AFTER_REQUEST.equals(actionStatus)) {
                  ticketStatusReopen = selectCollections.getLastOwnerGroupByTicketID(ticketId, "1");
                  ownergroupReopen = (ticketStatusReopen.getOwnerGroup() == null) ? 
                    "" : ticketStatusReopen.getOwnerGroup();
                }

                if (actionStatus.equalsIgnoreCase(ACT_DEADLINETOANALYSIS)) {
                  ticketSts.setChangeBy("SYSTEM");
                } else {
                  ticketSts.setChangeBy(owner);
                }
                  

                ticketSts.setStatus(STATUS_TARGET);
                ticketSts.setOwner("");
                ticketSts.setStatusCurrent(STATUS_TARGET);
                ticketSts.setAssignedOwnerGroup(ownergroupReopen);
                
//                executor.submit(() -> {
//                  try {
                    
                    // REOPEN DARI SALAMSIM
                    if (LAST_STATUS.equals(SALAMSIM) 
                        && customerSegment.equalsIgnoreCase("DCS")) {
                      mstParamSalamsim = mstParamDao.getUrl("update_salamsim_to_mycx");
                      statusToMycx.sendStatusSalamsimWithToken(
                          ticketSts, 
                          mstParamSalamsim, 
                          ACT_REOPEN,
                          TOKEN
                      ); // DARI SALAMSIM REOPEN SEND  IN_STATUS = 14
                    }
                    
                    if (ACT_REOPEN.equals(actionStatus)) {
                      String summary = "REOPEN TICKET FROM " + LAST_STATUS;
                      insertCollection.insertWorkLogs(
                        idTicket, 
                        ticketId, 
                        ownerGroup, 
                        summary, 
                        ""
                      );
                    }
                    
                    // send mycx & mihx
                    sendToMycxAndMyihx(channel, STATUS_TARGET, ticketSts);
                    
                    // SEND TO MYTENS
                    if (arrayManipulation.SearchDataOnArray(custSegmentHSIList, customerSegment)
                        && channel.equalsIgnoreCase("35")) {
                        if (arrayManipulation.SearchDataOnArray(TICKET35, STATUS_TARGET)) {
                            logicToOtherAPI.UpdateMYTENS(ticketSts, STATUS_TARGET, TOKEN);
                        }
                    }
                    
//                  } catch (Exception ex) {
//                    logInfo.Log(getClass().getName(), ex.getMessage());
//                  } 
//                  return null;
//                });
                
                // INSERT STATUS LOG, SEND OT MYCX & MYIHX
                // To Target
                updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                ticketSts.setStatusTracking("false");
                dao.insertTicketStatus(ticketSts);
                
                // CLEAR OWNER & MEMO, UPDATE OWNERGROUP TERBARU, UPDATE STATUS TARGET
                updateQueryCollectionDao.updateDataAfterRunProcess(
                    processId,
                    ownergroupReopen, 
                    STATUS_TARGET, 
                    statusSCCValidation, 
                    runProccess,
                    false,
                    ANALYSIS
                );
                
                break;
              case "BACKEND" :
                
                // KONDISI JIKA REOPEN GET OWNERGROUP
                if (ACT_REOPEN.equals(actionStatus)) {
                  ticketStatusReopen = selectCollections.getLastOwnerGroupByTicketID(ticketId, "3");
                  ownergroupReopen = (ticketStatusReopen.getOwnerGroup() == null) ? 
                    "" : ticketStatusReopen.getOwnerGroup();
                }

                // KONDISI REQUEST PENDING DI REJECT BALIK KE OWNERGROUP LAST
                if ("REQUEST_REJECTED".equalsIgnoreCase(pendingStatus)
                    && ACT_AFTER_REQUEST.equals(actionStatus)) {
                  ticketStatusReopen = selectCollections.getLastOwnerGroupByTicketID(ticketId, "1");
                  ownergroupReopen = (ticketStatusReopen.getOwnerGroup() == null) ? 
                    "" : ticketStatusReopen.getOwnerGroup();
                }
                
                ticketSts.setChangeBy(owner);
                ticketSts.setStatus(STATUS_TARGET);
                ticketSts.setOwner("");
                ticketSts.setStatusCurrent(STATUS_TARGET);
                ticketSts.setAssignedOwnerGroup(ownergroupReopen);
                
//                executor.submit(() -> {
//                  try {
                    // REOPEN DARI SALAMSIM
                    if (LAST_STATUS.equals(SALAMSIM) && customerSegment.equalsIgnoreCase("DCS")) {
                      mstParamSalamsim = mstParamDao.getUrl("update_salamsim_to_mycx");
                      statusToMycx.sendStatusSalamsimWithToken(
                          ticketSts, 
                          mstParamSalamsim, 
                          ACT_REOPEN,
                          TOKEN
                      ); // DARI SALAMSIM REOPEN SEND  IN_STATUS = 14
                    }
                    
                    /**
                     * NGIRIM WO REVIEW DI BATAS HANYA UNTUK DCS
                     * @param ticketStatus, statusWO, username
                     */
                    if(customerSegment.equalsIgnoreCase("DCS") && 
                        actionStatus.equalsIgnoreCase(ACT_REOPEN)){
                      updateQueryCollectionDao.UpdateWorkorderReview(
                          ticketSts, 
                          "REVIEW", 
                          username
                      );
                    }
                    
                    if (ACT_REOPEN.equals(actionStatus)) {
                      String summary = "REOPEN TICKET FROM " + LAST_STATUS;
                      insertCollection.insertWorkLogs(
                        idTicket, 
                        ticketId, 
                        ownerGroup, 
                        summary, 
                        ""
                      );
                    }
                    
                    // SEND TO MYTENS
                    if (arrayManipulation.SearchDataOnArray(custSegmentHSIList, customerSegment)
                        && channel.equalsIgnoreCase("35")) {
                        if (arrayManipulation.SearchDataOnArray(TICKET35, STATUS_TARGET)) {
                            logicToOtherAPI.UpdateMYTENS(ticketSts, STATUS_TARGET, TOKEN);
                        }
                    }

                    // send mycx & mihx
                    sendToMycxAndMyihx(channel, STATUS_TARGET, ticketSts);
                    
//                  } catch(Exception ex) {
//                    logInfo.Log(getClass().getName(), ex.getMessage());
//                  } 
//                  return null;
//                });
                
                
                
                // INSERT STATUS LOG, SEND OT MYCX & MYIHX
                // To Target
                updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                ticketSts.setStatusTracking("false");
                dao.insertTicketStatus(ticketSts);
                
                // CLEAR OWNER & MEMO, UPDATE OWNERGROUP TERBARU, UPDATE STATUS TARGET
                updateQueryCollectionDao.updateDataAfterRunProcess(
                    processId,
                    ownergroupReopen, 
                    STATUS_TARGET, 
                    statusSCCValidation, 
                    runProccess,
                    false,
                    BACKEND
                );
                break;
                
              case "FINALCHECK" :
                if ("DCS".equalsIgnoreCase(customerSegment)) {
                  runProccess = "2";
                }
                
                // KONDISI REQUEST PENDING DI REJECT BALIK KE OWNERGROUP LAST
                if ("REQUEST_REJECTED".equalsIgnoreCase(pendingStatus)
                    && ACT_AFTER_REQUEST.equals(actionStatus)) {
                  ticketStatusReopen = selectCollections.getLastOwnerGroupByTicketID(ticketId, "1");
                  ownergroupReopen = (ticketStatusReopen.getOwnerGroup() == null) ? 
                    "" : ticketStatusReopen.getOwnerGroup();
                }
                
                ticketSts.setChangeBy(owner);
                ticketSts.setStatus(STATUS_TARGET);
                ticketSts.setOwner("");
                ticketSts.setStatusCurrent(STATUS_TARGET);
                ticketSts.setAssignedOwnerGroup(ownergroupReopen);
                
                boolean checkSourceTicket = arrayManipulation
                .SearchDataOnArray(
                    SOURCETICKETLIST, 
                    sourceTicket
                );

                boolean checkOwnerGroupEBIS = arrayManipulation.SearchDataOnArray(
                  custSegmentHSIList,
                  customerSegment
                );

                if(checkOwnerGroupEBIS && !checkSourceTicket) {
                  String ow = getOwnergroupOnTkMapping(ticketSts, processId);
                  ownergroupReopen = (ow.length()>0) ? ow : ownergroupReopen;
                  ticketSts.setAssignedOwnerGroup(ownergroupReopen);
                }
                
//                executor.submit(() -> {
//                  try {
                    // send mycx & mihx
                    sendToMycxAndMyihx(channel, STATUS_TARGET, ticketSts);
                    
//                  } catch(Exception ex) {
//                    logInfo.Log(getClass().getName(), ex.getMessage());
//                  }
//                  return null;
//                });
                
                // INSERT STATUS LOG, SEND OT MYCX & MYIHX
                // To Target
                updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                ticketSts.setStatusTracking("false");
                dao.insertTicketStatus(ticketSts);
                
                // CLEAR OWNER & MEMO, UPDATE OWNERGROUP TERBARU, UPDATE STATUS TARGET
                updateQueryCollectionDao.updateDataAfterRunProcess(
                    processId,
                    ownergroupReopen, 
                    STATUS_TARGET, 
                    statusSCCValidation, 
                    runProccess,
                    false,
                    lastState
                );
                
                break;
              case "PENDING" :
                JSONArray arr_ = selectCollections.getConfMappingOwnergroup(ticketSts);
                ownergroupReopen = arrayManipulation.getOwnergroupOnJsonArray(arr_, channel, customerSegment);
                ticketSts.setAssignedOwnerGroup(ownergroupReopen);
                
                ticketSts.setStatus("PENDING");
                ticketSts.setChangeBy(owner);
                ticketSts.setOwner("");
                ticketSts.setStatusCurrent(STATUS_TARGET);
                
                // send mycx & mihx
//                executor.submit(() -> {
//                  try {
                    sendToMycxAndMyihx(channel, STATUS_TARGET, ticketSts);
//                  } catch(Exception ex) {
//                    logInfo.Log(getClass().getName(), ex.getMessage());
//                  }
//                  return null;
//                });
                
                // INSERT STATUS LOG, SEND OT MYCX & MYIHX
                // To Target
                updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                ticketSts.setStatusTracking("false");
                dao.insertTicketStatus(ticketSts);
                
                // CLEAR OWNER & MEMO, UPDATE OWNERGROUP TERBARU, UPDATE STATUS TARGET
                updateQueryCollectionDao.updateDataAfterRunProcess(
                    processId,
                    ownergroupReopen, 
                    STATUS_TARGET, 
                    statusSCCValidation, 
                    runProccess,
                    true,
                    lastState
                );
                
                arr_ = null;
                
                break;
              case "REQUESTPENDING" :
                JSONArray arr = selectCollections.getConfMappingOwnergroup(ticketSts);
                ownergroupReopen = arrayManipulation.getOwnergroupOnJsonArray(arr, channel, customerSegment);
                ticketSts.setAssignedOwnerGroup(ownergroupReopen);
                
                String sts = "REQUEST_PENDING_" + STATUS_TARGET;
                ticketSts.setStatus(sts);
                ticketSts.setChangeBy(owner);
                
                // send mycx & mihx
//                executor.submit(() -> {
//                  try {
                    // send mycx & mihx
                    sendToMycxAndMyihx(channel, STATUS_TARGET, ticketSts);
//                  } catch(Exception ex) {
//                    logInfo.Log(getClass().getName(), ex.getMessage());
//                  }
//                  return null;
//                });
                
                // INSERT STATUS LOG, SEND OT MYCX & MYIHX
                // To Target
                updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                ticketSts.setStatusTracking("false");
                dao.insertTicketStatus(ticketSts);
                
                // CLEAR OWNER & MEMO, UPDATE OWNERGROUP TERBARU, UPDATE STATUS TARGET
                updateQueryCollectionDao.updateDataAfterRunProcess(
                    processId,
                    ownergroupReopen, 
                    STATUS_TARGET, 
                    statusSCCValidation, 
                    runProccess,
                    true,
                    lastState
                );
                arr = null;
                break;
                
              case "RESOLVED" : 
                ticketSts.setChangeBy(owner);
                ticketSts.setStatus(STATUS_TARGET);
                ticketSts.setOwner("");
                ticketSts.setStatusCurrent(STATUS_TARGET);
                ticketSts.setAssignedOwnerGroup(ownergroupReopen);
                
                // send mycx & mihx
//                executor.submit(() -> {
//                  try {
                    // send mycx & mihx
                    sendToMycxAndMyihx(channel, STATUS_TARGET, ticketSts);
//                  } catch(Exception ex) {
//                    logInfo.Log(getClass().getName(), ex.getMessage());
//                  }
//                  return null;
//                });
                
                // INSERT STATUS LOG, SEND OT MYCX & MYIHX
                // To Target
                updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                ticketSts.setStatusTracking("false");
                dao.insertTicketStatus(ticketSts);
                
                // CLEAR OWNER & MEMO, UPDATE OWNERGROUP TERBARU, UPDATE STATUS TARGET
                updateQueryCollectionDao.updateDataAfterRunProcess(
                    processId,
                    ownergroupReopen, 
                    STATUS_TARGET, 
                    statusSCCValidation, 
                    runProccess,
                    false,
                    lastState
                );
                break;
                
              case "MEDIACARE" : 
                
                checkStatusInList =
                    arrayManipulation.SearchDataOnArray(
                        LISTSTATUSAFTERRESOLVED,
                        STATUS_TARGET
                    ); // check apakah status lewat resolved atau tidak
                checkStatusCurrentNotAfterResolved =
                    arrayManipulation.SearchDataOnArray(
                        LISTSTATUSAFTERRESOLVED,
                        LAST_STATUS
                    ); // check apakah status lewat resolved atau tidak
                checkActionDeadline =
                    arrayManipulation.SearchDataOnArray(
                        LISTDEADLINEACTSTAT,
                        actionStatus
                    ); // check apakah status lewat resolved atau tidak

                // apakah harus insert status resolved atau tidak
                if (checkStatusInList && !checkStatusCurrentNotAfterResolved && !checkActionDeadline) {
                  sendStatusResolve = true;
                }

                // ==> JIKA LAST STATUS ADA DI RESOLVED MAKA TIDAK PERLU ISI STATUS RESOLVED LAGI
                if(LAST_STATUS.equals(RESOLVED) && 
                    (actionStatus.equals(ACT_TOSALAMSIM) || actionStatus.equals(ACT_RESOLVE))
                  ) { sendStatusResolve = false; }
                // ==> END 
                
                if(sendStatusResolve) {
                  updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                  saveStatusResolved(owner); // SAVE STATUS RESOLVED
                  TimeUnit.SECONDS.sleep(1); // sleep process 1 detik
                  ticketSts.setChangeBy("SYSTEM");
                } else {
                  ticketSts.setChangeBy(owner);
                }
                
                ownergroupReopen = getMappingOwnergroup(customerSegment);
                
                // KONDISI REQUEST PENDING DI REJECT BALIK KE OWNERGROUP LAST
                if ("REQUEST_REJECTED".equalsIgnoreCase(pendingStatus)
                    && ACT_AFTER_REQUEST.equals(actionStatus)) {
                  ticketStatusReopen = selectCollections.getLastOwnerGroupByTicketID(ticketId, "1");
                  ownergroupReopen = (ticketStatusReopen.getOwnerGroup() == null) ? 
                    "" : ticketStatusReopen.getOwnerGroup();
                }
                
                
                // PERUBAHAN OWNERGROUP
                ticketSts.setAssignedOwnerGroup(ownergroupReopen);
                ticketSts.setChangeBy("SYSTEM");
                ticketSts.setStatus(STATUS_TARGET);
                ticketSts.setOwner("");
                ticketSts.setStatusCurrent(STATUS_TARGET);
                
                // send mycx & mihx
//                executor.submit(() -> {
//                  try {
                    // SEND TO MYTENS
                    if (arrayManipulation.SearchDataOnArray(custSegmentHSIList, customerSegment)
                        && channel.equalsIgnoreCase("35")) {
                        if (arrayManipulation.SearchDataOnArray(TICKET35, STATUS_TARGET)) {
                            logicToOtherAPI.UpdateMYTENS(ticketSts, STATUS_TARGET, TOKEN);
                        }
                    }
                    
                    // send mycx & mihx
                    sendToMycxAndMyihx(channel, STATUS_TARGET, ticketSts);
//                  } catch(Exception ex) {
//                    logInfo.Log(getClass().getName(), ex.getMessage());
//                  }
//                  return null;
//                });
                
                // INSERT STATUS LOG, SEND OT MYCX & MYIHX
                // To Target
                updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                ticketSts.setStatusTracking("false");
                dao.insertTicketStatus(ticketSts);
                
                statusSCCValidation = "INVALID";
                // CLEAR OWNER & MEMO, UPDATE OWNERGROUP TERBARU, UPDATE STATUS TARGET
                updateQueryCollectionDao.updateDataAfterRunProcess(
                    processId,
                    ownergroupReopen, 
                    STATUS_TARGET, 
                    statusSCCValidation, 
                    runProccess,
                    false,
                    lastState
                );
                
                break;
              case "SALAMSIM" : 
                checkStatusInList =
                    arrayManipulation.SearchDataOnArray(
                        LISTSTATUSAFTERRESOLVED,
                        STATUS_TARGET
                    ); // check apakah status lewat resolved atau tidak
                checkStatusCurrentNotAfterResolved =
                    arrayManipulation.SearchDataOnArray(
                        LISTSTATUSAFTERRESOLVED,
                        LAST_STATUS
                    ); // check apakah status lewat resolved atau tidak
                checkActionDeadline =
                    arrayManipulation.SearchDataOnArray(
                        LISTDEADLINEACTSTAT,
                        actionStatus
                    ); // check apakah status lewat resolved atau tidak

                // apakah harus insert status resolved atau tidak
                if (checkStatusInList && !checkStatusCurrentNotAfterResolved && !checkActionDeadline) {
                  sendStatusResolve = true;
                }

                // ==> JIKA LAST STATUS ADA DI RESOLVED MAKA TIDAK PERLU ISI STATUS RESOLVED LAGI
                if(LAST_STATUS.equals(RESOLVED) && 
                    (actionStatus.equals(ACT_TOSALAMSIM) || actionStatus.equals(ACT_RESOLVE))
                  ) { sendStatusResolve = false; }
                // ==> END 
                
                if(sendStatusResolve) {
                  updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                  saveStatusResolved(owner); // SAVE STATUS RESOLVED
                  TimeUnit.SECONDS.sleep(1); // sleep process 1 detik
                  ticketSts.setChangeBy("SYSTEM");
                } else {
                  ticketSts.setChangeBy(owner);
                }
                ownergroupReopen = getMappingOwnergroup(customerSegment);
                
                // KONDISI REQUEST PENDING DI REJECT BALIK KE OWNERGROUP LAST
                if ("REQUEST_REJECTED".equalsIgnoreCase(pendingStatus)
                    && ACT_AFTER_REQUEST.equals(actionStatus)) {
                  ticketStatusReopen = selectCollections.getLastOwnerGroupByTicketID(ticketId, "1");
                  ownergroupReopen = (ticketStatusReopen.getOwnerGroup() == null) ? 
                    "" : ticketStatusReopen.getOwnerGroup();
                }
                
                // PERUBAHAN OWNERGROUP
                ticketSts.setOwner("");
                ticketSts.setStatusCurrent(STATUS_TARGET);
                ticketSts.setAssignedOwnerGroup(ownergroupReopen);
                ticketSts.setStatus(STATUS_TARGET);
                
                // send mycx & mihx
//                executor.submit(() -> {
//                  try {
                    
                    if (customerSegment.equalsIgnoreCase("DCS")) {
                      mstParamSalamsim = mstParamDao.getUrl("update_salamsim_to_mycx");
                      statusToMycx.sendStatusSalamsimWithToken(
                          ticketSts, 
                          mstParamSalamsim, 
                          ACT_RESOLVE,
                          TOKEN
                      ); // MASUK KE STATUS SALAMSIM NGIRIM IN_STATUS = 11
                    }
                    
                    // SEND TO MYTENS
                    if (arrayManipulation.SearchDataOnArray(custSegmentHSIList, customerSegment)
                        && channel.equalsIgnoreCase("35")) {
                        if (arrayManipulation.SearchDataOnArray(TICKET35, STATUS_TARGET)) {
                            logicToOtherAPI.UpdateMYTENS(ticketSts, STATUS_TARGET, TOKEN);
                        }
                    }
                    
                    // send mycx & mihx
                    sendToMycxAndMyihx(channel, STATUS_TARGET, ticketSts);
//                  } catch(Exception ex) {
//                    logInfo.Log(getClass().getName(), ex.getMessage());
//                  }
//                  return null;
//                });
                
                // INSERT STATUS LOG, SEND OT MYCX & MYIHX
                // To Target
                updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                ticketSts.setStatusTracking("false");
                dao.insertTicketStatus(ticketSts);
                
                // CLEAR OWNER & MEMO, UPDATE OWNERGROUP TERBARU, UPDATE STATUS TARGET
                statusSCCValidation = "INVALID";
                updateQueryCollectionDao.updateDataAfterRunProcess(
                    processId,
                    ownergroupReopen, 
                    STATUS_TARGET, 
                    statusSCCValidation, 
                    runProccess,
                    false,
                    lastState
                );
                
                break;
              case "CLOSED" : 
                checkStatusInList =
                    arrayManipulation.SearchDataOnArray(
                        LISTSTATUSAFTERRESOLVED,
                        STATUS_TARGET
                    ); // check apakah status lewat resolved atau tidak
                checkStatusCurrentNotAfterResolved =
                    arrayManipulation.SearchDataOnArray(
                        LISTSTATUSAFTERRESOLVED,
                        LAST_STATUS
                    ); // check apakah status lewat resolved atau tidak
                checkActionDeadline =
                    arrayManipulation.SearchDataOnArray(
                        LISTDEADLINEACTSTAT,
                        actionStatus
                    ); // check apakah status lewat resolved atau tidak

                // apakah harus insert status resolved atau tidak
                if (checkStatusInList && !checkStatusCurrentNotAfterResolved && !checkActionDeadline) {
                  sendStatusResolve = true;
                }

                // ==> JIKA LAST STATUS ADA DI RESOLVED MAKA TIDAK PERLU ISI STATUS RESOLVED LAGI
                if(LAST_STATUS.equals(RESOLVED) && 
                    (actionStatus.equals(ACT_TOSALAMSIM) || actionStatus.equals(ACT_RESOLVE))
                  ) { sendStatusResolve = false; }
                // ==> END 
                
                if(sendStatusResolve) {
                  updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                  saveStatusResolved(owner); // SAVE STATUS RESOLVED
                  TimeUnit.SECONDS.sleep(1); // sleep process 1 detik
                  ticketSts.setChangeBy("SYSTEM");
                } else {
                  ticketSts.setChangeBy(owner);
                }
                
                // PERUBAHAN OWNERGROUP
                ownergroupReopen = getMappingOwnergroup(customerSegment);
                
                // KONDISI REQUEST PENDING DI REJECT BALIK KE OWNERGROUP LAST
                if ("REQUEST_REJECTED".equalsIgnoreCase(pendingStatus)
                    && ACT_AFTER_REQUEST.equals(actionStatus)) {
                  ticketStatusReopen = selectCollections.getLastOwnerGroupByTicketID(ticketId, "1");
                  ownergroupReopen = (ticketStatusReopen.getOwnerGroup() == null) ? 
                    "" : ticketStatusReopen.getOwnerGroup();
                }
                
                ticketSts.setAssignedOwnerGroup(ownergroupReopen);
                ticketSts.setOwner("");
                ticketSts.setStatusCurrent(STATUS_TARGET);
                ticketSts.setStatus(STATUS_TARGET);
                
                
                    // ==> LOGICAL CHECK SCC CODE == CODE VALIDATION
                if ((DRAFT.equals(LAST_STATUS) || ANALYSIS.equals(LAST_STATUS))
                    && "TECHNICAL".equalsIgnoreCase(technicalStatus)
                    && !"FISIK".equalsIgnoreCase(classificationType)) {

                  if(CLOSED.equals(STATUS_TARGET) && 

                    actionStatusTicket.CheckSccCodeValidation(ticketSts)) {
                    String CLOSEDBYCODE = "33";
                    statusSCCValidation = "VALID";

                    updateQueryCollectionDao.UpdateClosedReopenByParentId(
                      processId, 
                      CLOSEDBYCODE
                    ); // UPDATE CLOSEDBY TO DB

                  } else {
                     statusSCCValidation = "INVALID";
                  }

                }
                
//                executor.submit(() -> {
//                  try {
                    if (LAST_STATUS.equals(SALAMSIM) 
                        && customerSegment.equalsIgnoreCase("DCS")) {
                      mstParamSalamsim = mstParamDao.getUrl("update_salamsim_to_mycx");
                      statusToMycx.sendStatusSalamsimWithToken(
                          ticketSts, 
                          mstParamSalamsim, 
                          ACT_RESOLVE,
                          TOKEN
                      ); // DARI STATUS SALAMSIM KE STATUS CLOSED NGIRIM IN_STATUS = 13
                    }
                    
                    // send mycx & mihx
                    sendToMycxAndMyihx(channel, STATUS_TARGET, ticketSts);
                    
                    
//                  } catch(Exception ex) {
//                    logInfo.Log(getClass().getName(), ex.getMessage());
//                  }
//                  
//                  return null;
//                });
                
                if(!"DCS".equalsIgnoreCase(customerSegment)){
                  ttrE2E = new TtrEndToEnd();
                  ttrE2E.getTtrEndToEnd(ticketId, customerSegment);
                }
                
                updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketSts);
                ticketSts.setStatusTracking("false");
                dao.insertTicketStatus(ticketSts);
                
                // CLEAR OWNER & MEMO, UPDATE OWNERGROUP TERBARU, UPDATE STATUS TARGET
                updateQueryCollectionDao.updateDataAfterRunProcess(
                    processId,
                    ownergroupReopen, 
                    STATUS_TARGET, 
                    statusSCCValidation, 
                    runProccess,
                    false,
                    lastState
                );
                
                
                break;
              default:
                // send mycx & mihx
                ticketSts.setStatus(LAST_STATUS);
                sendToMycxAndMyihx(channel, LAST_STATUS, ticketSts);
            }
            
          } else {
            logInfo.Log(getClass().getName(), "TICKET ID kosong");
          }
        } else {
          logInfo.Log(getClass().getName(), "RUN PROCESS BUKAN 1");
        }
      } else {
        logInfo.Log(getClass().getName(), "PROCESS ID KOSONG");
      }

    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
//      logicToOtherAPI = null;
//      insertCollection = null;
//      mstParamMycx = null;
//      mstParamMyIhx = null;
//      mstParamSalamsim = null;
//      mstParamDao = null;
//      statusToMycx = null;
//      myihx = null;
//      selectCollections = null;
//      actionStatusTicket = null;
//      _RESTAPI = null;
//      updateQueryCollectionDao = null;
//      dao = null;
//      ticketStatusReopen = null;
//      arrayManipulation = null;
//      ttrE2E = null;
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
    return null;
  }
  
  private void saveStatusResolved(String owner) throws Exception {
      ticketSts.setStatus(RESOLVED);
      ticketSts.setStatusCurrent(RESOLVED);
      ticketSts.setChangeBy(owner);
      ticketSts.setOwner("");
      dao.insertTicketStatus(ticketSts); // INSERT TICKET STATUS RESOLVED
  }
  
  private String getMappingOwnergroup(String customerSegment) {
    boolean checkOwnerGroupHSI = arrayManipulation.SearchDataOnArray(
                  custSegmentHSIList,
                  customerSegment
                );
    String ownergroupMapping="";
    if (checkOwnerGroupHSI) { // check kondisi "DES", "DGS", "DBS"
      ownergroupMapping = "TIER1_TENESA";
    } else if ("DCS".equals(customerSegment)) {
      ownergroupMapping = "TIER1-BDG-SLMSPTK";
    } else if ("DWS".equals(customerSegment)) {
      String customerID = (ticketSts.getCustomerId() == null) 
          ? "" : ticketSts.getCustomerId();
      if (customerID.equalsIgnoreCase("C3700008")) {
        ownergroupMapping = "TIER1TELKOMSEL";
      } else {
        ownergroupMapping = "TIER1DWS";
      }
    }
    return ownergroupMapping;
  }
  
  final void sendToMycxAndMyihx(String channel, String STATUS_TARGET, TicketStatus ticketSts) throws Exception {
    
    // CALL MASTER PARAM GET URL MYCX & MYIHX
    mstParamMycx = mstParamDao.getUrl("update_status_ticket_to_mycx");
    mstParamMyIhx = mstParamDao.getUrl("update_status_ticket_to_myihx");
    statusToMycx.updateStatusTicketWithToken(ticketSts, mstParamMycx, TOKEN);
    if ("40".equals(channel)
        && (MEDIACARE.equalsIgnoreCase(STATUS_TARGET)
            || RESOLVED.equalsIgnoreCase(STATUS_TARGET)
            || CLOSED.equalsIgnoreCase(STATUS_TARGET))) {
      ticketSts.setStatus(STATUS_TARGET);
      myihx.updateStatusToMyihxWithToken(ticketSts, mstParamMyIhx, TOKEN);
    }

  }
  
  private String getOwnergroupOnTkMapping(TicketStatus ticketStatus, String processId) {
    String ownergroup = "";
    try {
      String symptomp = (ticketSts.getSymptomId()== null) ? 
          "" : ticketSts.getSymptomId(); // record id on ticket
      String workzone = (ticketSts.getWorkZone()== null) ? 
          "" : ticketSts.getWorkZone(); // record id on ticket
      String customerSegment = (ticketSts.getCustomerSegment() == null) ? 
          "" : ticketSts.getCustomerSegment().toUpperCase().trim();
      
      mstParamMycx = mstParamDao.getUrl("list_tk_mapping");
      JSONObject json = new JSONObject();
      json.put("classification_id", symptomp);
      json.put("workzone", workzone);
      json.put("customer_segment", customerSegment);

      String ownergroupRes = logicToOtherAPI.GET(mstParamMycx, json);

      Object obj = new JSONTokener(ownergroupRes).nextValue();
      JSONObject jsonRes = (JSONObject) obj;
      int size = jsonRes.getInt("size");
      if(size > 0) {
        for(int x=0; x<size; x++) {
          JSONArray arr = jsonRes.getJSONArray("data");
          JSONObject arrData = arr.getJSONObject(x);
          String ow_classificationId = arrData.getString("classification_id");
          String ow_workzone = arrData.getString("workzone");
          String ow_customer_segment = arrData.getString("customer_segment");
          String ow_ownergroup = arrData.getString("person_owner_group");
          String ow_finalcheck = arrData.getString("final_check");


          if(symptomp.equals(ow_classificationId) &&
              customerSegment.equals(ow_customer_segment) && 
              workzone.equals(ow_workzone) ) {
//              ow_finalcheck.equals("1")
              //UPDATE DATA
              ownergroup = ow_ownergroup;
//              updateQueryCollectionDao.UpdateOwnergroup(processId, ow_ownergroup);
              break;
          }
        }
      }
      
    } catch(Exception ex) {
    }
    return ownergroup;
  }
}
