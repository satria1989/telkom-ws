/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.OkHttpSingleton;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mtaup
 */
public class FindIncident extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Find Element";

    LogInfo logInfo = new LogInfo();

    @Override
    public String renderTemplate(FormData fd, Map map) {
        return "";
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "7.0";
    }

    @Override
    public String getDescription() {
        return pluginName;
    }

    @Override
    public String getLabel() {
        return pluginName;
    }

    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        String ticketId = req.getParameter("id_ticket");
        if (!workflowUserManager.isCurrentUserAnonymous()) {
            String username = workflowUserManager.getCurrentUsername().toUpperCase();

            try {
//                String profile = getPofileUserFromPersonApi(username);
                String ResProfile = getProfileUserFormDbIncident(username);
                String profile = "";
                
                if ("".equals(ResProfile)) {
                    Response response = null;
                    final MediaType JSON = MediaType.get("application/json; charset=utf-8");
                    org.json.simple.JSONObject objData = new org.json.simple.JSONObject();
                    objData.put("person_code", username.toUpperCase());
                    RequestBody body = RequestBody.create(JSON, objData.toString());
                    OkHttpSingleton localSingleton = OkHttpSingleton.getInstance();
                    MasterParamDao paramDao = new MasterParamDao();
                    MasterParam param = new MasterParam();
                    param = paramDao.getUrl("GetListPersonGroupMember");
                    Request request = new Request.Builder()
                            .url(param.getUrl() == null ? "" : param.getUrl().toString())
                            .post(body)
                            .build();
 
                    response = localSingleton.getClient().newCall(request).execute();

                    if (response.isSuccessful()) {
                        ResProfile = response.body().string();
                        JSONParser parse = new JSONParser();
                        org.json.simple.JSONObject data_obj = (org.json.simple.JSONObject) parse.parse(ResProfile);
                        String status = data_obj.get("status").toString();
                        profile = data_obj.get("Profile").toString();
                        if ("200".equals(status)) {
                            cekDataUser(username.toUpperCase(), ResProfile, profile);

                            response.body().close();
                            response.close();
                        } else {
                            response.body().close();
                            response.close();
                        }

                        data_obj = null;
                    } else {
                        response.body().close();
                        response.close();
                    }
                }
                // OLD CONDITION
//                String recorId = getReordId(ticketId);
//                JSONObject mainObj = new JSONObject();
//                if (!"".equals(recorId) && recorId != null) {
//                    mainObj.put("code", "200");
//                    mainObj.put("record_id", recorId);
//                } else {
//                    mainObj.put("code", "404");
//                    mainObj.put("record_id", "Not_Found");
//                }
                // NEW CONDITION

                JSONParser parse = new JSONParser();
                org.json.simple.JSONObject data_obj_2 = (org.json.simple.JSONObject) parse.parse(ResProfile);
                profile = (data_obj_2.get("Profile") == null ? "" : data_obj_2.get("Profile").toString());
                Map<String, String> dataTicket = getReordIdArray(ticketId);
                boolean status = false;
                JSONObject mainObj = new JSONObject();
                if (dataTicket != null) {
                    String custSegment = (dataTicket.get("custSegment") == null) ? "" : dataTicket.get("custSegment");
                    if ("retail".equalsIgnoreCase(profile) &&
                            ("DCS".equalsIgnoreCase(custSegment) ||
                            "PL-TSEL".equalsIgnoreCase(custSegment) ||
                            "".equalsIgnoreCase(custSegment))) {
                        status = true;
                    } else if ("enterprise".equalsIgnoreCase(profile) && ((!"DCS".equalsIgnoreCase(custSegment) && !"PL-TSEL".equalsIgnoreCase(custSegment)) || "".equalsIgnoreCase(custSegment))) {
                        status = true;
                    } else if ("all".equalsIgnoreCase(profile)) {
                        status = true;
                    }

                    if (status) {
                        if (!"".equals(dataTicket.get("parentId")) && dataTicket.get("parentId") != null) {
                            mainObj.put("code", "200");
                            mainObj.put("record_id", dataTicket.get("parentId"));
                            mainObj.put("status_ticket", dataTicket.get("statusTicket"));
                        } else {
                            mainObj.put("code", "404");
                            mainObj.put("record_id", "Not_Found");
                        }
                    } else {
                        mainObj.put("code", "404");
                        mainObj.put("record_id", "Not_Found");
                    }

                } else {
                    mainObj.put("code", "404");
                    mainObj.put("record_id", "Not_Found");
                }

                data_obj_2 = null;

                mainObj.write(res.getWriter());

            } catch (Exception ex) {
                logInfo.Error(ticketId, ticketId, ex);
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
        }
    }

    private String getReordId(String ticketId) throws SQLException, Exception {
        String result = "";
        GetConnections gc = new GetConnections();
        String query = "select c_parent_id from app_fd_ticket where c_id_ticket = ? ";

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("c_parent_id");
            }
        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "ex :" + e.getMessage());
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
        return result;
    }

    private String getPofileUserFromPersonApi(String username) throws JSONException, IOException, Exception {
        String profile = "";
        OkHttpSingleton localSingleton = OkHttpSingleton.getInstance();
        JSONObject paramListOwnerGroup = new JSONObject();
        paramListOwnerGroup.put("person_code", username);
        RESTAPI _RESTAPI = new RESTAPI();
        MasterParamDao mpd = new MasterParamDao();
        MasterParam masterParam = new MasterParam();
        masterParam = mpd.getUrl("GetListPersonGroupMember");
        RequestBody body = RequestBody.create(_RESTAPI.JSON, paramListOwnerGroup.toString());
        Request request;
        request = new Request.Builder()
                .url(masterParam.getUrl())
                .post(body)
                .build();

        Response response = localSingleton.getClient().newCall(request).execute();
        if (response.isSuccessful()) {
            String stringResponse = response.body().string();
            JSONParser parse = new JSONParser();
            org.json.simple.JSONObject data_obj = (org.json.simple.JSONObject) parse.parse(stringResponse);
            profile = data_obj.get("Profile").toString();
        }
        return profile;
    }

    private Map<String, String> getReordIdArray(String ticketId) throws SQLException, Exception {
//        String[] result = null;
        Map<String, String> result = null;
        GetConnections gc = new GetConnections();
        String query = "select c_parent_id, c_customer_segment from app_fd_ticket where c_id_ticket = ? and c_obsolete_status = '0'  ";

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = new HashMap<>();
                result.put("parentId", rs.getString("c_parent_id"));
                result.put("custSegment", rs.getString("c_customer_segment") == null ? "" : rs.getString("c_customer_segment"));
                result.put("statusTicket", "running");
            }else{
                result = getReordIdArrayRepo(ticketId);
            }
        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "ex :" + e.getMessage());
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
        return result;
    }
    
    private Map<String, String> getReordIdArrayRepo(String ticketId) throws SQLException, Exception {
//        String[] result = null;
        Map<String, String> result = null;
        GetConnections gc = new GetConnections();
        String query = "select c_parent_id, c_customer_segment from app_fd_ticket_repo where c_id_ticket = ? and c_obsolete_status = '0'  ";

        Connection con = gc.getJogetConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = new HashMap<>();
                result.put("parentId", rs.getString("c_parent_id"));
                result.put("custSegment", rs.getString("c_customer_segment") == null ? "" : rs.getString("c_customer_segment"));
                result.put("statusTicket", "closed");
            }
        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "ex :" + e.getMessage());
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
        return result;
    }
    
    

    public String getProfileUserFormDbIncident(String username) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        String data = "";
        String query = "select C_MENU_PERMISSION from app_fd_tis_user_permission where c_username = ? ";
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            ps = con.prepareStatement(query);

            ps.setString(1, username);
            try {
                result = ps.executeQuery();
                if (result.next()) {
                    data = result.getString("C_MENU_PERMISSION") == null ? "" : result.getString("C_MENU_PERMISSION");
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
        }
        return data;

    }

    public void cekDataUser(String currentUser, String response, String profile) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        String query = "select c_username from app_fd_tis_user_permission where c_username = ? ";
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            ps = con.prepareStatement(query);

            ps.setString(1, currentUser);
            try {
                result = ps.executeQuery();
                if (result.next()) {
                    updateDataUser(response, currentUser, profile);
                } else {
                    insertToTableMaster(response, currentUser, profile);
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
        }
    }

    public void insertToTableMaster(String result, String username, String profile) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        query.append(" insert into app_fd_tis_user_permission (id,dateCreated,dateModified,C_MENU_PERMISSION,c_username, c_profile) ")
                .append(" values (?,sysdate,sysdate,?,?,?) ");

        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());
            UuidGenerator uuid = UuidGenerator.getInstance();
            ps.setString(1, uuid.getUuid());
            ps.setString(2, result);
            ps.setString(3, username);
            ps.setString(4, profile);
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
        }
    }

    public void updateDataUser(String result, String username, String profile) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        query.append(" update app_fd_tis_user_permission set datemodified = sysdate, C_MENU_PERMISSION = ?, c_profile = ? where c_username = ? ");

        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, result);
            ps.setString(2, profile);
            ps.setString(3, username);
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
        }
    }

}
