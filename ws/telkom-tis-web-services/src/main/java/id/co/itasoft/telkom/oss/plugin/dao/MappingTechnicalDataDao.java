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
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author mtaup
 */
public class MappingTechnicalDataDao {

    LogInfo log = new LogInfo();

    public Map<String, String> getDatek(String parentId) throws Exception {
        Map<String, String> datek = new HashMap<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String data = "";
        StringBuilder query = new StringBuilder();
        query.append("select c_port_name, c_device_name from app_fd_tis_technical_data ")
                .append(" where c_parent_id = ? ")
                .append(" and c_port_name in ('SP_TARGET','SP_PORT','PRIMER_FEEDER', 'RK_ODC', 'SEKUNDER_DISTRIBUSI', " )
                .append(" 'STP_TARGET','CPE_SN','CPE_TYPE','DOMAIN_NAME','UPLOAD_SPEED','DOWNLOAD_SPEED','SERVICE_NUMBER')");
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());

            ps.setString(1, parentId);
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    datek.put(rs.getString("c_port_name"), rs.getString("c_device_name"));
                }

            } catch (SQLException e) {
                log.Error(this.getClass().getName(), "getDatek", e);
            }
        } catch (SQLException e) {
            log.Error(this.getClass().getName(), "getDatek", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                log.Error(this.getClass().getName(), "getDatek", ex);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                log.Error(this.getClass().getName(), "getDatek", ex);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                log.Error(this.getClass().getName(), "getDatek", ex);
            }
            query = null;
        }
        return datek;

    }
}
