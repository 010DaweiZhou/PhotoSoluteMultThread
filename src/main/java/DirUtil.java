import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class DirUtil implements Runnable {
    private String pathChoose = null;
    private File childDir = null;
    private boolean hasError = false;
    private boolean studentInfoLess = false;
    private CountDownLatch countDownLatch;
    private List<String> fileNames = new ArrayList<>();

    DirUtil(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        this.solutionBegin();
        countDownLatch.countDown();
    }

    private void getAllFileName(File dir) {
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().contains("")) {
                    String absolutePath = file.getAbsolutePath();   //绝对路径
                    fileNames.add(absolutePath);

                }
            }
        }
    }

    private void solutionBegin() {
        if (childDir == null) {
            return;
        }

        int result = childIsDirOrFile(childDir);
        switch (result) {
            case -1:                                        //如果无子文件,返回-1
                break;
            case 0:                                         //子文件都是文件，处理，返回0
                getAllFileName(childDir);
                break;
            default:                                         //子文件有文件夹，递归
                File[] files = childDir.listFiles();
                if (files != null) {
                    List<File> files1 = Arrays.asList(files);
                    Collections.sort(files1, new Comparator<File>() {
                        @Override
                        public int compare(File o1, File o2) {
                            if (o1.isDirectory() && o2.isFile()) {
                                return -1;
                            }
                            if (o1.isFile() && o2.isDirectory()) {
                                return 1;
                            }
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    for (File file : files1) {
                        childDir = file;
                        solutionBegin();
                    }
                }
                break;
        }
    }

    //为空返回true
    public boolean dirIsEmpty() {
        File file = new File(pathChoose);
        return file.listFiles() == null;
    }

    private int childIsDirOrFile(File file) {
        File[] files = file.listFiles();
        int flag = 0;
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    flag = 1;
                }
            }
        } else {
            return -1;
        }

        if (flag == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    public String getPathChoose() {
        return pathChoose;
    }

    public void setPathChoose(String pathChoose) {
        this.pathChoose = pathChoose;
        childDir = new File(pathChoose);
    }

    public File getChildDir() {
        return childDir;
    }

    public void setChildDir(File childDir) {
        this.childDir = childDir;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public boolean isStudentInfoLess() {
        return studentInfoLess;
    }

    public void setStudentInfoLess(boolean studentInfoLess) {
        this.studentInfoLess = studentInfoLess;
    }
}
