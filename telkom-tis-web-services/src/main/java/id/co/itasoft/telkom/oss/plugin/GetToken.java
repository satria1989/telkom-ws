/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.JWebToken;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
 *
 * @author asani
 */
public class GetToken extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Get TOKEN API";

    @Override
    public String renderTemplate(FormData fd, Map map) {
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
        return null;
    }

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

        if (!workflowUserManager.isCurrentUserAnonymous()) {
            try {
                LocalDateTime ldt = LocalDateTime.now(ZoneId.of("GMT")).plusSeconds(10);
//                long exp = LocalDateTime.now(ZoneId.of("GMT")).getSecond();

                String user = workflowUserManager.getCurrentUsername();
//                String tokenString = "{\"sub\":\"1234\",\"aud\":[\"+" + user + "\"],\"exp\":" + ldt.toEpochSecond(ZoneOffset.UTC) + "}";

                JSONObject json = new JSONObject();
                JSONObject jsonData = new JSONObject();
                JSONArray arr = new JSONArray();
                arr.put(user);
                json.put("sub", "1234");
                json.put("aud", arr);
                json.put("exp", ldt.toEpochSecond(ZoneOffset.UTC));
                json.put("data", jsonData);

                JSONObject payload = json;
                String token = new JWebToken(payload).toString();

                JSONObject mainObj = new JSONObject();
                JSONObject jsonObj;
                jsonObj = new JSONObject();
                mainObj.put("token", token);
                mainObj.write(res.getWriter());
                res.setStatus(HttpServletResponse.SC_OK);
            } catch (JSONException ex) {
                LogUtil.error(getClassName(), ex, "MESSAGE : " + ex.getMessage());
                res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Error logic");
            }
        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "not Autorization");
        }

    }

}
