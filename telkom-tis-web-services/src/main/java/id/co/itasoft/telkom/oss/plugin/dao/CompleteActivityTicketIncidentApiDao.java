/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.ParseDate;
import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.sql.DataSource;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author mtaup
 */
public class CompleteActivityTicketIncidentApiDao {

    CallRestAPI callApi = new CallRestAPI();
    GetMasterParamDao paramDao = new GetMasterParamDao();
    public String errorUpdateData = "";
    public String apiId = "";
    public String apiKey = "";
    public String apiSecret = "";

    //    private final OkHttpClient httpClient = new OkHttpClient().newBuilder()
//            .connectTimeout(120, TimeUnit.SECONDS)
//            .readTimeout(120, TimeUnit.SECONDS)
//            .build();
//    
    public Ticket getProcessIdTicket(String ticketNumber) throws SQLException, Exception {
        GetConnections gc = new GetConnections();

        Ticket r = new Ticket();
        StringBuilder query = new StringBuilder();

        query.append("SELECT b.c_customer_segment, b.c_work_zone, b.c_source_ticket, ")
                .append("b.c_finalcheck, b.c_last_state, b.c_service_no, b.c_realm, b.c_scc_result, ")
                .append("b.c_scc_value, b.c_measurement_category, b.c_owner_group, b.c_service_type, b.c_internet_test_result, b.c_qc_voice_ivr_result, ")
                .append("b.c_classification_flag, b.c_action_status, b.c_ticket_status, b.c_child_gamas, c_code_validation, ")
                .append("b.c_owner, b.c_memo, b.c_status, b.c_external_ticketid, b.c_service_address, b.c_name, c_channel, ")
                .append("b.c_description_customerid, b.c_classification_type, b.c_parent_id, b.c_actual_solution, b.c_incident_domain ")
                .append("FROM app_fd_ticket b ")
                .append("WHERE b.c_id_ticket = ? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketNumber);
            result = ps.executeQuery();
            if (result.next()) {
                r.setCust_segment(result.getString("c_customer_segment"));
                r.setWorkzone(result.getString("c_work_zone"));
                r.setSource_ticket(result.getString("c_source_ticket"));
                r.setFinalcheck(result.getString("c_finalcheck"));
                r.setLast_state(result.getString("c_last_state"));
                r.setService_no(result.getString("c_service_no"));
                r.setRealm(result.getString("c_realm"));
                r.setSccResult(result.getString("c_scc_result"));
                r.setSccValue(result.getString("c_scc_value"));
                r.setMeasurementCategory(result.getString("c_measurement_category"));
                r.setOwnerGroup(result.getString("c_owner_group"));
                r.setServiceType(result.getString("c_service_type"));
                r.setQcVoiceIvrResult(result.getString("c_internet_test_result"));
                r.setInternetTestResult(result.getString("c_qc_voice_ivr_result"));
                r.setClassificationFlag(result.getString("c_classification_flag"));
                r.setActionStatus(result.getString("c_action_status"));
                r.setTicketStatus(result.getString("c_ticket_status"));
                r.setChild_gamas(result.getString("c_child_gamas"));
                r.setCodeValidation(result.getString("c_code_validation"));
                r.setOwner(result.getString("c_owner"));
                r.setMemo(result.getString("c_memo"));
                r.setStatus(result.getString("c_status"));
                r.setExtenalTicketId(result.getString("c_external_ticketid"));
                r.setServiceAddress(result.getString("c_service_address"));
                r.setName(result.getString("c_name"));
                r.setChannel(result.getString("c_channel"));
                r.setCustomerName(result.getString("c_description_customerid"));
                r.setClassificationType(result.getString("c_classification_type"));
                r.setId(result.getString("c_parent_id"));
                r.setActualSolution(result.getString("c_actual_solution"));
                r.setIncidentDomain(result.getString("c_incident_domain"));

            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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

            query = null;
            gc = null;
        }
        return r;

    }

    public Ticket getProcessIdTicketWithShk(String ticketNumber) throws SQLException, Exception {
        GetConnections gc = new GetConnections();

        Ticket r = new Ticket();
        StringBuilder query = new StringBuilder();

        query.append(" SELECT a.id, b.id as id_ticket_inc, c.processId, b.c_customer_segment, b.c_work_zone, b.c_source_ticket,  ")
                .append(" b.c_finalcheck, b.c_last_state, b.c_service_no, b.c_realm, b.c_scc_result,  ")
                .append(" b.c_scc_value, b.c_measurement_category, b.c_owner_group, b.c_service_type, b.c_internet_test_result, b.c_qc_voice_ivr_result,  ")
                .append(" b.c_classification_flag, b.c_action_status, b.c_ticket_status, b.c_child_gamas, c_code_validation,  ")
                .append(" b.c_owner, b.c_memo, b.c_status, b.c_external_ticketid, b.c_service_address, c_name, c_channel, c_description_customerid, c_classification_type  ")
                .append(" ,d.id as activity_id, d.ACTIVITYDEFINITIONID, d.PDEFNAME, d.STATE, b.c_actual_solution, b.c_incident_domain ")
                .append(" FROM app_fd_parent_ticket a  ")
                .append(" JOIN app_fd_ticket b ON a.id = b.c_parent_id  ")
                .append(" JOIN wf_process_link c ON a.id = c.originProcessId  ")
                .append(" join SHKACTIVITIES d on c.PROCESSID = d.PROCESSID ")
                .append(" WHERE b.c_id_ticket = ? ")
                .append(" and d.state = '1000003' ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketNumber);
            result = ps.executeQuery();
            if (result.next()) {
                r.setId(result.getString("id"));
                r.setProcessId(result.getString("processId"));
                r.setCust_segment(result.getString("c_customer_segment"));
                r.setWorkzone(result.getString("c_work_zone"));
                r.setSource_ticket(result.getString("c_source_ticket"));
                r.setFinalcheck(result.getString("c_finalcheck"));
                r.setLast_state(result.getString("c_last_state"));
                r.setService_no(result.getString("c_service_no"));
                r.setRealm(result.getString("c_realm"));
                r.setSccResult(result.getString("c_scc_result"));
                r.setSccValue(result.getString("c_scc_value"));
                r.setMeasurementCategory(result.getString("c_measurement_category"));
                r.setOwnerGroup(result.getString("c_owner_group"));
                r.setIdTicketInc(result.getString("id_ticket_inc"));
                r.setServiceType(result.getString("c_service_type"));
                r.setQcVoiceIvrResult(result.getString("c_internet_test_result"));
                r.setInternetTestResult(result.getString("c_qc_voice_ivr_result"));
                r.setClassificationFlag(result.getString("c_classification_flag"));
                r.setActionStatus(result.getString("c_action_status"));
                r.setTicketStatus(result.getString("c_ticket_status"));
                r.setChild_gamas(result.getString("c_child_gamas"));
                r.setCodeValidation(result.getString("c_code_validation"));
                r.setOwner(result.getString("c_owner"));
                r.setMemo(result.getString("c_memo"));
                r.setStatus(result.getString("c_status"));
                r.setExtenalTicketId(result.getString("c_external_ticketid"));
                r.setServiceAddress(result.getString("c_service_address"));
                r.setName(result.getString("c_name"));
                r.setChannel(result.getString("c_channel"));
                r.setCustomerName(result.getString("c_description_customerid"));
                r.setClassificationType(result.getString("c_classification_type"));
                r.setActivityId(result.getString("activity_id"));
                r.setActiviytName(result.getString("ACTIVITYDEFINITIONID"));
                r.setProcessDefId(result.getString("PDEFNAME"));
                r.setState(result.getString("STATE"));
                r.setActualSolution(result.getString("c_actual_solution"));
                r.setIncidentDomain(result.getString("c_incident_domain"));

            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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

            query = null;
            gc = null;
        }
        return r;

    }

    public Ticket getProcessIdTicketWithShkV3(String ticketNumber) throws SQLException, Exception {
        GetConnections gc = new GetConnections();

        Ticket r = new Ticket();
        StringBuilder query = new StringBuilder();

        query
                .append(" SELECT a.id, b.id as id_ticket_inc, c.processId, b.c_customer_segment, b.c_work_zone, b.c_source_ticket,   ")
                .append(" b.c_finalcheck, b.c_last_state, b.c_service_no, b.c_realm, b.c_scc_result,   ")
                .append(" b.c_scc_value, b.c_measurement_category, b.c_owner_group, b.c_service_type, b.c_internet_test_result, b.c_qc_voice_ivr_result,   ")
                .append(" b.c_classification_flag, b.c_action_status, b.c_ticket_status, b.c_child_gamas, c_code_validation,   ")
                .append(" b.c_owner, b.c_memo, b.c_status, b.c_external_ticketid, b.c_service_address, c_name, c_channel, c_description_customerid, c_classification_type   ")
                .append(" ,d.id as activity_id, d.ACTIVITYDEFINITIONID, d.PDEFNAME, d.STATE, b.c_actual_solution, b.c_incident_domain, f.RESOURCEID  ")
                .append(" ,nvl(round((SYSDATE -(FROM_TZ( CAST(DATE '1970-01-01' + (1/24/60/60/1000) * d.ACTIVATED AS TIMESTAMP ), 'Asia/Jakarta') + 7/24)) * 24 * 60), 0)  running_time   ")
                .append(" FROM app_fd_parent_ticket a   ")
                .append(" JOIN app_fd_ticket b ON a.id = b.c_parent_id   ")
                .append(" LEFT JOIN wf_process_link c ON a.id = c.originProcessId   ")
                .append(" LEFT join SHKACTIVITIES d on c.PROCESSID = d.PROCESSID  ")
                .append(" LEFT join SHKASSIGNMENTSTABLE f on d.id = f.ACTIVITYID ")
                .append(" WHERE b.c_id_ticket = ? ")
                .append(" AND d.ACTIVATED IS NOT NULL ")
                .append(" order by d.ACTIVATED desc ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketNumber);
            result = ps.executeQuery();
            if (result.next()) {
                r.setId(result.getString("id"));
                r.setProcessId(result.getString("processId"));
                r.setCust_segment(result.getString("c_customer_segment"));
                r.setWorkzone(result.getString("c_work_zone"));
                r.setSource_ticket(result.getString("c_source_ticket"));
                r.setFinalcheck(result.getString("c_finalcheck"));
                r.setLast_state(result.getString("c_last_state"));
                r.setService_no(result.getString("c_service_no"));
                r.setRealm(result.getString("c_realm"));
                r.setSccResult(result.getString("c_scc_result"));
                r.setSccValue(result.getString("c_scc_value"));
                r.setMeasurementCategory(result.getString("c_measurement_category"));
                r.setOwnerGroup(result.getString("c_owner_group"));
                r.setIdTicketInc(result.getString("id_ticket_inc"));
                r.setServiceType(result.getString("c_service_type"));
                r.setQcVoiceIvrResult(result.getString("c_internet_test_result"));
                r.setInternetTestResult(result.getString("c_qc_voice_ivr_result"));
                r.setClassificationFlag(result.getString("c_classification_flag"));
                r.setActionStatus(result.getString("c_action_status"));
                r.setTicketStatus(result.getString("c_ticket_status"));
                r.setChild_gamas(result.getString("c_child_gamas"));
                r.setCodeValidation(result.getString("c_code_validation"));
                r.setOwner(result.getString("c_owner"));
                r.setMemo(result.getString("c_memo"));
                r.setStatus(result.getString("c_status"));
                r.setExtenalTicketId(result.getString("c_external_ticketid"));
                r.setServiceAddress(result.getString("c_service_address"));
                r.setName(result.getString("c_name"));
                r.setChannel(result.getString("c_channel"));
                r.setCustomerName(result.getString("c_description_customerid"));
                r.setClassificationType(result.getString("c_classification_type"));
                r.setActivityId(result.getString("activity_id"));
                r.setActiviytName(result.getString("ACTIVITYDEFINITIONID"));
                r.setProcessDefId(result.getString("PDEFNAME"));
                r.setState(result.getString("STATE"));
                r.setActualSolution(result.getString("c_actual_solution"));
                r.setIncidentDomain(result.getString("c_incident_domain"));
                r.setResourceId(result.getString("RESOURCEID"));
                r.setRunningTime(result.getString("running_time"));

            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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

            query = null;
            gc = null;
        }
        return r;

    }

    public void getNextActivity(Ticket ticket, String parentId) throws Exception {
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();

        query
                .append(" select d.PROCESSID,e.id as activity_id, e.ACTIVITYDEFINITIONID as activity_name, e.PDEFNAME, e.STATE  ")
                .append(" from wf_process_link d ")
                .append(" join SHKACTIVITIES e on e.PROCESSID = d.PROCESSID ")
                .append(" WHERE 1=1 ")
                .append(" and d.originprocessid = ? ")
                .append(" ORDER BY E.ACTIVATED DESC FETCH FIRST ROWS only ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = con.prepareStatement(query.toString());
            ps.setString(1, parentId);
            result = ps.executeQuery();
            if (result.next()) {

                ticket.setProcessId(result.getString("PROCESSID"));
                ticket.setActivityId(result.getString("activity_id"));
                ticket.setActiviytName(result.getString("activity_name"));
                ticket.setProcessDefId(result.getString("PDEFNAME"));
                ticket.setState(result.getString("STATE"));
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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

            query = null;
            gc = null;
        }
    }

    //sedang di develop
    public Ticket getProcessIdTicketWithShk2(String ticketNumber) throws SQLException, Exception {
        GetConnections gc = new GetConnections();

        Ticket r = new Ticket();
        StringBuilder query = new StringBuilder();

        query.append(" SELECT a.id, b.id as id_ticket_inc, c.processId, b.c_customer_segment, b.c_work_zone, b.c_source_ticket,  ")
                .append(" b.c_finalcheck, b.c_last_state, b.c_service_no, b.c_realm, b.c_scc_result,  ")
                .append(" b.c_scc_value, b.c_measurement_category, b.c_owner_group, b.c_service_type, b.c_internet_test_result, b.c_qc_voice_ivr_result,  ")
                .append(" b.c_classification_flag, b.c_action_status, b.c_ticket_status, b.c_child_gamas, c_code_validation,  ")
                .append(" b.c_owner, b.c_memo, b.c_status, b.c_external_ticketid, b.c_service_address, c_name, c_channel, c_description_customerid, c_classification_type  ")
                .append(" ,d.id as activity_id, d.ACTIVITYDEFINITIONID, d.PDEFNAME, d.STATE ")
                .append(" FROM app_fd_parent_ticket a  ")
                .append(" JOIN app_fd_ticket b ON a.id = b.c_parent_id  ")
                .append(" JOIN wf_process_link c ON a.id = c.originProcessId  ")
                .append(" join SHKACTIVITIES d on c.PROCESSID = d.PROCESSID ")
                .append(" WHERE b.c_id_ticket = ? ")
                .append(" and d.state = '1000003' ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketNumber);
            result = ps.executeQuery();
            if (result.next()) {
                r.setId(result.getString("id"));
                r.setProcessId(result.getString("processId"));
                r.setCust_segment(result.getString("c_customer_segment"));
                r.setWorkzone(result.getString("c_work_zone"));
                r.setSource_ticket(result.getString("c_source_ticket"));
                r.setFinalcheck(result.getString("c_finalcheck"));
                r.setLast_state(result.getString("c_last_state"));
                r.setService_no(result.getString("c_service_no"));
                r.setRealm(result.getString("c_realm"));
                r.setSccResult(result.getString("c_scc_result"));
                r.setSccValue(result.getString("c_scc_value"));
                r.setMeasurementCategory(result.getString("c_measurement_category"));
                r.setOwnerGroup(result.getString("c_owner_group"));
                r.setIdTicketInc(result.getString("id_ticket_inc"));
                r.setServiceType(result.getString("c_service_type"));
                r.setQcVoiceIvrResult(result.getString("c_internet_test_result"));
                r.setInternetTestResult(result.getString("c_qc_voice_ivr_result"));
                r.setClassificationFlag(result.getString("c_classification_flag"));
                r.setActionStatus(result.getString("c_action_status"));
                r.setTicketStatus(result.getString("c_ticket_status"));
                r.setChild_gamas(result.getString("c_child_gamas"));
                r.setCodeValidation(result.getString("c_code_validation"));
                r.setOwner(result.getString("c_owner"));
                r.setMemo(result.getString("c_memo"));
                r.setStatus(result.getString("c_status"));
                r.setExtenalTicketId(result.getString("c_external_ticketid"));
                r.setServiceAddress(result.getString("c_service_address"));
                r.setName(result.getString("c_name"));
                r.setChannel(result.getString("c_channel"));
                r.setCustomerName(result.getString("c_description_customerid"));
                r.setClassificationType(result.getString("c_classification_type"));
                r.setActivityId(result.getString("activity_id"));
                r.setActiviytName(result.getString("ACTIVITYDEFINITIONID"));
                r.setProcessDefId(result.getString("PDEFNAME"));
                r.setState(result.getString("STATE"));

            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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

            query = null;
            gc = null;
        }

        return r;

    }

    public void getApiAttribut() throws SQLException, Exception {
        GetConnections gc = new GetConnections();

//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "select c_api_id, c_api_key, c_api_secret " +
                "from app_fd_tis_mst_api " +
                "where c_use_of_api = 'ticket_incident_api' ";
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                apiId = rs.getString("c_api_id");
                apiKey = rs.getString("c_api_key");
                apiSecret = rs.getString("c_api_secret");
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
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

            query = null;
            gc = null;
        }
    }

    public void updatePendingStatus(String pendingStatus, String id) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        query.append("UPDATE app_fd_ticket SET ")
                .append("c_pending_status = ? ")
                .append("WHERE c_parent_id = ? ");

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());

                ps.setString(1, pendingStatus);
                ps.setString(2, id);
                ps.executeUpdate();
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

            query = null;
            gc = null;
        }
    }

    public void updateSccCodeValidation(String result, String id) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE app_fd_ticket SET ")
                .append("c_scc_code_validation = ? ")
                .append("WHERE c_parent_id = ? ");

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());
                ps.setString(1, result);
                ps.setString(2, id);
                ps.executeUpdate();
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
            gc = null;
            query = null;
        }
    }

    public void updateStatusTicket(String status, String ticketStatus, String id) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE app_fd_ticket SET ")
                .append("c_status = ? ")
                .append(",c_ticket_status = ? ")
                .append("WHERE c_parent_id = ? ");

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());
                ps.setString(1, status);
                ps.setString(2, ticketStatus);
                ps.setString(3, id);
                ps.executeUpdate();
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
            query = null;
            gc = null;
        }
    }

    public void updateStatusTmp(String actionStatus, String runProcess, String owner, String id) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        query.append("UPDATE app_fd_ticket SET ")
                .append("c_action_status = ? ")
                .append(",c_run_process = ? ")
                .append(",c_owner = ? ")
                .append("WHERE c_parent_id = ? ");

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());
                ps.setString(1, actionStatus);
                ps.setString(2, runProcess);
                ps.setString(3, owner);
                ps.setString(4, id);
                ps.executeUpdate();
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
            gc = null;
            query = null;
        }
    }

    public String getSolution(HashMap<String, String> params) {
        String desc = "";
        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("getActSolAndIncDom");
            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");

            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;
                desc = obj.get("description").toString();

            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        }
        return desc;
    }

    public void updateDataBackendCompleted(String ownerGroup, String solutionCode, String solutionDesc, String ticketStatus, String id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query.append("UPDATE app_fd_ticket SET ")
                .append("c_owner_group = ? ")
                .append(",c_solution_code = ?")
                .append(",c_solution_description = ? ")
                .append(",c_last_state = ? ")
                .append("WHERE c_parent_id = ? ");

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());
                ps.setString(1, ownerGroup);
                ps.setString(2, solutionCode);
                ps.setString(3, solutionDesc);
                ps.setString(4, ticketStatus);
                ps.setString(5, id);
                ps.executeUpdate();
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
            gc = null;
            query = null;
        }
    }

    public void insertWorkLogs(String parentId, String idTicket, String ownerGroup, String kategoriUkur) throws Exception {
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO app_fd_ticket_work_logs ")
                .append("(id, c_parentid, dateCreated, c_recordkey, c_class, createdBy, c_ownergroup, c_created_date, c_createdby, c_log_type, c_summary, c_detail) ")
                .append("VALUES (?, ?, sysdate, ?, ?, ?, ?, sysdate, ?, ?, ?, ?) ");
        Connection con = null;
        PreparedStatement ps = null;
        try {

            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());

            String uuid = UuidGenerator.getInstance().getUuid();
            ps.setString(1, uuid);
            ps.setString(2, parentId);
            ps.setString(3, idTicket);
            ps.setString(4, "INCIDENT");
            ps.setString(5, "BY_SYSTEM");
            ps.setString(6, ownerGroup);
            ps.setString(7, "BY_SYSTEM");
            ps.setString(8, "AGENTNOTE");
            ps.setString(9, "Hasil pengukuran ibooster " + kategoriUkur);
            ps.setString(10, "");
            ps.executeUpdate();

        } catch (SQLException e) {
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
            gc = null;
            query = null;
        }

    }

    public void updatePending(String pendingTimeOut, String id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        GetConnections gc = new GetConnections();
        String query = "UPDATE app_fd_ticket SET c_pen_timeout = ? WHERE c_parent_id = ? ";

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query);
                ps.setTimestamp(1, Timestamp.valueOf(pendingTimeOut));
                ps.setString(2, id);
                ps.executeUpdate();
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
            gc = null;
            query = null;
        }
    }

    public void updateOwnerGroup(String ownerGroup, String id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        GetConnections gc = new GetConnections();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "UPDATE app_fd_ticket SET c_owner_group = ? WHERE c_parent_id = ? ";

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query);
                ps.setString(1, ownerGroup);
                ps.setString(2, id);
                ps.executeUpdate();
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
            gc = null;
            query = null;
        }
    }

    public void updateOwner(String owner, String id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        GetConnections gc = new GetConnections();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "UPDATE app_fd_ticket SET c_owner_group = ? WHERE c_parent_id = ? ";

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query);
                ps.setString(1, owner);
                ps.setString(2, id);
                ps.executeUpdate();
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
            gc = null;
            query = null;
        }
    }

    public void updateIbooster(ListIbooster ib, String id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        GetConnections gc = new GetConnections();
        StringBuilder iboosterResult = new StringBuilder();
        if ("".equals(ib.getMessage())) {
            iboosterResult.append("Kategori Ukur : " + ib.getMeasurementCategory() + "\r");
            iboosterResult.append("oper_status : " + ib.getOperStatus() + "\r");
            iboosterResult.append("onu_rx_pwr : " + ib.getOnuRxPwr() + "\r");
            iboosterResult.append("onu_tx_pwr : " + ib.getOnuTxPwr() + "\r");
            iboosterResult.append("olt_rx_pwr : " + ib.getOltRxPwr() + "\r");
            iboosterResult.append("olt_tx_pwr : " + ib.getOltTxPwr() + "\r");
            iboosterResult.append("fiber_length : " + ib.getFiberLength() + "\r");
            iboosterResult.append("status_jaringan : " + ib.getStatusJaringan() + "\r");
            iboosterResult.append("identifier : " + ib.getIdentifier() + "\r");
        } else {
            iboosterResult.append(ib.getMessage());
        }
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String waktuUkur = (dateFormat2.format(new Date()).toString()).toLowerCase();
        StringBuilder query;
        if ("".equals(ib.getMessage())) {
            query = new StringBuilder();
            query.append("UPDATE app_fd_ticket SET  ")
                    .append("c_measurement_time = ?,  ")
                    .append("c_ibooster_result = ?,   ")
                    .append("c_mycx_result = ?,   ")
                    .append("c_mycx_category_result = ?,   ")
                    .append("c_measurement_category = ?, ")
                    .append("c_id_pengukuran = CONCAT(CONCAT(?, '|'),NVL(c_id_pengukuran, ' ')),   ")
                    .append("c_hostname_olt = CONCAT(CONCAT(?, '|'),NVL(c_hostname_olt, ' ')),  ")
                    .append("c_ip_olt = CONCAT(CONCAT(?, '|'),NVL(c_ip_olt, ' ')),   ")
                    .append("c_frame = CONCAT(CONCAT(?, '|'),NVL(c_frame, ' ')), ")
                    .append("c_olt_tx = CONCAT(CONCAT(?, '|'),NVL(c_olt_tx, ' ')),   ")
                    .append(".append(c_olt_rx = CONCAT(CONCAT(?, '|'),NVL(c_olt_rx, ' ')),   ")
                    .append("c_onu_tx = CONCAT(CONCAT(?, '|'),NVL(c_onu_tx, ' ')),   ")
                    .append("c_onu_rx = CONCAT(CONCAT(?, '|'),NVL(c_onu_rx, ' ')),   ")
                    .append("c_status_ont = CONCAT(CONCAT(?, '|'),NVL(c_status_ont, ' ')) ")
                    .append("WHERE c_parent_id = ? ");
        } else {
            query = new StringBuilder();
            query.append("UPDATE app_fd_ticket SET ")
                    .append("c_measurement_time = ?, ")
                    .append("c_ibooster_result = ?,  ")
                    .append("c_measurement_category = ?  ")
                    .append("WHERE c_parent_id = ?");
        }

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());
                if ("".equals(ib.getMessage())) {
                    ps.setTimestamp(1, Timestamp.valueOf(waktuUkur));
                    ps.setString(2, iboosterResult.toString());
                    ps.setString(3, "-");
                    ps.setString(4, "-");
                    ps.setString(5, ib.getMeasurementCategory());
                    ps.setString(6, ib.getIdUkur());
                    ps.setString(7, ib.getIdentifier());
                    ps.setString(8, ib.getNasIp() + " | " + ib.getHostname());
                    ps.setString(9, ib.getClid());
                    ps.setString(10, ib.getOltTxPwr());
                    ps.setString(11, ib.getOltRxPwr());
                    ps.setString(12, ib.getOnuTxPwr());
                    ps.setString(13, ib.getOnuRxPwr());
                    ps.setString(14, ib.getStatusCpe() + " | " + ib.getOperStatus());
                    ps.setString(15, id);
                } else {
                    ps.setTimestamp(1, Timestamp.valueOf(waktuUkur));
                    ps.setString(2, iboosterResult.toString());
                    ps.setString(3, ib.getMeasurementCategory());
                    ps.setString(4, id);
                }
                ps.executeUpdate();
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
            gc = null;
            query = null;
        }
    }

    public String getOwnerGroup(HashMap<String, String> params) {
        String data = "";
        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("list_tk_mapping");
            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");

            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;
                data = obj.get("person_owner_group").toString();
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        }
        return data;
    }

    public boolean updateTicketParam(JSONObject dataUpdate, String id) {
        boolean result = false;
        Connection con = null;
        PreparedStatement ps = null;
        StringBuilder sbQuery = new StringBuilder();
        ParseDate parsingDate = new ParseDate();
        GetConnections gc = new GetConnections();
        sbQuery.append("UPDATE app_fd_ticket SET ");
        for (Object paramKey : dataUpdate.keySet()) {
            sbQuery.append("c_" + paramKey + " = ?, ");
        }
        String query = (sbQuery.deleteCharAt(sbQuery.length() - 2).toString() + " where c_parent_id = ? ");
        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query);
                int i = 1;
                for (Object valParam : dataUpdate.keySet()) {

                    if ("reported_date".equalsIgnoreCase(valParam.toString()) ||
                            "pending_timeout".equalsIgnoreCase(valParam.toString()) ||
                            "scheduled_start".equalsIgnoreCase(valParam.toString()) ||
                            "actual_start".equalsIgnoreCase(valParam.toString()) ||
                            "qc_internet_time".equalsIgnoreCase(valParam.toString()) ||
                            "measurement_time".equalsIgnoreCase(valParam.toString()) ||
                            "stop_time".equalsIgnoreCase(valParam.toString()) ||
                            "qc_voice_ivr_time".equalsIgnoreCase(valParam.toString()) ||
                            "tsc_time".equalsIgnoreCase(valParam.toString()) ||
                            "scheduled_finish".equalsIgnoreCase(valParam.toString())) {
                        String sourceDate = dataUpdate.get(valParam).toString();
                        String dateFormated = parsingDate.parse(sourceDate);
                        ps.setTimestamp(i, Timestamp.valueOf(dateFormated));
                    } else {
                        ps.setString(i, dataUpdate.get(valParam).toString());
                    }

                    i++;
                }
                ps.setString(i, id);

                int r = ps.executeUpdate();
                if (r > 0) {
                    result = true;
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
            errorUpdateData = e.getMessage();
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
            query = null;
            gc = null;

        }
        return result;
    }

    public boolean updateTicketParamGlobal(JSONObject dataUpdate, Ticket dataTicket, String id) throws Exception {

        if (!"".equals((dataUpdate.get("pending_timeout") == null) ? "" : dataUpdate.get("pending_timeout").toString())) {
            dataUpdate.put("pen_timeout", dataUpdate.get("pending_timeout"));
        }
        if (!"".equals((dataUpdate.get("scc_result") == null ? "" : dataUpdate.get("scc_result").toString()))) {

            String idFieldReslut = "qc_voice_ivr_result";
            String idFieldTime = "qc_voice_ivr_time";
            if ("INTERNET".equalsIgnoreCase(dataTicket.getServiceType()) || "IPTV".equalsIgnoreCase(dataTicket.getServiceType())) {
                idFieldReslut = "internet_test_result";
                idFieldTime = "qc_internet_time";
            }
            dataUpdate.put(idFieldReslut, dataUpdate.get("scc_result"));
            dataUpdate.put(idFieldTime, dataUpdate.get("scc_time") == null ? "" : dataUpdate.get("scc_time").toString());
        }
        if (!"".equalsIgnoreCase(dataUpdate.get("tsc_result") == null ? "" : dataUpdate.get("tsc_result").toString())) {
            dataUpdate.put("tsc_result", dataUpdate.get("tsc_result"));
            dataUpdate.put("tsc_time", dataUpdate.get("tsc_time") == null ? "" : dataUpdate.get("tsc_time").toString());
        }

        if (!"".equalsIgnoreCase(dataUpdate.get("closed_by") == null ? "" : dataUpdate.get("closed_by").toString())) {
            dataUpdate.put("description_closed_by", updateParamDescriptionGlobal("CLOSEDBY", dataUpdate.get("closed_by").toString()));
        }

        if (!"".equals((dataUpdate.get("actual_solution") == null) ? "" : dataUpdate.get("actual_solution").toString())) {
            HashMap<String, String> paramsActSolution = new HashMap<String, String>();
            paramsActSolution.put("hierarchy_type", "ACTSOL");
            paramsActSolution.put("classification_code", dataUpdate.get("actual_solution").toString());
            String actSol = getActSolution(paramsActSolution);
            dataUpdate.put("description_actualsolution", actSol);
        }
        if (!"".equalsIgnoreCase((dataUpdate.get("solution_code") == null) ? "" : dataUpdate.get("solution_code").toString())) {
            HashMap<String, String> paramsSymptom = new HashMap<String, String>();
            paramsSymptom.put("hierarchy_type", "SOLUTION");
            paramsSymptom.put("classification_code", dataUpdate.get("solution_code").toString());
            String descSolution = getSolution(paramsSymptom);
            dataUpdate.put("solution_description", descSolution);

        }

        boolean result = false;
        Connection con = null;
        PreparedStatement ps = null;
        StringBuilder sbQuery = new StringBuilder();
        ParseDate parsingDate = new ParseDate();
        GetConnections gc = new GetConnections();
        sbQuery.append("UPDATE app_fd_ticket SET ");
        for (Object paramKey : dataUpdate.keySet()) {
            sbQuery.append("c_" + paramKey + " = ?, ");
        }
        String query = (sbQuery.deleteCharAt(sbQuery.length() - 2).toString() + " where c_parent_id = ? ");
        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query);
                int i = 1;
                for (Object valParam : dataUpdate.keySet()) {

                    if ("reported_date".equalsIgnoreCase(valParam.toString()) ||
                            "pending_timeout".equalsIgnoreCase(valParam.toString()) ||
                            "scheduled_start".equalsIgnoreCase(valParam.toString()) ||
                            "actual_start".equalsIgnoreCase(valParam.toString()) ||
                            "qc_internet_time".equalsIgnoreCase(valParam.toString()) ||
                            "measurement_time".equalsIgnoreCase(valParam.toString()) ||
                            "stop_time".equalsIgnoreCase(valParam.toString()) ||
                            "qc_voice_ivr_time".equalsIgnoreCase(valParam.toString()) ||
                            "tsc_time".equalsIgnoreCase(valParam.toString()) ||
                            "scheduled_finish".equalsIgnoreCase(valParam.toString())) {
                        String sourceDate = dataUpdate.get(valParam).toString();
                        String dateFormated = parsingDate.parse(sourceDate);
                        ps.setTimestamp(i, Timestamp.valueOf(dateFormated));
                    } else {
                        ps.setString(i, dataUpdate.get(valParam).toString());
                    }

                    i++;
                }
                ps.setString(i, id);

                int r = ps.executeUpdate();
                if (r > 0) {
                    result = true;
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
            errorUpdateData = e.getMessage();
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
            query = null;
            gc = null;

        }
        return result;
    }

    private String getToken() {
        String token = "";
        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("get_access_token");

            RequestBody formBody = new FormBody.Builder()
                    .add("grant_type", "client_credentials")
                    .add("client_id", apiConfig.getClientId())
                    .add("client_secret", apiConfig.getClientSecret())
                    .build();
            String response = "";
            response = callApi.sendPostTokenIbooster(apiConfig, formBody);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            token = data_obj.get("access_token").toString();

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
        }
        return token;
    }

    public ListIbooster getIbooster(String nd, String realm) {
        String tokenIbooster = getToken();
        ListIbooster data = new ListIbooster();
        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("get_ibooster");

            RequestBody formBody = new FormBody.Builder()
                    .add("nd", nd)
                    .add("realm", realm)
                    .build();

            String response = "";
            response = callApi.sendPostIbooster(apiConfig, formBody, tokenIbooster);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);

            String message = (data_obj.get("MESSAGE") == null ? "" : data_obj.get("MESSAGE").toString());

            if ("".equals(message)) {
                data.setOperStatus(data_obj.get("oper_status") == null ? "" : data_obj.get("oper_status").toString());
                data.setOnuRxPwr(data_obj.get("onu_rx_pwr") == null ? "" : data_obj.get("onu_rx_pwr").toString());
                data.setOnuTxPwr(data_obj.get("onu_tx_pwr") == null ? "" : data_obj.get("onu_tx_pwr").toString());
                data.setOltRxPwr(data_obj.get("olt_rx_pwr") == null ? "" : data_obj.get("olt_rx_pwr").toString());
                data.setOltTxPwr(data_obj.get("olt_tx_pwr") == null ? "" : data_obj.get("olt_tx_pwr").toString());
                data.setFiberLength(data_obj.get("fiber_length") == null ? "" : data_obj.get("fiber_length").toString());
                data.setStatusJaringan(data_obj.get("status_jaringan") == null ? "" : data_obj.get("status_jaringan").toString());
                data.setIdentifier(data_obj.get("identifier") == null ? "" : data_obj.get("identifier").toString());
                data.setIdUkur(data_obj.get("id_ukur") == null ? "" : data_obj.get("id_ukur").toString());
                data.setNasIp(data_obj.get("nas_ip") == null ? "" : data_obj.get("nas_ip").toString());
                data.setHostname(data_obj.get("hostname") == null ? "" : data_obj.get("hostname").toString());
                data.setClid(data_obj.get("clid") == null ? "" : data_obj.get("clid").toString());
                data.setStatusCpe(data_obj.get("status_cpe") == null ? "" : data_obj.get("status_cpe").toString());
                data.setMeasurementTime(data_obj.get("session_start") == null ? "" : data_obj.get("session_start").toString());
                data.setMessage(data_obj.get("MESSAGE") == null ? "" : data_obj.get("MESSAGE").toString());
            } else {
                data.setMessage(data_obj.get("MESSAGE") == null ? "" : data_obj.get("MESSAGE").toString());
            }

            double onuRxPwr = Double.valueOf((data_obj.get("onu_rx_pwr") == null ? "0" : data_obj.get("onu_rx_pwr").toString()));
            String measurementCategory = "";

            if (onuRxPwr <= -13 && onuRxPwr >= -24) {
                measurementCategory = "SPEC";
            } else {
                measurementCategory = "UNSPEC";
            }

            data.setMeasurementCategory(measurementCategory);

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        }
        return data;
    }

    public String updateParamDescription(String paramName, String paramCode, String idField, String parentId) throws Exception {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        String desc = "";
        String query = "SELECT c_param_description FROM app_fd_param WHERE c_param_name = ? AND c_param_code = ? ";
        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query);

            ps.setString(1, paramName);
            ps.setString(2, paramCode);
            try {
                result = ps.executeQuery();
                if (result.next()) {
                    desc = result.getString("c_param_description");
                    updateDescription(idField, desc, parentId);
                }

            } catch (SQLException e) {
                LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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
            query = null;
            gc = null;
        }
        return desc;

    }

    public String updateParamDescriptionGlobal(String paramName, String paramCode) throws Exception {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        String desc = "";
        String query = "SELECT c_param_description FROM app_fd_param WHERE c_param_name = ? AND c_param_code = ? ";
        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query);

            ps.setString(1, paramName);
            ps.setString(2, paramCode);
            try {
                result = ps.executeQuery();
                if (result.next()) {
                    desc = result.getString("c_param_description");
                }

            } catch (SQLException e) {
                LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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
            query = null;
            gc = null;
        }
        return desc;

    }

    public boolean cekDataOnMasterParam(String paramCode, String paramName) throws Exception {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        boolean resData = false;
        String query = "SELECT c_param_code FROM app_fd_param WHERE c_param_name = ? AND c_param_code = ? ";
        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query);

            ps.setString(1, paramName);
            ps.setString(2, paramCode);
            try {
                result = ps.executeQuery();
                if (result.next()) {
                    resData = true;
                }

            } catch (SQLException e) {
                LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
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
            query = null;
            gc = null;
        }
        return resData;

    }

    public void updateDescription(String idField, String description, String parentId) throws Exception {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE app_fd_ticket SET ")
                .append(idField).append(" = ? ")
                .append("WHERE c_parent_id = ? ");

        try {

            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, description);
            ps.setString(2, parentId);
            ps.executeUpdate();

        } catch (SQLException e) {
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
            gc = null;
        }
    }

    public void updateSccResult(String serviceType, String sccResult, String sccTime, String parentId) {
        GetConnections gc = new GetConnections();
        ParseDate parsingDate = new ParseDate();
        Connection con = null;
        PreparedStatement ps = null;

        String idFieldReslut = "c_qc_voice_ivr_result";
        String idFieldTime = "c_qc_voice_ivr_time";
        String dateFormated = parsingDate.parse(sccTime);

        if ("INTERNET".equalsIgnoreCase(serviceType) || "IPTV".equalsIgnoreCase(serviceType)) {
            idFieldReslut = "c_internet_test_result";
            idFieldTime = "c_qc_internet_time";
        }

        StringBuilder query = new StringBuilder();
        query.append("UPDATE app_fd_ticket SET ")
                .append(idFieldReslut).append(" = ? , ").append(idFieldTime).append(" = ? ")
                .append("WHERE c_parent_id = ? ");

        try {

            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());

            ps.setString(1, sccResult);
            ps.setTimestamp(2, Timestamp.valueOf(dateFormated));
            ps.setString(3, parentId);
            ps.executeUpdate();

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
            gc = null;
        }
    }

    public void updateTscResult(String tscResult, String tscTime, String parentId) throws Exception {
        GetConnections gc = new GetConnections();
        ParseDate parsingDate = new ParseDate();
        Connection con = null;
        PreparedStatement ps = null;

        String dateFormated = parsingDate.parse(tscTime);
        StringBuilder query = new StringBuilder();
        query.append("UPDATE app_fd_ticket SET ")
                .append(" c_tsc_result = ? , c_tsc_time = ? ")
                .append("WHERE c_parent_id = ? ");

        try {

            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());

            ps.setString(1, tscResult);
            ps.setTimestamp(2, Timestamp.valueOf(dateFormated));
            ps.setString(3, parentId);
            ps.executeUpdate();

        } catch (SQLException e) {
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
            gc = null;
        }
    }

    public String getActSolution(HashMap<String, String> params) {
        String result = "";
        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("getActSolAndIncDom");
            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");
            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;

                result = obj.get("description").toString();
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        }
        return result;
    }

    public void updateStatusWo(String idTicket, String parentId) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "UPDATE app_fd_tis_work_order SET c_status_wo_number = 'COMPLETED' WHERE c_id_ticket = ? AND c_parent_id = ? ";

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query);
                ps.setString(1, idTicket);
                ps.setString(2, parentId);
                ps.executeUpdate();
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
            query = null;
            gc = null;
        }
    }

    public String getStatusWo(HashMap<String, String> params) {

        StringBuilder sb = new StringBuilder();
        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("list_work_order");

            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");
            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;

                sb.append(";").append(obj.get("status") == null ? "" : obj.get("status").toString());

            }

        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "Error Call API :" + e.getMessage());
        }
        return sb.toString();
    }

    public boolean UpdateStatus(String status, String ticketStatus, String saveStatus, String originProcessId) throws SQLException, Exception {

        boolean result = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query;
        try {
            con = ds.getConnection();
            query = new StringBuilder();
            query
                    .append(" UPDATE app_fd_ticket ")
                    .append(" SET c_status = ? ")
                    .append(" , c_ticket_status = ? ")
                    .append(" , c_save_status = ? ")
                    .append(" WHERE c_parent_id = ? ");
            ps = con.prepareStatement(query.toString());
            ps.setString(1, status);
            ps.setString(2, ticketStatus);
            ps.setString(3, saveStatus);
            ps.setString(4, originProcessId);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "error : " + e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "error : " + e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "error : " + e.getMessage());
            }
            query = null;
        }

        return result;

    }

}
