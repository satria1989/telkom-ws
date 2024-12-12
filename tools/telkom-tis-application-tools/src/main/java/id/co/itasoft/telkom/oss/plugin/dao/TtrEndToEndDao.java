/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author mtaup
 */
public class TtrEndToEndDao {
    LogInfo logInfo = new LogInfo();  
  
    public ArrayList<String> getDataFromTicketStatus(String ticketId, String custSegment) throws SQLException {
        ArrayList<String> timestampsList = new ArrayList<String>();
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query
            .append("SELECT trim(c_statustracking) as c_statustracking ")
            .append("FROM app_fd_ticketstatus  ")
            .append("WHERE trim(c_ticketid) = ?  ");
        if ("DCS".equalsIgnoreCase(custSegment)) {
            query.append("AND trim(c_status) NOT IN ('MEDIACARE','SALAMSIM','PENDING','CLOSED') ");
        } else {
            query.append("AND trim(c_status) NOT IN ('MEDIACARE','SALAMSIM','CLOSED','RESOLVED') ");
        }

        try {

            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            rs = ps.executeQuery();
            while (rs.next()) {
                timestampsList.add(rs.getString("c_statustracking") == null ? "00:00:00" : rs.getString("c_statustracking"));
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
            query = null;
            gc = null;

        }
        return timestampsList;
    }
    
    
    public void updateTtr(String ttr, String idTicket) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        query.append("UPDATE app_fd_ticket SET ")
                .append("c_ttr_end_to_end = ? ")
                .append("WHERE c_id_ticket = ? ");

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());

                ps.setString(1, ttr);
                ps.setString(2, idTicket);
                ps.executeUpdate();
//                LogUtil.info(this.getClass().getName(), "## Update Pending Status");
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
            } catch (SQLException ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }

            query = null;
            gc = null;
        }
    }
}
