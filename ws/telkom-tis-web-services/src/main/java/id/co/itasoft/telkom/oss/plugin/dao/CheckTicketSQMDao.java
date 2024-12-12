/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author ADMIN
 */
public class CheckTicketSQMDao {
    LogInfo log = new LogInfo();
    
    public boolean getDataTicketSQM(String servcieType, String actualSolution, String serviceNumber) throws Exception{
        boolean result = false;
        GetConnections gc = new GetConnections();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        
        query
            .append(" SELECT C_SERVICE_ID, C_ACTUAL_SOLUTION ")
            .append(" FROM APP_FD_TICKET_REPO ")
            .append(" WHERE C_SERVICE_TYPE = ? ")
            .append(" AND C_ACTUAL_SOLUTION = ? ") 
            .append(" AND C_SERVICE_NO = ? ") 
            .append(" AND C_SOURCE_TICKET = 'PROACTIVE' ")
            .append(" AND C_CHANNEL = '50' ");
        
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try{
            ps = con.prepareStatement(query.toString());
            ps.setString(1, servcieType);
            ps.setString(2, actualSolution);
            ps.setString(3, serviceNumber);
            rs = ps.executeQuery();
            if(rs.next()){
                result = true;
            }
        }catch(SQLException e){
            LogUtil.error(this.getClass().getName(), e, "Error While Select Data : " + e);
        }finally{
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
}
