/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.CompleteActivityTicketIncidentApiDao;
import id.co.itasoft.telkom.oss.plugin.dao.GamasTicketRelationDao;
import id.co.itasoft.telkom.oss.plugin.dao.GetMasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketWorkLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.SelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.TicketWorkLogsSaveDao;
import id.co.itasoft.telkom.oss.plugin.function.*;
import id.co.itasoft.telkom.oss.plugin.kafkaHandler.KafkaProducerService;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.TicketWorkLogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.UuidGenerator;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.joget.commons.util.LogUtil;

/**
 * @author suena
 */
/*
Buat ngetest :
https://dev-joget-incident-ticketing-service-joget-dev.apps.mypaas.telkom.co.id/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.InsertTicketWorkLogs/service?recordkey=IN000414&class=INCIDENT&log_type=AGENTNOTE&user=admin&id_ticket=IN000414&created_date=2022-05-25 09:25:53.0&ownergroup=CCAN WITEL JATIM TIMUR (JEMBER)&summary=test&clientviewable=test clientviewable&assignmentid=test assignmentid&orgid=TELKOM&siteid=test siteid&worklogid=ID-000090&anywhererrefid=test anywhererrfid&summary_section_control=test summary section control&detail=<p>test</p>
 */
public class InsertTicketWorkLogs extends Element implements PluginWebSupport {

    /*deklarasi variable global*/
    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Insert Ticket Work Logs";

    LogInfo info = new LogInfo();
    ArrayManipulation arrayManipulation = new ArrayManipulation();
    LogHistoryDao logHistoryDao = new LogHistoryDao();

    @Override
    public String renderTemplate(FormData fd, Map map) {
        return ""; //To change body of generated methods, choose Tools | Templates.
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
        return pluginName;
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    CallRestAPI callApi = new CallRestAPI();

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        LogUtil.info(this.getClass().getName(), "insertWorklog");
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        JSONObject headMainObj = new JSONObject();
        JSONObject mainObj = new JSONObject();
        JSONObject jObj = new JSONObject();

        try {
            InsertTicketWorkLogsDao dao = new InsertTicketWorkLogsDao();
            TicketWorkLogsSaveDao itwDao = new TicketWorkLogsSaveDao();
            SendStatusToMYIHXApi myihx = new SendStatusToMYIHXApi();
            final RESTAPI restApi = new RESTAPI();

            dao.getApiAttribut();
            //Predefined Headers
            String apiIdDefined = dao.apiId;
            String apiKeyDefined = dao.apiKey;
            String apiSecretDefined = dao.apiSecret;
//        String apiTokenDefined = jw.generateTokenAutomation(apiKeyDefined, apiSecretDefined);

            // Headers API from Client
            String headerApiId = request.getHeader("api_id");
            String headerApiKey = request.getHeader("api_key");
//        String headerToken = hsr.getHeader("token");
            String bodyParam = "";
            String idTicket = "";

            String method = request.getMethod();

            if ("POST".equalsIgnoreCase(method)) {

                //check header
                if (apiIdDefined.equals(headerApiId) && apiKeyDefined.equals(headerApiKey)) {
                    Ticket tc;
                    try {

                        // Get Body Parameter
                        StringBuffer jb = new StringBuffer();
                        String line = null;
                        try {
                            BufferedReader reader = request.getReader();
                            while ((line = reader.readLine()) != null) {
                                jb.append(line);
                            }
                        } catch (Exception e) {
                            info.Log(getClassName(), e.getMessage());
                        }

                        bodyParam = jb.toString();
                        JSONParser parse = new JSONParser();
                        org.json.simple.JSONObject dataObj = (org.json.simple.JSONObject) parse.parse(bodyParam);

                        String recordkey = (dataObj.get("recordkey") == null ? "" : dataObj.get("recordkey").toString());
                        String clasS = (dataObj.get("class") == null ? "" : dataObj.get("class").toString());
                        String logType = (dataObj.get("log_type") == null ? "" : dataObj.get("log_type").toString());

                        idTicket = (dataObj.get("id_ticket") == null ? "" : dataObj.get("id_ticket").toString());
                        final String user = (dataObj.get("user") == null ? "" : dataObj.get("user").toString());
                        String clientViewAble = (dataObj.get("clientViewAble") == null ? "" : dataObj.get("clientViewAble").toString());
                        String OwnerGroup = (dataObj.get("ownergroup") == null ? "" : dataObj.get("ownergroup").toString());
                        String assignmentId = (dataObj.get("assignmentId") == null ? "" : dataObj.get("assignmentId").toString());
                        String orgId = (dataObj.get("orgid") == null ? "" : dataObj.get("orgid").toString());
                        final String summary = (dataObj.get("summary") == null ? "" : dataObj.get("summary").toString());
                        String siteid = (dataObj.get("siteid") == null ? "" : dataObj.get("siteid").toString());
                        String workLogId = (dataObj.get("worklogid") == null ? "" : dataObj.get("worklogid").toString());
                        String anyWhereRrefid = (dataObj.get("anywhererrefid") == null ? "" : dataObj.get("anywhererrefid").toString());
                        String summarySectionControl = (dataObj.get("summary_section_control") == null ? "" : dataObj.get("summary_section_control").toString());
                        final String detail = (dataObj.get("detail") == null ? "" : dataObj.get("detail").toString());
                        String attachmentFile = (dataObj.get("attachment_file") == null ? "" : dataObj.get("attachment_file").toString());

                        GamasTicketRelationDao gtrDao = new GamasTicketRelationDao();
                        ArrayManipulation am = new ArrayManipulation();
                        tc = new Ticket();
                        tc = gtrDao.getDataChildTicket(recordkey);
                        String ticketStatus = tc.getTicketStatus() == null ? "" : tc.getTicketStatus();
                        String tcTicketId = tc.getTicketId() == null ? "" : tc.getTicketId();

                        if (!"".equalsIgnoreCase(tc.getId())) {
                            if (!"CLOSED".equalsIgnoreCase(ticketStatus)) {
                                /*PROCES*/
                                TicketWorkLogs r = new TicketWorkLogs();
                                r.setRecordkey(recordkey);
                                r.setParentid(tc.getId());
                                r.setClasS(clasS);
                                r.setCreatedBy(user);
                                r.setLogType(logType);
                                r.setCreatedByName(user);
                                r.setModifiedBy(user);
                                r.setModifiedByName(user);
                                r.setClientviewable(clientViewAble);
                                r.setOwnergroup(OwnerGroup);
                                r.setAssignmentid(assignmentId);
                                r.setOrgid(orgId);
                                r.setSummary(summary);
                                r.setSiteid(siteid);
                                r.setWorklogid(workLogId);
                                r.setAnywhererrefid(anyWhereRrefid);
                                r.setSummarySectionControl(summarySectionControl);
                                r.setDetail(detail);
                                r.setCreatedDate(date);

                                UuidGenerator uuid = UuidGenerator.getInstance();
                                String idWorklog = uuid.getUuid();

                                String channel = tc.getChannel() == null ? "" : tc.getChannel();
                                String custSegment = tc.getCust_segment() == null ? "" : tc.getCust_segment();
                                String externalTicketId = tc.getExtenalTicketId() == null ? "" : tc.getExtenalTicketId();
                                String externalTicketTier3 = tc.getExtenalTicketTier3() == null ? "" : tc.getExtenalTicketTier3();
                                String symptom = tc.getSymptom() == null ? "" : tc.getSymptom();
                                String sourceTicket = tc.getSource_ticket() == null ? "" : tc.getSource_ticket();
                                String lati = tc.getLatitude() == null ? "" : tc.getLatitude();
                                String longi = tc.getLongitude() == null ? "" : tc.getLongitude();
                                String externalIdTA = tc.getExternalIdTA() == null ? "" : tc.getExternalIdTA();
                                String summ = r.getSummary() == null ? "" : r.getSummary();
                                String createdBY = r.getCreatedby() == null ? "" : r.getCreatedby();

                                String symptomMBravoWL[] = {"C_GAMAS_002_034", "C_GAMAS_001_034"};
                                boolean ceksymptomMBravoWL = am.SearchDataOnArray(symptomMBravoWL, symptom);

                                String fileName = "";
                                String extention = "";
                                if (!"".equals(attachmentFile)) {
                                    StringBuilder sbFileName = new StringBuilder();
                                    extention = attachmentFile.split(";")[0].split("/")[1];
                                    sbFileName.append(recordkey)
                                            .append("-")
                                            .append(idWorklog);
                                    fileName = sbFileName.toString();
                                    uploadAttachmentToMinio(attachmentFile, fileName, "worklog-production/");
                                }
                                r.setAttachmentFile(fileName + "." + extention);
                                boolean result = dao.InsertTicketWorkLogs(r, idWorklog);
                                boolean resultSerachSymptompInap;
                                resultSerachSymptompInap = arrayManipulation.SearchDataOnArray(getListSymptomCts(), symptom);
                                itwDao.updateWorklogSummary(summary, tc.getId());
                                if ("56".equals(channel) && "DWS".equalsIgnoreCase(custSegment)) {
                                    restApi.addWorklogCts(tc, summary, detail, user, externalTicketId);
                                }

                                if ("56".equals(channel) || resultSerachSymptompInap) {
                                    restApi.addWorklogInap(tc, summary, user, externalTicketId);
                                }

                                if ("CUSTOMER".equalsIgnoreCase(tc.getSource_ticket()) &&
                                        "DWS".equalsIgnoreCase(tc.getCust_segment())) {
                                    restApi.updateWorklogOGD(recordkey, summary, logType);
                                }

                                if (!"WFMTA".equalsIgnoreCase(user) && !externalIdTA.isEmpty()) {
                                    final Ticket finalTc = tc;
                                    final String finalIdTicket = idTicket;
                                    restApi.insertWorklogWFMTA(finalTc, summary, user, detail, finalIdTicket);
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                            } catch (Exception e) {
                                                Thread.currentThread().interrupt();
                                            }
                                        }
                                    });
                                    thread.setDaemon(true);
                                    thread.start();
                                }

                                if ("CLIENTNOTE".equalsIgnoreCase(logType)) {
                                    dao.UpdateLapul(idTicket);
                                    try {
                                        if ("MEDIACARE".equalsIgnoreCase(tc.getTicketStatus()) || "SALAMSIM".equalsIgnoreCase(tc.getTicketStatus())) {
                                            reopenTicket(idTicket);
                                        }

                                    } catch (Exception ex) {
                                        info.Log(getClassName(), ex.getMessage());
                                    }
                                }

                                if ("TIBA".equalsIgnoreCase(summary)) {
                                    if ("40".equalsIgnoreCase(tc.getChannel())) {
                                        info.Log(getClass().getName(), "call MYIHX+++++++++++++++++++++++++");
                                        GetMasterParamDao url = new GetMasterParamDao();
                                        ApiConfig ac = new ApiConfig();
                                        ac = url.getUrl("update_status_ticket_to_myihx");
                                        boolean updateToMyihx = myihx.updateStatusToMyihx(tc, ac);
                                    }
                                }

                                if (ceksymptomMBravoWL &&
                                        "GAMAS".equalsIgnoreCase(sourceTicket)) {
                                }

                                try {
                                    if (result) {
                                        jObj.put("recordkey", r.getRecordkey());
                                        jObj.put("parent_id", r.getParentid());
                                        jObj.put("class", r.getClasS());
                                        jObj.put("createdBy", r.getCreatedBy());
                                        jObj.put("log_type", r.getLogType());
                                        jObj.put("createdDate", r.getCreatedDate());
                                        jObj.put("ownergroup", r.getOwnergroup());
                                        jObj.put("summary", r.getSummary());

                                        mainObj.put("data", jObj);
                                        mainObj.put("code", "200");
                                        mainObj.put("message", "Data berhasil di insert");
                                        JSONObject jsonRequest = new JSONObject(dataObj);
                                        String detailReq = jsonRequest.has("detail") ? jsonRequest.getString("detail") : "";
                                        jsonRequest.remove("detail");
                                        jsonRequest.put("detail_", detailReq);
                                        JSONObject jsonResponse = new JSONObject(mainObj);
                                        insertLosgHystoryAPI(
                                                jsonRequest,
                                                idTicket,
                                                jsonResponse,
                                                200
                                        );

                                        if (((!"DCS".equalsIgnoreCase(tc.getCust_segment()) && !"PL-TSEL".equalsIgnoreCase(tc.getCust_segment())) && !"DWS".equalsIgnoreCase(tc.getCust_segment())) && "35".equals(tc.getChannel())) {
                                            updateToMyTens(myihx, recordkey, idWorklog, summary, logType, user, date);
                                        }
                                        mainObj.write(response.getWriter());

                                    } else {
                                        mainObj.put("date", date);
                                        mainObj.put("code", "500");
                                        mainObj.put("message", "Data gagal di insert");
                                        headMainObj.put("error", mainObj);

                                        JSONObject jsonRequest = new JSONObject(dataObj);
                                        String detailReq = jsonRequest.has("detail") ? jsonRequest.getString("detail") : "";
                                        jsonRequest.remove("detail");
                                        jsonRequest.put("detail_", detailReq);
                                        JSONObject jsonResponse = new JSONObject(headMainObj);
                                        insertLosgHystoryAPI(
                                                jsonRequest,
                                                idTicket,
                                                jsonResponse,
                                                200
                                        );

                                        headMainObj.write(response.getWriter());
                                    }

                                } catch (JSONException ex) {
                                    info.Log(getClassName(), ex.getMessage());
                                }
                            } else {
                                try {
                                    mainObj.put("date", date);
                                    mainObj.put("code", "500");
                                    mainObj.put("message", "The ticket is already closed.");
                                    headMainObj.put("error", mainObj);

                                    JSONObject jsonRequest = new JSONObject(dataObj);
                                    String detailReq = jsonRequest.has("detail") ? jsonRequest.getString("detail") : "";
                                    jsonRequest.remove("detail");
                                    jsonRequest.put("detail_", detailReq);
                                    JSONObject jsonResponse = new JSONObject(headMainObj);
                                    insertLosgHystoryAPI(
                                            jsonRequest,
                                            idTicket,
                                            headMainObj,
                                            200
                                    );

                                    headMainObj.write(response.getWriter());
                                } catch (JSONException ex) {
                                    info.Log(getClassName(), ex.getMessage());
                                }
                            }
                        } else {
                            try {
                                mainObj.put("date", date);
                                mainObj.put("code", "500");
                                mainObj.put("message", "ID Ticket tidak sesuai");
                                headMainObj.put("error", mainObj);

                                JSONObject jsonRequest = new JSONObject(dataObj);
                                String detailReq = jsonRequest.has("detail") ? jsonRequest.getString("detail") : "";
                                jsonRequest.remove("detail");
                                jsonRequest.put("detail_", detailReq);
                                JSONObject jsonResponse = new JSONObject(headMainObj);
                                insertLosgHystoryAPI(
                                        jsonRequest,
                                        idTicket,
                                        jsonResponse,
                                        200
                                );

                                headMainObj.write(response.getWriter());
                            } catch (JSONException ex) {
                                info.Log(getClassName(), ex.getMessage());
                            }
                        }
                    } catch (ParseException ex) {
                        info.Log(getClassName(), ex.getMessage());
                    } finally {
                        tc = null;
                    }
                } else {

                    //jika header tidak valid
                    try {
                        jObj = new JSONObject();
                        mainObj.put("date", date);
                        mainObj.put("code", "401");
                        mainObj.put("message", "Invalid Authentication");
                        headMainObj.put("error", mainObj);

                        JSONObject jsonRequest = new JSONObject();
                        JSONObject jsonResponse = new JSONObject(headMainObj);
                        insertLosgHystoryAPI(
                                jsonRequest,
                                idTicket,
                                jsonResponse,
                                200
                        );

                        headMainObj.write(response.getWriter());

                    } catch (JSONException ex) {
                        info.Log(getClassName(), ex.getMessage());
                    }
                }

            } else {
                try {
                    mainObj.put("date", date);
                    mainObj.put("code", "405");
                    mainObj.put("message", method + " Method Not Allowed");
                    headMainObj.put("error", mainObj);

                    JSONObject jsonRequest = new JSONObject();
                    JSONObject jsonResponse = new JSONObject(headMainObj);
                    insertLosgHystoryAPI(
                            jsonRequest,
                            idTicket,
                            jsonResponse,
                            200
                    );
                    headMainObj.write(response.getWriter());
                } catch (JSONException ex) {
                    info.Log(getClassName(), ex.getMessage());

                }
            }
        } catch (Exception e) {
            info.Log(getClassName(), e.getMessage());
        }

    }

    public void reopenTicket(String idTicket) throws Exception {
        Ticket dataTicket = null;
        List<WorkflowActivity> activityList = null;
        try {
            CompleteActivityTicketIncidentApiDao dao = new CompleteActivityTicketIncidentApiDao();
            dataTicket = new Ticket();
            dataTicket = dao.getProcessIdTicketWithShk(idTicket);
            String state = (dataTicket.getState() == null ? "" : dataTicket.getState());
            String status = dataTicket.getTicketStatus() == null ? "" : dataTicket.getTicketStatus();
            dao.updateStatusTmp("REOPEN", "1", "INTEGRATION", dataTicket.getId());
            ApplicationContext ac = AppUtil.getApplicationContext();
            WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
            if (state.equals("1000003") && !"CLOSED".equalsIgnoreCase(status)) {
                String activityid = dataTicket.getActiviytName();
                String processDefId = dataTicket.getProcessDefId().replace("#", ":");
                String ChildProcessId = dataTicket.getProcessId();
                String idActivity = dataTicket.getActivityId();
                if ("SALAMSIM".equalsIgnoreCase(status) || "MEDIACARE".equalsIgnoreCase(status)) {
                    workflowManager.assignmentForceComplete(processDefId, ChildProcessId, idActivity, "000000");
                }
            }
        } catch (JSONException e) {
            info.Log(getClassName(), e.getMessage());
        } finally {
            dataTicket = null;
            activityList = null;
        }
    }

    private void insertLosgHystoryAPI(JSONObject request,
            String ticketNumber,
            JSONObject response,
            int responseCode) {
        logHistoryDao.SENDHISTORY(
                ticketNumber,
                "INSERT WORKLOGS",
                "https://oss-incident.telkom.co.id/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.InsertTicketWorkLogs/service",
                "POST",
                request,
                response,
                responseCode
        );

        KafkaProducerService kafkaProducerService = new KafkaProducerService();
        kafkaProducerService.sendMessage("usrINSERA_topic", null, request.toString(), ticketNumber, "Ini Data Wokrlog");
    }

    public void updateToMyTens(SendStatusToMYIHXApi myihx, String ticketId, String worklogId, String summary, String type, String createdBy, String createdAt) throws Exception {
        MasterParamDao paramDao = new MasterParamDao();
        MasterParam param = new MasterParam();
        param = paramDao.getUrl("update_worklog_to_mytens");

        org.json.simple.JSONObject json = new org.json.simple.JSONObject();
        json.put("worklogId", worklogId);
        json.put("ticketId", ticketId);
        json.put("summary", summary);
        json.put("class", "INCIDENT");
        json.put("type", type);
        json.put("createdBy", createdBy);
        json.put("createdAt", createdAt);
        JSONObject jsn = new JSONObject(json);
        myihx.requestToAPIGWSIT("UpdateWorklogToMyTens(" + ticketId + ")", "", ticketId, param.getUrl(), jsn);

    }

    public boolean uploadAttachmentToMinio(String base64Image, String fileName, String path) throws Exception {
        MasterParam param = new MasterParam();
        MasterParamDao paramDao = new MasterParamDao();
        param = paramDao.getUrl("minIO_branch");
        boolean statusUpload = false;
        try {
            String minioUrl = param.getUrl();
            String accessKey = param.getjUsername();
            String secretKey = param.getjPassword();
            String bucketName = "oss-transformation";

            String image = base64Image.split(",")[1];
            byte[] imageBytes = Base64.getDecoder().decode(image);
            String extention = base64Image.split(";")[0].split(":")[1].split("/")[1];
            String objectName = path + fileName + "." + extention;
            InputStream inputStream = new ByteArrayInputStream(imageBytes);
            long objectSize = inputStream.available();

            MinioClient minioClient = new MinioClient.Builder()
                    .endpoint(minioUrl)
                    .credentials(accessKey, secretKey)
                    .build();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(new ByteArrayInputStream(imageBytes), objectSize, -1)
                    .contentType(getContentType(extention))
                    .build()
            );

            statusUpload = true;

        } catch (InsufficientDataException ex) {
            LogUtil.error(this.getClassName(), ex, "error InsufficientDataException : " + ex.getMessage());
        } catch (InternalException ex) {
            LogUtil.error(this.getClassName(), ex, "error InternalException : " + ex.getMessage());
        } catch (InvalidKeyException ex) {
            LogUtil.error(this.getClassName(), ex, "error InvalidKeyException : " + ex.getMessage());
        } catch (InvalidResponseException ex) {
            LogUtil.error(this.getClassName(), ex, "error InvalidResponseException : " + ex.getMessage());
        } catch (IOException ex) {
            LogUtil.error(this.getClassName(), ex, "error IOException : " + ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            LogUtil.error(this.getClassName(), ex, "error NoSuchAlgorithmException : " + ex.getMessage());
        } catch (ServerException ex) {
            LogUtil.error(this.getClassName(), ex, "error ServerException : " + ex.getMessage());
        } catch (XmlParserException ex) {
            LogUtil.error(this.getClassName(), ex, "error XmlParserException : " + ex.getMessage());
        } catch (ErrorResponseException ex) {
            LogUtil.error(this.getClassName(), ex, "error ErrorResponseException : " + ex.getMessage());
        }

        return statusUpload;

    }

    private String getContentType(String extension) {
        String type = "";
        switch (extension) {
            case "png":
                type = "image/png";
                break;
            case "jpg":
            case "jpeg":
                type = "image/jpeg";
                break;
            case "pdf":
                type = "application/pdf";
                break;
            case "txt":
                type = "text/plain";
                break;
            case "doc":
                type = "application/msword";
                break;
            case "docx":
                type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                break;
            case "xml":
                type = "application/xml";
                break;
        }
        return type;
    }

    private String[] getListSymptomCts() {
        SelectCollections sc = new SelectCollections();
        JSONObject getConfiguration;
        String symptompList = "";
        String[] _symptomListCTS = new String[]{};

        try {
            getConfiguration = sc.getConfigurationMapping();
            if (getConfiguration.has("cts_symptom")) {
                symptompList = getConfiguration.getString("cts_symptom").trim();
            }

            if (symptompList.length() > 0) {
                _symptomListCTS = symptompList.split(",");

                for (int i = 0; i < _symptomListCTS.length; i++) {
                    _symptomListCTS[i] = _symptomListCTS[i].trim();
                }

            }

        } catch (Exception e) {
            info.Error(this.getClass().getName(), e.getMessage(), e);
        }

        return _symptomListCTS;
    }

}
