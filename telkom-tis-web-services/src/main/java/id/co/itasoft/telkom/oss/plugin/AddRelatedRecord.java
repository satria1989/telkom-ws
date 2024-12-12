/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GamasTicketRelationDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author itasoft
 */
public class AddRelatedRecord extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Add Related Record";

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
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        JSONObject mainObj = new JSONObject();
        RESTAPI restApi = new RESTAPI();

        if (!workflowUserManager.isCurrentUserAnonymous()) {

            String ChildTicketId = req.getParameter("child_ticket_id");
            String GamasticketId = req.getParameter("gamas_ticket_id");
            String recIdChil = req.getParameter("recId_child");
            String og_child = req.getParameter("og_child");
            String status = req.getParameter("status");
            String channel = (req.getParameter("channel") == null ? "" : req.getParameter("channel"));
            DeleteRelatedRecord drr = new DeleteRelatedRecord();
            GamasTicketRelationDao daoGtr = new GamasTicketRelationDao();

            try {
                drr.updateIdTicketGamasOnChildTicket(GamasticketId, "TRUE", ChildTicketId);
                daoGtr.insertWorkLogs(recIdChil, ChildTicketId, og_child, "This Ticket is Related to Gamas " + GamasticketId,"");
                if ("BACKEND".equalsIgnoreCase(status)) {
                    HashMap<String, String> paramCkWo = new HashMap<String, String>();
                    paramCkWo.put("externalID1", ChildTicketId);
                    daoGtr.getStatusWo(paramCkWo, ChildTicketId, recIdChil);
                    paramCkWo.clear();
                }

                if ("50".equals(channel)) {
                    MasterParamDao paramDao = new MasterParamDao();
                    ApiConfig apiConfig = new ApiConfig();
                    Ticket ticket = new Ticket();
                    apiConfig = paramDao.getUrlapi("getActSolAndIncDom");
                    String actsolDesc = daoGtr.getActsolDescription(apiConfig, "ACTSOL", "59796");

                    
                    ticket = daoGtr.getProcessIdTicketWithShk(ChildTicketId);
                    if ("1000003".equals(ticket.getState())) {
                        ApplicationContext ac = AppUtil.getApplicationContext();
                        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
                        daoGtr.updateStatusTikcet("SQMTOCLOSED", "1", "59796", actsolDesc, "Change of Status by Gamas", ticket.getParentId());
                        workflowManager.assignmentForceComplete(ticket.getProcessDefId().replace("#", ":"), ticket.getProcessId(), ticket.getActivityId(), "000000");

                    }
                }
                
                restApi.updateTicketGamasWfmTa(ChildTicketId, GamasticketId);

                mainObj.put("code", "200");
                mainObj.put("message", "Update Data successful");
                mainObj.write(res.getWriter());
            } catch (Exception ex) {
                res.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Update Data failed");
//                        }
            } finally {
                drr = null;
                daoGtr = null;
            }

        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
        }

    }

}
