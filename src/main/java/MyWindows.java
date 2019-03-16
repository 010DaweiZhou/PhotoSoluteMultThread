import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.CountDownLatch;

public class MyWindows implements ActionListener {

    private JFrame frame = new JFrame("照片命名工具");// 框架布局  
    private Container con = new Container();
    private JLabel label1 = new JLabel("选择照片");
    private JLabel label2 = new JLabel("选择文档");
    private JLabel label3 = new JLabel("选择列号");
    private JTextField text1 = new JTextField();// TextField 目录的路径  
    private JTextField text2 = new JTextField();// 文件的路径  
    private JButton button1 = new JButton("...");// 选择  
    private JButton button2 = new JButton("...");// 选择  
    private JFileChooser jfc = new JFileChooser();// 文件选择器  
    private JButton button3 = new JButton("确定");
    private String[] item = {"A", "A+B", "A+B+C", "A+B+C+D"};
    private JComboBox<String> jComboBox = new JComboBox<String>(item);

    public void createJFrame() {
        jfc.setCurrentDirectory(new File("d://"));// 文件选择器的初始目录定为d盘
        double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        frame.setLocation(new Point((int) (lx / 2) - 150, (int) (ly / 2) - 150));// 设定窗口出现位置
        frame.setSize(380, 200);// 设定窗口大小
        frame.setResizable(false);
        Image image = new ImageIcon(getClass().getResource("icon.png")).getImage();
        frame.setIconImage(image);
        jComboBox.setSelectedIndex(0);
        label1.setBounds(10, 30, 70, 20);
        text1.setBounds(75, 30, 200, 20);
        button1.setBounds(290, 30, 50, 20);
        label2.setBounds(10, 60, 70, 20);
        label3.setBounds(10, 90, 70, 20);
        text2.setBounds(75, 60, 200, 20);
        button2.setBounds(290, 60, 50, 20);
        jComboBox.setBounds(75, 90, 90, 20);
        button3.setBounds(150, 120, 60, 20);
        button1.addActionListener(this); // 添加事件处理
        button2.addActionListener(this); // 添加事件处理
        button3.addActionListener(this); // 添加事件处理
        con.add(label1);
        con.add(jComboBox);
        con.add(text1);
        con.add(button1);
        con.add(label2);
        con.add(label3);
        con.add(text2);
        con.add(button2);
        con.add(button3);
        frame.setContentPane(con);
        frame.setVisible(true);// 窗口可见
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 使能关闭窗口，结束程序
    }

    public void actionPerformed(ActionEvent e) {
        int state = 0;
        if (e.getSource().equals(button1)) {// 判断触发方法的按钮是哪个
            jfc.setFileSelectionMode(1);// 设定只能选择到文件夹
            state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
            if (state == 1) {
                return;    // 撤销则返回
            } else {
                File f = jfc.getSelectedFile();// f为选择到的目录
                text1.setText(f.getAbsolutePath());
            }
        }

        if (e.getSource().equals(button2)) {
            jfc.setFileSelectionMode(0);// 设定只能选择到文件
            state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
            if (state == 1) {
                return;   // 撤销则返回
            } else {
                File f = jfc.getSelectedFile();// f为选择到的文件
                text2.setText(f.getAbsolutePath());
            }
        }

        if (e.getSource().equals(button3)) {
            commit();
        }
    }

    private void commit() {
        if (text1.getText() == null || text2.getText() == null || jComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(con, "选项不能为空", "提示", JOptionPane.WARNING_MESSAGE);
        } else {
            CountDownLatch countDownLatch = new CountDownLatch(2);
            DirUtil dirUtil = new DirUtil(countDownLatch);
            dirUtil.setPathChoose(text1.getText());
            ExcelUtil excelUtil = new ExcelUtil(countDownLatch);
            excelUtil.setPath(text2.getText());
            excelUtil.setColumnName(jComboBox.getSelectedItem().toString());


            if (!dirUtil.dirIsEmpty()) {

                Thread fileSolutionThread = new Thread(dirUtil);
                fileSolutionThread.start();

                Thread excelReadThread = new Thread(excelUtil);
                excelReadThread.start();

                try {
                    countDownLatch.await();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                Solution solution = new Solution(excelUtil, dirUtil);
                solution.changeAllDSCFileName();

                if( dirUtil.isHasError()) {
                    JOptionPane.showMessageDialog(con, "重命名失败，请检查", "提示", JOptionPane.WARNING_MESSAGE);
                }else{
                    if (!dirUtil.isStudentInfoLess()) {
                        JOptionPane.showMessageDialog(con, "重命名成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(con, "部分学生被跳过，请检查", "提示", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(con, "文件夹为空", "提示", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}


