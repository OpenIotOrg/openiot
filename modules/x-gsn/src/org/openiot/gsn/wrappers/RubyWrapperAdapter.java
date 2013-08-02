package org.openiot.gsn.wrappers;

public abstract class RubyWrapperAdapter  extends AbstractWrapper{

  public boolean initialize() {
    return bootstrap();
  }

  public void dispose() {
    shutdown();
  }

  public abstract  boolean bootstrap() ;
  
  public abstract void shutdown();

}
