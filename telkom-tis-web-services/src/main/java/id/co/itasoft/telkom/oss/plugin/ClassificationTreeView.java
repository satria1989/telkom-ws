/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.ClassificationTreeViewDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Symptom;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author tarkiman
 */
///jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.ClassificationTreeView/service?hierarchy_type=SYMPTOM&id=#
public class ClassificationTreeView extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Classification Tree View";

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

        JSONObject mainObj = new JSONObject();
        LogInfo info = new LogInfo();

        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

        /*Cek Hanya User Yang Sudah Login Bisa Mengakses Ini*/
        if (!workflowUserManager.isCurrentUserAnonymous()) {

            try {

                String id = "";
                String hierarchyType = "SYMPTOM";
                String description = "";

                if (request.getParameterMap().containsKey("id")) {
                    id = request.getParameter("id");
                    id = "#".equalsIgnoreCase(id) ? "" : id;
                }

                if (request.getParameterMap().containsKey("hierarchy_type")) {
                    hierarchyType = request.getParameter("hierarchy_type");
                }

                if (request.getParameterMap().containsKey("description")) {
                    description = request.getParameter("description");
                }

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("hierarchy_type", hierarchyType);
                params.put("classification_code", id);
                params.put("description", description);

                ClassificationTreeViewDao dao = new ClassificationTreeViewDao();

                List<Symptom> list = new ArrayList<Symptom>();
                list = dao.getNodes(params);

                JSONArray jsonArray = new JSONArray();
                JSONObject jObj;
                for (Symptom s : list) {
                    jObj = new JSONObject();
                    jObj.put("id", s.getClassificationCode());
                    jObj.put("text", s.getDescription());
                    jObj.put("children", s.isHasChildren());

                    jObj.put("classification_code", s.getClassificationCode());
                    jObj.put("description", s.getDescription());
                    jObj.put("classification_type", s.getClassificationType());
                    jObj.put("auto_backend", s.getAutoBackend());
                    jObj.put("solution", s.getSolution());
                    jObj.put("final_check", s.getFinalCheck());

                    jsonArray.put(jObj);
                }
                list.clear();
                dao = null;
                jObj = null;
                params.clear();
                jsonArray.write(response.getWriter());
            } catch (JSONException ex) {
               info.Error(getClass().getName(), ex.getMessage(), ex);
            } 

        } else {

            //handling error jika user yang merequest belum login
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
        }

    }

}
