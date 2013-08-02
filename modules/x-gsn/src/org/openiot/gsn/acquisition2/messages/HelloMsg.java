package org.openiot.gsn.acquisition2.messages;

import org.openiot.gsn.beans.AddressBean;

public class HelloMsg extends AbstractMessage {

  private static final long serialVersionUID = -4418222946153175066L;

  private boolean continueOnError = false;
  
  private AddressBean wrapperDetails;
  
  private String requster = null;

  public boolean isContinueOnError() {
    return continueOnError;
  }

  public void setContinueOnError(boolean isContinueOnError) {
    this.continueOnError = isContinueOnError;
  }

  public AddressBean getWrapperDetails() {
    return wrapperDetails;
  }

  public void setWrapperDetails(AddressBean wrapperDetails) {
    this.wrapperDetails = wrapperDetails;
  }

  public HelloMsg(AddressBean wrapperDetails,String requester, boolean isContinueOnError) {
    this.wrapperDetails = wrapperDetails;
    this.continueOnError = isContinueOnError;
    this.requster = requester;
  }

  /**
   * Sets the continueOnError to True by default.
   */
  public HelloMsg(AddressBean wrapperDetails,String requester) {
    this.wrapperDetails = wrapperDetails;
    this.continueOnError = true;
    this.requster = requester;
  }

  public String getRequster() {
    return requster;
  }
}
