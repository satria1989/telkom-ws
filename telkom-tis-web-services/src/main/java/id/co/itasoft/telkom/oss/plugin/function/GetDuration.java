/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author asani
 */
public class GetDuration {
    
    LogInfo info = new LogInfo();
    public String getDuration(Timestamp lastTimestamp) throws SQLException {
        Timestamp currentTimestamp = getCurrentDate();
        Timestamp lct = lastTimestamp;
        if (lastTimestamp == null || lastTimestamp.equals(null)) {
            lastTimestamp = currentTimestamp;
            lct = currentTimestamp;
        }
        
        int compare = lct.compareTo(currentTimestamp);
        if(compare > 0) {
            lastTimestamp = currentTimestamp;
            currentTimestamp = lct;
        }
       
        long diff = currentTimestamp.getTime() - lastTimestamp.getTime();
        long seconds = (diff / 1000) % 60;
        long minutes = (diff / (1000 * 60)) % 60;
        long hours = (diff / (1000 * 60 * 60)) % 24;
        long _minutes = minutes % 60;
        long _seconds = seconds % 60;
        String statusTracking = hours + ":" + minutes + ":" + seconds;
        return statusTracking;
    }
    
    private Timestamp getCurrentDate() throws SQLException {
        Timestamp tmp = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        StringBuilder query = new StringBuilder();
        boolean result = false;
        query.append(" SELECT SYSDATE FROM DUAL ");

        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());

            rs = ps.executeQuery();

            while (rs.next()) {
                tmp = rs.getTimestamp("SYSDATE");
            }
        } catch (Exception e) {
          info.Log(getClass().getName(), e.getMessage());
        } finally {
            query = null;
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
            if (rs != null) {
                rs.close();
            }

        }

        return tmp;

    }
}
