/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.CompleteActivityTicketIncidentApiDao;
import id.co.itasoft.telkom.oss.plugin.dao.GetMasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.TimeToResolveDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateTicketDao;
import id.co.itasoft.telkom.oss.plugin.function.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
import id.co.itasoft.telkom.oss.plugin.model.ListSymptom;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class CompleteActivityTicketIncidentApiV2 extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Complete Process Api V2";

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        return "";
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
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    CompleteActivityTicketIncidentApiDao dao = new CompleteActivityTicketIncidentApiDao();
    ApiConfig apiConfUrlOnly = new ApiConfig();
    GetMasterParamDao paramDao = new GetMasterParamDao();

    @Override
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {

//        LogUtil.info(this.getClassName(), "**update_status_api");

        ApplicationContext ac = AppUtil.getApplicationContext();
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
//        CompleteActivityTicketIncidentApiDao dao = new CompleteActivityTicketIncidentApiDao();

        String pattern = "EEE dd MMM HH:mm:ss yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        TicketStatus r = new TicketStatus();
        TicketStatus tss = new TicketStatus();
        String duration = "";
        Timestamp lastTimestamp = new Timestamp(System.currentTimeMillis());
        TimeToResolveDao ttr = new TimeToResolveDao();
        UpdateTicketDao utDao = new UpdateTicketDao();

        try {
            dao.getApiAttribut();
        } catch (SQLException ex) {
            LogUtil.error(this.getClassName(), ex, "Message : " + ex.getMessage());
        } catch (Exception ex) {
            LogUtil.error(this.getClassName(), ex, "Message : " + ex.getMessage());
        }

//        dao.getApiAttribut();
        String apiIdDefined = dao.apiId;
        String apiKeyDefined = dao.apiKey;
        String apiSecret = dao.apiSecret;

        String headerApiId = hsr.getHeader("api_id");
        String headerApiKey = hsr.getHeader("api_key");

        String rspStatus = "";
        String rspMessage = "";
        String ticketNumber = "";

        boolean authStatus = true;
        boolean methodStatus = true;

        if (!apiIdDefined.equals(headerApiId) && !apiKeyDefined.equals(headerApiKey)) {
            authStatus = false;
            rspStatus = "401";
            rspMessage = "Invalid Authentication";
        }

        if (!"POST".equals(hsr.getMethod())) {
            methodStatus = false;
            rspStatus = "405";
            rspMessage = "Method Not Allowed";
        }

        JSONObject headMainObj = new JSONObject();
        JSONObject mainObj = new JSONObject();
        JSONObject jsonObj;
        if (authStatus && methodStatus) {
            try {
                apiConfUrlOnly = paramDao.getUrlCompleteApi("complete_activity_api");
                try {
                    // Get Body Parameter
                    StringBuilder jb = new StringBuilder();
                    String line = null;
                    try {
                        BufferedReader reader = hsr.getReader();
                        while ((line = reader.readLine()) != null) {
                            jb.append(line);
                        }
                    } catch (IOException e) {
                        LogUtil.error(this.getClassName(), e, "Error : " + e.getMessage());
                    }
                    String bodyParam = jb.toString();
                    JSONParser parse = new JSONParser();
                    org.json.simple.JSONObject data_obj = (org.json.simple.JSONObject) parse.parse(bodyParam);
                    ticketNumber = data_obj.get("ticket_number").toString();
                    String action = data_obj.get("action").toString();
                    boolean dataUpdateStatus = true;
                    boolean ukurIboosterStatus = false;
                    boolean completeStatus = true;
                    boolean updateSccResultStatus = false;
                    boolean updateWoComplete = false;
                    boolean triggerFinalcheckResolve = false;
                    String objDataUpdate = (data_obj.get("data_update") == null ? "" : data_obj.get("data_update").toString());
                    String closedBy = "";
                    String actualSolution = "";
                    String woNumber = "";
                    String scc_result = "";
                    String scc_time = "";
                    String pendingReason = "";
                    String pendingTimeOut = "";
                    String pendingStatus = "";
                    String scc_code = "";
                    org.json.simple.JSONObject dataUpdate = null;
                    if (!"".equals(objDataUpdate)) {
                        dataUpdate = (org.json.simple.JSONObject) parse.parse(objDataUpdate);
                        closedBy = ((dataUpdate.get("closed_by") == null) ? "" : dataUpdate.get("closed_by").toString());
                        actualSolution = (dataUpdate.get("actual_solution") == null) ? "" : dataUpdate.get("actual_solution").toString();
                        scc_result = (dataUpdate.get("scc_result") == null ? "" : dataUpdate.get("scc_result").toString());
                        scc_time = (dataUpdate.get("scc_time") == null ? "" : dataUpdate.get("scc_time").toString());
                        pendingReason = (dataUpdate.get("pending_reason") == null) ? "" : dataUpdate.get("pending_reason").toString();
                        pendingTimeOut = (dataUpdate.get("pending_timeout") == null) ? "" : dataUpdate.get("pending_timeout").toString();
                        woNumber = (dataUpdate.get("wo_number") == null ? "" : dataUpdate.get("wo_number").toString());
                        scc_code = (dataUpdate.get("scc_code") == null ? "" : dataUpdate.get("scc_code").toString());
                        if (dataUpdate.isEmpty()) {
                            dataUpdateStatus = false;
                        }
                    }
                    if (dataUpdate == null) {
                        dataUpdateStatus = false;
                    }
//                boolean completeStatus = false;

                    Ticket dataTicket = new Ticket();
                    dataTicket = dao.getProcessIdTicket(ticketNumber);

                    String oldActionStatus = dataTicket.getActionStatus();
                    String oldTicketStatus = dataTicket.getTicketStatus();
                    String id = dataTicket.getId();
                    String idTicketInc = dataTicket.getIdTicketInc();
                    List<WorkflowActivity> activityList = (List<WorkflowActivity>) workflowManager.getActivityList(dataTicket.getProcessId(), 0, -1, "id", false);
                    Map<String, String> variables = new HashMap<String, String>();
                    String solution = "";
                    String ownerGroup = "";
                    for (WorkflowActivity wa : activityList) {
                        if (wa.getState().equals("open.not_running.not_started")) {
                            String activityName = wa.getName();
                            String activityid = wa.getActivityDefId();
                            String processDefId = wa.getProcessDefId().replace("#", ":");
                            variables = new HashMap();
                            StringBuilder sb = new StringBuilder();
                            String status = "";
                            String ticketStatus = "";
                            String lastState = "";
                            ListSymptom symptom = new ListSymptom();
                            String url = apiConfUrlOnly.getUrl() + wa.getId() + "?";
                            RequestBody formBody = new FormBody.Builder()
                                    .add("j_username", apiConfUrlOnly.getjUsername())
                                    .add("j_password", apiConfUrlOnly.getjPassword())
                                    .build();

                            if (!"update_data".equalsIgnoreCase(action)) {
                                // ACTIVITY BACKEND
                                if ("backend".equalsIgnoreCase(activityid)) {

                                    lastState = "BACKEND";
                                    if ("REDISPATCH_BY_SELECT_SOLUTION".equalsIgnoreCase(action.trim())) {
                                        solution = (dataUpdate.get("solution_code") == null) ? "" : dataUpdate.get("solution_code").toString();
                                        if ("".equals(solution)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "solution_code is required", ticketNumber, bodyParam);
                                        } else {
//                                            sb.append("var_action_status=REDISPATCH_BY_SELECT_SOLUTION&");
//                                            sb.append("var_ticket_status=BACKEND");
                                            status = "BACKEND";
                                            ticketStatus = "BACKEND";
                                            variables.put("action_status", "REDISPATCH_BY_SELECT_SOLUTION");
                                            variables.put("ticket_status", "BACKEND");
                                            //GET OWNER GROUPs
                                            HashMap<String, String> paramsSymptom = new HashMap<String, String>();
                                            paramsSymptom.put("classification_code", solution);
                                            String Descsymptom = dao.getSolution(paramsSymptom);

                                            HashMap<String, String> paramsTkMapping = new HashMap<String, String>();
                                            paramsTkMapping.put("classification_id", solution);
                                            paramsTkMapping.put("workzone", dataTicket.getWorkzone());
                                            paramsTkMapping.put("customer_segment", dataTicket.getCust_segment());
                                            ownerGroup = dao.getOwnerGroup(paramsTkMapping);
                                        }
//                                dao.updateDataBackendCompleted(ownerGroup, solution, symptom.getDescription(), ticketStatus, dataTicket.getId());
                                    } else if ("REASSIGN_OWNERGROUP".equalsIgnoreCase(action.trim())) {
                                        if (!"".equals(dataUpdate.get("owner_group"))) {
//                                            sb.append("var_action_status=REASSIGN_OWNER&");
//                                            sb.append("var_ticket_status=BACKEND");
                                            variables.put("action_status", "REASSIGN_OWNER");
                                            variables.put("ticket_status", "BACKEND");
                                            status = "BACKEND";
                                            ticketStatus = "BACKEND";
                                            ownerGroup = dataUpdate.get("owner_group").toString();
                                            //update owner groups
                                        } else {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "owner_group is required", ticketNumber, bodyParam);
                                        }
                                    } else if ("REQUEST_PENDING".equalsIgnoreCase(action.trim())) {
                                        if ("".equals(pendingReason)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason is required", ticketNumber, bodyParam);
                                        } else if ("".equals(pendingTimeOut)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_time_out is required", ticketNumber, bodyParam);
                                        } else if ("".equals(pendingReason) && "".equals(pendingTimeOut)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason and pending_time_out is required", ticketNumber, bodyParam);
                                        } else {
//                                            sb.append("var_action_status=REQUEST_PENDING&");
//                                            sb.append("var_ticket_status=BACKEND&");
//                                            sb.append("var_pending_timeout=" + pendingTimeOut);
                                            variables.put("action_status", "REQUEST_PENDING");
                                            variables.put("ticket_status", "BACKEND");
                                            variables.put("pending_timeout", pendingTimeOut);
                                            status = "BACKEND";
                                            ticketStatus = "BACKEND";
                                        }

                                    } else if ("RESOLVE".equalsIgnoreCase(action.trim())) {
                                        //
                                        HashMap<String, String> paramCkWo = new HashMap<String, String>();
                                        paramCkWo.put("externalID1", ticketNumber);
                                        String statusWo = dao.getStatusWo(paramCkWo);
                                        if (!"".equals(actualSolution)) {
                                            HashMap<String, String> paramsActSolution = new HashMap<String, String>();
                                            paramsActSolution.put("classification_code", actualSolution);
                                            String actSol = dao.getActSolution(paramsActSolution);
                                            dao.updateDescription("c_actual_solution", actualSolution, id);
                                            dao.updateDescription("c_description_actualsolution", actSol, id);
                                        }
//                                    else{
//                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required");
//                                        completeStatus = false;
//                                    }

                                        if (statusWo.contains("OPEN") || statusWo.contains("ASSIGNED") || statusWo.contains("HOLD")) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "WO status is still open", ticketNumber, bodyParam);
                                        } else {
//                                            sb.append("var_action_status=RESOLVE&");
                                            variables.put("action_status", "RESOLVE");
                                            if (!"GAMAS".equalsIgnoreCase(dataTicket.getSource_ticket())) {
                                                if ("1".equals(dataTicket.getFinalcheck())) {
//                                                    sb.append("var_ticket_status=FINALCHECK&");
//                                                    sb.append("var_finalcheck=1&");
//                                                    sb.append("var_customer_segment=").append(dataTicket.getCust_segment()).append("&");
//                                                    sb.append("var_service_type=").append(dataTicket.getServiceType()).append("&");
//                                                    sb.append("var_classification_flag=").append(dataTicket.getClassificationFlag()).append("&");
//                                                    sb.append("var_child_gamas=").append(dataTicket.getChild_gamas());

                                                    variables.put("ticket_status", "FINALCHECK");
                                                    variables.put("finalcheck", "1");
                                                    variables.put("customer_segment", dataTicket.getCust_segment());
                                                    variables.put("service_type", dataTicket.getServiceType());
                                                    variables.put("classification_flag", dataTicket.getClassificationFlag());
                                                    variables.put("child_gamas", dataTicket.getChild_gamas());

                                                    status = "FINALCHECK";
                                                    ticketStatus = "FINALCHECK";
                                                    if ("DCS".equalsIgnoreCase(dataTicket.getCust_segment())) {
                                                        if ("TECHNICAL".equalsIgnoreCase(dataTicket.getClassificationFlag())
                                                                && ("INTERNET".equalsIgnoreCase(dataTicket.getServiceType())
                                                                || "VOICE".equalsIgnoreCase(dataTicket.getServiceType())
                                                                || "IPTV".equalsIgnoreCase(dataTicket.getServiceType()))) {

                                                        }

                                                    }
                                                } else {
                                                    if ("".equals(actualSolution)) {
                                                        completeStatus = false;
                                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
                                                    } else {
//                                                    LogUtil.info(this.getClassName(), "finalcheck : 1");
                                                        if ("DCS".equalsIgnoreCase(dataTicket.getCust_segment())) {
//                                                            sb.append("var_ticket_status=MEDIACARE");
                                                            variables.put("ticket_status", "MEDIACARE");
                                                            status = "MEDIACARE";
                                                            ticketStatus = "MEDIACARE";
                                                            //ukur ibooster
                                                            if ("TECHNICAL".equalsIgnoreCase(dataTicket.getClassificationFlag())
                                                                    && ("INTERNET".equalsIgnoreCase(dataTicket.getServiceType())
                                                                    || "VOICE".equalsIgnoreCase(dataTicket.getServiceType())
                                                                    || "IPTV".equalsIgnoreCase(dataTicket.getServiceType()))) {

                                                                ukurIboosterStatus = true;
                                                            }
                                                        } else {
//                                                            sb.append("var_ticket_status=SALAMSIM");
                                                            variables.put("ticket_status", "SALAMSIM");
                                                            status = "SALAMSIM";
                                                            ticketStatus = "SALAMSIM";
                                                        }
                                                    }
                                                }

                                            } else {
                                                if ("".equals(actualSolution)) {
                                                    completeStatus = false;
                                                    rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
                                                } else {
//                                                    sb.append("var_ticket_status=RESOLVED");
                                                    variables.put("ticket_status", "RESOLVED");
                                                    status = "RESOLVED";
                                                    ticketStatus = "RESOLVED";
                                                    if ("DCS".equalsIgnoreCase(dataTicket.getCust_segment())) {
                                                        if ("TECHNICAL".equalsIgnoreCase(dataTicket.getClassificationFlag())
                                                                && ("INTERNET".equalsIgnoreCase(dataTicket.getServiceType())
                                                                || "VOICE".equalsIgnoreCase(dataTicket.getServiceType())
                                                                || "IPTV".equalsIgnoreCase(dataTicket.getServiceType()))) {

                                                            ukurIboosterStatus = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        updateSccResultStatus = true;
                                        updateWoComplete = true;
                                    }

                                } else if ("analysis".equalsIgnoreCase(activityid)) { //ACTIVITY ANALYSIS
                                    lastState = "ANALYSIS";
                                    if ("REDISPATCH_BY_SELECT_SOLUTION".equalsIgnoreCase(action.trim())) {
                                        solution = (dataUpdate.get("solution_code") == null) ? "" : dataUpdate.get("solution_code").toString();
                                        if ("".equals(solution)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "solution_code is required", ticketNumber, bodyParam);
                                        } else {
//                                            sb.append("var_action_status=REDISPATCH_BY_SELECT_SOLUTION&");
//                                            sb.append("var_ticket_status=BACKEND");

                                            variables.put("action_status", "REDISPATCH_BY_SELECT_SOLUTION");
                                            variables.put("ticket_status", "BACKEND");
                                            status = "BACKEND";
                                            ticketStatus = "BACKEND";
                                            //GET OWNER GROUPs
                                            HashMap<String, String> paramsSymptom = new HashMap<String, String>();
                                            paramsSymptom.put("classification_code", solution);
                                            String Descsymptom = dao.getSolution(paramsSymptom);

                                            HashMap<String, String> paramsTkMapping = new HashMap<String, String>();
                                            paramsTkMapping.put("classification_id", solution);
                                            paramsTkMapping.put("workzone", dataTicket.getWorkzone());
                                            paramsTkMapping.put("customer_segment", dataTicket.getCust_segment());
                                            ownerGroup = dao.getOwnerGroup(paramsTkMapping);
                                        }
                                    } else if ("REASSIGN_OWNERGROUP".equalsIgnoreCase(action.trim())) {

                                        if (!"".equals(dataUpdate.get("owner_group"))) {
//                                            sb.append("var_action_status=REASSIGN_OWNER&");
//                                            sb.append("var_ticket_status=ANALYSIS");

                                            variables.put("action_status", "REASSIGN_OWNER");
                                            variables.put("ticket_status", "ANALYSIS");

                                            status = "ANALYSIS";
                                            ticketStatus = "ANALYSIS";
                                            ownerGroup = dataUpdate.get("owner_group").toString();
                                            //update owner groups
                                        } else {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "owner_group is required", ticketNumber, bodyParam);
                                        }
                                    } else if ("REQUEST_PENDING".equalsIgnoreCase(action.trim())) {
                                        if ("".equals(pendingReason)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason is required", ticketNumber, bodyParam);
                                        } else if ("".equals(pendingTimeOut)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_time_out is required", ticketNumber, bodyParam);
                                        } else if ("".equals(pendingReason) && "".equals(pendingTimeOut)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason and pending_time_out is required", ticketNumber, bodyParam);
                                        } else {
//                                            sb.append("var_action_status=REQUEST_PENDING&");
//                                            sb.append("var_ticket_status=ANALYSIS&");
//                                            sb.append("var_pending_timeout=" + pendingTimeOut);

                                            variables.put("action_status", "REQUEST_PENDING");
                                            variables.put("ticket_status", "ANALYSIS");
                                            variables.put("pending_timeout", pendingTimeOut);
                                            status = "ANALYSIS";
                                            ticketStatus = "ANALYSIS";
                                        }

                                        //Updare REQUEST_PENDING Completed
                                    } else if ("RESOLVE".equalsIgnoreCase(action.trim())) {
                                        //
                                        sb.append("var_action_status=RESOLVE&");
                                        if (!"GAMAS".equalsIgnoreCase(dataTicket.getSource_ticket())) {
                                            if ("1".equals(dataTicket.getFinalcheck())) {

                                                variables.put("ticket_status", "FINALCHECK");
                                                variables.put("finalcheck", "1");
                                                variables.put("customer_segment", dataTicket.getCust_segment());
                                                variables.put("service_type", dataTicket.getServiceType());
                                                variables.put("classification_flag", dataTicket.getClassificationFlag());
                                                variables.put("child_gamas", dataTicket.getChild_gamas());

                                                sb.append("var_ticket_status=FINALCHECK&");
                                                sb.append("var_finalcheck=1");
                                                status = "FINALCHECK";
                                                ticketStatus = "FINALCHECK";
                                                //ukur ibooster
//                                                if ("DCS".equalsIgnoreCase(dataTicket.getCust_segment())) {
//                                                    if ("TECHNICAL".equalsIgnoreCase(dataTicket.getClassificationFlag()) &&
//                                                            ("INTERNET".equalsIgnoreCase(dataTicket.getServiceType()) ||
//                                                            "VOICE".equalsIgnoreCase(dataTicket.getServiceType()) ||
//                                                            "IPTV".equalsIgnoreCase(dataTicket.getServiceType()))) {
//
////                                                    ukurIboosterStatus = true;
//                                                    }
//                                                }
//                                            triggerFinalcheckResolve = true;
                                            } else {
                                                if ("".equals(actualSolution)) {
                                                    completeStatus = false;
                                                    rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
                                                } else {
                                                    if ("DCS".equalsIgnoreCase(dataTicket.getCust_segment())) {
//                                                        sb.append("var_ticket_status=MEDIACARE");
                                                        variables.put("ticket_status", "MEDIACARE");
                                                        status = "MEDIACARE";
                                                        ticketStatus = "MEDIACARE";
                                                        //ukur ibooster
                                                        if ("TECHNICAL".equalsIgnoreCase(dataTicket.getClassificationFlag())
                                                                && ("INTERNET".equalsIgnoreCase(dataTicket.getServiceType())
                                                                || "VOICE".equalsIgnoreCase(dataTicket.getServiceType())
                                                                || "IPTV".equalsIgnoreCase(dataTicket.getServiceType()))) {

                                                            ukurIboosterStatus = true;
                                                        }
                                                    } else {
//                                                        sb.append("var_ticket_status=SALAMSIM");
                                                        variables.put("ticket_status", "SALAMSIM");
                                                        status = "SALAMSIM";
                                                        ticketStatus = "SALAMSIM";

                                                    }
                                                }
                                            }

                                        } else {
                                            if ("".equals(actualSolution)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
                                            } else {
//                                                sb.append("var_ticket_status=RESOLVED");
                                                variables.put("ticket_status", "RESOLVED");
                                                status = "RESOLVED";
                                                ticketStatus = "RESOLVED";
                                                if ("DCS".equalsIgnoreCase(dataTicket.getCust_segment())) {
                                                    if ("TECHNICAL".equalsIgnoreCase(dataTicket.getClassificationFlag())
                                                            && ("INTERNET".equalsIgnoreCase(dataTicket.getServiceType())
                                                            || "VOICE".equalsIgnoreCase(dataTicket.getServiceType())
                                                            || "IPTV".equalsIgnoreCase(dataTicket.getServiceType()))) {

                                                        ukurIboosterStatus = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } //ACTIVITY FINALCHECK
                                else if ("finalcheck".equalsIgnoreCase(activityid)) {
                                    if ("REASSIGN_OWNERGROUP".equalsIgnoreCase(action.trim())) {
                                        if (!"".equals(dataUpdate.get("owner_group"))) {
//                                            sb.append("var_action_status=REASSIGN_OWNER&");
//                                            sb.append("var_ticket_status=FINALCHECK");

                                            variables.put("action_status", "REASSIGN_OWNER");
                                            variables.put("ticket_status", "FINALCHECK");
                                            status = "FINALCHECK";
                                            ticketStatus = "FINALCHECK";
                                            ownerGroup = dataUpdate.get("owner_group").toString();
                                            //update owner groups
                                        } else {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "owner_group is required", ticketNumber, bodyParam);
                                        }
                                    } else if ("REOPEN".equalsIgnoreCase(action.trim())) {
//                                        sb.append("var_action_status=REOPEN&");
//                                        sb.append("var_ticket_status=" + dataTicket.getLast_state());

                                        variables.put("action_status", "REOPEN");
                                        variables.put("ticket_status", dataTicket.getLast_state());
                                        status = dataTicket.getLast_state();
                                        ticketStatus = dataTicket.getLast_state();
//                                dao.updateTicketStatus(activityid, status, ticketStatus, ticketStatus, url);
                                    } else if ("REQUEST_PENDING".equalsIgnoreCase(action.trim())) {
                                        if ("".equals(pendingReason)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason is required", ticketNumber, bodyParam);
                                        } else if ("".equals(pendingTimeOut)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_time_out is required", ticketNumber, bodyParam);
                                        } else if ("".equals(pendingReason) && "".equals(pendingTimeOut)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason and pending_time_out is required", ticketNumber, bodyParam);
                                        } else {
//                                            sb.append("var_action_status=REQUEST_PENDING&");
//                                            sb.append("var_ticket_status=FINALCHECK&");
//                                            sb.append("var_pending_timeout=" + pendingTimeOut);
//                                            
                                            variables.put("action_status", "REQUEST_PENDING");
                                            variables.put("ticket_status", "FINALCHECK");
                                            variables.put("pending_timeout", pendingTimeOut);
                                            status = "FINALCHECK";
                                            ticketStatus = "FINALCHECK";
                                        }

                                    } else {
//                                    sb.append("var_action_status=RESOLVE&");
                                        if ("1".equals(dataTicket.getFinalcheck())) {
                                            String nd = dataTicket.getService_no();
                                            String realm = "telkom.net";
                                            ListIbooster ibooster = new ListIbooster();
                                            ibooster = dao.getIbooster(nd, realm);
                                            dao.updateIbooster(ibooster, id);
                                            dao.insertWorkLogs(dataTicket.getIdTicketInc(), ticketNumber, dataTicket.getOwnerGroup(), ibooster.getMeasurementCategory());

                                            if (("PASSED".equalsIgnoreCase(dataTicket.getInternetTestResult()) || "PASSED".equalsIgnoreCase(dataTicket.getQcVoiceIvrResult())) && "spec".equalsIgnoreCase(ibooster.getMeasurementCategory())) {
                                                if ("".equals(actualSolution)) {
                                                    completeStatus = false;
                                                    rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
                                                } else {
//                                                    sb.append("var_action_status=RESOLVE&");
//                                                    sb.append("var_ticket_status=CLOSED&");
//                                                    sb.append("var_scc_value=TRUE");
//                                                    
                                                    variables.put("action_status", "RESOLVE");
                                                    variables.put("ticket_status", "CLOSED");
                                                    variables.put("scc_value", "TRUE");

                                                    status = "CLOSED";
                                                    ticketStatus = "CLOSED";
                                                }
                                            } else if ((!"PASSED".equalsIgnoreCase(dataTicket.getInternetTestResult()) || !"PASSED".equalsIgnoreCase(dataTicket.getQcVoiceIvrResult())) || dataTicket.getSccValue() == null && "spec".equalsIgnoreCase(ibooster.getMeasurementCategory())) {
                                                if ("".equals(actualSolution)) {
                                                    completeStatus = false;
                                                    rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
                                                } else {
//                                                    sb.append("var_action_status=RESOLVE&");
//                                                    sb.append("var_ticket_status=MEDIACARE");

                                                    variables.put("action_status", "RESOLVE");
                                                    variables.put("ticket_status", "MEDIACARE");

                                                    status = "MEDIACARE";
                                                    ticketStatus = "MEDIACARE";
                                                }
                                            } else {
//                                                sb.append("var_action_status=REOPEN&");
//                                                sb.append("var_ticket_status=BACKEND");

                                                variables.put("action_status", "REOPEN");
                                                variables.put("ticket_status", "BACKEND");
                                                status = "BACKEND";
                                                ticketStatus = "BACKEND";
                                            }
                                        } else {
                                            if ("".equals(actualSolution)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
                                            } else {
                                                if ("DCS".equalsIgnoreCase(dataTicket.getCust_segment())) {
//                                                    sb.append("var_action_status=RESOLVE&");
//                                                    sb.append("var_ticket_status=MEDIACARE");
                                                    variables.put("action_status", "RESOLVE");
                                                    variables.put("ticket_status", "MEDIACARE");

                                                    status = "MEDIACARE";
                                                    ticketStatus = "MEDIACARE";
                                                } else {
//                                                    sb.append("var_action_status=RESOLVE&");
//                                                    sb.append("var_ticket_status=SALAMSIM");

                                                    variables.put("action_status", "RESOLVE");
                                                    variables.put("ticket_status", "SALAMSIM");

                                                    status = "SALAMSIM";
                                                    ticketStatus = "SALAMSIM";
                                                }
                                            }
                                        }

                                    }
                                    // ACTIVITY SALAMSIM
                                } else if ("SALAMSIM".equalsIgnoreCase(activityid)) {
                                    if ("REOPEN".equalsIgnoreCase(action.trim())) {
//                                        sb.append("var_action_status=REOPEN&");
//                                        sb.append("var_ticket_status=" + dataTicket.getLast_state());

                                        variables.put("action_status", "REOPEN");
                                        variables.put("ticket_status", dataTicket.getLast_state());

                                        status = dataTicket.getLast_state();
                                        ticketStatus = dataTicket.getLast_state();
                                        // update status
                                    } else if ("CLOSED".equalsIgnoreCase(action.trim())) {
//
//                                        sb.append("var_action_status=RESOLVE&");
//                                        sb.append("var_ticket_status=CLOSED");
                                        variables.put("action_status", "RESOLVE");
                                        variables.put("ticket_status", "CLOSED");

                                        status = "CLOSED";
                                        ticketStatus = "CLOSED";

                                        // update status
                                    } else {
                                        if ("".equals(pendingReason)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason is required", ticketNumber, bodyParam);
                                        } else if ("".equals(pendingTimeOut)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_time_out is required", ticketNumber, bodyParam);
                                        } else if ("".equals(pendingReason) && "".equals(pendingTimeOut)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason and pending_time_out is required", ticketNumber, bodyParam);
                                        } else {
//                                            sb.append("var_action_status=REQUEST_PENDING&");
//                                            sb.append("var_ticket_status=SALAMSIM&");
//                                            sb.append("var_pending_timeout=" + pendingTimeOut);

                                            variables.put("action_status", "REQUEST_PENDING");
                                            variables.put("ticket_status", "SALAMSIM");
                                            variables.put("pending_timeout", pendingTimeOut);
                                            status = "SALAMSIM";
                                            ticketStatus = "SALAMSIM";
                                        }
                                    }
                                    // ACTIVITY MEDIACARE
                                } else if ("mediacare".equalsIgnoreCase(activityid)) {
                                    if ("REOPEN".equalsIgnoreCase(action.trim())) {
//                                        sb.append("var_action_status=REOPEN&");
//                                        sb.append("var_ticket_status=" + dataTicket.getLast_state());

                                        variables.put("action_status", "REOPEN");
                                        variables.put("ticket_status", dataTicket.getLast_state());
                                        status = dataTicket.getLast_state();
                                        ticketStatus = dataTicket.getLast_state();

                                        // update status
                                    } else if ("CLOSED".equalsIgnoreCase(action.trim())) {

//                                        sb.append("var_action_status=RESOLVE&");
//                                        sb.append("var_ticket_status=CLOSED");
                                        variables.put("action_status", "RESOLVE");
                                        variables.put("ticket_status", "CLOSED");
                                        status = "CLOSED";
                                        ticketStatus = "CLOSED";

                                    } else {
                                        if ("".equals(pendingReason)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason is required", ticketNumber, bodyParam);
                                        } else if ("".equals(pendingTimeOut)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_time_out is required", ticketNumber, bodyParam);
                                        } else if ("".equals(pendingReason) && "".equals(pendingTimeOut)) {
                                            completeStatus = false;
                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason and pending_time_out is required", ticketNumber, bodyParam);
                                        } else {
//                                            sb.append("var_action_status=REQUEST_PENDING&");
//                                            sb.append("var_ticket_status=MEDIACARE&");
//                                            sb.append("var_pending_timeout=" + pendingTimeOut);

                                            variables.put("action_status", "REQUEST_PENDING");
                                            variables.put("ticket_status", "MEDIACARE");
                                            variables.put("pending_timeout", pendingTimeOut);

                                            status = "MEDIACARE";
                                            ticketStatus = "MEDIACARE";
                                        }
                                        // update status
                                    }
                                    // ACTIVITY APPROVAL PENDING
                                } else if ("approvalPending".equalsIgnoreCase(activityid)) {
                                    if ("APPROVED".equalsIgnoreCase(action.trim())) {
//                                        sb.append("var_pending_status=APPROVED");
                                        variables.put("pending_status", "APPROVED");

                                        status = dataTicket.getTicketStatus();
                                        ticketStatus = dataTicket.getTicketStatus();
                                        action = "REQUEST_PENDING";
                                        pendingStatus = "APPROVED";
                                    } else if ("REQUEST_REJECT".equalsIgnoreCase(action.trim())) {
//                                        sb.append("var_pending_status=REQUEST_REJECTED&");
//                                        sb.append("var_action_status=AFTER_REQUEST");

                                        variables.put("pending_status", "REQUEST_REJECTED");
                                        variables.put("action_status", "AFTER_REQUEST");
                                        status = dataTicket.getTicketStatus();
                                        ticketStatus = dataTicket.getTicketStatus();
                                        action = "AFTER_REQUEST";
                                        pendingStatus = "";
                                    }
                                    // ACTIVITY PENDING TO
                                } else if ("pendingToAnalysis".equalsIgnoreCase(activityid) || "pendingToBackend".equalsIgnoreCase(activityid) || "pendingToSalamsim".equalsIgnoreCase(activityid)
                                        || "pendingToMediacare".equalsIgnoreCase(activityid) || "pendingToFinalcheck".equalsIgnoreCase(activityid)) {

//                                    sb.append("var_pending_status=REJECTED&");
//                                    sb.append("var_action_status=AFTER PENDING");
                                    variables.put("pending_status", "REJECTED");
                                    variables.put("action_status", "AFTER PENDING");
                                    // ACTIVITY DRAFT
                                } else if ("draftActivity".equalsIgnoreCase(activityid)) {
                                    if ("RESOLVE".equalsIgnoreCase(action.trim())) {
//                                        if ("".equals(actualSolution)) {
//                                            completeStatus = false;
//                                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
//                                        } else {
//                                            sb.append("var_action_status=RESOLVE&");
//                                            sb.append("var_ticket_status=FINALCHECK");
//                                            status = "FINALCHECK";
//                                            ticketStatus = "FINALCHECK";
//                                        }
                                        if ("1".equals(dataTicket.getFinalcheck())) {
//                                            sb.append("var_ticket_status=FINALCHECK&");
//                                            sb.append("var_finalcheck=1&");
//                                            sb.append("var_customer_segment=").append(dataTicket.getCust_segment()).append("&");
//                                            sb.append("var_service_type=").append(dataTicket.getServiceType()).append("&");
//                                            sb.append("var_classification_flag=").append(dataTicket.getClassificationFlag()).append("&");
//                                            sb.append("var_child_gamas=").append(dataTicket.getChild_gamas());

                                            variables.put("ticket_status", "FINALCHECK");
                                            variables.put("finalcheck", "1");
                                            variables.put("customer_segment", dataTicket.getCust_segment());
                                            variables.put("service_type", dataTicket.getServiceType());
                                            variables.put("classification_flag", dataTicket.getClassificationFlag());
                                            variables.put("child_gamas", dataTicket.getChild_gamas());

                                            status = "FINALCHECK";
                                            ticketStatus = "FINALCHECK";
                                        } else {
                                            if ("".equals(actualSolution)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
                                            } else {
                                                if ("DCS".equalsIgnoreCase(dataTicket.getCust_segment())) {
//                                                    sb.append("var_ticket_status=MEDIACARE");

                                                    variables.put("ticket_status", "MEDIACARE");

                                                    status = "MEDIACARE";
                                                    ticketStatus = "MEDIACARE";
                                                    //ukur ibooster
                                                    if ("TECHNICAL".equalsIgnoreCase(dataTicket.getClassificationFlag())
                                                            && ("INTERNET".equalsIgnoreCase(dataTicket.getServiceType())
                                                            || "VOICE".equalsIgnoreCase(dataTicket.getServiceType())
                                                            || "IPTV".equalsIgnoreCase(dataTicket.getServiceType()))) {

//                                                        ukurIboosterStatus = true;
                                                    }
                                                } else {
//                                                    sb.append("var_ticket_status=SALAMSIM");
                                                    variables.put("ticket_status", "SALAMSIM");
                                                    status = "SALAMSIM";
                                                    ticketStatus = "SALAMSIM";

                                                }
                                            }
                                        }
                                    } else if ("SENDTOANALYSIS".equalsIgnoreCase(action.trim())) {
//                                        sb.append("var_action_status=SENDTOANALYSIS&");
//                                        sb.append("var_ticket_status=ANALYSIS");
                                        variables.put("action_status", "SENDTOANALYSIS");
                                        variables.put("ticket_status", "ANALYSIS");
                                        status = "ANALYSIS";
                                        ticketStatus = "ANALYSIS";
                                    } else if ("CLOSE_WITH_SCC".equalsIgnoreCase(action.trim())) {
                                        String sccCodeValidation = "";
                                        if (scc_code.equals(dataTicket.getCodeValidation())) {
                                            sccCodeValidation = "VALID";
//                                            sb.append("var_action_status=RESOLVE&");
//                                            sb.append("var_ticket_status=CLOSED");

                                            variables.put("action_status", "RESOLVE");
                                            variables.put("ticket_status", "CLOSED");

                                            status = "CLOSED";
                                            ticketStatus = "CLOSED";

                                        } else {
                                            sccCodeValidation = "NOT VALID";
//                                            sb.append("var_action_status=RESOLVE&");
//                                            sb.append("var_ticket_status=FINALCHECK");

                                            variables.put("action_status", "RESOLVE");
                                            variables.put("ticket_status", "FINALCHECK");

                                            status = "FINALCHECK";
                                            ticketStatus = "FINALCHECK";
                                        }
                                        dao.updateSccCodeValidation(sccCodeValidation, id);
                                    } else if ("SEND_TO_TIER_2".equalsIgnoreCase(action.trim())) {
//                                        sb.append("var_action_status=SENDTOANALYSIS&");
//                                        sb.append("var_ticket_status=ANALYSIS");

                                        variables.put("action_status", "SENDTOANALYSIS");
                                        variables.put("ticket_status", "ANALYSIS");
                                        status = "ANALYSIS";
                                        ticketStatus = "ANALYSIS";
                                    }
                                } else { // ACTIVITY RESOLVED
                                    if ("REOPEN".equalsIgnoreCase(action.trim())) {
//                                        sb.append("var_action_status=REOPEN&");
//                                        sb.append("var_ticket_status=" + dataTicket.getLast_state());

                                        variables.put("action_status", "REOPEN");
                                        variables.put("ticket_status", dataTicket.getLast_state());
                                        status = dataTicket.getLast_state();
                                        ticketStatus = dataTicket.getLast_state();
                                    } else {
//                                        sb.append("var_action_status=RESOLVE&");
//                                        sb.append("var_ticket_status=CLOSED");
                                        variables.put("action_status", "RESOLVE");
                                        variables.put("ticket_status", "CLOSED");

                                        status = "CLOSED";
                                        ticketStatus = "CLOSED";
                                    }
                                }
                                if (completeStatus) {

                                    lastTimestamp = utDao.getTicketStatusByTicketID(idTicketInc);
                                    duration = ttr.getDuration(lastTimestamp);
                                    r.setTicketId(ticketNumber);
                                    r.setStatusTracking(duration);
                                    r.setStatus(dataTicket.getStatus());
                                    r.setStatusCurrent(dataTicket.getStatus());
                                    r.setChangeBy("INTEGRATION");
                                    r.setOwner("INTEGRATION");
                                    r.setAssignedOwnerGroup(dataTicket.getOwnerGroup());
//                                    LogUtil.info(this.getClassName(), "**Insert To Log History");
                                    boolean insertTicketStatus = utDao.insertNewTicketStatus(r);

                                    ApiConfig apiConfig = new ApiConfig();
                                    apiConfig.setUrl(url + sb.toString());
//                                    dao.updateStatusTmp(action.trim(), ticketStatus, id);
                                    if (dataUpdateStatus) {
                                        dao.updateTicketParam(dataUpdate, id);
                                    }
                                    if (!"".equals(pendingTimeOut)) {
                                        dao.updatePending(pendingTimeOut, id);
                                    }
                                    if (updateSccResultStatus) {
                                        dao.updateSccResult(dataTicket.getServiceType(), scc_result, scc_time, id);
                                    }

                                    boolean completeProcess;

                                    try {
                                        workflowManager.activityVariables(wa.getId(), variables);
                                        workflowManager.assignmentForceComplete(processDefId, dataTicket.getProcessId(), wa.getId(), "000000");
                                        completeProcess = true;
                                    } catch (Exception ex) {
                                        completeProcess = false;
                                        JSONObject jObj;
                                        jObj = new JSONObject();
                                        mainObj.put("code", 500);
                                        mainObj.put("message", "update status failed");
                                        mainObj.put("nextActivityId", "-");
                                        insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
                                        mainObj.write(hsr1.getWriter());
                                    }

//                                    String response = assigmentCompleteProcess(apiConfig, formBody);
//                                    JSONParser parseResp = new JSONParser();
//                                    org.json.simple.JSONObject data_obj_rsp = (org.json.simple.JSONObject) parseResp.parse(response);
                                    if (completeProcess) {
                                        if ("backend".equalsIgnoreCase(activityid) || "analysis".equalsIgnoreCase(activityid)) {
                                            if ("REDISPATCH_BY_SELECT_SOLUTION".equalsIgnoreCase(action.trim())) {
                                                dao.updateDataBackendCompleted(ownerGroup, solution, symptom.getDescription(), ticketStatus, dataTicket.getId());
                                            }

                                        }

                                        if (ukurIboosterStatus) {
                                            String nd = dataTicket.getService_no();
//                                        String realm = dataTicket.getRealm();
//                                    String nd = "121122212061";
                                            String realm = "telkom.net";
                                            ListIbooster ibooster = new ListIbooster();
                                            ibooster = dao.getIbooster(nd, realm);
                                            dao.updateIbooster(ibooster, id);
                                            dao.insertWorkLogs(dataTicket.getIdTicketInc(), ticketNumber, dataTicket.getOwnerGroup(), ibooster.getMeasurementCategory());
                                        }

                                        if (!"".equalsIgnoreCase(closedBy)) {
//                                        LogUtil.info(this.getClassName(), "masuk fungsi close by");
                                            dao.updateParamDescription("CLOSEDBY", closedBy, "c_description_closed_by", id);
                                        }

                                        if (!"".equals(actualSolution)) {
//                                        LogUtil.info(this.getClassName(), "actual_solution != null");
                                            HashMap<String, String> paramsActSolution = new HashMap<String, String>();
                                            paramsActSolution.put("classification_code", actualSolution);
                                            String actSol = dao.getActSolution(paramsActSolution);
                                            dao.updateDescription("c_description_actualsolution", actSol, id);
                                        }

                                        if (!"".equals(woNumber)) {
                                            dao.updateStatusWo(idTicketInc, id);
                                        }
                                    if (updateSccResultStatus) {
                                        
                                        dao.updateSccResult(dataTicket.getServiceType(), scc_result, scc_time, id);
                                    }

                                        JSONObject jObj;
                                        jObj = new JSONObject();
                                        mainObj.put("code", 200);
                                        mainObj.put("message", "update status successful");
                                        mainObj.put("nextActivityId", "-");
                                        insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
                                        mainObj.write(hsr1.getWriter());

                                    } else {
//                                        dao.updateStatusTmp(oldActionStatus, oldTicketStatus, id);
                                    }
                                }
                            } else if ("update_data".equalsIgnoreCase(action)) {
                                if (dao.updateTicketParam(dataUpdate, id)) {
                                    JSONObject jObj;
                                    jObj = new JSONObject();
                                    mainObj.put("code", 200);
                                    mainObj.put("message", "data update successful");
                                    insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
                                    mainObj.write(hsr1.getWriter());

                                } else {
                                    JSONObject jObj;
                                    jObj = new JSONObject();
                                    mainObj.put("date", date);
                                    mainObj.put("code", 500);
                                    mainObj.put("message", dao.errorUpdateData);
                                    headMainObj.put("error", mainObj);
                                    insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
                                    headMainObj.write(hsr1.getWriter());
                                }
                            } else {
                                try {
                                    JSONObject jObj;
                                    jObj = new JSONObject();
                                    mainObj.put("date", date);
                                    mainObj.put("code", 500);
                                    mainObj.put("message", "System error, please check the logs file");
                                    headMainObj.put("error", mainObj);
                                    insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
                                    headMainObj.write(hsr1.getWriter());
                                } catch (JSONException ex) {
                                    LogUtil.error(this.getClassName(), ex, "Error : " + ex.getMessage());
                                }
                            }

                        }
                    }

                } catch (ParseException ex) {
                    LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
                } catch (Exception ex) {
                    LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClassName(), ex, "Message : " + ex.getMessage());
            }
        } else {
            try {
                JSONObject jObj;
                jObj = new JSONObject();
                mainObj.put("code", rspStatus);
                mainObj.put("message", rspMessage);
//                insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
                mainObj.write(hsr1.getWriter());
            } catch (JSONException ex) {
                LogUtil.error(this.getClassName(), ex, "Error : " + ex.getMessage());
            }
        }

    }

    private void triggerFinalcheckRosove(HttpServletResponse hsr1, Ticket dataTicket, String sccResult, String id, String date, String ticketNumber, String status, String ticketStatus,
            String actualSolution, String activityId, String activity, String bodyParam) throws JSONException, IOException {
//        LogUtil.info(this.getClassName(), "Trigger Finalcheck Resolve");
        boolean completeStatus = true;
        String actionStatus = "";

        try {
            String url = apiConfUrlOnly.getUrl() + activityId + "?";
            RequestBody formBody = new FormBody.Builder()
                    .add("j_username", apiConfUrlOnly.getjUsername())
                    .add("j_password", apiConfUrlOnly.getjUsername())
                    .build();

            JSONObject headMainObj = new JSONObject();
            JSONObject mainObj = new JSONObject();
            JSONObject jsonObj;
            StringBuilder sb = new StringBuilder();

//            sb.append("var_action_status=RESOLVE&");
            if ("1".equals(dataTicket.getFinalcheck())) {
                String nd = dataTicket.getService_no();
                String realm = "telkom.net";
                ListIbooster ibooster = new ListIbooster();
                ibooster = dao.getIbooster(nd, realm);
                dao.updateIbooster(ibooster, dataTicket.getId());
                dao.insertWorkLogs(dataTicket.getIdTicketInc(), ticketNumber, dataTicket.getOwnerGroup(), ibooster.getMeasurementCategory());
//                LogUtil.info(this.getClassName(), "TRIGER RESOLVE FINALCHECK : " + ibooster.getMeasurementCategory());
                if ("analysis".equalsIgnoreCase(activity)) {
                    if ("SPEC".equalsIgnoreCase(ibooster.getMeasurementCategory())) {
                        sb.append("var_action_status=RESOLVE&");
                        sb.append("var_ticket_status=MEDIACARE");
                        status = "MEDIACARE";
                        ticketStatus = "MEDIACARE";
                        actionStatus = "RESOLVE";
                    } else {
                        completeStatus = false;
                        try {
                            JSONObject jObj;
                            jObj = new JSONObject();
                            mainObj.put("date", date);
                            mainObj.put("code", "200");
                            mainObj.put("message", "Ibooster measurement results unspec, ticket are in finalcheck");
                            headMainObj.put("error", mainObj);
                            headMainObj.write(hsr1.getWriter());
                        } catch (IOException ex) {
                            LogUtil.error(this.getClassName(), ex, "Error Response : " + ex.getMessage());
                        }
                    }
                } else {
                    if ("PASSED".equalsIgnoreCase(sccResult) && "SPEC".equalsIgnoreCase(ibooster.getMeasurementCategory())) {
//                        LogUtil.info(this.getClassName(), "SCC PASSED , Ibooster SPEC");
                        if ("".equals(actualSolution)) {
                            completeStatus = false;
                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
                        } else {
                            sb.append("var_action_status=RESOLVE&");
                            sb.append("var_ticket_status=CLOSED&");
                            sb.append("var_scc_value=TRUE");
                            status = "CLOSED";
                            ticketStatus = "CLOSED";
                            actionStatus = "RESOLVE";
                        }
                    } else if ((!"PASSED".equalsIgnoreCase(sccResult) || sccResult == null) && "spec".equalsIgnoreCase(ibooster.getMeasurementCategory())) {
//                        LogUtil.info(this.getClassName(), "SCC != PASSED && SEPEC");
                        if ("".equals(actualSolution)) {;
                            completeStatus = false;
                            rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
                        } else {
                            sb.append("var_action_status=RESOLVE&");
                            sb.append("var_ticket_status=MEDIACARE");
                            status = "MEDIACARE";
                            ticketStatus = "MEDIACARE";
                            actionStatus = "RESOLVE";
                        }
                    } else {
//                        LogUtil.info(this.getClassName(), "## MASUK UNSEPEC");
                        sb.append("var_action_status=REOPEN&");
                        sb.append("var_ticket_status=BACKEND");
                        status = "BACKEND";
                        ticketStatus = "BACKEND";
//                        dao.updateStatusTmp("REOPEN", ticketStatus, id);
                    }
                }

            } else {
                if ("".equals(actualSolution)) {
                    completeStatus = false;
                    rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam);
                } else {
                    if ("DCS".equalsIgnoreCase(dataTicket.getCust_segment())) {
                        sb.append("var_action_status=RESOLVE&");
                        sb.append("var_ticket_status=MEDIACARE");
                        status = "MEDIACARE";
                        ticketStatus = "MEDIACARE";
                    } else {
                        sb.append("var_action_status=RESOLVE&");
                        sb.append("var_ticket_status=SALAMSIM");
                        status = "SALAMSIM";
                        ticketStatus = "SALAMSIM";
                    }
                }
            }

            if (completeStatus) {
//                dao.updateStatusTmp(actionStatus, ticketStatus, id);
                ApiConfig apiConfig = new ApiConfig();
                apiConfig.setUrl(url + sb.toString());

                String response = assigmentCompleteProcess(apiConfig, formBody);
                dao.updateStatusTicket(status, ticketStatus, id);
                hsr1.getWriter().print(response);
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClassName(), ex, "Error Trigger Complete Finalcheck : " + ex.getMessage());
        }
    }

    private final OkHttpClient httpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build();

    public String assigmentCompleteProcess(ApiConfig apiConfig, RequestBody formBody) throws Exception {
//        LogUtil.info(this.getClassName(), "Assigment Complete Function");
        String stringResponse = "";

        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .post(formBody)
                .build();

        try ( Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
//                LogUtil.info(this.getClass().getName(), "Unexpected code " + response);
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        }
        return stringResponse;
    }

    private void rspFieldMandatoryFalse(JSONObject mainObj, JSONObject headMainObj, String date, HttpServletResponse hsr1, String message, String ticketNumber, String bodyParam) throws JSONException {
        try {
            JSONObject jObj;
            jObj = new JSONObject();
            mainObj.put("date", date);
            mainObj.put("code", "500");
            mainObj.put("message", message);
            headMainObj.put("error", mainObj);
            insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
            headMainObj.write(hsr1.getWriter());
        } catch (IOException ex) {
            LogUtil.error(this.getClassName(), ex, "Error Response : " + ex.getMessage());
        }
    }

    private void insertLosgHystoryAPI(String bodyParam, String ticketNumber, String response) {
        LogHistoryDao lh = new LogHistoryDao();
        LogHistory dataLh = new LogHistory();

        dataLh.setRequest(bodyParam);
        dataLh.setMethod("POST");
        dataLh.setAction("UPDATE_STATUS_API");
        dataLh.setUrl("https://oss-incident.telkom.co.id/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.CompleteActivityTicketIncidentApiV2/service");
        dataLh.setTicketId(ticketNumber);
        dataLh.setResponse(response);
        try {

            lh.insertToLogHistory(dataLh);
        } catch (Exception ex) {
            LogUtil.error(getClassName(), ex, "ex err:" + ex.getMessage());
        }
    }

}
