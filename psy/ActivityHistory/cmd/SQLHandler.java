import java.sql.*;
import java.util.Date;

/**
 * Handles all SQL tasks
 */
public class SQLHandler{
    private Connection con;

    /**
     * Establishes a connection to the SQL server.
     * Not yet implemented
     */
    public SQLHandler(){
    }

    public void createAttendanceTable(){
        Statement stmnt = con.createStatement();
        stmnt.executeUpdate(
            "CREATE TABLE Attendance("
            + "EntryID INT NOT NULL AUTO_INCREMENT, Player VARCHAR(15) NOT NULL,"
            + "Day DATE NOT NULL, Hour0 VARBINARY(60), Hour1 VARBINARY(60),"
            + "Hour2 VARBINARY(60), Hour3 VARBINARY(60), Hour4 VARBINARY(60),"
            + "Hour5 VARBINARY(60), Hour6 VARBINARY(60), Hour7 VARBINARY(60),"
            + "Hour8 VARBINARY(60), Hour9 VARBINARY(60), Hour10 VARBINARY(60),"
            + "Hour11 VARBINARY(60), Hour12 VARBINARY(60), Hour13 VARBINARY(60),"
            + "Hour14 VARBINARY(60), Hour15 VARBINARY(60), Hour16 VARBINARY(60),"
            + "Hour17 VARBINARY(60), Hour18 VARBINARY(60), Hour19 VARBINARY(60),"
            + "Hour20 VARBINARY(60), Hour21 VARBINARY(60), Hour22 VARBINARY(60),"
            + "Hour23 VARBINARY(60), PRIMARY KEY (EntryID)"
        );
    }
}
