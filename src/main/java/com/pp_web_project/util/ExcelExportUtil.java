package com.pp_web_project.util;

import com.pp_web_project.domain.JoytelProduct;
import com.pp_web_project.domain.SftpData;
import com.pp_web_project.domain.SkProductDetalis;
import com.pp_web_project.domain.TcpResponseData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static com.pp_web_project.util.ProductEnum.getProductNameByCode;

public class ExcelExportUtil {

    ExcelExportUtil util = new ExcelExportUtil(); // ✅ 객체 생성 가능 (원하지 않음)


    public static byte[] exportSalesDataToExcel(List<SftpData> salesData) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        LocalDate now = LocalDate.now();
        Sheet sheet = workbook.createSheet("정산 실적_" + now);

        // ✅ 1. 엑셀 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = {"번호", "전문일련번호", "상품", "가맹점 번호", "거래년월일시", "거래 구분", "상품 금액"};

        CellStyle headerStyle = getHeaderCellStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ✅ 헤더 행 높이 증가
        headerRow.setHeightInPoints(35);

        // ✅ 2. 데이터 추가
        int rowNum = 1;
        int no = 1;
        CellStyle dataStyle = getDataCellStyle(workbook);
        CellStyle numericStyle = getNumericCellStyle(workbook);

        for (SftpData data : salesData) {
            Row row = sheet.createRow(rowNum++);
            boolean isOddRow = rowNum % 2 != 0; // 홀수/짝수 행 구분

            row.createCell(0).setCellValue(no);
            row.createCell(1).setCellValue(data.getSerialNumber());
            row.createCell(2).setCellValue(getProductNameByCode(data.getBarcode()));
            row.createCell(3).setCellValue(data.getStoreNumber());
            row.createCell(4).setCellValue(data.getTransactionDate().toString());

            // ✅ 거래 구분 처리
            String transactionType = switch (data.getTransactionType()) {
                case "01" -> "아웃바운드";
                case "02" -> "인바운드";
                default -> "기타";
            };
            row.createCell(5).setCellValue(transactionType);
            row.createCell(6).setCellValue(data.getTransactionAmount());
            no++;

            // ✅ 스타일 적용 (홀수/짝수 행 색상 적용)
            CellStyle rowStyle = getAlternatingRowStyle(workbook, isOddRow);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.getCell(i);
                if (i == 6) { // 🔥 "상품 금액"은 숫자 스타일 적용
                    cell.setCellStyle(numericStyle);
                } else {
                    cell.setCellStyle(rowStyle);
                }
            }

            row.setHeightInPoints(20);
        }

        // ✅ 3. 열 너비 최적화
        int[] columnWidths = {3000, 8000, 7000, 5000, 8000, 5000, 6000};
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        // ✅ 4. 파일 변환
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    public static byte[] exportRetailSalesDataToExcel(List<TcpResponseData> salesData,
                                                      ProductBoundUtil productBoundUtil,
                                                      ProductNameUtil productNameUtil,
                                                      ProductAmountUtil productAmountUtil) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        LocalDate now = LocalDate.now();
        Sheet sheet = workbook.createSheet("판매 실적_" + now);

        // ✅ 1. 엑셀 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = {"번호", "주문 번호", "구분", "가맹점 번호", "거래 년월일", "거래 일시", "포스", "전문 일련 번호", "상품", "유효 기간", "승인 번호", "LPA", "상품 금액"};

        CellStyle headerStyle = getHeaderCellStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ✅ 헤더 행 높이 증가 (기본값보다 크게 설정)
        headerRow.setHeightInPoints(35); // 🔥 기존 20 → 35로 증가

        // ✅ 2. 데이터 추가
        int rowNum = 1;
        int no = 1;
        CellStyle numericStyle = getNumericCellStyle(workbook);

        for (TcpResponseData data : salesData) {
            Row row = sheet.createRow(rowNum++);
            boolean isOddRow = rowNum % 2 != 0; // 홀수/짝수 행 구분

            row.createCell(0).setCellValue(no);
            row.createCell(1).setCellValue(data.getManageNo().trim());
            row.createCell(2).setCellValue(productBoundUtil.getInboundOrOutbound(data.getPlunm().trim()));
            row.createCell(3).setCellValue(data.getStoreNo());
            row.createCell(4).setCellValue(data.getSaleDate());
            row.createCell(5).setCellValue(data.getSaleTime());
            row.createCell(6).setCellValue(data.getPosNo());
            row.createCell(7).setCellValue(data.getDealDatDom());
            row.createCell(8).setCellValue(productNameUtil.getProductName(data.getPlunm().trim()));
            row.createCell(9).setCellValue(data.getValidTerm().trim());
            row.createCell(10).setCellValue(data.getOkNo().trim());
            row.createCell(11).setCellValue(data.getPinNo1().trim());
            row.createCell(12).setCellValue(productAmountUtil.getProductAmount(data.getPlunm().trim()));
            no++;

            // ✅ 스타일 적용 (홀수/짝수 행 구분)
            CellStyle rowStyle = getAlternatingRowStyle(workbook, isOddRow);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.getCell(i);
                if (i == 12 || i == 10) { // "승인 번호", "상품 금액" 열에 숫자 스타일 적용
                    cell.setCellStyle(numericStyle);
                } else {
                    cell.setCellStyle(rowStyle);
                }
            }

            row.setHeightInPoints(20); // 기본 높이 유지
        }

        // ✅ 열 너비 수동 조정
        int[] columnWidths = {3000, 6000, 4000, 3000, 3000, 3000, 2000, 7500, 5000, 3000, 3000, 15000, 5000};
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]); // 기본 너비 설정
        }

        // ✅ 4. 파일 변환
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }


    public static byte[] exportJoytelInventoryListExcel(List<JoytelProduct> inventory, JoytelProductCodeAndProductNameUtil joytelProductCodeAndProductNameUtil) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        LocalDate now = LocalDate.now();
        Sheet sheet = workbook.createSheet("JOYTEL_상품 현황_" + now);

        // ✅ 1. 엑셀 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = {"번호", "상품명", "일자", "리딤 날짜", "유효 기간", "Lpa", "국가", "쿠폰", "트랜스 아이디"};

        CellStyle headerStyle = getHeaderCellStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ✅ 헤더 행 높이 증가
        headerRow.setHeightInPoints(35);

        // ✅ 2. 데이터 추가
        int rowNum = 1;
        int no = 1;
        CellStyle numericStyle = getNumericCellStyle(workbook);

        for (JoytelProduct data : inventory) {
            Row row = sheet.createRow(rowNum++);
            boolean isOddRow = rowNum % 2 != 0; // 홀수/짝수 행 구분

            row.createCell(0).setCellValue(no);
            row.createCell(1).setCellValue(joytelProductCodeAndProductNameUtil.getJoytelPoructName(data.getProductCode()));
            row.createCell(2).setCellValue(data.getDays());
            row.createCell(3).setCellValue(data.getInputDate());
            row.createCell(4).setCellValue(data.getValidity());
            row.createCell(5).setCellValue(data.getLpa());
            row.createCell(6).setCellValue(data.getCoupon());
            row.createCell(7).setCellValue(data.getTransId());
            no++;

            // ✅ 스타일 적용 (홀수/짝수 행 색상 적용)
            CellStyle rowStyle = getAlternatingRowStyle(workbook, isOddRow);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.getCell(i);
                if (i == 6) { // 🔥 "상품 금액"은 숫자 스타일 적용
                    cell.setCellStyle(numericStyle);
                } else {
                    cell.setCellStyle(rowStyle);
                }
            }

            row.setHeightInPoints(20);
        }

        // ✅ 3. 열 너비 최적화
        int[] columnWidths = {3000, 4000, 4000, 5000, 4000, 15000, 6000, 9000};
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        // ✅ 4. 파일 변환
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    public static byte[] exportJoytelSoldItemListExcel(List<JoytelProduct> inventory, JoytelProductCodeAndProductNameUtil joytelProductCodeAndProductNameUtil) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        LocalDate now = LocalDate.now();
        Sheet sheet = workbook.createSheet("JOYTEL_판매 완료된 상품_" + now);

        // ✅ 1. 엑셀 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = {"번호","주문 번호", "상품명", "일자", "리딤 날짜", "유효 기간", "Lpa", "쿠폰", "트랜스 아이디"};

        CellStyle headerStyle = getHeaderCellStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ✅ 헤더 행 높이 증가
        headerRow.setHeightInPoints(35);

        // ✅ 2. 데이터 추가
        int rowNum = 1;
        int no = 1;
        CellStyle numericStyle = getNumericCellStyle(workbook);

        for (JoytelProduct data : inventory) {
            Row row = sheet.createRow(rowNum++);
            boolean isOddRow = rowNum % 2 != 0; // 홀수/짝수 행 구분

            row.createCell(0).setCellValue(no);
            row.createCell(1).setCellValue(data.getOrderNum());
            row.createCell(2).setCellValue(joytelProductCodeAndProductNameUtil.getJoytelPoructName(data.getProductCode()));
            row.createCell(3).setCellValue(data.getDays());
            row.createCell(4).setCellValue(data.getInputDate());
            row.createCell(5).setCellValue(data.getValidity());
            row.createCell(6).setCellValue(data.getLpa());
            row.createCell(7).setCellValue(data.getCoupon());
            row.createCell(8).setCellValue(data.getTransId());
            no++;

            // ✅ 스타일 적용 (홀수/짝수 행 색상 적용)
            CellStyle rowStyle = getAlternatingRowStyle(workbook, isOddRow);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.getCell(i);
                if (i == 6) { // 🔥 "상품 금액"은 숫자 스타일 적용
                    cell.setCellStyle(numericStyle);
                } else {
                    cell.setCellStyle(rowStyle);
                }
            }

            row.setHeightInPoints(20);
        }

        // ✅ 3. 열 너비 최적화
        int[] columnWidths = {3000, 5000, 4000, 4000, 5000, 4000, 15000, 6000, 9000};
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        // ✅ 4. 파일 변환
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    public static byte[] exportJoytelRefundItemListExcel(List<JoytelProduct> inventory, JoytelProductCodeAndProductNameUtil joytelProductCodeAndProductNameUtil) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        LocalDate now = LocalDate.now();
        Sheet sheet = workbook.createSheet("JOYTEL_환불 상품_" + now);

        // ✅ 1. 엑셀 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = {"번호", "상품명", "일자", "리딤 날짜", "유효 기간", "Lpa", "쿠폰", "트랜스 아이디"};

        CellStyle headerStyle = getHeaderCellStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ✅ 헤더 행 높이 증가
        headerRow.setHeightInPoints(35);

        // ✅ 2. 데이터 추가
        int rowNum = 1;
        int no = 1;
        CellStyle numericStyle = getNumericCellStyle(workbook);

        for (JoytelProduct data : inventory) {
            Row row = sheet.createRow(rowNum++);
            boolean isOddRow = rowNum % 2 != 0; // 홀수/짝수 행 구분

            row.createCell(0).setCellValue(no);
            row.createCell(1).setCellValue(joytelProductCodeAndProductNameUtil.getJoytelPoructName(data.getProductCode()));
            row.createCell(2).setCellValue(data.getDays());
            row.createCell(3).setCellValue(data.getInputDate());
            row.createCell(4).setCellValue(data.getValidity());
            row.createCell(5).setCellValue(data.getLpa());
            row.createCell(6).setCellValue(data.getCoupon());
            row.createCell(7).setCellValue(data.getTransId());
            no++;

            // ✅ 스타일 적용 (홀수/짝수 행 색상 적용)
            CellStyle rowStyle = getAlternatingRowStyle(workbook, isOddRow);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.getCell(i);
                if (i == 6) { // 🔥 "상품 금액"은 숫자 스타일 적용
                    cell.setCellStyle(numericStyle);
                } else {
                    cell.setCellStyle(rowStyle);
                }
            }

            row.setHeightInPoints(20);
        }

        // ✅ 3. 열 너비 최적화
        int[] columnWidths = {3000, 4000, 4000, 5000, 4000, 15000, 6000, 9000};
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        // ✅ 4. 파일 변환
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    public static byte[] exportSoldSkItemsToExcel(List<SkProductDetalis> skItems) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        LocalDate now = LocalDate.now();
        Sheet sheet = workbook.createSheet("sk 판매된 상품_" + now);

        // ✅ 1. 엑셀 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = {"번호", "주문 번호", "서비스 번호", "MGMT 번호", "수량", "코드원", "날짜", "Lpa"};

        CellStyle headerStyle = getHeaderCellStyle(workbook);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ✅ 헤더 행 높이 증가
        headerRow.setHeightInPoints(35);

        // ✅ 2. 데이터 추가
        int rowNum = 1;
        int no = 1;
        CellStyle numericStyle = getNumericCellStyle(workbook);

        for (SkProductDetalis data : skItems) {
            Row row = sheet.createRow(rowNum++);
            boolean isOddRow = rowNum % 2 != 0; // 홀수/짝수 행 구분

            row.createCell(0).setCellValue(no);
            row.createCell(1).setCellValue(data.getOrderNum());
            row.createCell(2).setCellValue(data.getRomingPhoneNum());
            row.createCell(3).setCellValue(data.getRentalMgmtNum());
            row.createCell(4).setCellValue(data.getTotalCnt());
            row.createCell(5).setCellValue(data.getIsCodeOne() ? "O" : "X");
            row.createCell(6).setCellValue(data.getSellDate().toString());
            row.createCell(7).setCellValue(data.getEsimMappingId());

            no++;

            // ✅ 스타일 적용 (홀수/짝수 행 색상 적용)
            CellStyle rowStyle = getAlternatingRowStyle(workbook, isOddRow);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.getCell(i);
                if (i == 6) { // 🔥 "상품 금액"은 숫자 스타일 적용
                    cell.setCellStyle(numericStyle);
                } else {
                    cell.setCellStyle(rowStyle);
                }
            }

            row.setHeightInPoints(20);
        }

        // ✅ 3. 열 너비 최적화
        int[] columnWidths = {3000, 5000, 5000, 5000, 3000, 3000, 5000, 15000};
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        // ✅ 4. 파일 변환
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    // ✅ 헤더 스타일 (굵은 글씨 + 배경색 + 중앙 정렬)
    private static CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12); // 🔥 폰트 크기를 12로 키움
        font.setColor(IndexedColors.WHITE.getIndex()); // 🔥 헤더 글씨색을 흰색으로 변경
        style.setFont(font);

        // ✅ 중앙 정렬 (가운데 배치)
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // ✅ 배경색 변경 (연한 파란색)
        style.setFillForegroundColor(IndexedColors.BLACK1.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // ✅ 테두리 추가 (선명한 구분)
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);

        return style;
    }

    // ✅ 데이터 스타일 (테두리 적용 + 숫자 오른쪽 정렬)
    private static CellStyle getDataCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11); // 🔥 데이터 폰트 크기 11로 설정
        style.setFont(font);

        // ✅ 테두리 추가 (얇은 선)
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // ✅ 정렬 설정 (일반 텍스트: 왼쪽 정렬 / 숫자: 오른쪽 정렬)
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.LEFT); // 기본 왼쪽 정렬

        return style;
    }

    // ✅ 숫자 데이터 스타일 (오른쪽 정렬)
    private static CellStyle getNumericCellStyle(Workbook workbook) {
        CellStyle style = getDataCellStyle(workbook);
        style.setAlignment(HorizontalAlignment.RIGHT); // 🔥 숫자는 오른쪽 정렬
        return style;
    }

    // ✅ 홀수/짝수 행 배경색 변경 (가독성 향상)
    private static CellStyle getAlternatingRowStyle(Workbook workbook, boolean isOddRow) {
        CellStyle style = getDataCellStyle(workbook);
        if (isOddRow) {
            style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        } else {
            style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        }
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

}
