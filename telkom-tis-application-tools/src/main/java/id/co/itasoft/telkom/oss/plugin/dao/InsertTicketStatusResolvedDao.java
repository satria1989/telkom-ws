/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.UuidGenerator;

/**
 *
 * @author tarkiman
 */
public class InsertTicketStatusResolvedDao {
    LogInfo logInfo = new LogInfo();
  
    public TicketStatus getTicketId(String processId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        TicketStatus r = new TicketStatus();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" b.id, ")
                .append(" b.c_id_ticket, ")
                .append(" b.c_status, ")
                .append(" b.c_memo, ")
                .append(" b.c_owner, ")
                .append(" b.c_owner_group ")
                .append(" FROM app_fd_ticket b ")
                .append(" WHERE b.c_parent_id=? ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);

            rs = ps.executeQuery();
            while (rs.next()) {
                r.setTicketId(rs.getString("c_id_ticket"));
                r.setStatus(rs.getString("c_status"));
                r.setMemo(rs.getString("c_memo"));
                r.setOwner(rs.getString("c_owner"));
                r.setOwnerGroup(rs.getString("c_owner_group"));
            }

        } catch (SQLException ex) {
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

        return r;
    }

    public TicketStatus getTicketStatusByTicketID(String ticketId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        TicketStatus ts = new TicketStatus();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" a.id, ")
                .append(" a.dateCreated, ")
                .append(" a.c_ticketid, ")
                .append(" a.c_ownergroup, ")
                .append(" a.c_status ")
                .append(" FROM app_fd_ticketstatus a ")
                .append(" WHERE trim(a.c_ticketid)=trim(?) ")
                .append(" ORDER BY a.dateCreated DESC ")
                .append(" LIMIT 1 ");
        Connection con = ds.getConnection();
        try {

            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);

            rs = ps.executeQuery();
            while (rs.next()) {
                timestamp = new Timestamp(rs.getDate("dateCreated").getTime());
                ts.setDateCreated(timestamp);
                ts.setOwnerGroup(rs.getString("c_ownergroup"));
            }

        } catch (SQLException ex) {
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

        return ts;
    }

    public boolean insertNewTicketStatus(TicketStatus r) throws SQLException {
        PreparedStatement ps = null;

        boolean result = false;

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query
                .append(" INSERT INTO app_fd_ticketstatus ")
                .append(" ( ")
                .append(" id, ")
                .append(" dateCreated, ")
                .append(" dateModified, ")
                .append(" createdBy, ")
                .append(" createdByName, ")
                .append(" modifiedBy, ")
                .append(" modifiedByName, ")
                .append(" c_owner, ")
                .append(" c_ticketstatusid, ")
                .append(" c_changeby, ")
                .append(" c_memo, ")
                .append(" c_changedate, ")
                .append(" c_ownergroup, ")
                .append(" c_assignedownergroup, ")
                .append(" c_orgid, ")
                .append(" c_statustracking, ")
                .append(" c_siteid, ")
                .append(" c_class, ")
                .append(" c_ticketid, ")
                .append(" c_status, ")
                .append(" c_tkstatusid ")
                .append(" ) ")
                .append(" VALUES ( ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")//createdByName - 5
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")//c_changeby - 10
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")//c_orgid - 15
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")//c_status - 20
                .append(" ? ")
                .append(" ) ");
        Connection con = ds.getConnection();

        try {
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, "");//createdBy
            ps.setString(5, "");//createdByName
            ps.setString(6, "");//modifiedBy
            ps.setString(7, "");//modifiedByName
            ps.setString(8, r.getOwner());
            ps.setString(9, r.getTicketStatusId());
            ps.setString(10, r.getChangeBy());
            ps.setString(11, r.getMemo());
            ps.setString(12, r.getChangeDate());
            ps.setString(13, r.getOwnerGroup());
            ps.setString(14, r.getAssignedOwnerGroup());
            ps.setString(15, r.getOrgId());
            ps.setString(16, r.getStatusTracking());
            ps.setString(17, r.getSiteId());
            ps.setString(18, r.getClasS());
            ps.setString(19, r.getTicketId());
            ps.setString(20, r.getStatus());
            ps.setString(21, r.getTkStatusId());

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }

        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            r = null;
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

        return result;

    }

    public boolean UpdateOwnerTicket(String processId) throws SQLException {
        PreparedStatement ps = null;
        boolean result = false;

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_owner='' ")
                .append(" WHERE c_parent_id=? ");

        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
                // LogUtil.info(this.getClass().getName(), "OWNER TICKET - UPDATED");
            }

        } catch (SQLException ex) {
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

        return result;

    }

}
