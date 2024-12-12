/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GlobalQuerySelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.LogicToOtherAPI;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;

/**
 * @author asani
 */
public class SendToMyTENS extends DefaultApplicationPlugin {

  private String pluginName = "Telkom New OSS - Ticket Incident Services - Send To MyTENS";

  GlobalQuerySelectCollections selectCollections;
  TicketStatus ticketStatus;
  ArrayManipulation arrayManipulation;
  LogicToOtherAPI logicToOtherAPI;
  RESTAPI _RESTAPI;
  String token = "";
  LogInfo logInfo = new LogInfo();
  

  public SendToMyTENS() {
    _RESTAPI = new RESTAPI();
    logicToOtherAPI = new LogicToOtherAPI();
    arrayManipulation = new ArrayManipulation();
    ticketStatus = new TicketStatus();
    selectCollections = new GlobalQuerySelectCollections();
    token = _RESTAPI.getToken();
  }

  @Override
  public Object execute(Map map) {
    try {
      AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
      WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
      String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
      
      ticketStatus = selectCollections.getTicketId(processId);

      String SOURCETICKETLIST[] = {"GAMAS", "FALLOUT"};
      String TICKET35[] = {"ANALYSIS", "BACKEND", "MEDIACARE", "SALAMSIM", "CLOSED"};
      String custSegmentHSIList[] = {"DES", "DGS", "DBS"};
      String finalcheck =
          (ticketStatus.getFinalCheck() == null) ? "" : ticketStatus.getFinalCheck();
      String SOURCETICKET =
          (ticketStatus.getSourceTicket() == null)
              ? ""
              : ticketStatus.getSourceTicket().toUpperCase();
      String createdBy =
          (ticketStatus.getCreatedTicketBy() == null) ? "" : ticketStatus.getCreatedTicketBy();
      String ticketId = (ticketStatus.getTicketId() == null) ? "" : ticketStatus.getTicketId();
      String customerSegment =
          (ticketStatus.getCustomerSegment() == null) ? "" : ticketStatus.getCustomerSegment();
      String serviceId = (ticketStatus.getServiceId() == null) ? "" : ticketStatus.getServiceId();
      String channel = (ticketStatus.getChannel() == null) ? "" : ticketStatus.getChannel();
      String STATUS_TARGET = (ticketStatus.getStatusCurrent()== null) ? "" : ticketStatus.getStatusCurrent();

      // SEND TO UPDATE MYTENS
      if (arrayManipulation.SearchDataOnArray(custSegmentHSIList, customerSegment)
          && channel.equalsIgnoreCase("35")) {
//        LogUtil.info(getClassName(), "SEND MY TENS START");
        if (arrayManipulation.SearchDataOnArray(TICKET35, STATUS_TARGET)) {
          logicToOtherAPI.UpdateMYTENS(ticketStatus, STATUS_TARGET, token);
        }
      }

    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
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
