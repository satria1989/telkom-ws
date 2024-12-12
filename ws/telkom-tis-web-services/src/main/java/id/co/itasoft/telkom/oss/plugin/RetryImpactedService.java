/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.RetryImpactedServiceDao;
import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.jooq.DAO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

/**
 *
 * @author mtaup
 */
public class RetryImpactedService extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Retry Get Impacted Service";
    LogInfo logInfo = new LogInfo();

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
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        try {
//            LogUtil.info(this.getClassName(), "PERANGKAT = "+perangkat);
//            String ticketId = req.getParameter("ticketId");
            String stringResponse = "";
            JSONObject jo = new JSONObject();
            JSONObject gismr = new JSONObject();
            JSONObject eh = new JSONObject();
            JSONObject eb = new JSONObject();

            ApiConfig apiConfig = new ApiConfig();
            RESTAPI restApi = new RESTAPI();
            MasterParam param = new MasterParam();
            MasterParamDao paramDao = new MasterParamDao();
            CallRestAPI cra = new CallRestAPI();
            LogHistory lh = new LogHistory();
            LogHistoryDao lhDao = new LogHistoryDao();
            final RetryImpactedServiceDao risDao = new RetryImpactedServiceDao();

            String referrer = req.getHeader("referer");
            URL url = new URL(referrer);
            String queryStr = url.getQuery();
            String[] paramss = queryStr.split("&");
            org.json.JSONObject paramReferer = new org.json.JSONObject();
            for (String param2 : paramss) {
                String key = param2.substring(0, param2.indexOf('='));
                String val = param2.substring(param2.indexOf('=') + 1);
                paramReferer.put(key, val);
            }
            String PARENT_ID = paramReferer.getString("id");

            Ticket ticket = new Ticket();
            ticket = risDao.getProcessIdTicket(PARENT_ID);
            String _perangkat = (ticket.getPerangkat() == null ? "" : ticket.getPerangkat());
            String _sourceTicket = ticket.getSourceTicket() == null ? "" : ticket.getSourceTicket();
            String _channel = ticket.getChannel() == null ? "" : ticket.getChannel();
            JSONObject respObj = new JSONObject();
            org.json.JSONObject config = new org.json.JSONObject();
            int total_all_service = 0, total_service_info = 0, total_service_info_datin = 0, total_service_sdwan = 0;
            if (!"".equals(_perangkat) && ("GAMAS".equalsIgnoreCase(_sourceTicket) || "62".equals(_channel))) {
//                LogUtil.info(this.getClassName(), "KONDISI GAMAS/62 && PERANGKAT != NULL");
                String _token = restApi.getToken();
                String lastPerangkat = "";
                try {
                    String listPerangkat[] = ticket.getPerangkat().split(",");
                    List<Ticket> r = new ArrayList<>();
                    for (String perangkat : listPerangkat) {
                        if (!lastPerangkat.equals(perangkat)) {

                            Map eaiHeader = new LinkedHashMap(2);
                            eaiHeader.put("externalId", ticket.getTicketId());
                            eaiHeader.put("timestamp", "");

                            Map eaiBody = new LinkedHashMap(3);
                            eaiBody.put("input", perangkat.trim());
                            eaiBody.put("page", "");
                            eaiBody.put("limit", "");

                            Map impactedServicesRequest = new LinkedHashMap(2);
                            impactedServicesRequest.put("eaiHeader", eaiHeader);
                            impactedServicesRequest.put("eaiBody", eaiBody);
                            gismr.put("impactedServicesRequest", impactedServicesRequest);

                            param = paramDao.getUrl("impact_service_v2");
                            apiConfig.setUrl(param.getUrl());
                            RequestBody jsonRequestBody = RequestBody.create(cra.JSON, gismr.toJSONString());

                            Request request
                                    = new Request.Builder()
                                            .url(apiConfig.getUrl())
                                            .addHeader("Authorization", "Bearer " + _token)
                                            .post(jsonRequestBody)
                                            .build();

                            stringResponse = restApi.CALLAPI(request);

                            org.json.JSONObject reqObj = new org.json.JSONObject(gismr);
                            org.json.JSONObject resObj = new org.json.JSONObject(stringResponse);
                            lhDao.SENDHISTORY(
                                    ticket.getIdTicket(),
                                    "Retry Get Impacted Service",
                                    apiConfig.getUrl(),
                                    "POST",
                                    reqObj,
                                    resObj,
                                    0
                            );

                            String assetnum = "", realm = "", segment = "", phoneNumber = "";
                            org.json.JSONObject obj = new org.json.JSONObject(stringResponse);

                            if (obj.has("status")) {
                                String status = obj.get("status").toString();
                                if ("Success".equalsIgnoreCase(status)) {
                                    logInfo.Log(this.getClassName(), "masuk status");
                                    org.json.JSONObject data = new org.json.JSONObject(obj.get("data").toString());
                                    org.json.JSONObject dataTotal = data.getJSONObject("total");
                                    String service_info = "";
                                    String service_info_datin = "";
                                    String service_sdwan = "";
                                    total_all_service = total_all_service + dataTotal.getInt("total_all_data");
                                    total_service_info = total_service_info + dataTotal.getInt("total_service_info");
                                    total_service_info_datin = total_service_info_datin + dataTotal.getInt("total_service_info_datin");
                                    total_service_sdwan = total_service_sdwan + dataTotal.getInt("total_service_sdwan");

                                    JSONArray service_info_arr = null;
                                    if (data.has("service_info")) {
                                        logInfo.Log(this.getClassName(), "masuk service info");
                                        service_info = data.getJSONArray("service_info").toString();
                                        service_info_arr = new JSONArray(service_info);
                                    }

                                    JSONArray service_info_datin_arr = null;
                                    if (data.has("service_info_datin")) {
                                        service_info_datin = data.getJSONArray("service_info_datin").toString();
                                        service_info_datin_arr = new JSONArray(service_info_datin);
                                    }

                                    JSONArray service_sdwan_arr = null;
                                    if (data.has("service_sdwan")) {
                                        service_sdwan = data.getJSONArray("service_sdwan").toString();
                                        service_sdwan_arr = new JSONArray(service_sdwan);
                                    }

                                    if (service_info_datin_arr != null) {
                                        for (int i = 0; i < service_info_datin.length(); i++) {
                                            org.json.JSONObject jsonObjek = service_info_datin_arr.getJSONObject(i);
                                            service_info_arr.put(jsonObjek);
                                        }
                                    }

                                    if (service_sdwan_arr != null) {
                                        for (int i = 0; i < service_sdwan.length(); i++) {
                                            org.json.JSONObject jsonObjek = service_sdwan_arr.getJSONObject(i);
                                            service_info_arr.put(jsonObjek);
                                        }
                                    }

                                    String serviceNumber = "", serviceType = "";
                                    List<String> currentImpacted = risDao.getCurentImpactedervice(ticket.getParentId());
                                    List<String> serviceId = new ArrayList<>();
                                    Ticket tc = null;

                                    for (int i = 0; i < service_info_arr.length(); i++) {
                                        assetnum = service_info_arr.getJSONObject(i).getString("AGG_SERVICE_ID");
                                        serviceNumber = service_info_arr.getJSONObject(i).getString("SERVICE_NUMBER");
                                        serviceType = service_info_arr.getJSONObject(i).getString("SERVICE_TYPE");
                                        realm = !service_info_arr.getJSONObject(i).isNull("DOMAIN_NAME") ? service_info_arr.getJSONObject(i).getString("DOMAIN_NAME") : "";
                                        phoneNumber = !service_info_arr.getJSONObject(i).isNull("SERVICE_CONTACT") ? service_info_arr.getJSONObject(i).getString("SERVICE_CONTACT") : "";

                                        if (currentImpacted.size() > 0) {
                                            if (currentImpacted.contains(assetnum) == false) {
                                                tc = new Ticket();

                                                if (i + 1 <= total_service_info) {
                                                    if ("telkom.b2b".equalsIgnoreCase(realm)) {
                                                        segment = "non_retail";
                                                    } else {
                                                        segment = "retail";
                                                    }
                                                } else {
                                                    segment = "non_retail";
                                                }

                                                tc.setServiceId(assetnum);
                                                tc.setOperStatus("");
                                                tc.setParentId(ticket.getParentId());
                                                tc.setTicketId(ticket.getIdTicket());
                                                tc.setServiceNumber(serviceNumber);
                                                tc.setEstimation(ticket.getEstimation());
                                                tc.setSymptom(ticket.getSymptom());
                                                tc.setSymptomDesc(ticket.getSymptomDesc());
                                                tc.setRegion(ticket.getRegion());
                                                tc.setPerangkat(perangkat);
                                                tc.setMethod("RETRY_VIA_UI");
                                                tc.setChannel(_channel);
                                                tc.setServiceType(serviceType);
                                                tc.setRealm(realm);
                                                tc.setSegment(segment);
                                                tc.setPhoneNumber(phoneNumber);

                                                r.add(tc);
                                            }
                                        } else {
                                            tc = new Ticket();
                                            if (i + 1 <= total_service_info) {
                                                if ("telkom.b2b".equalsIgnoreCase(realm)) {
                                                    segment = "non_retail";
                                                } else {
                                                    segment = "retail";
                                                }
                                            } else {
                                                segment = "non_retail";
                                            }
                                            tc.setServiceId(assetnum);
                                            tc.setOperStatus("");
                                            tc.setParentId(ticket.getParentId());
                                            tc.setTicketId(ticket.getIdTicket());
                                            tc.setServiceNumber(serviceNumber);
                                            tc.setEstimation(ticket.getEstimation());
                                            tc.setSymptom(ticket.getSymptom());
                                            tc.setSymptomDesc(ticket.getSymptomDesc());
                                            tc.setRegion(ticket.getRegion());
                                            tc.setPerangkat(perangkat);
                                            tc.setMethod("RETRY_VIA_UI");
                                            tc.setChannel(_channel);
                                            tc.setServiceType(serviceType);
                                            tc.setRealm(realm);
                                            tc.setSegment(segment);
                                            tc.setPhoneNumber(phoneNumber);

                                            r.add(tc);
                                        }

                                    }

                                    risDao.insertToTableService(r);
                                    //                            restApi.updateCallbackToMyihx(serviceId, ticket, _token);
                                    lastPerangkat = perangkat;
                                    final List<Ticket> newR = r;
                                    if (r.size() > 0) {
                                        respObj.put("code", "200");
                                        respObj.put("message", r.size() + " service id successfully loaded");

                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String realm = "telkom.net";
                                                RESTAPI restApi = new RESTAPI();
                                                String _token = restApi.getToken();
                                                for (Ticket ticket1 : newR) {

                                                    try {
                                                        String operStatus = risDao.getOperStatus(ticket1.getServiceNumber(), realm, _token);
                                                        risDao.updateOperStatus(operStatus, ticket1.getServiceId(), ticket1.getParentId());
                                                    } catch (Exception ex) {
                                                        logInfo.Error(this.getClass().getName(), "Get Oper Status", ex);
                                                    }
                                                }
                                            }
                                        });
                                        thread.setDaemon(true);
                                        thread.start();
                                    } else {
                                        respObj.put("code", "200");
                                        respObj.put("message", "The service_id could not be loaded successfully");
                                    }
                                } else {
                                    respObj.put("code", "404");
                                    respObj.put("message", obj.get("message").toString());
                                }
                            }
                        }
                    }

                    Map<String, Integer> mapDataTotal = new HashMap<String, Integer>();

                    mapDataTotal.put("total_all_service", total_all_service);
                    mapDataTotal.put("total_service_info", total_service_info);
                    mapDataTotal.put("total_service_info_datin", total_service_info_datin);
                    mapDataTotal.put("total_service_sdwan", total_service_sdwan);

                    risDao.attributtTicket(ticket.getIdTicket(), mapDataTotal);

                    respObj.writeJSONString(res.getWriter());
                } catch (Exception ex) {
                    logInfo.Error(this.getClass().getName(), "Get Oper Status", ex);
                }
            }
        } catch (Exception ex) {
            logInfo.Error(this.getClass().getName(), "Get Oper Status", ex);
        }
    }

}
