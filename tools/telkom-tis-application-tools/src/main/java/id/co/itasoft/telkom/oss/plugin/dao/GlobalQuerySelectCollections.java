/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author asani
 */
public class GlobalQuerySelectCollections {

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    GetConnections gn = new GetConnections();
    LogInfo logInfo = new LogInfo();

    /**
     * GET DATA FROM TBL APP_FD_TICKET BY c_parent_id=processId
     *
     * @param processId
     * @return TicketStatus
     * @throws Exception
     */
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
                .append(" b.c_tsc_result_category ")
                .append(" FROM app_fd_ticket b ")
                .append(" WHERE b.c_parent_id=? ");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processId);

            rs = ps.executeQuery();
            while (rs.next()) {
                pending_status
                        = (rs.getString("c_pending_status") == null) ? "" : rs.getString("c_pending_status");
                action_status
                        = (rs.getString("c_action_status") == null) ? "" : rs.getString("c_action_status");

                r.setId(rs.getString("id"));
                r.setTicketId(rs.getString("c_id_ticket"));
                if ("REJECTED".equalsIgnoreCase(pending_status)
                        && "AFTER PENDING".equalsIgnoreCase(action_status)) {
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
                r.setTscMeasurement(rs.getString("c_tsc_result_category"));
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

    /**
     * GET COUNT OTHER TICKET (GAUL) IN MONTH AND YEAR CURRENT BY SERVICE ID
     *
     * @param service_id
     * @param date_created
     * @return integer
     */
    public Integer getCountGaulByServiceId(String service_id, String date_created) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        int total = 0;
        query
                .append(" SELECT EXTRACT(YEAR FROM dateCreated) year, EXTRACT(MONTH FROM dateCreated) month , COUNT(dateCreated) total  ")
                .append(" FROM app_fd_ticket WHERE  ")
                .append(" EXTRACT(MONTH FROM dateCreated) = EXTRACT(MONTH FROM TO_TIMESTAMP(?,'YYYY-MM-DD HH24:MI:SS.FF'))  ")
                .append(" AND EXTRACT(YEAR FROM dateCreated) = EXTRACT(YEAR FROM TO_TIMESTAMP(?,'YYYY-MM-DD HH24:MI:SS.FF'))  ")
                .append(" AND c_service_id = ?  ")
                .append(" GROUP BY EXTRACT(YEAR FROM dateCreated), EXTRACT(MONTH FROM dateCreated) ");

        try {
            con = gn.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, date_created);
            ps.setString(2, date_created);
            ps.setString(3, service_id);

            rs = ps.executeQuery();
            while (rs.next()) {
                total = (rs.getInt("total") - 1);
            }

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
            query = null;
        }

        return total;
    }

    /**
     * 
     * @param service_id
     * @param customerSegment
     * @return 
     */
    public Integer getCountGaulByServiceIdCstmSgmn(String service_id, String customerSegment, String parent_id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        int total = 0;
        if ("DCS".equalsIgnoreCase(customerSegment) || customerSegment.isEmpty()) {
            query
                .append(" SELECT  count(datecreated) total ")
                .append(" FROM app_fd_ticket WHERE 1=1 ")
                .append(" AND c_service_id = ?  ")
//                .append(" AND (SELECT C_REPORTED_DATE FROM APP_FD_TICKET WHERE C_PARENT_ID = ?)  ")
                .append(" AND C_REPORTED_DATE ")
                .append(" >= (SELECT C_REPORTED_DATE FROM APP_FD_TICKET WHERE C_PARENT_ID = ?)-30  ")
                .append(" ORDER BY datecreated asc ");
        } else {
            query
//                    .append(" SELECT count(c_id_ticket) total ")
//                    .append(" FROM app_fd_ticket WHERE 1=1 ")
//                    .append(" AND c_service_id = ? ")
//                    .append(" AND c_reported_date > sysdate-60 ");
                .append(" SELECT  count(datecreated) total ")
                .append(" FROM app_fd_ticket WHERE 1=1 ")
                .append(" AND c_service_id = ?  ")
//               .append(" AND (SELECT C_REPORTED_DATE FROM APP_FD_TICKET WHERE C_PARENT_ID = ?)  ")
               .append(" AND C_REPORTED_DATE ")
                .append(" >= (SELECT C_REPORTED_DATE FROM APP_FD_TICKET WHERE C_PARENT_ID = ?)-60  ")
//                .append(" AND EXTRACT(MONTH FROM c_reported_date) >= EXTRACT(MONTH FROM c_reported_date-60) ")
//                .append(" AND EXTRACT(DAY FROM c_reported_date) >= EXTRACT(DAY FROM c_reported_date-60) ")
//                .append(" AND EXTRACT(YEAR FROM c_reported_date) = EXTRACT(YEAR FROM c_reported_date-60) ")
                .append(" ORDER BY datecreated asc ");
        }

        try {
            con = gn.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, service_id);
            ps.setString(2, parent_id);
//            ps.setString(3, parent_id);

            rs = ps.executeQuery();
            while (rs.next()) {
                total = (rs.getInt("total") - 1);
            }

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
            query = null;
        }

        return total;
    }

    /**
     * GET OWNERGROUP LAST - REOPEN ACTION
     *
     * @param ticketId
     * @param cdsn
     * @return
     * @throws SQLException
     */
    public TicketStatus getLastOwnerGroupByTicketID(String ticketId, String cdsn)
            throws SQLException {
        TicketStatus ts = new TicketStatus();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        StringBuilder query = new StringBuilder();
        String limit = "";

        if (!cdsn.equalsIgnoreCase("3")) {
            // reject
            query
                    .append(" SELECT a.c_assignedownergroup c_assignedownergroup,  ")
                    .append(
                            " a.c_status c_status, a.c_action_status c_action_status, a.dateCreated dateCreated  ")
                    .append(" FROM app_fd_ticketstatus a  ")
                    .append(" JOIN app_fd_ticket b ON trim(a.c_ticketid) = trim(b.c_id_ticket)   ")
                    .append(" WHERE trim(a.c_status) = trim(b.c_status)  ")
                    .append(" AND trim(a.c_ticketid)=trim(?) AND trim(a.c_pin_point)='TRUE'  ")
                    .append(" ORDER BY DATECREATED desc fetch first 1 row only ");
            //                    .append(" SELECT max(a.c_assignedownergroup) c_assignedownergroup, ")
            //                    .append(" max(a.c_status)c_status, max(a.c_action_status)
            // c_action_status, max(a.dateCreated) dateCreated ")
            //                    .append(" FROM app_fd_ticketstatus a ")
            //                    .append(" JOIN app_fd_ticket b ON trim(a.c_ticketid) =
            // trim(b.c_id_ticket)  ")
            //                    .append(" WHERE trim(a.c_status) = trim(b.c_status) ")
            //                    .append(" AND trim(a.c_ticketid)=trim(?) AND trim(a.c_pin_point)='TRUE'
            // ");

        } else {
            query
                    //                    .append(" SELECT DISTINCT max(dateCreated) dateCreated,
                    // max(a.c_assignedownergroup) c_assignedownergroup,")
                    //                    .append(" max(a.c_status) c_status, max(a.c_action_status)
                    // c_action_status ")
                    .append(
                            " SELECT DISTINCT  dateCreated, c_assignedownergroup, c_status, c_action_status, c_pin_point ")
                    .append(" FROM app_fd_ticketstatus a  ")
                    .append(" WHERE trim(a.c_status) IN ('ANALYSIS', 'BACKEND', 'DRAFT') ")
                    .append(" AND trim(a.c_pin_point)='TRUE' AND trim(a.c_ticketid) = trim(?) ")
                    .append(" ORDER BY dateCreated DESC fetch first 1 row only ");
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);

            rs = ps.executeQuery();
            if (rs.next()) {
                timestamp = new Timestamp(rs.getDate("dateCreated").getTime());
                ts.setDateCreated(timestamp);
                ts.setOwnerGroup(rs.getString("c_assignedownergroup"));
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
        return ts;
    }

    /**
     * CHECK TECHNLOGY DI TECHNICAL_DATA
     *
     * @param ticket_id
     * @return
     * @throws Exception
     */
    public boolean checkTechnology(String ticket_id) throws Exception {
        gn = new GetConnections();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append(
                " select count(*) as total from APP_FD_TIS_TECHNICAL_DATA where C_ID_TICKET = ? and trim(C_PORT_NAME) = trim('TECHNOLOGY') and trim(C_PIPE_NAME) = trim('Non Fiber')  ");
        boolean bool = false;
        Connection con = gn.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticket_id);
            rs = ps.executeQuery();
            int total = 0;
            while (rs.next()) {
                total = rs.getInt("total");
            }

            if (total > 0) {
                bool = true;
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

        return bool;
    }

    public ListIbooster getIbooster(String nd, String realm, String ticketId) throws SQLException, Exception {
        CallRestAPI callApi = new CallRestAPI();
        MasterParamDao paramDao = new MasterParamDao();
        ListIbooster data = new ListIbooster();
        LogHistoryDao lh = new LogHistoryDao();
        ApiConfig apiConfig = new ApiConfig();
        RESTAPI _RESTAPI = new RESTAPI();

        String tokenIbooster = _RESTAPI.getToken();
        String response = "";
        MasterParam param = new MasterParam();
        try {
            param = paramDao.getUrl("get_ibooster");
            apiConfig.setUrl(param.getUrl());

            RequestBody formBody = new FormBody.Builder().add("nd", nd).add("realm", realm).build();
            response = callApi.sendPostIbooster(apiConfig, formBody, tokenIbooster);
            
            Object obj = new JSONTokener(response).nextValue();
            JSONObject data_obj = (JSONObject) obj;

            if (!data_obj.has("MESSAGE")) {
              String oper_status =  (data_obj.has("oper_status")) ? data_obj.get("oper_status").toString() : "";
              String onu_rx_pwr =  (data_obj.has("onu_rx_pwr")) ? data_obj.get("onu_rx_pwr").toString() : "";
              String onu_tx_pwr =  (data_obj.has("onu_tx_pwr")) ? data_obj.get("onu_tx_pwr").toString() : "";
              String olt_rx_pwr =  (data_obj.has("olt_rx_pwr")) ? data_obj.get("olt_rx_pwr").toString() : "";
              String olt_tx_pwr =  (data_obj.has("olt_tx_pwr")) ? data_obj.get("olt_tx_pwr").toString() : "";
              String fiber_length =  (data_obj.has("fiber_length")) ? data_obj.get("fiber_length").toString() : "";
              String status_jaringan =  (data_obj.has("status_jaringan")) ? data_obj.get("status_jaringan").toString() : "";
              String identifier =  (data_obj.has("identifier")) ? data_obj.get("identifier").toString() : "";
              String id_ukur =  (data_obj.has("id_ukur")) ? data_obj.get("id_ukur").toString() : "";
              String nas_ip =  (data_obj.has("nas_ip")) ? data_obj.get("nas_ip").toString() : "";
              String hostname =  (data_obj.has("hostname")) ? data_obj.get("hostname").toString() : "";
              String clid =  (data_obj.has("clid")) ? data_obj.get("clid").toString() : "";
              String status_cpe =  (data_obj.has("status_cpe")) ? data_obj.get("status_cpe").toString() : "";
              String session_start =  (data_obj.has("session_start")) ? data_obj.get("session_start").toString() : "";

              oper_status = (oper_status == null) ? "" : oper_status;
              onu_rx_pwr = (onu_rx_pwr == null) ? "" : onu_rx_pwr;
              onu_tx_pwr = (onu_tx_pwr == null) ? "" : onu_tx_pwr;
              olt_rx_pwr = (olt_rx_pwr == null) ? "" : olt_rx_pwr;
              olt_tx_pwr = (olt_tx_pwr == null) ? "" : olt_tx_pwr;
              fiber_length = (fiber_length == null) ? "" : fiber_length;
              status_jaringan = (status_jaringan == null) ? "" : status_jaringan;
              identifier = (identifier == null) ? "" : identifier;
              id_ukur = (id_ukur == null) ? "" : id_ukur;
              nas_ip = (nas_ip == null) ? "" : nas_ip;
              hostname = (hostname == null) ? "" : hostname;
              clid = (clid == null) ? "" : clid;
              status_cpe = (status_cpe == null) ? "" : status_cpe;
              session_start = (session_start == null) ? "" : session_start;


                data.setOperStatus(oper_status);
                data.setOnuRxPwr(onu_rx_pwr);
                data.setOnuTxPwr(onu_tx_pwr);
                data.setOltRxPwr(olt_rx_pwr);
                data.setOltTxPwr(olt_tx_pwr);
                data.setFiberLength(fiber_length);
                data.setStatusJaringan(status_jaringan);
                data.setIdentifier(identifier);
                data.setIdUkur(id_ukur);
                data.setNasIp(nas_ip);
                data.setHostname(hostname);
                data.setClid(clid);
                data.setStatusCpe(status_cpe);
                data.setMeasurementTime(session_start);
//                data.setMessage(data_obj.get("MESSAGE") == null ? "" : data_obj.get("MESSAGE").toString());
                data.setMessage("");
            } else {
                data.setMessage(data_obj.get("MESSAGE") == null ? "" : data_obj.get("MESSAGE").toString());
                data.setOnuRxPwr("null");
                data.setOnuTxPwr("null");
                data.setOltRxPwr("null");
                data.setOltTxPwr("null");
            }

            String onuRxPwrStr = data_obj.has("onu_rx_pwr") ? data_obj.getString("onu_rx_pwr") : "0";
            Float onuRxPwr = Float.valueOf(onuRxPwrStr == null ? "0" : onuRxPwrStr);
            String measurementCategory = "";

            if (onuRxPwr <= Float.valueOf(-13) && onuRxPwr >= Float.valueOf(-24)) {
                measurementCategory = "SPEC";
            } else {
                measurementCategory = "UNSPEC";
            }

            data.setMeasurementCategory(measurementCategory);

            formBody = null;
            data_obj = null;
        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            LogHistory dataLh = new LogHistory();
            dataLh.setRequest("{\"nd\": \"" + nd + "\",\"realm\": \"" + realm + "\"}");
            dataLh.setMethod("POST");
            dataLh.setResponse(response);
            dataLh.setAction("Measure ibooster - GAMAS");
            dataLh.setUrl(param.getUrl());
            dataLh.setTicketId(ticketId);
            lh.insertToLogHistory(dataLh);
            callApi = null;
            paramDao = null;
            lh = null;
            param = null;
            apiConfig = null;
        }

        return data;
    }

    /**
     * GET CONFIGURATION MAPPING
     *
     * @return
     * @throws Exception
     */
    public JSONObject getConfigurationMapping() throws Exception {
        gn = new GetConnections();
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        JSONObject result = new JSONObject();
        query
                .append(" select id, datecreated, datemodified, c_button_wo, c_check_ibooster, c_atvr_resolved, ")
                .append(" c_day_resolved, c_hour_resolved, c_minutes_resolved From app_fd_configuration fetch first 1 row only ");
        boolean bool = false;
        Connection con = gn.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
//            result = new JSONObject((Map) DSL.using(gn.getJogetConnection()).fetch(rs));
            if (rs.next()) {
                result.put("id", rs.getString("id"));
                result.put("datecreated", rs.getString("datecreated"));
                result.put("datemodified", rs.getString("datemodified"));
                result.put("checkIbooster", rs.getBoolean("c_check_ibooster"));
                result.put("buttonWo", rs.getBoolean("c_button_wo"));
                result.put("deadline_resolved", rs.getBoolean("c_atvr_resolved"));
//                result.put("duration_deadline_resolved", rs.getString("c_deadline_resolved"));
                result.put("day_resolved", rs.getInt("c_day_resolved"));
                result.put("hour_resolved", rs.getInt("c_hour_resolved"));
                result.put("minutes_resolved", rs.getInt("c_minutes_resolved"));
            } else {
                result.put("id", "");
                result.put("datecreated", "");
                result.put("datemodified", "");
                result.put("checkIbooster", true);
                result.put("buttonWo", true);
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

        return result;
    }
    
    
    public List<TicketStatus> getListTicket() throws Exception {
        TicketStatus r = new TicketStatus();
        List<TicketStatus> list = new ArrayList<TicketStatus>();
        
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
                .append(" b.c_tsc_result_category ")
                .append(" FROM app_fd_ticket b ")
                .append(" WHERE c_owner_group is null ")
                .append(" fetch first 30 rows only ");

        try {
            ps = con.prepareStatement(query.toString());

            rs = ps.executeQuery();
            while (rs.next()) {
                r = new TicketStatus();
                pending_status = (rs.getString("c_pending_status") == null) ? "" : rs.getString("c_pending_status");
                action_status = (rs.getString("c_action_status") == null) ? "" : rs.getString("c_action_status");

                r.setId(rs.getString("id"));
                r.setTicketId(rs.getString("c_id_ticket"));
                if ("REJECTED".equalsIgnoreCase(pending_status)
                        && "AFTER PENDING".equalsIgnoreCase(action_status)) {
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
                r.setTscMeasurement(rs.getString("c_tsc_result_category"));
                
                list.add(r);
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

        return list;
    }
    
    /**
     * get mapping ownergroup
     * @param ticketStatus
     * @return
     * @throws Exception 
     */
    public JSONArray getConfMappingOwnergroup(TicketStatus ticketStatus) throws Exception {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        
        JSONObject jsonData = null;
        JSONArray jSONArray = null;
        
        query.append(" select c_list_cs_sgmnt, c_channel, c_mp_ownergroup, c_reversevalue From app_fd_mapping_ownergroup ");
        Connection con = ds.getConnection();
        String statusTracking = "";
        try {
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
            String channel, customerSegment, ownergroup, reversevalue;
            jSONArray = new JSONArray();
            while (rs.next()) {
                jsonData = new JSONObject();
                
                channel = (rs.getString("c_channel") == null) 
                    ? "" : rs.getString("c_channel");
                customerSegment = (rs.getString("c_list_cs_sgmnt") == null) 
                    ? "" : rs.getString("c_list_cs_sgmnt");
                ownergroup = (rs.getString("c_mp_ownergroup") == null) 
                    ? "" : rs.getString("c_mp_ownergroup");
                reversevalue = (rs.getString("c_reversevalue") == null) 
                    ? "" : rs.getString("c_reversevalue");

                boolean boolReverse = Boolean.parseBoolean(reversevalue);
                jsonData.put("customer_segment", customerSegment);
                jsonData.put("channel", channel);
                jsonData.put("ownergroup", ownergroup);
                jsonData.put("reversevalue", boolReverse);
                jSONArray.put(jsonData);
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

        return jSONArray;
    }
}
