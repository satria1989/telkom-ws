package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.ListService;
import id.co.itasoft.telkom.oss.plugin.model.ListServiceInformation;
import id.co.itasoft.telkom.oss.plugin.model.ListSymptom;
import id.co.itasoft.telkom.oss.plugin.model.ListTkMapping;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mtaup
 */
public class StartProcessTicketIncidentDao {

//    CallRestAPI callApi = new CallRestAPI();
//
////    private String host = getEnvVariableValue("host_asset_area_service");
////    private String apiId = getEnvVariableValue("api_id_asset_area_service");
////    private String apiKey = getEnvVariableValue("api_key_asset_area_service");
//    public ListServiceInformation getListServiceInformation(HashMap<String, String> params) {
//
//        ListServiceInformation data = new ListServiceInformation();
//
//        try {
//
//            ApiConfig apiConfig = new ApiConfig();
//            apiConfig.setUrl("http://dev-joget-asset-area-service-joget-dev.apps.mypaas.telkom.co.id/jw/api/list/list_serviceInformation");
//            apiConfig.setApiId("API-5cf99fc8-76f1-4634-911c-6ef795f6c437");
//            apiConfig.setApiKey("83524886cdec4cbfb70dfbfc14b1f9e7");
////            ApiConfig apiConfig = new ApiConfig();
////            apiConfig.setUrl(host + "/jw/api/list/list_serviceInformation");
////            apiConfig.setApiId(apiId);
////            apiConfig.setApiKey(apiKey);
//
//            String response = "";
//            response = callApi.sendGet(apiConfig, params);
//
//            JSONParser parse = new JSONParser();
//            JSONObject data_obj = (JSONObject) parse.parse(response);
//            JSONArray arrData = (JSONArray) data_obj.get("data");
//            for (Object object : arrData) {
//                JSONObject obj = (JSONObject) object;
//                data.setServiceId(obj.get("service_id").toString());
//                data.setServiceNumber(obj.get("service_number").toString());
//                data.setServiceCategory(obj.get("service_category").toString());
//                data.setWorkzone(obj.get("workzone").toString());
//                data.setWorkzoneName(obj.get("workzone_name").toString());
//                data.setServiceCategory(obj.get("service_category").toString());
//                data.setCustomerID(obj.get("customer_id").toString());
//                data.setCustomerCategory(obj.get("customer_category").toString());
//                data.setCustomerSegment(obj.get("customer_segment").toString());
//                data.setCustomerName(obj.get("customer_name").toString());
//                data.setCustomerType(obj.get("customer_type").toString());
//                data.setWitel(obj.get("witel").toString());
//                data.setReferenceNumber(obj.get("reference_number").toString());
//                data.setRegion(obj.get("region").toString());
//                data.setCustomerPriority(obj.get("customer_priority").toString());
//            }
//
//        } catch (Exception e) {
//            LogUtil.error(this.getClass().getName(), e, "Error Call API :" + e.getMessage());
//        }
//        return data;
//    }
//
//    public ListService getListService(HashMap<String, String> params) {
//
//        ListService data = new ListService();
//        try {
//
//            ApiConfig apiConfig = new ApiConfig();
//            apiConfig.setUrl("http://dev-joget-asset-area-service-joget-dev.apps.mypaas.telkom.co.id/jw/api/list/list_service");
//            apiConfig.setApiId("API-5cf99fc8-76f1-4634-911c-6ef795f6c437");
//            apiConfig.setApiKey("83524886cdec4cbfb70dfbfc14b1f9e7");
//
////            ApiConfig apiConfig = new ApiConfig();
////            apiConfig.setUrl(host + "/jw/api/list/list_service");
////            apiConfig.setApiId(apiId);
////            apiConfig.setApiKey(apiKey);
//            String response = "";
//            response = callApi.sendGet(apiConfig, params);
//
//            JSONParser parse = new JSONParser();
//            JSONObject data_obj = (JSONObject) parse.parse(response);
//            JSONArray arrData = (JSONArray) data_obj.get("data");
//
//            for (Object object : arrData) {
//                JSONObject obj = (JSONObject) object;
//                data.setServiceIdDescription((obj.get("service_id_decription") == null ? "" : obj.get("service_id_decription").toString()));
//                data.setServiceType((obj.get("service_type").toString() == null ? "" : obj.get("service_type").toString()));
//            }
//        } catch (Exception ex) {
//            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
//        }
//        return data;
//    }
//
//    public ListSymptom getListSymptom(HashMap<String, String> params) {
////        List<ListSymptom> datas = new ArrayList<>();
//        ListSymptom data = new ListSymptom();
//        try {
//
//            ApiConfig apiConfig = new ApiConfig();
//            apiConfig.setUrl("http://dev-joget-asset-area-service-joget-dev.apps.mypaas.telkom.co.id/jw/api/list/list_symptomClass");
//            apiConfig.setApiId("API-5af9602f-4237-4d50-b9a4-1076b866ffaa");
//            apiConfig.setApiKey("83524886cdec4cbfb70dfbfc14b1f9e7");
//
////            ApiConfig apiConfig = new ApiConfig();
////            apiConfig.setUrl(getEnvVariableValue("host_asset_area_service") + "/jw/api/list/list_service");
////            apiConfig.setApiId(getEnvVariableValue("api_id_asset_area_service"));
////            apiConfig.setApiKey(getEnvVariableValue("api_key_asset_area_service"));
//            String response = "";
//            response = callApi.sendGet(apiConfig, params);
//
//            JSONParser parse = new JSONParser();
//            JSONObject data_obj = (JSONObject) parse.parse(response);
//            JSONArray arrData = (JSONArray) data_obj.get("data");
//
////            ListSymptom data = new ListSymptom();
//            for (Object object : arrData) {
//                JSONObject obj = (JSONObject) object;
////                data = new ListSymptom();
//
//                data.setParent(obj.get("parent").toString());
//                data.setCreatedByName(obj.get("createdByName").toString());
//                data.setAutoBackend(obj.get("auto_backend").toString());
//                data.setClassificationFlag(obj.get("classification_flag").toString());
//                data.setFinalCheck(obj.get("final_check").toString());
//                data.setClassificationCode(obj.get("classification_code").toString());
//                data.setDescription(obj.get("description").toString());
//                data.setDateModified(obj.get("dateModified").toString());
//                data.setClassificationDescription(obj.get("classification_description").toString());
//                data.setDateCreated(obj.get("dateCreated").toString());
//                data.setModifiedByName(obj.get("modifiedByName").toString());
//                data.setSolution(obj.get("solution").toString());
//                data.setClassificationType(obj.get("classification_type").toString());
//                data.setDispatchClassification(obj.get("dispatch_classification").toString());
//                data.setHierarchyType(obj.get("hierarchy_type").toString());
//
//            }
//        } catch (Exception ex) {
//            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
//        }
//        return data;
//    }
//
//    public ListSymptom getSolution(HashMap<String, String> params) {
////        List<ListSymptom> datas = new ArrayList<>();
//        ListSymptom data = new ListSymptom();
//        try {
//
//            ApiConfig apiConfig = new ApiConfig();
//            apiConfig.setUrl("http://dev-joget-asset-area-service-joget-dev.apps.mypaas.telkom.co.id/jw/api/list/list_symptomClass");
//            apiConfig.setApiId("API-5af9602f-4237-4d50-b9a4-1076b866ffaa");
//            apiConfig.setApiKey("83524886cdec4cbfb70dfbfc14b1f9e7");
//
////            ApiConfig apiConfig = new ApiConfig();
////            apiConfig.setUrl(getEnvVariableValue("host_asset_area_service") + "/jw/api/list/list_service");
////            apiConfig.setApiId(getEnvVariableValue("api_id_asset_area_service"));
////            apiConfig.setApiKey(getEnvVariableValue("api_key_asset_area_service"));
//            String response = "";
//            response = callApi.sendGet(apiConfig, params);
//
//            JSONParser parse = new JSONParser();
//            JSONObject data_obj = (JSONObject) parse.parse(response);
//            JSONArray arrData = (JSONArray) data_obj.get("data");
//
////            ListSymptom data = new ListSymptom();
//            for (Object object : arrData) {
//                JSONObject obj = (JSONObject) object;
////                data = new ListSymptom();
//
////                data.setParent(obj.get("parent").toString());
////                data.setCreatedByName(obj.get("createdByName").toString());
////                data.setAutoBackend(obj.get("auto_backend").toString());
////                data.setClassificationFlag(obj.get("classification_flag").toString());
////                data.setFinalCheck(obj.get("final_check").toString());
//                data.setClassificationCode(obj.get("classification_code").toString());
//                data.setDescription(obj.get("description").toString());
////                data.setDateModified(obj.get("dateModified").toString());
////                data.setClassificationDescription(obj.get("classification_description").toString());
////                data.setDateCreated(obj.get("dateCreated").toString());
////                data.setModifiedByName(obj.get("modifiedByName").toString());
////                data.setSolution(obj.get("solution").toString());
////                data.setClassificationType(obj.get("classification_type").toString());
////                data.setDispatchClassification(obj.get("dispatch_classification").toString());
////                data.setHierarchyType(obj.get("hierarchy_type").toString());
//
//            }
//        } catch (Exception ex) {
//            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
//        }
//        return data;
//    }
//
//    public ListTkMapping getListTkMapping(HashMap<String, String> params) {
//
//        ListTkMapping data = new ListTkMapping();
//        try {
//
//            ApiConfig apiConfig = new ApiConfig();
//            apiConfig.setUrl("http://dev-joget-asset-area-service-joget-dev.apps.mypaas.telkom.co.id/jw/api/list/list_TKMappingSolution");
//            apiConfig.setApiId("API-5af9602f-4237-4d50-b9a4-1076b866ffaa");
//            apiConfig.setApiKey("83524886cdec4cbfb70dfbfc14b1f9e7");
//
////            ApiConfig apiConfig = new ApiConfig();
////            apiConfig.setUrl(host + "/jw/api/list/list_TKMappingSolution");
////            apiConfig.setApiId(apiId);
////            apiConfig.setApiKey(apiKey);
//            String response = "";
//            response = callApi.sendGet(apiConfig, params);
//
//            JSONParser parse = new JSONParser();
//            JSONObject data_obj = (JSONObject) parse.parse(response);
//            JSONArray arrData = (JSONArray) data_obj.get("data");
//
//            for (Object object : arrData) {
//                JSONObject obj = (JSONObject) object;
//                data.setCreatedByName(obj.get("createdByName").toString());
//                data.setDateCreated(obj.get("dateCreated").toString());
//                data.setModifiedByName(obj.get("modifiedByName").toString());
//                data.setFinalCheck(obj.get("final_check").toString());
//                data.setPersonOwnerGroup(obj.get("person_owner_group").toString());
//                data.setWorkzone(obj.get("workzone").toString());
//                data.setDescription(obj.get("description").toString());
//                data.setClassificationId(obj.get("classification_id").toString());
//                data.setDateModified(obj.get("dateModified").toString());
//                data.setCustomerSegment(obj.get("customer_segment").toString());
//                data.setProductName(obj.get("product_name").toString());
//            }
//        } catch (Exception ex) {
//            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
//        }
//        return data;
//    }
//
//    public String getOriginProcessId(String processId) {
//        String originProcessId = "";
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "select originProcessId from wf_process_link where processId = ?";
//        try (Connection con = ds.getConnection();
//                PreparedStatement ps = con.prepareStatement(query)) {
//            ps.setString(1, processId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    originProcessId = rs.getString("originProcessId");
//                }
//            }
//        } catch (SQLException ex) {
//            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
//        }
//        return originProcessId;
//    }
//
//    public void insertToTableParentTicket(String originProcessId, String childId) {
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "INSERT INTO app_fd_parent_ticket (id, dateCreated, c_child_id) VALUES (?,NOW(),?) ";
//
//        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
//            ps.setString(1, originProcessId);
//            ps.setString(2, childId);
//            ps.executeUpdate();
//            // LogUtil.info(this.getClass().getName(), "Insert To Table Parent Ticket");
//
//        } catch (SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
//        }
//    }
//
//    public void insertToTableTicket(ListServiceInformation lsi, ListService ls, ListTkMapping tkMapping, ListSymptom symptom, ListSymptom solution,
//            String OriginProcessId, String ticketNumber, String childId, String contactName, String contactPhone, String contactEmail,
//            String status, String ticketStatus, String reportedBy, String chanel, String externalSystem, String externalTicketId, String chanelDesc,
//            String summary, String details, String startProcessId) {
//        
//        String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
//
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "INSERT INTO  app_fd_ticket (id, dateCreated, c_service_no, c_slg, c_work_zone, " +
//                "c_customer_id, c_customer_category, c_customer_segment, c_customer_type, c_witel, " +
//                "c_reference_number, c_id_ticket, c_parent_id, c_service_id, c_description_customerid, " +
//                "c_description_work_zone, c_region, c_customer_priority, c_description_serviceid, c_service_type, " +
//                "c_status, c_ticket_status, c_contact_name, c_contact_phone, c_contact_email, " +
//                "c_class_description, c_solution_description, c_owner_group, c_classification_type, c_reported_date, " +
//                "c_classification_path, c_auto_backend, c_solution_code, c_reported_by, c_channel, " +
//                "c_source_ticket, c_external_ticketid, c_description_channel, c_summary, c_details, c_start_process_id) " +
//                "VALUES(?,NOW(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,?,?,?,?,?,?,?,?,?) ";
//        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
////            for (ListServiceInformation data : listServiceInformation) {
//            ps.setString(1, childId);
//            ps.setString(2, lsi.getServiceNumber());
//            ps.setString(3, lsi.getServiceCategory());
//            ps.setString(4, lsi.getWorkzone());
//            ps.setString(5, lsi.getCustomerID());
//            ps.setString(6, lsi.getCustomerCategory());
//            ps.setString(7, lsi.getCustomerSegment());
//            ps.setString(8, lsi.getCustomerType());
//            ps.setString(9, lsi.getWitel());
//            ps.setString(10, lsi.getReferenceNumber());
//            ps.setString(11, ticketNumber);
//            ps.setString(12, OriginProcessId);
//            ps.setString(13, lsi.getServiceId());
//            ps.setString(14, lsi.getCustomerName());
//            ps.setString(15, lsi.getWorkzoneName());
//            ps.setString(16, lsi.getRegion());
//            ps.setString(17, lsi.getCustomerPriority());
//            ps.setString(18, ls.getServiceIdDescription());
//            ps.setString(19, ls.getServiceType());
//            ps.setString(20, status);
//            ps.setString(21, ticketStatus);
//            ps.setString(22, contactName);
//            ps.setString(23, contactPhone);
//            ps.setString(24, contactEmail);
//            ps.setString(25, symptom.getDescription());
//            ps.setString(26, solution.getDescription());
//            ps.setString(27, tkMapping.getPersonOwnerGroup());
//            ps.setString(28, symptom.getClassificationType());
////            ps.setString(29, strDate);
//            ps.setString(29, symptom.getClassificationCode());
//            ps.setString(30, symptom.getAutoBackend());
//            ps.setString(31, solution.getClassificationCode());
//            ps.setString(32, reportedBy);
//            ps.setString(33, chanel);
//            ps.setString(34, externalSystem);
//            ps.setString(35, externalTicketId);
//            ps.setString(36, chanelDesc);
//            ps.setString(37, summary);
//            ps.setString(38, details);
//            ps.setString(39, startProcessId);
//
//            ps.executeUpdate();
//            // LogUtil.info(this.getClass().getName(), "Insert To Table Ticket");
////            }
//        } catch (SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
//        }
//    }
//
//    public int getCounterTicket(String appVersion) {
//        // LogUtil.info(this.getClass().getName(), "Get Counter Ticket Number");
//        int counter = 0;
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT value FROM app_env_variable \n" +
//                "WHERE id = 'counter_ticket' \n" +
//                "AND appVersion = ? ";
//        try (Connection con = ds.getConnection();
//                PreparedStatement ps = con.prepareStatement(query)) {
//            ps.setString(1, appVersion);
//            try {
//                ResultSet result = ps.executeQuery();
//                if (result.next()) {
//                    counter = result.getInt("value") + 1;
//                }
//
//            } catch (SQLException e) {
//                LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
//            }
//        } catch (SQLException e) {
//            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
//        }
//        return counter;
//
//    }
//
//    public String getChanelDescription(String chanel) {
//// //        LogUtil.info(this.getClass().getName(), "Get Counter Ticket Number");
//        String desc = "";
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT c_param_description FROM app_fd_param \n" +
//                "WHERE c_param_name = 'CHANNEL' \n" +
//                "AND c_param_code = ? ";
//        try (Connection con = ds.getConnection();
//                PreparedStatement ps = con.prepareStatement(query)) {
//            ps.setString(1, chanel);
//            try {
//                ResultSet result = ps.executeQuery();
//                if (result.next()) {
//                    desc = result.getString("c_param_description");
//                }
//
//            } catch (SQLException e) {
//                LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
//            }
//        } catch (SQLException e) {
//            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
//        }
//        return desc;
//
//    }
//
//    public void updateCounterTicket(String counter, String appVersion) {
//        Connection con = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "UPDATE app_env_variable SET VALUE = ? \n" +
//                "WHERE id = 'counter_ticket' \n" +
//                "AND appVersion = ? ";
//
//        try {
//            con = ds.getConnection();
//            if (!con.isClosed()) {
//                ps = con.prepareStatement(query);
//                ps.setString(1, counter);
//                ps.setString(2, appVersion);
//                ps.executeUpdate();
//                // LogUtil.info(this.getClass().getName(), "Update Counter Ticket Number");
//            }
//        } catch (SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
//        } catch (Exception ex) {
//            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
//        } finally {
//            try {
//                if (rs != null) {
//                    rs.close();
//                }
//            } catch (SQLException ex) {
//                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
//            }
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//            } catch (SQLException ex) {
//                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
//            }
//            try {
//                if (con != null) {
//                    con.close();
//                }
//            } catch (SQLException ex) {
//                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
//            }
//        }
//    }
//
////    public String getEnvVariableValue(String envVariableKey) {
////
////        AppDefinition appDef = (AppDefinition) AppUtil.getCurrentAppDefinition();
////        EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
////        EnvironmentVariable envVariable = environmentVariableDao.loadById(envVariableKey, appDef);
////
////        return envVariable.getValue();
////    }
}
