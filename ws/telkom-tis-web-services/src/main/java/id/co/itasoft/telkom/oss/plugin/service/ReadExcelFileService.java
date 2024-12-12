package id.co.itasoft.telkom.oss.plugin.service;

import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import id.co.itasoft.telkom.oss.plugin.jwt.JWTUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joget.apps.app.service.AppUtil;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;

public class ReadExcelFileService {

    LogInfo logInfo = new LogInfo();

    public JSONArray readExcelServiceId(File file, String idTemplate, String newFileName) {

        JSONArray result = new JSONArray();
        JWTUtil jwt = new JWTUtil();
        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        String username = workflowUserManager.getCurrentUsername();

        try {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

//            for (Row row : sheet) {
//                if (row.getRowNum() > 0) {
//                    JSONObject data = new JSONObject();
//                    for (Cell currentCell : row) {
//                        String value = formatter.formatCellValue(currentCell);
//                        data.put("service_id", value);
//                        data.put("id_template", idTemplate);
//                        data.put("filename", newFileName);
//                    }
//                    String token = jwt.generateToken(username, data);
//                    result.put(token);
//                }
//            }
            for (Row row : sheet) {
                if (row.getRowNum() > 0) { // Skip header row
                    JSONObject data = new JSONObject();

                    for (Cell currentCell : row) {
                        String value = "";

                        // Ensure we treat the cell value as a string regardless of the type
                        if (currentCell.getCellType() == CellType.NUMERIC) {
                            value = BigDecimal.valueOf(currentCell.getNumericCellValue()).toPlainString();
                        } else {
                            value = currentCell.getStringCellValue();
                        }

                        data.put("service_number", value);
                        data.put("id_template", idTemplate);
                        data.put("filename", newFileName);
                    }

                    String token = jwt.generateToken(username, data);
                    result.put(token);
                }
            }
        } catch (Exception e) {
            logInfo.Error(this.getClass().getName(), e.getMessage(), e);
        }

        return result;

    }

}
