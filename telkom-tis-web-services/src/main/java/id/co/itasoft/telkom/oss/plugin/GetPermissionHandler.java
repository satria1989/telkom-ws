package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.SelectCollections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * @author asani
 */
public class GetPermissionHandler extends Element implements PluginWebSupport {

  public static String pluginName =
      "Telkom New OSS - Ticket Incident Services - GetPermissionHandler";
  LogInfo logInfo = new LogInfo();

  @Override
  public String renderTemplate(FormData fd, Map map) {
    throw new UnsupportedOperationException(
        "Not supported yet."); // To change body of generated methods, choose Tools | Templates.
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
  public void webService(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {

    ApiConfig apiConfig;
    MasterParam mp;
    MasterParamDao mpd;
    RESTAPI _RESTAPI;
    CheckOrigin checkOrigin;
    SelectCollections selectCollections;
    try {
      checkOrigin = new CheckOrigin();
      apiConfig = new ApiConfig();
      mp = new MasterParam();
      mpd = new MasterParamDao();
      _RESTAPI = new RESTAPI();
      selectCollections = new SelectCollections();

      String origin = req.getHeader("Origin");
      boolean allowOrigin = checkOrigin.checkingOrigin(origin, res);

      if (allowOrigin) {

        JSONArray _JSONARRAY;
        JSONObject _JSONOBJECT;

        WorkflowUserManager workflowUserManager =
            (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

        String currentUser = workflowUserManager.getCurrentUsername().toUpperCase();

        StringBuffer jb = new StringBuffer();
        String line = null;
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
          jb.append(line);
        }
        reader.close();
        String bodyParam = jb.toString();

        try {
          if (!workflowUserManager.isCurrentUserAnonymous()) {

            Object objBodyParam = new JSONTokener(bodyParam).nextValue();
            JSONObject jsonBodyParam = (JSONObject) objBodyParam;

            String type = jsonBodyParam.getString("type");
            String api_key = "";
            String api_id = "";

            JSONObject jsonOBJJ;
            jsonOBJJ = new JSONObject();
            String URL = "";
            String _token = "";
            String token = _RESTAPI.getToken();
            String stringResponse = "";
            Object object = new Object();
            if ("getPermission".equalsIgnoreCase(type)) {
              String getOnDB = selectCollections.getDataPermission(currentUser);

              logInfo.Log(getClassName(), "==========DB ==== : "+getOnDB);
              if (!getOnDB.isEmpty()) {
                object = new JSONTokener(getOnDB).nextValue();
              logInfo.Log(this.getClassName(), "getOnDB ==== " +getOnDB);
              } else {
                jsonOBJJ.put("person_code", currentUser);
                jsonOBJJ.put("app", "Incident");

                mp = mpd.getUrl("get_list_permission");
                URL = mp.getUrl();
                api_id = mp.getApi_id();
                api_key = mp.getApi_key();

                RequestBody body = RequestBody.create(_RESTAPI.JSON, jsonOBJJ.toString());
                Request request;
                request =
                    new Request.Builder()
                        .url(URL)
                        .addHeader("api_key", api_key)
                        .addHeader("api_id", api_id)
                        .post(body)
                        .build();
                String reqAPI = _RESTAPI.CALLAPI(request);
                logInfo.Log(getClassName(), "=========="+reqAPI);
                object = new JSONTokener(reqAPI).nextValue();
                body = null;
                request = null;
                reqAPI = null;
              }
            }

            if (object instanceof JSONObject) {
              _JSONOBJECT = (JSONObject) object;
              _JSONOBJECT.write(res.getWriter());
            } else if (object instanceof JSONArray) {
              _JSONARRAY = (JSONArray) object;
              _JSONARRAY.write(res.getWriter());
            }

            objBodyParam = null;
          } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
          }

        } catch (Exception ex) {
          res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
          logInfo.Error(getClass().getSimpleName(), "RES_ERROR_PERMISSION", ex);
        }
      } else {
        // Throw 403 status OR send default allow
        res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "");
      }
    } catch (Exception ex) {
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
      logInfo.Error(getClass().getSimpleName(), "RES_ERROR_PERMISSION", ex);
    } finally {
      apiConfig = null;
      mp = null;
      mpd = null;
      _RESTAPI = null;
      checkOrigin = null;
    }
  }
}
