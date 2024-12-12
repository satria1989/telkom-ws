/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mtaup
 */
public class AddImpactedServiceDao {

    LogInfo info = new LogInfo();

    public Ticket GetDataTicekt(String ticketId) throws Exception {
        Ticket ticket = new Ticket();
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query.append("select C_PARENT_ID, C_ID_TICKET, C_ESTIMATION, C_CLASSIFICATION_PATH , C_CLASS_DESCRIPTION, C_CLASSIFICATION_TYPE, C_CHANNEL, C_PERANGKAT ")
                .append("from app_fd_ticket  ")
                .append("where c_id_ticket = ? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet result = null;
        try {

            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            result = ps.executeQuery();
            if (result.next()) {
                ticket.setParentId(result.getString("C_PARENT_ID"));
                ticket.setIdTicket(result.getString("C_ID_TICKET"));
                ticket.setEstimation(result.getString("C_ESTIMATION"));
                ticket.setSymptom(result.getString("C_CLASSIFICATION_PATH"));
                ticket.setSymptomDesc(result.getString("C_CLASS_DESCRIPTION"));
                ticket.setClassificationType(result.getString("C_CLASSIFICATION_TYPE"));
                ticket.setChannel(result.getString("C_CHANNEL"));
                ticket.setPerangkat(result.getString("C_PERANGKAT"));

            }

        } catch (SQLException e) {
            info.Log(getClass().getName(), e.getMessage());
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            gc = null;
            query = null;

        }

        return ticket;

    }

    public boolean checkServiceId(String serviceId, String ticketId) throws Exception {
        boolean result = true;
        GetConnections gc = new GetConnections();
        Connection con = gc.getJogetConnection();

        String query = "select c_service_id from app_fd_ticket_imp_service where c_service_id = ? and c_ticket_id = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            ps = con.prepareStatement(query);
            ps.setString(1, serviceId);
            ps.setString(2, ticketId);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = false;
            }

        } catch (SQLException e) {
            info.Log(getClass().getName(), e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            gc = null;
            query = null;

        }

        return result;

    }
    
    public ArrayList<String> getExistingSId(String ticketId) throws Exception {
        ArrayList<String> existingSId = new ArrayList<>();
        GetConnections gc = new GetConnections();
        Connection con = gc.getJogetConnection();

        String query = "SELECT c_service_id FROM app_fd_ticket_imp_service WHERE c_ticket_id = ? ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            ps = con.prepareStatement(query);
            ps.setString(1, ticketId);
            rs = ps.executeQuery();
            while (rs.next()) {
                existingSId.add(rs.getString("c_service_id"));
            }

        } catch (SQLException e) {
            info.Log(getClass().getName(), e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            gc = null;
            query = null;

        }

        return existingSId;

    }

    public String getOperStatus(String nd, String realm) throws Exception {
        CallRestAPI cra = new CallRestAPI();
        String token = cra.getToken();

        String operStatus = "";
        ApiConfig apiConfig = new ApiConfig();
        GetMasterParamDao paramDao = new GetMasterParamDao();

        try {
            apiConfig = paramDao.getUrl("get_ibooster");
            RequestBody formBody = new FormBody.Builder()
                    .add("nd", (nd == null ? "" : nd))
                    .add("realm", realm)
                    .build();

            String response = "";
            response = cra.sendPostIbooster(apiConfig, formBody, token);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);

            String message = (data_obj.get("MESSAGE") == null ? "" : data_obj.get("MESSAGE").toString());
            if ("".equals(message)) {
                operStatus = (data_obj.get("oper_status") == null ? "" : data_obj.get("oper_status").toString());
            }
            parse = null;
            data_obj = null;
            formBody = null;

        } catch (Exception ex) {
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            apiConfig = null;
            token = null;
            paramDao = null;
        }
        return operStatus;
    }

    public int insertToTableService(List<Map<String, String>> listMapSid, String operStatus, String processId, String idTicket,
            String estimation, String symptom, String symptomDesc,
            String perangkat, String method, String channel) throws SQLException, Exception {
        int count = 0;
        GetConnections gc = new GetConnections();
        Connection con = gc.getJogetConnection();

        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO app_fd_ticket_imp_service ")
                .append("(id, dateCreated, dateModified, c_service_id, c_ibooster_oper_status, c_parent_id, c_ticket_id, ")
                .append("c_service_number, c_estimation, c_symptom, c_symptom_desc, c_region, c_perangkat, c_method, c_channel) ")
                .append("VALUES (?,sysdate,sysdate,?,?,?,?,?,?,?,?,?,?,?,?) ");

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            int batchSize = 200;

            for (Map<String, String> map : listMapSid) {
                ps.setString(1, uuid.getUuid());
                ps.setString(2, map.get("sId"));
                ps.setString(3, operStatus);
                ps.setString(4, processId);
                ps.setString(5, idTicket);
                ps.setString(6, map.get("service_number"));
                ps.setString(7, estimation);
                ps.setString(8, symptom);
                ps.setString(9, symptomDesc);
                ps.setString(10, map.get("region"));
                ps.setString(11, perangkat);
                ps.setString(12, method);
                ps.setString(13, channel);
                ps.addBatch();
                count++;
                if (count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
        } catch (Exception e) {
            info.Log(getClass().getName(), e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            gc = null;
            query = null;
        }
        return count;
    }

    public void updateDataImpactedService(String serviceId, String ticketId, String operStatus, String region) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        Connection con = gc.getJogetConnection();

        StringBuilder query = new StringBuilder();
        query.append(" update APP_FD_TICKET_IMP_SERVICE ")
                .append(" c_ibooster_oper_status = ?, c_region ? ")
                .append(" where c_service_id = ? ")
                .append(" and c_ticket_id = ? ");

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, operStatus);
            ps.setString(2, region);
            ps.setString(3, serviceId);
            ps.setString(4, ticketId);
            ps.executeUpdate();
        } catch (Exception e) {
            info.Log(getClass().getName(), e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                info.Log(getClass().getName(), e.getMessage());
            }
            gc = null;
            query = null;
        }
    }
}
