/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.SendStatusToMycx;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;

/**
 * @author asani
 */
public class sendToMyCX extends DefaultApplicationPlugin {

  static final String pluginName = "Telkom New OSS - Ticket Incident Services - SendTo MyCX";
  MasterParam param;
  MasterParamDao paramDao;
  InsertTicketStatusLogsDao dao;
  SendStatusToMycx mycx;
  TicketStatus ticketStatus;
  ArrayManipulation arrayManipulation;
  LogInfo logInfo = new LogInfo();

  @Override
  public Object execute(Map map) {
    AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
    WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
    String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());

    param = new MasterParam();
    paramDao = new MasterParamDao();
    dao = new InsertTicketStatusLogsDao();
    mycx = new SendStatusToMycx();
    ticketStatus = new TicketStatus();
    arrayManipulation = new ArrayManipulation();

    try {
      ticketStatus = dao.getTicketId(processId);
      String status = (ticketStatus.getStatusCurrent() == null) ? 
          "" : ticketStatus.getStatusCurrent(); 
      String statusList[] = {"DRAFT", "NEW"};

      if (!arrayManipulation.SearchDataOnArray(statusList, status)) {
        param = paramDao.getUrl("update_status_ticket_to_mycx");
        mycx.updateStatusTicket(ticketStatus, param);
      }

    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      param = null;
      paramDao = null;
      dao = null;
      mycx = null;
      ticketStatus = null;
      arrayManipulation = null;
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
