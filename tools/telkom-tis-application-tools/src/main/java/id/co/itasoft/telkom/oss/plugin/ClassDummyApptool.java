/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import java.util.Map;
import org.joget.plugin.base.DefaultApplicationPlugin;

/**
 *
 * @author asani
 */
public class ClassDummyApptool extends DefaultApplicationPlugin{

    
    private String pluginName = "Telkom New OSS - Ticket Incident Services - Class Dummy";
    
    @Override
    public Object execute(Map map) {
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
