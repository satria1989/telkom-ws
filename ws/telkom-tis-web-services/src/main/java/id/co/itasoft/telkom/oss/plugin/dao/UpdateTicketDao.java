/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.sql.*;

/**
 * @author Tarkiman
 */
public class UpdateTicketDao {

    LogInfo logInfo = new LogInfo();
    
    public boolean updateCheckONT(String keterangan, String actSol, String desActSol, String ticketId) throws SQLException, Exception {

        boolean result = false;
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" C_CO_KETERANGAN = ?, ")
                .append(" C_ACTUAL_SOLUTION = ?, ")
                .append(" C_DESCRIPTION_ACTUALSOLUTION = ? ")
                .append(" WHERE C_ID_TICKET = ? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, keterangan);
            ps.setString(2, actSol);
            ps.setString(3, desActSol);
            ps.setString(4, ticketId);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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

        return result;

    }
    
    public boolean updateReplaceONT(String onuSN, String onuType, String techCode,String techName, String ticketId) throws SQLException, Exception {

        boolean result = false;
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" C_CO_NEW_ONU_SN = ? ,")
                .append(" C_CO_NEW_ONU_TYPE = ? ,")
                .append(" C_CO_TECHNICIAN_CODE = ? ,")
                .append(" C_CO_TECHNICIAN_NAME = ? ")
                .append(" WHERE C_ID_TICKET = ? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, onuSN);
            ps.setString(2, onuType);
            ps.setString(3, techCode);
            ps.setString(4, techName);
            ps.setString(5, ticketId);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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

        return result;

    }

    public Timestamp getTicketStatusByTicketID(String ticketId) throws SQLException, Exception {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" a.id, ")
                .append(" a.dateCreated, ")
                .append(" a.c_ticketid, ")
                .append(" a.c_status ")
                .append(" FROM app_fd_ticketstatus a ")
                .append(" WHERE a.c_ticketid=? ")
                .append(" AND rownum = 1 ")
                .append(" ORDER BY a.dateCreated DESC ");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);

            rs = ps.executeQuery();
            while (rs.next()) {
                timestamp = new Timestamp(rs.getDate("dateCreated").getTime());
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "ex:" + e.getMessage());
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

        return timestamp;
    }

    public boolean insertNewTicketStatus(TicketStatus r) throws SQLException, Exception {

        boolean result = false;
        String pinPoint = "";
        if (!"REQUEST_PENDING".equalsIgnoreCase(r.getActionStatus())) {
            pinPoint = "TRUE";
        }

        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
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
                .append(" c_status, ")
                .append(" c_tkstatusid, ")
                .append(" c_action_status, ")
                .append(" c_pin_point ")
                .append(" ) ")
                .append(" VALUES ( ")
                .append(" ?, ")
                .append(" sysdate, ")
                .append(" sysdate, ")
                .append(" ?, ")
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
                .append(" ?, ")//c_orgid - 15
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")//c_status - 20
                .append(" ?, ")
                .append(" ?, ")
                .append(" ? ")
                .append(" ) ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setString(2, "");//createdBy
            ps.setString(3, "");//createdByName
            ps.setString(4, "");//modifiedBy
            ps.setString(5, "");//modifiedByName
            ps.setString(6, r.getOwner());
            ps.setString(7, r.getTicketStatusId());
            ps.setString(8, r.getChangeBy());
            ps.setString(9, r.getMemo());
            ps.setString(10, r.getChangeDate());
            ps.setString(11, r.getOwnerGroup());
            ps.setString(12, r.getAssignedOwnerGroup());
            ps.setString(13, r.getOrgId());
            ps.setString(14, "");
            ps.setString(15, r.getSiteId());
            ps.setString(16, r.getClasS());
            ps.setString(17, r.getTicketId());
            ps.setString(18, r.getStatus());
            ps.setString(19, r.getTkStatusId());
            ps.setString(20, r.getActionStatus());
            ps.setString(21, pinPoint);

            int i = ps.executeUpdate();
//            LogUtil.info(this.getClass().getName(), "Insert To log History");
            if (i > 0) {
                result = true;
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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

        return result;

    }

    public boolean UpdateTicketStatus(Ticket o) throws SQLException, Exception {

        boolean result = false;
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_status=?, ")
                .append(" c_owner=NULL ")
                .append(" WHERE id=? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, o.getStatus());
            ps.setString(2, o.getId());

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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

        return result;

    }

    public void updateScStatus(String ticketId, String scStatus) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query.
                append(" UPDATE APP_FD_TICKET SET ")
                .append(" C_SC_STATUS = ? ")
                .append(" WHERE C_ID_TICKET = ? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, scStatus);
            ps.setString(2, ticketId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
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

    public void updateTSC(String TICKETID, String tscTime, String tscResult, String tscResCategory) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query.
                append(" UPDATE APP_FD_TICKET SET ")
                .append(" C_TSC_TIME = ? ,")
                .append(" C_TSC_RESULT = ? ,")
                .append(" C_TSC_RESULT_CATEGORY = ? ")
                .append(" WHERE C_ID_TICKET = ? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setTimestamp(1, Timestamp.valueOf(tscTime));
            ps.setString(2, tscResult);
            ps.setString(3, tscResCategory);
            ps.setString(4, TICKETID);
            ps.executeUpdate();
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
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

    public void updateSCC(String ticketId, String timestampString, String serviceType,
            String result, String responseCode) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        Timestamp timestamp = Timestamp.valueOf(timestampString);
        query.
                append(" UPDATE APP_FD_TICKET SET ");
        if ("INTERNET".equals(serviceType) || "IPTV".equals(serviceType)) {
            query.append(" C_INTERNET_TEST_RESULT = ?, ")
                    .append(" C_QC_INTERNET_TIME = ?, ");
        } else if ("VOICE".equals(serviceType)) {
            query.append(" C_QC_VOICE_IVR_RESULT = ?, ")
                    .append(" C_QC_VOICE_IVR_TIME = ?, ");
        }
        query.append(" C_INTERNET_RESPONSE_CODE = ? ")
                .append(" WHERE C_ID_TICKET = ? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, result);
            ps.setTimestamp(2, timestamp);
            ps.setString(3, responseCode);
            ps.setString(4, ticketId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
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

    public void updateIbooster(Object _dataObj, ListIbooster data, String ticketId) throws JSONException {
        JSONObject data_obj = new JSONObject();
        Object msgRes = new Object();
        JSONObject _data_obj = (JSONObject) _dataObj;
        String msg = "";
        boolean boolUpdateIbooster = _data_obj.getBoolean("status");
        int responseCode = _data_obj.getInt("status_code");
        IboosterDao iboosterDao = new IboosterDao();
        try {
            if (boolUpdateIbooster) {
                msg = _data_obj.getString("msg");
                msgRes = new JSONTokener(msg).nextValue();
                data_obj = (JSONObject) msgRes;

                if (!data_obj.has("MESSAGE")) {
                    String oper_status = (data_obj.has("oper_status")) ? data_obj.get("oper_status").toString() : "";
                    String onu_rx_pwr = (data_obj.has("onu_rx_pwr")) ? data_obj.get("onu_rx_pwr").toString() : "";
                    String onu_tx_pwr = (data_obj.has("onu_tx_pwr")) ? data_obj.get("onu_tx_pwr").toString() : "";
                    String olt_rx_pwr = (data_obj.has("olt_rx_pwr")) ? data_obj.get("olt_rx_pwr").toString() : "";
                    String olt_tx_pwr = (data_obj.has("olt_tx_pwr")) ? data_obj.get("olt_tx_pwr").toString() : "";
                    String fiber_length = (data_obj.has("fiber_length")) ? data_obj.get("fiber_length").toString() : "";
                    String status_jaringan = (data_obj.has("status_jaringan")) ? data_obj.get("status_jaringan").toString() : "";
                    String identifier = (data_obj.has("identifier")) ? data_obj.get("identifier").toString() : "";
                    String id_ukur = (data_obj.has("id_ukur")) ? data_obj.get("id_ukur").toString() : "";
                    String nas_ip = (data_obj.has("nas_ip")) ? data_obj.get("nas_ip").toString() : "";
                    String hostname = (data_obj.has("hostname")) ? data_obj.get("hostname").toString() : "";
                    String clid = (data_obj.has("clid")) ? data_obj.get("clid").toString() : "";
                    String status_cpe = (data_obj.has("status_cpe")) ? data_obj.get("status_cpe").toString() : "";
                    String session_start = (data_obj.has("session_start")) ? data_obj.get("session_start").toString() : "";

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

                String onuRxPwrStr = data_obj.has("onu_rx_pwr") ? data_obj.getString("onu_rx_pwr") : "null";

                String measurementCategory = "";
                if (onuRxPwrStr == null || onuRxPwrStr.equals("null") || onuRxPwrStr.isEmpty()) {
                    measurementCategory = "UNSPEC";
                } else {
                    Float onuRxPwr = Float.parseFloat(onuRxPwrStr);
                    if (onuRxPwr <= Float.valueOf(-13) && onuRxPwr >= Float.valueOf(-24)) {
                        measurementCategory = "SPEC";
                    } else {
                        measurementCategory = "UNSPEC";
                    }
                }
                data.setMeasurementCategory(measurementCategory);
                iboosterDao.updateIbooster(data, ticketId);
            }
        } catch (JSONException | SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message ps : " + e.getMessage());
        } finally {
            data_obj = null;
            msgRes = null;
            _data_obj = null;
        }
    }

    public boolean updateIntegrateKominfo(String ticketId) throws SQLException, Exception {
        boolean result = false;
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" UPDATE app_fd_ticket SET ")
                .append(" c_integrate_to_kominfo = ? ")
                .append(" WHERE c_id_ticket = ? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, "TRUE");
            ps.setString(2, ticketId);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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

        return result;

    }

    public boolean updateCustSegment(String custSegment, String ticketId) throws Exception {
        boolean result = false;
        String custSegementDesc = "";
        GetConnections gc = new GetConnections();
        StringBuilder queryUpdate = new StringBuilder();
        StringBuilder querySelect = new StringBuilder();
        Connection con = gc.getJogetConnection();
        PreparedStatement psSelect = null, psUpdate = null;
        ResultSet rs = null;

        if (!"".equals(custSegment)) {
            querySelect
                    .append(" SELECT ")
                    .append("	C_PARAM_DESCRIPTION ")
                    .append(" FROM ")
                    .append("	APP_FD_PARAM ")
                    .append(" WHERE ")
                    .append("	C_PARAM_NAME = 'CUSTOMERSEGMENT' ")
                    .append("	AND C_PARAM_CODE = ? ");
            
            try {
                psSelect = con.prepareStatement(querySelect.toString());
                psSelect.setString(1, custSegment);
                rs = psSelect.executeQuery();
                
                queryUpdate
                        .append(" UPDATE app_fd_ticket SET ")
                        .append(" C_CUSTOMER_SEGMENT = ? ,")
                        .append(" C_CUSTOMER_SEGMENT_DESC = ? ")
                        .append(" WHERE c_id_ticket = ? ");
                
                if (rs.next()) {
                    custSegementDesc = rs.getString("C_PARAM_DESCRIPTION");
                    logInfo.Log(getClass().getName(), "Cust Segment : "+ custSegment + " - " +custSegementDesc);

                    psUpdate = con.prepareStatement(queryUpdate.toString());
                    psUpdate.setString(1, custSegment);
                    psUpdate.setString(2, custSegementDesc);
                    psUpdate.setString(3, ticketId);

                    int i = psUpdate.executeUpdate();
                    if (i > 0) {
                        result = true;
                    }
                }
            } catch (Exception e) {
                LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
                }
                try {
                    if (psSelect != null) {
                        psSelect.close();
                    }
                } catch (Exception e) {
                    LogUtil.error(this.getClass().getName(), e, "Error message psSelect : " + e.getMessage());
                }
                try {
                    if (psUpdate != null) {
                        psUpdate.close();
                    }
                } catch (Exception e) {
                    LogUtil.error(this.getClass().getName(), e, "Error message psUpdate : " + e.getMessage());
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
        return result;
    }

}
