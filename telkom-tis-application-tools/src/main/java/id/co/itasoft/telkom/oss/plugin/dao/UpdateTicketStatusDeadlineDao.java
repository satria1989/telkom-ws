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
 * @author tarki, rizki
 */
public class UpdateTicketStatusDeadlineDao {
  
    LogInfo logInfo = new LogInfo();

    public void UpdateTicketStatus(String processId, String status) throws SQLException {

        boolean result = false;
        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_status=?, ")
                .append(" c_ticket_status=? ")
                .append(" WHERE c_parent_id=? ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setString(3, processId);
            ps.executeUpdate();

        } catch (SQLException e) {
          logInfo.Log(getClass().getName(), e.getMessage());
        } finally {
            query = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }

        }

    }

    public void UpdateActionStatus(String processId, String actionstatus) throws SQLException {
        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_action_status=? ")
                .append(" WHERE c_parent_id=? ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, actionstatus);
            ps.setString(2, processId);
            ps.executeUpdate();

        } catch (Exception e) {
            logInfo.Log(getClass().getName(), e.getMessage());
        } finally {
            query = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }
        }
    }

    public void UpdateClosedReopenBy(String closed_by, String desc, String parent_id) throws SQLException {

        PreparedStatement ps = null;
        boolean result = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_closed_by=?, ")
                .append(" c_description_closed_by=? ")
                .append(" WHERE c_parent_id=? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, closed_by);
            ps.setString(2, desc);
            ps.setString(3, parent_id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logInfo.Log(getClass().getName(), e.getMessage());
        } finally {
            query = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }
        }

    }

    public void UpdateOwnerBy(String processId, String owner) throws SQLException {

        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();
//        con.setAutoCommit(false);
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_owner=? ")
                .append(" WHERE c_parent_id=? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, owner);
            ps.setString(2, processId);
            ps.executeUpdate();

        } catch (SQLException e) {
            logInfo.Log(getClass().getName(), e.getMessage());
        } finally {
            query = null;

            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                logInfo.Log(getClass().getName(), e.getMessage());
            }

        }

    }
}
