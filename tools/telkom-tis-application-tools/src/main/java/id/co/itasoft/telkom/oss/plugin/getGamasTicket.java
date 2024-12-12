/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertChildTicketGamasDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.GamasTicket;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joget.plugin.base.DefaultApplicationPlugin;

/**
 * UNUSED
 * @author mtaup
 */
public class getGamasTicket extends DefaultApplicationPlugin {

    public static String pluginName = "Telkom New OSS - Get Gamas Ticket";
    LogInfo logInfo = new LogInfo();
    
    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "7.0.0";
    }

    @Override
    public String getDescription() {
        return pluginName;
    }

    @Override
    public String getLabel() {
        return pluginName;
    }

    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return null;
    }

    @Override
    public Object execute(Map map) {

        InsertChildTicketGamasDao dao = new InsertChildTicketGamasDao();
        InsertChildTicketGamasDao ictgd = new InsertChildTicketGamasDao();
        InsertRelatedRecordDao daoTicket = new InsertRelatedRecordDao();
        TicketStatus r = new TicketStatus();
        try {
            String processId = "dfd16d89-3cae-4372-a90a-4c8eaf8c0ea5";

            r = dao.getTicketId(processId);
            List<GamasTicket> listGamasTicket = new ArrayList<>();
            boolean tikcetFoundStatus = false;

            listGamasTicket = ictgd.getPrentTicketLogic(r.getServiceType(), r.getClassification_type(), r.getRegion(), r.getWitel(), r.getWorkZone());
            String[] hierarchiGamas = {"NASIONAL", "REGIONAL", "WITEL", "WORKZONE"};
            for (String hierarchi : hierarchiGamas) {
//                LogUtil.info(this.getClassName(), "HIERARCHI : " + hierarchi);
                for (GamasTicket gamasTicket : listGamasTicket) {
                    if (hierarchi.equalsIgnoreCase(gamasTicket.getLevelGamas())) {

                        String parentGamas = gamasTicket.getParentId();
                        String idTicketGamas = gamasTicket.getIdTicket();
//                        insertChildTicketToParent(parentGamas, r.getTicketId(), idTicketGamas, r.getClassification_type());
//                        updateIdGamasToChild(idTicketGamas, processId);
//                        LogUtil.info(this.getClass().getName(), "ID_TICKET_GAMAS : " + idTicketGamas);
                        // LogUtil.info(this.getClass().getName(), "CHECK PARENT TICKET");

                        InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
                        HashMap<String, String> paramCkWo = new HashMap<String, String>();
//                        paramCkWo.put("externalID1", ticketId);
//                        irdao.getStatusWo(paramCkWo, ticketId);

                        tikcetFoundStatus = true;
                        break;
                    }
                }
                if (tikcetFoundStatus) {
//                    LogUtil.info(this.getClassName(), "Tiket gamas ditemukan");
                    break;
                }
            }

//                else if("REGIONAL".equalsIgnoreCase(gamasTicket.getLevelGamas())){
//                    LogUtil.info(this.getClass().getName(), "Level Gamas : "+gamasTicket.getLevelGamas());
//                    LogUtil.info(this.getClass().getName(), "ID TICKET : "+gamasTicket.getIdTicket());
//                    LogUtil.info(this.getClass().getName(), "parentId : "+gamasTicket.getParentId());
//                    LogUtil.info(this.getClass().getName(), "dateCereated : "+gamasTicket.getDateCreated());
//                    LogUtil.info(this.getClass().getName(), "serviceType : "+gamasTicket.getServiceType());
//                    LogUtil.info(this.getClass().getName(), "Region : "+gamasTicket.getRegion());
//                    LogUtil.info(this.getClass().getName(), "Witel : "+gamasTicket.getWitel());
//                    LogUtil.info(this.getClass().getName(), "Workzone : "+gamasTicket.getWorkzone());
//                    break;
//                }else if ("WITEL".equalsIgnoreCase(gamasTicket.getLevelGamas())){
//                    LogUtil.info(this.getClass().getName(), "Level Gamas : "+gamasTicket.getLevelGamas());
//                    LogUtil.info(this.getClass().getName(), "ID TICKET : "+gamasTicket.getIdTicket());
//                    LogUtil.info(this.getClass().getName(), "parentId : "+gamasTicket.getParentId());
//                    LogUtil.info(this.getClass().getName(), "dateCereated : "+gamasTicket.getDateCreated());
//                    LogUtil.info(this.getClass().getName(), "serviceType : "+gamasTicket.getServiceType());
//                    LogUtil.info(this.getClass().getName(), "Region : "+gamasTicket.getRegion());
//                    LogUtil.info(this.getClass().getName(), "Witel : "+gamasTicket.getWitel());
//                    LogUtil.info(this.getClass().getName(), "Workzone : "+gamasTicket.getWorkzone());
//                    break;
//                }else if("WORKZONE".equalsIgnoreCase(gamasTicket.getLevelGamas())){
//                    LogUtil.info(this.getClass().getName(), "Level Gamas : "+gamasTicket.getLevelGamas());
//                    LogUtil.info(this.getClass().getName(), "ID TICKET : "+gamasTicket.getIdTicket());
//                    LogUtil.info(this.getClass().getName(), "parentId : "+gamasTicket.getParentId());
//                    LogUtil.info(this.getClass().getName(), "dateCereated : "+gamasTicket.getDateCreated());
//                    LogUtil.info(this.getClass().getName(), "serviceType : "+gamasTicket.getServiceType());
//                    LogUtil.info(this.getClass().getName(), "Region : "+gamasTicket.getRegion());
//                    LogUtil.info(this.getClass().getName(), "Witel : "+gamasTicket.getWitel());
//                    LogUtil.info(this.getClass().getName(), "Workzone : "+gamasTicket.getWorkzone());
//                    break;
//                }
//            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            dao = null;
            ictgd = null;
            daoTicket = null;
            r = null;
        }
        return null;
    }

}
