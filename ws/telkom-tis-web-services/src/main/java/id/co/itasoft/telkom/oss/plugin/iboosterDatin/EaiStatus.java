package id.co.itasoft.telkom.oss.plugin.iboosterDatin;

public class EaiStatus {
    private String responseCode = "";
    private String responseMsg = "";
    private String srcResponseCode = "";
    private String srcResponseMsg = "";

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getSrcResponseCode() {
        return srcResponseCode;
    }

    public void setSrcResponseCode(String srcResponseCode) {
        this.srcResponseCode = srcResponseCode;
    }

    public String getSrcResponseMsg() {
        return srcResponseMsg;
    }

    public void setSrcResponseMsg(String srcResponseMsg) {
        this.srcResponseMsg = srcResponseMsg;
    }
}
