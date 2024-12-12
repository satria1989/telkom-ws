/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.JWebToken;
import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateTicketOwnerDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.SysInfo;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *UNUSED
 * @author Tarkiman
 */
public class UpdateTicketOwner extends Element implements PluginWebSupport {

    /*
Buat ngetest :
http://54.179.192.182:8080/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.UpdateTicketOwner/service?
     */
    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Update Ticket Owner";

    LogInfo info = new LogInfo();
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
        String authorizationHeaderValue = request.getHeader("Authorization");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JWebToken incomingToken;
        
//        String idFieldElement = FormUtil.getElementParameterName(element);
        try {
            /*Cek Hanya User Yang Sudah Login Bisa Mengakses    Ini*/
            if (!workflowUserManager.isCurrentUserAnonymous()) {

                if (authorizationHeaderValue != null && authorizationHeaderValue.startsWith("Bearer")) {

                    String tokenD = authorizationHeaderValue.substring(7, authorizationHeaderValue.length());
                    incomingToken = new JWebToken(tokenD);

                    if (incomingToken.isValid()) {
                        final UpdateTicketOwnerDao dao = new UpdateTicketOwnerDao();

                        JSONObject payloadData = incomingToken.getPayloadJSONObject(tokenD);
                        String TICKETID = payloadData.getString("ticket_id");
                        String OWNERGROUP = payloadData.getString("ownergroup");
                        String STATUS = payloadData.getString("status");
                        String OWNER = workflowUserManager.getCurrentUsername();
                        String MEMO = payloadData.getString("memo");

                        Ticket t = new Ticket();
                        t.setOwner(OWNER);
                        t.setTicketId(TICKETID);

                        LoadTicketDao daoTicket = new LoadTicketDao();
                        TicketStatus ts = new TicketStatus();
                        ts = daoTicket.LoadTicketByIdTicket(TICKETID);
                        String pendingStatus = (ts.getPendingStatus() == null) ? "" : ts.getPendingStatus();
                        boolean status = false;
                        String stsFalseRes = "";

                        if ("APPROVED".equalsIgnoreCase(pendingStatus)) {
                            ts.setStatus("PENDING");
                        }

                        status = dao.UpdateOwner(t);

                        //insert ticket status
                        TicketStatus r = new TicketStatus();
                        String action_status = ts.getActionStatus();
                        String sts = "";
                        if ("REQUEST_PENDING".equalsIgnoreCase(action_status) && !"PENDING".equalsIgnoreCase(ts.getStatus())) {
                            sts = "REQUEST_PENDING_" + ts.getStatus();
                        } else {
                            sts = ts.getStatus();
                        }

                        String result = "";

                        Map tempSystemInfo = SysInfo.getSystemProperties();
                        result = InetAddress.getLocalHost().getHostName();
//                        return result;

                        r.setTicketId(TICKETID);
                        r.setStatus(sts);
                        r.setChangeBy(OWNER);
                        r.setMemo(MEMO);
                        r.setOwner(OWNER);
                        r.setOwnerGroup(OWNERGROUP);
                        r.setAssignedOwnerGroup(OWNERGROUP);
                        r.setActionStatus(ts.getActionStatus());

                        if (status) {
                            final TicketStatus tss = r;
                            tss.setStatusTracking(tss.getTicketId());
                            dao.insertTicketStatus(tss);

                            jsonObj = new JSONObject();
                            mainObj.put("message", "allow-access");
                            mainObj.write(response.getWriter());
                            response.setStatus(HttpServletResponse.SC_OK);

                        } else {
                            response.sendError(HttpServletResponse.SC_CONFLICT, "CONFLICT OWNER");
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token Expired");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authenticated");
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "you must login first");
            }
        } catch (Exception ex) {
          info.Log(getClassName(), ex.getMessage());
        } finally {
            
        }

    }

}
