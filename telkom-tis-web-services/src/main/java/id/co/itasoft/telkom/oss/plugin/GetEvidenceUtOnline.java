/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.MasterParamDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.function.OkHttpSingleton;
import id.co.itasoft.telkom.oss.plugin.function.RESTAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.MasterParam;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONObject;

/**
 *
 * @author mtaup
 */
public class GetEvidenceUtOnline extends Element implements PluginWebSupport {

    String pluginName = "Telkom New OSS - Ticket Incident Services - Get Evidence UT Online";
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
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

        if (!workflowUserManager.isCurrentUserAnonymous()) {
            try {
                String key = req.getParameter("key");
                String channel = req.getParameter("channel");
                OkHttpSingleton localSingleton = OkHttpSingleton.getInstance();
                String stringResponse = null;
                RESTAPI _RESTAPI = new RESTAPI();
                MasterParam param = new MasterParam();
                MasterParamDao paramDao = new MasterParamDao();
                ApiConfig apiConfig = new ApiConfig();
                String token = _RESTAPI.getToken();

                //=====================Beneran===============================//
                Request request = null;
                if ("58".equals(channel)) {
                    param = paramDao.getUrl("getEvidenceUtOnline");
                    apiConfig.setUrl(param.getUrl() + "?key=" + key);
                    request =
                            new Request.Builder()
                                    .url(apiConfig.getUrl())
                                    .addHeader("Authorization", "Bearer " + token)
                                    .build();

                } else {
                    param = paramDao.getUrl("getEvidenceMIA");

                    JSONObject reqObj = new JSONObject();
                    JSONObject getFileObjectReq = new JSONObject();
                    JSONObject eaiBody = new JSONObject();

                    eaiBody.put("key", key);
                    getFileObjectReq.put("eaiBody", eaiBody);
                    reqObj.put("getFileObjectRequest", getFileObjectReq);

                    RequestBody body = RequestBody.create(_RESTAPI.JSON, reqObj.toString());

                    request =
                            new Request.Builder()
                                    .url(param.getUrl())
                                    .addHeader("Authorization", "Bearer " + token)
                                    .post(body)
                                    .build();
                }
                
                Response response = null;

                try {
                    response = localSingleton.getClient().newCall(request).execute();
                    if (!response.isSuccessful()) {

                        InputStream imageStream = new ByteArrayInputStream(getDefaultImage());
                        res.setContentType("image/png");
                        OutputStream outputStream = res.getOutputStream();
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = imageStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        // Close streams
                        imageStream.close();
                        outputStream.close();

                        throw new IOException("Unexpected code " + response);
                    } else {
                        InputStream imageStream = response.body().byteStream();
                        res.setContentType("image/jpeg");
                        OutputStream outputStream = res.getOutputStream();

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = imageStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        imageStream.close();
                        outputStream.close();
                    }

                } catch (Exception ex) {
                    LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
                } finally {

                    // Get response body
                    response.close();
                }

            } catch (Exception ex) {
                LogUtil.error(this.getClassName(), ex, "error : " + ex.getMessage());
            }
        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication");
        }
    }

    private byte[] getDefaultImage() {

        String defaultImageData = "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";
        return Base64.getDecoder().decode(defaultImageData);
    }

}
