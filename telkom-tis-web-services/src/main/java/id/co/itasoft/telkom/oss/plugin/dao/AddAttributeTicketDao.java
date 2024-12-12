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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author mtaup
 */
public class AddAttributeTicketDao {

    GetConnections gc = new GetConnections();
    LogInfo info = new LogInfo();

    public int CheckAttribute(String idTicket, String attribteName, String attributeVal) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        int countInsert = 0;
        int countDuplicate = 0;

        try {
//            ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            StringBuilder query = new StringBuilder();

            query.append(" SELECT  c_attribute_name from app_fd_attribute_ticket ");
            query.append(" where c_attribute_name = ? and c_ticket_id = ? ");

            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            rs = ps.executeQuery();
            // LogUtil.info(this.getClass().getName(), "TICKET ID:" + ticketId);
            if (rs.next()) {
                countDuplicate++;
            } else {
                insertAttributeTicket(idTicket, attribteName, attributeVal);
                countInsert++;
            }

        } catch (Exception ex) {
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
        }
        count = countInsert;
        return count;
    }

    public void insertAttributeTicket(String idTicket, String attributeName, String attributeVal) {
        Connection con = null;
        PreparedStatement ps = null;
        UUID uuid = UUID.randomUUID();
        String StringUuid = uuid.toString();

        String query = "INSERT INTO app_fd_attribute_ticket (id, dateCreated, c_ticket_id, c_attribute_name, c_attribute_value ) VALUES (?, sysdate, ?, ?, ?) ";

        try {
            con = gc.getJogetConnection();
            if (!con.isClosed()) {
                ps = con.prepareStatement(query);
                ps.setString(1, StringUuid);
                ps.setString(2, idTicket);
                ps.setString(3, attributeName);
                ps.setString(4, attributeVal);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            info.Log(getClass().getName(), e.getMessage());
        } catch (Exception ex) {
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
        }
    }

    public void updateGuaranteStatus(String guaranteStatus, String idTicket) {
        PreparedStatement ps = null;
        Connection con = null;
        StringBuilder query = new StringBuilder();
        try {
            con = gc.getJogetConnection();
            query
                    .append(" UPDATE app_fd_ticket SET ")
                    .append(" c_guarante_status = ? ")
                    .append(" WHERE c_id_ticket = ? ");
            ps = con.prepareStatement(query.toString());
            ps.setString(1, guaranteStatus);
            ps.setString(2, idTicket);
            ps.executeUpdate();
        } catch (Exception ex) {
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
        }
    }
    
    public void updateSummary(String attributeValue, String idTicket) {
        PreparedStatement ps = null;
        Connection con = null;
        StringBuilder query = new StringBuilder();
        try {
            con = gc.getJogetConnection();
            query
                    .append(" UPDATE app_fd_ticket SET ")
                    .append(" c_summary  = 'TSA ' || ? || ' - ' || c_summary ")
                    .append(" WHERE c_id_ticket = ? ");
            ps = con.prepareStatement(query.toString());
            ps.setString(1, attributeValue);
            ps.setString(2, idTicket);
            ps.executeUpdate();
        } catch (Exception ex) {
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
        }
    }
    
    public String getReportedDate(String ticketId) throws Exception {
        String result = "";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        query.append("select c_reported_date ")
                .append("from app_fd_ticket ")
                .append("where c_id_ticket = ? ");

        try {
            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, ticketId);
            rs = ps.executeQuery();

            if (rs.next()) {
                result = rs.getString("c_reported_date");
            }

        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error message di sini : " + e.getMessage());
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

            query = null;
        }
        return result;
    }
    
    public List<String> getAttribute(String idTicket) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> data = new ArrayList<>();

        try {
            StringBuilder query = new StringBuilder();

            query.append(" SELECT c_attribute_name from app_fd_attribute_ticket ");
            query.append(" where c_ticket_id = ? ");

            con = gc.getJogetConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, idTicket);
            rs = ps.executeQuery();
            while (rs.next()) {
                data.add(rs.getString("c_attribute_name"));
            }

        } catch (Exception ex) {
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
        }
        return data;
    }
    
    public void updateValueAttribute(String attributeName, String attributeValue, String idTicket) {
        PreparedStatement ps = null;
        Connection con = null;
        StringBuilder query = new StringBuilder();
        try {
            con = gc.getJogetConnection();
            query
                    .append(" UPDATE app_fd_attribute_ticket SET ")
                    .append(" dateModified = sysdate ")
                    .append(" ,c_attribute_value = ? ")
                    .append(" WHERE c_ticket_id = ? ")
                    .append(" AND c_attribute_name = ? ");
            ps = con.prepareStatement(query.toString());
            ps.setString(1, attributeValue);
            ps.setString(2, idTicket);
            ps.setString(3, attributeName);
            ps.executeUpdate();
        } catch (Exception ex) {
            info.Log(getClass().getName(), ex.getMessage());
        } finally {
            query = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                info.Log(getClass().getName(), ex.getMessage());
            }
        }
    }
}
