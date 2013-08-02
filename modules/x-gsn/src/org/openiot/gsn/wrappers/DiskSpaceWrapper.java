package org.openiot.gsn.wrappers;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;

public class DiskSpaceWrapper extends AbstractWrapper{
    
    private static final int            DEFAULT_SAMPLING_RATE       = 1000;
    
    private int                         samplingRate                = DEFAULT_SAMPLING_RATE;
    
    private final transient Logger      logger                      = Logger.getLogger(DiskSpaceWrapper.class);
    
    private static int                  threadCounter               = 0;
    
    private transient DataField[]       outputStructureCache        = new DataField[]{new DataField("FREE_SPACE", "bigint", "Free Disk Space")};

    private File[] roots;
    
      
    public boolean initialize() {
        logger.info("Initializing DiskSpaceWrapper Class");
        String javaVersion = System.getProperty("java.version");
        if(!javaVersion.startsWith("1.6")){
            logger.error("Error in initializing DiskSpaceWrapper because of incompatible jdk version: " + javaVersion + " (should be 1.6.x)");
            return false;
        }
        setName("DiskSpaceWrapper-Thread" + (++threadCounter));
        return true;
    }
    
    public void run(){
        while(isActive()){
            try{
                Thread.sleep(samplingRate);
            }catch (InterruptedException e){
                logger.error(e.getMessage(), e);
            }
            roots = File.listRoots();
            long totalFreeSpace = 0;
            for (int i = 0; i < roots.length; i++) {
                totalFreeSpace += roots[i].getFreeSpace();
            }
            
            //convert to MB
            totalFreeSpace = totalFreeSpace / (1024 * 1024);
            StreamElement streamElement = new StreamElement(new String[]{"FREE_SPACE"}, new Byte[]{DataTypes.BIGINT}, new Serializable[] {totalFreeSpace
            },System.currentTimeMillis());
            postStreamElement(streamElement);
        }
    }
    
    public void dispose() {
        threadCounter--;
    }
    
    public String getWrapperName() {
        return "Free Disk Space";
    }
    
    public DataField[] getOutputFormat() {
        return outputStructureCache;
    }
    
}
