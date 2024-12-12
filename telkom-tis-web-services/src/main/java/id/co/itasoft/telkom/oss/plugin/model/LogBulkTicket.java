package id.co.itasoft.telkom.oss.plugin.model;

public class LogBulkTicket {

    private String ticketId;
    private String idTemplate;
    private String serviceId;
    private String filename;
    private String responseAPI;
    private String serviceNumber;

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getIdTemplate() {
        return idTemplate;
    }

    public void setIdTemplate(String idTemplate) {
        this.idTemplate = idTemplate;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getResponseAPI() {
        return responseAPI;
    }

    public void setResponseAPI(String responseAPI) {
        this.responseAPI = responseAPI;
    }
}
