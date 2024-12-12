/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.WorkOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author itasoft
 */
public class UpdateWoHandlerDao {
  
    LogInfo info = new LogInfo();

    public boolean UpdateWoDb(WorkOrder wo) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        boolean result = false;
        query.append(" UPDATE app_fd_tis_work_order SET c_status_wo_number=?, c_schedule_date =?, c_assignee=? WHERE c_id_ticket=? AND c_wo_number=? ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {

            ps = con.prepareStatement(query.toString());
            ps.setString(1, wo.getStatusWoNumber());
            ps.setString(2, wo.getScheduleDate());
            ps.setString(3, wo.getAssigne());
            ps.setString(4, wo.getIdTicket());
            ps.setString(5, wo.getWoNumber());

            int i = ps.executeUpdate();
            if (i > 0) {
                result = !result;
            }

        } catch (SQLException e) {
          info.Log(getClass().getSimpleName(), e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getSimpleName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getSimpleName(), e.getMessage());
            }

        }

        return result;
    }

}
