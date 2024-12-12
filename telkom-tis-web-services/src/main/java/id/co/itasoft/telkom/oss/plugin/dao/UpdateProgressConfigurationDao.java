/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;

/**
 *
 * @author mtaup
 */
public class UpdateProgressConfigurationDao {

    GetConnections gc = new GetConnections();
    LogInfo info = new LogInfo();

    public boolean insertProgress(String idTicket,
            String action,
            String status,
            String note,
            String data1,
            String data2,
            String data3,
            String data4,
            String data5,
            Ticket ticket) {
        boolean result = false;
        Connection con = null;
        PreparedStatement ps = null;
        UUID uuid = UUID.randomUUID();
        String StringUuid = uuid.toString();

        String query = "INSERT INTO app_fd_progress_config " +
                "(" +
                "id, " +
                "dateCreated, " +
                "dateModified, " +
                "c_id_ticket, " +
                "c_transaction_type, " +
                "c_status, " +
                "c_note, " +
                "c_data_1," +
                "c_data_2," +
                "c_data_3," +
                "c_data_4," +
                "c_data_5," +
                "c_parent_id" +
                ") " +
                "VALUES (?, sysdate, sysdate, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query);
                ps.setString(1, StringUuid);
                ps.setString(2, idTicket);
                ps.setString(3, action);
                ps.setString(4, status);
                ps.setString(5, note);
                ps.setString(6, data1);
                ps.setString(7, data2);
                ps.setString(8, data3);
                ps.setString(9, data4);
                ps.setString(10, data5);
                ps.setString(11, ticket.getId());
                int st = ps.executeUpdate();
                if (st > 0) {
                    result = true;
                } else {
                    result = false;
                }
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "error : " + e.getMessage());
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "error : " + ex.getMessage());
            }
        }
        return result;
    }

    public void updateFormAcs(String idTicket, JSONObject dataFromAcs) throws Exception {

        String data1 = dataFromAcs.get("ticket_id") == null ? "" : dataFromAcs.get("ticket_id").toString();
        String data2 = dataFromAcs.get("keterangan") == null ? "" : dataFromAcs.get("keterangan").toString();
        String data3 = dataFromAcs.get("node_id") == null ? "" : dataFromAcs.get("node_id").toString();
        String data4 = dataFromAcs.get("node_ip") == null ? "" : dataFromAcs.get("node_ip").toString();
        String data5 = dataFromAcs.get("slot") == null ? "" : dataFromAcs.get("slot").toString();
        String data6 = dataFromAcs.get("port") == null ? "" : dataFromAcs.get("port").toString();
        String data7 = dataFromAcs.get("onu_id") == null ? "" : dataFromAcs.get("onu_id").toString();
        String data8 = dataFromAcs.get("onu_sn") == null ? "" : dataFromAcs.get("onu_sn").toString();
        String data9 = dataFromAcs.get("hsi") == null ? "" : dataFromAcs.get("hsi").toString();
        String data10 = dataFromAcs.get("voice1") == null ? "" : dataFromAcs.get("voice1").toString();
        String data11 = dataFromAcs.get("voice2") == null ? "" : dataFromAcs.get("voice2").toString();
        String data12 = dataFromAcs.get("username_stb_1") == null ? "" : dataFromAcs.get("username_stb_1").toString();
        String data13 = dataFromAcs.get("username_stb_2") == null ? "" : dataFromAcs.get("username_stb_2").toString();
        String data14 = dataFromAcs.get("username_stb_3") == null ? "" : dataFromAcs.get("username_stb_3").toString();
        String data15 = dataFromAcs.get("onu_type") == null ? "" : dataFromAcs.get("onu_type").toString();

        Connection con = null;
        PreparedStatement ps = null;
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("UPDATE app_fd_ticket SET ")
                .append("c_co_id_ticket = ? , ")
                .append("c_co_keterangan = ? , ")
                .append("c_co_pon_port_down_gpon_name = ? , ")
                .append("c_co_ip_olt = ? , ")
                .append("c_co_pon_port_down_slot = ? , ")
                .append("c_co_pon_port_down_port = ? , ")
                .append("c_co_onu_id = ? , ")
                .append("c_co_sn_ont = ? , ")
                .append("c_co_service_no = ? , ") // HSI
                .append("c_co_contact_phone1 = ? , ") // Voice 1
                .append("c_co_contact_phone2 = ? , ") // Voice 2
                .append("c_co_username1 = ? , ") // USER STB 1
                .append("c_co_username2 = ? , ") // USER STB 2
                .append("c_co_username3 = ? , ") // USER STB 3
                .append("c_co_type_ont = ?  ")
                .append("where c_id_ticket = ?");
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(sbQuery.toString());

                ps.setString(1, data1);
                ps.setString(2, data2);
                ps.setString(3, data3);
                ps.setString(4, data4);
                ps.setString(5, data5);
                ps.setString(6, data6);
                ps.setString(7, data7);
                ps.setString(8, data8);
                ps.setString(9, data9);
                ps.setString(10, data10);
                ps.setString(11, data11);
                ps.setString(12, data12);
                ps.setString(13, data13);
                ps.setString(14, data14);
                ps.setString(15, data15);
                ps.setString(16, idTicket);

                int r = ps.executeUpdate();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            sbQuery = null;

        }
    }

    public String getSeqTransNumb() throws Exception {
        String seqTransNumb = "";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT GET_SEQUENCE_ONT FROM DUAL");
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(sbQuery.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    seqTransNumb = rs.getString("GET_SEQUENCE_ONT");
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            sbQuery = null;

        }
        return seqTransNumb;
    }

}
