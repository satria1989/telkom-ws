package id.co.itasoft.telkom.oss.plugin.iboosterDatin;

import java.util.Objects;

public class EaiBody {
    String info = "";
    String message = "";
    String id_ukur = "NULL";
    String SID = "";
    String gangguan = "";
    String status_jaringan = "";
    String node_status = "";
    String hostname = "NULL";
    String identifier = "NULL";
    String type = "";
    String shelf = "";
    String slot = "";
    String port = "";
    String onu = "";
    String merk_ont = "";
    String vendor_id = "";
    String version_id = "";
    String reg_type = "";
    String onu_type_actual = "";
    String oper_status = "NULL";
    String admin_status = "";
    String fiber_length = "";
    String onu_temp = "";
    String onu_rx_pwr = "NULL";
    String onu_tx_pwr = "NULL";
    String olt_tx_pwr = "NULL";
    String olt_rx_pwr = "NULL";
    String olt_temp = "";
    String serial_number = "";
    String online_at = "";
    String offline_at = "";
    String info_direct = "";
    private String measurement_category = "UNSPEC";

    public String getAdmin_status() {
        return admin_status;
    }

    public void setAdmin_status(String admin_status) {
        this.admin_status = admin_status;
    }

    public String getFiber_length() {
        return fiber_length;
    }

    public void setFiber_length(String fiber_length) {
        this.fiber_length = fiber_length;
    }

    public String getGangguan() {
        return gangguan;
    }

    public void setGangguan(String gangguan) {
        this.gangguan = gangguan;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        if (!hostname.isEmpty()) {
            this.hostname = hostname;
        }
    }

    public String getId_ukur() {
        return id_ukur;
    }

    public void setId_ukur(String id_ukur) {
        if (!id_ukur.isEmpty()) {
            this.id_ukur = id_ukur;
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        if (!identifier.isEmpty()) {
            this.identifier = identifier;
        }
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo_direct() {
        return info_direct;
    }

    public void setInfo_direct(String info_direct) {
        this.info_direct = info_direct;
    }

    public String getMeasurement_category() {
        return measurement_category;
    }

    public void setMeasurement_category(String measurement_category) {
        this.measurement_category = measurement_category;
    }

    public String getMerk_ont() {
        return merk_ont;
    }

    public void setMerk_ont(String merk_ont) {
        this.merk_ont = merk_ont;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNode_status() {
        return node_status;
    }

    public void setNode_status(String node_status) {
        this.node_status = node_status;
    }

    public String getOffline_at() {
        return offline_at;
    }

    public void setOffline_at(String offline_at) {
        this.offline_at = offline_at;
    }

    public String getOlt_rx_pwr() {
        return olt_rx_pwr;
    }

    public void setOlt_rx_pwr(String olt_rx_pwr) {
        this.olt_rx_pwr = olt_rx_pwr;
    }

    public String getOlt_temp() {
        return olt_temp;
    }

    public void setOlt_temp(String olt_temp) {
        this.olt_temp = olt_temp;
    }

    public String getOlt_tx_pwr() {
        return olt_tx_pwr;
    }

    public void setOlt_tx_pwr(String olt_tx_pwr) {
        this.olt_tx_pwr = olt_tx_pwr;
    }

    public String getOnline_at() {
        return online_at;
    }

    public void setOnline_at(String online_at) {
        this.online_at = online_at;
    }

    public String getOnu() {
        return onu;
    }

    public void setOnu(String onu) {
        this.onu = onu;
    }

    public String getOnu_rx_pwr() {
        return onu_rx_pwr;
    }

    public void setOnu_rx_pwr(String onu_rx_pwr) {
        this.onu_rx_pwr = onu_rx_pwr;
    }

    public String getOnu_temp() {
        return onu_temp;
    }

    public void setOnu_temp(String onu_temp) {
        this.onu_temp = onu_temp;
    }

    public String getOnu_tx_pwr() {
        return onu_tx_pwr;
    }

    public void setOnu_tx_pwr(String onu_tx_pwr) {
        this.onu_tx_pwr = onu_tx_pwr;
    }

    public String getOnu_type_actual() {
        return onu_type_actual;
    }

    public void setOnu_type_actual(String onu_type_actual) {
        this.onu_type_actual = onu_type_actual;
    }

    public String getOper_status() {
        return oper_status;
    }

    public void setOper_status(String oper_status) {
        if (!oper_status.isEmpty()) {
            this.oper_status = oper_status;
        }
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getReg_type() {
        return reg_type;
    }

    public void setReg_type(String reg_type) {
        this.reg_type = reg_type;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getStatus_jaringan() {
        return status_jaringan;
    }

    public void setStatus_jaringan(String status_jaringan) {
        this.status_jaringan = status_jaringan;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getVersion_id() {
        return version_id;
    }

    public void setVersion_id(String version_id) {
        this.version_id = version_id;
    }
}
