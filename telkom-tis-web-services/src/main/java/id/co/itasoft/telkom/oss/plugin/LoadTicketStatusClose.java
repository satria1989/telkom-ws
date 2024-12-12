/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketStatusDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Status;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.json.JSONObject;



/**
 *
 * @author Tarkiman
 */
public class LoadTicketStatusClose extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Update Ticket Status";
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
        JSONObject jsonObj;
        JSONObject indicatorJsonObj;
        JSONObject ticketJsonObj;
        LoadTicketStatusDao dao = new LoadTicketStatusDao();
        CheckOrigin checkOrigin = new CheckOrigin();
        try {
            String origin = request.getHeader("Origin");
            String ACAO = request.getHeader("Access-Control-Allow-Origin");
            if (origin == null) {
                origin = ACAO;
            }
            boolean allowOrigin = checkOrigin.checkingOrigin(origin, response);

            if (allowOrigin) {
                WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

                /*Cek Hanya User Yang Sudah Login Bisa Mengakses Ini*/
                if (!workflowUserManager.isCurrentUserAnonymous()) {

                    if (request.getParameterMap().containsKey("ticket_id")) {

                        String ticketId = request.getParameter("ticket_id");

                        String result[] = dao.getCurrentStatusOfTicket(ticketId, "tableRepo");
                        String[] arrayTicketStatus = {"NEW", "DRAFT", "ANALYSIS", "PENDING", "BACKEND", "FINALCHECK", "RESOLVED", "MEDIACARE", "SALAMSIM", "CLOSED"};

                        JSONArray ticketStatusObj = new JSONArray();
                        JSONArray listIndicator = new JSONArray();
                        JSONArray listTicket = new JSONArray();

                        if (!"".equalsIgnoreCase(result[1])) {
                            int indexCurrentStatus = 1;

                            List<Status> listStatus = new ArrayList<>();

                            Status status;

                            int i = 1;
                            for (String s : arrayTicketStatus) {
                                status = new Status();
                                status.setIndex(i);
                                status.setStatusName(s);
                                listStatus.add(status);

                                if (s.equalsIgnoreCase(result[1])) {
                                    indexCurrentStatus = i;
                                }

                                i++;
                            }
                            ticketJsonObj = new JSONObject();
                            for (Status r : listStatus) {

                                indicatorJsonObj = new JSONObject();

                                if (r.getIndex() == indexCurrentStatus) {
                                    indicatorJsonObj.put("id", r.getStatusName().toLowerCase());
                                    indicatorJsonObj.put("status", "running");
                                } else if (r.getIndex() < indexCurrentStatus) {
                                    indicatorJsonObj.put("id", r.getStatusName().toLowerCase());
                                    indicatorJsonObj.put("status", "closed");

                                } else if (r.getIndex() > indexCurrentStatus) {
                                    indicatorJsonObj.put("id", r.getStatusName().toLowerCase());
                                    indicatorJsonObj.put("status", "open");
                                }
                                listIndicator.put(indicatorJsonObj);

                            }
                            listStatus.clear();
                            ticketJsonObj.put("pending_status", result[2]);
                            ticketJsonObj.put("customer_segment", result[3]);
                            ticketJsonObj.put("action_status", result[0]);
                            listTicket.put(ticketJsonObj);

                            mainObj.put("indicator", listIndicator);
                            mainObj.put("ticket", listTicket);
                            mainObj.put("status", true);
                            mainObj.put("message", "OK");
                            mainObj.put("errors", "");
                            mainObj.write(response.getWriter());

                            ticketJsonObj = null;

                        } else {

                            for (String s : arrayTicketStatus) {

                                jsonObj = new JSONObject();
                                if ("NEW".equalsIgnoreCase(s)) {
                                    jsonObj.put("id", s.toLowerCase());
                                    jsonObj.put("status", "running");
                                } else {
                                    jsonObj.put("id", s.toLowerCase());
                                    jsonObj.put("status", "open");
                                }
                                ticketStatusObj.put(jsonObj);
                            }
                            mainObj.put("data", ticketStatusObj);
                            mainObj.put("status", true);
                            mainObj.put("message", "record is not found");
                            mainObj.put("errors", "");
                            mainObj.write(response.getWriter());
                            arrayTicketStatus = null;
                        }

                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing required parameter");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
                }

            } else {
                response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "NO ACCEPTABLE");
            }

        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR");
            info.Log(getClass().getSimpleName(), ex.getMessage());
        } finally {
            jsonObj = null;
            indicatorJsonObj = null;
            dao = null;
            checkOrigin = null;
        }
    }

}
