/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketWorkLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.net.InetAddress;
import org.joget.commons.util.SysInfo;



/**
 *
 * @author asani
 */
public class APISendWhatsapp extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Call API Non Joget";
    
    LogInfo info = new LogInfo();

    @Override
    public String renderTemplate(FormData fd, Map map) {
        return "";
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

    MasterParam masterParam;
    MasterParamDao masterParamDao;
    TicketStatus ticketStatus;
    LoadTicketDao ticketStatusDao;
    RESTAPI _RESTAPI;
    LogHistory logHistory;
    LogHistoryDao logHistoryDao;
    ArrayManipulation arrayManipulation;
    InsertTicketWorkLogsDao insertTicketWorkLogsDao;

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        masterParam = new MasterParam();
        masterParamDao = new MasterParamDao();
        _RESTAPI = new RESTAPI();
        logHistory = new LogHistory();
        logHistoryDao = new LogHistoryDao();
        ticketStatusDao = new LoadTicketDao();
        ticketStatus = new TicketStatus();
        arrayManipulation = new ArrayManipulation();
        insertTicketWorkLogsDao = new InsertTicketWorkLogsDao();

        try {
            masterParam = masterParamDao.getUrl("ticket_incident_api");
            WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
            String userLogin = workflowUserManager.getCurrentUsername();

            String result = "";

            Map tempSystemInfo = SysInfo.getSystemProperties();
            result = InetAddress.getLocalHost().getHostName();


            String apiIdDefined = masterParam.getApi_id();
            String apiKeyDefined = masterParam.getApi_key();
            String apiSecret = masterParam.getApi_secret();

            String headerApiId = req.getHeader("api_id");
            String headerApiKey = req.getHeader("api_key");

            info.Log("LOG DATA", "URL HIT :" + req.getRemoteHost());

            if (apiIdDefined.equals(headerApiId) && apiKeyDefined.equals(headerApiKey)) {

                boolean isPost = "POST".equals(req.getMethod());

                if (isPost) {

                    StringBuffer jb = new StringBuffer();
                    String line = null;
                    JSONObject jsonRes = new JSONObject();

                    BufferedReader reader = req.getReader();
                    while ((line = reader.readLine()) != null) {
                        jb.append(line);
                    }
                    reader.close();

                    String bodyParam = jb.toString();
                    Object objBodyParam = new JSONTokener(bodyParam).nextValue();
                    JSONObject jsonBodyParam = (JSONObject) objBodyParam;

                    if (jsonBodyParam.has("ticket_id") && jsonBodyParam.has("technician") && jsonBodyParam.has("booking_date")) {

                        String PARAM_TICKET_ID = jsonBodyParam.getString("ticket_id");
                        String PARAM_TECHNICIAN = jsonBodyParam.getString("technician");
                        String PARAM_BOOKING_DATE = jsonBodyParam.getString("booking_date");

                        Timestamp tm = Timestamp.valueOf(PARAM_BOOKING_DATE);

                        String DATE_DATA5 = tm.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MMM-YYYY")).toString();
                        String Hour = tm.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));

                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(tm.getTime());
                        cal.add(Calendar.HOUR, 2);

                        Timestamp AddingHour = new Timestamp(cal.getTime().getTime());
                        String timeSpent = AddingHour.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")).toString();

                        String TIME_DATA6 = Hour + " - " + timeSpent;

                        // GET DATA 
                        
                        ticketStatus = ticketStatusDao.LoadTicketByIdTicket(PARAM_TICKET_ID);
                        
                        String ticketId = (ticketStatus.getTicketId() == null) ? "" :
                            ticketStatus.getTicketId();
                        String custSegment = (ticketStatus.getCustomerSegment() == null) ? 
                          "" : ticketStatus.getCustomerSegment().toUpperCase().trim();
                        String phone = (ticketStatus.getPhone() == null) ? "" : ticketStatus.getPhone().trim();
                        String bookingId = (ticketStatus.getBookingId() == null) ? "" : ticketStatus.getBookingId();
                        String classficationFlag = (ticketStatus.getClassificationFlag() == null) ? 
                            "" : ticketStatus.getClassificationFlag();
                        String sourceTicket = (ticketStatus.getSourceTicket() == null) ? 
                            "" : ticketStatus.getSourceTicket().toUpperCase().trim();
                        String classificationType = (ticketStatus.getClassification_type() == null) ? 
                            "" : ticketStatus.getClassification_type().toUpperCase().trim();
                        String serviceType = (ticketStatus.getServiceType() == null) ? 
                            "" : ticketStatus.getServiceType().toUpperCase().trim();
                        String serviceNum = (ticketStatus.getServiceNo() == null) ? 
                            "" : ticketStatus.getServiceNo().toUpperCase().trim();
                        String password = (ticketStatus.getLandingPage() == null) ? 
                            "" : ticketStatus.getLandingPage().trim(); // landingpage
                        String channel = (ticketStatus.getChannel() == null) ? 
                            "" : ticketStatus.getChannel().toUpperCase().trim(); // landingpage
                        String symptodDesc = (ticketStatus.getSymptomDesc() == null) ? 
                            "" : ticketStatus.getSymptomDesc().toUpperCase().trim(); // landingpage
                        String landingPage = (ticketStatus.getLandingPage() == null) ? 
                            "" : ticketStatus.getLandingPage().trim(); // landingpage

                        info.Log("LOG DATA", "TICKET ID:" +ticketId);
                        info.Log("LOG DATA", "CUSTOMER SEGMENT :" +custSegment);
                        
                        logHistory.setCreatedBy("SYSTEM");
                        logHistory.setMethod("POST");
                        logHistory.setTicketId(ticketId);
                        
                        if("PL-".equalsIgnoreCase(custSegment)) {
                          custSegment = "PL-TSEL";
                        }
                        
                        String custSegmentHSIList[] = {"DES", "DGS", "DBS", "DSS", "DPS", "REG", "RBS"};
                        
                        
                        
                        // CHECKING TICKET ID
                        if (!ticketId.isEmpty() 
                            && ("DCS".equalsIgnoreCase(custSegment) 
                               || "PL-TSEL".equalsIgnoreCase(custSegment))
                                && !channel.equalsIgnoreCase("40")) {
                            logHistory.setAction("notificationWAFromScheduling(RETAIL)");
                            // BUILD FORMAT SEND WA KHUSU UNTUK FISIK
                            Map m = new LinkedHashMap(5);
                            Map templateDataJson = new LinkedHashMap(10);
                            JSONObject root = new JSONObject();
                            JSONObject jo = new JSONObject();
                            
                            if (phone.length() > 0) {
                              phone.replace("-", "");
                              String[] phoneSplit = phone.split(" ");
                              phone = phoneSplit[0];

                              if ("0".equals(phone.substring(0, 1))) {
                                phone = "62" + phone.substring(1).toString();
                              }

                              if ("+62".equals(phone.substring(0, 3))) {
                                phone = "62" + phone.substring(3).toString();
                              }
                            }

                            m.put("ticketID", ticketId);
                            m.put("phone", phone);
                            m.put("source", "NOSSA");

                            m.put("templateID", "tek_f_manja_3");
                            m.put("smsTest", "Pelanggan Yth, laporan gangguan " + serviceNum + "telah diterima dgn tiket " + ticketStatus.getTicketId() + ". "
                                    + "Info progres klik https://tlkm.id/" + ticketId + ". Mhn maaf atas ketidaknyamanannya");
                            templateDataJson.put("data1", ticketId);
                            templateDataJson.put("data2", serviceType);
                            templateDataJson.put("data3", serviceNum);
                            templateDataJson.put("data4", PARAM_TECHNICIAN); // chief
                            templateDataJson.put("data5", DATE_DATA5); // booking_date 
                            templateDataJson.put("data6", TIME_DATA6); // booking_date += 2jam
                            templateDataJson.put("data7", ticketId);
                            templateDataJson.put("data8", landingPage);
                            templateDataJson.put("data9", "#");
                            templateDataJson.put("data10", "#");
                            m.put("templateData", templateDataJson);
                            jo.put("input", m);
                            root.put("notificationWA", jo);

                            RequestBody body = RequestBody.create(_RESTAPI.JSON, root.toString());
                            String _token = _RESTAPI.getToken();

                            masterParam = masterParamDao.getUrl("notificationWA");
                            logHistory.setUrl(masterParam.getUrl());

                            Request request = new Request.Builder()
                                    .url(masterParam.getUrl())
                                    .addHeader("Accept", "application/json")
                                    .addHeader("Authorization", "Bearer " + _token)
                                    .post(body)
                                    .build();
                            
                            logHistory.setRequest(root.toString());

                            JSONObject stringResponse = _RESTAPI.CALLAPIHANDLER(request);
                            insertTicketWorkLogsDao.InsertTicketWorkLogsByParentTicket(
                                    userLogin,
                                    "notificationWA",
                                    stringResponse.toString(),
                                    ticketId
                            );
                            boolean resBool = stringResponse.getBoolean("status");
                            int resCode = stringResponse.getInt("status_code");
                            logHistory.setResponse(stringResponse.toString());
                            logHistory.setResponseCode(resCode);
                            
                            if(resBool) {
                              String msg = stringResponse.getString("msg");
                              Object object = new JSONTokener(msg).nextValue();
                              JSONObject jsonResponse = (JSONObject) object;
                              object = null;
  //                        stringResponse.
                              jsonResponse.write(res.getWriter());
                              res.setStatus(HttpServletResponse.SC_OK);
                            } else {
                              res.setStatus(resCode);
                            }

                            request = null;
                            jo = null;
                            root = null;
                            m = null;
                            templateDataJson = null;
                        } 
                        else if(arrayManipulation.SearchDataOnArray(
                                custSegmentHSIList, 
                                custSegment)
                               && !bookingId.isEmpty()) {
                            logHistory.setAction("notificationWAFromScheduling(EBIS)");
                            JSONObject json = new JSONObject();
                            JSONObject sendWATenesaRequest = new JSONObject();
                            JSONObject eaiHeader = new JSONObject();
                            JSONObject eaiBody = new JSONObject();
                            Map templateDataJson = new LinkedHashMap(10);
                            
                            if (phone.length() > 0) {
                              phone.replace("-", "");
                              String[] phoneSplit = phone.split(" ");
                              phone = phoneSplit[0];

                              if ("0".equals(phone.substring(0, 1))) {
                                phone = "62" + phone.substring(1).toString();
                              }

                              if ("+62".equals(phone.substring(0, 3))) {
                                phone = "62" + phone.substring(3).toString();
                              }
                            }
                            
                            eaiHeader.put("externalId", "");
                            eaiHeader.put("timestamp","");
                            
                            eaiBody.put("TICKET_ID", ticketId);
                            eaiBody.put("PHONE", phone);
                            eaiBody.put("SOURCE", "NOSSA");
                            eaiBody.put("TEMPLATE_ID", "open_tiket_tenesa_4");
                            JSONObject s1 = new JSONObject();
                            JSONObject s2 = new JSONObject();
                            JSONObject s3 = new JSONObject();
                            JSONObject s4 = new JSONObject();
                            JSONObject s5 = new JSONObject();
                            JSONObject s6 = new JSONObject();
                            JSONObject s7 = new JSONObject();
                            JSONObject s8 = new JSONObject();
                            
                            s1.put("1", ticketId);
                            s2.put("2", serviceType);
                            s3.put("3", serviceNum);
                            s4.put("4", PARAM_TECHNICIAN);
                            s5.put("5", PARAM_BOOKING_DATE);
                            s6.put("6", TIME_DATA6);
                            s7.put("7", ticketId);
                            s8.put("8", landingPage);
                            JSONArray TEMPLATE_DATA = new JSONArray();
                            TEMPLATE_DATA.put(s1);
                            TEMPLATE_DATA.put(s2);
                            TEMPLATE_DATA.put(s3);
                            TEMPLATE_DATA.put(s4);
                            TEMPLATE_DATA.put(s5);
                            TEMPLATE_DATA.put(s6);
                            TEMPLATE_DATA.put(s7);
                            TEMPLATE_DATA.put(s8);
                            
                            eaiBody.put("TEMPLATE_DATA", TEMPLATE_DATA);
                          
                            sendWATenesaRequest.put("eaiHeader", eaiHeader);
                            sendWATenesaRequest.put("eaiBody", eaiBody);
                            json.put("sendWATenesaRequest", sendWATenesaRequest);
                            
                            RequestBody body = RequestBody.create(_RESTAPI.JSON, json.toString());
                            String _token = _RESTAPI.getToken();

                            masterParam = masterParamDao.getUrl("notificationWAEbis");
                            logHistory.setUrl(masterParam.getUrl());

                            Request request = new Request.Builder()
                                    .url(masterParam.getUrl())
                                    .addHeader("Accept", "application/json")
                                    .addHeader("Authorization", "Bearer " + _token)
                                    .post(body)
                                    .build();
                            
                            logHistory.setRequest(json.toString());

                            JSONObject stringResponse = _RESTAPI.CALLAPIHANDLER(request);
                            insertTicketWorkLogsDao.InsertTicketWorkLogsByParentTicket(
                                    userLogin,
                                    "notificationWAFromScheduling(EBIS)",
                                    stringResponse.toString(),
                                    ticketId
                            );
                            boolean resBool = stringResponse.getBoolean("status");
                            int resCode = stringResponse.getInt("status_code");
                            logHistory.setResponse(stringResponse.toString());
                            logHistory.setResponseCode(resCode);
                            
                            if(resBool) {
                              String msg = stringResponse.getString("msg");
                              Object object = new JSONTokener(msg).nextValue();
                              JSONObject jsonResponse = (JSONObject) object;
                              object = null;
  //                        stringResponse.
                              jsonResponse.write(res.getWriter());
                              res.setStatus(HttpServletResponse.SC_OK);
                            } else {
                              res.setStatus(resCode);
                            }

                            request = null;
                            json = null;
                            eaiHeader = null;
                            eaiBody = null;
                            sendWATenesaRequest = null;
                            
                        } else {
                            res.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Ticket ID Not Found.");
                        }

                    } else {
                        res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
                    }
                    
                    jsonBodyParam = null;
                    objBodyParam = null;
                    jb = null;
                    jsonRes = null;
                } else {
                    res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method not allow.");
                }

            } else {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication.");
            }
        } catch (Exception ex) {
//            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server Error");
            info.Log(getClassName(), ex.getMessage());
        } finally {
            try {
                logHistoryDao.insertToLogHistoryWA(logHistory);
            } catch (Exception ex) {
                info.Log(getClassName(), ex.getMessage());
            }

            masterParam = null;
            masterParamDao = null;
            ticketStatus = null;
            ticketStatusDao = null;
            _RESTAPI = null;
            logHistory = null;
            logHistoryDao = null;
        }
    }

}
