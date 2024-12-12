/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.UpdateLapulDao;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;

/**
 *
 * @author suena
 */
/*
Buat ngetest :
[POST] https://dev-joget-incident-ticketing-service-joget-dev.apps.mypaas.telkom.co.id/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.UpdateLapul/service?id_ticket=IN001385&lapul=10
 */
public class UpdateLapul extends Element implements PluginWebSupport {

    /*deklarasi variable global*/
    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Update Lapul";

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
        return pluginName;
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JSONObject mainObj = new JSONObject();
        JSONObject jsonObj;

        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

        String method = request.getMethod();
        Ticket t;
        UpdateLapulDao dao;
        try {
            /*Cek Hanya User Yang Sudah Login Bisa Mengakses Ini*/
            if (!workflowUserManager.isCurrentUserAnonymous()) {

                if (request.getParameterMap().containsKey("id_ticket")) {
                    t = new Ticket();
                    dao = new UpdateLapulDao();
                    t.setIdTicket(request.getParameter("id_ticket"));
                    t.setLapul(request.getParameter("lapul"));
                    boolean status = false;
                    status = dao.UpdateLapul(t);

                    if (status) {
                        jsonObj = new JSONObject();
                        jsonObj.put("id", t.getId());
                        jsonObj.put("id_ticket", t.getIdTicket());
                        jsonObj.put("lapul", t.getLapul());
                        mainObj.put("data", jsonObj);
                        mainObj.put("status", true);
                        mainObj.put("message", "OK");
                        mainObj.put("errors", "");
                        mainObj.write(response.getWriter());
                    } else {
                        response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Failed update data");
                    }

                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameter");
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR");
        } finally {
            t = null;
            dao = null;
            jsonObj = null;
        }

    }

}
