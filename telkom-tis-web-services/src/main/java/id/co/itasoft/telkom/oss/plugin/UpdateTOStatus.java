/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateTicketOwnerDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.net.URL;
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
 * @author asani
 */
public class UpdateTOStatus extends Element implements PluginWebSupport {

    public String pluginName
            = "Telkom New OSS - Ticket Incident Services - UpdateTOStatus";

    LoadTicketDao daoTicket;
    TicketStatus ticketStatus, tss;
    URL url;
    CheckOrigin checkOrigin;
    UpdateTicketOwnerDao updateTicketOwnerDao;
    LogInfo info = new LogInfo();

    public UpdateTOStatus() {
        updateTicketOwnerDao = new UpdateTicketOwnerDao();
        daoTicket = new LoadTicketDao();
        ticketStatus = new TicketStatus();
        checkOrigin = new CheckOrigin();
        tss = new TicketStatus();
    }

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

        try {
            if (!workflowUserManager.isCurrentUserAnonymous()) {
                String referrer = req.getHeader("referer");
                String origin = req.getHeader("Origin");
                String ACAO = req.getHeader("Access-Control-Allow-Origin");

                url = new URL(referrer);
                String queryStr = url.getQuery();
                if (origin == null) {
                    origin = ACAO;
                }
                boolean allowOrigin = checkOrigin.checkingOrigin(origin, res); // CHECK ORIGIN ACCESS

                if (allowOrigin) {

                    String[] params = queryStr.split("&");
                    JSONObject paramReferer = new JSONObject();
                    for (String param : params) {
                        String key = param.substring(0, param.indexOf('='));
                        String val = param.substring(param.indexOf('=') + 1);
                        paramReferer.put(key, val);
                    }

                    if (paramReferer.has("id")) {
                        String PARENT_ID = paramReferer.getString("id");
//                        LogUtil.info(getClassName(), "PARENT TICKET :: "+ PARENT_ID);
                        ticketStatus = daoTicket.LoadTicketByParentId(PARENT_ID);
                        String owner = workflowUserManager.getCurrentUsername();
                        String employeeCode = daoTicket.getEmployeeCode(owner);

                        Ticket ticket = new Ticket();
                        String action_status = ticketStatus.getActionStatus();
                        String sts = "";
                        String TICKETID = ticketStatus.getTicketId();
                        
                        String OWNERGROUP = ticketStatus.getAssignedOwnerGroup();
                        String ActionStatus = ticketStatus.getActionStatus();
                        if ("APPROVED".equalsIgnoreCase(ticketStatus.getPendingStatus())) {
                            ticketStatus.setStatus("PENDING");
                        }
                        if ("REQUEST_PENDING".equalsIgnoreCase(action_status) && !"PENDING".equalsIgnoreCase(ticketStatus.getStatus())) {
                            sts = "REQUEST_PENDING_" + ticketStatus.getStatus();
                        } else {
                            sts = ticketStatus.getStatus();
                        }

                        ticket.setOwner(employeeCode);
                        ticket.setTicketId(TICKETID);

                        boolean updateOwner = updateTicketOwnerDao.UpdateOwner(ticket);

                        ticketStatus = new TicketStatus();
                        tss.setTicketId(TICKETID);
                        tss.setStatus(sts);
                        tss.setChangeBy(employeeCode);
                        tss.setMemo("Take Ownership");
                        tss.setOwner(employeeCode);
                        tss.setOwnerGroup(OWNERGROUP);
                        tss.setAssignedOwnerGroup(OWNERGROUP);
                        tss.setActionStatus(ActionStatus);
                        tss.setStatusTracking(TICKETID);
                        
                    updateTicketOwnerDao.UpdateTimeonLastTicketSta(tss);
                        tss.setStatusTracking("false");
                        updateTicketOwnerDao.insertTicketStatus(tss);
                        
                        JSONObject response = new JSONObject();
                        response.put("res", "OK");
                        
                        response.write(res.getWriter());
//                        res.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Id is requeired");
                    }

                } else {
                    res.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Domain not Allowed.");
                }

            } else {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
            }

        } catch (Exception ex) {
          res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
          info.Log(getClassName(), ex.getMessage());
        } finally {
            updateTicketOwnerDao = null;
            daoTicket = null;
            ticketStatus = null;
            checkOrigin = null;
            tss = null;
        }
    }

}
