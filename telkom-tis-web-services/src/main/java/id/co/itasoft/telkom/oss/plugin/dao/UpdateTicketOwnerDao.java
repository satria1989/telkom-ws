/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;

/**
 *
 * @author Tarkiman
 */
public class UpdateTicketOwnerDao {

    public boolean UpdateOwner(Ticket o) throws SQLException, Exception {

        boolean result = false;
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" UPDATE app_fd_ticket  ")
                .append(" SET ")
                .append(" c_owner=?, ")
                .append(" dateModified=SYSDATE, ")
                .append(" modifiedBy=?, ")
                .append(" modifiedByName=? ")
                .append(" WHERE c_id_ticket=? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        String owner = (o.getOwner() == null) ? "" : o.getOwner().toUpperCase();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, owner);
            ps.setString(2, owner);
            ps.setString(3, owner);
            ps.setString(4, o.getTicketId());

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
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

        }

        return result;

    }

    public Timestamp getTicketStatusByTicketID(String ticketId) throws SQLException, Exception {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" a.id, ")
                .append(" a.dateCreated, ")
                .append(" a.c_ticketid, ")
                .append(" a.c_status ")
                .append(" FROM app_fd_ticketstatus a ")
                .append(" WHERE a.c_ticketid=? ")
                .append(" AND rownum = 1 ")
                .append(" ORDER BY a.dateCreated DESC ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);

            rs = ps.executeQuery();
            while (rs.next()) {
                timestamp = new Timestamp(rs.getDate("dateCreated").getTime());
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "error:" + e.getMessage());
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

        }

        return timestamp;
    }

    public boolean insertNewTicketStatus(TicketStatus r) throws SQLException, Exception {

        boolean result = false;
        String pinPoint = "";
        if (!"REQUEST_PENDING".equalsIgnoreCase(r.getActionStatus())) {
            pinPoint = "TRUE";
        }
        GetConnections gc = new GetConnections();
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
                .append(" c_tkstatusid, ")
                .append(" c_action_status, ")
                .append(" c_pin_point ")
                .append(" ) ")
                .append(" VALUES ( ")
                .append(" ?, ")
                .append(" SYSDATE, ")
                .append(" SYSDATE, ")
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
                .append(" ?, ")
                .append(" ?, ")
                .append(" ? ")
                .append(" ) ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setString(2, "");//createdBy
            ps.setString(3, "");//createdByName
            ps.setString(4, "");//modifiedBy
            ps.setString(5, "");//modifiedByName
            ps.setString(6, r.getOwner());
            ps.setString(7, r.getTicketStatusId());
            ps.setString(8, r.getChangeBy());
            ps.setString(9, r.getMemo());
            ps.setString(10, r.getChangeDate());
            ps.setString(11, r.getOwnerGroup());
            ps.setString(12, r.getAssignedOwnerGroup());
            ps.setString(13, r.getOrgId());
            ps.setString(14, r.getStatusTracking());
            ps.setString(15, r.getSiteId());
            ps.setString(16, r.getClasS());
            ps.setString(17, r.getTicketId());
            ps.setString(18, r.getStatus());
            ps.setString(19, r.getTkStatusId());
            ps.setString(20, r.getActionStatus());
            ps.setString(21, pinPoint);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {

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

        }

        return result;

    }

    public void insertTicketStatus(TicketStatus r) throws SQLException, Exception {
        boolean result = false;
        String pinPoint = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        GetConnections gc = new GetConnections();
        if (!"REQUEST_PENDING".equalsIgnoreCase(r.getActionStatus())) {
            pinPoint = "TRUE";
        }
        
        StringBuilder query = new StringBuilder();
        Connection con = gc.getJogetConnection();

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
                .append(" c_action_status, ")
                .append(" c_status, ")
                .append(" c_tkstatusid, ")
                .append(" c_pin_point ")
                .append(" ) ")
                .append(" VALUES ( ")
                .append(" ?, ")
                .append(" SYSDATE, ")
                .append(" SYSDATE, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ");
                if ("false".equalsIgnoreCase(r.getStatusTracking())) {
                  query.append(" ?, ");
                }else {
                  query.append(" getduration(?), ");
                }
            query
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ? ")
                .append(" ) ");

        try {
            String owner = (r.getOwner() == null) ? "" : r.getOwner().toUpperCase();
            String changeBy = (r.getChangeBy() == null) ? "" : r.getChangeBy().toUpperCase();
            
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setString(2, owner);//createdBy
            ps.setString(3, "");//createdByName
            ps.setString(4, "");//modifiedBy
            ps.setString(5, "");//modifiedByName
            ps.setString(6, owner);
            ps.setString(7, r.getTicketStatusId());
            ps.setString(8, changeBy); //r.getOwner()
            ps.setString(9, r.getMemo());
            ps.setString(10, r.getChangeDate());
            if ("NEW".equalsIgnoreCase(r.getStatus())) {
                ps.setString(11, "");
                ps.setString(12, "");
            } else {
                ps.setString(11, r.getOwnerGroup()); // last 
                ps.setString(12, r.getAssignedOwnerGroup()); //now
            }

            ps.setString(13, r.getOrgId());
            if ("false".equalsIgnoreCase(r.getStatusTracking())) {
              ps.setString(14, "");
            }else {
              ps.setString(14, r.getStatusTracking());
            }
            ps.setString(15, r.getSiteId());
            ps.setString(16, r.getClasS());
            ps.setString(17, r.getTicketId());
            ps.setString(18, (r.getActionStatus() == null) ? "" : r.getActionStatus());
            ps.setString(19, r.getStatus());
            ps.setString(20, r.getTkStatusId());
            ps.setString(21, pinPoint);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
        } finally {
            r = null;
            query = null;
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

        }
    }

    public void updateOwnerGroup(String result, String id) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE app_fd_ticket SET ")
                .append("c_owner_group = ? ")
                .append("WHERE c_parent_id = ? ");

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());
                ps.setString(1, result);
                ps.setString(2, id);
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
            gc = null;
            query = null;
        }
    }
    
    public void updateOwnerGroupByTicketID(String result, String ticketId) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE app_fd_ticket SET ")
                .append("c_owner_group = ? ")
                .append("WHERE c_id_ticket = ? ");

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());
                ps.setString(1, result);
                ps.setString(2, ticketId);
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
            gc = null;
            query = null;
        }
    }
    
    public String getDuration(String ticketid) throws Exception {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append(" SELECT GETDURATION(?) as statustracking FROM DUAL ");
        boolean bool = false;
        Connection con = ds.getConnection();
        String statusTracking = "";
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketid);
            rs = ps.executeQuery();
            while (rs.next()) {
                statusTracking = rs.getString("statustracking");
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "CHECK TECNOLOGY ERROR :" + ex.getMessage());
        } finally {
            query = null;
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
        }

        return statusTracking;
    }
    
    public void UpdateTimeonLastTicketSta(TicketStatus r) throws SQLException, Exception {
      DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        StringBuilder query = new StringBuilder();
        query
            .append(" UPDATE APP_FD_TICKETSTATUS SET c_statustracking = ? ")
            .append(" WHERE id = (select id  from APP_FD_TICKETSTATUS a where c_ticketid= ? order by datecreated desc fetch first row only) ");
        PreparedStatement ps = null;
        try {
            String getStatusTracking = getDuration(r.getTicketId());
            ps = con.prepareStatement(query.toString());
            ps.setString(1, getStatusTracking);
            ps.setString(2, r.getTicketId());
            ps.executeUpdate();
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "update lapul :" + ex.getMessage());
        } finally {
            query = null;
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
        }
    }

}
