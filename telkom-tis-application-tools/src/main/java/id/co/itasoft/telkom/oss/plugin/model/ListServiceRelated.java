/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author asani
 */
public class ListServiceRelated {

    String serviceNumber;
    String serviceCategory;
    String workzone;
    String workzoneName;
    String customerID;
    String customerCategory;
    String customerSegment;
    String customerName;
    String customerType;
    String witel;
    String referenceNumber;
    String serviceId;
    String region;
    String customerPriority;
    
    public ListServiceRelated(String serviceId){
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getWorkzone() {
        return workzone;
    }

    public void setWorkzone(String workzone) {
        this.workzone = workzone;
    }

    public String getWorkzoneName() {
        return workzoneName;
    }

    public void setWorkzoneName(String workzoneName) {
        this.workzoneName = workzoneName;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerCategory() {
        return customerCategory;
    }

    public void setCustomerCategory(String customerCategory) {
        this.customerCategory = customerCategory;
    }

    public String getCustomerSegment() {
        return customerSegment;
    }

    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getWitel() {
        return witel;
    }

    public void setWitel(String witel) {
        this.witel = witel;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCustomerPriority() {
        return customerPriority;
    }

    public void setCustomerPriority(String customerPriority) {
        this.customerPriority = customerPriority;
    }

}
