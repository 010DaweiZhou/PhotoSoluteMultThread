
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ExcelUtil implements Runnable {
    private int excelName;
    private Sheet sheet;
    private int row;
    private int column;
    private String path;
    private String columnName;
    private CountDownLatch countDownLatch;
    private List<String> rowDateList = new ArrayList<String>();
    private List<String> columnDateList = new ArrayList<String>();

    ExcelUtil(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;                                               //it's for thread's awaken
    }

    @Override
    public void run() {                                                                     //start the thread to read the excel file then add the result to the columnDateList
        this.readColumnDate();
        countDownLatch.countDown();
    }

    private void readColumnDate() {
        FileInputStream fileInputStream = null;
        HSSFWorkbook workbook = null;
        XSSFWorkbook XLSXSheet = null;
        Iterator<Sheet> sheetIterator = null;

        try {
            fileInputStream = new FileInputStream(path);
            if (path.contains(".xlsx")) {
                XLSXSheet = new XSSFWorkbook(fileInputStream);                              //xlsx file ，for new excel version
                sheetIterator = XLSXSheet.sheetIterator();                                  //get page iterator
            } else {
                workbook = new HSSFWorkbook(fileInputStream);                               //xls file , for the version of 97-2003
                sheetIterator = workbook.sheetIterator();                                   //get page iterator
            }
            while (sheetIterator.hasNext()) {
                sheet = sheetIterator.next();
                for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {     //row iterator
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }

                    if (column != -1) {
                        stringAdd(row, column);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stringAdd(Row row, int columnNumber) {
        Cell[] cell = new Cell[columnNumber];
        String[] strings = new String[columnNumber];
        StringBuilder result = new StringBuilder();
        int nullFlag = 0;

        for (int j = 0; j < columnNumber; j++) {
            cell[j] = row.getCell(j);
            if (cell[j] == null) {
                nullFlag = 1;
                break;
            }
            cell[j].setCellType(CellType.STRING);
            strings[j] = resultCheck(cell[j].getStringCellValue());
            if (strings[j] == null) {
                nullFlag = 1;
                break;
            }
            result.append(strings[j]);
        }
        if (nullFlag != 1) {
            columnDateList.add(result.toString());
        } else {
            columnDateList.add("");
        }
    }

    private int columnNameToInt() {                                                        // change the column name (string) which choose by user to int
        switch (columnName) {
            case "A":
                return 1;
            case "A+B":
                return 2;
            case "A+B+C":
                return 3;
            case "A+B+C+D":
                return 4;
            default:
                return -1;
        }
    }

    private String resultCheck(String result) {

        if (result == null) {
            return null;
        }

        if (result.contains("号") || result.contains("姓名") || result.contains("学生")
                || result.contains("日期") || result.length() == 0) {
            return null;
        }

        if (result.contains("\n")) {
            int index = result.lastIndexOf("\n");
            result = result.substring(0, index).trim();
            return result;
        }

        return result;
    }

    public int getExcelName() {
        return excelName;
    }

    public void setExcelName(int excelName) {
        this.excelName = excelName;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public List<String> getRowDateList() {
        return rowDateList;
    }

    public void setRowDateList(List<String> rowDateList) {
        this.rowDateList = rowDateList;
    }

    public List<String> getColumnDateList() {
        return columnDateList;
    }

    public void setColumnDateList(List<String> columnDateList) {
        this.columnDateList = columnDateList;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
        this.column = columnNameToInt();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
