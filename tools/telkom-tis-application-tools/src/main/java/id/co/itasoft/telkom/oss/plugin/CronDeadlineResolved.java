/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.CronDeadlineResolvedDao;
import id.co.itasoft.telkom.oss.plugin.dao.GlobalQuerySelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateQueryCollectionDao;
import id.co.itasoft.telkom.oss.plugin.function.ActionStatusTicket;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ListEnum;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

/**
 * @author asani
 */
public class CronDeadlineResolved extends DefaultApplicationPlugin {

  private String pluginName =
      "Telkom New OSS - Ticket Incident Services - CRON DEADLINE STATUS RESOLVED";
  LogInfo logInfo = new LogInfo();

  CronDeadlineResolvedDao cronDeadlineResolvedDao;
  ActionStatusTicket actionStatusTicket;
  UpdateQueryCollectionDao updateQueryCollectionDao;
  GlobalQuerySelectCollections querySelectCollections;

  public CronDeadlineResolved() {
    querySelectCollections = new GlobalQuerySelectCollections();
    cronDeadlineResolvedDao = new CronDeadlineResolvedDao();
    actionStatusTicket = new ActionStatusTicket();
    updateQueryCollectionDao = new UpdateQueryCollectionDao();
  }

  @Override
  public Object execute(Map map) {
    
    try {
      AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
      WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
      ApplicationContext ac = AppUtil.getApplicationContext();
      WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");

      JSONObject jsonConfiguration = new JSONObject();
      jsonConfiguration = querySelectCollections.getConfigurationMapping();
      
      boolean deadlineResolved = jsonConfiguration.getBoolean("deadline_resolved");
      int day = jsonConfiguration.getInt("day_resolved");
      int hour = jsonConfiguration.getInt("hour_resolved");
      int minutes = jsonConfiguration.getInt("minutes_resolved");

      int TotalHour = 0;
      int TotalMinutes = 0;
      String duration = "";
      if (day > 0) {
        TotalHour += day * 24;
      }

      if (hour > 0) {
        TotalHour += hour;
      }
      
      if(hour == 0 && minutes == 0 && day == 0) {
        deadlineResolved = false;
      }

      TotalMinutes = (TotalHour * 60) + minutes;
      duration = String.valueOf(TotalMinutes);
      if (deadlineResolved) {
        List<TicketStatus> listTicket = new ArrayList<TicketStatus>();

        listTicket = cronDeadlineResolvedDao.getDataResolved(duration);
        String DEADLINETOSALAMSIM = ListEnum.DEADLINETOSALAMSIM.toString();
        String DEADLINETOMEDIACARE = ListEnum.DEADLINETOMEDIACARE.toString();

        if (!listTicket.isEmpty()) {
          for (TicketStatus ticketStatus : listTicket) {
            String processDefId = ticketStatus.getProcess_def_id().replace("#", ":");
            String processId = ticketStatus.getProcessId();
            String parentTicket = ticketStatus.getParentId();
            String activityId = ticketStatus.getActivicity_id();

//            executor.submit(
//                () -> {
//                  try {
                    updateQueryCollectionDao.updateDeadlineLogic(parentTicket, DEADLINETOMEDIACARE);
                    workflowManager.assignmentForceComplete(
                        processDefId, 
                        processId, 
                        activityId, 
                        "000000"
                    );
//                  } catch (SQLException ex) {
//                    LogUtil.error(getClass().getName(), ex, ex.getMessage());
//                  }
//                }
//            );
          }
        }
      }

    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      querySelectCollections = null;
      cronDeadlineResolvedDao = null;
      actionStatusTicket = null;
      updateQueryCollectionDao = null;
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
