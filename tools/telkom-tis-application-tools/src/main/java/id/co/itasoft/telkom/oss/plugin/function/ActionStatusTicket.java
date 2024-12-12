/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.function;

import id.co.itasoft.telkom.oss.plugin.dao.GlobalQueryInsertCollection;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateQueryCollectionDao;
import id.co.itasoft.telkom.oss.plugin.model.ListEnum;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.SQLException;
import java.util.ArrayList;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author asani
 */
public class ActionStatusTicket {

  MasterParam masterParam;
  MasterParamDao masterParamDao;
  RESTAPI _RESTAPI;
  GlobalQueryInsertCollection insertCollection;
  UpdateQueryCollectionDao updateQueryCollectionDao;
  LogInfo logInfo;

  public ActionStatusTicket() {
    masterParam = new MasterParam();
    masterParamDao = new MasterParamDao();
    _RESTAPI = new RESTAPI();
    insertCollection = new GlobalQueryInsertCollection();
    updateQueryCollectionDao = new UpdateQueryCollectionDao();
    logInfo = new LogInfo();
  }

  // STATUS
  String NEW = ListEnum.NEW.toString();
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

  // ACTION STATUS
  String TOSALAMSIM = ListEnum.TOSALAMSIM.toString();
  String REDISPATCH_BY_SELECT_SOLUTION = ListEnum.REDISPATCH_BY_SELECT_SOLUTION.toString();
  String RESOLVE = ListEnum.RESOLVE.toString();
  String REASSIGN_OWNERGROUP = ListEnum.REASSIGN_OWNERGROUP.toString();
  String REQUEST_PENDING = ListEnum.REQUEST_PENDING.toString();
  String REOPEN = ListEnum.REOPEN.toString();
  String SENDTOANALYSIS = ListEnum.SENDTOANALYSIS.toString();
  String AFTERPENDING = ListEnum.AFTERPENDING.toString();
  String AFTER_REQUEST = ListEnum.AFTER_REQUEST.toString();
  String DEADLINETOSALAMSIM = ListEnum.DEADLINETOSALAMSIM.toString();
  String DEADLINETOCLOSED = ListEnum.DEADLINETOCLOSED.toString();
  String DEADLINETOANALYSIS = ListEnum.DEADLINETOANALYSIS.toString();
  String DEADLINETOMEDIACARE = ListEnum.DEADLINETOMEDIACARE.toString();
  String SQMTOCLOSED = ListEnum.SQMTOCLOSED.toString();
  String RESOLVEMASAL = ListEnum.RESOLVEMASAL.toString();
  String CLOSEDMASSAL = ListEnum.CLOSEDMASSAL.toString();

  public String getStatus(TicketStatus ticketStatus, String processId) throws SQLException {
    ArrayManipulation arrayManipulation = new ArrayManipulation();

    String statusTarget = "";
    String ID = (ticketStatus.getId() == null) ? 
        "" : ticketStatus.getId().trim();
    String TICKETID = (ticketStatus.getTicketId() == null) ? 
        "" : ticketStatus.getTicketId().trim();
    String classificationFlag = (ticketStatus.getClassificationFlag() == null) ? 
        "" : ticketStatus.getClassificationFlag();
    String lastStatus = ("".equals(ticketStatus.getStatus())) ? 
        "" : ticketStatus.getStatus().toUpperCase().trim();
    String statusCurrent = ("".equals(ticketStatus.getStatus())) ? 
        "" : ticketStatus.getStatus().toUpperCase().trim();
    String actionStatus = (ticketStatus.getActionStatus() == null) ? 
        "" : ticketStatus.getActionStatus();
    String classificationType =(ticketStatus.getClassification_type() == null) ? 
        "" : ticketStatus.getClassification_type();
    String sourceTicket = (ticketStatus.getSourceTicket() == null) ? 
        "" : ticketStatus.getSourceTicket();
    String custSegment = (ticketStatus.getCustomerSegment() == null) ? 
        "" : ticketStatus.getCustomerSegment().toUpperCase().trim();
    String autoBackend = (ticketStatus.getAutoBackend() == null) ? 
        "" : ticketStatus.getAutoBackend();
    String channel = (ticketStatus.getChannel() == null) ? 
        "" : ticketStatus.getChannel();
    String finalcheck = (ticketStatus.getFinalCheck() == null) ? 
        "" : ticketStatus.getFinalCheck();
    String codeValidation = (ticketStatus.getCode_validation() == null) ? 
        "" : ticketStatus.getCode_validation();
    String lastState = (ticketStatus.getLast_state() == null) ? 
        "" : ticketStatus.getLast_state();
    String sccCode = (ticketStatus.getSccCode() == null) ? 
        "" : ticketStatus.getSccCode();
    String pendingStatus = (ticketStatus.getPendingStatus() == null) ? 
        "" : ticketStatus.getPendingStatus();
    String serviceType = (ticketStatus.getServiceType() == null) ? 
        "" : ticketStatus.getServiceType().toUpperCase().trim();
    String woStatus = (ticketStatus.getWoStatus() == null) ? 
        "" : ticketStatus.getWoStatus().trim();
    String ownergroup = (ticketStatus.getAssignedOwnerGroup() == null) ? 
        "" : ticketStatus.getAssignedOwnerGroup().trim();
    String HSILIST[] = {"DBS", "DES", "DGS"};
    String SERVICETYPELIST[] = {"INTERNET", "IPTV", "VOICE"};
    String SOURCETICKETLIST[] = {"GAMAS", "FALLOUT", "PROACTIVE"};
    String TSC_RESULT = (ticketStatus.getTscMeasurement() == null) ? 
        "" : ticketStatus.getTscMeasurement().trim();
    String flagFcr = (ticketStatus.getFlagFcr() == null) ? 
        "" : ticketStatus.getFlagFcr().trim();

    // CHECK APAKAH STATUS SEKARANG PENDING
    if ("AFTER_REQUEST".equals(actionStatus) || "APPROVED".equals(pendingStatus)) {
      statusCurrent = REQUESTPENDING;
    } else if ("AFTERPENDING".equals(actionStatus)
        || ("AFTER PENDING".equals(actionStatus)
        && "REJECTED".equals(pendingStatus))) {
      statusCurrent = PENDING;
    }

    if (classificationType.length() <= 0) {
      classificationType = "LOGIC";
    }
    
    if(classificationFlag.isEmpty()) {
      classificationFlag = "NONTECHNICAL";
    }

    int flagNum = 0;
    if (classificationFlag.contains("NONTECHNICAL")) {
      flagNum = 2; // nontechnical
    } else {
      flagNum = 1; // technical
    }

    switch (statusCurrent) {
      case "NEW":
        if ("FCR".equalsIgnoreCase(flagFcr)) {
          statusTarget = CLOSED;
        } else if (("YES".equalsIgnoreCase(autoBackend)
                && "FISIK".equalsIgnoreCase(classificationType)
                && !"GAMAS".equalsIgnoreCase(sourceTicket))
            || (flagNum == 1 && "YES".equalsIgnoreCase(autoBackend))) {
          statusTarget = BACKEND;
        } else if (!"YES".equalsIgnoreCase(autoBackend)
            && "LOGIC".equalsIgnoreCase(classificationType)
            && !"GAMAS".equalsIgnoreCase(sourceTicket)
            && !"PROACTIVE".equalsIgnoreCase(sourceTicket)
            && !"FALLOUT".equalsIgnoreCase(sourceTicket)
            && "DCS".equalsIgnoreCase(custSegment)
            && flagNum == 1) {
          statusTarget = DRAFT;
        } else if ( // CHECK JIKA BUKAN FISIK/LOGIC
        (!"YES".equalsIgnoreCase(autoBackend)
                && ((!"LOGIC".equalsIgnoreCase(classificationType)
                        && !"FISIK".equalsIgnoreCase(classificationType))
                    || (!"FISIK".equalsIgnoreCase(classificationType)
                        && ("PROACTIVE".equalsIgnoreCase(sourceTicket)
                            || "FALLOUT".equalsIgnoreCase(sourceTicket)))))
            || ("LOGIC".equalsIgnoreCase(classificationType)
                && !"DCS".equalsIgnoreCase(custSegment)
                && "CUSTOMER".equalsIgnoreCase(sourceTicket))
            || "GAMAS".equalsIgnoreCase(sourceTicket)
            || flagNum == 2) {
          statusTarget = ANALYSIS;
        } else if (actionStatus.equalsIgnoreCase(CLOSEDMASSAL)) {
          statusTarget = CLOSED;
        } 
        else {
          statusTarget = NEW;
        }
        break;

      case "ANALYSIS":
        if (actionStatus.equals(REDISPATCH_BY_SELECT_SOLUTION)) {
          statusTarget = BACKEND;
        } else if (actionStatus.equals(REASSIGN_OWNERGROUP)) {
          statusTarget = ANALYSIS; // "ANALYSIS";
        } else if (actionStatus.equals(REQUEST_PENDING)) {
          statusTarget = ANALYSIS;
        } else if (actionStatus.equals(RESOLVE) || actionStatus.equals(RESOLVEMASAL)) {
          
          boolean checkHSI = arrayManipulation
              .SearchDataOnArray(
                  HSILIST,
                  custSegment
              );
          boolean checkSourceTicket = arrayManipulation
              .SearchDataOnArray(
                  SOURCETICKETLIST, 
                  sourceTicket
              );

          if (!checkSourceTicket) {

            if ("DCS".equals(custSegment)) {
                statusTarget = MEDIACARE;
            } else {

              // CHECK customerSegment HSI ATAU BUKAN HSI(DGS,DES,DBS)
              // HSI merupakan sebutan untuk service_type = INTERNET/IPTV/VOICE & customer_segment = DBS/DGS/DES.
              // DATIN(Non-HSI) sebutan untuk service_type != INTERNET/IPTV/VOICE & customer_segment = DBS/DGS/DES.
             
              if (checkHSI) { // DES,DBS,DGS
                if ("INTERNET".equalsIgnoreCase(serviceType)){
                  if (TSC_RESULT.equalsIgnoreCase("PASSED")) {
                    statusTarget = CLOSED;
                  } else {
                    statusTarget = MEDIACARE;
                  }
                } else if("IPTV".equalsIgnoreCase(serviceType) ||
                    "VOICE".equals(serviceType)) {
                  statusTarget = MEDIACARE;
                } else {
                  // DATIN
                  statusTarget = RESOLVED;
                }
              } else { // NON HSI & NON DATIN / DWS
                statusTarget = SALAMSIM;
              }
            }
          } else {
            if ("1".equals(finalcheck.trim())) {
              statusTarget = FINALCHECK;
            } else {
              statusTarget = RESOLVED;
            }
          }

          // CHECK JIKA CLASSIFICATION LOGIC ADA PENGECEKAN SCC CODE
          if ("LOGIC".equals(classificationType)) {
            boolean CHECKSCC = CheckSccCodeValidation(ticketStatus);
            if (CHECKSCC) {
              statusTarget = CLOSED; // update scc code validation valid
            }
          }

        } else if (actionStatus.equals(SQMTOCLOSED) || actionStatus.equals(CLOSEDMASSAL)) {
          statusTarget = CLOSED;
        } else {
          statusTarget = ANALYSIS;
        }
        break;

      case "BACKEND":
        if (actionStatus.equals(REDISPATCH_BY_SELECT_SOLUTION)) {
          statusTarget = BACKEND;
        } else if (actionStatus.equals(REASSIGN_OWNERGROUP)) {
          statusTarget = BACKEND;
        } else if (actionStatus.equals(REQUEST_PENDING)) {
          statusTarget = BACKEND;
        } else if (actionStatus.equals(RESOLVE) || actionStatus.equals(RESOLVEMASAL)) {
          if (!"FALLOUT".equals(sourceTicket)
              && !"GAMAS".equals(sourceTicket)
              && !"PROACTIVE".equals(sourceTicket)) {

            if ("DCS".equals(custSegment)) {
                statusTarget = FINALCHECK; //nanti akan masuk ke auto finalcheck
            } else {
              // CHECK customerSegment HSI ATAU BUKAN HSI(DGS,DES,DBS)
              boolean checkHSI = arrayManipulation.SearchDataOnArray(
                  HSILIST, 
                  custSegment
              );
              
              if (checkHSI) {
                if ("INTERNET".equals(serviceType)) { // HSI
                  // CHECK WO
                  String responseBody = "";
                  JSONObject woRequest = new JSONObject();
                  try {
                    masterParam = masterParamDao.getUrl("list_work_order");
                    HttpUrl.Builder httpBuilder = HttpUrl.parse(masterParam.getUrl()).newBuilder();
                    httpBuilder.addQueryParameter("externalID1", TICKETID);
                    Request request =
                        new Request.Builder()
                            .url(httpBuilder.build())
                            .addHeader("api_key", masterParam.getApi_key().toString())
                            .addHeader("api_id", masterParam.getApi_id().toString())
                            .build();

                    woRequest = _RESTAPI.CALLAPIHANDLER(request);
                    boolean status = woRequest.getBoolean("status");
                    responseBody = woRequest.getString("msg");

                    if (status) { // jika pemanggilan wo berhasil

                      Object json = new JSONTokener(responseBody).nextValue();
                      JSONObject WORKORDER = (JSONObject) json;
                      JSONArray DATA_WORKORDER = WORKORDER.getJSONArray("data");
                      int totalWorkorder = (WORKORDER.has("total")) ? WORKORDER.getInt("total") : 0;

                      if (totalWorkorder > 0) { // punya workorder
                        boolean nextProcess = false;
                        ArrayList<String> woStatusList = new ArrayList<String>();

                        // CHECK STATUS DI LIST WO
                        for (int i = 0; i < DATA_WORKORDER.length(); i++) {
                          JSONObject wojson = DATA_WORKORDER.getJSONObject(i);
                          String STATUSWO =(wojson.has("status")) 
                              ? wojson.getString("status") : "";
                          woStatusList.add(STATUSWO);
                        }

                        if (!woStatusList.contains("ASSIGNED")
                            && !woStatusList.contains("OPEN")
                            && !woStatusList.contains("")
                            && !woStatusList.contains("HOLD")) {
                          nextProcess = true;
                        }

                        if (nextProcess) { // STATUS WO SUDAH COMPLETED ATAU CANCELED
                          // check TSC RESULT
                          if (TSC_RESULT.equalsIgnoreCase("PASSED")) {
                            statusTarget = CLOSED;
                            updateQueryCollectionDao.UpdateClosedReopenByParentId(processId, "25");
                          } else {
                            statusTarget = MEDIACARE;
                          }
                        } else { // STATUS WO MASIH OPEN
                          statusTarget = BACKEND;
                          insertCollection.insertWorkLogs(
                              ID, 
                              TICKETID, 
                              ownergroup, 
                              "REOPEN TICKET BECAUSE WORKORDER STATUS ARE OPEN",
                              woRequest.toString()
                          );
                        }

                      } else { 
                        // tidak punya workorder
                        if (TSC_RESULT.equalsIgnoreCase("PASSED")) {
                          statusTarget = CLOSED;
                        } else {
                          statusTarget = MEDIACARE;
                        }
                      }

                    } else { // jika pemanggilan gagal
                      statusTarget = BACKEND;
                      insertCollection.insertWorkLogs(
                          ID, 
                          TICKETID, 
                          ownergroup, 
                          "REOPEN TICKET BECAUSE SERVICE WORKORDER UNAVAILABLE",
                          woRequest.toString()
                      );
                    }

                  } catch (Exception ex) {
                    logInfo.Log(getClass().getName(), ex.getMessage());
                    statusTarget = BACKEND;
                  } finally {
                    try {
                      // ID, TICKETID, OWNERGROUP, MSG
                      insertCollection.insertWorkLogs(
                          ID, 
                          TICKETID, 
                          ownergroup, 
                          "CHECK WORKORDER",
                          woRequest.toString()
                      );
                    } catch (Exception ex) {
                      logInfo.Log(getClass().getName(), ex.getMessage());
                    }
                    masterParam = null;
                    masterParamDao = null;
                    _RESTAPI = null;
                    insertCollection = null;
                  }

                } else if ("VOICE".equals(serviceType) || "IPTV".equals(serviceType)) {
                  // CHECK WO
                  String responseBody = "";
                  JSONObject woRequest = new JSONObject();
                  try {
                    masterParam = masterParamDao.getUrl("list_work_order");
                    HttpUrl.Builder httpBuilder = HttpUrl.parse(masterParam.getUrl()).newBuilder();
                    httpBuilder.addQueryParameter("externalID1", TICKETID);
                    Request request =
                        new Request.Builder()
                            .url(httpBuilder.build())
                            .addHeader("api_key", masterParam.getApi_key().toString())
                            .addHeader("api_id", masterParam.getApi_id().toString())
                            .build();

                    woRequest = _RESTAPI.CALLAPIHANDLER(request);
                    boolean status = woRequest.getBoolean("status");
                    responseBody = woRequest.getString("msg");

                    if (status) { // jika pemanggilan wo berhasil

                      Object json = new JSONTokener(responseBody).nextValue();
                      JSONObject WORKORDER = (JSONObject) json;
                      JSONArray DATA_WORKORDER = WORKORDER.getJSONArray("data");
                      int totalWorkorder = (WORKORDER.has("total")) ? WORKORDER.getInt("total") : 0;

                      if (totalWorkorder > 0) { // punya workorder
                        boolean nextProcess = false;
                        ArrayList<String> woStatusList = new ArrayList<String>();

                        // CHECK STATUS DI LIST WO
                        for (int i = 0; i < DATA_WORKORDER.length(); i++) {
                          JSONObject wojson = DATA_WORKORDER.getJSONObject(i);
                          String STATUSWO = (wojson.has("status")) ? 
                              wojson.getString("status") : "";
                          woStatusList.add(STATUSWO);
                        }

                        if (!woStatusList.contains("ASSIGNED")
                            && !woStatusList.contains("OPEN")
                            && !woStatusList.contains("")
                            && !woStatusList.contains("HOLD")) {
                          nextProcess = true;
                        }

                        if (nextProcess) { // STATUS WO SUDAH COMPLETED ATAU CANCELED
                          // check TSC RESULT
                          if (TSC_RESULT.equalsIgnoreCase("PASSED")) {
                            statusTarget = CLOSED;
                            updateQueryCollectionDao.UpdateClosedReopenByParentId(processId, "25");
                          } else {
                            statusTarget = MEDIACARE;
                          }
                        } else { // STATUS WO MASIH OPEN
                          statusTarget = BACKEND;
                          insertCollection.insertWorkLogs(
                              ID, 
                              TICKETID, 
                              ownergroup, 
                              "REOPEN TICKET BECAUSE WORKORDER STATUS ARE OPEN",
                              woRequest.toString()
                          );
                        }

                      } else { // tidak punya workorder
                        statusTarget = MEDIACARE;
                      }

                    } else { // jika pemanggilan gagal
                      statusTarget = BACKEND;
                      insertCollection.insertWorkLogs(
                          ID, 
                          TICKETID, 
                          ownergroup, 
                          "REOPEN TICKET BECAUSE SERVICE WORKORDER UNAVAILABLE",
                          woRequest.toString()
                      );
                    }

                  } catch (Exception ex) {
                    logInfo.Log(getClass().getName(), ex.getMessage());
                    statusTarget = BACKEND;
                  } finally {
                    try {
                      // ID, TICKETID, OWNERGROUP, MSG
                      insertCollection.insertWorkLogs(
                          ID, 
                          TICKETID, 
                          ownergroup, 
                          "CHECK WORKORDER", 
                          woRequest.toString()
                      );
                    } catch (Exception ex) {
                      logInfo.Log(getClass().getName(), ex.getMessage());
                    }
                    masterParam = null;
                    masterParamDao = null;
                    _RESTAPI = null;
                    insertCollection = null;
                  }
                } else {
                  // NON HSI / DATIN
                  statusTarget = FINALCHECK;
                }
              } else { // NON HSI && NON DATIN
                statusTarget = SALAMSIM; // RULE DULU
              }
            }
          } else { // PROACTIVE, FALLOUT, GAMAS
            if ("1".equals(finalcheck.trim())) {
              statusTarget = FINALCHECK;
            } else {
              statusTarget = RESOLVED;
            }
          }
        } else if (actionStatus.equals(SQMTOCLOSED) || actionStatus.equals(CLOSEDMASSAL)) {
          statusTarget = CLOSED;
        } else {
          statusTarget = BACKEND;
          insertCollection.insertWorkLogs(
              ID, 
              TICKETID, 
              ownergroup, 
              "REOPEN TICKET BECAUSE SERVICE WORKORDER UNAVAILABLE",
              ""
          );
        }
        break;
      case "FINALCHECK":
        if (actionStatus.equals(REQUEST_PENDING)) {
          statusTarget = FINALCHECK;
        } else if (actionStatus.equals(REASSIGN_OWNERGROUP)) {
          statusTarget = FINALCHECK;
        } else if (actionStatus.equals(REOPEN)) {
          statusTarget = lastState.toUpperCase();
        } else if (actionStatus.equals(RESOLVE) || actionStatus.equals(RESOLVEMASAL)) {
          if (!"FALLOUT".equals(sourceTicket)
              && !"GAMAS".equals(sourceTicket)
              && !"PROACTIVE".equals(sourceTicket)) {

            if ("DCS".equals(custSegment)) {
              statusTarget = MEDIACARE;
            } else {
              boolean checkHSI = arrayManipulation.SearchDataOnArray(HSILIST, custSegment);
              boolean checkServiceType = arrayManipulation.SearchDataOnArray(
                  SERVICETYPELIST, 
                  serviceType
              );
              if (checkHSI) {
                if (checkServiceType) { // HSI
                  statusTarget = MEDIACARE;
                } else { // DATIN
                  statusTarget = RESOLVED;
                }
              } else { // NON HSI
                statusTarget = SALAMSIM; // RULE DULU
              }
            }
          } else {
            statusTarget = RESOLVED;
          }
        } else if (actionStatus.equals(SQMTOCLOSED) || actionStatus.equals(CLOSEDMASSAL)) {
          statusTarget = CLOSED;
        } else {
          statusTarget = FINALCHECK;
        }
        break;
      case "RESOLVED":
        
        if (actionStatus.equals(REOPEN)) {
          statusTarget = lastState.toUpperCase();
        } else if (actionStatus.equals(RESOLVE)) {
          statusTarget = CLOSED;
        } else if (actionStatus.equals(SQMTOCLOSED) || actionStatus.equals(CLOSEDMASSAL)) {
          statusTarget = CLOSED;
        } else if (actionStatus.equals(DEADLINETOMEDIACARE)) {
          statusTarget = MEDIACARE;
        } else if (actionStatus.equals(TOSALAMSIM)) {
          statusTarget = MEDIACARE;
        } else {
          statusTarget = RESOLVED;
        }
        break;
      case "MEDIACARE":
        if (actionStatus.equals(REOPEN)) {
          statusTarget = lastState.toUpperCase();
        } else if (actionStatus.equals(RESOLVE)) {
          statusTarget = CLOSED;
        } else if (actionStatus.equals(REQUEST_PENDING)) {
          statusTarget = MEDIACARE;
        } else if (actionStatus.equals(DEADLINETOSALAMSIM)) {
          statusTarget = SALAMSIM;
        } else if (actionStatus.equals(SQMTOCLOSED) || actionStatus.equals(CLOSEDMASSAL)) {
          statusTarget = CLOSED;
        } else {
          statusTarget = MEDIACARE;
        }
        break;
      case "SALAMSIM":
        if (actionStatus.equals(REOPEN)) {
          statusTarget = lastState.toUpperCase();
        } else if (actionStatus.equals(RESOLVE)) {
          statusTarget = CLOSED;
        } else if (actionStatus.equals(REQUEST_PENDING)) {
          statusTarget = SALAMSIM;
        } else if (actionStatus.equals(DEADLINETOCLOSED)) {
          statusTarget = CLOSED;
        } else if (actionStatus.equals(SQMTOCLOSED) || actionStatus.equals(CLOSEDMASSAL)) {
          statusTarget = CLOSED;
        } else {
          statusTarget = SALAMSIM;
        }
        break;
      case "DRAFT":
        if (actionStatus.equals(SENDTOANALYSIS)) {
          statusTarget = ANALYSIS;
        } else if (actionStatus.equals(RESOLVE) || actionStatus.equals(RESOLVEMASAL)) {
          if ("40".equals(channel)) {
            statusTarget = CLOSED;
          } else {
            // UPDATE SCC CODE VALIDATION = NOT VALID
            if ("DCS".equals(custSegment)) {
              statusTarget = MEDIACARE;
            } else {
              boolean checkHSI = arrayManipulation.SearchDataOnArray(
                  HSILIST, 
                  custSegment
              );
              boolean checkServiceType = arrayManipulation.SearchDataOnArray(
                  SERVICETYPELIST, 
                  serviceType
              );
              if (checkHSI) {
                if (checkServiceType) { // INTERNET, IPTV, VOICE
                  statusTarget = MEDIACARE;
                } else {
                  statusTarget = SALAMSIM;
                }
              } else { // NON HSI
                statusTarget = SALAMSIM; // RULE DULU
              }
            }
            
            // CHECK JIKA CLASSIFICATION LOGIC ADA PENGECEKAN SCC CODE
            if ("LOGIC".equals(classificationType)) {
              boolean CHECKSCC = CheckSccCodeValidation(ticketStatus);
              if (CHECKSCC) {
                statusTarget = CLOSED; // update scc code validation valid
              }
            }
            
          }
        } else if (actionStatus.equals(DEADLINETOANALYSIS)) {
          statusTarget = ANALYSIS;
        } else if (actionStatus.equals(SQMTOCLOSED) || actionStatus.equals(CLOSEDMASSAL)) {
          statusTarget = CLOSED;
        } else {
          statusTarget = DRAFT;
        }
        break;
      case "PENDING":
        if (actionStatus.equals(AFTERPENDING)) {
          statusTarget = lastStatus;
        } else if (actionStatus.equals(SQMTOCLOSED) || actionStatus.equals(CLOSEDMASSAL)) {
          statusTarget = CLOSED;
        }
        break;
      case "REQUESTPENDING":
        if (actionStatus.equals(AFTER_REQUEST)) {
          statusTarget = lastStatus;
        } else if (pendingStatus.equals("APPROVED")) {
          statusTarget = lastStatus;
        } else if (actionStatus.equals(SQMTOCLOSED) || actionStatus.equals(CLOSEDMASSAL)) {
          statusTarget = CLOSED;
        }
        break;
      default:
        statusTarget = lastStatus;
        break;
    }
    return statusTarget;
  }

  public boolean CheckSccCodeValidation(TicketStatus ts) {
    boolean IS_VALID = false;

    String codeValidation = (ts.getCode_validation() == null) ? "" : ts.getCode_validation().trim();
    String sccCode = (ts.getSccCode() == null) ? "" : ts.getSccCode().trim();

    if (!sccCode.isEmpty()) {
      if (codeValidation.equalsIgnoreCase(sccCode)) {
        IS_VALID = true;
      }
    }
    return IS_VALID;
  }
}
