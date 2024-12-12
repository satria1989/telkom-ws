/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.TicketWorkLogsSaveDao;
import id.co.itasoft.telkom.oss.plugin.model.TicketWorkLogs;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author mtaup
 */
public class GetWorkLogs extends DefaultApplicationPlugin implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Get Wrok Logs";

    @Override
    public Object execute(Map map) {
        return null;
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
        return null;
    }

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String recordKey = req.getParameter("recordkey");
            String apiKey = req.getHeader("api_key");
            String apiId = req.getHeader("api_id");

            TicketWorkLogsSaveDao twDao = new TicketWorkLogsSaveDao();
//            LogUtil.info(this.getClass().getName(), "ticketId : "+recordKey);
            List<TicketWorkLogs> tw = twDao.getDataWorklogs(recordKey);
//            LogUtil.info(this.getClass().getName(), "tw : "+tw.size());
            JSONObject mainObj = new JSONObject();
            JSONObject childObj = null;
            JSONArray arr = new JSONArray();
            mainObj.put("total", tw.size());
            mainObj.put("size", tw.size());
            mainObj.put("data", arr);
            for (TicketWorkLogs ticketWorkLogs : tw) {
                childObj = new JSONObject();
                childObj.put("summary", ticketWorkLogs.getId());
                childObj.put("createdByName", ticketWorkLogs.getId());
                childObj.put("gaul", ticketWorkLogs.getId());
                childObj.put("attachment_file", ticketWorkLogs.getId());
                childObj.put("clientviewable", ticketWorkLogs.getId());
                childObj.put("ownergroup", ticketWorkLogs.getId());
                childObj.put("lapul", ticketWorkLogs.getId());
                childObj.put("orgid", ticketWorkLogs.getId());
                childObj.put("parentid", ticketWorkLogs.getId());
                childObj.put("id_ticket", ticketWorkLogs.getId());
                childObj.put("assignment_id", ticketWorkLogs.getId());
                childObj.put("log_type", ticketWorkLogs.getId());
                childObj.put("dateCreated", ticketWorkLogs.getId());
                childObj.put("modifiedByName", ticketWorkLogs.getId());
                childObj.put("recordkey", ticketWorkLogs.getId());
                childObj.put("siteid", ticketWorkLogs.getId());
                childObj.put("modifiedBy", ticketWorkLogs.getId());
                childObj.put("worklogid", ticketWorkLogs.getId());
                childObj.put("created_date", ticketWorkLogs.getId());
                childObj.put("detail", ticketWorkLogs.getId());
                childObj.put("id", ticketWorkLogs.getId());
                childObj.put("anywhererrefid", ticketWorkLogs.getId());
                childObj.put("class", ticketWorkLogs.getId());
                arr.put(childObj);
            }
        } catch (Exception ex) {
            Logger.getLogger(GetWorkLogs.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
