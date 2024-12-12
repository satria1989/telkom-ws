/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.OutTechnicalData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.workflow.model.service.WorkflowUserManager;

/**
 *
 * @author suena
 */
public class TechnicalDataHandlerDao {
  
    LogInfo logInfo = new LogInfo();

    public boolean insertBatchOutTechnicalData(List<OutTechnicalData> list) throws SQLException {
        PreparedStatement ps = null;
        //dapatkan current user
        WorkflowUserManager wum = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        String currentUser = wum.getCurrentUsername();

        boolean result = true;

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        StringBuilder query = new StringBuilder();
        query
                .append(" INSERT INTO ")
                .append(" app_fd_tis_technical_data ( ")
                .append(" id, ")
                .append(" dateCreated, ")
                .append(" dateModified, ")
                .append(" createdBy, ")
                .append(" createdByName, ")
                .append(" modifiedBy, ")
                .append(" modifiedByName, ")
                .append(" c_port_name, ")
                .append(" c_pipe_name, ")
                .append(" c_device_name, ")
                .append(" c_pipe_order, ")
                .append(" c_service_name, ")
                .append(" c_parent_id, ")
                .append(" c_id_ticket ")
                .append(" ) ")
                .append(" VALUES ")
                .append(" ( ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ? ")
                .append(" ) ");

        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
//            con.setAutoCommit(false);
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            UuidGenerator uuid = UuidGenerator.getInstance();

            for (OutTechnicalData r : list) {

                ps.setString(1, uuid.getUuid());// id
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));// dateCreated
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));// dateModified
                ps.setString(4, currentUser);// createdBy
                ps.setString(5, currentUser);// createdByName
                ps.setString(6, currentUser);// modifiedBy
                ps.setString(7, currentUser);// modifiedByName
                ps.setString(8, r.getPortName());// c_port_name
                ps.setString(9, r.getPipeName());// c_pipe_name
                ps.setString(10, r.getDeviceName());// c_device_name
                ps.setString(11, r.getPipeOrder());// c_pipe_order
                ps.setString(12, r.getServiceName());// c_service_name
                ps.setString(13, r.getParentId());// c_parent_id
                ps.setString(14, r.getTicketId());// c_id_ticket
                ps.addBatch();
            }

            int j[] = ps.executeBatch();
            // // LogUtil.info(getClass().getName(), "Data yang masuk: " + j);

//            con.commit();
//            LogUtil.info(getClass().getName(), "COMMIT TRUE");
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
            result = false;
        } finally {
            query = null;
            list = null;
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

    public void updateTechnology(String id, String technology) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        String fixTech = technology.equalsIgnoreCase("non fiber") ? "COPPER" : technology;
        String query = "SELECT c_technology from app_fd_ticket where id = ? ";
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        java.sql.ResultSet result = null;
        Connection con2 = gc.getJogetConnection();
        PreparedStatement ps2 = null;
        try {

            ps = con.prepareStatement(query);
            ps.setString(1, id);
            result = ps.executeQuery();
            if (result.next()) {
                String tech = result.getString("c_technology") == null ? "" : result.getString("c_technology");
                if ("".equals(tech)) {
                    String query2 = "update app_fd_ticket set c_technology = ? where id = ? ";
                    ps2 = con2.prepareStatement(query2);
                    ps2.setString(1, fixTech);
                    ps2.setString(2, id);
                    ps2.executeUpdate();

                }

            }

        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
            if (result != null) {
                result.close();
            }

            if (ps2 != null) {
                ps2.close();
            }
            if (con2 != null) {
                con2.close();
            }

        }

    }

}
