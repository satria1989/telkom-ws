/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.StringManipulation;
import id.co.itasoft.telkom.oss.plugin.model.TechnicalDataModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import javax.sql.DataSource;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;

/**
 * @author suena
 */
public class TechnicalDataDao {

    StringManipulation stringManipulation = new StringManipulation();
    LogInfo logInfo = new LogInfo();

    public boolean insertBatchOutTechnicalData(List<TechnicalDataModel> list) throws SQLException {
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

            for (TechnicalDataModel r : list) {

                ps.setString(1, uuid.getUuid());// id
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));// dateCreated
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));// dateModified
                ps.setString(4, currentUser);// createdBy
                ps.setString(5, currentUser);// createdByName
                ps.setString(6, currentUser);// modifiedBy
                ps.setString(7, currentUser);// modifiedByName
                ps.setString(8, r.getPortName());// c_port_name
                ps.setString(9, "");// c_pipe_name
                ps.setString(10, r.getDeviceName());// c_device_name
                ps.setString(11, "");// c_pipe_order
                ps.setString(12, "");// c_service_name
                ps.setString(13, r.getParentId());// c_parent_id
                ps.setString(14, r.getTicketId());// c_id_ticket
                ps.addBatch();
            }

            int j[] = ps.executeBatch();

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Exception in execute Inesert Technical Data : " + e.getMessage());
            result = false;
        } finally {
            query = null;
            list = null;
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

    public void deleteTechnicalData(String recordId) {
        Connection con2 = null;
        PreparedStatement ps2 = null;
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con2 = ds.getConnection();
            if (!con2.isClosed()) {
                String query = "delete from app_fd_tis_technical_data where c_parent_id = ? ";

                ps2 = con2.prepareStatement(query);
                ps2.setString(1, recordId);
                ps2.executeUpdate();
            }

        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {

            try {
                if (con2 != null) {
                    con2.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps2 != null) {
                    ps2.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
        }
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

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
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

    public void updateTicketFromTechnicalData(String ticketId, JSONObject technicalData) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" UPDATE APP_FD_TICKET SET ")
                .append(" C_WORK_ZONE = ?, ")
                .append(" C_CUSTOMER_ID = ?, ")
                .append(" C_DESCRIPTION_CUSTOMERID = ?, ")
                .append(" C_STREET_ADDRESS = ?, ")
                .append(" C_SERVICE_TYPE = ?, ")
                .append(" C_CONTACT_PHONE = ?, ")
                .append(" C_FLAG_EVENT = ?, ")
                .append(" C_SERVICE_ID_DESC = ?, ")
                .append(" C_RK_INFORMATION = ?, ")
                .append(" C_WITEL = ?, ")
                .append(" C_REGION = ?, ")
                .append(" C_DESCRIPTION_WORK_ZONE = ?, ")
                .append(" C_TECHNOLOGY = ? ")
                .append(" WHERE C_ID_TICKET = ? ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        java.sql.ResultSet result = null;
        try {

            ps = con.prepareStatement(String.valueOf(query));
            //urutan keys harus sama dengan urutan kolom yg akan di update
            String[] keys = {
                    "STO", "CUSTOMER_ID", "CUSTOMER_ID_DESCRIPTION", "ADDRESS",
                    "SERVICE_TYPE", "SERVICE_CONTACT", "EVENT_FLAG",
                    "SLA_CATEGORY_DETAIL", "RK_ODC", "witel", "region", "sto_name", "TECHNOLOGY"
            };
            int index = 1;

            for (int i = 0; i < keys.length; i++) {
                String value = technicalData.has(keys[i])
                        ? stringManipulation.getNonNullTrimmed(technicalData.getString(keys[i])) : ""; // Default to empty string if key is not present
                ps.setString(i + 1, value);
                index++;
            }
            logInfo.Log(this.getClass().getName(), "index parameter untuk ticket id == " + index);
            ps.setString(index, ticketId);
            result = ps.executeQuery();

            if (result.next()) {

            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
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
        }

    }

}
