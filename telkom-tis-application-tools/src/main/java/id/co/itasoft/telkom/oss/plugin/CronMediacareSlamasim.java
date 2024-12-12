/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.service.WorkflowManager;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class CronMediacareSlamasim extends DefaultApplicationPlugin {

    String pluginName = "Telkom New OSS - Cron Mediacare Salamsim";
    LogInfo logInfo = new LogInfo();

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "7.0.0";
    }

    @Override
    public String getDescription() {
        return pluginName;
    }

    @Override
    public String getLabel() {
        return pluginName;
    }

    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/CronMediacareSalamsim.json", null, true, null);

    }

    @Override
    public Object execute(Map map) {
        try {
            ApplicationContext ac = AppUtil.getApplicationContext();
            WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
            double dcs = Integer.valueOf(getPropertyString("dcs"));
            double nonDcs = Integer.valueOf(getPropertyString("non_dcs"));
            String kindOfTime = getPropertyString("kind_of_time");

            List<Ticket> listTicket = getTicket(kindOfTime);
            for (Ticket ticket : listTicket) {
                double diffHour = Double.valueOf(ticket.getDiffHour() == null ? "0" : ticket.getDiffHour());
                String custSegment = ticket.getCust_segment() == null ? "" : ticket.getCust_segment();
                if ("1000003".equals(ticket.getState())) {

                    if (("DCS".equalsIgnoreCase(custSegment) || "PL-TSEL".equalsIgnoreCase(custSegment)) && diffHour >= dcs) {
                        updateStatusTikcet(ticket.getParentTicketInc());
                        workflowManager.assignmentForceComplete(ticket.getProcessDefId().replace("#", ":"), ticket.getProcessId(), ticket.getActivityId(), "000000");
                    } else if ((!"DCS".equalsIgnoreCase(custSegment) && !"PL-TSEL".equalsIgnoreCase(custSegment)) && diffHour >= nonDcs) {
                        updateStatusTikcet(ticket.getParentTicketInc());
                        workflowManager.assignmentForceComplete(ticket.getProcessDefId().replace("#", ":"), ticket.getProcessId(), ticket.getActivityId(), "000000");
                    }
                }
            }

        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        }
        return null;
    }

    private List<Ticket> getTicket(String kindOfTime) throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;

        List<Ticket> r = new ArrayList<>();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append(" SELECT c.c_id_ticket, d.processId, c.c_parent_id AS parent_id_ticket, e.id as activity_id, e.PDEFNAME as process_def_id, e.STATE, c.c_customer_segment, "); 
        if ("hour".equalsIgnoreCase(kindOfTime)) {
            query.append(" 24 * (sysdate - to_date(to_char(c.datemodified, 'YYYY-MM-DD hh24:mi'), 'YYYY-MM-DD hh24:mi')) as diff_hours  "); //diff time in hour
        } else {
            query.append(" ( CAST( sysdate AS DATE ) - CAST( c.datemodified AS DATE ) ) * 1440 AS diff_hours  "); // diff time in minute
        }
        query.append(" from app_fd_ticket c  ")
                .append(" JOIN wf_process_link d ON c.c_parent_id = d.originProcessId  ")
                .append(" join SHKACTIVITIES e on e.PROCESSID = d.PROCESSID  ")
                .append(" WHERE c.C_TICKET_STATUS = 'MEDIACARE' ")
                .append(" and e.state = '1000003'  ")
                .append(" order by c.DATEMODIFIED asc ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());

            result = ps.executeQuery();
            Ticket tc = null;
            while (result.next()) {
                tc = new Ticket();
                tc.setTicketId(result.getString("c_id_ticket"));
                tc.setProcessId(result.getString("processId"));
                tc.setParentTicketInc(result.getString("parent_id_ticket"));
                tc.setActivityId(result.getString("activity_id"));
                tc.setProcessDefId(result.getString("process_def_id"));
                tc.setState(result.getString("STATE"));
                tc.setDiffHour(result.getString("diff_hours"));
                tc.setCust_segment(result.getString("c_customer_segment"));

                r.add(tc);
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            try {
                if (result != null) {
                    result.close();
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
        return r;
    }

    private void updateStatusTikcet(String processIdTicketSqm) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = null;
        rs = null;
        StringBuilder queryUpdate = new StringBuilder();
        queryUpdate.append("update app_fd_ticket ")
                .append("set c_action_status = ? ")
                .append(",c_run_process = ? ")
                .append("WHERE c_parent_id = ? ");

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(queryUpdate.toString());
                ps.setString(1, "DEADLINETOSALAMSIM");
                ps.setString(2, "1");
                ps.setString(3, processIdTicketSqm);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
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
    }

}
