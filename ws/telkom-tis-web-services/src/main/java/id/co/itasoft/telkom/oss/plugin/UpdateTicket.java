/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.UpdateTicketDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.sql.Timestamp;
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
 * UNUSED
 * @author Tarkiman
 */
public class UpdateTicket extends Element implements PluginWebSupport {

    /*
Buat ngetest :
http://54.179.192.182:8080/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.UpdateTicket/service?
     */
    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Update Ticket Status";
    LogInfo log = new LogInfo();

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
        JSONArray jsonArr;
        JSONObject jsonObj;

        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

        try {
            /*Cek Hanya User Yang Sudah Login Bisa Mengakses Ini*/
            if (!workflowUserManager.isCurrentUserAnonymous()) {

                if (request.getParameterMap().containsKey("ticket_id")) {
                    TicketStatus r = new TicketStatus();
                    r.setTicketId(request.getParameter("ticket_id"));
                    r.setClasS(request.getParameter("class"));
                    r.setStatus(request.getParameter("status"));
                    r.setChangeBy(request.getParameter("change_by"));
                    r.setChangeDate(request.getParameter("change_date"));
                    r.setMemo(request.getParameter("memo"));
                    r.setSiteId(request.getParameter("site_id"));
                    r.setOrgId(request.getParameter("org_id"));
                    r.setTkStatusId(request.getParameter("tk_status_id"));
                    r.setStatusTracking(request.getParameter("status_tracking"));
                    r.setOwner(request.getParameter("owner"));
                    r.setOwnerGroup(request.getParameter("owner_group"));
                    r.setAssignedOwnerGroup(request.getParameter("assigned_owner_group"));
                    r.setActionStatus(request.getParameter("action_status"));

                    Ticket t = new Ticket();
                    t.setId(request.getParameter("ticket_id"));
                    t.setStatus(request.getParameter("status"));

                    UpdateTicketDao dao = new UpdateTicketDao();

                    //dapatkan GAP waktu terakhir di ticket status                
                    Timestamp lastTimestamp = new Timestamp(System.currentTimeMillis());
                    lastTimestamp = dao.getTicketStatusByTicketID(r.getTicketId());
                    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

                    long diff = currentTimestamp.getTime() - lastTimestamp.getTime();
                    long seconds = diff / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long _minutes = minutes % 60;
                    long _seconds = seconds % 60;
                    String statusTracking = hours + ":" + _minutes + ":" + _seconds;
                    r.setStatusTracking(statusTracking);
              

                    if (true) {

                        try {

                            jsonObj = new JSONObject();
                            jsonObj.put("ticket_id", r.getTicketId());
                            jsonObj.put("class", r.getClasS());
                            jsonObj.put("status", r.getStatus());
                            jsonObj.put("change_by", r.getChangeBy());
                            jsonObj.put("change_date", r.getChangeDate());
                            jsonObj.put("memo", r.getMemo());
                            jsonObj.put("site_id", r.getSiteId());
                            jsonObj.put("org_id", r.getOrgId());
                            jsonObj.put("tk_status_id", r.getTkStatusId());
                            jsonObj.put("status_tracking", r.getStatusTracking());
                            jsonObj.put("owner", r.getOwner());
                            jsonObj.put("owner_group", r.getOwnerGroup());
                            jsonObj.put("assigned_owner_group", r.getAssignedOwnerGroup());

                            mainObj.put("data", jsonObj);
                            mainObj.put("status", true);
                            mainObj.put("message", "OK");
                            mainObj.put("errors", "");
                            mainObj.write(response.getWriter());
                        } catch (JSONException ex) {
                          log.Log(getClassName(), ex.getMessage());
                        }

                    } else {
                        try {
                            jsonArr = new JSONArray();
                            mainObj.put("data", jsonArr);
                            mainObj.put("status", false);
                            mainObj.put("message", "failed insert data");
                            mainObj.put("errors", "");
                            mainObj.write(response.getWriter());
                        } catch (JSONException ex) {
                          log.Log(getClassName(), ex.getMessage());
                        }
                    }

                } else {

                    try {
                        jsonArr = new JSONArray();
                        mainObj.put("data", jsonArr);
                        mainObj.put("status", false);
                        mainObj.put("message", "missing required parameter");
                        mainObj.put("errors", "");
                        mainObj.write(response.getWriter());
                    } catch (JSONException ex) {
                      log.Log(getClassName(), ex.getMessage());
                    }
                }
            } else {

                try {
                    jsonArr = new JSONArray();
                    mainObj.put("data", jsonArr);
                    mainObj.put("status", false);
                    mainObj.put("errors", "");
                    mainObj.put("message", "you must login first");
                    mainObj.write(response.getWriter());
                } catch (JSONException ex) {
                  log.Log(getClassName(), ex.getMessage());
                }
            }
        } catch (Exception ex) {
          log.Log(getClassName(), ex.getMessage());
        }

    }

}
