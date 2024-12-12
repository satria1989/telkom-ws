/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogicToOtherAPI;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;

/**
 * @author asani
 */
public class UpdatePhoneNumberToOtherService extends DefaultApplicationPlugin {

  public static String pluginName =
      "Telkom New OSS - Ticket Incident Services - Update Phone Number To Other Service";
  InsertTicketStatusLogsDao dao;
  LogicToOtherAPI otherAPI;
  TicketStatus ticketStatus;
  LogInfo logInfo = new LogInfo();

  @Override
  public Object execute(Map map) {
    try {
      otherAPI = new LogicToOtherAPI();
      dao = new InsertTicketStatusLogsDao();
      ticketStatus = new TicketStatus();

      AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
      WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
      String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());

      ticketStatus = dao.getTicketId(processId);
      String sourceTicket = (ticketStatus.getSourceTicket() == null) ? 
          "" : ticketStatus.getSourceTicket();
      
      if (!"GAMAS".equals(sourceTicket)) { // YG GAMAS TIDAK MENGISI PHONE NUMBER
        otherAPI.UpdatePhoneNumberToCstmrIfmtn(ticketStatus);
      }
    } catch (Exception ex) {
      LogInfo logInfo = new LogInfo();
    } finally {
      otherAPI = null;
      dao = null;
      ticketStatus = null;
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
