/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author Mujib
 */
public class UpdateStatusPendingDao {
    
    LogInfo logInfo = new LogInfo();
  
    public void clearField(String parent_id, String status) throws SQLException {

        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();
        query
                .append(" UPDATE app_fd_ticket ")
                .append(" SET c_action_status = '',  ")
                .append("     c_pending_status = '', ")
                .append("     c_pending_timeout = '', ")
                .append("     c_pending_reason = '', ")
                .append("     c_pen_timeout= '', ")
                .append("     c_status= ?, ")
                .append("     c_ticket_status= ?, ")
                .append("     c_owner=? ")
                .append(" WHERE c_parent_id = ? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setString(3, "SYSTEM");
            ps.setString(4, parent_id);
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
