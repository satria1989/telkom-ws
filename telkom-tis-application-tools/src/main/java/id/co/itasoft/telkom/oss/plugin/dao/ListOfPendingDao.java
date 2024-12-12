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
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author asani
 */
public class ListOfPendingDao {
  
  LogInfo logInfo = new LogInfo();

    public List<String> getProcessIdNotListPending() throws SQLException {
        List<String> list = new ArrayList<String>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();

        StringBuilder query = new StringBuilder();
        query.append(" select a.Id as activityId, p.Id")
                .append(" from SHKProcesses p ")
                .append("          inner join SHKActivities a on a.ProcessId = p.Id ")
                .append(" where p.State in (1000000, 1000002) ")
                .append("   and a.State in (1000001, 1000003) ")
                .append("   and (select count(*) from shkassignmentstable s  where s.ActivityId = a.id) < 1 ")
                .append("   and a.ActivityDefinitionId in ('new','analysis','backend','draft', 'finalcheck', 'mediacare','resolved','salamsim') ")
                .append("   and rownum <=100 ");

        try {
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("activityId"));
            }

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            ds = null;
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

        return list;
    }
}
