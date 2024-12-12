/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.model;

/**
 *
 * @author asani
 */
public class FinalcheckActModel {

    String action_status = "";
    String ticket_status = "";
    String ibooster = "";
    String scc_internet = "";
    String scc_voice = "";
    String serviceType = "";
    String serviceNo = ""; // reference no
    String realm = "telkom.net";
    String ownergroup = "";

    public String getOwnergroup() {
        return ownergroup;
    }

    public void setOwnergroup(String ownergroup) {
        this.ownergroup = ownergroup;
    }

    public String getAction_status() {
        return action_status;
    }

    public void setAction_status(String action_status) {
        this.action_status = action_status;
    }

    public String getTicket_status() {
        return ticket_status;
    }

    public void setTicket_status(String ticket_status) {
        this.ticket_status = ticket_status;
    }

    public String getIbooster() {
        return ibooster;
    }

    public void setIbooster(String ibooster) {
        this.ibooster = ibooster;
    }

    public String getScc_internet() {
        return scc_internet;
    }

    public void setScc_internet(String scc_internet) {
        this.scc_internet = scc_internet;
    }

    public String getScc_voice() {
        return scc_voice;
    }

    public void setScc_voice(String scc_voice) {
        this.scc_voice = scc_voice;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceNo() {
        return serviceNo;
    }

    public void setServiceNo(String serviceNo) {
        this.serviceNo = serviceNo;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

}
