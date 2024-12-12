/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GlobalQuerySelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogicToOtherAPI;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateQueryCollectionDao;
import id.co.itasoft.telkom.oss.plugin.function.ActionStatusTicket;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

/**
 * @author tarkiman, rizki eka
 */
public class InsertTicketStatusLogsNewAfterRunProcess extends DefaultApplicationPlugin {

  public static String pluginName =
      "Telkom New OSS - Ticket Incident Services - Insert Ticket Status Logs (For Status New / After Run Process)";

  InsertTicketStatusLogsDao dao;
  TicketStatus ticketStatus;
  ActionStatusTicket actionStatusTicket;
  ArrayManipulation arrayManipulation;
  UpdateQueryCollectionDao uqcd;
  GlobalQuerySelectCollections selectCollections;
  UpdateQueryCollectionDao updateQueryCollectionDao;
  LogicToOtherAPI logicToOtherAPI;
  LogInfo logInfo = new LogInfo();

  public InsertTicketStatusLogsNewAfterRunProcess() {
    updateQueryCollectionDao = new UpdateQueryCollectionDao();
    dao = new InsertTicketStatusLogsDao();
    uqcd = new UpdateQueryCollectionDao();
    actionStatusTicket = new ActionStatusTicket();
    arrayManipulation = new ArrayManipulation();
    ticketStatus = new TicketStatus();
    selectCollections = new GlobalQuerySelectCollections();
    logicToOtherAPI = new LogicToOtherAPI();
  }

  @Override
  public Object execute(Map map) {

    try {
      AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
      WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
      String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
       
      int delayInSecond = 1;

      if (!"".equalsIgnoreCase(processId)) {

        // GET AND UPDATE TICKET ID
        ticketStatus = selectCollections.getTicketId(processId);

        int countGaul = 0;
        String SOURCETICKETLIST[] = {"GAMAS", "FALLOUT"};
        String TICKET35[] = {"ANALYSIS", "BACKEND", "SALAMSIM", "CLOSED"};
        String custSegmentHSIList[] = {"DES", "DGS", "DBS"};
        String finalcheck = (ticketStatus.getFinalCheck() == null) 
              ? "" : ticketStatus.getFinalCheck();
        String SOURCETICKET = (ticketStatus.getSourceTicket() == null)
              ? "" : ticketStatus.getSourceTicket().toUpperCase();
        String createdBy = (ticketStatus.getCreatedTicketBy() == null) 
              ? "" : ticketStatus.getCreatedTicketBy();
        String ticketId = (ticketStatus.getTicketId() == null)
              ? "" : ticketStatus.getTicketId();
        String customerSegment = (ticketStatus.getCustomerSegment()== null)
              ? "" : ticketStatus.getCustomerSegment();
        String serviceId = (ticketStatus.getServiceId() == null) 
              ? "" : ticketStatus.getServiceId();
        String channel = (ticketStatus.getChannel() == null) 
              ? "" : ticketStatus.getChannel();
        
        // HARUS SYNC, LGI DICOBA ASYNC
        if ("MANUAL".equalsIgnoreCase(createdBy)) {
          ticketId = dao.getTicketIdFunction();
          ticketStatus.setTicketId(ticketId);
          uqcd.UpdateTicketIdByProcess(processId, ticketId);
        }
        ticketStatus.setStatusTracking(ticketId);

        if (!"".equalsIgnoreCase(ticketId)) {

          // INSERT STATUS NEW AND LAST STATUS STATE
          ticketStatus.setStatus("NEW");
          ticketStatus.setStatusCurrent("NEW");
          dao.insertTicketStatus(ticketStatus);

          // CARI MAPPING STATUS TARGET
          String STATUS_TARGET = actionStatusTicket.getStatus(ticketStatus, processId); 

          // *** INSERT TICKET STATUS LOG
          if (!"NEW".equalsIgnoreCase(STATUS_TARGET)) {
            // INSERT CURRENT STATUS
            TimeUnit.SECONDS.sleep(1);
            ticketStatus.setStatus(STATUS_TARGET);
            ticketStatus.setChangeBy("");
            ticketStatus.setStatusTracking("false");
            dao.insertTicketStatus(ticketStatus);
            // *** END
          }

          /** UPDATE FINALCHECK = 1 */
          boolean checkSourceTicket = arrayManipulation.SearchDataOnArray(
              SOURCETICKETLIST, 
              SOURCETICKET
          );
          
          if (!checkSourceTicket) {
            if ("BACKEND".equalsIgnoreCase(STATUS_TARGET)) {
              finalcheck = "1";
            }
          }

          /** UPDATE GAUL */
          if (!("GAMAS").equalsIgnoreCase(SOURCETICKET)) {
//                        countGaul
//             =selectCollections.getCountGaulByServiceId(ticketStatus.getServiceId(),
//             ticketStatus.getDateCreated().toString());
            if(serviceId.length() > 0) {
              countGaul = selectCollections.getCountGaulByServiceIdCstmSgmn(
                  serviceId, 
                  customerSegment,
                  processId
              );
            }
          }

          /**
           * UPDATE DATA
           * @param processId, 
           * @param gaul, 
           * @param finalcheck, 
           * @param statusTarget
           */
          uqcd.updateDataGaulFinalcheck(
              processId, 
              countGaul, 
              finalcheck, 
              STATUS_TARGET
          );

        } else {
          logInfo.Log(getClass().getName(), "TICKET ID kosong");
        }
      } else {
        logInfo.Log(getClass().getName(), "PROCESS ID kosong");
      }
    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      dao = null;
      uqcd = null;
      actionStatusTicket = null;
      arrayManipulation = null;
      ticketStatus = null;
      selectCollections = null;
      logicToOtherAPI = null;
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

  public String getDuration(Timestamp lastTimestamp, Timestamp currentTime) throws SQLException {
    Timestamp currentTimestamp = dao.getCurrentDate();
    Timestamp lct = lastTimestamp;
    if (lastTimestamp == null || lastTimestamp.equals(null)) {
      lastTimestamp = currentTimestamp;
      lct = currentTimestamp;
    }
    int compare = lct.compareTo(currentTimestamp);
    if (compare > 0) {
      lastTimestamp = currentTimestamp;
      currentTimestamp = lct;
    }

    long diff = currentTimestamp.getTime() - lastTimestamp.getTime();
    long seconds = (diff / 1000) % 60;
    long minutes = (diff / (1000 * 60)) % 60;
    long hours = (diff / (1000 * 60 * 60)) % 24;
    long _minutes = minutes % 60;
    long _seconds = seconds % 60;
    String statusTracking = hours + ":" + minutes + ":" + seconds;
    return statusTracking;
  }

  public void saveWorkFlowVariable(Map map, String wVariableName, String wVariableValue) {
    PluginManager pluginManager = (PluginManager) map.get("pluginManager");
    WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
    WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
    wm.activityVariable(workflowAssignment.getActivityId(), wVariableName, wVariableValue);
  }
}
