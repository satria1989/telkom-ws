/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.AttachmentTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.CompleteActivityTicketIncidentApiDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketWorkLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.SelectCollections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mtaup
 */
public class AttachmentTicketIncident extends Element implements PluginWebSupport {

    private String pluginName = "Telkom New OSS - Ticket Incident Services - Attachment Ticket Incident";

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

    LogInfo info = new LogInfo();

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String method = req.getMethod();
        if ("post".equalsIgnoreCase(method)) {
            try {
                InsertTicketWorkLogsDao dao = new InsertTicketWorkLogsDao();
                Ticket ticket = new Ticket();
                dao.getApiAttribut();

                //Predefined Headers
                String apiIdDefined = dao.apiId;
                String apiKeyDefined = dao.apiKey;
                String headerApiId = req.getHeader("api_id");
                String headerApiKey = req.getHeader("api_key");

                if (apiIdDefined.equals(headerApiId) && apiKeyDefined.equals(headerApiKey)) {
                    String bodyParam = "";
                    InsertTicketWorkLogs itw = new InsertTicketWorkLogs();
                    CompleteActivityTicketIncidentApiDao cDao = new CompleteActivityTicketIncidentApiDao();
                    AttachmentTicketDao aDao = new AttachmentTicketDao();
                    SelectCollections sc = new SelectCollections();

                    StringBuffer jb = new StringBuffer();
                    String line = null;
                    try {
                        BufferedReader reader = req.getReader();
                        while ((line = reader.readLine()) != null) {
                            jb.append(line);
                        }
                    } catch (Exception e) {
                        info.Error(this.getClassName(), "AddAttachmentTicket", e);
                    }

                    bodyParam = jb.toString();
                    JSONParser parse = new JSONParser();
                    org.json.simple.JSONObject dataObj = (org.json.simple.JSONObject) parse.parse(bodyParam);

                    String ticketId = (dataObj.get("ticket_id") == null ? "" : dataObj.get("ticket_id").toString());
                    String attachment = (dataObj.get("attachment") == null ? "" : dataObj.get("attachment").toString());

                    ticket = cDao.getProcessIdTicket(ticketId);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    Date currentDate = new Date();
                    String formattedDate = dateFormat.format(currentDate);
                    String extention = attachment.split(";")[0].split("/")[1];
                    String fileName = ticketId + "_" + formattedDate;
                    String path = sc.getPathMinio("c_minio_attachment");

                    boolean statusUpload = itw.uploadAttachmentToMinio(attachment, fileName, path+"/");
                    aDao.updateFileNameOnTicket(fileName + "." + extention, ticketId);
                    
                        SimpleDateFormat dateFormatRsp = new SimpleDateFormat("EEE dd MMM HH:mm:ss yyyy");
                        JSONObject obj = new JSONObject();
                        JSONObject successObj = new JSONObject();
                    if (statusUpload) {
                        successObj.put("date", dateFormatRsp.format(currentDate));
                        successObj.put("code", "200");
                        successObj.put("message", "Attachment successfully uploaded.");
                        obj.putOpt("success", successObj);
                        obj.write(res.getWriter());
                    }else{
                        res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "System error, please check the log file");
                    }

                } else {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
                }

            } catch (Exception ex) {
                info.Error(this.getClassName(), "AddAttachmentTicket", ex);
            }
        } else {
            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method not allowed");
        }

    }

}
