/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.TtrEndToEndDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author mtaup
 */
public class TtrEndToEnd {
    LogInfo logInfo = new LogInfo();
    public String getTtrEndToEnd (String ticketId, String custSegment) throws SQLException{
        TtrEndToEndDao ttrDao = new TtrEndToEndDao();
        String ttr = "";
        
        ArrayList<String> ttrFromDb = ttrDao.getDataFromTicketStatus(ticketId, custSegment);
        ttr = calculateTime(ttrFromDb);
        ttrDao.updateTtr(ttr, ticketId);

        return ttr;
    }
    
    private String calculateTime(ArrayList<String> data) {
        String result = "";
        try {
            long tm = 0;
            for (String tmp : data) {
                String[] arr = tmp.split(":");
                tm += Integer.parseInt(arr[2]);
                tm += 60 * Integer.parseInt(arr[1]);
                tm += 3600 * Integer.parseInt(arr[0]);
            }

            long hh = tm / 3600;
            tm %= 3600;
            long mm = tm / 60;
            tm %= 60;
            long ss = tm;
            result = format(hh) + ":" + format(mm) + ":" + format(ss);
        }catch(Exception ex){
          logInfo.Log(getClass().getName(), ex.getMessage());
        }
        return result;
    }
    
    private static String format(long s) {
        if (s < 10) {
            return "0" + s;
        } else {
            return "" + s;
        }
    }
    
}
