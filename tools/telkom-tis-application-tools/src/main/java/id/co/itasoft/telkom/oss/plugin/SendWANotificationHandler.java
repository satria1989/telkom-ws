/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateSCCCode;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.function.SendWANotification;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;

/**
 *
 * @author itasoft
 */
public class SendWANotificationHandler extends DefaultApplicationPlugin {

    final static String pluginName = "Telkom New OSS - Ticket Incident Services - Send WA Notification";
    LogInfo logInfo = new LogInfo();
    private String processIdIncident;
    SendWANotification swa;
    TicketStatus ticketStatus;
    InsertTicketStatusLogsDao dao;
    GenerateRandomNum grn;
    UpdateSCCCode sCCCode;
    ArrayManipulation arrayManipulation;
    RESTAPI _RESTAPI;
    String token = "";
    MasterParam masterParam;
    MasterParamDao masterParamDao;
    LogHistory logHistory;
    LogHistoryDao logHistoryDao;

    public SendWANotificationHandler() {
        dao = new InsertTicketStatusLogsDao();
        ticketStatus = new TicketStatus();
        swa = new SendWANotification();
        grn = new GenerateRandomNum();
        sCCCode = new UpdateSCCCode();
        arrayManipulation = new ArrayManipulation();
        _RESTAPI = new RESTAPI();
        token = _RESTAPI.getToken();
        masterParam = new MasterParam();
        masterParamDao = new MasterParamDao();
        logHistory = new LogHistory();
        logHistoryDao = new LogHistoryDao();
    }

    @Override
    public Object execute(Map map) {

        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        final String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());

        try {
            ticketStatus = dao.getTicketId(processId);
            String custSegmentHSIList[] = {"DES", "DGS", "DBS"};
            String SERVICETYPELIST[] = {"INTERNET", "IPTV", "VOICE"};
            String CHANNELLISTEBIS[] = {"2", "4", "10", "20", "32", "67", "68", "69"};

            String custSegment = (ticketStatus.getCustomerSegment() == null) ? 
                "" : ticketStatus.getCustomerSegment().toUpperCase().trim();
            String sourceTicket = (ticketStatus.getSourceTicket() == null) ? 
                "" : ticketStatus.getSourceTicket().toUpperCase().trim();
            String classificationType = (ticketStatus.getClassification_type() == null) ? 
                "" : ticketStatus.getClassification_type().toUpperCase().trim();
            String ticketId = (ticketStatus.getTicketId()== null) ? 
                "" : ticketStatus.getTicketId().toUpperCase().trim();
            String phone = (ticketStatus.getPhone()== null) ? 
                "" : ticketStatus.getPhone().toUpperCase().trim();
            String serviceType = (ticketStatus.getServiceType()== null) ? 
                "" : ticketStatus.getServiceType().toUpperCase().trim();
            String serviceNum = (ticketStatus.getServiceNo()== null) ? 
                "" : ticketStatus.getServiceNo().toUpperCase().trim();
            String password = (ticketStatus.getLandingPage()== null) ? 
                "" : ticketStatus.getLandingPage().toUpperCase().trim(); // landingpage
            String channel = (ticketStatus.getChannel()== null) ? 
                "" : ticketStatus.getChannel().toUpperCase().trim(); // landingpage

            
            if ("CUSTOMER".equalsIgnoreCase(sourceTicket) && "DCS".equalsIgnoreCase(custSegment)) {
              logInfo.Log(getClass().getName(), "MAASUK DCS");
            
                if ("LOGIC".equalsIgnoreCase(classificationType)) {
                    /**
                     * LOGIC MUST UPDATE SCC_CODE_VALIDATION
                     */

                    String codeValidation = grn.getNumericRandom(4);
                    ticketStatus.setCode_validation(codeValidation);
                    sCCCode.updateCodeValidation(ticketStatus); // UPDATE CODE VALIDATION SCC TO DB
                    swa.sendNotify(ticketStatus);
                    codeValidation = null;

                } else if ("FISIK".equalsIgnoreCase(ticketStatus.getClassification_type())) {
                    swa.sendNotify(ticketStatus);
                } else {
                    swa.sendNotify(ticketStatus);
                }
            } else if ("CUSTOMER".equalsIgnoreCase(sourceTicket)
                && arrayManipulation.SearchDataOnArray(custSegmentHSIList, custSegment)
                && arrayManipulation.SearchDataOnArray(CHANNELLISTEBIS, channel)) {
              
                swa.sendWAEbis(ticketStatus, token);
            }

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            ticketStatus = null;
            dao = null;
            grn = null;
            sCCCode = null;
            swa = null;
        }

//        LogUtil.info(getClassName(), "UDAH GK PERLU NUNGGU BROO --- LLL");
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
