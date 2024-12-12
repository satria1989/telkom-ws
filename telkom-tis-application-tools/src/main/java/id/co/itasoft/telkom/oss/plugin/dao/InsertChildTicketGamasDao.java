/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.GamasTicket;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
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
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.UuidGenerator;

/**
 *
 * @author mtaup
 */
public class InsertChildTicketGamasDao {
    LogInfo logInfo = new LogInfo();
//    MeasureIboosterGamasDao mig = new MeasureIboosterGamasDao(); // UNUSED JADI DIKOMEN
    public TicketStatus getTicketId(String processId) throws SQLException {

        TicketStatus r = new TicketStatus();

        PreparedStatement ps = null;
        ResultSet rs = null;

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();

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
                .append(" c_service_id, ")
                .append(" c_perangkat, ")
                .append(" c_estimation, ")
                .append(" c_classification_path, ")
                .append(" c_class_description, ")
                .append(" c_closed_by, ")
                .append(" c_urgensi, ")
                .append(" c_channel, ")
                .append(" c_actual_solution, ")
                .append(" c_hard_complaint, ")
                .append(" c_solution_code, ")
                .append(" c_street_address, ")
                .append(" c_service_id, ")
                .append(" c_customer_segment, ")
                .append(" c_subsidiary, ")
                .append(" c_created_ticket_by, ")
                .append(" c_realm, ")
                .append(" c_impact, ")
                .append(" c_ticket_status, ")
                .append(" c_service_category, ")
                .append(" c_owner_group, ")
                .append(" c_flag_fcr, ")
                .append(" c_service_no ")
                .append(" FROM app_fd_ticket ")
                .append(" WHERE c_parent_id= ? ");
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);

            rs = ps.executeQuery();
            while (rs.next()) {

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
                r.setPerangkat(rs.getString("c_perangkat"));
                r.setEstimation(rs.getString("c_estimation"));
                r.setSymptomId(rs.getString("c_classification_path"));
                r.setSymptomDesc(rs.getString("c_class_description"));

                r.setClosedBy(rs.getString("c_closed_by"));
                r.setUrgency(rs.getString("c_urgensi"));
                r.setChannel(rs.getString("c_channel"));
                r.setHardComplaint(rs.getString("c_hard_complaint"));
                r.setActualSolution(rs.getString("c_actual_solution"));
                r.setSolution(rs.getString("c_solution_code"));
                r.setStreetAddress(rs.getString("c_street_address"));
                r.setServiceId(rs.getString("c_service_id"));
                r.setCustomerSegment(rs.getString("c_customer_segment"));
                r.setSubsidiary(rs.getString("c_subsidiary"));
                r.setCreatedTicketBy(rs.getString("c_created_ticket_by"));
                r.setRealm(rs.getString("c_realm"));
                r.setImpact(rs.getString("c_impact"));
                r.setTicketStatusId(rs.getString("c_ticket_status"));
                r.setServiceCategory(rs.getString("c_service_category"));
                r.setOwnerGroup(rs.getString("c_owner_group"));
                r.setFlagFcr(rs.getString("c_flag_fcr"));
                r.setServiceNo(rs.getString("c_service_no"));

                // LogUtil.info(this.getClass().getName(), "## SERVICE ID : " + rs.getString("C_SERVICE_ID"));
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            try {
                rs.close();
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

        return r;
    }

    public void checkParentTicketFisik(String serviceId, String parentId, String clsfType, String ticketId, String recordId, String ownerGroup) throws SQLException {
        // Get Ticket Induk
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT tc.c_id_ticket, tc.c_parent_id, tc.datecreated  ")
                .append("FROM app_fd_ticket tc   ")
                .append("JOIN app_fd_ticket_imp_service im ON tc.c_parent_id = im.c_parent_id   ")
                .append("WHERE tc.c_source_ticket = 'GAMAS'   ")
                .append("AND im.c_service_id = ? ")
                .append("AND tc.C_TICKET_STATUS IN ('DRAFT','ANALYSIS','PENDING','BACKEND','FINALCHECK')  ")
                .append("AND tc.c_classification_type = ? ")
                .append("AND tc.c_id_ticket is not null ")
                .append("AND ROWNUM = 1 ")
                .append("ORDER BY tc.datecreated DESC ");
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, serviceId);
            ps.setString(2, clsfType);
            rs = ps.executeQuery();
            if (rs.next()) {
                String parentGamas = rs.getString("c_parent_id");
                String idTicketGamas = rs.getString("c_id_ticket");

                insertChildTicketToParent(parentGamas, ticketId, idTicketGamas, clsfType);
                updateIdGamasToChild(idTicketGamas, "TRUE", parentId);
                insertWorkLogs(recordId, ticketId, ownerGroup, "This Ticket is Related to Gamas " + idTicketGamas);
//                LogUtil.info(this.getClass().getName(), "parentGamas : " + idTicketGamas);

                // cancel wo
                InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
                HashMap<String, String> paramCkWo = new HashMap<String, String>();
                paramCkWo.put("externalID1", ticketId);
                irdao.getStatusWo(paramCkWo, ticketId, "checkWorkORder - GAMAS");
//                irdao.updateStatusChildGamas("TRUE",ticketId);
            } else {
//                LogUtil.info(this.getClass().getName(), "PARENT TICKET NOT FOUND");
            }
        } catch (SQLException ex) {
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
            query = null;

        }
    }

    public boolean checkParentTicketLogic(String ticketId, String classificationType, String serviceType, String hierarchi, String region, String witel, String workzone, String processId) throws SQLException {
        boolean result = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String level = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        StringBuilder query = new StringBuilder();
        query.append(" SELECT DISTINCT c_id_ticket, c_parent_id, datecreated  ")
                .append(" FROM app_fd_ticket ")
                .append(" WHERE c_source_ticket = 'GAMAS' ")
                .append(" AND C_TICKET_STATUS IN ('DRAFT','ANALYSIS','PENDING','BACKEND','FINALCHECK') ")
                .append(" AND (c_service_type = ? or c_service_type = 'INDIHOME')")
                .append(" AND (c_classification_type = ? or c_classification_type = 'INDIHOME') ");
        if ("NASIONAL".equalsIgnoreCase(hierarchi)) {
//            LogUtil.info(this.getClass().getName(), "LEVEL NASIONAL");
            query.append(" AND C_LEVEL_GAMAS  = 'NASIONAL' ");
        } else if ("REGIONAL".equalsIgnoreCase(hierarchi)) {
            query.append(" AND c_region = ? ");
            query.append(" AND C_LEVEL_GAMAS  = 'REGIONAL' ");
            level = region;
//            LogUtil.info(this.getClass().getName(), "LEVEL REGIONAL");
        } else if ("WITEL".equalsIgnoreCase(hierarchi)) {
            query.append(" AND c_witel = ? ");
            query.append(" AND C_LEVEL_GAMAS  = 'WITEL' ");
            level = witel;
//            LogUtil.info(this.getClass().getName(), "LEVEL WITEL");
        } else if ("WORKZONE".equalsIgnoreCase(hierarchi)) {
            query.append(" AND c_work_zone = ? ");
            query.append(" AND C_LEVEL_GAMAS  = 'WORKZONE' ");
            level = workzone;
//            LogUtil.info(this.getClass().getName(), "LEVEL WOROKZONE");
        }
//        query.append(" AND ROWNUM = 1 ")
        query.append(" ORDER BY datecreated DESC ");
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, serviceType);
            ps.setString(2, classificationType);
            if (!"NASIONAL".equalsIgnoreCase(hierarchi)) {
                ps.setString(3, level);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                result = true;
                // LogUtil.info(this.getClass().getName(), "Result True , Level " + hierarchi);
                String parentGamas = rs.getString("c_parent_id");
                String idTicketGamas = rs.getString("c_id_ticket");
                insertChildTicketToParent(parentGamas, ticketId, idTicketGamas, classificationType);
                updateIdGamasToChild(idTicketGamas, "TRUE", processId);
//                LogUtil.info(this.getClass().getName(), "ID_TICKET_GAMAS : " + idTicketGamas);
                // LogUtil.info(this.getClass().getName(), "CHECK PARENT TICKET");

//                InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
//                HashMap<String, String> paramCkWo = new HashMap<String, String>();
//                paramCkWo.put("externalID1", ticketId);
//                irdao.getStatusWo(paramCkWo, ticketId);
//                    irdao.updateStatusTicket(ticketId);
            }
        } catch (SQLException ex) {
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

    public List<GamasTicket> getPrentTicketLogic(String serviceType, String classificationType, String region, String witel, String workzone) throws SQLException {
        //Get Parent Ticket (GAMAS)
        List<GamasTicket> gamasTicket = new ArrayList<>();
//        CallRestAPI callApi = new CallRestAPI();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
//        LogUtil.info(this.getClass().getName(), "DAO RR get Ticket Fisik - create_gamas");
        query.append("SELECT c_id_ticket, c_parent_id, datecreated, c_level_gamas, c_service_type, c_classification_type, ")
                .append("c_region,c_witel,c_work_zone ")
                .append("FROM app_fd_ticket  ")
                .append("WHERE c_source_ticket = 'GAMAS' ")
                .append("AND C_TICKET_STATUS IN ('DRAFT','ANALYSIS','PENDING','BACKEND','FINALCHECK') ")
                .append("AND (c_service_type = ? or c_service_type = 'INDIHOME') ")
                .append("AND (c_classification_type = ? or c_classification_type = 'INDIHOME') ")
                .append("AND c_id_ticket is not null ")
                .append("order by dateCreated desc");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, serviceType);
            ps.setString(2, classificationType);
            rs = ps.executeQuery();
            GamasTicket gt = null;

            while (rs.next()) {
                gt = new GamasTicket();
                gt.setIdTicket(rs.getString("c_id_ticket"));
                gt.setParentId(rs.getString("c_parent_id"));
                gt.setDateCreated(rs.getString("datecreated"));
                gt.setLevelGamas(rs.getString("c_level_gamas"));
                gt.setServiceType(rs.getString("c_service_type"));
                gt.setClassificationType(rs.getString("c_classification_type"));
                gt.setRegion(rs.getString("c_region"));
                gt.setWitel(rs.getString("c_witel"));
                gt.setWorkzone(rs.getString("c_work_zone"));

                gamasTicket.add(gt);
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

        return gamasTicket;
    }

    public void insertChildTicketToParent(String parentGamas, String ticketId, String idTicketGamas, String classificationType) throws SQLException {

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "INSERT INTO app_fd_related_record_id (id, dateCreated, c_ticket_id, c_parent_id, c_relationship, c_ticket_id_gamas,c_classification_type) VALUES (?, sysdate, ?,?,?,?,?) ";
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query);
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setString(2, ticketId);
            ps.setString(3, parentGamas);
            ps.setString(4, "RELATEDTOGLOBAL");
            ps.setString(5, idTicketGamas);
            ps.setString(6, classificationType);
            ps.executeUpdate();

//            LogUtil.info(this.getClass().getName(), "Insert To Table Related Record");
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

    public void updateIdGamasToChild(String idTicketGamas, String statusChildGamas, String parentId) throws SQLException {

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "update app_fd_ticket set c_ticket_id_gamas = ? where c_parent_id = ? ";
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, idTicketGamas);
            ps.setString(2, parentId);
            ps.executeUpdate();

//            LogUtil.info(this.getClass().getName(), "Update ID Gamas on Child Gamas");
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
            ps.setString(10, summary);
            ps.setString(11, "");
            ps.executeUpdate();
//            LogUtil.info(this.getClass().getName(), "GAMAS INSERT WORKLOG");

        } catch (SQLException ex) {
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

//    public void checkParentTicketLogic(String serviceId, String clsfType, String ticketId) {
//
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT DISTINCT tc.c_id_ticket, tc.c_parent_id, tc.datecreated " +
//                "FROM app_fd_ticket tc " +
//                "JOIN app_fd_ticket_imp_service im ON tc.c_parent_id = im.c_parent_id " +
//                "WHERE tc.c_source_ticket = 'GAMAS' " +
//                "AND tc.C_TICKET_STATUS IN ('DRAFT','ANALYSIS','PENDING','BACKEND','FINALCHECK') " +
//                "AND tc.c_classification_type = ? " +
////                "AND tc.datecreated  >= (sysdate-3/24)" +
//                "AND ROWNUM = 1 " +
//                "ORDER BY tc.datecreated DESC";
//        try (Connection con = ds.getConnection()) {
//            ps = con.prepareStatement(query);
//            ps.setString(1, clsfType);
//            try (rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    String parentGamas = rs.getString("c_parent_id");
//                    String idTicketGamas = rs.getString("c_id_ticket");
//                    insertChildTicketToParent(serviceId, parentGamas, ticketId, idTicketGamas);
// //                    LogUtil.info(this.getClass().getName(), "CHECK PARENT TICKET");
//                    
//                    InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
//                    HashMap<String, String> paramCkWo = new HashMap<String, String>();
//                    paramCkWo.put("externalID1", ticketId);
//                    irdao.getStatusWo(paramCkWo, ticketId);
//                }
//            }
//        } catch (SQLException ex) {
//            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
//        }
//    }
}
