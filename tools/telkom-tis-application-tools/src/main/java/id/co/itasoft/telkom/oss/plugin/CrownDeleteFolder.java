/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import java.util.Map;
import org.joget.plugin.base.DefaultApplicationPlugin;

/**
 * UNUSED
 * @author asani
 */
public class CrownDeleteFolder extends DefaultApplicationPlugin{

    final static String pluginName = "Telkom New OSS - Ticket Incident Services - CROWN DELETE FOLDER UPLOAD";
    
    @Override
    public Object execute(Map map) {
//        String pathWorklog = "/home/jboss/wflow/app_formuploads/ticket_work_logs/";
//        String pathTicket = "/home/jboss/wflow/app_formuploads/ticket/";
//        File directoryWl = new File(pathWorklog);
//        File directoryTicket = new File(pathTicket);
//        FileManager.deleteFile(directoryWl);
//        FileManager.deleteFile(directoryTicket);
//        
//        if(!directoryWl.exists()) {
//            directoryWl.mkdir();
//        }
//        if(!directoryTicket.exists()) {
//            directoryTicket.mkdir();
//        }
        
        return null;
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
        return null;
    }
    
    
}
