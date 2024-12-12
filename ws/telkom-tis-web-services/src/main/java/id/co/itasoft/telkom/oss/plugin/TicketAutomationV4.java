/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import com.google.gson.Gson;
import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.TicketAutomationDaoV4;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.ListServiceInformationNew;
import id.co.itasoft.telkom.oss.plugin.model.ListSymptom;
import id.co.itasoft.telkom.oss.plugin.model.ListTkMapping;
import id.co.itasoft.telkom.oss.plugin.model.LogHistory;
import id.co.itasoft.telkom.oss.plugin.model.TicketAutomationModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;
//import protostream.com.google.gson.Gson;

/**
 * @author mtaupiq
 */
public class TicketAutomationV4 extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Ticket Automation V4";
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

    TicketAutomationModel ticket = new TicketAutomationModel();
    ListSymptom symptom = new ListSymptom();
    ListServiceInformationNew lsi = new ListServiceInformationNew();
    ListTkMapping tkMapping = new ListTkMapping();
    String status = "NEW";
    String ticketStatus = "NEW";
    String ticketNumber = "";
    String lastState = "";
    final TicketAutomationDaoV4 dao = new TicketAutomationDaoV4();

    String sidDefault[] = {
        "DEFAULT_SERVICE_DCS",
        "DEFAULT_SERVICE_DGS",
        "DEFAULT_SERVICE_DBS",
        "DEFAULT_SERVICE_DES",
        "DEFAULT_SERVICE_DWS",
        "DEFAULT_SERVICE_DPS",
        "DEFAULT_SERVICE_DSS",
        "DEFAULT_SERVICE_REG",
        "DEFAULT_SERVICE_RBS",
        "DEFAULT_NN_NAS"
    };
    String sourceTarget[] = {"GAMAS", "PROACTIVE", "FALLOUT", "CUSTOMER"};
    String pattern = "EEE dd MMM HH:mm:ss yyyy";
    ArrayManipulation arr = new ArrayManipulation();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String date = simpleDateFormat.format(new Date());
    JSONObject HeadmainObj = new JSONObject();
    JSONObject mainObj = new JSONObject();

    @Override
    //        hsr1.getWriter().print(response);
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {
        try {
            boolean allowOrigin = true;

            if (allowOrigin) {

                try {
                    dao.getApiAttribut();
                } catch (Exception ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }

                //Predefined Headers
                String apiIdDefined = dao.apiId;
                String apiKeyDefined = dao.apiKey;
                String apiSecretDefined = dao.apiSecret;

                // Headers API from Client
                String headerApiId = hsr.getHeader("api_id");
                String headerApiKey = hsr.getHeader("api_key");

                boolean methodStatus = false;
                boolean authStatus = false;
                if ("POST".equals(hsr.getMethod())) {
                    methodStatus = true;
                }
                if (apiIdDefined.equals(headerApiId) && apiKeyDefined.equals(headerApiKey)) {
                    authStatus = true;
                }

                if (methodStatus && authStatus) {
                    try {

                        // Get Body Parameter
                        StringBuffer jb = new StringBuffer();
                        String line = null;
                        try {
                            BufferedReader reader = hsr.getReader();
                            while ((line = reader.readLine()) != null) {
                                jb.append(line);
                            }
                        } catch (Exception e) {
                            LogUtil.error(this.getClassName(), e, "error : " + e.getMessage());
                        }

                        boolean jsonValidate = true;
                        String bodyParam = jb.toString();
                        Gson gson = new Gson();
                        try {
                            ticket = gson.fromJson(bodyParam, TicketAutomationModel.class);
                            logInfo.Log(this.getClassName(), "package form request == " + ticket.getPackageName());
                        } catch (Exception ex) {
                            jsonValidate = false;
                            hsr1.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
                        }

                        if (jsonValidate) {

                            JSONObject response = new JSONObject();
                            response = createTicket(ticket, "other_system");
                            response.write(hsr1.getWriter());
                            insertLosgHystoryAPI(bodyParam, ticketNumber, response.toString());
                        }

                    } catch (Exception ex) {
                        LogUtil.error(this.getClass().getName(), ex, "error iniii : " + ex.getMessage());
                        hsr1.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
                    } finally {
                        ticket = null;
                        symptom = null;
                        lsi = null;
                        tkMapping = null;
                    }

                } else {
                    if (!methodStatus) {
                        try {
                            JSONObject jObj;
                            jObj = new JSONObject();
                            mainObj.put("date", date);
                            mainObj.put("code", "405");
                            mainObj.put("message", "Method Not Allowed");
                            HeadmainObj.put("error", mainObj);
                            HeadmainObj.write(hsr1.getWriter());
                        } catch (JSONException ex) {
//                            logInfo.Log(getClass().getSimpleName(), ex.getMessage());
                            LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                        }
                    } else {
                        try {
                            JSONObject jObj;
                            jObj = new JSONObject();
                            mainObj.put("date", date);
                            mainObj.put("code", "401");
                            mainObj.put("message", "Invalid Authentication");
                            HeadmainObj.put("error", mainObj);
                            HeadmainObj.write(hsr1.getWriter());

                        } catch (JSONException ex) {
                            LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                        }
                    }
                }

            } else {
                hsr1.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "");
            }

        } catch (Exception ex) {
            LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
        }
    }

    private void insertLosgHystoryAPI(String bodyParam, String ticketNumber, String response) throws JSONException, ParseException {

        LogHistoryDao lhDao = new LogHistoryDao();
        String url = "https://oss-incident.telkom.co.id/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.TicketAutomationV4/service";
        JSONObject reqObj = new JSONObject(bodyParam);
        JSONObject data = new JSONObject();
        data.put("data", reqObj);
        JSONObject resObj = new JSONObject(response);
        try {

            lhDao.SENDHISTORY(
                    ticketNumber,
                    "CREATE_TICKET",
                    url,
                    "POST",
                    data,
                    resObj,
                    200);

        } catch (Exception ex) {
            LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
        } finally {
            reqObj = null;
            resObj = null;
        }
    }

    public JSONObject createTicket(TicketAutomationModel ticket, String ticketOrigin) throws SQLException, JSONException, Exception {

        ApplicationContext ac = AppUtil.getApplicationContext();
        final WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
        JSONObject response = new JSONObject();

        ParseDate parsingDate = new ParseDate();

        String flagFcr = (ticket.getFlag_fcr() == null ? "" : ticket.getFlag_fcr());
        String source = (ticket.getSource_ticket() == null ? "" : ticket.getSource_ticket());
        String channel = (ticket.getChannel() == null ? "" : ticket.getChannel());
        String serviceNumber = (ticket.getService_number() == null ? "" : ticket.getService_number());
        String serviceType = (ticket.getService_type() == null ? "" : ticket.getService_type());
        String CustSegmentParam = ticket.getCustomer_segment() == null ? "" : ticket.getCustomer_segment();
        String frmReportedDate = parsingDate.parse(ticket.getReported_date());
        String wzParam = ticket.getWork_zone() == null ? "" : ticket.getWork_zone();
        ticketNumber = dao.getTicketIdFunction();
        String customerId = ticket.getCustomer_id() == null ? "" : ticket.getCustomer_id();
        String manageBy = ticket.getManage_by() == null ? "" : ticket.getManage_by();
        final String childId = UuidGenerator.getInstance().getUuid();

        if ("PL-".equalsIgnoreCase(CustSegmentParam)) {
            CustSegmentParam = "PL-TSEL";
        }

        //GETSYMPTOM
        if (!"".equals(ticket.getClassification_path()) && ticket.getClassification_path() != null) {
            HashMap<String, String> paramsSymptom = new HashMap<String, String>();
            paramsSymptom.put("classification_code", ticket.getClassification_path());
            paramsSymptom.put("hierarchy_type", "SYMPTOM");
            symptom = dao.getListSymptom(paramsSymptom, ticketNumber);
        }

        boolean gamasValidation = true;
        boolean checkTicket = true;
        boolean deviceStatus = true;
        boolean estimationStatus = true;
        boolean serviceIdIsExist = true;
        boolean phoneNumberValidation = true;
        boolean phoneNumberMandatory = true;
        boolean channel44Validation = true;
        boolean channel76Validation = true;
        boolean channelValidation = true;
        boolean SourceValidation = true;

        if ("GAMAS".equalsIgnoreCase(source)) {
            String perangkat = ticket.getPerangkat() == null ? "" : ticket.getPerangkat();
            String estimation = ticket.getEstimation() == null ? "" : ticket.getEstimation();
            String classificationType = symptom.getClassificationType() == null ? "" : symptom.getClassificationType();
            if ("".equals(perangkat) && "FISIK".equalsIgnoreCase(classificationType)) {
                deviceStatus = false;
                gamasValidation = false;
            }
            if ("".equals(estimation)) {
                estimationStatus = false;
                gamasValidation = false;
            }
        }

        if ("44".equalsIgnoreCase(channel)) {
            if ("".equals(ticket.getWork_zone() == null ? "" : ticket.getWork_zone())) {
                channel44Validation = false;
            }
        }
        String hierarchyPath = (symptom.getClassificationCode() == null ? "" : symptom.getClassificationCode());
        String errorMessageChannelValidation = "Invalid Channel";

        if (!"".equals(channel)) {
            String ListBlockedChannel = dao.getBlockedChannel();
            if (!"".equals(ListBlockedChannel)) {
                String[] blockedChannel = ListBlockedChannel.split(",");
                boolean blockedChannelStatus = !arr.SearchDataOnArray(blockedChannel, channel);
                if (blockedChannelStatus) {
                    channelValidation = dao.getParam("CHANNEL", channel);
                } else {
                    channelValidation = false;
                }
            } else {
                if (!"".equals(channel)) {
                    channelValidation = dao.getParam("CHANNEL", channel);
                } else {
                    channelValidation = false;
                    errorMessageChannelValidation = "The channel cannot be empty.";
                }
            }

        }
        String device = ticket.getPerangkat() == null ? "" : ticket.getPerangkat();
        String serviceId = ticket.getService_id() == null ? "" : ticket.getService_id().trim();
        boolean includeSidDefault = arr.SearchDataOnArray(sidDefault, serviceId);
        if (!"56".equals(channel)) {
            if (("GAMAS".equalsIgnoreCase(source) ||
                    "PROACTIVE".equalsIgnoreCase(source)) && !"".equals(device)) {
//                checkTicket = dao.checkTicketGamas(device, channel, hierarchyPath);
            } else {
                if ((!includeSidDefault) &&
                        !"58".equalsIgnoreCase(channel) &&
                        !serviceId.toUpperCase().contains("DEFAULT") &&
                        !serviceId.toUpperCase().contains("DUMMY")) {
//                    checkTicket = dao.checkTicket(serviceId, symptom.getClassificationFlag(), channel, source);
                }
            }
        }
        /**
         * ` VALIDASI CONTACT PHONE Rule : - awalan +62 / 0 - tidak boleh lebih
         * dari 12 digit
         */
        if (!"".equals(ticket.getContact_phone()) && ticket.getContact_phone() != null && !"GAMAS".equalsIgnoreCase(source) && !"PROACTIVE".equalsIgnoreCase(source)) {
            String prefix = ticket.getContact_phone().substring(0, 2);
            if (!"08".equalsIgnoreCase(prefix) && !"62".equalsIgnoreCase(prefix) && !"+6".equalsIgnoreCase(prefix)) {
                phoneNumberValidation = false;
            }
        } else {
            if ("GAMAS".equalsIgnoreCase(source) || "PROACTIVE".equalsIgnoreCase(source)) {
                phoneNumberMandatory = true;
            } else {
                phoneNumberMandatory = false;
            }
        }

        if ("76".equals(channel) && "".equals(serviceNumber) & "".equals(serviceType)) {
            channel76Validation = false;
        }

        String messageSourceValidation = "";
        if (!"".equals(source)) {
            SourceValidation = arr.SearchDataOnArray(sourceTarget, source);
            if (!SourceValidation) {
                messageSourceValidation = "Invalid Source ticket";
            }
        } else {
            SourceValidation = false;
            messageSourceValidation = "Source ticket is required";
        }

        if (checkTicket &&
                gamasValidation &&
                phoneNumberMandatory &&
                phoneNumberValidation &&
                channel44Validation &&
                channel76Validation &&
                channelValidation &&
                SourceValidation) {
            final TicketAutomationModel param = new TicketAutomationModel();

            // GETSID
            HashMap<String, String> params = new HashMap<String, String>();
            if (!"".equals(serviceId)) {
//                if ("76".equals(channel)) {
//                    params.put("service_number", serviceNumber);
//                    params.put("service_type", serviceType);
//                    params.put("channel", channel);
//                } else {
//                    params.put("service_id", ticket.getService_id());
//                    params.put("channel", channel);
//                }
//                if ("DWS".equalsIgnoreCase(CustSegmentParam)) {
//                    params.put("customer_segment", CustSegmentParam);
//                }
//                lsi = dao.getListServiceInformation(params, ticketNumber);
//                if (lsi.getServiceId() == null) {
//                    serviceIdIsExist = false;
//                }

                //GETDATEK
                String paramDatek = "";
//                String paramServiceType = "";
//                String lsiServiceType = lsi.getServiceType() == null ? "" : lsi.getServiceType();
                lsi.setServiceId(serviceId);

                if (serviceId.contains("_")) {
                    String[] partSid = serviceId.split("_");

                    if (partSid.length > 2) {
                        paramDatek = partSid[1];
                    } else {
                        paramDatek = serviceId;
                    }
                } else {
                    paramDatek = serviceId;
                }

                JSONObject resDatek = new JSONObject();
                resDatek = dao.getTechnicalData(ticketNumber, paramDatek, serviceId, childId, "CREATE");
                logInfo.Log(this.getClassName(), "resDatek DATEK : " + resDatek);
                if (resDatek.has("status")) {
                    logInfo.Log(this.getClassName(), "masuk cek status");

                    if (resDatek.getBoolean("status")) {
                        logInfo.Log(this.getClassName(), "masuk status");
                        if (resDatek.has("STO")) {
                            logInfo.Log(this.getClassName(), "cek sto");
                            logInfo.Log(this.getClassName(), "workzone from datek : " + resDatek.get("STO"));
                        }
                        if ("".equals(wzParam)) {
                            if (resDatek.has("STO")) {
                                wzParam = resDatek.get("STO") == null ? "" : resDatek.getString("STO");
                            }
                        }
                        if ("".equals(customerId)) {
                            if (resDatek.has("CUSTOMER_ID")) {
                                ticket.setCustomer_id(resDatek.get("CUSTOMER_ID") == null ? "" : resDatek.getString("CUSTOMER_ID"));
                            }
                        }
                        if (resDatek.has("ADDRESS")) {
                            lsi.setServiceAddress(resDatek.get("ADDRESS") == null ? "" : resDatek.getString("ADDRESS"));
                        }
                        if (resDatek.has("CUSTOMER_ID")) {
                            lsi.setCustomerID(resDatek.get("CUSTOMER_ID") == null ? "" : resDatek.getString("CUSTOMER_ID"));
                        }
                        if (resDatek.has("CUSTOMER_ID_DESCRIPTION")) {
                            lsi.setCustomerName(resDatek.get("CUSTOMER_ID_DESCRIPTION") == null ? "" : resDatek.getString("CUSTOMER_ID_DESCRIPTION"));
                        }
                        if (resDatek.has("SERVICE_TYPE")) {
                            lsi.setServiceType(resDatek.get("SERVICE_TYPE") == null ? "" : resDatek.getString("SERVICE_TYPE"));
                            serviceType = resDatek.get("SERVICE_TYPE").toString();
                        } else {
                            lsi.setServiceType("");
                            serviceType = "";
                        }
                        if (resDatek.has("EVENT_FLAG")) {
                            lsi.setService_flag(resDatek.get("EVENT_FLAG") == null ? "" : resDatek.getString("EVENT_FLAG"));
                        }
                        if (resDatek.has("SLA_CATEGORY_DETAIL")) {
                            LogUtil.info(this.getClass().getName(), "log SLA_CATEGORY_DETAIL : " + resDatek.getString("SLA_CATEGORY_DETAIL"));
                            lsi.setServiceIdDescription(resDatek.get("SLA_CATEGORY_DETAIL") == null ? "" : resDatek.getString("SLA_CATEGORY_DETAIL"));
                        }

                        logInfo.Log(this.getClassName(), "end check");
                    } else {
                        logInfo.Log(this.getClassName(), "masuk else");
                    }
                }

                logInfo.Log(this.getClassName(), "SERVICE_CONTACT : " + ticket.getContact_phone());
                logInfo.Log(this.getClassName(), "ticketOrigin : " + ticketOrigin);
                if (ticketOrigin.equalsIgnoreCase("bulk_ticket")) {
                    StringBuilder summaryBuilder = new StringBuilder();
                    summaryBuilder.append(ticket.getSummary()).append(paramDatek).append(" | ").append(ticket.getContact_phone());
                    ticket.setSummary(summaryBuilder.toString());
                    ticket.setService_id(paramDatek);
                }

                //GETBUD
//                            lsi = dao.getListServiceInformation(params, ticketNumber);
                String serviceTypeTarget[] = {"INTERNET", "IPTV", "VOICE", "LOC_AP", "WICO", "WIFI MESH", "WIFI", "WIFI_AP", "WIFI_HOMESPOT", "WISTA", "WMSLITE"};
                String channelTarget[] = {"2", "4", "10", "11", "19", "26", "68", "71", "72", "73", "74", "77", "35", "30"}; // selain
                String custSegementTarget[] = {"PL-TSEL"};
                String cstSegmentForBud = CustSegmentParam;
                boolean resService = arr.SearchDataOnArray(serviceTypeTarget, serviceType);
                boolean resChannel = arr.SearchDataOnArray(channelTarget, channel);
                boolean resCustSegmen = arr.SearchDataOnArray(custSegementTarget, cstSegmentForBud);
                if (resService && !resChannel && !resCustSegmen) {
                    try {
                        logInfo.Log(this.getClassName(), "MASUK KONDISI GETBUD");
                        String customerSegmentBUD = dao.getBUD(paramDatek, ticketNumber);
                        String[] listCstSegment = {"DPS", "DSS", "DES", "DGS", "DBS", "DCS", "PL-TSEL", "REG", "RBS"};
                        String fixCstSegment = "";
                        for (String cs : listCstSegment) {
                            if (customerSegmentBUD.contains(cs)) {
                                fixCstSegment = cs;
                                break;
                            }
                        }
                        if (!"".equals(fixCstSegment)) {
                            lsi.setCustomerSegment(fixCstSegment);
                            CustSegmentParam = fixCstSegment;
                            ticket.setCustomer_segment(fixCstSegment);
                        }
                    } catch (Exception ex) {
                        LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                    }

                }
                //getBudDatin

                String serviceTypeTargetDatin[] = {"INTERNET", "IPTV", "VOICE"};
                String channelTargetDatin[] = {"35", "30", "15", "40"};
                String costSegementTargetDatin[] = {"PL-TSEL", "DWS"};
                boolean resServiceDatin = arr.SearchDataOnArray(serviceTypeTargetDatin, serviceType);
                boolean resChannelDatin = arr.SearchDataOnArray(channelTargetDatin, channel);
                boolean resCustSegmenDatin = arr.SearchDataOnArray(costSegementTargetDatin, cstSegmentForBud);
                if (!resServiceDatin && resChannelDatin && !resCustSegmenDatin) {
                    try {
                        String fixCstSegment = "";
                        logInfo.Log(this.getClassName(), "MASUK KONDISI GETBUD DATIN");
                        Map<String, String> customerSegmentBUDDatin = dao.getBUDDatin(serviceId, ticketNumber);

                        if (customerSegmentBUDDatin != null) {
                            if ("Private Service".equalsIgnoreCase(customerSegmentBUDDatin.get("accounttype"))) {
                                fixCstSegment = "DPS";
                            } else if ("Regional".equalsIgnoreCase(customerSegmentBUDDatin.get("accounttype"))) {
                                fixCstSegment = "RBS";
                            } else if ("Government".equalsIgnoreCase(customerSegmentBUDDatin.get("accounttype"))) {
                                fixCstSegment = "DGS";
                            } else if ("State-Owned Enterprise Service".equalsIgnoreCase(customerSegmentBUDDatin.get("accounttype"))) {
                                fixCstSegment = "DSS";
                            }
                            if (!"".equals(fixCstSegment)) {
                                lsi.setCustomerSegment(fixCstSegment);
                                CustSegmentParam = fixCstSegment;
                                ticket.setCustomer_segment(fixCstSegment);
                            }
                        }
                    } catch (Exception ex) {
                        LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                    }
                }

            }

            if ("56".equals(channel)) {
                lsi.setCustomerID("C3700008");
                lsi.setCustomerName("TELEKOMUNIKASI SELULAR");
                lsi.setCustomerSegment("DWS");
                if ("".equals(wzParam)) {
                    lsi.setWorkzone("NAS");
                    wzParam = "NAS";
                }
            }

            if (("DWS".equalsIgnoreCase(CustSegmentParam) || "DWS".equalsIgnoreCase(lsi.getCustomerSegment())) &&
                    "".equals(lsi.getWorkzone()) &&
                    "".equals(lsi.getCustomerID()) &&
                    !"56".equals(channel)) {
                lsi.setWorkzone("NAS");
                lsi.setCustomerID("DEFAULT_CUSTOMER_DWS");
            }

            if ("".equals(wzParam) && "62".equals(channel) && !"".equals(device)) {
                HashMap<String, String> paramsWz = new HashMap<String, String>();
                paramsWz.put("hostname", device.split(",")[0]);
                wzParam = dao.getWorkzone(paramsWz, ticketNumber);
            }
            if ((symptom.getClassificationCode() != null && (!"GAMAS".equalsIgnoreCase(source) && !"PROACTIVE".equalsIgnoreCase(source))) ||
                    (symptom.getClassificationCode() != null && ("GAMAS".equalsIgnoreCase(source) || "PROACTIVE".equalsIgnoreCase(source))) ||
                    ((symptom.getClassificationCode() != null && "44".equalsIgnoreCase(channel)))) {
                String workZone = ("".equals(wzParam) ? "NAS" : wzParam);
                String custSegment = ("".equals(CustSegmentParam) ? lsi.getCustomerSegment() : CustSegmentParam);
                hierarchyPath = (symptom.getClassificationCode() == null ? "" : symptom.getClassificationCode());

                String ignoredCstSegment = dao.getIgnoredCustSegment();
                String[] cstSegmentList = ignoredCstSegment.split(";");
                boolean substringCstSegment = true;
                if (!"".equals(custSegment == null ? "" : custSegment)) {
                    for (String cstSgmt : cstSegmentList) {
                        if (cstSgmt.equals(custSegment)) {
                            substringCstSegment = false;
                            break;
                        }
                    }
                }

                if (substringCstSegment) {
                    if ((custSegment == null ? "" : custSegment).length() > 3) {
                        custSegment = custSegment.substring(0, 3);
                    }
                }

                LogHistory lh = null;
                LogHistoryDao lhDao = null;
                JSONObject reqObj = null;
                JSONObject resObj = null;

                //GETOWNERGROUP
                HashMap<String, String> paramsTkMapping = new HashMap<String, String>();
                if (!"".equals(manageBy)) {
                    if ("TELKOMSEL.TBG_ESB".equalsIgnoreCase(manageBy)) {
                        manageBy = "TBG";
                    }else if ("TELKOMSEL_ESB".equalsIgnoreCase(manageBy)) {
                        manageBy = "";
                    }
                    paramsTkMapping.put("product_name", manageBy);
                }
                if ("GAMAS".equalsIgnoreCase(source) || "44".equalsIgnoreCase(channel)) {
                    if (!"".equals(hierarchyPath) && !"".equals(workZone)) {
                        paramsTkMapping.put("classification_id", hierarchyPath);
                        paramsTkMapping.put("workzone", workZone);
                        if (!"".equals(manageBy)) {
                            paramsTkMapping.put("product_name", manageBy);
                        }
                        tkMapping = dao.getListTkMapping(paramsTkMapping, ticketNumber);
                    } else {
                        lhDao = new LogHistoryDao();
                        reqObj = new JSONObject();
                        resObj = new JSONObject();
                        reqObj.put("classification_id", ("".equals(hierarchyPath) ? "null" : hierarchyPath));
                        reqObj.put("customer_segment", ("".equals(custSegment) ? "null" : custSegment));
                        reqObj.put("workzone", ("".equals(workZone) ? "null" : workZone));
                        if (!"".equals(manageBy)) {
                            paramsTkMapping.put("product_name", manageBy);
                        }

                        lhDao.SENDHISTORY(
                                ticketNumber,
                                "CREATE_TICKET GET_OWNERGROUP",
                                "Not calling api get ownergroup",
                                "-",
                                reqObj,
                                resObj,
                                0);
                    }
                } else {
                    if ((!"".equals(hierarchyPath) && !"".equals(workZone) && !"".equals(custSegment)) ||
                            ("PROACTIVE".equalsIgnoreCase(source) && !"".equals(hierarchyPath))) {
                        paramsTkMapping.put("classification_id", hierarchyPath);
                        paramsTkMapping.put("workzone", workZone);
                        paramsTkMapping.put("customer_segment", custSegment);
                        if (!"".equals(manageBy)) {
                            paramsTkMapping.put("product_name", manageBy);
                        }
                        tkMapping = dao.getListTkMapping(paramsTkMapping, ticketNumber);
                    } else {
                        lhDao = new LogHistoryDao();
                        reqObj = new JSONObject();
                        resObj = new JSONObject();
                        reqObj.put("classification_id", ("".equals(hierarchyPath) ? "null" : hierarchyPath));
                        reqObj.put("customer_segment", ("".equals(custSegment) ? "null" : custSegment));
                        reqObj.put("workzone", ("".equals(workZone) ? "null" : workZone));
                        if (!"".equals(manageBy)) {
                            paramsTkMapping.put("product_name", manageBy);
                        }

                        lhDao.SENDHISTORY(
                                ticketNumber,
                                "CREATE_TICKET GET_OWNERGROUP",
                                "Not calling api get ownergroup",
                                "-",
                                reqObj,
                                resObj,
                                0);
                        reqObj = null;
                        resObj = null;

                    }
                }
                logInfo.Log(this.getClass().getName(), "log thread");
                String autoBackEnd = (symptom.getAutoBackend() == null ? "" : symptom.getAutoBackend());
                String clasificationType = (symptom.getClassificationType() == null ? "" : symptom.getClassificationType());

                UUID uuid = UUID.randomUUID();
                final String originProcessId = uuid.toString();

                dao.insertToTableParentTicket(originProcessId, childId);

                final boolean insertTicketStatus = dao.insertToTableTicket(lsi, tkMapping, symptom, originProcessId, ticketNumber, childId,
                        status, ticketStatus, frmReportedDate, custSegment, workZone, lastState, ticket);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (insertTicketStatus) {
                                String processDefId = "ticketIncidentService:latest:flowIncidentTicket";
                                WorkflowProcessResult startProcess = workflowManager.processStartWithLinking(processDefId, null, "Automation", originProcessId);
                            }
                        } catch (Exception e) {
                            Thread.currentThread().interrupt();
                        }

                    }
                });
                thread.setDaemon(true);
                thread.start();
                try {
                    if (insertTicketStatus) {
                        String ownerGroup = (tkMapping.getPersonOwnerGroup() == null ? "" : tkMapping.getPersonOwnerGroup());
                        String message = "";
                        if (!serviceIdIsExist) {
                            message = "Service ID does not exist in list_serviceInformation";
                        } else if ("".equals(ownerGroup)) {
                            message = "Owner group not found";
                        } else {
                            message = "Success";
                        }
                        JSONObject jObj;
                        jObj = new JSONObject();
                        jObj.put("service_id", ticket.getService_id());
                        jObj.put("workzone", workZone);
                        jObj.put("customer_segment", custSegment);
                        jObj.put("symptom", ("".equals(hierarchyPath) ? ticket.getClassification_path() + " Not Found" : hierarchyPath));
                        jObj.put("ticket_id", ticketNumber);
                        jObj.put("process_id", originProcessId);
                        mainObj.put("code", "200");
                        mainObj.put("message", message);
                        mainObj.put("data", jObj);

                        response = mainObj;
                    }
                } catch (JSONException ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }
            } else {
                try {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("date", date);
                    mainObj.put("code", "500");
                    if ("GAMAS".equalsIgnoreCase(source) || "PROACTIVE".equalsIgnoreCase(source)) {
                        mainObj.put("message", "Ticket not generated because symptom " + ticket.getClassification_path() + " not found");
                    } else {
                        mainObj.put("message", "Ticket not generated because symptom " + ticket.getClassification_path() + " and service id " + lsi.getServiceId() + " not found");
                    }
                    HeadmainObj.put("error", mainObj);
                    response = HeadmainObj;
                } catch (JSONException ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }
            }

        } else {
            if (!checkTicket) {
                try {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    jObj.put("ticket_id", dao.existing_ticketId);
                    jObj.put("service_id", dao.existing_serviceIid);
                    jObj.put("classification_flag", dao.existing_classificationFlag);
                    jObj.put("device", dao.existing_device);
                    jObj.put("ticket_status", dao.existing_Ticketstatus);
                    jObj.put("errorCode", "10020");
                    jObj.put("errorMessage", "Ticket duplikasi SCCDTicket duplikasi SCCD");
                    mainObj.put("code", "409");
                    mainObj.put("message", "Ticket incident already exists");
                    mainObj.put("data", jObj);
                    response = mainObj;

                } catch (JSONException ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }
            } else if (!deviceStatus) {
                try {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("date", date);
                    mainObj.put("code", "500");
                    mainObj.put("message", "Device is required");
                    HeadmainObj.put("error", mainObj);
                    response = HeadmainObj;
                } catch (JSONException ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }
            } else if (!estimationStatus) {
                try {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("date", date);
                    mainObj.put("code", "500");
                    mainObj.put("message", "estimation is required");
                    HeadmainObj.put("error", mainObj);
                    response = HeadmainObj;
                } catch (JSONException ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }
            } else if (!phoneNumberMandatory) {
                try {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("date", date);
                    mainObj.put("code", "500");
                    mainObj.put("message", "Phone Number is required");
                    HeadmainObj.put("error", mainObj);
                    response = HeadmainObj;
                } catch (JSONException ex) {

                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }
            } else if (!phoneNumberValidation) {
                try {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("date", date);
                    mainObj.put("code", "500");
                    mainObj.put("message", "Make sure to fill the correct contact phone format with 08 or 62");
                    HeadmainObj.put("error", mainObj);
                    response = HeadmainObj;
                } catch (JSONException ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }
            } else if (!channel44Validation) {
                try {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("date", date);
                    mainObj.put("code", "500");
                    mainObj.put("message", "Workzone is required");
                    HeadmainObj.put("error", mainObj);
                    response = HeadmainObj;
                } catch (JSONException ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }
            } else if (!channel76Validation) {
                try {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("date", date);
                    mainObj.put("code", "500");
                    mainObj.put("message", "service_type and service_number is required");
                    HeadmainObj.put("error", mainObj);
                    response = HeadmainObj;
                } catch (JSONException ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }
            } else if (!channelValidation) {
                try {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("date", date);
                    mainObj.put("code", "500");
                    mainObj.put("message", errorMessageChannelValidation);
                    HeadmainObj.put("error", mainObj);
                    response = HeadmainObj;
                } catch (JSONException ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }
            } else if (!SourceValidation) {
                try {
                    JSONObject jObj;
                    jObj = new JSONObject();
                    mainObj.put("date", date);
                    mainObj.put("code", "500");
                    mainObj.put("message", messageSourceValidation);
                    HeadmainObj.put("error", mainObj);
                    response = HeadmainObj;
                } catch (JSONException ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                }
            }
        }
        return response;
    }

}
