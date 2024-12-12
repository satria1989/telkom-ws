/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.TicketWorkLogs;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.joget.commons.util.LogUtil;

/**
 * @author suena
 */
public class InsertTicketWorkLogsDao {

  public String apiId = "";
  public String apiKey = "";
  public String apiSecret = "";
  //    final String token = generateToken(apiKey, apiSecret);

  public void getApiAttribut() throws SQLException, Exception {
    GetConnections gc = new GetConnections();
    String query =
        "select c_api_id, c_api_key, c_api_secret "
            + "from app_fd_tis_mst_api "
            + "where c_use_of_api = 'ticket_incident_api' ";
    Connection con = gc.getJogetConnection();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = con.prepareStatement(query);
      rs = ps.executeQuery();
      if (rs.next()) {
        apiId = rs.getString("c_api_id");
        apiKey = rs.getString("c_api_key");
        apiSecret = rs.getString("c_api_secret");
      }

    } catch (SQLException e) {
      LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
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
  }

  public boolean InsertTicketWorkLogs(TicketWorkLogs r, String idWorklog)
      throws SQLException, Exception {

    String detailVal = r.getDetail();
    if (detailVal.length() > 3800) {
      r.setDetail(detailVal.substring(0, 3800));
    }

    boolean result = false;
    GetConnections gc = new GetConnections();
    StringBuilder query = new StringBuilder();
    query
        .append(" INSERT INTO app_fd_ticket_work_logs ")
        .append(" ( ")
        .append(" id, ") // UUID
        .append(" dateCreated, ") // DATE NOW
        .append(" dateModified, ") // DATE NOW
        .append(" createdBy, ") // SYSTEM
        .append(" createdByName, ") // SYSTEM
        .append(" modifiedBy, ") // SYSTEM
        .append(" modifiedByName, ") // SYSTEM
        .append(" c_clientviewable, ") // SYSTEM
        .append(" c_ownergroup, ") // SYSTEM
        .append(" c_assignmentid, ") // SYSTEM
        .append(" c_parentid, ") // SYSTEM
        .append(" c_orgid, ") // SYSTEM
        .append(" c_log_type, ") // SYSTEM
        .append(" c_createdby, ") // SYSTEM
        .append(" c_recordkey, ") // SYSTEM
        .append(" c_summary, ") // SYSTEM
        .append(" c_siteid, ") // SYSTEM
        .append(" c_worklogid, ") // SYSTEM
        .append(" c_created_date, ") // SYSTEM
        .append(" c_anywhererrefid, ") // SYSTEM
        .append(" c_class, ") // SYSTEM
        .append(" c_detail, ") // SYSTEM
        .append(" c_attachment_file ") // SYSTEM
        .append(" ) ")
        .append(" VALUES  ")
        .append(" ( ")
        .append(" ?, ") // uuid
        .append(" ?, ") // dateCreated
        .append(" ?, ") // dateModified
        .append(" ?, ") // createdBy
        .append(" ?, ") // createdByName
        .append(" ?, ") // modifiedBy
        .append(" ?, ") // modifiedByName
        .append(" ?, ") // c_clientviewable
        .append(" ?, ") // c_ownergroup
        .append(" ?, ") // c_assignmentid
        .append(" ?, ") // di isi parent id dari tabel app_fd_ticket
        .append(" ?, ") // c_orgid
        .append(" ?, ") // c_log_type
        .append(" ?, ") // c_createdby
        .append(" ?, ") // c_recordkey
        .append(" ?, ") // c_summary
        .append(" ?, ") // c_siteid
        .append(" ?, ") // c_worklogid
        .append(" ?, ") // c_created_date
        .append(" ?, ") // c_anywhererrefid
        .append(" ?, ") // c_class
        .append(" ?, ") // c_detail
        .append(" ? ") // c_attachment_file
        .append(" ) ");

    Connection con = gc.getJogetConnection();
    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(query.toString());
      ps.setString(1, idWorklog);
      ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
      ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
      ps.setString(4, r.getCreatedBy()); // createdBy
      ps.setString(5, r.getCreatedByName()); // createdByName
      ps.setString(6, r.getModifiedBy()); // modifiedBy
      ps.setString(7, r.getModifiedByName()); // modifiedByName
      ps.setString(8, r.getClientviewable());
      ps.setString(9, r.getOwnergroup());
      ps.setString(10, r.getAssignmentid());
      ps.setString(11, r.getParentid());
      ps.setString(12, r.getOrgid());
      ps.setString(13, r.getLogType());
      ps.setString(14, r.getCreatedBy());
      ps.setString(15, r.getRecordkey());
      ps.setString(16, r.getSummary());
      ps.setString(17, r.getSiteid());
      ps.setString(18, r.getWorklogid());
      ps.setTimestamp(19, Timestamp.valueOf(r.getCreatedDate()));
      ps.setString(20, r.getAnywhererrefid());
      ps.setString(21, r.getClasS());
      ps.setString(22, r.getDetail());
      ps.setString(23, r.getAttachmentFile());

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
      query = null;
    }

    return result;
  }

  public String getIdByTicketId(String ticketId) throws SQLException, Exception {
    String result = "";
    GetConnections gc = new GetConnections();
    StringBuilder query = new StringBuilder();
    query
        .append(" SELECT   ")
        .append(" id ")
        .append(" FROM app_fd_ticket ")
        .append(" WHERE c_id_ticket=? ");
    Connection con = gc.getJogetConnection();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = con.prepareStatement(query.toString());
      ps.setString(1, ticketId);

      rs = ps.executeQuery();
      while (rs.next()) {
        result = rs.getString("id");
      }

    } catch (SQLException e) {
      LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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

    return result;
  }

  // UPDATE LAPUL DARI TABEL TICKET UNTUK (CLIENTNOTE WORK LOGS)
  public boolean UpdateLapul(String idTicket) throws SQLException, Exception {
    boolean result = false;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    GetConnections gc = new GetConnections();
    StringBuilder query = new StringBuilder();
    query
        .append(" UPDATE app_fd_ticket a ")
        .append(" SET a.c_lapul = ( ")
        .append(" 	SELECT  ")
        .append(" 	COUNT(b.id) ")
        .append(" 	FROM app_fd_ticket_work_logs b ")
        .append(" 	WHERE b.c_recordkey = ? AND b.c_log_type ='CLIENTNOTE' ")
        .append(" 	) ")
        .append(" WHERE a.c_id_ticket = ? ");

    Connection con = gc.getJogetConnection();
    PreparedStatement Ps = null;
    try {
      Ps = con.prepareStatement(query.toString());
      Ps.setString(1, idTicket); // IdTicket
      Ps.setString(2, idTicket); // IdTicket

      int i = Ps.executeUpdate();
      if (i > 0) {
        result = true;
      }

    } catch (SQLException e) {
      LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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

    return result;
  }

  public void InsertTicketWorkLogsByParentTicket(String user,
                                                 String summary,
                                                 String detail,
                                                 String TICKETID)
      throws SQLException, Exception {

    boolean result = false;
    GetConnections gc = new GetConnections();
    StringBuilder query = new StringBuilder();

    query.append(" INSERT INTO app_fd_ticket_work_logs  ")
            .append(" ( id, dateCreated, dateModified, createdBy, createdByName, modifiedBy,  ")
            .append(" modifiedByName, c_clientviewable, c_ownergroup, c_assignmentid, c_parentid,  ")
            .append(" c_orgid, c_log_type, c_createdby, c_recordkey, c_summary, c_siteid,  ")
            .append(" c_worklogid, c_created_date, c_anywhererrefid, c_class, c_detail,  ")
            .append(" c_attachment_file )  ")
            .append(" SELECT SYS_GUID(), ") //id
            .append(" SYSDATE, ") //dateCreated
            .append(" SYSDATE, ")//dateModified
            .append(" ?, ") //createdBy
            .append(" ?, ") //createdByName
            .append(" '', ") //modifiedBy
            .append(" '', ") //modifiedByName
            .append(" '', ") //c_clientviewable
            .append(" C_OWNER_GROUP, ") // c_ownergroup
            .append(" '', ") // c_assignmentid
            .append(" ID, ") // c_parentid
            .append(" 'TELKOM', ") // c_orgid
            .append(" 'AGENTNOTE', ") // c_log_type
            .append(" ?, ") // c_createdby
            .append(" C_ID_TICKET, ") // c_recordkey
            .append(" ?, ") // c_summary
            .append(" '', ") // c_siteid
            .append(" '', ") // c_worklogid
            .append(" SYSDATE, ") // c_created_date
            .append(" '', ") // c_anywhererrefid
            .append(" 'INCIDENT', ") // c_class
            .append(" ?, ") // c_detail
            .append(" '' ") // c_attachment_file
            .append(" FROM APP_FD_TICKET WHERE  ")
            .append(" C_ID_TICKET = ? ");

    Connection con = gc.getJogetConnection();
    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(query.toString());
      ps.setString(1, user); // createdBy
      ps.setString(2, user); // createdBy
      ps.setString(3, user); // createdBy
      ps.setString(4, summary);
      ps.setString(5, detail);
      ps.setString(6, TICKETID);
      ps.executeUpdate();

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
      query = null;
    }
  }
}
