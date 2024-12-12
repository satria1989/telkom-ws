/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GamasTicketRelationDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.RetryImpactedServiceDao;
import id.co.itasoft.telkom.oss.plugin.dao.SelectCollections;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.RelatedRecords;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.attributeTicket;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class getSummaryImpactedService extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Get summary impacted service";
    LogInfo logInfo = new LogInfo();

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        return "";
    }

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
        return "";
    }

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        RetryImpactedServiceDao riDao;
        Ticket ticket;
        JSONObject resObj = null;
        List<attributeTicket> listAtt;
        try {
            String referrer = req.getHeader("referer");
            URL url = new URL(referrer);
            String queryStr = url.getQuery();
            String[] paramss = queryStr.split("&");
            JSONObject paramReferer = new JSONObject();
            for (String param2 : paramss) {
                String key = param2.substring(0, param2.indexOf('='));
                String val = param2.substring(param2.indexOf('=') + 1);
                paramReferer.put(key, val);
            }
            String PARENT_ID = paramReferer.getString("id");

            riDao = new RetryImpactedServiceDao();
            ticket = new Ticket();

            ticket = riDao.getProcessIdTicketByParentId(PARENT_ID);

            String sourceTicket = ticket.getSourceTicket() == null ? "" : ticket.getSourceTicket();
            String channel = ticket.getChannel() == null ? "" : ticket.getChannel();
            String classificationType = ticket.getClassificationType() == null ? "" : ticket.getClassificationType();
            String ticketId = ticket.getIdTicket() == null ? "" : ticket.getIdTicket();
            JSONObject obj = new JSONObject();
            JSONObject data = new JSONObject();
            listAtt = new ArrayList<attributeTicket>();
            if ("GAMAS".equalsIgnoreCase(sourceTicket) && "FISIK".equalsIgnoreCase(classificationType)) {
                SelectCollections sc = new SelectCollections();
                String[] attName = {"total_all_service", "total_service_info", "total_service_info_datin", "total_service_sdwan"};
                listAtt = sc.getAttributeTicket(ticketId, attName);
                if (listAtt.size() > 0) {
                    for (attributeTicket dataAtt : listAtt) {
                        data.put(dataAtt.getAttributeName(), dataAtt.getAttributeValue());
                    }
                    obj.put("data", data);
                    obj.write(res.getWriter());
                } else {
                    obj.put("status", "404");
                    obj.put("message", "data not found");
                }

            }

            resObj.write(res.getWriter());

        } catch (JSONException ex) {
            logInfo.Error(this.getClassName(), "error : ", ex);
        } catch (Exception ex) {
            logInfo.Error(this.getClassName(), "error : ", ex);
        } finally {
            riDao = null;
            ticket = null;
        }
    }

    private int relatingTickets(List<RelatedRecords> listRR, Ticket fticket) throws SQLException, ParseException {
        int i = 0;
        GamasTicketRelationDao daoGtr = new GamasTicketRelationDao();
        RetryImpactedServiceDao risDao = new RetryImpactedServiceDao();
        for (RelatedRecords relatedRecords : listRR) {
            if (!risDao.checkToRr(relatedRecords.getTicket_id())) {
                risDao.insertToRelatedRecord(relatedRecords.getTicket_id(), fticket.getParentId(), fticket.getIdTicket(), fticket.getClassificationType(), "RELOAD_RELATED_RECORD");
                risDao.updateIdGamasToChild2(fticket.getIdTicket(), relatedRecords.getParentId());
                risDao.insertWorkLogs(relatedRecords.getRecordId(), relatedRecords.getTicket_id(), relatedRecords.getOwnerGroup(), "This Ticket is Related to Gamas " + fticket.getIdTicket());
                if ("BACKEND".equalsIgnoreCase(relatedRecords.getTicket_status())) {
                    HashMap<String, String> paramCkWo = new HashMap<String, String>();
                    paramCkWo.put("externalID1", relatedRecords.getTicket_id());
                    daoGtr.getStatusWo(paramCkWo, relatedRecords.getTicket_id(), relatedRecords.getRecordId());
                    paramCkWo.clear();
                }

                if ("50".equals(relatedRecords.getChannel())) {
                    MasterParamDao paramDao = new MasterParamDao();
                    ApiConfig apiConfig = new ApiConfig();
                    Ticket ticket = new Ticket();
                    apiConfig = paramDao.getUrlapi("getActSolAndIncDom");
                    String actsolDesc = daoGtr.getActsolDescription(apiConfig, "ACTSOL", "59796");

                    ticket = daoGtr.getProcessIdTicketWithShk(relatedRecords.getTicket_id());
                    if ("1000003".equals(ticket.getState())) {
                        ApplicationContext ac = AppUtil.getApplicationContext();
                        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
                        daoGtr.updateStatusTikcet("SQMTOCLOSED", "1", "59796", actsolDesc, "Change of Status by Gamas", ticket.getParentId());
                        workflowManager.assignmentForceComplete(ticket.getProcessDefId().replace("#", ":"), ticket.getProcessId(), ticket.getActivityId(), "000000");

                    }
                }
                i++;
            }
        }
        return i;
    }
}