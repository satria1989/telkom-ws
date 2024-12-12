/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.model;

/**
 *
 * @author Mujib
 */
public class GetStatus {

    private String actionStatus;
    private String pendingStatus;
    private String id;
    private String childId;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    

    public String getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(String actionStatus) {
        this.actionStatus = actionStatus;
    }

    public String getPendingStatus() {
        return pendingStatus;
    }

    public void setPendingStatus(String pendingStatus) {
        this.pendingStatus = pendingStatus;
    }
    
    
}
