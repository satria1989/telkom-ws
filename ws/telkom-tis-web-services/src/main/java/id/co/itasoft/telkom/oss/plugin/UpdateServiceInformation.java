/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateServiceInformationDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.context.ApplicationContext;

/**
 * @author asani
 */
public class UpdateServiceInformation extends Element implements PluginWebSupport{
  
  public static String pluginName =
      "Telkom New OSS - Ticket Incident Services - UPDATE WORKORDER HANDLER PLUGIN";

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
    return "7.1.2";
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
    return getClass().getSimpleName();
  }

  @Override
  public String getPropertyOptions() {
    return null;
  }

  @Override
  public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    
    RESTAPI _RESTAPI = new RESTAPI();
    CheckOrigin checkOrigin = new CheckOrigin();
    MasterParam masterParam = new MasterParam();
    MasterParamDao mpd = new MasterParamDao();
    ApiConfig API = new ApiConfig();
    LogInfo logInfo = new LogInfo();
    
    try {
      
      checkOrigin = new CheckOrigin();
      String origin = req.getHeader("Origin");
      String ACAO = req.getHeader("Access-Control-Allow-Origin");
      if (origin == null) {
          origin = ACAO;
      }
      boolean allowOrigin = checkOrigin.checkingOrigin(origin, res);
      
      if(allowOrigin) {
      
        ApplicationContext ac = AppUtil.getApplicationContext();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

        String param = req.getParameter("request");
        String type = req.getParameter("type");
        String referrer = req.getHeader("referer");
        String ticketId = req.getParameter("ticketId");

        String DATA = param;
        JSONArray _arr = new JSONArray();
        API.setApiToken("");
        API.setApiKey("");
        API.setApiId("");

        JSONObject jsonOBJJ = new JSONObject(DATA);
        
        if (!workflowUserManager.isCurrentUserAnonymous()) {
          
          Iterator<?> iterator = jsonOBJJ.keys();
          HashMap<String, String> params = new HashMap<String, String>();

          if ("listServiceInformation".equalsIgnoreCase(type)) {
              masterParam = mpd.getUrl("list_service_information_custom");
              API.setUrl(masterParam.getUrl());
              API.setApiId(masterParam.getApi_id());
              API.setApiKey(masterParam.getApi_key());
              
              JSONObject paramJson = null;
              String value = "";
              while (iterator.hasNext()) {
                  String key = (String) iterator.next();
                  if (jsonOBJJ.get(key) instanceof JSONObject) {
                      if (key.equalsIgnoreCase("param")) {
                          paramJson = (JSONObject) jsonOBJJ.get("param");
                          Iterator<?> paramIterator = paramJson.keys();

                          while (paramIterator.hasNext()) {
                              String paramKey = (String) paramIterator.next();
                              value = ("".equals(paramJson.getString(paramKey))) ? "" : paramJson.getString(paramKey);
                              params.put(paramKey, value);
                          }
                      }
                  } else {
                      if (key.equalsIgnoreCase("url")) {
                          API.setUrl(jsonOBJJ.getString(key));
                      }
                  }
              }
              iterator.remove();
              
              HttpUrl.Builder httpBuilder = HttpUrl.parse(API.getUrl()).newBuilder();
              if (params != null) {
                  for (Map.Entry<String, String> parameter : params.entrySet()) {
                    logInfo.Log(parameter.getKey(), parameter.getValue());
                      httpBuilder.addQueryParameter(parameter.getKey(), parameter.getValue());
                  }
              }

              Request request = new Request.Builder()
                      .url(httpBuilder.build())
                      .addHeader("api_key", API.getApiKey()) // add request headers
                      .addHeader("api_id", API.getApiId())
                      .addHeader("token", API.getApiToken())
                      .addHeader("Origin", "https://oss-incident.telkom.co.id")
                      .build();

              JSONObject jsonResponse = _RESTAPI.CALLAPIHANDLER(request);
              boolean resBool = jsonResponse.getBoolean("status");
              int code = jsonResponse.getInt("status_code");
              if(resBool) {
                String message = jsonResponse.getString("msg");
                
                Object object = new JSONTokener(message).nextValue();
                JSONObject json = (JSONObject) object;
                logInfo.Log(getClass().getName(), json.toString());
                logInfo.Log(getClass().getName(), ticketId);
                
                if(json.has("total")) {
                  int total = json.getInt("total");
                  
                  if(total>0) {
                    JSONArray jsonData = json.getJSONArray("data");
                      TicketStatus ticketStatuss = new TicketStatus();
                      LoadTicketDao loadTicketDao = new LoadTicketDao();
                      ticketStatuss = loadTicketDao.LoadTicketByIdTicket(ticketId);
                      
                      String customerSegment = (ticketStatuss.getCustomerSegment() == null) ? 
                              "" : ticketStatuss.getCustomerSegment();
                    UpdateServiceInformationDao upsi = new UpdateServiceInformationDao();
//                    upsi.UpdateDetailServiceInformation(jsonData, ticketId, customerSegment);
                    JSONObject resp = new JSONObject();
                    resp.put("status", "DATA UPDATED");
                    resp.write(res.getWriter());
                    res.setStatus(HttpServletResponse.SC_ACCEPTED);
                  } else {
                    JSONObject resp = new JSONObject();
                    resp.put("status", "DATA NOT EXIST");
                    resp.write(res.getWriter());
                    res.setStatus(HttpServletResponse.SC_NO_CONTENT);
                  }
                } else {
                  JSONObject resp = new JSONObject();
                  resp.put("status", "DATA NOT EXIST");
                  resp.write(res.getWriter());
                  res.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
 
              } else {
                res.sendError(code);
              }
              
          } else {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
          }
          
        } else {
          res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
        }
        
      } else {
        res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Not Acceptable");
      }
      
    } catch(Exception ex) {
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error");
      logInfo.Error(getClass().getName(), ex.getMessage(), ex);
    }
    
  }
}
