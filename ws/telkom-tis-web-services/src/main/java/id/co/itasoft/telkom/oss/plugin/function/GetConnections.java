/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package id.co.itasoft.telkom.oss.plugin.function;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author asani
 */
public class GetConnections {

    public Connection getJogetConnection() throws Exception {
        //prod
//        String driver = "oracle.jdbc.driver.OracleDriver";
//        String url = "jdbc:oracle:thin:@10.6.12.27:1521/ossticketing";
//        String username = "NEWOSSOPR";
//        String password = "N3w_o5sOpR";
        
//         String driver = "oracle.jdbc.driver.OracleDriver";
//        String url = "jdbc:oracle:thin:@10.6.12.27:1521/ossticketing_srv1";
//        String username = "NEWOSSOPR";
//        String password = "N3w_o5sOpR";
        
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        
        // Dev
//        String driver = "oracle.jdbc.driver.OracleDriver";
//        String url = "jdbc:oracle:thin:@10.60.160.160:1521/newoss";
//        String username = "incident_dev";
//        String password = "joget#22";
        
//        Class.forName(driver).newInstance();
//        return DriverManager.getConnection(url, username, password);

        return con;
    }

}
