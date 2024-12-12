/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.InsertChildTicketGamasDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertRelatedRecordDao;
import id.co.itasoft.telkom.oss.plugin.dao.UpdateAttributeTicketDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ListLocation;
import id.co.itasoft.telkom.oss.plugin.model.ListService;
import id.co.itasoft.telkom.oss.plugin.model.ListSymptom;
import id.co.itasoft.telkom.oss.plugin.model.ListWorkzone;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import java.util.HashMap;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;

/**
 *
 * @author mtaup
 */
public class UpdateAttributeTicket extends DefaultApplicationPlugin {

    String pluginName = "Telkom New OSS - Update Attribute Ticket";
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
//        return AppUtil.readPluginResource(this.getClass().getName(), "/properties/settingWorkOrder.json");
    }

    InsertChildTicketGamasDao ictgd;
    InsertRelatedRecordDao daoTicket;
    TicketStatus r;
    InsertChildTicketGamasDao dao;
    UpdateAttributeTicketDao tDao;
    ListLocation ll;

    @Override
    public Object execute(Map map) {
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        String processIdDD = appService.getOriginProcessId(workflowAssignment.getProcessId());

        final String processId = processIdDD;
        ictgd = new InsertChildTicketGamasDao();
        daoTicket = new InsertRelatedRecordDao();
        r = new TicketStatus();
        dao = new InsertChildTicketGamasDao();

        try {
            r = dao.getTicketId(processId);

            String serviceNo = (r.getServiceNo() == null ? "" : r.getServiceNo());
            String channel = (r.getChannel() == null ? "" : r.getChannel());
            String custSegment = (r.getCustomerSegment() == null ? "" : r.getCustomerSegment());
            String sourceTicket = (r.getSourceTicket() == null ? "" : r.getSourceTicket());
            String customerType = "";
            LogUtil.info(this.getClassName(), "Customer Segment : "+custSegment);
            LogUtil.info(this.getClassName(), "service number : "+serviceNo);
            if (!"".equals(serviceNo) && "DCS".equalsIgnoreCase(custSegment)) {
                LogUtil.info(this.getClassName(), "GetCustomerTpe Condition");
                customerType = tDao.getCustomerType(serviceNo, r.getTicketId());
            }

            if ("API".equals(r.getCreatedTicketBy())) {
                tDao = new UpdateAttributeTicketDao();
                ll = new ListLocation();
                HashMap<String, String> paramLl = new HashMap<String, String>();
//                if (!"".equals(r.getStreetAddress())) {
//                    paramLl.put("address_code", r.getStreetAddress()); 
//                    ll = tDao.getLocation(paramLl);
//                }

                ListService ls = new ListService();
//                HashMap<String, String> paramService = new HashMap<String, String>();
//                if (!"".equals(r.getServiceId()) && r.getServiceId() != null) {
//                    paramService.put("service_id", r.getServiceId());
//                    LogUtil.info(getClassName(), "PARAM LOCATION ADDRESS CODE :" + r.getStreetAddress());
//                    ls = tDao.getListService(paramService);
//                }

                ListWorkzone workzone = new ListWorkzone();
                if (r.getWorkZone() != null && !"".equals(r.getWorkZone())) {
                    HashMap<String, String> paramsWorkzone = new HashMap<String, String>();
                    paramsWorkzone.put("sto_code", r.getWorkZone());
                    workzone = tDao.getListWorkzone(paramsWorkzone);
                }

                ListSymptom solution = new ListSymptom();
                String solutionCode = (r.getSolution() == null ? "" : r.getSolution());
                if (!"".equals(solutionCode)) {
                    HashMap<String, String> paramsSolution = new HashMap<String, String>();
                    paramsSolution.put("classification_code", solutionCode);
                    solution = tDao.getSolution(paramsSolution);
                }

                String actualSolution = (r.getActualSolution() == null ? "" : r.getActualSolution());
                String descActualSolution = "";
                if (!"".equals(actualSolution)) {
                    HashMap<String, String> paramActSolution = new HashMap<String, String>();
                    paramActSolution.put("classification_code", actualSolution);
                    paramActSolution.put("hierarchy_type", "ACTSOL");
                    descActualSolution = tDao.getDescActualSolution(paramActSolution);
                }

//                String serviceNo = (r.getServiceNo() == null ? "" : r.getServiceNo());
//                String channel = (r.getChannel() == null ? "" : r.getChannel());
//                String custSegment = (r.getCustomerSegment() == null ? "" : r.getCustomerSegment());
//                String sourceTicket = (r.getSourceTicket() == null ? "" : r.getSourceTicket());
//                String customerType = "";
//                if (!"".equals(serviceNo) && "50".equals(channel) && "PROACTIVE".equalsIgnoreCase(sourceTicket) && "DCS".equalsIgnoreCase(custSegment)) {
////                    serviceNo = "122874207782";
//                    customerType = tDao.getCustomerType(serviceNo, r.getTicketId());
//                }
                String chanelDesc = tDao.getParamDescription("CHANNEL", r.getChannel());
                String hardComplaintDesc = tDao.getParamDescription("HARDCOMPLAINT", r.getHardComplaint());
                String urgencyDesc = tDao.getParamDescription("URGENCY", r.getUrgency());
                String closdByDesc = tDao.getParamDescription("CLOSEDBY", r.getClosedBy());
                String sourceDesc = tDao.getParamDescription("SOURCE", r.getSourceTicket());
                String custSegmentDesc = tDao.getParamDescription("CUSTOMERSEGMENT", r.getCustomerSegment());
                String subsidiaryDesc = tDao.getParamDescription("SUBSIDIARY", r.getSubsidiary());
                String realmDesc = tDao.getParamDescription("REALM", r.getRealm());
                String impactDesc = tDao.getParamDescription("IMPACT", r.getImpact());
                String serviceCategoryDesc = tDao.getParamDescription("SERVICE_CATEGORY", r.getServiceCategory());

                tDao.updateTicketParam(ll, ls, workzone, chanelDesc, hardComplaintDesc, urgencyDesc, closdByDesc, solution,
                        descActualSolution, sourceDesc, custSegmentDesc, subsidiaryDesc, realmDesc, impactDesc,
                        serviceCategoryDesc, processId, r.getSourceTicket(), customerType);
            }else{
                tDao.updateCustomerType(processId, customerType);
            }

        } catch (Exception ex) {
            logInfo.Log(getClass().getName(), ex.getMessage());
        }
        return null;
    }

}
