/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.function.SendWANotification;
import id.co.itasoft.telkom.oss.plugin.model.ImpactedService;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author itasoft
 */
public class SendWAChildImpactGamasHandler extends DefaultApplicationPlugin {

    final static String pluginName = "Telkom New OSS - Ticket Incident Services - Send WA to Child Gamas";
    private String processIdIncident;
    LogInfo logInfo = new LogInfo();

    @Override
    public Object execute(Map map) {
        List<ImpactedService> listService = new ArrayList<ImpactedService>();
        InsertRelatedRecordDao insertRelatedRecordDao = new InsertRelatedRecordDao();
        InsertTicketStatusLogsDao daoTicket = new InsertTicketStatusLogsDao();
        SendWANotification swa = new SendWANotification();
        MasterParamDao mpd = new MasterParamDao();
        MasterParam masterParam = new MasterParam();
        JSONArray listPhoneNumber = new JSONArray();
        JSONObject objPhoneNumber = new JSONObject();
        TicketStatus ts;
        RESTAPI _RESTAPI = new RESTAPI();
        JSONArray listCustomer = new JSONArray();
        try {

//                ts = daoTicket.getTicketId(processId);
            masterParam = mpd.getUrl("list_service_information_custom");

            ts = new TicketStatus();
            /**
             * GET LOS CHILD GAMAS
             */
            String processId = "";
            listService = insertRelatedRecordDao.getChildGamasHandler(processId);

            String msg;
            RequestBody formBody;
            Request request;

            for (ImpactedService list : listService) {
                JSONObject objCustomer = new JSONObject();
                JSONObject jsonData;
                ts.setSourceTicket("GAMAS");

                /**
                 * GET TO SERVICE INFORMATION BY SERVICE ID
                 */
                HttpUrl.Builder httpBuilder = HttpUrl.parse(masterParam.getUrl()).newBuilder();
//                LogUtil.info(getClassName(), "SERVICE ID ::" + list.getService_id());
                httpBuilder.addQueryParameter("service_id", list.getService_id());
                request = new Request.Builder()
                        .url(httpBuilder.build())
                        .addHeader("api_key", masterParam.getApi_key().toString())
                        .addHeader("api_id", masterParam.getApi_id().toString())
                        .addHeader("Origin", "https://oss-incident.telkom.co.id")
                        .build();

                String requestListServiceInformation = _RESTAPI.CALLAPI(request);
                Object json = new JSONTokener(requestListServiceInformation).nextValue();
                jsonData = (JSONObject) json;

                if (jsonData.has("total")) {
                    if (Integer.parseInt(jsonData.getString("total")) > 0) {
                        Object objData = new JSONTokener(jsonData.getString("data")).nextValue();
                        JSONArray arr = (JSONArray) objData;

                        // LOOPING SERVICE INFORMATION
                        for (int i = 0; i < arr.length(); ++i) {

                            JSONObject objService = arr.getJSONObject(i);
                            if (list.getService_id().equalsIgnoreCase(objService.getString("service_id"))) {
                                String Estimation = (list.getEstimation() == null) ? "" : list.getEstimation();
                                objCustomer.put("service_id", list.getService_id());
                                objCustomer.put("ticket_id", list.getTicket_id());
                                objCustomer.put("customer_id", objService.getString("customer_id"));
                                objCustomer.put("service_number", objService.getString("service_number"));
                                objCustomer.put("dateCreated", list.getDatecreated());
                                objCustomer.put("estimation", Estimation);
                                objCustomer.put("phone_number", objService.getString("phone_number"));
                                listCustomer.put(objCustomer);
                                objCustomer = null;
//                                objCustomer.put("phone_number", "082218099810");

                            }

                        }
                        arr = null;
                        json = null;
                        jsonData = null;
                    }
                }
            }

            /**
             * SEND WA IMPACT GAMAS
             */
            if (listCustomer.length() > 0) {
                for (int x = 0; x < listCustomer.length(); x++) {

                    String phoneNumber = listCustomer.getJSONObject(x).getString("phone_number");
                    String serviceNumber = listCustomer.getJSONObject(x).getString("service_number");
                    String serviceId = listCustomer.getJSONObject(x).getString("service_id");
                    String estimation = listCustomer.getJSONObject(x).getString("estimation");
                    String ticketId = listCustomer.getJSONObject(x).getString("ticket_id");
                    String dateCreated = listCustomer.getJSONObject(x).getString("dateCreated");

                    Timestamp dateCtd = Timestamp.valueOf(dateCreated);
                    msg = "";
                    ts.setPhone(phoneNumber);
                    ts.setServiceNo(serviceNumber);
                    ts.setServiceId(serviceId);
                    ts.setEstimation(estimation);
                    ts.setTicketId(ticketId);
                    ts.setDateCreated(dateCtd);
                    ts.setSourceTicket("GAMAS");
                    swa.sendNotify(ts);
                    
                    insertRelatedRecordDao.updateStatusSendWANonActive(ts);
                }
            }

            listCustomer = null;

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            listService = null;
            insertRelatedRecordDao = null;
            daoTicket = null;
            swa = null;
            mpd = null;
            masterParam = null;
            listPhoneNumber = null;
            objPhoneNumber = null;
            ts = null;
            _RESTAPI = null;
            listCustomer = null;
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
//        return AppUtil.readPluginResource(getClassName(), "/properties/settingWorkOrder.json", null, true, null);
        return null;
    }

}
