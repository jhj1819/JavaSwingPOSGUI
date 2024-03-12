
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
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class OrderGUI extends JFrame implements ActionListener, MouseListener {

    static String dbTableName = "ItemTable_202058043";
    static String[] dbFieldName = {"menu","price"}; //변경시바꿔줘야... 수량은 가져오는게 아니지..
    Connection con = null;
    //String
    String orderBtnText[] = {"주문","취소","선택취소","모두담기"}; //아래 버튼
    String orderItemName[] = {"메뉴","가격","개수"}; 	//테이블 타이틀
    //JPanel
    JPanel menuBtnPanel = new JPanel();
    JPanel orderPanel = new JPanel() ;
    //JButton
    JButton[] menuBtn;
    JButton[] orderBtn = new JButton[orderBtnText.length];

    TitledBorder menuTitle;
    JTable order_table;
    Vector order_header = new Vector();
    Vector<Vector> order_data = new Vector();
    DefaultTableModel order_model;

    //JTextField
    JTextField priceTf = new JTextField(10);

    //JLabel
    JLabel priceLabel = new JLabel("금액: ");

    int selectedRow;
    PreparedStatement pstmt;
    ResultSet result;

    public OrderGUI(Connection con) {
        this.setTitle("POS: Order_GUI!! 202058043 컴퓨터공학과 정한준");
        this.setSize(500,300);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.con = con;

        menuTitle = BorderFactory.createTitledBorder("POS 메뉴");
        menuBtnPanel.setBorder(menuTitle);

        menuBtnPanel.setLayout(new GridLayout(0,2));

        String selectSQL = "SELECT * FROM " + dbTableName + " ;";


        try {
            pstmt = con.prepareStatement(selectSQL,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            result = pstmt.executeQuery(); //executeQuery():  PreparedStatement에 있는 sql문 실행 //ResultSet: select문을 table형태로 받는다..
            //다 가져옴 select*
            int size = 0;
            if (result != null) {
                result.last();
                size = result.getRow();
            }
            result.first();
            menuBtn = new JButton[size];

            for (int i=0; i<menuBtn.length; i++) {
                menuBtn[i] = new JButton();
                menuBtn[i].setText(result.getString(1) + ":" + result.getString(2));  // 필드 1,3 선택 1이시작임
                menuBtnPanel.add(menuBtn[i]);
                menuBtn[i].addActionListener(this);
                result.next();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        for (int i=0; i<orderItemName.length; i++) {
            order_header.add(orderItemName[i]);
        }
        order_model = new DefaultTableModel(order_header, 0);
        order_model.setDataVector(order_data, order_header);
        order_table = new JTable(order_model);
        JScrollPane scrollPane = new JScrollPane(order_table);
        this.add(scrollPane, BorderLayout.CENTER);


        this.add(menuBtnPanel, BorderLayout.EAST);

        //btnPanel
        orderPanel.setLayout(new FlowLayout());
        for(int i =0; i<orderBtn.length;i++) {
            orderBtn[i] = new JButton(orderBtnText[i]);
            orderBtn[i].addActionListener(this);
            orderPanel.add(orderBtn[i]);
        }
        orderPanel.add(priceLabel);
        priceTf.addActionListener(this);
        orderPanel.add(priceTf);
        this.add(orderPanel , BorderLayout.SOUTH);






        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

        for(int i =0; i<menuBtn.length;i++) {

            String[] token = menuBtn[i].getText().split(":"); //메뉴/가격
            //아하... order_table에 학번넣을때 스트링으로 넣었고.. 꺼내진값은.. 엘리먼트라 string으로 바꿔줘야하고.. +1하고 int로 넣으면 안되고 string으로 넣어줘야하는구나...
            if(e.getSource()== menuBtn[i]) {
                //result에 있는 데이터를... 벡터로 바꿔... 테이블에...
                boolean isAddedItem = false;
                for(Vector order_data_row : order_data) { //for each로
                    if(order_data_row.get(0).equals(token[0])) {  //order_data_row데이터의 한
                        int temp = Integer.parseInt(order_data_row.get(2)+"") + 1 ; //Vector을 String으로하면 +""필요없다...
                        //order_data_row.set(2, ""+temp);

                        Vector newRow = new Vector();
                        newRow.add(order_data_row.get(0));
                        newRow.add(order_data_row.get(1));
                        newRow.add(temp+"");
                        order_data.add(newRow);
                        //삭제는 어떻게...
                        order_data.remove(order_data_row);

                        //
                        isAddedItem = true;
                        break;
                    }
                }
                if(!isAddedItem) {
                    Vector row = new Vector() ;
                    row.add(token[0]);
                    row.add(token[1]);
                    row.add("1");
                    order_data.add(row);
                }


                //테이블갱신
                order_model = new DefaultTableModel(order_data,order_header);
                order_table.setModel(order_model);
                order_table.addMouseListener(this);
            }
        }


        if(e.getSource() == orderBtn[0]) { //주문버튼
            int sum =0;
            for (int i=0; i<order_model.getRowCount(); i++) {
                Vector<String> checkRow = new Vector<String>();
                checkRow = (Vector<String>) order_data.get(i);
                sum += Integer.parseInt(checkRow.get(1)) * Integer.parseInt(checkRow.get(2));
            }
            priceTf.setText(sum+"원");

        }
        if(e.getSource() == orderBtn[1]) { //전체취소

            order_data.clear();

            order_model = new DefaultTableModel(order_data,order_header);
            order_table.setModel(order_model);
            order_table.addMouseListener(this);

            int sum =0;
            for (int i=0; i<order_model.getRowCount(); i++) {
                Vector<String> checkRow = new Vector<String>();
                checkRow = (Vector<String>) order_data.get(i);
                sum += Integer.parseInt(checkRow.get(1)) * Integer.parseInt(checkRow.get(2));
            }
            priceTf.setText(sum+"원");
        }

        if(e.getSource() == orderBtn[2]) { //취소버튼
            int count = Integer.parseInt(order_data.get(selectedRow).get(2)+"");
            if(count>1) { //수량감소
                order_data.get(selectedRow).set(2, (count-1)+"");
            }else {  //삭제
                order_data.remove(selectedRow);
            }

            order_model = new DefaultTableModel(order_data,order_header);
            order_table.setModel(order_model);
            order_table.addMouseListener(this);

        }

        if(e.getSource() == orderBtn[3]) { //모두담기
            //선택한열의 DB의 수량만큼 추가..
            int max_count =0;
            max_count = getItemCount(order_data.get(selectedRow).get(0)+"");
            System.out.println(max_count);

            Vector newRow = new Vector();
            newRow.add(order_data.get(selectedRow).get(0));
            newRow.add(order_data.get(selectedRow).get(1));
            newRow.add(max_count+"");
            order_data.add(newRow);

            order_data.remove(order_data.get(selectedRow));

            order_model = new DefaultTableModel(order_data,order_header);
            order_table.setModel(order_model);
            order_table.addMouseListener(this);
        }
    }


    private int getItemCount(String selectMenu) {
        try {
            result.first();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            while(result.next()) {
                if(selectMenu.equals(result.getString("menu"))) {
                    int getCount = result.getInt("count");
                    result.first();
                    return getCount;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.print("못찾음");
        return 0;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        selectedRow = order_table.getSelectedRow();
        System.out.println("Selected Row #" + selectedRow + "번 라인");
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
}

//중복이 되지 않게도 가능하겠다...
//주문버튼.... 누르면 전체 금액 찍히게...
// 프렌드디비 중심으로.. 테이블구조가 바뀔 수 있고... sql문도 그에따라 수정.....
