/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import id.co.itasoft.telkom.oss.plugin.model.WorkOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.simple.parser.JSONParser;



/**
 *
 * @author itasoft
 */
public class WorkOrderDao {

    JSONParser parse = new JSONParser();

    WorkflowUserManager wum = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
    String currentUser = wum.getCurrentUsername();
    GetConnections gc = new GetConnections();
    LogInfo info = new LogInfo();
    boolean result = false;

    public boolean InsertWorkOrder(WorkOrder wo, TicketStatus ts) throws SQLException, Exception {

        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO app_fd_tis_work_order  ")
                .append(" ( id, dateCreated, dateModified,  ")
                .append(" createdBy, createdByName, modifiedBy,  ")
                .append(" c_wo_number, c_status_wo_number,  ")
                .append(" c_parent_id,  ")
                .append(" c_id_ticket) ")
                .append(" VALUES ")
                .append(" (?,?,?, ")
                .append(" ?,?,?, ")
                .append(" ?,?, ")
                .append(" ?, ")
                .append(" ?) ");

        PreparedStatement ps = null;
        Connection con = gc.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, currentUser);
            ps.setString(5, currentUser);
            ps.setString(6, currentUser);
            ps.setString(7, wo.getWoNumber());
            ps.setString(8, wo.getStatusWoNumber().toUpperCase());
            ps.setString(9, ts.getId());
            ps.setString(10, ts.getTicketId());

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

    public Boolean UpdateFinalcheck(String ticketId) throws SQLException, Exception {
        StringBuilder query = new StringBuilder();

        PreparedStatement ps = null;
        query.append(" UPDATE app_fd_ticket set c_finalcheck = '1' WHERE c_id_ticket = ? ");
        Connection con = gc.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            int i = ps.executeUpdate();
            if (i > 0) {
                result = !result;
            }
        } catch (Exception ex) {
            info.Log(getClass().getSimpleName(), ex.getMessage());
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

    public Boolean UpdateLastState(String ticketId) throws SQLException, Exception {
        StringBuilder query = new StringBuilder();

        PreparedStatement ps = null;
        query.append(" UPDATE app_fd_ticket set c_last_state = 'BACKEND' WHERE c_id_ticket = ? ");
        Connection con = gc.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            int i = ps.executeUpdate();
            if (i > 0) {
                result = !result;
            }
        } catch (Exception ex) {
            info.Log(getClass().getSimpleName(), ex.getMessage());
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

    public void UpdateActualSolution(
            String ActualSolution, 
            String ActualSolutionDesc, 
            String incidentDomain, 
            String mitraGamas,
            String mitraGamasDesc,
            String ticketId
    ) throws Exception {
         StringBuilder query = new StringBuilder();

        PreparedStatement ps = null;
        query
            .append(" UPDATE app_fd_ticket set c_actual_solution = ?, c_description_actualsolution=?, ")
            .append(" c_incident_domain=?, c_mitra_gamas=?, c_mitra_gamas_desc=? ")
            .append(" WHERE c_id_ticket = ? ");
        Connection con = gc.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ActualSolution);
            ps.setString(2, ActualSolutionDesc);
            ps.setString(3, incidentDomain);
            ps.setString(4, mitraGamas);
            ps.setString(5, mitraGamasDesc);
            ps.setString(6, ticketId);
            int i = ps.executeUpdate();
        } catch (Exception ex) {
            info.Log(getClass().getSimpleName(), ex.getMessage());
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
    }
}
