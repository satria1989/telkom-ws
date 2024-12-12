/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONObject;

/**
 * // UNSUSED
 * @author itasoft
 */
public class CallPluginAssetArea extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - CALL PLUGIN ASSET AREA";
    LogInfo info = new LogInfo();
    
    @Override
    public String renderTemplate(FormData fd, Map map) {
        return pluginName;
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
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
        try {
            JSONObject mainObj;
            String id = "";
            String hierarchyType = "SYMPTOM";

            if (request.getParameterMap().containsKey("id")) {
                id = request.getParameter("id");
                id = "#".equalsIgnoreCase(id) ? "" : id;
            }

            if (request.getParameterMap().containsKey("hierarchy_type")) {
                hierarchyType = request.getParameter("hierarchy_type");
            }
            
            ApiConfig _setAPI = new ApiConfig();
            _setAPI.setUrl("http://dev-joget-asset-area-service-joget-dev.apps.mypaas.telkom.co.id/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.GetClassificationTreeView/service?hierarchy_type="+hierarchyType+"&id="+id);
            HashMap<String, String> params = new HashMap<String, String>();
            CallRestAPI CRA = new CallRestAPI();
            
            String req = CRA.sendGet(_setAPI, params);
            
            mainObj = new JSONObject(req);
            mainObj.write(response.getWriter());
        } catch(Exception ex) {
          info.Error(getClass().getName(), ex.getMessage(), ex);
        }
    }
    
}
