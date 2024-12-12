/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 *
 * @author suena
 */
/*
Buat ngetest :
https://dev-joget-incident-ticketing-service-joget-dev.apps.mypaas.telkom.co.id/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.SetPriorityStatus/service?urgensi=Super%20Emergency&hard_complaint=Marah&lapul=6&gaul=1&chanel=MyIndihome%20X&ttr_customer=4&customer_category=ALL
 */
public class SetPriorityStatus extends Element implements PluginWebSupport {

    /*deklarasi variable global*/
    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Set Priority Status";
    LogInfo logInfo = new LogInfo();

    @Override
    public String renderTemplate(FormData fd, Map map) {
        return ""; //To change body of generated methods, choose Tools | Templates.
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
        return pluginName;
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
//    @CrossOrigin(origins = "insera.telkom.co.id")
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            JSONObject mainObj = new JSONObject();
            JSONArray jArrObj;
            JSONObject jObj;

            CheckOrigin checkOrigin = new CheckOrigin();
            String origin = request.getHeader("Origin");
            boolean allowOrigin = checkOrigin.checkingOrigin(origin, response);

            if (allowOrigin) {
                String result = "";

                WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
                if (!workflowUserManager.isCurrentUserAnonymous()) {

                    String urgensi = request.getParameter("urgensi");
                    String hardComplaint = request.getParameter("hard_complaint");
                    String strLapul = (("undefined".equalsIgnoreCase(request.getParameter("lapul")) || "".equalsIgnoreCase(request.getParameter("lapul"))) ? "0" : request.getParameter("lapul"));
                    String strGaul = (("undefined".equalsIgnoreCase(request.getParameter("gaul")) || "".equalsIgnoreCase(request.getParameter("gaul"))) ? "0" : request.getParameter("gaul"));
                    String channel = request.getParameter("channel");
                    String strTtrCustomer = (("undefined".equalsIgnoreCase(request.getParameter("ttr_customer")) || "".equalsIgnoreCase(request.getParameter("ttr_customer"))) ? "0" : request.getParameter("ttr_customer"));
                    String classificationFlag = request.getParameter("classification_flag");

                    String customerCategory = request.getParameter("customer_category");
                    String customerType = request.getParameter("customer_type");
                    String packageType = request.getParameter("package");
                    String splitTtr[] = strTtrCustomer.split(":");

                    byte[] dbCustomerCategory = Base64.getDecoder().decode(customerCategory);
                    String dCustomerCategory = new String(dbCustomerCategory);

                    byte[] dbCustomerType = Base64.getDecoder().decode(customerType);
                    String dCustomerType = new String(dbCustomerType);

                    byte[] dbPackageType = Base64.getDecoder().decode(packageType);
                    String dPackageType = new String(dbPackageType);

                    String customerSement = request.getParameter("cst_segment");
                    String slg_ncx = request.getParameter("slg_ncx");
                    String tk_custranking = request.getParameter("tk_custranking");
                    String sid_desc = request.getParameter("sid_desc");

                    List<String> packageList = null;

                    if ("DCS".equalsIgnoreCase(customerSement) || "PL-TSEL".equalsIgnoreCase(customerSement)) {
                        int lapul = Integer.valueOf(strLapul);
                        int gaul = Integer.valueOf(strGaul);
                        double ttrCustomer = Double.valueOf(splitTtr[0]);
                        double dayOfTtrCustomer = ttrCustomer / 24;

                        String packages[] = {
                            "INET50M",
                            "INETFN50M",
                            "INETF30M350",
                            "INETC50M",
                            "INETLOY50",
                            "INETCS50M",
                            "INETCSOD50M",
                            "INET10Q050",
                            "INETMBQ50",
                            "INET L50H",
                            "INETSS50M",
                            "INETP50M",
                            "INETG50M",
                            "INETNLOY50",
                            "INETF50M",
                            "INET10Q50",
                            "INETSSQ50",
                            "INETF30M750",
                            "INETSS100M",
                            "INETNLOY100",
                            "INETCS100M",
                            "INETMBQ100",
                            "INETP100M",
                            "INET1000S",
                            "INETLOY100",
                            "INET100M",
                            "INET10Q100",
                            "INETSSQ100",
                            "INETCSOD100M",
                            "INET1000H",
                            "INETF100M",
                            "INETFN100M",
                            "INETC100M",
                            "INETG100M",
                            "INET2000H",
                            "INETLOY200",
                            "INETCS200M",
                            "INETNLOY200",
                            "INETFN200M",
                            "INETF30M200",
                            "INETF200M",
                            "INETC200M"
                        };

                        packageList = new ArrayList<>(Arrays.asList(packages));
                        boolean packageMoreThan50 = packageList.contains(dPackageType);

                        //SUPER_EMERGENCY
                        if (("1".equalsIgnoreCase(urgensi) && "3".equalsIgnoreCase(hardComplaint) && lapul > 5 && gaul >= 0) ||
                                ("1".equalsIgnoreCase(urgensi) && lapul > 5 && gaul >= 0) ||
                                ("1".equalsIgnoreCase(urgensi) && lapul >= 0 && gaul >= 2) ||
                                (lapul >= 0 && gaul >= 0 && packageMoreThan50) ||
                                (lapul >= 0 && gaul >= 0 && ("40".equals(channel) || "6".equals(channel))) ||
                                (gaul >= 0) && ("HVC_PLATINUM".equalsIgnoreCase(customerType) || "HVC_VVIP".equalsIgnoreCase(customerType))) {
                            result = "Super Emergency";
                        } //SUPER_EMERGENCY NON TECHNICAL
                        else if ((lapul >= 0 && gaul >= 0 && "NON TECHNICAL".equalsIgnoreCase(classificationFlag)) ||
                                (lapul >= 0 && gaul >= 0 && "NON TECHNICAL".equalsIgnoreCase(classificationFlag)) ||
                                (lapul >= 0 && gaul >= 0 && ("40".equals(channel) || "6".equals(channel)) && "NON TECHNICAL".equalsIgnoreCase(classificationFlag))) {
                            result = "Super Emergency";
                        } // VERY_HIGH
                        else if (("5".equalsIgnoreCase(urgensi) && "3".equalsIgnoreCase(hardComplaint) && lapul > 3 && gaul >= 0) ||
                                (lapul >= 0 && gaul >= 0 && ("202 - Apartment".equalsIgnoreCase(dCustomerCategory) || "201 - PRIME CLUSTER".equalsIgnoreCase(dCustomerCategory))) ||
                                (lapul >= 0 && gaul >= 0 && dayOfTtrCustomer > 7) ||
                                (gaul >= 0 && "HVC_GOLD".equalsIgnoreCase(dCustomerType))) {
                            result = "Very High";
                        } // HIGH
                        else if (("2".equalsIgnoreCase(hardComplaint) && lapul >= 2 && gaul >= 0 && (!"202 - Apartment".equalsIgnoreCase(dCustomerCategory) && !"201 - PRIME CLUSTER".equalsIgnoreCase(dCustomerCategory)) ||
                                (gaul >= 0 && "HVC_SILVER".equalsIgnoreCase(dCustomerType)))) {
                            result = "High";
                        } //MEDIUM
                        else if ("2".equalsIgnoreCase(hardComplaint) &&
                                lapul >= 0 &&
                                gaul >= 0 &&
                                (!"202 - Apartment".equalsIgnoreCase(dCustomerCategory) && !"201 - PRIME CLUSTER".equalsIgnoreCase(dCustomerCategory))) {
                            result = "Medium";
                        } //      LOW (NORMAL)
                        else if ("1".equalsIgnoreCase(hardComplaint) &&
                                lapul >= 0 &&
                                gaul >= 0) {
                            result = "Low(Normal)";
                        }
                        packages = null;
                    } else if ("DES".equalsIgnoreCase(customerSement) 
                            || "DBS".equalsIgnoreCase(customerSement) 
                            || "DGS".equalsIgnoreCase(customerSement)
                            || "DSS".equalsIgnoreCase(customerSement)
                            || "DPS".equalsIgnoreCase(customerSement)
                            || "RBS".equalsIgnoreCase(customerSement)
                            || "REG".equalsIgnoreCase(customerSement)) {
                        if ("K1".equalsIgnoreCase(sid_desc) || "K2".equalsIgnoreCase(sid_desc)) {
                            result = "Super Emergency";
                        } else if ("99.90".equalsIgnoreCase(slg_ncx) || "99.99".equalsIgnoreCase(slg_ncx)) {
                            result = "Super Emergency";
                        } else if (!"K1".equalsIgnoreCase(sid_desc) && !"K2".equalsIgnoreCase(sid_desc)) {
                            if("99.80".equalsIgnoreCase(slg_ncx)||"99.70".equalsIgnoreCase(slg_ncx)||"99.60".equalsIgnoreCase(slg_ncx)||"99.50".equalsIgnoreCase(slg_ncx)){
                                result = "Very High";
                            }else if ("99.00".equalsIgnoreCase(slg_ncx)||"98.50".equalsIgnoreCase(slg_ncx)){
                                result = "High";
                            }else{
                                result = "Low";
                            }
                        }
                    } else if ("DWS".equalsIgnoreCase(customerSement)) {
                        if("TELKOMSEL".equalsIgnoreCase(tk_custranking)){
                            result = "Super Emergency";
                        }else if("TOP20".equalsIgnoreCase(tk_custranking)){
                            result = "Very High";
                        }
                    }

                    try {
                        jObj = new JSONObject();
                        jObj.put("result", result);
                        mainObj.put("data", jObj);
                        mainObj.put("status", "200");
                        mainObj.put("message", "success");
                        mainObj.put("errors", "");
                        mainObj.write(response.getWriter());
                    } catch (JSONException ex) {
                      logInfo.Log(getClass().getSimpleName(), ex.getMessage());
                    }

//                    packageList.clear();
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication.");
                }

            } else {
                // Throw 403 status OR send default allow
                response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "");
            }

        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
            logInfo.Log(getClass().getSimpleName(), ex.getMessage());
        }

    }
}
