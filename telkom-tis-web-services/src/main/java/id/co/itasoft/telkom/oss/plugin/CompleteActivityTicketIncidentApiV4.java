/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.CompleteActivityTicketIncidentApiDao;
import id.co.itasoft.telkom.oss.plugin.dao.GetMasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.TimeToResolveDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateTicketOwnerDao;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.function.RequestToken;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class CompleteActivityTicketIncidentApiV4 extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Complete Process Api V4";

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
        outerLoop:
        {
//        LogUtil.info(this.getClassName(), "**update_status_api v4");
            ApplicationContext ac = AppUtil.getApplicationContext();
            WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");

            String pattern = "EEE dd MMM HH:mm:ss yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());

            TicketStatus r = new TicketStatus();
            TicketStatus tss = new TicketStatus();
            Timestamp lastTimestamp = new Timestamp(System.currentTimeMillis());
            TimeToResolveDao ttr = new TimeToResolveDao();
            UpdateTicketDao utDao = new UpdateTicketDao();
            UpdateTicketOwnerDao utoDoa = new UpdateTicketOwnerDao();
            String actionConsition = "";

            try {
                dao.getApiAttribut();
            } catch (SQLException ex) {
                LogUtil.error(this.getClassName(), ex, "Message : " + ex.getMessage());
            } catch (Exception ex) {
                LogUtil.error(this.getClassName(), ex, "Message : " + ex.getMessage());
            }

            String apiIdDefined = dao.apiId;
            String apiKeyDefined = dao.apiKey;

            String headerApiId = hsr.getHeader("api_id");
            String headerApiKey = hsr.getHeader("api_key");

            int rspStatus = 0;
            String rspMessage = "";
            String ticketNumber = "";

            boolean authStatus = true;
            boolean methodStatus = true;

            if (!apiIdDefined.equals(headerApiId) && !apiKeyDefined.equals(headerApiKey)) {
                authStatus = false;
                rspStatus = 401;
                rspMessage = "Invalid Authentication";
            }

            if (!"POST".equals(hsr.getMethod())) {
                methodStatus = false;
                rspStatus = 405;
                rspMessage = "Method Not Allowed";
            }

            JSONObject headMainObj = new JSONObject();
            JSONObject mainObj = new JSONObject();
            JSONObject jsonObj;
            if (authStatus && methodStatus) {
                Ticket dataTicket = null;
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
                    String action = (data_obj.get("action") == null ? "" : data_obj.get("action")).toString();

                    if (action != null && !"".equals(action)) {
                        boolean dataUpdateStatus = true;
                        boolean ukurIboosterStatus = false;
                        boolean completeStatus = true;
                        boolean updateSccResultStatus = false;
                        boolean updateTscResultStatus = false;
                        boolean updateWoComplete = false;
                        boolean triggerFinalcheckResolve = false;
                        boolean runningTicketStatus = false;
                        boolean createWoStatus = false;
                        String objDataUpdate = (data_obj.get("data_update") == null ? "" : data_obj.get("data_update").toString());
                        String closedBy = "";
                        String actualSolution = "";
                        String incidentDomain = "";
                        String woNumber = "";
                        String scc_result = "";
                        String tsc_result = "";
                        String scc_time = "";
                        String tsc_time = "";
                        String pendingReason = "";
                        String pendingTimeOut = "";
                        String pendingStatus = "";
                        String scc_code = "";
                        String flagFcr = "";
                        String flagLevel = "";
                        org.json.simple.JSONObject dataUpdate = null;
                        if (!"".equals(objDataUpdate)) {
                            dataUpdate = (org.json.simple.JSONObject) parse.parse(objDataUpdate);
                            closedBy = ((dataUpdate.get("closed_by") == null) ? "" : dataUpdate.get("closed_by").toString());

                            if (!"".equals(closedBy)) {
                                if (!"5".equals(closedBy)) {
                                    boolean cekResult = dao.cekDataOnMasterParam(closedBy, "CLOSEDBY");
                                    if (!cekResult) {
                                        completeStatus = false;
                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "invalid closed_by code", ticketNumber, bodyParam, 500);
                                        break outerLoop;
                                    }
                                } else {
                                    completeStatus = false;
                                    rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "invalid closed_by code", ticketNumber, bodyParam, 500);
                                    break outerLoop;
                                }
                            }

                            if (dataUpdate.containsKey("flag_fcr")) {
                                completeStatus = false;
                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "Unable to update FCR flag", ticketNumber, bodyParam, 400);
                                break outerLoop;
                            }

                            actualSolution = (dataUpdate.get("actual_solution") == null) ? "" : dataUpdate.get("actual_solution").toString();
                            incidentDomain = (dataUpdate.get("incident_domain") == null) ? "" : dataUpdate.get("incident_domain").toString();
                            scc_result = (dataUpdate.get("scc_result") == null ? "" : dataUpdate.get("scc_result").toString());
                            tsc_result = (dataUpdate.get("tsc_result") == null ? "" : dataUpdate.get("tsc_result").toString());
                            scc_time = (dataUpdate.get("scc_time") == null ? "" : dataUpdate.get("scc_time").toString());
                            tsc_time = (dataUpdate.get("tsc_time") == null ? "" : dataUpdate.get("tsc_time").toString());
                            pendingReason = (dataUpdate.get("pending_reason") == null) ? "" : dataUpdate.get("pending_reason").toString();
                            pendingTimeOut = (dataUpdate.get("pending_timeout") == null) ? "" : dataUpdate.get("pending_timeout").toString();
                            woNumber = (dataUpdate.get("wo_number") == null ? "" : dataUpdate.get("wo_number").toString());
                            scc_code = (dataUpdate.get("scc_code") == null ? "" : dataUpdate.get("scc_code").toString());
                            incidentDomain = (dataUpdate.get("incident_domain") == null ? "" : dataUpdate.get("incident_domain").toString());
                            flagLevel = (dataUpdate.get("flag_level") == null ? "" : dataUpdate.get("flag_level").toString());
                            if (dataUpdate.isEmpty()) {
                                dataUpdateStatus = false;
                            }
                        }
                        if (dataUpdate == null) {
                            dataUpdate = new org.json.simple.JSONObject();
                        }

                        dataTicket = new Ticket();
                        if ("update_data".equalsIgnoreCase(action)) {
                            dataTicket = dao.getProcessIdTicket(ticketNumber);
                        } else {
                            dataTicket = dao.getProcessIdTicketWithShkV3(ticketNumber);
                        }

                        String oldActionStatus = dataTicket.getActionStatus();
                        String id = dataTicket.getId();
                        String idTicketInc = dataTicket.getIdTicketInc();
                        String solution = "";
                        String ownerGroup = "";
                        String oldOwner = dataTicket.getOwner();
                        String ticketStatusCurrent = dataTicket.getTicketStatus() == null ? "" : dataTicket.getTicketStatus();
                        String actSolDb = dataTicket.getActualSolution() == null ? "" : dataTicket.getActualSolution();
                        String incDomainDb = dataTicket.getActualSolution() == null ? "" : dataTicket.getActualSolution();
                        String activityName = dataTicket.getActiviytName() == null ? "" : dataTicket.getActiviytName();
                        String processId = dataTicket.getProcessId() == null ? "" : dataTicket.getProcessId();
                        String runningTime = dataTicket.getRunningTime() == null ? "0" : dataTicket.getRunningTime();
                        double dRunningTime = Double.parseDouble(runningTime);
                        String resourceId = dataTicket.getRecordId() == null ? "" : dataTicket.getRecordId();
                        String activityId = dataTicket.getActivityId() == null ? "" : dataTicket.getActivityId();

                        if (!"update_data".equalsIgnoreCase(action) && !"CLOSED".equalsIgnoreCase(ticketStatusCurrent) && !"".equals(ticketStatusCurrent) && completeStatus) {
                            String state = (dataTicket.getState() == null ? "" : dataTicket.getState());
                            if (("1000001".equals(state) && !"closed".equalsIgnoreCase(activityName) && dRunningTime > 2) ||
                                    "".equals(processId)) {
                                workflowManager.processAbort(dataTicket.getProcessId());
                                dao.UpdateStatus(dataTicket.getStatus(), dataTicket.getTicketStatus(), "EDOTENSEI", dataTicket.getId());
                                WorkflowProcessResult startProcess = workflowManager.processStartWithLinking("ticketIncidentService:latest:flowIncidentTicket", null, "Correction", dataTicket.getId());
                                dao.getNextActivity(dataTicket, id);
                                actionConsition = "Kondisi 1";
                            }
                            state = (dataTicket.getState() == null ? "" : dataTicket.getState());
                            TimeUnit.SECONDS.sleep(2);
                            if (state.equals("1000003")) {

                                if (!"new".equalsIgnoreCase(activityName) &&
                                        !"analysis".equalsIgnoreCase(activityName) &&
                                        !"backend".equalsIgnoreCase(activityName) &&
                                        !"draft".equalsIgnoreCase(activityName) &&
                                        !"pending".equalsIgnoreCase(activityName) &&
                                        !"approvalPending".equalsIgnoreCase(activityName) &&
                                        !"resolved".equalsIgnoreCase(activityName) &&
                                        !"finalcheck".equalsIgnoreCase(activityName) &&
                                        !"mediacare".equalsIgnoreCase(activityName) &&
                                        !"approvalPending".equalsIgnoreCase(activityName) &&
                                        !"pending".equalsIgnoreCase(activityName) &&
                                        !"salamsim".equalsIgnoreCase(activityName)) {
                                    workflowManager.processAbort(dataTicket.getProcessId());
                                    dao.UpdateStatus(dataTicket.getStatus(), dataTicket.getTicketStatus(), "EDOTENSEI", dataTicket.getId());
                                    WorkflowProcessResult startProcess = workflowManager.processStartWithLinking("ticketIncidentService:latest:flowIncidentTicket", null, "Correction", dataTicket.getId());
                                    dao.getNextActivity(dataTicket, id);
                                    actionConsition = "Kondsisi 2";
                                } else if ("".equals(resourceId) &&
                                        ("new".equalsIgnoreCase(activityName) ||
                                        "analysis".equalsIgnoreCase(activityName) ||
                                        "backend".equalsIgnoreCase(activityName) ||
                                        "draft".equalsIgnoreCase(activityName) ||
                                        "pending".equalsIgnoreCase(activityName) ||
                                        "approvalPending".equalsIgnoreCase(activityName) ||
                                        "resolved".equalsIgnoreCase(activityName) ||
                                        "finalcheck".equalsIgnoreCase(activityName) ||
                                        "mediacare".equalsIgnoreCase(activityName) ||
                                        "salamsim".equalsIgnoreCase(activityName)) &&
                                        !"1000007".equals(state)) {
                                    workflowManager.reevaluateAssignmentsForActivity(activityId);
                                    actionConsition = "kondisi 3";
                                }

                                runningTicketStatus = true;
                                String activityid = dataTicket.getActiviytName();
                                String processDefId = dataTicket.getProcessDefId().replace("#", ":");
                                String idActivity = dataTicket.getActivityId();
                                String ticketStatus = "";
                                String lastState = "";
                                ListSymptom symptom = new ListSymptom();

                                // ACTIVITY BACKEND
                                if ("backend".equalsIgnoreCase(activityid)) {
                                    if (!"REDISPATCH_BY_SELECT_SOLUTION".equalsIgnoreCase(action.trim()) &&
                                            !"REASSIGN_OWNERGROUP".equalsIgnoreCase(action.trim()) &&
                                            !"REQUEST_PENDING".equalsIgnoreCase(action.trim()) &&
                                            !"SQMTOCLOSED".equalsIgnoreCase(action.trim()) &&
                                            !"CLOSEDMASSAL".equalsIgnoreCase(action.trim()) &&
                                            !"RESOLVE".equalsIgnoreCase(action.trim())) {
                                        completeStatus = false;
                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "Invalid Action", ticketNumber, bodyParam, 500);
                                        break outerLoop;
                                    } else {
                                        lastState = "BACKEND";
                                        if ("REDISPATCH_BY_SELECT_SOLUTION".equalsIgnoreCase(action.trim())) {
                                            solution = (dataUpdate.get("solution_code") == null) ? "" : dataUpdate.get("solution_code").toString();
                                            if ("".equals(solution)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "solution_code is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else {

                                                HashMap<String, String> paramsTkMapping = new HashMap<String, String>();
                                                paramsTkMapping.put("classification_id", solution);
                                                paramsTkMapping.put("workzone", dataTicket.getWorkzone());
                                                paramsTkMapping.put("customer_segment", dataTicket.getCust_segment());
                                                ownerGroup = dao.getOwnerGroup(paramsTkMapping);
                                                dataUpdate.put("owner_group", ownerGroup);
                                            }
                                        } else if ("REASSIGN_OWNERGROUP".equalsIgnoreCase(action.trim())) {
                                            if (!"".equals(dataUpdate.get("owner_group"))) {
                                                ownerGroup = dataUpdate.get("owner_group").toString();
                                            } else {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "owner_group is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            }
                                        } else if ("REQUEST_PENDING".equalsIgnoreCase(action.trim())) {
                                            if ("".equals(pendingReason)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else if ("".equals(pendingTimeOut)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_timeout is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else if ("".equals(pendingReason) && "".equals(pendingTimeOut)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason and pending_timeout is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else {
                                                dataUpdate.put("memo", pendingReason);
                                            }

                                        } else if ("RESOLVE".equalsIgnoreCase(action.trim())) {

                                            if (!"GAMAS".equalsIgnoreCase(dataTicket.getSource_ticket())) {
                                                if ("".equals(actSolDb)) {
                                                    if ("".equals(actualSolution)) {
                                                        completeStatus = false;
                                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam, 500);
                                                        break outerLoop;
                                                    }
                                                }

                                            } else {
                                                if ("".equals(actualSolution)) {
                                                    completeStatus = false;
                                                    rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam, 500);
                                                    break outerLoop;
                                                } else {
                                                    if ("DCS".equalsIgnoreCase(dataTicket.getCust_segment()) || "PL-TSEL".equalsIgnoreCase(dataTicket.getCust_segment())) {
                                                        if ("TECHNICAL".equalsIgnoreCase(dataTicket.getClassificationFlag()) &&
                                                                ("INTERNET".equalsIgnoreCase(dataTicket.getServiceType()) ||
                                                                "VOICE".equalsIgnoreCase(dataTicket.getServiceType()) ||
                                                                "IPTV".equalsIgnoreCase(dataTicket.getServiceType()))) {

                                                            ukurIboosterStatus = true;
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }

                                } else if ("analysis".equalsIgnoreCase(activityid)) { //ACTIVITY ANALYSIS
                                    if (!"REDISPATCH_BY_SELECT_SOLUTION".equalsIgnoreCase(action.trim()) &&
                                            !"REASSIGN_OWNERGROUP".equalsIgnoreCase(action.trim()) &&
                                            !"REQUEST_PENDING".equalsIgnoreCase(action.trim()) &&
                                            !"CLOSEDMASSAL".equalsIgnoreCase(action.trim()) &&
                                            !"RESOLVE".equalsIgnoreCase(action.trim())) {
                                        completeStatus = false;
                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "Invalid Action", ticketNumber, bodyParam, 500);
                                        break outerLoop;
                                    } else {
                                        lastState = "ANALYSIS";
                                        if ("REDISPATCH_BY_SELECT_SOLUTION".equalsIgnoreCase(action.trim())) {
                                            solution = (dataUpdate.get("solution_code") == null) ? "" : dataUpdate.get("solution_code").toString();
                                            if ("".equals(solution)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "solution_code is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else {

                                                HashMap<String, String> paramsTkMapping = new HashMap<String, String>();
                                                paramsTkMapping.put("classification_id", solution);
                                                paramsTkMapping.put("workzone", dataTicket.getWorkzone());
                                                paramsTkMapping.put("customer_segment", dataTicket.getCust_segment());
                                                ownerGroup = dao.getOwnerGroup(paramsTkMapping);
                                                dataUpdate.put("owner_group", ownerGroup);
                                            }
                                        } else if ("REASSIGN_OWNERGROUP".equalsIgnoreCase(action.trim())) {

                                            if (!"".equals(dataUpdate.get("owner_group") == null ? "" : dataUpdate.get("owner_group"))) {
                                                dataUpdate.put("owner_group", dataUpdate.get("owner_group"));
                                            } else {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "owner_group is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            }
                                        } else if ("REQUEST_PENDING".equalsIgnoreCase(action.trim())) {
                                            if ("".equals(pendingReason)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else if ("".equals(pendingTimeOut)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_timeout is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else if ("".equals(pendingReason) && "".equals(pendingTimeOut)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason and pending_timeout is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else {
                                                dataUpdate.put("memo", pendingReason);
                                            }

                                            //Update REQUEST_PENDING Completed
                                        } else if ("RESOLVE".equalsIgnoreCase(action.trim())) {
                                            if ("".equals(actualSolution)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else {
                                                if (!"GAMAS".equalsIgnoreCase(dataTicket.getSource_ticket())) {
                                                    if (!"".equals(scc_code)) {
                                                        String sccCodeValidation = "";

                                                        if (scc_code.equals(dataTicket.getCodeValidation())) {
                                                            sccCodeValidation = "VALID";
                                                        } else {
                                                            sccCodeValidation = "NOT VALID";
                                                        }
                                                        dataUpdate.put("scc_code_validation", "sccCodeValidation");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } // ACTIVITY SALAMSIM
                                else if ("salamsim".equalsIgnoreCase(activityid)) {
                                    if (!"REOPEN".equalsIgnoreCase(action.trim()) &&
                                            !"CLOSED".equalsIgnoreCase(action.trim()) &&
                                            !"CLOSEDMASSAL".equalsIgnoreCase(action.trim()) &&
                                            !"REQUEST_PENDING".equalsIgnoreCase(action.trim())) {
                                        completeStatus = false;
                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "Invalid Action", ticketNumber, bodyParam, 500);
                                        break outerLoop;
                                    } else {
                                        if ("REOPEN".equalsIgnoreCase(action.trim())) {
                                            if (!"".equals(objDataUpdate)) {
                                                if (!"".equals(dataUpdate.get("booking_id") == null ? "" : dataUpdate.get("booking_id")) &&
                                                        ("DCS".equalsIgnoreCase(dataTicket.getCust_segment()) || "PL-TSEL".equalsIgnoreCase(dataTicket.getCust_segment())) &&
                                                        "40".equalsIgnoreCase(dataTicket.getChannel()) &&
                                                        "CUSTOMER".equalsIgnoreCase(dataTicket.getSource_ticket()) &&
                                                        "FISIK".equalsIgnoreCase(dataTicket.getClassificationType())) {

                                                    createWoStatus = true;
                                                }
                                            }
                                        } else if ("CLOSED".equalsIgnoreCase(action.trim())) {
                                            action = "RESOLVE";
                                        } else if ("REQUEST_PENDING".equalsIgnoreCase(action.trim())) {
                                            if ("".equals(pendingReason)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else if ("".equals(pendingTimeOut)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_timeout is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else if ("".equals(pendingReason) && "".equals(pendingTimeOut)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason and pending_timeout is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else {
                                                dataUpdate.put("memo", pendingReason);
                                            }
                                        }
                                    }
                                    // ACTIVITY MEDIACARE
                                } else if ("mediacare".equalsIgnoreCase(activityid)) {
                                    if (!"REOPEN".equalsIgnoreCase(action.trim()) &&
                                            !"CLOSED".equalsIgnoreCase(action.trim()) &&
                                            !"DEADLINETOSALAMSIM".equalsIgnoreCase(action.trim()) &&
                                            !"CLOSEDMASSAL".equalsIgnoreCase(action.trim()) &&
                                            !"REQUEST_PENDING".equalsIgnoreCase(action.trim())) {
                                        completeStatus = false;
                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "Invalid Action", ticketNumber, bodyParam, 500);
                                        break outerLoop;
                                    } else {
                                        if ("REOPEN".equalsIgnoreCase(action.trim())) {
                                            if (!"".equals(objDataUpdate)) {
                                                if (!"".equals(dataUpdate.get("booking_id") == null ? "" : dataUpdate.get("booking_id")) &&
                                                        ("DCS".equalsIgnoreCase(dataTicket.getCust_segment()) || "PL-TSEL".equalsIgnoreCase(dataTicket.getCust_segment())) &&
                                                        "40".equalsIgnoreCase(dataTicket.getChannel()) &&
                                                        "CUSTOMER".equalsIgnoreCase(dataTicket.getSource_ticket()) &&
                                                        "FISIK".equalsIgnoreCase(dataTicket.getClassificationType())) {
                                                    createWoStatus = true;
                                                }
                                            }
                                        } else if ("CLOSED".equalsIgnoreCase(action.trim())) {
                                            action = "RESOLVE";
                                        } else if ("REQUEST_PENDING".equalsIgnoreCase(action.trim())) {
                                            if ("".equals(pendingReason)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else if ("".equals(pendingTimeOut)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_timeout is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else if ("".equals(pendingReason) && "".equals(pendingTimeOut)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "pending_reason and pending_timeout is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else {
                                                dataUpdate.put("memo", pendingReason);
                                            }
                                        }
                                    }
                                    // ACTIVITY APPROVAL PENDING
                                } else if ("approvalPending".equalsIgnoreCase(activityid)) {
                                    if (!"APPROVED".equalsIgnoreCase(action.trim()) &&
                                            !"CLOSEDMASSAL".equalsIgnoreCase(action.trim()) &&
                                            !"REJECT".equalsIgnoreCase(action.trim())) {
                                        completeStatus = false;
                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "Invalid Action", ticketNumber, bodyParam, 500);
                                        break outerLoop;
                                    } else {
                                        if ("APPROVED".equalsIgnoreCase(action.trim())) {
                                            action = "REQUEST_PENDING";
                                            pendingStatus = "APPROVED";
                                            dataUpdate.put("pending_status", pendingStatus);
                                        } else if ("REJECT".equalsIgnoreCase(action.trim())) {
                                            action = "AFTER_REQUEST";
                                            pendingStatus = "";
                                            dataUpdate.put("pending_status", pendingStatus);
                                        }
                                    }
                                    // ACTIVITY PENDING TO
                                } else if ("pending".equalsIgnoreCase(activityid)) {
                                    if (!"REJECTED".equalsIgnoreCase(action.trim()) &&
                                            !"CLOSEDMASSAL".equalsIgnoreCase(action.trim())) {
                                        completeStatus = false;
                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "Invalid Action", ticketNumber, bodyParam, 500);
                                        break outerLoop;
                                    } else {
                                        if ("REJECTED".equalsIgnoreCase(action.trim())) {
                                            pendingStatus = "REJECTED";
                                            dataUpdate.put("pending_status", pendingStatus);
                                            action = "AFTER PENDING";
                                        }
                                    }
                                } // ACTIVITY DRAFT
                                else if ("draft".equalsIgnoreCase(activityid)) {
                                    if (!"RESOLVE".equalsIgnoreCase(action.trim()) &&
                                            !"CLOSEDMASSAL".equalsIgnoreCase(action.trim()) &&
                                            !"SEND_TO_TIER2".equalsIgnoreCase(action.trim())) {
                                        completeStatus = false;
                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "Invalid Action", ticketNumber, bodyParam, 500);
                                        break outerLoop;
                                    } else {
                                        if ("RESOLVE".equalsIgnoreCase(action.trim())) {
                                            if ("".equals(actualSolution)) {
                                                completeStatus = false;
                                                rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "actual_solution is required", ticketNumber, bodyParam, 500);
                                                break outerLoop;
                                            } else {
                                                if (!"".equals(scc_code)) {
                                                    String sccCodeValidation = "";
                                                    if (scc_code.equals(dataTicket.getCodeValidation())) {
                                                        sccCodeValidation = "VALID";
                                                    } else {
                                                        sccCodeValidation = "NOT VALID";
                                                    }
                                                    dataUpdate.put("scc_code_validation", "sccCodeValidation");
                                                }
                                            }
                                        } else if ("SEND_TO_TIER2".equalsIgnoreCase(action.trim())) {
                                            action = "SENDTOANALYSIS";
                                        }
                                    }
                                } // ACTIVITY RESOLVED
                                else if ("resolved".equalsIgnoreCase(activityid)) {
                                    if (!"REOPEN".equalsIgnoreCase(action.trim()) &&
                                            !"CLOSEDMASSAL".equalsIgnoreCase(action.trim()) &&
                                            !"CLOSE".equalsIgnoreCase(action.trim())) {
                                        completeStatus = false;
                                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "Invalid Action", ticketNumber, bodyParam, 500);
                                        break outerLoop;
                                    } else {
                                        if ("CLOSE".equalsIgnoreCase(action.trim())) {
                                            action = "RESOLVE";
                                        }
                                    }
                                }
                                if (completeStatus) {

                                    lastTimestamp = utDao.getTicketStatusByTicketID(idTicketInc);
//                                duration = ttr.getDuration(lastTimestamp);
                                    if ("approvalPending".equalsIgnoreCase(activityid)) {
                                        r.setStatus("REQUEST_PENDING_" + dataTicket.getStatus());
                                    } else if ("pending".equalsIgnoreCase(activityid)) {
                                        r.setStatus("PENDING");
                                    } else {
                                        r.setStatus(dataTicket.getStatus());
                                    }
                                    r.setTicketId(ticketNumber);
                                    r.setStatusCurrent(dataTicket.getStatus());
                                    r.setChangeBy("INTEGRATION");
                                    r.setOwner("INTEGRATION");
                                    r.setAssignedOwnerGroup(dataTicket.getOwnerGroup());
                                    utoDoa.UpdateTimeonLastTicketSta(r);
                                    boolean insertTicketStatus = utDao.insertNewTicketStatus(r);

                                    dataUpdate.put("action_status", action.trim());
                                    dataUpdate.put("run_process", "1");
                                    dataUpdate.put("owner", "INTEGRATION");

                                    if (!dataUpdate.isEmpty()) {
                                        dao.updateTicketParamGlobal(dataUpdate, dataTicket, id);
                                    }
                                    boolean completeProcess;

                                    try {
                                        TimeUnit.SECONDS.sleep(1);
                                        workflowManager.assignmentForceComplete(processDefId, dataTicket.getProcessId(), idActivity, "000000");
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

                                    if (completeProcess) {

                                        if (ukurIboosterStatus) {
                                            String nd = dataTicket.getService_no();
                                            String realm = "telkom.net";
                                            ListIbooster ibooster = new ListIbooster();
                                            ibooster = dao.getIbooster(nd, realm);
                                            dao.updateIbooster(ibooster, id);
                                            dao.insertWorkLogs(dataTicket.getIdTicketInc(), ticketNumber, dataTicket.getOwnerGroup(), ibooster.getMeasurementCategory());
                                        }

                                        if (createWoStatus) {
                                            final String _ticketNumber = ticketNumber;
                                            final String bookingId = dataUpdate.get("booking_id").toString();
                                            final String _ticketStatus = ticketStatus;
                                            final Ticket _dataTicket = dataTicket;
                                            Thread TREADWO = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        apiConfUrlOnly = paramDao.getUrl("createWorkOrder");
                                                        RequestToken requestToken = new RequestToken();
                                                        String url = apiConfUrlOnly.getUrl();
                                                        String api_key = apiConfUrlOnly.getApiKey();
                                                        String api_id = apiConfUrlOnly.getApiId();
                                                        RESTAPI _RESTAPI = new RESTAPI();
//                                                    LogHistoryDao lhdaoS = new LogHistoryDao();

                                                        JSONObject mainObjWo = new JSONObject();
                                                        mainObjWo.put("serviceType", _dataTicket.getServiceType());
                                                        mainObjWo.put("serviceNum", _dataTicket.getService_no());
                                                        mainObjWo.put("scheduleType", "ASSURANCE");
                                                        mainObjWo.put("externalId", _ticketNumber);
                                                        mainObjWo.put("serviceAddress", _dataTicket.getServiceAddress());
                                                        mainObjWo.put("rk", "");
                                                        mainObjWo.put("bookingId", bookingId);
                                                        mainObjWo.put("customerName", _dataTicket.getCustomerName());
                                                        mainObjWo.put("externalSystem", "Incident");
                                                        mainObjWo.put("status", _ticketStatus);
                                                        mainObjWo.put("sto", _dataTicket.getWorkzone());
                                                        JSONArray arrrDetails = new JSONArray();
                                                        JSONObject objDetails;
                                                        objDetails = new JSONObject();
                                                        objDetails.put("attributeValue", "");
                                                        objDetails.put("attributeName", "scc_result");
                                                        arrrDetails.put(objDetails);
                                                        objDetails = new JSONObject();
                                                        objDetails.put("attributeValue", _dataTicket.getCust_segment());
                                                        objDetails.put("attributeName", "customer_segment");
                                                        arrrDetails.put(objDetails);
                                                        objDetails = new JSONObject();
                                                        objDetails.put("attributeValue", "");
                                                        objDetails.put("attributeName", "scc_time");
                                                        arrrDetails.put(objDetails);
                                                        mainObjWo.put("details", arrrDetails);

                                                        RequestBody body = RequestBody.create(_RESTAPI.JSON, mainObjWo.toString());

                                                        Request requestWo = new Request.Builder()
                                                                .url(url)
                                                                .addHeader("api_key", api_key)
                                                                .addHeader("api_id", api_id)
                                                                .post(body)
                                                                .build();

                                                        String responseWo = _RESTAPI.CALLAPI(requestWo);

                                                        LogHistoryDao lh = new LogHistoryDao();

                                                        org.json.JSONObject reqObj = new org.json.JSONObject(mainObjWo);
                                                        org.json.JSONObject resObj = new org.json.JSONObject(responseWo);
                                                        lh.SENDHISTORY(
                                                                _ticketNumber,
                                                                "createWoReopen_via_api",
                                                                url,
                                                                "POST",
                                                                reqObj,
                                                                resObj,
                                                                0);
                                                        resObj = null;
                                                        reqObj = null;
                                                    } catch (Exception ex) {
                                                        LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
                                                    } finally {
                                                        apiConfUrlOnly = null;
                                                    }
                                                }
                                            });
                                            TREADWO.setDaemon(false);
                                            TREADWO.start();

                                        }

                                        JSONObject jObj;
                                        jObj = new JSONObject();
                                        mainObj.put("code", 200);
                                        mainObj.put("message", "update status successfull");
                                        mainObj.put("nextActivityId", "-");
                                        insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
                                        mainObj.write(hsr1.getWriter());

                                    } else {
                                        dao.updateStatusTmp(oldActionStatus, "0", oldOwner, id);
                                    }
                                }
//                                break;
                            }

                            if (!runningTicketStatus) {
                                String message = "";
                                if (dRunningTime < 1) {
                                    message = "The ticket is being processed, please try again in a minute.";
                                } else {
                                    message = "This ticket process has been closed, please check the ticket process in the monitoring process";
                                }
                                JSONObject jObj;
                                jObj = new JSONObject();
                                mainObj.put("date", date);
                                mainObj.put("code", 404);
                                mainObj.put("message", message);
                                headMainObj.put("error", mainObj);
                                insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
                                headMainObj.write(hsr1.getWriter());
                            }
                        } else if ("update_data".equalsIgnoreCase(action) && !"CLOSED".equalsIgnoreCase(ticketStatusCurrent) && completeStatus) {
                            if (dao.updateTicketParamGlobal(dataUpdate, dataTicket, id)) {
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
                        } else if (!"update_data".equalsIgnoreCase(action) && !runningTicketStatus) {
                            JSONObject jObj;
                            jObj = new JSONObject();
                            mainObj.put("date", date);
                            mainObj.put("code", 404);
                            mainObj.put("message", "Ticket not found");
                            headMainObj.put("error", mainObj);
                            insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
                            headMainObj.write(hsr1.getWriter());
                        } else if ("CLOSED".equalsIgnoreCase(ticketStatusCurrent)) {

                            JSONObject jObj;
                            jObj = new JSONObject();
                            mainObj.put("date", date);
                            mainObj.put("code", 404);
                            mainObj.put("message", "Ticket is closed");
                            headMainObj.put("error", mainObj);
                            insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
                            headMainObj.write(hsr1.getWriter());
                        }
                    } else {
                        rspFieldMandatoryFalse(mainObj, headMainObj, date, hsr1, "action is required", ticketNumber, bodyParam, 500);
                        break outerLoop;
                    }

                } catch (ParseException ex) {
                    LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
                } catch (Exception ex) {
                    LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
                } finally {
                    if (!"".equals(actionConsition)) {
                        try {
                            LogHistoryDao lhDao = new LogHistoryDao();
//                        LogUtil.info(this.getClass().getName(), "actionConsition : " + actionConsition);
                            JSONObject param = new JSONObject(dataTicket);

                            LogHistory lh = new LogHistory();
                            lh.setUrl("CompleteActivityTicketIncidentApiV4");
                            lh.setAction("korektif");
                            lh.setMethod(actionConsition);
                            lh.setRequest(param.toString());
                            lh.setResponse("");
                            lh.setTicketId(ticketNumber);

                            lhDao.insertToLog(lh);
                        } catch (Exception ex) {
                            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
                        }

                    }

                }
            } else {
                try {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("code", rspStatus);
                    mainObj.put("message", rspMessage);
                    mainObj.write(hsr1.getWriter());
                } catch (JSONException ex) {
                    LogUtil.error(this.getClassName(), ex, "Error : " + ex.getMessage());
                }
            }

        }

    }

    private void rspFieldMandatoryFalse(JSONObject mainObj, JSONObject headMainObj, String date, HttpServletResponse hsr1,
            String message, String ticketNumber, String bodyParam, int responseCode) throws JSONException {
        try {
            JSONObject jObj;
            jObj = new JSONObject();
            mainObj.put("date", date);
            mainObj.put("code", responseCode);
            mainObj.put("message", message);
            headMainObj.put("error", mainObj);
            insertLosgHystoryAPI(bodyParam, ticketNumber, mainObj.toString());
            headMainObj.write(hsr1.getWriter());
        } catch (IOException ex) {
            LogUtil.error(this.getClassName(), ex, "Error Response : " + ex.getMessage());
        }
    }

    private void insertLosgHystoryAPI(String bodyParam, String ticketNumber, String response) {
        try {
            LogHistoryDao lh = new LogHistoryDao();
            String url = "https://oss-incident.telkom.co.id/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.CompleteActivityTicketIncidentApiV4/service";

            org.json.JSONObject reqObj = new org.json.JSONObject(bodyParam);
            org.json.JSONObject resObj = new org.json.JSONObject(response);
            lh.SENDHISTORY(
                    ticketNumber,
                    "UPDATE_STATUS_API",
                    url,
                    "POST",
                    reqObj,
                    resObj,
                    0);
            resObj = null;
            reqObj = null;
        } catch (Exception ex) {
            LogUtil.error(getClassName(), ex, "ex err:" + ex.getMessage());
        }
    }

}
