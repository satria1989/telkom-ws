package id.co.itasoft.telkom.oss.plugin.scc;

import id.co.itasoft.telkom.oss.plugin.dao.*;
import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.function.*;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import id.co.itasoft.telkom.oss.plugin.model.TicketStatus;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class SccController {

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
    StringManipulation stringManipulation = new StringManipulation();


    UpdateTicketDao updTicketDao = new UpdateTicketDao();

    public JSONObject SccVoice(TicketStatus ticketStatus) {
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil
                .getApplicationContext()
                .getBean("workflowUserManager");
        String USERNAME = workflowUserManager.getCurrentUsername();


        JSONObject json = new JSONObject();
        try {
            String serviceType = (ticketStatus.getServiceType() == null) ? "" : ticketStatus.getServiceType();
            String serviceNo = (ticketStatus.getServiceNo() == null) ? "" : ticketStatus.getServiceNo();
            String ticketId = (ticketStatus.getTicketId() == null) ? "" : ticketStatus.getTicketId();

            param = paramDao.getUrl("sccVoice");
            String URL = param.getUrl();

            JSONObject request = new JSONObject();
            JSONObject readLogCallLastRequest = new JSONObject();
            JSONObject eaiHeader = new JSONObject();
            JSONObject eaiBody = new JSONObject();

            // EAIHEADER
            eaiHeader.put("externalId", "");
            eaiHeader.put("timestamp", new Timestamp(System.currentTimeMillis()));

            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            // Format tanggal yang diinginkan
            String dateFormat = "yyyy-MM-dd";

            // Menggunakan SimpleDateFormat untuk parsing
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

            // Mengubah timestamp menjadi string
            String dateString = sdf.format(currentTimestamp);

            // Parsing kembali string ke objek Date
//            Date parsedDate = sdf.parse(dateString);

            LocalDate currentDate = LocalDate.now();

            String hash = "telkom#" + serviceNo + "#" + currentDate;

            // EAIBODY
            eaiBody.put("phone", serviceNo);
            eaiBody.put("date", currentDate);
            eaiBody.put("hash", generateSHA1Handler.sha1(hash));

            readLogCallLastRequest.put("eaiHeader", eaiHeader);
            readLogCallLastRequest.put("eaiBody", eaiBody);

            request.put("readLogCallLastRequest", readLogCallLastRequest);

            RequestBody body = RequestBody.create(_RESTAPI.JSON, request.toString());
            String TOKEN = _RESTAPI.getToken();
            Request requestBuilder
                    = new Request.Builder()
                    .url(URL)
                    .addHeader("Authorization", "Bearer " + TOKEN)
                    .post(body)
                    .build();

            JSONObject response = _RESTAPI.CALLAPIHANDLER(requestBuilder);
//            json = response;

            boolean boolCheckScc = response.getBoolean("status");
            int stsCodeScc = response.getInt("status_code");
            json.put("status", boolCheckScc);
            json.put("status_code", stsCodeScc);

            String testResult = "";
            JSONObject _JSONOBJECT = new JSONObject();
            String timestamp = "";
            String responseCode = "";
            if (boolCheckScc) {
                String ticket_close_flag = "";

                String msg = response.getString("msg");
                Object object = new JSONTokener(msg).nextValue();
                _JSONOBJECT = (JSONObject) object;

                if (_JSONOBJECT.has("readlogCallLastResponse")) {
                    timestamp = _JSONOBJECT
                            .getJSONObject("readlogCallLastResponse")
                            .getJSONObject("eaiHeader")
                            .getString("timestamp");
                    responseCode = _JSONOBJECT
                            .getJSONObject("readlogCallLastResponse")
                            .getJSONObject("eaiStatus")
                            .getString("srcResponseCode");

                }

                if (_JSONOBJECT.getJSONObject("readlogCallLastResponse").has("eaiBody")) {
                    // CALBACK SUCCESS
                    if (_JSONOBJECT
                            .getJSONObject("readlogCallLastResponse")
                            .getJSONObject("eaiBody")
                            .getBoolean("success")) {

                        ticket_close_flag = _JSONOBJECT
                                .getJSONObject("readlogCallLastResponse")
                                .getJSONObject("eaiBody")
                                .getJSONObject("data")
                                .getString("ticket_close_flag");

                        if (ticket_close_flag.equals("1")) {
                            testResult = "PASSED";
                        } else {
                            testResult = "FAILED";
                        }

                    }

                    // CALLBACK ERROR
                    if (_JSONOBJECT
                            .getJSONObject("readlogCallLastResponse")
                            .getJSONObject("eaiBody")
                            .has("error")) {
                        testResult = "not_found";
                    }
                    json.put("ivrTime", timestamp);
                    json.put("testResult", testResult);
                } else {
                    testResult = "not_found";
                    json.put("response_message", response.getString("response_message"));
                    json.put("testResult", testResult);
                }

                insertTicketWorkLogs.InsertTicketWorkLogsByParentTicket(
                        USERNAME,
                        "Check SCC QC",
                        _JSONOBJECT.toString(),
                        ticketId
                );

                updTicketDao.updateSCC(
                        ticketId,
                        timestamp,
                        serviceType,
                        testResult,
                        responseCode
                );

            }

            logHistoryDao.SENDHISTORY(
                    ticketId,
                    "CallSCC",
                    URL,
                    "POST",
                    request,
                    response,
                    stsCodeScc
            );


        } catch (Exception ex) {
            logInfo.Error(getClass().getName(), "ERROR SCC VOICE : ", ex);
        }

        return json;

    }

    public JSONObject SccInternetIptv(TicketStatus ticketStatus) {
        JSONObject json = new JSONObject();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil
                .getApplicationContext()
                .getBean("workflowUserManager");
        String USERNAME = workflowUserManager.getCurrentUsername();

        try {

            String serviceType = (ticketStatus.getServiceType() == null) ? "" : ticketStatus.getServiceType();
            String serviceNo = (ticketStatus.getServiceNo() == null) ? "" : ticketStatus.getServiceNo();
            String ticketId = (ticketStatus.getTicketId() == null) ? "" : ticketStatus.getTicketId();
            String workzone = (ticketStatus.getWorkZone() == null) ? "" : ticketStatus.getWorkZone();

            JSONObject msgError = new JSONObject();

            // GET URL ON MASTERPARAM FOR CALL SCC API (APIGW LINK)
            param = paramDao.getUrl("get_scc");
            String urlSCC = param.getUrl();

            // CREATE REQUEST BODY
            String hash = "INFOMEDIA#" + serviceNo + "#NUSANTARA";
            JSONObject jsonBody = new JSONObject();
            JSONObject jsonChild = new JSONObject();

            // set jsonHeader
            jsonChild.put("externalId", "");
            jsonChild.put("timestamp", new Timestamp(System.currentTimeMillis()));
            jsonBody.put("eaiHeader", jsonChild);

            // set jsonBody
            jsonChild = new JSONObject();
            jsonChild.put("nd", serviceNo);
            jsonChild.put("hash", generateSHA1Handler.sha1(hash));
            jsonChild.put("ticket", ticketId);
            jsonBody.put("eaiBody", jsonChild);

            JSONObject jsonOBJJ = new JSONObject();
            jsonOBJJ.put("apiCloseTickInet_Request", jsonBody);

            RequestBody body = RequestBody.create(_RESTAPI.JSON, jsonOBJJ.toString());
            String TOKEN = _RESTAPI.getToken();
            Request request
                    = new Request.Builder()
                    .url(urlSCC)
                    .addHeader("Authorization", "Bearer " + TOKEN)
                    .post(body)
                    .build();

            JSONObject resScc = _RESTAPI.CALLAPIHANDLER(request);
//            json = resScc;

            boolean boolCheckScc = resScc.getBoolean("status");
            int stsCodeScc = resScc.getInt("status_code");

            if (boolCheckScc) {

                String timestamp = "";
                String testResult = "";
                String close = "";
                String responseCode = "";
                String speed_passed = "";
                String redaman_passed = "";
                String error = "";
                String msg = resScc.getString("msg");
                Object object = new JSONTokener(msg).nextValue();
                JSONObject _JSONOBJECT = (JSONObject) object;

                if (_JSONOBJECT.has("apiCloseTickInet_Response")) {
                    timestamp = _JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                            .getJSONObject("eaiHeader").getString("responseTimestamp");
                    responseCode = _JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                            .getJSONObject("eaiStatus").getString("srcResponseCode");
                }

//                logInfo.Log(getClass().getName(), "CHECK SCC INTERNET RESPONSE" + msg);
                if (_JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                        .has("eaiBody")) {
                    if (_JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                            .getJSONObject("eaiBody").has("data")) {

                        JSONObject data = _JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                                .getJSONObject("eaiBody").getJSONObject("data");

                        close = data.isNull("close") ? "" : data.getString("close");

                        speed_passed = data.isNull("speed_passed") ? "" : data.getString("speed_passed");

                        redaman_passed = data.isNull("redaman_passed") ? "" : data.getString("redaman_passed");

                        // GET SSC_PLUS ON CONFIGURATION
                        JSONObject CONFIGURESETTING = configurationDao.getConfigurationMapping();
//                        list exclude
                        String sccPlusList = CONFIGURESETTING.getString("scc_plus");
//                        check button scc+
                        Boolean buttonSccPlus = CONFIGURESETTING.getBoolean("check_sccplus_nas");
                        String arrSccPlus[] = {};

                        if (sccPlusList.length() > 0) {
                            arrSccPlus = sccPlusList.split(",");
                            for (int i = 0; i < arrSccPlus.length; i++) {
                                arrSccPlus[i] = arrSccPlus[i].trim();
                            }
                        }

                        boolean isWorkzoneExternalSccPlus = arrayManipulation.SearchDataOnArray(
                                arrSccPlus,
                                workzone
                        );

                        if (buttonSccPlus && !isWorkzoneExternalSccPlus) {
                            if ("1".equals(close)) {
                                testResult = "PASSED";
                            } else {
                                testResult = "FAILED";
                            }
                        } else if (isWorkzoneExternalSccPlus) {
                            if ("1".equals(speed_passed) && "1".equals(redaman_passed)) {
                                testResult = "PASSED";
                            } else {
                                testResult = "FAILED";
                            }
                        }
                    }

                    if (_JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                            .getJSONObject("eaiBody").has("error")) {
                        error = _JSONOBJECT.getJSONObject("apiCloseTickInet_Response")
                                .getJSONObject("eaiBody").getJSONArray("error").getString(0);
                        if (error.contains("not found")) {
                            testResult = "not_found";
                        }
                    }
                    json.put("internetTime", timestamp);
                    json.put("responseCode", responseCode);
                    json.put("testResult", testResult);
                } else {
                    testResult = "not_found";
                    json.put("response_message", resScc.getString("response_message"));
                    json.put("testResult", testResult);
                }

                updTicketDao.updateSCC(
                        ticketId,
                        timestamp,
                        serviceType,
                        testResult,
                        responseCode
                );

                json.put("status", boolCheckScc);
                json.put("status_code", stsCodeScc);

                String details = _JSONOBJECT.toString() + json.toString();
                insertTicketWorkLogs.InsertTicketWorkLogsByParentTicket(
                        USERNAME,
                        "Check SCC QC",
                        details,
                        ticketId
                );
                msg = null;
                _JSONOBJECT = null;
            }

            logHistoryDao.SENDHISTORY(
                    ticketId,
                    "CallSCC",
                    urlSCC,
                    "POST",
                    jsonOBJJ,
                    resScc,
                    stsCodeScc
            );


            jsonBody = null;
            jsonChild = null;
            jsonOBJJ = null;
            resScc = null;

        } catch (Exception ex) {
            logInfo.Error(getClass().getName(), "error scc", ex);
        }

        return json;

    }

}
