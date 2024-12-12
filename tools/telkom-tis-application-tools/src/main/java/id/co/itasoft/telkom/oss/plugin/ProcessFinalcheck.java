/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GlobalQueryInsertCollection;
import id.co.itasoft.telkom.oss.plugin.dao.GlobalQuerySelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogicToOtherAPI;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.MeasureIboosterGamasDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateQueryCollectionDao;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.function.SendStatusToMYIHX;
import id.co.itasoft.telkom.oss.plugin.function.SendStatusToMycx;
import id.co.itasoft.telkom.oss.plugin.model.FinalcheckActModel;
import id.co.itasoft.telkom.oss.plugin.model.ListEnum;
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author asani
 */
public class ProcessFinalcheck extends DefaultApplicationPlugin {

  private final String pluginName =
      "Telkom New OSS - Ticket Incident Services - Process Finalcheck";
  LogInfo logInfo = new LogInfo();
  TicketStatus ticketStatus;
  ArrayManipulation arrayManipulation;
  GlobalQuerySelectCollections gqsc;
  FinalcheckActModel fam;
  ListIbooster listIbooster;
  UpdateQueryCollectionDao updateQueryCollectionDao;
  GlobalQueryInsertCollection insertCollection;
  MeasureIboosterGamasDao mib;
  InsertRelatedRecordDao irdao;
  MasterParam paramWO, mstParamMyCx, mstParamMyIhx, mstParamSalamsim;
  MasterParamDao paramDao;
  RESTAPI _RESTAPI;
  SendStatusToMYIHX sendStatusToMYIHX;
  SendStatusToMycx sendStatusToMycx;
  InsertTicketStatusLogsDao dao;
  LogicToOtherAPI logicToOtherAPI;
  String TOKEN = "";

  public ProcessFinalcheck() {
    logicToOtherAPI = new LogicToOtherAPI();
    dao = new InsertTicketStatusLogsDao();
    sendStatusToMycx = new SendStatusToMycx();
    sendStatusToMYIHX = new SendStatusToMYIHX();
    _RESTAPI = new RESTAPI();
    mstParamMyCx = new MasterParam();
    mstParamMyIhx = new MasterParam();
    mstParamSalamsim = new MasterParam();
    paramWO = new MasterParam();
    ticketStatus = new TicketStatus();
    arrayManipulation = new ArrayManipulation();
    gqsc = new GlobalQuerySelectCollections();
    fam = new FinalcheckActModel();
    listIbooster = new ListIbooster();
    updateQueryCollectionDao = new UpdateQueryCollectionDao();
    insertCollection = new GlobalQueryInsertCollection();
    mib = new MeasureIboosterGamasDao();
    irdao = new InsertRelatedRecordDao();
    paramDao = new MasterParamDao();
    TOKEN = _RESTAPI.getToken();
  }

  @Override
  public Object execute(Map map) {

    AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
    WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
    String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
    WorkflowUserManager workflowUserManager =
        (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

    String ANALYSIS = ListEnum.ANALYSIS.toString();
    String BACKEND = ListEnum.BACKEND.toString();
    String FINALCHECK = ListEnum.FINALCHECK.toString();
    String RESOLVED = ListEnum.RESOLVED.toString();
    String MEDIACARE = ListEnum.MEDIACARE.toString();
    String SALAMSIM = ListEnum.SALAMSIM.toString();
    String CLOSED = ListEnum.CLOSED.toString();

    String ACT_RESOLVE = ListEnum.RESOLVE.toString();
    String ACT_REOPEN = ListEnum.REOPEN.toString();

    try {
      // get mycx url

      // GET DATA TICKET BY PROCESS ID
      JSONObject getConfiguration = gqsc.getConfigurationMapping();
      boolean CONFIGURE_IBOOSTER = getConfiguration.getBoolean("checkIbooster");

      ticketStatus = gqsc.getTicketId(processId);

      String username = workflowUserManager.getCurrentUsername();
      
      String HSILIST[] = {"DBS", "DES", "DGS"};
      String TICKET35[] = {ANALYSIS, BACKEND, MEDIACARE, SALAMSIM, CLOSED}; // mytens
      String SOURCETICKETLIST[] = {"GAMAS", "FALLOUT", "PROACTIVE"};
      String SERVICETYPELIST[] = {"INTERNET", "IPTV", "VOICE"};
      String ACTSOLLIST[] = {
        "SQM001", 
        "SQM002", 
        "SQM005", 
        "SQM006", 
        "SQM008",
        "SQM009",
        "SQM010", 
        "SQM011"
      };
      
      // CHECK ACTSOL
//      ArrayList<String> arrlist = new ArrayList();
//      arrlist.add("SQM001");
//      arrlist.add("SQM002");
//      arrlist.add("SQM005");
//      arrlist.add("SQM006");
//      arrlist.add("SQM008");
//      arrlist.add("SQM009");
//      arrlist.add("SQM010");
//      arrlist.add("SQM011");

      String TICKETID = (ticketStatus.getTicketId() == null) ? 
          "" : ticketStatus.getTicketId().toUpperCase().trim();
      String TECHNOLOGY = (ticketStatus.getTechnology() == null) ? 
          "" : ticketStatus.getTechnology().toUpperCase().trim();
      String LAST_STATUS = (ticketStatus.getStatusCurrent() == null) ? 
          "" : ticketStatus.getStatusCurrent().toUpperCase().trim();
      String STATUSTARGET = (ticketStatus.getStatus() == null) ? 
          "" : ticketStatus.getStatus().toUpperCase().trim();
      String ACTSOL = (ticketStatus.getActualSolution() == null) ? 
          "" : ticketStatus.getActualSolution().trim();
      String owner = (ticketStatus.getOwner() == null) ? 
          "" : ticketStatus.getOwner();
      String CLOSEDBYCODE = "";
      String custSegment = (ticketStatus.getCustomerSegment() == null) ? 
          "" : ticketStatus.getCustomerSegment().toUpperCase().trim();
      String serviceType = (ticketStatus.getServiceType() == null) ? 
          "" : ticketStatus.getServiceType().toUpperCase().trim();
      String sccVoice = (ticketStatus.getScc_voice() == null) ? 
          "" : ticketStatus.getScc_voice().toUpperCase().trim();
      String serviceNo = (ticketStatus.getServiceNo() == null) ? 
          "" : ticketStatus.getServiceNo().toUpperCase().trim();
      String parentId = (ticketStatus.getId() == null) ? 
          "" : ticketStatus.getId().trim();
      String currentOwnergroup = (ticketStatus.getAssignedOwnerGroup() == null) ? 
          "" : ticketStatus.getAssignedOwnerGroup().trim();
      String classificationFlag = (ticketStatus.getClassificationFlag() == null) ? 
          "" : ticketStatus.getClassificationFlag().trim(); // TECHNICAL || NONTECHNICAL
      String childGamas = (ticketStatus.getChild_gamas() == null) ? 
          "" : ticketStatus.getChild_gamas();
      String actualSolution = (ticketStatus.getActualSolution() == null) ? 
          "" : ticketStatus.getActualSolution();
      String ibooster = (ticketStatus.getIbooster() == null) ? 
          "" : ticketStatus.getIbooster();
      String sccInternet = (ticketStatus.getScc_internet() == null) ? 
          "" : ticketStatus.getScc_internet();
      String lastState = (ticketStatus.getLast_state() == null) ? 
          "" : ticketStatus.getLast_state().trim();
      String sourceTicket = (ticketStatus.getSourceTicket() == null) ? 
          "" : ticketStatus.getSourceTicket().toUpperCase().trim();
      String customerId = (ticketStatus.getCustomerId() == null) ? 
          "" : ticketStatus.getCustomerId().trim();
      String channel = (ticketStatus.getChannel() == null) ? 
          "" : ticketStatus.getChannel().trim();
      String runProcessStr = (ticketStatus.getRunProcess() == null) ? 
          "" : ticketStatus.getRunProcess().trim();
      String customerID =(ticketStatus.getCustomerId() == null) ? 
          "" : ticketStatus.getCustomerId();
      String TSC_RESULT = (ticketStatus.getTscMeasurement() == null) ? 
          "" : ticketStatus.getTscMeasurement().trim();
      
      String measurementValue = "";
      
      boolean checkHSI = arrayManipulation
          .SearchDataOnArray(
              HSILIST,
              custSegment
          );
      
      boolean runProcess = (runProcessStr.equalsIgnoreCase("2")) ? true : false;

      if (runProcess) {
        if ("FINALCHECK".equalsIgnoreCase(LAST_STATUS)) {
          
//          ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(135);
//          executor.setKeepAliveTime(30, TimeUnit.SECONDS);
//          executor.allowCoreThreadTimeOut(true);

          Request request;
          boolean isrtTktRelvBool = false;
          boolean updtOwnergroupBool = false;
          boolean NEXTPROCESSTARGET = true;
          boolean checkActualSolution = true;
          boolean technicalCheckIbooster = false;
          boolean isReopen = false;
          boolean CLOSEDBYBOOL = false;
          boolean UKURIBOOSTER = false;
          boolean serviceWorkoder = false;
          boolean workoderOpenStatus = false;
          
          boolean checkServiceType = arrayManipulation
              .SearchDataOnArray(
                  SERVICETYPELIST, 
                  serviceType
              );

          
          if ("false".equalsIgnoreCase(childGamas)) {
            // khusus yg TIDAK di resolve oleh induk gamas/ TIKET BIASA
            // CHECK ACTUAL SOLUTION
            if (actualSolution.isEmpty()) {
              // JIKA ACTUAL SOLUTION KOSONG MASUK KE FORM FINALCHECK
              fam.setAction_status("TO_ACTIVITY");
              fam.setTicket_status(FINALCHECK);
              STATUSTARGET = FINALCHECK;
              NEXTPROCESSTARGET = false;
              checkActualSolution = false;
            } 
          }
          
          // CHECK ACTUAL SOLUTION
          String IBOOSTERUKURRESULT = "";
          if (checkActualSolution) {
            // CHECK CUSTOMER DCS DAN SERVICE TYPE VOICE, IPTV, INTERNET
            if ("DCS".equalsIgnoreCase(custSegment) && checkServiceType) {
              
              JSONObject woRequest = new JSONObject();
              boolean status = false;
              String responseBody = "";
              
              // 1. CHECK WORKORDER
              paramWO = paramDao.getUrl("list_work_order");
              HttpUrl.Builder httpBuilder = HttpUrl.parse(paramWO.getUrl()).newBuilder();
              httpBuilder.addQueryParameter("externalID1", TICKETID);
              request =
                  new Request.Builder()
                      .url(httpBuilder.build())
                      .addHeader("api_key", paramWO.getApi_key().toString())
                      .addHeader("api_id", paramWO.getApi_id().toString())
                      .build();

              woRequest = _RESTAPI.CALLAPIHANDLER(request);
              status = woRequest.getBoolean("status");
              responseBody = woRequest.getString("msg");
              insertCollection.insertWorkLogs(
                  parentId, 
                  TICKETID, 
                  currentOwnergroup, 
                  "CHECK WORKORDER", 
                  responseBody
              );
              
              if (status) {
                serviceWorkoder = true;

                // check status wo
                Object json = new JSONTokener(responseBody).nextValue();
                JSONObject WORKORDER = (JSONObject) json;
                JSONArray DATA_WORKORDER = WORKORDER.getJSONArray("data");
                int totalWorkorder = (WORKORDER.has("total")) ? WORKORDER.getInt("total") : 0;

                if(totalWorkorder>0) {
                  
                  boolean nextProcess = false;
                  ArrayList<String> woStatusList = new ArrayList<String>();

                  // CHECK STATUS DI LIST WO
                  for (int i = 0; i < DATA_WORKORDER.length(); i++) {
                    JSONObject wojson = DATA_WORKORDER.getJSONObject(i);
                    String STATUSWO = (wojson.has("status")) ? wojson.getString("status") : "";
                    woStatusList.add(STATUSWO);
                  }

                  if (woStatusList.contains("ASSIGNED")
                      || woStatusList.contains("OPEN")
                      || woStatusList.contains("")
                      || woStatusList.contains("HOLD")) {
                    nextProcess = false;
                    workoderOpenStatus = false;
                  } else {
                    nextProcess = true;
                    workoderOpenStatus = true;
                  }
                  
                  if (nextProcess) {
                    if (!CONFIGURE_IBOOSTER) {
                      technicalCheckIbooster = true;
                      measurementValue = "OTHERS";
                      // listIbooster = gqsc.getIbooster(fam.getServiceNo(), fam.getRealm(), TICKETID);
                      listIbooster.setMessage("ibooster check is turned off");
                      listIbooster.setOnuRxPwr("null");
                      listIbooster.setOnuTxPwr("null");
                      listIbooster.setOltRxPwr("null");
                      listIbooster.setOltTxPwr("null");
                      listIbooster.setMeasurementCategory(measurementValue);
                      fam.setIbooster(measurementValue);
                      updateQueryCollectionDao.updateIbooster(
                          listIbooster, 
                          processId
                      ); // UPDATE IBOOSTER TO DB

                      String kategoriUkur = "Hasil pengukuran ibooster " + measurementValue;
                      insertCollection.insertWorkLogs(
                          parentId, 
                          TICKETID, 
                          currentOwnergroup, 
                          kategoriUkur, 
                          "ibooster check is turned off"
                      ); // INSERT IBOOSTER TO WORKLOGS

                    } else {
                      // CHECK TECHNOLOGY NON FIBER
                      boolean checkTechnologyNonFiber = gqsc.checkTechnology(TICKETID);
                      boolean CHECKACTSOL = arrayManipulation.SearchDataOnArray(
                          ACTSOLLIST, 
                          actualSolution
                      );

                      if (!"COPPER".equalsIgnoreCase(TECHNOLOGY)
                          && !CHECKACTSOL
                          && !checkTechnologyNonFiber) {
                        technicalCheckIbooster = true;

                        fam.setIbooster(ibooster);
                        fam.setScc_internet(sccInternet);
                        fam.setServiceType(serviceType);
                        fam.setScc_voice(sccVoice);
                        fam.setServiceNo(serviceNo);

                        if ("".equalsIgnoreCase(ibooster) || "UNSPEC".equalsIgnoreCase(ibooster)) {
                          UKURIBOOSTER = true;

                          /**
                           * UKUR IBOOSTER
                           * DAN CHECK TURN ON/OFF on CONFIGURATION
                           **/
                            listIbooster = gqsc.getIbooster(
                                serviceNo, 
                                fam.getRealm(), 
                                TICKETID
                            ); // PENGUKURAN IBOOSTER

                            measurementValue = listIbooster.getMeasurementCategory();
                            fam.setIbooster(measurementValue);
                            updateQueryCollectionDao.updateIbooster(
                                listIbooster, 
                                processId
                            ); // UPDATE IBOOSTER TO DB

                            String kategoriUkur = "Hasil pengukuran ibooster " + measurementValue;
                            insertCollection.insertWorkLogs(
                                parentId, 
                                TICKETID, 
                                currentOwnergroup, 
                                kategoriUkur, 
                                ""
                            ); // INSERT IBOOSTER TO WORKLOGS
                        }
                      }
                    } 

                    // CHECK HASIL PENGUKURAN IBOOSTER  
                    if(!CONFIGURE_IBOOSTER) {
                      ibooster = fam.getIbooster();
                    } else {
                      if (UKURIBOOSTER) {
                        if (fam.getIbooster().isEmpty()) {
                          IBOOSTERUKURRESULT = "UNSPEC";
                        } else {
                          IBOOSTERUKURRESULT = fam.getIbooster();
                        }
                      } else {
                        IBOOSTERUKURRESULT = ibooster;
                      }
                    }
                    
                    // UNSPEC REOPEN TO BEFORE PROCESS
                    if ("UNSPEC".equalsIgnoreCase(IBOOSTERUKURRESULT)
                        && !"VOICE".equalsIgnoreCase(serviceType)) {
                      logInfo.Log(getClassName(), "REOPEN TICKET :");
                      fam.setAction_status(ACT_REOPEN);
                      fam.setTicket_status(BACKEND);
                      fam.setOwnergroup(currentOwnergroup);
                      STATUSTARGET = lastState;
                      NEXTPROCESSTARGET = true;
                      mib.deleteRelatedRecord(TICKETID);
                      irdao.updateStatusChildGamas(
                          "FALSE",
                          "SYSTEM", 
                          "", 
                          "", 
                          TICKETID
                      );
                    } else {
                      /** CHECK SOURCE GAMAS, PROACTIVE, FALLOUT */
                      boolean checkSourceTicket = arrayManipulation
                          .SearchDataOnArray(
                              SOURCETICKETLIST, 
                              sourceTicket
                          );

                      if (checkSourceTicket) {

                        if ("50".equalsIgnoreCase(channel)
                            && "PROACTIVE".equalsIgnoreCase(sourceTicket)
                            && IBOOSTERUKURRESULT.equalsIgnoreCase("SPEC")) {
                          fam.setAction_status(ACT_RESOLVE);
                          fam.setTicket_status(CLOSED);
                          fam.setOwnergroup("TIER1-BDG-SLMSPTK");

                          STATUSTARGET = CLOSED;
                          isrtTktRelvBool = true;
                          NEXTPROCESSTARGET = true;
                          updtOwnergroupBool = true;
                        } else {
                          fam.setAction_status(ACT_RESOLVE);
                          fam.setTicket_status(RESOLVED);
                          fam.setOwnergroup(currentOwnergroup);

                          STATUSTARGET = RESOLVED;
                          isrtTktRelvBool = false;
                          NEXTPROCESSTARGET = true;
                          updtOwnergroupBool = true;
                        }

                      } else {
                        // customer
                        // WO PASTI SUDAH COMPLETE ATAU CLOSED
                        if ("INTERNET".equalsIgnoreCase(serviceType)
                            || "IPTV".equalsIgnoreCase(serviceType)) {

                          // HARUS INSERT STATUS RESOLVED
                          NEXTPROCESSTARGET = true;
                          isrtTktRelvBool = true;
                          updtOwnergroupBool = true;
                          fam.setOwnergroup("TIER1-BDG-SLMSPTK");

                          if (!"UNSPEC".equalsIgnoreCase(IBOOSTERUKURRESULT)
                              && !"PASSED".equalsIgnoreCase(sccInternet)) {
                            fam.setAction_status(ACT_RESOLVE);
                            fam.setTicket_status(MEDIACARE);
                            STATUSTARGET = MEDIACARE;

                          } else if ("SPEC".equalsIgnoreCase(IBOOSTERUKURRESULT)
                              && "PASSED".equalsIgnoreCase(sccInternet)) {
                            fam.setAction_status(ACT_RESOLVE);
                            fam.setTicket_status(CLOSED);
                            STATUSTARGET = CLOSED;

                            CLOSEDBYBOOL = true; // CLOSEDBY
                            CLOSEDBYCODE = "25";
                          } else {
                            fam.setAction_status(ACT_RESOLVE);
                            fam.setTicket_status(MEDIACARE);
                            STATUSTARGET = MEDIACARE;

                          }
                        } else if ("VOICE".equals(serviceType)) {

                          // HARUS INSERT STATUS RESOLVED
                          NEXTPROCESSTARGET = true;
                          isrtTktRelvBool = true;
                          updtOwnergroupBool = true;
                          fam.setOwnergroup("TIER1-BDG-SLMSPTK");

                          if (!"PASSED".equalsIgnoreCase(sccVoice)) {
                            fam.setAction_status(ACT_RESOLVE);
                            fam.setTicket_status(MEDIACARE);
                            STATUSTARGET = MEDIACARE;
                          } else {
                            fam.setAction_status(ACT_RESOLVE);
                            fam.setTicket_status(CLOSED);
                            STATUSTARGET = CLOSED;
                            CLOSEDBYBOOL = true; // CLOSEDBY
                            CLOSEDBYCODE = "18";
                          }

                        } else {
                          fam.setAction_status(ACT_REOPEN);
                          fam.setTicket_status(BACKEND);
                          STATUSTARGET = lastState;
                          ticketStatus.setMemo("there is no logic mapping in the system");
                          insertCollection.insertWorkLogs(
                              parentId,
                              TICKETID,
                              currentOwnergroup,
                              "CHECK WORKORDER",
                              "there is no logic mapping in the system"
                          );

                          NEXTPROCESSTARGET = true;
                          isrtTktRelvBool = false;
                        }
                      }
                    }
                  } else {
                    fam.setAction_status(ACT_REOPEN);
                    fam.setTicket_status(BACKEND);
                    ticketStatus.setMemo("WO STATUS IS STILL OPEN");
                    NEXTPROCESSTARGET = true;
                    STATUSTARGET = lastState;
                    isrtTktRelvBool = false;
                    insertCollection.insertWorkLogs(
                        parentId, 
                        TICKETID, 
                        currentOwnergroup, 
                        "CHECK WORKORDER", 
                        "STATUS WO MASIH OPEN"
                    );
                  }
                  
                } else {
                  // gk ada wo jadi gk perlu ukur ibooster 
                  // HARUS INSERT STATUS RESOLVED
                  NEXTPROCESSTARGET = true;
                  isrtTktRelvBool = true;
                  updtOwnergroupBool = true;
                  fam.setOwnergroup("TIER1-BDG-SLMSPTK");
                  fam.setAction_status(ACT_RESOLVE);
                  fam.setTicket_status(MEDIACARE);
                  STATUSTARGET = MEDIACARE;
                }
                
              } else {
                serviceWorkoder = false;

                // JIKA SERVICE WO MATI
                fam.setAction_status(ACT_REOPEN);
                fam.setTicket_status(BACKEND);
                STATUSTARGET = BACKEND;

                ticketStatus.setMemo("Service Workorder Unvailable");
                NEXTPROCESSTARGET = true;
                isrtTktRelvBool = false;

                insertCollection.insertWorkLogs(
                    parentId,
                    TICKETID,
                    currentOwnergroup,
                    "CHECK WORKORDER",
                    "Service Workorder Unvailable"
                );
              }
            } else { //EBIS
              
              // 1. CHECK WORKORDER
              paramWO = paramDao.getUrl("list_work_order");
              HttpUrl.Builder httpBuilder = HttpUrl.parse(paramWO.getUrl()).newBuilder();
              httpBuilder.addQueryParameter("externalID1", TICKETID);
              request =
                  new Request.Builder()
                      .url(httpBuilder.build())
                      .addHeader("api_key", paramWO.getApi_key().toString())
                      .addHeader("api_id", paramWO.getApi_id().toString())
                      .build();

              JSONObject woRequest = _RESTAPI.CALLAPIHANDLER(request);
              boolean status = woRequest.getBoolean("status");
              String responseBody = woRequest.getString("msg");
              insertCollection.insertWorkLogs(
                  parentId, 
                  TICKETID, 
                  currentOwnergroup, 
                  "CHECK WORKORDER", 
                  responseBody
              );

              if (status) {
                serviceWorkoder = true;

                // check status wo
                Object json = new JSONTokener(responseBody).nextValue();
                JSONObject WORKORDER = (JSONObject) json;
                JSONArray DATA_WORKORDER = WORKORDER.getJSONArray("data");

                boolean nextProcess = false;
                ArrayList<String> woStatusList = new ArrayList<String>();

                // CHECK STATUS DI LIST WO
                for (int i = 0; i < DATA_WORKORDER.length(); i++) {
                  JSONObject wojson = DATA_WORKORDER.getJSONObject(i);
                  String STATUSWO = (wojson.has("status")) ? wojson.getString("status") : "";
                  woStatusList.add(STATUSWO);
                }

                if (woStatusList.contains("ASSIGNED")
                    || woStatusList.contains("OPEN")
                    || woStatusList.contains("")
                    || woStatusList.contains("HOLD")) {
                  nextProcess = false;
                  workoderOpenStatus = false;
                } else {
                  nextProcess = true;
                  workoderOpenStatus = true;
                }

                if (nextProcess) {
                  /** NON TECHNICAL */
                  fam.setAction_status(ACT_RESOLVE);
                  // HARUS INSERT STATUS RESOLVED
                  isrtTktRelvBool = true;

                  // CHECK customerSegment HSI ATAU BUKAN HSI(DGS,DES,DBS)
                  boolean checkServiceTypes = arrayManipulation
                      .SearchDataOnArray(
                          SERVICETYPELIST, 
                          serviceType
                      );

                  // CHECK EBIS
                  // DES,DBS,DGS
                  if (checkHSI) { 
                    if (checkServiceTypes) { 
                      // INTERNET, IPTV, VOICE - HSI
                      fam.setAction_status(ACT_RESOLVE);
                      isrtTktRelvBool = true;
                      updtOwnergroupBool = true;
                      if ("INTERNET".equalsIgnoreCase(serviceType)){
                        if (TSC_RESULT.equalsIgnoreCase("PASSED")) {
                          STATUSTARGET = CLOSED;
                          fam.setTicket_status(CLOSED);
                          
                          CLOSEDBYBOOL = true; // CLOSEDBY
                          CLOSEDBYCODE = "25";
                        } else {
                          STATUSTARGET = MEDIACARE;
                          fam.setTicket_status(MEDIACARE);
                        }
                      } else if("IPTV".equalsIgnoreCase(serviceType) ||
                          "VOICE".equals(serviceType)) {
                        if (TSC_RESULT.equalsIgnoreCase("PASSED")) {
                          STATUSTARGET = CLOSED;
                          fam.setTicket_status(CLOSED);
                          
                          CLOSEDBYBOOL = true; // CLOSEDBY
                          CLOSEDBYCODE = "25";
                        } else {
                          STATUSTARGET = MEDIACARE;
                          fam.setTicket_status(MEDIACARE);
                        }
                      } else {
                        STATUSTARGET = FINALCHECK;
                        fam.setTicket_status(FINALCHECK);
                        isrtTktRelvBool = false;
                        updtOwnergroupBool = false;
                      }

                      // HARUS INSERT STATUS RESOLVED
                      NEXTPROCESSTARGET = true;

                      if (checkHSI) { // check kondisi "DES", "DGS", "DBS"
                        fam.setOwnergroup("TIER1_TENESA");
                      } else if ("DCS".equals(custSegment)) {
                        fam.setOwnergroup("TIER1-BDG-SLMSPTK");
                      } else if ("DWS".equals(custSegment)) {
                        fam.setOwnergroup("TIER1-BDG-SLMSPTK");
                        if (customerID.equalsIgnoreCase("C3700008")) {
                          fam.setOwnergroup("TIER1TELKOMSEL");
                        } else {
                          fam.setOwnergroup("TIER1DWS");
                        }
                      }

                    } else { 
                      // DATIN
                      fam.setAction_status(ACT_RESOLVE);
                      fam.setTicket_status(FINALCHECK);
                      ticketStatus.setMemo("TICKET DATIN");

                      // HARUS INSERT STATUS RESOLVED
                      STATUSTARGET = FINALCHECK;
                      NEXTPROCESSTARGET = true;
                      isrtTktRelvBool = false;
                      updtOwnergroupBool = false;
                    }
                  } else { 
                    // SELAIN EBIS
                    fam.setAction_status(ACT_RESOLVE);
                    fam.setTicket_status(SALAMSIM);
                    STATUSTARGET = SALAMSIM;

                    // HARUS INSERT STATUS RESOLVED
                    NEXTPROCESSTARGET = true;
                    isrtTktRelvBool = true;
                    updtOwnergroupBool = true;

                    if (checkHSI) { // check kondisi "DES", "DGS", "DBS"
                      fam.setOwnergroup("TIER1_TENESA");
                    } else if ("DCS".equals(custSegment)) {
                      fam.setOwnergroup("TIER1-BDG-SLMSPTK");
                    } else if ("DWS".equals(custSegment)) {
                      fam.setOwnergroup("TIER1-BDG-SLMSPTK");

                      if (customerID.equalsIgnoreCase("C3700008")) {
                        fam.setOwnergroup("TIER1TELKOMSEL");
                      } else {
                        fam.setOwnergroup("TIER1DWS");
                      }
                    }
                  }
              } else {
                  fam.setAction_status(ACT_REOPEN);
                  fam.setTicket_status(BACKEND);
                  ticketStatus.setMemo("Service Workorder Unvailable");
                  NEXTPROCESSTARGET = true;
                  STATUSTARGET = lastState;
                  isrtTktRelvBool = false;
                  insertCollection.insertWorkLogs(
                      parentId, 
                      TICKETID, 
                      currentOwnergroup, 
                      "CHECK WORKORDER", 
                      "STATUS WO MASIH OPEN"
                  );
                }
              } else {
                serviceWorkoder = false;

                // JIKA SERVICE WO MATI
                fam.setAction_status(ACT_REOPEN);
                fam.setTicket_status(BACKEND);
                STATUSTARGET = BACKEND;

                ticketStatus.setMemo("Service Workorder Unvailable");
                NEXTPROCESSTARGET = true;
                isrtTktRelvBool = false;

                insertCollection.insertWorkLogs(
                    parentId,
                    TICKETID,
                    currentOwnergroup,
                    "CHECK WORKORDER",
                    "Service Workorder Unvailable"
                );
              }
            } // end else
          } else {
            // TIDAK MENGECEK ACTUAL SOLUTION
            NEXTPROCESSTARGET = false;
          }

          
          // GET MASTER PARAM
          mstParamMyCx = paramDao.getUrl("update_status_ticket_to_mycx");
          mstParamMyIhx = paramDao.getUrl("update_status_ticket_to_myihx");

          // INSERT TICKET STATUS RESOLVED KARENA TICKET AKAN MELEWATI RESOLVED
          // INSERT STATUS RESOLVED
          if (isrtTktRelvBool) {
            ticketStatus.setStatusTracking(TICKETID);
            ticketStatus.setStatus(RESOLVED);
            ticketStatus.setStatusCurrent(RESOLVED);

            if (owner.isEmpty()) {
              ticketStatus.setChangeBy("SYSTEM");
            } else {
              ticketStatus.setChangeBy(owner);
            }

            ticketStatus.setOwner("");
            if ("TRUE".equalsIgnoreCase(childGamas)) {
              ticketStatus.setMemo("Change of Status by Gamas");
            }

            updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketStatus);
            ticketStatus.setStatusTracking("false");
            insertCollection.insertTicketStatus(ticketStatus);

            // send to mycx
            sendStatusToMycx.updateStatusTicketWithToken(
                ticketStatus, 
                mstParamMyCx, 
                TOKEN
            );
            
            // send to myihx
            if ("40".equals(channel)) {
              sendStatusToMYIHX.updateStatusToMyihxWithToken(
                  ticketStatus, 
                  mstParamMyIhx, 
                  TOKEN
              );
            }
          }

          TimeUnit.SECONDS.sleep(1);

          // INSERT TICKET STATUS TARGET
          if (NEXTPROCESSTARGET) {
            ticketStatus.setStatusTracking(TICKETID);
            ticketStatus.setStatus(STATUSTARGET);
            ticketStatus.setStatusCurrent(STATUSTARGET);
            ticketStatus.setAssignedOwnerGroup(fam.getOwnergroup());
            ticketStatus.setOwnerGroup(fam.getOwnergroup());
            ticketStatus.setChangeBy("SYSTEM");
            ticketStatus.setOwner("");
            if ("TRUE".equalsIgnoreCase(childGamas)) {
              ticketStatus.setMemo("Change of Status by Gamas");
            }
            insertCollection.insertTicketStatus(ticketStatus);

            sendStatusToMycx.updateStatusTicketWithToken(
                ticketStatus, 
                mstParamMyCx, 
                TOKEN
            );

            if (MEDIACARE.equalsIgnoreCase(STATUSTARGET)) {
              /** Send Status to MyIhx (Mediacare) */
              if ("40".equals(channel)) {
                sendStatusToMYIHX.updateStatusToMyihxWithToken(
                    ticketStatus, 
                    mstParamMyIhx, 
                    TOKEN
                );
              }
            }
          }

          if (updtOwnergroupBool) {
            dao.UpdateOwnergroup(processId, fam.getOwnergroup());
          }

          // UPDATE CLOSED BY
          if (CLOSEDBYBOOL) {
            updateQueryCollectionDao.UpdateClosedReopenByParentId(processId, CLOSEDBYCODE);
          }

          if (ACT_REOPEN.equals(fam.getAction_status())) {
            /**
             * NGIRIM WO REVIEW
             *
             * @param ticketStatus, statusWO, username
             */
            updateQueryCollectionDao.UpdateWorkorderReview(ticketStatus, "REVIEW", username);

            String summary = "";
            if (IBOOSTERUKURRESULT.equals("UNSPEC")) {
              summary = "REOPEN TICKET BECAUSE IBOOSTER IS UNSPEC";
            }
            if (!serviceWorkoder) {
              summary = "REOPEN TICKET BECAUSE SERVICE WORKORDER UNAVAILABLE";
            }

            if (!workoderOpenStatus) {
              summary = "REOPEN TICKET BECAUSE WORKORDER STATUS ARE OPEN";
            }

            if (workoderOpenStatus) {
              summary = "REOPEN TICKET BECAUSE THERE IS NO LOGIC IN THE SYSTEM";
            }

            insertCollection.insertWorkLogs(parentId, TICKETID, fam.getOwnergroup(), summary, "");
          }

          // UPDATE STATUS DB & WORKFLOW VARIABLED
          if ("TRUE".equalsIgnoreCase(childGamas)) {
            dao.UpdateStatusChildGamas(processId, "FALSE");
          }

          boolean updateDb = updateQueryCollectionDao.UpdateStatusAndActStat(fam, processId);
          
          // KIRIM MYTENS
          if (checkHSI && channel.equalsIgnoreCase("35")) {
            if (arrayManipulation.SearchDataOnArray(TICKET35, STATUSTARGET)) {
              logicToOtherAPI.UpdateMYTENS(
                  ticketStatus, 
                  STATUSTARGET,
                  TOKEN
              );
            }
          }

          if (SALAMSIM.equalsIgnoreCase(STATUSTARGET) && custSegment.equalsIgnoreCase("DCS")) {
            // MASUK KE STATUS SALAMSIM NGIRIM IN_STATUS = 11
            mstParamSalamsim = paramDao.getUrl("update_salamsim_to_mycx");
            sendStatusToMycx.sendStatusSalamsim(
                ticketStatus, 
                mstParamSalamsim, 
                "RESOLVE"
            );
          }
        }
      } else {
        // CLEAR RUN PROCESS
      }

    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      dao = null;
      sendStatusToMycx = null;
      sendStatusToMYIHX = null;
      _RESTAPI = null;
      mstParamMyCx = null;
      mstParamMyIhx = null;
      mstParamSalamsim = null;
      paramWO = null;
      ticketStatus = null;
      arrayManipulation = null;
      gqsc = null;
      fam = null;
      listIbooster = null;
      updateQueryCollectionDao = null;
      insertCollection = null;
      mib = null;
      irdao = null;
      paramDao = null;
    }

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
}
