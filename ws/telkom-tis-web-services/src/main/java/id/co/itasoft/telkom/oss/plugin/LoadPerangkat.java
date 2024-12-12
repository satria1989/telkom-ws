/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.LoadPerangkatDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Tree;
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
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;



/**
 *
 * @author itasoft
 */
public class LoadPerangkat extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Get Perangkat Tree View";
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

        JSONObject mainObj = new JSONObject();
        JSONObject jsonObj;

        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

        try {
            if (!workflowUserManager.isCurrentUserAnonymous()) {
                String id = "";

                if (req.getParameterMap().containsKey("id")) {
                    id = req.getParameter("id");
                    id = "#".equalsIgnoreCase(id) ? "" : id;
                }

                LoadPerangkatDao dao = new LoadPerangkatDao();
                List<Tree> list = new ArrayList<Tree>();
                list = dao.getPerangkat(id);
                JSONArray jsonArray = new JSONArray();
                JSONObject jObj;
                for (Tree s : list) {
                    jObj = new JSONObject();
                    jObj.put("id", s.getClassificationCode());
                    jObj.put("text", s.getClassificationCode() + " - " + s.getDescription());
                    jObj.put("children", s.isHasChildren());
                    jObj.put("classification_code", s.getClassificationCode());
                    jObj.put("description", s.getDescription());
                    jObj.put("classtructure_id", s.getClassstructureId());

                    jsonArray.put(jObj);
                }

                jsonArray.write(res.getWriter());
            } else {
                mainObj = new JSONObject();
                JSONArray jsonArr;
                JSONObject jObj;
                jObj = new JSONObject();
                mainObj.put("code", "401");
                mainObj.put("message", "Invalid Authentication");
                mainObj.write(res.getWriter());
            }
        } catch (Exception ex) {
          info.Log(getClassName(), ex.getMessage());
        }
    }

}
