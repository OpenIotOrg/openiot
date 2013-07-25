package org.openiot.gsn.http.ac;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

/* a user object has many attributees, for ex. a list of groups to which it has access, a list of virtual sensor it has access, etc */
public class User
{

    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Vector groupList;
	private Vector dataSourceList;
    private DataSource dataSource;
    private String isCandidate="no";// if isCandidte = yes, it means that user has alredy signed-up and is waiting for Admin  confirmation to become a real user */
    private String isWaiting="no";// if isWaiting = yes, it means that user has modified(added, changed, deleted) his access right for a virtual sensor or a group and waits for Owner/Admin decision
     private static transient Logger logger                             = Logger.getLogger( User.class );


    /****************************************** Constructors*******************************************/
    /*************************************************************************************************/

    public User(String userName, String password, Vector dataSourceList,Vector groupList)
	{
		this.userName = userName;
        this.password = password;
		this.dataSourceList = dataSourceList;
        this.groupList = groupList;
	}
    public User(String userName,String password,String firstName,String lastName,String email,Vector groupList,String isCandidate)
    {
        this.userName = userName;
        this.password = password;
		this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.groupList = groupList;
        this.isCandidate=isCandidate;

    }
    public User(String userName)
    {
        this.userName=userName;
    }

    public User (User cuser)
    {
        
        this.userName = cuser.userName;
        this.password = cuser.password;
		this.firstName = cuser.firstName;
        this.lastName = cuser.lastName;
        this.email = cuser.email;
        this.groupList = cuser.groupList;
        this.isCandidate = cuser.isCandidate;
        this.dataSourceList = cuser.dataSourceList;
        this.dataSource = cuser.dataSource;
        this.isCandidate = cuser.isCandidate;
        this.isWaiting = cuser.isWaiting;
    }

    public User(String userName,DataSource dataSource)
    {
        this.userName =  userName;
        this.dataSource= new DataSource(dataSource.getDataSourceName(),dataSource.getDataSourceType());
    }
    /****************************************** Set Methods*******************************************/
   /*************************************************************************************************/

   void setFirstName(String firstName)
   {
       this.firstName=firstName;
   }
   void setLastName(String lastName)
   {
       this.lastName=lastName;
   }
   void setEmail(String email)
   {
       this.email=email;
   }
   void setUserName(String userName)
   {
       this.userName=userName;
   }
   void setPassword(String password)
   {
       this.password=password;
   }
   void setGroupList(Vector groupList)
   {
       this.groupList=groupList;
   }
   void setDataSourceList(Vector dataSourceList)
   {
       this.dataSourceList=dataSourceList;
   }

    void setIsCandidate(String isCandidate)
   {
       this.isCandidate=isCandidate;
   }
    void setIsWaiting(String isWaiting)
    {
        this.isWaiting=isWaiting;
    }

    void setDataSource(DataSource dataSource)
    {
        this.dataSource=dataSource;
    }



   /****************************************** Get Methods*******************************************/
   /*************************************************************************************************/
   String getFirstName()
   {
       return this.firstName;
   }
   String getLastName()
   {
       return this.lastName;
   }
   String getEmail()
   {
       return this.email;
   }
   public String getUserName()
   {
       return this.userName;
   }
   public String getPassword()
   {
       return this.password;
   }
   Vector getGroupList()
   {
       return this.groupList;
   }
   Vector getDataSourceList()
   {
       return this.dataSourceList;
   }
    String getIsCandidate()
   {
       return this.isCandidate;
   }
    String getIsWaiting()
    {
        return this.isWaiting;
    }

    DataSource getDataSource()
    {
        return this.dataSource;
    }


   
     /****************************************** User AC Methods********************************************/
    /*************************************************************************************************/

    /* given the name of a virtual sensor, checks if the user can read its output stream */
    public boolean hasReadAccessRight(String srname)
    {
        DataSource dataSource =null;
		boolean found=false;

        if(groupListHasReadAccessRight(srname)==true)
        {
            found=true;    
        }
        else
        {
            found = DataSourceListHasReadAccessRight(srname);
        }

		return found || ! DataSource.isVSManaged(srname);
	}
    public boolean hasWriteAccessRight(String srname)
    {
        DataSource dataSource =null;
		boolean found=false;

        if(groupListHasWriteAccessRight(srname)==true)
        {
            found=true;
        }
        else
        {
            found = DataSourceListHasWriteAccessRight(srname);
        }

		return found;
	}
    public boolean hasReadWriteAccessRight(String srname)
    {
        DataSource dataSource =null;
		boolean found=false;

        if(groupListHasReadWriteAccessRight(srname)==true)
        {
            found=true;
        }
        else
        {
            found = DataSourceListHasReadWriteAccessRight(srname);
        }

		return found;
	}

     public boolean hasOwnAccessRight(String srname)
    {
        return DataSourceListHasOwnAccessRight(srname);

    }


    /****************************************** UserGroupList AC Methods********************************************/
    /*************************************************************************************************/



    public boolean groupListHasReadAccessRight(String srname)
    {
        int i=0;
        boolean found=false;
        while(i<this.groupList.size()&& found==false)
        {
            Group gr= (Group) this.groupList.get(i);
            if(gr.hasReadAccessRight(srname)== true)
            {
                found=true;
            }
            i++;

        }
        return found;
    }
    public boolean groupListHasWriteAccessRight(String srname)
    {
        int i=0;
        boolean found=false;
        while(i<this.groupList.size()&& found==false)
        {
            Group gr= (Group) this.groupList.get(i);
            if(gr.hasWriteAccessRight(srname)== true)
            {
                found=true;
            }
            i++;

        }
        return found;
    }
    public boolean groupListHasReadWriteAccessRight(String srname)
    {
        int i=0;
        boolean found=false;
        while(i<this.groupList.size()&& found==false)
        {
            Group gr= (Group) this.groupList.get(i);
            if(gr.hasReadWriteAccessRight(srname)== true)
            {
                found=true;
            }
            i++;

        }
        return found;
    }

    /****************************************** UserDataSourceList AC Methods********************************************/
    /*************************************************************************************************/



    public boolean DataSourceListHasReadAccessRight(String srname)
    {
        int i=0;
        boolean found=false;
        while(i<this.dataSourceList.size()&& found==false)
        {
            DataSource dataSource = (DataSource)this.dataSourceList.get(i);
            if(dataSource.hasReadAccessRight(srname)== true)
            {
                found=true;
            }
            i++;

        }
        return found;
    }
    public boolean DataSourceListHasWriteAccessRight(String srname)
    {
        int i=0;
        boolean found=false;
        while(i<this.dataSourceList.size()&& found==false)
        {
            DataSource dataSource = (DataSource)this.dataSourceList.get(i);
            if(dataSource.hasWriteAccessRight(srname)== true)
            {
                found=true;
            }
            i++;

        }
        return found;
    }
    public boolean DataSourceListHasReadWriteAccessRight(String srname)
    {
        int i=0;
        boolean found=false;
        while(i<this.dataSourceList.size()&& found==false)
        {
            DataSource dataSource = (DataSource)this.dataSourceList.get(i);
            if(dataSource.hasReadWriteAccessRight(srname)== true)
            {
                found=true;
            }
            i++;

        }
        return found;
    }
    public boolean DataSourceListHasOwnAccessRight(String srname)
    {
        int i=0;
        boolean found=false;
        while(i<this.dataSourceList.size()&& found==false)
        {
            DataSource dataSource = (DataSource)this.dataSourceList.get(i);
            if(dataSource.hasOwnAccessRight(srname)== true)
            {
                found=true;
            }
            i++;

        }
        return found;
    }
    /****************************************** General AC Methods********************************************/
    /*************************************************************************************************/

    public boolean isAdmin()
    { 
        boolean userIsAdmin=false;
        if(this.userName.equals("Admin") )
        {
            ConnectToDB ctdb=null;
            try
            {
                ctdb=new ConnectToDB();
                if(ctdb.isPasswordCorrectForThisUser(this.userName,this.password)== true)
                {
                    userIsAdmin=true;
                }
            }
            catch(ClassNotFoundException e)
		    {

                logger.error("ERROR IN ISADMIN :Could not load database driver  ");
			    logger.error(e.getMessage(),e);
		    }
		    catch(SQLException e)
		    {
			    System.out.println(" ERROR IN ISADMIN : SQLException caught : ");
			    while((e = e.getNextException())!= null )
			    {
				    System.out.println(e.getMessage());
			    }
            }
            catch(Exception e)
            {
                System.out.println("Exception caught :"+e.getLocalizedMessage());
            }
            finally
            {
                ctdb.closeStatement();
                ctdb.closeConnection();
            }

        }
        return userIsAdmin;
    }
    


}