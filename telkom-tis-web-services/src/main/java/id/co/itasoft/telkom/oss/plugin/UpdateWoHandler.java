/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GetMasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateWoHandlerDao;
import id.co.itasoft.telkom.oss.plugin.dao.WorkOrderDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import id.co.itasoft.telkom.oss.plugin.model.WorkOrder;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author itasoft
 */
public class UpdateWoHandler extends Element implements PluginWebSupport {

    public static String pluginName
            = "Telkom New OSS - Ticket Incident Services - UPDATE WORKORDER HANDLER PLUGIN";

    LogInfo info = new LogInfo();

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

    ApiConfig _setAPI;
    WorkOrder wo;
    WorkOrderDao woDao;
    GetMasterParamDao getMasterParamDao;
    UpdateWoHandlerDao dao;
    RESTAPI _RESTAPI;
    RequestBody body;
    JSONObject dataObjParam;
    String apiCallUpdate;

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        /**
         * 1. insert to db 2. send to asset wo
         */
        JSONObject json = new JSONObject();
        JSONObject resObject;
        JSONArray resJsonArray;

        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil
                .getApplicationContext()
                .getBean("workflowUserManager");

        String param = req.getParameter("request");
        Collection<String> ROLEUSERCollection = workflowUserManager.getCurrentRoles();

        outerLabel:
        try {

            if (workflowUserManager.isCurrentUserAnonymous()) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
                break outerLabel;
            }

            _setAPI = new ApiConfig();
            wo = new WorkOrder();
            woDao = new WorkOrderDao();
            getMasterParamDao = new GetMasterParamDao();
            dao = new UpdateWoHandlerDao();
            _RESTAPI = new RESTAPI();
            dataObjParam = new JSONObject(param);
            LoadTicketDao loadTicketDao = new LoadTicketDao();

            // BOOKING ID LAST
            String bookingIDLast = (dataObjParam.getString("bookingIDLast") == null)
                    ? "" : dataObjParam.getString("bookingIDLast");
            String status = dataObjParam.getString("status");
            String actualSoliton = (!dataObjParam.getString("actual_solution").equals(""))
                    ? dataObjParam.getString("actual_solution") : "";
            String actualSolutionDesc = (!dataObjParam.getString("actual_solution_desc").equals(""))
                    ? dataObjParam.getString("actual_solution_desc") : "";
            String ticketId = dataObjParam.getString("id_ticket");
            String ticketStatus = dataObjParam.getString("ticketStatus");
            String username = workflowUserManager.getCurrentUsername();
            String workzone = (!dataObjParam.getString("workzone").equals(""))
                    ? dataObjParam.getString("workzone") : "";
            String incidentDomain = (!dataObjParam.getString("incident_domain").equals(""))
                    ? dataObjParam.getString("incident_domain") : "";
            String jenisByod = (!dataObjParam.getString("jenis_byod").equals(""))
                    ? dataObjParam.getString("jenis_byod") : "";
            String rk_information = (!dataObjParam.getString("rk_information").equals(""))
                    ? dataObjParam.getString("rk_information") : "";
            String customer_segment = (dataObjParam.has("customer_segment"))
                    ? dataObjParam.getString("customer_segment") : "";
            String mitraGamas = (dataObjParam.has("mitraGamas"))
                    ? dataObjParam.getString("mitraGamas") : "";
            String mitraGamasDesc = (dataObjParam.has("mitraGamasDesc"))
                    ? dataObjParam.getString("mitraGamasDesc") : "";

            customer_segment = (!customer_segment.equals(""))
                    ? customer_segment : "";

            if (!"RCIND220".equalsIgnoreCase(actualSoliton)
                    && jenisByod.equalsIgnoreCase("BYOD")) {
                jenisByod = "";
            }

            TicketStatus getStatus = loadTicketDao.LoadTicketByIdTicket(ticketId);

            json.put("wonum", dataObjParam.getString("wonum"));
            json.put("changeBy", username);
            json.put("sto", workzone);
            json.put("jenis_byod", jenisByod);
            json.put("customerSegment", customer_segment);
            json.put("ticket_id_gamas", getStatus.getTicketIdGamas());

            if (!ticketStatus.equals(getStatus.getStatusCurrent())){
                res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Status is different");
                break outerLabel;
            }
            /**
             * CHECK IF ASSIGNED ALREADY EXIST OR CHECK STATUS ON ELSE.
             */
            if (status.equals("ASSIGNED") && !"".equals(bookingIDLast)) {
                // TIDAK DIGUNAKAN
                _setAPI = getMasterParamDao.getUrl("relaseAndUpdateWo");
                json.put("ticket_number", dataObjParam.getString("id_ticket"));
                json.put("booking_id", dataObjParam.getString("bookingID"));

            } else {
                _setAPI = getMasterParamDao.getUrl("updateWorkOrder");
                json.put("action", status);
                json.put("status", status);

                if ("COMPLETED".equalsIgnoreCase(status)) {
                    json.put("actual_solution", actualSoliton);
                    json.put("incident_domain", incidentDomain);
                    json.put("externalId", ticketId);
                    Iterator<String> iterator = ROLEUSERCollection.iterator();

                    // while loop
                    while (iterator.hasNext()) {
                        String ROLEUSER = iterator.next();

                        if (ROLEUSER.equalsIgnoreCase("ROLE_ADMIN")
                                || ROLEUSER.equalsIgnoreCase("ROLE_SYSADMIN")) {
                            json.put("completedBy", "COMPLETE by DIT");
                        }

                        if (ROLEUSER.equalsIgnoreCase("ROLE_USER")) {
                            json.put("completedBy", "COMPLETE by HD");
                        }
                    }
                }

                if ("ASSIGNED".equalsIgnoreCase(status)) {
                    json.put("bookingId", dataObjParam.getString("bookingID"));
                    json.put("externalId", ticketId);
                    json.put("rk", rk_information);
                    json.put("scheduleType", "ASSURANCE");
                }
            }

            if ("COMPLETED".equalsIgnoreCase(status) || "ASSIGNED".equalsIgnoreCase(status)) {
                woDao.UpdateActualSolution(
                        actualSoliton,
                        actualSolutionDesc,
                        incidentDomain,
                        mitraGamas,
                        mitraGamasDesc,
                        ticketId
                );
            }

            body = RequestBody.create(_RESTAPI.JSON, json.toString()); // PARAM JSON TO BODY

            Request request
                    = new Request.Builder()
                            .url(_setAPI.getUrl())
                            .addHeader("api_key", _setAPI.getApiKey()) // add request headers
                            .addHeader("api_id", _setAPI.getApiId())
                            .addHeader("token", _setAPI.getApiToken())
                            .addHeader("Origin", "https://oss-incident.telkom.co.id")
                            .post(body)
                            .build();

            JSONObject REQ_WO = _RESTAPI.CALLAPIHANDLERWO(request);
            info.Log("response api", "UPDATE WO :" + REQ_WO.toString());
            boolean statusAPI = REQ_WO.getBoolean("status");
            int responseCode = REQ_WO.getInt("status_code");

            _RESTAPI.SENDHISTORY(
                    ticketId,
                    "UPDATE_WORKORDER",
                    _setAPI.getUrl(),
                    "POST",
                    json,
                    REQ_WO,
                    responseCode
            );

            if (statusAPI) {
                String msgAPI = (REQ_WO.has("msg")) ? REQ_WO.getString("msg") : "";
                String statusResWO = dataObjParam.getString("status");
                if ("COMPLETED".equalsIgnoreCase(statusResWO)) {
                    boolean updateLastState = woDao.UpdateLastState(ticketId);
                }
                if (msgAPI.length() > 0) {
                    info.Log(getClassName(), "MASUK MSG");
                    Object resJson = new JSONTokener(msgAPI).nextValue();
                    res.setStatus(responseCode);
                    if (resJson instanceof JSONObject) {
                        resObject = (JSONObject) resJson;
                        resObject.write(res.getWriter());
                    } else if (resJson instanceof JSONArray) {
                        resJsonArray = (JSONArray) resJson;
                        resJsonArray.write(res.getWriter());
                    } else {
                        info.Log(getClassName(), "GTO RESPONSE");
                        res.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT, "TIMEOUT");
                    }
                    resJson = null;
                } else {
                    JSONObject jsonR = new JSONObject();
                    jsonR.put("msg", "UPDATE WO SUCCESS");
                    jsonR.write(res.getWriter());
                }

            } else {
                info.Log(getClassName(), "ELSE response");
                String msgError = REQ_WO.getString("response_message");
                res.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, msgError);
            }

            apiCallUpdate = null;

        } catch (Exception ex) {
            info.Error(getClassName(), ex.getMessage(), ex);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        } finally {
            json = null;
            _setAPI = null;
            wo = null;
            woDao = null;
            getMasterParamDao = null;
            dao = null;
            _RESTAPI = null;
            body = null;
            param = null;
            dataObjParam = null;
            apiCallUpdate = null;
        }
    }
}
