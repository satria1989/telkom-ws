package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketWorkLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import id.co.itasoft.telkom.oss.plugin.model.TicketWorkLogs;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Map;

public class SaveFieldChangesDetail extends Element implements PluginWebSupport {

    LogInfo logInfo = new LogInfo();
    LoadTicketDao loadTicketDao;
    InsertTicketWorkLogsDao insertTicketWorkLogsDao;
    InsertTicketHistoryDao ticketHistoryDao;

    public SaveFieldChangesDetail() {
        this.loadTicketDao = new LoadTicketDao();
        this.insertTicketWorkLogsDao = new InsertTicketWorkLogsDao();
        this.ticketHistoryDao = new InsertTicketHistoryDao();
    }

    private String pluginName = "Telkom New OSS - Ticket Incident Services - Save Field Changes Detail on Form";

    @Override
    public String renderTemplate(FormData formData, Map map) {
        return null;
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return "Menyimpan data perubahan ketika ada perubahan data di form khusus symptom dan workzone";
    }

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        outerLabel:
        try {
            WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");


            if (workflowUserManager.isCurrentUserAnonymous()) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                break outerLabel;
            }

            String referer = req.getHeader("referer");
            URL url = new URL(referer);
            String queryStr = url.getQuery();
            String[] paramss = queryStr.split("&");
            JSONObject paramReferer = new JSONObject();
            for (String param2 : paramss) {
                String key = param2.substring(0, param2.indexOf('='));
                String val = param2.substring(param2.indexOf('=') + 1);
                paramReferer.put(key, val);
            }
            String PARENTID = paramReferer.getString("id");

            if (PARENTID == null) {
                res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                break outerLabel;
            }

            // GET DETAIL TICKET FROM DATABASE
            TicketStatus ticketStatus = loadTicketDao.LoadTicketByParentId(PARENTID);

            String USERNAME = workflowUserManager.getCurrentUsername();
            String CURRENTWORKZONE = req.getParameter("workzone");
            String LASTWORKZONE = req.getParameter("workzoneBefore");
            String CURRENTSYMPTOM = req.getParameter("symtom");
            String LASTSYMPTOM = req.getParameter("symptomBefore");
            String CURRENTCUSTOMERSEGMENT = req.getParameter("customerSegment");
            String LASTCUSTOMERSEGMENT = req.getParameter("customerSegmentBefore");
            String CURRENTINCIDENTDOMAIN = (req.getParameter("incidentDomain").equals("-") ? "EMPTY" : req.getParameter("incidentDomain"));
            String LASTINCIDENTDOMAIN = (req.getParameter("incidentDomainBefore").equals("-") ? "EMPTY" : req.getParameter("incidentDomainBefore"));
            String CURRENTIBOOSTER = (req.getParameter("ibooster").isEmpty() ? "EMPTY" : req.getParameter("ibooster"));
            String LASTIBOOSTER = (req.getParameter("iboosterBefore").isEmpty() ? "EMPTY" : req.getParameter("iboosterBefore"));
            String CURRENTSCCINTERNET = (req.getParameter("sccInternet").isEmpty() ? "EMPTY" : req.getParameter("sccInternet"));
            String LASTSCCINTERNET = (req.getParameter("sccInternetBefore").isEmpty() ? "EMPTY" : req.getParameter("sccInternetBefore"));
            String CURRENTSCCVOICE = (req.getParameter("sccVoice").isEmpty() ? "EMPTY" : req.getParameter("sccVoice"));
            String LASTSCCVOICE = (req.getParameter("sccVoiceBefore").isEmpty() ? "EMPTY" : req.getParameter("sccVoiceBefore"));
            String CURRENTTSC = (req.getParameter("tsc").isEmpty() ? "EMPTY" : req.getParameter("tsc"));
            String LASTTSC = (req.getParameter("tscBefore").isEmpty() ? "EMPTY" : req.getParameter("tscBefore"));
            String CURRENTSOURCETICKET = (req.getParameter("sourceTicket").isEmpty() ? "EMPTY" : req.getParameter("sourceTicket"));
            String LASTSOURCETICKET = (req.getParameter("sourceTicketBefore").isEmpty() ? "EMPTY" : req.getParameter("sourceTicketBefore"));
            String CURRENTCHANNEL = (req.getParameter("channel").isEmpty() ? "EMPTY" : req.getParameter("channel"));
            String LASTCHANNEL = (req.getParameter("channelBefore").isEmpty() ? "EMPTY" : req.getParameter("channelBefore"));

            // ADD WORKLOG AND ADD TICKETSTATUS
            StringBuilder msg = new StringBuilder();
            String msgSummary = "CHANGE VALUE OF FIELDS";

            msg.append(" USERNAME : " + USERNAME + "<br>");
            if (!CURRENTWORKZONE.equalsIgnoreCase(LASTWORKZONE)) {
                msg.append("Workzone : " + LASTWORKZONE + " >> " + CURRENTWORKZONE + "<br>");
            }

            if (!CURRENTSYMPTOM.equalsIgnoreCase(LASTSYMPTOM)) {
                msg.append("Symptom : " + LASTSYMPTOM + " >> " + CURRENTSYMPTOM + "<br>");
            }

            if (!CURRENTCUSTOMERSEGMENT.equalsIgnoreCase(LASTCUSTOMERSEGMENT)) {
                msg.append("Customer Segment : " + LASTCUSTOMERSEGMENT + " >> " + CURRENTCUSTOMERSEGMENT + "<br>");
            }

            if (!CURRENTINCIDENTDOMAIN.equalsIgnoreCase(LASTINCIDENTDOMAIN)) {
                msg.append("Incident Domain : " + LASTINCIDENTDOMAIN + " >> " + CURRENTINCIDENTDOMAIN + "<br>");
            }

            if (!CURRENTIBOOSTER.equalsIgnoreCase(LASTIBOOSTER)) {
                msg.append("Ibooster : " + LASTIBOOSTER + " >> " + CURRENTIBOOSTER + "<br>");
            }

            if (!CURRENTSCCINTERNET.equalsIgnoreCase(LASTSCCINTERNET)) {
                msg.append("SCC Internet : " + LASTSCCINTERNET + " >> " + CURRENTSCCINTERNET + "<br>");
            }

            if (!CURRENTSCCVOICE.equalsIgnoreCase(LASTSCCVOICE)) {
                msg.append("SCC Voice : " + LASTSCCVOICE + " >> " + CURRENTSCCVOICE + "<br>");
            }

            if (!CURRENTTSC.equalsIgnoreCase(LASTTSC)) {
                msg.append("TSC : " + LASTTSC + " >> " + CURRENTTSC + "<br>");
            }

            if (!CURRENTSOURCETICKET.equalsIgnoreCase(LASTSOURCETICKET)) {
                msg.append("Source ticket : " + LASTSOURCETICKET + " >> " + CURRENTSOURCETICKET + "<br>");
            }

            if (!CURRENTCHANNEL.equalsIgnoreCase(LASTCHANNEL)) {
                msg.append("Channel : " + LASTCHANNEL + " >> " + CURRENTCHANNEL + "<br>");
            }

            // insert worklogs
//            insertTicketWorkLogsDao.InsertTicketWorkLogsByParentTicket(
//                    USERNAME,
//                    "CHANGES VALUE OF SYMPTOM OR WORKZONE OR CUSTOMER SEGMENT OR INCIDENT DOMAIN",
//                    msg.toString(),
//                    ticketStatus.getTicketId()
//            );
            insertTicketWorkLogsDao.InsertTicketWorkLogsByParentTicket(
                    USERNAME,
                    msgSummary,
                    msg.toString(),
                    ticketStatus.getTicketId()
            );

            logInfo.Log(getClassName(), "TICKETID WORKLOGS :" + ticketStatus.getTicketId());
            logInfo.Log(getClassName(), "MSG :: " + msg.toString());


        } catch (Exception ex) {
            LogUtil.error(this.getClassName(), ex, ex.getMessage());
        }
    }

    @Override
    public String getLabel() {
        return pluginName;
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return null;
    }
}
