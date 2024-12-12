/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author asani
 */
public class CronDeadlineResolvedDao {

  GetConnections gn = new GetConnections();
  LogInfo logInfo = new LogInfo();

  public List<TicketStatus> getDataResolved(String duration) {
    List<TicketStatus> list = new ArrayList<TicketStatus>();
    TicketStatus ticketStatus = new TicketStatus();
    PreparedStatement ps = null;
    ResultSet rs = null;
    String pending_status = "";
    String action_status = "";

    StringBuilder query = new StringBuilder();
    Connection con = null;
    try {
      con = gn.getJogetConnection();

      query
          .append(" SELECT a.ID id, b.processId, C_PARENT_ID, C_ID_TICKET, C_STATUS, ")
          .append(" c.id as activity_id, c.ACTIVITYDEFINITIONID as activity_name, ")
          .append(" c.PDEFNAME as process_def_id, c.STATE ")
          .append(" FROM APP_FD_TICKET a ")
          .append(" JOIN wf_process_link b ON a.c_parent_id = b.originProcessId  ")
          .append(" join SHKACTIVITIES c on c.PROCESSID = b.PROCESSID  ")
          .append(" WHERE c.state = '1000003' ")
          .append(" AND C_STATUS = 'RESOLVED' ")
          .append(" AND C_CUSTOMER_SEGMENT IN ('DBS', 'DES', 'DGS') ")
          .append(" AND SYSDATE > (DATEMODIFIED+"+duration+"/1440) ")
          .append(" AND (C_SERVICE_TYPE NOT IN ('VOICE', 'IPTV', 'INTERNET') OR  c_service_type is null) ")
          .append(" fetch first 100 rows only ");

      ps = con.prepareStatement(query.toString());
      rs = ps.executeQuery();

      while (rs.next()) {
        ticketStatus = new TicketStatus();
        ticketStatus.setId(rs.getString("id"));
        ticketStatus.setStatus(rs.getString("c_status"));
        ticketStatus.setStatusCurrent(rs.getString("c_status"));
        ticketStatus.setTicketId(rs.getString("C_ID_TICKET"));
        ticketStatus.setActivicity_id(rs.getString("activity_id"));
        ticketStatus.setActivity_name(rs.getString("activity_name"));
        ticketStatus.setProcess_def_id(rs.getString("process_def_id"));
        ticketStatus.setSTATE(rs.getString("STATE"));
        ticketStatus.setProcessId(rs.getString("processId"));
        ticketStatus.setParentId(rs.getString("C_PARENT_ID"));

        list.add(ticketStatus);
      }

    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    } finally {
      try {
        ps.close();
      } catch (Exception ex) {
        logInfo.Log(getClass().getName(), ex.getMessage());
      }
      try {
        con.close();
      } catch (Exception ex) {
        logInfo.Log(getClass().getName(), ex.getMessage());
      }
      try {
        rs.close();
      } catch (Exception ex) {
        logInfo.Log(getClass().getName(), ex.getMessage());
      }
      query = null;
    }

    return list;
  }
}
