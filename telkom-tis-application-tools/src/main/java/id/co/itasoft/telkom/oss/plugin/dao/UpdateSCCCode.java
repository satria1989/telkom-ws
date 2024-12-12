/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author itasoft
 */
public class UpdateSCCCode {

    LogInfo logInfo = new LogInfo();
  
    public void updateCodeValidation(TicketStatus ts) throws SQLException {

        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();
//        con.setAutoCommit(false);
        query.
                append(" UPDATE app_fd_ticket SET ").
                append(" c_code_validation = ? WHERE c_id_ticket = ? ");
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ts.getCode_validation());
            ps.setString(2, ts.getTicketId());
            ps.executeUpdate();
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            ts = null;
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
