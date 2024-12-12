/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.model;

/**
 *
 * @author mtaup
 * 
 * c.id, c.c_id_ticket, c.c_parent_id, c.c_status, c.c_ticket_status, d.PROCESSID,
e.id as activity_id, e.ACTIVITYDEFINITIONID as activity_name, e.PDEFNAME as process_def_id, e.STATE
 */
public class ListKorektifTicketModel {
    String idTableTicket;
    String idTicket;
    String parentId;
    String status;
    String ticketStatus;
    String processId;
    String activityId;
    String activityName;
    String processDefId;
    String state;
    String sourceTicket;
    String workzone;
    String symptom;
    String channel;

    public String getIdTableTicket() {
        return idTableTicket;
    }

    public void setIdTableTicket(String idTableTicket) {
        this.idTableTicket = idTableTicket;
    }

    public String getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(String idTicket) {
        this.idTicket = idTicket;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSourceTicket() {
        return sourceTicket;
    }

    public void setSourceTicket(String sourceTicket) {
        this.sourceTicket = sourceTicket;
    }

    public String getWorkzone() {
        return workzone;
    }

    public void setWorkzone(String workzone) {
        this.workzone = workzone;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    
    
}
