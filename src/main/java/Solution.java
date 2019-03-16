import java.io.File;
import java.util.List;

public class Solution {

    private ExcelUtil excelUtil;
    private DirUtil dirUtil;

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

        if (columnDateList.size() != fileNames.size()) {
            dirUtil.setHasError(true);
            return;
        }

        for (int i = 0; i < fileNames.size(); i++) {
            String newName = columnDateList.get(i);
            if (newName.length() == 0) {
                dirUtil.setStudentInfoLess(true);
                continue;
            }
            String absolutePath = fileNames.get(i);
            File file = new File(absolutePath);
            int indexSuffix = absolutePath.lastIndexOf(".");
            String suffix = absolutePath.substring(indexSuffix, absolutePath.length());     //拿到后缀
            int index = absolutePath.lastIndexOf(File.separator);
            String s = absolutePath.substring(0, index).trim();                             //拿到前缀
            absolutePath = s + File.separator + newName + suffix;                           //组新的绝对路径
            boolean b = file.renameTo(new File(absolutePath));                              //重命名
            if (!b)
                dirUtil.setHasError(true);
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
}
