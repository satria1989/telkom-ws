/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.FinalcheckActModel;
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sql.DataSource;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.json.JSONObject;

/**
 * @author asani
 */
public class UpdateQueryCollectionDao {

  GetConnections gc = new GetConnections();
  LogInfo logInfo = new LogInfo();

  /**
   * SELECT AND UPDATE CLOSED BY
   *
   * @param parentId
   * @param param_code
   * @throws SQLException
   */
  public void UpdateClosedReopenByParentId(String parentId, String param_code) {
    StringBuilder query = new StringBuilder();
    query
        .append(" UPDATE APP_fD_TICKET  ")
        .append(" SET (c_closed_by, c_description_closed_by) =  ")
        .append("  (SELECT c_param_code, C_PARAM_DESCRIPTION FROM app_fd_param ")
        .append("   WHERE C_PARAM_NAME = 'CLOSEDBY' AND C_PARAM_CODE = ?) ")
        .append(" WHERE C_PARENT_ID = ? ");
    PreparedStatement ps = null;
    Connection con = null;
    try {
      con = gc.getJogetConnection();
      ps = con.prepareStatement(query.toString());
      ps.setString(1, param_code);
      ps.setString(2, parentId);
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

  /**
   * UPDATE LAPUL, GAUL, FINALCHECK
   *
   * @param ticketID
   * @param gaul
   * @param finalcheck
   * @throws SQLException
   */
  public void updateDataGaulFinalcheck(
      String processId, int gaul, String finalcheck, String statusTarget) throws SQLException {
    boolean result = false;
    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
    Connection con = ds.getConnection();
    StringBuilder query = new StringBuilder();
    query.append(" UPDATE app_fd_ticket SET c_gaul=?, c_finalcheck=?, c_status=?, c_ticket_status=?, c_run_process=null, c_last_state=?, c_status_date = sysdate WHERE C_PARENT_ID=? ");
    PreparedStatement ps = null;
    String lastState = (statusTarget == null) ? "" : statusTarget;

    if (statusTarget.equalsIgnoreCase("DRAFT")) {
      lastState = "ANALYSIS";
    }

    try {
      ps = con.prepareStatement(query.toString());
      ps.setInt(1, gaul);
      ps.setString(2, finalcheck);
      ps.setString(3, statusTarget);
      ps.setString(4, statusTarget);
      ps.setString(5, lastState);
      ps.setString(6, processId);
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

  /**
   * @param processId
   * @param statusTarget
   */
  public void UpdateStatusTarget(String processId, String statusTarget) {

    PreparedStatement ps = null;
    StringBuilder query = new StringBuilder();
    boolean result = false;
    query
        .append(" UPDATE app_fd_ticket SET ")
        .append(" c_status = ?, c_ticket_status=? ")
        .append(" WHERE c_parent_id = ? ");

    Connection con = null;
    try {
      con = gc.getJogetConnection();
      ps = con.prepareStatement(query.toString());
      ps.setString(1, statusTarget);
      ps.setString(2, statusTarget);
      ps.setString(3, processId);
      ps.executeUpdate();
      result = true;
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

  /**
   * UPDATE TICKET ID BY PARENT ID
   *
   * @param processId
   * @param ticket_id
   * @throws SQLException
   * @throws Exception
   */
  public void UpdateTicketIdByProcess(String processId, String ticket_id) {
    boolean bool = false;

    PreparedStatement ps = null;
    Connection con = null;
    StringBuilder query = new StringBuilder();
    try {
      con = gc.getJogetConnection();
      boolean result = false;
      query
          .append(" UPDATE app_fd_ticket SET ")
          .append(" c_id_ticket = ? ")
          .append(" WHERE c_parent_id = ? ");
      ps = con.prepareStatement(query.toString());
      ps.setString(1, ticket_id);
      ps.setString(2, processId);
      ps.executeUpdate();
      result = true;
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

  /**
   * @param processId
   * @param newOwnergoup
   * @param statusTarget
   * @param statusSCC
   * @throws SQLException
   */
  public void updateDataAfterRunProcess(
      String processId,
      String newOwnergoup,
      String statusTarget,
      String statusSCC,
      String runProccess,
      boolean penStatus,
      String lastState)
      throws SQLException {
    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
    Connection con = ds.getConnection();
    StringBuilder query = new StringBuilder();
    //            .append(" UPDATE app_fd_ticket SET c_owner_group=?, c_status=?, c_ticket_status=?,
    // c_run_process=?, c_owner=null, c_memo=null, c_scc_code_validation=? WHERE C_PARENT_ID=? ");
    if (penStatus) {
      query
          .append(" UPDATE app_fd_ticket SET ")
          .append(" c_owner_group=?, ")
          .append(" c_status=?, ")
          .append(" c_ticket_status=?, ")
          .append(" c_run_process=?, ")
          .append(" c_owner=null, ")
          .append(" c_memo=null, ")
          .append(" c_scc_code_validation=?, ")
          .append(" c_last_state=? WHERE C_PARENT_ID=? ");
    } else {
      query
          .append(" UPDATE app_fd_ticket SET ")
          .append(" c_owner_group=?, ")
          .append(" c_status=?, ")
          .append(" c_ticket_status=?, ")
          .append(" c_run_process=?, ")
          .append(" c_owner=null, ")
          .append(" c_memo=null, ")
          .append(" c_pending_status = null, ")
          .append(" c_pending_timeout = null, ")
          .append(" c_pending_reason = null, ")
          .append(" c_pen_timeout= null, ")
          .append(" c_scc_code_validation=?, ")
          .append(" c_last_state=? WHERE C_PARENT_ID=? ");
    }

    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(query.toString());
      ps.setString(1, newOwnergoup);
      ps.setString(2, statusTarget);
      ps.setString(3, statusTarget);
      ps.setString(4, runProccess);
      ps.setString(5, statusSCC);
      ps.setString(6, lastState);
      ps.setString(7, processId);
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

  /**
   * AUTO FINALCECK
   *
   * @param fam
   * @param processId
   * @return
   * @throws SQLException
   */
  public boolean UpdateStatusAndActStat(FinalcheckActModel fam, String processId)
      throws SQLException {

    PreparedStatement ps = null;
    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
    Connection con = ds.getConnection();
    StringBuilder query = new StringBuilder();
    boolean result = false;
    query
        .append(" UPDATE app_fd_ticket SET ")
        .append(" c_status = ?, c_ticket_status = ?, c_action_status = ?, ")
        .append(" c_owner='', c_memo='', c_run_process=null ")
        .append(" WHERE c_parent_id = ? ");
    try {
      ps = con.prepareStatement(query.toString());
      ps.setString(1, fam.getTicket_status());
      ps.setString(2, fam.getTicket_status());
      ps.setString(3, fam.getAction_status());
      ps.setString(4, processId);
      ps.executeUpdate();
      result = true;
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

    return result;
  }

  /**
   * @param processId
   * @param statusTarget
   * @throws SQLException
   */
  public void updateReopenLogic(String processId, String statusTarget, String Ownergroup)
      throws SQLException {
    boolean result = false;
    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
    Connection con = ds.getConnection();
    StringBuilder query = new StringBuilder();
    query
        .append(" UPDATE app_fd_ticket SET c_status=?, ")
        .append(" c_pending_status = null, ")
        .append(" c_pending_timeout = null, ")
        .append(" c_pending_reason = null, ")
        .append(" c_pen_timeout= null, ")
        .append(" c_ticket_status=?, c_run_process=null, ")
        .append(" c_owner=null, c_memo=null, c_owner_group=? WHERE C_PARENT_ID=? ");
    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(query.toString());
      ps.setString(1, statusTarget);
      ps.setString(2, statusTarget);
      ps.setString(3, Ownergroup);
      ps.setString(4, processId);
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

  /**
   * @param processId
   * @param statusTarget
   * @throws SQLException
   */
  public void updateDeadlineLogic(String processId, String actionStatus) throws SQLException {
    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
    Connection con = ds.getConnection();
    StringBuilder query = new StringBuilder();
    query.append(
        " UPDATE app_fd_ticket SET c_run_process='1', c_owner=null, c_memo=null, c_action_status=? WHERE C_PARENT_ID=? ");
    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(query.toString());
      ps.setString(1, actionStatus);
      ps.setString(2, processId);
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

  public void updateIbooster(ListIbooster ib, String id) throws SQLException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    Connection con = null;

    StringBuilder iboosterResult = new StringBuilder();
    if ("".equals(ib.getMessage())) {
      iboosterResult.append("Kategori Ukur : " + ib.getMeasurementCategory() + "\r");
      iboosterResult.append("oper_status : " + ib.getOperStatus() + "\r");
      iboosterResult.append("onu_rx_pwr : " + ib.getOnuRxPwr() + "\r");
      iboosterResult.append("onu_tx_pwr : " + ib.getOnuTxPwr() + "\r");
      iboosterResult.append("olt_rx_pwr : " + ib.getOltRxPwr() + "\r");
      iboosterResult.append("olt_tx_pwr : " + ib.getOltTxPwr() + "\r");
      iboosterResult.append("fiber_length : " + ib.getFiberLength() + "\r");
      iboosterResult.append("status_jaringan : " + ib.getStatusJaringan() + "\r");
      iboosterResult.append("identifier : " + ib.getIdentifier() + "\r");
    } else {
      iboosterResult.append(ib.getMessage());
    }
    DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String waktuUkur = (dateFormat2.format(new Date()).toString()).toLowerCase();
    StringBuilder query;
    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
    if ("".equals(ib.getMessage())) {
      query = new StringBuilder();
      query
          .append(" UPDATE app_fd_ticket SET  ")
          .append(" c_measurement_time = ?,  ")
          .append(" c_ibooster_result = ?,   ")
          .append(" c_mycx_result = ?,   ")
          .append(" c_mycx_category_result = ?,   ")
          .append(" c_measurement_category = ?, ")
          .append(" c_id_pengukuran = concat(substr(nvl(c_id_pengukuran, 'null |'), 1, instr(nvl(c_id_pengukuran, 'null |'), '|') -1), ?),   ")
          .append(" c_hostname_olt = concat(substr(nvl(c_hostname_olt, 'null |'), 1, instr(nvl(c_hostname_olt, 'null |'), '|') -1), ?),  ")
          .append(" c_ip_olt = concat(substr(nvl(c_ip_olt, 'null |'), 1, instr(nvl(c_ip_olt, 'null |'), '|') -1), ?),   ")
          .append(" c_frame = ?, ")
          .append(" c_olt_tx = concat(substr(nvl(c_olt_tx, 'null |'), 1, instr(nvl(c_olt_tx, 'null |'), '|') -1), ?),   ")
          .append(" c_olt_rx = concat(substr(nvl(c_olt_rx, 'null |'), 1, instr(nvl(c_olt_rx, 'null |'), '|') -1), ?),   ")
          .append(" c_onu_tx = concat(substr(nvl(c_onu_tx, 'null |'), 1, instr(nvl(c_onu_tx, 'null |'), '|') -1), ?),   ")
          .append(" c_onu_rx = concat(substr(nvl(c_onu_rx, 'null |'), 1, instr(nvl(c_onu_rx, 'null |'), '|') -1), ?),   ")
          .append(" c_status_ont = concat(substr(nvl(c_status_ont, 'null |'), 1, instr(nvl(c_status_ont, 'null |'), '|') -1), ?) ")
          .append(" WHERE c_parent_id = ? ");
    } else {
      query = new StringBuilder();
      query
          .append(" UPDATE app_fd_ticket SET ")
          .append(" c_measurement_time = ?, ")
          .append(" c_ibooster_result = ?,  ")
          .append(" c_measurement_category = ?,  ")
          .append(" c_olt_tx = concat(substr(nvl(c_olt_tx, 'null |'), 1, instr(nvl(c_olt_tx, 'null |'), '|') -1), ?),   ")
          .append(" c_olt_rx = concat(substr(nvl(c_olt_rx, 'null |'), 1, instr(nvl(c_olt_rx, 'null |'), '|') -1), ?),   ")
          .append(" c_onu_tx = concat(substr(nvl(c_onu_tx, 'null |'), 1, instr(nvl(c_onu_tx, 'null |'), '|') -1), ?),   ")
          .append(" c_onu_rx = concat(substr(nvl(c_onu_rx, 'null |'), 1, instr(nvl(c_onu_rx, 'null |'), '|') -1), ?)   ")
          .append(" WHERE c_parent_id = ?");
    }

    try {
      con = ds.getConnection();
      if (!con.isClosed()) {
        ps = con.prepareStatement(query.toString());
        if ("".equals(ib.getMessage())) {
          // LogUtil.info(this.getClass().getName(), "Masuk query parameter normal");
          ps.setTimestamp(1, Timestamp.valueOf(waktuUkur));
          ps.setString(2, iboosterResult.toString());
          ps.setString(3, "-");
          ps.setString(4, "-");
          ps.setString(5, ib.getMeasurementCategory());
          ps.setString(6, "| " + ib.getIdUkur());
          ps.setString(7, "| " + ib.getIdentifier());
          ps.setString(8, "| " + ib.getHostname());
          ps.setString(9, ib.getClid());
          ps.setString(10, "| " + ib.getOltTxPwr());
          ps.setString(11, "| " + ib.getOltRxPwr());
          ps.setString(12, "| " + ib.getOnuTxPwr());
          ps.setString(13, "| " + ib.getOnuRxPwr());
          ps.setString(14, "| " + ib.getStatusCpe());
          ps.setString(15, id);
        } else {
          // LogUtil.info(this.getClass().getName(), "Masuk query parameter upnormal");
          ps.setTimestamp(1, Timestamp.valueOf(waktuUkur));
          ps.setString(2, iboosterResult.toString());
          ps.setString(3, ib.getMeasurementCategory());
          ps.setString(4, "| " + ib.getOltTxPwr());
          ps.setString(5, "| " + ib.getOltRxPwr());
          ps.setString(6, "| " + ib.getOnuTxPwr());
          ps.setString(7, "| " + ib.getOnuRxPwr());
          ps.setString(8, id);
        }

        ps.executeUpdate();
        // LogUtil.info(this.getClass().getName(), "updateIbooster success");
      }
    } catch (SQLException ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      query = null;
      ib = null;
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

  public void UpdateWorkorderReview(TicketStatus ts, String statusWo, String username) {
    RESTAPI _RESTAPI = null;
    MasterParamDao masterParamDao = null;
    MasterParam masterParam = null;
    LogHistory logHistory = null;
    LogHistoryDao logHistoryDao = null;

    try {
      _RESTAPI = new RESTAPI();
      logHistory = new LogHistory();
      logHistoryDao = new LogHistoryDao();
      masterParamDao = new MasterParamDao();
      masterParam = new MasterParam();

      masterParam = masterParamDao.getUrl("updateWorkOrder");

      JSONObject json = new JSONObject();
      json.put("status", "REVIEW");
      json.put("externalId", ts.getTicketId());
      json.put("changeBy", username);

      RequestBody body = RequestBody.create(_RESTAPI.JSON, json.toString());
      Request request =
          new Request.Builder()
              .url(masterParam.getUrl())
              .addHeader("api_key", masterParam.getApi_key()) // add request headers
              .addHeader("api_id", masterParam.getApi_id())
              .addHeader("Origin", "https://oss-incident.telkom.co.id")
              .post(body)
              .build();

      logHistory.setAction("UPDATE WO(" + ts.getTicketId() + ")");
      logHistory.setMethod("POST");
      logHistory.setRequest(json.toString());
      logHistory.setTicketId(ts.getTicketId());
      logHistory.setUrl(masterParam.getUrl());

      JSONObject REQ_WO = _RESTAPI.CALLAPIHANDLER(request);
      logHistory.setResponse(REQ_WO.toString());
      logHistoryDao.insertToLogHistory(logHistory);

    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      _RESTAPI = null;
      masterParamDao = null;
      masterParam = null;
      logHistory = null;
      logHistoryDao = null;
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

    return statusTracking;
  }

  public void UpdateTimeonLastTicketSta(TicketStatus r) throws SQLException, Exception {
    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
    Connection con = ds.getConnection();
    StringBuilder query = new StringBuilder();
    query
        .append(" UPDATE APP_FD_TICKETSTATUS SET c_statustracking = ? ")
        .append(" WHERE id = (select id  from APP_FD_TICKETSTATUS a where c_ticketid= ? ")
        .append(" order by datecreated desc fetch first row only) ");
    PreparedStatement ps = null;
    try {
      String getStatusTracking = getDuration(r.getTicketId());
      ps = con.prepareStatement(query.toString());
      ps.setString(1, getStatusTracking);
      ps.setString(2, r.getTicketId());
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

  public void UpdateOwnergroup(String processId, String ownergoup) {
    PreparedStatement ps = null;
    Connection con = null;
    StringBuilder query = new StringBuilder();
    try {
      con = gc.getJogetConnection();
      boolean result = false;
      query
          .append(" UPDATE app_fd_ticket SET ")
          .append(" c_owner_group= ? ")
          .append(" WHERE c_parent_id = ? ");
      ps = con.prepareStatement(query.toString());
      ps.setString(1, ownergoup);
      ps.setString(2, processId);
      ps.executeUpdate();
      result = true;
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
