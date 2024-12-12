/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.WhattshapLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/**
 * @author itasoft
 */
public class MasterParamDao {

    GetConnections gn = new GetConnections();

    public MasterParam GetMasterParam(String param_code, String param_name) throws SQLException, Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

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

        } catch (SQLException err) {
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
        }
        return r;
    }

    public WhattshapLog getReqSendWa(String id) throws SQLException, Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String result = "";
        StringBuilder query = new StringBuilder();
        WhattshapLog whattshapLog = new WhattshapLog();
        query
                .append(" SELECT  ")
                .append(" c_request, c_id_ticket, c_action ")
                .append(" FROM app_fd_log_history_wa_api ")
                .append(" WHERE id = ? ");
        Connection con = gn.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                whattshapLog.setRequest(rs.getString("c_request"));
                whattshapLog.setAction(rs.getString("c_action"));
                whattshapLog.setTicketId(rs.getString("c_id_ticket"));
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "MASTERPARAMDAO:" + ex.getMessage());
        } finally {
            query = null;
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

        return whattshapLog;
    }

    public MasterParam getUrl(String use_of_api) throws SQLException, Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        MasterParam mstParam = new MasterParam();
        gn = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT  ")
                .append(" c_api_id, ")
                .append(" c_api_key, ")
                .append(" c_use_of_api, ")
                .append(" c_url, ")
                .append(" c_api_secret, ")
                .append(" c_client_id, ")
                .append(" c_client_secret, ")
                .append(" c_j_username, ")
                .append(" c_j_password ")
                .append(" FROM app_fd_tis_mst_api ")
                .append(" WHERE c_use_of_api = ? ");
//        Connection con = ds.getConnection();
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
                mstParam.setjUsername(rs.getString("c_j_username"));
                mstParam.setjPassword(rs.getString("c_j_password"));
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "MASTERPARAMDAO:" + ex.getMessage());
        } finally {
            query = null;
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

        return mstParam;
    }

    public ApiConfig getUrlCompleteApi(String use_of_api) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ApiConfig mstParam = new ApiConfig();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT  ")
                .append(" c_j_username, ")
                .append(" c_j_password, ")
                .append(" c_url ")
                .append(" FROM app_fd_tis_mst_api ")
                .append(" WHERE c_use_of_api = ? ");
        Connection con = ds.getConnection();
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
            LogUtil.error(this.getClass().getName(), ex, "MASTERPARAMDAO_GET_COMPLTE_ACTIVITY_API:" + ex.getMessage());
        } finally {
            query = null;
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

        return mstParam;
    }

    public ApiConfig getUrlapi(String use_of_api) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ApiConfig mstParam = new ApiConfig();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT  ")
                .append(" c_api_id, ")
                .append(" c_api_key, ")
                .append(" c_use_of_api, ")
                .append(" c_url ")
                .append(" FROM app_fd_tis_mst_api ")
                .append(" WHERE c_use_of_api = ? ");

        Connection con = ds.getConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, use_of_api);

            rs = ps.executeQuery();
            while (rs.next()) {
                mstParam.setApiId(rs.getString("c_api_id"));
                mstParam.setApiKey(rs.getString("c_api_key"));
                mstParam.setUrl(rs.getString("c_url"));
            }
            rs.close();
            ps.close();

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "MASTERPARAMDAO_GET_COMPLTE_ACTIVITY_API:" + ex.getMessage());
        } finally {
//            mstParam = null;
            query = null;
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

        return mstParam;
    }

    public ApiConfig getUrlToken(String use_of_api) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ApiConfig mstParam = new ApiConfig();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT  ")
                .append(" c_client_id, ")
                .append(" c_client_secret, ")
                .append(" c_url ")
                .append(" FROM app_fd_tis_mst_api ")
                .append(" WHERE c_use_of_api = ? ");
        Connection con = ds.getConnection();
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
            LogUtil.error(this.getClass().getName(), ex, "MASTERPARAMDAO_GET_COMPLTE_ACTIVITY_API:" + ex.getMessage());
        } finally {
//            mstParam = null;
            query = null;
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

        return mstParam;
    }

    public String getCounterRetryWa(String ticketId, String action) throws SQLException, Exception {
        String result = "";
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" COUNT(*) AS TOTAL ")
                .append(" FROM APP_FD_LOG_HISTORY_WA_API ")
                .append(" WHERE c_id_ticket=? AND c_action=?");
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            ps.setString(2, action);

            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("TOTAL");
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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

        return result;
    }

    public String getUsername(String employeeCode) throws SQLException, Exception {
        String result = "";
        GetConnections gc = new GetConnections();
        String query = "select userid from DIR_EMPLOYMENT where EMPLOYEECODE = ? ";
        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, employeeCode);

            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("userid");
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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

        return result;
    }

    public void updateTokenGetPerangkat(String token) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        MasterParam mstParam = new MasterParam();
        gn = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" UPDATE APP_FD_TIS_MST_API set C_TOKEN = ? WHERE C_USE_OF_API = 'get_token_aggregator' ");
        Connection con = gn.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, token);
            rs = ps.executeQuery();
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "MASTERPARAMDAO:" + ex.getMessage());
        } finally {
            query = null;
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
    }

    public String getTokenFromMstApi(String useOfApi) throws Exception {
        String token = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        MasterParam mstParam = new MasterParam();
        gn = new GetConnections();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT c_token FROM app_fd_tis_mst_api WHERE c_use_of_api = ? ");
//        Connection con = ds.getConnection();
        Connection con = gn.getJogetConnection();
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, useOfApi);
            rs = ps.executeQuery();
            while (rs.next()) {
                token = rs.getString("c_token");
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "MASTERPARAMDAO:" + ex.getMessage());
        } finally {
            query = null;
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

        return token;
    }
}
