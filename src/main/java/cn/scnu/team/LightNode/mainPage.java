package cn.scnu.team.LightNode;

import cn.scnu.team.Account.Account;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class mainPage {
    private JTextField MyAddresstextField;
    private JTextField GoalAccountAddressTextField;
    private JTextField MyBalancetextField;
    private JButton addressButton;
    private JButton transferButton;
    private JButton balanceButton;

    public static void main() {
        JFrame frame = new JFrame("mainPage");
        LightNode.mainPage=new mainPage();
        frame.setContentPane(LightNode.mainPage.mainPage);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private JTextField AmountTextField;
    private JLabel goalAccountAddressLabel;
    private JLabel AmountLabel;
    private JLabel MyAddressLabel;
    private JLabel MyBalanceLabel;
    private JPanel mainPage;
    private JLabel MyAccountDetails;
    private JTextArea MyAccountDetailsTextArea;
    private JButton detailsButton;
    private JTextField MyAccountDetailsTextField;

    public void setBalance(double balance){
        MyBalancetextField.setText(String.valueOf(balance));
    };

    public mainPage() {

        addressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //得到我的公钥
                MyAddresstextField.setText(LightNode.getAddress());
            }
        });

        balanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //得到我的余额
                MyBalancetextField.setText(LightNode.getBalance());
            }
        });

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //传参 需要转入的账号和金额
                LightNode.Transfer(GoalAccountAddressTextField.getText(), Double.valueOf(AmountTextField.getText()));
            }
        });

        detailsButton.addActionListener(new ActionListener() {
            //得到我的账号详情
            @Override
            public void actionPerformed(ActionEvent e) {
                MyAccountDetailsTextField.setText(LightNode.getDetails());
            }
        });
    }
}
