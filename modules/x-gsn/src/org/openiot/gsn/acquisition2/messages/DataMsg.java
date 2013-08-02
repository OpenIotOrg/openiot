package org.openiot.gsn.acquisition2.messages;

public class DataMsg extends AbstractMessage {

  private static final long serialVersionUID = 6707634030386675571L;

  private Object[] data;

  private long sequenceNumber = -1;
  
private long created_at = -1;
  
  public long getSequenceNumber() {
    return sequenceNumber;
  }

  public Object[] getData() {
    return data;
  }

  public long getCreated_at() {
    return created_at;
  }

  public DataMsg(Object[] data,long seqNo,long created_at) {
    this.data = data;
    this.sequenceNumber=seqNo;
    this.created_at = created_at;
  }
  
}
