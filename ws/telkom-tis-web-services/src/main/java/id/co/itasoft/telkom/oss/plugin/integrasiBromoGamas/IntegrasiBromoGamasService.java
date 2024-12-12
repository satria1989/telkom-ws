/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.integrasiBromoGamas;

import id.co.itasoft.telkom.oss.plugin.*;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONObject;

/**
 *
 * @author 12
 */
public class IntegrasiBromoGamasService implements IntegrasiBromoGamas {

    public JSONObject integrasiBromoGamas(String ticketID, Ticket ticket) {
        JSONObject req = new JSONObject();
        JSONObject createOrderGamasRequest = new JSONObject();
        JSONObject eaiHeader = new JSONObject();
        JSONObject eaiBody = new JSONObject();
        if ("GAMAS".equalsIgnoreCase((ticket.getSourceTicket() == null) ? "" : ticket.getSourceTicket())) {
            String reg = (ticket.getRegion() == null) ? "" : ticket.getRegion();
            String witel = (ticket.getWitel() == null) ? "" : ticket.getWitel();
            String sto = (ticket.getSto() == null) ? "" : ticket.getSto();
            String summary = (ticket.getSummary() == null) ? "" : ticket.getSummary();
            String reportedDate = (ticket.getReportedDate() == null) ? "" : ticket.getReportedDate();
            String ownerGroup = (ticket.getOwnerGroup() == null) ? "" : ticket.getOwnerGroup();
            String custSegment = (ticket.getCust_segment() == null) ? "" : ticket.getCust_segment();
            String status = (ticket.getStatus() == null) ? "" : ticket.getStatus();
            String reportedBy = (ticket.getReportedBy() == null) ? "" : ticket.getReportedBy();
            String contactPhone = (ticket.getContactPhone() == null) ? "" : ticket.getContactPhone();
            String contactName = (ticket.getContactName() == null) ? "" : ticket.getContactName();
            String contactEmail = (ticket.getContactEmail() == null) ? "" : ticket.getContactEmail();
            String incident = (ticket.getSymptomDesc() == null) ? "" : ticket.getSymptomDesc();
            String symtomp = (ticket.getSymptom() == null) ? "" : ticket.getSymptom();
            String perangkat = (ticket.getPerangkat() == null) ? "" : ticket.getPerangkat();
            String technician = (ticket.getTechnician() == null) ? "" : ticket.getTechnician();

            try {
                eaiHeader.put("externalId", "");
                eaiHeader.put("timestamp", "");
                createOrderGamasRequest.put("eaiHeader", eaiHeader);

                eaiBody.put("tiket", ticketID);
                eaiBody.put("regional", reg);
                eaiBody.put("witel", witel);
                eaiBody.put("sto", sto);
                eaiBody.put("summary", summary);
                eaiBody.put("reported_date", reportedDate);
                eaiBody.put("owner_group", ownerGroup);
                eaiBody.put("customer_segment", custSegment);
                eaiBody.put("status", status);
                eaiBody.put("reported_by", reportedBy);
                eaiBody.put("contact_phone", contactPhone);
                eaiBody.put("contact_name", contactName);
                eaiBody.put("contact_email", contactEmail);
                eaiBody.put("incident", incident);
                eaiBody.put("symptomp", symtomp);
                eaiBody.put("perangkat", perangkat);
                eaiBody.put("technician", technician);
                eaiBody.put("device_name", perangkat);
                createOrderGamasRequest.put("eaiBody", eaiBody);

                req.put("createOrderGamasRequest", createOrderGamasRequest);

            } catch (Exception e) {
                LogUtil.error(getClass().getName(), e, e.getMessage());
            } finally {
                eaiBody = null;
                eaiHeader = null;
                createOrderGamasRequest = null;
            }
        }

        return req;
    }

}
