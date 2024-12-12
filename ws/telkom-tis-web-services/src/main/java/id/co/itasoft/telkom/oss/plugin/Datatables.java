package id.co.itasoft.telkom.oss.plugin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author tarkiman
 */
public class Datatables extends Element implements PluginWebSupport {

    /*
Buat ngetest :
54.179.192.182:8080/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.Datatables/service?
     */
    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Datatables";

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        return "";
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "7.0.0";
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
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            CheckOrigin checkOrigin = new CheckOrigin();
            String origin = request.getHeader("Origin");
            boolean allowOrigin = checkOrigin.checkingOrigin(origin, response);

            if (allowOrigin) {

                String[] cols = {"id", "c_id_ticket", "c_ticket_number", "c_classification_path", "c_status"};
                String table = "app_fd_ticket";

                JSONObject result = new JSONObject();
                JSONArray array = new JSONArray();
                int amount = 10;
                int start = 0;
                int echo = 0;
                int col = 0;

                String id = "";
                String c_id_ticket = "";
                String c_ticket_number = "";
                String c_classification_path = "";
                String c_status = "";

                String dir = "asc";
                String sStart = request.getParameter("iDisplayStart");
                String sAmount = request.getParameter("iDisplayLength");
                String sEcho = request.getParameter("sEcho");
                String sCol = request.getParameter("iSortCol_0");
                String sdir = request.getParameter("sSortDir_0");

                id = request.getParameter("sSearch_0");
                c_id_ticket = request.getParameter("sSearch_1");
                c_ticket_number = request.getParameter("sSearch_2");
                c_classification_path = request.getParameter("sSearch_3");
                c_status = request.getParameter("sSearch_4");

                List<String> sArray = new ArrayList<String>();
                if (!id.equals("")) {
                    String sEngine = " id like '%" + id + "%'";
                    sArray.add(sEngine);
                    //or combine the above two steps as:
                    //sArray.add(" engine like '%" + engine + "%'");
                    //the same as followings
                }
                if (!c_id_ticket.equals("")) {
                    String sBrowser = " c_id_ticket like '%" + c_id_ticket + "%'";
                    sArray.add(sBrowser);
                }
                if (!c_ticket_number.equals("")) {
                    String sPlatform = " c_ticket_number like '%" + c_ticket_number + "%'";
                    sArray.add(sPlatform);
                }
                if (!c_classification_path.equals("")) {
                    String sVersion = " c_classification_path like '%" + c_classification_path + "%'";
                    sArray.add(sVersion);
                }
                if (!c_status.equals("")) {
                    String sGrade = " c_status like '%" + c_status + "%'";
                    sArray.add(sGrade);
                }

                String individualSearch = "";
                if (sArray.size() == 1) {
                    individualSearch = sArray.get(0);
                } else if (sArray.size() > 1) {
                    for (int i = 0; i < sArray.size() - 1; i++) {
                        individualSearch += sArray.get(i) + " and ";
                    }
                    individualSearch += sArray.get(sArray.size() - 1);
                }

                if (sStart != null) {
                    start = Integer.parseInt(sStart);
                    if (start < 0) {
                        start = 0;
                    }
                }
                if (sAmount != null) {
                    amount = Integer.parseInt(sAmount);
                    if (amount < 10 || amount > 100) {
                        amount = 10;
                    }
                }
                if (sEcho != null) {
                    echo = Integer.parseInt(sEcho);
                }
                if (sCol != null) {
                    col = Integer.parseInt(sCol);
                    if (col < 0 || col > 5) {
                        col = 0;
                    }
                }
                if (sdir != null) {
                    if (!sdir.equals("asc")) {
                        dir = "desc";
                    }
                }
                String colName = cols[col];
                int total = 0;

                DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
                StringBuilder query = new StringBuilder();
                query
                        .append("SELECT count(*) FROM ")
                        .append(table);

                Connection con = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    con = ds.getConnection();
                    ps = con.prepareStatement(query.toString());
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        total = rs.getInt("count(*)");
                    }

                } catch (SQLException e) {
                    LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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
                }

                int totalAfterFilter = total;
                //result.put("sEcho",echo);

                try {
                    String searchSQL = "";
                    String sql = "SELECT * FROM " + table;
                    String searchTerm = request.getParameter("sSearch");
                    String globeSearch = " where (id like '%" + searchTerm + "%'" +
                            " or c_id_ticket like '%" + searchTerm + "%'" +
                            " or c_ticket_number like '%" + searchTerm + "%'" +
                            " or c_classification_path like '%" + searchTerm + "%'" +
                            " or c_status like '%" + searchTerm + "%')";
                    if (searchTerm != "" && individualSearch != "") {
                        searchSQL = globeSearch + " and " + individualSearch;
                    } else if (individualSearch != "") {
                        searchSQL = " where " + individualSearch;
                    } else if (searchTerm != "") {
                        searchSQL = globeSearch;
                    }
                    sql += searchSQL;
                    sql += " order by " + colName + " " + dir;
                    sql += " limit " + start + ", " + amount;

                    try {
                        con = ds.getConnection();
                        ps = con.prepareStatement(query.toString());
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            JSONArray ja = new JSONArray();
                            ja.put(rs.getString("id"));
                            ja.put(rs.getString("c_id_ticket"));
                            ja.put(rs.getString("c_ticket_number"));
                            ja.put(rs.getString("c_classification_path"));
                            ja.put(rs.getString("c_status"));
                            array.put(ja);
                        }

                    } catch (SQLException e) {
                        LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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
                    }

                    String sql2 = "SELECT count(*) FROM " + table;
                    if (searchTerm != "") {
                        sql2 += searchSQL;

                        try {
                            con = ds.getConnection();
                            ps = con.prepareStatement(query.toString());
                            rs = ps.executeQuery();

                            while (rs.next()) {
                                totalAfterFilter = rs.getInt("count(*)");
                            }

                        } catch (SQLException e) {
                            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
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
                        }

                    }
                    result.put("iTotalRecords", total);
                    result.put("iTotalDisplayRecords", totalAfterFilter);
                    result.put("aaData", array);
                    response.setContentType("application/json");
                    response.setHeader("Cache-Control", "no-store");

                    result.write(response.getWriter());
//            out.print(result);
//            conn.close();
                } catch (Exception e) {
                    LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
                }

            } else {
                // Throw 403 status OR send default allow
                response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "");
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClassName(), ex, "Error : " + ex.getMessage());
        }

    }

}
