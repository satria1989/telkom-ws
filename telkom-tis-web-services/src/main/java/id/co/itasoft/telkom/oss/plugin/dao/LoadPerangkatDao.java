/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.GetConnections;
import id.co.itasoft.telkom.oss.plugin.model.Tree;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author itasoft
 */
public class LoadPerangkatDao {

    public List<Tree> getPerangkat(String classificationCode) throws SQLException, Exception {

        List<Tree> list = new ArrayList<Tree>();
        Tree s;
        GetConnections gc = new GetConnections();
        StringBuilder query = new StringBuilder();
        Connection con = gc.getJogetConnection();
        query
                .append(" SELECT ")
                .append(" A.C_DESCRIPTION as C_DESCRIPTION, ")
                .append(" A.C_PARENT as C_PARENT, ")
                .append(" A.C_CLASSSTRUCTURE_ID as C_CLASSSTRUCTURE_ID, ")
                .append(" A.C_CLASSIFICATION_CODE as C_CLASSIFICATION_CODE, ")
                .append(" (SELECT CASE WHEN COUNT(C_PARENT) > 0 THEN 1 ELSE 0 END TOTAL ")
                .append(" FROM APP_FD_CLASSIFICATION WHERE C_PARENT = A.C_CLASSIFICATION_CODE AND ROWNUM <=1) as CHILD ")
                .append(" FROM APP_FD_CLASSIFICATION A WHERE 1=1 ");

        if ("".equalsIgnoreCase(classificationCode)) {
            query.append(" AND A.C_PARENT = 'NATIONAL' ORDER BY A.C_DESCRIPTION ASC ");
        } else {
            query.append(" AND A.C_PARENT=? ORDER BY A.C_DESCRIPTION ASC ");
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query.toString());
            if (!"".equalsIgnoreCase(classificationCode)) {
                ps.setString(1, classificationCode);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                s = new Tree();
                s.setParent(rs.getString("C_PARENT"));
                s.setClassificationCode(rs.getString("C_CLASSIFICATION_CODE"));
                s.setDescription(rs.getString("C_DESCRIPTION"));
                s.setClassstructureId(rs.getString("C_CLASSSTRUCTURE_ID"));
                s.setHasChildren(rs.getBoolean("CHILD"));

                list.add(s);

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
            query = null;
            

        }

        return list;
    }

}
