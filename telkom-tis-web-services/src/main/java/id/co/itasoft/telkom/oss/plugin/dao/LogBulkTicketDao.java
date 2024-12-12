package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.StringManipulation;
import id.co.itasoft.telkom.oss.plugin.model.LogBulkTicket;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.service.WorkflowUserManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogBulkTicketDao {

    public void insertLogBulk(LogBulkTicket logBulkTicket) {

        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        StringManipulation stringManipulation = new StringManipulation();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        Connection con = null;
        PreparedStatement ps = null;
        query.append("INSERT INTO APP_FD_LOG_BULK_TICKET (id, DATECREATED, CREATEDBY, C_RESPONSE_API, C_ID_TEMPLATE, C_SERVICE_ID, C_TICKET_ID, C_FILENAME)")
                .append("VALUES (SYS_GUID(),sysdate,?,?,?,?,?,?)");

        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, workflowUserManager.getCurrentUsername());
            ps.setString(2, stringManipulation.getNonNullTrimmed(logBulkTicket.getResponseAPI()));
            ps.setString(3, stringManipulation.getNonNullTrimmed(logBulkTicket.getIdTemplate()));
            ps.setString(4, stringManipulation.getNonNullTrimmed(logBulkTicket.getServiceId()));
            ps.setString(5, stringManipulation.getNonNullTrimmed(logBulkTicket.getTicketId()));
            ps.setString(6, stringManipulation.getNonNullTrimmed(logBulkTicket.getFilename()));
            ps.executeQuery();
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
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
            query = null;
        }

    }

}
