package org.openiot.gsn.http.ac;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 16, 2010
 * Time: 6:35:18 PM
 * To change this template use File | Settings | File Templates.
 */

/*
This class defines a column of a DB table:
 */
    
public class Column
{
    protected String columnLabel;//name of the column
    protected String columnValue; // value of the column

    public Column(String columnLabel)
    {
        this.columnLabel=columnLabel;
    }
    public Column(String columnLabel, String columnValue)
    {
        this.columnLabel=columnLabel;
        this.columnValue=columnValue;
    }
}
