package com.pp_web_project.utill;

import com.pp_web_project.domain.SftpData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.pp_web_project.utill.ProductEnum.getProductNameByCode;

public class ExcelExportUtil {

    public static byte[] exportSalesDataToExcel(List<SftpData> salesData) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Sales Data");

        // ✅ 1. 엑셀 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = {"번호","전문일련번호","상품", "가맹점 번호", "거래년월일시","거래 구분", "상품 금액"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(getHeaderCellStyle(workbook));
        }

        // ✅ 2. 데이터 추가
        int rowNum = 1;
        for (SftpData data : salesData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(data.getSerialNumber());
            row.createCell(2).setCellValue(getProductNameByCode(data.getBarcode()));
            row.createCell(3).setCellValue(data.getStoreNumber());
            row.createCell(4).setCellValue(data.getTransactionDate().toString());
            // ✅ 삼항 연산자 대신 독립된 변수 사용
            String transactionType  = null;
            if ("01".equals(data.getDataType())) {
                transactionType = "아웃바운드";
            } else if ("02".equals(data.getDataType())) {
                transactionType = "인바운드";
            }
            row.createCell(5).setCellValue(transactionType);
            row.createCell(6).setCellValue(data.getTransactionAmount());
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
