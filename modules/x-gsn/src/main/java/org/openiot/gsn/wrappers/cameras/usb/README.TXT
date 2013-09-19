The wrapper for OV511/518 wired usb cameras is not distributed in the Ghazal (because of the license incompatibility).
You can find the wrapper (the java files) from the svn repository of GSN at sourceforge.

For using the ovcam511/518 wrapper with linux : 
1. Get the driver from : http://ovcam.org/ov511/ , compile it and install it.
2. Download the Java Media Framework for your platform and install it. At
this stage, the jmf should detect your webcam otherwise try to fix the bug :-)
3. Once Webcam is detected by the jmf, On linux : sudo rmmod ov511; sudo modprobe ovcamchip;sudo modprobe ov511
4. The system environment or the java execution environment(in eclipse).
4. LD_LIBRARY_PATH=/home/ali/download/JMF-2.1.1e/lib:/usr/local/java/jdk1.5.0/jre/lib/i386:/usr/local/java/jdk1.5.0/jre/lib/i386/client:/usr/local/java/jdk1.5.0/jre/lib/i386/xawt
4. LD_PRELOAD=/usr/local/java/jdk1.5.0/jre/lib/i386/libjawt.so
