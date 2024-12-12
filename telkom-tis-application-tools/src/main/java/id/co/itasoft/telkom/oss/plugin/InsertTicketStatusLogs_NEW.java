/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GlobalQuerySelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.function.ActionStatusTicket;
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
public class InsertTicketStatusLogs_NEW extends DefaultApplicationPlugin {

  public static String pluginName =
      "Telkom New OSS - Ticket Incident Services - Insert Ticket Status Logs (ACTIVITY NEW)";
  InsertTicketStatusLogsDao dao;
  TicketStatus ticketStatus;
  ActionStatusTicket actionStatusTicket;
  GlobalQuerySelectCollections selectCollections;
  LogInfo logInfo = new LogInfo();

  public InsertTicketStatusLogs_NEW() {
    dao = new InsertTicketStatusLogsDao();
    actionStatusTicket = new ActionStatusTicket();
    ticketStatus = new TicketStatus();
    selectCollections = new GlobalQuerySelectCollections();
  }

  @Override
  public Object execute(Map map) {

    try {
      AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
      WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
      String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());

      if (!"".equalsIgnoreCase(processId)) {
        /** GET AND UPDATE TICKET ID */
        ticketStatus = selectCollections.getTicketId(processId);
        ticketStatus.setStatusTracking(ticketStatus.getTicketId());

        if (!"".equalsIgnoreCase(ticketStatus.getTicketId())) {

          ticketStatus.setStatus("NEW");
          ticketStatus.setStatusCurrent("NEW");
          String STATUS_TARGET = actionStatusTicket.getStatus(ticketStatus, processId);

            // INSERT CURRENT STATUS
          if (!"NEW".equalsIgnoreCase(STATUS_TARGET)) {
            ticketStatus.setStatus(STATUS_TARGET);
            ticketStatus.setStatusCurrent(STATUS_TARGET);
            dao.insertTicketStatus(ticketStatus);
          }

          /** UPDATE STATUS, and clear owner dan memo */
          dao.UpdateStatusAndClearOwner(processId, STATUS_TARGET);
        }

      }  // else process id kosong
    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      dao = null;
      ticketStatus = null;
      actionStatusTicket = null;
      selectCollections = null;
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
