/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;


/**
 * @author asani
 */
public class CronOwnergroupDao {

  LogInfo logInfo = new LogInfo();
  public void UpdateOwnergroup(String ticketId, String ownergroup) throws SQLException {
    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
    StringBuilder query = new StringBuilder();
    PreparedStatement ps = null;
    Connection con = ds.getConnection();
    query
        .append(" UPDATE app_fd_ticket SET ")
        .append(" c_owner_group = ? ")
        .append(" WHERE c_id_ticket = ? ");
    try {
      ps = con.prepareStatement(query.toString());
      ps.setString(1, ownergroup);
      ps.setString(2, ticketId);
      ps.executeUpdate();
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
