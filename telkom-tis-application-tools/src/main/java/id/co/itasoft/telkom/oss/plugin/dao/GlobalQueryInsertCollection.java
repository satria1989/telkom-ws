/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.UuidGenerator;

/**
 *
 * @author asani
 */
public class GlobalQueryInsertCollection {
    
    LogInfo logInfo = new LogInfo();
    
    /**
     *  INSERT WORKLOGS
     * @param parentId
     * @param idTicket
     * @param ownerGroup
     * @param summary
     * @param msg
     * @throws SQLException 
     */
    public void insertWorkLogs(String parentId, String idTicket, String ownerGroup, String summary, String msg) throws SQLException {
        PreparedStatement ps = null;
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
            ps.setString(11, msg);
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
    
    public void insertTicketStatus(TicketStatus r) throws SQLException, Exception {
        String pinPoint = "";
        PreparedStatement ps = null;
        if (!"REQUEST_PENDING".equalsIgnoreCase(r.getActionStatus())
                && !"AFTER PENDING".equalsIgnoreCase(r.getActionStatus())
                && !"AFTER_REQUEST".equalsIgnoreCase(r.getActionStatus())) {
            pinPoint = "TRUE";
        }
        StringBuilder query = new StringBuilder();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        query
                .append(" INSERT INTO app_fd_ticketstatus ")
                .append(" ( ")
                .append(" id, ")
                .append(" dateCreated, ")
                .append(" dateModified, ")
                .append(" createdBy, ")
                .append(" createdByName, ")
                .append(" modifiedBy, ")
                .append(" modifiedByName, ")
                .append(" c_owner, ")
                .append(" c_ticketstatusid, ")
                .append(" c_changeby, ")
                .append(" c_memo, ")
                .append(" c_changedate, ")
                .append(" c_ownergroup, ")
                .append(" c_assignedownergroup, ")
                .append(" c_orgid, ")
                .append(" c_statustracking, ")
                .append(" c_siteid, ")
                .append(" c_class, ")
                .append(" c_ticketid, ")
                .append(" c_action_status, ")
                .append(" c_status, ")
                .append(" c_tkstatusid, ")
                .append(" c_pin_point ")
                .append(" ) ")
                .append(" VALUES ( ")
                .append(" ?, ")
                .append(" SYSDATE, ")
                .append(" SYSDATE, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" getduration(?), ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ? ")
                .append(" ) ");

        try {
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setString(2, r.getOwner());//createdBy
            ps.setString(3, "");//createdByName
            ps.setString(4, "");//modifiedBy
            ps.setString(5, "");//modifiedByName
            ps.setString(6, r.getOwner());
            ps.setString(7, r.getTicketStatusId());
            ps.setString(8, r.getChangeBy()); //r.getOwner()
            ps.setString(9, r.getMemo());
            ps.setString(10, r.getChangeDate());
            if ("NEW".equalsIgnoreCase(r.getStatus())) {
                ps.setString(11, "");
                ps.setString(12, "");
            } else {
                ps.setString(11, r.getOwnerGroup()); // last 
                ps.setString(12, r.getAssignedOwnerGroup()); //now
            }

            ps.setString(13, r.getOrgId());
            ps.setString(14, r.getStatusTracking());
            ps.setString(15, r.getSiteId());
            ps.setString(16, r.getClasS());
            ps.setString(17, r.getTicketId());
            ps.setString(18, (r.getActionStatus() == null) ? "" : r.getActionStatus());
            ps.setString(19, r.getStatus());
            ps.setString(20, r.getTkStatusId());
            ps.setString(21, pinPoint);
            ps.executeUpdate();

        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            r = null;
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

}
