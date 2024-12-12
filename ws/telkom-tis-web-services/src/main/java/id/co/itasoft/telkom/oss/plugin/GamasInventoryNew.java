package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.ConfigurationDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.OkHttpSingleton;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class GamasInventoryNew extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Get Gamas Inventory New";

    LogInfo logInfo = new LogInfo();
    ConfigurationDao configurationDao = new ConfigurationDao();

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

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String token = "";
        JSONObject mainObj = new JSONObject();
        RESTAPI restapi = new RESTAPI();
        MasterParam masterParam = new MasterParam();
        MasterParamDao masterParamDao = new MasterParamDao();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        outerLabel:
        try {
            JSONObject configuration = configurationDao.getConfigurationMapping();
            String getPerangkat = configuration.getString("get_perangkat");

            if ("true".equalsIgnoreCase(getPerangkat)) {
                JSONObject balikan = new JSONObject();
                masterParam = masterParamDao.getUrl("GamasInventoryNew");
                String sto = (req.getParameter("sto") != null) ? req.getParameter("sto") : "";
//                untuk datatable
                String stringDraw = (req.getParameter("draw") != null) ? req.getParameter("draw") : "0";
                Integer draw = Integer.parseInt(stringDraw);

                if (sto.isEmpty()) {
                    JSONArray data = new JSONArray();
                    JSONObject dt = new JSONObject();
                    dt.put("WITEL", "-");
                    dt.put("HOST_NAME", "-");
                    dt.put("PROCESS_AT", "-");
                    dt.put("IP_ADDRESS", "-");
                    dt.put("UPDATED_AT", "-");
                    dt.put("TYPE", "-");
                    dt.put("REGIONAL", "-");
                    dt.put("STO", "-");
                    data.put(0, dt);
                    balikan.put("total", 0);
                    balikan.put("size", 0);
                    balikan.put("data", data);
//                    untuk datatable
                    balikan.put("recordsTotal", 0);
                    balikan.put("recordsFiltered", 0);
                    balikan.put("draw", draw);
                    res.getWriter().print(balikan);
                    break outerLabel;
                }

                token = masterParamDao.getTokenFromMstApi("get_token_aggregator");
                mainObj = callGetPerangkatNew(sto, token, masterParam.getUrl());
                logInfo.Log(this.getClass().getName(), "balikan : "+mainObj);

                if (mainObj.has("response_message")) {
                    String responseMessage = (mainObj.getString("response_message") != null) ? mainObj.getString("response_message") : "";
                    if (responseMessage.equalsIgnoreCase("Unauthorized")) {
                        token = restapi.getTokenAggregator();
                        mainObj = callGetPerangkatNew(sto, token, masterParam.getUrl());
                    }
                }

                if (mainObj.has("msg")) {
                    balikan = new JSONObject(mainObj.getString("msg"));
                    JSONArray data = balikan.getJSONArray("data");
                    balikan.put("total", data.length());
                    balikan.put("size", 10);
//                    untuk datatable
                    balikan.put("draw", draw);
                    balikan.put("recordsTotal", data.length());
                    balikan.put("recordsFiltered", data.length());
                    res.getWriter().print(balikan);
                } else {
                    res.getWriter().print(mainObj);
                }
            } else {
                JSONObject balikan = new JSONObject();
                masterParam = masterParamDao.getUrl("GamasInventory");
                String apiId = masterParam.getApi_id();
                String apiKey = masterParam.getApi_key();
                String url = masterParam.getUrl();
                String classstructure_id = (req.getParameter("classstructure_id") != null) ? req.getParameter("classstructure_id") : "";
                String hostname = (req.getParameter("hostname") != null) ? req.getParameter("hostname") : "";
                String ip_address = (req.getParameter("ip_address") != null) ? req.getParameter("ip_address") : "";
                String type = (req.getParameter("type") != null) ? req.getParameter("type") : "";
                String stringStart = (req.getParameter("start") != null) ? req.getParameter("start") : "0";
                String stringLength = (req.getParameter("length") != null) ? req.getParameter("length") : "0";
                 String stringDraw = (req.getParameter("draw") != null) ? req.getParameter("draw") : "0";

                Integer draw = Integer.parseInt(stringDraw);
                Integer start = Integer.parseInt(stringStart);
                Integer length = Integer.parseInt(stringLength);

                JSONObject request = new JSONObject();
                request.put("startOffset", start);
                request.put("pageSize", length);
                request.put("classstructure_id", classstructure_id);
                request.put("ip_address", ip_address);
                request.put("type", type);
                request.put("hostname", hostname);

                balikan = callGetPerangkatOld(request, apiId, apiKey, url);
//                tambahan untuk datatable
                balikan.put("draw", draw);
                balikan.put("recordsTotal", balikan.getInt("total"));
                balikan.put("recordsFiltered", balikan.getInt("total"));
                res.getWriter().print(balikan);
            }
        } catch (Exception ex) {
            LogUtil.error(getClassName(), ex, ex.getMessage());
        } finally {
            masterParam = null;
        }
    }

    private JSONObject callGetPerangkatNew(String sto, String token, String url) {
        JSONObject getDeviceBySTORequest = new JSONObject();
        JSONObject res = new JSONObject();
        JSONObject req = new JSONObject();
        JSONObject eaiHeader = new JSONObject();
        JSONObject eaiBody = new JSONObject();
        RESTAPI restapi = new RESTAPI();
        RequestBody body;
        Request request;

        try {
            req.put("sto", sto);

            body = RequestBody.create(restapi.JSON, req.toString());
            request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            res = restapi.CALLAPIHANDLER(request);
        } catch (Exception ex) {
            LogUtil.error(getClassName(), ex, ex.getMessage());
        } finally {
            getDeviceBySTORequest = null;
            req = null;
            eaiHeader = null;
            eaiBody = null;
            body = null;
            request = null;
        }

        return res;
    }

    private JSONObject callGetPerangkatOld(JSONObject request, String apiId, String apiKey, String url) {
        JSONObject response = new JSONObject();
        Response res;
        Request req;
        OkHttpSingleton localSingleton = OkHttpSingleton.getInstance();
        String responseBody;
        try {
            String classstructure_id = (request.getString("classstructure_id") == null ? null : request.getString("classstructure_id"));
            String hostname = (request.getString("hostname") == null ? null : request.getString("hostname"));
            String ip_address = (request.getString("ip_address") == null ? null : request.getString("ip_address"));
            String type = (request.getString("type") == null ? null : request.getString("type"));
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            urlBuilder.addQueryParameter("startOffset", String.valueOf(request.getInt("startOffset")));
            urlBuilder.addQueryParameter("pageSize", String.valueOf(request.getInt("pageSize")));
            urlBuilder.addQueryParameter("classstructure_id", classstructure_id);
            urlBuilder.addQueryParameter("hostname", hostname);
            urlBuilder.addQueryParameter("ip_address", ip_address);
            urlBuilder.addQueryParameter("type", type);

            req = new Request.Builder()
                    .url(urlBuilder.build())
                    .addHeader("api_key", apiKey)
                    .addHeader("api_id", apiId)
                    .addHeader("Connection", "keep-alive")
                    .build();

            res = localSingleton.getClient().newCall(req).execute();
            Integer codeResponse = res.code();

            if (!res.isSuccessful()) {
                response = new JSONObject();
                response.put("status", false);
                response.put("status_code", res.code());
                response.put("response_message", res.message());
                response.put("msg", res);
            } else {
                responseBody = res.body().string();
                response = new JSONObject(responseBody);
            }
        } catch (Exception ex) {
            LogUtil.error(getClassName(), ex, ex.getMessage());
        } finally {
            req = null;
        }
        return response;
    }

}
