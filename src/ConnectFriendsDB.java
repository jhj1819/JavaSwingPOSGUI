import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

//DB커넥션에 해당하는 라이브러리만 모아둔것....
public class ConnectFriendsDB {
    static Connection con =null;
    static String dbTableName = "ItemTable_202058043";
    static String[] dbFieldName = {"menu","price","barcode","count"};


    public static Connection makeConnection() {
        // TODO Auto-generated method stub
        String driver = ""; //jar파일과 연결...
        String hostName = "";
        //개인pc의 mysql시 hostName = 127.0.0.1 or localhost,  // userName,databaseName,password
        String databaseName ="";
        String userName ="";
        String password ="";

        String utf8Connection =""; //한국어를 사용할것임,서울에서 함을 알림
        String url ="jdbc:mysql://"+hostName+":3306/"+databaseName+utf8Connection;

        //try catch대신 throw 사용시 에러발생 위치를 찾기 쉽지않음..
        try {
            Class.forName(driver); //jar파일과 연결...
            System.out.println("JDBC Connection OK!!");
            con = DriverManager.getConnection(url, userName, password);
            System.out.println("MySQL Server getConnerction OK!!");
            return con;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace(); //주석처리시 빨간글씨 안뜨고 아래 에러문만뜸...// 주석하는건 비추천..
            System.out.println("JDBC Connection Error!!");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("MySQL Server getConnerction Error!!");
        }
        return null;
    }

    public static void createTable(Connection con) {
        String createTableSQL = "create table if not exists "+dbTableName+" ("
                +"menu VARCHAR(15), "
                +"price INT, "
                +"barcode VARCHAR(20), "
                +"count INT, "
                +"UNIQUE INDEX(menu), "
                +"Primary key(menu)"
                +") default charset=utf8;";
        try {
            PreparedStatement createTable = con.prepareStatement(createTableSQL,ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE) ;
            createTable.execute();
            System.out.println("CreateTable OK!!!");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("CreateTable Error!!!");
        }
    }

    public static void dropTable(Connection con) {
        // TODO Auto-generated method stub
        String dropTableSQL = "DROP TABLE IF Exists "+dbTableName+";";
        try {
            PreparedStatement dropTable = con.prepareStatement(dropTableSQL);
            dropTable.execute();
            System.out.println("DropTable OK!!");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void printList(ArrayList<String> list) {
        for(String eachline : list) {
            System.out.println(eachline);
        }
    }


    public static ArrayList<String> getDataFromDB() {
        String selectDataSQL = "select * from " + dbTableName + ";";
        ArrayList<String> resultList = new ArrayList<String>();
        try {
            PreparedStatement selectData = con.prepareStatement(selectDataSQL);
            ResultSet result = selectData.executeQuery();
            while(result.next()) {
                resultList.add(dbFieldName[0]+": "+result.getInt(dbFieldName[0])
                        +"\t"+ dbFieldName[1]+": "+result.getString(dbFieldName[1])
                        +"\t"+ dbFieldName[2]+": "+result.getString(dbFieldName[2])
                        +"\t"+ dbFieldName[3]+": "+result.getString(dbFieldName[3])
                );
            }
            System.out.println("Select SQL OK!!!");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultList;
    }




    public static void insertDataOnTable(Connection con, String menu, int price, String barcode, int count) {
        String insertDataSQL ="INSERT INTO " + dbTableName + "(menu,price,barcode,count) "
                +"VALUES(?,?,?,?);";

        PreparedStatement insertData;
        try {
            insertData = con.prepareStatement(insertDataSQL);
            insertData.setString(1, menu);
            insertData.setInt(2, price);
            insertData.setString(3, barcode);
            insertData.setInt(4, count);
            insertData.execute();
            System.out.println("InsertData Ok!!");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("InsertData Error!!");
        }
    }

    public static void deleteDataOnTable(Connection con, String menu) {
        // TODO Auto-generated method stub
        String deleteDataSqL ="Delete from " + dbTableName + " where menu = ? ;";
        PreparedStatement deleteData;
        try {
            deleteData = con.prepareStatement(deleteDataSqL);
            deleteData.setString(1,menu);
            deleteData.execute();
            System.out.println("DeleteData OK!!!");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("DeleteData ERROR!!!");
        }

    }

    public static void updateDataOnTable(Connection con, String menu, int price, String barcode, int count) {
        // TODO Auto-generated method stub
        String updateDataSqL ="Update " + dbTableName + " set price = ?, barcode = ?, count = ? where menu = ?;";
        PreparedStatement updateData;
        try {
            updateData = con.prepareStatement(updateDataSqL);
            updateData.setString(4,menu);
            updateData.setInt(1,price);
            updateData.setString(2,barcode);
            updateData.setInt(3,count);
            updateData.execute();
            System.out.println("UpdateData OK!!!");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("UpdateData ERROR!!!");
        }
    }

}

//테이블 프린트...
