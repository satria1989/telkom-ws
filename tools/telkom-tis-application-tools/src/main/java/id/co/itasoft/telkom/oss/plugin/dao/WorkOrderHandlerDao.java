/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author itasoft
 */
public class WorkOrderHandlerDao {

  ApiConfig apiConfig = null;
  LogHistory lh = null;
  LogHistoryDao lhdao = null;
  RESTAPI _RESTAPI = null;
  LogInfo logInfo = new LogInfo();

//  WorkflowUserManager wum = (WorkflowUserManager) AppUtil
//      .getApplicationContext()
//      .getBean("workflowUserManager");
  
  String currentUser = "000000";
  boolean result = false;

  GetConnections gn = new GetConnections();

  public void GetandStartWO(MasterParam param, TicketStatus ts) throws SQLException, JSONException {
    // object
    _RESTAPI = new RESTAPI();
    apiConfig = new ApiConfig();
    lh = new LogHistory();
    lhdao = new LogHistoryDao();

    apiConfig.setUrl(param.getUrl());
    apiConfig.setApiId(param.getApi_id());
    apiConfig.setApiKey(param.getApi_key());

    JSONObject json = new JSONObject();
    JSONObject jsonData = new JSONObject();
    JSONObject detailField = new JSONObject();
    JSONArray detailsJson = new JSONArray();

    String StatusCurrent = (ts.getStatusCurrent() == null) ? "" : ts.getStatusCurrent();
    String TicketId = (ts.getTicketId() == null) ? "" : ts.getTicketId();
    String ServiceType = (ts.getServiceType() == null) ? "" : ts.getServiceType();
    String ServiceNo = (ts.getServiceNo() == null) ? "" : ts.getServiceNo();
    String ServiceAddress = (ts.getServiceAddress() == null) ? "" : ts.getServiceAddress();
    String customerSegment = (ts.getCustomerSegment() == null) ? "" : ts.getCustomerSegment();
    String sto = (ts.getWorkZone() == null) ? "" : ts.getWorkZone();
    String rk = (ts.getRkInformation() == null) ? "" : ts.getRkInformation();
    String bookingId = (ts.getBookingId() == null) ? "" : ts.getBookingId();
    String customerName = (ts.getCustomerName() == null) ? "" : ts.getCustomerName();

    json.put("externalSystem", "Incident");
    json.put("status", StatusCurrent);
    json.put("externalId", TicketId);
    json.put("serviceType", ServiceType);
    json.put("serviceNum", ServiceNo);
    json.put("serviceAddress", ServiceAddress);
    json.put("bookingId", bookingId);
    json.put("customerName", customerName);
    json.put("rk", rk);
    json.put("sto", sto);
    json.put("scheduleType", "ASSURANCE");

    // detail scc result
    detailField.put("attributeName", "scc_result");
    detailField.put("attributeValue", "");
    detailsJson.put(detailField);

    // detail customer segment
    detailField = new JSONObject();
    detailField.put("attributeName", "customer_segment");
    detailField.put("attributeValue", customerSegment);
    detailsJson.put(detailField);

    // detail scc time
    detailField = new JSONObject();
    detailField.put("attributeName", "scc_time");
    detailField.put("attributeValue", "");
    detailsJson.put(detailField);

    json.put("details", detailsJson);
    lh.setUrl(apiConfig.getUrl());
    lh.setMethod("POST");
    lh.setRequest(json.toString());
    lh.setTicketId(ts.getTicketId());
    lh.setAction("createWO");
    RequestBody body = RequestBody.create(_RESTAPI.JSON, json.toString());

    try {
      Request request =
          new Request.Builder()
              .url(apiConfig.getUrl())
              .addHeader("api_key", apiConfig.getApiKey()) // add request headers
              .addHeader("api_id", apiConfig.getApiId())
              .post(body)
              .build();

      JSONObject responseAPI = _RESTAPI.CALLAPIHANDLER(request);
//      boolean statusAPI = responseAPI.getBoolean("status");
//      String reqHandler = responseAPI.getString("msg");
      lh.setResponse(responseAPI.toString());
      lhdao.insertToLogHistory(lh);

      // clear variable
//      reqHandler = null;
//      responseAPI = null;
      request = null;
    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      json = null;
      jsonData = null;
      detailField = null;
      detailsJson = null;
      body = null;
      _RESTAPI = null;
      apiConfig = null;
      lh = null;
      lhdao = null;
    }
  }

  public void UpdateFinalcheck(TicketStatus ts) throws SQLException, Exception {
    StringBuilder query = new StringBuilder();
    PreparedStatement ps = null;
    query.append(" UPDATE app_fd_ticket set c_finalcheck = '1' WHERE c_id_ticket = ? ");
    Connection con = gn.getJogetConnection();
    try {
      ps = con.prepareStatement(query.toString());
      ps.setString(1, ts.getTicketId());
      ps.executeUpdate();
    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      query = null;
      try {
        if (ps != null) {
          ps.close();
        }
      } catch (Exception e) {
        logInfo.Log(getClass().getName(), e.getMessage());
      }
      try {
        if (con != null) {
          con.close();
        }
      } catch (Exception e) {
        logInfo.Log(getClass().getName(), e.getMessage());
      }
    }
  }
}
