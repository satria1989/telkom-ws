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
 * @author itasoft
 */
public class GaulDao {

    LogInfo logInfo = new LogInfo();
    StringBuilder query;

    public Integer getCountGaulByServiceId(String service_id, String date_created) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        query = new StringBuilder();
        int total = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        query
                .append(" SELECT EXTRACT(YEAR FROM dateCreated) year, EXTRACT(MONTH FROM dateCreated) month , COUNT(dateCreated) total  ")
                .append(" FROM app_fd_ticket WHERE  ")
                .append(" EXTRACT(MONTH FROM dateCreated) = EXTRACT(MONTH FROM TO_TIMESTAMP(?,'YYYY-MM-DD HH24:MI:SS.FF'))  ")
                .append(" AND EXTRACT(YEAR FROM dateCreated) = EXTRACT(YEAR FROM TO_TIMESTAMP(?,'YYYY-MM-DD HH24:MI:SS.FF'))  ")
                .append(" AND c_service_id = ?  ")
                .append(" GROUP BY EXTRACT(YEAR FROM dateCreated), EXTRACT(MONTH FROM dateCreated) ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, date_created);
            ps.setString(2, date_created);
            ps.setString(3, service_id);

            rs = ps.executeQuery();
            while (rs.next()) {
                total = (rs.getInt("total") - 1);
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
        }
        return total;
    }

    public void updateGaulHandler(String ticketID, int gaul) throws SQLException {
        boolean result = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        query = new StringBuilder();
        query.append(" UPDATE app_fd_ticket SET c_gaul=? WHERE c_id_ticket=? ");
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setInt(1, gaul);
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
