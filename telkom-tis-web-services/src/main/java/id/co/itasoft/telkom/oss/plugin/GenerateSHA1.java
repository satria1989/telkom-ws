/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.itasoft.telkom.oss.plugin;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONObject;

/**
 *
 * @author itasoft
 */
public class GenerateSHA1 extends Element implements PluginWebSupport {

    final static String pluginName = "Telkom New OSS - Ticket Incident Services - Generate SHA1";
    
    @Override
    public String renderTemplate(FormData fd, Map map) {
        return "";
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "1.0";
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
        
        try {
            JSONObject json = new JSONObject();
            String input = req.getParameter("data");
            
            String sha1Generate = sha1(input);
            json.put("hash", sha1Generate);
            
            
            json.write(res.getWriter());
            
        } catch (Exception ex) {
            LogUtil.error(getClassName(), ex, "ERROR GENERATE SHA1 :" + ex.getMessage());
        }
        
    }
 
    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
         
        return sb.toString();
    }
    
    
}
