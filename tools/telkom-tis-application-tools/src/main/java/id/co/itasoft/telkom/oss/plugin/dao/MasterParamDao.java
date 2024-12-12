/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author itasoft
 */
public class MasterParamDao {

    LogInfo logInfo = new LogInfo();
    GetConnections gn;

    public MasterParam GetMasterParam(String param_code, String param_name) throws SQLException, Exception {
        gn = new GetConnections();
        PreparedStatement ps = null;
        ResultSet rs = null;
        MasterParam r = new MasterParam();
        StringBuilder query = new StringBuilder();//return object buat nilai atau isi kembalian nya
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
        if (!param_code.isEmpty()) {
            query.append(" AND c_param_code=? ");
        }
        if (!param_name.isEmpty()) {
            query.append(" AND c_param_name=? ");
        }
        Connection con = null;
        try {
            con = gn.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            int no = 0;
            if (!param_code.isEmpty()) {
                no += 1;
                ps.setString(no, param_code); //
            }
            if (!param_name.isEmpty()) {
                no += 1;
                ps.setString(no, param_name); //
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                r.setId(rs.getString("id"));
                r.setDateCreated(rs.getString("dateCreated"));
                r.setDateModified(rs.getString("dateModified"));
                r.setCreatedBy(rs.getString("createdBy"));
                r.setCreatedByName(rs.getString("createdByName"));
                r.setModifiedBy(rs.getString("modifiedBy"));
                r.setModifiedByName(rs.getString("modifiedByName"));
                r.setParamCode(rs.getString("c_param_code"));
                r.setParamDescription(rs.getString("c_param_description"));
                r.setParamName(rs.getString("c_param_name"));
                r.setFlag(rs.getString("c_flag"));

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
        return r;
    }

    public MasterParam getUrl(String use_of_api) throws SQLException, Exception {
        gn = new GetConnections();
        PreparedStatement ps = null;
        ResultSet rs = null;
        MasterParam mstParam = new MasterParam();
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
        Connection con = gn.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, use_of_api);
            rs = ps.executeQuery();
            while (rs.next()) {
                mstParam.setApi_id(rs.getString("c_api_id"));
                mstParam.setApi_key(rs.getString("c_api_key"));
                mstParam.setUrl(rs.getString("c_url"));
                mstParam.setApi_secret(rs.getString("c_api_secret"));
                mstParam.setClient_id(rs.getString("c_client_id"));
                mstParam.setClient_secret(rs.getString("c_client_secret"));
            }

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            try {
                rs.close();
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                ps.close();
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
            try {
                con.close();
            } catch (Exception ex) {
              logInfo.Log(getClass().getName(), ex.getMessage());
            }
        }

        return mstParam;
    }

    public ApiConfig getUrlCompleteApi(String use_of_api) throws Exception {
        gn = new GetConnections();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ApiConfig mstParam = new ApiConfig();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT  ")
                .append(" c_j_username, ")
                .append(" c_j_password, ")
                .append(" c_url ")
                .append(" FROM app_fd_tis_mst_api ")
                .append(" WHERE c_use_of_api = ? ");
        Connection con = gn.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, use_of_api);

            rs = ps.executeQuery();
            while (rs.next()) {
                mstParam.setjUsername(rs.getString("c_j_username"));
                mstParam.setjPassword(rs.getString("c_j_password"));
                mstParam.setUrl(rs.getString("c_url"));
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

        return mstParam;
    }

    public ApiConfig getUrlapi(String use_of_api) throws Exception {
        gn = new GetConnections();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ApiConfig mstParam = new ApiConfig();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT  ")
                .append(" c_api_id, ")
                .append(" c_api_key, ")
                .append(" c_use_of_api, ")
                .append(" c_url ")
                .append(" FROM app_fd_tis_mst_api ")
                .append(" WHERE c_use_of_api = ? ");

        Connection con = gn.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, use_of_api);

            rs = ps.executeQuery();
            while (rs.next()) {
                mstParam.setApiId(rs.getString("c_api_id"));
                mstParam.setApiKey(rs.getString("c_api_key"));
                mstParam.setUrl(rs.getString("c_url"));
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

        return mstParam;
    }

    public ApiConfig getUrlToken(String use_of_api) throws Exception {
        gn = new GetConnections();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ApiConfig mstParam = new ApiConfig();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT  ")
                .append(" c_client_id, ")
                .append(" c_client_secret, ")
                .append(" c_url ")
                .append(" FROM app_fd_tis_mst_api ")
                .append(" WHERE c_use_of_api = ? ");
        Connection con = gn.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, use_of_api);

            rs = ps.executeQuery();
            while (rs.next()) {
                mstParam.setClientId(rs.getString("c_client_id"));
                mstParam.setClientSecret(rs.getString("c_client_secret"));
                mstParam.setUrl(rs.getString("c_url"));
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

        return mstParam;
    }
    
    
}
