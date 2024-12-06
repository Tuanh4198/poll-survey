package io.yody.yosurvey.survey.service.helpers;

import org.apache.commons.lang3.StringUtils;
import org.nentangso.core.service.errors.NtsValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.monitorjbl.xlsx.StreamingReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ImportFileHelper {
    private static final int IMPORT_START_ROW = 1;
    public static List<String> parseGroupUsersFromFile(String base64) throws IOException {
        List<String> users = new ArrayList<>();
        List<String> messages = new ArrayList<>();

        byte[] bytes = Base64.getDecoder().decode(base64);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes, 0, bytes.length);

        try (Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(1024).open(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
//            int lastRowIndex = sheet.getLastRowNum();
//            if (IMPORT_START_ROW > lastRowIndex) {
//                throw new NtsValidationException("base64", "Import template must be wrong");
//            }
            for (Row row : sheet) {
                if (row.getRowNum() >= IMPORT_START_ROW) {
                    String user = readUserFromTemplateImport(row, messages);
                    if (user != null && StringUtils.isNotBlank(user)) {
                        user = user.toUpperCase();
                        users.add(user);
                    }
                }
            }
        }

        return users;
    }

    public static String readUserFromTemplateImport(Row row, List<String> messages) {
        int rowNum = row.getRowNum() + 1;
        String userInput = StringUtils.EMPTY;
        for (Cell cell : row) {
            String value = cell.getStringCellValue().trim();
            switch (cell.getColumnIndex()) {
                case 0:
                    userInput = value;
                    break;
                default:
                    break;
            }
        }

        boolean isValid = false;
        if (StringUtils.isBlank(userInput)) {
            messages.add("Dòng " + rowNum + ": Mã nhân viên không được để trống");
            isValid = true;
        }

        if (isValid) return null;

        return userInput;
    }
}
