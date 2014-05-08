/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
 * @author Behnaz Bostanipour
 * @author Timotee Maret
*/

package org.openiot.gsn.http.ac;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 12, 2010
 * Time: 5:42:00 PM
 * To change this template use File | Settings | File Templates.
 */



import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.*;
import java.sql.DatabaseMetaData;



/* This class defines a ctdb object which helps to connect to AC DB and have
methods for accessing information in AC DB*/

public class ConnectToDB
{
	private static String driverName;
	private static String connectionname;
	private static String dbUser;
	private static String dbPassword;
    private static Vector ACTables;//list of AC tables

	private Connection con ;
	private Statement stmt ;
    private PreparedStatement pstmt ;
	private ResultSet rs ;
    private DatabaseMetaData meta;
    private String usedDB ; //used DB among diffrent choices(MySQL, Oracle, H2)
    private static transient Logger logger= Logger.getLogger( ConnectToDB.class );


    /****************************************** Constructors*******************************************/
    /*************************************************************************************************/
    
	public ConnectToDB()throws ClassNotFoundException,SQLException
	{
        //Load and register the MySQL driver
        Class.forName(driverName);
        //Get a connection to the database
        con = DriverManager.getConnection(connectionname,dbUser,dbPassword);
        //create a statement object
        stmt= con.createStatement();  ///// changed 20.03.2013
        meta= this.con.getMetaData();
        initUsedDB();
    }
    
    /****************************************** Init Methods*******************************************/
   /*************************************************************************************************/

   /* initialize DB connection */
    public static void init(String jdbcDriver, String jdbcUsername, String jdbcPassword, String jdbcURL)
    {
        driverName= jdbcDriver;
        dbUser=jdbcUsername;
        dbPassword= jdbcPassword;
        connectionname=jdbcURL;
        checkACTables();
    }
     /* Check if AC tables exist , and create them otherwise*/
     static void checkACTables()
    {
        Connection connection=null;
        Statement statement=null;

       try
		{

           //Load and register the MySQL driver
            Class.forName(driverName);
            //Get a connection to the database
            connection = DriverManager.getConnection(connectionname,dbUser,dbPassword);

			//create a statement object
			statement= connection.createStatement();
            ResultSet resultset=null;
            defineACTables();
            int indexOfMissingTable= findMissingTableIndex(statement,resultset);

            if(indexOfMissingTable==-1)
            {

                if(adminExists(statement,resultset) == false)
                {
                    createAdmin(statement,resultset);

                }

            }
            else
            {

               if(cleanDB(indexOfMissingTable,statement,resultset))
               {

                  if(createACTables(statement))
                  {

                    if(adminExists(statement,resultset) == false)
                    {
                        createAdmin(statement,resultset);

                    }
                  }
                   else
                  {
                     return;//throw an exception
                  }

               }
               else
               {
                   return;//throw an exception 
               }


            }
		}
        catch(ClassNotFoundException e)
        {
            logger.error("ERROR IN CHECKACTABLES METHOD :Couldn't load database driver ");
			logger.error(e.getMessage(),e);
        }
		catch(SQLException e)
		{
            logger.error("ERROR IN CHECKACTABLES METHOD :SQLException caught ");
			logger.error(e.getMessage(),e);

			while((e = e.getNextException())!= null )
			{
				logger.error(e.getMessage(),e);
			}
		}
       catch(Exception e)
       {

       }
        finally
         {
             try
             {
                if(statement!=null)
                {
                    statement.close();
                }
                if(connection !=null)
                {
                    connection.close();
                }
             }
             catch(SQLException e)
             {
                 logger.error("ERROR IN CHECKACTABLES METHOD :");
			     logger.error(e.getMessage(),e);
             }
         }

    }

    /* create a default Administrator for access control system */
    static boolean createAdmin(Statement statement,ResultSet resultset) throws Exception
    {

        boolean insertOK=false;
        String request = "INSERT INTO ACUSER(USERNAME,PASSWORD,FIRSTNAME,LASTNAME,EMAIL,ISCANDIDATE) VALUES ('Admin','"+ Protector.encrypt("changeit")+ "','Admin','Admin','gsn.administrator@host.com','no')";
        int f =statement.executeUpdate(request);
        if(f!=0)
        {
            insertOK=true;
        }
        return insertOK;

    }

    /* create a Vector of names of all AC DB tables, the order of elements in Vector is important because of the dependencies between tables */
    static void defineACTables()
    {
         ACTables = new Vector();

         ACTables.add("ACUSER_ACDATASOURCE");
         ACTables.add("ACUSER_ACGROUP");
         ACTables.add("ACGROUP_ACDATASOURCE");
         ACTables.add("ACGROUP");
         ACTables.add("ACUSER");
         ACTables.add("ACDATASOURCE");

    }
    /* return  the index of missing AC table in the AC DB, and -1 if no table is missing */
    static int findMissingTableIndex(Statement statement,ResultSet resultset)
    {

         for(int i=0; i<ACTables.size();i++)
         {
             if(tableExists((String)ACTables.get(i),statement,resultset)== false)
             {

                 return i;
             }

         }
         return -1;
     }

    /*remove all AC DB Tables if one of them is missing, in fact if one AC table is missing in the AC DB , it indicates
     an abnormal situation
      */
     static boolean cleanDB(int missingTableIndex,Statement statement,ResultSet resultset)
      {

          for(int i=0;i<missingTableIndex;i++)
          {
               if(tableExists((String)ACTables.get(i),statement,resultset))
               {
                    if(dropTable((String)ACTables.get(i),statement)==false)
                    {
                        return false;
                    }
               }
          }
          for(int i=missingTableIndex+1;i<ACTables.size();i++)
          {
                if(tableExists((String)ACTables.get(i),statement,resultset))
                {
                    if(dropTable((String)ACTables.get(i),statement)== false)
                    {
                        return false;
                    }
                }
           }
          return true;
      }

    /* checks if a given AC table exists in AC DB */
    static boolean tableExists(String tableName,Statement statement,ResultSet resultset)
    {
        if(tableNameExists(tableName.toUpperCase(),statement,resultset))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /* check if a default Admin exists in ACUSER table in DB */
    static boolean adminExists(Statement statement,ResultSet resultset)
    {
        if(adminNameExists(statement,resultset)==true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    /* drop a AC table */
    static boolean dropTable(String tableName,Statement statement)
    {
        boolean operationOK=true;
        try
        {
            statement.executeUpdate("DROP TABLE "+ tableName);
        }
        catch(SQLException e)
        {

            logger.error("ERROR IN DROPTABLE METHOD :");
			logger.error(e.getMessage(),e);
            operationOK=false;
        }
        return operationOK;
    }
    /* check if a table name exists in AC DB */
    static boolean tableNameExists(String tableName,Statement statement,ResultSet resultset)
    {
        boolean nameExists=true;
        try
        {
            resultset= statement.executeQuery("SELECT * FROM "+ tableName);
        }
        catch(SQLException e)
        {
            nameExists=false;
        }
        return nameExists;
    }

    /* check if the default administrator name exists in DB */
    static boolean adminNameExists(Statement statement,ResultSet resultset)
    {
        boolean nameExists=false;
        try
        {
            resultset= statement.executeQuery("SELECT * FROM ACUSER WHERE USERNAME='Admin'");
            while(resultset.next())
            {
                nameExists = true;
            }

        }
        catch(SQLException e)
        {
            nameExists=false;
        }
        return nameExists;
    }

    /* create all AC tables, order of creation is important because of the dependencies between tables */
    static boolean createACTables(Statement statement)
    {
        String query;
        /*
        ACTables.add("ACUSER_ACDATASOURCE");
        ACTables.add("ACUSER_ACGROUP");
        ACTables.add("ACGROUP_ACDATASOURCE");
        ACTables.add("ACGROUP");
        ACTables.add("ACUSER");
        ACTables.add("ACDATASOURCE");
        */
        try
        {
            query="CREATE TABLE ACDATASOURCE " +
                    "(DATASOURCENAME VARCHAR(100) NOT NULL, " +
                    "ISCANDIDATE VARCHAR(10) DEFAULT 'no', " +
                    "PRIMARY KEY (DATASOURCENAME))";
            statement.executeUpdate(query);

            query="CREATE TABLE ACUSER " +
                    "(USERNAME VARCHAR(100) NOT NULL, " +
                    "FIRSTNAME VARCHAR(100) NOT NULL, " +
                    "LASTNAME VARCHAR(100) NOT NULL, " +
                    "EMAIL VARCHAR(100) NOT NULL, " +
                    "PASSWORD VARCHAR(4000) NOT NULL, " +
                    "ISCANDIDATE VARCHAR(10) DEFAULT 'no', " +
                    "PRIMARY KEY (USERNAME))";
            statement.executeUpdate(query);

            query="CREATE TABLE ACGROUP " +
                    "(GROUPNAME VARCHAR(100) NOT NULL, " +
                    "PRIMARY KEY (GROUPNAME))";
            statement.executeUpdate(query);

            query="CREATE TABLE ACGROUP_ACDATASOURCE " +
                    "(GROUPNAME VARCHAR(100) NOT NULL, " +
                    "DATASOURCENAME VARCHAR(100) NOT NULL, " +
                    "DATASOURCETYPE VARCHAR(30) NOT NULL, "+
                    "PRIMARY KEY (GROUPNAME,DATASOURCENAME), "+
                    "FOREIGN KEY(GROUPNAME) REFERENCES ACGROUP(GROUPNAME),"+
                    "FOREIGN KEY(DATASOURCENAME) REFERENCES ACDATASOURCE(DATASOURCENAME))";
            statement.executeUpdate(query);

            query="CREATE TABLE ACUSER_ACGROUP " +
                    "(USERNAME VARCHAR(100) NOT NULL, " +
                    "GROUPNAME VARCHAR(100) NOT NULL, " +
                    "GROUPTYPE VARCHAR(10) DEFAULT 'n', " +
                    "ISUSERWAITING VARCHAR(10) DEFAULT 'no', " +
                    "PRIMARY KEY (USERNAME,GROUPNAME), "+
                    "FOREIGN KEY(USERNAME) REFERENCES ACUSER(USERNAME),"+
                    "FOREIGN KEY(GROUPNAME) REFERENCES ACGROUP(GROUPNAME))";
            statement.executeUpdate(query);
            
            query="CREATE TABLE ACUSER_ACDATASOURCE " +
                    "(USERNAME VARCHAR(100) NOT NULL, " +
                    "DATASOURCENAME VARCHAR(100) NOT NULL, " +
                    "DATASOURCETYPE VARCHAR(30), "+
                    "PATH VARCHAR(50) DEFAULT 'n', "+
                    "FILENAME VARCHAR(50) DEFAULT 'n', "+
                    "FILETYPE VARCHAR(50) DEFAULT 'n', "+
                    "OWNERDECISION VARCHAR(30) DEFAULT 'notreceived', "+
                    "ISUSERWAITING VARCHAR(10) DEFAULT 'no', " +
                    "PRIMARY KEY (USERNAME,DATASOURCENAME), "+
                    "FOREIGN KEY(USERNAME) REFERENCES ACUSER(USERNAME),"+
                    "FOREIGN KEY(DATASOURCENAME) REFERENCES ACDATASOURCE(DATASOURCENAME))";
            statement.executeUpdate(query);

            ////////////////////////////// ADD one more table for the permissible access date
            query="CREATE TABLE ACACCESS_DURATION " +
                    "(USERNAME VARCHAR(100) NOT NULL, " +
                    "DATASOURCENAME VARCHAR(100) NOT NULL, " +
                    "DEADLINE DATE, "+
                    "PRIMARY KEY (USERNAME,DATASOURCENAME), "+
                    "FOREIGN KEY(USERNAME) REFERENCES ACUSER(USERNAME),"+
                    "FOREIGN KEY(DATASOURCENAME) REFERENCES ACDATASOURCE(DATASOURCENAME))";
            statement.executeUpdate(query);
        }
        catch(SQLException e)
        {

            logger.error("ERROR IN CREATEACTABLES METHOD :");
			logger.error(e.getMessage(),e);
            return false;
        }
        return true;
    }

    /* precise which DB platform we are using */
    void initUsedDB()
    {
        if(driverName.equals("com.mysql.jdbc.Driver"))
            {
                 usedDB="MySQL";

            }
            else if(driverName.equals("oracle.jdbc.driver.OracleDriver"))
            {
                  usedDB="Oracle";
            }
            else if(driverName.equals("org.h2.Driver"))
            {
                  usedDB="H2";
            }

    }
    /****************************************** Set Methods*******************************************/
   /*************************************************************************************************/

   void setConnection(Connection con)
   {
       this.con=con;
   }
   void setStatement(Statement stmt)
   {
       this.stmt=stmt;
   }
   void setResultSet(ResultSet rs)
   {
       this.rs=rs;
   }
    void setMetaData(DatabaseMetaData meta)
    {
        this.meta=meta;
    }
    void setUsedDB(String usedDB)
    {
        this.usedDB=usedDB;
    }

    /****************************************** Get Methods*******************************************/
   /*************************************************************************************************/

   Connection getConnection()
   {
       return this.con;
   }
    Statement getStatement()
    {
        return this.stmt;
   }
    ResultSet getResultSet()
    {
        return this.rs;
    }
    DatabaseMetaData getMetaData()
    {
        return this.meta;
    }
    String getUsedDB()
    {
        return this.usedDB;
    }
    Vector getACTables()
    {
        return ACTables;
    }

    /****************************************** DB Basic Queries*******************************************/
   /******************************************************************************************************/

    //OK
    ResultSet  selectOneColumn(Column col, String tableName)throws SQLException
    {
        String query="SELECT "+col.columnLabel+" FROM "+tableName;
        return stmt.executeQuery(query);
        
    }
    //OK
    ResultSet selectOneColumnUnderOneCondition(Column col, String tableName,Column cond)throws SQLException
	{
        String query="SELECT "+col.columnLabel+" FROM "+tableName+" WHERE "+cond.columnLabel+" = '"+cond.columnValue+"' ";
        return stmt.executeQuery(query);
    }
    //OK
    ResultSet selectOneColumnUnderTwoConditions(Column col, String tableName,Column firstCond,Column secondCond)throws SQLException
	{
        String query="SELECT "+col.columnLabel+" FROM "+tableName+" WHERE "+firstCond.columnLabel+" = '"+firstCond.columnValue+"' AND "+secondCond.columnLabel+" = '"+secondCond.columnValue+"'";
        return stmt.executeQuery(query);
    }
    //OK
    ResultSet selectTwoColumnsUnderOneCondition(Column firstCol,Column secondCol, String tableName,Column cond)throws SQLException
	{

        String query="SELECT "+firstCol.columnLabel+","+secondCol.columnLabel+" FROM "+tableName+" WHERE "+cond.columnLabel+"='"+cond.columnValue+"'";
        return stmt.executeQuery(query);
    }

     ResultSet selectFiveColumns(Column firstCol,Column secondCol,Column thirdCol,Column fourthCol,Column fifthCol, String tableName)throws SQLException
	{

        String query="SELECT "+firstCol.columnLabel+","+secondCol.columnLabel+","+thirdCol.columnLabel+","+fourthCol.columnLabel+","+fifthCol.columnLabel+" FROM "+tableName;
        return stmt.executeQuery(query);
    }
     //to be checked
    ResultSet selectTwoColumnsUnderTwoConditions(Column firstCol,Column secondCol, String tableName,Column firstCond,Column secondCond)throws SQLException
	{

        String query="SELECT "+firstCol.columnLabel+","+secondCol.columnLabel+" FROM "+tableName+" WHERE "+firstCond.columnLabel+" = '"+firstCond.columnValue+"' AND "+secondCond.columnLabel+" = '"+secondCond.columnValue+"'";
        return stmt.executeQuery(query);
    }
    //to be checked
    ResultSet selectTwoColumnsUnderThreeConditions(Column firstCol,Column secondCol, String tableName,Column firstCond,Column secondCond,Column thirdCond)throws SQLException
	{

        String query="SELECT "+firstCol.columnLabel+","+secondCol.columnLabel+" FROM "+tableName+" WHERE "+firstCond.columnLabel+" = '"+firstCond.columnValue+"' AND "+secondCond.columnLabel+" = '"+secondCond.columnValue+"' AND "+thirdCond.columnLabel+" = '"+thirdCond.columnValue+"'";
        return stmt.executeQuery(query);
    }
    ResultSet selectThreeColumnsUnderTwoConditions(Column firstCol,Column secondCol,Column thirdCol, String tableName,Column firstCond,Column secondCond)throws SQLException
	{

        String query="SELECT "+firstCol.columnLabel+","+secondCol.columnLabel+","+thirdCol.columnLabel+" FROM "+tableName+" WHERE "+firstCond.columnLabel+" = '"+firstCond.columnValue+"' AND "+secondCond.columnLabel+" = '"+secondCond.columnValue+"'";
        return stmt.executeQuery(query);
    }
    //OK
    ResultSet selectAllColumns(String tableName)throws SQLException
    {
        String query="SELECT * FROM "+tableName;
        return stmt.executeQuery(query);
    }
    //OK
    ResultSet selectAllColumnsUnderOneCondition(String tableName,Column cond)throws SQLException
    {
       String query="SELECT * FROM "+tableName+" WHERE "+cond.columnLabel+"='"+cond.columnValue+"'";
       return stmt.executeQuery(query);
    }
    //OK
    ResultSet selectAllColumnsUnderTwoConditions(String tableName,Column firstCond,Column secondCond)throws SQLException
    {
       String query="SELECT * FROM "+tableName+" WHERE "+firstCond.columnLabel+" = '"+firstCond.columnValue+"' AND "+secondCond.columnLabel+" = '"+secondCond.columnValue+"'";
       return stmt.executeQuery(query);
    }
    //Ok
    Vector getValuesVectorForOneColumnUnderOneCondition(Column col,Column cond,String tableName) throws SQLException
    {
        Vector values=new Vector();
        rs=this.selectOneColumnUnderOneCondition(col, tableName,cond);
        while(rs.next())
        {
            values.add(rs.getString(col.columnLabel));
        }
        return values;
    }
    // to be checked
    Vector getValuesVectorForOneColumnUnderTwoConditions(Column col,Column firstCond,Column secondCond,String tableName) throws SQLException
    {
        Vector values=new Vector();

        rs=selectOneColumnUnderTwoConditions(col, tableName,firstCond,secondCond);
        while(rs.next())
        {
            values.add(rs.getString(col.columnLabel));
        }
        return values;
    }
        //Ok
    Vector getValuesVectorForOneColumn(Column col,String tableName) throws SQLException
    {
        Vector values=new Vector();
        rs=this.selectOneColumn(col, tableName);
        while(rs.next())
        {
            values.add(rs.getString(col.columnLabel));
        }
        return values;
    }
    //OK
    String getValueForOneColumnUnderTwoConditions(Column col,Column firstCond,Column secondCond,String tableName) throws SQLException
    {
        String value=null;
        rs=this.selectOneColumnUnderTwoConditions(col, tableName,firstCond,secondCond);
        while(rs.next())
        {
            value=rs.getString(col.columnLabel);
        }
        return value;

    }
    String getValueForOneColumnUnderOneConditions(Column col,Column cond,String tableName) throws SQLException
    {
        String value=null;
        rs=this.selectOneColumnUnderOneCondition(col,tableName,cond);
        while(rs.next())
        {
            value=rs.getString(col.columnLabel);
        }
        return value;

    }
    //OK
    boolean insertOneColumnValue(Column col,String tableName) throws Exception
	{
        boolean insertOK=false;
        String request = "INSERT INTO "+tableName+"("+col.columnLabel+") VALUES ('" + col.columnValue +"')";
        int t =stmt.executeUpdate(request);
		if(t!=0)
		{
            insertOK=true;
        }
        return insertOK;
    }
     boolean insertTwoColumnsValues(Column firstCol,Column secondCol,String tableName) throws SQLException
	{
		boolean insertOK=false;
        String request = "INSERT INTO "+tableName+"("+firstCol.columnLabel+","+secondCol.columnLabel+") VALUES ('" + firstCol.columnValue + "','"+ secondCol.columnValue+"')";
        int f =stmt.executeUpdate(request);
        if(f!=0)
        {
            insertOK=true;
        }
        return insertOK;
	}
    //OK
     boolean insertThreeColumnsValues(Column firstCol,Column secondCol,Column thirdCol,String tableName) throws SQLException
	{
		boolean insertOK=false;
        String request = "INSERT INTO "+tableName+"("+firstCol.columnLabel+","+secondCol.columnLabel+","+thirdCol.columnLabel+") VALUES ('" + firstCol.columnValue + "','"+ secondCol.columnValue+ "','"+thirdCol.columnValue+"')";
        int f =stmt.executeUpdate(request);
        if(f!=0)
        {
            insertOK=true;
        }
        return insertOK;
	}

    boolean insertThreeColumnsValuesStrings(String firstCol,String secondCol,String thirdCol,String tableName) throws SQLException
    {
        boolean insertOK=false;
        String request = "INSERT INTO "+tableName+" VALUES ('" + firstCol + "','"+ secondCol+ "','"+thirdCol+"')";
        int f =stmt.executeUpdate(request);
        if(f!=0)
        {
            insertOK=true;
        }
        return insertOK;
    }

     boolean insertFourColumnsValues(Column firstCol,Column secondCol,Column thirdCol,Column fourthCol,String tableName)throws SQLException
    {
        boolean insertOK=false;
        String request = "INSERT INTO "+tableName+"("+firstCol.columnLabel+","+secondCol.columnLabel+","+thirdCol.columnLabel+","+fourthCol.columnLabel+") VALUES ('" + firstCol.columnValue + "','"+ secondCol.columnValue+ "','"+thirdCol.columnValue+"','"+ fourthCol.columnValue+"')";
        int f =stmt.executeUpdate(request);
        if(f!=0)
        {
            insertOK=true;
        }
        return insertOK;
    }

    boolean insertSixColumnsValues(Column firstCol,Column secondCol,Column thirdCol,Column fourthCol,Column fifthCol,Column sixthCol,String tableName)throws SQLException
    {
        boolean insertOK=false;
        String request = "INSERT INTO "+tableName+"("+firstCol.columnLabel+","+secondCol.columnLabel+","+thirdCol.columnLabel+","+fourthCol.columnLabel+","+fifthCol.columnLabel+","+sixthCol.columnLabel+") VALUES ('" + firstCol.columnValue + "','"+ secondCol.columnValue+ "','"+thirdCol.columnValue+"','"+ fourthCol.columnValue+ "','"+fifthCol.columnValue+ "','"+sixthCol.columnValue+"')";
        int f =stmt.executeUpdate(request);
        if(f!=0)
        {
            insertOK=true;
        }
        return insertOK;
    }

    boolean insertSeventhColumnsValues(Column firstCol,Column secondCol,Column thirdCol,Column fourthCol,Column fifthCol,Column sixthCol,Column seventhCol,String tableName)throws SQLException
    {
        boolean insertOK=false;
        String request = "INSERT INTO "+tableName+"("+firstCol.columnLabel+","+secondCol.columnLabel+","+thirdCol.columnLabel+","+fourthCol.columnLabel+","+fifthCol.columnLabel+","+sixthCol.columnLabel+","+seventhCol.columnLabel+") VALUES ('" + firstCol.columnValue + "','"+ secondCol.columnValue+ "','"+thirdCol.columnValue+"','"+fourthCol.columnValue+ "','"+fifthCol.columnValue+ "','"+sixthCol.columnValue+ "','"+seventhCol.columnValue+"')";
        int f =stmt.executeUpdate(request);
        if(f!=0)
        {
            insertOK=true;
        }
        return insertOK;
    }

    /****************************************** DB Check methods *********************************************/
   /******************************************************************************************************/

    /*This method will find all kind of condition value(for candidate registration, group registration)*/
    boolean valueExistsForThisColumn(Column cond, String tableName)throws SQLException
	{
		boolean valueFound=false;
        rs = selectAllColumnsUnderOneCondition(tableName,cond);

		//Display
		while(rs.next())
		{
            valueFound=true;
        }
		return valueFound;

	}
    /*This method only find the same condition value(for user loginhandler)*/
    boolean valueExistsForThisColumnUnderOneCondition(Column col,Column cond,String tableName)throws SQLException
    {
        boolean valueFound=false;
        rs=this.selectOneColumnUnderOneCondition(col,tableName,cond);

		//Display
		while(rs.next())
		{
			if(col.columnValue!= null)
			{

				if(col.columnValue.equals(rs.getString(col.columnLabel)))
				{

					valueFound=true;
				}
			}
		}
		return valueFound;

    }
     /*This method only find the same condition value*/
    boolean valueExistsForThisColumnUnderTwoConditions(Column col,Column firstCond,Column secondCond, String tableName)throws SQLException
	{
		boolean valueFound=false;
        rs =  selectOneColumnUnderTwoConditions(col, tableName,firstCond,secondCond);

		//Display
		while(rs.next())
		{
			if(col.columnValue!= null)
			{

				if(col.columnValue.equals(rs.getString(col.columnLabel)))
				{

					valueFound=true;
				}
			}
		}
		return valueFound;

	}

    boolean isPasswordCorrectForThisUser(String userName,String password)throws Exception //password is already encrypted when arrives here!
    {
        boolean passwordOK=false;
        rs=this.selectOneColumnUnderOneCondition(new Column("PASSWORD"), "ACUSER", new Column("USERNAME",userName));
        while(rs.next())
        {
            if(password.equals(rs.getString("PASSWORD")))
            {
                passwordOK=true;
            }
        }
        return passwordOK;
    }
    


     /****************************************** DB Get methods *********************************************/
   /********************************************************************************************************/

   /* return the list of all groups existing in AC DB */
   Vector getGroupList()throws SQLException
   {
       String groupname=null;
       Vector groupnamevector = this.getValuesVectorForOneColumn(new Column("GROUPNAME"),"ACGROUP");
       Vector groupvector = new Vector();
       for(int i=0;i< groupnamevector.size();i++)
       {
           groupname=(String)(groupnamevector.get(i));
           //System.out.println(" values in groupname vector : "+ groupname );
           groupvector.add(new Group( groupname,getDataSourceListForGroup(groupname) ));
        }
       return groupvector;
    }

    /* return the list of all virtual sensors existing in AC DB */
   Vector getDataSourceList()throws SQLException
   {
       return this.getValuesVectorForOneColumnUnderOneCondition(new Column("DATASOURCENAME"),new Column("ISCANDIDATE","no"),"ACDATASOURCE");
   }

    /* get list of all prospective users, everybody who has already signed up but waits for Admin confirmation to become a user */
    Vector getUserCandidates()throws SQLException
    {
        String username=null;
        Vector usernamevector = this.getValuesVectorForOneColumnUnderOneCondition(new Column("USERNAME"),new Column("ISCANDIDATE","yes"),"ACUSER");
        Vector uservector = new Vector();
        for(int i=0;i< usernamevector.size();i++)
        {
              username=(String)(usernamevector.get(i));//WAITINGACUSER
              uservector.add(new User(username,getValueForOneColumnUnderOneConditions(new Column("PASSWORD"),new Column("USERNAME",username),"ACUSER"),getValueForOneColumnUnderOneConditions(new Column("FIRSTNAME"),new Column("USERNAME",username),"ACUSER"),getValueForOneColumnUnderOneConditions(new Column("LASTNAME"),new Column("USERNAME",username),"ACUSER"),getValueForOneColumnUnderOneConditions(new Column("EMAIL"),new Column("USERNAME",username),"ACUSER"),getGroupListForUser(username),"yes"));
         }
        return uservector;
    }


    /* get list of all users who are applied for an access right modification for (a) group(s) or for (a) virtual sensor(s) */
     Vector getWaitingUsers()throws SQLException       
    {
        User user=null;
        Vector usersVector = getUsers();
        Vector usersCompletedVector = new Vector();
        for(int i=0;i< usersVector.size();i++)
        {
            user=(User)(usersVector.get(i));
            user.setDataSourceList(getDataSourcesForWaitingUser(user.getUserName()));
            user.setGroupList(getGroupsForWaitingUserSecondPart(getGroupsForWaitingUserFirstPart(user.getUserName())));
            if(user.getDataSourceList().size()!=0 || user.getGroupList().size()!=0)
            {
                usersCompletedVector.add(user);
            }
         }
        return usersCompletedVector;
    }

    /* get list of all users of AC system */

    Vector getUsers()throws SQLException
    {
        User user=null;
        Vector usersVector=new Vector();
        rs = this.selectAllColumns("ACUSER");
        while(rs.next())
        {
            user=new User(rs.getString("USERNAME"));
            user.setFirstName(rs.getString("FIRSTNAME"));
            user.setLastName(rs.getString("LASTNAME"));
            user.setEmail(rs.getString("EMAIL"));
            usersVector.add(user);
        }
        return usersVector;

    }
    /* get the list of groups(each element is a GROUP with groupName and GroupType, the list of virtual sensors in group is not precised) for which a user is waiting the access right modification */
    Vector getGroupsForWaitingUserFirstPart(String userName)throws SQLException
    {
        Vector groupVector= new Vector();
        rs = this.selectTwoColumnsUnderTwoConditions(new Column("GROUPNAME"),new Column("GROUPTYPE"), "ACUSER_ACGROUP",new Column("USERNAME",userName),new Column("ISUSERWAITING","yes"));
        while(rs.next())
        {
            groupVector.add(new Group(rs.getString("GROUPNAME"),rs.getString("GROUPTYPE")));
        }
        return groupVector;

    }
    /* get the list of groups(each element is a GROUP with groupName and GroupType, the list of virtual sensors in group is precised) for which a user is waiting the access right modification */
    Vector getGroupsForWaitingUserSecondPart(Vector groupList)throws SQLException
    {
        Vector vector= new Vector();
        Group group=null;
        for(int i=0;i<groupList.size();i++)
        {
            group=(Group)groupList.get(i);
            group.setDataSourceList(getDataSourceListForGroup(group.getGroupName()));
            vector.add(group);

        }
        return vector;

    }

    /* get the list of virtual sensors existing in the group with their coressponding access rights */
    Vector getDataSourcesForWaitingUser(String userName)throws SQLException
    {
        Vector dsVector= new Vector();

        rs = this.selectThreeColumnsUnderTwoConditions(new Column("DATASOURCENAME"),new Column("DATASOURCETYPE"),new Column("OWNERDECISION"), "ACUSER_ACDATASOURCE",new Column("USERNAME",userName),new Column("ISUSERWAITING","yes"));
        while(rs.next())
        {
            dsVector.add(new DataSource(rs.getString("DATASOURCENAME"),rs.getString("DATASOURCETYPE"),rs.getString("OWNERDECISION")));
        }
        return dsVector;

    }

    /* get the list of groups to which a user has access to */
   Vector getGroupListForUser(String userName)throws SQLException
   {
       String groupname=null;
       String grouptype=null;
       Vector groupnamevector = this.getValuesVectorForOneColumnUnderOneCondition(new Column("GROUPNAME"),new Column("USERNAME",userName),"ACUSER_ACGROUP");
       Vector groupvector = new Vector();
       for(int i=0;i< groupnamevector.size();i++)
       {
           groupname=(String)(groupnamevector.get(i));
           grouptype=this.getValueForOneColumnUnderTwoConditions(new Column("GROUPTYPE"),new Column("GROUPNAME",groupname),new Column("USERNAME", userName),"ACUSER_ACGROUP");
           if(grouptype.charAt(0)!='5')
           {
                groupvector.add(new Group( groupname , grouptype,getDataSourceListForGroup(groupname) ));
           }
       }
       return groupvector;
   }
    /* given the groupname this method returns the list of all virtual sensors access rights in the group */
    Vector getDataSourceListForGroup(String groupName) throws SQLException
    {
        Vector dsList=new Vector();
        rs= this.selectTwoColumnsUnderOneCondition(new Column("DATASOURCENAME"),new Column("DATASOURCETYPE"),"ACGROUP_ACDATASOURCE",new Column("GROUPNAME", groupName));
        //Display
        while(rs.next())
        {
            dsList.add(new DataSource(rs.getString("DATASOURCENAME"),rs.getString("DATASOURCETYPE")));
        }
        return dsList;

    }

    /* given the enterd parameters for a list virtual sensors, access rights from a HTML form, retun the list of DataSource objects */
    Vector getDataSourceListForParameterSet(ParameterSet pm)throws SQLException
    {
        Vector dsList=new Vector();
        rs = selectOneColumn(new Column("DATASOURCENAME"), "ACDATASOURCE");
        while(rs.next())
        {
            if(pm.valueForName(rs.getString("DATASOURCENAME"))!=null)
            {
                dsList.add( new DataSource(rs.getString("DATASOURCENAME"), pm.valueForName(rs.getString("DATASOURCENAME"))));
            }
        }
        return dsList;
    }

    Vector getChangedDataSourceListForParameterSet(ParameterSet pm)throws SQLException
    {
        Vector dsList=new Vector();
        rs = selectOneColumn(new Column("DATASOURCENAME"), "ACDATASOURCE");
        while(rs.next())
        {
            if(pm.valueForName(rs.getString("DATASOURCENAME"))!=null && (pm.valueForName(rs.getString("DATASOURCENAME")).equals("0")==false))
            {
                dsList.add( new DataSource(rs.getString("DATASOURCENAME"), pm.valueForName(rs.getString("DATASOURCENAME"))));
            }
        }
        return dsList;
    }


    /*  given the username, return the data source list( virtual sensor, access rights) to which user has access */
    Vector getDataSourceListForUser(String userName)throws SQLException
    {
        String dstype=null;
        Vector dsList=new Vector();
        rs=this.selectAllColumnsUnderOneCondition("ACUSER_ACDATASOURCE",new Column("USERNAME",userName));
        while(rs.next())
        {
            dsList.add(new DataSource(rs.getString("DATASOURCENAME"),rs.getString("DATASOURCETYPE"),rs.getString("FILENAME"),rs.getString("FILETYPE"),rs.getString("PATH"),rs.getString("OWNERDECISION")));
        }
        return dsList;
    }
    Vector getDataSourceListForUserLogin(String userName)throws SQLException
    {
        return filterDataSourceListForLogin(getDataSourceListForUser(userName));

    }

    /* return the list of datasources(virtual sensor name, access rights) for an owner */
    Vector getDataSourceNamesListForThisOwner(User owner)throws SQLException
    {
        DataSource ds=null;
        Vector dsNamesList= new Vector();
        //return getValuesVectorForOneColumnUnderTwoConditions(new Column("DATASOURCENAME"),new Column("DATASOURCETYPE","4"),new Column("USERNAME",ownername),"ACUSER_ACDATASOURCE");
        Vector dsList=owner.getDataSourceList();
        if(dsList !=null)
        {
            for(int i=0;i<dsList.size();i++)
            {
                ds=(DataSource)dsList.get(i);
                if(ds.getDataSourceType().equals("4"))
                {
                    dsNamesList.add(ds.getDataSourceName());
                }
            }

        }
        return dsNamesList;
    }

    /* return the list of users who have modified their access rights for this owner virtual sensors and are waitin for his/her decision */
    Vector getUsersWaitingForThisOwnerDecision(String dsname)throws SQLException
    {
        Vector users= new Vector();
        String dataSourceType=null;
        String userName=null;
        rs =this.selectTwoColumnsUnderThreeConditions(new Column("USERNAME"),new Column("DATASOURCETYPE"), "ACUSER_ACDATASOURCE",new Column("DATASOURCENAME",dsname),new Column("OWNERDECISION","notreceived"),new Column("ISUSERWAITING","yes"));
        while(rs.next())
        {
            dataSourceType=rs.getString("DATASOURCETYPE");
            userName= rs.getString("USERNAME");
            

            if(dataSourceType.charAt(1)!=('0')&& dataSourceType.equals("4")==false)
            {
                users.add(new User( userName, new DataSource(dsname,dataSourceType)));

            }
        }
        return users;
    }

   /* auxiliary method */
    Vector completeUsersList(Vector usersList)throws SQLException
    {
        User userA=null;
        User userB=null;
        Vector newList=new Vector();
        for(int i=0;i<usersList.size();i++)
        {
            userA=(User)usersList.get(i);
            userB=this.getUserForUserName(userA.getUserName());
            userA.setFirstName(userB.getFirstName());
            userA.setLastName(userB.getLastName());
            userA.setEmail(userB.getEmail());
            newList.add(userA);

        }
       return newList;
    }


     /* auxiliary method */
    Vector filterDataSourceListForLogin(Vector oldVec)throws SQLException
    {
        Vector newVec=new Vector();
        DataSource old=null;
        for(int i=0;i<oldVec.size();i++)
        {
            old=(DataSource)oldVec.get(i);
            if(isDataSourceCandidate(old)== false && isUserWaitingToAddThisDataSource(old)==false )
            {
                newVec.add(old);
            }
        }
        return newVec;
    }

    /* check if this virtual sensor is waiting  */
    boolean isDataSourceCandidate(DataSource ds)throws SQLException
    {
        boolean isCandi=false;
        if((getValueForOneColumnUnderOneConditions(new Column("ISCANDIDATE"),new Column("DATASOURCENAME",ds.getDataSourceName()),"ACDATASOURCE")).equals("yes"))
        {
            isCandi=true;
        }
        return isCandi;

    }
    /* checks if user is waiting the owner decision for adding this virtual sensor, charAt(0)=='5' indicates that user
    wants to add this virtual sensor
     */
    boolean isUserWaitingToAddThisDataSource(DataSource ds)
    {
        boolean isWaiting=false;
        if( ds.getDataSourceType().charAt(0)=='5')
        {
            isWaiting=true;
        }
        return isWaiting;
    }


   /* get the list of all prospective virtual sensors */
   Vector getDataSourceCandidates()throws SQLException
   {
       String dsname=null;
       Vector dsnamevector = this.getValuesVectorForOneColumnUnderOneCondition(new Column("DATASOURCENAME"),new Column("ISCANDIDATE","yes"),"ACDATASOURCE");
       Vector dsvector = new Vector();
       
       for(int i=0;i< dsnamevector.size();i++)
       {
           dsname=(String)(dsnamevector.get(i));
           dsvector.add(getDataSourceCandidateForDataSourceName(dsname));

        }
       return dsvector;
    }
    /* given the name of a prospective virtual sensor, this method retrurns an object DataSource with auxiliary information */
    DataSource getDataSourceCandidateForDataSourceName(String dataSourceName)throws SQLException
    {
        DataSource ds= new DataSource(dataSourceName);
        ds.setIsCandidate("yes");
        String ownerName=null;
        rs=this.selectAllColumnsUnderOneCondition("ACUSER_ACDATASOURCE",new Column("DATASOURCENAME",dataSourceName));
        while(rs.next())
        {
            ds.setFileName(rs.getString("FILENAME"));
            ds.setFileType(rs.getString("FILETYPE"));
            ds.setPath(rs.getString("PATH"));
            ownerName=rs.getString("USERNAME");
        }
        ds.setOwner(this.getUserForUserName(ownerName));
        return ds;
    }
    /* given the name of the user and  a virtual sensor, returns an object DataSource which contains also access
       right( data source type ) of the user for  the virtual sensor
     */
    DataSource getDataSourceForUser(User user,String dataSourceName)throws SQLException
    {
        DataSource ds = null;
        rs = selectAllColumnsUnderTwoConditions("ACUSER_ACDATASOURCE",new Column("USERNAME",user.getUserName()),new Column("DATASOURCENAME",dataSourceName));
        while(rs.next())
        {
           ds = new DataSource(dataSourceName,rs.getString("DATASOURCETYPE"));                                      
        }
        return ds;
    }
    /* given username, returns an object user with other fields */
    User getUserForUserName(String userName)throws SQLException
    {
        User user=new User(userName);
        rs = this.selectAllColumnsUnderOneCondition("ACUSER",new Column("USERNAME",userName));
        while(rs.next())
        {
            user.setFirstName(rs.getString("FIRSTNAME"));
            user.setLastName(rs.getString("LASTNAME"));
            user.setEmail(rs.getString("EMAIL"));
        }
        return user;
    }

    /* given datasourcename, returns an object user that owns this source */
    User getUserFromDataSource(String datasourcename)throws SQLException
    {
        String query="SELECT USERNAME FROM ACUSER_ACDATASOURCE WHERE DATASOURCENAME ='"+datasourcename+"'";    // get the username for this user
        rs = stmt.executeQuery(query);
        rs.next();
        User user=new User(rs.getString("USERNAME"));                                     // create what will be returned
        query="SELECT * FROM ACUSER WHERE USERNAME ='"+rs.getString("USERNAME")+"'";      // initialize the rest of the information
        rs = stmt.executeQuery(query);
        while(rs.next())
        {
            user.setFirstName(rs.getString("FIRSTNAME"));
            user.setLastName(rs.getString("LASTNAME"));
            user.setEmail(rs.getString("EMAIL"));
        }
        return user;
    }

    /****************************************** DB register methods *********************************************/
   /************************************************************************************************************/

    /* regiser a virtual sensor group into DB */
    boolean registerGroup(Group group)throws Exception
    {
        boolean success=false;
        DataSource ds=null;
		if(this.insertOneColumnValue(new Column("GROUPNAME",group.getGroupName()),"ACGROUP")== true)
		{
            success=true;
			int i=0;
			while(i<group.getDataSourceList().size()&& (success==true))
			{
				ds = (DataSource)(group.getDataSourceList().get(i));
                if(insertThreeColumnsValues(new Column("GROUPNAME",group.getGroupName()),new Column("DATASOURCENAME",ds.getDataSourceName()),new Column("DATASOURCETYPE",ds.getDataSourceType()),"ACGROUP_ACDATASOURCE")==false)
                {
                    success =false;
                }
                i++;
			}

        }
        return success;

    }

    /* register a prospective virtual sensor as a virtual sensor of this GSN server */
     boolean registerDataSourceCandidate(DataSource ds)throws Exception
    {
        boolean success=false;
		if(this.insertTwoColumnsValues(new Column("DATASOURCENAME",ds.getDataSourceName()),new Column("ISCANDIDATE",ds.getIsCandidate()),"ACDATASOURCE")== true)
		{
            if(registerDataSourceForUser(ds.getOwner(),ds)==true)
            {
                success=true;
            }
        }
        return success;

    }

    /* register a prospective user as GSN AC user */
    boolean registerUserCandidate(User user)throws SQLException
    {
        boolean success=false;
        Group group = null;
         if(insertSixColumnsValues(new Column("USERNAME",user.getUserName()),new Column("PASSWORD",user.getPassword()),new Column("FIRSTNAME",user.getFirstName()),new Column("LASTNAME",user.getLastName()),new Column("EMAIL",user.getEmail()),new Column("ISCANDIDATE",user.getIsCandidate()),"ACUSER")== true)
		{
            success=true;
            int i=0;
			while(i<user.getGroupList().size()&& (success==true))
            {
               group=(Group)user.getGroupList().get(i);
               if(registerGroupForUser(user,group)== false)
              {
                  success =false;
              }
                i++;
            }
        }
        return success;
    }


   /* register this group as a group to which this user has access to */
    boolean  registerGroupForUser(User user,Group group) throws SQLException
    {
        return this.insertFourColumnsValues(new Column("USERNAME",user.getUserName()),new Column("GROUPNAME",group.getGroupName()),new Column("GROUPTYPE",group.getGroupType()),new Column("ISUSERWAITING",user.getIsWaiting()),"ACUSER_ACGROUP");

    }
    /* register this DataSource(virtual sensor, acces right) for this user*/
    boolean  registerDataSourceForUser(User user,DataSource ds) throws SQLException
    {
        return this.insertSeventhColumnsValues(new Column("USERNAME",user.getUserName()), new Column("DATASOURCENAME",ds.getDataSourceName()),  new Column("DATASOURCETYPE",ds.getDataSourceType()), new Column("FILENAME",ds.getFileName()),new Column("FILETYPE",ds.getFileType()),new Column("PATH",ds.getPath()),new Column("ISUSERWAITING",user.getIsWaiting()),"ACUSER_ACDATASOURCE");

    }

    /* put this DataSource(virtual sensor, access right) in this group */
    boolean  registerDataSourceForGroup(Group group,DataSource ds) throws SQLException
    {
        return this.insertThreeColumnsValues(new Column("GROUPNAME",group.getGroupName()), new Column("DATASOURCENAME",ds.getDataSourceName()),  new Column("DATASOURCETYPE",ds.getDataSourceType()),"ACGROUP_ACDATASOURCE");

    }
    /****************************************** DB update methods *********************************************/
   /************************************************************************************************************/
    boolean updateOneColumnUnderOneCondition(Column col,Column cond,String tableName)throws SQLException
   {
       String query = "UPDATE "+tableName+" SET "+col.columnLabel+"= '"+col.columnValue+"' WHERE "+cond.columnLabel+"= '"+cond.columnValue+"'";
       if(stmt.executeUpdate(query) !=0)
           return true;
       else
           return false;
   }
    boolean updateOneColumnUnderTwoConditions(Column col,Column firstCond,Column secondCond,String tableName)throws SQLException
   {
       String query = "UPDATE "+tableName+" SET "+col.columnLabel+"= '"+col.columnValue+"' WHERE "+firstCond.columnLabel+"= '"+firstCond.columnValue+"' AND "+secondCond.columnLabel+"= '"+secondCond.columnValue+"'";
       if(stmt.executeUpdate(query) !=0)
           return true;
       else
           return false;
   }

    /* change the combination of a given group */
    void changeGroupCombination(Group group)throws SQLException
    {

        DataSource ds=null;
        this.deleteUnderOneCondition(new Column("GROUPNAME",group.getGroupName()),"ACGROUP_ACDATASOURCE");

            for(int i=0;i<group.getDataSourceList().size();i++)
            {
                ds=(DataSource)group.getDataSourceList().get(i);
                this.registerDataSourceForGroup(group,ds);
            }
    }
    
    void updateGroupForUser(User user,Group group)throws SQLException
    {
        this.updateOneColumnUnderTwoConditions(new Column("ISUSERWAITING",user.getIsWaiting()),new Column("USERNAME",user.getUserName()),new Column("GROUPNAME",group.getGroupName()),"ACUSER_ACGROUP");
        this.updateOneColumnUnderTwoConditions(new Column("GROUPTYPE",group.getGroupType()),new Column("USERNAME",user.getUserName()),new Column("GROUPNAME",group.getGroupName()),"ACUSER_ACGROUP");

    }
    void updateDataSourceForUser(User user,DataSource ds)throws SQLException
    {
        this.updateOneColumnUnderTwoConditions(new Column("ISUSERWAITING",user.getIsWaiting()),new Column("USERNAME",user.getUserName()),new Column("DATASOURCENAME",ds.getDataSourceName()),"ACUSER_ACDATASOURCE");
        this.updateOneColumnUnderTwoConditions(new Column("DATASOURCETYPE",ds.getDataSourceType()),new Column("USERNAME",user.getUserName()),new Column("DATASOURCENAME",ds.getDataSourceName()),"ACUSER_ACDATASOURCE");

    }
    boolean updateUserDetails(User user) throws SQLException
    {
        StringBuilder query = new StringBuilder()
            .append("UPDATE ACUSER SET FIRSTNAME='")
            .append(user.getFirstName())
            .append("', LASTNAME='")
            .append(user.getLastName())
            .append("', EMAIL='")
            .append(user.getEmail())
            .append("', PASSWORD='")
            .append(user.getPassword())
            .append("' WHERE USERNAME='")
            .append(user.getUserName())
            .append("'");
       if(stmt.executeUpdate(query.toString()) !=0)
           return true;
       else
           return false;
    }

    void updateOwnerDecision(String decision, String userName, String dataSourceName )throws SQLException
    {
        this.updateOneColumnUnderTwoConditions(new Column("OWNERDECISION",decision),new Column("DATASOURCENAME",dataSourceName),new Column("USERNAME",userName),"ACUSER_ACDATASOURCE");
    }

    /****************************************** DB delete methods *********************************************/
   /************************************************************************************************************/
    int deleteUnderOneCondition(Column cond,String tableName)throws SQLException
   {
       String query = "DELETE FROM "+tableName+" WHERE "+cond.columnLabel+"= '"+cond.columnValue+"'";
       return stmt.executeUpdate(query);
      
   }
    int deleteUnderTwoConditions(Column firstCond,Column secondCond,String tableName)throws SQLException
   {
       String query = "DELETE FROM "+tableName+" WHERE "+firstCond.columnLabel+"= '"+firstCond.columnValue+"' AND "+secondCond.columnLabel+"= '"+secondCond.columnValue+"'";
       return stmt.executeUpdate(query);
   }
    void deleteUserCandidate(String userName)throws SQLException
    {
        this.deleteUnderOneCondition(new Column("USERNAME",userName),"ACUSER_ACGROUP");
        this. deleteUnderOneCondition(new Column("USERNAME",userName),"ACUSER");
    }
    void deleteDataSourceCandidate(String dataSourceName)throws SQLException
    {
        this.deleteUnderOneCondition(new Column("DATASOURCENAME",dataSourceName),"ACUSER_ACDATASOURCE");
        this. deleteUnderOneCondition(new Column("DATASOURCENAME",dataSourceName),"ACDATASOURCE");
    }
    void deleteGroup(String groupName)throws SQLException
    {
        this.deleteUnderOneCondition(new Column("GROUPNAME",groupName),"ACUSER_ACGROUP");
        this.deleteUnderOneCondition(new Column("GROUPNAME",groupName),"ACGROUP_ACDATASOURCE");
        this.deleteUnderOneCondition(new Column("GROUPNAME",groupName),"ACGROUP");

    }
    void deleteGroupForUser(Group group, User user)throws SQLException
    {
        this.deleteUnderTwoConditions(new Column("GROUPNAME",group.getGroupName()),new Column("USERNAME",user.getUserName()),"ACUSER_ACGROUP");
    }
    void deleteDataSourceForUser(DataSource ds, User user)throws SQLException
    {
        this.deleteUnderTwoConditions(new Column("DATASOURCENAME",ds.getDataSourceName()),new Column("USERNAME",user.getUserName()),"ACUSER_ACDATASOURCE");
    }

    void deleteGroupListsDifferenceForUser(Vector oldGroupList,Vector newGroupList,String username)throws SQLException //for using this method oldVector.size()>= newVector.size()
    {
        Group gr=null;
        Vector grdiffvector=this.getGroupListsDifference(oldGroupList,newGroupList);
        for(int i=0;i<grdiffvector.size();i++)
        {
            gr=(Group)grdiffvector.get(i);
            this.deleteUnderTwoConditions(new Column("USERNAME",username),new Column("GROUPNAME",gr.getGroupName()),"ACUSER_ACGROUP");
        }
    }

    /****************************************** Other Methods*******************************************/
     /***************************************************************************************************/

      void  closeStatement()
      {
          try
          {
              if(this.stmt !=null)
              {
                  stmt.close();
              }
          }
          catch(SQLException e)
          {

              logger.error("ERROR IN CLOSESTATEMENT METHOD :SQLException caught ");
			  logger.error(e.getMessage(),e);
          }
      }
      void  closeConnection()
      {
          try
          {
              if(this.con !=null)
              {
                  con.close();
              }
          }
          catch(SQLException e)
          {
              
              logger.error("ERROR IN CLOSECONNECTION METHOD :SQLException caught ");
			  logger.error(e.getMessage(),e);
          }
      }

    public Vector getGroupListsIntersection(Vector firstVector,Vector secondVector)
    {
        Group firstgroup=null;
        Group secondgroup=null;
        Vector intersectionVector= new Vector();

		for(int i=0;i<firstVector.size();i++)
        {
            firstgroup=(Group)firstVector.get(i);
            for(int j=0;j<secondVector.size();j++)
            {
                secondgroup=(Group)secondVector.get(j);
                if(firstgroup.getGroupName().equals(secondgroup.getGroupName()))
                {
                    intersectionVector.add(firstgroup);
                }

            }
        }
        return intersectionVector;
	}

    public Vector getGroupListsDifference(Vector oldVector,Vector newVector)throws SQLException //for using this method oldVector.size()>= newVector.size()
    {
        for(int i=0;i<newVector.size();i++)
        {
            Group newGroup=(Group)newVector.get(i);
            for(int j=0;j<oldVector.size();j++)
            {
                Group oldGroup=(Group)oldVector.get(j);
                if(oldGroup.getGroupName().equals(newGroup.getGroupName()))
                {
                    oldVector.removeElementAt(j);
                }
            }
        }
        return oldVector;

	}
    //New Ones
     public Vector getDataSourceListsIntersection(Vector firstVector,Vector secondVector)throws SQLException
	{
        DataSource firstds=null;
        DataSource secondds=null;
        Vector intersectionVector= new Vector();
        for(int i=0;i<firstVector.size();i++)
        {
            firstds=(DataSource)firstVector.get(i);
            for(int j=0;j<secondVector.size();j++)
            {
                secondds=(DataSource)secondVector.get(j);
                if(firstds.getDataSourceName().equals(secondds.getDataSourceName()))
                {
                    intersectionVector.add(firstds);
                }
            }
        }
        return intersectionVector;
    }
    public Vector getDataSourceListsDifference(Vector oldVector,Vector newVector)throws SQLException //for using this method oldVector.size()>= newVector.size()
    {
        for(int i=0;i<newVector.size();i++)
        {
            DataSource newDS=(DataSource)newVector.get(i);
            for(int j=0;j<oldVector.size();j++)
            {
                DataSource oldDS=(DataSource)oldVector.get(j);
                if(oldDS.getDataSourceName().equals(newDS.getDataSourceName()))
                {
                    oldVector.removeElementAt(j);
                }
            }
        }
        return oldVector;


	}

    public void checkVSDuration(String username) throws SQLException
    {
        Date dateNow = new Date(); // get the current date
        SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder now = new StringBuilder( dateformatYYYYMMDD.format( dateNow ) );

        String query="SELECT * FROM ACACCESS_DURATION WHERE USERNAME ='"+username+"'";
        Statement stmt2 = con.createStatement();
        ResultSet rs2 = stmt2.executeQuery(query);         // get the sources that are used by this user and belong to other users

        while(rs2.next())
        {
            logger.warn("Source  "+ rs2.getString("DATASOURCENAME")+ " has time "+rs2.getString("DEADLINE"));
            if (now.toString().compareTo(rs2.getString("DEADLINE")) > 0) {   // if the current day is after the set deadline
                logger.warn("Will be deleted "+ rs2.getString("DATASOURCENAME"));
                Column column1 = new Column("USERNAME", username);
                Column column2 = new Column("DATASOURCENAME", rs2.getString("DATASOURCENAME"));
                deleteUnderTwoConditions(column1, column2,"ACACCESS_DURATION");           // delete the entries from the respective arrays
                deleteUnderTwoConditions(column1, column2,"ACUSER_ACDATASOURCE");
                // Send email informing the user
                Emailer email = new Emailer();
                User userFromBD = getUserForUserName(username); // get the details for the Admin account
                String msgHead = "Dear "+ userFromBD.getFirstName() + " " + userFromBD.getLastName() +", "+"\n"+"\n";
                String msgTail = "Best Regards,"+"\n"+"GSN Team";
                String msgBody = "We would like to inform you that you no longer have access to the Virtual Sensor: " + column2.columnValue+
                         "\n\nYou can manage your Virtual Sensors by choosing the following options in GSN:\n"+
                        "Access Rights Management -> User Account Management -> Update Access Rights Form\n"+
                        "or via the URL:{sitename}/gsn/MyUserUpdateServlet\n\n";
                // first change Emailer class params to use sendEmail
                email.sendEmail( "GSN ACCESS ", "GSN USER",userFromBD.getEmail(),"Update for a Virtual Sensor access", msgHead, msgBody, msgTail);
            }
        }
    }
}
