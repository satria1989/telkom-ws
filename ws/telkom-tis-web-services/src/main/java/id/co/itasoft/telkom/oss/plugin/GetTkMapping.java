/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * UNUSED
 * @author Tarkiman
 */
public class GetTkMapping extends Element implements PluginWebSupport {

    /*
Buat ngetest :
54.179.192.182:8080/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.GetTkMapping/service?ticket_id=IN1234567
     */
    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Update Ticket Status";

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
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        JSONObject mainObj = new JSONObject();
//        JSONArray dataJsonArrObj;
//
//        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
//
//        /*Cek Hanya User Yang Sudah Login Bisa Mengakses Ini*/
//        if (!workflowUserManager.isCurrentUserAnonymous()) {
//
//            try {
//                mainObj.put("createdByName", "Admin Admin");
//                mainObj.put("final_check", "0");
//                mainObj.put("person_owner_group", "DSO ASR INTERNET ENGR");
//                mainObj.put("description", "INTERNET - ALAMAT");
//                mainObj.put("dateModified", "2022-03-04 09:09:22.0");
//                mainObj.put("tk_mapping_id", "2");
//                mainObj.put("customer_segment", "DBS");
//                mainObj.put("product_name", "");
//                mainObj.put("dateCreated", "2022-03-04 09:09:22.0");
//                mainObj.put("modifiedByName", "Admin Admin");
//                mainObj.put("createdBy", "admin");
//                mainObj.put("workzone", "AJS");
//                mainObj.put("modifiedBy", "admin");
//                mainObj.put("classification_id", "S_INTERNET_001");
//                mainObj.put("id", "534e0075-b02a-4bc5-b874-4278351ee971");
//
//                mainObj.write(response.getWriter());
//            } catch (JSONException ex) {
//                LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
//            }
//
//        } else {
//
//            try {
//                dataJsonArrObj = new JSONArray();
//                mainObj.put("data", dataJsonArrObj);
//                mainObj.put("status", false);
//                mainObj.put("message", "you must login first");
//                mainObj.put("errors", "");
//                mainObj.write(response.getWriter());
//            } catch (JSONException ex) {
//                LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
//            }
//        }

    }

}
