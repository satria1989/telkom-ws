/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin.function;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;
import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.EnvironmentVariable;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author suena
 */
public class JwLib {

    public String generateTokenAutomation(String apiKey, String apiSecret) {
        String token = "";
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH").format(new Date());
        /*
        TOKEN PATTERN : SHA-256({api_key}::{api_secret}::{yyyy-MM-dd HH:mm})
         */
        String plainToken = apiKey + "::" + apiSecret + "::" + currentDateTime;
        token = DigestUtils.sha256Hex(plainToken);

        return token;
    }

    public String getEnvVariableValue(String envVariableKey) {

        AppDefinition appDef = (AppDefinition) AppUtil.getCurrentAppDefinition();
        EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
        EnvironmentVariable envVariable = environmentVariableDao.loadById(envVariableKey, appDef);

        return envVariable.getValue();
    }

    public String getCurrentDateTime() {
        String currentDateTime = "";

        //fungsi untuk mendapatkan tanggal, jam menit saat ini
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        currentDateTime = sdf.format(new Date());

        return currentDateTime;
    }

}
