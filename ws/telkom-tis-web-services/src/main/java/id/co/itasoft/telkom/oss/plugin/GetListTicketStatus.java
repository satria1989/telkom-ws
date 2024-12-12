/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GetListTicketStatusDao;
import id.co.itasoft.telkom.oss.plugin.model.TicketHistory;
import java.io.BufferedReader;
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
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author asani
 */
public class GetListTicketStatus extends Element implements PluginWebSupport {

    private static String pluginName = "Telkom New OSS - Get List Ticket Status";

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

        try {
            CheckOrigin checkOrigin = new CheckOrigin();
            String origin = req.getHeader("Origin");
            String ACAO = req.getHeader("Access-Control-Allow-Origin");
            if (origin == null) {
                origin = ACAO;
            }
            boolean allowOrigin = checkOrigin.checkingOrigin(origin, res);

            if (allowOrigin) {
                res.addHeader("Access-Control-Allow-Origin", origin);
                WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

                boolean isPost = "POST".equals(req.getMethod());
                if (isPost) {
                        GetListTicketStatusDao listTicketStatus = new GetListTicketStatusDao();
                        StringBuffer jb = new StringBuffer();
                        String line = null;
                        JSONObject jsonRes = new JSONObject();
                        try {
                            BufferedReader reader = req.getReader();
                            while ((line = reader.readLine()) != null) {
                                jb.append(line);
                            }
                            reader.close();

                            String bodyParam = jb.toString();
                            Object objBodyParam = new JSONTokener(bodyParam).nextValue();
                            JSONObject jsonBodyParam = (JSONObject) objBodyParam;

                            String TICKET_ID = (jsonBodyParam.getString("TICKET_ID") != null) ? jsonBodyParam.getString("TICKET_ID") : "";
                            List<TicketHistory> list = new ArrayList<TicketHistory>();
                            list = listTicketStatus.getTicketHistory(TICKET_ID);

                            JSONObject json;
                            JSONArray arr = new JSONArray();
                            if (!"".equals(TICKET_ID)) {
                                for (TicketHistory th : list) {
                                    json = new JSONObject();
                                    json.put("ticket_id", (th.getC_ticketid() == null) ? "" : th.getC_ticketid());
                                    json.put("ticket_id", (th.getC_ticketid() == null) ? "" : th.getC_ticketid());
                                    json.put("date_created", (th.getDatecreatedStr() == null) ? "" : th.getDatecreatedStr());
                                    json.put("owner", (th.getC_owner() == null) ? "" : th.getC_owner());
                                    json.put("changeby", (th.getC_changeby() == null) ? "" : th.getC_changeby());
                                    json.put("memo", (th.getC_memo() == null) ? "" : th.getC_memo());
                                    json.put("changedate", (th.getC_changedate() == null) ? "" : th.getC_changedate());
                                    json.put("ownergroup", (th.getC_ownergroup() == null) ? "" : th.getC_ownergroup());
                                    json.put("assignedownergroup", (th.getC_assignedownergroup() == null) ? "" : th.getC_assignedownergroup());
                                    json.put("orgid", (th.getC_orgid() == null) ? "" : th.getC_orgid());
                                    json.put("siteid", (th.getC_siteid() == null) ? "" : th.getC_siteid());
                                    json.put("status", (th.getC_status() == null) ? "" : th.getC_status());
                                    json.put("tkstatusid", (th.getC_tkstatusid() == null) ? "" : th.getC_tkstatusid());
                                    json.put("statustracking", (th.getC_statustracking() == null) ? "" : th.getC_statustracking());

                                    arr.put(json);
                                }
                            }

                            if (arr.length() <= 0) {
                                json = new JSONObject();
                                json.put("ticket_id", "");
                                json.put("ticket_id", "");
                                json.put("date_created", "");
                                json.put("owner", "");
                                json.put("changeby", "");
                                json.put("memo", "");
                                json.put("changedate", "");
                                json.put("ownergroup", "");
                                json.put("assignedownergroup", "");
                                json.put("orgid", "");
                                json.put("siteid", "");
                                json.put("status", "");
                                json.put("tkstatusid", "");
                                json.put("statustracking", "");
                                arr.put(json);
                            }

//                
                            jsonRes.put("total", arr.length());
                            jsonRes.put("size", arr.length());
                            jsonRes.put("data", arr);

                            res.addHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
                            jsonRes.write(res.getWriter());

                        } catch (Exception ex) {
                            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error Logic");
                            LogUtil.error(getClassName(), ex, "Error Get List Ticket Status :: " + ex.getMessage());
                        }
                } else {
                    res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Invalid Method");
                }

            } else {
                res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR CORS DOMAIN");
            }
        } catch (Exception ex) {
//            LogUtil.info(getClassName(), "ERROR :" + ex.getMessage());
        }

    }
}
