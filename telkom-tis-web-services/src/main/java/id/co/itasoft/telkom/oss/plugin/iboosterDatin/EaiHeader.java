package id.co.itasoft.telkom.oss.plugin.iboosterDatin;

public class EaiHeader {
    private String internalId = "";
    private String externalId = "";
    private String timestamp = "";
    private String responseTimestamp = "";

    // Getters and setters

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getResponseTimestamp() {
        return responseTimestamp;
    }

    public void setResponseTimestamp(String responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
