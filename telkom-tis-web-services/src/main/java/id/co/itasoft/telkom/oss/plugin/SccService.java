package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.*;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.GenerateSHA1Handler;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import id.co.itasoft.telkom.oss.plugin.scc.SccController;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Map;

// Penyesuaian Rule SCC+ Untuk Perluasan Piloting git point : #120 #124
public class SccService extends Element implements PluginWebSupport {

    private String pluginName = "Telkom New OSS-Ticket Incident Services-WEBSERVICE-SCC SERVICE";

    LogInfo logInfo = new LogInfo();
    LoadTicketDao loadTicketDao = new LoadTicketDao();
    MasterParam param = new MasterParam();
    MasterParamDao paramDao = new MasterParamDao();
    GenerateSHA1Handler generateSHA1Handler = new GenerateSHA1Handler();
    RESTAPI _RESTAPI = new RESTAPI();
    ConfigurationDao configurationDao = new ConfigurationDao();
    ArrayManipulation arrayManipulation = new ArrayManipulation();
    LogHistoryDao logHistoryDao = new LogHistoryDao();
    InsertTicketWorkLogsDao insertTicketWorkLogs = new InsertTicketWorkLogsDao();
    SccController sccController = new SccController();

    @Override
    public String renderTemplate(FormData formData, Map map) {
        return "";
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
        return "RestAPI untuk call scc yang ditrigger dari UI";
    }

    @Override
    public void webService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        outerLabel:
        try {

            WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil
                    .getApplicationContext()
                    .getBean("workflowUserManager");


            if (workflowUserManager.isCurrentUserAnonymous()) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                break outerLabel;
            }

            String USERNAME = workflowUserManager.getCurrentUsername();

            // GET ID FROM REFERER ON HEADER REQUEST
            String referrer = req.getHeader("referer");
            if (referrer == null || referrer.isEmpty()) {
                res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                break outerLabel;
            }

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
            TicketStatus ticketStatus = loadTicketDao.LoadTicketByParentId(PARENT_ID);

//            LogUtil.info(getClassName(), "ukur scc parentId :" + PARENT_ID);

            String serviceType = (ticketStatus.getServiceType() == null) ? "" : ticketStatus.getServiceType();

            JSONObject JSON = new JSONObject();
            switch (serviceType) {
                case "VOICE" :
                    JSON = sccController.SccVoice(ticketStatus);
                    break ;
                case "INTERNET":
                case "IPTV":
                    JSON = sccController.SccInternetIptv(ticketStatus);
                    break;
            }

            boolean boolCheckScc = JSON.getBoolean("status");
            int stsCodeScc = JSON.getInt("status_code");

            if(boolCheckScc) {

                JSON.write(res.getWriter());
            } else {
                String msg = JSON.getString("response_message");
                res.sendError(stsCodeScc, msg);
            }



        } catch (Exception ex) {
            logInfo.Error(getClassName(), ex.getMessage(), ex);
        }
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
}
