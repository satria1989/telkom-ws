/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import id.co.itasoft.telkom.oss.plugin.dao.IboosterDao;
import id.co.itasoft.telkom.oss.plugin.dao.LoadTicketDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateTicketDao;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.iboosterDatin.ApiIboosterDatinModel;
import id.co.itasoft.telkom.oss.plugin.iboosterDatin.IboosterDatinService;
import id.co.itasoft.telkom.oss.plugin.model.ListIbooster;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author mtaup
 */
public class GetIbooster extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Get I-Booster";

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

    LogInfo logInfo = new LogInfo();

    @Override
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {
        JSONObject mainObj = new JSONObject();
        RESTAPI _RESTAPI = new RESTAPI();
        LoadTicketDao loadTicketDao = new LoadTicketDao();
        MasterParam masterParam = new MasterParam();
        MasterParamDao masterParamDao = new MasterParamDao();
        ArrayManipulation arrayManipulation = new ArrayManipulation();
        JSONObject jsonObj;
        Object dataResponse = new Object();
        TicketStatus ticketStatus = new TicketStatus();
        try {
            masterParam = masterParamDao.getUrl("get_ibooster");
            UpdateTicketDao updateTicketDao = new UpdateTicketDao();
            IboosterDao iboosterDao = new IboosterDao();
            ListIbooster listIbooster = new ListIbooster();
            String nd = hsr.getParameter("nd");
            String realm = hsr.getParameter("realm");
            String ticketId = (hsr.getParameter("ticketId") == "") ? "" : hsr.getParameter("ticketId");
            String token = _RESTAPI.getToken();
            ticketStatus = loadTicketDao.LoadTicketByIdTicket(ticketId);
            String[] custSegmentRetail = {"DCS", "PL-TSEL"};
            String customerSegment = ticketStatus.getCustomerSegment() != null ? ticketStatus.getCustomerSegment() : "";
            boolean custSegmentRetailBool = arrayManipulation.SearchDataOnArray(custSegmentRetail, customerSegment);
            JSONObject data = new JSONObject();
            int statusCode = 204; //default no content
            if (custSegmentRetailBool) {
                data = getIbooster(token, nd, realm, masterParam, ticketId);
                statusCode = data.getInt("status_code");

                hsr1.setStatus(statusCode);

                String msg = data.getString("msg");
                dataResponse = new JSONTokener(msg).nextValue();
                JSONObject reqb = new JSONObject();
                reqb.put("nd", nd);
                reqb.put("realm", realm);
                updateTicketDao.updateIbooster(data, listIbooster, ticketId);
                hsr1.setStatus(statusCode);
                hsr1.getWriter().print(dataResponse);
            } else {
                IboosterDatinService iboosterDatinService = new IboosterDatinService();
                ApiIboosterDatinModel apiIboosterDatinModel = iboosterDatinService.ukurIboosterDatin(ticketStatus, token);
                JSONObject res = new JSONObject(new ObjectMapper().writeValueAsString(apiIboosterDatinModel));
                logInfo.Log(this.getClass().getName(), "res for get ibooster == " + res);
                iboosterDao.updateIboosterDatin(apiIboosterDatinModel, ticketId);
                if (!apiIboosterDatinModel.getEaiStatus().getSrcResponseCode().isEmpty()) {
                    Pattern isNumeric = Pattern.compile("-?\\d+(\\.\\d+)?");
                    String srcResponseCode = apiIboosterDatinModel.getEaiStatus().getSrcResponseCode();
                    if (isNumeric.matcher(srcResponseCode).matches()) {
                        statusCode = Integer.parseInt(srcResponseCode);
                    }
                }
                hsr1.setStatus(statusCode);
                hsr1.getWriter().print(res);
            }

        } catch (Exception ex) {
            LogUtil.error(getClassName(), ex, ex.getMessage());
        } finally {

            masterParam = null;
            masterParamDao = null;
            _RESTAPI = null;
            dataResponse = null;
        }

    }

    private final OkHttpClient httpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private JSONObject getIbooster(String token, String nd, String realm, MasterParam masterParam, String ticketId) {
        id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao logElastic = new id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao();
        JSONObject response = new JSONObject();
        JSONObject data_obj = null;
        RESTAPI _RESTAPI = new RESTAPI();
        JSONObject reqGetIbooster = new JSONObject();
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("nd", nd)
                    .add("realm", realm)
                    .build();

            int sizeFormBody = ((FormBody) formBody).size();

            for (int i = 0; i < sizeFormBody; i++) {
                String key = ((FormBody) formBody).name(i);
                String value = ((FormBody) formBody).value(i);
                reqGetIbooster.put(key, value);
            }

            Request request = new Request.Builder()
                    .url(masterParam.getUrl())
                    .addHeader("Authorization", "Bearer " + token)
                    .post(formBody)
                    .build();

            response = _RESTAPI.CALLAPIHANDLER(request);

            int statusCode = response.getInt("status_code");

            logElastic.SENDHISTORY(ticketId,
                    "UKUR IBOOSTER",
                    masterParam.getUrl(),
                    "POST",
                    reqGetIbooster,
                    response,
                    statusCode
            );
        } catch (Exception ex) {
            LogUtil.error(this.getClassName(), ex, "Error : " + ex.getMessage());
        } finally {
            _RESTAPI = null;
            reqGetIbooster = null;
        }
        return response;
    }

}
