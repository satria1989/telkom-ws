/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.IboosterDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketWorkLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONObject;

/**
 * @author asani
 */
public class CheckIbooster extends Element implements PluginWebSupport {
  String pluginName = "Telkom New OSS - Ticket Incident Services - Check Ibooster";

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
    return "7.2";
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
    TicketStatus ticketStatus = new TicketStatus();
    LoadTicketDao loadTicket = new LoadTicketDao();
    IboosterDao iboosterDao = new IboosterDao();
    ListIbooster listIbooster = new ListIbooster();
    InsertTicketWorkLogsDao dao = new InsertTicketWorkLogsDao();
    
    try {
      
      dao.getApiAttribut();
      //Predefined Headers
      String apiIdDefined = dao.apiId;
      String apiKeyDefined = dao.apiKey;
      String apiSecretDefined = dao.apiSecret;
      
      String headerApiId = req.getHeader("api_id");
      String headerApiKey = req.getHeader("api_key");
      
      String method = req.getMethod();
      
      if ("GET".equalsIgnoreCase(method)) {
        
        if (apiIdDefined.equals(headerApiId) && apiKeyDefined.equals(headerApiKey)) {
          String TICKETID = req.getParameter("ticket_id");
      
          ticketStatus = loadTicket.LoadTicketByIdTicket(TICKETID);
          String serviceNo = ticketStatus.getServiceNo();
          listIbooster = iboosterDao.getIbooster(
              serviceNo, 
              "telkom.net", // REALM :: SEKARANG MASIH DI HARDCODE
              TICKETID
          );

          iboosterDao.updateIbooster(listIbooster, TICKETID);

          StringBuilder iboosterResult = new StringBuilder();
          if ("".equals(listIbooster.getMessage())) {
            iboosterResult.append("Kategori Ukur : " + ((listIbooster.getMeasurementCategory() == null) ? "NULL" : listIbooster.getMeasurementCategory().toUpperCase()) + "\r");
            iboosterResult.append("oper_status : " + ((listIbooster.getOperStatus() == null) ? "NULL" : listIbooster.getOperStatus().toUpperCase()) + "\r");
            iboosterResult.append("onu_rx_pwr : " + ((listIbooster.getOnuRxPwr() == null) ? "NULL" : listIbooster.getOnuRxPwr().toUpperCase()) + "\r");
            iboosterResult.append("onu_tx_pwr : " + ((listIbooster.getOnuTxPwr() == null) ? "NULL" : listIbooster.getOnuTxPwr().toUpperCase()) + "\r");
            iboosterResult.append("olt_rx_pwr : " + ((listIbooster.getOltRxPwr() == null) ? "NULL" : listIbooster.getOltRxPwr().toUpperCase()) + "\r");
            iboosterResult.append("olt_tx_pwr : " + ((listIbooster.getOltTxPwr() == null) ? "NULL" : listIbooster.getOltTxPwr().toUpperCase()) + "\r");
            iboosterResult.append("fiber_length : " + ((listIbooster.getFiberLength() == null) ? "NULL" : listIbooster.getFiberLength().toUpperCase()) + "\r");
            iboosterResult.append("status_jaringan : " + ((listIbooster.getStatusJaringan() == null) ? "NULL" : listIbooster.getStatusJaringan().toUpperCase()) + "\r");
            iboosterResult.append("identifier : " + ((listIbooster.getIdentifier() == null) ? "NULL" : listIbooster.getIdentifier().toUpperCase()) + "\r");
          } else {
            iboosterResult.append((listIbooster.getMessage() == null) ? "" : listIbooster.getMessage().toUpperCase());
          }


          JSONObject json = new JSONObject();
          DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          String waktuUkur = (dateFormat2.format(new Date()).toString()).toLowerCase();
          json.put("waktu_ukur", waktuUkur);
          json.put("hasil_ukur", iboosterResult.toString());
          json.put("kategori_ukur", listIbooster.getMeasurementCategory());

          json.write(res.getWriter());
        } else {
          res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
      } else {
        res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
      }
    } catch(Exception ex) {
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      info.Error(getClassName(), ex.getMessage(), ex);
    }
    
  }
}
