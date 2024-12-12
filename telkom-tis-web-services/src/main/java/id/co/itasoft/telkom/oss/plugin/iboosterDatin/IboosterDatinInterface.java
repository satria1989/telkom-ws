/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.iboosterDatin;

import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import org.json.JSONObject;

/**
 *
 * @author 12
 */
public interface IboosterDatinInterface {
    ApiIboosterDatinModel ukurIboosterDatin (TicketStatus ticketStatus, String TOKEN);
}
