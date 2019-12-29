package cn.scnu.team.LightNode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class HomePage {
    private JButton enterButton;
    private JTextField fileNameTextFieldTextField;
    private JLabel FileNameLabel;
    private JPanel HomePage;

    public static void main(String[] args) {
        JFrame frame = new JFrame("HomePage");
        frame.setContentPane(new HomePage().HomePage);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public HomePage() {
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //FileName作为参数
                try {
                    LightNode.main(fileNameTextFieldTextField.getText());
                } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
                mainPage.main();
            }
        });
    }
}
