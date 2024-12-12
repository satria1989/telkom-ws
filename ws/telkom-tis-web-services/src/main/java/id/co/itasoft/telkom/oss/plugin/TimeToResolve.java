/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.itasoft.telkom.oss.plugin;

import id.co.itasoft.telkom.oss.plugin.dao.TimeToResolveDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.model.HistoryTicket;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.json.JSONObject;

/**
 * @author mtaup
 */
public class TimeToResolve extends Element implements PluginWebSupport {

    LogInfo info = new LogInfo();
    /*
  Buat ngetest :
  54.179.192.182:8080/jw/web/json/plugin/id.co.itasoft.telkom.oss.plugin.TimeToResolve/service?ticket_id=IN1234567
     */
    public static String pluginName = "Telkom New OSS - Ticket Incident Services - Time To Resolve";

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
    public void webService(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONObject mainObj = new JSONObject();
        JSONObject jObj;

        List<HistoryTicket> historyTicket = null;
        ArrayList<String> ttrCustomer = null;
        ArrayList<String> ttrPending = null;
        ArrayList<String> ttrAgent = null;
        ArrayList<String> ttrMitra = null;
        ArrayList<String> ttrWitel = null;
        ArrayList<String> ttrRegion = null;
        ArrayList<String> ttrNasional = null;
        ArrayList<String> ttrEndToEnd = null;

        try {

            WorkflowUserManager workflowUserManager =
                    (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");

            String ticketId = request.getParameter("ticketId");
            String custSegment = request.getParameter("custSegment");
            String finalTtrCustomer = "";
            TimeToResolveDao dao = new TimeToResolveDao();
//            ArrayList<String> ttrFromDb = dao.getTtr2(ticketId, custSegment);

            historyTicket = dao.getTicketHistory(ticketId);
            ttrCustomer = new ArrayList<>();
            ttrPending = new ArrayList<>();
            ttrAgent = new ArrayList<>();
            ttrMitra = new ArrayList<>();
            ttrWitel = new ArrayList<>();
            ttrRegion = new ArrayList<>();
            ttrNasional = new ArrayList<>();
            ttrEndToEnd = new ArrayList<>();

            for (HistoryTicket ht : historyTicket) {
                if ("DCS".equalsIgnoreCase(custSegment) || "PL-TSEL".equalsIgnoreCase(custSegment) || "".equalsIgnoreCase(custSegment)) {
                    if (!"MEDIACARE".equalsIgnoreCase(ht.getStatus()) &&
                            !"SALAMSIM".equalsIgnoreCase(ht.getStatus()) &&
                            !"PENDING".equalsIgnoreCase(ht.getStatus()) &&
                            !"CLOSED".equalsIgnoreCase(ht.getStatus())) {
                        ttrCustomer.add(ht.getStatusTracking());
                    }
                } else if ("DBS".equalsIgnoreCase(custSegment) ||
                         "DES".equalsIgnoreCase(custSegment) ||
                         "DGS".equalsIgnoreCase(custSegment) ||
                         "not_found".equalsIgnoreCase(custSegment) ||
                         "DPS".equalsIgnoreCase(custSegment) ||
                         "DSS".equalsIgnoreCase(custSegment) ||
                         "RBS".equalsIgnoreCase(custSegment) ||
                         "REG".equalsIgnoreCase(custSegment)) {
                    if (!"MEDIACARE".equalsIgnoreCase(ht.getStatus()) &&
                            !"SALAMSIM".equalsIgnoreCase(ht.getStatus()) &&
                            !"PENDING".equalsIgnoreCase(ht.getStatus()) &&
                            !"CLOSED".equalsIgnoreCase(ht.getStatus()) &&
                            !"REQUEST_PENDING_MEDIACARE".equalsIgnoreCase(ht.getStatus()) &&
                            !"REQUEST_PENDING_SALAMSIM".equalsIgnoreCase(ht.getStatus()) &&
                            !"RESOLVED".equalsIgnoreCase(ht.getStatus())) {
                        ttrCustomer.add(ht.getStatusTracking());
                    }
                }

                if ("PENDING".equalsIgnoreCase(ht.getStatus())) {
                    ttrPending.add(ht.getStatusTracking());
                }

                if ("AGENT".equalsIgnoreCase(ht.getLevel())) {
                    ttrAgent.add(ht.getStatusTracking());
                }

                if ("ANPRUS".equalsIgnoreCase(ht.getLevel())) {
                    ttrMitra.add(ht.getStatusTracking());
                }

                if ("WITEL".equalsIgnoreCase(ht.getLevel())) {
                    ttrWitel.add(ht.getStatusTracking());
                }

                if ("REGIONAL".equalsIgnoreCase(ht.getLevel())) {
                    ttrRegion.add(ht.getStatusTracking());
                }

                if ("NASIONAL".equalsIgnoreCase(ht.getLevel())) {
                    ttrNasional.add(ht.getStatusTracking());
                }

            }

//            if (!workflowUserManager.isCurrentUserAnonymous()) {
//                String ttr = calculateTime(ttrFromDb);
                String sttrCustomer = calculateTime(ttrCustomer);
                Timestamp dateCreate = historyTicket.get(0).getDateCreted();
                String lastStatus = historyTicket.get(0).getStatus();
                if (("MEDIACARE".equalsIgnoreCase(lastStatus) ||
                        "SALAMSIM".equalsIgnoreCase(lastStatus) ||
                        "PENDING".equalsIgnoreCase(lastStatus) ||
                        "CLOSED".equalsIgnoreCase(lastStatus) ||
                        "REQUEST_PENDING_MEDIACARE".equalsIgnoreCase(lastStatus) ||
                        "REQUEST_PENDING_SALAMSIM".equalsIgnoreCase(lastStatus)) ||
                        ((!"DCS".equalsIgnoreCase(custSegment) && !"PL-TSEL".equalsIgnoreCase(custSegment)) && "RESOLVED".equalsIgnoreCase(lastStatus))) {
                    finalTtrCustomer = sttrCustomer;
//                    response.getWriter().print(ttr);
                } else {
                    String duration = dao.getDuration(dateCreate);
                    ArrayList<String> calcTimeList = new ArrayList<String>();
                    calcTimeList.add(sttrCustomer);
                    calcTimeList.add(duration);
                    String calulationTime = calculateTime(calcTimeList);
                    finalTtrCustomer = calulationTime;
//                    response.getWriter().print(calulationTime);
                    calcTimeList.clear();
                }

                ttrNasional.addAll(ttrWitel);
                ttrNasional.addAll(ttrRegion);
                String sTtrNasional = calculateTime(ttrNasional);
                String sTtrPending = calculateTime(ttrPending);
                String sTtrAgent = calculateTime(ttrAgent);
                String sTtrMitra = calculateTime(ttrMitra);
                String sTtrWitel = calculateTime(ttrWitel);
                ttrRegion.add(sTtrWitel);
                String sTtrRegion = calculateTime(ttrRegion);
                ttrEndToEnd.add(finalTtrCustomer);
                ttrEndToEnd.add(sTtrPending);
                String sTtrEndToEnd = calculateTime(ttrEndToEnd);

                if (!"CLOSED".equalsIgnoreCase(lastStatus)) {
                    dao.updateTtr(finalTtrCustomer, sTtrMitra, sTtrNasional, sTtrPending, sTtrWitel, sTtrAgent, sTtrRegion, sTtrEndToEnd, ticketId);
                }

                mainObj.put("ttr_customer", finalTtrCustomer);
                mainObj.put("ttr_pending", sTtrPending);
                mainObj.put("ttr_agent", sTtrAgent);
                mainObj.put("ttr_mitra", sTtrMitra);
                mainObj.put("ttr_witel", sTtrWitel);
                mainObj.put("ttr_region", sTtrRegion);
                mainObj.put("ttr_nasional", sTtrNasional);
                mainObj.put("ttr_end_to_end", sTtrEndToEnd);

                mainObj.write(response.getWriter());

//            } else {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authentication.");
//            }

//            ttrFromDb.clear();
            dao = null;
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid Authentication.");
            info.Log(getClassName(), ex.getMessage());
        } finally {
            historyTicket.clear();
            ttrAgent.clear();
            ttrCustomer.clear();
            ttrEndToEnd.clear();
            ttrMitra.clear();
            ttrNasional.clear();
            ttrPending.clear();
            ttrRegion.clear();
            ttrWitel.clear();
        }
    }

    private String calculateTime(ArrayList<String> data) {
        String result = "";
        try {
            long tm = 0;
            for (String tmp : data) {
                String[] arr = tmp.split(":");
                tm += Integer.parseInt(arr[2]);
                tm += 60 * Integer.parseInt(arr[1]);
                tm += 3600 * Integer.parseInt(arr[0]);
            }

            long hh = tm / 3600;
            tm %= 3600;
            long mm = tm / 60;
            tm %= 60;
            long ss = tm;
            result = format(hh) + ":" + format(mm) + ":" + format(ss);
        } catch (Exception e) {
            info.Log(getClassName(), e.getMessage());
        }
        return result;
    }

    private static String format(long s) {
        if (s < 10) {
            return "0" + s;
        } else {
            return "" + s;
        }
    }
}
