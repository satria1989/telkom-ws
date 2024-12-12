/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.function.SendWANotification;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

/**
 * @author asani
 */
public class LogicToOtherAPI {

  MasterParam masterParam;
  MasterParamDao masterParamDao;
  SendWANotification swa;
  LogHistory lh;
  LogHistoryDao lhDao;
  RESTAPI _RESTAPI;
  LogInfo logInfo = new LogInfo();

  /**
   * UPDATE PHONE NUMBER SETIAP CREATE TICKET BARU
   * @param ts
   * @throws SQLException 
   */
  public void UpdatePhoneNumberToCstmrIfmtn(TicketStatus ts) throws SQLException {
    try {
      
      //INISIALISASI OBJECT
      masterParam = new MasterParam();
      masterParamDao = new MasterParamDao();
      swa = new SendWANotification();
      lh = new LogHistory();
      lhDao = new LogHistoryDao();
      _RESTAPI = new RESTAPI();
      
      JSONObject json = new JSONObject();
      json.put("customerCode", ts.getCustomerId());
      json.put("phoneNumber", ts.getPhone());

      masterParam = masterParamDao.getUrl("updatePhoneCustomer");

      RequestBody body = RequestBody.create(_RESTAPI.JSON, json.toString());
      String response = "";
      Request request =
          new Request.Builder()
              .url(masterParam.getUrl())
              .addHeader("api_id", masterParam.getApi_id())
              .addHeader("api_key", masterParam.getApi_key())
              .addHeader("Origin", "https://oss-incident.telkom.co.id")
              .post(body)
              .build();

      lh.setUrl(masterParam.getUrl());
      lh.setAction("updatePhoneNumber(" + ts.getTicketId() + ")");
      lh.setMethod("POST");
      lh.setRequest(json.toString());
      lh.setTicketId(ts.getTicketId());

      JSONObject updatePhone = _RESTAPI.CALLAPIHANDLER(request);
      lh.setResponse(updatePhone.toString());
      
      // INSER LOG HISTORY
      lhDao.insertToLogHistory(lh);

      body = null;
      updatePhone = null;
      request = null;

    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      masterParam = null;
      masterParamDao = null;
      swa = null;
      lh = null;
      lhDao = null;
      _RESTAPI = null;
    }
  }

  /**
   * KIRIM MYTENS UNTUK STATUS ANALYSIS, BACKEND, MEDIACARE, SALAMSIM, CLOSED && CHANNEL =35
   * EBIS
   * @param ts
   * @param statusTarget
   * @param token
   * @throws Exception 
   */
  public void UpdateMYTENS(TicketStatus ts, String statusTarget, String token) throws Exception {
    try {
      masterParam = new MasterParam();
      masterParamDao = new MasterParamDao();
      _RESTAPI = new RESTAPI();

      masterParam = masterParamDao.getUrl("UPDATE_STATUS_MYTENS");

      String TICKET = (ts.getTicketId() == null) ? "" : ts.getTicketId();
      String status = (statusTarget == null) ? "" : statusTarget;
      String summary = (ts.getSummary() == null) ? "" : ts.getSummary();

      JSONObject json = new JSONObject();
      json.put("nossaTicketNumber", TICKET);
      json.put("status", status);
      json.put("description", summary);
      lh = new LogHistory();
      lhDao = new LogHistoryDao();

      RequestBody jsonRequestBody = RequestBody.create(_RESTAPI.JSON, json.toString());
      Request request =
          new Request.Builder()
              .url(masterParam.getUrl())
              .addHeader("Authorization", "Bearer " + token)
              .post(jsonRequestBody)
              .build();
      
      lh.setUrl(masterParam.getUrl());
      lh.setAction("UPDATE STATUS MYTENS("+statusTarget+")");
      lh.setMethod("POST");
      lh.setRequest(json.toString());
      lh.setTicketId(TICKET);
      
      String response = _RESTAPI.CALLAPI(request);
      lh.setResponse(response);

    } catch (Exception ex) {
      lh.setResponse(ex.getMessage());
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      lhDao.insertToLogHistory(lh);
      masterParam = null;
      masterParamDao = null;
      swa = null;
      lh = null;
      lhDao = null;
    }
  }
  
  /**
   * SEND OVRS SETIAP MASUK MEDIACARE KHUSUS EBIS
   * @param ts
   * @param TOKEN 
   */
  public void SENDOVRS(TicketStatus ts, String TOKEN) {
    try {
      masterParam = new MasterParam();
      masterParamDao = new MasterParamDao();
      lhDao = new LogHistoryDao();
      lh = new LogHistory();
      _RESTAPI = new RESTAPI();
      
      String ownergrouHSIList[] = {"DES", "DGS", "DBS"};
      String customerSegment = (ts.getCustomerSegment() == null) ? 
          "" : ts.getCustomerSegment().toUpperCase().trim();
      String TICKETID = (ts.getTicketId() == null) ? 
          "" : ts.getTicketId().toUpperCase().trim();
      String serviceType = (ts.getServiceType() == null) ? 
          "" : ts.getServiceType().toUpperCase().trim();
      String serviceNumber = (ts.getServiceNo()== null) ? 
          "" : ts.getServiceNo().toUpperCase().trim();
      String phone = (ts.getPhone() == null) ? 
          "" : ts.getPhone();

      if (phone.length() > 0) {
        if ("0".equals(phone.substring(0, 1))) {
          phone = "62" + phone.substring(1).toString();
        }

        if ("+62".equals(phone.substring(0, 3))) {
          phone = "62" + phone.substring(1).toString();
        }
      }
      
      // SEND API OVRS
      JSONObject apiOVRSRequest = new JSONObject();
      JSONObject jsonRequest = new JSONObject();
      JSONObject eaiHeader = new JSONObject();
      JSONObject eaiBody = new JSONObject();

      eaiBody.put("Ticket", TICKETID);
      eaiBody.put("Phone", phone);
      eaiBody.put("Layanan", serviceType);
      eaiBody.put("NumberLayanan", serviceNumber);

      eaiHeader.put("internalId", "");
      eaiHeader.put("externalId", "");
      eaiHeader.put("timestamp", "");
      eaiHeader.put("responseTimestamp", "");

      apiOVRSRequest.put("eaiBody", eaiBody);
      apiOVRSRequest.put("eaiHeader", eaiHeader);
      jsonRequest.put("apiOVRSRequest", apiOVRSRequest);

      // CALL MASTER API DB
      masterParam = masterParamDao.getUrl("urlOVRS");

      RequestBody jsonRequestBody = RequestBody.create(_RESTAPI.JSON, jsonRequest.toString());
      Request request =
          new Request.Builder()
              .url(masterParam.getUrl())
              .addHeader("Authorization", "Bearer " + TOKEN)
              .post(jsonRequestBody)
              .build();

      lh.setCreatedBy("SYSTEM");
      lh.setUrl(masterParam.getUrl());
      lh.setAction("Send to OVRS(" + TICKETID+ ")");
      lh.setMethod("POST");
      lh.setRequest(jsonRequest.toString());
      lh.setTicketId(TICKETID);
      
      JSONObject stringResponse = _RESTAPI.CALLAPIHANDLER(request);
      lh.setResponse(stringResponse.toString());
      lhDao.insertToLogHistory(lh);
      
      // clear setiap object agar gc dapat langsung process variable null
      jsonRequest = null;
      apiOVRSRequest = null;
      eaiHeader = null;
      eaiBody = null;
      
    } catch(Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      masterParam = null;
      masterParamDao = null;
      swa = null;
      lh = null;
      lhDao = null;
      _RESTAPI = null;
    }
  }
  
  public String GET(MasterParam masterParam, JSONObject param) {
    String response = "";
    try {
      _RESTAPI = new RESTAPI();
      Iterator<Object> iterator = param.keys();
      HashMap<String, String> params = new HashMap<String, String>();
      
      HttpUrl.Builder httpBuilder = HttpUrl.parse(masterParam.getUrl()).newBuilder();
      JSONObject paramJson = null;
      while (iterator.hasNext()) {
          String key = (String) iterator.next();
          String value = ("".equals(param.getString(key))) ? "" : param.getString(key);
          
          httpBuilder.addQueryParameter(key, value);
          
      }
      iterator.remove();
      
      Request request = new Request.Builder()
              .url(httpBuilder.build())
              .addHeader("api_key", masterParam.getApi_key()) // add request headers
              .addHeader("api_id", masterParam.getApi_id())
              .addHeader("User-Agent", "Incident")
              .addHeader("Origin", "https://oss-incident.telkom.co.id")
              .build();

      response = _RESTAPI.CALLAPI(request);
    } catch(Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      _RESTAPI = null;
    }
    return response;
  }
      
}
