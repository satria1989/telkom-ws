/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GlobalQuerySelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.LogicToOtherAPI;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.function.SendWANotification;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;

/**
 * @author itasoft
 */
public class SendWANotificationHandlerToMediacare extends DefaultApplicationPlugin {

  private String pluginName = "Telkom New OSS - Ticket Incident Services - Send WA Notification Mediacare";
  LogInfo logInfo = new LogInfo();
  GlobalQuerySelectCollections selectCollections;
  TicketStatus ticketStatus;
  SendWANotification swa;
  ArrayManipulation arrayManipulation;
  RESTAPI _RESTAPI;
  String TOKEN = "";
  LogicToOtherAPI logicToOtherAPI;

  public SendWANotificationHandlerToMediacare() {
    logicToOtherAPI = new LogicToOtherAPI();
    selectCollections = new GlobalQuerySelectCollections();
    ticketStatus = new TicketStatus();
    swa = new SendWANotification();
    arrayManipulation = new ArrayManipulation();
    _RESTAPI = new RESTAPI();
    TOKEN = _RESTAPI.getToken();
  }

  @Override
  public Object execute(Map map) {

    AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
    WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
    String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
    try {

      if (!"".equalsIgnoreCase(processId)) {
        boolean checkOwnerGroupHSI = false;

        ticketStatus = selectCollections.getTicketId(processId);
        
        String ownergrouHSIList[] = {"DES", "DGS", "DBS"};
        String customerSegment = (ticketStatus.getCustomerSegment() == null) ? 
            "" : ticketStatus.getCustomerSegment().toUpperCase().trim();
        String status = (ticketStatus.getStatusCurrent() == null) ? 
            "" : ticketStatus.getStatusCurrent();

        /** ADD WA NOTIFY */
        if ("MEDIACARE".equalsIgnoreCase(status)) {
          String msg = swa.sendNotifyResolve(ticketStatus, TOKEN);
        }

        checkOwnerGroupHSI = arrayManipulation.SearchDataOnArray(ownergrouHSIList, customerSegment);

        if (checkOwnerGroupHSI) {
          logicToOtherAPI.SENDOVRS(ticketStatus, TOKEN); // SEND OVRS
        }

      } 
    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      logicToOtherAPI = null;
      selectCollections = null;
      ticketStatus = null;
      swa = null;
      arrayManipulation = null;
      _RESTAPI = null;
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
    return "";
  }
}
