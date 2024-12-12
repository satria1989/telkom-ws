/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.function;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author itasoft
 */
public class OtherFunction {

  LogInfo logInfo = new LogInfo();
  public String AddingHoursToDate(int hours, String dt) {
    String resDate = "";
    try {
      SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String time = dt.toString();
      Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.add(Calendar.HOUR_OF_DAY, hours);

      resDate = format1.format(calendar.getTime());
    } catch (Exception ex) {
      logInfo.Log(getClass().getName(), ex.getMessage());
    }

    return resDate;
  }
}
