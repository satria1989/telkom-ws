/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.ListLocation;
import id.co.itasoft.telkom.oss.plugin.model.ListService;
import id.co.itasoft.telkom.oss.plugin.model.ListSymptom;
import id.co.itasoft.telkom.oss.plugin.model.ListWorkzone;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.sql.DataSource;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mtaupiq
 */
public class UpdateAttributeTicketDao {
    LogInfo logInfo = new LogInfo();
  
    CallRestAPI callApi;
    ListLocation listLocation;
    ListService listService;
    ListWorkzone listWorkzone;
    ListSymptom listSymptom;

    public ListLocation getLocation(HashMap<String, String> params) {
        MasterParamDao paramDao = new MasterParamDao();
        ApiConfig apiConfig = new ApiConfig();
        listLocation = new ListLocation();
        callApi = new CallRestAPI();
        try {
            apiConfig = paramDao.getUrlapi("list_location");
            JSONObject response = new JSONObject();
            response = callApi.sendGetWithoutToken(apiConfig, params);
            boolean statusAPI = response.getBoolean("status");
            String msg = response.getString("msg");

            if (statusAPI) {
                Object obj = new JSONTokener(msg).nextValue();
                JSONObject data = (JSONObject) obj;
                JSONArray arr = data.getJSONArray("data");

                if (data.getInt("total") > 0) {
                    for (int x = 0; x < arr.length(); x++) {
                        JSONObject key = arr.getJSONObject(x);
                        if (key.get("address_code").equals(params.get("address_code"))) {
                            listLocation.setLatitude((key.get("latitude") == null ? "" : key.get("latitude").toString()));
                            listLocation.setLongitude((key.get("longitude") == null ? "" : key.get("longitude").toString()));
                            listLocation.setPostalCode((key.get("postal_code") == null ? "" : key.get("postal_code").toString()));
                            listLocation.setCountry((key.get("country") == null ? "" : key.get("country").toString()));
                        }
                    }
                }

                obj = null;
                data = null;
                arr = null;

            }
            response = null;
            msg = null;
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            params = null;
            paramDao = null;
            callApi = null;
            apiConfig = null;
        }
        return listLocation;
    }

    public ListService getListService(HashMap<String, String> params) {
        listService = new ListService();
        MasterParamDao paramDao = new MasterParamDao();
        ApiConfig apiConfig = new ApiConfig();
        callApi = new CallRestAPI();
        try {

            apiConfig = paramDao.getUrlapi("list_service");
            JSONObject response = new JSONObject();
            response = callApi.sendGetWithoutToken(apiConfig, params);
            boolean statusAPI = response.getBoolean("status");
            String msg = response.getString("msg");

            if (statusAPI) {
                Object obj = new JSONTokener(msg).nextValue();
                JSONObject data = (JSONObject) obj;
                JSONArray arr = data.getJSONArray("data");

                if (data.getInt("total") > 0) {
                    for (int x = 0; x < arr.length(); x++) {
                        JSONObject key = arr.getJSONObject(x);
                        listService.setServiceIdDescription((key.get("service_id_decription") == null ? "" : key.get("service_id_decription").toString()));
                        listService.setServiceType((key.get("service_type").toString() == null ? "" : key.get("service_type").toString()));
                    }
                }
                obj = null;
                data = null;
                arr = null;
            }
            response = null;
            msg = null;

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            paramDao = null;
            callApi = null;
            apiConfig = null;

        }
        return listService;
    }

    public ListWorkzone getListWorkzone(HashMap<String, String> params) {
        MasterParamDao paramDao = new MasterParamDao();
        ApiConfig apiConfig = new ApiConfig();
        listWorkzone = new ListWorkzone();
        callApi = new CallRestAPI();
        try {

            apiConfig = paramDao.getUrlapi("list_workzone");
            JSONObject response = new JSONObject();
            response = callApi.sendGetWithoutToken(apiConfig, params);
            boolean statusAPI = response.getBoolean("status");
            String msg = response.getString("msg");

            if (statusAPI) {
                Object obj = new JSONTokener(msg).nextValue();
                JSONObject data = (JSONObject) obj;
                JSONArray arr = data.getJSONArray("data");

                if (data.getInt("total") > 0) {
                    for (int x = 0; x < arr.length(); x++) {
                        JSONObject key = arr.getJSONObject(x);
                        listWorkzone.setStoCode((key.get("sto_code") == null ? "" : key.get("sto_code").toString()));
                        listWorkzone.setStoName((key.get("sto_name") == null ? "" : key.get("sto_name").toString()));
                        listWorkzone.setRegion((key.get("region_id") == null ? "" : key.get("region_id").toString()));
                        listWorkzone.setWitel((key.get("witel_id") == null ? "" : key.get("witel_id").toString()));
                    }
                }
                obj = null;
                data = null;
                arr = null;
            }
            response = null;
            msg = null;

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            paramDao = null;
            callApi = null;
            apiConfig = null;
        }
        return listWorkzone;
    }

    public ListSymptom getSolution(HashMap<String, String> params) {
//        List<ListSymptom> datas = new ArrayList<>();
        MasterParamDao paramDao = new MasterParamDao();
        listSymptom = new ListSymptom();
        callApi = new CallRestAPI();
        ApiConfig apiConfig = new ApiConfig();
        try {

            apiConfig = paramDao.getUrlapi("list_symptomp");
            JSONObject response = new JSONObject();
            response = callApi.sendGetWithoutToken(apiConfig, params);
            boolean statusAPI = response.getBoolean("status");
            String msg = response.getString("msg");

            if (statusAPI) {
                Object obj = new JSONTokener(msg).nextValue();
                JSONObject data = (JSONObject) obj;
                JSONArray arr = data.getJSONArray("data");

                if (data.getInt("total") > 0) {
                    for (int x = 0; x < arr.length(); x++) {
                        JSONObject key = arr.getJSONObject(x);
                        listSymptom.setClassificationCode(key.get("classification_code").toString());
                        listSymptom.setDescription(key.get("description").toString());
                    }

                }

                obj = null;
                data = null;
                arr = null;
            }
            response = null;
            msg = null;

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            paramDao = null;
            callApi = null;
            apiConfig = null;
        }
        return listSymptom;
    }

    public String getDescActualSolution(HashMap<String, String> params) {
        String result = "";
        MasterParamDao paramDao = new MasterParamDao();
        ApiConfig apiConfig = new ApiConfig();
        callApi = new CallRestAPI();
        try {

            apiConfig = paramDao.getUrlapi("list_symptomp");
            JSONObject response = new JSONObject();
            response = callApi.sendGetWithoutToken(apiConfig, params);
            boolean statusAPI = response.getBoolean("status");
            String msg = response.getString("msg");
            if (statusAPI) {
                Object obj = new JSONTokener(msg).nextValue();
                JSONObject data = (JSONObject) obj;
                JSONArray arr = data.getJSONArray("data");

                for (int x = 0; x < arr.length(); x++) {
                    JSONObject key = arr.getJSONObject(x);
                    result = key.get("description").toString();
                }
                obj = null;
                data = null;
                arr = null;
            }

            response = null;
            msg = null;

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            paramDao = null;
            callApi = null;
            apiConfig = null;
            params = null;
        }
        return result;
    }

    public String getParamDescription(String paramName, String paramCode) throws SQLException, SQLException {
        String desc = "";
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
                desc = result.getString("c_param_description");
            }

        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            try {
                result.close();
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                ps.close();
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                con.close();
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }

        }
        return desc;

    }

    public String getCustomerType(String serviceNumber, String ticketId) {
        LogUtil.info(this.getClass().getName(), "serviceNumber : "+serviceNumber);
        String stringResponse = "";
        String HVC = "";
        MasterParamDao paramDao = new MasterParamDao();
        ApiConfig apiConfig = new ApiConfig();
        callApi = new CallRestAPI();
        LogHistoryDao logHistoryDao = new LogHistoryDao();
        LogHistory logHistory = new LogHistory();
        RESTAPI _RESTAPI = new RESTAPI();
        String token = _RESTAPI.getToken();
        try {
            LogUtil.info(this.getClass().getName(), "Get Customer Type");
            apiConfig = paramDao.getUrlapi("get_customer_type");
            org.json.simple.JSONObject paramOobj = new org.json.simple.JSONObject();
            paramOobj.put("ND", serviceNumber);
            RequestBody jsonRequestBody = RequestBody.create(_RESTAPI.JSON, paramOobj.toJSONString());
            Request request = new Request.Builder()
                    .url(apiConfig.getUrl())
                    .addHeader("Authorization", "Bearer " + token)
                    .post(jsonRequestBody)
                    .build();

            stringResponse = _RESTAPI.CALLAPI(request);

            logHistory.setAction("GET_CUSTOMER_TYPE(" + ticketId + ")");
            logHistory.setCreatedBy("SYSTEM");
            logHistory.setMethod("POST");
            logHistory.setRequest(paramOobj.toString());
            logHistory.setTicketId(ticketId);
            logHistory.setUrl(apiConfig.getUrl());
            logHistory.setResponse(stringResponse);
            logHistoryDao.insertToLogHistory(logHistory);

            JSONParser parse = new JSONParser();
            org.json.simple.JSONObject data_obj = (org.json.simple.JSONObject) parse.parse(stringResponse);
            LogUtil.info(this.getClass().getName(), "response service_type : "+stringResponse);
            if (data_obj.get("data") != null) {
                org.json.simple.JSONArray arrData = (org.json.simple.JSONArray) data_obj.get("data");
                for (Object object : arrData) {
                    org.json.simple.JSONObject obj = (org.json.simple.JSONObject) object;
                    HVC = obj.get("KAT_HVC") == null ? "" : obj.get("KAT_HVC").toString();
                }
            }
            LogUtil.info(this.getClass().getName(), "HVC : "+HVC);

        } catch (Exception ex) {
//          logInfo.Log(getClass().getName(), ex.getMessage());
          LogUtil.error(this.getClass().getName(), ex, "error get cust type : "+ex.getMessage());
        } finally {
            paramDao = null;
            callApi = null;
            apiConfig = null;
        }
        return HVC;
    }

    public void updateTicketParam(ListLocation ll, ListService ls, ListWorkzone lw, String descChannel,
        String descHardComplaint, String descUrgency, String descClosedBy, ListSymptom solution, String descActualSolution,
        String sourceDesc, String custSegmentDesc, String subsidiaryDesc, String realmDesc, String impactDesc,
        String serviceCategoryDesc, String processId, String sourceTicket, String customerType) throws ParseException, SQLException {

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateNow = simpleDateFormat.format(new Date());
        Timestamp tsDateNow = Timestamp.valueOf(dateNow);

        Connection con = null;
        PreparedStatement ps = null;
        StringBuilder sbQuery = new StringBuilder();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        sbQuery.append(" update app_fd_ticket set ");
        sbQuery.append("c_solution_description = ? "); //1
        sbQuery.append(",c_solution_code = ? "); //2
        sbQuery.append(",c_description_hard_complaint = ? "); //3
        sbQuery.append(",c_description_urgensi = ? "); //4
        sbQuery.append(",c_description_channel = ? "); //5
        sbQuery.append(",c_description_closed_by = ? "); //6
        sbQuery.append(",c_description_actualsolution = ? "); //7
        sbQuery.append(",c_source_ticket_desc = ? "); //8
        sbQuery.append(",c_customer_segment_desc = ? "); //9
        sbQuery.append(",c_description_realm = ? "); //10
        sbQuery.append(",c_description_impact = ? "); //11
        sbQuery.append(",c_description_subsidiary = ? "); //12
        sbQuery.append(",c_description_ctgrysrvc = ? "); //13
        sbQuery.append(",c_description_work_zone = ? ");
        sbQuery.append(",c_witel = ? ");
        sbQuery.append(",c_region = ? ");
        sbQuery.append(",c_customer_type = ? ");
        sbQuery.append("where c_parent_id = ? "); //14

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(sbQuery.toString());
                ps.setString(1, solution.getDescription());
                ps.setString(2, solution.getClassificationCode());
                ps.setString(3, descHardComplaint);
                ps.setString(4, descUrgency);
                ps.setString(5, descChannel);
                ps.setString(6, descClosedBy);
                ps.setString(7, descActualSolution);
                ps.setString(8, sourceDesc);
                ps.setString(9, custSegmentDesc);
                ps.setString(10, realmDesc);
                ps.setString(11, impactDesc);
                ps.setString(12, subsidiaryDesc);
                ps.setString(13, serviceCategoryDesc);
                ps.setString(14, lw.getStoName());
                ps.setString(15, lw.getWitel());
                ps.setString(16, lw.getRegion());
                ps.setString(17, customerType);
                ps.setString(18, processId);

                ps.executeUpdate();
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            sbQuery = null;
            ll = null;
            lw = null;
            ls = null;
            try {
                ps.close();
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                con.close();
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }

        }
    }
    
    public void updateCustomerType(String processId, String customerType) throws ParseException, SQLException {

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateNow = simpleDateFormat.format(new Date());
        Timestamp tsDateNow = Timestamp.valueOf(dateNow);

        Connection con = null;
        PreparedStatement ps = null;
        StringBuilder sbQuery = new StringBuilder();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        sbQuery.append(" update app_fd_ticket set ");
        sbQuery.append("c_customer_type = ? ");
        sbQuery.append("where c_parent_id = ? "); //14

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(sbQuery.toString());
                ps.setString(1, customerType);
                ps.setString(2, processId);

                ps.executeUpdate();
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            sbQuery = null;
            try {
                ps.close();
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                con.close();
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }

        }
    }

}
