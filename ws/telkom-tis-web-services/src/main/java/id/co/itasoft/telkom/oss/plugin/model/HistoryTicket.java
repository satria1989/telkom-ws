/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.model;

import java.sql.Timestamp;

/**
 *
 * @author mtaup
 */
public class HistoryTicket {
    String status;
    String ownerGroup;
    String level;
    String statusTracking;
    Timestamp dateCreted;
    String sdateCreated;
    String ticketId;
    String owner;
    String changeBy;
    String memo;
    String changeDate;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerGroup() {
        return ownerGroup;
    }

    public void setOwnerGroup(String ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatusTracking() {
        return statusTracking;
    }

    public void setStatusTracking(String statusTracking) {
        this.statusTracking = statusTracking;
    }

    public Timestamp getDateCreted() {
        return dateCreted;
    }

    public void setDateCreted(Timestamp dateCreted) {
        this.dateCreted = dateCreted;
    }

    public String getSdateCreated() {
        return sdateCreated;
    }

    public void setSdateCreated(String sdateCreated) {
        this.sdateCreated = sdateCreated;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getChangeBy() {
        return changeBy;
    }

    public void setChangeBy(String changeBy) {
        this.changeBy = changeBy;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    
    
    
}
