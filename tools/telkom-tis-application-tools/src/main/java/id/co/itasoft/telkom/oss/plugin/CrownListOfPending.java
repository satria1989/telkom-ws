/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.ListOfPendingDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.service.WorkflowManager;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author asani
 */
public class CrownListOfPending extends DefaultApplicationPlugin {

    final static String pluginName = "Telkom New OSS - Crown List Of Pending";
    LogInfo logInfo = new LogInfo();
    ListOfPendingDao dao;
    
    @Override
    public Object execute(Map map) {
        dao = new ListOfPendingDao();
        ApplicationContext ac = AppUtil.getApplicationContext();
        WorkflowManager workflowManager = (WorkflowManager) ac.getBean("workflowManager");
        
        String processId;
        List<String> list = new ArrayList<String>();
        
        try {
            list = dao.getProcessIdNotListPending();
            
            for(String lst : list) {
                workflowManager.reevaluateAssignmentsForActivity(lst); // REEVALUATE LIST OF PENDING ON PROCESS
            }
        } catch (SQLException ex) {
          logInfo.Log(getClass().getName(), ex.getMessage());
        } finally {
            list = null;
            ac = null;
            workflowManager = null;
            dao = null;
        }
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
