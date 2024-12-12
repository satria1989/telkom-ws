
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
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mtaup
 */
public class MeasureIboosterGamasDao {
    
    LogInfo logInfo = new LogInfo();
  
    public TicketStatus getDataTicket(String processId) throws SQLException {
        CallRestAPI callApi = new CallRestAPI();
        MasterParamDao paramDao = new MasterParamDao();
        PreparedStatement ps = null;
        ResultSet rs = null;

        TicketStatus r = new TicketStatus();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append("  id, ")
                .append("  c_id_ticket, ")
                .append("  c_status, ")
                .append("  c_source_ticket, ")
                .append("  c_customer_segment, ")
                .append("  c_classification_type, ")
                .append("  c_service_type, ")
                .append("  c_service_no, ")
                .append("  c_realm, ")
                .append("  c_external_ticketid, ")
                .append("  c_last_state, ")
                .append("  c_classification_flag, ")
                .append("  c_child_gamas, ")
                .append("  c_finalcheck ")
                .append(" FROM app_fd_ticket  ")
                .append(" WHERE  c_parent_id = ? ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);

            rs = ps.executeQuery();
            while (rs.next()) {

                r.setId(rs.getString("id"));
                r.setTicketId(rs.getString("c_id_ticket"));
                r.setStatus(rs.getString("c_status"));
                r.setSourceTicket(rs.getString("c_source_ticket"));
                r.setCustomerSegment(rs.getString("c_customer_segment"));
                r.setClassification_type(rs.getString("c_classification_type"));
                r.setServiceType(rs.getString("c_service_type"));
                r.setServiceNo(rs.getString("c_service_no") == null ? "" : rs.getString("c_service_no"));
                r.setRealm(rs.getString("c_realm"));
                r.setExternalTicketid(rs.getString("c_external_ticketid"));
                r.setLast_state(rs.getString("c_last_state"));
                r.setClassificationFlag(rs.getString("c_classification_flag"));
                r.setChild_gamas(rs.getString("c_child_gamas"));
                r.setFinalCheck(rs.getString("c_finalcheck"));

            }

        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            callApi = null;
            paramDao = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
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

        return r;
    }

    public List<Ticket> getProcessIdTicket(String idParent) throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;

        List<Ticket> r = new ArrayList<>();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append("SELECT d.processId, c.id AS id_ticket_inc, c.c_parent_id AS parent_id_ticket, a.c_ticket_id, c.c_customer_segment, ")
                .append("c.c_service_no, c.c_realm, c.c_owner_group, c.c_finalcheck, c.c_service_type, c.c_classification_flag, c.c_child_gamas, ")
                .append("c.c_classification_type, c.c_last_state ")
                .append("FROM app_fd_related_record_id a ")
                .append("JOIN app_fd_parent_ticket b ON a.c_parent_id = b.id ")
                .append("JOIN app_fd_ticket c ON c.c_id_ticket = a.c_ticket_id ")
                .append("JOIN wf_process_link d ON c.c_parent_id = d.originProcessId  ")
                .append("WHERE a.c_parent_id = ? ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, idParent);

            result = ps.executeQuery();
            Ticket tc = null;
            while (result.next()) {
                tc = new Ticket();
                tc.setProcessId(result.getString("processId"));
                tc.setIdTicketInc(result.getString("id_ticket_inc"));
                tc.setParentTicketInc(result.getString("parent_id_ticket"));
                tc.setTicketId(result.getString("c_ticket_id"));
                tc.setCust_segment(result.getString("c_customer_segment"));
                tc.setService_no(result.getString("c_service_no"));
                tc.setRealm(result.getString("c_realm"));
                tc.setOwnerGroup(result.getString("c_owner_group"));
                tc.setFinalcheck(result.getString("c_finalcheck"));
                tc.setServiceType(result.getString("c_service_type"));
                tc.setClassificationFlag(result.getString("c_classification_flag"));
                tc.setChildGamas(result.getString("c_child_gamas"));
                tc.setClassificationType(result.getString("c_classification_type"));
                tc.setLast_state(result.getString("c_last_state"));

                r.add(tc);
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            try {
                if (result != null) {
                    result.close();
                }
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
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
        return r;
    }

    public List<Ticket> getProcessIdTicketWithShk(String idParent) throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;

        List<Ticket> r = new ArrayList<>();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append(" SELECT d.processId, c.id AS id_ticket_inc, c.c_parent_id AS parent_id_ticket, a.c_ticket_id, c.c_customer_segment,  ")
                .append(" c.c_service_no, c.c_realm, c.c_owner_group, c.c_finalcheck, c.c_service_type, c.c_classification_flag, c.c_child_gamas,  ")
                .append(" c.c_classification_type, c.c_last_state, ")
                .append(" e.id as activity_id, e.ACTIVITYDEFINITIONID as activity_name, e.PDEFNAME as process_def_id, e.STATE  ")
                .append(" FROM app_fd_related_record_id a  ")
                .append(" JOIN app_fd_ticket c ON c.c_id_ticket = a.c_ticket_id  ")
                .append(" JOIN wf_process_link d ON c.c_parent_id = d.originProcessId  ")
                .append(" join SHKACTIVITIES e on e.PROCESSID = d.PROCESSID  ")
                .append(" WHERE a.c_parent_id = ? ")
                .append(" and e.state = '1000003' ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, idParent);

            result = ps.executeQuery();
            Ticket tc = null;
            while (result.next()) {
                tc = new Ticket();
                tc.setProcessId(result.getString("processId"));
                tc.setIdTicketInc(result.getString("id_ticket_inc"));
                tc.setParentTicketInc(result.getString("parent_id_ticket"));
                tc.setTicketId(result.getString("c_ticket_id"));
                tc.setCust_segment(result.getString("c_customer_segment"));
                tc.setService_no(result.getString("c_service_no"));
                tc.setRealm(result.getString("c_realm"));
                tc.setOwnerGroup(result.getString("c_owner_group"));
                tc.setFinalcheck(result.getString("c_finalcheck"));
                tc.setServiceType(result.getString("c_service_type"));
                tc.setClassificationFlag(result.getString("c_classification_flag"));
                tc.setChildGamas(result.getString("c_child_gamas"));
                tc.setClassificationType(result.getString("c_classification_type"));
                tc.setActivityId(result.getString("activity_id"));
                tc.setActivityName(result.getString("activity_name"));
                tc.setProcessDefId(result.getString("process_def_id"));
                tc.setState(result.getString("STATE"));

                r.add(tc);
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            try {
                if (result != null) {
                    result.close();
                }
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
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
        return r;
    }

    ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.SECONDS);
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .connectionPool(connectionPool)
            .build();

    public String assigmentCompleteProcess(ApiConfig apiConfig, RequestBody formBody) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String stringResponse = "";

        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                // LogUtil.info(this.getClass().getName(), "Unexpected code " + response);
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            stringResponse = response.body().string();
            response.body().close();
            response.close();
        }
        apiConfig = null;
        formBody = null;
        return stringResponse;
    }

    public String getToken() {
        CallRestAPI callApi = new CallRestAPI();
        MasterParamDao paramDao = new MasterParamDao();
        ApiConfig apiConfig = new ApiConfig();

        PreparedStatement ps = null;
        ResultSet rs = null;
        String token = "";
        try {

            apiConfig = paramDao.getUrlToken("get_access_token");
//            apiConfig.setUrl("https://apigwsit.telkom.co.id:7777/invoke/pub.apigateway.oauth2/getAccessToken");

            RequestBody formBody = new FormBody.Builder()
                    .add("grant_type", "client_credentials")
                    .add("client_id", apiConfig.getClientId())
                    .add("client_secret", apiConfig.getClientSecret())
                    .build();
            String response = "";
            response = callApi.sendPostTokenIbooster(apiConfig, formBody);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
//            token = (String) data_obj.get("access_token");
            token = data_obj.get("access_token").toString();

            parse = null;
            data_obj = null;
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            callApi = null;
            paramDao = null;
            apiConfig = null;
        }
        return token;
    }

    public ListIbooster getIbooster(String nd, String realm, String ticketId) throws SQLException, Exception {
        CallRestAPI callApi = new CallRestAPI();
        MasterParamDao paramDao = new MasterParamDao();
        ListIbooster data = new ListIbooster();
        LogHistoryDao lh = new LogHistoryDao();
        ApiConfig apiConfig = new ApiConfig();
        RESTAPI _RESTAPI = new RESTAPI();

        String tokenIbooster = _RESTAPI.getToken();
        String response = "";
        MasterParam param = new MasterParam();
        try {
            param = paramDao.getUrl("get_ibooster");
            apiConfig.setUrl(param.getUrl());

            RequestBody formBody = new FormBody.Builder()
                    .add("nd", nd)
                    .add("realm", realm)
                    .build();

            response = "";
//            response = callApi.sendGet(apiConfig, params);
            response = callApi.sendPostIbooster(apiConfig, formBody, tokenIbooster);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);

            String message = (data_obj.get("MESSAGE") == null ? "" : data_obj.get("MESSAGE").toString());

//            data.setKategoriUkur(data_obj.get("").toString());
            if ("".equals(message)) {
                // LogUtil.info(this.getClass().getName(), "Masuk Message == null");
                data.setOperStatus(data_obj.get("oper_status") == null ? "" : data_obj.get("oper_status").toString());
                data.setOnuRxPwr(data_obj.get("onu_rx_pwr") == null ? "null" : data_obj.get("onu_rx_pwr").toString());
                data.setOnuTxPwr(data_obj.get("onu_tx_pwr") == null ? "null" : data_obj.get("onu_tx_pwr").toString());
                data.setOltRxPwr(data_obj.get("olt_rx_pwr") == null ? "null" : data_obj.get("olt_rx_pwr").toString());
                data.setOltTxPwr(data_obj.get("olt_tx_pwr") == null ? "null" : data_obj.get("olt_tx_pwr").toString());
                data.setFiberLength(data_obj.get("fiber_length") == null ? "" : data_obj.get("fiber_length").toString());
                data.setStatusJaringan(data_obj.get("status_jaringan") == null ? "" : data_obj.get("status_jaringan").toString());
                data.setIdentifier(data_obj.get("identifier") == null ? "" : data_obj.get("identifier").toString());
                data.setIdUkur(data_obj.get("id_ukur") == null ? "" : data_obj.get("id_ukur").toString());
                data.setNasIp(data_obj.get("nas_ip") == null ? "" : data_obj.get("nas_ip").toString());
                data.setHostname(data_obj.get("hostname") == null ? "" : data_obj.get("hostname").toString());
                data.setClid(data_obj.get("clid") == null ? "" : data_obj.get("clid").toString());
                data.setStatusCpe(data_obj.get("status_cpe") == null ? "" : data_obj.get("status_cpe").toString());
                data.setMeasurementTime(data_obj.get("session_start") == null ? "" : data_obj.get("session_start").toString());
                data.setMessage(data_obj.get("MESSAGE") == null ? "" : data_obj.get("MESSAGE").toString());
            } else {
                // LogUtil.info(this.getClass().getName(), "Masuk Message != null");
                data.setMessage(data_obj.get("MESSAGE") == null ? "" : data_obj.get("MESSAGE").toString());
                data.setOnuRxPwr("null");
                data.setOnuTxPwr("null");
                data.setOltRxPwr("null");
                data.setOltTxPwr("null");
            }

            Float onuRxPwr = Float.valueOf((data_obj.get("onu_rx_pwr") == null ? "0" : data_obj.get("onu_rx_pwr").toString()));
            String measurementCategory = "";

            if (onuRxPwr <= Float.valueOf(-13) && onuRxPwr >= Float.valueOf(-24)) {
                measurementCategory = "SPEC";
            } else {
                measurementCategory = "UNSPEC";
            }

//            measurementCategory = "SPEC"; // untuk sementara
            data.setMeasurementCategory(measurementCategory);

            formBody = null;
            parse = null;
            data_obj = null;
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            LogHistory dataLh = new LogHistory();
            dataLh.setRequest("{\"nd\": \"" + nd + "\",\"realm\": \"" + realm + "\"}");
            dataLh.setMethod("POST");
            dataLh.setResponse(response);
            dataLh.setAction("Measure ibooster - GAMAS");
            dataLh.setUrl(param.getUrl());
            dataLh.setTicketId(ticketId);
            lh.insertToLogHistory(dataLh);
            callApi = null;
            paramDao = null;
            lh = null;
            param = null;
            apiConfig = null;
        }

        return data;

    }

    public void updateIbooster(ListIbooster ib, String id) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        
        StringBuilder iboosterResult = new StringBuilder();
        if ("".equals(ib.getMessage())) {
            iboosterResult.append("Kategori Ukur : " + ib.getMeasurementCategory() + "\r");
            iboosterResult.append("oper_status : " + ib.getOperStatus() + "\r");
            iboosterResult.append("onu_rx_pwr : " + ib.getOnuRxPwr() + "\r");
            iboosterResult.append("onu_tx_pwr : " + ib.getOnuTxPwr() + "\r");
            iboosterResult.append("olt_rx_pwr : " + ib.getOltRxPwr() + "\r");
            iboosterResult.append("olt_tx_pwr : " + ib.getOltTxPwr() + "\r");
            iboosterResult.append("fiber_length : " + ib.getFiberLength() + "\r");
            iboosterResult.append("status_jaringan : " + ib.getStatusJaringan() + "\r");
            iboosterResult.append("identifier : " + ib.getIdentifier() + "\r");
        } else {
            iboosterResult.append(ib.getMessage());
        }
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String waktuUkur = (dateFormat2.format(new Date()).toString()).toLowerCase();
        StringBuilder query;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        if ("".equals(ib.getMessage())) {
            // LogUtil.info(this.getClass().getName(), "Masuk query normal");
            query = new StringBuilder();
            query.append("UPDATE app_fd_ticket SET  ")
                    .append("c_measurement_time = ?,  ")
                    .append("c_ibooster_result = ?,   ")
                    .append("c_mycx_result = ?,   ")
                    .append("c_mycx_category_result = ?,   ")
                    .append("c_measurement_category = ?, ")
                    .append("c_id_pengukuran = ?,   ")
                    .append("c_hostname_olt = ?,  ")
                    .append("c_ip_olt = ?,   ")
                    .append("c_frame = ?, ")
                    .append("c_olt_tx = CONCAT(CONCAT(?, ' | '),NVL(c_olt_tx, 'null')),   ")
                    .append("c_olt_rx = CONCAT(CONCAT(?, ' | '),NVL(c_olt_rx, 'null')),   ")
                    .append("c_onu_tx = CONCAT(CONCAT(?, ' | '),NVL(c_onu_tx, 'null')),   ")
                    .append("c_onu_rx = CONCAT(CONCAT(?, ' | '),NVL(c_onu_rx, 'null')),   ")
                    .append("c_status_ont = ? ")
                    .append("WHERE c_parent_id = ? ");
        } else {
            query = new StringBuilder();
            query.append("UPDATE app_fd_ticket SET ")
                    .append("c_measurement_time = ?, ")
                    .append("c_ibooster_result = ?,  ")
                    .append("c_measurement_category = ?,  ")
                    .append("c_olt_tx = CONCAT(CONCAT(?, ' | '),NVL(c_olt_tx, 'null')),   ")
                    .append("c_olt_rx = CONCAT(CONCAT(?, ' | '),NVL(c_olt_rx, 'null')),   ")
                    .append("c_onu_tx = CONCAT(CONCAT(?, ' | '),NVL(c_onu_tx, 'null')),   ")
                    .append("c_onu_rx = CONCAT(CONCAT(?, ' | '),NVL(c_onu_rx, 'null'))   ")
                    .append("WHERE c_parent_id = ?");
        }

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());
                if ("".equals(ib.getMessage())) {
                    // LogUtil.info(this.getClass().getName(), "Masuk query parameter normal");
                    ps.setTimestamp(1, Timestamp.valueOf(waktuUkur));
                    ps.setString(2, iboosterResult.toString());
                    ps.setString(3, "-");
                    ps.setString(4, "-");
                    ps.setString(5, ib.getMeasurementCategory());
                    ps.setString(6, ib.getIdUkur());
                    ps.setString(7, ib.getIdentifier());
                    ps.setString(8, ib.getNasIp() + " | " + ib.getHostname());
                    ps.setString(9, ib.getClid());
                    ps.setString(10, ib.getOltTxPwr());
                    ps.setString(11, ib.getOltRxPwr());
                    ps.setString(12, ib.getOnuTxPwr());
                    ps.setString(13, ib.getOnuRxPwr());
                    ps.setString(14, ib.getStatusCpe() + " | " + ib.getOperStatus());
                    ps.setString(15, id);
                } else {
                    // LogUtil.info(this.getClass().getName(), "Masuk query parameter upnormal");
                    ps.setTimestamp(1, Timestamp.valueOf(waktuUkur));
                    ps.setString(2, iboosterResult.toString());
                    ps.setString(3, ib.getMeasurementCategory());
                    ps.setString(4, ib.getOltTxPwr());
                    ps.setString(5, ib.getOltRxPwr());
                    ps.setString(6, ib.getOnuTxPwr());
                    ps.setString(7, ib.getOnuRxPwr());
                    ps.setString(8, id);
                }
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            ib = null;
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

    public void insertWorkLogs(String parentId, String idTicket, String ownerGroup, String kategoriUkur) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNow = (dateFormat.format(new Date()).toString());
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO app_fd_ticket_work_logs ")
                .append("(id, c_parentid, dateCreated, c_recordkey, c_class, createdBy, c_ownergroup, c_created_date, c_createdby, c_log_type, c_summary, c_detail) ")
                .append("VALUES (?, ?, sysdate, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());

            String uuid = UuidGenerator.getInstance().getUuid();
            // LogUtil.info(this.getClass().getName(), "UUID Insert Work logs : " + uuid);
            ps.setString(1, uuid);
            ps.setString(2, parentId);
            ps.setString(3, idTicket);
            ps.setString(4, "INCIDENT");
            ps.setString(5, "BY_SYSTEM");
            ps.setString(6, ownerGroup);
            ps.setTimestamp(7, Timestamp.valueOf(dateNow));
            ps.setString(8, "BY_SYSTEM");
            ps.setString(9, "AGENTNOTE");
            ps.setString(10, "Hasil pengukuran ibooster " + kategoriUkur);
            ps.setString(11, "");
            ps.executeUpdate();

        } catch (SQLException ex) {
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

    public void deleteRelatedRecord(String ticketId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "DELETE FROM app_fd_related_record_id WHERE c_ticket_id = ? ";

        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, ticketId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    public void updateStatusTicket(String ticketStatus, String id) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "UPDATE app_fd_ticket SET c_ticket_status = ? WHERE c_parent_id = ? ";

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query);
                ps.setString(1, ticketStatus);
                ps.setString(2, id);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    public void updateStatusAndAtionTicket(String actionStatus, String ticketStatus, String id) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "UPDATE app_fd_ticket SET c_action_status = ? ,c_ticket_status = ? WHERE c_parent_id = ? ";

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query);
                ps.setString(1, actionStatus);
                ps.setString(2, ticketStatus);
                ps.setString(3, id);
                ps.executeUpdate();
                // LogUtil.info(this.getClass().getName(), "Update Ticket Status");
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    public ListIbooster getDataIboosterByTicketId(String ticketId) {
        ListIbooster ibooster = new ListIbooster();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

        } catch (Exception ex) {

        }
        return ibooster;
    }
}
