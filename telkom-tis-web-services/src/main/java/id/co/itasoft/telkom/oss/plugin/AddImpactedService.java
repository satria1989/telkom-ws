/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import com.google.gson.Gson;
import id.co.itasoft.telkom.oss.plugin.dao.AddImpactedServiceDao;
import id.co.itasoft.telkom.oss.plugin.dao.CompleteActivityTicketIncidentApiDao;
import id.co.itasoft.telkom.oss.plugin.dao.GetMasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.TicketAutomationDaoV4;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.AddImpactedServiceModel;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.ListServiceInformationNew;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.joget.commons.util.LogUtil;
//import protostream.com.google.gson.Gson;

/**
 * @author mtaup
 */
public class AddImpactedService extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Add Impacted Service";

    LogInfo info = new LogInfo();

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
    ApiConfig apiConf = new ApiConfig();
    GetMasterParamDao paramDao = new GetMasterParamDao();

    @Override
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {
        JSONObject mainObj;
        JSONObject jObj;

        StringBuilder jb;
        String line = null;
        int insert = 0;
        Set<String> uniqueServiceIds = null;
        try {

            if ("POST".equals(hsr.getMethod())) {
                apiConf = paramDao.getUrl("ticket_incident_api");
                if (hsr.getHeader("api_id").equals(apiConf.getApiId()) && hsr.getHeader("api_key").equals(apiConf.getApiKey())) {
                    final AddImpactedServiceDao ais = new AddImpactedServiceDao();
                    Ticket ticket = new Ticket();
                    TicketAutomationDaoV4 ta = new TicketAutomationDaoV4();

                    jb = new StringBuilder();

                    BufferedReader reader = hsr.getReader();
                    while ((line = reader.readLine()) != null) {
                        jb.append(line);
                    }

                    String bodyParam = jb.toString();

                    Gson gson = new Gson();
                    final AddImpactedServiceModel data = gson.fromJson(bodyParam, AddImpactedServiceModel.class);

                    ticket = ais.GetDataTicekt(data.getId_ticket());
                    mainObj = new JSONObject();
//                    String[] exsistingServiceId = null;
                    ArrayList<String> exsistingServiceId = new ArrayList<>();
                    ArrayList<String> getExistingServiceId = new ArrayList<>();
                    List<Map<String, String>> listMapSid = new ArrayList<>();
                    Map<String, String> mapData;
                    String operStatus = "";

                    getExistingServiceId = ais.getExistingSId(data.getId_ticket());
                    uniqueServiceIds = new LinkedHashSet<>(Arrays.asList(data.getService_id()));
                    LogUtil.info(this.getClass().getName(), "uniqueServiceIds size : " + uniqueServiceIds.size());
                    data.setService_id(uniqueServiceIds.toArray(new String[0]));
                    Set<String> existingServiceIdSet = new HashSet<>(getExistingServiceId);

                    if ("FISIK".equalsIgnoreCase(ticket.getClassificationType())) {
                        if (uniqueServiceIds.size() <= 5000) {

//                        int i = 0;
                            for (Object sId : data.getService_id()) {
                                mapData = new HashMap<String, String>();
                                String serviceNumber = "";
                                String serviceId = sId.toString();
                                if (serviceId.contains("_")) {
                                    String[] partSid = serviceId.split("_");
                                    if (partSid.length > 2) {
                                        serviceNumber = partSid[1];
                                    } else {
                                        serviceNumber = serviceId;
                                    }
                                } else {
                                    serviceNumber = serviceId;
                                }

                                if (!existingServiceIdSet.contains(sId)) {
                                    mapData.put("sId", sId.toString());
                                    mapData.put("service_number", serviceNumber);
                                    mapData.put("region", "");
                                    listMapSid.add(mapData);
                                } else {
                                    exsistingServiceId.add(sId.toString());
                                }

//                            if (ais.checkServiceId(sId.toString(), data.getId_ticket())) {
//                                HashMap<String, String> param = new HashMap<String, String>();
//
//                                param.put("service_id", sId.toString());
//                                ListServiceInformationNew lsi = new ListServiceInformationNew();
//                                lsi = ta.getListServiceInformationWithoutLog(param);
//                                mapData.put("sId", sId.toString());
//                                mapData.put("service_number", serviceNumber);
//                                mapData.put("region", "");
//                                operStatus = ais.getOperStatus(lsi.getServiceNumber(), "telkom.net");

//                                if (insert) {
//                                    i++;
//                                }
//                                listMapSid.add(mapData);
//                            } else {
//                                exsistingServiceId.add(sId.toString());
//                            }
                            }
                            if (!listMapSid.isEmpty() || listMapSid != null) {
                                insert = ais.insertToTableService(
                                        listMapSid,
                                        operStatus,
                                        ticket.getParentId(),
                                        data.getId_ticket(),
                                        //                                    lsi.getServiceNumber(),
                                        ticket.getEstimation(),
                                        ticket.getSymptom(),
                                        ticket.getSymptomDesc(),
                                        //                                    lsi.getRegion(),
                                        ticket.getPerangkat(),
                                        "MANUAL_VIA_API",
                                        ticket.getChannel()
                                );

                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            for (Object sId : data.getService_id()) {
                                                HashMap<String, String> param = new HashMap<String, String>();
//
                                                param.put("service_id", sId.toString());
                                                ListServiceInformationNew lsi = new ListServiceInformationNew();
                                                lsi = ta.getListServiceInformationWithoutLog(param);
                                                String operStatus = ais.getOperStatus(lsi.getServiceNumber(), "telkom.net");
                                                ais.updateDataImpactedService(sId.toString(), data.getId_ticket(), operStatus, lsi.getRegion());
                                            }

                                        } catch (Exception e) {
                                            Thread.currentThread().interrupt();
                                        }

                                    }
                                });
                                thread.setDaemon(true);
                                thread.start();

                            }
                            jObj = new JSONObject();
                            if (insert > 0) {
                                mainObj.put("code", "200");
                                mainObj.put("message", String.valueOf(insert) + " service_id successfully added");
                                mainObj.put("exsisting_service_id", exsistingServiceId);
                                mainObj.write(hsr1.getWriter());
                            } else {
                                mainObj.put("code", "500");
                                mainObj.put("message", "Add Impacted Service failed");
                                mainObj.put("exsisting_service_id", exsistingServiceId);
                                mainObj.write(hsr1.getWriter());
                            }
                            bodyParam = null;
                            gson = null;
                        } else {
                            hsr1.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "The data impacted services are too many, with a maximum limit of 5000.");
                        }
                    } else {
                        mainObj.put("code", "500");
                        mainObj.put("message", "Only for GAMAS FISIK");
                        mainObj.write(hsr1.getWriter());
                    }

                } else {
                    mainObj = new JSONObject();
                    jObj = new JSONObject();
                    mainObj.put("code", "401");
                    mainObj.put("message", "Invalid Authentication");
                    mainObj.write(hsr1.getWriter());
                }
            } else {
                mainObj = new JSONObject();
                jObj = new JSONObject();
                mainObj.put("code", "405");
                mainObj.put("message", "Method Not Allowed");
                mainObj.write(hsr1.getWriter());
            }

        } catch (Exception ex) {
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            jb = null;
            line = null;
            uniqueServiceIds = null;
        }

    }
}
