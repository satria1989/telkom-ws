/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.TicketWorkLogs;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author suena
 */
public class TicketWorkLogsSaveDao {

    public boolean TicketWorkLogsSave(TicketWorkLogs r) throws SQLException, Exception {
        
        boolean result = false;
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" INSERT INTO app_fd_ticket_work_logs ")
                .append(" ( ")
                .append(" id, ")//UUID
                .append(" dateCreated, ")//DATE NOW
                .append(" dateModified, ")//DATE NOW
                .append(" createdBy, ")//SYSTEM
                .append(" createdByName, ")//SYSTEM
                .append(" modifiedBy, ")//SYSTEM
                .append(" modifiedByName, ")//SYSTEM
                //                .append(" c_createddate, ")//DATE NOW
                .append(" c_clientviewable, ")//SYSTEM
                .append(" c_ownergroup, ")//SYSTEM
                .append(" c_assignmentid, ")//SYSTEM
                .append(" c_parentid, ")//SYSTEM
                .append(" c_orgid, ")//SYSTEM
                .append(" c_log_type, ")//SYSTEM
                .append(" c_createdby, ")//SYSTEM
                .append(" c_recordkey, ")//SYSTEM
                .append(" c_summary, ")//SYSTEM
                .append(" c_siteid, ")//SYSTEM
                .append(" c_worklogid, ")//SYSTEM
                .append(" c_created_date, ")//SYSTEM
                .append(" c_anywhererrefid, ")//SYSTEM
                .append(" c_class, ")//SYSTEM
                //                .append(" c_summary_section_control, ")//SYSTEM
                .append(" c_detail, ")//SYSTEM
                .append(" c_attachment_file ")//SYSTEM
                .append(" ) ")
                .append(" VALUES  ")
                .append(" ( ")
                .append(" ?, ")//uuid
                .append(" ?, ")//dateCreated
                .append(" ?, ")//dateModified
                .append(" ?, ")//createdBy
                .append(" ?, ")//createdByName
                .append(" ?, ")//modifiedBy
                .append(" ?, ")//modifiedByName
                //                .append(" ?, ")//c_createddate
                .append(" ?, ")//c_clientviewable
                .append(" ?, ")//c_ownergroup
                .append(" ?, ")//c_assignmentid
                .append(" ?, ")//di isi parent id dari tabel app_fd_ticket
                .append(" ?, ")//c_orgid
                .append(" ?, ")//c_log_type
                .append(" ?, ")//c_createdby
                .append(" ?, ")//c_recordkey
                .append(" ?, ")//c_summary
                .append(" ?, ")//c_siteid
                .append(" ?, ")//c_worklogid
                .append(" ?, ")//c_created_date
                .append(" ?, ")//c_anywhererrefid
                .append(" ?, ")//c_class
                //                .append(" ?, ")//c_summary_section_control
                .append(" ?, ")//c_detail
                .append(" ? ")//c_attachment_file
                .append(" ) ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
//            UuidGenerator uuid = UuidGenerator.getInstance();
//            ps.setString(1, uuid.getUuid());
            ps.setString(1, r.getId());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, r.getCreatedBy());//createdBy
            ps.setString(5, r.getCreatedByName());//createdByName
            ps.setString(6, r.getModifiedBy());//modifiedBy
            ps.setString(7, r.getModifiedByName());//modifiedByName
//            ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
//            ps.setString(8, r.getCreatedDate());
            ps.setString(8, r.getClientviewable());
            ps.setString(9, r.getOwnergroup());
            ps.setString(10, r.getAssignmentid());
            ps.setString(11, r.getParentid());
            ps.setString(12, r.getOrgid());
            ps.setString(13, r.getLogType());
            ps.setString(14, r.getCreatedBy());
            ps.setString(15, r.getRecordkey());
            ps.setString(16, r.getSummary());
            ps.setString(17, r.getSiteid());
            ps.setString(18, r.getWorklogid());
            ps.setTimestamp(19, Timestamp.valueOf(r.getCreatedDate()));
            ps.setString(20, r.getAnywhererrefid());
            ps.setString(21, r.getClasS());
//            ps.setString(23, r.getSummarySectionControl());
            ps.setString(22, r.getDetail());
            ps.setString(23, r.getAttachmentFile());
//            LogUtil.info(this.getClass().getName(), "INSERT WORKLOG");
            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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

        return result;

    }

    public String getIdByTicketId(String ticketId) throws SQLException, Exception {
        String result = "";
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" id, ")
                .append(" c_id_ticket, ")
                .append(" c_parent_id ")
                .append(" FROM app_fd_ticket ")
                .append(" WHERE c_id_ticket = ? ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);

            rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getString("id");
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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

    public String getTicketStatus(String ticketNumber) throws SQLException, Exception {
        String ticketStatus = "";
        GetConnections gc = new GetConnections();
        String query = "SELECT c_ticket_status from app_fd_ticket WHERE c_id_ticket = ? ";
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, ticketNumber);
            result = ps.executeQuery();
            if (result.next()) {
                ticketStatus = result.getString("c_ticket_status");
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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
        return ticketStatus;

    }

    public void updateWorklogSummary(String worklogSummary, String id) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        query.append("UPDATE app_fd_ticket SET ")
                .append("c_worklog_summary = ? ")
                .append("WHERE id = ? ");

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());

                ps.setString(1, worklogSummary);
                ps.setString(2, id);
                ps.executeUpdate();
//                LogUtil.info(this.getClass().getName(), "## Update Pending Status");
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
    }

    public List<TicketWorkLogs> getDataWorklogs(String recordkey) throws SQLException, Exception {
        String result = "";
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        List<TicketWorkLogs> r = new ArrayList<>();
        query
                .append(" SELECT ")
                .append(" a.id, ")
                .append(" a.createdByName, ")
                .append(" a.c_anywhererrefid, ")
                .append(" a.c_assignmentid, ")
                .append(" a.c_attachment_file, ")
                .append(" a.c_class, ")
                .append(" a.c_clientviewable, ")
                .append(" a.c_created_date, ")
                .append(" a.c_detail, ")
                .append(" a.c_log_type, ")
                .append(" a.c_orgid, ")
                .append(" a.c_ownergroup, ")
                .append(" a.c_parentid, ")
                .append(" a.c_recordkey, ")
                .append(" a.c_siteid, ")
                .append(" a.c_summary, ")
                .append(" a.c_worklogid, ")
                .append(" a.dateCreated, ")
                .append(" a.modifiedBy, ")
                .append(" a.modifiedByName, ")
                .append(" b.c_lapul, ")
                .append(" b.c_gaul, ")
                .append(" b.c_id_ticket ")
                .append(" FROM APP_FD_TICKET_WORK_LOGS a ")
                .append(" LEFT JOIN APP_FD_TICKET b ON b.c_id_ticket=a.c_recordkey ")
                .append(" where a.c_recordkey = ? ")
                .append(" order by a.dateCreated desc ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, recordkey);
            rs = ps.executeQuery();
            TicketWorkLogs twl = null;
            while (rs.next()) {
                twl = new TicketWorkLogs();
                twl.setId(rs.getString("id"));
                twl.setCreatedByName(rs.getString("createdByName"));
                twl.setAnywhererrefid(rs.getString("c_anywhererrefid"));
                twl.setAssignmentid(rs.getString("c_assignmentid"));
                twl.setAttachmentFile(rs.getString("c_attachment_file"));
                twl.setClasS(rs.getString("c_class"));
                twl.setClientviewable(rs.getString("c_clientviewable"));
                twl.setCreatedDate(rs.getString("c_created_date"));
                twl.setDetail(rs.getString("c_detail"));
                twl.setLogType(rs.getString("c_log_type"));
                twl.setOrgid(rs.getString("c_orgid"));
                twl.setOwnergroup(rs.getString("c_ownergroup"));
                twl.setParentid(rs.getString("c_parentid"));
                twl.setRecordkey(rs.getString("c_recordkey"));
                twl.setSiteid(rs.getString("c_siteid"));
                twl.setSummary(rs.getString("c_summary"));
                twl.setWorklogid(rs.getString("c_worklogid"));
                twl.setDateCreated(rs.getString("dateCreated"));
                twl.setModifiedBy(rs.getString("modifiedBy"));
                twl.setModifiedByName(rs.getString("modifiedByName"));
                twl.setLapul(rs.getString("c_lapul"));
                twl.setGaul(rs.getString("c_gaul"));
                twl.setTicketId(rs.getString("c_id_ticket"));
                r.add(twl);
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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
    
}
