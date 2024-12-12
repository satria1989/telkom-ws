package id.co.itasoft.telkom.oss.plugin.dao;

import id.co.itasoft.telkom.oss.plugin.function.CallRestAPI;
import id.co.itasoft.telkom.oss.plugin.model.ApiConfig;
import id.co.itasoft.telkom.oss.plugin.model.Symptom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;
import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.EnvironmentVariable;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author tarkiman
 */
public class ClassificationTreeViewDao {

    private String host;
    private String apiId;
    private String apiKey;
    private String apiSecret;
    private String apiToken;

    CallRestAPI callApi;

    public ClassificationTreeViewDao() {
        host = getEnvVariableValue("host_asset_area_service");
        apiId = getEnvVariableValue("api_id_asset_area_service");
        apiKey = getEnvVariableValue("api_key_asset_area_service");
        apiSecret = getEnvVariableValue("api_secret_asset_area_service");
        apiToken = generateToken();
        callApi = new CallRestAPI();
    }

    public List<Symptom> getNodes(HashMap<String, String> params) {
        List<Symptom> list = new ArrayList<>();
        ApiConfig apiConfig;
        try {
            apiConfig = new ApiConfig();
            apiConfig.setUrl(host + "/jw/api/list/list_symptomClass");
            apiConfig.setApiId(apiId);
            apiConfig.setApiKey(apiKey);
            apiConfig.setApiToken(generateToken());

            String response = "";
            response = callApi.sendGet(apiConfig, params);

            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(response);
            JSONArray arrData = (JSONArray) data_obj.get("data");

            Symptom s;

            for (Object object : arrData) {
                JSONObject obj = (JSONObject) object;
                s = new Symptom();
                s.setAutoBackend(obj.get("auto_backend").toString());
                s.setClassificationCode(obj.get("classification_code").toString());
                s.setClassificationType(obj.get("classification_type").toString());
                s.setDescription(obj.get("description").toString());
                s.setFinalCheck(obj.get("final_check").toString());
                s.setParent(obj.get("parent").toString());
                s.setSolution(obj.get("solution").toString());
                s.setHasChildren(this.isHasChildren(obj.get("classification_code").toString()));
                list.add(s);
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        } finally {
            apiConfig = null;
            list.clear();
        }

        return list;
    }

    public boolean isHasChildren(String classificationCode) {

        boolean status = false;
        ApiConfig apiConfig;
        JSONParser parse;
        JSONObject data_obj;
        try {

            apiConfig = new ApiConfig();
            apiConfig.setUrl(host + "/jw/api/list/list_symptomClass");
            apiConfig.setApiId(apiId);
            apiConfig.setApiKey(apiKey);
            apiConfig.setApiToken(apiToken);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("parent", classificationCode);

            String response = "";
            response = callApi.sendGet(apiConfig, params);

            parse = new JSONParser();
            data_obj = (JSONObject) parse.parse(response);
            int total = Integer.valueOf(data_obj.get("total").toString());

            if (total > 0) {
                status = true;
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        } finally {
            apiConfig = null;
            parse = null;
            data_obj = null;
        }

        return status;
    }

    public String getEnvVariableValue(String envVariableKey) {

        AppDefinition appDef = (AppDefinition) AppUtil.getCurrentAppDefinition();
        EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
        EnvironmentVariable envVariable = environmentVariableDao.loadById(envVariableKey, appDef);

        return envVariable.getValue();
    }

    public String generateToken() {

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH").format(new Date());
        /*
        TOKEN PATTERN : SHA-256({api_key}::{api_secret}::{yyyy-MM-dd HH:mm})
         */
        String plainToken = apiKey + "::" + apiSecret + "::" + currentDateTime;

        String token = DigestUtils.sha256Hex(plainToken);

        return token;
    }

}
