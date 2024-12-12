/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.StringManipulation;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.RelatedRecords;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.sql.DataSource;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mtaup
 */
public class RetryImpactedServiceDao {

    public void attributtTicket(String idTicket, Map<String, Integer> mapDataTotal) throws Exception {
        GetConnections gc = new GetConnections();
        Ticket r = new Ticket();

        List<String> resSelect = new ArrayList<>();
        StringBuilder querySelect = new StringBuilder();
        StringBuilder queryInsert = new StringBuilder();
        StringBuilder queryUpdate = new StringBuilder();

        querySelect.append("SELECT C_ATTRIBUTE_NAME FROM APP_FD_ATTRIBUTE_TICKET WHERE C_TICKET_ID = ?");
        queryInsert.append("INSERT INTO app_fd_attribute_ticket (id, dateCreated, c_ticket_id, c_attribute_name, c_attribute_value ) VALUES (?, sysdate, ?, ?, ?)");
        queryUpdate.append("UPDATE APP_FD_ATTRIBUTE_TICKET SET C_ATTRIBUTE_VALUE = ? WHERE C_TICKET_ID = ? AND C_ATTRIBUTE_NAME = ?");

        Connection con = gc.getJogetConnection();
        PreparedStatement psSelect = null, psInsert = null, psUpdate = null;
        ResultSet rs = null;
        try {
            psSelect = con.prepareStatement(querySelect.toString());
            psSelect.setString(1, idTicket);
            rs = psSelect.executeQuery();
            while (rs.next()) {
                resSelect.add(rs.getString("C_ATTRIBUTE_NAME"));
            }

            for (Map.Entry<String, Integer> entry : mapDataTotal.entrySet()) {
                if (resSelect.contains(entry.getKey())) {
                    psUpdate = con.prepareStatement(queryUpdate.toString());
                    psUpdate.setString(1, entry.getValue().toString());
                    psUpdate.setString(2, idTicket);
                    psUpdate.setString(3, entry.getKey());
                    psUpdate.executeUpdate();
                } else {
                    psInsert = con.prepareStatement(queryInsert.toString());
                    UuidGenerator uuid = UuidGenerator.getInstance();
                    psInsert.setString(1, uuid.getUuid());
                    psInsert.setString(2, idTicket);
                    psInsert.setString(3, entry.getKey());
                    psInsert.setString(4, entry.getValue().toString());
                    psInsert.executeUpdate();
                }

            }

        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "error :" + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (psSelect != null) {
                    psSelect.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (psUpdate != null) {
                    psUpdate.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (psInsert != null) {
                    psInsert.close();
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

            querySelect = null;
            queryUpdate = null;
            queryInsert = null;
        }
    }

    public String getOperStatus(String nd, String realm, String token) throws Exception {
        String operStatus = "";
        ApiConfig apiConfig = new ApiConfig();
        MasterParam param = new MasterParam();
        MasterParamDao paramDaoo = new MasterParamDao();
        CallRestAPI callApi = new CallRestAPI();
        param = paramDaoo.getUrl("get_ibooster");
        try {

            apiConfig.setUrl(param.getUrl());

            RequestBody formBody = new FormBody.Builder()
                    .add("nd", (nd == null ? "" : nd))
                    .add("realm", realm)
                    .build();

            String response = "";
            response = callApi.sendPostIbooster(apiConfig, formBody, token);

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
            LogUtil.error(this.getClass().getName(), ex, "error :" + ex.getMessage());
        } finally {
            apiConfig = null;
        }
        return operStatus;
    }

 public void insertToTableService(List<Ticket> ticket) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO app_fd_ticket_imp_service ")
                .append("(id, dateCreated, dateModified, c_service_id, c_ibooster_oper_status, c_parent_id, c_ticket_id, c_service_number, c_estimation, c_symptom, c_symptom_desc, c_region, c_perangkat, c_method, c_channel, c_service_type, c_segment, c_realm,C_PHONE_NUMBER) ")
                .append("VALUES (?,sysdate,sysdate,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        try {
            int batchSize = 200;
            int count = 0;
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();

            for (Ticket t : ticket) {
                ps.setString(1, uuid.getUuid());
                ps.setString(2, t.getServiceId());
                ps.setString(3, t.getOperStatus());
                ps.setString(4, t.getParentId());
                ps.setString(5, t.getTicketId());
                ps.setString(6, t.getServiceNumber());
                ps.setString(7, t.getEstimation());
                ps.setString(8, t.getSymptom());
                ps.setString(9, t.getSymptomDesc());
                ps.setString(10, t.getRegion());
                ps.setString(11, t.getPerangkat());
                ps.setString(12, t.getMethod());
                ps.setString(13, t.getChannel());
                ps.setString(14, t.getServiceType());
                ps.setString(15, t.getSegment());
                ps.setString(16, t.getRealm());
                ps.setString(17, t.getPhoneNumber());
                ps.addBatch();

                count++;
                if (count % batchSize == 0) {
                    ps.executeBatch();
                }
            }

            ps.executeBatch();
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }
        }
    }

    public Ticket getProcessIdTicket(String parentId) throws SQLException, Exception {
        GetConnections gc = new GetConnections();

        Ticket r = new Ticket();
        StringBuilder query = new StringBuilder();

        query.append(" select c_id_ticket, c_perangkat, c_ticket_status, c_parent_id, c_estimation, c_classification_path, c_class_description, c_region ")
                .append(" , c_classification_type, c_parent_id, datecreated, c_source_ticket, c_channel ")
                .append(" from app_fd_ticket ")
                .append(" where c_parent_id = ? ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = con.prepareStatement(query.toString());
            ps.setString(1, parentId);
            result = ps.executeQuery();
            if (result.next()) {
                r.setIdTicket(result.getString("c_id_ticket"));
                r.setPerangkat(result.getString("c_perangkat"));
                r.setTicketStatus(result.getString("c_ticket_status"));
                r.setParentId(result.getString("c_parent_id"));
                r.setEstimation(result.getString("c_estimation"));
                r.setSymptom(result.getString("c_classification_path"));
                r.setSymptomDesc(result.getString("c_class_description"));
                r.setRegion(result.getString("c_region"));
                r.setClassificationType(result.getString("c_classification_type"));
                r.setParentId(result.getString("c_parent_id"));
                r.setDateCreated(result.getString("datecreated"));
                r.setSourceTicket(result.getString("c_source_ticket"));
                r.setChannel(result.getString("c_channel"));
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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
            gc = null;
        }
        return r;

    }

    public List<String> getCurentImpactedervice(String parent_id) throws SQLException, Exception {
        GetConnections gc = new GetConnections();

        List<String> listImpcted = new ArrayList<>();
        StringBuilder query = new StringBuilder();

        query.append(" select c_service_id from app_fd_ticket_imp_service where c_parent_id = ? ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = con.prepareStatement(query.toString());
            ps.setString(1, parent_id);
            result = ps.executeQuery();
            while (result.next()) {
                listImpcted.add(result.getString("c_service_id"));
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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
            gc = null;
        }
        return listImpcted;

    }

    public Ticket getProcessIdTicketByParentId(String parentId) throws SQLException, Exception {
        GetConnections gc = new GetConnections();

        Ticket r = new Ticket();
        StringBuilder query = new StringBuilder();

        query.append(" select c_id_ticket, c_perangkat, c_ticket_status, c_parent_id, c_estimation, c_classification_path, c_class_description, c_region ")
                .append(" , c_classification_type, c_parent_id, datecreated, c_source_ticket, c_witel, c_work_zone, c_level_gamas, c_service_type, c_channel ")
                .append(" from app_fd_ticket ")
                .append(" where c_parent_id = ? ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = con.prepareStatement(query.toString());
            ps.setString(1, parentId);
            result = ps.executeQuery();
            if (result.next()) {
                r.setIdTicket(result.getString("c_id_ticket"));
                r.setPerangkat(result.getString("c_perangkat"));
                r.setTicketStatus(result.getString("c_ticket_status"));
                r.setParentId(result.getString("c_parent_id"));
                r.setEstimation(result.getString("c_estimation"));
                r.setSymptom(result.getString("c_classification_path"));
                r.setSymptomDesc(result.getString("c_class_description"));
                r.setRegion(result.getString("c_region"));
                r.setClassificationType(result.getString("c_classification_type"));
                r.setParentId(result.getString("c_parent_id"));
                r.setTimeDateCreated(result.getTimestamp("datecreated"));
                r.setSourceTicket(result.getString("c_source_ticket"));
                r.setWitel(result.getString("c_witel"));
                r.setWorkzone(result.getString("c_work_zone"));
                r.setLevelGamas(result.getString("c_level_gamas"));
                r.setServiceType(result.getString("c_service_type"));
                r.setChannel(result.getString("c_channel"));
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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
            gc = null;
        }
        return r;

    }

    public List<RelatedRecords> getTicketWithRuleLogic(
            String processId,
            String serviceType,
            String level,
            String region,
            String witel, String workzone,
            String idTicketGamas,
            Timestamp dateCreated,
            int estimation
    ) throws SQLException {

        List<RelatedRecords> r = new ArrayList<>();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        ResultSet rs = null;
        query
                .append(" SELECT DISTINCT ")
                .append(" C_ID_TICKET, ")
                .append(" C_SOURCE_TICKET, ")
                .append(" C_REGION, ")
                .append(" C_TICKET_STATUS, ")
                .append(" C_PERANGKAT, ")
                .append(" C_CUSTOMER_ID, ")
                .append(" C_SERVICE_ID, ")
                .append(" c_classification_type, ")
                .append(" C_PARENT_ID, ")
                .append(" ID, ")
                .append(" C_OWNER_GROUP, ")
                .append(" C_CHANNEL ")
                .append(" FROM app_fd_ticket tc ")
                .append(" WHERE C_TICKET_STATUS IN ('DRAFT','ANALYSIS','PENDING','BACKEND','FINALCHECK') ")
                .append(" AND C_SOURCE_TICKET != 'GAMAS'  ")
                .append(" AND datecreated >= (? - 3/24) ")
                .append(" AND datecreated <= (? + ?/24) ")
                .append(" AND C_SERVICE_ID is not null ")
                .append(" AND (c_ticket_id_gamas = '' or c_ticket_id_gamas is null ) ");

        if (!"INDIHOME".equalsIgnoreCase(serviceType)) {
            query.append(" and c_classification_type = 'LOGIC' ")
                    .append(" AND c_service_type = ?  ");
        } else {
            query.append(" and (c_classification_type = 'LOGIC' OR c_classification_type = 'FISIK') ");
        }

        if ("REGIONAL".equalsIgnoreCase(level)) {
            query.append(" AND c_region = ? ");
        } else if ("WITEL".equalsIgnoreCase(level)) {
            query.append(" AND c_witel = ? ");
        } else if ("WORKZONE".equalsIgnoreCase(level)) {
            query.append(" AND c_work_zone = ? ");
        }
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            if (!"INDIHOME".equalsIgnoreCase(serviceType)) {
                ps.setTimestamp(1, dateCreated);
                ps.setTimestamp(2, dateCreated);
                ps.setInt(3, estimation);
                ps.setString(4, serviceType);

                if ("REGIONAL".equalsIgnoreCase(level)) {
                    ps.setString(5, region);
                } else if ("WITEL".equalsIgnoreCase(level)) {
                    ps.setString(5, witel);
                } else if ("WORKZONE".equalsIgnoreCase(level)) {
                    ps.setString(5, workzone);
                }
            } else {
                ps.setTimestamp(1, dateCreated);
                ps.setTimestamp(2, dateCreated);
                ps.setInt(3, estimation);
                if ("REGIONAL".equalsIgnoreCase(level)) {
                    ps.setString(4, region);
                } else if ("WITEL".equalsIgnoreCase(level)) {
                    ps.setString(4, witel);
                } else if ("WORKZONE".equalsIgnoreCase(level)) {
                    ps.setString(4, workzone);
                }
            }

            try {
                rs = ps.executeQuery();
                RelatedRecords rr = null;

                while (rs.next()) {
                    rr = new RelatedRecords();
                    rr.setTicket_id(rs.getString("C_ID_TICKET"));
                    rr.setSource_ticket(rs.getString("C_SOURCE_TICKET"));
                    rr.setRegion(rs.getString("C_REGION"));
                    rr.setTicket_status(rs.getString("C_TICKET_STATUS"));
                    rr.setPerangkat(rs.getString("C_PERANGKAT"));
                    rr.setCustomer_id(rs.getString("C_CUSTOMER_ID"));
                    rr.setService_id(rs.getString("C_SERVICE_ID"));
                    rr.setIdTicketGamas(idTicketGamas);
                    rr.setParentId(rs.getString("C_PARENT_ID"));
                    rr.setRecordId(rs.getString("ID"));
                    rr.setOwnerGroup(rs.getString("C_OWNER_GROUP"));
                    rr.setChannel(rs.getString("C_CHANNEL"));

                    r.add(rr);
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }

        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            query = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }

        }
        return r;
    }

    public List<RelatedRecords> getTicketWithRuleFisik(
            String processId,
            String idTicketGamas,
            String classificationType,
            Timestamp datecreated,
            int estimation) throws SQLException {

        List<RelatedRecords> r = new ArrayList<>();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        query
                .append(" SELECT DISTINCT ")
                .append(" tc.C_ID_TICKET, ")
                .append(" tc.C_SOURCE_TICKET, ")
                .append(" tc.C_REGION, ")
                .append(" tc.C_TICKET_STATUS, ")
                .append(" tc.C_PERANGKAT, ")
                .append(" tc.C_CUSTOMER_ID, ")
                .append(" tc.C_SERVICE_ID, ")
                .append(" tc.c_classification_type, ")
                .append(" tc.c_parent_id, ")
                .append(" tc.id, ")
                .append(" tc.c_owner_group, ")
                .append(" tc.c_channel ")
                .append(" FROM app_fd_ticket tc ")
                .append(" WHERE tc.C_SERVICE_ID IN (SELECT c_service_id FROM app_fd_ticket_imp_service WHERE c_parent_id = ? ) ")
                .append(" and tc.c_classification_type = ? ")
                .append(" AND tc.C_SOURCE_TICKET != 'GAMAS'  ")
                .append(" AND tc.datecreated >= (? -3/24) ")
                .append(" AND tc.datecreated <= (? +?/24) ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);
            ps.setString(2, classificationType);
            ps.setTimestamp(3, datecreated);
            ps.setTimestamp(4, datecreated);
            ps.setInt(5, estimation);
            rs = ps.executeQuery();
            RelatedRecords rr = null;

            while (rs.next()) {
                // LogUtil.info(this.getClass().getName(), "DAO RR Set to Object");
                rr = new RelatedRecords();
                rr.setTicket_id(rs.getString("C_ID_TICKET"));
                rr.setSource_ticket(rs.getString("C_SOURCE_TICKET"));
                rr.setRegion(rs.getString("C_REGION"));
                rr.setTicket_status(rs.getString("C_TICKET_STATUS"));
                rr.setPerangkat(rs.getString("C_PERANGKAT"));
                rr.setCustomer_id(rs.getString("C_CUSTOMER_ID"));
                rr.setService_id(rs.getString("C_SERVICE_ID"));
                rr.setParentId(rs.getString("c_parent_id"));
                rr.setIdTicketGamas(idTicketGamas);
                rr.setRecordId(rs.getString("id"));
                rr.setOwnerGroup(rs.getString("c_owner_group"));
                rr.setChannel(rs.getString("c_channel"));

                r.add(rr);
            }
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
//            callApi = null;
            query = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }

        }

        return r;
    }

    public List<RelatedRecords> getTicketWithRuleCra(
            String processId,
            String idTicketGamas,
            String classificationType,
            Timestamp datecreated,
            int estimation) throws SQLException {

        List<RelatedRecords> r = new ArrayList<>();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        query
                .append(" SELECT DISTINCT ")
                .append(" tc.C_ID_TICKET, ")
                .append(" tc.C_SOURCE_TICKET, ")
                .append(" tc.C_REGION, ")
                .append(" tc.C_TICKET_STATUS, ")
                .append(" tc.C_PERANGKAT, ")
                .append(" tc.C_CUSTOMER_ID, ")
                .append(" tc.C_SERVICE_ID, ")
                .append(" tc.c_classification_type, ")
                .append(" tc.c_parent_id, ")
                .append(" tc.id, ")
                .append(" tc.c_owner_group, ")
                .append(" tc.c_channel ")
                .append(" FROM app_fd_ticket tc ")
                .append(" WHERE tc.C_SERVICE_ID IN (SELECT c_service_id FROM app_fd_ticket_imp_service WHERE c_parent_id = ? ) ")
                .append(" AND tc.datecreated <= (? +?/24) ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);
            ps.setTimestamp(2, datecreated);
            ps.setInt(3, estimation);
            rs = ps.executeQuery();
            RelatedRecords rr = null;

            while (rs.next()) {
                rr = new RelatedRecords();
                rr.setTicket_id(rs.getString("C_ID_TICKET"));
                rr.setSource_ticket(rs.getString("C_SOURCE_TICKET"));
                rr.setRegion(rs.getString("C_REGION"));
                rr.setTicket_status(rs.getString("C_TICKET_STATUS"));
                rr.setPerangkat(rs.getString("C_PERANGKAT"));
                rr.setCustomer_id(rs.getString("C_CUSTOMER_ID"));
                rr.setService_id(rs.getString("C_SERVICE_ID"));
                rr.setParentId(rs.getString("c_parent_id"));
                rr.setIdTicketGamas(idTicketGamas);
                rr.setRecordId(rs.getString("id"));
                rr.setOwnerGroup(rs.getString("c_owner_group"));
                rr.setChannel(rs.getString("c_channel"));

                r.add(rr);
            }
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            query = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }

        }

        return r;
    }

    public void deleteImpactedService(String recordId) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            if (!con.isClosed()) {
                String query = "DELETE FROM app_fd_ticket_imp_service WHERE c_parent_id = ? ";

                ps = con.prepareStatement(query);
                ps.setString(1, recordId);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {

            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
        }
    }

    public HashMap<String, String> getOperStatatus2(MasterParam param, String serviceNumber, String token) throws Exception {
        HashMap<String, String> result = new HashMap<>();
        String urlUkur = param.getUrl();
        String operStatus = "";
        // Membuat objek HttpClient
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // Membuat objek HttpPost dengan URL
        HttpPost httpPost = new HttpPost(urlUkur);
        JSONObject obj = new JSONObject();
        obj.put("nd", serviceNumber);
        obj.put("realm", "telkom.net");

        // Menetapkan body JSON
        StringEntity requestEntity = new StringEntity(obj.toJSONString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);

        // Menetapkan header Authorization dengan Bearer Token
        httpPost.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // Membaca respon menjadi String

            int status = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity);
            JSONParser parse = new JSONParser();
            if (status == 200) {
                JSONObject data_obj = (JSONObject) parse.parse(responseBody);
                String message = (data_obj.get("MESSAGE") == null ? "" : data_obj.get("MESSAGE").toString());
                if ("".equals(message)) {
                    operStatus = (data_obj.get("oper_status") == null ? "" : data_obj.get("oper_status").toString());
                }
                parse = null;
                data_obj = null;
            }

            result.put("statusRsp", String.valueOf(status));
            result.put("operStatus", operStatus);

            // Mengembalikan respon
            return result;
        }
    }

    public void updateOperStatus(String operStatus, String serviceId, String parentId) {
        Connection con = null;
        PreparedStatement ps = null;

        String query = "update app_fd_ticket_imp_service set c_ibooster_oper_status = ?, dateModified = sysdate where c_service_id = ? and c_parent_id = ? ";

        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());

                ps.setString(1, operStatus);
                ps.setString(2, serviceId);
                ps.setString(3, parentId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
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
    }

    public boolean checkToRr(String ticketId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean result = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        ps = null;
        rs = null;
        String queryUpdate = "SELECT c_ticket_id FROM app_fd_related_record_id where c_ticket_id = ? ";

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(queryUpdate);
                ps.setString(1, ticketId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    result = true;
                }
            }
        } catch (SQLException ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
            }

        }
        return result;
    }

    public void insertToRelatedRecord(String ticket_id, String processId, String idTicketGamas, String classificationType, String method) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = null;
        rs = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "INSERT INTO app_fd_related_record_id (id, dateCreated, c_ticket_id, c_parent_id, c_relationship, c_ticket_id_gamas, c_classification_type, c_method) VALUES (?,sysdate,?,?,?,?,?,?) ";
        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query);
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setString(2, ticket_id);
            ps.setString(3, processId);
            ps.setString(4, "RELATEDTOGLOBAL");
            ps.setString(5, idTicketGamas);
            ps.setString(6, classificationType);
            ps.setString(7, method);
            ps.executeUpdate();
//            ps.close();
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }

        }
    }

    public void updateIdGamasToChild2(String idTicketGamas, String parentId)
            throws SQLException {
        boolean result = false;
        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        String query = " update app_fd_ticket set c_ticket_id_gamas = ?, c_related_to_gamas = ? where c_parent_id = ? ";
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, idTicketGamas);
            ps.setString(2, "1");
            ps.setString(3, parentId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                result = true;
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
        } finally {
            query = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }
        }
//        return result;
    }

    public void insertWorkLogs(String parentId, String idTicket, String ownerGroup, String summary) throws SQLException {
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
            ps.setString(1, uuid);
            ps.setString(2, parentId);
            ps.setString(3, idTicket);
            ps.setString(4, "INCIDENT");
            ps.setString(5, "BY_SYSTEM");
            ps.setString(6, ownerGroup);
            ps.setTimestamp(7, Timestamp.valueOf(dateNow));
            ps.setString(8, "BY_SYSTEM");
            ps.setString(9, "AGENTNOTE");
            ps.setString(10, summary);
            ps.setString(11, "");
            ps.executeUpdate();

        } catch (SQLException ex) {
            LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }

        }
    }

}
