/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GetMasterParamDao;
import id.co.itasoft.telkom.oss.plugin.model.Param;
import java.io.IOException;
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
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author suena
 */
/*
Buat ngetest :
https://dev-joget-incident-ticketing-service-joget-dev.apps.mypaas.telkom.co.id/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.GetMaterParam/service?param_code=1
 */
public class GetMasterParam extends Element implements PluginWebSupport {

    /*deklarasi variable global*/
    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Master Param";

    @Override
    public String renderTemplate(FormData fd, Map map) {
        return ""; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
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
        return pluginName;
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
//    @CrossOrigin(origins = "https://oss-incident.telkom.co.id")
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        CheckOrigin checkOrigin = null;
        GetMasterParamDao dao = new GetMasterParamDao();
        JSONArray resJsonArray = new JSONArray();
        JSONObject resJson = new JSONObject();
        Param r = new Param();

        try {
            checkOrigin = new CheckOrigin();
            String origin = request.getHeader("Origin");
            String ACAO = request.getHeader("Access-Control-Allow-Origin");

            if (origin == null) {
                origin = ACAO;
            }
            boolean allowOrigin = checkOrigin.checkingOrigin(origin, response);

            if (allowOrigin) {

                WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

                if (!workflowUserManager.isCurrentUserAnonymous()) {
                    /*INPUT ini buat nampung request*/
                    String param_code = (request.getParameter("param_code") != null) ? request.getParameter("param_code") : "";
                    String param_name = (request.getParameter("param_name") != null) ? request.getParameter("param_name") : "";

                    /*PROCES*/
                    String masterParam = dao.GetMasterParam(param_code, param_name);//
                    Object json = new JSONTokener(masterParam).nextValue();
                    resJsonArray = (JSONArray) json;

                    resJson.put("total", resJsonArray.length());
                    resJson.put("size", resJsonArray.length());
                    resJson.put("data", resJsonArray);

                    resJson.write(response.getWriter());
                    json = null;

                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
                }

            } else {
                // Throw 403 status OR send default allow
                response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Not Acceptable");
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            LogUtil.error(this.getClassName(), ex, "Error : " + ex.getMessage());
        } finally {
            dao = null;
            r = null;
            resJsonArray = null;
            checkOrigin = null;
        }

    }
}
