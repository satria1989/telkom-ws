package id.co.itasoft.telkom.oss.plugin.iboosterDatin;

public class ApiIboosterDatinModel {
    private EaiHeader eaiHeader = new EaiHeader();
    private EaiStatus eaiStatus = new EaiStatus();
    private EaiBody eaiBody = new EaiBody();

    public EaiBody getEaiBody() {
        return eaiBody;
    }

    public void setEaiBody(EaiBody eaiBody) {
        this.eaiBody = eaiBody;
    }

    public EaiHeader getEaiHeader() {
        return eaiHeader;
    }

    public void setEaiHeader(EaiHeader eaiHeader) {
        this.eaiHeader = eaiHeader;
    }

    public EaiStatus getEaiStatus() {
        return eaiStatus;
    }

    public void setEaiStatus(EaiStatus eaiStatus) {
        this.eaiStatus = eaiStatus;
    }
}


