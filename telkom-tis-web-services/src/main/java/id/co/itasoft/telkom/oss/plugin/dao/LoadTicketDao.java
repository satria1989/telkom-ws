/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author itasoft
 */
public class LoadTicketDao {

    public String pending_status = "";
    public String action_status = "";
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public TicketStatus LoadTicketByIdTicket(String id_ticket) throws SQLException, Exception {

        TicketStatus r = new TicketStatus();
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" b.id, ")
                .append(" b.c_id_ticket, ")
                .append(" b.c_status, ")
                .append(" b.c_memo, ")
                .append(" b.c_owner, ")
                .append(" b.c_owner_group, ")
                .append(" b.c_action_status, ")
                .append(" b.c_finalcheck, ")
                .append(" b.c_pending_status, ")
                .append(" b.c_source_ticket, ")
                .append(" b.c_customer_segment, ")
                .append(" b.c_customer_id, ")
                .append(" b.c_classification_type, ")
                .append(" b.c_service_type, ")
                .append(" b.c_service_no, ")
                .append(" b.c_ticket_status, ")
                .append(" b.c_channel, ")
                .append(" b.c_service_id, ")
                .append(" b.c_contact_phone, ")
                .append(" b.c_name, ")
                .append(" b.c_classification_path, ")
                .append(" b.c_class_description, ")
                .append(" b.c_perangkat, ")
                .append(" b.c_otp_Landing_page_tracking, ")
                .append(" b.c_created_ticket_by, ")
                .append(" b.dateModified, ")
                .append(" b.dateCreated, ")
                .append(" b.c_external_ticketid, ")
                .append(" b.c_service_address, ")
                .append(" b.c_closed_by, ")
                .append(" b.c_booking_id, ")
                .append(" b.c_estimation, ")
                .append(" b.c_classification_flag, ")
                .append(" b.c_street_address, ")
                .append(" b.c_work_zone, ")
                .append(" b.c_solution, ")
                .append(" b.c_hard_complaint, ")
                .append(" b.c_urgensi, ")
                .append(" b.c_actual_solution, ")
                .append(" b.c_level_gamas, ")
                .append(" b.C_INTERNET_TEST_RESULT, ")
                .append(" b.C_MEASUREMENT_CATEGORY, ")
                .append(" b.c_qc_voice_ivr_result, ")
                .append(" b.c_last_state, ")
                .append(" b.C_SCC_CODE_VALIDATION, ")
                .append(" b.c_child_gamas, ")
                .append(" b.c_flag_fcr, ")
                .append(" b.c_rk_information, ")
                .append(" b.c_region, ")
                .append(" b.c_witel, ")
                .append(" b.c_pen_timeout, ")
                .append(" b.c_wo_status, ")
                .append(" b.c_contact_name, ")
                .append(" b.c_reference_number, ")
                .append(" b.c_technology, ")
                .append(" b.c_scc_code, ")
                .append(" b.c_reported_date, ")
                .append(" b.c_summary, ")
                .append(" b.c_details, ")
                .append(" b.c_code_validation, ")
                .append(" b.C_TICKET_ID_GAMAS ")
                .append(" FROM app_fd_ticket b ")
                .append(" WHERE b.c_id_ticket=? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, id_ticket);

            rs = ps.executeQuery();
            while (rs.next()) {
                pending_status = rs.getString("c_pending_status");
                action_status = rs.getString("c_action_status");

                r.setId(rs.getString("id"));
                r.setTicketId(rs.getString("c_id_ticket"));
                if ("REJECTED".equalsIgnoreCase(pending_status) && "AFTER PENDING".equalsIgnoreCase(action_status)) {
                    r.setStatus("PENDING");
                } else {
                    r.setStatus(rs.getString("c_status"));
                }
                r.setStatusCurrent(rs.getString("c_ticket_status"));
                r.setMemo(rs.getString("c_memo"));
                r.setOwner(rs.getString("c_owner"));
                r.setAssignedOwnerGroup(rs.getString("c_owner_group"));
                r.setActionStatus(action_status);
                r.setFinalCheck(rs.getString("c_finalcheck"));
                r.setSourceTicket(rs.getString("c_source_ticket"));
                r.setCustomerSegment(rs.getString("c_customer_segment"));
                r.setCustomerId(rs.getString("c_customer_id"));
                r.setClassification_type(rs.getString("c_classification_type"));
                r.setServiceType(rs.getString("c_service_type"));
                r.setServiceNo(rs.getString("c_service_no"));
                r.setPendingStatus(rs.getString("c_pending_status"));
                r.setChannel(rs.getString("c_channel"));
                r.setServiceId(rs.getString("c_service_id"));
                r.setPhone(rs.getString("c_contact_phone"));
                r.setCustomerName(rs.getString("c_name"));
                r.setSymptomId(rs.getString("c_classification_path"));
                r.setSymptomDesc(rs.getString("c_class_description"));
                r.setPerangkat(rs.getString("c_perangkat"));
                r.setLandingPage(rs.getString("c_otp_Landing_page_tracking"));
                r.setCreatedTicketBy(rs.getString("c_created_ticket_by"));
                r.setClosedBy(rs.getString("c_closed_by"));
                r.setEstimation(rs.getString("c_estimation"));
                r.setClassificationFlag(rs.getString("c_classification_flag"));
                r.setStreetAddress(rs.getString("c_street_address"));
                r.setWorkZone(rs.getString("c_work_zone"));
                r.setSolution(rs.getString("c_solution"));
                r.setHardComplaint(rs.getString("c_hard_complaint"));
                r.setUrgency(rs.getString("c_urgensi"));
                r.setActualSolution(rs.getString("c_actual_solution"));
                r.setLevetGamas(rs.getString("c_level_gamas"));
                r.setIbooster(rs.getString("c_measurement_category"));
                r.setScc_voice(rs.getString("c_qc_voice_ivr_result"));
                r.setScc_internet(rs.getString("C_INTERNET_TEST_RESULT"));
                r.setLast_state(rs.getString("c_last_state"));
                r.setScc_code_validation(rs.getString("c_scc_code_validation"));
                r.setChild_gamas(rs.getString("c_child_gamas"));
                r.setFlagFcr(rs.getString("c_flag_fcr"));
                r.setRkInformation(rs.getString("c_rk_information"));
                r.setRegion(rs.getString("c_region"));
                r.setWitel(rs.getString("c_witel"));
                r.setPen_timeout(rs.getString("c_pen_timeout"));
                r.setWoStatus(rs.getString("c_wo_status"));
                r.setReferenceNumber(rs.getString("c_reference_number"));
                r.setTechnology(rs.getString("c_technology"));
                r.setSccCode(rs.getString("c_scc_code"));
                r.setCode_validation(rs.getString("c_code_validation"));

                timestamp = new Timestamp(rs.getDate("dateCreated").getTime());
                r.setDateCreated(timestamp);
                r.setBookingId(rs.getString("c_booking_id"));

                timestamp = new Timestamp(rs.getDate("dateModified").getTime());
                r.setDateModified(timestamp);
                r.setReportedDate(rs.getString("c_reported_date"));
                r.setSummary(rs.getString("c_summary"));
                r.setDetails(rs.getString("c_details"));
                r.setContactName(rs.getString("c_contact_name"));
                r.setTicketIdGamas(rs.getString("C_TICKET_ID_GAMAS"));
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
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
            query = null;

        }

        return r;
    }

    public TicketStatus LoadTicketByParentId(String parent_id) throws SQLException, Exception {

        TicketStatus r = new TicketStatus();
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" b.id, ")
                .append(" b.c_id_ticket, ")
                .append(" b.c_status, ")
                .append(" b.c_memo, ")
                .append(" b.c_owner, ")
                .append(" b.c_owner_group, ")
                .append(" b.c_action_status, ")
                .append(" b.c_finalcheck, ")
                .append(" b.c_pending_status, ")
                .append(" b.c_source_ticket, ")
                .append(" b.c_customer_segment, ")
                .append(" b.c_customer_id, ")
                .append(" b.c_classification_type, ")
                .append(" b.c_service_type, ")
                .append(" b.c_service_no, ")
                .append(" b.c_ticket_status, ")
                .append(" b.c_channel, ")
                .append(" b.c_service_id, ")
                .append(" b.c_contact_phone, ")
                .append(" b.c_name, ")
                .append(" b.c_classification_path, ")
                .append(" b.c_class_description, ")
                .append(" b.c_perangkat, ")
                .append(" b.c_otp_Landing_page_tracking, ")
                .append(" b.c_created_ticket_by, ")
                .append(" b.dateModified, ")
                .append(" b.dateCreated, ")
                .append(" b.c_external_ticketid, ")
                .append(" b.c_service_address, ")
                .append(" b.c_closed_by, ")
                .append(" b.c_booking_id, ")
                .append(" b.c_estimation, ")
                .append(" b.c_classification_flag, ")
                .append(" b.c_street_address, ")
                .append(" b.c_work_zone, ")
                .append(" b.c_solution, ")
                .append(" b.c_hard_complaint, ")
                .append(" b.c_urgensi, ")
                .append(" b.c_actual_solution, ")
                .append(" b.c_level_gamas, ")
                .append(" b.C_INTERNET_TEST_RESULT, ")
                .append(" b.C_MEASUREMENT_CATEGORY, ")
                .append(" b.c_qc_voice_ivr_result, ")
                .append(" b.c_last_state, ")
                .append(" b.C_SCC_CODE_VALIDATION, ")
                .append(" b.c_child_gamas, ")
                .append(" b.c_flag_fcr, ")
                .append(" b.c_rk_information, ")
                .append(" b.c_region, ")
                .append(" b.c_witel, ")
                .append(" b.c_pen_timeout, ")
                .append(" b.c_wo_status, ")
                .append(" b.c_contact_name, ")
                .append(" b.c_reference_number, ")
                .append(" b.c_technology, ")
                .append(" b.c_scc_code, ")
                .append(" b.c_code_validation ")
                .append(" FROM app_fd_ticket b ")
                .append(" WHERE b.c_parent_id=? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, parent_id);

            rs = ps.executeQuery();
            while (rs.next()) {
                pending_status = rs.getString("c_pending_status");
                action_status = rs.getString("c_action_status");

                r.setId(rs.getString("id"));
                r.setTicketId(rs.getString("c_id_ticket"));
                if ("REJECTED".equalsIgnoreCase(pending_status) && "AFTER PENDING".equalsIgnoreCase(action_status)) {
                    r.setStatus("PENDING");
                } else {
                    r.setStatus(rs.getString("c_status"));
                }
                r.setStatusCurrent(rs.getString("c_ticket_status"));
                r.setMemo(rs.getString("c_memo"));
                r.setOwner(rs.getString("c_owner"));
                r.setAssignedOwnerGroup(rs.getString("c_owner_group"));
                r.setActionStatus(action_status);
                r.setFinalCheck(rs.getString("c_finalcheck"));
                r.setSourceTicket(rs.getString("c_source_ticket"));
                r.setCustomerSegment(rs.getString("c_customer_segment"));
                r.setCustomerId(rs.getString("c_customer_id"));
                r.setClassification_type(rs.getString("c_classification_type"));
                r.setServiceType(rs.getString("c_service_type"));
                r.setServiceNo(rs.getString("c_service_no"));
                r.setPendingStatus(rs.getString("c_pending_status"));
                r.setChannel(rs.getString("c_channel"));
                r.setServiceId(rs.getString("c_service_id"));
                r.setPhone(rs.getString("c_contact_phone"));
                r.setCustomerName(rs.getString("c_name"));
                r.setSymptomId(rs.getString("c_classification_path"));
                r.setSymptomDesc(rs.getString("c_class_description"));
                r.setPerangkat(rs.getString("c_perangkat"));
                r.setLandingPage(rs.getString("c_otp_Landing_page_tracking"));
                r.setCreatedTicketBy(rs.getString("c_created_ticket_by"));
                r.setClosedBy(rs.getString("c_closed_by"));
                r.setEstimation(rs.getString("c_estimation"));
                r.setClassificationFlag(rs.getString("c_classification_flag"));
                r.setStreetAddress(rs.getString("c_street_address"));
                r.setWorkZone(rs.getString("c_work_zone"));
                r.setSolution(rs.getString("c_solution"));
                r.setHardComplaint(rs.getString("c_hard_complaint"));
                r.setUrgency(rs.getString("c_urgensi"));
                r.setActualSolution(rs.getString("c_actual_solution"));
                r.setLevetGamas(rs.getString("c_level_gamas"));
                r.setIbooster(rs.getString("c_measurement_category"));
                r.setScc_voice(rs.getString("c_qc_voice_ivr_result"));
                r.setScc_internet(rs.getString("C_INTERNET_TEST_RESULT"));
                r.setLast_state(rs.getString("c_last_state"));
                r.setScc_code_validation(rs.getString("c_scc_code_validation"));
                r.setChild_gamas(rs.getString("c_child_gamas"));
                r.setFlagFcr(rs.getString("c_flag_fcr"));
                r.setRkInformation(rs.getString("c_rk_information"));
                r.setRegion(rs.getString("c_region"));
                r.setWitel(rs.getString("c_witel"));
                r.setPen_timeout(rs.getString("c_pen_timeout"));
                r.setWoStatus(rs.getString("c_wo_status"));
                r.setReferenceNumber(rs.getString("c_reference_number"));
                r.setTechnology(rs.getString("c_technology"));
                r.setSccCode(rs.getString("c_scc_code"));
                r.setCode_validation(rs.getString("c_code_validation"));

                timestamp = new Timestamp(rs.getDate("dateCreated").getTime());
                r.setDateCreated(timestamp);

                timestamp = new Timestamp(rs.getDate("dateModified").getTime());
                r.setDateModified(timestamp);
                r.setBookingId(rs.getString("c_booking_id"));
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
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
            query = null;

        }

        return r;
    }

    public String getEmployeeCode(String username) throws Exception {
        String result = "";

        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" EMPLOYEECODE ")
                .append(" FROM DIR_EMPLOYMENT ")
                .append(" WHERE USERID = ? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, username);

            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("EMPLOYEECODE");
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
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

        return result;
    }

    public void updateBookingDateByTicket(Timestamp bookingDate, String id_ticket) throws Exception {
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query.append(" UPDATE app_fd_ticket SET c_booking_date=? where c_id_ticket=? ");
        PreparedStatement ps = null;
        Connection con = null;
        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, bookingDate.toString());
            ps.setString(2, id_ticket);
            ps.executeUpdate();

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
        } finally {
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
    }
}
