package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.ParseDate;
import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.ListServiceInformationNew;
import id.co.itasoft.telkom.oss.plugin.model.ListSymptom;
import id.co.itasoft.telkom.oss.plugin.model.ListTkMapping;
import id.co.itasoft.telkom.oss.plugin.model.TicketAutomationModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mtaup
 */
public class TicketAutomationDao {

    CallRestAPI callApi = new CallRestAPI();
    GetConnections gc = new GetConnections();

    public String apiId = "";
    public String apiKey = "";
    public String apiSecret = "";
    public String existing_ticketId = "";
    public String existing_serviceIid = "";
    public String existing_Ticketstatus = "";
    public String existing_classificationFlag = "";
    public String existing_device = "";
    GetMasterParamDao paramDao = new GetMasterParamDao();

    public String getTicketIdFunction() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String ticketId = "";
        StringBuilder query;

        try {
            query = new StringBuilder();

            query.append(" SELECT  GET_INCIDENT_ID as ticket_id from dual ");

            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                ticketId = rs.getString("ticket_id");
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "ERROR GET TICKET ID :" + ex.getMessage());
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
        }
        return ticketId;
    }

    public void getApiAttribut() throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        query.append("select c_api_id, c_api_key, c_api_secret ")
                .append("from app_fd_tis_mst_api ")
                .append("where c_use_of_api = 'ticket_incident_api' ");

        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
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
        }

    }

    public boolean checkTicket(String serviceId, String classificationFlag) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        boolean result = true;

        query.append("SELECT c_id_ticket, c_service_id, c_classification_flag, c_ticket_status ")
                .append("FROM app_fd_ticket ")
                .append("WHERE c_service_id = ? ")
                .append("AND c_classification_flag = ? ")
                .append("AND c_ticket_status != 'CLOSED' ");

        try {
            con = gc.getJogetConnection();

            ps = con.prepareStatement(query.toString());
            ps.setString(1, serviceId);
            ps.setString(2, classificationFlag);

            rs = ps.executeQuery();

            if (rs.next()) {
                result = false;
                existing_ticketId = rs.getString("c_id_ticket");
                existing_serviceIid = rs.getString("c_service_id");
                existing_Ticketstatus = rs.getString("c_ticket_status");
                existing_classificationFlag = rs.getString("c_classification_flag");
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
        }

        return result;

    }

    public boolean checkTicketGamas(String perangkat) throws Exception {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        boolean result = true;
        query.append("SELECT c_id_ticket, c_service_id, c_classification_flag, c_ticket_status , c_perangkat ")
                .append("FROM app_fd_ticket ")
                .append("WHERE c_perangkat = ? ")
                .append("AND c_ticket_status != 'CLOSED' ");

        try {
            con = gc.getJogetConnection();

            ps = con.prepareStatement(query.toString());
            ps.setString(1, perangkat);

            rs = ps.executeQuery();

            if (rs.next()) {
                result = false;
                existing_ticketId = rs.getString("c_id_ticket");
                existing_serviceIid = rs.getString("c_service_id");
                existing_Ticketstatus = rs.getString("c_ticket_status");
                existing_classificationFlag = rs.getString("c_classification_flag");
                existing_device = rs.getString("c_perangkat");
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
        }

        return result;

    }

    public ListServiceInformationNew getListServiceInformation(HashMap<String, String> params) {

        ListServiceInformationNew data = new ListServiceInformationNew();

        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("list_service_information_custom");
            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);
            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");
            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;
                data.setServiceId((obj.get("service_id") == null ? "" : obj.get("service_id").toString()));
                data.setServiceNumber(obj.get("service_number").toString());
                data.setWorkzone(obj.get("workzone").toString());
                data.setWorkzoneName(obj.get("workzone_name").toString());
                data.setServiceCategory((obj.get("service_category") == null ? "" : obj.get("service_category").toString()));
                data.setCustomerID(obj.get("customer_id").toString());
                data.setCustomerCategory(obj.get("customer_category").toString());
                data.setCustomerSegment(obj.get("customer_segment").toString());
                data.setCustomerName(obj.get("customer_name").toString());
                data.setCustomerType((obj.get("customer_type") == null ? "" : obj.get("customer_type").toString()));
                data.setWitel(obj.get("witel").toString());
                data.setReferenceNumber(obj.get("reference_number").toString());
                data.setRegion(obj.get("region").toString());
                data.setCustomerPriority(obj.get("customer_priority").toString());
                data.setServiceType((obj.get("service_type") == null ? "" : obj.get("service_type").toString()));
                data.setAddressCode((obj.get("address_code") == null ? "" : obj.get("address_code").toString()));
                data.setLongitude(obj.get("longitude").toString());
                data.setServicePackage(obj.get("service_package").toString());
                data.setStatus(obj.get("status").toString());
                data.setCity(obj.get("city").toString());
                data.setLatitude(obj.get("latitude").toString());
                data.setPiloting(obj.get("piloting").toString());
                data.setStreetAddress(obj.get("street_address").toString());
                data.setServiceAddress((obj.get("service_address") == null ? "" : obj.get("service_address").toString()));
                data.setServiceIdDescription((obj.get("service_id_description") == null ? "" : obj.get("service_id_description").toString()));
                data.setPostalCode(obj.get("postal_code").toString());
                data.setCountry(obj.get("country").toString());
//piloting
            }

        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "Error Call API :" + e.getMessage());
        }
        return data;
    }

    public ListSymptom getListSymptom(HashMap<String, String> params) {
        ListSymptom data = new ListSymptom();
        try {

            ApiConfig apiConfig = new ApiConfig();
            apiConfig = paramDao.getUrl("list_symptomp");
            String response = "";
            response = callApi.sendGetWithoutToken(apiConfig, params);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");

            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;
                // LogUtil.info(this.getClass().getName(), obj.get("classification_code").toString());
                data.setParent(obj.get("parent").toString());
                data.setCreatedByName(obj.get("createdByName").toString());
                data.setAutoBackend(obj.get("auto_backend").toString());
                data.setClassificationFlag(obj.get("classification_flag").toString());
                data.setFinalCheck(obj.get("final_check").toString());
                data.setClassificationCode(obj.get("classification_code").toString());
                data.setDescription(obj.get("description").toString());
                data.setDateModified(obj.get("dateModified").toString());
                data.setClassificationDescription(obj.get("classification_description").toString());
                data.setDateCreated(obj.get("dateCreated").toString());
                data.setModifiedByName(obj.get("modifiedByName").toString());
                data.setSolution(obj.get("solution").toString());
                data.setClassificationType(obj.get("classification_type").toString());
                data.setDispatchClassification(obj.get("dispatch_classification").toString());
                data.setHierarchyType(obj.get("hierarchy_type").toString());

            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        }
        return data;
    }

    public ListTkMapping getListTkMapping(HashMap<String, String> params) {

        ListTkMapping data = new ListTkMapping();
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
                data.setCreatedByName(obj.get("createdByName").toString());
                data.setDateCreated(obj.get("dateCreated").toString());
                data.setModifiedByName(obj.get("modifiedByName").toString());
                data.setFinalCheck(obj.get("final_check").toString());
                data.setPersonOwnerGroup(obj.get("person_owner_group").toString());
                data.setWorkzone(obj.get("workzone").toString());
                data.setDescription(obj.get("description").toString());
                data.setClassificationId(obj.get("classification_id").toString());
                data.setDateModified(obj.get("dateModified").toString());
                data.setCustomerSegment(obj.get("customer_segment").toString());
                data.setProductName(obj.get("product_name").toString());

                if (!"".equals(obj.get("person_owner_group").toString()) && obj.get("person_owner_group") != null) {
                    break;
                }
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        }
        return data;
    }

    public void insertToTableParentTicket(String originProcessId, String childId) throws Exception {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "INSERT INTO app_fd_parent_ticket (id, dateCreated, c_child_id) VALUES (?,sysdate,?) ";

        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query);
            ps.setString(1, originProcessId);
            ps.setString(2, childId);
            ps.executeUpdate();

        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Error : " + e.getMessage());
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
        }
    }

    /*
    Perubahan Jum'at 16-09-2022
     */
    public boolean insertToTableTicket(ListServiceInformationNew lsi, ListTkMapping tkMapping, ListSymptom symptom, String OriginProcessId,
            String ticketNumber, String childId, String status, String ticketStatus, String reportedDate, String cutSegment, String workZone,
            String lastState, TicketAutomationModel ticket) throws Exception {

        String classificationFlag = symptom.getClassificationFlag();
        if (classificationFlag.contains("_")) {
            classificationFlag = classificationFlag.substring(0, classificationFlag.indexOf("_"));
        }

        String cont_symptom = lsi.getServiceType() + " | " + classificationFlag + " | " + symptom.getClassificationDescription();

        Connection con = null;
        PreparedStatement ps = null;

        boolean result = false;
        ParseDate parsingDate = new ParseDate();
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO  app_fd_ticket ( " )
                .append("id, c_service_no, c_slg, c_work_zone, c_customer_id, " )
                .append("c_customer_category, c_customer_segment, c_customer_type, c_witel, c_reference_number, " )
                .append("c_id_ticket, c_parent_id, c_service_id, c_description_customerid, c_region, " )
                .append("c_customer_priority, c_service_type, c_status, c_ticket_status, c_class_description, " )
                .append("c_owner_group, c_classification_type, c_classification_path, c_auto_backend, c_description_channel, " )
                .append("c_finalcheck, c_actual_solution, c_perangkat, c_street_address, c_street_no, " )
                .append("c_city, c_district, c_service_address,c_classification_flag " )
                //param
                .append(",c_booking_id, c_priority, c_description_closed_by, c_gaul, c_hard_complaint, " )
                .append("c_description_hard_complaint, c_lapul, c_urgensi, c_description_urgensi, c_tipe_stb, " )
                .append("c_external_ticketid, c_mycx_result, c_technology, c_package, c_mycx_category_result, " )
                .append("c_cluster_id, c_keterangan, c_tipe_ont, c_id_pengukuran, c_hostname_olt, " )
                .append("c_ip_olt, c_frame, c_olt_tx, c_olt_rx, c_onu_tx, " )
                .append("c_onu_rx, c_flag_fcr, c_status_covid19, c_source_lockdown, c_estimation, " )
                .append("c_scid, c_guarante_status, c_otp_Landing_page_tracking, c_status_ont, c_ip_bras, " )
                .append("c_sn_ont, c_jenis_byod, c_sn_byod, c_mitra_byod, c_start_guarantee_byod, " )
                .append("c_flag_pdd, c_ibooster_alert_id, c_flag_manja_by_cust, c_details, c_contact_name," )
                .append("c_contact_phone, c_contact_email, c_summary, c_source_ticket, c_channel, " )
                .append("c_reported_by, c_ossid, c_closed_by" )
                .append(", c_reported_date, c_qc_voice_ivr_time, c_stop_time, c_solution , c_subsidiary, c_created_ticket_by, " )
                .append("c_solution_code, c_last_state, c_child_gamas, c_rk_information, c_service_ps_date, c_billing_status, c_realm, " )
                .append("c_impact, c_service_category, c_description_work_zone, c_description_serviceid, c_latitude, c_longitude, c_zip_code, c_country, " )
                .append("c_symptom " )
                .append(",dateCreated, dateModified ) " )
                .append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" )
                .append(",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" )
                .append(",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" )
                .append(",sysdate,sysdate) ");

        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, childId);
            ps.setString(2, lsi.getServiceNumber());
            ps.setString(3, lsi.getServiceCategory());
            ps.setString(4, workZone);
            ps.setString(5, lsi.getCustomerID());
            ps.setString(6, ((ticket.getCustomer_category() == null || "".equals(ticket.getCustomer_category())) ? lsi.getCustomerCategory() : ticket.getCustomer_category()));
            ps.setString(7, cutSegment);
            ps.setString(8, ((ticket.getCustomer_type() == null || "".equals(ticket.getCustomer_type())) ? lsi.getCustomerType() : ticket.getCustomer_type()));
            ps.setString(9, lsi.getWitel());
            ps.setString(10, lsi.getReferenceNumber());
            ps.setString(11, ticketNumber);
            ps.setString(12, OriginProcessId);
            ps.setString(13, lsi.getServiceId());
            ps.setString(14, lsi.getCustomerName());
            ps.setString(15, lsi.getRegion());
            ps.setString(16, lsi.getCustomerPriority());
            ps.setString(17, lsi.getServiceType());
            ps.setString(18, status);
            ps.setString(19, ticketStatus);
            ps.setString(20, symptom.getDescription());
            ps.setString(21, (tkMapping.getPersonOwnerGroup() == null ? "" : tkMapping.getPersonOwnerGroup()));
            ps.setString(22, symptom.getClassificationType());
            ps.setString(23, symptom.getClassificationCode());
            ps.setString(24, symptom.getAutoBackend());
            ps.setString(25, "");
            ps.setString(26, (symptom.getFinalCheck() == null ? "" : symptom.getFinalCheck()));
            ps.setString(27, (ticket.getActual_solution()));
            ps.setString(28, (ticket.getPerangkat()));
            ps.setString(29, lsi.getStreetAddress());
            ps.setString(30, "");
            ps.setString(31, lsi.getCity());
            ps.setString(32, "");
            ps.setString(33, lsi.getServiceAddress());
            ps.setString(34, symptom.getClassificationFlag());

            ps.setString(35, (ticket.getBooking_id()));
            ps.setString(36, (ticket.getPriority()));
            ps.setString(37, "");
            ps.setString(38, (ticket.getGaul()));
            ps.setString(39, (ticket.getHard_complaint()));
            ps.setString(40, "");
            ps.setString(41, (ticket.getLapul()));
            ps.setString(42, (ticket.getUrgensi()));
            ps.setString(43, "");
            ps.setString(44, (ticket.getTipe_stb()));
            ps.setString(45, (ticket.getExternal_ticketid()));
            ps.setString(46, (ticket.getHasil_ukur_mycx()));
            ps.setString(47, (ticket.getTechnology()));
            ps.setString(48, (ticket.getPackages()));
            ps.setString(49, (ticket.getKategory_hasil_ukur_mycx()));
            ps.setString(50, (ticket.getCluster_id()));
            ps.setString(51, (ticket.getKeterangan_clustername()));
            ps.setString(52, (ticket.getTipe_ont()));
            ps.setString(53, (ticket.getId_pengukuran()));
            ps.setString(54, (ticket.getHostname_olt()));
            ps.setString(55, (ticket.getIp_olt()));
            ps.setString(56, (ticket.getFrame_slot_port_onuid()));
            ps.setString(57, (ticket.getOlt_tx()));
            ps.setString(58, (ticket.getOlt_rx()));
            ps.setString(59, (ticket.getOnu_tx()));
            ps.setString(60, (ticket.getOnu_rx()));
            ps.setString(61, (ticket.getFlag_fcr()));
            ps.setString(62, (ticket.getStatus_covid_19()));
            ps.setString(63, (ticket.getSource_lockdown()));
            ps.setString(64, (ticket.getEstimation()));
            ps.setString(65, (ticket.getScid()));
            ps.setString(66, (ticket.getGuarantee_non_guarantee()));
            ps.setString(67, (ticket.getOtp_landing_page_tracking()));
            ps.setString(68, (ticket.getStatus_ont()));
            ps.setString(69, (ticket.getIp_bras()));
            ps.setString(70, (ticket.getSn_ont()));
            ps.setString(71, (ticket.getJenis_byod()));
            ps.setString(72, (ticket.getSn_byod()));
            ps.setString(73, (ticket.getMitra_byod()));
            ps.setString(74, (ticket.getStart_guarantee_byod()));
            ps.setString(75, (ticket.getFlag_pdd()));
            ps.setString(76, (ticket.getIbooster_alert_id()));
            ps.setString(77, (ticket.getFlag_manja_by_cust()));
            ps.setString(78, (ticket.getDetails()));
            ps.setString(79, (ticket.getContact_name()));
            ps.setString(80, (ticket.getContact_phone()));
            ps.setString(81, (ticket.getContact_email()));
            ps.setString(82, (ticket.getSummary()));
            ps.setString(83, (ticket.getSource_ticket()));
            ps.setString(84, (ticket.getChannel()));
            ps.setString(85, (ticket.getReported_by()));
            ps.setString(86, (ticket.getOssid()));
            ps.setString(87, (ticket.getClosed_by()));
            ps.setTimestamp(88, Timestamp.valueOf(reportedDate));
//            
            if ("".equals(ticket.getQc_voice_ivr_time()) || ticket.getQc_voice_ivr_time() == null) {
                ps.setTimestamp(89, null);
            } else {
                ps.setTimestamp(89, Timestamp.valueOf(parsingDate.parse(ticket.getQc_voice_ivr_time())));
            }
            if (ticket.getStop_time() == null) {
                ps.setTimestamp(90, null);
            } else {
                ps.setTimestamp(90, Timestamp.valueOf(parsingDate.parse(ticket.getStop_time())));
            }

            if ("FISIK".equalsIgnoreCase(symptom.getClassificationType())) {
                ps.setString(91, symptom.getSolution());
            } else {
                ps.setString(91, "");
            }

            ps.setString(92, (ticket.getSubsidiary()));
            ps.setString(93, "API");
            ps.setString(94, symptom.getSolution());
            ps.setString(95, lastState);
            ps.setString(96, "FALSE");
            ps.setString(97, (ticket.getRk_information()));
            ps.setString(98, (ticket.getService_ps_date()));
            ps.setString(99, (ticket.getBilling_status()));
            ps.setString(100, (ticket.getRealm()));
            ps.setString(101, (ticket.getImpact()));
            ps.setString(102, (lsi.getServiceCategory()));
            ps.setString(103, (lsi.getWorkzoneName()));
            ps.setString(104, (lsi.getServiceIdDescription()));
            ps.setString(105, (lsi.getLatitude()));
            ps.setString(106, (lsi.getLongitude()));
            ps.setString(107, (lsi.getPostalCode()));
            ps.setString(108, (lsi.getCountry()));
            ps.setString(109, cont_symptom);

            int i = ps.executeUpdate();

            if (i > 0) {
                result = true;
            }

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
        }

        return result;
    }

}
