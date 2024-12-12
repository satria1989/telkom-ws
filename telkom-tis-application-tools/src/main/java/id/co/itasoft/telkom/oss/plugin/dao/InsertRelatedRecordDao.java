/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.ImpactedService;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.RelatedRecords;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author asani
 */
public class InsertRelatedRecordDao {

    LogInfo logInfo = new LogInfo();
    public TicketStatus getDataTicket(String processId) throws SQLException {

        CallRestAPI callApi = new CallRestAPI();
        MasterParamDao paramDao = new MasterParamDao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        TicketStatus r = new TicketStatus();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" id, ")
                .append(" c_id_ticket, ")
                .append(" c_classification_type, ")
                .append(" c_service_type, ")
                .append(" c_level_gamas, ")
                .append(" c_source_ticket, ")
                .append(" c_work_zone, ")
                .append(" c_witel, ")
                .append(" c_region, ")
                .append(" c_service_id ")
                .append(" FROM app_fd_ticket ")
                .append(" WHERE c_parent_id=? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);

            rs = ps.executeQuery();
            if (rs.next()) {

                r.setId(rs.getString("id"));
                r.setTicketId(rs.getString("c_id_ticket"));
                r.setClassification_type(rs.getString("c_classification_type"));
                r.setServiceType(rs.getString("c_service_type"));
                r.setLevetGamas(rs.getString("c_level_gamas"));
                r.setSourceTicket(rs.getString("c_source_ticket"));
                r.setWorkZone(rs.getString("c_work_zone"));
                r.setWitel(rs.getString("c_witel"));
                r.setRegion(rs.getString("c_region"));
                r.setServiceId(rs.getString("c_service_id"));
            }
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            callApi = null;
            paramDao = null;
            query = null;
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

    public List<RelatedRecords> getTicketWithRuleFisik(String processId, String idTicketGamas, String classificationType) throws SQLException {
        List<RelatedRecords> r = new ArrayList<>();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
//        LogUtil.info(this.getClass().getName(), "DAO RR get Ticket Fisik - create_gamas");
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
                .append(" tc.c_owner_group ")
                //                .append(" (SELECT C_ID_TICKET FROM APP_FD_TICKET WHERE C_PARENT_ID = ? ) AS ID_TICKET_GAMAS ")
                .append(" FROM app_fd_ticket tc ")
                .append(" WHERE tc.C_TICKET_STATUS IN ('DRAFT','ANALYSIS','PENDING','BACKEND','FINALCHECK') ")
                .append(" AND tc.C_SERVICE_ID IN (SELECT c_service_id FROM app_fd_ticket_imp_service WHERE c_parent_id = ? ) ")
                .append(" and tc.c_classification_type = ? ")
                .append(" AND tc.C_SOURCE_TICKET != 'GAMAS'  ")
                .append(" AND tc.datecreated >= (sysdate-3/24) ")
                .append(" AND (tc.c_ticket_id_gamas = '' or tc.c_ticket_id_gamas is null ) ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);
            ps.setString(2, classificationType);
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

                r.add(rr);
            }
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
//            callApi = null;
            query = null;
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

    public List<RelatedRecords> getTicketWithRuleLogic(String processId, String serviceType, String level, String region, String witel, String workzone, String idTicketGamas) throws SQLException {
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
                .append(" C_OWNER_GROUP ")
                //                .append(" (SELECT a.C_ID_TICKET FROM APP_FD_TICKET a WHERE a.C_PARENT_ID = ? ) AS ID_TICKET_GAMAS ")
                .append(" FROM app_fd_ticket tc ")
                .append(" WHERE C_TICKET_STATUS IN ('DRAFT','ANALYSIS','PENDING','BACKEND','FINALCHECK') ")
                .append(" AND C_SOURCE_TICKET != 'GAMAS'  ")
                .append(" AND datecreated >= (sysdate-3/24) ")
                .append(" AND (c_ticket_id_gamas = '' or c_ticket_id_gamas is null ) ");

        if (!"INDIHOME".equalsIgnoreCase(serviceType)) {
//            LogUtil.info(this.getClass().getName(), "Q - SERVICE TYPE : " + serviceType);
            query.append(" and c_classification_type = 'LOGIC' ")
                    .append(" AND c_service_type = ?  ");
        } else {
            query.append(" and (c_classification_type = 'LOGIC' OR c_classification_type = 'FISIK') ");
        }

        if ("REGIONAL".equalsIgnoreCase(level)) {
            query.append(" AND c_region = ? ");
//            LogUtil.info(this.getClass().getName(), "Q - LEVEL REGIONAL  : " + region);
        } else if ("WITEL".equalsIgnoreCase(level)) {
            query.append(" AND c_witel = ? ");
//            LogUtil.info(this.getClass().getName(), "Q - LEVEL WITEL  : " + witel);
        } else if ("WORKZONE".equalsIgnoreCase(level)) {
            query.append(" AND c_work_zone = ? ");
//            LogUtil.info(this.getClass().getName(), "Q - LEVEL wokzone  : " + workzone);
        }
        // LogUtil.info(this.getClass().getName(), "Q uery  : " + query.toString());
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
//            ps.setString(1, processId);
            if (!"INDIHOME".equalsIgnoreCase(serviceType)) {
                ps.setString(1, serviceType);
//                LogUtil.info(this.getClass().getName(), "Set - SERVICE TYPE : " + serviceType);

                if ("REGIONAL".equalsIgnoreCase(level)) {
                    ps.setString(2, region);
//                    LogUtil.info(this.getClass().getName(), "Set - LEVEL region  : " + region);
                } else if ("WITEL".equalsIgnoreCase(level)) {
                    ps.setString(2, witel);
//                    LogUtil.info(this.getClass().getName(), "Set - LEVEL witel  : " + witel);
                } else if ("WORKZONE".equalsIgnoreCase(level)) {
                    ps.setString(2, workzone);
//                    LogUtil.info(this.getClass().getName(), "Set - LEVEL wokzone  : " + workzone);
                }
            } else {
                if ("REGIONAL".equalsIgnoreCase(level)) {
                    ps.setString(1, region);
//                    LogUtil.info(this.getClass().getName(), "Set - LEVEL region  : " + region);
                } else if ("WITEL".equalsIgnoreCase(level)) {
                    ps.setString(1, witel);
//                    LogUtil.info(this.getClass().getName(), "Set - LEVEL witel  : " + witel);
                } else if ("WORKZONE".equalsIgnoreCase(level)) {
                    ps.setString(1, workzone);
//                    LogUtil.info(this.getClass().getName(), "Set - LEVEL wokzone  : " + workzone);
                }
            }

            try {
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
                    rr.setIdTicketGamas(idTicketGamas);
                    rr.setParentId(rs.getString("C_PARENT_ID"));
                    rr.setRecordId(rs.getString("ID"));
                    rr.setOwnerGroup(rs.getString("C_OWNER_GROUP"));

                    r.add(rr);
                }
            } catch (SQLException ex) {
                logInfo.Log(getClass().getName(), ex.getMessage());
            }

        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    public void insertToRelatedRecord(String ticket_id, String processId, String idTicketGamas, String classificationType) throws SQLException {
        // LogUtil.info(this.getClass().getName(), "DAO RR Insert Ticket");
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = null;
        rs = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "INSERT INTO app_fd_related_record_id (id, dateCreated, c_ticket_id, c_parent_id, c_relationship, c_ticket_id_gamas, c_classification_type) VALUES (?,sysdate,?,?,?,?,?) ";
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
            ps.executeUpdate();
//            ps.close();
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
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
    }

    public List<ImpactedService> getChildGamasHandler(String processId) throws SQLException {
        List<ImpactedService> ts = new ArrayList<>();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        query
                .append(" SELECT c_service_id, c_ibooster_oper_status, ")
                .append(" c_estimation, ")
                .append(" c_service_number, ")
                .append(" c_symptom_desc, ")
                .append(" c_ticket_id, ")
                .append(" DATECREATED ")
                .append("  FROM app_fd_ticket_imp_service  ")
                .append("  WHERE LOWER(c_ibooster_oper_status)=LOWER('los') AND  ( c_send_wa is null or c_send_wa = '') AND rownum <= 100 AND DATECREATED is not null order by dateCreated asc ");

        Connection con = ds.getConnection();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
//            ps.setString(1, processId);

            rs = ps.executeQuery();
            ImpactedService getTs = null;
            while (rs.next()) {
                getTs = new ImpactedService();
                getTs.setService_id(rs.getString("c_service_id"));
                getTs.setIbooster_oper_status(rs.getString("c_ibooster_oper_status"));
                getTs.setEstimation(rs.getString("c_estimation"));
                getTs.setService_number(rs.getString("c_service_number"));
                getTs.setSymptomp_des(rs.getString("c_symptom_desc"));
                getTs.setTicket_id(rs.getString("c_ticket_id"));

                timestamp = new Timestamp(rs.getDate("DATECREATED").getTime());
                getTs.setDatecreated(timestamp);

                ts.add(getTs);
            }

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
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

        return ts;
    }

    public boolean checkToRr(String ticketId) throws SQLException {
        // LogUtil.info(this.getClass().getName(), "CheckToRr Function");
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
                    // LogUtil.info(this.getClass().getName(), "Ticket ID Already Exist In Related Record");
                }
            }
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
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
        return result;
    }

    public void updateStatusChildGamas(String status, String owner, String idTicketGamas, String memo, String ticketId) throws SQLException {
        // LogUtil.info(this.getClass().getName(), "Update child_gamas");
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = null;
        rs = null;
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("update app_fd_ticket ")
                .append("set c_child_gamas = ? ")
                .append(",c_owner = ? ")
                .append(",c_ticket_id_gamas = ? ")
                .append(",c_memo = ? ")
                .append("WHERE c_id_ticket = ? ");

        try {
//            LogUtil.info(this.getClass().getName(),"**Memo"+ memo);
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(queryUpdate.toString());
                ps.setString(1, status);
                ps.setString(2, owner);
                ps.setString(3, idTicketGamas);
                ps.setString(4, memo);
                ps.setString(5, ticketId);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
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
    }
    
    public void updateStatusChildGamas2(String status, String owner, String idTicketGamas, String memo, String ticketId, String action, String runProcess) throws SQLException {
        // LogUtil.info(this.getClass().getName(), "Update child_gamas");
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = null;
        rs = null;
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("update app_fd_ticket ")
                .append("set c_child_gamas = ? ")
                .append(",c_owner = ? ")
                .append(",c_ticket_id_gamas = ? ")
                .append(",c_memo = ? ")
                .append(",c_action_status = ? ")
                .append(",c_run_process = ? ")
                .append("WHERE c_id_ticket = ? ");

        try {
//            LogUtil.info(this.getClass().getName(),"**Memo"+ memo);
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(queryUpdate.toString());
                ps.setString(1, status);
                ps.setString(2, owner);
                ps.setString(3, idTicketGamas);
                ps.setString(4, memo);
                ps.setString(5, action);
                ps.setString(6, runProcess);
                ps.setString(7, ticketId);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
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
    }

    public void getStatusWo(HashMap<String, String> params, String idTicket, String action) {

        LogHistory lh = new LogHistory();
        LogHistoryDao lhdao = new LogHistoryDao();
        CallRestAPI callApi = new CallRestAPI();
        MasterParamDao paramDao = new MasterParamDao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            MasterParam paramGetWo = new MasterParam();
            paramGetWo = paramDao.getUrl("get_wo");

            MasterParam paramUpWo = new MasterParam();
            paramUpWo = paramDao.getUrl("updateWorkOrder");

            ApiConfig apiConfig = new ApiConfig();
            apiConfig.setUrl(paramGetWo.getUrl());
            apiConfig.setApiId(paramGetWo.getApi_id());
            apiConfig.setApiKey(paramGetWo.getApi_key());

            ApiConfig apiConfig2 = new ApiConfig();
            apiConfig2.setUrl(paramUpWo.getUrl());
            apiConfig2.setApiId(paramUpWo.getApi_id());
            apiConfig2.setApiKey(paramUpWo.getApi_key());

            String response = "";

            response = callApi.sendGetWithoutTokenString(apiConfig, params);
            lh.setUrl(paramGetWo.getUrl());
            lh.setAction(action);
            lh.setMethod("GET");
            lh.setRequest(params.toString());
            lh.setResponse(response);
            lh.setTicketId(idTicket);
            boolean insertLog = lhdao.insertToLogHistory(lh);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");
            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;
                LogUtil.info(this.getClass().getName(), "SSTATUS WORK ORDER  :  " + obj.get("status").toString());
                String status = obj.get("status").toString();
                if ("OPEN".equalsIgnoreCase(status) || "ASSIGNED".equalsIgnoreCase(status)) {
                    // LogUtil.info(this.getClass().getName(), "**** WO OPEN *****");
                    String wonum = (obj.get("wonum") == null ? "" : obj.get("wonum").toString());
                    callApi.updateWo(apiConfig2, wonum, idTicket);
                } else {
                    // LogUtil.info(this.getClass().getName(), "**** WO not OPEN *****");
                }

//                sb.append(";").append(obj.get("status") == null ? "" : obj.get("status").toString());
            }

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {

        }

    }

    public void updateStatusSendWANonActive(TicketStatus ts) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean result = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        ps = null;
        String queryUpdate = " update APP_FD_TICKET_IMP_SERVICE set c_send_wa = 'TRUE' WHERE C_TICKET_ID = ? AND C_SERVICE_ID = ? ";
        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(queryUpdate);
                ps.setString(1, ts.getTicketId());
                ps.setString(2, ts.getServiceId());
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
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
}
