/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.attributeTicket;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author asani
 */
public class SelectCollections {

    LogInfo log = new LogInfo();

    public String getDataPermission(String currentUser) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        String data = "";
        String query = "select c_menu_permission from app_fd_tis_user_permission where c_username = ? ";
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            ps = con.prepareStatement(query);

            ps.setString(1, currentUser);
            try {
                result = ps.executeQuery();
                if (result.next()) {
                    data = (result.getString("c_menu_permission") == null ? "" : result.getString("c_menu_permission"));
                }

            } catch (SQLException e) {
                log.Log(getClass().getName(), e.getMessage());
            }
        } catch (SQLException e) {
            log.Log(getClass().getName(), e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            query = null;
        }
        return data;

    }

    public JSONObject getConfigurationMapping() throws Exception {
        GetConnections gc = new GetConnections();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        JSONObject result = new JSONObject();
        query
                .append(" select id, datecreated, datemodified, c_button_wo, ")
                .append(" c_check_ibooster, c_atvr_resolved, ")
                .append(" c_day_resolved, c_hour_resolved, c_minutes_resolved, ")
                .append(" c_check_tsc, c_check_scc, ")
                .append(" C_CTS_SYMPTOM, c_scc_plus, c_gamas_pending, c_check_sccplus_nas ")
                .append(" From app_fd_configuration where id = '1' ");

        Connection con = gc.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                result.put("id", rs.getString("id"));
                result.put("datecreated", rs.getString("datecreated"));
                result.put("datemodified", rs.getString("datemodified"));
                result.put("checkIbooster", rs.getBoolean("c_check_ibooster"));
                result.put("checkScc", rs.getBoolean("c_check_scc"));
                result.put("checkTsc", rs.getBoolean("c_check_tsc"));
                result.put("buttonWo", rs.getBoolean("c_button_wo"));
                result.put("deadline_resolved", rs.getBoolean("c_atvr_resolved"));
                result.put("day_resolved", rs.getInt("c_day_resolved"));
                result.put("hour_resolved", rs.getInt("c_hour_resolved"));
                result.put("minutes_resolved", rs.getInt("c_minutes_resolved"));
                result.put("gamas_pending", (rs.getString("c_gamas_pending") == null) ? "" : rs.getString("c_gamas_pending"));

                result.put("cts_symptom", (rs.getString("C_CTS_SYMPTOM") == null) ? "" : rs.getString("C_CTS_SYMPTOM"));
                result.put("scc_plus", (rs.getString("c_scc_plus") == null) ? "" : rs.getString("c_scc_plus"));
                result.put("check_sccplus_nas", (rs.getString("c_check_sccplus_nas") == null) ? "" : rs.getString("c_check_sccplus_nas"));
            } else {
                result.put("id", "");
                result.put("datecreated", "");
                result.put("datemodified", "");
                result.put("checkIbooster", true);
                result.put("buttonWo", true);
                result.put("checkScc", true);
                result.put("checkTsc", true);
                result.put("cts_symptom", "");
                result.put("scc_plus", "");
                result.put("c_check_sccplus_nas", true);
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
        }
        return result;
    }

    public List<attributeTicket> getAttributeTicket(String ticketId, String[] attributeName) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        attributeTicket attr = null;
        String data = "";
        List<attributeTicket> att = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT c_attribute_name, c_attribute_value "
                + "FROM app_fd_attribute_ticket "
                + "WHERE c_ticket_id = ? "
                + "AND c_attribute_name IN ("
        );
        for (int i = 0; i < attributeName.length; i++) {
            queryBuilder.append("?");
            if (i < attributeName.length - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(")");

        String query = queryBuilder.toString();
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            ps = con.prepareStatement(query);

            ps.setString(1, ticketId);
            for (int i = 0; i < attributeName.length; i++) {
                ps.setString(i + 2, attributeName[i]);
            }

            try {
                result = ps.executeQuery();
                while (result.next()) {
                    attr = new attributeTicket();
                    attr.setAttributeName(result.getString("c_attribute_name"));
                    attr.setAttributeValue(result.getString("c_attribute_value"));
                    att.add(attr);
                }

            } catch (SQLException e) {
                log.Log(getClass().getName(), e.getMessage());
            }
        } catch (SQLException e) {
            log.Log(getClass().getName(), e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            query = null;
        }
        return att;

    }

    public String getPathMinio(String field) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        String result = "";
        query
                .append(" SELECT ")
                .append(field)
                .append(" FROM app_fd_configuration ")
                .append(" where id = '1'");
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            log.Log(this.getClass().getName(), "query : " + query.toString());
            ps = con.prepareStatement(query.toString());

            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("c_minio_attachment") == null ? "" : rs.getString("c_minio_attachment").trim();
            }
        } catch (Exception ex) {
            log.Error(this.getClass().getName(), "Get Oper Status", ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.Error(this.getClass().getName(), "Get Oper Status", ex);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                log.Error(this.getClass().getName(), "Get Oper Status", ex);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
        }
        return result;
    }

    public Ticket getDataTicket(String idTicket) throws Exception {
        Ticket t = new Ticket();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        String data = "";
        StringBuilder sb = new StringBuilder();

        sb.append(" select ")
                .append(" id ")
                .append(" ,c_id_ticket ")
                .append(" ,c_service_id ")
                .append(" ,c_contact_name ")
                .append(" ,c_street_address ")
                .append(" ,c_latitude ")
                .append(" ,c_longitude ")
                .append(" ,c_work_zone ")
                .append(" ,c_technician ")
                .append(" ,c_co_sn_ont ")
                .append(" ,c_co_new_onu_sn ")
                .append(" ,c_co_technician_code ")
                .append(" ,c_co_service_no ")
                .append(" from app_fd_ticket ")
                .append(" where c_id_ticket = ? ");
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            ps = con.prepareStatement(sb.toString());

            ps.setString(1, idTicket);
            try {
                result = ps.executeQuery();
                if (result.next()) {
                    t.setId(result.getString("id"));
                    t.setIdTicket(result.getString("c_id_ticket"));
                    t.setServiceId(result.getString("c_service_id"));
                    t.setContactName(result.getString("c_contact_name"));
                    t.setStreetAddress(result.getString("c_street_address"));
                    t.setLatitude(result.getString("c_latitude"));
                    t.setLongitude(result.getString("c_longitude"));
                    t.setWorkzone(result.getString("c_work_zone"));
                    t.setTechnician(result.getString("c_technician"));
                    t.setCoSnOnt(result.getString("c_co_sn_ont"));
                    t.setCoNewOnuSn(result.getString("c_co_new_onu_sn"));
                    t.setCoTechnicianCode(result.getString("c_co_technician_code"));
                    t.setServiceNumber(result.getString("c_co_service_no"));
                }

            } catch (SQLException e) {
                log.Log(getClass().getName(), e.getMessage());
            }
        } catch (SQLException e) {
            log.Log(getClass().getName(), e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            sb = null;
        }
        return t;
    }

    public Ticket reqDataBromoGamas(String ticketId) {
        GetConnections gc = new GetConnections();
        Ticket ticket = new Ticket();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT "
                + "  C_REGION, "
                + "  C_WITEL, "
                + "  C_WORK_ZONE, "
                + "  C_SUMMARY, "
                + "  C_REPORTED_DATE, "
                + "  C_OWNER_GROUP, "
                + "  C_CUSTOMER_SEGMENT, "
                + "  C_STATUS, "
                + "  C_REPORTED_BY, "
                + "  C_CONTACT_PHONE, "
                + "  C_CONTACT_NAME, "
                + "  C_CONTACT_EMAIL, "
                + "  C_CLASS_DESCRIPTION, "
                + "  C_CLASSIFICATION_PATH, "
                + "  C_PERANGKAT, "
                + "  C_TECHNICIAN, "
                + "  C_SOURCE_TICKET "
                + "FROM "
                + "  APP_FD_TICKET "
                + "WHERE "
                + "  C_ID_TICKET = ? ";

        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query);
            ps.setString(1, ticketId);
            rs = ps.executeQuery();
            if (rs.next()) {
                ticket.setRegion(rs.getString("C_REGION"));
                ticket.setWitel(rs.getString("C_WITEL"));
                ticket.setSto(rs.getString("C_WORK_ZONE"));
                ticket.setSummary(rs.getString("C_SUMMARY"));
                ticket.setReportedDate(rs.getString("C_REPORTED_DATE"));
                ticket.setOwnerGroup(rs.getString("C_OWNER_GROUP"));
                ticket.setCust_segment(rs.getString("C_CUSTOMER_SEGMENT"));
                ticket.setStatus(rs.getString("C_STATUS"));
                ticket.setReportedBy(rs.getString("C_REPORTED_BY"));
                ticket.setContactPhone(rs.getString("C_CONTACT_PHONE"));
                ticket.setContactName(rs.getString("C_CONTACT_NAME"));
                ticket.setContactEmail(rs.getString("C_CONTACT_EMAIL"));
                ticket.setSymptomDesc(rs.getString("C_CLASS_DESCRIPTION"));
                ticket.setSymptom(rs.getString("C_CLASSIFICATION_PATH"));
                ticket.setPerangkat(rs.getString("C_PERANGKAT"));
                ticket.setTechnician(rs.getString("C_TECHNICIAN"));
                ticket.setSourceTicket(rs.getString("C_SOURCE_TICKET"));
            }

        } catch (Exception e) {
            log.Log(getClass().getName(), e.getMessage());
        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
            }
            query = "";
        }

        return ticket;
    }
    
    public String getSeqExtIdNte() throws Exception {
        String seqExtIdNte = "";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT GET_EXTERNALID_NTE FROM dual");
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(sbQuery.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    seqExtIdNte = rs.getString("GET_EXTERNALID_NTE");
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                log.Log(getClass().getName(), ex.getMessage());
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
            sbQuery = null;

        }
        return seqExtIdNte;
    }

}
