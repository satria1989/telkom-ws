/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.*;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author mtaup
 */
public class AddAttributeTicket extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Add Attribute Ticket";

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

    LogInfo info = new LogInfo();

    @Override
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {

        TicketAutomationDaoV4 dao = new TicketAutomationDaoV4();
//        CheckOrigin checkOrigin = new CheckOrigin();
        AddAttributeTicketDao aat = new AddAttributeTicketDao();
        LogHistoryDao logElastic = new LogHistoryDao();

        try {
            String origin = hsr.getHeader("Origin");
//            boolean allowOrigin = checkOrigin.checkingOrigin(origin, hsr1);
            boolean allowOrigin = true;

            if (allowOrigin) {

                String pattern = "EEE dd MMM HH:mm:ss yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(new Date());

//                JSONObject HeadmainObj = new JSONObject();
                JSONObject mainObj = new JSONObject();
//                JSONObject jsonObj;
                dao.getApiAttribut();

                //Predefined Headers
                String apiIdDefined = dao.apiId;
                String apiKeyDefined = dao.apiKey;

                // Headers API from Client
                String headerApiId = hsr.getHeader("api_id");
                String headerApiKey = hsr.getHeader("api_key");

                boolean methodStatus = false;
                boolean authStatus = false;
                if ("POST".equals(hsr.getMethod())) {
                    methodStatus = true;
                }
                if (apiIdDefined.equals(headerApiId) && apiKeyDefined.equals(headerApiKey)) {
                    authStatus = true;
                }

                if (methodStatus && authStatus) {

                    try {

                        ApplicationContext ac = AppUtil.getApplicationContext();
                        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");

                        // Get Body Parameter
                        StringBuffer jb = new StringBuffer();
                        String line = null;
                        BufferedReader reader = hsr.getReader();
                        while ((line = reader.readLine()) != null) {
                            jb.append(line);
                        }

                        String bodyParam = jb.toString();

                        JSONParser parse = new JSONParser();
                        org.json.simple.JSONObject data_obj = (org.json.simple.JSONObject) parse.parse(bodyParam);
                        parse = null;
                        String idTicket = data_obj.get("ticket_id").toString();
                        if (data_obj.get("data") != null) {

                        }
                        JSONArray arrData = (JSONArray) data_obj.get("data");
                        boolean attributeTSA = false;
                        data_obj = null;
                        int arrSize = arrData.size();
                        int i = 0;
                        int callTSA = 0;
                        String attributeName = "";
                        String attributeValue = "";
                        List<String> data = aat.getAttribute(idTicket);
                        for (Object object : arrData) {
                            org.json.simple.JSONObject obj = (org.json.simple.JSONObject) object;
                            attributeName = (obj.get("attribute_name") == null ? "" : obj.get("attribute_name").toString());
                            attributeValue = (obj.get("attribute_value") == null ? "" : obj.get("attribute_value").toString());
                            if (data.contains(attributeName)) {
                                aat.updateValueAttribute(attributeName, attributeValue, idTicket);
                            } else {
                                aat.insertAttributeTicket(idTicket, attributeName, attributeValue);
                            }

                            if ("Network Cat-3".equalsIgnoreCase(attributeName) && "Mitratel".equalsIgnoreCase(attributeValue) && callTSA == 0) {
                                attributeTSA = true;
//                                aat.updateSummary(attributeValue, idTicket);
//                                callTSA(idTicket, attributeTSA);
                                callTSA++;
                            }

//                            if ("tgl ps".equalsIgnoreCase(attributeName) && !"".equals(attributeValue)) {
//                                updateGuaranteStatus(aat, attributeValue, idTicket);
//                            }
                            i++;
                        }

                        mainObj.put("date", date);
                        mainObj.put("code", "200");
                        mainObj.put("message", String.valueOf(i) + " data successfully added");
                        mainObj.write(hsr1.getWriter());

                        JSONObject req = new JSONObject(bodyParam);
                        logElastic.SENDHISTORY(
                                idTicket,
                                "updateAttributeTicket",
                                hsr.getRequestURL().toString(),
                                "POST",
                                req,
                                mainObj,
                                200
                        );

                        arrData = null;
                        bodyParam = null;
                        data = null;

                    } catch (ParseException ex) {
                        info.Error(getClass().getName(), ex.getMessage(), ex);
                    }

                } else {
                    if (!methodStatus) {
                        hsr1.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allowed");
                    } else {
                        hsr1.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
                    }
                }

            } else {
                hsr1.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "");
            }

        } catch (Exception ex) {
            dao = null;
            aat = null;
            info.Error(getClass().getName(), ex.getMessage(), ex);
        }
    }

    private void updateGuaranteStatus(AddAttributeTicketDao aat, String psDate, String idTicket) throws Exception {
        String reportedDate = aat.getReportedDate(idTicket);

        String[] patterns = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "yyyy-MM-dd HH:mm:ss.SSS"
        };

        DateTimeFormatter formatDate = null;
        String foundPatern = "";

        for (String pattern : patterns) {
            try {
                formatDate = DateTimeFormatter.ofPattern(pattern);
                LocalDateTime dateTime = LocalDateTime.parse(reportedDate, formatDate);
                foundPatern = pattern;
                break;
            } catch (DateTimeParseException e) {
                info.Error(getClass().getName(), e.getMessage(), e);
            }
        }

        if (!"yyyy-MM-dd HH:mm:ss".equals(foundPatern)) {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(foundPatern);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime dateTime = LocalDateTime.parse(reportedDate, inputFormatter);

            String newDateString = dateTime.format(outputFormatter);
            reportedDate = newDateString;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime tanggal = LocalDateTime.parse(psDate, formatter);
        LocalDateTime fReportedDate = LocalDateTime.parse(reportedDate, formatter);
        long dayDifference = ChronoUnit.DAYS.between(tanggal, fReportedDate);

        String guaranteStatus = "";

        if (dayDifference > 60) {
            guaranteStatus = "NON GUARANTEE";
        } else {
            guaranteStatus = "GUARANTEE";
        }
        aat.updateGuaranteStatus(guaranteStatus, idTicket);
    }

    private void callTSA(String idTicket, boolean attributeTSA) throws Exception {
        LoadTicketDao loadTicketDao = new LoadTicketDao();
        LogHistoryDao logElastic = new LogHistoryDao();
        TicketStatus ticketStatus = loadTicketDao.LoadTicketByIdTicket(idTicket);

        if ("56".equals(ticketStatus.getChannel()) && attributeTSA) {
            String ticketId = (ticketStatus.getTicketId() == null) ?
                    "" : ticketStatus.getTicketId();
            String witel = (ticketStatus.getWitel() == null) ?
                    "" : ticketStatus.getWitel();
            String reportedDate = (ticketStatus.getReportedDate() == null) ?
                    "" : ticketStatus.getReportedDate();
            String summary = (ticketStatus.getSummary() == null) ?
                    "" : ticketStatus.getSummary();
            String detail = (ticketStatus.getDetails() == null) ?
                    "" : ticketStatus.getDetails();
            String serviceId = (ticketStatus.getServiceId() == null) ?
                    "" : ticketStatus.getServiceId();
            String contactName = (ticketStatus.getContactName() == null) ?
                    "" : ticketStatus.getContactName();

            JSONObject request = new JSONObject();
            JSONObject createTicketRequest = new JSONObject();
            JSONObject eaiHeader = new JSONObject();
            JSONObject eaiBody = new JSONObject();

            eaiHeader.put("externalId", "");
            eaiHeader.put("timestamp", "");
            createTicketRequest.put("eaiHeader", eaiHeader);

            eaiBody.put("ticket_no", ticketId);
            eaiBody.put("location", witel);
            eaiBody.put("segment", "TSA");
            eaiBody.put("ne", detail);
            eaiBody.put("start_time", reportedDate);
            eaiBody.put("headline", summary);
            eaiBody.put("descr", detail);
            eaiBody.put("severity", "");
            eaiBody.put("priority", "");
            eaiBody.put("impact", serviceId);
            eaiBody.put("rfo", "");
            eaiBody.put("pic", contactName);
            eaiBody.put("fe", "");
            createTicketRequest.put("eaiBody", eaiBody);

            request.put("createTicketRequest", createTicketRequest);

            RESTAPI _RESTAPI = new RESTAPI();
            String token = _RESTAPI.getToken();
            MasterParamDao masterParamDao = new MasterParamDao();
            MasterParam mp = masterParamDao.getUrl("createTicketMyTSA");
            String URL = mp.getUrl();

            RequestBody body = RequestBody.create(_RESTAPI.JSON, request.toString());
            Request requestTSA = new Request.Builder()
                    .url(URL)
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            JSONObject resTSA = _RESTAPI.CALLAPIHANDLER(requestTSA);
            int stsCodeScc = resTSA.getInt("status_code");

            logElastic.SENDHISTORY(
                    idTicket,
                    "createTicketMyTSA",
                    URL,
                    "POST",
                    request,
                    resTSA,
                    stsCodeScc
            );
        }
    }
}
