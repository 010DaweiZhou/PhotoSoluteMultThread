import java.io.File;
import java.util.List;

public class Solution {

    private ExcelUtil excelUtil;
    private DirUtil dirUtil;
    private boolean numberNotEqual = false;
    private boolean fileNameExistLength0 = false;
    private boolean renameFailed = false;

    Solution(ExcelUtil excelUtil, DirUtil dirUtil) {
        this.excelUtil = excelUtil;
        this.dirUtil = dirUtil;
    }

    public void changeAllDSCFileName() {
        List<String> columnDateList = excelUtil.getColumnDateList();
        if (columnDateList == null) {
            return;
        }

        List<String> fileNames = dirUtil.getFileNames();
        if (fileNames == null) {
            return;
        }

        // two lists's length is no equal
        if (columnDateList.size() != fileNames.size()) {
            this.numberNotEqual = true;
            return;
        }

        for (String s : columnDateList) {
            if (s.length() == 0) {
                this.fileNameExistLength0 = true;
                return;
            }
        }

        for (int i = 0; i < fileNames.size(); i++) {
            String newName = columnDateList.get(i);
            String absolutePath = fileNames.get(i);
            File file = new File(absolutePath);
            int indexSuffix = absolutePath.lastIndexOf(".");
            String suffix = absolutePath.substring(indexSuffix, absolutePath.length());     //拿到后缀
            int index = absolutePath.lastIndexOf(File.separator);
            String s = absolutePath.substring(0, index).trim();                             //拿到前缀
            absolutePath = s + File.separator + newName + suffix;                           //组新的绝对路径
            boolean b = file.renameTo(new File(absolutePath));                              //重命名
            if (!b)
                this.renameFailed = true;
        }
    }

    public ExcelUtil getExcelUtil() {
        return excelUtil;
    }

    public void setExcelUtil(ExcelUtil excelUtil) {
        this.excelUtil = excelUtil;
    }

    public DirUtil getDirUtil() {
        return dirUtil;
    }

    public void setDirUtil(DirUtil dirUtil) {
        this.dirUtil = dirUtil;
    }

    public boolean isNumberNotEqual() {
        return numberNotEqual;
    }

    public void setNumberNotEqual(boolean numberNotEqual) {
        this.numberNotEqual = numberNotEqual;
    }

    public boolean isFileNameExistLength0() {
        return fileNameExistLength0;
    }

    public void setFileNameExistLength0(boolean fileNameExistLength0) {
        this.fileNameExistLength0 = fileNameExistLength0;
    }

    public boolean isRenameFailed() {
        return renameFailed;
    }

    public void setRenameFailed(boolean renameFailed) {
        this.renameFailed = renameFailed;
    }
}
