/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.iboosterDatin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.itasoft.telkom.oss.plugin.dao.GetMasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.SelectCollections;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.json.JSONObject;

/**
 * @author 12
 */
public class IboosterDatinService implements IboosterDatinInterface {

    SelectCollections selectCollections;
    LogInfo logInfo;
    ArrayManipulation arrayManipulation;
    CallRestAPI callRestAPI;
    String arrCustSegment[] = {"DBS", "DES", "DGS", "RBS", "DPS", "REG"};
    String arrServiceType[] = {"SL WDM", "ASTINet", "SDWAN", "VPNIP", "METRO-E"};
    String arrSourceTicket[] = {"CUSTOMER", "PROACTIVE"};

    public IboosterDatinService() {
        selectCollections = new SelectCollections();
        logInfo = new LogInfo();
        arrayManipulation = new ArrayManipulation();
        callRestAPI = new CallRestAPI();
    }

    @Override
    public ApiIboosterDatinModel ukurIboosterDatin(TicketStatus ticketStatus, String TOKEN) {
        JSONObject req = new JSONObject();
        JSONObject getHasilUkurBySIDRequest = new JSONObject();
        JSONObject eaiHeader = new JSONObject();
        JSONObject eaiBody = new JSONObject();
        JSONObject response = new JSONObject();
        ApiIboosterDatinModel apiIboosterDatinModel = null;
        ApiConfig apiConfig = new ApiConfig();
        GetMasterParamDao getMasterParamDao = new GetMasterParamDao();
        try {
            String ticketId = (ticketStatus.getTicketId() == null) ? "" : ticketStatus.getTicketId();
            String sId = (ticketStatus.getServiceNo() == null) ? "" : ticketStatus.getServiceNo();
            String custSegment = (ticketStatus.getCustomerSegment() == null) ? "" : ticketStatus.getCustomerSegment();
            String serviceType = (ticketStatus.getServiceType() == null) ? "" : ticketStatus.getServiceType();
            String sourceTicket = (ticketStatus.getSourceTicket() == null) ? "" : ticketStatus.getSourceTicket();

//            boolean kondisiCustSegment = arrayManipulation.SearchDataOnArray(arrCustSegment, custSegment);
//            boolean kondisiServiceType = arrayManipulation.SearchDataOnArray(arrServiceType, serviceType);
//            boolean kondisiSourceTicket = arrayManipulation.SearchDataOnArray(arrSourceTicket, sourceTicket);
//
//            if (kondisiCustSegment && kondisiServiceType && kondisiSourceTicket) {

            eaiHeader.put("externalId", "");
            eaiHeader.put("timestamp", "");
            getHasilUkurBySIDRequest.put("eaiHeader", eaiHeader);

            eaiBody.put("SID", sId);
            getHasilUkurBySIDRequest.put("eaiBody", eaiBody);

            req.put("getHasilUkurBySIDRequest", getHasilUkurBySIDRequest);

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, req.toString());

            apiConfig = getMasterParamDao.getUrl("ukurIboosterDatin");
            response = callRestAPI.callToApigw(apiConfig, requestBody, TOKEN, ticketId, "ukurIboosterDatin", req);

            if (response.has("status")) {
                if (response.getBoolean("status")) {
                    if (response.has("response")) {
                        String getKeyResponseMsg = response.getString("response");

                        JSONObject objResponseIbooster = new JSONObject(getKeyResponseMsg);
                        JSONObject getHasilUkurBySIDResponse = new JSONObject();
                        if (objResponseIbooster.has("getHasilUkurBySIDResponse")) {
                            getHasilUkurBySIDResponse = objResponseIbooster.getJSONObject("getHasilUkurBySIDResponse");
                        }
                        logInfo.Log(this.getClass().getName(), "obj response ibooster == " + objResponseIbooster);

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        apiIboosterDatinModel = objectMapper.readValue(getHasilUkurBySIDResponse.toString(), ApiIboosterDatinModel.class);

                        String onuRxPwrStr = apiIboosterDatinModel.getEaiBody().getOnu_rx_pwr();

                        if (onuRxPwrStr == null || onuRxPwrStr.equalsIgnoreCase("null") || onuRxPwrStr.isEmpty()) {
                            apiIboosterDatinModel.getEaiBody().setMeasurement_category("UNSPEC");
                        } else {
                            Float onuRxPwr = Float.parseFloat(onuRxPwrStr);
                            if (onuRxPwr <= Float.valueOf(-13) && onuRxPwr >= Float.valueOf(-24)) {
                                apiIboosterDatinModel.getEaiBody().setMeasurement_category("SPEC");
                            } else {
                                apiIboosterDatinModel.getEaiBody().setMeasurement_category("UNSPEC");
                            }
                        }
                    }
                } else {
                    if (apiIboosterDatinModel == null) {
                        apiIboosterDatinModel = new ApiIboosterDatinModel();
                    }
                }
            }

//            }
        } catch (Exception e) {
            logInfo.Error(getClass().getName(), e.getMessage(), e);
        } finally {
            req = null;
            getHasilUkurBySIDRequest = null;
            eaiBody = null;
            eaiHeader = null;
        }
        return apiIboosterDatinModel;
    }

}
