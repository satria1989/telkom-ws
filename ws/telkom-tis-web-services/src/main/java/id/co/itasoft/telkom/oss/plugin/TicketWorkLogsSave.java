/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GamasTicketRelationDao;
import id.co.itasoft.telkom.oss.plugin.dao.GetMasterParamDao;
import id.co.itasoft.telkom.oss.plugin.dao.InsertTicketWorkLogsDao;
import id.co.itasoft.telkom.oss.plugin.dao.SelectCollections;
import id.co.itasoft.telkom.oss.plugin.dao.TicketWorkLogsSaveDao;
import id.co.itasoft.telkom.oss.plugin.function.ArrayManipulation;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.function.SendStatusToMYIHXApi;
import id.co.itasoft.telkom.oss.plugin.kafkaHandler.KafkaProducerService;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.Ticket;
import id.co.itasoft.telkom.oss.plugin.model.TicketWorkLogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.FileManager;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.SecurityUtil;
import org.joget.commons.util.SetupManager;
import org.joget.commons.util.UuidGenerator;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.util.FileCopyUtils;

/**
 * @author suena
 */
// [POST] /jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.TicketWorkLogsSave/service?
public class TicketWorkLogsSave extends Element implements PluginWebSupport {

    /*deklarasi variable global*/
    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Ticket Work Logs Save";
    LogInfo info = new LogInfo();
    ArrayManipulation arrayManipulation = new ArrayManipulation();

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

    InsertTicketWorkLogs itw = new InsertTicketWorkLogs();
    RESTAPI _RESTAPI = new RESTAPI();

    public void storeFile(File file, String tableName, String primaryKeyValue) {
        try {
            if (file != null && file.exists()) {
                String path = getUploadPath(tableName, primaryKeyValue);
                String newPath = file.getPath();

                File newDirectory = new File(path);
                String fileUpload = path + "/" + file.getName();
                if (!newDirectory.exists()) {
                    newDirectory.mkdir();
                }
                FileCopyUtils.copy(file, new File(newDirectory, file.getName()));
            }
        } catch (IOException ex) {
            info.Log(getClassName(), ex.getMessage());
        }
    }

    public String getUploadPath(String tableName, String primaryKeyValue) {
        // validate input
        String normalizedTableName = SecurityUtil.normalizedFileName(tableName);

        String normalizedPrimaryKeyValue = SecurityUtil.normalizedFileName(primaryKeyValue);

        String formUploadPath = SetupManager.getBaseDirectory();

        // determine base path
        SetupManager setupManager = (SetupManager) AppUtil.getApplicationContext().getBean("setupManager");
        String dataFileBasePath = setupManager.getSettingValue("dataFileBasePath");
        if (dataFileBasePath != null && dataFileBasePath.length() > 0) {
            formUploadPath = dataFileBasePath;
        }

        return formUploadPath + "app_formuploads" + File.separator + normalizedTableName + File.separator + normalizedPrimaryKeyValue + File.separator;
    }

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject mainObj = new JSONObject();
        JSONObject jObj = new JSONObject();
        JSONArray jArray = new JSONArray();

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        String reqUrl = request.getRequestURL().toString();
        String domain = reqUrl.split("/jw")[0];

        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        try {
            /*Cek Hanya User Yang Sudah Login Bisa Mengakses Ini*/
            if (!workflowUserManager.isCurrentUserAnonymous()) {

                TicketWorkLogsSaveDao dao = new TicketWorkLogsSaveDao();
                String method = request.getMethod();
                SendStatusToMYIHXApi myihx = new SendStatusToMYIHXApi();
                InsertTicketWorkLogsDao itwDao = new InsertTicketWorkLogsDao();
                RESTAPI restApi = new RESTAPI();

                if ("POST".equalsIgnoreCase(method)) {

                    //fungsi untuk mendapatkan tanggal, jam menit saat ini
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateTime = sdf.format(new Date());

                    String recordkey = request.getParameter("recordkey");
                    String clasS = request.getParameter("class");
                    String logType = request.getParameter("log_type");

                    String idTicket = request.getParameter("id_ticket");
                    String user = request.getParameter("user");
                    String clientViewAble = request.getParameter("clientviewable");
                    String OwnerGroup = request.getParameter("ownergroup");
                    String assignmentId = request.getParameter("assignmentid");
                    String orgId = request.getParameter("orgid");
                    String summary = "";
                    if (logType.equalsIgnoreCase("UPDATEINFO MGT")) {
                        summary = request.getParameter("summary_mgt");
                    } else {
                        summary = request.getParameter("summary");
                    }
                    info.Log(this.getClass().getName(), summary);
                    String siteid = request.getParameter("siteid");
                    String workLogId = request.getParameter("worklogid");
                    String anyWhereRrefid = request.getParameter("anywhererrefid");
                    String summarySectionControl = request.getParameter("summary_section_control");
                    String detail = (request.getParameter("detail") == null ? "" : request.getParameter("detail"));
                    String attachmentFile = request.getParameter("attachment_file");
                    String[] attachmentFilePaths = request.getParameterValues("attachment_file_path");
                    String parentId = request.getParameter("parentid");
                    String createdBy = request.getParameter("createdby");

                    //Pastikan id_ticket atau recordkey uniq
//                    String id = dao.getIdByTicketId(recordkey);
                    List<String> resultedValue = new ArrayList<String>();
                    List<String> filePaths = new ArrayList<String>();
//
                    UuidGenerator uuid = UuidGenerator.getInstance();
                    String uidStr = uuid.getUuid();
                    String delimitedValue = null;

                    Document detailHtml = (Document) Jsoup.parse(detail);
                    Elements imgDetail = detailHtml.getElementsByTag("img");
                    String fixPathDetail = "";
                    String fileName = "";
//                    String base64String = "";
                    if (!imgDetail.isEmpty()) {
                        for (org.jsoup.nodes.Element element : imgDetail) {
                            String[] path = element.toString().split(";");
                            for (String spath : path) {
                                if (spath.contains("_path=")) {
                                    fixPathDetail = spath.substring(6, spath.length() - 2);
//                                    base64String = fileToBase64("/home/jboss/wflow/app_tempfile/"+fixPathDetail.replace("%2F", "/"));

                                    File fileDetail = FileManager.getFileByPath(fixPathDetail);
                                    if (fileDetail == null) {
                                    } else {
                                        filePaths.add(fixPathDetail);
                                        resultedValue.add(fileDetail.getName());
                                        fileName = fileDetail.getName();
                                        sendFileMinio(fileDetail);
                                        info.Log(getClassName(), "FILE DETAIL :" + fileDetail);
                                        FileManager.deleteFile(fileDetail);
//                                        storeFile(fileDetail, "ticket_work_logs", uidStr);
                                    }
                                }
                            }

//                            element.attr("src", "data:image/png;base64,"+base64String);
//                            element.attr("src", "/jw/web/client/app/ticketIncidentService/form/download/formWorkLogs/" + uidStr + "/" + fileName + ".");
                            element.attr("src", domain + "/jw/web/json/plugin/id.co.telkom.webservice.insera.plugin.GetFileStream/service?fileName=" + fileName);
                        }
                    }

                    detail = detailHtml.toString();

//                    LogUtil.info(this.getClassName(), "detail : "+detail);
                    // SUMMARY
                    if (attachmentFilePaths != null) {
                        for (String attachmentFilePath : attachmentFilePaths) {
                            File file = FileManager.getFileByPath(attachmentFilePath);

                            if (file == null) {
                            } else {
                                filePaths.add(attachmentFilePath);
//                                LogUtil.info(this.getClassName(), "file name : " + file.getName());
                                resultedValue.add(file.getName());
                                sendFileMinio(file);
                            }

                        }
                        delimitedValue = FormUtil.generateElementPropertyValues(resultedValue.toArray(new String[]{}));
                    } else {
                        delimitedValue = "";
                    }

                    ArrayManipulation am = new ArrayManipulation();
                    GamasTicketRelationDao gtrDao = new GamasTicketRelationDao();
                    Ticket tc = new Ticket();

                    tc = gtrDao.getDataChildTicket(recordkey);

                    if (!"".equalsIgnoreCase(tc.getId())) {
                        /*PROCES*/
                        TicketWorkLogs r = new TicketWorkLogs();
                        r.setId(uidStr);
                        r.setRecordkey(recordkey);
                        r.setParentid(tc.getId());
                        r.setClasS(clasS);
                        r.setCreatedBy(createdBy);
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
                        r.setAttachmentFile(delimitedValue);
                        r.setCreatedDate(currentDateTime);

                        Enumeration<String> parameterNames = request.getParameterNames();
                        String channel = tc.getChannel() == null ? "" : tc.getChannel();
                        String custSegment = tc.getCust_segment() == null ? "" : tc.getCust_segment();
                        String externalTicketId = tc.getExtenalTicketId() == null ? "" : tc.getExtenalTicketId();
                        String externalTicketTier3 = tc.getExtenalTicketTier3() == null ? "" : tc.getExtenalTicketTier3();
                        String symptom = tc.getSymptom() == null ? "" : tc.getSymptom();
                        String sourceTicket = tc.getSource_ticket() == null ? "" : tc.getSource_ticket();
                        String lati = tc.getLatitude() == null ? "" : tc.getLatitude();
                        String longi = tc.getLongitude() == null ? "" : tc.getLongitude();
                        String summ = r.getSummary() == null ? "" : r.getSummary();
                        String createdBY = r.getCreatedby() == null ? "" : r.getCreatedby();

                        String symptomMBravoWL[] = {"C_GAMAS_002_034", "C_GAMAS_001_034"};
                        boolean ceksymptomMBravoWL = am.SearchDataOnArray(symptomMBravoWL, symptom);
                        while (parameterNames.hasMoreElements()) {

                            String paramName = parameterNames.nextElement();
                            String[] paramValues = request.getParameterValues(paramName);
                            for (int i = 0; i < paramValues.length; i++) {
                                String paramValue = paramValues[i];
                            }
                        }

                        boolean result = dao.TicketWorkLogsSave(r);//
                        dao.updateWorklogSummary(summary, tc.getId());
                        boolean resultSerachSymptompInap;
                        resultSerachSymptompInap = arrayManipulation.SearchDataOnArray(getListSymptomCts(), symptom);
//                        String ticketStatus = dao.getTicketStatus(recordkey);
                        if ("56".equals(channel) && "DWS".equalsIgnoreCase(custSegment)) {
                            restApi.addWorklogCts(tc, summary, detail, user, externalTicketId);
                        }

                        if ("56".equals(channel) || resultSerachSymptompInap) {
                            restApi.addWorklogInap(tc, summary, createdBy, externalTicketId);
                        }

                        if ("CUSTOMER".equalsIgnoreCase(tc.getSource_ticket())
                                && "DWS".equalsIgnoreCase(tc.getCust_segment())) {
                            restApi.updateWorklogOGD(recordkey, summary, logType);
                        }

//                        info.Log(getClass().getName(), "Ticket MBravo ? " + ceksymptomMBravoWL);
                        if (ceksymptomMBravoWL
                                && "GAMAS".equalsIgnoreCase(sourceTicket)) {
                            info.Log(getClass().getName(), "MASUK MBRAVO WORKLOGS");
                            restApi.addWorkLogsMBravo(tc, createdBY, externalTicketTier3, summary);
                        }

                        if ("CLIENTNOTE".equalsIgnoreCase(logType)) {
                            itwDao.UpdateLapul(idTicket);
                            if ("MEDIACARE".equalsIgnoreCase(tc.getTicketStatus()) || "SALAMSIM".equalsIgnoreCase(tc.getTicketStatus())) {
                                try {
                                    itw.reopenTicket(recordkey);
//                            reopenClientNote(recordkey);
                                } catch (Exception ex) {
                                    info.Log(getClassName(), ex.getMessage());
                                }
                            }

                        }

                        try {
                            /*OUTPUT : buat response /return / hasil*/
                            if (result) {

                                jObj.put("recordkey", r.getRecordkey());
                                jObj.put("parent_id", r.getParentid());
                                jObj.put("class", r.getClasS());
                                jObj.put("createdBy", r.getCreatedBy());
                                jObj.put("log_type", r.getLogType());
                                jObj.put("createdDate", r.getCreatedDate());
                                jObj.put("ownergroup", r.getOwnergroup());
                                jObj.put("summary", r.getSummary());
                                jObj.put("id_worklog", r.getId());

                                mainObj.put("data", jObj);
                                mainObj.put("status", true);
                                mainObj.put("message", "Data berhasil di insert");
                                KafkaProducerService kafkaProducerService = new KafkaProducerService();
                                kafkaProducerService.sendMessage("usrINSERA_topic", null, jObj.toString(), idTicket, "Ini Data Wokrlog");
                                if (((!"DCS".equalsIgnoreCase(tc.getCust_segment()) && !"PL-TSEL".equalsIgnoreCase(tc.getCust_segment())) && !"DWS".equalsIgnoreCase(tc.getCust_segment()) && "35".equals(tc.getChannel()))) {
                                    itw.updateToMyTens(myihx, recordkey, uidStr, summary, logType, createdBy, date);
                                }
                            } else {
                                mainObj.put("status", false);
                                mainObj.put("data", jObj);
                                mainObj.put("message", "Data gagal di insert");
                            }

                            mainObj.write(response.getWriter());

                            //tulis hasil akhir
                        } catch (JSONException ex) {
                            info.Log(getClassName(), ex.getMessage());
                        } finally {
                            tc = null;
                            imgDetail.clear();
                        }
                    }

                } else {
                    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method Not Allowed");
                }

            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication.");
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            LogUtil.error(this.getClassName(), ex, "error_wl : " + ex.getMessage());
        }

    }

    public void reopenClientNote(String idTicket) throws Exception {
        GetMasterParamDao paramDao = new GetMasterParamDao();
        ApiConfig apiConfig = new ApiConfig();
        apiConfig = paramDao.getUrl("update_status");
        RequestBody formBody = new FormBody.Builder()
                .add("ticket_number", idTicket)
                .add("action", "REOPEN")
                .build();

        Request request = new Request.Builder()
                .url(apiConfig.getUrl())
                .addHeader("api_key", apiConfig.getApiKey())
                .addHeader("api_id", apiConfig.getApiId())
                .post(formBody)
                .build();

        _RESTAPI.CALLAPI(request);

    }

    //Untuk Mengirim File ke Minio
    public String sendFileMinio(File file) throws Exception {

        GetMasterParamDao paramDao = new GetMasterParamDao();
        ApiConfig apiConfig = new ApiConfig();
        apiConfig = paramDao.getUrl("minIo_worklog");
        String url = apiConfig.getUrl();
        String stringResponse = "";

        info.Log(getClassName(), "fileName :" + file.getName());
        info.Log(getClassName(), "fileName :" + file.getPath());

        //format send menggunakan json
        JSONObject jsonData = new JSONObject();
        jsonData.put("action", "upload");
        jsonData.put("fileName", file.getName());
        jsonData.put("filePath", file.getPath());

//        LogUtil.info(this.getClass().getName(), json);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), jsonData.toString());

        Request request = new Request.Builder()
                .url(url)//url dari web service /json/plugin/org.joget.marketplace.CustomMinioUploadAndDownload/service?";
                .post(body)
                .build();

        stringResponse = _RESTAPI.CALLAPI(request);
        return stringResponse;
    }

    private String fileToBase64(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStream.read(bytes);
        fileInputStream.close();

        // Encode the byte array to a Base64 string
        return Base64.getEncoder().encodeToString(bytes);
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
