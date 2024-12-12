/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.model;

/**
 *
 * @author mtaup
 */
public class AddImpactedServiceModel {
    String id_ticket;
    String[] service_id;

    public String getId_ticket() {
        return id_ticket;
    }

    public void setId_ticket(String id_ticket) {
        this.id_ticket = id_ticket;
    }

    public String[] getService_id() {
        return service_id;
    }

    public void setService_id(String[] service_id) {
        this.service_id = service_id;
    }
    
    
}
