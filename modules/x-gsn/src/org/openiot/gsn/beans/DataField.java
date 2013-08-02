package org.openiot.gsn.beans;

import org.openiot.gsn.utils.GSNRuntimeException;

import java.io.Serializable;

public final class DataField implements Serializable {

    private static final long serialVersionUID = -8841539191525018987L;

    private String            description      = "Not Provided";

    private String            name;

    private byte               dataTypeID       = -1;

    private String            type;

    private DataField ( ) {}

    public DataField ( final String fieldName , final String type , final String description ) throws GSNRuntimeException {
        this.name = fieldName;
        this.type = type;
        this.dataTypeID = DataTypes.convertTypeNameToGSNTypeID( type );
        this.description = description;
    }

    /*
   * Use this constructor only with types which require precision parameter (char, varchar, blob, binary)
   * */
    public DataField ( final String fieldName , final String type, final int precision , final String description ) throws GSNRuntimeException {
        this.name = fieldName;
        this.type = type +"("+precision+")";
        this.dataTypeID = DataTypes.convertTypeNameToGSNTypeID( this.type );
        this.description = description;
    }

    public DataField ( final String name , final String type ) {
        this.name = name;
        this.type = type;
        this.dataTypeID = DataTypes.convertTypeNameToGSNTypeID( type );
    }

    public DataField(String colName,byte dataTypeID) {
        this.name=colName;
        this.dataTypeID = dataTypeID;
        this.type = DataTypes.TYPE_NAMES[this.dataTypeID];
    }

    public String getDescription ( ) {
        return this.description;
    }
    transient boolean fieldNameConvertedToLowerCase = false;
    public String getName ( ) {
        if (fieldNameConvertedToLowerCase==false) {
            fieldNameConvertedToLowerCase=true;
            this.name=name.toLowerCase( );
        }
        return this.name;
    }

    public boolean equals ( final Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof DataField ) ) return false;

        final DataField dataField = ( DataField ) o;
        if ( this.name != null ? !this.name.equals( dataField.name ) : dataField.name != null ) return false;
        return true;
    }

    /**
     * @return Returns the dataTypeID.
     */
    public byte getDataTypeID ( ) {
        if ( this.dataTypeID == -1 ) this.dataTypeID = DataTypes.convertTypeNameToGSNTypeID( this.type );
        return this.dataTypeID;
    }

    public int hashCode ( ) {
        return ( this.name != null ? this.name.hashCode( ) : 0 );
    }

    public String toString ( ) {
        final StringBuilder result = new StringBuilder( );
        result.append( "[Field-Name:" ).append( this.name ).append( ", Type:" ).append( DataTypes.TYPE_NAMES[ this.getDataTypeID( ) ] ).append( "[" + this.type + "]" ).append( ", Description:" )
                .append( this.description ).append( "]" );
        return result.toString( );
    }

    /**
     * @return Returns the type. This method is just used in the web interface
     * for detection the output of binary fields.
     */
    public String getType ( ) {
        return this.type;
    }

}
