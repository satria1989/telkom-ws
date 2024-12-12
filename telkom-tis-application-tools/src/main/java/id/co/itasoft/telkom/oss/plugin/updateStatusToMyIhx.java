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
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.function.SendStatusToMYIHX;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;

/**
 *
 * @author mtaup
 */
public class updateStatusToMyIhx extends DefaultApplicationPlugin {

    public static String pluginName = "Telkom New OSS - Update Status to MYIHX";
    SendStatusToMYIHX sstm;
    MasterParam myihxParamUpdate;
    MasterParamDao paramDao;
    InsertTicketStatusLogsDao itsDao;
    TicketStatus ticketStatus;
    RESTAPI _RESTAPI;
    ArrayManipulation arrayManipulation;
    LogInfo logInfo = new LogInfo();

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
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return null;
    }

    @Override
    public Object execute(Map map) {
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
        sstm = new SendStatusToMYIHX();
        myihxParamUpdate = new MasterParam();
        paramDao = new MasterParamDao();
        itsDao = new InsertTicketStatusLogsDao();
        ticketStatus = new TicketStatus();
        _RESTAPI = new RESTAPI();
        arrayManipulation = new ArrayManipulation();
        try {
            String token = _RESTAPI.getToken();
            ticketStatus = itsDao.getTicketId(processId);
            String Status = (ticketStatus.getStatus() == null) ? 
                "" : ticketStatus.getStatus().toUpperCase().trim();
            String channel = (ticketStatus.getChannel()== null) ? 
                "" : ticketStatus.getChannel();
            String StatusList[] = {"BACKEND", "DRAFT", "ANALYSIS", "CLOSED"};
            
            if (arrayManipulation.SearchDataOnArray(StatusList, Status)
                && "40".equals(channel)) {
                    myihxParamUpdate = paramDao.getUrl("update_ticket_id_to_myihx");
                    sstm.updateTicketToMyihx(ticketStatus, myihxParamUpdate, token);
            }
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            sstm = null;
            myihxParamUpdate = null;
            paramDao = null;
            itsDao = null;
            ticketStatus = null;
            _RESTAPI = null;
        }

        return null;
    }

}
