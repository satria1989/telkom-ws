/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertChildTicketGamasDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertImpactServiceDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.GamasTicket;
import id.co.itasoft.telkom.oss.plugin.model.ListServiceInformation;
import id.co.itasoft.telkom.oss.plugin.model.RelatedRecords;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author mtaup
 */
public class InsertChildTicketGamas extends DefaultApplicationPlugin {

    public static String pluginName = "Telkom New OSS - Insert Child Ticket Gamas";
    private String processIdIncident;
    InsertChildTicketGamasDao dao = new InsertChildTicketGamasDao();
    LogInfo logInfo = new LogInfo();

    @Override
    public Object execute(Map map) {
        List<GamasTicket> listGamasTicket = new ArrayList<>();
        TicketStatus r = new TicketStatus();
        try {
            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
<<<<<<< HEAD
            String processIdDD = appService.getOriginProcessId(workflowAssignment.getProcessId());

            final String processId = processIdDD;

            InsertChildTicketGamasDao ictgd = new InsertChildTicketGamasDao();
            InsertRelatedRecordDao daoTicket = new InsertRelatedRecordDao();

            r = dao.getTicketId(processId);
            // function untuk tiket anak (individu ==> source ticket != GAMAS) 
            if (!"GAMAS".equalsIgnoreCase(r.getSourceTicket()) && !"FCR".equalsIgnoreCase(r.getFlagFcr())) {
//                LogUtil.info(this.getClassName(), "processid gamas relation parent : " + processId);
                if ("FISIK".equalsIgnoreCase(r.getClassification_type())) {
                    if (!"BACKEND".equalsIgnoreCase(r.getTicketStatusId())) {
                        ictgd.checkParentTicketFisik(r.getServiceId(), processId, r.getClassification_type(), r.getTicketId(), r.getId(), r.getOwnerGroup());
                    }

                } else {
                    boolean tikcetFoundStatus = false;
                    listGamasTicket = ictgd.getPrentTicketLogic(r.getServiceType(), r.getClassification_type(), r.getRegion(), r.getWitel(), r.getWorkZone());
                    String[] hierarchiGamas = {"NASIONAL", "REGIONAL", "WITEL", "WORKZONE"};
                    for (String hierarchi : hierarchiGamas) {
                        for (GamasTicket gamasTicket : listGamasTicket) {
                            if (hierarchi.equalsIgnoreCase(gamasTicket.getLevelGamas())) {
                                if (hierarchi.equalsIgnoreCase("NASIONAL")) {
                                    ictgd.insertChildTicketToParent(gamasTicket.getParentId(), r.getTicketId(), gamasTicket.getIdTicket(), r.getClassification_type());
                                    ictgd.updateIdGamasToChild(gamasTicket.getIdTicket(), "TRUE", processId);
                                    ictgd.insertWorkLogs(r.getId(), r.getTicketId(), r.getOwnerGroup(), "This Ticket is Related to Gamas " + gamasTicket.getIdTicket());
                                    InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
                                    if ("BACKEND".equalsIgnoreCase(r.getTicketStatusId())) {
                                        HashMap<String, String> paramCkWo = new HashMap<String, String>();
                                        paramCkWo.put("externalID1", r.getTicketId());
                                        irdao.getStatusWo(paramCkWo, r.getTicketId(), "checkWorkORder - GAMAS");
                                    }
                                    tikcetFoundStatus = true;
                                    break;
                                } else if (hierarchi.equalsIgnoreCase("REGIONAL") &&
                                         "REGIONAL".equalsIgnoreCase(gamasTicket.getLevelGamas()) &&
                                         r.getRegion().equalsIgnoreCase(gamasTicket.getRegion())) {

                                    ictgd.insertChildTicketToParent(gamasTicket.getParentId(), r.getTicketId(), gamasTicket.getIdTicket(), r.getClassification_type());
                                    ictgd.updateIdGamasToChild(gamasTicket.getIdTicket(), "TRUE", processId);
                                    ictgd.insertWorkLogs(r.getId(), r.getTicketId(), r.getOwnerGroup(), "This Ticket is Related to Gamas " + gamasTicket.getIdTicket());
                                    InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
                                    if ("BACKEND".equalsIgnoreCase(r.getTicketStatusId())) {
                                        HashMap<String, String> paramCkWo = new HashMap<String, String>();
                                        paramCkWo.put("externalID1", r.getTicketId());
                                        irdao.getStatusWo(paramCkWo, r.getTicketId(), "checkWorkORder - GAMAS");
                                    }
                                    tikcetFoundStatus = true;
                                    break;
                                } else if (hierarchi.equalsIgnoreCase("WITEL") &&
                                         "WITEL".equalsIgnoreCase(gamasTicket.getLevelGamas()) &&
                                         r.getWitel().equalsIgnoreCase(gamasTicket.getWitel())) {

                                    ictgd.insertChildTicketToParent(gamasTicket.getParentId(), r.getTicketId(), gamasTicket.getIdTicket(), r.getClassification_type());
                                    ictgd.updateIdGamasToChild(gamasTicket.getIdTicket(), "TRUE", processId);
                                    ictgd.insertWorkLogs(r.getId(), r.getTicketId(), r.getOwnerGroup(), "This Ticket is Related to Gamas " + gamasTicket.getIdTicket());
                                    InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
                                    if ("BACKEND".equalsIgnoreCase(r.getTicketStatusId())) {
                                        HashMap<String, String> paramCkWo = new HashMap<String, String>();
                                        paramCkWo.put("externalID1", r.getTicketId());
                                        irdao.getStatusWo(paramCkWo, r.getTicketId(), "checkWorkORder - GAMAS");
                                    }
                                    tikcetFoundStatus = true;
                                    break;
                                } else if (hierarchi.equalsIgnoreCase("WORKZONE") &&
                                         "WORKZONE".equalsIgnoreCase(gamasTicket.getLevelGamas()) &&
                                         r.getWorkZone().equalsIgnoreCase(gamasTicket.getWorkzone())) {

                                    ictgd.insertChildTicketToParent(gamasTicket.getParentId(), r.getTicketId(), gamasTicket.getIdTicket(), r.getClassification_type());
                                    ictgd.updateIdGamasToChild(gamasTicket.getIdTicket(), "TRUE", processId);
                                    ictgd.insertWorkLogs(r.getId(), r.getTicketId(), r.getOwnerGroup(), "This Ticket is Related to Gamas " + gamasTicket.getIdTicket());
                                    InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();
                                    if ("BACKEND".equalsIgnoreCase(r.getTicketStatusId())) {
                                        HashMap<String, String> paramCkWo = new HashMap<String, String>();
                                        paramCkWo.put("externalID1", r.getTicketId());
                                        irdao.getStatusWo(paramCkWo, r.getTicketId(), "checkWorkORder - GAMAS");
                                    }
                                    tikcetFoundStatus = true;
                                    break;
                                }
                            }
                        }
                        if (tikcetFoundStatus) {
                            break;
                        }
                    }
                }
            }
=======
            String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
            ApplicationContext ac = AppUtil.getApplicationContext();
            WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
            String clsfType = workflowManager.getProcessVariable(workflowAssignment.getProcessId(), "classification_type");
            String source = workflowManager.getProcessVariable(workflowAssignment.getProcessId(), "source");
            LogUtil.info(this.getClass().getName(), "processId : " + processId);
            LogUtil.info(this.getClass().getName(), "**SOURCE** : " + source);
            InsertChildTicketGamasDao dao = new InsertChildTicketGamasDao();

            if (!"".equalsIgnoreCase(processId)) {
                if (!"GAMAS".equalsIgnoreCase(source)) {
                    TicketStatus r = new TicketStatus();
                    try {
                        r = dao.getTicketId(processId);
                        if ("FISIK".equalsIgnoreCase(r.getClassification_type())) {
                            LogUtil.info(this.getClass().getName(), "FISIK CONDITION");
                            dao.checkParentTicketFisik(r.getServiceId(), processId, r.getClassification_type(), r.getTicketId());
                        } else {
                            LogUtil.info(this.getClass().getName(), "LOGIC CONDITION");
                            dao.checkParentTicketLogic(r.getServiceId(), r.getClassification_type(), r.getTicketId());
                        }
>>>>>>> 423957160df56bc821300565513ccee2937218b2

            String perangkat = (r.getPerangkat() == null ? "" : r.getPerangkat());

            if ("GAMAS".equalsIgnoreCase(r.getSourceTicket())) {
//                LogUtil.info(this.getClassName(), "processid - imp_srvc : " + processId);
                if (!"".equals(perangkat)) {
//                    LogUtil.info(this.getClass().getName(), "**Impacted Service Function");
                    ListServiceInformation lsi = new ListServiceInformation();
                    InsertImpactServiceDao daoImp = new InsertImpactServiceDao();
                    CallImpactService swa = new CallImpactService();

                    String jsonString = swa.callImpact(r);//assign your JSON String here
                    String assetnum = "";
                    JSONObject obj = new JSONObject(jsonString);

                    String header = obj.getJSONObject("getImpactServiceManualResponse").getString("eaiBody");
                    String body = obj.getJSONObject("getImpactServiceManualResponse").getString("eaiBody");

                    JSONObject objBody = new JSONObject(body);
                    JSONArray arr = objBody.getJSONArray("OUT_IMPACT_RESULT");
                    String serviceNumber = "";
                    for (int i = 0; i < arr.length(); i++) {
                        assetnum = arr.getJSONObject(i).getString("assetnum");

//                        HashMap<String, String> paramSn = new HashMap<String, String>();
//                        paramSn.put("service_id", assetnum);
//                        lsi = daoImp.getServiceNumber(paramSn);
                        if (!assetnum.contains("_")) {
                            serviceNumber = assetnum;
                        } else {
                            serviceNumber = StringUtils.substringBetween(assetnum, "_");
                        }

                        String realm = "telkom.net";
                        String operStatus = daoImp.getOperStatus(serviceNumber, realm);
                        daoImp.insertToTableService(assetnum, operStatus, processId, r.getTicketId(), serviceNumber, r.getEstimation(), r.getSymptomId(), r.getSymptomDesc(), lsi.getRegion());
                        operStatus = "";
                    }

                    // GAMAS TICKET RELATION (PADA SAAT MEMBUAT TIKET GAMAS)
                    header = "";
                    body = "";
                    assetnum = "";
                    objBody = null;
                    arr = null;
                    lsi = null;
                }

                try {
//                    LogUtil.info(this.getClassName(), "processid - gamas relation parent: " + processId);
                    InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();

                    List<RelatedRecords> listRR = new ArrayList<RelatedRecords>();

                    if ("FISIK".equalsIgnoreCase(r.getClassification_type())) {
                        listRR = irdao.getTicketWithRuleFisik(processId, r.getTicketId(), r.getClassification_type());
                    } else {
//                        LogUtil.info(this.getClass().getName(), "**Related Record with Gamas - Logic");
                        listRR = irdao.getTicketWithRuleLogic(processId, r.getServiceType(),
                                r.getLevetGamas(), r.getRegion(), r.getWitel(), r.getWorkZone(), r.getTicketId());
                    }

                    for (RelatedRecords relatedRecord : listRR) {
                        if (!irdao.checkToRr(relatedRecord.getTicket_id())) {
                            irdao.insertToRelatedRecord(relatedRecord.getTicket_id(), processId, relatedRecord.getIdTicketGamas(), r.getClassification_type());
                            ictgd.updateIdGamasToChild(relatedRecord.getIdTicketGamas(), "TRUE", relatedRecord.getParentId());
                            ictgd.insertWorkLogs(relatedRecord.getRecordId(), relatedRecord.getTicket_id(), relatedRecord.getOwnerGroup(), "This Ticket is Related to Gamas " + relatedRecord.getIdTicketGamas());

                            if ("BACKEND".equalsIgnoreCase(relatedRecord.getTicket_status())) {
                                HashMap<String, String> paramCkWo = new HashMap<String, String>();
                                paramCkWo.put("externalID1", relatedRecord.getTicket_id());
                                irdao.getStatusWo(paramCkWo, relatedRecord.getTicket_id(), "checkWorkORder - GAMAS");
                            }

                        }
                    }

                } catch (Exception ex) {
                    logInfo.Log(getClass().getName(), ex.getMessage());
                }
            }

        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            r = null;
            listGamasTicket = null;
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
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return null;
//        return AppUtil.readPluginResource(getClassName(), "/properties/settingWorkOrder.json", null, true, null);
    }

}
