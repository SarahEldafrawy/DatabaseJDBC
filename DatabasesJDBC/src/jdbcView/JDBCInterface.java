package jdbcView;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import databaseView.DatabaseConsole;
import jdbc.Drive;

public class JDBCInterface {

  private JFrame frame;
  private JTextField pathField;
  public static Connection con;
  public static JTextPane mainPane;
  public static JTextField textField;
  public static  JLabel lblSec;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          JDBCInterface window = new JDBCInterface();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public JDBCInterface() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */

  private void initialize() {
   frame = new JFrame();
    frame.getContentPane().setBackground(Color.LIGHT_GRAY);
    frame.setBounds(585, 200, 427, 493);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(null);

    JLabel statusLabel = new JLabel("");
    statusLabel.setBackground(Color.ORANGE);
    statusLabel.setForeground(Color.GREEN);
    statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
    statusLabel.setBounds(230, 61, 153, 31);
    frame.getContentPane().add(statusLabel);

    Drive myDrive = new Drive();

    JButton enterQueryBtn = new JButton("Enter Query");
    enterQueryBtn.setBackground(Color.WHITE);
    enterQueryBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        DatabaseConsole c = new DatabaseConsole();
        c.main(null);
      }
    });
    enterQueryBtn.setVisible(false);
    enterQueryBtn.setBounds(10, 380, 388, 31);
    frame.getContentPane().add(enterQueryBtn);

    pathField = new JTextField();
    pathField.setBounds(10, 11, 388, 31);
    frame.getContentPane().add(pathField);
    pathField.setColumns(10);

    JButton btnConnect = new JButton("Connect");
    btnConnect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        String path = pathField.getText();
        path.trim();
        Properties prop = new Properties();
        prop.put("path", path);
        try {
          myDrive.acceptsURL(path);
          con = myDrive.connect(null, prop);
          enterQueryBtn.setVisible(true);
          statusLabel.setForeground(Color.GREEN);
          statusLabel.setText("Connected");
        } catch (Exception e) {
          // TODO Auto-generated catch block
          JOptionPane.showMessageDialog(null,
              "Error while Connection, Connection may not be available");
        }
      }
    });
    btnConnect.setBounds(10, 53, 89, 39);
    frame.getContentPane().add(btnConnect);

    JButton btnDisconnect = new JButton("DisConnect");
    btnDisconnect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        try {
          con.close();
          enterQueryBtn.setVisible(false);
          statusLabel.setForeground(Color.RED);
          statusLabel.setText("DisConnected");
        } catch (Exception e) {
          // TODO Auto-generated catch block
          JOptionPane.showMessageDialog(null,
              "Error while Disconnecting, Connection may not be available");
        }
      }
    });
    btnDisconnect.setBounds(109, 53, 111, 39);
    frame.getContentPane().add(btnDisconnect);

    mainPane = new JTextPane();
    mainPane.setEditable(false);
    mainPane.setFont(new Font("Tahoma", Font.PLAIN, 15));
    mainPane.setBounds(10, 103, 388, 266);
    frame.getContentPane().add(mainPane);
    
    JLabel lblNewLabel = new JLabel("Query Time Out:");
    lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
    lblNewLabel.setBounds(39, 413, 142, 42);
    frame.getContentPane().add(lblNewLabel);
    
    textField = new JTextField();
    textField.setFont(new Font("Tahoma", Font.PLAIN, 14));
    textField.setText("10");
    textField.setBounds(171, 422, 58, 25);
    frame.getContentPane().add(textField);
    textField.setColumns(10);
    
    lblSec = new JLabel("Sec");
    lblSec.setForeground(Color.DARK_GRAY);
    lblSec.setFont(new Font("Tahoma", Font.PLAIN, 12));
    lblSec.setBounds(230, 429, 46, 14);
    frame.getContentPane().add(lblSec);
    mainPane.setVisible(false);

  }
}
