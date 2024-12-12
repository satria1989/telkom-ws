/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author suenawati
 */
public class LapulDao {

    LogInfo logInfo = new LogInfo();
    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
    StringBuilder query;

    public Integer getCountLapulByIdTicket(String idTicket) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        query = new StringBuilder();
        Connection con = ds.getConnection();
        int total = 0;
        query
                .append(" SELECT  ")
                .append(" COUNT(b.id) total ")
                .append(" FROM app_fd_ticket_work_logs b ")
                .append(" WHERE b.c_recordkey = ? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, idTicket);

            rs = ps.executeQuery();
            while (rs.next()) {
                total = rs.getInt("total");
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

        return total;
    }

    public void updateLapul(String ticketID, int lapul) throws SQLException {
        PreparedStatement ps = null;
        Connection con = ds.getConnection();
//        con.setAutoCommit(false);
        query = new StringBuilder();
        query.append(" UPDATE app_fd_ticket SET c_lapul=? WHERE c_id_ticket=? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setInt(1, lapul);
            ps.setString(2, ticketID);
            ps.executeUpdate();
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
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
