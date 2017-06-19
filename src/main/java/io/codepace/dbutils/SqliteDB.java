package io.codepace.dbutils;

// http://www.sqlitetutorial.net/sqlite-java/select/ for resources

// Database interaction stuff
import org.sqlite.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;


public class SqliteDB {

    private String path;
    Connection conn = null;


    /**
     * Creates a blank database file at <code>path</code>
     * @param path The file to save the database at
     * @param initConnect Whether to skip the <code>db.connect()</code> line (jump start connection)
     */
    public SqliteDB(String path, boolean initConnect) throws IOException{

        if (path.endsWith("/")) throw new IOException("Cannot create a file with no filename!");
        if (!path.endsWith(".db")) path += ".db";  // Just in case
        this.path = path;
        File db = new File(path);
        if (initConnect)
            connect();
    }


    private boolean connect(){
        String dbUrl = "jdbc:sqlite:" + path;
        try {
            conn = DriverManager.getConnection(dbUrl);
        } catch (SQLException sqle){
            System.err.println("Unable to connect to database: " + sqle.getMessage());
            return false;
        }
        return true;
    }

    public boolean addTable(String name, String... lines){
        name = quickSafeString(name);
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + name + "(\n");
        for (String line : lines){
            sql.append(line + ",\n");
        }
        sql.append(");");

        try(Connection connection = getConnection()){
            Statement stmt = connection.createStatement();
            stmt.execute(sql.toString());
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return false;
        }
        return true;
    }

    public ResultSet selectAll(String table, String... args){

        ResultSet set = null;
        StringBuilder sql = new StringBuilder("SELECT ");
        for (int i = 0; i < args.length; i++) {
            if (i == args.length - 1){
                sql.append(args[i] + " ");
            } else {
                sql.append(args[i] + ", ");
            }
        }
        sql.append("FROM " + table);

        try (Connection connection = conn){
             Statement stmt = conn.createStatement();
             set = stmt.executeQuery(sql.toString());
        } catch (SQLException sqle){
            sqle.printStackTrace();
            return null;
        }

        return set;

    }



    /**
     * Gets the current database connection
     * @return The current connection
     */
    public Connection getConnection(){
        return conn;
    }

    private String quickSafeString(String in){
        StringBuilder out = new StringBuilder();
        out.append(in.replace("\\", "\\\\"));
        out.append(in.replace("'", "\\'"));
        out.append(in.replace("\"", "\\\""));
        return out.toString();
    }


}
