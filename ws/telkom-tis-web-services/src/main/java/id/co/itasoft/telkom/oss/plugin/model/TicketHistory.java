/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.model;

import java.sql.Timestamp;

/**
 *
 * @author asani
 */
public class TicketHistory {

    String c_ticketid;
    Timestamp dateCreated;
    String c_owner;
    String c_changeby;
    String c_memo;
    String c_changedate;
    String c_ownergroup;
    String c_assignedownergroup;
    String c_orgid;
    String c_siteid;
    String c_status;
    String c_tkstatusid;
    String c_statustracking;
    String datecreatedStr;

    public String getDatecreatedStr() {
        return datecreatedStr;
    }

    public void setDatecreatedStr(String datecreatedStr) {
        this.datecreatedStr = datecreatedStr;
    }
    
    public String getC_ticketid() {
        return c_ticketid;
    }

    public void setC_ticketid(String c_ticketid) {
        this.c_ticketid = c_ticketid;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getC_owner() {
        return c_owner;
    }

    public void setC_owner(String c_owner) {
        this.c_owner = c_owner;
    }

    public String getC_changeby() {
        return c_changeby;
    }

    public void setC_changeby(String c_changeby) {
        this.c_changeby = c_changeby;
    }

    public String getC_memo() {
        return c_memo;
    }

    public void setC_memo(String c_memo) {
        this.c_memo = c_memo;
    }

    public String getC_changedate() {
        return c_changedate;
    }

    public void setC_changedate(String c_changedate) {
        this.c_changedate = c_changedate;
    }

    public String getC_ownergroup() {
        return c_ownergroup;
    }

    public void setC_ownergroup(String c_ownergroup) {
        this.c_ownergroup = c_ownergroup;
    }

    public String getC_assignedownergroup() {
        return c_assignedownergroup;
    }

    public void setC_assignedownergroup(String c_assignedownergroup) {
        this.c_assignedownergroup = c_assignedownergroup;
    }

    public String getC_orgid() {
        return c_orgid;
    }

    public void setC_orgid(String c_orgid) {
        this.c_orgid = c_orgid;
    }

    public String getC_siteid() {
        return c_siteid;
    }

    public void setC_siteid(String c_siteid) {
        this.c_siteid = c_siteid;
    }

    public String getC_status() {
        return c_status;
    }

    public void setC_status(String c_status) {
        this.c_status = c_status;
    }

    public String getC_tkstatusid() {
        return c_tkstatusid;
    }

    public void setC_tkstatusid(String c_tkstatusid) {
        this.c_tkstatusid = c_tkstatusid;
    }

    public String getC_statustracking() {
        return c_statustracking;
    }

    public void setC_statustracking(String c_statustracking) {
        this.c_statustracking = c_statustracking;
    }

}
