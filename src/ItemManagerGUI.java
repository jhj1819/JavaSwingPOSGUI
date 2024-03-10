import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;




class MyDialogModel extends JDialog {
    JTextField tf = new JTextField(10);
    JButton okBtn = new JButton("OK");

    public MyDialogModel(ItemManagerGUI friendsDBGUI, String string) {
        // TODO Auto-generated constructor stub
        super(friendsDBGUI, string, true);
        this.setLayout(new FlowLayout());
        this.setSize(200, 100);
        this.add(tf);
        this.add(okBtn);
        this.setVisible(false);
        this.pack();

        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                setVisible(false);
            }});

    }

    public String getInput() {
        // TODO Auto-generated method stub

        return tf.getText();
    }

}
public class ItemManagerGUI extends JFrame implements MouseListener, ActionListener{

    MyDialogModel myDialog = new MyDialogModel(this, "Message"); //미리 만들어두고, 버튼이 눌러지면 setVisible로 보이도록...

    static Connection con = null;
    static String dbTableName = "ItemTable_202058043";

    private JTable table;
    private DefaultTableModel model;
    private ResultSet result;
    public static Vector data = new Vector();
    private Vector<String> title = new Vector<String>();
    static String[] dbFieldName = {"menu","price","barcode","count"};
    JPanel panelLow = new JPanel();
    int[] tfSize = {10,10,10,12};
    JTextField[] tf = new JTextField[dbFieldName.length];
    JLabel[] label = new JLabel[dbFieldName.length];
    String[] btnText = {"삽입", "삭제", "수정", "Clear","POS","Dev."};
    JButton[] btn = new JButton[btnText.length];

    public ItemManagerGUI() {
        this.setTitle("POS : Item Manager!!! 202058043 컴퓨터공학부 정한준");
        this.setSize(700,300);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        createMenu();

        for(int i=0;i<dbFieldName.length;i++) {
            title.addElement(dbFieldName[i]);
        }

        Vector resultData = selectFromDB(con, dbTableName);
        model = new DefaultTableModel(resultData,title);  //result data, title : header
        table = new JTable(model);
        table.addMouseListener(this);
        JScrollPane sp =new JScrollPane(table);
        this.add(sp,BorderLayout.CENTER);
        panelLow.setLayout(new GridLayout(0,2));
        for(int i=0; i<tf.length;i++) {
            label[i] = new JLabel(dbFieldName[i],JLabel.CENTER);
            tf[i] = new JTextField(tfSize[i]);
            panelLow.add(label[i]);
            panelLow.add(tf[i]);
        }
        for(int i=0; i<btnText.length;i++) {
            btn[i] = new JButton(btnText[i]);
            btn[i].addActionListener(this);
            panelLow.add(btn[i]);
        }
        this.add(panelLow,BorderLayout.WEST);
        this.setVisible(true);

    }


    private void createMenu() {
        // TODO Auto-generated method stub
        JMenuBar mb = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        String[] menuItemText = {"book_DB_GUI","Adv_Pizza_GUI",
                "Image_Gallery_GUI", "Help","Exit"};

        JMenuItem[] menuItem = new JMenuItem[menuItemText.length];
        mb.add(fileMenu);
        for(int i =0; i<menuItem.length;i++) {
            menuItem[i] = new JMenuItem(menuItemText[i]);
            menuItem[i].addActionListener(new ActionListener() { //this로 처리시 위에 필드값이 너무 많아지므로...

                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stubA
                    String cmd = e.getActionCommand(); //menuItemText가 반환됨...
                    //System.out.println("선택한 메뉴: "+ cmd);

                    if(cmd.equals(menuItemText[0])) { // book_DB_GUI

                    }
                    if(cmd.equals(menuItemText[1])) { //Adv_Pizza_GUI
                        //new JavaLec_1011.SimplePizzaFrame();

                    }
                    if(cmd.equals(menuItemText[3])) { //Help -->일단 다이얼로그 보이는 기능..
                        myDialog.setVisible(true);

                    }

                }

            });
            fileMenu.add(menuItem[i]);
        }
        this.setJMenuBar(mb);
    }

    public Vector selectFromDB(Connection con, String dbTableName) {
        // TODO Auto-generated method stub
        data.clear();
        String selectDataSQL = "SELECT * FROM " + dbTableName + ";";
        PreparedStatement selectData;
        try {
            selectData = con.prepareStatement(selectDataSQL);
            result = selectData.executeQuery(selectDataSQL);
            while(result.next()) {
                Vector<String> row = new Vector<String>();
                row.add(result.getString("menu"));
                row.add(result.getInt("price")+"");
                row.add(result.getString("barcode"));
                row.add(result.getInt("count")+"");
                data.add(row);
            }
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return data;
    }



    public static void main(String[] args) {
        // TODO Auto-generated method stub
        con = ConnectFriendsDB.makeConnection();
        ConnectFriendsDB.dropTable(con);
        ConnectFriendsDB.createTable(con);
        ConnectFriendsDB.insertDataOnTable(con,"라면",500, "5 152145 121951",5);
        ConnectFriendsDB.insertDataOnTable(con,"스파게티",1500, "3 114215 128152",6);
        ConnectFriendsDB.insertDataOnTable(con,"짜파게티",1300, "6 145215 121752",2);
        ConnectFriendsDB.insertDataOnTable(con,"우동",2000, "7 152155 126152",10);
        new ItemManagerGUI();
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        int selectedRow = table.getSelectedRow();
        System.out.println("Selected Row #" + selectedRow + "번 라인");
        Vector<String> row = new Vector<String>();
        row = (Vector<String>) data.get(selectedRow);
        for (int i=0; i<tf.length; i++) {
            tf[i].setText(row.get(i));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }
    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }
    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }
    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

        if (e.getSource() == btn[0]) {
            String menu = tf[0].getText();
            int price = Integer.parseInt(tf[1].getText());
            String barcode = tf[2].getText();
            int count = Integer.parseInt(tf[3].getText());
            ConnectFriendsDB.insertDataOnTable(con, menu, price, barcode, count);

            Vector resultData = selectFromDB(con, dbTableName);
            model = new DefaultTableModel(resultData, title);
            table.setModel(model);  // 추가...
            //this.repaint();
        }
        if (e.getSource() == btn[1]) {   /// DELETE....
            String menu = tf[0].getText();
            ConnectFriendsDB.deleteDataOnTable(con, menu);
            Vector resultData = selectFromDB(con, dbTableName);
            model = new DefaultTableModel(resultData, title);
            table.setModel(model);
            this.repaint();

        }
        if (e.getSource() == btn[2]) {
            String menu = tf[0].getText();
            int price = Integer.parseInt(tf[1].getText());
            String barcode = tf[2].getText();
            int count = Integer.parseInt(tf[3].getText());
            ConnectFriendsDB.updateDataOnTable(con, menu, price, barcode, count);
            Vector resultData = selectFromDB(con, dbTableName);
            model = new DefaultTableModel(resultData, title);
            table.setModel(model);
            this.repaint();

        }
        if (e.getSource() == btn[3]) {

            for (int i=0; i<tf.length; i++) {
                tf[i].setText("");
            }

        }
        if (e.getSource() == btn[4]) { //POS 클릭했을때
            //new OrderGUI(con);
            new OrderGUI(con);
        }
        if (e.getSource() == btn[5]) {
            JOptionPane.showMessageDialog(null,"개발자: 202058043 정한준 Ver.1.0 \\n 2023.12.13\");");
        }

    }

}

//마우스 이벤트로 테이블 어떤 라인을 눌렀는지
//삽입시 검색을 다시 해서 테이블에 뿌려줘야함

//학번-이름 을 가지고 동적으로 버튼 만들기..

//지금까지 한거에서.. 테이블 필드명이 바뀔 수 있음... -> 버튼에대한.. SQL문도 바꿔야함... -> POS기에 어떤값을 불러올지도 새로..
//버튼 눌렀을때 POS기안의 테이블에 또 추가되게..
