/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;

/**
 * @author asani
 */
public class CheckProcessIsRunning extends Element implements PluginWebSupport {

  public static String pluginName =
      "Telkom New OSS - Ticket Incident Services - CheckProcessIsRunning";

  LogInfo info = new LogInfo();

  @Override
  public String renderTemplate(FormData formData, Map dataModel) {
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
  public void webService(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    GetConnections gc = new GetConnections();
    Connection con = null;
    ResultSet rs = null;
    PreparedStatement ps = null;

    WorkflowUserManager workflowUserManager =
        (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
    AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");

    try {

      CheckOrigin checkOrigin = new CheckOrigin();
      String origin = req.getHeader("Origin");
      String ACAO = req.getHeader("Access-Control-Allow-Origin");
      if (origin == null) {
        origin = ACAO;
      }
      boolean allowOrigin = checkOrigin.checkingOrigin(origin, res);

      String referrer = req.getHeader("referer");
      URL url = new URL(referrer);
      String queryStr = url.getQuery();

      if (allowOrigin) {

        String[] params = queryStr.split("&");
        JSONObject paramReferer = new JSONObject();
        for (String param : params) {
          String key = param.substring(0, param.indexOf('='));
          String val = param.substring(param.indexOf('=') + 1);
          paramReferer.put(key, val);
        }
        params = null;

        if (paramReferer.has("id")) {
          String PARENT_ID = paramReferer.getString("id");

          if (!workflowUserManager.isCurrentUserAnonymous()) {
            con = gc.getJogetConnection();
            StringBuilder query = new StringBuilder();
            query
                .append(" SELECT c.c_id_ticket, d.processId, E.STATE stattee ")
                .append(" from app_fd_ticket c  ")
                .append(" JOIN wf_process_link d ON c.c_parent_id = d.originProcessId  ")
                .append(" JOIN SHKACTIVITIES e ON e.PROCESSID = d.PROCESSID ")
                .append(" WHERE E.STATE = '1000000' and c_parent_id = ? ");

            ps = con.prepareStatement(query.toString());
            ps.setString(1, PARENT_ID);
            rs = ps.executeQuery();

            if (rs.next()) {
              // false
              res.sendError(HttpServletResponse.SC_CONFLICT, "PROCESS IS RUNNING");
            } else {
              res.sendError(HttpServletResponse.SC_CONFLICT, "PROCESS IS RUNNING");
            }

          } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
          }
        } else {
          res.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "ID Not Support");
        }

      } else {
        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Not Access.");
      }

    } catch (Exception ex) {
      info.Error(getClassName(), ex.getMessage(), ex);
    }
  }
}
