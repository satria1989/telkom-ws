/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.DataDurations;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author mtaup
 */
public class SummaryPerstatusDao {
    LogInfo logInfo = new LogInfo();
    
    public List<DataDurations> getDataDuration(String ticketId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;

        List<DataDurations> r = new ArrayList<>();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append(" select listagg(a.c_statustracking, '#') as duration, a.c_status ")
                .append(" FROM app_fd_ticketstatus a  ")
                .append(" WHERE a.c_ticketid = ? ")
                .append(" and c_status != 'NEW' ")
                .append(" group by a.c_status ")
                .append(" order by a.c_status desc ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            result = ps.executeQuery();
            DataDurations tc = null;
            while (result.next()) {
                tc = new DataDurations();
                tc.setStatus(result.getString("c_status"));
                tc.setDuration(result.getString("duration") == null ? "00:00:00" : result.getString("duration"));
                r.add(tc);
            }
        } catch (SQLException e) {
          logInfo.Log(getClass().getSimpleName(), e.getMessage());
        } finally {
            query = null;
            try {
                if (result != null) {
                    result.close();
                }
            } catch (Exception e) {
              logInfo.Log(getClass().getSimpleName(), e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
              logInfo.Log(getClass().getSimpleName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
              logInfo.Log(getClass().getSimpleName(), e.getMessage());
            }

        }
        return r;
    }
    
    public List<DataDurations> getDataDurations(String ticketId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;

        List<DataDurations> r = new ArrayList<>();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append(" select a.c_statustracking as durations, a.c_status , a.c_assignedownergroup ")
                .append(" FROM app_fd_ticketstatus a  ")
                .append(" WHERE a.c_ticketid = ? ")
                .append(" and c_status != 'NEW' ")
                .append(" order by a.c_status desc ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            result = ps.executeQuery();
            DataDurations tc = null;
            while (result.next()) {
                tc = new DataDurations();
                tc.setStatus(result.getString("c_status"));
                tc.setDuration(result.getString("durations") == null ? "00:00:00" : result.getString("durations"));
                tc.setOwnerGroup(result.getString("c_assignedownergroup"));
                r.add(tc);
            }
        } catch (SQLException e) {
          logInfo.Log(getClass().getSimpleName(), e.getMessage());
        } finally {
            query = null;
            try {
                if (result != null) {
                    result.close();
                }
            } catch (Exception e) {
              logInfo.Log(getClass().getSimpleName(), e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
              logInfo.Log(getClass().getSimpleName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
              logInfo.Log(getClass().getSimpleName(), e.getMessage());
            }

        }
        return r;
    }
}
