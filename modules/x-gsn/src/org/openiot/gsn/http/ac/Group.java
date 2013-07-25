package org.openiot.gsn.http.ac;


import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 12, 2010
 * Time: 5:01:58 PM
 * To change this template use File | Settings | File Templates.
 */

/* A group object basically composed of a list of DataSource objects */
public class Group
{
    private String groupName;
    private String groupType="n"; // group type defines the statut of a group as (add :if a user wants to add the group but still waits for Admin decision, delete: if a user wants to delete a group but still waits for Admin decision, n : normal)
    private Vector dataSourceList;

     /****************************************** Constructors*******************************************/
    /*************************************************************************************************/

    public Group(String groupName,Vector dataSourceList)
	{
		this.groupName = groupName;
		this.dataSourceList = dataSourceList;
	}
    public Group(String groupName,String groupType,Vector dataSourceList)
    {
        this.groupName = groupName;
        this.groupType = groupType;
		this.dataSourceList = dataSourceList;
    }
    public Group(String groupName)
	{
		this.groupName = groupName;
		
	}
    public Group(String groupName, String groupType)
	{
		this.groupName = groupName;
        this.groupType = groupType;

	}

     /****************************************** AC Methods********************************************/
    /*************************************************************************************************/

    /* given the name of a virtual sensor, returns true if at least one of the DataSources in the group has read access right for it */
    public boolean hasReadAccessRight(String srname)
	{
		DataSource dataSource =null;
		boolean found=false;
		int i=0;

		while(i<this.dataSourceList.size()&& found==false)
		{
			dataSource = (DataSource)this.dataSourceList.get(i);
			if(dataSource.hasReadAccessRight(srname)== true)
			{
				found=true;
			}
			i++;

		}
		return found;

	}

    /* given the name of a virtual sensor, returns true if at least one of the DataSources in the group has write access right for it */
    public boolean hasWriteAccessRight(String srname)
	{
		DataSource dataSource =null;
		boolean found=false;
		int i=0;

		while(i<this.dataSourceList.size()&& found==false)
		{
			dataSource = (DataSource)this.dataSourceList.get(i);
			if(dataSource.hasWriteAccessRight(srname)== true)
			{
				found=true;
			}
			i++;

		}
		return found;

	}

    /* given the name of a virtual sensor, returns true if at least one of the DataSources in the group has resd/write access right for it */  
    public boolean hasReadWriteAccessRight(String srname)
	{
		DataSource dataSource =null;
		boolean found=false;
		int i=0;

		while(i<this.dataSourceList.size()&& found==false)
		{
			dataSource = (DataSource)this.dataSourceList.get(i);
			if(dataSource.hasReadWriteAccessRight(srname)== true)
			{
				found=true;
			}
			i++;

		}
		return found;

	}
    /****************************************** Set Methods*******************************************/
   /*************************************************************************************************/

   void setGroupName(String groupName)
   {
       this.groupName=groupName;
   }
   void setGroupType(String groupType)
   {
       this.groupType=groupType;
   }
   void setDataSourceList(Vector dataSourceList)
   {
       this.dataSourceList=dataSourceList;
   }

   /****************************************** Get Methods*******************************************/
   /*************************************************************************************************/

   String getGroupName()
   {
       return this.groupName;
   }
   String getGroupType()
   {
       return this.groupType;
   }
   Vector getDataSourceList()
   {
       return this.dataSourceList;
   }

}
