/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.CronOwnergroupDao;
import id.co.itasoft.telkom.oss.plugin.dao.GlobalQuerySelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author asani
 */
public class CronRecoveryOwnergroup extends DefaultApplicationPlugin {

  private final String pluginName =
      "Telkom New OSS - Ticket Incident Services - Cron Recovery Ownergoroup";
  LogInfo logInfo = new LogInfo();
  MasterParam masterParam;
  GlobalQuerySelectCollections selectCollection;
  RESTAPI _RESTAPI;
  MasterParamDao masterParamDao;
  CronOwnergroupDao cronOwnergroupDao;
  LogHistory logHistory;
  LogHistoryDao logHistoryDao;

  public CronRecoveryOwnergroup() {
    logHistoryDao = new LogHistoryDao();
    _RESTAPI = new RESTAPI();
    cronOwnergroupDao = new CronOwnergroupDao();
    masterParamDao = new MasterParamDao();
    masterParam = new MasterParam();
    selectCollection = new GlobalQuerySelectCollections();
  }

  @Override
  public Object execute(Map map) {
    List<TicketStatus> listTicket = new ArrayList<TicketStatus>();
    try {
      masterParam = masterParamDao.getUrl("list_tk_mapping");

      listTicket = selectCollection.getListTicket();

      if (!listTicket.isEmpty()) {

        for (TicketStatus ticketStatus : listTicket) {
          String id = (ticketStatus.getId() == null) ? "" : ticketStatus.getId();
          String ticketId = (ticketStatus.getTicketId() == null) ? "" : ticketStatus.getTicketId();
          String classificationId =
              (ticketStatus.getSymptomId() == null) ? "" : ticketStatus.getSymptomId();
          String workzone = (ticketStatus.getWorkZone() == null) ? "" : ticketStatus.getWorkZone();
          String customerSegment =
              (ticketStatus.getCustomerSegment() == null) ? "" : ticketStatus.getCustomerSegment();
          String sourceTicket =
              (ticketStatus.getSourceTicket() == null) ? "" : ticketStatus.getSourceTicket().trim();
          String channel =
              (ticketStatus.getChannel() == null) ? "" : ticketStatus.getChannel().trim();

          HttpUrl.Builder httpBuilder = HttpUrl.parse(masterParam.getUrl()).newBuilder();

          httpBuilder.addQueryParameter("classification_id", classificationId);
          httpBuilder.addQueryParameter("customer_segment", customerSegment);
          httpBuilder.addQueryParameter("workzone", workzone);

          Request request =
              new Request.Builder()
                  .url(httpBuilder.build())
                  .addHeader("api_key", masterParam.getApi_key()) // add request headers
                  .addHeader("api_id", masterParam.getApi_id())
                  .addHeader("Origin", "https://oss-incident.telkom.co.id")
                  .build();

          JSONObject jsonResponse = _RESTAPI.CALLAPIHANDLER(request);
          boolean responseBool = jsonResponse.getBoolean("status");

          //          LogUtil.info(
          //              getClassName(),
          //              "LIST DATA TICKET :: "
          //                  + id
          //                  + " || "
          //                  + ticketId
          //                  + " || "
          //                  + classificationId
          //                  + " || "
          //                  + workzone
          //                  + " || "
          //                  + customerSegment
          //                  + " || "
          //                  + sourceTicket
          //                  + " || "
          //                  + channel);

          if (responseBool) {
            String response = jsonResponse.getString("msg");
            Object obj = new JSONTokener(response).nextValue();
            JSONObject json = new JSONObject();
            json = (JSONObject) obj;

            int total = json.getInt("total");
            logHistory = new LogHistory();
            logHistory.setCreatedBy("SYSTEM");
            logHistory.setUrl(masterParam.getUrl());
            logHistory.setAction("Cron Update Ownergroup(" + ticketId + ")");
            logHistory.setMethod("POST");
            logHistory.setRequest(request.url().encodedQuery());
            logHistory.setTicketId(ticketId);
            logHistory.setResponse(jsonResponse.toString());
            if (total > 0) {
              Object objData = new JSONTokener(json.getString("data")).nextValue();
              JSONArray jsonData = (JSONArray) objData;
              String data1 = jsonData.get(0).toString();
              Object dataStr = new JSONTokener(data1).nextValue();
              JSONObject jsonDataStr = (JSONObject) dataStr;

              String ownerGroup = jsonDataStr.getString("person_owner_group");
              cronOwnergroupDao.UpdateOwnergroup(ticketId, ownerGroup);
              
              objData = null;
              jsonData = null;
              data1 = null;
              dataStr = null;
              jsonDataStr = null;
            }
            logHistoryDao.insertToLogHistory(logHistory);
           }
        }
      }

    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      listTicket.clear();
      logHistoryDao = null;
      _RESTAPI = null;
      cronOwnergroupDao = null;
      masterParamDao = null;
      masterParam = null;
      selectCollection = null;
    }

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
}
