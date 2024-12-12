/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.CallImpactService;
import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.ListServiceInformation;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import javax.sql.DataSource;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author asani
 */
public class InsertImpactServiceDao {

    CallImpactService cis = new CallImpactService();
    CallRestAPI callApi = new CallRestAPI();
    MeasureIboosterGamasDao migDao = new MeasureIboosterGamasDao();
    LogInfo logInfo = new LogInfo();

    public ListServiceInformation getListServiceInformation(String ticket) {
        ListServiceInformation data = new ListServiceInformation();
        TicketStatus ts = new TicketStatus();
        try {

            String response = "";
            response = cis.callImpact(ts);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("OUT_IMPACT_RESULT");
            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;
                data.setServiceId(obj.get("assetnum").toString());
            }

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            ts = null;
        }
        return data;
    }

    public void insertToTableService(String serviceId, String operStatus, String processId, String idTicket, String serviceNumber, String estimation, String symptom, String symptomDesc, String region) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO app_fd_ticket_imp_service ")
                .append("(id, dateCreated, dateModified, c_service_id, c_ibooster_oper_status, c_parent_id, c_ticket_id, c_service_number, c_estimation, c_symptom, c_symptom_desc, c_region) ")
                .append("VALUES (?,sysdate,sysdate,?,?,?,?,?,?,?,?,?) ");
        Connection con = ds.getConnection();
//        con.setAutoCommit(false);
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setString(2, serviceId);
            ps.setString(3, operStatus);
            ps.setString(4, processId);
            ps.setString(5, idTicket);
            ps.setString(6, serviceNumber);
            ps.setString(7, estimation);
            ps.setString(8, symptom);
            ps.setString(9, symptomDesc);
            ps.setString(10, region);
            ps.executeUpdate();
//            con.commit();
//            LogUtil.info(getClass().getName(), "COMMIT TRUE");
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
        }
    }

    public String getOperStatus(String nd, String realm) throws Exception {
        String tokenIbooster = migDao.getToken();
        String operStatus = "";
        ApiConfig apiConfig = new ApiConfig();
        MasterParam param = new MasterParam();
        MasterParamDao paramDaoo = new MasterParamDao();
        param = paramDaoo.getUrl("get_ibooster");
        try {

            apiConfig.setUrl(param.getUrl());

            RequestBody formBody = new FormBody.Builder()
                    .add("nd", (nd == null ? "" : nd))
                    .add("realm", realm)
                    .build();

            String response = "";
            response = callApi.sendPostIbooster(apiConfig, formBody, tokenIbooster);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);

            String message = (data_obj.get("MESSAGE") == null ? "" : data_obj.get("MESSAGE").toString());
            if ("".equals(message)) {
                operStatus = (data_obj.get("oper_status") == null ? "" : data_obj.get("oper_status").toString());
            }
            parse = null;
            data_obj = null;
            formBody = null;

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            apiConfig = null;
            tokenIbooster = null;
        }
        return operStatus;
    }

    public ListServiceInformation getServiceNumber(HashMap<String, String> params) {

        ListServiceInformation lsi = new ListServiceInformation();
        String serviceNumber = "";
        MasterParam param = new MasterParam();
        ApiConfig apiConfig = new ApiConfig();
        MasterParamDao paramDao = new MasterParamDao();

        try {

            param = paramDao.getUrl("list_service_information_custom");

            apiConfig.setUrl(param.getUrl());
            apiConfig.setApiId(param.getApi_id());
            apiConfig.setApiKey(param.getApi_key());

            String response = "";
            response = callApi.sendGet(apiConfig, params);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");
            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;
                lsi.setServiceNumber(obj.get("service_number") == null ? "" : obj.get("service_number").toString());
                lsi.setRegion(obj.get("region") == null ? "" : obj.get("region").toString());
                lsi.setCustomerSegment(obj.get("customer_segment") == null ? "" : obj.get("customer_segment").toString());
            }
            parse = null;
            data_obj = null;
            arrData = null;
            apiConfig = null;
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            param = null;
            paramDao = null;
            apiConfig = null;
        }
        return lsi;
    }

}
