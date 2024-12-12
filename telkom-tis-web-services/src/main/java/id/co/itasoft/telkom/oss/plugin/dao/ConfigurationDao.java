package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.StringManipulation;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConfigurationDao {

    GetConnections gn = new GetConnections();
    LogInfo logInfo = new LogInfo();
    StringManipulation stringManipulation = new StringManipulation();

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
                .append(" select id, datecreated, datemodified, c_button_wo, ")
                .append(" c_check_ibooster, c_atvr_resolved, ")
                .append(" c_day_resolved, c_hour_resolved, c_minutes_resolved, ")
                .append(" c_check_tsc, c_check_scc, ")
                .append(" C_CTS_SYMPTOM, c_scc_plus, c_gamas_pending, c_check_sccplus_nas, C_GET_PERANGKAT, c_service_gamas_wifi,C_GET_TECHNICAL_DATA, ")
                .append(" C_KFK_TSPWD,C_KFK_TSLOC,C_KFK_BOOTSTRAP,C_KFK_MECHANISM,C_KFK_SECURITY,C_KFK_JAAS ")
                .append(" From app_fd_configuration where id = '1' ");

        Connection con = gn.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                result.put("id", rs.getString("id"));
                result.put("datecreated", rs.getString("datecreated"));
                result.put("datemodified", rs.getString("datemodified"));
                result.put("checkIbooster", rs.getBoolean("c_check_ibooster"));
                result.put("checkScc", rs.getBoolean("c_check_scc"));
                result.put("checkTsc", rs.getBoolean("c_check_tsc"));
                result.put("buttonWo", rs.getBoolean("c_button_wo"));
                result.put("deadline_resolved", rs.getBoolean("c_atvr_resolved"));
                result.put("day_resolved", rs.getInt("c_day_resolved"));
                result.put("hour_resolved", rs.getInt("c_hour_resolved"));
                result.put("minutes_resolved", rs.getInt("c_minutes_resolved"));
                result.put("gamas_pending", rs.getString("c_gamas_pending"));

                result.put("cts_symptom", stringManipulation.getNonNullTrimmed(rs.getString("C_CTS_SYMPTOM")));
                result.put("scc_plus", stringManipulation.getNonNullTrimmed(rs.getString("c_scc_plus")));
                result.put("check_sccplus_nas", stringManipulation.getNonNullTrimmed(rs.getString("c_check_sccplus_nas")));
                result.put("service_gamas_wifi", (rs.getString("c_service_gamas_wifi") == null) ? "" : rs.getString("c_service_gamas_wifi"));
                result.put("get_technical_data", rs.getBoolean("C_GET_TECHNICAL_DATA"));
                result.put("get_perangkat", rs.getString("C_GET_PERANGKAT"));

                result.put("kfk_tspwd", rs.getString("C_KFK_TSPWD"));
                result.put("kfk_tsloc", rs.getString("C_KFK_TSLOC"));
                result.put("kfk_bootstrap", rs.getString("C_KFK_BOOTSTRAP"));
                result.put("kfk_mechanism", rs.getString("C_KFK_MECHANISM"));
                result.put("kfk_security", rs.getString("C_KFK_SECURITY"));
                result.put("kfk_jaas", rs.getString("C_KFK_JAAS"));


            } else {

                result.put("id", "");
                result.put("datecreated", "");
                result.put("datemodified", "");
                result.put("checkIbooster", true);
                result.put("buttonWo", true);
                result.put("checkScc", true);
                result.put("checkTsc", true);
                result.put("cts_symptom", "");
                result.put("scc_plus", "");
                result.put("c_check_sccplus_nas", true);
                result.put("gamas_pending", "");
                result.put("service_gamas_wifi", stringManipulation.getNonNullTrimmed(rs.getString("c_service_gamas_wifi")));
                result.put("get_technical_data", true);

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
}
