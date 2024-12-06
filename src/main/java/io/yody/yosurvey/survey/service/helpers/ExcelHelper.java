package io.yody.yosurvey.survey.service.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelHelper {
    private static final Logger log = LoggerFactory.getLogger(ExcelHelper.class);
    public static <T> T convertParamToPojo(String queryString, Class<T> toValueType, ObjectMapper mapper, List<String> includedCriteria) {
        try {
            // Parse the query string into a Map
            Map<String, String> paramMap = UriComponentsBuilder.fromUriString("?" + queryString).build().getQueryParams().toSingleValueMap();

            // Create a new instance of the target class
            T criteriaObject = toValueType.getDeclaredConstructor().newInstance();

            // Manually map each included criteria to the POJO
            for (String criteriaKey : includedCriteria) {
                if (paramMap.containsKey(criteriaKey)) {
                    String value = paramMap.get(criteriaKey);
                    // Set the corresponding value in the POJO
                    mapper.readerForUpdating(criteriaObject).readValue("{\"" + criteriaKey + "\":\"" + value + "\"}");
                }
            }

            return criteriaObject;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert parameters to POJO", e);
        }
    }


    public static List<Field> getAllFields(List<Field> fields, Class<?> clazz) {
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields = getAllFields(fields, clazz.getSuperclass());
        }
        return fields;
    }

    public static <T> void writeObject(
        T obj,
        Map<String, Integer> columnFieldMaps,
        int rowNumber,
        Sheet ws,
        Map<String, CellStyle> styles
    ) {
        Class<?> aClass = obj.getClass();
        List<Field> fields = new ArrayList<Field>();

        fields = getAllFields(fields, aClass);

        Set<String> keys = columnFieldMaps.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();

            Field field = null;

            for (int j = 0; j < fields.size(); j++) {
                Field element = fields.get(j);
                String fieldName = element.getName();
                if (fieldName.equals(key)) {
                    field = element;
                }
            }

            if (field == null) {
                continue;
            }

            field.setAccessible(true);

            try {
                Object fieldValue = field.get(obj);

                writeValueToCell(columnFieldMaps.get(key), rowNumber, ws, fieldValue, styles);
            } catch (Exception e) {
                log.warn("Exception: ", e);
            }
        }
    }

    public static boolean isInteger(BigDecimal value) {
        int intValue = value.intValue();
        BigDecimal convertedValue = new BigDecimal(intValue);
        return value.compareTo(convertedValue) == 0;
    }
    public static void writeValueToCell(int colNumber, int rowNumber, Sheet ws, Object fieldValue, Map<String, CellStyle> styles) {
        if (fieldValue != null && fieldValue.getClass().equals(Integer.class)) {
            writeIntegerValueCell((int) fieldValue, rowNumber, colNumber, ws, styles.get("integer"));
        } else if (fieldValue != null && fieldValue.getClass().equals(Long.class)) {
            writeLongValueCell((Long) fieldValue, rowNumber, colNumber, ws, styles.get("text"));
        } else if (fieldValue != null && fieldValue.getClass().equals(BigInteger.class)) {
            writeBigIntegerValueCell((BigInteger) fieldValue, rowNumber, colNumber, ws, styles.get("integer"));
        } else if (fieldValue != null && fieldValue.getClass().equals(String.class)) {
            writeStringValueCell((String) fieldValue, rowNumber, colNumber, ws, styles.get("text"));
        } else if (fieldValue != null && fieldValue.getClass().equals(BigDecimal.class)) {
            BigDecimal value = (BigDecimal) fieldValue;
            if (value.compareTo(BigDecimal.ZERO) == 0) {
                writeNumberValueCell((BigDecimal) fieldValue, rowNumber, colNumber, ws, styles.get("decimal"));
                return;
            }

            if (isInteger(value)) {
                writeNumberValueCell((BigDecimal) fieldValue, rowNumber, colNumber, ws, styles.get("number"));
            } else {
                writeNumberValueCell((BigDecimal) fieldValue, rowNumber, colNumber, ws, styles.get("decimal"));
            }
        } else if (fieldValue != null && fieldValue.getClass().equals(Boolean.class)) {
            writeBooleanValueCell((boolean) fieldValue, rowNumber, colNumber, ws, styles.get("normal"));
        } else if (fieldValue != null && (fieldValue.getClass().equals(Timestamp.class) || fieldValue.getClass().equals(Date.class))) {
            writeDateValueCell((Date) fieldValue, rowNumber, colNumber, ws, styles.get("text"));
        } else {
            writeStringValueCell((String) fieldValue, rowNumber, colNumber, ws, styles.get("text"));
        }
        if (fieldValue == null) {
            writeStringValueCell("", rowNumber, colNumber, ws, styles.get("text"));
        }
    }

    public static void writeTemplateSheet(Map<String, String> columnMaps, Sheet ws) {
        Set<String> keys = columnMaps.keySet();
        keys.forEach(key -> {
            CellReference cellReference = new CellReference(key);
            CellAddress cellAddress = new CellAddress(cellReference);
            writeStringValueCell(columnMaps.get(key), cellAddress.getRow(), cellAddress.getColumn(), ws);
        });
    }

    public static void writeStringValueCell(String value, int rowNum, int colNum, Sheet ws) {
        Row row = ws.getRow(rowNum);
        if (row == null) {
            row = ws.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }

        cell.setCellType(CellType.STRING);
        cell.setCellValue(value == null ? "" : value);
    }

    public static void writeStringValueCell(String value, int rowNum, int colNum, Sheet ws, CellStyle style) {
        ws.setColumnWidth(colNum, 2000);
        Row row = ws.getRow(rowNum);
        if (row == null) {
            row = ws.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }

        cell.setCellType(CellType.STRING);
        cell.setCellStyle(style);
        cell.setCellValue(value == null ? "" : value);
    }

    public static HashMap<String, CellStyle> getCellStylePatterns(Workbook wb) {
        boolean isNoneBorder = true;
        HashMap<String, CellStyle> result = new HashMap<>();
        result.put("text", getTextCellStyle(wb, isNoneBorder));
        result.put("integer", getIntCellStyle(wb, isNoneBorder));
        result.put("number", getNumberCellStyle(wb, isNoneBorder));
        result.put("decimal", getDecimalCellStyle(wb, isNoneBorder));
        result.put("normal", getNormalCellStyle(wb));
        return result;
    }

    public static void setNoneBorderStyle(CellStyle style) {
        style.setBorderTop(BorderStyle.NONE);
        style.setBorderRight(BorderStyle.NONE);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.NONE);
    }

    public static void setThinBorderStyle(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
    }

    public static CellStyle getNormalCellStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        setThinBorderStyle(style);
        return style;
    }

    public static CellStyle getTextCellStyle(Workbook wb, boolean isNoneBorder) {
        CellStyle style = wb.createCellStyle();
        if (isNoneBorder) {
            setNoneBorderStyle(style);
        } else {
            setThinBorderStyle(style);
        }
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    public static CellStyle getIntCellStyle(Workbook wb, boolean isNoneBorder) {
        CellStyle style = wb.createCellStyle();
        if (isNoneBorder) {
            setNoneBorderStyle(style);
        } else {
            setThinBorderStyle(style);
        }
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    public static CellStyle getDecimalCellStyle(Workbook wb, boolean isNoneBorder) {
        CellStyle style = wb.createCellStyle();
        if (isNoneBorder) {
            setNoneBorderStyle(style);
        } else {
            setThinBorderStyle(style);
        }
        style.setAlignment(HorizontalAlignment.RIGHT);
        DataFormat moneyFormat = wb.createDataFormat();
        style.setDataFormat(moneyFormat.getFormat("#,##0.000"));
        return style;
    }

    public static CellStyle getNumberCellStyle(Workbook wb, boolean isNoneBorder) {
        CellStyle style = wb.createCellStyle();
        if (isNoneBorder) {
            setNoneBorderStyle(style);
        } else {
            setThinBorderStyle(style);
        }
        style.setAlignment(HorizontalAlignment.RIGHT);
        DataFormat moneyFormat = wb.createDataFormat();
        style.setDataFormat(moneyFormat.getFormat("#,###"));
        return style;
    }

    public static void formatTemplate(HashMap<String, String> columnMaps, Sheet ws) {
        Workbook wb = ws.getWorkbook();
        CellStyle boldCellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        boldCellStyle.setFont(font);
        boldCellStyle.setBorderTop(BorderStyle.THIN);
        boldCellStyle.setBorderRight(BorderStyle.THIN);
        boldCellStyle.setBorderBottom(BorderStyle.THIN);
        boldCellStyle.setBorderLeft(BorderStyle.THIN);

        Set<String> keys = columnMaps.keySet();
        keys.forEach(key -> {
            setCellStyle(ws, boldCellStyle, key);
        });

        CellStyle headCellStyle = wb.createCellStyle();
        Font headFont = wb.createFont();
        headFont.setBold(true);
        headFont.setFontHeightInPoints((short) 20);
        headCellStyle.setFont(headFont);
        setCellStyle(ws, headCellStyle, "A1");
    }

    private static void setCellStyle(Sheet ws, CellStyle boldCellStyle, String reference) {
        CellReference cellReference = new CellReference(reference);
        CellAddress cellAddress = new CellAddress(cellReference);
        Row row = ws.getRow(cellAddress.getRow());
        if (row == null) {
            row = ws.createRow(cellAddress.getRow());
        }
        Cell cell = row.getCell(cellAddress.getColumn());
        if (cell == null) {
            cell = row.createCell(cellAddress.getColumn());
        }

        cell.setCellStyle(boldCellStyle);
    }

    public static void writeNumberValueCell(BigDecimal value, int rowNum, int colNum, Sheet ws, CellStyle style) {
        try {
            Row row = ws.getRow(rowNum);
            if (row == null) {
                row = ws.createRow(rowNum);
            }
            Cell cell = row.getCell(colNum);
            if (cell == null) {
                cell = row.createCell(colNum);
            }

            cell.setCellType(CellType.NUMERIC);
            cell.setCellStyle(style);
            cell.setCellValue(value == null ? 0 : value.doubleValue());
            int i = 0;
            i++;
        } catch (Exception e) {
            return;
        }
    }

    public static void writeIntegerValueCell(Integer value, int rowNum, int colNum, Sheet ws, CellStyle style) {
        Row row = ws.getRow(rowNum);
        if (row == null) {
            row = ws.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    public static void writeLongValueCell(Long value, int rowNum, int colNum, Sheet ws, CellStyle style) {
        Row row = ws.getRow(rowNum);
        if (row == null) {
            row = ws.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    public static void writeBigIntegerValueCell(BigInteger value, int rowNum, int colNum, Sheet ws, CellStyle style) {
        Row row = ws.getRow(rowNum);
        if (row == null) {
            row = ws.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }
        cell.setCellType(CellType.NUMERIC);
        cell.setCellStyle(style);
        cell.setCellValue(value.intValue());
    }

    public static void writeBooleanValueCell(boolean value, int rowNum, int colNum, Sheet ws, CellStyle style) {
        Row row = ws.getRow(rowNum);
        if (row == null) {
            row = ws.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }
        cell.setCellType(CellType.BOOLEAN);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    public static void writeDateValueCell(Date date, int rowNum, int colNum, Sheet ws, CellStyle style) {
        Row row = ws.getRow(rowNum);
        if (row == null) {
            row = ws.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        cell.setCellType(CellType.STRING);
        cell.setCellStyle(style);
        String value = dateFormat.format(date);
        cell.setCellValue(value == null ? "" : value);
    }
}
