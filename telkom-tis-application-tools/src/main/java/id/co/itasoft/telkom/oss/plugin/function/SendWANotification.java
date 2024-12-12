/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.function;

import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author itasoft
 */
public class SendWANotification {

  ApiConfig apiConfig = null;
  LogHistory lh, logHistory = null;
  LogHistoryDao lhdao, logHistoryDao = null;
  MasterParam param, masterParam = null;
  MasterParamDao paramDao,masterParamDao = null;
  RESTAPI _RESTAPI = null;
  LogInfo logInfo = new LogInfo();

  public void sendNotify(TicketStatus ts) throws IOException, SQLException, Exception {
    
    String custSegment = (ts.getCustomerSegment() == null) ? 
          "" : ts.getCustomerSegment().toUpperCase().trim();
    
    apiConfig = new ApiConfig();
    lh = new LogHistory();
    lhdao = new LogHistoryDao();
    param = new MasterParam();
    paramDao = new MasterParamDao();
    _RESTAPI = new RESTAPI();

    JSONObject jo = new JSONObject();
    OtherFunction OF = new OtherFunction();
    Map m = new LinkedHashMap(5);

    String phone = (ts.getPhone() == null) ? "" : ts.getPhone();
    if (phone.length() > 0) {
      if ("0".equals(phone.substring(0, 1))) {
        phone = "62" + phone.substring(1).toString();
      }
    }

    m.put("ticketID", ts.getTicketId());
    m.put("phone", phone);
    m.put("source", "NOSSA");
    JSONObject root = new JSONObject();

    Map templateDataJson = new LinkedHashMap(10);
    
    
    if ("GAMAS".equalsIgnoreCase(ts.getSourceTicket())) {
      String estimation = ts.getEstimation();
      if (estimation == null || "".equals(estimation)) {
        estimation = "0";
      }

      int duration = Integer.parseInt(estimation);
      String dateCreated = ts.getDateCreated().toString();

      String dateEstimation = OF.AddingHoursToDate(duration, ts.getDateCreated().toString());
      m.put("templateID", "gamas_open_11");
      m.put(
          "smsTest",
          "Pelanggan yth, diinformasikan terjadi gangguan massal yang kemungkinan berdampak pada layanan Anda. Saat ini kami sedang melakukan perbaikan. Terimakasih");
      templateDataJson.put("data1", ts.getServiceNo());
      templateDataJson.put("data2", ts.getSymptomDesc());
      templateDataJson.put("data3", dateEstimation); // 04-03-2022
      templateDataJson.put("data4", "https://goo.gl/H7xzYk");
      templateDataJson.put("data5", "https://goo.gl/va3a1E");
      templateDataJson.put("data6", "#");
      templateDataJson.put("data7", "#");
      templateDataJson.put("data8", "#");
      templateDataJson.put("data9", "#");
      templateDataJson.put("data10", "#");
      m.put("templateData", templateDataJson);
      jo.put("input", m);
      root.put("notificationWA", jo);

    } else {
      logInfo.Log("customer segment wa", custSegment);
      switch(custSegment) {
        case "DCS" :
          String bookingId = (ts.getBookingId() == null) ? "" : ts.getBookingId();
          String classficationFlag =
              (ts.getClassificationFlag() == null) ? "" : ts.getClassificationFlag();

          if (classficationFlag.contains("NONTECHNICAL")
              && custSegment.equals("DCS")) { // NON TECHNICAL / LOGIC

            m.put("templateID", "ot_nonteknis_6");
            m.put(
                "smsTest",
                "Pelanggan Yth, laporan gangguan "
                    + ts.getServiceNo()
                    + " telah diterima dgn tiket "
                    + ts.getTicketId()
                    + ". "
                    + "Info progres klik https://tlkm.id/"
                    + ts.getTicketId()
                    + ". Nomor konfirmasi : "
                    + ts.getCode_validation()
                    + ". Mhn maaf atas ketidaknyamanannya");
            templateDataJson.put("data1", ts.getTicketId());
            templateDataJson.put("data2", ts.getServiceType());
            templateDataJson.put("data3", ts.getServiceNo());
            templateDataJson.put("data4", ts.getTicketId());
            //                templateDataJson.put("data5", ts.getLandingPage());
            templateDataJson.put("data5", ts.getCode_validation());
            templateDataJson.put("data6", "#");
            templateDataJson.put("data7", "#");
            templateDataJson.put("data8", "#");
            templateDataJson.put("data9", "#");
            templateDataJson.put("data10", "#");
            m.put("templateData", templateDataJson);
            jo.put("input", m);
            root.put("notificationWA", jo);

          } else if ("TECHNICAL".equalsIgnoreCase(classficationFlag)
              && "FISIK".equalsIgnoreCase(ts.getClassification_type())
              && bookingId.isEmpty() && custSegment.equals("DCS")) {
            m.put("templateID", "tek_f_nonmanja");
            m.put(
                "smsTest",
                "Pelanggan Yth, laporan gangguan "
                    + ts.getServiceNo()
                    + "telah diterima dgn tiket "
                    + ts.getTicketId()
                    + ". "
                    + "Info progres klik https://tlkm.id/"
                    + ts.getTicketId()
                    + ". Mhn maaf atas ketidaknyamanannya");
            templateDataJson.put("data1", ts.getTicketId());
            templateDataJson.put("data2", ts.getSourceTicket());
            templateDataJson.put("data3", ts.getServiceNo());
            templateDataJson.put("data4", ts.getTicketId());
            templateDataJson.put("data5", ts.getLandingPage());
            templateDataJson.put("data6", "#");
            templateDataJson.put("data7", "#");
            templateDataJson.put("data8", "#");
            templateDataJson.put("data9", "#");
            templateDataJson.put("data10", "#");
            m.put("templateData", templateDataJson);
            jo.put("input", m);
            root.put("notificationWA", jo);

          } else if ("TECHNICAL".equalsIgnoreCase(classficationFlag)
              && "FISIK".equalsIgnoreCase(ts.getClassification_type())
              && !bookingId.isEmpty() && custSegment.equals("DCS")) {
            m.put("templateID", "tek_f_manja");
            m.put(
                "smsTest",
                "Pelanggan Yth, laporan gangguan "
                    + ts.getServiceNo()
                    + "telah diterima dgn tiket "
                    + ts.getTicketId()
                    + ". "
                    + "Info progres klik https://tlkm.id/"
                    + ts.getTicketId()
                    + ". Mhn maaf atas ketidaknyamanannya");
            templateDataJson.put("data1", ts.getTicketId());
            templateDataJson.put("data2", ts.getServiceType());
            templateDataJson.put("data3", ts.getServiceNo());
            templateDataJson.put("data4", "SELAMAT HASAN"); // chief
            templateDataJson.put("data5", "09-Jun-2021"); // booking_date
            templateDataJson.put("data6", "13:00 - 15:00"); // booking_date += 2jam
            templateDataJson.put("data7", ts.getTicketId());
            templateDataJson.put("data8", ts.getLandingPage());
            templateDataJson.put("data9", "#");
            templateDataJson.put("data10", "#");
            m.put("templateData", templateDataJson);
            jo.put("input", m);
            root.put("notificationWA", jo);
          } else if ("TECHNICAL".equalsIgnoreCase(classficationFlag)
              && !"FISIK".equalsIgnoreCase(ts.getClassification_type())) {
            m.put("templateID", "tek_l_v1");
            m.put(
                "smsTest",
                "Pelanggan Yth, laporan gangguan "
                    + ts.getServiceNo()
                    + "telah diterima dgn tiket "
                    + ts.getTicketId()
                    + ". "
                    + "Info progres klik https://tlkm.id/"
                    + ts.getTicketId()
                    + "Nomor konfirmasi : "
                    + ts.getCode_validation()
                    + ". Mhn maaf atas ketidaknyamanannya");
            templateDataJson.put("data1", ts.getTicketId());
            templateDataJson.put("data2", ts.getSourceTicket());
            templateDataJson.put("data3", ts.getServiceNo());
            templateDataJson.put("data4", ts.getTicketId());
            templateDataJson.put("data5", ts.getLandingPage());
            templateDataJson.put("data6", ts.getCode_validation());
            templateDataJson.put("data7", "#");
            templateDataJson.put("data8", "#");
            templateDataJson.put("data9", "#");
            templateDataJson.put("data10", "#");
            m.put("templateData", templateDataJson);
            jo.put("input", m);
            root.put("notificationWA", jo);
          } else {
            // LogUtil.info(getClass().getName(), "NO WA SENDING FOR TICKET " + ts.getTicketId());
          }
           break;
          default:
      } 
    }
    
    if(root.length() >0) {
      // send notifywa
      String stringResponse = "";
      RequestBody body = RequestBody.create(_RESTAPI.JSON, root.toString());
      String _token = _RESTAPI.getToken();

      param = paramDao.getUrl("notificationWA");

      Request request =
          new Request.Builder()
              .url(param.getUrl())
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + _token)
              .post(body)
              .build();
      lh.setCreatedBy("SYSTEM");
      lh.setUrl(param.getUrl());
      lh.setAction("notificationWA(" + ts.getSourceTicket() + ")");
      lh.setMethod("POST");
      lh.setRequest(root.toString());
      lh.setTicketId(ts.getTicketId());

      try {
        if (!"FISIK".equalsIgnoreCase(ts.getClassification_type())) {
          stringResponse = _RESTAPI.CALLAPI(request);
          lh.setResponse(stringResponse);
        } else {
          lh.setResponse("waiting for the notification trigger from the scheduling service");
        }
      } catch (Exception ex) {
        lh.setResponse(ex.getMessage());
      } finally {
        try {
          if (!"FISIK".equalsIgnoreCase(ts.getClassification_type())) {
            lhdao.insertToLogHistoryWA(lh);
          }
          ts = null;
          m.clear();
          phone = null;
          templateDataJson.clear();
          OF = null;
          lh = null;
          lhdao = null;
          request = null;
          body = null;
          param = null;
          apiConfig = null;
          lh = null;
          lhdao = null;
          param = null;
          paramDao = null;
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        }
      }
    }

    

  }

  public String sendNotifyResolve(TicketStatus ts, String TOKEN)
      throws IOException, SQLException, Exception {

    _RESTAPI = new RESTAPI();
    apiConfig = new ApiConfig();
    lh = new LogHistory();
    lhdao = new LogHistoryDao();
    param = new MasterParam();
    paramDao = new MasterParamDao();
    JSONObject jo = new JSONObject();
    Map m = new LinkedHashMap(11);
    String phone = (ts.getPhone() == null) ? "" : ts.getPhone();
    String phoneTenat = phone;

    if (phone.length() > 0) {
      if ("62".equals(phone.substring(0, 2))) {
        phoneTenat = "0" + phone.substring(2).toString();
      }
      if ("+62".equals(phone.substring(0, 3))) {
        phoneTenat = "0" + phone.substring(3).toString();
      }
      if ("0".equals(phone.substring(0, 1))) {
        phone = "62" + phone.substring(1).toString();
      }
    }

    String REFERENCENUMBER = (ts.getReferenceNumber() == null) ? "" : ts.getReferenceNumber();

    String TenantKeyPrivate = "ee55655cd3cae67ceaca36c6fd5dc6c0bf6e02dc";
    StringBuilder tenantKeyHashing = new StringBuilder();
    tenantKeyHashing.append("INFOMEDIA#");
    tenantKeyHashing.append(TenantKeyPrivate);
    tenantKeyHashing.append("#");
    tenantKeyHashing.append(REFERENCENUMBER);
    tenantKeyHashing.append("#");
    tenantKeyHashing.append(phoneTenat);
    tenantKeyHashing.append("#");
    tenantKeyHashing.append(phoneTenat);
    tenantKeyHashing.append("#");
    tenantKeyHashing.append(ts.getTicketId());
    tenantKeyHashing.append("#14#NUSANTARA");

    GenerateSHA1Handler generateSHA1Handler = new GenerateSHA1Handler();
    String SHA1 = generateSHA1Handler.sha1(tenantKeyHashing.toString());
    String PHONEFORMAT = REFERENCENUMBER + "#" + phoneTenat + "#" + phoneTenat;

    m.put("tenantKeyPublic", "inf-telkom-001");
    m.put("tenantKeyHash", SHA1); // 006b57d44688526c775b1df42ebadebcf0f96f65
    m.put("phone", PHONEFORMAT);
    m.put("ticketID", ts.getTicketId());
    m.put("channel", "14");
    m.put("timeZone", "WIB"); // GET DARI timezone skrng blm ada
    m.put("serviceType", ts.getServiceType());
    m.put("serviceNumber", ts.getServiceNo());
    m.put("mediaCare", "WA");
    m.put(
        "smsContent",
        "Pelanggan Yth, gangguan "
            + ts.getServiceType()
            + " "
            + ts.getServiceNo()
            + " telah diperbaiki.");
    m.put("templateID", "konfirmasi_satu");
    jo.put("doCreate", m);

    String stringResponse = "";
    RequestBody jsonRequestBody = RequestBody.create(_RESTAPI.JSON, jo.toString());
    String _token = TOKEN;

    param = paramDao.getUrl("url_send_wa_mediacare");

    lh.setCreatedBy("SYSTEM");
    lh.setUrl(param.getUrl());
    lh.setAction("notificationWAMediacare(" + ts.getSourceTicket() + ")");
    lh.setMethod("POST");
    lh.setRequest(jo.toString());
    lh.setTicketId(ts.getTicketId());

    try {
      Request request =
          new Request.Builder()
              .url(param.getUrl())
              .addHeader("Authorization", "Bearer " + _token)
              .post(jsonRequestBody)
              .build();
      stringResponse = _RESTAPI.CALLAPI(request);
      logInfo.Log("WA MEDIACARE", "RESPONESE :" + stringResponse);
      lh.setResponse(stringResponse);
      request = null;
      stringResponse = null;
    } catch (Exception ex) {
      lh.setResponse(ex.getMessage());
    } finally {
      try {
        lhdao.insertToLogHistoryWA(lh);
        ts = null;
        jsonRequestBody = null;
        m.clear();
        _token = null;
        param = null;
        lh = null;
        lhdao = null;
      } catch (Exception ex) {
        logInfo.Log(getClass().getName(), ex.getMessage());
      }
    }
    return stringResponse;
  }

  public void sendWAEbis(TicketStatus ticketStatus, String token) {
    try {
      
      String custSegment = (ticketStatus.getCustomerSegment() == null) ? 
          "" : ticketStatus.getCustomerSegment().toUpperCase().trim();
      String sourceTicket = (ticketStatus.getSourceTicket() == null) ? 
          "" : ticketStatus.getSourceTicket().toUpperCase().trim();
      String classificationType = (ticketStatus.getClassification_type() == null) ? 
          "" : ticketStatus.getClassification_type().toUpperCase().trim();
      String ticketId = (ticketStatus.getTicketId()== null) ? 
          "" : ticketStatus.getTicketId().toUpperCase().trim();
      String phone = (ticketStatus.getPhone()== null) ? 
          "" : ticketStatus.getPhone().toUpperCase().trim();
      String serviceType = (ticketStatus.getServiceType()== null) ? 
          "" : ticketStatus.getServiceType().toUpperCase().trim();
      String serviceNum = (ticketStatus.getServiceNo()== null) ? 
          "" : ticketStatus.getServiceNo().toUpperCase().trim();
      String password = (ticketStatus.getLandingPage()== null) ? 
          "" : ticketStatus.getLandingPage().toUpperCase().trim(); // landingpage
      String channel = (ticketStatus.getChannel()== null) ? 
          "" : ticketStatus.getChannel().toUpperCase().trim(); // landingpage
      
      apiConfig = new ApiConfig();
      logHistory = new LogHistory();
      logHistoryDao = new LogHistoryDao();
      masterParam = new MasterParam();
      masterParamDao = new MasterParamDao();
      _RESTAPI = new RESTAPI();

      if (phone.length() > 0) {
        if ("0".equals(phone.substring(0, 1))) {
          phone = "62" + phone.substring(1).toString();
        }
      }

      JSONObject json = new JSONObject();
      JSONObject sendWATenesaRequestJson = new JSONObject();
      JSONObject eaiHeader = new JSONObject();
      JSONObject eaiBody = new JSONObject();
      JSONArray templateData = new JSONArray();
      JSONObject satu = new JSONObject();
      JSONObject dua = new JSONObject();
      JSONObject tiga = new JSONObject();
      JSONObject empat = new JSONObject();
      JSONObject lima = new JSONObject();

      // EAI HEADER
      eaiHeader.put("externalId", "");
      eaiHeader.put("timestamp", "");

      // EAI BODY
      eaiBody.put("TICKET_ID", ticketId);
      eaiBody.put("PHONE", phone);
      eaiBody.put("SOURCE", "NOSSA");
      eaiBody.put("TEMPLATE_ID", "open_tiket_tenesa_2");

      // ==> TEMPLATE DATA
      satu.put("1", ticketId);
      dua.put("2", serviceType);
      tiga.put("3", serviceNum);
      empat.put("4", ticketId);
      lima.put("5", password);
      templateData.put(satu);
      templateData.put(dua);
      templateData.put(tiga);
      templateData.put(empat);
      templateData.put(lima);
      eaiBody.put("TEMPLATE_DATA", templateData);

      sendWATenesaRequestJson.put("eaiHeader", eaiHeader);
      sendWATenesaRequestJson.put("eaiBody", eaiBody);
      json.put("sendWATenesaRequest", sendWATenesaRequestJson);

      RequestBody body = RequestBody.create(_RESTAPI.JSON, json.toString());
      masterParam = masterParamDao.getUrl("sendWaOpenEbis");
      Request request =
          new Request.Builder()
              .url(masterParam.getUrl())
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + token)
              .post(body)
              .build();
      logHistory.setCreatedBy("SYSTEM");
      logHistory.setUrl(masterParam.getUrl());
      logHistory.setAction("notificationWA(" + sourceTicket + ")");
      logHistory.setMethod("POST");
      logHistory.setRequest(json.toString());
      logHistory.setTicketId(ticketId);

      JSONObject response = _RESTAPI.CALLAPIHANDLER(request);
      logHistory.setResponse(response.toString());
      logHistoryDao.insertToLogHistoryWA(logHistory);
    } catch (Exception e) {

    }
  }
}
