/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.model;

import java.sql.Timestamp;

/**
 *
 * @author itasoft
 */
public class ImpactedService {

    private String ticket_id;
    private String service_id;
    private Timestamp datecreated;
    private String ibooster_oper_status;
    private String estimation;
    private String service_number;
    private String symptomp_des;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }

    public Timestamp getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Timestamp datecreated) {
        this.datecreated = datecreated;
    }

    public String getEstimation() {
        return estimation;
    }

    public void setEstimation(String estimation) {
        this.estimation = estimation;
    }

    public String getService_number() {
        return service_number;
    }

    public void setService_number(String service_number) {
        this.service_number = service_number;
    }

    public String getSymptomp_des() {
        return symptomp_des;
    }

    public void setSymptomp_des(String symptomp_des) {
        this.symptomp_des = symptomp_des;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getIbooster_oper_status() {
        return ibooster_oper_status;
    }

    public void setIbooster_oper_status(String ibooster_oper_status) {
        this.ibooster_oper_status = ibooster_oper_status;
    }

}
