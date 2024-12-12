/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GlobalQuerySelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateAttributeTicketDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class CloseSqm extends DefaultApplicationPlugin {

    private final String pluginName = "Telkom New OSS - Ticket Incident Services - SQM Close";
    private final String pluginClassName = this.getClass().getName();

    InsertRelatedRecordDao irdao; 
    UpdateAttributeTicketDao tDao;
    GlobalQuerySelectCollections gqc;
    TicketStatus ts;
    LogInfo logInfo = new LogInfo();
    
    public CloseSqm() {
      irdao = new InsertRelatedRecordDao();
      tDao = new UpdateAttributeTicketDao();
      gqc = new GlobalQuerySelectCollections();
      ts = new TicketStatus();
    }
    
    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "7.0";
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
        return pluginClassName;
    }

    @Override
    public String getPropertyOptions() {
        return null;
    }

    @Override
    public Object execute(Map map) {
        try {
            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
            ApplicationContext ac = AppUtil.getApplicationContext();
            WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
            String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());

            
            ts = gqc.getTicketId(processId);
            if ("CUSTOMER".equalsIgnoreCase(ts.getSourceTicket())) {
                String serviceId = (ts.getServiceId() == null) ? "" : ts.getServiceId();
                String classificationFlag = (ts.getClassificationFlag() == null) ? 
                    "" : ts.getClassificationFlag();
                
                Ticket ticket = new Ticket();
                ticket = getTicketSqm(serviceId, classificationFlag);
                
                if (ticket.getProcessDefId() != null) {
                    String processDefId = ticket.getProcessDefId().replace("#", ":");
                    
                    HashMap<String, String> paramCkWo = new HashMap<String, String>();
                    paramCkWo.put("externalID1", ticket.getTicketId());
                    irdao.getStatusWo(paramCkWo, ticket.getTicketId(), "checkWorkORder - Close SQM");

                    HashMap<String, String> paramActSolution = new HashMap<String, String>();
                    paramActSolution.put("classification_code", "59069");
                    paramActSolution.put("hierarchy_type", "ACTSOL");
                    String descActualSolution = tDao.getDescActualSolution(paramActSolution);

                    updateStatusTikcet(ticket.getParentTicketInc(), "59069", descActualSolution);
                    workflowManager.assignmentForceComplete(processDefId, ticket.getProcessId(), ticket.getActivityId(), "000000");
                    
                    paramCkWo.clear();
                    paramActSolution.clear();
                }
            }

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
          irdao = null;
          tDao = null;
          gqc = null;
          ts = null;
        }
        return null;
    }

    public Ticket getTicketSqm(String serviceId, String classificationFlag) throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;
        Ticket t = new Ticket();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query.append(" SELECT c.c_service_id,d.processId, c.id AS id_ticket_inc, c.c_parent_id AS parent_id_ticket, c.c_customer_segment, c.c_classification_flag, ")
                .append(" c.c_service_no, c.c_realm, c.c_owner_group, c.c_finalcheck, c.c_service_type, c.c_classification_flag, c.c_child_gamas, ")
                .append(" c.c_classification_type, c.c_last_state, ")
                .append(" e.id as activity_id, e.ACTIVITYDEFINITIONID as activity_name, e.PDEFNAME as process_def_id, e.STATE , c.c_id_ticket ")
                .append(" from app_fd_ticket c ")
                .append(" JOIN wf_process_link d ON c.c_parent_id = d.originProcessId ")
                .append(" join SHKACTIVITIES e on e.PROCESSID = d.PROCESSID ")
                .append(" WHERE c.c_service_id = ? ")
                .append(" AND c.c_channel = '50' ")
                .append(" AND c.c_source_ticket = 'PROACTIVE' ")
                .append(" and c.C_TICKET_STATUS != 'CLOSED' ")
                .append(" and e.state in ('1000001','1000003')  ");
        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, serviceId);
//            ps.setString(2, classificationFlag);

            result = ps.executeQuery();
//            Ticket tc = null;
            if (result.next()) {

                t.setProcessId(result.getString("processId"));
                t.setIdTicketInc(result.getString("id_ticket_inc"));
                t.setParentTicketInc(result.getString("parent_id_ticket"));
                t.setCust_segment(result.getString("c_customer_segment"));
                t.setService_no(result.getString("c_service_no"));
                t.setRealm(result.getString("c_realm"));
                t.setOwnerGroup(result.getString("c_owner_group"));
                t.setFinalcheck(result.getString("c_finalcheck"));
                t.setServiceType(result.getString("c_service_type"));
                t.setClassificationFlag(result.getString("c_classification_flag"));
                t.setChildGamas(result.getString("c_child_gamas"));
                t.setClassificationType(result.getString("c_classification_type"));
                t.setActivityId(result.getString("activity_id"));
                t.setActivityName(result.getString("activity_name"));
                t.setProcessDefId(result.getString("process_def_id"));
                t.setState(result.getString("STATE"));
                t.setTicketId(result.getString("c_id_ticket"));
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
        return t;
    }

    public void updateStatusTikcet(String processIdTicketSqm, String actualSolution, String descActualSolution) throws SQLException {
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
                .append(",c_actual_solution = ? ")
                .append(",c_description_actualsolution = ? ")
                .append("WHERE c_parent_id = ? ");

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(queryUpdate.toString());
                ps.setString(1, "SQMTOCLOSED");
                ps.setString(2, "1");
                ps.setString(3, actualSolution);
                ps.setString(4, descActualSolution);
                ps.setString(5, processIdTicketSqm);
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
