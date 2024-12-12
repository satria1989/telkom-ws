/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.model;

/**
 *
 * @author rizki
 */
public class WorkOrder {
    
    public String woNumber;
    public String statusWoNumber;
    public String scheduleDate;
    public String Assigne;

    public String getWoNumber() {
        return woNumber;
    }

    public void setWoNumber(String woNumber) {
        this.woNumber = woNumber;
    }

    public String getStatusWoNumber() {
        return statusWoNumber;
    }

    public void setStatusWoNumber(String statusWoNumber) {
        this.statusWoNumber = statusWoNumber;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getAssigne() {
        return Assigne;
    }

    public void setAssigne(String Assigne) {
        this.Assigne = Assigne;
    }
    
    
}
