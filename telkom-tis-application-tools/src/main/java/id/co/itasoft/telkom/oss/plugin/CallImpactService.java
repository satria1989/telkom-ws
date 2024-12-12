/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
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
import org.json.simple.JSONObject;

/**
 * @author asani
 */
public class CallImpactService {

  String token = "";
  MasterParam param = null;
  MasterParamDao paramDao = null;
  RESTAPI _RESTAPI = null;
  LogInfo logInfo = new LogInfo();

  public String callImpact(TicketStatus ts) throws IOException, SQLException {
    LogHistory lh = new LogHistory();
    LogHistoryDao lhdao = new LogHistoryDao();

    paramDao = new MasterParamDao();
    param = new MasterParam();
    _RESTAPI = new RESTAPI();

    String stringResponse = "";
    JSONObject jo = new JSONObject();
    JSONObject gismr = new JSONObject();
    JSONObject eh = new JSONObject();
    JSONObject eb = new JSONObject();
    ApiConfig apiConfig = new ApiConfig();
    try {
      Map ehb = new LinkedHashMap(2);
      ehb.put("externalId", "");
      ehb.put("timestamp", "");

      Map ebb = new LinkedHashMap(3);
      ebb.put("IN_FAULTID", ts.getTicketId());
      ebb.put("IN_DEVICEID", ts.getPerangkat());
      ebb.put("IN_STATUS", ts.getStatus());

      Map eheb = new LinkedHashMap(2);
      eheb.put("eaiHeader", ehb);
      eheb.put("eaiBody", ebb);
      gismr.put("getImpactServiceManualRequest", eheb);

      String _token = _RESTAPI.getToken();

      param = paramDao.getUrl("impact_service");
      apiConfig.setUrl(param.getUrl());

      RequestBody jsonRequestBody = RequestBody.create(_RESTAPI.JSON, gismr.toJSONString());

      lh.setUrl(param.getUrl());
      lh.setAction("Gamas Get Impacted Service");
      lh.setMethod("POST");
      lh.setRequest(jsonRequestBody.toString());
      lh.setTicketId(ts.getTicketId());

      Request request =
          new Request.Builder()
              .url(apiConfig.getUrl())
              .addHeader("Authorization", "Bearer " + _token)
              .post(jsonRequestBody)
              .build();

      stringResponse = _RESTAPI.CALLAPI(request);
      lh.setResponse(stringResponse);
      request = null;
      jsonRequestBody = null;
      ehb.clear();
      ebb.clear();
      eheb.clear();
    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      try {
        lhdao.insertToLogHistory(lh);
      } catch (Exception ex) {
        logInfo.Log(getClass().getName(), ex.getMessage());
      }
      apiConfig = null;
      jo = null;
      gismr = null;
      eh = null;
      eb = null;
      param = null;
      paramDao = null;
      _RESTAPI = null;
    }

    return stringResponse;
  }
}
