/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.CompleteActivityTicketIncidentApiDao;
import id.co.itasoft.telkom.oss.plugin.dao.GamasTicketRelationDao;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
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
public class GamasTicketRelation extends Element implements PluginWebSupport {

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

    @Override
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {

        try {

            CheckOrigin checkOrigin = new CheckOrigin();
            String origin = hsr.getHeader("Origin");
            boolean allowOrigin = checkOrigin.checkingOrigin(origin, hsr1);
//            LogUtil.info(this.getClass().getName(), "allowOrigin : "+allowOrigin);

            if (allowOrigin) {

                ApplicationContext ac = AppUtil.getApplicationContext();
                WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
                CompleteActivityTicketIncidentApiDao dao = new CompleteActivityTicketIncidentApiDao();
                GamasTicketRelationDao daoGtr = new GamasTicketRelationDao();

                String pattern = "EEE dd MMM HH:mm:ss yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(new Date());

                dao.getApiAttribut();

                String apiIdDefined = dao.apiId;
                String apiKeyDefined = dao.apiKey;
                String apiSecret = dao.apiSecret;

                String headerApiId = hsr.getHeader("api_id");
                String headerApiKey = hsr.getHeader("api_key");

                String rspStatus = "";
                String rspMessage = "";

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
                        //LogUtil.info(this.getClassName(), "BodyParameter : " + jb.toString());

                        String bodyParam = jb.toString();
                        JSONParser parse = new JSONParser();
                        org.json.simple.JSONObject data_obj = (org.json.simple.JSONObject) parse.parse(bodyParam);
                        String gamasTicket = data_obj.get("gamas_ticket").toString();
                        String childTicket = data_obj.get("child_ticket").toString();

                        Ticket dataCt = new Ticket();
                        dataCt = daoGtr.getDataChildTicket(childTicket);

                        Ticket dataPt = new Ticket();
                        dataPt = daoGtr.getDataParentTicket(gamasTicket);

                        boolean ticketStatus = false;
                        boolean clsfTypeStatus = true;
                        boolean sourceStatus = true;
                        boolean checkServiceId = true;
                        boolean checkChildTicketToPrent = true;
                        boolean checkDeviceToTechnicalData = true;

                        //LogUtil.info(this.getClass().getName(), "##ticketStatus## :" + dataCt.getTicketStatus());
                        if ("DRAFT".equalsIgnoreCase(dataCt.getTicketStatus()) ||
                                "ANALYSIS".equalsIgnoreCase(dataCt.getTicketStatus()) ||
                                "PENDING".equalsIgnoreCase(dataCt.getTicketStatus()) ||
                                "BACKEND".equalsIgnoreCase(dataCt.getTicketStatus()) ||
                                "FINALCHECK".equalsIgnoreCase(dataCt.getTicketStatus())) {
                            ticketStatus = true;
                        }

                        if (!dataCt.getClassificationType().equalsIgnoreCase(dataPt.getClassificationType())) {
                            clsfTypeStatus = false;
                        }

                        if (dataCt.getSource_ticket().equalsIgnoreCase(dataPt.getSource_ticket())) {
                            sourceStatus = false;
                        }

                        if (!daoGtr.checkToIpactedService(dataCt.getServiceId(), dataPt.getRecordId())) {
                            checkServiceId = false;
                        }

                        if (!daoGtr.checkToRelatedRecords(childTicket)) {
                            checkChildTicketToPrent = false;
                        }

                        if ("FISIK".equalsIgnoreCase(dataCt.getClassificationType())) {
                            if (ticketStatus && clsfTypeStatus && sourceStatus && checkServiceId && checkChildTicketToPrent && checkDeviceToTechnicalData) {

                                daoGtr.insertToRelatedRecord(dataCt.getIdTicket(), dataPt.getRecordId(), dataPt.getIdTicket(), "API_TICKET_RELATIONS");
                                daoGtr.updateIdGamasToChild(dataPt.getIdTicket(), dataCt.getParentId());
                                daoGtr.insertWorkLogs(dataCt.getId(), dataCt.getIdTicket(), dataCt.getOwnerGroup(), "This Ticket is Related to Gamas","");
                                try {
                                    JSONObject jObj;
                                    jObj = new JSONObject();
                                    mainObj.put("code", "200");
                                    mainObj.put("message", "Success Related Ticket Gamas");
                                    mainObj.write(hsr1.getWriter());
                                } catch (JSONException ex) {
                                    LogUtil.error(this.getClassName(), ex, "Error : " + ex.getMessage());
                                }
                            } else {
                                if (!ticketStatus) {
                                    rspMessage = "ticket status has passed the resolve process";
                                    responseError(hsr1, date, rspMessage);
                                } else if (!clsfTypeStatus) {
                                    rspMessage = "classification type does not match the ticket gamas";
                                    responseError(hsr1, date, rspMessage);
                                } else if (!sourceStatus) {
                                    rspMessage = "Gamas tickets can't be related";
                                    responseError(hsr1, date, rspMessage);
                                } else if (!checkServiceId) {
                                    rspMessage = "service_id does not exist in impacted service";
                                    responseError(hsr1, date, rspMessage);
                                } else if (!checkChildTicketToPrent) {
                                    rspMessage = "the ticket has been related to the ticket " + daoGtr.parentTicket;
                                    responseError(hsr1, date, rspMessage);
                                }
                            }
                        } else {
                            if (ticketStatus && clsfTypeStatus && sourceStatus) {

                                daoGtr.insertToRelatedRecord(dataCt.getIdTicket(), dataPt.getRecordId(), dataPt.getTicketId(), "MANUAL_API_TICKET_RELATIONS");
                                daoGtr.updateIdGamasToChild(dataPt.getIdTicket(), dataCt.getParentId());
                                try {
                                    JSONObject jObj;
                                    jObj = new JSONObject();
                                    mainObj.put("code", "200");
                                    mainObj.put("message", "Ticket successfully related to Gamas");
                                    mainObj.write(hsr1.getWriter());
                                } catch (JSONException ex) {
                                    LogUtil.error(this.getClassName(), ex, "Error : " + ex.getMessage());
                                }
                            } else {
                                if (!ticketStatus) {
                                    rspMessage = "ticket status has passed the resolve process";
                                    responseError(hsr1, date, rspMessage);
                                } else if (!clsfTypeStatus) {
                                    rspMessage = "classification type does not match the ticket gamas";
                                    responseError(hsr1, date, rspMessage);
                                } else if (!sourceStatus) {
                                    rspMessage = "Gamas tickets can't be related";
                                    responseError(hsr1, date, rspMessage);
                                }
                            }
                        }
//                        LogUtil.info(this.getClassName(), "Ticket Status : " + dataCt.getTicketStatus());
                        if ("BACKEND".equalsIgnoreCase(dataCt.getTicketStatus())) {
                            HashMap<String, String> paramCkWo = new HashMap<String, String>();
                            paramCkWo.put("externalID1", dataCt.getIdTicket());
                            daoGtr.getStatusWo(paramCkWo, dataCt.getIdTicket(), dataCt.getId());
                        }

                    } catch (ParseException ex) {
                        LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
                    } catch (Exception ex) {
                        LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
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

            } else {
                // Throw 403 status OR send default allow
                hsr1.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "");
            }
        } catch (Exception e) {
            LogUtil.error(getClassName(), e, e.getMessage());
        }

    }

    private void responseError(HttpServletResponse hsr1, String date, String message) throws IOException {
        try {
            JSONObject headMainObj = new JSONObject();
            JSONObject mainObj = new JSONObject();
            JSONObject jObj;

            jObj = new JSONObject();
            mainObj.put("date", date);
            mainObj.put("code", "500");
            mainObj.put("message", message);
            headMainObj.put("error", mainObj);
            headMainObj.write(hsr1.getWriter());
        } catch (JSONException ex) {
            LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
        }
    }

    private final OkHttpClient httpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public String assigmentCompleteProcess(ApiConfig apiConfig, RequestBody formBody) throws Exception {

        String stringResponse = "";

        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        }
        return stringResponse;
    }

}
