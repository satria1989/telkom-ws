/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.JSONObject;

/**
 *
 * @author itasoft
 */
public class LogHistoryDao {

    LogInfo logInfo = new LogInfo();
    GetConnections gc = new GetConnections();
    MasterParam masterParam;
    MasterParamDao masterParamDao;
    RESTAPI _RESTAPI;
    
    public boolean insertToLog(LogHistory lh) throws Exception {
        boolean result = false;
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        Connection con = gc.getJogetConnection();

        query
                .append(" INSERT INTO app_fd_log_history_api ")
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


    public boolean insertToLogHistory(LogHistory lh) throws Exception {
        boolean result = false;
//        PreparedStatement ps = null;
//        StringBuilder query = new StringBuilder();
//        Connection con = gc.getJogetConnection();
////        con.setAutoCommit(false);

//        query
//                //                .append(" INSERT INTO app_fd_log_history_api@TICKETING_DB1 ")
//                //                .append(" INSERT INTO app_fd_log_history_api ")
//                .append(" ( ")
//                .append(" id, ")
//                .append(" dateCreated, ")
//                .append(" dateModified, ")
//                .append(" createdBy, ")
//                .append(" createdByName, ")
//                .append(" modifiedBy, ")
//                .append(" modifiedByName, ")
//                .append(" c_request, ")
//                .append(" c_method, ")
//                .append(" c_response, ")
//                .append(" c_action, ")
//                .append(" c_url, ")
//                .append(" c_id_ticket, ")
//                .append(" c_status ")
//                .append(" ) VALUES (SYS_GUID(),SYSDATE,SYSDATE,?,?,?,?,?,?,?,?,?,?,?) ");
//
//        try {
//            ps = con.prepareStatement(query.toString());
//            UuidGenerator uuid = UuidGenerator.getInstance();
//            ps.setString(1, "");//createdBy
//            ps.setString(2, "");//createdByName
//            ps.setString(3, "");//modifiedBy
//            ps.setString(4, "");//modifiedByName
//            ps.setString(5, lh.getRequest());
//            ps.setString(6, lh.getMethod());
//            ps.setString(7, lh.getResponse());
//            ps.setString(8, lh.getAction());
//            ps.setString(9, lh.getUrl());
//            ps.setString(10, lh.getTicketId());
//            ps.setString(11, lh.getStatus());
//            ps.executeUpdate();
//        } catch (SQLException ex) {
//            logInfo.Log(getClass().getName(), ex.getMessage());
//        } finally {
//            query = null;
//            lh = null;
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//            } catch (Exception ex) {
//                logInfo.Log(getClass().getName(), ex.getMessage());
//            }
//            try {
//                if (con != null) {
//                    con.close();
//                }
//            } catch (Exception ex) {
//                logInfo.Log(getClass().getName(), ex.getMessage());
//            }
//        }
        return result;
    }

    public void insertToLogHistoryWA(LogHistory lh) throws Exception {
        PreparedStatement ps = null;
        boolean result = false;
        StringBuilder query = new StringBuilder();
        Connection con = gc.getJogetConnection();
        query
//                .append(" INSERT INTO app_fd_log_history_wa_api@TICKETING_DB1 ")
                .append(" INSERT INTO app_fd_log_history_wa_api ")
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

    public void SENDHISTORY(
            String ticketId,
            String action,
            String url,
            String method,
            JSONObject request,
            JSONObject response,
            int responseCode
    ) {
        try {

            JSONObject reqLogHistory = new JSONObject();
            reqLogHistory.put("ticketId", ticketId);
            reqLogHistory.put("action", action);
            reqLogHistory.put("url", url);
            reqLogHistory.put("method", method);
            reqLogHistory.put("@TIMESTAMP", LocalDateTime.now() + "+07:00");
            reqLogHistory.put("request", request.toString());
            reqLogHistory.put("response", response.toString());
            reqLogHistory.put("responseCode", responseCode);

            logInfo.Log(getClass().getName(), reqLogHistory.toString());
            this.InsertLogApi(reqLogHistory, ticketId);

            reqLogHistory = null;
        } catch (Exception ex) {
            logInfo.Error(getClass().getName(), ex.getMessage(), ex);
        }
    }

    public void InsertLogApi(JSONObject json, String TicketId) {
        try {
            masterParam = new MasterParam();
            masterParamDao = new MasterParamDao();
            _RESTAPI = new RESTAPI();

            masterParam = masterParamDao.getUrl("insertLogApi");

            RequestBody body = RequestBody.create(_RESTAPI.JSON, json.toString());
            Request request =
                    new Request.Builder()
                            .url(masterParam.getUrl())
                            .header(
                                    "Authorization",
                                    getBasicAuthenticationHeader(
                                            masterParam.getjUsername(),
                                            masterParam.getjPassword()
                                    )
                            )
                            .post(body)
                            .build();

            _RESTAPI.CALLAPIHANDLER(request);
            
            logInfo.Log(this.getClass().getName(), "INSERT_LOG_HISTORY "+TicketId);
        } catch (Exception ex) {
            logInfo.Error(getClass().getName(), ex.getMessage(), ex);
        }
    }

    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

}
