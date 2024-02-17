/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package jdbc_type_1;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 *
 * @author Jay
 */
class dbOperations {
    
    Connection con;
    Statement st;
    PreparedStatement pst;
    
    dbOperations() {
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            con = DriverManager.getConnection("jdbc:odbc:mydb");
            System.out.println("First");
            st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch(ClassNotFoundException | SQLException e) {
            System.out.print(e);
        }
    }
    
    ResultSet fetchAllRecords() throws SQLException {
        ResultSet rs = st.executeQuery("SELECT * FROM table1");
        return rs;
    }
    
    ResultSet updateOne(int id, String name, String pass) throws SQLException {
        pst = con.prepareStatement("UPDATE table1 SET name=?, password=? WHERE ID=?");
        pst.setString(1, name);
        pst.setString(2, pass);
        pst.setInt(3, id);
        System.out.println(pst.executeUpdate());
        
        return fetchAllRecords();
    }
    
    ResultSet inserted(int id, String name, String pass) throws SQLException {
        pst = con.prepareStatement("INSERT INTO table1 VALUES(?,?,?)");
        pst.setInt(1, id);
        pst.setString(2, name);
        pst.setString(3, pass);
        
        pst.executeUpdate();
        
        return fetchAllRecords();
    }
}

class GUI extends JFrame implements ActionListener {
    JTextField t1,t2,t3;
    JButton b1,b2,b3,b4;
    JPanel p1,p2,pp1,pp2,pp3;
    
    dbOperations db;
    ResultSet rs;
    int currId = 1;
    int lastId;
    boolean Submit= false;
    
    GUI() {
        super("Type1 JDBC ODBC Driver");
        
        try {
            db = new dbOperations();
            rs = db.fetchAllRecords();
            rs.last();
            lastId = rs.getRow();
            rs.first();
            System.out.println(lastId);
            
            if(rs.next()) {
                t1 = new JTextField("" + rs.getInt(1), 20);
                t2 = new JTextField(rs.getString(2), 20);
                t3 = new JTextField(rs.getString(3), 20);
            } else {
                t1 = new JTextField("ID");
                t2 = new JTextField("NAME");
                t3 = new JTextField("PASSWD");
            }
            
            b1 = new JButton("Next");
            b1.addActionListener(this);
            
            b2 = new JButton("Previous");
            b2.addActionListener(this);
            
            b3 = new JButton("Reset");
            b3.addActionListener(this);
            
            b4 = new JButton("Submit");
            b4.addActionListener(this);
            
            p1 = new JPanel();
            p2 = new JPanel();
            
            pp1 = new JPanel();
            pp2 = new JPanel();
            pp3 = new JPanel();

            setLayout(new GridLayout(2,1));
            
            p1.setLayout(new GridLayout(3,1));
            p2.setLayout(new GridLayout(2,2));
            
            pp1.setLayout(new FlowLayout());
            pp1.add(new JLabel("ID : "));
            pp1.add(t1);
            
            pp2.setLayout(new FlowLayout());
            pp2.add(new JLabel("NAME : "));
            pp2.add(t2);
            
            pp3.setLayout(new FlowLayout());
            pp3.add(new JLabel("PASSWD : "));
            pp3.add(t3);
            
            p1.add(pp1);
            p1.add(pp2);
            p1.add(pp3);

            p2.add(b1);
            p2.add(b2);
            p2.add(b3);
            p2.add(b4);

            add(p1);
            add(p2);

        } catch(SQLException e) {
            System.out.println(e);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String str = ae.getActionCommand();
        
        if(str.equals("Next")) {
            currId++;
            
            try {
                if(rs.next()) {
                    t1.setText(""+rs.getInt(1));
                    t2.setText(rs.getString(2));
                    t3.setText(rs.getString(3));
                } else {
                    t1.setText("ID");
                    t2.setText("NAME");
                    t3.setText("PASSWD");
                }
            } catch(SQLException e) {
                System.out.println(e);
            }
        } else if(str.equals("Previous")) {
            currId--;
            
            try {
                if(rs.next()) {
                    t1.setText(""+rs.getInt(1));
                    t2.setText(rs.getString(2));
                    t3.setText(rs.getString(3));
                } else {
                    t1.setText("ID");
                    t2.setText("NAME");
                    t3.setText("PASSWD");
                }
            } catch(SQLException e) {
                System.out.println(e);
            }
        } else if(str.equals("Reset")) {
            String name, passwd;
            name = t2.getText();
            passwd = t3.getText();
            int id = Integer.parseInt(t1.getText());
            System.out.println("Here");
            try {
                rs = db.updateOne(id, name, passwd);
            } catch(SQLException e) {
                System.out.println(e);
            }
            
            System.out.println("Done");
        } else if(str.equals("Submit")) {
            if(Submit == false) {
                t1.setText("" + (lastId + 1) + "");
                t2.setText("Enter Name");
                t3.setToolTipText("Enter passwd & hit submit btn again");
                Submit = true;
            } else {
                try {
                    rs = db.inserted(Integer.parseInt(t1.getText()), t2.getText(), t3.getText());
                    Submit = false;
                    lastId++;
                } catch(NumberFormatException | SQLException e) {
                    System.out.println(e);
                }
            }
        }
    } 
}

public class Jdbc_type_1 {
    
    public static void main(String[] args) {
        
        GUI app = new GUI();
        app.setVisible(true);
        app.setSize(500,600);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }  
}
