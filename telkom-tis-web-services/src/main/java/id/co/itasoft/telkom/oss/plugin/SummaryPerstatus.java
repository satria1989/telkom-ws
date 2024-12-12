/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.SummaryPerstatusDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.DataDurations;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mtaup
 */
public class SummaryPerstatus extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Summary per Status";

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

//        ApplicationContext ac = AppUtil.getApplicationContext();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

        if (!workflowUserManager.isCurrentUserAnonymous()) {
            SummaryPerstatusDao spDao = new SummaryPerstatusDao();
            try {
                String ticketId = req.getParameter("ticketId");
                List<DataDurations> listDuration = spDao.getDataDurations(ticketId);
                ArrayList valTmp = null;
                Map<String, ArrayList<String>> sumStatus = new HashMap<String, ArrayList<String>>();
                Map<String, ArrayList<String>> sumOwnerGroup = new HashMap<String, ArrayList<String>>();

                for (DataDurations dataDurations : listDuration) {
                    if (!sumStatus.containsKey(dataDurations.getStatus())) {
                        valTmp = new ArrayList();
                        valTmp.add(dataDurations.getDuration());
                        sumStatus.putIfAbsent(dataDurations.getStatus(), valTmp);
//                        valTmp.clear();
                    } else {
                        sumStatus.get(dataDurations.getStatus()).add(dataDurations.getDuration());
                    }

                    if (!sumOwnerGroup.containsKey(dataDurations.getOwnerGroup())) {
                        valTmp = new ArrayList();
                        valTmp.add(dataDurations.getDuration());
                        sumOwnerGroup.putIfAbsent(dataDurations.getOwnerGroup(), valTmp);
//                        valTmp.clear();
                    } else {
                        sumOwnerGroup.get(dataDurations.getOwnerGroup()).add(dataDurations.getDuration());
                    }
                }

                JSONObject objStatus = new JSONObject();
                JSONObject childObjStatus = null;
                JSONArray arr = new JSONArray();
                if (!sumStatus.isEmpty()) {
                    for (Map.Entry<String, ArrayList<String>> st : sumStatus.entrySet()) {
                        childObjStatus = new JSONObject();
                        String statusDuration = calculateTimes(st.getValue());
                        childObjStatus.putOpt("cumulative_status", st.getKey());
                        childObjStatus.putOpt("cumulative_time", statusDuration);
                        arr.put(childObjStatus);
                    }
                }
                objStatus.put("total", sumStatus.size());
                objStatus.put("size", sumStatus.size());
                objStatus.put("data", arr);

                JSONObject objOg = new JSONObject();
                JSONObject childObjOg = null;
                JSONArray arrOg = new JSONArray();
                if (!sumOwnerGroup.isEmpty()) {
                    for (Map.Entry<String, ArrayList<String>> sg : sumOwnerGroup.entrySet()) {
                        childObjOg = new JSONObject();
                        String statusDuration = calculateTimes(sg.getValue());
                        childObjOg.putOpt("cumulative_ownergroup", sg.getKey());
                        childObjOg.putOpt("cumulative_time_og", statusDuration);
                        arrOg.put(childObjOg);
                    }
                }
                objOg.put("total", sumOwnerGroup.size());
                objOg.put("size", sumOwnerGroup.size());
                objOg.put("data", arrOg);

                JSONObject mainObj = new JSONObject();
                mainObj.append("summary_status", objStatus);
                mainObj.append("summary_og", objOg);

                mainObj.write(res.getWriter());

            } catch (SQLException ex) {
                logInfo.Log(getClass().getSimpleName(), ex.getMessage());
            } catch (JSONException ex) {
                logInfo.Log(getClass().getSimpleName(), ex.getMessage());
            }
        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
        }
    }

    private String calculateTimes(ArrayList<String> data) {
        String result = "";
        try {
            long tm = 0;
            for (String tmp : data) {
                String[] arr = tmp.split(":");
                tm += Integer.parseInt(arr[2]);
                tm += 60 * Integer.parseInt(arr[1]);
                tm += 3600 * Integer.parseInt(arr[0]);
            }

            long hh = tm / 3600;
            tm %= 3600;
            long mm = tm / 60;
            tm %= 60;
            long ss = tm;
            result = format(hh) + ":" + format(mm) + ":" + format(ss);
        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "error : " + e.getMessage());
        }
        return result;
    }

    private String calculateTime(String data) {
        String result = "";
        String[] datas = data.split("#");
        try {
            long tm = 0;
            for (String tmp : datas) {
                String[] arr = tmp.split(":");
                tm += Integer.parseInt(arr[2]);
                tm += 60 * Integer.parseInt(arr[1]);
                tm += 3600 * Integer.parseInt(arr[0]);
            }

            long hh = tm / 3600;
            tm %= 3600;
            long mm = tm / 60;
            tm %= 60;
            long ss = tm;
            result = format(hh) + ":" + format(mm) + ":" + format(ss);
        } catch (Exception e) {
        }
        return result;
    }

    private static String format(long s) {
        if (s < 10) {
            return "0" + s;
        } else {
            return "" + s;
        }
    }

}
