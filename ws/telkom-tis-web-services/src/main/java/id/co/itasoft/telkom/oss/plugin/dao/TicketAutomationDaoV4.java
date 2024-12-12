package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.ParseDate;
//import static id.co.itasoft.telkom.oss.plugin.PopulateDatek.json;
import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;

import static id.co.itasoft.telkom.oss.plugin.function.RESTAPI.JSON;

import id.co.itasoft.telkom.oss.plugin.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import javax.sql.DataSource;

import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;

import org.joget.commons.util.LogUtil;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * @author mtaup
 */
public class TicketAutomationDaoV4 {

    CallRestAPI callApi = new CallRestAPI();
    GetConnections gc = new GetConnections();
    LogInfo info = new LogInfo();

    public String apiId = "";
    public String apiKey = "";
    public String apiSecret = "";
    public String existing_ticketId = "";
    public String existing_serviceIid = "";
    public String existing_Ticketstatus = "";
    public String existing_classificationFlag = "";
    public String existing_device = "";
    GetMasterParamDao paramDao = new GetMasterParamDao();

    public String getBUD(String serviceNumber, String ticketNumber) throws JSONException, SQLException, Exception {
        String result = "";
        ApiConfig api = new ApiConfig();
        MasterParamDao par = new MasterParamDao();
        CallRestAPI callApi = new CallRestAPI();
        LogHistoryDao lhDao = new LogHistoryDao();

        api = par.getUrlapi("Pelanggan2Request");
        String token = callApi.getTokenApigwsit();

        org.json.JSONObject getPelanggan2Request = new org.json.JSONObject();
        org.json.JSONObject eaiHeader = new org.json.JSONObject();
        org.json.JSONObject eaiBody = new org.json.JSONObject();
        org.json.JSONObject json = new org.json.JSONObject();

        eaiHeader.put("externalId", "insera");
        eaiHeader.put("timestamp", "");
        getPelanggan2Request.put("eaiHeader", eaiHeader);

        eaiBody.put("nd", serviceNumber);
        eaiBody.put("ncli", "");
        getPelanggan2Request.put("eaiBody", eaiBody);
        json.put("getPelanggan2Request", getPelanggan2Request);

        RequestBody body = RequestBody.create(JSON, json.toString());
        String response = callApi.sendPostIbooster(api, body, token);

        org.json.JSONObject resObj = new org.json.JSONObject(response);
        if (resObj.has("getPelanggan2Response")) {
            org.json.JSONObject getPelanggan2Response = new org.json.JSONObject(resObj.get("getPelanggan2Response").toString());
            if (getPelanggan2Response.has("eaiBody")) {
                org.json.JSONObject eaiBodyres = new org.json.JSONObject(getPelanggan2Response.get("eaiBody").toString());
                if (eaiBodyres.has("cabasaDetails")) {
                    org.json.JSONObject cabasaDetails = new org.json.JSONObject(eaiBodyres.get("cabasaDetails").toString());
                    result = (cabasaDetails.getString("caLGEST") == null) ?
                            "" : cabasaDetails.getString("caLGEST");

                }
            }
        }

        org.json.JSONObject resBud = new org.json.JSONObject();
        resBud.put("resGetBud", response);

        lhDao.SENDHISTORY(
                ticketNumber,
                "GETBUD",
                api.getUrl(),
                "POST",
                json,
                resBud,
                200);
        return result;
    }

    public Map<String, String> getBUDDatin(String serviceNumber, String ticketNumber) throws JSONException, SQLException, Exception {
        Map<String, String> result = new HashMap<String, String>();
        ApiConfig api = new ApiConfig();
        MasterParamDao par = new MasterParamDao();
        CallRestAPI callApi = new CallRestAPI();
        LogHistoryDao lhDao = new LogHistoryDao();

        api = par.getUrlapi("getBudDatin");
        String token = callApi.getToken();

        org.json.JSONObject getDataSiebelRequest = new org.json.JSONObject();
        org.json.JSONObject eaiHeader = new org.json.JSONObject();
        org.json.JSONObject eaiBody = new org.json.JSONObject();
        org.json.JSONObject json = new org.json.JSONObject();

        eaiHeader.put("externalId", ticketNumber);
        eaiHeader.put("timestamp", "");
        getDataSiebelRequest.put("eaiHeader", eaiHeader);

        eaiBody.put("i_sid", serviceNumber);
        eaiBody.put("i_page_size", "10");
        eaiBody.put("i_page_num", "1");
        getDataSiebelRequest.put("eaiBody", eaiBody);
        json.put("getDataSiebelRequest", getDataSiebelRequest);

        RequestBody body = RequestBody.create(JSON, json.toString());
        String response = callApi.sendPostIbooster(api, body, token);

        String accounttype = "";
        String segment = "";
        org.json.JSONObject resObj = new org.json.JSONObject(response);
        if (resObj.has("getDataSiebelResponse")) {
            org.json.JSONObject getDataSiebelResponse = new org.json.JSONObject(resObj.get("getDataSiebelResponse").toString());
            if (getDataSiebelResponse.has("eaiBody")) {
                org.json.JSONObject eaiBodyres = new org.json.JSONObject(getDataSiebelResponse.get("eaiBody").toString());
                if (eaiBodyres.has("data")) {
                    if (!eaiBodyres.isNull("data")) {
                        org.json.JSONObject data = new org.json.JSONObject(eaiBodyres.get("data").toString());
                        if (data.has("o_pelanggan")) {
                            org.json.JSONObject o_pelanggan = new org.json.JSONObject(data.get("o_pelanggan").toString());
                            if (o_pelanggan.has("accounttype")) {
                                accounttype = o_pelanggan.getString("accounttype") == null ? "-" : o_pelanggan.getString("accounttype");
                                if (data.has("segment")) {
                                    segment = o_pelanggan.getString("segment") == null ? "-" : o_pelanggan.getString("segment");
                                }
                                result.put("accounttype", accounttype);
                                result.put("segment", segment);
                            }
                        }

                    }
                }
            }
        }

        org.json.JSONObject resBudDatin = new org.json.JSONObject();
        org.json.JSONObject reqBudDatin = new org.json.JSONObject();
        resBudDatin.put("resGetBudDatin", response);
        reqBudDatin.put("reqGetBudDatin", json.toString());

        lhDao.SENDHISTORY(
                ticketNumber,
                "getBudDatin",
                api.getUrl(),
                "POST",
                reqBudDatin,
                resBudDatin,
                200);
        return result;
    }

    public String getTicketIdFunction() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String ticketId = "";
        StringBuilder query;

        try {
            query = new StringBuilder();

            query.append(" SELECT  GET_INCIDENT_ID as ticket_id from dual ");

            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                ticketId = rs.getString("ticket_id");
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "ERROR GET TICKET ID :" + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            query = null;
        }
        return ticketId;
    }

    public void getApiAttribut() throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        query.append("select c_api_id, c_api_key, c_api_secret ")
                .append("from app_fd_tis_mst_api ")
                .append("where c_use_of_api = 'ticket_incident_api' ");

        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();

            if (rs.next()) {
                apiId = rs.getString("c_api_id");
                apiKey = rs.getString("c_api_key");
                apiSecret = rs.getString("c_api_secret");
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }

            query = null;
        }

    }

    public boolean checkTicket(String serviceId, String classificationFlag, String channel, String source) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        boolean result = true;
        if ("PROACTIVE".equalsIgnoreCase(source)) {
            query.append("SELECT c_id_ticket, c_service_id, c_classification_flag, c_ticket_status ")
                    .append("FROM app_fd_ticket ")
                    .append("WHERE c_service_id = ? ")
                    .append("AND c_classification_flag = ? ")
                    .append("AND c_ticket_status != 'CLOSED' ")
                    .append("AND c_obsolete_status != '1' ");
        } else {
            query.append("SELECT c_id_ticket, c_service_id, c_classification_flag, c_ticket_status ")
                    .append("FROM app_fd_ticket ")
                    .append("WHERE c_service_id = ? ")
                    .append("AND c_classification_flag = ? ")
                    .append("AND c_ticket_status != 'CLOSED' ")
                    .append("AND c_source_ticket != 'PROACTIVE' ")
                    .append("AND c_obsolete_status != '1' ");
        }

        try {
            con = gc.getJogetConnection();

            ps = con.prepareStatement(query.toString());
            ps.setString(1, serviceId);
            ps.setString(2, classificationFlag);
//            }
            rs = ps.executeQuery();

            if (rs.next()) {
                result = false;
                existing_ticketId = rs.getString("c_id_ticket");
                existing_serviceIid = rs.getString("c_service_id");
                existing_Ticketstatus = rs.getString("c_ticket_status");
                existing_classificationFlag = rs.getString("c_classification_flag");
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            query = null;
        }

        return result;

    }

    public boolean checkTicketGamas(String perangkat, String channel, String symptom) throws Exception {
//        LogUtil.info(this.getClass().getName(), "checkTicketGamas-symptom : " + symptom);
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        boolean result = true;
        query.append("SELECT c_id_ticket, c_service_id, c_classification_flag, c_ticket_status , c_perangkat ")
                .append("FROM app_fd_ticket ")
                .append("WHERE c_perangkat = ? ");
        if ("58".equalsIgnoreCase(channel)) {
            query.append("AND c_classification_path = ? ");
        }
        query.append("AND c_ticket_status != 'CLOSED' ");

        try {
            con = gc.getJogetConnection();

            ps = con.prepareStatement(query.toString());
            ps.setString(1, perangkat);
            if ("58".equalsIgnoreCase(channel)) {
                ps.setString(2, symptom);
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                result = false;
                existing_ticketId = rs.getString("c_id_ticket");
                existing_serviceIid = rs.getString("c_service_id");
                existing_Ticketstatus = rs.getString("c_ticket_status");
                existing_classificationFlag = rs.getString("c_classification_flag");
                existing_device = rs.getString("c_perangkat");
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            query = null;
        }

        return result;

    }

    public ListServiceInformationNew getListServiceInformation(HashMap<String, String> params, String ticketId) {

        ListServiceInformationNew data = new ListServiceInformationNew();
        LogHistoryDao lhDao = new LogHistoryDao();

        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("list_service_information_custom");
            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);

            String param = params.toString();
            org.json.JSONObject reqObj = new org.json.JSONObject(params);
            org.json.JSONObject resObj = new org.json.JSONObject(response.toString());
            lhDao.SENDHISTORY(
                    ticketId,
                    "CREATE_TICKET GET_SERVICE_ID",
                    apiConfig.getUrl(),
                    "GET",
                    reqObj,
                    resObj,
                    0);
            resObj = null;
            reqObj = null;
            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            if (data_obj.get("data") != null) {
                JSONArray arrData = (JSONArray) data_obj.get("data");
                for (Object object : arrData) {
                    JSONObject obj = (JSONObject) object;
                    data.setServiceId((obj.get("service_id") == null ? "" : obj.get("service_id").toString()).toUpperCase());
                    data.setServiceNumber((obj.get("service_number") == null ? "" : obj.get("service_number").toString()).toUpperCase());
                    data.setWorkzone((obj.get("workzone") == null ? "" : obj.get("workzone").toString()).toUpperCase());
                    data.setWorkzoneName((obj.get("workzone_name") == null ? "" : obj.get("workzone_name").toString()).toUpperCase());
                    data.setServiceCategory((obj.get("service_category") == null ? "" : obj.get("service_category").toString()).toUpperCase());
                    data.setCustomerID((obj.get("customer_id") == null ? "" : obj.get("customer_id").toString()).toUpperCase());
                    data.setCustomerCategory((obj.get("customer_category") == null ? "" : obj.get("customer_category").toString()).toUpperCase());
                    data.setCustomerSegment((obj.get("customer_segment") == null ? "" : obj.get("customer_segment").toString()).toUpperCase());
                    data.setCustomerName((obj.get("customer_name") == null ? "" : obj.get("customer_name").toString()).toUpperCase());
                    data.setCustomerType((obj.get("customer_type") == null ? "" : obj.get("customer_type").toString()).toUpperCase());
                    data.setWitel((obj.get("witel") == null ? "" : obj.get("witel").toString()).toUpperCase());
                    data.setReferenceNumber((obj.get("reference_number") == null ? "" : obj.get("reference_number").toString()).toUpperCase());
                    data.setRegion((obj.get("region") == null ? "" : obj.get("region").toString()).toUpperCase());
                    data.setCustomerPriority((obj.get("customer_priority") == null ? "" : obj.get("customer_priority").toString()).toUpperCase());
                    data.setServiceType((obj.get("service_type") == null ? "" : obj.get("service_type").toString()).toUpperCase());
                    data.setAddressCode((obj.get("address_code") == null ? "" : obj.get("address_code").toString()).toUpperCase());
                    data.setLongitude((obj.get("longitude") == null ? "" : obj.get("longitude").toString()).toUpperCase());
                    data.setServicePackage((obj.get("service_package") == null ? "" : obj.get("service_package").toString()).toUpperCase());
                    data.setStatus((obj.get("status") == null ? "" : obj.get("status").toString()).toUpperCase());
                    data.setCity((obj.get("city") == null ? "" : obj.get("city").toString()).toUpperCase());
                    data.setLatitude((obj.get("latitude") == null ? "" : obj.get("latitude").toString()).toUpperCase());
                    data.setPiloting((obj.get("piloting") == null ? "" : obj.get("piloting").toString()).toUpperCase());
                    data.setStreetAddress((obj.get("street_address") == null ? "" : obj.get("street_address").toString()).toUpperCase());
                    data.setServiceAddress((obj.get("service_address") == null ? "" : obj.get("service_address").toString()).toUpperCase());
                    data.setServiceIdDescription((obj.get("service_id_description") == null ? "" : obj.get("service_id_description").toString()).toUpperCase());
                    data.setPostalCode((obj.get("postal_code") == null ? "" : obj.get("postal_code").toString()).toUpperCase());
                    data.setCountry((obj.get("country") == null ? "" : obj.get("country").toString()).toUpperCase());
                    data.setService_flag((obj.get("service_flag") == null ? "" : obj.get("service_flag").toString()).toUpperCase());
                }
            }
        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "Error Call API :" + e.getMessage());
        }
        return data;
    }

    public String getWorkzone(HashMap<String, String> params, String ticketId) {

//        ListServiceInformationNew data = new ListServiceInformationNew();
        LogHistoryDao lhDao = new LogHistoryDao();
        String workzone = "";

        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("listPerangkatCra");
            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);

            String param = params.toString();
            org.json.JSONObject reqObj = new org.json.JSONObject(params);
            org.json.JSONObject resObj = new org.json.JSONObject(response.toString());
            lhDao.SENDHISTORY(
                    ticketId,
                    "CREATE_TICKET GET_WORKZONE_BY_PERANGKAT",
                    apiConfig.getUrl(),
                    "GET",
                    reqObj,
                    resObj,
                    0);
            resObj = null;
            reqObj = null;
            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");
            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;
                workzone = (obj.get("workzone") == null ? "" : obj.get("workzone").toString());
            }

        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "Error Call API :" + e.getMessage());
        }
        return workzone;
    }

    public ListServiceInformationNew getListServiceInformationWithoutLog(HashMap<String, String> params) {

        ListServiceInformationNew data = new ListServiceInformationNew();

        try {

            ApiConfig apiConfig = new ApiConfig();
//            apiConfig = paramDao.getUrl("list_service_information");
            apiConfig = paramDao.getUrl("list_service_information_custom");
            String response = "";
//            LogUtil.info(this.getClass().getName(), "httpClient : "+httpClient.toString());
            response = callApi.sendGetWithoutToken(apiConfig, params);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");
            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;
                data.setServiceId((obj.get("service_id") == null ? "" : obj.get("service_id").toString()));
                data.setServiceNumber(obj.get("service_number").toString());
                data.setWorkzone(obj.get("workzone").toString());
                data.setWorkzoneName(obj.get("workzone_name").toString());
                data.setServiceCategory((obj.get("service_category") == null ? "" : obj.get("service_category").toString()));
                data.setCustomerID(obj.get("customer_id").toString());
                data.setCustomerCategory(obj.get("customer_category").toString());
                data.setCustomerSegment(obj.get("customer_segment").toString());
                data.setCustomerName(obj.get("customer_name").toString());
                data.setCustomerType((obj.get("customer_type") == null ? "" : obj.get("customer_type").toString()));
                data.setWitel(obj.get("witel").toString());
                data.setReferenceNumber(obj.get("reference_number").toString());
                data.setRegion(obj.get("region").toString());
                data.setCustomerPriority(obj.get("customer_priority").toString());
                data.setServiceType((obj.get("service_type") == null ? "" : obj.get("service_type").toString()));
                data.setAddressCode((obj.get("address_code") == null ? "" : obj.get("address_code").toString()));
                data.setLongitude(obj.get("longitude").toString());
                data.setServicePackage(obj.get("service_package").toString());
                data.setStatus(obj.get("status").toString());
                data.setCity(obj.get("city").toString());
                data.setLatitude(obj.get("latitude").toString());
                data.setPiloting(obj.get("piloting").toString());
                data.setStreetAddress(obj.get("street_address").toString());
                data.setServiceAddress((obj.get("service_address") == null ? "" : obj.get("service_address").toString()));
                data.setServiceIdDescription((obj.get("service_id_description") == null ? "" : obj.get("service_id_description").toString()));
                data.setPostalCode(obj.get("postal_code").toString());
                data.setCountry(obj.get("country").toString());
            }

        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "Error Call API :" + e.getMessage());
        }
        return data;
    }

    public ListSymptom getListSymptom(HashMap<String, String> params, String ticketId) {
        ListSymptom data = new ListSymptom();
        LogHistoryDao lhDao = new LogHistoryDao();
        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("getActSolAndIncDom");
            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);

            String param = params.toString();
            org.json.JSONObject reqObj = new org.json.JSONObject(params);
            org.json.JSONObject resObj = new org.json.JSONObject(response.toString());
            lhDao.SENDHISTORY(
                    ticketId,
                    "CREATE_TICKET GET_SYMPTOM",
                    apiConfig.getUrl(),
                    "GET",
                    reqObj,
                    resObj,
                    0);
            resObj = null;
            reqObj = null;
            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            if (data_obj.get("data") != null) {
                JSONArray arrData = (JSONArray) data_obj.get("data");
                if (!arrData.isEmpty()) {
                    JSONObject obj = (JSONObject) arrData.get(0);
                    data.setParent(obj.get("parent").toString());
                    data.setAutoBackend(obj.get("auto_backend").toString());
                    data.setClassificationFlag(obj.get("classification_flag").toString());
                    data.setFinalCheck(obj.get("final_check").toString());
                    data.setClassificationCode(obj.get("classification_code").toString());
                    data.setDescription(obj.get("description").toString());
                    data.setClassificationDescription(obj.get("classification_description").toString());
                    data.setSolution(obj.get("solution").toString());
                    data.setClassificationType(obj.get("classification_type").toString());
                    data.setDispatchClassification(obj.get("dispatch_classification").toString());
                    data.setHierarchyType(obj.get("hierarchy_type").toString());
                    data.setHierarchyPath(obj.get("classification_path") == null ? "" : obj.get("classification_path").toString());
                    data.setClassificationCategory(obj.get("classification_category") == null ? "" : obj.get("classification_category").toString());
                    data.setLevel(obj.get("level") == null ? "" : obj.get("level").toString());
                    data.setFlagEvent(obj.get("flag_event") == null ? "" : obj.get("flag_event").toString());
                }
            }
//            if (data_obj.get("data") != null) {
//                JSONArray arrData = (JSONArray) data_obj.get("data");
//                for (Object object : arrData) {
//                    JSONObject obj = (JSONObject) object;
//                    data.setParent(obj.get("parent").toString());
//                    data.setAutoBackend(obj.get("auto_backend").toString());
//                    data.setClassificationFlag(obj.get("classification_flag").toString());
//                    data.setFinalCheck(obj.get("final_check").toString());
//                    data.setClassificationCode(obj.get("classification_code").toString());
//                    data.setDescription(obj.get("description").toString());
//                    data.setClassificationDescription(obj.get("classification_description").toString());
//                    data.setSolution(obj.get("solution").toString());
//                    data.setClassificationType(obj.get("classification_type").toString());
//                    data.setDispatchClassification(obj.get("dispatch_classification").toString());
//                    data.setHierarchyType(obj.get("hierarchy_type").toString());
//                    data.setHierarchyPath((obj.get("classification_path") == null ? "" : obj.get("classification_path").toString()));
//                    data.setClassificationCategory((obj.get("classification_category") == null ? "" : obj.get("classification_category").toString()));
//                    data.setLevel((obj.get("level") == null ? "" : obj.get("level").toString()));
//                    data.setFlagEvent((obj.get("flag_event") == null ? "" : obj.get("flag_event").toString()));
//
//                }
//            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        }
        return data;
    }

    public ListTkMapping getListTkMapping(HashMap<String, String> params, String ticketId) {

        ListTkMapping data = new ListTkMapping();
        LogHistoryDao lhDao = new LogHistoryDao();
        try {

            ApiConfig apiConfig = new ApiConfig();

            apiConfig = paramDao.getUrl("list_tk_mapping");
            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);

            String param = params.toString();
            org.json.JSONObject reqObj = new org.json.JSONObject(params);
            org.json.JSONObject resObj = new org.json.JSONObject(response.toString());
            lhDao.SENDHISTORY(
                    ticketId,
                    "CREATE_TICKET GET_OWNERGROUP",
                    apiConfig.getUrl(),
                    "GET",
                    reqObj,
                    resObj,
                    0);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            if (data_obj.get("data") != null) {
                JSONArray arrData = (JSONArray) data_obj.get("data");
                for (Object object : arrData) {
                    JSONObject obj = (JSONObject) object;
                    data.setCreatedByName(obj.get("createdByName").toString());
                    data.setDateCreated(obj.get("dateCreated").toString());
                    data.setModifiedByName(obj.get("modifiedByName").toString());
                    data.setFinalCheck(obj.get("final_check").toString());
                    data.setPersonOwnerGroup(obj.get("person_owner_group").toString());
                    data.setWorkzone(obj.get("workzone").toString());
                    data.setDescription(obj.get("description").toString());
                    data.setClassificationId(obj.get("classification_id").toString());
                    data.setDateModified(obj.get("dateModified").toString());
                    data.setCustomerSegment(obj.get("customer_segment").toString());
                    data.setProductName(obj.get("product_name").toString());

                    if (!"".equals(obj.get("person_owner_group").toString()) && obj.get("person_owner_group") != null) {
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        }
        return data;
    }

    public void insertToTableParentTicket(String originProcessId, String childId) throws Exception {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "INSERT INTO app_fd_parent_ticket (id, dateCreated, c_child_id) VALUES (?,sysdate,?) ";

        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query);
            ps.setString(1, originProcessId);
            ps.setString(2, childId);
            ps.executeUpdate();
            // LogUtil.info(this.getClass().getName(), "Insert To Table Parent Ticket");

        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            query = null;
        }
    }

    /*
    Perubahan Jum'at 16-09-2022
     */
    public boolean insertToTableTicket(ListServiceInformationNew lsi, ListTkMapping tkMapping, ListSymptom symptom, String OriginProcessId,
            String ticketNumber, String childId, String status, String ticketStatus, String reportedDate, String cutSegment, String workZone,
            String lastState, TicketAutomationModel ticket) throws Exception {

        String classificationFlag = symptom.getClassificationFlag();
        if (classificationFlag != null && classificationFlag.contains("_")) {
            classificationFlag = classificationFlag.substring(0, classificationFlag.indexOf("_"));
        }
        String paramServiceType = ticket.getService_type() == null ? "" : ticket.getService_type();

        String cont_symptom = lsi.getServiceType() + " | " + classificationFlag + " | " + symptom.getClassificationDescription();

        String detailVal = ticket.getDetails() == null ? "" : ticket.getDetails();
        if (detailVal.length() > 3800) {
            ticket.setDetails(detailVal.substring(0, 3800));
        }

        String summaryVal = ticket.getSummary() == null ? "" : ticket.getSummary();
        if (summaryVal.length() > 1000) {
            ticket.setSummary(summaryVal.substring(0, 1000));
        }

        String symptomFlagEvent = symptom.getFlagEvent() == null ? "" : symptom.getFlagEvent();
        String sidFlagEvent = lsi.getService_flag() == null ? "" : lsi.getService_flag();
        String eventVal = ("".equals(symptomFlagEvent)) ? sidFlagEvent : symptomFlagEvent;
        String customerType = "";
        String serviceNumber = "";
        String descCustomerId = ticket.getDescription_customerid() == null ? "" : ticket.getDescription_customerid();
        String customerAddress = ticket.getCustomer_address() == null ? "" : ticket.getCustomer_address();
        String customerId = ticket.getCustomer_id() == null ? "" : ticket.getCustomer_id();
        String customerPriority = ticket.getCustomer_priority() == null ? "" : ticket.getCustomer_priority();
        String serviceId = ticket.getService_id() == null ? "" : ticket.getService_id().trim();
        String channel = ticket.getChannel() == null ? "" : ticket.getChannel().trim();
        String lsiServiceCtegory = (lsi.getServiceCategory() == null ? "" : lsi.getServiceCategory()).toUpperCase();
        String serviceCtegoryParam = ticket.getSlg() == null ? "" : ticket.getSlg();
        
        String activationDate = "";
        String statusHvc = "";

        Map<String, String> mapHvc = new HashMap<String, String>();


        if (serviceId.contains("_")) {
            String[] partSid = serviceId.split("_");
            if (partSid.length > 2) {
                serviceNumber = partSid[1];
            } else {
                serviceNumber = serviceId;
            }
        } else {
            serviceNumber = serviceId;
        }
        String paramGetHvc = "";
        if ((!"".equals(serviceNumber) || !"".equals(lsi.getReferenceNumber())) &&
                ("DCS".equalsIgnoreCase(cutSegment) || "PL-TSEL".equalsIgnoreCase(cutSegment)) //                && !"21".equals(channel)
                ) {
            if ("VOICE".equalsIgnoreCase(paramServiceType) || "VOICE".equalsIgnoreCase(lsi.getServiceType())) {
                paramGetHvc = lsi.getReferenceNumber();
            } else {
                paramGetHvc = serviceNumber;
            }
            info.Log(getClass().getName(), "paramGetHvc" + paramGetHvc);
            mapHvc = getCustomerTypeFromDb(paramGetHvc, ticketNumber);
            customerType = mapHvc.get("HVC") == null ? "" : mapHvc.get("HVC");
            activationDate = mapHvc.get("ACTIVATION_DATE") == null ? "" : mapHvc.get("ACTIVATION_DATE");
            statusHvc = mapHvc.get("STATUS") == null ? "" : mapHvc.get("STATUS");
        }
        Connection con = null;
        PreparedStatement ps = null;

        boolean result = false;
        ParseDate parsingDate = new ParseDate();
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO  app_fd_ticket ( ")
                .append("id, c_service_no, c_slg, c_work_zone, c_customer_id, ")
                .append("c_customer_category, c_customer_segment, c_customer_type, c_witel, c_reference_number, ")
                .append("c_id_ticket, c_parent_id, c_service_id, c_description_customerid, c_region, ")
                .append("c_customer_priority, c_service_type, c_status, c_ticket_status, c_class_description, ")
                .append("c_owner_group, c_classification_type, c_classification_path, c_auto_backend, c_description_channel, ")
                .append("c_finalcheck, c_actual_solution, c_perangkat, c_street_address, c_street_no, ")
                .append("c_city, c_district, c_service_address,c_classification_flag ")
                //param
                .append(",c_booking_id, c_priority, c_description_closed_by, c_gaul, c_hard_complaint, ")
                .append("c_description_hard_complaint, c_lapul, c_urgensi, c_description_urgensi, c_tipe_stb, ")
                .append("c_external_ticketid, c_mycx_result, c_technology, c_package, c_mycx_category_result, ")
                .append("c_cluster_id, c_keterangan, c_tipe_ont, c_id_pengukuran, c_hostname_olt, ")
                .append("c_ip_olt, c_frame, c_olt_tx, c_olt_rx, c_onu_tx, ")
                .append("c_onu_rx, c_flag_fcr, c_status_covid19, c_source_lockdown, c_estimation, ")
                .append("c_scid, c_guarante_status, c_otp_Landing_page_tracking, c_status_ont, c_ip_bras, ")
                .append("c_sn_ont, c_jenis_byod, c_sn_byod, c_mitra_byod, c_start_guarantee_byod, ")
                .append("c_flag_pdd, c_ibooster_alert_id, c_flag_manja_by_cust, c_details, c_contact_name,")
                .append("c_contact_phone, c_contact_email, c_summary, c_source_ticket, c_channel, ")
                .append("c_reported_by, c_ossid, c_closed_by")
                .append(", c_reported_date, c_qc_voice_ivr_time, c_stop_time, c_solution , c_subsidiary, c_created_ticket_by, ")
                .append("c_solution_code, c_last_state, c_child_gamas, c_rk_information, c_service_ps_date, c_billing_status, c_realm, ")
                .append("c_impact, c_service_category, c_description_work_zone, c_description_serviceid, c_latitude, c_longitude, c_zip_code, c_country, ")
                .append("c_symptom, c_slg_ncx, c_hierarchy_path, c_obsolete_status, c_incident_domain, c_kode_produk, c_classification_category, c_level_gamas, c_related_to_gamas, ")
                .append("c_flag_event, c_sqm_category, c_jumlah_site_tsel, c_flag_mywifi, c_external_ticket_tier3, c_manufacture_ont, c_slg_ttr, c_activation_date, c_status_billing, c_manage_by ")
                .append(",dateCreated, dateModified ) ")
                .append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?")
                .append(",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?")
                .append(",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?")
                .append(",sysdate,sysdate) ");

        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, childId);
            ps.setString(2, serviceNumber);
            ps.setString(3, ("".equals(serviceCtegoryParam) ? lsiServiceCtegory : serviceCtegoryParam));
            ps.setString(4, workZone);
            ps.setString(5, ("".equals(customerId) ? lsi.getCustomerID() : customerId));
            ps.setString(6, (("".equals(ticket.getCustomer_category()) || ticket.getCustomer_category() == null) ? lsi.getCustomerCategory() : (ticket.getCustomer_category() == null ? "" : ticket.getCustomer_category())));
            ps.setString(7, cutSegment);
            ps.setString(8, customerType);
            ps.setString(9, lsi.getWitel());
            ps.setString(10, lsi.getReferenceNumber());
            ps.setString(11, ticketNumber);
            ps.setString(12, OriginProcessId);
            ps.setString(13, serviceId);
            ps.setString(14, ("".equals(descCustomerId) ? lsi.getCustomerName() : descCustomerId));
            ps.setString(15, lsi.getRegion());
            ps.setString(16, ("".equals(customerPriority) ? lsi.getCustomerPriority() : customerPriority));
            ps.setString(17, ("".equals(paramServiceType) ? lsi.getServiceType() : paramServiceType));
            ps.setString(18, status);
            ps.setString(19, ticketStatus);
            ps.setString(20, symptom.getDescription());
            ps.setString(21, (tkMapping.getPersonOwnerGroup() == null ? "" : tkMapping.getPersonOwnerGroup()));
            ps.setString(22, symptom.getClassificationType());
            ps.setString(23, symptom.getClassificationCode());
            ps.setString(24, symptom.getAutoBackend());
            ps.setString(25, "");
            ps.setString(26, (symptom.getFinalCheck() == null ? "" : symptom.getFinalCheck()));
            ps.setString(27, (ticket.getActual_solution()));
            ps.setString(28, (ticket.getPerangkat()));
            ps.setString(29, ((ticket.getStreet_address() == null || "".equals(ticket.getStreet_address())) ? lsi.getStreetAddress() : (ticket.getStreet_address() == null ? "" : ticket.getStreet_address())));
            ps.setString(30, (ticket.getStreet_no() == null ? "" : ticket.getStreet_no()));
            ps.setString(31, ((ticket.getCity() == null || "".equals(ticket.getCity())) ? lsi.getCity() : (ticket.getCity() == null ? "" : ticket.getCity())));
            ps.setString(32, (ticket.getDistrict() == null ? "" : ticket.getDistrict()));
            ps.setString(29, ("".equals(customerAddress) ? lsi.getStreetAddress() : customerAddress));
            ps.setString(30, "");
            ps.setString(31, lsi.getCity());
            ps.setString(32, "");
            ps.setString(33, lsi.getServiceAddress());
            ps.setString(34, symptom.getClassificationFlag());

            ps.setString(35, (ticket.getBooking_id()));
            ps.setString(36, (ticket.getPriority()));
            ps.setString(37, "");
            ps.setString(38, (ticket.getGaul()));
            ps.setString(39, (ticket.getHard_complaint()));
            ps.setString(40, "");
            ps.setString(41, (ticket.getLapul()));
            ps.setString(42, (ticket.getUrgensi()));
            ps.setString(43, "");
            ps.setString(44, (ticket.getTipe_stb()));
            ps.setString(45, (ticket.getExternal_ticketid()));
            ps.setString(46, (ticket.getHasil_ukur_mycx()));
            ps.setString(47, (ticket.getTechnology()));
            ps.setString(48, (ticket.getPackages()));
            ps.setString(49, (ticket.getKategory_hasil_ukur_mycx()));
            ps.setString(50, (ticket.getCluster_id()));
            ps.setString(51, (ticket.getKeterangan_clustername()));
            ps.setString(52, (ticket.getTipe_ont()));
            ps.setString(53, (ticket.getId_pengukuran()));
            ps.setString(54, (ticket.getHostname_olt()));
            ps.setString(55, (ticket.getIp_olt()));
            ps.setString(56, (ticket.getFrame_slot_port_onuid()));
            ps.setString(57, (ticket.getOlt_tx()));
            ps.setString(58, (ticket.getOlt_rx()));
            ps.setString(59, (ticket.getOnu_tx()));
            ps.setString(60, (ticket.getOnu_rx()));
            ps.setString(61, (ticket.getFlag_fcr()));
            ps.setString(62, (ticket.getStatus_covid_19()));
            ps.setString(63, (ticket.getSource_lockdown()));
            ps.setString(64, (ticket.getEstimation()));
            ps.setString(65, (ticket.getScid()));
            ps.setString(66, (ticket.getGuarantee_non_guarantee()));
            ps.setString(67, (ticket.getOtp_landing_page_tracking()));
            ps.setString(68, (ticket.getStatus_ont()));
            ps.setString(69, (ticket.getIp_bras()));
            ps.setString(70, (ticket.getSn_ont()));
            ps.setString(71, (ticket.getJenis_byod()));
            ps.setString(72, (ticket.getSn_byod()));
            ps.setString(73, (ticket.getMitra_byod()));
            ps.setString(74, (ticket.getStart_guarantee_byod()));
            ps.setString(75, (ticket.getFlag_pdd()));
            ps.setString(76, (ticket.getIbooster_alert_id()));
            ps.setString(77, (ticket.getFlag_manja_by_cust()));
            ps.setString(78, (ticket.getDetails()));
            ps.setString(79, (ticket.getContact_name()));
            ps.setString(80, (ticket.getContact_phone()));
            ps.setString(81, (ticket.getContact_email()));
            ps.setString(82, ((("".equals(eventVal) || "null".equals(eventVal)) ? "" : eventVal + " - ") + ticket.getSummary()));
            ps.setString(83, (ticket.getSource_ticket()));
            ps.setString(84, (ticket.getChannel()));
            ps.setString(85, (ticket.getReported_by()));
            ps.setString(86, (ticket.getOssid()));
            ps.setString(87, (ticket.getClosed_by()));
            ps.setTimestamp(88, Timestamp.valueOf(reportedDate));
//            
            if ("".equals(ticket.getQc_voice_ivr_time()) || ticket.getQc_voice_ivr_time() == null) {
                ps.setTimestamp(89, null);
            } else {
                ps.setTimestamp(89, Timestamp.valueOf(parsingDate.parse(ticket.getQc_voice_ivr_time())));
            }
            if (ticket.getStop_time() == null) {
                ps.setTimestamp(90, null);
            } else {
                ps.setTimestamp(90, Timestamp.valueOf(parsingDate.parse(ticket.getStop_time())));
            }

            if ("FISIK".equalsIgnoreCase(symptom.getClassificationType())) {
                ps.setString(91, symptom.getSolution());
            } else {
                ps.setString(91, "");
            }

            ps.setString(92, (ticket.getSubsidiary()));
            ps.setString(93, "API");
            ps.setString(94, symptom.getSolution());
            ps.setString(95, lastState);
            ps.setString(96, "FALSE");
            ps.setString(97, (ticket.getRk_information()));
            ps.setString(98, (ticket.getService_ps_date()));
            ps.setString(99, (ticket.getBilling_status()));
            ps.setString(100, (ticket.getRealm()));
            ps.setString(101, (ticket.getImpact()));
            ps.setString(102, (lsi.getServiceCategory()));
            ps.setString(103, (lsi.getWorkzoneName()));
            ps.setString(104, (lsi.getServiceIdDescription()));
            ps.setString(105, ((ticket.getLatitude() == null || "".equals(ticket.getLatitude())) ? lsi.getLatitude() : (ticket.getLatitude() == null ? "" : ticket.getLatitude())));
            ps.setString(106, ((ticket.getLongitude() == null || "".equals(ticket.getLongitude())) ? lsi.getLongitude() : (ticket.getLongitude() == null ? "" : ticket.getLongitude())));
            ps.setString(107, ((ticket.getZip_code() == null || "".equals(ticket.getZip_code())) ? lsi.getPostalCode() : (ticket.getZip_code() == null ? "" : ticket.getZip_code())));
            ps.setString(108, ((ticket.getCountry() == null || "".equals(ticket.getCountry())) ? lsi.getCountry() : (ticket.getCountry() == null ? "" : ticket.getCountry())));
            ps.setString(109, cont_symptom);
            ps.setString(110, ticket.getSlg_ncx());
            ps.setString(111, symptom.getHierarchyPath());
            ps.setString(112, "0");
            ps.setString(113, ticket.getIncident_domain());
            ps.setString(114, ticket.getKode_produk());
            ps.setString(115, symptom.getClassificationCategory());
            ps.setString(116, symptom.getLevel());
            ps.setString(117, "0");
            ps.setString(118, ("".equals(symptomFlagEvent)) ? sidFlagEvent : symptomFlagEvent);
            ps.setString(119, ticket.getSqm_category());
            ps.setString(120, ticket.getJumlah_site_tsel());
            ps.setString(121, ticket.getFlag_mywifi());
            ps.setString(122, ticket.getExternal_ticket_tier3());
            ps.setString(123, ticket.getManufacture_ont());
            ps.setString(124, ticket.getSlg_ttr());
            ps.setString(125, activationDate);
            ps.setString(126, statusHvc);
            ps.setString(127, ticket.getManage_by());

            int i = ps.executeUpdate();

            if (i > 0) {
                result = true;
                // LogUtil.info(this.getClass().getName(), "AUTOMATION TICKET INCIDENT - INSERTED");
            }

        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            query = null;
        }

        return result;
    }

    public String getIgnoredCustSegment() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String result = "";
        StringBuilder query;

        try {
            query = new StringBuilder();

            query.append(" SELECT c_cst_segment_ignored FROM APP_FD_configuration ");

            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getString("c_cst_segment_ignored");
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "ERROR GET TICKET ID :" + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            query = null;
        }
        return result;
    }

    public boolean getParam(String paramName, String paramCode) throws SQLException, SQLException {
        boolean status = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        String query = "SELECT c_param_description FROM app_fd_param WHERE c_param_name = ? AND c_param_code = ? ";
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, paramName);
            ps.setString(2, paramCode);
            result = ps.executeQuery();
            if (result.next()) {
                status = true;
            }

        } catch (SQLException ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            query = null;
            try {
                result.close();
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                ps.close();
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                con.close();
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }

        }
        return status;

    }

    public Map<String, String> getCustomerTypeFromDb(String serviceNumber, String ticketId) throws SQLException, SQLException, JSONException {
        Map<String, String> mapHVC = new HashMap<String, String>();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
//        String query = " SELECT HVC, ACTIVATION_DATE, STATUS FROM DWH_INSERA.LIS_WSA_TSEL WHERE bb_id = ? "; // Untuk server PROD
        String query = " SELECT HVC, ACTIVATION_DATE, STATUS FROM HVC_Test WHERE bb_id = ? "; // Untuk server DEV
        info.Log(getClass().getName(), "Service No : " + serviceNumber);
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, serviceNumber);
            result = ps.executeQuery();
            if (result.next()) {
                mapHVC.put("HVC", result.getString("HVC"));
                mapHVC.put("ACTIVATION_DATE", result.getString("ACTIVATION_DATE"));
                mapHVC.put("STATUS", result.getString("STATUS"));
            }

        } catch (SQLException ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            query = null;
            try {
                result.close();
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                ps.close();
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                con.close();
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }

            LogHistoryDao lhDao = new LogHistoryDao();
            org.json.JSONObject req = new org.json.JSONObject();
            org.json.JSONObject res = new org.json.JSONObject();
            req.put("service_number_ticket", serviceNumber);
            res.put("result_get_hvc", mapHVC.get("HVC"));
            lhDao.SENDHISTORY(
                    ticketId,
                    "GET_HVC",
                    query,
                    "QUERY",
                    req,
                    res,
                    0);

            res = null;
            req = null;
            lhDao = null;

        }
        return mapHVC;
    }

    public org.json.JSONObject getTechnicalData(String ticketId, String serviceNumber, String serviceId, String parentId, String flag) {
        org.json.JSONObject response = new org.json.JSONObject();
        org.json.JSONObject result = null;
        CallRestAPI restApi = new CallRestAPI();

        try {
            boolean status = false;
            ApiConfig apiConfig = new ApiConfig();
            org.json.JSONObject req = new org.json.JSONObject();
            org.json.JSONObject getDataTeknisRequest = new org.json.JSONObject();
            org.json.JSONObject eaiHeader = new org.json.JSONObject();
            org.json.JSONObject eaiBody = new org.json.JSONObject();
            List<TechnicalDataModel> technicalDataModels = new ArrayList<>();

            apiConfig = paramDao.getUrl("technical_data_new");
            String token = callApi.getToken();
            String sto = "";

            eaiHeader.put("externalId", "");
            eaiHeader.put("timestamp", "");
            getDataTeknisRequest.put("eaiHeader", eaiHeader);
            eaiBody.put("cfsID", serviceNumber);
            getDataTeknisRequest.put("eaiBody", eaiBody);

            req.put("getDataTeknisRequest", getDataTeknisRequest);

            RequestBody body = RequestBody.create(JSON, req.toString());

            if ("CREATE".equalsIgnoreCase(flag)) {
                response = restApi.callToApigw(apiConfig, body, token, ticketId, "CREATE_TICKET LOAD_DATEK", req);
            } else if ("RELOAD".equalsIgnoreCase(flag)) {
                response = restApi.callToApigw(apiConfig, body, token, ticketId, "RELOAD_DATEK", req);
            }

            info.Log(this.getClass().getName(), "INIRESDATEK FUNCTION : " + response.toString());
            result = new org.json.JSONObject();
            if (response.has("status")) {
                if (response.getBoolean("status")) {
                    if (response.has("response")) {
                        org.json.JSONObject jsonObject = new org.json.JSONObject(response.get("response").toString());
                        if (jsonObject.has("status")) {
                            if ("Success".equalsIgnoreCase(jsonObject.get("status").toString())) {
                                status = true;
                                result.put("status", status);
                            }
                        }
                        String serviceNameParam = "";
                        org.json.JSONArray dataArray = jsonObject.getJSONArray("data");

                        if (serviceId.contains("_")) {
                            String[] partSid = serviceId.split("_");
                            System.out.println("partSid.length : " + partSid.length);
                            if (partSid.length > 2) {
                                serviceNameParam = partSid[1] + "_" + partSid[2];
                            }
                        }

                        for (int i = 0; i < dataArray.length(); i++) {
                            org.json.JSONObject dataObject = dataArray.getJSONObject(i);
                            org.json.JSONObject total = dataObject.getJSONObject("TOTAL");
                            org.json.JSONObject totalService = total.getJSONObject("SERVICE");
                            org.json.JSONObject data = dataObject.getJSONObject("DATA");
                            org.json.JSONObject serviceData = data.getJSONObject("SERVICE");
                            org.json.JSONObject attributeData = data.getJSONObject("ATTRIBUTE");
                            Iterator<String> serviceKeys = serviceData.keys();
                            Iterator<String> attributeKeys = attributeData.keys();

                            while (serviceKeys.hasNext()) {
                                String key = serviceKeys.next();
                                org.json.JSONArray serviceArray = serviceData.optJSONArray(key);
                                LogUtil.info(this.getClass().getName(), "## serviceArray.length() : " + serviceArray.length());
                                if (serviceArray != null && serviceArray.length() > 0) {
                                    for (int j = 0; j < serviceArray.length(); j++) {
                                        org.json.JSONObject item = new org.json.JSONObject(serviceArray.get(j).toString());
                                        Iterator<String> itemKeys = item.keys();

                                        String ND = "";
                                        if (item.has("ND")) {
                                            ND = item.optString("ND", "");
                                        }
                                        if (item.has("SERVICE_NAME")) {
                                            if (serviceId.equalsIgnoreCase(item.getString("SERVICE_NAME")) ||
                                                        item.getString("SERVICE_NAME").contains(serviceNameParam)
                                                ) {
                                                    setData(item, result);
                                                    while (itemKeys.hasNext()) {
                                                        TechnicalDataModel technicalDataModel = new TechnicalDataModel();
                                                        String portName = itemKeys.next();
                                                        String deviceName = item.optString(portName, "");
                                                        technicalDataModel.setPortName(portName);
                                                        technicalDataModel.setDeviceName(deviceName);
                                                        technicalDataModel.setParentId(parentId);
                                                        technicalDataModel.setTicketId(ticketId);
                                                        technicalDataModels.add(technicalDataModel);
                                                    }
                                                }
                                            }else if (item.has("CFS_ID")) {
                                                        if (serviceId.equalsIgnoreCase(item.getString("CFS_ID")) ||
                                                            ND.equalsIgnoreCase(serviceId) ||
                                                            serviceNumber.equalsIgnoreCase(item.getString("CFS_ID")) ||
                                                            ND.equalsIgnoreCase(serviceNumber)
                                                            ) {
                                                                setData(item, result);
                                                                while (itemKeys.hasNext()) {
                                                                    TechnicalDataModel technicalDataModel = new TechnicalDataModel();
                                                                    String portName = itemKeys.next();
                                                                    String deviceName = item.optString(portName, "");
                                                                    technicalDataModel.setPortName(portName);
                                                                    technicalDataModel.setDeviceName(deviceName);
                                                                    technicalDataModel.setParentId(parentId);
                                                                    technicalDataModel.setTicketId(ticketId);
                                                                    technicalDataModels.add(technicalDataModel);
                                                                }
                                                            }
                                                        }else if (item.has("SERVICE_NUMBER")) {
                                            if (serviceNumber.equals(item.getString("SERVICE_NUMBER"))) {
                                                setData(item, result);
                                                while (itemKeys.hasNext()) {
                                                    TechnicalDataModel technicalDataModel = new TechnicalDataModel();
                                                    String portName = itemKeys.next();
                                                    String deviceName = item.optString(portName, "");
                                                    technicalDataModel.setPortName(portName);
                                                    technicalDataModel.setDeviceName(deviceName);
                                                    technicalDataModel.setParentId(parentId);
                                                    technicalDataModel.setTicketId(ticketId);
                                                    technicalDataModels.add(technicalDataModel);
                                                }
                                            }
                                        } else if (item.has("SITEID")) {
                                            if (serviceNumber.equals(item.getString("SITEID"))) {
                                                setData(item, result);
                                                while (itemKeys.hasNext()) {
                                                    TechnicalDataModel technicalDataModel = new TechnicalDataModel();
                                                    String portName = itemKeys.next();
                                                    String deviceName = item.optString(portName, "");
                                                    technicalDataModel.setPortName(portName);
                                                    technicalDataModel.setDeviceName(deviceName);
                                                    technicalDataModel.setParentId(parentId);
                                                    technicalDataModel.setTicketId(ticketId);
                                                    technicalDataModels.add(technicalDataModel);
                                                }
                                            }
                                        }
                                                    }
                                        }
                                    }

                                    result.put("RK_ODC", ""); //di set dulu empty string
                                    while (attributeKeys.hasNext()) {

                                        String key = attributeKeys.next();
                                        org.json.JSONArray attributeArray = attributeData.optJSONArray(key);
                                        if (attributeArray != null && !attributeArray.isEmpty()) {
                                            info.Log(this.getClass().getName(), "attribute yang ada == " + attributeArray);

                                            for (int j = 0; j < attributeArray.length(); j++) {

                                                if (attributeArray.get(j) instanceof org.json.JSONArray) {
                                                    org.json.JSONArray arrItem = attributeArray.getJSONArray(j);
                                                    for (int k = 0; k < arrItem.length(); k++) {
                                                        TechnicalDataModel technicalDataModel = new TechnicalDataModel();
                                                        org.json.JSONObject item = new org.json.JSONObject(arrItem.get(k).toString());
                                                        String keyAttribute = "";
                                                        if (item.has("ATTRIBUTES_DETAIL")) {
                                                            keyAttribute = item.optString("ATTRIBUTES_DETAIL", "");
                                                        } else if (item.has("ATTRIBUTE_DETAIL")) {
                                                            keyAttribute = item.optString("ATTRIBUTE_DETAIL", "");
                                                        }
                                                        String valueAttribute = item.optString("VALUE", "");

                                                        if ("RK_ODC".equalsIgnoreCase(keyAttribute)) {
                                                            result.put("RK_ODC", valueAttribute);
                                                        }

                                                        if (!"".equals(keyAttribute)) {
                                                            technicalDataModel.setPortName(keyAttribute);
                                                            technicalDataModel.setDeviceName(valueAttribute);
                                                            technicalDataModel.setTicketId(ticketId);
                                                            technicalDataModel.setParentId(parentId);
                                                            technicalDataModels.add(technicalDataModel);
                                                        }
                                                    }
                                                } else if (attributeArray.get(j) instanceof org.json.JSONObject) {
                                                    TechnicalDataModel technicalDataModel = new TechnicalDataModel();
                                                    org.json.JSONObject item = new org.json.JSONObject(attributeArray.get(j).toString());
                                                    String keyAttribute = "";
                                                    if (item.has("ATTRIBUTES_DETAIL")) {
                                                        keyAttribute = item.optString("ATTRIBUTES_DETAIL", "");
                                                    } else if (item.has("ATTRIBUTE_DETAIL")) {
                                                        keyAttribute = item.optString("ATTRIBUTE_DETAIL", "");
                                                    }
                                                    String valueAttribute = item.optString("VALUE", "");

                                                    if ("RK_ODC".equalsIgnoreCase(keyAttribute)) {
                                                        result.put("RK_ODC", valueAttribute);
                                                    }

                                                    if (!"".equals(keyAttribute)) {
                                                        technicalDataModel.setPortName(keyAttribute);
                                                        technicalDataModel.setDeviceName(valueAttribute);
                                                        technicalDataModel.setTicketId(ticketId);
                                                        technicalDataModel.setParentId(parentId);
                                                        technicalDataModels.add(technicalDataModel);
                                                    }
                                                }
//                                        else if (item.has("SITEID")) {
//                                            if (serviceNumber.equals(item.getString("SITEID"))) {
//                                                setData(item, result);
//                                            }
//                                        }
                                            }
                                        }
                                    }
                                }
                            }
                            TechnicalDataDao technicalDataDao = new TechnicalDataDao();
                            if ("RELOAD".equalsIgnoreCase(flag)) {
                                technicalDataDao.deleteTechnicalData(parentId);

                                sto = result.getString("STO");
                                HashMap<String, String> paramListWorkzone = new HashMap<>();
                                paramListWorkzone.put("sto_code", sto);
                                ListWorkzone listWorkzone = this.getListWorkzone(paramListWorkzone, ticketId);
                                result.put("witel", listWorkzone.getWitel());
                                result.put("region", listWorkzone.getRegion());
                                result.put("sto_name", listWorkzone.getStoName());
                            }
                            technicalDataDao.insertBatchOutTechnicalData(technicalDataModels);
                            info.Log(this.getClass().getName(), "id == " + parentId);
                            info.Log(this.getClass().getName(), " ticket id == " + ticketId);
                        }
                    }

                    response = null;
                    apiConfig = null;
                    eaiBody = null;
                    getDataTeknisRequest = null;
                    eaiHeader = null;
                    eaiBody = null;
                }catch (JSONException ex) {
            info.Error(this.getClass().getName(), "getTechnicalData", ex);
        }catch (Exception ex) {
            info.Error(this.getClass().getName(), "getTechnicalData", ex);
        }
                return result;
            }

    

    

    

    

    public String getBlockedChannel() throws SQLException, SQLException {
        String channel = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        String query = "SELECT c_blocked_channel FROM app_fd_configuration where id = ? ";
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, "1");
            result = ps.executeQuery();
            if (result.next()) {
                channel = (result.getString("c_blocked_channel") == null ? "" : result.getString("c_blocked_channel"));
            }

        } catch (SQLException ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            query = null;
            try {
                result.close();
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                ps.close();
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                con.close();
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }

        }
        return channel;

    }

    private void setData(org.json.JSONObject item, org.json.JSONObject result) throws JSONException {
        if (item.has("STO")) {
            result.put("STO", item.get("STO") == null ? "" : item.get("STO").toString());
        }
        if (item.has("SERVICE_NUMBER")) {
            result.put("SERVICE_NUMBER", item.get("SERVICE_NUMBER") == null ? "" : item.get("SERVICE_NUMBER").toString());
        }

        if (item.has("ADDRESS")) {
            result.put("ADDRESS", item.get("ADDRESS") == null ? "" : item.get("ADDRESS").toString());
        }
        if (item.has("SERVICE_PARTY")) {
            result.put("CUSTOMER_ID", item.get("SERVICE_PARTY") == null ? "" : item.get("SERVICE_PARTY").toString());
        }
        if (item.has("SERVICE_PARTY_NAME")) {
            result.put("CUSTOMER_ID_DESCRIPTION", item.get("SERVICE_PARTY_NAME") == null ? "" : item.get("SERVICE_PARTY_NAME").toString());
        }
        if (item.has("CUSTOMER_ID")) {
            result.put("CUSTOMER_ID", item.get("CUSTOMER_ID") == null ? "" : item.get("CUSTOMER_ID").toString());
        }
        if (item.has("CUSTOMER_NAME")) {
            result.put("CUSTOMER_ID_DESCRIPTION", item.get("CUSTOMER_NAME") == null ? "" : item.get("CUSTOMER_NAME").toString());
        }
        if (item.has("SERVICETYPE")) {
            result.put("SERVICE_TYPE", item.get("SERVICETYPE") == null ? "" : item.get("SERVICETYPE").toString());
        }
        if (item.has("EVENT_FLAG")) {
            result.put("EVENT_FLAG", item.get("EVENT_FLAG_DETAIL") == null ? "" : item.get("EVENT_FLAG_DETAIL").toString());
        }
        if (item.has("SLA_CATEGORY_DETAIL")) {
            result.put("SLA_CATEGORY_DETAIL", item.get("SLA_CATEGORY_DETAIL") == null ? "" : item.get("SLA_CATEGORY_DETAIL").toString());
        }
        if (item.has("SERVICE_CONTACT")) {
            info.Log(this.getClass().getName(), "SERVICE CONTACT : " + item.getString("SERVICE_CONTACT"));
            result.put("SERVICE_CONTACT", item.get("SERVICE_CONTACT") == null ? "" : item.get("SERVICE_CONTACT").toString());
        }
        if (item.has("TECHNOLOGY")) {
            result.put("TECHNOLOGY", item.get("TECHNOLOGY") == null ? "" : item.get("TECHNOLOGY").toString());
        }
    }

    public ListWorkzone getListWorkzone(HashMap<String, String> params, String ticketId) {
        ListWorkzone data = new ListWorkzone();
        LogHistoryDao lhDao = new LogHistoryDao();
        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("list_workzone");
            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);

            String param = params.toString();
            org.json.JSONObject reqObj = new org.json.JSONObject(params);
            org.json.JSONObject resObj = new org.json.JSONObject(response);
            lhDao.SENDHISTORY(
                    ticketId,
                    "GET_WORKZONE",
                    apiConfig.getUrl(),
                    "GET",
                    reqObj,
                    resObj,
                    0);
            resObj = null;
            reqObj = null;
            org.json.JSONObject data_obj = new org.json.JSONObject(response);
            org.json.JSONArray arr = data_obj.getJSONArray("data");
            if (data_obj.getInt("total") > 0) {
                for (int x = 0; x < arr.length(); x++) {
                    org.json.JSONObject key = arr.getJSONObject(x);
                    data.setStoCode((key.get("sto_code") == null ? "" : key.get("sto_code").toString()));
                    data.setStoName((key.get("sto_name") == null ? "" : key.get("sto_name").toString()));
                    data.setRegion((key.get("region_id") == null ? "" : key.get("region_id").toString()));
                    data.setWitel((key.get("witel_id") == null ? "" : key.get("witel_id").toString()));
                }
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        }
        return data;
    }

}
