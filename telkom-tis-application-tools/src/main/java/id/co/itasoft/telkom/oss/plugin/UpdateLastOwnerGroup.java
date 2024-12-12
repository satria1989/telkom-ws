/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editoticketStatus.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GlobalQuerySelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogicToOtherAPI;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateLastOwnerGroupDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateQueryCollectionDao;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.function.SendStatusToMycx;
import id.co.itasoft.telkom.oss.plugin.model.ListEnum;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

/**
 * @author Suenawati Rizky
 */
public class UpdateLastOwnerGroup extends DefaultApplicationPlugin {

  public static String pluginName =
      "Telkom New OSS - Ticket Incident Services - Update Last Owner Group";
  TicketStatus ticketStatus;
  TicketStatus ts;
  SendStatusToMycx mycx;
  UpdateLastOwnerGroupDao updtLastDao;
  InsertTicketStatusLogsDao dao;
  UpdateQueryCollectionDao updateQueryCollectionDao;
  ArrayManipulation arrayManipulation;
  GlobalQuerySelectCollections querySelectCollections;
  RESTAPI _RESTAPI;
  String TOKEN;
  LogicToOtherAPI logicToOtherAPI;
  LogInfo logInfo = new LogInfo();

  public UpdateLastOwnerGroup() {
    logicToOtherAPI = new LogicToOtherAPI();
    querySelectCollections = new GlobalQuerySelectCollections();
    _RESTAPI = new RESTAPI();
    arrayManipulation = new ArrayManipulation();
    updateQueryCollectionDao = new UpdateQueryCollectionDao();
    updtLastDao = new UpdateLastOwnerGroupDao();
    dao = new InsertTicketStatusLogsDao();
    mycx = new SendStatusToMycx();
    ticketStatus = new TicketStatus();
    TOKEN = _RESTAPI.getToken();
  }

  @Override
  public Object execute(Map map) {
    AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
    WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
    String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());

    try {

      String ownerGroup = "";

      if (!"".equalsIgnoreCase(processId)) {
        
        //  Number of threads = Number of Available Cores * (1 + Wait time / Service time)
//        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(135);
//        executor.setKeepAliveTime(30, TimeUnit.SECONDS);
//        executor.allowCoreThreadTimeOut(true);
//        executor.setKeepAliveTime(1, TimeUnit.MINUTES);

        String MEDIACARE = ListEnum.MEDIACARE.toString();
        String ACT_RESOLVE = ListEnum.RESOLVE.toString();
        String custSegmentEBISList[] = {"DES", "DGS", "DBS"};
        String TICKET35[] = {"ANALYSIS", "BACKEND", "SALAMSIM", "CLOSED"};
        
        // GET DATA TICKET
        ticketStatus = dao.getTicketId(processId);

        String owner = (ticketStatus.getOwner() == null) ? 
            "" : ticketStatus.getOwner();
        String TICKETID = (ticketStatus.getTicketId() == null) ? 
            "" : ticketStatus.getTicketId();
        String pendingStatus = (ticketStatus.getPendingStatus() == null) ? 
            "" : ticketStatus.getPendingStatus().trim();
        String actionStatus = (ticketStatus.getActionStatus() == null) ? 
            "" : ticketStatus.getActionStatus().trim();
        String channel = (ticketStatus.getChannel() == null) ? 
            "" : ticketStatus.getChannel();
        String customerSegment = (ticketStatus.getCustomerSegment() == null) ? 
            "" : ticketStatus.getCustomerSegment().toUpperCase().trim();
        String LAST_STATUS = (ticketStatus.getStatusCurrent() == null) ? 
            "" : ticketStatus.getStatusCurrent().trim();
        String lastOwnerGroup = (ticketStatus.getAssignedOwnerGroup() == null) ?
            "" : ticketStatus.getAssignedOwnerGroup();
        // kerubah jadi pending

        if (!TICKETID.isEmpty()) {
          ts = new TicketStatus();
          if ("REQUEST_REJECTED".equalsIgnoreCase(pendingStatus)) {
            ts = updtLastDao.getLastOwnerGroupByTicketID(TICKETID, "1");
          } else if ("AFTER PENDING".equalsIgnoreCase(actionStatus)
              || "REJECTED".equalsIgnoreCase(pendingStatus)) {
            ts = updtLastDao.getLastOwnerGroupByTicketID(TICKETID, "2");
          } else if ("REOPEN".equalsIgnoreCase(TICKETID)) {
            ts = updtLastDao.getLastOwnerGroupByTicketID(TICKETID, "3");
          } else {
            ts = updtLastDao.getLastOwnerGroupByTicketID(TICKETID, "4");
            if ("ANALYSIS".equalsIgnoreCase(LAST_STATUS)
                && "DEADLINE".equalsIgnoreCase(actionStatus)) {
              ts.setOwnerGroup(ticketStatus.getAssignedOwnerGroup());
            }
          }

          ownerGroup = (ts.getOwnerGroup() == null) ? "" : ts.getOwnerGroup();

          if ("REQUEST_PENDING".equalsIgnoreCase(actionStatus)
              && !"APPROVED".equalsIgnoreCase(pendingStatus)) {
            ticketStatus.setStatus("REQUEST_PENDING_" + LAST_STATUS);
          } else if ("APPROVED".equalsIgnoreCase(pendingStatus)) {
            ticketStatus.setStatus("PENDING");
          } else {
//            ticketStatus.setStatus(ticketStatus.getStatusCurrent());
            ticketStatus.setStatus(LAST_STATUS);
            dao.ClearPendingStatus(processId);
            // CLEAR PENDING STATUS IF REJECTED
          }
          
          TimeUnit.SECONDS.sleep(1);

          ticketStatus.setOwnerGroup(lastOwnerGroup);
          ticketStatus.setAssignedOwnerGroup(ownerGroup);
          ticketStatus.setChangeBy(owner);
          ticketStatus.setOwner("");
          ticketStatus.setStatusTracking("false");
          
          updateQueryCollectionDao.UpdateTimeonLastTicketSta(ticketStatus);
          dao.insertTicketStatus(ticketStatus);
          updateQueryCollectionDao.updateReopenLogic(processId, LAST_STATUS, ownerGroup);

          // SEND TO MYCX
//          executor.submit(
//              () -> {
                MasterParam paramF = new MasterParam();
                MasterParamDao paramDaod = new MasterParamDao();
                mycx = new SendStatusToMycx();
//                try {

                  // SEND TO MYTENS
                  boolean checkGroupHSI =
                      arrayManipulation.SearchDataOnArray(custSegmentEBISList, customerSegment);

                  if (checkGroupHSI && LAST_STATUS.equalsIgnoreCase(MEDIACARE)) {
                    logicToOtherAPI.SENDOVRS(ticketStatus, TOKEN); // SEND OVRS
                  }

                  if (checkGroupHSI && channel.equalsIgnoreCase("35")) {
                    if (arrayManipulation.SearchDataOnArray(TICKET35, LAST_STATUS)) {
                      logicToOtherAPI.UpdateMYTENS(ticketStatus, LAST_STATUS, TOKEN);
                    }
                  }

                  paramF = paramDaod.getUrl("update_status_ticket_to_mycx");
                  mycx.updateStatusTicketWithToken(ticketStatus, paramF, TOKEN);

                  if ("SALAMSIM".equalsIgnoreCase(LAST_STATUS)
                      && customerSegment.equalsIgnoreCase("DCS")) {
                    // MASUK KE STATUS SALAMSIM NGIRIM IN_STATUS = 11
                    paramF = paramDaod.getUrl("update_salamsim_to_mycx");
                    mycx.sendStatusSalamsimWithToken(ticketStatus, paramF, ACT_RESOLVE, TOKEN);
                  }

//                } catch (Exception ex) {
//                  logInfo.Log(getClass().getName(), ex.getMessage());
//                } finally {
//                  paramF = null;
//                  paramDaod = null;
//                }
//                return null;
//              });

        }
        // else TIKET ID KOSONG
      } 
      // else PROCESS ID KOSONG
    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
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
    //        return AppUtil.readPluginResource(getClass().getName(),
    // "/properties/InsertTicketStatusLogsProperties.json");
  }

  public void saveWorkFlowVariable(Map map, String wVariableName, String wVariableValue) {
    PluginManager pluginManager = (PluginManager) map.get("pluginManager");
    WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
    WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
    wm.activityVariable(workflowAssignment.getActivityId(), wVariableName, wVariableValue);
  }
}
