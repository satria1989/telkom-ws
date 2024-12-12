/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertImpactServiceDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketStatusLogsDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ListServiceInformation;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.HashMap;
import java.util.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * UNUSED
 * @author asani
 */
public class InsertImpactService extends DefaultApplicationPlugin {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Insert Impact Service";
    private String processIdIncident;
    LogInfo logInfo = new LogInfo();

    @Override
    public Object execute(Map map) {
//        LogUtil.info(getClassName(), "processIdIncident : " + processIdIncident);
//        String IN_DEVICEID = getPropertyString("deviceId");
        try {
            processIdIncident = getPropertyString("processIdIncident");

//            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
//            WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
//            String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
//            
//            LogUtil.info(this.getClass().getName(), "processId : " + processId);
            InsertTicketStatusLogsDao dao = new InsertTicketStatusLogsDao();
            InsertImpactServiceDao daoImp = new InsertImpactServiceDao();

            if (!"".equalsIgnoreCase(processIdIncident)) {
                TicketStatus r = new TicketStatus();
                r = dao.getTicketId(processIdIncident);
                if ("GAMAS".equalsIgnoreCase(r.getSourceTicket())) {

                    ListServiceInformation lsi = new ListServiceInformation();

//                    LogUtil.info(getClassName(), "rrr : " + r.getTicketId());
                    CallImpactService swa = new CallImpactService();
//
                    String jsonString = swa.callImpact(r);//assign your JSON String here
                    String assetnum = "";
                    JSONObject obj = new JSONObject(jsonString);
//                LogUtil.info(getClassName(), "responnya : " + jsonString);
//                LogUtil.info(getClassName(), "obj : " + obj);
                    String header = obj.getJSONObject("getImpactServiceManualResponse").getString("eaiBody");
                    String body = obj.getJSONObject("getImpactServiceManualResponse").getString("eaiBody");
//                LogUtil.info(getClassName(), "body : " + body);
                    JSONObject objBody = new JSONObject(body);
                    JSONArray arr = objBody.getJSONArray("OUT_IMPACT_RESULT");
//                LogUtil.info(getClassName(), "OUT IM : " + arr);
                    for (int i = 0; i < arr.length(); i++) {
                        assetnum = arr.getJSONObject(i).getString("assetnum");
//                    LogUtil.info(getClassName(), "assetnumnya : " + assetnum);

                        HashMap<String, String> paramSn = new HashMap<String, String>();
                        paramSn.put("service_id", assetnum);
                        lsi = daoImp.getServiceNumber(paramSn);
                        String realm = "telkom.net";
                        String operStatus = daoImp.getOperStatus(lsi.getServiceNumber(), realm);
//                        String region = lsi.getRegion();
                        daoImp.insertToTableService(assetnum, "LOS", processIdIncident, r.getTicketId(), lsi.getServiceNumber(), r.getEstimation(), r.getSymptomId(), r.getSymptomDesc(), lsi.getRegion());

                    }

                }
            } else {
              logInfo.Log(getClass().getName(), "PROCESS ID kosong");
            }

        } catch (Exception ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        }
        return null;
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
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClassName(), "/properties/settingWorkOrder.json", null, true, null);
    }

}
