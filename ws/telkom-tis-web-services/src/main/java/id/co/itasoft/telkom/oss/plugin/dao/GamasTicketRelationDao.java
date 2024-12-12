package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import okhttp3.OkHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mtaup
 */
public class GamasTicketRelationDao {

    CallRestAPI callApi = new CallRestAPI();
    public String parentTicket = "";

    public Ticket getDataChildTicket(String childTicketNumber) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        Ticket r = new Ticket();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append("SELECT  ")
                .append("id, ")
                .append("c_id_ticket,  ")
                .append("c_ticket_status,   ")
                .append("c_source_ticket, ")
                .append("c_service_id, ")
                .append("c_classification_type, ")
                .append("c_customer_segment, ")
                .append("c_external_ticketid, ")
                .append("C_EXTERNAL_TICKET_TIER3, ")
                .append("c_parent_id, ")
                .append("c_owner_group, ")
                .append("c_channel, ")
                .append("C_CLASSIFICATION_PATH, ")
                .append("C_LATITUDE, ")
                .append("C_LONGITUDE, ")
                .append("C_EXTERNALID_TA, ")
                .append("c_summary, ")
                .append("c_description_actualsolution, ")
                .append("c_work_zone, ")
                .append("c_incident_domain ")
                .append("FROM app_fd_ticket ")
                .append("WHERE c_id_ticket = ? ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, childTicketNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                r.setId(rs.getString("id"));
                r.setIdTicket(rs.getString("c_id_ticket"));
                r.setTicketStatus(rs.getString("c_ticket_status"));
                r.setSource_ticket(rs.getString("c_source_ticket"));
                r.setServiceId(rs.getString("c_service_id"));
                r.setClassificationType(rs.getString("c_classification_type"));
                r.setCust_segment(rs.getString("c_customer_segment"));
                r.setExtenalTicketId(rs.getString("c_external_ticketid"));
                r.setExtenalTicketTier3(rs.getString("C_EXTERNAL_TICKET_TIER3"));
                r.setParentId(rs.getString("c_parent_id"));
                r.setOwnerGroup(rs.getString("c_owner_group"));
                r.setChannel(rs.getString("c_channel"));
                r.setSymptom(rs.getString("C_CLASSIFICATION_PATH"));
                r.setLatitude(rs.getString("C_LATITUDE"));
                r.setLongitude(rs.getString("C_LONGITUDE"));
                r.setExternalIdTA(rs.getString("C_EXTERNALID_TA"));
                r.setSummary(rs.getString("c_summary"));
                r.setDescActsol(rs.getString("c_description_actualsolution"));
                r.setWorkzone(rs.getString("c_work_zone"));
                r.setIncidentDomain(rs.getString("c_incident_domain"));
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message rs : " + e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message con : " + e.getMessage());
            }
            gc = null;
            query = null;
        }
        return r;
    }

    public Ticket getDataParentTicket(String gamasTicket) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        Ticket r = new Ticket();
        StringBuilder query = new StringBuilder();
        query.append("SELECT  ")
                .append("c_id_ticket, ")
                .append("c_source_ticket, ")
                .append("c_perangkat, ")
                .append("c_classification_type, ")
                .append("c_parent_id ")
                .append("FROM app_fd_ticket ")
                .append("WHERE c_id_ticket = ? ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, gamasTicket);
            rs = ps.executeQuery();
            if (rs.next()) {
                r.setIdTicket(rs.getString("c_id_ticket"));
                r.setSource_ticket(rs.getString("c_source_ticket"));
                r.setPerangkat(rs.getString("c_perangkat"));
                r.setClassificationType(rs.getString("c_classification_type"));
                r.setRecordId(rs.getString("c_parent_id"));
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message rs : " + e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message con : " + e.getMessage());
            }
            query = null;
            gc = null;
        }
        return r;
    }

    public boolean checkToIpactedService(String serviceId, String recordId) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        boolean result = false;
        String query = "SELECT c_service_id FROM app_fd_ticket_imp_service WHERE c_service_id = ? and c_parent_id = ? ";
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, serviceId);
            ps.setString(2, recordId);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = true;
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message rs : " + e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message con : " + e.getMessage());
            }
            query = null;
            gc = null;
        }
        return result;

    }

    public boolean checkToRelatedRecords(String childTicket) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        boolean result = true;
        StringBuilder query = new StringBuilder();
        query.append("SELECT b.c_id_ticket ")
                .append("FROM app_fd_related_record_id a ")
                .append("JOIN app_fd_ticket b ON a.c_parent_id = b.c_parent_id ")
                .append("WHERE c_ticket_id = ? ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, childTicket);
            rs = ps.executeQuery();

            if (rs.next()) {
                parentTicket = rs.getString("c_id_ticket");
                result = false;
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message rs : " + e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message con : " + e.getMessage());
            }
            gc = null;
            query = null;
        }
        return result;
    }

    // NOT USED
    public boolean checkToTechnicalData(String parentId, String device) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        boolean result = false;
        String query = "select distinct c_device_name from app_fd_tis_technical_data where c_parent_id = ? and c_device_name = ? ";
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, parentId);
            ps.setString(2, device);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = true;
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message rs : " + e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message con : " + e.getMessage());
            }
            gc = null;
            query = null;
        }
        return result;
    }

    public void insertToRelatedRecord(String ticket_id, String processId, String idTicketGamas, String method) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        String query = "INSERT INTO app_fd_related_record_id (id, dateCreated, c_ticket_id, c_parent_id, c_relationship, c_ticket_id_gamas, c_method) VALUES (?,sysdate,?,?,?,?,?) ";
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setString(2, ticket_id);
            ps.setString(3, processId);
            ps.setString(4, "RELATEDTOGLOBAL");
            ps.setString(5, idTicketGamas);
            ps.setString(6, method);
            ps.executeUpdate();

        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message con : " + e.getMessage());
            }
            query = null;
            gc = null;
        }
    }

    public void updateIdGamasToChild(String idTicketGamas, String parentId) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        Connection con = gc.getJogetConnection();
        String query = "update app_fd_ticket set c_ticket_id_gamas = ? , c_related_to_gamas = ? where c_parent_id = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, idTicketGamas);
            ps.setString(2, "1");
            ps.setString(3, parentId);
            ps.executeUpdate();

        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message rs : " + e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message con : " + e.getMessage());
            }
            query = null;
            gc = null;
        }
    }

    public void insertWorkLogs(String parentId, String idTicket, String ownerGroup, String summary, String detail) throws SQLException, Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNow = (dateFormat.format(new Date()).toString());
        GetConnections gc = new GetConnections();
        Connection con = gc.getJogetConnection();
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO app_fd_ticket_work_logs ")
                .append("(id, c_parentid, dateCreated, c_recordkey, c_class, createdBy, c_ownergroup, c_created_date, c_createdby, c_log_type, c_summary, c_detail) ")
                .append("VALUES (?, ?, sysdate, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

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
            ps.setString(11, detail);
            ps.executeUpdate();


        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message rs : " + e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error message con : " + e.getMessage());
            }
            query = null;
            gc = null;
        }
    }

    public void getStatusWo(HashMap<String, String> params, String idTicket, String recordId) {

        LogHistory lh;
        LogHistoryDao lhdao;
        CallRestAPI callApi;
        MasterParamDao paramDao;
        MasterParam paramGetWo;
        MasterParam paramUpWo;
        ApiConfig apiConfig;
        ApiConfig apiConfig2;
        org.json.JSONObject reqObj;
        org.json.JSONObject resObj;
        GamasTicketRelationDao daoGtr;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            lh = new LogHistory();
            lhdao = new LogHistoryDao();
            callApi = new CallRestAPI();
            paramDao = new MasterParamDao();
            paramGetWo = new MasterParam();
            paramUpWo = new MasterParam();
            apiConfig = new ApiConfig();
            apiConfig2 = new ApiConfig();

            paramGetWo = paramDao.getUrl("get_wo");
            paramUpWo = paramDao.getUrl("updateWorkOrder");

            apiConfig.setUrl(paramGetWo.getUrl());
            apiConfig.setApiId(paramGetWo.getApi_id());
            apiConfig.setApiKey(paramGetWo.getApi_key());

            apiConfig2.setUrl(paramUpWo.getUrl());
            apiConfig2.setApiId(paramUpWo.getApi_id());
            apiConfig2.setApiKey(paramUpWo.getApi_key());

            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);
            
            
            LogHistoryDao lhDao = new LogHistoryDao();
            reqObj = new org.json.JSONObject(params);
            resObj = new org.json.JSONObject(response.toString());
            lhDao.SENDHISTORY(
                    idTicket,
                    "checkWorkORder",
                    paramGetWo.getUrl(), 
                    "GET", 
                    reqObj, 
                    resObj, 
                    0);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");
            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;
                String status = obj.get("status").toString();
                if (!"CANCELED".equalsIgnoreCase(status) && !"COMPLETED".equalsIgnoreCase(status)) {
                    daoGtr = new GamasTicketRelationDao();
                    String wonum = (obj.get("wonum") == null ? "" : obj.get("wonum").toString());
                    String rspCancelWo = callApi.updateWo(apiConfig2, wonum, idTicket);
                    daoGtr.insertWorkLogs(recordId, idTicket, "SYSTEM", "The work order is canceled because this ticket is related to Gamas", rspCancelWo);
                } else {
                }

            }

        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "Error Call API :" + e.getMessage());
        } finally {
            lh = null;
            lhdao = null;
            callApi = null;
            paramDao = null;
            paramGetWo = null;
            paramUpWo = null;
            apiConfig = null;
            apiConfig2 = null;
            reqObj = null;
            resObj = null;
        }

    }
    
    public void updateStatusTikcet(String actionStatus, String runProcess, String actualSolution, String descActualSolution, String memo, String processIdTicketSqm ) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = null;
        rs = null;
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("update app_fd_ticket ")
                .append("set c_action_status = ? ")
                .append(",c_run_process = ? ")
                .append(",c_actual_solution = ? ")
                .append(",c_description_actualsolution = ? ")
                .append(",c_memo = ? ")
                .append("WHERE c_parent_id = ? ");

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(queryUpdate.toString());
                ps.setString(1, actionStatus);
                ps.setString(2, runProcess);
                ps.setString(3, actualSolution);
                ps.setString(4, descActualSolution);
                ps.setString(5, memo);
                ps.setString(6, processIdTicketSqm);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            LogUtil.error(this.getClass().getName(), ex, "error : "+ex.getMessage());
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "error : "+ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
              LogUtil.error(this.getClass().getName(), ex, "error : "+ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
              LogUtil.error(this.getClass().getName(), ex, "error : "+ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
              LogUtil.error(this.getClass().getName(), ex, "error : "+ex.getMessage());
            }

        }
    }
    
    public String getActsolDescription(ApiConfig apiConfig, String hierarchyType, String classCode) throws ParseException {
        String result = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            ByteBuffer bytHierarchyType = StandardCharsets.UTF_8.encode(hierarchyType);
            String encodedHierarchyType = StandardCharsets.UTF_8.decode(bytHierarchyType).toString();
            ByteBuffer bytCslCode = StandardCharsets.UTF_8.encode(classCode);
            String encodedClassificationCode = StandardCharsets.UTF_8.decode(bytCslCode).toString();

            String urlWithParams = String.format("%s?hierarchy_type=%s&classification_code=%s", apiConfig.getUrl(), encodedHierarchyType, encodedClassificationCode);
            HttpGet request = new HttpGet(urlWithParams);
            request.setHeader("api_key", apiConfig.getApiKey());
            request.setHeader("api_id", apiConfig.getApiId());

            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = (HttpEntity) response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString((org.apache.http.HttpEntity) entity);
                    JSONParser parse = new JSONParser();
                    org.json.simple.JSONObject data_obj = (org.json.simple.JSONObject) parse.parse(responseBody);
                    JSONArray arrData = (JSONArray) data_obj.get("data");

                    for (Object object : arrData) {
                        org.json.simple.JSONObject obj = (org.json.simple.JSONObject) object;
                        result = obj.get("classification_description").toString();
                    }
                }
            } else {

            }
        } catch (IOException e) {
            LogUtil.error(this.getClass().getName(), e, "error : " + e.getMessage());
        }
        return result;
    }
    
    public Ticket getProcessIdTicketWithShk(String ticketId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;
        Ticket tc = new Ticket();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append(" select c.id, c.c_id_ticket, c.c_parent_id, c.c_status, c.c_ticket_status, d.PROCESSID, ")
                .append(" e.id as activity_id, e.ACTIVITYDEFINITIONID as activity_name, e.PDEFNAME as process_def_id, e.STATE")
                .append(" from app_fd_ticket c ")
                .append(" LEFT JOIN wf_process_link d ON c.c_parent_id = d.originProcessId ")
                .append(" LEFT join SHKACTIVITIES e on e.PROCESSID = d.PROCESSID ")
                .append(" WHERE 1=1 ")
                .append(" AND c.c_id_ticket = ? ")
                .append(" ORDER BY E.ACTIVATED DESC fetch first 1 row only ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);

            result = ps.executeQuery();
            while (result.next()) {
                tc.setId(result.getString("processId"));
                tc.setIdTicket(result.getString("c_id_ticket"));
                tc.setParentId(result.getString("c_parent_id"));
                tc.setStatus(result.getString("c_status"));
                tc.setTicketStatus(result.getString("c_ticket_status"));
                tc.setProcessId(result.getString("PROCESSID"));
                tc.setActivityId(result.getString("activity_id"));
                tc.setActivityName(result.getString("activity_name"));
                tc.setProcessDefId(result.getString("process_def_id"));
                tc.setState(result.getString("STATE"));
            }
        } catch (SQLException ex) {
            LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
        } finally {
            query = null;
            try {
                if (result != null) {
                    result.close();
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
        return tc;
    }

}
