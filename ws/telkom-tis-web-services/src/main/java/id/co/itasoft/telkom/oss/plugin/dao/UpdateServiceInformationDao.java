package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * @author asani
 */
public class UpdateServiceInformationDao {

    LogInfo info = new LogInfo();

    public void UpdateDetailServiceInformation(JSONArray jsonArray, String TicketID, String customerSegmentTicket) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;
        GetMasterParamDao getMasterParamDao = new GetMasterParamDao();

        try {
            info.Log(getClass().getName(), jsonArray.toString());
            JSONObject json = jsonArray.getJSONObject(0);
            String customerSegment = customerSegmentTicket;

            if(customerSegmentTicket.isEmpty()) {
                customerSegment = json.getString("customer_segment");
                if (customerSegment.equalsIgnoreCase("PL-")) {
                    customerSegment = "PL-TSEL";
                }
            }

            con = gc.getJogetConnection();
            String masterParam = getMasterParamDao.GetMasterParam(customerSegment, "");//
            info.Log(getClass().getName(), masterParam);
            Object obj = new JSONTokener(masterParam).nextValue();

            JSONArray jsonParam = (JSONArray) obj;
            JSONObject jsonPr = jsonParam.getJSONObject(0);
            String descCustomerSegment = (jsonPr.has("param_description")) ? jsonPr.getString("param_description") : "";

            StringBuilder query = new StringBuilder();
            query
                    .append(" UPDATE APP_FD_TICKET SET c_work_zone = ?, c_description_work_zone = ?,  ")
                    .append(" c_city=?, c_service_category=?, c_street_address=?, ")
                    .append(" c_latitude=?, c_reference_number=?, c_service_id=?,  ")
                    .append(" c_description_serviceid=?, c_service_type=?, c_witel=?, ")
                    .append(" c_region=?, c_customer_id=?, c_customer_priority=?,  ")
                    .append(" c_service_no=?, c_longitude=?, c_description_customerid=?, ")
                    .append(" c_customer_segment=?, c_customer_segment_desc=?, c_zip_code=?, ")
                    .append(" c_country=?, c_package=? ")
                    .append(" where c_id_ticket = ? ");

            ps = con.prepareStatement(query.toString());
            ps.setString(1, json.getString("workzone"));
            ps.setString(2, json.getString("workzone_name"));
            ps.setString(3, json.getString("city"));
            ps.setString(4, json.getString("service_category"));
            ps.setString(5, json.getString("street_address"));
            ps.setString(6, json.getString("latitude"));
            ps.setString(7, json.getString("reference_number"));
            ps.setString(8, json.getString("service_id"));
            ps.setString(9, json.getString("service_id_description"));
            ps.setString(10, json.getString("service_type"));
            ps.setString(11, json.getString("witel"));
            ps.setString(12, json.getString("region"));
            ps.setString(13, json.getString("customer_id"));
            ps.setString(14, json.getString("customer_priority"));
            ps.setString(15, json.getString("service_number"));
            ps.setString(16, json.getString("longitude"));
            ps.setString(17, json.getString("customer_name"));
            ps.setString(18, customerSegment);
            ps.setString(19, descCustomerSegment);
            ps.setString(20, json.getString("postal_code"));
            ps.setString(21, json.getString("country"));
            ps.setString(22, json.getString("service_package"));
            ps.setString(23, TicketID);
            ps.executeUpdate();

        } catch (Exception ex) {
            info.Error(getClass().getName(), ex.getMessage(), ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
        }

    }
}
