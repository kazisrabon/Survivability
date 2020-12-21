import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JavaApplicationPath {
    public static void main(String[] args) {
//        String s = System.getProperty("user.dir")+ "\\ADM.csv";
//        System.out.println("ADM.csv Directory = " + s);
        JPanel jPanel = new JPanel();
        //jPanel.setBorder(BorderFactory.createEmptyBorder());
        jPanel.setLayout(new GridLayout(1,2));

        JButton jInputButton= new JButton("Input");
        JButton jSaveButton= new JButton("Save");

        jPanel.add(jInputButton);
        jPanel.add(jSaveButton);

        JFrame jFrame = new JFrame("Test");
        jFrame.setSize(800,500);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(jPanel, BorderLayout.CENTER);

        jFrame.setVisible(true);


        jInputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
}
