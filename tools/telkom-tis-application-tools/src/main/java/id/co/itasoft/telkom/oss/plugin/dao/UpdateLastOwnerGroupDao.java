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
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author suenawati rizky
 */
public class UpdateLastOwnerGroupDao {

    LogInfo logInfo = new LogInfo();
  
    public String pending_status = "";
    public String action_status = "";

    public TicketStatus getLastOwnerGroupByTicketID(String ticketId, String cdsn) throws SQLException {
        TicketStatus ts = new TicketStatus();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        StringBuilder query = new StringBuilder();
        String limit = "";

        if (!cdsn.equalsIgnoreCase("3")) {
            //reject
            query
                .append(" SELECT a.c_assignedownergroup c_assignedownergroup,  ")
                .append(" a.c_status c_status, a.c_action_status c_action_status, a.dateCreated dateCreated  ")
                .append(" FROM app_fd_ticketstatus a  ")
                .append(" JOIN app_fd_ticket b ON trim(a.c_ticketid) = trim(b.c_id_ticket)   ")
                .append(" WHERE trim(a.c_status) = trim(b.c_status)  ")
                .append(" AND trim(a.c_ticketid)=trim(?) AND trim(a.c_pin_point)='TRUE'  ")
                .append(" ORDER BY DATECREATED desc fetch first 1 row only ");
//                    .append(" SELECT max(a.c_assignedownergroup) c_assignedownergroup, ")
            //                    .append(" max(a.c_status)c_status, max(a.c_action_status) c_action_status, max(a.dateCreated) dateCreated ")
            //                    .append(" FROM app_fd_ticketstatus a ")
            //                    .append(" JOIN app_fd_ticket b ON trim(a.c_ticketid) = trim(b.c_id_ticket)  ")
            //                    .append(" WHERE trim(a.c_status) = trim(b.c_status) ")
            //                    .append(" AND trim(a.c_ticketid)=trim(?) AND trim(a.c_pin_point)='TRUE' ");

        } else {
            query
                    //                    .append(" SELECT DISTINCT max(dateCreated) dateCreated, max(a.c_assignedownergroup) c_assignedownergroup,")
                    //                    .append(" max(a.c_status) c_status, max(a.c_action_status) c_action_status ")
                .append(" SELECT DISTINCT  dateCreated, c_assignedownergroup, c_status, c_action_status, c_pin_point ")
                .append(" FROM app_fd_ticketstatus a  ")
                .append(" WHERE trim(a.c_status) IN ('ANALYSIS', 'BACKEND', 'DRAFT') ")
                .append(" AND trim(a.c_pin_point)='TRUE' AND trim(a.c_ticketid) = trim(?) ")
                .append(" ORDER BY dateCreated DESC fetch first 1 row only ");
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);

            rs = ps.executeQuery();
            if (rs.next()) {
                timestamp = new Timestamp(rs.getDate("dateCreated").getTime());
                ts.setDateCreated(timestamp);
                ts.setOwnerGroup(rs.getString("c_assignedownergroup"));
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
        return ts;
    }

    public void UpdateStatus(String processId, String ownergroup) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        PreparedStatement ps = null;
        Connection con = ds.getConnection();
//        con.setAutoCommit(false);
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_status = c_ticket_status, ")
                .append(" c_owner_group = ? ")
                .append(" WHERE c_parent_id = ? ");
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ownergroup);
            ps.setString(2, processId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    }

}
