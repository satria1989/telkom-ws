/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.model;

import java.sql.Timestamp;

/**
 *
 * @author Tarkiman
 */
public class TicketStatus {

    private String id;
    private String ticketId;
    private String clasS;
    private String status;
    private String statusCurrent;
    private String changeBy;
    private String changeDate;
    private String memo;
    private String siteId;
    private String orgId;
    private String tkStatusId;
    private String ticketStatusId;
    private String statusTracking;
    private String owner;
    private String ownerGroup;
    private String assignedOwnerGroup;
    private String actionStatus;
    private String finalCheck;
    private String sourceTicket;
    private String customerSegment;
    private String pendingStatus;
    private Timestamp dateCreated;
    private Timestamp dateModified;
    private String customerId;
    private String customerName;
    private String code_validation;
    private String classification_type;
    private String serviceType;
    private String serviceNo;
    private String phone;
    private String source = "NOSSA";
    private String templateID;
    private String channel;
    private String serviceId;
    private String symptomId;
    private String symptomDesc;
    private String perangkat;
    private String sccResult = "scc_result";
    private String landingPage;
    private String createdTicketBy;
    private String addressCode;
    private String exernalTicketId;
    private String bookingId;
    private String classificationFlag;
    private String estimation;
    private String realm;
    private String closedBy;
    private String streetAddress;
    private String workZone;
    private String solution;
    private String hardComplaint;
    private String urgency;
    private String levetGamas;
    private String witel;
    private String region;
    private String actualSolution;
    private String ibooster;
    private String scc_internet;
    private String scc_voice;
    private String last_state;
    private String scc_code_validation;
    private String child_gamas;
    private String flagFcr;
    private String rkInformation;
    private String pen_timeout;
    private String subsidiary;
    private String impact;
    private String serviceCategory;
    private String woStatus;
    private String referenceNumber;
    private String technology;
    private String sccCode;
    private String reportedDate;
    private String summary;
    private String details;
    private String contactName;
    private String ticketIdGamas;

    public String getTicketIdGamas() {
        return ticketIdGamas;
    }

    public void setTicketIdGamas(String ticketIdGamas) {
        this.ticketIdGamas = ticketIdGamas;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(String reportedDate) {
        this.reportedDate = reportedDate;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getWorkZone() {
        return workZone;
    }

    public void setWorkZone(String workZone) {
        this.workZone = workZone;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getHardComplaint() {
        return hardComplaint;
    }

    public void setHardComplaint(String hardComplaint) {
        this.hardComplaint = hardComplaint;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getLevetGamas() {
        return levetGamas;
    }

    public void setLevetGamas(String levetGamas) {
        this.levetGamas = levetGamas;
    }

    public String getWitel() {
        return witel;
    }

    public void setWitel(String witel) {
        this.witel = witel;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getActualSolution() {
        return actualSolution;
    }

    public void setActualSolution(String actualSolution) {
        this.actualSolution = actualSolution;
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

    public String getLast_state() {
        return last_state;
    }

    public void setLast_state(String last_state) {
        this.last_state = last_state;
    }

    public String getScc_code_validation() {
        return scc_code_validation;
    }

    public void setScc_code_validation(String scc_code_validation) {
        this.scc_code_validation = scc_code_validation;
    }

    public String getChild_gamas() {
        return child_gamas;
    }

    public void setChild_gamas(String child_gamas) {
        this.child_gamas = child_gamas;
    }

    public String getFlagFcr() {
        return flagFcr;
    }

    public void setFlagFcr(String flagFcr) {
        this.flagFcr = flagFcr;
    }

    public String getRkInformation() {
        return rkInformation;
    }

    public void setRkInformation(String rkInformation) {
        this.rkInformation = rkInformation;
    }

    public String getPen_timeout() {
        return pen_timeout;
    }

    public void setPen_timeout(String pen_timeout) {
        this.pen_timeout = pen_timeout;
    }

    public String getSubsidiary() {
        return subsidiary;
    }

    public void setSubsidiary(String subsidiary) {
        this.subsidiary = subsidiary;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getWoStatus() {
        return woStatus;
    }

    public void setWoStatus(String woStatus) {
        this.woStatus = woStatus;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getSccCode() {
        return sccCode;
    }

    public void setSccCode(String sccCode) {
        this.sccCode = sccCode;
    }

    
    

    public String getExernalTicketId() {
        return exernalTicketId;
    }

    public void setExernalTicketId(String exernalTicketId) {
        this.exernalTicketId = exernalTicketId;
    }

    public String getStatusCurrent() {
        return statusCurrent;
    }

    public void setStatusCurrent(String statusCurrent) {
        this.statusCurrent = statusCurrent;
    }

    public String getFinalCheck() {
        return finalCheck;
    }

    public void setFinalCheck(String finalCheck) {
        this.finalCheck = finalCheck;
    }

    public String getSourceTicket() {
        return sourceTicket;
    }

    public void setSourceTicket(String sourceTicket) {
        this.sourceTicket = sourceTicket;
    }

    public String getCustomerSegment() {
        return customerSegment;
    }

    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
    }

    public String getPendingStatus() {
        return pendingStatus;
    }

    public void setPendingStatus(String pendingStatus) {
        this.pendingStatus = pendingStatus;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Timestamp getDateModified() {
        return dateModified;
    }

    public void setDateModified(Timestamp dateModified) {
        this.dateModified = dateModified;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCode_validation() {
        return code_validation;
    }

    public void setCode_validation(String code_validation) {
        this.code_validation = code_validation;
    }

    public String getClassification_type() {
        return classification_type;
    }

    public void setClassification_type(String classification_type) {
        this.classification_type = classification_type;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTemplateID() {
        return templateID;
    }

    public void setTemplateID(String templateID) {
        this.templateID = templateID;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSymptomId() {
        return symptomId;
    }

    public void setSymptomId(String symptomId) {
        this.symptomId = symptomId;
    }

    public String getSymptomDesc() {
        return symptomDesc;
    }

    public void setSymptomDesc(String symptomDesc) {
        this.symptomDesc = symptomDesc;
    }

    public String getPerangkat() {
        return perangkat;
    }

    public void setPerangkat(String perangkat) {
        this.perangkat = perangkat;
    }

    public String getSccResult() {
        return sccResult;
    }

    public void setSccResult(String sccResult) {
        this.sccResult = sccResult;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    public String getCreatedTicketBy() {
        return createdTicketBy;
    }

    public void setCreatedTicketBy(String createdTicketBy) {
        this.createdTicketBy = createdTicketBy;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
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

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getClasS() {
        return clasS;
    }

    public void setClasS(String clasS) {
        this.clasS = clasS;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChangeBy() {
        return changeBy;
    }

    public void setChangeBy(String changeBy) {
        this.changeBy = changeBy;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getTicketStatusId() {
        return ticketStatusId;
    }

    public void setTicketStatusId(String ticketStatusId) {
        this.ticketStatusId = ticketStatusId;
    }

    public String getTkStatusId() {
        return tkStatusId;
    }

    public void setTkStatusId(String tkStatusId) {
        this.tkStatusId = tkStatusId;
    }

    public String getStatusTracking() {
        return statusTracking;
    }

    public void setStatusTracking(String statusTracking) {
        this.statusTracking = statusTracking;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerGroup() {
        return ownerGroup;
    }

    public void setOwnerGroup(String ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

    public String getAssignedOwnerGroup() {
        return assignedOwnerGroup;
    }

    public void setAssignedOwnerGroup(String assignedOwnerGroup) {
        this.assignedOwnerGroup = assignedOwnerGroup;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getClassificationFlag() {
        return classificationFlag;
    }

    public void setClassificationFlag(String classificationFlag) {
        this.classificationFlag = classificationFlag;
    }

    public String getEstimation() {
        return estimation;
    }

    public void setEstimation(String estimation) {
        this.estimation = estimation;
    }

}
