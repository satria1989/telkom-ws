/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author Tarkiman
 */
public class LoadTicketStatusDao {

    public String[] getCurrentStatusOfTicket(String ticketId, String table) throws SQLException, Exception {
        String[] result = new String[4];
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" c_action_status, ")
                .append(" c_ticket_status, ")
                .append(" c_pending_status, ")
                .append(" c_customer_segment ");
        if (table.equals("tableRepo")) {
            query.append(" FROM app_fd_ticket_repo ");
        } else {
            query.append(" FROM app_fd_ticket ");
        }
        query.append(" WHERE c_id_ticket=? ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);

            rs = ps.executeQuery();
            while (rs.next()) {
                if ("APPROVED".equalsIgnoreCase(rs.getString("c_pending_status"))) {
                    result[0] = (!(rs.getString("c_action_status") == null) ? rs.getString("c_action_status") : "");
                    result[1] = "PENDING";
                    result[2] = (!(rs.getString("c_pending_status") == null) ? rs.getString("c_pending_status") : "");
                    result[3] = (!(rs.getString("c_customer_segment") == null) ? rs.getString("c_customer_segment") : "");
                } else {
                    result[0] = (!(rs.getString("c_action_status") == null) ? rs.getString("c_action_status") : "");
                    result[1] = (!(rs.getString("c_ticket_status") == null) ? rs.getString("c_ticket_status") : "");
                    result[2] = (!(rs.getString("c_pending_status") == null) ? rs.getString("c_pending_status") : "");
                    result[3] = (!(rs.getString("c_customer_segment") == null) ? rs.getString("c_customer_segment") : "");
                }
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

        }

        return result;
    }

}
