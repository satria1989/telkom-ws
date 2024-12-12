/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author mtaup
 */
public class AttachmentTicketDao {

    LogInfo info = new LogInfo();

    public void updateFileNameOnTicket(String fileName, String TicketID) {
        GetConnections gc = new GetConnections();
        Connection con = null;
        PreparedStatement ps = null;

        try {

            con = gc.getJogetConnection();

            StringBuilder query = new StringBuilder();
            query
                    .append(" UPDATE APP_FD_TICKET  ")
                    .append(" SET c_attachments = ")
                    .append("     CASE ")
                    .append("         WHEN c_attachments IS NOT NULL THEN c_attachments || ';' || ? ")
                    .append("         ELSE ? ")
                    .append("     END ")
                    .append(" where c_id_ticket = ? ");

            ps = con.prepareStatement(query.toString());
            ps.setString(1, fileName);
            ps.setString(2, fileName);
            ps.setString(3, TicketID);
            ps.executeUpdate();

        } catch (Exception ex) {
            info.Error(getClass().getName(), ex.getMessage(), ex);
        } finally {
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

    }
}
