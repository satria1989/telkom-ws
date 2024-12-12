/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GetMasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * UNUSED
 * @author asani
 */
public class GetSchedulingAssigment extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Get SchedulingAssigment";

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
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        GetMasterParamDao getMasterParamDao;
        ApiConfig apiConfig;
        CallRestAPI CRA;
        JSONObject json;
        JSONArray jsonArr;
        try {
            getMasterParamDao = new GetMasterParamDao();
            apiConfig = new ApiConfig();
            CRA = new CallRestAPI();
            json = new JSONObject();
            jsonArr = new JSONArray();

//            GET list_appointment_schedule
            apiConfig = getMasterParamDao.getUrl("list_appointment_schedule");
            HashMap<String, String> params = new HashMap<String, String>();

            String ticketId = req.getParameter("ticket_id");

            params.put("externalID", ticketId);

            String reqAppoinmentSchedl = CRA.sendGet(apiConfig, params);
            JSONObject resObject;
            Object obj = new JSONTokener(reqAppoinmentSchedl).nextValue();
            resObject = (JSONObject) obj;
            JSONArray arrSchedule = this.RebuildJson(resObject);
            // TO ARRAY DATA 

            // GET list_fullbookedAssign
            apiConfig = getMasterParamDao.getUrl("list_fullbookedAssign");
            params = new HashMap<String, String>();
            params.put("ticketID", ticketId);

            String reqFullBooked = CRA.sendGet(apiConfig, params);
            obj = new JSONTokener(reqFullBooked).nextValue();
            resObject = (JSONObject) obj;
            JSONArray arrFullBooked = this.RebuildJson(resObject);

            //merge json array 
            json = this.mergeJSONArry(arrSchedule, arrFullBooked);
            json.write(res.getWriter());

        } catch (Exception ex) {
//            LogUtil.info(getClassName(), "ERROR GET SCHEDULING ASSIGMENT :" + ex.getMessage());
        } finally {
            getMasterParamDao = null;
            apiConfig = null;
            CRA = null;
            json = null;
            jsonArr = null;
        }

    }

    private JSONArray RebuildJson(JSONObject json) throws JSONException {
        JSONObject jSONObject;
        JSONArray _arrJSON = new JSONArray();
        JSONArray _arrJSONRES = new JSONArray();

        String data = json.getString("data");
        int total = json.getInt("total");
        _arrJSON = (JSONArray) new JSONTokener(data).nextValue();
        if (total > 0) {
            for (int i = 0; i < total; i++) {
                JSONObject obj = _arrJSON.getJSONObject(i);
                String id = (!obj.has("id")) ? "" : obj.getString("id");
                String scheduleType = (!obj.has("scheduleType")) ? "" : obj.getString("scheduleType");
                String ticketID = "";
                if (obj.has("ticketID")) {
                    ticketID = obj.getString("ticketID");
                }
                if (obj.has("externalID")) {
                    ticketID = obj.getString("externalID");
                }

                String bookingDate = (!obj.has("bookingDate")) ? "" : obj.getString("bookingDate");
                String crew = (!obj.has("crew")) ? "" : obj.getString("crew");
                String dateModified = (!obj.has("dateModified")) ? "" : obj.getString("dateModified");
                String slot = (!obj.has("slot")) ? "" : obj.getString("slot");
                String status = (!obj.has("status")) ? "" : obj.getString("status");
                String sto = (!obj.has("sto")) ? "" : obj.getString("sto");
                String technicianID = (!obj.has("technicianID")) ? "" : obj.getString("technicianID");
                String wonum = (!obj.has("wonum")) ? "" : obj.getString("wonum");
                String partnerID = (!obj.has("partnerID")) ? "" : obj.getString("partnerID");
                String rk = (!obj.has("rk")) ? "" : obj.getString("rk");

                jSONObject = new JSONObject();
                jSONObject.put("id", id);
                jSONObject.put("scheduleType", scheduleType);
                jSONObject.put("ticketID", ticketID);
                jSONObject.put("bookingDate", bookingDate);
                jSONObject.put("crew", crew);
                jSONObject.put("dateModified", dateModified);
                jSONObject.put("slot", slot);
                jSONObject.put("status", status);
                jSONObject.put("sto", sto);
                jSONObject.put("technicianID", technicianID);
                jSONObject.put("wonum", wonum);
                jSONObject.put("partnerID", partnerID);
                jSONObject.put("rk", rk);
                
                _arrJSON.put(jSONObject);
                
            }
            jSONObject = null;
        }

        return _arrJSON;
    }

    private JSONObject mergeJSONArry(JSONArray arr1, JSONArray arr2) throws JSONException {
        JSONObject json = new JSONObject();
        int x = 0;

        for (int i = 0; i < arr1.length(); i++) {
            json.put("data", arr1.getJSONObject(i));
            x++;
        }
        for (int i = 0; i < arr2.length(); i++) {
            json.put("data", arr2.getJSONObject(i));
            x++;
        }
        json.put("total", x);
        json.put("size", x);

        return json;
    }
}
