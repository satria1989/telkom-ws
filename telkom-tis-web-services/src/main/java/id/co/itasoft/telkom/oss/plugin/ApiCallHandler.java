package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateTicketOwnerDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.OkHttpSingleton;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;
import org.springframework.context.ApplicationContext;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author itasoft
 */
public class ApiCallHandler extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Api Call Handler";

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
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        OkHttpSingleton localSingleton = OkHttpSingleton.getInstance();
        RESTAPI _RESTAPI = new RESTAPI();
        CheckOrigin checkOrigin = null;
        MasterParam masterParam = null;
        JSONObject mainObj = null;
        JSONArray _arr = null;
        MasterParamDao mpd = null;
        JSONObject jsonOBJJ;
        Object json = null;
        Response response = null;
        String stringResponse = "";
        JSONArray resJsonArray;
        JSONObject resObject;
        Request request;

        outerLabel:
        try {
            // CHECK ORIGIN

            checkOrigin = new CheckOrigin();
            String origin = req.getHeader("Origin");
            String ACAO = req.getHeader("Access-Control-Allow-Origin");
            if (origin == null) {
                origin = ACAO;
            }

            boolean allowOrigin = checkOrigin.checkingOrigin(origin, res);

            if (!allowOrigin) {
                res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Not Acceptable");
                break outerLabel;
            }

            ApplicationContext ac = AppUtil.getApplicationContext();
            WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
            if (workflowUserManager.isCurrentUserAnonymous()) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
                break outerLabel;
            }

//                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
//                executor.setKeepAliveTime(1, TimeUnit.MINUTES);
            masterParam = new MasterParam();
            mpd = new MasterParamDao();

            String param = req.getParameter("request");
            String type = req.getParameter("type");
            String referrer = req.getHeader("referer");

            info.Log("PARAM", param);
            info.Log("type", type);

            String DATA = param;
            _arr = new JSONArray();

            jsonOBJJ = new JSONObject(DATA);
            Iterator<?> iterator = jsonOBJJ.keys();
            HashMap<String, String> params = new HashMap<String, String>();

            switch (type) {
                case ("listServiceInformation"):
                    masterParam = mpd.getUrl("list_service_information_custom");
                    break;
                case ("listLocation"):
                    masterParam = mpd.getUrl("list_location");
                    break;
                case ("listTkMapping"):
                case ("reloadOwnerGroup"):
                    masterParam = mpd.getUrl("list_tk_mapping");
                    break;
                case ("listSymptomp"):
                    masterParam = mpd.getUrl("list_symptomp");
                    break;
                case ("workzone"):
                    masterParam = mpd.getUrl("list_workzone");
                    break;
                case ("symptomCstm"):
                    masterParam = mpd.getUrl("symptom_custom");
                    break;
                case ("listFullbookedAssign"):
                    masterParam = mpd.getUrl("list_fullbookedAssign");
                    break;
                case ("listAppointmentSchedule"):
                    masterParam = mpd.getUrl("list_appointment_schedule");
                    break;
                case ("loadWorkorder"):
                    masterParam = mpd.getUrl("list_work_order");
                    break;
                case ("listOwnerGroup"):
                    masterParam = mpd.getUrl("url_ownergroup_list");
                    break;
                case ("perangkat"):
                    masterParam = mpd.getUrl("GetPerangkat");
                    break;
                case ("person_details"):
                    masterParam = mpd.getUrl("person_details");
                    break;
                case ("getSchedule"):
                    masterParam = mpd.getUrl("get_data_scheduling");
                    break;
                case ("getClasifications"):
                    masterParam = mpd.getUrl("getActSolAndIncDom");
                    break;
                default:
                    break;
            }


            JSONObject paramJson = null;
            String value = "";
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if (jsonOBJJ.get(key) instanceof JSONObject) {
                    info.Log(getClassName(), "MASUK SINI");
                    if (key.equalsIgnoreCase("param")) {
                        paramJson = (JSONObject) jsonOBJJ.get("param");
                        Iterator<?> paramIterator = paramJson.keys();

                        while (paramIterator.hasNext()) {
                            String paramKey = (String) paramIterator.next();
                            value = ("".equals(paramJson.getString(paramKey))) ? "" : paramJson.getString(paramKey);
                            params.put(paramKey, value);
                        }
                    }
                } else {
                    if (key.equalsIgnoreCase("url")) {
                        masterParam.setUrl(jsonOBJJ.getString(key));
                    }
                }
            }
            iterator.remove();

            if ("person_details".equalsIgnoreCase(type)) {
                String username = mpd.getUsername(value);
                JSONObject paramPersonDetails = new JSONObject();
                paramPersonDetails.put("person_code", username.toUpperCase());
                RequestBody body = RequestBody.create(_RESTAPI.JSON, paramPersonDetails.toString());
                request = new Request.Builder()
                        .url(masterParam.getUrl())
                        .addHeader("api_key", masterParam.getApi_key()) // add request headers
                        .addHeader("api_id", masterParam.getApi_id())
                        .addHeader("token", "")
                        .addHeader("Origin", "https://oss-incident.telkom.co.id")
                        .post(body)
                        .build();

                paramPersonDetails = null;
                response = localSingleton.getClient().newCall(request).execute();
            } else {
//                    CRA = new CallRestAPI();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(masterParam.getUrl()).newBuilder();
                if (params != null) {
                    for (Map.Entry<String, String> parameter : params.entrySet()) {
                        httpBuilder.addQueryParameter(parameter.getKey(), parameter.getValue());
                    }
                }
                info.Log(getClassName(), "----" + httpBuilder.toString());
                info.Log(getClassName(), "----" + masterParam.getApi_key());
                info.Log(getClassName(), "----" + masterParam.getApi_id());

                info.Log(getClassName(), "MASUK SINI REQUEST");
                request = new Request.Builder()
                        .url(httpBuilder.build())
                        .addHeader("api_key", masterParam.getApi_key()) // add request headers
                        .addHeader("api_id", masterParam.getApi_id())
                        .addHeader("token", "")
                        .addHeader("Connection", "keep-alive")
                        //.addHeader("Origin", "https://dev-joget-incident-ticketing-service-joget-dev.apps.mypaas.telkom.co.id")
                        .addHeader("Origin", "https://oss-incident.telkom.co.id")
                        .build();

//                    httpBuilder = null;
                response = localSingleton.getClient().newCall(request).execute();
//                info.Log(getClassName(), response.body().string());
            }
            
            int codeResponse = response.code();

            if (!response.isSuccessful()) {
                info.Log(getClassName(), "error");
                res.sendError(codeResponse, response.message());
            } else {

                stringResponse = response.body().string();
                if ("reloadOwnerGroup".equalsIgnoreCase(type)) {
                    String ownerGroup = "";
                    JSONParser parse = new JSONParser();
                    InsertTicketHistoryDao dao = new InsertTicketHistoryDao();
                    LoadTicketDao loadTicket = new LoadTicketDao();
                    TicketStatus ticketStatus = new TicketStatus();

                    org.json.simple.JSONObject data_obj = (org.json.simple.JSONObject) parse.parse(stringResponse);
                    org.json.simple.JSONArray arrData = (org.json.simple.JSONArray) data_obj.get("data");

                    for (Object object : arrData) {
                        org.json.simple.JSONObject obj = (org.json.simple.JSONObject) object;
                        ownerGroup = obj.get("person_owner_group").toString();
                    }

                    URL url = new URL(referrer);
                    String queryStr = url.getQuery();
                    String[] paramss = queryStr.split("&");
                    JSONObject paramReferer = new JSONObject();
                    for (String param2 : paramss) {
                        String key = param2.substring(0, param2.indexOf('='));
                        String val = param2.substring(param2.indexOf('=') + 1);
                        paramReferer.put(key, val);
                    }
                    String PARENT_ID = paramReferer.getString("id");

                    UpdateTicketOwnerDao utoDao = new UpdateTicketOwnerDao();
                    utoDao.updateOwnerGroup(ownerGroup, PARENT_ID);

                    ticketStatus = loadTicket.LoadTicketByParentId(PARENT_ID);
                    ticketStatus.setStatusTracking(ticketStatus.getTicketId());
                    ticketStatus.setAssignedOwnerGroup(ownerGroup);
                    ticketStatus.setOwnerGroup(ownerGroup);
                    dao.insertTicketStatus(ticketStatus);
                }


                json = new JSONTokener(stringResponse).nextValue();

                if (json instanceof JSONObject) {
                    resObject = (JSONObject) json;
                    resObject.write(res.getWriter());
                } else if (json instanceof JSONArray) {
                    resJsonArray = (JSONArray) json;
                    resJsonArray.write(res.getWriter());
                } else {
                    res.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Format Response API Not Acceptable");
                }
//                stringResponse = null;
            }
            DATA = null;
            response.close();
            params.clear();
        } catch (Exception ex) {
            info.Error(getClassName(), ex.getMessage(), ex);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Service Error");
        } finally {
            checkOrigin = null;
            masterParam = null;
            mainObj = null;
            _arr = null;
            mpd = null;
            jsonOBJJ = null;
            json = null;
            response = null;
            stringResponse = "";
            request = null;

        }
    }

}
