package id.co.itasoft.telkom.oss.plugin.bulkTicket;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.StringManipulation;
import id.co.itasoft.telkom.oss.plugin.model.TicketAutomationModel;
import org.joget.commons.util.LogUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketTemplate {

    LogInfo logInfo = new LogInfo();
    StringManipulation stringManipulation = new StringManipulation();

    public TicketAutomationModel getDetailTicket(String id) {
        TicketAutomationModel automationModel = new TicketAutomationModel();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            GetConnections gc = new GetConnections();
            StringBuilder query = new StringBuilder();
            query.append(" SELECT ")
                    .append(" id, ")
                    .append(" datecreated, ")
                    .append(" datemodified, ")
                    .append(" createdby, ")
                    .append(" createdbyname, ")
                    .append(" modifiedby, ")
                    .append(" modifiedbyname,  ")
                    .append(" c_finalcheck,")
                    .append(" c_classification_path, ")
                    .append(" c_customer_segment, ")
                    .append(" c_source_ticket, ")
                    .append(" c_description_hard_complaint, ")
                    .append(" c_details, ")
                    .append(" c_classification_flag, ")
                    .append(" c_urgensi, ")
                    .append(" c_name, ")
                    .append(" c_source_ticket_desc, ")
                    .append(" c_id_template, ")
                    .append(" c_service_type, ")
                    .append(" c_parent_id, ")
                    .append(" c_customer_segment_desc, ")
                    .append(" c_auto_backend, ")
                    .append(" c_classification_type,  ")
                    .append(" c_hard_complaint, ")
                    .append(" c_description_urgensi, ")
                    .append(" c_class_description, ")
                    .append(" c_classification_category, ")
                    .append(" c_description_channel, ")
                    .append(" c_channel, ")
                    .append(" c_summary, ")
                    .append(" c_title, ")
                    .append(" c_reported_by")
                    .append(" FROM ")
                    .append(" app_fd_template_ticket ")
                    .append(" WHERE id = ? ");

            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, id);

            rs = ps.executeQuery();
            while (rs.next()) {
                automationModel.setClassification_path(stringManipulation.getNonNullTrimmed(rs.getString("c_classification_path")));
                automationModel.setReported_by(stringManipulation.getNonNullTrimmed(rs.getString("c_reported_by")));
                automationModel.setSource_ticket(stringManipulation.getNonNullTrimmed(rs.getString("c_source_ticket")));
                automationModel.setChannel(stringManipulation.getNonNullTrimmed(rs.getString("c_channel")));
                automationModel.setSummary(stringManipulation.getNonNullTrimmed(rs.getString("c_summary")));
                automationModel.setDetails(stringManipulation.getNonNullTrimmed(rs.getString("c_details")));
                automationModel.setCustomer_segment(stringManipulation.getNonNullTrimmed(rs.getString("c_customer_segment")));
                automationModel.setHard_complaint(stringManipulation.getNonNullTrimmed(rs.getString("c_hard_complaint")));
                automationModel.setUrgensi(stringManipulation.getNonNullTrimmed(rs.getString("c_urgensi")));
                automationModel.setService_type(stringManipulation.getNonNullTrimmed(rs.getString("c_service_type")));

            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
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

        return automationModel;
    }
}
