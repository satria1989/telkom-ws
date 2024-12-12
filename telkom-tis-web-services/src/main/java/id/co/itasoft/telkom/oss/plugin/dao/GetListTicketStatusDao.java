package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.TicketHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.joget.commons.util.LogUtil;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author asani
 */
public class GetListTicketStatusDao {

    public List<TicketHistory> getTicketHistory(String TicketID) throws SQLException {

        List<TicketHistory> list = new ArrayList<TicketHistory>();
        GetConnections gc = new GetConnections();
        TicketHistory th;
        StringBuilder query = new StringBuilder();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        query
                .append(" SELECT ")
                .append(" a.c_ticketid AS id_ticket, ")
                .append(" TO_CHAR(a.dateCreated, 'YYYY-MM-DD HH24:MI:SS') AS datecreated, ")
                .append(" a.c_owner AS owner, ")
                .append(" a.c_changeby AS change_by, ")
                .append(" a.c_memo AS memo, ")
                .append(" a.c_changedate, ")
                .append(" a.c_ownergroup AS owner_group, ")
                .append(" a.c_assignedownergroup AS assignedownergroup, ")
                .append(" a.c_orgid AS org_id, ")
                .append(" a.c_siteid AS site_ud, ")
                .append(" a.c_status AS status, ")
                .append(" a.c_tkstatusid AS tkstatus_id, ")
                .append(" a.c_statustracking AS statustracking ")
                .append(" FROM app_fd_ticketstatus a ")
                .append(" WHERE a.c_ticketid = ? ")
                .append(" ORDER BY TO_CHAR(a.dateCreated, 'YYYY-MM-DD HH24:MI:SS SSFF3')DESC, c_status DESC ");

        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, TicketID);

            rs = ps.executeQuery();
            while (rs.next()) {
                th = new TicketHistory();
                th.setC_ticketid(rs.getString("id_ticket"));
                th.setC_owner(rs.getString("owner"));
                th.setC_changeby(rs.getString("change_by"));
                th.setC_memo(rs.getString("memo"));
                th.setC_changedate(rs.getString("c_changedate"));
                th.setC_ownergroup(rs.getString("owner_group"));
                th.setC_assignedownergroup(rs.getString("assignedownergroup"));
                th.setC_orgid(rs.getString("org_id"));
                th.setC_siteid(rs.getString("site_ud"));
                th.setC_status(rs.getString("status"));
                th.setC_tkstatusid(rs.getString("tkstatus_id"));
                th.setC_statustracking(rs.getString("statustracking"));

                th.setDatecreatedStr(rs.getString("datecreated"));

                list.add(th);
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "WERER :" + ex.getMessage());
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
            query = null;
            gc = null;
        }

        return list;
    }
}
