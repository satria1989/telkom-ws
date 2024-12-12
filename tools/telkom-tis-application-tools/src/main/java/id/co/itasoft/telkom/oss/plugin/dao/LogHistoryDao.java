/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.joget.commons.util.UuidGenerator;

/**
 *
 * @author itasoft
 */
public class LogHistoryDao {

    GetConnections gc = new GetConnections();
    LogInfo logInfo = new LogInfo();

    public boolean insertToLogHistory(LogHistory lh) throws Exception {
        PreparedStatement ps = null;
        boolean result = false;
        StringBuilder query = new StringBuilder();
        Connection con = gc.getJogetConnection();;
//        con.setAutoCommit(false);
        query
                .append(" INSERT INTO app_fd_log_history_api@TICKETING_DB1 ")
                .append(" ( ")
                .append(" id, ")
                .append(" dateCreated, ")
                .append(" dateModified, ")
                .append(" createdBy, ")
                .append(" createdByName, ")
                .append(" modifiedBy, ")
                .append(" modifiedByName, ")
                .append(" c_request, ")
                .append(" c_method, ")
                .append(" c_response, ")
                .append(" c_action, ")
                .append(" c_url, ")
                .append(" c_id_ticket, ")
                .append(" c_status ")
                .append(" ) VALUES (SYS_GUID(),SYSDATE,SYSDATE,?,?,?,?,?,?,?,?,?,?,?) ");

        try {
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, "");//createdBy
            ps.setString(2, "");//createdByName
            ps.setString(3, "");//modifiedBy
            ps.setString(4, "");//modifiedByName
            ps.setString(5, lh.getRequest());
            ps.setString(6, lh.getMethod());
            ps.setString(7, lh.getResponse());
            ps.setString(8, lh.getAction());
            ps.setString(9, lh.getUrl());
            ps.setString(10, lh.getTicketId());
            ps.setString(11, lh.getStatus());
            ps.executeUpdate();
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            lh = null;
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

    public void insertToLogHistoryWA(LogHistory lh) throws Exception {
        PreparedStatement ps = null;
        boolean result = false;
        StringBuilder query = new StringBuilder();
        Connection con = gc.getJogetConnection();
        query
                .append(" INSERT INTO app_fd_log_history_wa_api@TICKETING_DB1 ")
                .append(" ( ")
                .append(" id, ") //1
                .append(" dateCreated, ")
                .append(" dateModified, ")
                .append(" createdBy, ") //2
                .append(" createdByName, ") //3
                .append(" modifiedBy, ") //4
                .append(" modifiedByName, ") //5
                .append(" c_request, ")//6
                .append(" c_method, ")//7
                .append(" c_response, ")//8
                .append(" c_action, ")//
                .append(" c_url, ")//
                .append(" c_id_ticket, ")//
                .append(" c_status, ")//
                .append(" c_sent_via, ")//
                .append(" c_created_by, ")//
                .append(" c_created_date ")
                .append(" ) VALUES (SYS_GUID(),SYSDATE,SYSDATE,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE) ");
        try {
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
//            ps.setString(1, uuid.getUuid());
            ps.setString(1, lh.getCreatedBy());
            ps.setString(2, lh.getCreatedBy());
            ps.setString(3, lh.getCreatedBy());
            ps.setString(4, lh.getCreatedBy());
            ps.setString(5, lh.getRequest());
            ps.setString(6, lh.getMethod());
            ps.setString(7, lh.getResponse());
            ps.setString(8, lh.getAction());
            ps.setString(9, lh.getUrl());
            ps.setString(10, lh.getTicketId());
            ps.setString(11, lh.getStatus());
            ps.setString(12, lh.getSendVia());
            ps.setString(13, lh.getCreatedBy());
            ps.executeUpdate();
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            lh = null;
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
