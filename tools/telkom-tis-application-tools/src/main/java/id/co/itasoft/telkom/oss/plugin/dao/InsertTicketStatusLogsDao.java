/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.FinalcheckActModel;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;

/**
 *
 * @author tarkiman
 * @modified rizki
 */
public class InsertTicketStatusLogsDao {

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    GetConnections gn = new GetConnections();
    LogInfo logInfo = new LogInfo();

    public TicketStatus getTicketId(String processId) throws Exception {
        TicketStatus r = new TicketStatus();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String pending_status = "";
        String action_status = "";

        StringBuilder query = new StringBuilder();
        Connection con = gn.getJogetConnection();
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
                .append(" b.c_code_validation, ")
                .append(" b.c_auto_backend, ")
                .append(" b.c_run_process, ")
                .append(" b.c_summary ")
                .append(" FROM app_fd_ticket b ")
                .append(" WHERE b.c_parent_id=? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);

            rs = ps.executeQuery();
            while (rs.next()) {
//                LogUtil.info(getClass().getName(), "ticket id :" + rs.getString("c_id_ticket"));
                pending_status = (rs.getString("c_pending_status") == null) ? "" : rs.getString("c_pending_status");
                action_status = (rs.getString("c_action_status") == null) ? "" : rs.getString("c_action_status");

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
                r.setExternalTicketid(rs.getString("c_external_ticketid"));
                r.setServiceAddress(rs.getString("c_service_address"));
                r.setClosedBy(rs.getString("c_closed_by"));
                timestamp = new Timestamp(rs.getDate("dateCreated").getTime());
                r.setDateCreated(timestamp);
                r.setBookingId(rs.getString("c_booking_id"));

                timestamp = new Timestamp(rs.getDate("dateModified").getTime());
                r.setDateModified(timestamp);
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
                r.setContactName(rs.getString("c_contact_name"));
                r.setReferenceNumber(rs.getString("c_reference_number"));
                r.setTechnology(rs.getString("c_technology"));
                r.setSccCode(rs.getString("c_scc_code"));
                r.setCode_validation(rs.getString("c_code_validation"));
                r.setAutoBackend(rs.getString("c_auto_backend"));
                r.setRunProcess(rs.getString("c_run_process"));
                r.setSummary(rs.getString("c_summary"));
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

        return r;
    }

    public TicketStatus getTicketStatusByTicketID(String ticketId) throws SQLException, Exception {

        TicketStatus ts = new TicketStatus();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        Connection con = gn.getJogetConnection();
        query
                .append(" SELECT * FROM ( ")
                .append(" SELECT ")
                .append(" a.id, ")
                .append(" a.dateCreated, ")
                .append(" a.c_ticketid, ")
                //                .append(" a.c_ownergroup, ")
                .append(" a.c_assignedownergroup, ")
                .append(" a.c_status ")
                .append(" FROM app_fd_ticketstatus a ")
                .append(" WHERE trim(a.c_ticketid)=trim(?) ")
                .append(" ORDER BY a.dateCreated DESC ")
                .append(" ) WHERE ROWNUM =1 ");
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);

            rs = ps.executeQuery();
            while (rs.next()) {
                timestamp = new Timestamp(rs.getDate("dateCreated").getTime());
                ts.setDateCreated(timestamp);
                ts.setOwnerGroup(rs.getString("c_assignedownergroup")); // USED FOR LAST STATUS OWNERGROUP
            }
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

        return ts;
    }

    public boolean insertNewTicketStatus(TicketStatus r) throws SQLException, Exception {
        boolean result = false;
        String pinPoint = "";
        PreparedStatement ps = null;
        if (!"REQUEST_PENDING".equalsIgnoreCase(r.getActionStatus())
                && !"AFTER PENDING".equalsIgnoreCase(r.getActionStatus())
                && !"AFTER_REQUEST".equalsIgnoreCase(r.getActionStatus())) {
            pinPoint = "TRUE";
        }

        StringBuilder query = new StringBuilder();
        Connection con = gn.getJogetConnection();
//        con.setAutoCommit(false);
        query
                .append(" INSERT INTO app_fd_ticketstatus ")
                .append(" ( ")
                .append(" id, ")
                .append(" dateCreated, ")
                .append(" dateModified, ")
                .append(" createdBy, ")
                .append(" createdByName, ")
                .append(" modifiedBy, ")
                .append(" modifiedByName, ")
                .append(" c_owner, ")
                .append(" c_ticketstatusid, ")
                .append(" c_changeby, ")
                .append(" c_memo, ")
                .append(" c_changedate, ")
                .append(" c_ownergroup, ")
                .append(" c_assignedownergroup, ")
                .append(" c_orgid, ")
                .append(" c_statustracking, ")
                .append(" c_siteid, ")
                .append(" c_class, ")
                .append(" c_ticketid, ")
                .append(" c_action_status, ")
                .append(" c_status, ")
                .append(" c_tkstatusid, ")
                .append(" c_pin_point ")
                .append(" ) ")
                .append(" VALUES ( ")
                .append(" ?, ") // ID
                .append(" SYSDATE, ")
                .append(" SYSDATE, ")
                .append(" ?, ") //createdby
                .append(" ?, ")//createdByName - 5
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")//c_changeby - 10
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")//c_orgid - 15
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")//c_status - 20
                .append(" ?, ")
                .append(" ? ")
                .append(" ) ");

        try {
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setString(2, r.getOwner());//createdBy
            ps.setString(3, "");//createdByName
            ps.setString(4, "");//modifiedBy
            ps.setString(5, "");//modifiedByName
            ps.setString(6, r.getOwner());
            ps.setString(7, r.getTicketStatusId());
            ps.setString(8, r.getChangeBy()); //r.getOwner()
            ps.setString(9, r.getMemo());
            ps.setString(10, r.getChangeDate());
            if ("NEW".equalsIgnoreCase(r.getStatus())) {
                ps.setString(11, "");
                ps.setString(12, "");
            } else {
                ps.setString(11, r.getOwnerGroup()); // last 
                ps.setString(12, r.getAssignedOwnerGroup()); //now
            }

            ps.setString(13, r.getOrgId());
            ps.setString(14, r.getStatusTracking());
            ps.setString(15, r.getSiteId());
            ps.setString(16, r.getClasS());
            ps.setString(17, r.getTicketId());
            ps.setString(18, (r.getActionStatus() == null) ? "" : r.getActionStatus());
            ps.setString(19, r.getStatus());
            ps.setString(20, r.getTkStatusId());
            ps.setString(21, pinPoint);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }
//            con.commit();
//            LogUtil.info(getClass().getName(), "COMMIT TRUE");
        } catch (SQLException ex) {
//            con.rollback();
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            r = null;
            query = null;
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

    public void UpdateOwnerTicket(String processId) throws SQLException {

        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_owner='', c_memo='' ")
                .append(" WHERE c_parent_id=? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    public void UpdatePinPointLastStatus(TicketStatus r) throws SQLException {

        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();
        query.append(" UPDATE app_fd_ticketstatus SET c_pin_point='FALSE' WHERE trim(c_ticketid)=trim(?) AND trim(c_status)=trim(?) ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, r.getTicketId());
            ps.setString(2, r.getStatus());
            ps.executeUpdate();
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    public void UpdateStatus(String processId) throws SQLException, Exception {
        PreparedStatement ps = null;
        Connection con = gn.getJogetConnection();
        StringBuilder query = new StringBuilder();
        boolean result = false;
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_status = c_ticket_status ")
                .append(" WHERE c_parent_id = ? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);
            ps.executeUpdate();
            result = true;
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    /**
     * UPDATE STATUS TARGET, AND CLEAR OWNER DAN MEMO
     *
     * @param processId
     * @param statusTarget
     * @throws SQLException
     * @throws Exception
     */
    public void UpdateStatusAndClearOwner(String processId, String statusTarget) throws SQLException, Exception {
        PreparedStatement ps = null;
        Connection con = gn.getJogetConnection();
        StringBuilder query = new StringBuilder();
        boolean result = false;
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_status = ?, c_ticket_status=?, c_owner='', c_memo='' ")
                .append(" WHERE c_parent_id = ? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, statusTarget);
            ps.setString(2, statusTarget);
            ps.setString(3, processId);
            ps.executeUpdate();
            result = true;
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    public void UpdateOwnergroup(String processId, String ownergroup) throws SQLException, Exception {

        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        Connection con = gn.getJogetConnection();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_owner_group = ? ")
                .append(" WHERE c_parent_id = ? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ownergroup);
            ps.setString(2, processId);
            ps.executeUpdate();
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

//        return response;
    }

    public void UpdateStatusChildGamas(String processId, String statusChildGamas) throws SQLException, Exception {
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        Connection con = gn.getJogetConnection();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_child_gamas = ? ")
                .append(" WHERE c_parent_id = ? ");
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, statusChildGamas);
            ps.setString(2, processId);
            ps.executeUpdate();
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    public String getLastAssignedOwnerGroup(String ticketId) throws SQLException, Exception {
        String lastAssignedOwnerGroup = "";
        StringBuilder query = new StringBuilder();
        Connection con = gn.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        query
                .append(" SELECT ")
                .append(" c_assignedownergroup ")
                .append(" FROM app_fd_ticketstatus ")
                .append(" WHERE trim(c_ticketID) = ? ")
                .append(" AND ROWNUM = 1 ")
                .append(" ORDER BY dateCreated DESC ");
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            rs = ps.executeQuery();

            if (rs.next()) {
                lastAssignedOwnerGroup = (rs.getString("c_assignedownergroup") != null) ? rs.getString("c_assignedownergroup") : "";
            }

        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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
        // LogUtil.info("INSERT TICKET STATUS DAO - ", "LAST ASSIGNED OWNER GROUP : " + lastAssignedOwnerGroup);
        return lastAssignedOwnerGroup;
    }

    public void ClearPendingStatus(String processId) throws SQLException {

        boolean result = false;
        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        Connection con = ds.getConnection();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_pending_status='' ")
                .append(" WHERE c_parent_id=? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    public String getTicketIdFunction() throws SQLException, Exception {
        String ticketId = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = gn.getJogetConnection();
        StringBuilder query = new StringBuilder();
        try {
            query.append(" SELECT  GET_INCIDENT_ID as ticket_id from dual ");
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                ticketId = rs.getString("ticket_id");
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "ERROR GET TICKET ID :" + ex.getMessage());
        } finally {
            query = null;
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

        return ticketId;
    }

    public void UpdateTicketIdByProcess(String processId, String ticket_id) throws SQLException, Exception {
        boolean bool = false;

        PreparedStatement ps = null;
        Connection con = gn.getJogetConnection();
        StringBuilder query = new StringBuilder();
        try {
            boolean result = false;
            query
                    .append(" UPDATE app_fd_ticket SET ")
                    .append(" c_id_ticket = ? ")
                    .append(" WHERE c_parent_id = ? ");

            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticket_id);
            ps.setString(2, processId);
            ps.executeUpdate();
            result = true;
        } catch (Exception ex) {
//            con.rollback();
            LogUtil.error(this.getClass().getName(), ex, "ERROR UPDATE TICKET BY PROCESS ID:" + ex.getMessage());
        } finally {
            query = null;
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

    public boolean UpdateStatusAndActStat(FinalcheckActModel fam, String processId) throws SQLException {
        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        StringBuilder query = new StringBuilder();
        boolean result = false;
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_status = ?, c_ticket_status = ?, c_action_status = ? ")
                .append(" WHERE c_parent_id = ? ");
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, fam.getTicket_status());
            ps.setString(2, fam.getTicket_status());
            ps.setString(3, fam.getAction_status());
            ps.setString(4, processId);
            ps.executeUpdate();
            result = true;
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

    public Timestamp getCurrentDate() throws SQLException {
        Timestamp tmp = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        StringBuilder query = new StringBuilder();
        boolean result = false;
        query.append(" SELECT SYSDATE FROM DUAL ");

        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                tmp = rs.getTimestamp("SYSDATE");
            }
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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

        return tmp;

    }

    public void insertTicketStatus(TicketStatus r) throws SQLException, Exception {
        String pinPoint = "";
        PreparedStatement ps = null;
        if (!"REQUEST_PENDING".equalsIgnoreCase(r.getActionStatus())
                && !"AFTER PENDING".equalsIgnoreCase(r.getActionStatus())
                && !"AFTER_REQUEST".equalsIgnoreCase(r.getActionStatus())) {
            pinPoint = "TRUE";
        }
        StringBuilder query = new StringBuilder();
        Connection con = gn.getJogetConnection();
        query
                .append(" INSERT INTO app_fd_ticketstatus ")
                .append(" ( ")
                .append(" id, ")
                .append(" dateCreated, ")
                .append(" dateModified, ")
                .append(" createdBy, ")
                .append(" createdByName, ")
                .append(" modifiedBy, ")
                .append(" modifiedByName, ")
                .append(" c_owner, ")
                .append(" c_ticketstatusid, ")
                .append(" c_changeby, ")
                .append(" c_memo, ")
                .append(" c_changedate, ")
                .append(" c_ownergroup, ")
                .append(" c_assignedownergroup, ")
                .append(" c_orgid, ")
                .append(" c_statustracking, ")
                .append(" c_siteid, ")
                .append(" c_class, ")
                .append(" c_ticketid, ")
                .append(" c_action_status, ")
                .append(" c_status, ")
                .append(" c_tkstatusid, ")
                .append(" c_pin_point ")
                .append(" ) ")
                .append(" VALUES ( ")
                .append(" ?, ")
                .append(" SYSDATE, ")
                .append(" SYSDATE, ")
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
                .append(" ?, ");
                if ("false".equalsIgnoreCase(r.getStatusTracking())) {
                  query.append(" ?, ");
                }else {
                  query.append(" getduration(?), ");
                }
            query
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ? ")
                .append(" ) ");

        try {
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setString(2, r.getOwner());//createdBy
            ps.setString(3, "");//createdByName
            ps.setString(4, "");//modifiedBy
            ps.setString(5, "");//modifiedByName
            ps.setString(6, r.getOwner());
            ps.setString(7, r.getTicketStatusId());
            ps.setString(8, r.getChangeBy()); //r.getOwner()
            ps.setString(9, r.getMemo());
            ps.setString(10, r.getChangeDate());
            if ("NEW".equalsIgnoreCase(r.getStatus())) {
                ps.setString(11, "");
                ps.setString(12, "");
            } else {
                ps.setString(11, r.getOwnerGroup()); // last 
                ps.setString(12, r.getAssignedOwnerGroup()); //now
            }

            ps.setString(13, r.getOrgId());
            if ("false".equalsIgnoreCase(r.getStatusTracking())) {
              ps.setString(14, "");
            }else {
              ps.setString(14, r.getStatusTracking());
            }
            ps.setString(15, r.getSiteId());
            ps.setString(16, r.getClasS());
            ps.setString(17, r.getTicketId());
            ps.setString(18, (r.getActionStatus() == null) ? "" : r.getActionStatus());
            ps.setString(19, r.getStatus());
            ps.setString(20, r.getTkStatusId());
            ps.setString(21, pinPoint);
            ps.executeUpdate();

        } catch (SQLException ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            r = null;
            query = null;
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

    public void UpdateDateModifiedByProcess(String processId) throws SQLException {
        PreparedStatement ps = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        StringBuilder query = new StringBuilder();
        boolean result = false;
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" dateModified = SYSDATE ")
                .append(" WHERE c_parent_id = ? ");
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);
            ps.executeUpdate();
            result = true;
        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
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
