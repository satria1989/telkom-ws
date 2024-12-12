/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertChildTicketGamasDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.WorkOrderHandlerDao;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ListEnum;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import id.co.itasoft.telkom.oss.plugin.model.WorkOrder;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.json.JSONObject;

/**
 * @author rizki
 */
public class CreateWorkOrder extends DefaultApplicationPlugin {

  private String pluginName = "Telkom New OSS - Ticket Incident Services - Create Work Order";
  private String processIdIncident;
  LogInfo logInfo = new LogInfo();

  InsertTicketStatusLogsDao dao;
  TicketStatus ticketStatus;
  WorkOrder wo;
  WorkOrderHandlerDao wodao;
  MasterParam param;
  MasterParamDao paramDao;
  InsertChildTicketGamasDao ictgDao;
  LogHistory logHistory;
  LogHistoryDao logHistoryDao;
  RESTAPI _RESTAPI;
  ArrayManipulation arrManipulation;
  
  public CreateWorkOrder() {
    arrManipulation = new ArrayManipulation();
    dao = new InsertTicketStatusLogsDao();
    ticketStatus = new TicketStatus();
    wodao = new WorkOrderHandlerDao();
    param = new MasterParam();
    paramDao = new MasterParamDao();
    ictgDao = new InsertChildTicketGamasDao();
    _RESTAPI = new RESTAPI();
    logHistory = new LogHistory();
    logHistoryDao = new LogHistoryDao();
  }

  @Override
  public Object execute(Map map) {

    AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
    WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
    final String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());

    try {
      String LISTSOURCETICKET[] = {"GAMAS", "FALLOUT", "PROACTIVE"};
      
      String BACKEND = ListEnum.BACKEND.toString();
      
      ticketStatus = dao.getTicketId(processId);
      param = paramDao.getUrl("createWorkOrder");
      String customerSegment = (ticketStatus.getCustomerSegment() == null) ? 
          "" : ticketStatus.getCustomerSegment().toUpperCase().trim();
      String status = (ticketStatus.getStatusCurrent() == null) ? 
          "" : ticketStatus.getStatusCurrent();
      String sourceTicket = (ticketStatus.getSourceTicket() == null) ? 
          "" : ticketStatus.getSourceTicket().toUpperCase().trim();
      String serviceId = (ticketStatus.getServiceId() == null) ?
          "" : ticketStatus.getServiceId();
      String classificationType = (ticketStatus.getClassification_type() == null) ?
          "" : ticketStatus.getClassification_type();
      String ticketId = (ticketStatus.getTicketId() == null) ?
          "" : ticketStatus.getTicketId();
      String ID = (ticketStatus.getId() == null) ?
          "" : ticketStatus.getId();
      String ownergroup = (ticketStatus.getAssignedOwnerGroup() == null) ?
          "" : ticketStatus.getAssignedOwnerGroup();
      String techno = (ticketStatus.getTechnology() == null) ? 
          "" : ticketStatus.getTechnology();
      

      boolean CHECKSOURCE = arrManipulation.SearchDataOnArray(LISTSOURCETICKET, sourceTicket);

      if (!CHECKSOURCE) {
        if (BACKEND.equalsIgnoreCase(status)) {
          wodao.GetandStartWO(param, ticketStatus); // CREATE WO

          // AFTER CREATE WO UPDAT TECHNO
          param = paramDao.getUrl("UpdateTechToWo");

          JSONObject json = new JSONObject();
          json.put("action", "UpdateTech");
          json.put("externalId", ticketId);
          json.put("changeBy", "SYSTEM");
          json.put("technologyType", techno);

          logHistory.setAction("UPDATE TECHNOLOGY(" + ticketId + ")");
          logHistory.setCreatedBy("SYSTEM");
          logHistory.setMethod("POST");
          logHistory.setRequest(json.toString());
          logHistory.setTicketId(ticketId);
          logHistory.setUrl(param.getUrl());

          RequestBody body = RequestBody.create(_RESTAPI.JSON, json.toString());
          Request request =
              new Request.Builder()
                  .url(param.getUrl())
                  .addHeader("api_key", param.getApi_key()) // add request headers
                  .addHeader("api_id", param.getApi_id())
                  .addHeader("Origin", "https://oss-incident.telkom.co.id")
                  .post(body)
                  .build();

          JSONObject requestUpdate = _RESTAPI.CALLAPIHANDLER(request);
          logHistory.setResponse(requestUpdate.toString());
          logHistoryDao.insertToLogHistory(logHistory);
          
          TimeUnit.SECONDS.sleep(5);
          // CHECK PARENT TICKET GAMAS FROM CREATE WO
          ictgDao.checkParentTicketFisik(
              serviceId,
              processId,
              classificationType,
              ticketId,
              ID,
              ticketStatus.getAssignedOwnerGroup()
          );
          
          json = null;
          requestUpdate = null;
        }
      }

    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      dao = null;
      ticketStatus = null;
      wodao = null;
      param = null;
      paramDao = null;
      logHistory = null;
      logHistoryDao = null;
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
    //        return AppUtil.readPluginResource(getClassName(), "/properties/settingWorkOrder.json",
    // null, true, null);
  }
}
