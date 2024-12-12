/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.itasoft.telkom.oss.plugin.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.iboosterDatin.ApiIboosterDatinModel;
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sql.DataSource;

import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author asani
 */
public class IboosterDao {

    LogInfo logInfo = new LogInfo();

    public ListIbooster getIbooster(String nd, String realm, String ticketId) {
        RESTAPI _RESTAPI = new RESTAPI();
        MasterParam masterParam = new MasterParam();
        MasterParamDao masterParamDao = new MasterParamDao();
        ListIbooster data = new ListIbooster();
        String response = "";
        LogHistory dataLh = new LogHistory();
        LogHistoryDao logHistoryDao = new LogHistoryDao();

        try {
            String TOKEN = _RESTAPI.getToken();
            masterParam = masterParamDao.getUrl("get_ibooster");

            JSONObject json = new JSONObject();
            json.put("nd", nd);
            json.put("realm", realm);


            RequestBody body = RequestBody.create(_RESTAPI.JSON, json.toString());
            Request request = new Request.Builder()
                    .url(masterParam.getUrl())
                    .addHeader("Authorization", "Bearer " + TOKEN)
                    .post(body)
                    .build();

            dataLh.setUrl(masterParam.getUrl());
            dataLh.setRequest(json.toString());

            response = _RESTAPI.CALLAPI(request);
            Object obj = new JSONTokener(response).nextValue();
            JSONObject data_obj = (JSONObject) obj;


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


        } catch (Exception ex) {

        } finally {

            dataLh.setMethod("POST");
            dataLh.setResponse(response);
            dataLh.setAction("Measure ibooster - GAMAS");
            dataLh.setTicketId(ticketId);
            try {
                logHistoryDao.insertToLogHistory(dataLh);
            } catch (Exception ex) {

            }
        }

        return data;
    }

    public void updateIbooster(ListIbooster ib, String ticketId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;

        StringBuilder iboosterResult = new StringBuilder();
        if ("".equals(ib.getMessage())) {
            iboosterResult.append("Kategori Ukur : " + ((ib.getMeasurementCategory() == null) ? "NULL" : ib.getMeasurementCategory().toUpperCase()) + "\r");
            iboosterResult.append("oper_status : " + ((ib.getOperStatus() == null) ? "NULL" : ib.getOperStatus().toUpperCase()) + "\r");
            iboosterResult.append("onu_rx_pwr : " + ((ib.getOnuRxPwr() == null) ? "NULL" : ib.getOnuRxPwr().toUpperCase()) + "\r");
            iboosterResult.append("onu_tx_pwr : " + ((ib.getOnuTxPwr() == null) ? "NULL" : ib.getOnuTxPwr().toUpperCase()) + "\r");
            iboosterResult.append("olt_rx_pwr : " + ((ib.getOltRxPwr() == null) ? "NULL" : ib.getOltRxPwr().toUpperCase()) + "\r");
            iboosterResult.append("olt_tx_pwr : " + ((ib.getOltTxPwr() == null) ? "NULL" : ib.getOltTxPwr().toUpperCase()) + "\r");
            iboosterResult.append("fiber_length : " + ((ib.getFiberLength() == null) ? "NULL" : ib.getFiberLength().toUpperCase()) + "\r");
            iboosterResult.append("status_jaringan : " + ((ib.getStatusJaringan() == null) ? "NULL" : ib.getStatusJaringan().toUpperCase()) + "\r");
            iboosterResult.append("identifier : " + ((ib.getIdentifier() == null) ? "NULL" : ib.getIdentifier().toUpperCase()) + "\r");
        } else {
            iboosterResult.append((ib.getMessage() == null) ? "" : ib.getMessage().toUpperCase());
        }
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String waktuUkur = (dateFormat2.format(new Date()).toString()).toLowerCase();
        StringBuilder query;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        if ("".equals(ib.getMessage())) {
            query = new StringBuilder();
            query
                    .append(" UPDATE app_fd_ticket  ")
                    .append(" SET c_measurement_time = ?,  ")//1
                    .append(" c_ibooster_result = ?,  ")//2
                    .append(" c_mycx_result = ?,  ")//3
                    .append(" c_mycx_category_result = ?,  ")//4
                    .append(" c_measurement_category = ?,  ")//5
                    .append(" c_id_pengukuran = CASE ")
                    .append(" WHEN c_id_pengukuran IS NOT NULL AND INSTR(c_id_pengukuran, '|') = 0 THEN c_id_pengukuran || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(c_id_pengukuran, 'NULL |'), 1, INSTR(NVL(c_id_pengukuran, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//6,7
                    .append(" c_hostname_olt = CASE ")
                    .append(" WHEN c_hostname_olt IS NOT NULL AND INSTR(c_hostname_olt, '|') = 0 THEN c_hostname_olt || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(c_hostname_olt, 'NULL |'), 1, INSTR(NVL(c_hostname_olt, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//8,9
                    .append(" c_ip_olt = CASE ")
                    .append(" WHEN c_ip_olt IS NOT NULL AND INSTR(c_ip_olt, '|') = 0 THEN c_ip_olt || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(c_ip_olt, 'NULL |'), 1, INSTR(NVL(c_ip_olt, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//10,11
                    .append(" c_frame = ?,  ")//12
                    .append(" c_olt_tx = CASE ")
                    .append(" WHEN c_olt_tx IS NOT NULL AND INSTR(c_olt_tx, '|') = 0 THEN C_OLT_TX || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(c_olt_tx, 'NULL |'), 1, INSTR(NVL(c_olt_tx, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//13,14
                    .append(" c_olt_rx = CASE ")
                    .append(" WHEN C_OLT_RX IS NOT NULL AND INSTR(C_OLT_RX, '|') = 0 THEN C_OLT_RX || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(C_OLT_RX, 'NULL |'), 1, INSTR(NVL(C_OLT_RX, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//15,16
                    .append(" C_ONU_TX = CASE")
                    .append(" WHEN C_ONU_TX IS NOT NULL AND INSTR(C_ONU_TX, '|') = 0 THEN C_ONU_TX || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(C_ONU_TX, 'NULL |'), 1, INSTR(NVL(C_ONU_TX, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//17,18
                    .append(" C_ONU_RX = CASE")
                    .append(" WHEN C_ONU_RX IS NOT NULL AND INSTR(C_ONU_RX, '|') = 0 THEN C_ONU_RX || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(C_ONU_RX, 'NULL |'), 1, INSTR(NVL(C_ONU_RX, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//19,20
                    .append(" c_status_ont = CASE ")
                    .append(" WHEN c_status_ont IS NOT NULL AND INSTR(c_status_ont, '|') = 0 THEN c_status_ont || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(c_status_ont, 'NULL |'), 1, INSTR(NVL(c_status_ont, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END ")//21,22
                    .append(" WHERE c_id_ticket = ? ");//23
        } else {
            query = new StringBuilder();
            query
                    .append(" UPDATE app_fd_ticket ")
                    .append(" SET ")
                    .append(" c_measurement_time = ?, ")//1
                    .append(" c_ibooster_result = ?, ")//2
                    .append(" c_measurement_category = ?, ")//3
                    .append(" c_id_pengukuran = CASE ")
                    .append(" WHEN c_id_pengukuran IS NOT NULL AND INSTR(c_id_pengukuran, '|') = 0 THEN c_id_pengukuran || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(c_id_pengukuran, 'NULL |'), 1, INSTR(NVL(c_id_pengukuran, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//4,5
                    .append(" c_hostname_olt = CASE ")
                    .append(" WHEN c_hostname_olt IS NOT NULL AND INSTR(c_hostname_olt, '|') = 0 THEN c_hostname_olt || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(c_hostname_olt, 'NULL |'), 1, INSTR(NVL(c_hostname_olt, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//6,7
                    .append(" c_ip_olt = CASE ")
                    .append(" WHEN c_ip_olt IS NOT NULL AND INSTR(c_ip_olt, '|') = 0 THEN c_ip_olt || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(c_ip_olt, 'NULL |'), 1, INSTR(NVL(c_ip_olt, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//8,9
                    .append(" c_status_ont = CASE ")
                    .append(" WHEN c_status_ont IS NOT NULL AND INSTR(c_status_ont, '|') = 0 THEN c_status_ont || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(c_status_ont, 'NULL |'), 1, INSTR(NVL(c_status_ont, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//10,11
                    .append(" c_olt_tx = CASE ")
                    .append(" WHEN c_olt_tx IS NOT NULL AND INSTR(c_olt_tx, '|') = 0 THEN C_OLT_TX || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(c_olt_tx, 'NULL |'), 1, INSTR(NVL(c_olt_tx, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//12,13
                    .append(" c_olt_rx = CASE ")
                    .append(" WHEN C_OLT_RX IS NOT NULL AND INSTR(C_OLT_RX, '|') = 0 THEN C_OLT_RX || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(C_OLT_RX, 'NULL |'), 1, INSTR(NVL(C_OLT_RX, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//14,15
                    .append(" C_ONU_TX = CASE")
                    .append(" WHEN C_ONU_TX IS NOT NULL AND INSTR(C_ONU_TX, '|') = 0 THEN C_ONU_TX || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(C_ONU_TX, 'NULL |'), 1, INSTR(NVL(C_ONU_TX, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END, ")//16,17
                    .append(" C_ONU_RX = CASE")
                    .append(" WHEN C_ONU_RX IS NOT NULL AND INSTR(C_ONU_RX, '|') = 0 THEN C_ONU_RX || ' | ' || ? ")
                    .append(" ELSE TRIM(SUBSTR(NVL(C_ONU_RX, 'NULL |'), 1, INSTR(NVL(C_ONU_RX, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                    .append(" END ")//18,19
                    .append(" WHERE ")
                    .append(" c_id_ticket = ? ");//20

        }

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());
                logInfo.Log(getClass().getName(), query.toString());
                if ("".equals(ib.getMessage())) {
                    // LogUtil.info(this.getClass().getName(), "Masuk query parameter normal");
                    String IdUkur = (ib.getIdUkur() == null) ? "" : ib.getIdUkur().toUpperCase();
                    String Identifier = (ib.getIdentifier() == null) ? "" : ib.getIdentifier().toUpperCase();
                    String Hostname = (ib.getHostname() == null) ? "" : ib.getHostname().toUpperCase();
                    String OltTxPwr = (ib.getOltTxPwr() == null) ? "NULL" : ib.getOltTxPwr();
                    String OltRxPwr = (ib.getOltRxPwr() == null) ? "NULL" : ib.getOltRxPwr();
                    String OnuTxPwr = (ib.getOnuTxPwr() == null) ? "NULL" : ib.getOnuTxPwr();
                    String OnuRxPwr = (ib.getOnuRxPwr() == null) ? "NULL" : ib.getOnuRxPwr();
                    String StatusCpe = (ib.getStatusCpe() == null) ? "NULL" : ib.getStatusCpe();

                    ps.setTimestamp(1, Timestamp.valueOf(waktuUkur));
                    ps.setString(2, iboosterResult.toString());
                    ps.setString(3, "-");
                    ps.setString(4, "-");
                    ps.setString(5, ib.getMeasurementCategory());
                    ps.setString(6, IdUkur);
                    ps.setString(7, IdUkur);
                    ps.setString(8, Identifier);
                    ps.setString(9, Identifier);
                    ps.setString(10, Hostname);
                    ps.setString(11, Hostname);
                    ps.setString(12, ib.getClid());
                    ps.setString(13, OltTxPwr);
                    ps.setString(14, OltTxPwr);
                    ps.setString(15, OltRxPwr);
                    ps.setString(16, OltRxPwr);
                    ps.setString(17, OnuTxPwr);
                    ps.setString(18, OnuTxPwr);
                    ps.setString(19, OnuRxPwr);
                    ps.setString(20, OnuRxPwr);
                    ps.setString(21, StatusCpe);
                    ps.setString(22, StatusCpe);
                    ps.setString(23, ticketId);
                } else {
                    String idUkur = (ib.getIdUkur() == null) ? "NULL" : ib.getIdUkur().toUpperCase();
                    String identifier = (ib.getIdentifier() == null) ? "NULL" : ib.getIdentifier().toUpperCase();
                    String hostname = (ib.getHostname() == null) ? "NULL" : ib.getHostname().toUpperCase();
                    String MeasurementCategory = (ib.getMeasurementCategory() == null) ? "NULL" : ib.getMeasurementCategory().toUpperCase();
                    String OltTxPwr = (ib.getOltTxPwr() == null) ? "NULL" : ib.getOltTxPwr().toUpperCase();
                    String OltRxPwr = (ib.getOltRxPwr() == null) ? "NULL" : ib.getOltRxPwr().toUpperCase();
                    String OnuTxPwr = (ib.getOnuTxPwr() == null) ? "NULL" : ib.getOnuTxPwr().toUpperCase();
                    String OnuRxPwr = (ib.getOnuRxPwr() == null) ? "NULL" : ib.getOnuRxPwr().toUpperCase();
                    String statusCpe = (ib.getStatusCpe() == null) ? "NULL" : ib.getStatusCpe();

                    ps.setTimestamp(1, Timestamp.valueOf(waktuUkur));
                    ps.setString(2, iboosterResult.toString());
                    ps.setString(3, MeasurementCategory);
                    ps.setString(4, idUkur);
                    ps.setString(5, idUkur);
                    ps.setString(6, identifier);
                    ps.setString(7, identifier);
                    ps.setString(8, hostname);
                    ps.setString(9, hostname);
                    ps.setString(10, statusCpe);
                    ps.setString(11, statusCpe);
                    ps.setString(12, OltTxPwr);
                    ps.setString(13, OltTxPwr);
                    ps.setString(14, OltRxPwr);
                    ps.setString(15, OltRxPwr);
                    ps.setString(16, OnuTxPwr);
                    ps.setString(17, OnuTxPwr);
                    ps.setString(18, OnuRxPwr);
                    ps.setString(19, OnuRxPwr);
                    ps.setString(20, ticketId);
                }

                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            logInfo.Error(getClass().getName(), getClass().getName(), ex);
        } catch (Exception ex) {
            logInfo.Error(getClass().getName(), getClass().getName(), ex);
        } finally {
            query = null;
            ib = null;
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

    public void updateIboosterDatin(ApiIboosterDatinModel iboosterModel, String ticketId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;

        String waktuUkur = iboosterModel.getEaiHeader().getResponseTimestamp();
        StringBuilder query;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        query = new StringBuilder();
        query
                .append(" UPDATE app_fd_ticket  ")
                .append(" SET c_measurement_time = ?,  ")//1
                .append(" c_ibooster_result = ?,  ")//2
                .append(" c_mycx_result = ?,  ")//3
                .append(" c_mycx_category_result = ?,  ")//4
                .append(" c_measurement_category = ?,  ")//5
                .append(" c_id_pengukuran = CASE ")
                .append(" WHEN c_id_pengukuran IS NOT NULL AND INSTR(c_id_pengukuran, '|') = 0 THEN c_id_pengukuran || ' | ' || ? ")
                .append(" ELSE TRIM(SUBSTR(NVL(c_id_pengukuran, 'NULL |'), 1, INSTR(NVL(c_id_pengukuran, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                .append(" END, ")//6,7
                .append(" c_hostname_olt = CASE ")
                .append(" WHEN c_hostname_olt IS NOT NULL AND INSTR(c_hostname_olt, '|') = 0 THEN c_hostname_olt || ' | ' || ? ")
                .append(" ELSE TRIM(SUBSTR(NVL(c_hostname_olt, 'NULL |'), 1, INSTR(NVL(c_hostname_olt, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                .append(" END, ")//8,9
                .append(" c_ip_olt = CASE ")
                .append(" WHEN c_ip_olt IS NOT NULL AND INSTR(c_ip_olt, '|') = 0 THEN c_ip_olt || ' | ' || ? ")
                .append(" ELSE TRIM(SUBSTR(NVL(c_ip_olt, 'NULL |'), 1, INSTR(NVL(c_ip_olt, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                .append(" END, ")//10,11
                .append(" c_frame = ?,  ")//12
                .append(" c_olt_tx = CASE ")
                .append(" WHEN c_olt_tx IS NOT NULL AND INSTR(c_olt_tx, '|') = 0 THEN C_OLT_TX || ' | ' || ? ")
                .append(" ELSE TRIM(SUBSTR(NVL(c_olt_tx, 'NULL |'), 1, INSTR(NVL(c_olt_tx, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                .append(" END, ")//13,14
                .append(" c_olt_rx = CASE ")
                .append(" WHEN C_OLT_RX IS NOT NULL AND INSTR(C_OLT_RX, '|') = 0 THEN C_OLT_RX || ' | ' || ? ")
                .append(" ELSE TRIM(SUBSTR(NVL(C_OLT_RX, 'NULL |'), 1, INSTR(NVL(C_OLT_RX, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                .append(" END, ")//15,16
                .append(" C_ONU_TX = CASE")
                .append(" WHEN C_ONU_TX IS NOT NULL AND INSTR(C_ONU_TX, '|') = 0 THEN C_ONU_TX || ' | ' || ? ")
                .append(" ELSE TRIM(SUBSTR(NVL(C_ONU_TX, 'NULL |'), 1, INSTR(NVL(C_ONU_TX, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                .append(" END, ")//17,18
                .append(" C_ONU_RX = CASE")
                .append(" WHEN C_ONU_RX IS NOT NULL AND INSTR(C_ONU_RX, '|') = 0 THEN C_ONU_RX || ' | ' || ? ")
                .append(" ELSE TRIM(SUBSTR(NVL(C_ONU_RX, 'NULL |'), 1, INSTR(NVL(C_ONU_RX, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                .append(" END, ")//19,20
                .append(" c_status_ont = CASE ")
                .append(" WHEN c_status_ont IS NOT NULL AND INSTR(c_status_ont, '|') = 0 THEN c_status_ont || ' | ' || ? ")
                .append(" ELSE TRIM(SUBSTR(NVL(c_status_ont, 'NULL |'), 1, INSTR(NVL(c_status_ont, 'NULL |'), '|') - 1)) || ' | ' || ? ")
                .append(" END ")//21,22
                .append(" WHERE c_id_ticket = ? ");//23

        try {
            con = ds.getConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query.toString());
                String IdUkur = iboosterModel.getEaiBody().getId_ukur();
                String Identifier = iboosterModel.getEaiBody().getIdentifier();
                String Hostname = iboosterModel.getEaiBody().getHostname();
                String OltTxPwr = iboosterModel.getEaiBody().getOlt_tx_pwr();
                String OltRxPwr = iboosterModel.getEaiBody().getOlt_rx_pwr();
                String OnuTxPwr = iboosterModel.getEaiBody().getOnu_tx_pwr();
                String OnuRxPwr = iboosterModel.getEaiBody().getOnu_rx_pwr();
                String StatusCpe = iboosterModel.getEaiBody().getOper_status();

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonOutput = objectMapper.writeValueAsString(iboosterModel);

                ps.setTimestamp(1, Timestamp.valueOf(waktuUkur));
                ps.setString(2, jsonOutput);
                ps.setString(3, "-");
                ps.setString(4, "-");
                ps.setString(5, iboosterModel.getEaiBody().getMeasurement_category());
                ps.setString(6, IdUkur);
                ps.setString(7, IdUkur);
                ps.setString(8, Identifier);
                ps.setString(9, Identifier);
                ps.setString(10, Hostname);
                ps.setString(11, Hostname);
                ps.setString(12, iboosterModel.getEaiBody().getSlot());
                ps.setString(13, OltTxPwr);
                ps.setString(14, OltTxPwr);
                ps.setString(15, OltRxPwr);
                ps.setString(16, OltRxPwr);
                ps.setString(17, OnuTxPwr);
                ps.setString(18, OnuTxPwr);
                ps.setString(19, OnuRxPwr);
                ps.setString(20, OnuRxPwr);
                ps.setString(21, StatusCpe);
                ps.setString(22, StatusCpe);
                ps.setString(23, ticketId);
                ps.executeUpdate();
            }
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
