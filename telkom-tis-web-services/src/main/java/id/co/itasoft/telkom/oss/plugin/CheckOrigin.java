/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mtaup
 */
public class CheckOrigin {

    LogInfo info = new LogInfo();
    public boolean checkingOrigin(String reqOrigin, HttpServletResponse response) throws Exception {
        boolean result = false;
        String[] allowedOriginList = getDomainWhiteList().split(";");
        String origin = reqOrigin;
        URL originUrl = null;
        if (reqOrigin != null) {
            if (allowedOriginList[0].equals("*")) {
                result = true;
            } else {
                try {
                    originUrl = new URL(origin);
                } catch (MalformedURLException ex) {
                }
                for (Object allowedOrigin : allowedOriginList) {
                    if (originUrl.getHost().equals(allowedOrigin)) {
                        result = true;
                        break;
                    }
                }
            }

        }

//        Pattern hostAllowedPattern = Pattern.compile("(.+\\.)*telkom\\.co.id", Pattern.CASE_INSENSITIVE);
        response.setHeader("Access-Control-Allow-Origin", null);
        response.setHeader("access-control-allow-credentials", null);
        return result;
    }

    public String getDomainWhiteList() throws SQLException, Exception {
        String result = "";
        id.co.itasoft.telkom.oss.plugin.function.GetConnections gc = new id.co.itasoft.telkom.oss.plugin.function.GetConnections();
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
            if (rs.next()) {
                result = rs.getString("VALUE");
            }
        } catch (Exception ex) {
          info.Error(getClass().getName(), ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
              info.Error(getClass().getName(), e.getMessage(), e);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
              info.Error(getClass().getName(), e.getMessage(), e);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
              info.Error(getClass().getName(), e.getMessage(), e);
            }

        }

        return result;
    }
}
