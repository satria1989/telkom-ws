/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.HistoryTicket;
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
public class ExportDataDao {

    LogInfo logInfo = new LogInfo();

    public List<HistoryTicket> getHistoryTicket(String ticketId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;

        List<HistoryTicket> ht = new ArrayList<>();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();

        query.append(" SELECT ")
                .append(" a.c_ticketid, ")
                .append(" TO_CHAR(a.dateCreated, 'YYYY-MM-DD HH24:MI:SS') AS datecreated, ")
                .append(" a.c_owner, ")
                .append(" a.c_changeby, ")
                .append(" a.c_memo, ")
                .append(" a.c_changedate, ")
                .append(" a.c_assignedownergroup,")
                .append(" a.c_status, ")
                .append(" a.c_statustracking ")
                .append(" FROM app_fd_ticketstatus a ")
                .append(" WHERE a.c_ticketid = ? ")
                .append(" ORDER BY TO_CHAR(a.dateCreated, 'YYYY-MM-DD HH24:MI:SS SSFF3')DESC, c_status DESC ");

        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            result = ps.executeQuery();
            HistoryTicket tc = null;
            while (result.next()) {
                tc = new HistoryTicket();
                tc.setTicketId(result.getString("c_ticketid") == null ? "" : result.getString("c_ticketid"));
                tc.setSdateCreated(result.getString("datecreated") == null ? "" : result.getString("datecreated"));
                tc.setOwner(result.getString("c_owner") == null ? "" : result.getString("c_owner"));
                tc.setChangeBy(result.getString("c_changeby") == null ? "" : result.getString("c_changeby"));
                tc.setMemo(result.getString("c_memo") == null ? "" : result.getString("c_memo"));
                tc.setChangeDate(result.getString("datecreated") == null ? "" : result.getString("datecreated"));
                tc.setOwnerGroup(result.getString("c_assignedownergroup") == null ? "" : result.getString("c_assignedownergroup"));
                tc.setStatus(result.getString("c_status") == null ? "" : result.getString("c_status"));
                tc.setStatusTracking(result.getString("c_statustracking") == null ? "" : result.getString("c_statustracking"));

                ht.add(tc);
            }
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            try {
                if (result != null) {
                    result.close();
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
        return ht;
    }
}
