/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mtaup
 */
public class ParseDate {

    private static final String[] formats = {"yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd HH:mm:ss",
        "MM/dd/yyyy HH:mm:ss",
        "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
        "MM/dd/yyyy'T'HH:mm:ss.SSSZ",
        "MM/dd/yyyy'T'HH:mm:ss.SSS",
        "MM/dd/yyyy'T'HH:mm:ssZ",
        "MM/dd/yyyy'T'HH:mm:ss",
        "yyyy:MM:dd HH:mm:ss"};

    public String parse(String sSourceDate) {
        String sDate = "";
        if ("".equals(sSourceDate) || sSourceDate == null) {
            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            sDate = simpleDateFormat.format(new Date());
        } else {
            for (String parse : formats) {
                SimpleDateFormat sdf = new SimpleDateFormat(parse);
                try {
                    sdf.parse(sSourceDate);
                    Date dSourceDate = sdf.parse(sSourceDate);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sDate = sdf2.format(dSourceDate);
                } catch (ParseException e) {
                }
            }
        }
        return sDate;
    }
}
