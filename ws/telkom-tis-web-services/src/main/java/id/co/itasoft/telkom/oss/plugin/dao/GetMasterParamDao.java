/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.Param;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.joget.commons.util.LogUtil;
import org.json.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author suena
 */
public class GetMasterParamDao {

    public String GetMasterParam(String param_code, String param_name) throws SQLException, Exception {
        GetConnections gc = new GetConnections();
        Param r = new Param();
        StringBuilder query = new StringBuilder();//return object buat nilai atau isi kembalian nya
        JSONArray root = new JSONArray();
        JSONObject jsonData = new JSONObject();
        query
                .append(" SELECT ")
                .append(" id, ")
                .append(" dateCreated, ")
                .append(" dateModified, ")
                .append(" createdBy, ")
                .append(" createdByName, ")
                .append(" modifiedBy, ")
                .append(" modifiedByName, ")
                .append(" c_param_code, ")
                .append(" c_param_description, ")
                .append(" c_param_name, ")
                .append(" c_flag ")
                .append(" FROM app_fd_param WHERE 1=1 ");
        int no = 0;
        if (!"".equals(param_code)) {
            query.append(" AND c_param_code=? ");
        }

        if (!"".equals(param_name)) {
            query.append(" AND c_param_name=? ");
        }
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            ps = con.prepareStatement(query.toString());
            if (!"".equals(param_code)) {
                no += 1;
                ps.setString(no, param_code); //
            }

            if (!"".equals(param_name)) {
                no += 1;
                ps.setString(no, param_name); //
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                jsonData = new JSONObject();
                jsonData.put("id", rs.getString("id"));
                jsonData.put("dateCreated", rs.getString("dateCreated"));
                jsonData.put("dateModified", rs.getString("dateModified"));
                jsonData.put("createdByName", rs.getString("createdBy"));
                jsonData.put("createdByName", rs.getString("createdByName"));
                jsonData.put("modifiedBy", rs.getString("modifiedBy"));
                jsonData.put("modifiedByName", rs.getString("modifiedByName"));
                jsonData.put("param_code", rs.getString("c_param_code"));
                jsonData.put("param_description", rs.getString("c_param_description"));
                jsonData.put("param_name", rs.getString("c_param_name"));
                jsonData.put("flag", rs.getString("c_flag"));
                root.put(jsonData);
            }

        } catch (SQLException err) {
            LogUtil.error(this.getClass().getName(), err, "err:" + err.getMessage());
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
            gc = null;

        }
        return root.toString();
    }

    public ApiConfig getUrl(String use_of_api) throws SQLException, Exception {
        ApiConfig mstParam = new ApiConfig();
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT  ")
                .append(" c_api_id, ")
                .append(" c_api_key, ")
                .append(" c_use_of_api, ")
                .append(" c_url, ")
                .append(" c_api_secret, ")
                .append(" c_client_id, ")
                .append(" c_client_secret ")
                .append(" FROM app_fd_tis_mst_api ")
                .append(" WHERE c_use_of_api = ? ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, use_of_api);

            rs = ps.executeQuery();
            while (rs.next()) {
                mstParam.setApiId(rs.getString("c_api_id"));
                mstParam.setApiKey(rs.getString("c_api_key"));
                mstParam.setUrl(rs.getString("c_url"));
                mstParam.setApiToken("");
                mstParam.setClientId((rs.getString("c_client_id") == null ? "" : rs.getString("c_client_id")));
                mstParam.setClientSecret((rs.getString("c_client_secret") == null ? "" : rs.getString("c_client_secret")));
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "MASTERPARAMDAO:" + ex.getMessage());
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
            gc = null;

        }

        return mstParam;
    }

    public ApiConfig getUrlCompleteApi(String use_of_api) throws SQLException {
        ApiConfig mstParam = new ApiConfig();
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        query
                .append(" SELECT  ")
                .append(" c_j_username, ")
                .append(" c_j_password, ")
                .append(" c_url ")
                .append(" FROM app_fd_tis_mst_api ")
                .append(" WHERE c_use_of_api = ? ");

        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, use_of_api);

            rs = ps.executeQuery();
            while (rs.next()) {
                mstParam.setjUsername(rs.getString("c_j_username"));
                mstParam.setjPassword(rs.getString("c_j_password"));
                mstParam.setUrl(rs.getString("c_url"));
                mstParam.setApiToken("");
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "MASTERPARAMDAO_GET_COMPLTE_ACTIVITY_API:" + ex.getMessage());
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
            gc = null;
        }

        return mstParam;
    }

    public String getDomainWhiteList() throws SQLException, Exception {
        String result = "";
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT  ")
                .append(" VALUE ")
                .append(" FROM WF_SETUP ")
                .append(" WHERE PROPERTY = 'jsonpWhitelist' ");

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getString("VALUE");
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "MASTERPARAMDAO:" + ex.getMessage());
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
            gc = null;

        }

        return result;
    }
}
