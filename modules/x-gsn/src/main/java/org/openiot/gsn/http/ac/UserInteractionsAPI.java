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
*/

package org.openiot.gsn.http.ac;

import org.apache.log4j.Logger;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 14, 2010
 * Time: 1:51:47 PM
 * To change this template use File | Settings | File Templates.
 */

/* This class helps to use access control functionalities for a user without using the Web application user interface */
public class UserInteractionsAPI
{
    private static UserInteractionsAPI singleton = new UserInteractionsAPI();
    private static transient Logger logger                             = Logger.getLogger( UserInteractionsAPI.class );

    public static UserInteractionsAPI getInstance()
    {
        return singleton;
    }
    public boolean isUserAdmin(User user)
    {
       return user.isAdmin();
    }

    public boolean canReadVirtualSensor(User user, String virtualsensorname)
    {
        return user.hasReadAccessRight(virtualsensorname);
    }
    public boolean canWriteIntoVirtualSensor(User user, String virtualsensorname)
    {
        return user.hasWriteAccessRight(virtualsensorname);
    }
    public boolean canReadWriteVirtualSensor(User user, String virtualsensorname)
    {
        return user.hasReadWriteAccessRight(virtualsensorname);
    }
    public boolean isOwnerOfVirtualSensor(User user, String virtualsensorname)
    {
        return user.hasOwnAccessRight(virtualsensorname);
    }
    public Vector getVirtualSensorListForUser(User user)
    {
        return user.getDataSourceList();
    }
    public Vector getGroupListForUser(User user)
    {
        return user.getGroupList();
    }
    public boolean hasAccessToGroup(User user, String groupname)
    {
        Vector vector = user.getGroupList();
        Group gr=null;
        boolean hasAccess=false;
        for(int i=0;i<vector.size();i++)
        {
            gr = (Group)vector.get(i);
            if(gr.getGroupName().equals(groupname))
            {
                hasAccess = true;
            }
        }
        return hasAccess;
    }
    public String getUserAccessRightForVirtualSensor(User user, String virtualsensorname)
    {
        Vector vector  =user.getDataSourceList();
        DataSource ds=null;
        String accessRight="no access";
        for(int i=0; i<vector.size();i++)
        {
            ds = (DataSource)vector.get(i);
            if(ds.getDataSourceName().equals(virtualsensorname))
            {
                accessRight = ds.getDataSourceType();
            }
        }
        return accessRight;
    }

    public void changeAccessRightForVirtualSensor(User user, String virtualsensorname, String newAccessRight)
    {
        String oldAccessRight = getUserAccessRightForVirtualSensor(user, virtualsensorname);
        if(oldAccessRight.equals("no access"))
        {
            System.out.println("User does not have any access right to the virtual sensor");

        }
        else
        {
            ConnectToDB ctdb= null;
            try
            {
                ctdb = new ConnectToDB();
               if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("USERNAME",user.getUserName()),new Column("DATASOURCENAME",virtualsensorname), "ACUSER_ACDATASOURCE")==false)
                {
                    user.setIsWaiting("yes");
                    DataSource oldDataSource = new DataSource(virtualsensorname,oldAccessRight);
                    oldDataSource.setDataSourceType(oldDataSource.getDataSourceType().charAt(0)+ newAccessRight);
                    oldDataSource.setOwnerDecision("notreceived");
                    ctdb.updateDataSourceForUser(user,oldDataSource);
                }
                else
               {
                   System.out.println("this user is waiting for updates, no update is possible !");
               }
            }
            catch(Exception e)
            {

                logger.error("ERROR IN changeAccessRightForVirtualSensor");
			    logger.error(e.getMessage(),e);

            }
            finally
            {
                if(ctdb!=null)
                {
                    ctdb.closeStatement();
                    ctdb.closeConnection();
                }
            }
        }

    }


    public void removeAccessRightForVirtualSensor(User user, String virtualsensorname)
    {
        String oldAccessRight = getUserAccessRightForVirtualSensor(user, virtualsensorname);
        if(oldAccessRight.equals("no access"))
        {
            System.out.println("User does not have any access right to the virtual sensor");
        }
        else
        {
            ConnectToDB ctdb= null;
            try
            {
                ctdb = new ConnectToDB();
               if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("USERNAME",user.getUserName()),new Column("DATASOURCENAME",virtualsensorname), "ACUSER_ACDATASOURCE")==false)
                {
                    String newAccessRight="0";
                    user.setIsWaiting("yes");
                    DataSource oldDataSource = new DataSource(virtualsensorname,oldAccessRight);
                    oldDataSource.setDataSourceType(oldDataSource.getDataSourceType().charAt(0)+ newAccessRight);
                    oldDataSource.setOwnerDecision("notreceived");
                    ctdb.updateDataSourceForUser(user,oldDataSource);
                }
                else
               {
                   System.out.println("this user is waiting for updates, no update is possible !");
               }
            }
            catch(Exception e)
            {
                 logger.error("ERROR IN removeAccessRightForVirtualSensor");
			    logger.error(e.getMessage(),e);
            }
            finally
            {
                if(ctdb!=null)
                {
                    ctdb.closeStatement();
                    ctdb.closeConnection();
                }
            }
        }

    }

    public void addAccessRightForVirtualSensor(User user, String virtualsensorname, String accessRight)
    {
        String oldAccessRight = getUserAccessRightForVirtualSensor(user, virtualsensorname);
        if(oldAccessRight.equals("no access")== false)
        {
            System.out.println("User has already access to the virtual sensor");
        }
        else
        {
            ConnectToDB ctdb= null;
            try
            {
                ctdb = new ConnectToDB();
               if(ctdb.valueExistsForThisColumnUnderOneCondition(new Column("DATASOURCENAME", virtualsensorname),new Column("ISCANDIDATE","no"),"ACDATASOURCE"))
               {
                   if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("USERNAME",user.getUserName()),new Column("DATASOURCENAME",virtualsensorname), "ACUSER_ACDATASOURCE")==false)
                    {
                        user.setIsWaiting("yes");
                        DataSource newDataSource = new DataSource(virtualsensorname,"5"+accessRight);
                        newDataSource.setOwnerDecision("notreceived");
                        ctdb.registerDataSourceForUser(user,newDataSource);
                    }
                    else
                    {
                        System.out.println("this user is waiting for updates, no update is possible !");
                    }
               }
                else
               {
                   System.out.println("The virtual sensor does not exist!");
               }
            }
            catch(Exception e)
            {
                   logger.error("ERROR IN addAccessRightForVirtualSensor");
			       logger.error(e.getMessage(),e);
            }
            finally
            {
                if(ctdb!=null)
                {
                    ctdb.closeStatement();
                    ctdb.closeConnection();
                }
            }
        }

    }
    public void removeAccessToGroup(User user, String groupname)
    {
        if(hasAccessToGroup(user, groupname)== false)
        {
            System.out.println("User does not have access to the group");
        }
        else
        {
            ConnectToDB ctdb= null;
            try
            {
                ctdb = new ConnectToDB();

                if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("GROUPNAME",groupname),new Column("USERNAME",user.getUserName()),"ACUSER_ACGROUP")==false)
                {
                    Group group=new Group(groupname);
                    group.setGroupType("0");
                    user.setIsWaiting("yes");
                    ctdb.updateGroupForUser(user,group);

                }
                else
               {
                   System.out.println("this user is waiting for updates, no update is possible !");
               }
            }
            catch(Exception e)
            {
                logger.error("ERROR IN removeAccessToGroup");
			       logger.error(e.getMessage(),e);
            }
            finally
            {
                if(ctdb!=null)
                {
                    ctdb.closeStatement();
                    ctdb.closeConnection();
                }
            }

        }
        
    }

    public void applyForAccessToGroup(User user, String groupname)
    {
        if(hasAccessToGroup(user, groupname)== true)
        {
            System.out.println("User has already access to the group");
        }
        else
        {
            ConnectToDB ctdb= null;
            try
            {
                ctdb = new ConnectToDB();
                 if(ctdb.valueExistsForThisColumn(new Column("GROUPNAME",groupname), "ACGROUP")== false)
                {
                   System.out.println("Group does not exist !");
                }
                else
                 {
                     if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("GROUPNAME",groupname),new Column("USERNAME",user.getUserName()),"ACUSER_ACGROUP")==false)
                     {
                         Group group=new Group(groupname);
                         group.setGroupType("5");
                         user.setIsWaiting("yes");
                         ctdb.registerGroupForUser(user,group);
                     }
                    else
                    {
                        System.out.println("this user is waiting for updates, no update is possible !");
                    }
                 }
            }
            catch(Exception e)
            {
                logger.error("ERROR IN applyForAccessToGroup");
			   logger.error(e.getMessage(),e);
            }
            finally
            {
                if(ctdb!=null)
                {
                    ctdb.closeStatement();
                    ctdb.closeConnection();
                }
            }

        }

    }


}
