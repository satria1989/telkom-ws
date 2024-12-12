/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.SelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.TicketAutomationDaoV4;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateProgressConfigurationDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mtaup
 */
public class UpdateProgressConfiguration extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Update Progress Configuration";

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        return "";
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "7.0.0";
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

    LogInfo info = new LogInfo();

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            TicketAutomationDaoV4 dao = new TicketAutomationDaoV4();
            UpdateProgressConfigurationDao pcDao = new UpdateProgressConfigurationDao();
            dao.getApiAttribut();

            String apiIdDefined = dao.apiId;
            String apiKeyDefined = dao.apiKey;

            String headerApiId = req.getHeader("api_id");
            String headerApiKey = req.getHeader("api_key");

            if ("POST".equals(req.getMethod())) {
                if (apiIdDefined.equals(headerApiId) && apiKeyDefined.equals(headerApiKey)) {

                    /**
                     *
                     * Status : - INPROGRESS - SUCCESS - FAILED
                     *
                     */
                    StringBuffer jb = new StringBuffer();
                    String line = null;
                    BufferedReader reader = req.getReader();
                    while ((line = reader.readLine()) != null) {
                        jb.append(line);
                    }

                    String bodyParam = jb.toString();

                    JSONParser parse = new JSONParser();
                    org.json.simple.JSONObject dataObj = (org.json.simple.JSONObject) parse.parse(bodyParam);
                    parse = null;

//                    JSONObject dataObj = new JSONObject(bodyParam);
                    String idTicket = dataObj.get("ticket_id") == null ? "" : dataObj.get("ticket_id").toString();
                    String action = dataObj.get("transaction_type") == null ? "" : dataObj.get("transaction_type").toString();
                    String status = dataObj.get("status") == null ? "" : dataObj.get("status").toString();
                    String note = dataObj.get("note") == null ? "" : dataObj.get("note").toString();
                    String data1 = dataObj.get("data_1") == null ? "" : dataObj.get("data_1").toString();
                    String data2 = dataObj.get("data_2") == null ? "" : dataObj.get("data_2").toString();
                    String data3 = dataObj.get("data_3") == null ? "" : dataObj.get("data_3").toString();
                    String data4 = dataObj.get("data_4") == null ? "" : dataObj.get("data_4").toString();
                    String data5 = dataObj.get("data_5") == null ? "" : dataObj.get("data_5").toString();

                    SelectCollections select = new SelectCollections();
                    Ticket ticket = new Ticket();
                    ticket = select.getDataTicket(idTicket);

                    if (!"".equals(idTicket) && !"".equals(action) && !"".equals(status)) {
                        if ("FAILED".equalsIgnoreCase(status) && "".equals(note)) {
                            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "note is required for status FAILED");
                        } else {
                            boolean statusInsert = pcDao.insertProgress(
                                    idTicket,
                                    action,
                                    status,
                                    note,
                                    data1,
                                    data2,
                                    data3,
                                    data4,
                                    data5,
                                    ticket);
                            if (statusInsert) {
                                info.Log(this.getClassName(), "status insert progress ont : " + statusInsert);
                                JSONObject obj = new JSONObject();
                                obj.put("code", "200");
                                obj.put("message", "update progress success");
                                obj.write(res.getWriter());
                            } else {
                                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "update progress failed");
                            }

                            if ("SUCCESS".equalsIgnoreCase(status)) {
                                info.Log(this.getClassName(), "SUCCESS CONDITION");
//                                createInstalationReport(idTicket, ticket);
                                JSONObject dataFromAcs = new JSONObject(data1);
                                pcDao.updateFormAcs(idTicket, dataFromAcs);
                                info.Log(this.getClassName(), "data1.idTicket : " + dataFromAcs.get("ticket_id"));

                            }
                        }

                    } else {
                        res.sendError(HttpServletResponse.SC_BAD_REQUEST, "ticket_id, action, status is required");
                    }

                } else {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
                }
            } else {
                res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allowed");
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
        }
    }

    /*
                        backup nama function supaya mudah ditrace
     */
//    public void createInstalationReport(String ticketId, Ticket ticket, String serialNumber, String serialNumberNew, String technicianCode) throws JSONException, IOException, Exception {
    public void updateOntInventoryPlTsel(String ticketId, Ticket ticket, String serialNumber, String serialNumberNew, String technicianCode) throws JSONException, IOException, Exception {
        Date now = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateNow1 = sdf1.format(now);
        String dateNow2 = sdf2.format(now);
        info.Log(this.getClassName(), "SUCCESS CONDITION");
        RESTAPI restApi = new RESTAPI();

        JSONObject req = new JSONObject();
        JSONObject createInstallationReportRequest = new JSONObject();
        JSONObject wsaHeader = new JSONObject();
        JSONObject wsaBody = new JSONObject();
        JSONObject jobResult = new JSONObject();
        JSONObject customer = new JSONObject();

        wsaHeader.put("externalId", ticketId);
        wsaHeader.put("timestamp", dateNow1);
        wsaHeader.put("callerID", "TELKOM_APPS");
        createInstallationReportRequest.put("wsaHeader", wsaHeader);

        wsaBody.put("sourceSystem", "INSERA");
        jobResult.put("workZone", ticket.getWorkzone());
        jobResult.put("salesFlag", "0");
        jobResult.put("salesPrice", "100000.0");
        info.Log(this.getClassName(), "TECHNICIAN CODE : " + ticket.getCoTechnicianCode());
        jobResult.put("installClass", "2");
        jobResult.put("transactionType", "3");
        jobResult.put("serialNumber", serialNumber);
        jobResult.put("serialNumberNew", serialNumberNew);
        jobResult.put("technicianCode", ticket.getCoTechnicianCode());
        jobResult.put("executionDate", dateNow2);
        jobResult.put("extOrderNo", ticket.getIdTicket());
        jobResult.put("serviceOrderId", ticket.getIdTicket());
        jobResult.put("sid", ticket.getServiceNumber()); // hsi
//        jobResult.put("sidOld", "1286X000001");

        wsaBody.put("jobResult", jobResult);

        String contactName = ticket.getContactName() == null ? "-" : ticket.getContactName();
        String lat = ticket.getLatitude() == null ? "-" : ticket.getLatitude();
        String longi = ticket.getLongitude() == null ? "-" : ticket.getLongitude();
        String address = ticket.getStreetAddress() == null ? "-" : ticket.getStreetAddress();

        customer.put("name", contactName);
        customer.put("address", address);
        customer.put("longitude", longi);
        customer.put("latitude", lat);
        wsaBody.put("customer", customer);

        createInstallationReportRequest.put("wsaBody", wsaBody);
        req.put("createInstallationReportRequest", createInstallationReportRequest);

        MasterParam masterParam = new MasterParam();
        MasterParamDao paramDao = new MasterParamDao();
//        masterParam = paramDao.getUrl("createInstalationReport");
        masterParam = paramDao.getUrl("updateOntInventoryPlTsel");

        String token = restApi.getTokenFlexible("get_access_token_apigwdev");

        restApi.callApiWithBearerToken(
                ticketId,
                //                "",
                "updateOntInventoryPlTsel",
                masterParam,
                req,
                token);

    }

    public void updateOntInventoryNonPlTsel(String ticketId, Ticket ticket, String serialNumber, String serialNumberNew, String technicianCode) throws JSONException, IOException, Exception {
        MasterParam masterParam = new MasterParam();
        MasterParamDao paramDao = new MasterParamDao();
        UpdateProgressConfigurationDao pcDao = new UpdateProgressConfigurationDao();
        RESTAPI restApi = new RESTAPI();

        masterParam = paramDao.getUrl("updateOntInventoryNonPlTsel");
        String token = restApi.getTokenFlexible("get_token_mytechnician");
        String seqTransNumb = pcDao.getSeqTransNumb();

        JSONObject req = new JSONObject();
        JSONObject apiIntRequest = new JSONObject();
        JSONObject eaiHeader = new JSONObject();
        JSONObject eaiBody = new JSONObject();

        eaiHeader.put("internalId", "");
        eaiHeader.put("externalId", "");
        eaiHeader.put("timestamp", "");
        eaiHeader.put("responseTimestamp", "");
        apiIntRequest.put("eaiHeader", eaiHeader);

        eaiBody.put("transaction_number", seqTransNumb);//sequence number date
        eaiBody.put("trc_type", 7);
        eaiBody.put("ext_order_no", ticketId);//noticket
        eaiBody.put("technician_code", technicianCode);
        eaiBody.put("service_id", ticket.getServiceNumber());
        eaiBody.put("old_serial_number", serialNumber);
        eaiBody.put("new_serial_number", serialNumberNew);
        eaiBody.put("caller", "MYTECH");
        apiIntRequest.put("eaiBody", eaiBody);

        req.put("apiIntRequest", apiIntRequest);

        restApi.callApiWithBearerToken(
                ticketId,
                "updateOntInventoryNonPlTsel",
                masterParam,
                req,
                token);
    }

}
