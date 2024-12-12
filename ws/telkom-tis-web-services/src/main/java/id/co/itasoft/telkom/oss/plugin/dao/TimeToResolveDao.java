/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.HistoryTicket;
//import id.co.itsoft.telkom.insera.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author mtaup
 */
public class TimeToResolveDao {

    public ArrayList<String> getTtr2(String ticketId, String custSegment) throws SQLException {
        ArrayList<String> timestampsList = new ArrayList<String>();
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT trim(c_statustracking) as c_statustracking ")
                .append("FROM app_fd_ticketstatus  ")
                .append("WHERE trim(c_ticketid) = ?  ");
        if ("DCS".equalsIgnoreCase(custSegment) || "PL-TSEL".equalsIgnoreCase(custSegment)) {
            query.append("AND trim(c_status) NOT IN ('MEDIACARE','SALAMSIM','PENDING','CLOSED') ");
        } else {
            query.append("AND trim(c_status) NOT IN ('MEDIACARE','SALAMSIM','PENDING','CLOSED','RESOLVED') ");
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
            LogUtil.error(this.getClass().getName(), ex, "ex:" + ex.getMessage());
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
        return timestampsList;
    }

    public String lastStatus(String ticketId) throws SQLException, Exception {
        String result = "";
        GetConnections gc = new GetConnections();
        String query = "SELECT trim(c_status) as c_status FROM app_fd_ticketstatus WHERE trim(c_ticketid) = ? ORDER BY datecreated DESC ";

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("c_status");
            }
        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "ex :" + e.getMessage());
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

        }
        return result;
    }

    public Timestamp lastDateCreated(String ticketId) throws SQLException, Exception {
        Timestamp result = null;
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query.append("SELECT datecreated ")
                .append("FROM app_fd_ticketstatus  ")
                .append("WHERE trim(c_ticketid) = trim(?) ")
                .append("AND c_status NOT IN ('MEDIACARE','SALAMSIM','PENDING')  ")
                .append("AND rownum = 1 ")
                .append("ORDER BY datecreated DESC  ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getTimestamp("datecreated");
            }
        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "ex :" + e.getMessage());
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

        }
        return result;
    }

    public String getDuration(Timestamp lastTimestamp) {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Timestamp fixLastTimestamp = (lastTimestamp == null ? currentTimestamp : lastTimestamp);

        long diff = currentTimestamp.getTime() - fixLastTimestamp.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long _minutes = minutes % 60;
        long _seconds = seconds % 60;
        String statusTracking = hours + ":" + _minutes + ":" + _seconds;
        return statusTracking;

    }

    public void updateTtr(String ttrCustomer, String ttrMitra, String ttrNasional, String ttrPending, String ttrWitel,
            String ttrAgent, String ttrRegion, String ttrEndToEnd, String idTicket) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE app_fd_ticket SET ")
                .append(" C_TTR_CUSTOMER = ? ")
                .append(" ,C_TTR_MITRA = ? ")
                .append(" ,C_TTR_NASIONAL = ? ")
                .append(" ,C_TTR_PENDING = ? ")
                .append(" ,C_TTR_WITEL = ? ")
                .append(" ,C_TTR_AGENT = ? ")
                .append(" ,C_TTR_REGION = ? ")
                .append(" ,C_TTR_END_TO_END = ? ")
                .append(" WHERE c_id_ticket = ? ");

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());

                ps.setString(1, ttrCustomer);
                ps.setString(2, ttrMitra);
                ps.setString(3, ttrNasional);
                ps.setString(4, ttrPending);
                ps.setString(5, ttrWitel);
                ps.setString(6, ttrAgent);
                ps.setString(7, ttrRegion);
                ps.setString(8, ttrEndToEnd);
                ps.setString(9, idTicket);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }

            query = null;
            gc = null;
        }
    }

    public List<HistoryTicket> getTicketHistory(String ticketId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;

        List<HistoryTicket> ht = new ArrayList<>();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();

        query.append(" SELECT A.datecreated, A.C_STATUS, A.C_ASSIGNEDOWNERGROUP, B.C_LEVEL, A.C_STATUSTRACKING ")
                .append(" FROM APP_FD_TICKETSTATUS A ")
                .append(" LEFT JOIN APP_FD_TIS_MST_OWNERGROUP B ON A.C_ASSIGNEDOWNERGROUP = B.C_OWNER_GROUP ")
                .append(" WHERE A.C_TICKETID = ? ")
                .append(" ORDER BY TO_CHAR(a.dateCreated, 'YYYY-MM-DD HH24:MI:SS SSFF3')DESC, A.c_status ASC  ");

        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            result = ps.executeQuery();
            HistoryTicket tc = null;
            while (result.next()) {
                tc = new HistoryTicket();
                tc.setDateCreted(result.getTimestamp("datecreated"));
                tc.setStatus(result.getString("C_STATUS"));
                tc.setOwnerGroup(result.getString("C_ASSIGNEDOWNERGROUP"));
                tc.setLevel(result.getString("C_LEVEL"));
                tc.setStatusTracking(result.getString("C_STATUSTRACKING") == null ? "00:00:00" : result.getString("C_STATUSTRACKING"));

                ht.add(tc);
            }
        } catch (SQLException ex) {
            LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
        } finally {
            query = null;
            try {
                if (result != null) {
                    result.close();
                }
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }

        }
        return ht;
    }

}
