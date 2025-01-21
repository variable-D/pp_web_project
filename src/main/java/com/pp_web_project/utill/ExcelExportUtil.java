package com.pp_web_project.utill;

import com.pp_web_project.domain.SftpData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExportUtil {

    public static byte[] exportSalesDataToExcel(List<SftpData> salesData) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Sales Data");

        // ✅ 1. 엑셀 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = { "가맹점 번호", "상품", "거래년월일시", "상품 가격"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(getHeaderCellStyle(workbook));
        }

        // ✅ 2. 데이터 추가
        int rowNum = 1;
        for (SftpData data : salesData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getId());
            row.createCell(1).setCellValue(data.getStoreNumber());
            row.createCell(2).setCellValue(data.getBarcode());
            row.createCell(3).setCellValue(data.getTransactionDate().toString());
            row.createCell(4).setCellValue(data.getTransactionAmount());
        }

        // ✅ 3. 파일을 ByteArray로 변환
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    // ✅ 헤더 스타일 설정
    private static CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerCellStyle.setFont(font);
        return headerCellStyle;
    }
}
