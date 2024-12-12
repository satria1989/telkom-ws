/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FileUtil;
import org.joget.apps.form.service.FormUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowUserManager;

/**
 *
 * @author mtaup
 */
public class FileUploadApi extends DefaultApplicationPlugin {

    String pluginName = "Telkom New OSS - Ticket Incident Services - File Upload API";
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
        return AppUtil.readPluginResource(this.getClass().getName(), "/properties/UploadFileApi.json");
    }

    @Override
    public Object execute(Map map) {

        try {
            AppDefinition appDefinition = AppUtil.getCurrentAppDefinition();
            WorkflowAssignment wfAssignment = (WorkflowAssignment) map.get("workflowAssignment");
            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            WorkflowUserManager wu = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
            String nik = wu.getCurrentUsername();

            String fieldId = "file";
            String formDefId = "apiFileUpload";
            String primaryKey = appService.getOriginProcessId(wfAssignment.getProcessId());

            FormData formData = new FormData();
            formData.setPrimaryKeyValue(primaryKey);
            Form loadForm = appService.viewDataForm(appDefinition.getId(), appDefinition.getVersion().toString(),
                    formDefId, null, null, null, formData, null, null);
            Element el = FormUtil.findElement(fieldId, loadForm, formData);
            String v = FormUtil.getElementPropertyValue(el, formData);

            File file;
            FileInputStream fis;
            file = FileUtil.getFile(v, loadForm, primaryKey);

        } catch (IOException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        }
        return null;
    }
}
