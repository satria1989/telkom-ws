/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin.integrasiBromoGamas;

import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import org.json.JSONObject;

/**
 *
 * @author 12
 */
public interface IntegrasiBromoGamas {
    JSONObject integrasiBromoGamas(String ticketID, Ticket ticket);
}
