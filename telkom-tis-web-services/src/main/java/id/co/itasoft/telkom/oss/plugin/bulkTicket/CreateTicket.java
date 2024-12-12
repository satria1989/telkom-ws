package id.co.itasoft.telkom.oss.plugin.bulkTicket;

import id.co.itasoft.telkom.oss.plugin.TicketAutomationV4;
import id.co.itasoft.telkom.oss.plugin.dao.LogBulkTicketDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.StringManipulation;
import id.co.itasoft.telkom.oss.plugin.jwt.JWTUtil;
import id.co.itasoft.telkom.oss.plugin.model.LogBulkTicket;
import id.co.itasoft.telkom.oss.plugin.model.TicketAutomationModel;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class CreateTicket extends Element implements PluginWebSupport {

    TicketAutomationModel automationModel = new TicketAutomationModel();
    TicketTemplate ticketTemplate = new TicketTemplate();
    StringManipulation stringManipulation = new StringManipulation();
    TicketAutomationV4 ticketAutomationV4 = new TicketAutomationV4();
    LogInfo logInfo = new LogInfo();


    private String pluginName = "Telkom New OSS - Ticket Incident Services - Creaete Ticket (Bulk)";
    private String version = "1.0.0";
    private String description = "Create Bulk Tickets Based on Template Ticket and Service ID List in Excel";

    @Override
    public String renderTemplate(FormData formData, Map map) {
        return "";
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        outerLabel:
        try {
            logInfo.Log(getClassName(), "looogggg");

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

            if (workflowUserManager.isCurrentUserAnonymous()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
                break outerLabel;
            }

            if (!request.getMethod().equalsIgnoreCase("POST")) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                break outerLabel;
            }

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.replace("Bearer ", "");
                //                String username = JWTUtil.extractUsername(token);
                String username = workflowUserManager.getCurrentUsername();

                if (!JWTUtil.validateToken(token, username)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                    break outerLabel;
                }

                JSONObject payload = JWTUtil.extractPayload(token);

                if (!payload.has("id_template") || !payload.has("service_number")) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Bad Request\"}");
                    break outerLabel;
                }

                String idTemplate = stringManipulation.getNonNullTrimmed(payload.getString("id_template"));
                String serviceNumber = stringManipulation.getNonNullTrimmed(payload.getString("service_number"));
                String filename = stringManipulation.getNonNullTrimmed(payload.getString("filename"));

                automationModel = ticketTemplate.getDetailTicket(idTemplate);
                String serviceId = "ABC_" + serviceNumber + "_" + automationModel.getService_type();
                automationModel.setService_id(serviceId);
                automationModel.setSource_ticket("PROACTIVE");
                automationModel.setChannel("81");

                JSONObject result = ticketAutomationV4.createTicket(automationModel, "bulk_ticket");
                logInfo.Log(this.getClass().getName(), result.toString());
                if (result.has("error")) {
                    result.put("service_number", serviceNumber);
                }
                String ticketId = "";
                if (result.has("data")) {
                    ticketId = result.getJSONObject("data").getString("ticket_id");
                }

                //insert to logbulk
                LogBulkTicket logBulkTicket = new LogBulkTicket();
                LogBulkTicketDao logBulkTicketDao = new LogBulkTicketDao();
                logBulkTicket.setServiceId(serviceNumber);
                logBulkTicket.setIdTemplate(idTemplate);
                logBulkTicket.setTicketId(stringManipulation.getNonNullTrimmed(ticketId));
                logBulkTicket.setResponseAPI(result.toString());
                logBulkTicket.setFilename(filename);
                logBulkTicketDao.insertLogBulk(logBulkTicket);

                logInfo.Log(getClassName(), "Payload :" + result.toString());
                result.write(response.getWriter());

            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Authorization header missing or invalid\"}");
            }

        } catch (Exception ex) {
            logInfo.Error(getClassName(), ex.getMessage(), ex);
        }


    }

    @Override
    public String getLabel() {
        return pluginName;
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }
}
