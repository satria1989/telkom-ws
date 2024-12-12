/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateServiceInformationDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateTicketOwnerDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author asani
 */
public class ReloadOwnergroup extends Element implements PluginWebSupport {

  private String pluginName =
      "Telkom New OSS - Ticket Incident Services - Reload Ownergroup Service";

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
    return getClass().getName();
  }

  @Override
  public String getPropertyOptions() {
    return null;
  }

  @Override
  public void webService(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    LogInfo info = new LogInfo();
    MasterParam masterParam = new MasterParam();
    MasterParamDao masterParamDao = new MasterParamDao();
    TicketStatus ticketStatus = new TicketStatus();
    LoadTicketDao loadTicket = new LoadTicketDao();
    RESTAPI _RESTAPI = new RESTAPI();

    try {

      if(req.getParameterMap().containsKey("ticketId")) {
        
        String ticketId = req.getParameter("ticketId");

        if (!ticketId.isEmpty()) {
          
          // FORMAT CALLBACK
          JSONObject callback = new JSONObject();
          callback.put("updateDetailServiceId", false);
          callback.put("updateOwnerGroup", false);
          
          
          ticketStatus = loadTicket.LoadTicketByIdTicket(ticketId);
          
          String ticket = (ticketStatus.getTicketId()== null) ? "" : ticketStatus.getTicketId();
          
          if(!ticket.isEmpty()) {
            String serviceId = (ticketStatus.getServiceId() == null) ? "" : ticketStatus.getServiceId();
            String customerSegment = (ticketStatus.getCustomerSegment()== null) 
                ? "" : ticketStatus.getCustomerSegment();
            String workzone = (ticketStatus.getWorkZone() == null) ? "" : ticketStatus.getWorkZone();
            String symtomp = (ticketStatus.getSymptomId()== null) ? "" : ticketStatus.getSymptomId();
            String sourceTicket = (ticketStatus.getSourceTicket()== null) 
                ? "" : ticketStatus.getSourceTicket();
            String channel = (ticketStatus.getChannel()== null) ? "" : ticketStatus.getChannel();

            // check if already service_id -> call to asset then update service id detail
            HashMap<String, String> params = new HashMap<String, String>();

            if (!serviceId.isEmpty()) {
              masterParam = masterParamDao.getUrl("list_service_information_custom");

              params.put("service_id", serviceId);

              HttpUrl.Builder httpBuilder = HttpUrl.parse(masterParam.getUrl()).newBuilder();
              if (params != null) {
                for (Map.Entry<String, String> parameter : params.entrySet()) {
                  httpBuilder.addQueryParameter(parameter.getKey(), parameter.getValue());
                }
              }

              Request request =
                  new Request.Builder()
                      .url(httpBuilder.build())
                      .addHeader("api_key", masterParam.getApi_key()) // add request headers
                      .addHeader("api_id", masterParam.getApi_id())
                      .addHeader("Origin", "https://oss-incident.telkom.co.id")
                      .build();

              JSONObject jsonResponse = _RESTAPI.CALLAPIHANDLER(request);
              info.Log(getClassName(), "RESPONSE SERVICE ID ::" +jsonResponse.toString());
              boolean resBool = jsonResponse.getBoolean("status");
              int code = jsonResponse.getInt("status_code");
              if (resBool) {
                String message = jsonResponse.getString("msg");

                Object object = new JSONTokener(message).nextValue();
                JSONObject json = (JSONObject) object;

                if (json.has("total")) {
                  int total = json.getInt("total");

                  if (total > 0) {
                    JSONArray jsonData = json.getJSONArray("data");

                    UpdateServiceInformationDao upsi = new UpdateServiceInformationDao();
//                    upsi.UpdateDetailServiceInformation(jsonData, ticketId, customerSegment); // UPDATE DATA DETAIL
                    JSONObject jsonListData = jsonData.getJSONObject(0);
                    
                    if(customerSegment.isEmpty()) {
                        customerSegment = (jsonListData.has("customer_segment")) ?
                            jsonListData.getString("customer_segment") : customerSegment;
                        
                        if(customerSegment.equals("PL-")) {
                          customerSegment = "PL-TSEL";
                        }
                    }
                    
                    workzone = jsonListData.has("workzone") ? 
                        jsonListData.getString("workzone") : workzone;
                    
                    callback.put("updateDetailServiceId", true);
                  }  
                } 

              } else {
                res.sendError(code);
              }
            }


            // GET DATA OWNERGROUP
            params = new HashMap<String, String>();
            params.put("classification_id", symtomp);
            params.put("workzone", workzone);

            if(!sourceTicket.equalsIgnoreCase("GAMAS") && !channel.equalsIgnoreCase("44")) {
              params.put("customer_segment", customerSegment);
            }

            masterParam = masterParamDao.getUrl("list_tk_mapping");
            HttpUrl.Builder httpBuilder = HttpUrl.parse(masterParam.getUrl()).newBuilder();
            if (params != null) {
              for (Map.Entry<String, String> parameter : params.entrySet()) {
                httpBuilder.addQueryParameter(parameter.getKey(), parameter.getValue());
                info.Log(getClassName(), "PARAM OG : " + parameter.getKey() + ":" + parameter.getValue());
              }

              Request request =
                new Request.Builder()
                    .url(httpBuilder.build())
                    .addHeader("api_key", masterParam.getApi_key()) // add request headers
                    .addHeader("api_id", masterParam.getApi_id())
                    .addHeader("Origin", "https://oss-incident.telkom.co.id")
                    .build();

              JSONObject ownerGroupReq = _RESTAPI.CALLAPIHANDLER(request);
              info.Log(getClassName(), "RESPONSE OwnerGroup ::" +ownerGroupReq.toString());
              boolean resBool = ownerGroupReq.getBoolean("status");
              int code = ownerGroupReq.getInt("status_code");
               if (resBool) {
                String message = ownerGroupReq.getString("msg");

                Object object = new JSONTokener(message).nextValue();
                JSONObject json = (JSONObject) object;

                if (json.has("total")) {
                    int total = json.getInt("total");

                    if (total > 0) {
                      String dataOwnerGroup =
                          json.getJSONArray("data")
                              .getJSONObject(0)
                              .getString("person_owner_group");
                      
                      UpdateTicketOwnerDao utoDao = new UpdateTicketOwnerDao();
                      
                        utoDao.updateOwnerGroupByTicketID(dataOwnerGroup, ticketId);
                      // INSERT LOG HISTORY
                        ticketStatus.setChangeBy("SYSTEM");
                        ticketStatus.setMemo("RELOAD OWNERGROUP");
                        ticketStatus.setOwnerGroup(dataOwnerGroup);
                        ticketStatus.setAssignedOwnerGroup(dataOwnerGroup);
                        ticketStatus.setStatusTracking(ticketId);

                        utoDao.UpdateTimeonLastTicketSta(ticketStatus);
                        ticketStatus.setStatusTracking("false");
                        utoDao.insertTicketStatus(ticketStatus);
                      
                      
                      callback.put("updateOwnerGroup", true);
                    }
                }

               }

            }
            
            callback.write(res.getWriter());
            
          } else {
            res.sendError(
                HttpServletResponse.SC_NOT_FOUND, 
                "DATA Ticket Id '"+ticketId+"' is not found."
            );
          }
          
        } else {
          res.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, "Ticket ID Is Empty.");
        }
        
      } else {
        res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing Parameter.");
      }
      

    } catch (Exception ex) {
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      info.Error(getClass().getName(), pluginName, ex);
    }
  }
}
