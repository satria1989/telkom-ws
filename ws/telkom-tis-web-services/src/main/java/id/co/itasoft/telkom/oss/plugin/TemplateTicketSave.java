package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.GetMasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.service.ReadExcelFileService;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.FileManager;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

public class TemplateTicketSave extends Element implements PluginWebSupport {

    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Template Ticket Save";
    ReadExcelFileService readExcelFileService = new ReadExcelFileService();
    LogInfo logInfo = new LogInfo();

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

        outerLabel:
        try {

            if (!workflowUserManager.isCurrentUserAnonymous()) {
                String idTemplate = request.getParameter("id_template") != null ? request.getParameter("id_template") : "";
                String serviceIdFilePath = request.getParameter("service_id_excel_path") != null ? request.getParameter("service_id_excel_path") : "";
                File file;

                if (serviceIdFilePath == null || serviceIdFilePath.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Upload service id file first");
                    break outerLabel;
                } else {
                    file = FileManager.getFileByPath(serviceIdFilePath);
                }

                String newFileName = new Timestamp(System.currentTimeMillis()) + " - " + file.getName();

                //read excel
                JSONArray res = readExcelFileService.readExcelServiceId(file, idTemplate, newFileName);

                //send file to minio
                this.sendFileMinio(file, newFileName);

                response.getWriter().print(res);

            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            logInfo.Error(this.getClass().getName(), e.getMessage(), e);
        }

    }

    public void sendFileMinio(File file, String newFileName) throws Exception {

        GetMasterParamDao paramDao = new GetMasterParamDao();
        ApiConfig apiConfig = new ApiConfig();
        RESTAPI _RESTAPI = new RESTAPI();
        apiConfig = paramDao.getUrl("minIo_worklog");
        String url = apiConfig.getUrl();
        String stringResponse = "";

        logInfo.Log(getClassName(), "fileName :" + file.getName());
        logInfo.Log(getClassName(), "fileName :" + file.getPath());

        //format send menggunakan json
        JSONObject jsonData = new JSONObject();
        jsonData.put("action", "upload");
        jsonData.put("fileName", newFileName);
        jsonData.put("filePath", file.getPath());

//        LogUtil.info(this.getClass().getName(), json);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), jsonData.toString());

        Request request = new Request.Builder()
                .url(url)//url dari web service /json/plugin/org.joget.marketplace.CustomMinioUploadAndDownload/service?";
                .post(body)
                .build();

        stringResponse = _RESTAPI.CALLAPI(request);
    }

    @Override
    public String renderTemplate(FormData formData, Map map) {
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
    }
}
