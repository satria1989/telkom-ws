/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertChildTicketGamasDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.function.SendWANotification;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.TechnicalDataHandlerDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.OutTechnicalData;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author suena, rizki
 */
public class TechnicalDataHandler extends DefaultApplicationPlugin {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Technical Data Handler";
    private String processIdIncident;
    InsertChildTicketGamasDao dao = new InsertChildTicketGamasDao();
    RESTAPI _RESTAPI;
    LogHistory logHistory;
    LogHistoryDao logHistoryDao;
    LogInfo logInfo = new LogInfo();

    @Override
    public Object execute(Map map) {
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        String processIdDD = appService.getOriginProcessId(workflowAssignment.getProcessId());

        final String processId = processIdDD;
        _RESTAPI = new RESTAPI();

        InsertChildTicketGamasDao ictgd = new InsertChildTicketGamasDao();
        InsertRelatedRecordDao daoTicket = new InsertRelatedRecordDao();
        InsertTicketStatusLogsDao insertTicketStatusLogsDao = new InsertTicketStatusLogsDao();
        MasterParam param;
        MasterParamDao paramDao;
        SendWANotification swa;
        ApiConfig apiConfig;
        TechnicalDataHandlerDao daoTech;
        TicketStatus r;
        try {
            r = new TicketStatus();
            r = insertTicketStatusLogsDao.getTicketId(processId);
            if (!"GAMAS".equalsIgnoreCase(r.getSourceTicket())) {
                param = new MasterParam();
                paramDao = new MasterParamDao();
                swa = new SendWANotification();
                apiConfig = new ApiConfig();

                String token = _RESTAPI.getToken();
                org.json.JSONObject jo = new org.json.JSONObject();
                org.json.JSONObject root = new org.json.JSONObject();
                Map child = new LinkedHashMap(2);

                child.put("externalId", "");
                child.put("timestamp", "");
                jo.put("eaiHeader", child);

                child = new LinkedHashMap(2);
                child.put("IN_FAULTID", r.getTicketId());
                child.put("IN_SERVICEID", r.getServiceId());
                jo.put("eaiBody", child);

                root.put("getTechnicalDataUIMRequest", jo);

                String stringResponse = "";
                param = paramDao.getUrl("technical_data");
                apiConfig.setUrl(param.getUrl());

                RequestBody jsonRequestBody = RequestBody.create(_RESTAPI.JSON, root.toString());
                Request request = new Request.Builder()
                        .url(apiConfig.getUrl())
                        .addHeader("Authorization", "Bearer " + token)
                        .post(jsonRequestBody)
                        .build();

                stringResponse = _RESTAPI.CALLAPI(request);
                JSONParser parse = new JSONParser();
                try {
                    Object json = new JSONTokener(stringResponse).nextValue();
                    org.json.JSONObject resTechData = (org.json.JSONObject) json;

                    org.json.JSONObject getTechnicalDataUIMResponse = (org.json.JSONObject) resTechData.get("getTechnicalDataUIMResponse");
                    org.json.JSONObject eaiStatusJSON = (org.json.JSONObject) getTechnicalDataUIMResponse.get("eaiStatus");

                    String eaiStatus = (String) eaiStatusJSON.get("responseMsg");

                    if ("SUCCESS".equalsIgnoreCase(eaiStatus)) {
                        org.json.JSONObject eaiBodyJson = (org.json.JSONObject) getTechnicalDataUIMResponse.get("eaiBody");
                        org.json.JSONObject getTechnicalDataResultJson = (org.json.JSONObject) eaiBodyJson.get("getTechnicalDataResult");
                        org.json.JSONArray getOUT_TECHNICALDATA = (org.json.JSONArray) getTechnicalDataResultJson.get("OUT_TECHNICALDATA");

                        List<OutTechnicalData> list = new ArrayList<>();
                        org.json.JSONObject jsonObject;
                        OutTechnicalData otd;
                        String technology = "";
                        for (int i = 0; i < getOUT_TECHNICALDATA.length(); i++) {
                            jsonObject = (org.json.JSONObject) getOUT_TECHNICALDATA.get(i);
                            otd = new OutTechnicalData();

                            otd.setServiceName(jsonObject.getString("SERVICE_NAME"));
                            otd.setPipeOrder(jsonObject.getString("PIPE_ORDER"));
                            otd.setPipeName(jsonObject.getString("PIPE_NAME"));
                            otd.setDeviceName(jsonObject.getString("DEVICE_NAME"));
                            otd.setPortName(jsonObject.getString("PORT_NAME"));
                            if ("TECHNOLOGY".equalsIgnoreCase(jsonObject.getString("PORT_NAME"))) {
                                technology = jsonObject.getString("DEVICE_NAME");
//                                technology = "Non Fiber";
                            }
                            otd.setParentId(r.getId());
                            otd.setTicketId(r.getTicketId());
                            list.add(otd);
                        }

                        daoTech = new TechnicalDataHandlerDao();
                        daoTech.insertBatchOutTechnicalData(list);
                        daoTech.updateTechnology(r.getId(), technology);

                        param = paramDao.getUrl("UpdateTechToWo");

                        String ticketID = (r.getTicketId() == null) ? "" : r.getTicketId();
                        String techno = (r.getTechnology() == null) ? "" : r.getTechnology();
                        logHistory = new LogHistory();
                        logHistoryDao = new LogHistoryDao();
                        logHistory.setAction("UPDATE TECHNOLOGY(" + ticketID + ")");
                        logHistory.setCreatedBy("SYSTEM");
                        logHistory.setMethod("POST");

                        JSONObject jsonWo = new JSONObject();
                        jsonWo.put("action", "UpdateTech");
                        jsonWo.put("externalId", ticketID);
                        jsonWo.put("changeBy", "SYSTEM");
                        jsonWo.put("technologyType", techno);
                        logHistory.setRequest(jsonWo.toString());
                        logHistory.setTicketId(ticketID);
                        logHistory.setUrl(param.getUrl());

                        RequestBody body = RequestBody.create(_RESTAPI.JSON, jsonWo.toString());
                        request = new Request.Builder()
                                .url(param.getUrl())
                                .addHeader("api_key", param.getApi_key()) // add request headers
                                .addHeader("api_id", param.getApi_id())
                                .addHeader("Origin", "https://oss-incident.telkom.co.id")
                                .post(body)
                                .build();

                        JSONObject requestUpdate = _RESTAPI.CALLAPIHANDLER(request);
                        logHistory.setResponse(requestUpdate.toString());
                        logHistoryDao.insertToLogHistory(logHistory);

                        getOUT_TECHNICALDATA = null;
                        eaiBodyJson = null;
                        getTechnicalDataResultJson = null;
                        jsonObject = null;
                        list = null;
                        getTechnicalDataUIMResponse = null;
                        eaiStatusJSON = null;
                        resTechData = null;
                    } else if ("ERROR".equalsIgnoreCase(eaiStatus)) {
                    } else {
                    }

                    jo = null;
                    root = null;
                    json = null;
                    child = null;

                } catch (Exception ex) {
                  logInfo.Log(getClass().getName(), ex.getMessage());
                }

            }

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted..." + ex);
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            ictgd = null;
            daoTicket = null;
            param = null;
            paramDao = null;
            swa = null;
            apiConfig = null;
            r = null;
            daoTech = null;
            _RESTAPI = null;
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
//        return AppUtil.readPluginResource(getClassName(), "/properties/settingWorkOrder.json", null, true, null);
    }

}
