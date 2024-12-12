/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
/**
 *
 * @author asani
 */
public class FinalcheckHandlerAuto {

    LogInfo logInfo = new LogInfo();
    /**
     * 
     * @param idTicket
     * @return
     * @throws SQLException 
     * GET ID ON TABLE TICKETSTATUS
     */
    
    public String GetIDFinalcheckTS(String idTicket) throws SQLException {
        String ID = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        try {
            query
                    .append(" SELECT a.id idfn  FROM app_fd_ticketstatus a ")
                    .append(" JOIN app_fd_ticket b ON a.c_ticketid = b.c_id_ticket ")
                    .append(" WHERE a.c_status = b.c_status ")
                    .append(" AND a.c_ticketid=? AND a.c_pin_point='TRUE' ORDER BY a.dateCreated DESC fetch first 1 row only ");
            ps = con.prepareStatement(query.toString());
            ps.setString(1, idTicket);
            rs = ps.executeQuery();
            while (rs.next()) {
                ID = rs.getString("idfn");
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

        return ID;
    }

    /**
     * 
     * @param owner
     * @param id
     * @throws SQLException 
     * UPDATE TICKETSTATUS COLUMN CHANGEBY DAN OWNER BY ID 
     */
    public void UpdateTs(String owner, String id) throws SQLException {
        boolean res = false;

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();
//        con.setAutoCommit(false);
        query.append(" UPDATE app_fd_ticketstatus SET c_changeby=?, c_owner=? WHERE trim(id)=trim(?) ");
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, owner);
            ps.setString(2, "");
            ps.setString(3, id);
            ps.executeUpdate();
//            con.commit();   
//            LogUtil.info(getClass().getName(), "COMMIT TRUE");
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

        }
    }
}
