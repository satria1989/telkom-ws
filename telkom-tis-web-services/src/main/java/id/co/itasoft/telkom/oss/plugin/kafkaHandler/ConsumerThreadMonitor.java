package id.co.itasoft.telkom.oss.plugin.kafkaHandler;

import id.co.itasoft.telkom.oss.plugin.dao.ConfigurationDao;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joget.apps.app.dao.PluginDefaultPropertiesDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PluginDefaultProperties;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.service.FileUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.PluginThread;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.*;

public class ConsumerThreadMonitor implements Runnable {

    private boolean stopThreadMonitor = false;
    private int threadMonitorInterval = 10000; // 10s

    // Store configurations and runnables for each app
    static Collection<String> pluginConfigs = new HashSet<>();
    static Map<String, ConsumerRunnable> runnables = new HashMap<>();

    @Override
    public void run() {
        // LogUtil.info(getClass().getName(), "Waiting for platform init");
        waitForInit();
        // LogUtil.info(getClass().getName(), "Started thread monitor");

        try {
            while (!stopThreadMonitor) {
                monitorRunnables();
                Thread.sleep(threadMonitorInterval);
            }
            cleanup();
        } catch (InterruptedException e) {
            LogUtil.error(getClass().getName(), e, "Error checking threads: " + e.getMessage());
        }
    }

    // Wait for platform to initialize to prevent premature thread creation on startup
    protected void waitForInit() {
        try {
            ApplicationContext appContext = AppUtil.getApplicationContext();
            AppUtil appUtil = (appContext != null) ? (AppUtil) appContext.getBean("appUtil") : null;

            if (appUtil == null) {
                Thread.sleep(500);
                waitForInit();
            }
        } catch (InterruptedException | BeansException e) {
            LogUtil.error(getClass().getName(), e, "Error retrieving app service: " + e.getMessage());
        }
    }

    // Stop the thread monitor
    public void shutdown() {
        // LogUtil.info(getClass().getName(), "Stopping thread monitor");
        stopThreadMonitor = true;
    }

    // Clean up all running threads
    public void cleanup() {
        for (String appId : runnables.keySet()) {
            stopRunnable(appId);
        }
        runnables.clear();
        pluginConfigs.clear();
    }

    // Monitor and start/stop consumer threads based on plugin configurations
    public void monitorRunnables() {
// get published apps
        try {
            ApplicationContext appContext = AppUtil.getApplicationContext();
            AppService appService = (AppService) appContext.getBean("appService");
            Collection<AppDefinition> appDefs = appService.getPublishedApps(null);

            // loop thru apps to retrieve configured plugins
            String pluginName = new KafkaConsumerAuditTrail().getName();
            PluginDefaultPropertiesDao dao = (PluginDefaultPropertiesDao) appContext.getBean("pluginDefaultPropertiesDao");
            Map<String, String> pluginMap = new HashMap<>();

            for (AppDefinition appDef : appDefs) {
                // LogUtil.info(getClass().getName(), "appDef :" + appDef);
                // LogUtil.info(getClass().getName(), "appDef :" + appDef.getAppId());
                Collection<PluginDefaultProperties> pluginDefaultProperties = dao.getPluginDefaultPropertiesList(pluginName, appDef, null, Boolean.TRUE, null, null);

                if (appDef.getAppId().equalsIgnoreCase("ticketIncidentService")) {
                    // get plugin config
//                    PluginDefaultProperties pluginProperties = pluginDefaultProperties.iterator().next();
                    String appId = appDef.getAppId();
                    // LogUtil.info(getClass().getName(), "appId :" + appId);

//                    String json = pluginProperties.getPluginProperties();
                    JSONObject json = new JSONObject();
                    json.put("key", "test");
                    String key = appId;

                    // start threads for new or modified plugins
//                if (!pluginConfigs.contains(key)) {
//                if (appId..equalsIgnoreCase()) {
                    if (runnables.containsKey(appId)) {
                        stopRunnable(appId);
                    }
                    try {
                        ConsumerRunnable newRunnable = startRunnable(appDef);
                        if (newRunnable != null) {
                            runnables.put(appId, newRunnable);
                        }
                    } catch (Exception e) {
                        LogUtil.error(getClass().getName(), e, "Error starting runnable " + e.getMessage());
                    }
//                }
                    pluginMap.put(appId, key);
                }
            }

            for (String appId : runnables.keySet()) {
                if (!pluginMap.containsKey(appId)) {
                    stopRunnable(appId);
                    runnables.remove(appId);
                }
            }

            pluginConfigs.clear();
            pluginConfigs.addAll(pluginMap.values());

        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, ex.getMessage());
        }

    }

    // Start a consumer thread for a given app definition and configuration
    public ConsumerRunnable startRunnable(AppDefinition appDef) {
        ConsumerRunnable runnable = null;

//        Map<String, Object> propertiesMap = AppPluginUtil.getDefaultProperties(new KafkaConsumerAuditTrail(), json, appDef, null);
//        String bootstrapServers = WorkflowUtil.processVariable((String) propertiesMap.get("bootstrapServers"), "", null);
//        String clientId = WorkflowUtil.processVariable((String) propertiesMap.get("clientId"), "", null);
//        String apiKey = WorkflowUtil.processVariable((String) propertiesMap.get("apiKey"), "", null);
//        String topic = WorkflowUtil.processVariable((String) propertiesMap.get("topic"), "", null);
        String topic = "usrINSERA_new_ticket";

        boolean debug = true;

        Properties consumerProperties = getClientConfigForConsumer();

        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(this.getClass().getClassLoader());

            runnable = new ConsumerRunnable(consumerProperties, topic);
            PluginThread thread = new PluginThread(runnable);
            thread.start();
        } finally {
            currentThread.setContextClassLoader(threadContextClassLoader);
        }

        // LogUtil.info(getClass().getName(), "Started Kafka consumer thread for app " + appDef.getAppId());
//        if (debug) {
//            // LogUtil.info(getClass().getName(), "Kafka consumer thread JSON: " + json);
//        }
        return runnable;
    }

    // Stop a specific consumer thread
    public void stopRunnable(String appId) {
        ConsumerRunnable runnable = runnables.get(appId);
        if (runnable != null) {
            runnable.shutdown();
            // LogUtil.info(getClass().getName(), "Stopped Kafka consumer thread for " + appId);
        }
    }

    // Get Kafka client configuration properties
    public Properties getClientConfigForConsumer() {
        Properties props = new Properties();
        try {
            ConfigurationDao getConfigurationTicket = new ConfigurationDao();
            JSONObject configKafka = getConfigurationTicket.getConfigurationMapping();

            // Common properties
            props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, configKafka.getString("kfk_bootstrap"));
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, configKafka.getString("kfk_security"));
            props.put(SaslConfigs.SASL_MECHANISM, configKafka.getString("kfk_mechanism"));

            // Configure SASL with JAAS
            String jaasConfig = configKafka.getString("kfk_jaas").replace("&quot;", "\"");
            props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);

            // SSL Truststore properties
            File trustStoreFile = FileUtil.getFile(configKafka.getString("kfk_tsloc"), "configuration", configKafka.getString("id"));
            props.put("ssl.truststore.location", trustStoreFile.getPath());
            props.put("ssl.truststore.password", configKafka.getString("kfk_tspwd"));

            // Consumer-specific properties
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "usrINSERA_g1"); // Example group ID
//            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");



//            // LogUtil.info(getClass().getName(), "Consumer properties: " + props.toString());
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), ex, "Failed to create Kafka consumer config.");
        }
        return props;
    }
}