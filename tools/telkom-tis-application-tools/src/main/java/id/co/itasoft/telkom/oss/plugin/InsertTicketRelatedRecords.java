/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.RelatedRecords;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;

/**
 * UNUSED
 * @author ASANI
 */
public class InsertTicketRelatedRecords extends DefaultApplicationPlugin {

    public static String pluginName = "Telkom New OSS - Insert Ticket Related Records";
    private String processIdIncident;
    LogInfo logInfo = new LogInfo();

    @Override
    public Object execute(Map map) {
        processIdIncident = getPropertyString("processIdIncident");
        try {

//            LogUtil.info(getClassName(), " ### InsertTicketRelatedRecords ###");
//            LogUtil.info(getClassName(), "processIdIncident : " + processIdIncident);
//            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
//            WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
//            String processId = appService.getOriginProcessId(workflowAssignment.getProcessId());
//            ApplicationContext ac = AppUtil.getApplicationContext();
//            WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
//            String clsfType = workflowManager.getProcessVariable(workflowAssignment.getProcessId(), "classification_type");
//            LogUtil.info(this.getClass().getName(), "processId : " + processId);
            TicketStatus r = new TicketStatus();
            InsertRelatedRecordDao dao = new InsertRelatedRecordDao();
            r = dao.getDataTicket(processIdIncident);
            if (!"".equalsIgnoreCase(processIdIncident)) {

                if ("GAMAS".equalsIgnoreCase(r.getSourceTicket())) {
                    try {
                        InsertRelatedRecordDao irdao = new InsertRelatedRecordDao();

                        List<RelatedRecords> listRR = new ArrayList<RelatedRecords>();

                        if ("FISIK".equalsIgnoreCase(r.getClassification_type())) {
//                            listRR = irdao.getTicketWithRuleFisik(processIdIncident);
                        } else {
//                            listRR = irdao.getTicketWithRuleLogic(processIdIncident, r.getServiceType(),
//                                    r.getLevetGamas(), r.getRegion(), r.getWitel(), r.getWorkZone());
                        }

                        for (RelatedRecords relatedRecord : listRR) {
//                            LogUtil.info(pluginName, "Looping related record");
                            if (!irdao.checkToRr(relatedRecord.getTicket_id())) {
//                                irdao.insertToRelatedRecord(relatedRecord.getTicket_id(), processIdIncident, relatedRecord.getIdTicketGamas());
                                if ("BACKEND".equalsIgnoreCase(relatedRecord.getTicket_status())) {
                                    HashMap<String, String> paramCkWo = new HashMap<String, String>();
                                    paramCkWo.put("externalID1", relatedRecord.getTicket_id());
                                    irdao.getStatusWo(paramCkWo, relatedRecord.getTicket_id(),"checkWorkORder - GAMAS");
                                }
//                                dao.updateStatusTicket(relatedRecord.getTicket_id());

                            }
                        }

                    } catch (Exception ex) {
                      logInfo.Log(getClass().getName(), ex.getMessage());
                    }
                }

            } else {
              logInfo.Log(getClass().getName(), "process id kosong");
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
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
//        return AppUtil.readPluginResource(getClassName(), "/properties/settingWorkOrder.json", null, true, null);
        return AppUtil.readPluginResource(this.getClass().getName(), "/properties/settingWorkOrder.json");
    }

}
