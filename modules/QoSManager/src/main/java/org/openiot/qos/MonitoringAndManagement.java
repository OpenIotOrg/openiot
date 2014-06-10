package org.openiot.qos;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.proj.coords.MGRSPoint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.common.Triplet;
import org.openiot.cupus.common.enums.Operator;
import org.openiot.cupus.entity.mobilebroker.MobileBroker;
import org.openiot.cupus.util.LogWriter;
import org.openiot.integration.VirtualSensor;

public class MonitoringAndManagement {

    //GSN parameters
    private String gsnAddress;
    private int wrapperPort;

    private HashMap<String, Long> batteryTime = new HashMap<String, Long>();
    private HashMap<String, Integer> virtualSensors = new HashMap<String, Integer>();

    private volatile HashMap<String, Set<HashtablePublication>> publicationsInArea = new HashMap<String, Set<HashtablePublication>>();
    private volatile HashMap<String, Set<String>> sensorsInArea = new HashMap<String, Set<String>>();
    private volatile HashMap<String, Set<String>> activeSensorsInArea = new HashMap<String, Set<String>>();
    private volatile HashMap<String, HashtablePublication> announcedSensors = new HashMap<String, HashtablePublication>();
    private volatile HashMap<String, ArrayList<TripletSubscription>> subscriptionsInArea = new HashMap<String, ArrayList<TripletSubscription>>();
    private volatile HashMap<String, ArrayList<TripletSubscription>> sensorActiveSubs = new HashMap<String, ArrayList<TripletSubscription>>();
    private volatile HashMap<TripletSubscription, Set<String>> subCandidates = new HashMap<TripletSubscription, Set<String>>();

    private Object mutexPublicationsInArea = new Object();
    private Object mutexSensorsInArea = new Object();
    private Object mutexActiveSensorsInArea = new Object();
    private Object mutexAnnouncedSensors = new Object();
    private Object mutexSubscriptionsInArea = new Object();
    private Object mutexSensorActiveSubs = new Object();
    private Object mutexSubCandidates = new Object();

    protected LogWriter log;
    protected QoSLogic qosLogic;
    private MobileBroker qosMB;

    private List<String> sensorParameters;
    private List<String> sensorTypes;
    private List<String> lsmProperty;
    private List<String> lsmUnit;

    public MonitoringAndManagement(LogWriter logger, MobileBroker mb, QoSLogic appLogic, List<String> param, List<String> paramTypes, List<String> lsmProp, List<String> lsmUnits, String gsn, int port) {
        this.log = logger;
        this.qosMB = mb;
        this.qosLogic = appLogic;
        this.sensorParameters = param;
        this.sensorTypes = paramTypes;
        this.lsmProperty = lsmProp;
        this.lsmUnit = lsmUnits;

        this.gsnAddress = gsn;
        this.wrapperPort = port;

        this.startTimer();
    }

    public Set<String> getAllSensorsInArea(String area) {
        synchronized (sensorsInArea) {
            return sensorsInArea.get(area);
        }
    }

    public Set<String> getActiveSensorsInArea(String area) {
        synchronized (activeSensorsInArea) {
            return activeSensorsInArea.get(area);
        }
    }

    public Set<String> getAllCurrentlyKnownSensors() {
        synchronized (announcedSensors) {
            return announcedSensors.keySet();
        }
    }

    public List<TripletSubscription> getAllSubscriptionsInArea(String area) {
        synchronized (subscriptionsInArea) {
            return subscriptionsInArea.get(area);
        }
    }

    public void notify(HashtablePublication sensorPublication) {
        log.writeToLog("--------------------------------------", true);
        if (((String) sensorPublication.getProperties().get("Type")).equalsIgnoreCase("SensorReading")) {
            String area = (String) sensorPublication.getProperties().get("Area");
            String sensorID = (String) sensorPublication.getProperties().get("SensorID");

            // this sensor was already announced in the system
            if (announcedSensors.containsKey(sensorID)) {
                if (!announcedSensors.get(sensorID).getProperties().get("Area").equals(area)) {
                    log.writeToLog("Sensor has sent new announcement. " + sensorPublication + " Remove old announcement in area " + (String) announcedSensors.get(sensorID).getProperties().get("Area") + " and all active subscriptions of a sensor. Remove sensor " + sensorID + " from list of candidates for other subscriptions in the system. ", true);
                    // remove sensor from previous area and check if sensor had active subs in that area, if had remove them
                    synchronized (mutexSensorsInArea) {
                        sensorsInArea.get((String) announcedSensors.get(sensorID).getProperties().get("Area")).remove(sensorID);
                    }
                    removeSensorActiveSubsInPreviousArea((String) announcedSensors.get(sensorID).getProperties().get("Area"), sensorID);
                    batteryTime.put(sensorID, System.currentTimeMillis());
                    qosLogic.setBattery(sensorPublication);
                    // add sensor 			
                    addSensorInArea(area, sensorID);
                } else if (System.currentTimeMillis() - batteryTime.get(sensorID) > 600000) {
                    log.writeToLog("Refresh sensor battery!", true);
                    batteryTime.put(sensorID, System.currentTimeMillis());
                    qosLogic.setBattery(sensorPublication);
                }
            } else {
                // add sensor 			
                addSensorInArea(area, sensorID);
                batteryTime.put(sensorID, System.currentTimeMillis());
                qosLogic.setBattery(sensorPublication);
            }
            //refresh sensor announcement
            synchronized (mutexAnnouncedSensors) {
                announcedSensors.put((String) sensorPublication.getProperties().get("SensorID"), sensorPublication);
            }
            //search possible subscriptions in that area
            matchWithSubscriptionsInArea(sensorPublication, area);
            addPublicationInArea(sensorPublication);

            //send publication to X-GSN
            if (!virtualSensors.containsKey(area)) {
                try {
                    registerVirtualSensor(area);
                } catch (IOException ex) {
                    Logger.getLogger(MonitoringAndManagement.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MonitoringAndManagement.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            sendToXGSN(virtualSensors.get(area), sensorPublication);

        } else if (((String) sensorPublication.getProperties().get("Type")).equalsIgnoreCase("LowBattery")) {
            //napravi nesto s tim
        }
        log.writeToLog("--------------------------------------", true);
    }

    public void notify(TripletSubscription subscription) {
        log.writeToLog("--------------------------------------", true);

        Set<Triplet> type = subscription.attributePredicates("Type");
        for (Triplet t : type) {
            if (t.getValue().equals("AverageReading")) {
                log.writeToLog("Aggregate subscription arrived!", true);
                Set<Triplet> areas = subscription.attributePredicates("Area");
                //new aggregate subscription arrived; subscribe on all readings in all areas it contains
                for (Triplet area : areas) {
                    subscribeOnIndividualReadingsInArea(area.getValue().toString());
                    HashtablePublication pub = getAverageSensorReadingsInArea(area.getValue().toString());
                    if (pub.getProperties().size() != 3) {
                        qosMB.publish(pub);
                    } else {
                        log.writeToLog("No pubs in the system!", true);
                    }
                }
            } else if (t.getValue().equals("SensorReading")) {
                Set<Triplet> areas = subscription.attributePredicates("Area");
                //new subscription arrived; put subscription in all areas it contains
                if (areas != null) {
                    for (Triplet area : areas) {
                        addSubscriptionInArea(area.getValue().toString(), subscription);
                        //search possible sensors in the area
                        matchWithSensorsInArea(subscription, area.getValue().toString());
                    }
                    log.writeToLog("All subscriptions in the system by areas: " + subscriptionsInArea, true);
                    log.writeToLog("All sensors in the system by areas: " + sensorsInArea, true);
                    log.writeToLog("Active sensors by areas: " + activeSensorsInArea, true);
                } else {
                    log.writeToLog("New subscription: " + subscription.toString(), true);
                }
            } else {
                log.writeToLog(subscription.toString(), true);
            }
        }
        log.writeToLog("--------------------------------------", true);
    }

    public HashtablePublication getAverageSensorReadingsInArea(String area) {
        log.writeToLog("--------------------------------------", true);
        log.writeToLog("Calculate average sensor readings in last 30 minutes in specified area", true);

        Set<HashtablePublication> pubsInArea = new HashSet<HashtablePublication>();

        synchronized (mutexPublicationsInArea) {
            if (publicationsInArea.get(area) != null) {
                for (HashtablePublication p : publicationsInArea.get(area)) {
                    if (System.currentTimeMillis() - p.getStartTime() < 1800000) {
                        pubsInArea.add(p);
                    }
                }
            }
        }
        HashtablePublication pub = qosLogic.calculateAverageSensorReadings(area, pubsInArea);
        return pub;
    }

    private void addPublicationInArea(HashtablePublication sensorPublication) {
        //if there are already some pubs in the area, add new one; else add new area and sensor pub in the map publicationsInArea 
        synchronized (mutexPublicationsInArea) {
            if (publicationsInArea.containsKey((String) sensorPublication.getProperties().get("Area"))) {
                publicationsInArea.get((String) sensorPublication.getProperties().get("Area")).add(sensorPublication);
            } else {
                Set<HashtablePublication> sensorPubs = new HashSet<HashtablePublication>();
                sensorPubs.add(sensorPublication);
                publicationsInArea.put((String) sensorPublication.getProperties().get("Area"), sensorPubs);
            }
        }
    }

    private void matchWithSensorsInArea(TripletSubscription subscription, String area) {
        Set<String> possibleSensors = new HashSet<String>();
        Set<String> activeSensors = new HashSet<String>();
        Set<String> activateNewSensors = new HashSet<String>();
        Set<HashtablePublication> sensorsInAreaToRemove = new HashSet<HashtablePublication>();
        Set<String> listOfSensorsInArea = getAllSensorsInArea(area);

        log.writeToLog("--------------------------------------", true);
        log.writeToLog("New subscription arrived! Search sensors in the area " + area, true);

        synchronized (sensorsInArea) {
            if (listOfSensorsInArea != null) {
                for (String sensor : listOfSensorsInArea) {
                    //sensor announcement and subscription have to be valid, sub covers sensor announcement
                    if (subscription.isValid() && announcedSensors.get(sensor).isValid() && subscription.coversPublication(announcedSensors.get(sensor))) {
                        if (activeSensorsInArea.containsKey(area) && activeSensorsInArea.get(area).contains(sensor)) {
                            activeSensors.add(sensor);
                        } else {
                            possibleSensors.add(sensor);
                        }
                    } else {
                        if (!subscription.isValid() && !announcedSensors.get(sensor).isValid()) {
                            removeSubscriptionInArea(subscription);
                            sensorsInAreaToRemove.add(announcedSensors.get(sensor));
                        } else if (!subscription.isValid() && announcedSensors.get(sensor).isValid()) {
                            removeSubscriptionInArea(subscription);
                        } else if (subscription.isValid() && !announcedSensors.get(sensor).isValid()) {
                            sensorsInAreaToRemove.add(announcedSensors.get(sensor));
                        }
                    }
                }
                log.writeToLog("Already active sensors in the area: " + activeSensors, true);
                log.writeToLog("Possible candidates in the area: " + possibleSensors, true);

                activateNewSensors = qosLogic.findBestSensors(activeSensors, possibleSensors);
                if (activateNewSensors != null && activateNewSensors.size() >= activeSensors.size()) {
                    for (String s : activateNewSensors) {
                        //put sub in the list of active subs for every sensor in bestK sensors
                        activateSensorSub(s, subscription);
                        //put bestK sensors in the list of active sensors in the area
                        activateSensorInArea(area, s);
                    }
                    for (String s : possibleSensors) {
                        if (!activateNewSensors.contains(s)) {
                            addSubscriptionCandidate(s, subscription);
                        }
                    }
                    for (String s : activeSensors) {
                        if (!activateNewSensors.contains(s)) {
                            if (checkSensorActiveSubs(s, subscription)) {
                                activateCandidateIfExists(s, subscription, (String) announcedSensors.get(s).getProperties().get("Area"));
                                sensorActiveSubs.remove(s);
                                System.out.println(s);
                                activeSensorsInArea.get(area).remove(s);
                                addSubscriptionCandidate(s, subscription);
                            }
                        }
                    }
                } else if (activateNewSensors != null && activateNewSensors.size() < activeSensors.size()) {
                    for (String s : activateNewSensors) {
                        //put sub in the list of active subs for every sensor in bestK sensors
                        activateSensorSub(s, subscription);
                        //put bestK sensors in the list of active sensors in the area
                        activateSensorInArea(area, s);
                    }
                    for (String s : possibleSensors) {
                        if (!activateNewSensors.contains(s)) {
                            addSubscriptionCandidate(s, subscription);
                        }
                    }
                    for (String s : activeSensors) {
                        if (!activateNewSensors.contains(s)) {

                            sensorActiveSubs.remove(s);
                            System.out.println(s);
                            activeSensorsInArea.get(area).remove(s);
                            addSubscriptionCandidate(s, subscription);
                        }
                    }
                } else if (activateNewSensors == null) {
                    //treba ugasit sve senzore
                    turnOffAllSensors();
                }
            }
        }
        //remove expired announcement of a sensor
        if (!sensorsInAreaToRemove.isEmpty()) {
            for (HashtablePublication pub : sensorsInAreaToRemove) {
                removeAnnouncedSensorInArea(pub);
            }
        }

        log.writeToLog("Active sensors by areas: " + activeSensorsInArea, true);
        log.writeToLog("Active subscriptions by sensor: " + sensorActiveSubs, true);
        log.writeToLog("--------------------------------------", true);

    }

    private void turnOffAllSensors() {
        //makni sve senzore iz aktivnih
        //sve aktivne stavi u kandidate

        synchronized (mutexSensorActiveSubs) {
            for (String sensor : sensorActiveSubs.keySet()) {
                for (TripletSubscription sub : sensorActiveSubs.get(sensor)) {
                    addSubscriptionCandidate(sensor, sub);
                }
            }
            sensorActiveSubs.clear();
        }
        synchronized (mutexActiveSensorsInArea) {
            activeSensorsInArea.clear();
        }
    }

    private void activateCandidateIfExists(String s, TripletSubscription subscription, String area) {
        synchronized (mutexSensorActiveSubs) {
            synchronized (mutexSubCandidates) {
                for (TripletSubscription sub : sensorActiveSubs.get(s)) {
                    if (!sub.equals(subscription) && subCandidates.get(sub) != null) {
                        String activateSensor = qosLogic.activateBestCandidate(subCandidates.get(sub));
                        activateSensorInArea(area, activateSensor);
                        activateSensorSub(activateSensor, sub);
                        subCandidates.get(sub).remove(activateSensor);
                        turnSensorOn(activateSensor);
                    }
                }
            }
        }
    }

    private void matchWithSubscriptionsInArea(HashtablePublication pub, String area) {
        String sensorID = (String) pub.getProperties().get("SensorID");
        ArrayList<TripletSubscription> subsInAreaToRemove = new ArrayList<TripletSubscription>();
        Set<String> newSensors = new HashSet<String>();

        log.writeToLog("--------------------------------------", true);
        log.writeToLog("New sensor publication arrived! Sensor: " + sensorID + " Area: " + area, true);
        log.writeToLog("All subscriptions in the area: " + subscriptionsInArea.get(area), true);

        if (subscriptionsInArea.containsKey(area)) {
            for (TripletSubscription sub : subscriptionsInArea.get(area)) {
                Set<String> activeSensors = new HashSet<String>();
                if (activeSensorsInArea.containsKey(area) && !activeSensorsInArea.get(area).isEmpty()) {
                    for (String s : activeSensorsInArea.get(area)) {
                        if (sensorActiveSubs.containsKey(s) && sensorActiveSubs.get(s).contains(sub) && !sensorActiveSubs.get(s).isEmpty()) {
                            activeSensors.add(s);
                        }
                    }
                }
                if (sub.isValid() && sub.coversPublication(announcedSensors.get(sensorID)) && announcedSensors.get(sensorID).isValid() && !activeSensors.contains(sensorID)) {

                    newSensors = qosLogic.newActiveSensors(activeSensors, sensorID);
                    if (newSensors != null && newSensors.size() >= activeSensors.size()) {
                        for (String s : newSensors) {
                            //put sub in the list of active subs for every sensor in bestK sensors
                            activateSensorSub(s, sub);
                            //put bestK sensors in the list of active sensors in the area
                            activateSensorInArea(area, s);
                        }
                        if (!newSensors.contains(sensorID)) {
                            addSubscriptionCandidate(sensorID, sub);
                        }

                        for (String s : activeSensors) {
                            if (!newSensors.contains(s)) {
                                if (checkSensorActiveSubs(s, sub)) {
                                    activateCandidateIfExists(s, sub, (String) announcedSensors.get(s).getProperties().get("Area"));
                                    sensorActiveSubs.remove(s);
                                    System.out.println(s);
                                    activeSensorsInArea.get(area).remove(s);
                                    addSubscriptionCandidate(s, sub);
                                }
                            }
                        }
                    } else if (newSensors != null && newSensors.size() < activeSensors.size()) {
                        for (String s : newSensors) {
                            //put sub in the list of active subs for every sensor in bestK sensors
                            activateSensorSub(s, sub);
                            //put bestK sensors in the list of active sensors in the area
                            activateSensorInArea(area, s);
                        }
                        if (!newSensors.contains(sensorID)) {
                            addSubscriptionCandidate(sensorID, sub);
                        }

                        for (String s : activeSensors) {
                            if (!newSensors.contains(s)) {
                                sensorActiveSubs.remove(s);
                                System.out.println(s);
                                activeSensorsInArea.get(area).remove(s);
                                addSubscriptionCandidate(s, sub);
                            }
                        }
                    } else if (newSensors == null) {
                        //treba ugasit sve senzore
                        turnOffAllSensors();
                    }

//					if (!qosLogic.deactivateSensor(sub,activeSensors, sensorID)) {
//						// add sub in the list of active subs of the sensor
//						activateSensorSub(sensorID, sub);	
//						// put sensor in active sensors in area
//						activateSensorInArea(area, sensorID);
//					}else
//						addSubscriptionCandidate(sensorID, sub);					
                } else {
                    if (!sub.isValid() && !announcedSensors.get(sensorID).isValid()) {
                        //remove both sub and sensor announcement from area because they aren't valid anymore
                        subsInAreaToRemove.add(sub);
                        removeAnnouncedSensorInArea(announcedSensors.get(sensorID));
                    } else if (!sub.isValid() && announcedSensors.get(sensorID).isValid()) {
                        //remove sub from area because it isn't valid anymore
                        subsInAreaToRemove.add(sub);
                    } else if (sub != null && sub.isValid() && announcedSensors.get(sensorID) != null && !announcedSensors.get(sensorID).isValid()) {
                        //remove sensor announcement from area because it isn't valid anymore
                        removeAnnouncedSensorInArea(announcedSensors.get(sensorID));
                    }
                }
            }
        }
        //remove expired subs
        if (!subsInAreaToRemove.isEmpty()) {
            for (TripletSubscription s : subsInAreaToRemove) {
                removeSubscriptionInArea(s);
            }
        }
        log.writeToLog("Active sensors by areas: " + activeSensorsInArea, true);
        log.writeToLog("All candidates by subscriptions: " + subCandidates, true);
        log.writeToLog("Active subscriptions by sensors: " + sensorActiveSubs, true);
        log.writeToLog("--------------------------------------", true);

    }

    private void removeSubscriptionInArea(TripletSubscription subscription) {
        log.writeToLog("--------------------------------------", true);
        log.writeToLog("Subscription is not valid anymore! " + subscription, true);

        Set<Triplet> triplets = subscription.attributePredicates("Area");
        for (Triplet t : triplets) {
            //remove sub from all areas
            removeSubscriptionFromActiveSensorSubs((String) t.getValue(), subscription);
            synchronized (mutexSubscriptionsInArea) {
                subscriptionsInArea.get((String) t.getValue()).remove(subscription);
                //if there is no more subs in area, remove that area
                if (subscriptionsInArea.get((String) t.getValue()).isEmpty()) {
                    subscriptionsInArea.remove((String) t.getValue());
                }
            }
        }
        log.writeToLog("--------------------------------------", true);
    }

    //checks whether sensor has other active subs for which there is no substitute sensor
    private boolean checkSensorActiveSubs(String sensorID, TripletSubscription s) {
        boolean b = false;
        synchronized (mutexSensorActiveSubs) {
            if (sensorActiveSubs.get(sensorID).size() == 1) {
                b = true;
            } else {
                for (TripletSubscription sub : sensorActiveSubs.get(sensorID)) {
                    if (existOtherActiveSensorsForSub(sensorID, sub)) {
                        b = true;
                    } else {
                        b = false;
                        break;
                    }
                }
            }
        }
        return b;
    }

    private boolean existOtherActiveSensorsForSub(String sensorID, TripletSubscription sub) {
        boolean b = false;
        synchronized (mutexSensorActiveSubs) {
            for (String sensor : sensorActiveSubs.keySet()) {
                for (TripletSubscription s : sensorActiveSubs.get(sensor)) {
                    if (sensor != sensorID && s.equals(sub)) {
                        b = true;
                    }
                }
            }
        }
        return b;
    }

    private void removeSubscriptionFromActiveSensorSubs(String area, TripletSubscription sub) {
        ArrayList<String> activeSensorsInAreaToRemove = new ArrayList<String>();

        log.writeToLog("--------------------------------------", true);
        log.writeToLog("Remove the subscription from active subscriptions of a sensor in this area " + area, true);
        //if there are active sensors in the area remove sub
        if (activeSensorsInArea.containsKey(area) && !activeSensorsInArea.get(area).isEmpty()) {
            for (String sensorID : activeSensorsInArea.get(area)) {
                //remove sub from the list of active sensor subs
                synchronized (mutexSensorActiveSubs) {
                    if (sensorActiveSubs.containsKey(sensorID) && !sensorActiveSubs.get(sensorID).isEmpty() && sensorActiveSubs.get(sensorID).contains(sub)) {
                        sensorActiveSubs.get(sensorID).remove(sub);
                    }
                    //if sensor doesn't have any more active subs, remove sensor from active sensors in the area
                    if (sensorActiveSubs.containsKey(sensorID) && sensorActiveSubs.get(sensorID).isEmpty()) {
                        activeSensorsInAreaToRemove.add(sensorID);
                        turnSensorOff(sensorID);
                    }
                }
            }
        }
        synchronized (mutexActiveSensorsInArea) {
            if (!activeSensorsInAreaToRemove.isEmpty()) {
                activeSensorsInArea.get(area).removeAll(activeSensorsInAreaToRemove);
            }
            if (activeSensorsInArea.containsKey(area) && activeSensorsInArea.get(area).isEmpty()) {
                activeSensorsInArea.remove(area);
            }
        }

        synchronized (mutexSubCandidates) {
            if (subCandidates.containsKey(sub) && !subCandidates.get(sub).isEmpty()) {
                subCandidates.remove(sub);
            }
        }

        log.writeToLog("Active sensors by areas after removing invalid subscription: " + activeSensorsInArea, true);
        log.writeToLog("--------------------------------------", true);
    }

    private void removeAnnouncedSensorInArea(HashtablePublication announcement) {
        log.writeToLog("--------------------------------------", true);
        log.writeToLog("Sensor announcement is not valid anymore! ", true);

        synchronized (mutexAnnouncedSensors) {
            //remove sensor announcement
            log.writeToLog("Remove announced sensor from the system: " + announcedSensors.get(announcement.getProperties().get("SensorID")), true);
            announcedSensors.remove(announcement.getProperties().get("SensorID"));
        }
        synchronized (mutexSensorsInArea) {
            //remove sensor from area
            if (sensorsInArea.get(announcement.getProperties().get("Area")) != null && sensorsInArea.get(announcement.getProperties().get("Area")).contains(announcement.getProperties().get("SensorID"))) {
                sensorsInArea.get(announcement.getProperties().get("Area")).remove(announcement.getProperties().get("SensorID"));
                log.writeToLog("Remaining sensors in the area: " + sensorsInArea.get(announcement.getProperties().get("Area")), true);
            }
            //if there is no more sensors in the area, remove that area
            if (sensorsInArea.get(announcement.getProperties().get("Area")) == null && sensorsInArea.get(announcement.getProperties().get("Area")).isEmpty()) {
                sensorsInArea.remove(announcement.getProperties().get("Area"));
            }
        }
        synchronized (mutexActiveSensorsInArea) {
            if (activeSensorsInArea.containsKey(announcement.getProperties().get("Area")) && activeSensorsInArea.get(announcement.getProperties().get("Area")).contains(announcement.getProperties().get("SensorID"))) {
                activeSensorsInArea.get(announcement.getProperties().get("Area")).remove(announcement.getProperties().get("SensorID"));
            }
        }

        if (sensorActiveSubs.containsKey(announcement.getProperties().get("SensorID")) && !sensorActiveSubs.get(announcement.getProperties().get("SensorID")).isEmpty()) {
            for (TripletSubscription sub : sensorActiveSubs.get(announcement.getProperties().get("SensorID"))) {
                if (subCandidates.containsKey(sub) && !subCandidates.get(sub).isEmpty()) {
                    String activateSensor = qosLogic.activateBestCandidate(subCandidates.get(sub));
                    activateSensorInArea((String) announcement.getProperties().get("Area"), activateSensor);

                    activateSensorSub(activateSensor, sub);

                    //remove new sensor that will be activated from list of candidates
                    synchronized (mutexSubCandidates) {
                        subCandidates.get(sub).remove(activateSensor);
                    }
                    turnSensorOn(activateSensor);
                }
            }
        }

        //remove all sensor's active subs
        synchronized (mutexSensorActiveSubs) {
            sensorActiveSubs.remove(announcement.getProperties().get("SensorID"));
        }

        //if sensor was candidate for other subs, remove it from list of candidates
        ArrayList<TripletSubscription> list = new ArrayList<TripletSubscription>();
        synchronized (mutexSubCandidates) {
            for (TripletSubscription s : subCandidates.keySet()) {
                if (subCandidates.get(s).contains(announcement.getProperties().get("SensorID"))) {
                    list.add(s);
                }
            }
        }
        synchronized (mutexSubCandidates) {
            for (TripletSubscription s : list) {
                subCandidates.get(s).remove(announcement.getProperties().get("SensorID"));
            }
        }

        log.writeToLog("--------------------------------------", true);
    }

    private void removeSensorActiveSubsInPreviousArea(String oldSensorArea, String sensorID) {
        log.writeToLog("--------------------------------------", true);
        log.writeToLog("Remove sensor active subscriptions from previously announced area", true);
        Set<String> set = new HashSet<String>();

        synchronized (mutexActiveSensorsInArea) {
            log.writeToLog("Active sensors by areas. " + activeSensorsInArea, true);
            if (activeSensorsInArea.get(oldSensorArea) != null && activeSensorsInArea.get(oldSensorArea).contains(sensorID)) {
                activeSensorsInArea.get(oldSensorArea).remove(sensorID);
            }
            if (activeSensorsInArea.get(oldSensorArea) == null || activeSensorsInArea.get(oldSensorArea).isEmpty()) {
                activeSensorsInArea.remove(oldSensorArea);
            }
            log.writeToLog("Active sensors by areas after deleting. " + activeSensorsInArea, true);
        }

        ArrayList<TripletSubscription> tempSensorActiveSubs = new ArrayList<TripletSubscription>();

        synchronized (mutexSensorActiveSubs) {
            tempSensorActiveSubs = sensorActiveSubs.get(sensorID);
        }
        synchronized (sensorActiveSubs) {
            if (tempSensorActiveSubs != null) {
                log.writeToLog("Active subs of a sensor that need to be deleted. " + tempSensorActiveSubs, true);
                //for every active sub of a sensor, activate sensor from list of candidates if exist and delete sub from list of sensor active subs
                for (TripletSubscription sub : tempSensorActiveSubs) {
                    if (subCandidates.containsKey(sub) && !subCandidates.get(sub).isEmpty()) {
                        String activateSensor = qosLogic.activateBestCandidate(subCandidates.get(sub));
                        activateSensorInArea(oldSensorArea, activateSensor);
                        activateSensorSub(activateSensor, sub);
                        set.add(activateSensor);
//						putAllSensorInactiveSubsInActive(activateSensor);
//						removeSensorFromCandidates(activateSensor);

                    }
                }
            }
        }
        for (String s : set) {
            putAllSensorInactiveSubsInActive(s);
            removeSensorFromCandidates(s);
        }
        removeSensorFromCandidates(sensorID);

        //remove all sensor's active subs
        synchronized (mutexSensorActiveSubs) {
            sensorActiveSubs.remove(sensorID);
        }

        log.writeToLog("--------------------------------------", true);
    }

    private void putAllSensorInactiveSubsInActive(String sensor) {
        //if sensor was candidate for other subs, activate all this subs 
        synchronized (mutexSubCandidates) {
            for (TripletSubscription s : subCandidates.keySet()) {
                if (subCandidates.get(s).contains(sensor)) {
                    activateSensorSub(sensor, s);
                }
            }
        }
    }

    private void removeSensorFromCandidates(String sensorID) {
        //if sensor was candidate for other subs, remove it from list of candidates
        ArrayList<TripletSubscription> list = new ArrayList<TripletSubscription>();
        synchronized (mutexSubCandidates) {
            for (TripletSubscription s : subCandidates.keySet()) {
                if (subCandidates.get(s).contains(sensorID)) {
                    list.add(s);
                }
            }
        }
        synchronized (mutexSubCandidates) {
            for (TripletSubscription s : list) {
                subCandidates.get(s).remove(sensorID);
            }
        }
    }

    private void addSensorInArea(String area, String sensorID) {
        // if new area is not empty add sensor in area
        synchronized (mutexSensorsInArea) {
            if (sensorsInArea.containsKey(area)) {
                sensorsInArea.get(area).add(sensorID);
            } // create new area and add sensor
            else {
                Set<String> sensors = new HashSet<String>();
                sensors.add(sensorID);
                sensorsInArea.put(area, sensors);
            }
        }
    }

    private void addSubscriptionInArea(String area, TripletSubscription sub) {
        synchronized (mutexSubscriptionsInArea) {
            if (subscriptionsInArea.containsKey(area) && !subscriptionsInArea.get(area).contains(sub)) {
                subscriptionsInArea.get(area).add(sub);
            } else if (!subscriptionsInArea.containsKey(area)) {
                ArrayList<TripletSubscription> subs = new ArrayList<TripletSubscription>();
                subs.add(sub);
                subscriptionsInArea.put(area, subs);
            }
        }
    }

    private void activateSensorSub(String sensorID, TripletSubscription sub) {
        synchronized (mutexSensorActiveSubs) {
            if (!sensorActiveSubs.containsKey(sensorID)) {
                ArrayList<TripletSubscription> subs = new ArrayList<TripletSubscription>();
                subs.add(sub);
                sensorActiveSubs.put(sensorID, subs);
            } else if (sensorActiveSubs.containsKey(sensorID) && !sensorActiveSubs.get(sensorID).contains(sub)) {
                sensorActiveSubs.get(sensorID).add(sub);
            }
        }
    }

    private void activateSensorInArea(String area, String sensorID) {
        synchronized (mutexActiveSensorsInArea) {
            if (activeSensorsInArea.containsKey(area) && !activeSensorsInArea.get(area).contains(sensorID)) {
                activeSensorsInArea.get(area).add(sensorID);
            } else if (!activeSensorsInArea.containsKey(area)) {
                Set<String> sensors = new HashSet<String>();
                sensors.add(sensorID);
                activeSensorsInArea.put(area, sensors);

            }
        }
    }

    private void addSubscriptionCandidate(String sensorID, TripletSubscription sub) {
        log.writeToLog("Candidate! Turn sensor off. " + sensorID, true);
        boolean alreadyCandidate = false;

        synchronized (mutexSubCandidates) {
            for (TripletSubscription ts : subCandidates.keySet()) {
                if (subCandidates.get(ts).contains(sensorID)) {
                    alreadyCandidate = true;
                }
            }
            if (!alreadyCandidate) {
                turnSensorOff(sensorID);
            }

            if (subCandidates.containsKey(sub)) {
                if (!subCandidates.get(sub).contains(sensorID)) {
                    subCandidates.get(sub).add(sensorID);
                }
            } else {
                Set<String> list = new HashSet<String>();
                list.add(sensorID);
                subCandidates.put(sub, list);
            }
        }

    }

    private void turnSensorOn(String sensorID) {
        //turn on the sensor
        HashtablePublication hp = new HashtablePublication(-1, System.currentTimeMillis());
        hp.setProperty("Type", "SensorControl");
        hp.setProperty("Status", "True");
        hp.setProperty("Timestamp", hp.getStartTime());
        hp.setProperty("SensorID", sensorID);
        qosMB.publish(hp);
    }

    private void turnSensorOff(String sensorID) {
        //turn off the sensor
        HashtablePublication hp = new HashtablePublication(-1, System.currentTimeMillis());
        hp.setProperty("Type", "SensorControl");
        hp.setProperty("Status", "False");
        hp.setProperty("Timestamp", hp.getStartTime());
        hp.setProperty("SensorID", sensorID);
        qosMB.publish(hp);
    }

    private void subscribeOnIndividualReadingsInArea(String area) {

        TripletSubscription sub = new TripletSubscription(-1, System.currentTimeMillis());
        sub.addPredicate(new Triplet("Area", area, Operator.EQUAL));
        sub.addPredicate(new Triplet("Type", "SensorReading", Operator.EQUAL));
        sub.addPredicate(new Triplet("Temperature", Float.NEGATIVE_INFINITY, Operator.GREATER_OR_EQUAL));
        qosMB.subscribe(sub);

        TripletSubscription sub1 = new TripletSubscription(-1, System.currentTimeMillis());
        sub1.addPredicate(new Triplet("Area", area, Operator.EQUAL));
        sub1.addPredicate(new Triplet("Type", "SensorReading", Operator.EQUAL));
        sub1.addPredicate(new Triplet("Humidity", Integer.MIN_VALUE, Operator.GREATER_OR_EQUAL));
        qosMB.subscribe(sub1);

        TripletSubscription sub2 = new TripletSubscription(-1, System.currentTimeMillis());
        sub2.addPredicate(new Triplet("Area", area, Operator.EQUAL));
        sub2.addPredicate(new Triplet("Type", "SensorReading", Operator.EQUAL));
        sub2.addPredicate(new Triplet("Pressure", Integer.MIN_VALUE, Operator.GREATER_OR_EQUAL));
        qosMB.subscribe(sub2);

        TripletSubscription sub3 = new TripletSubscription(-1, System.currentTimeMillis());
        sub3.addPredicate(new Triplet("Area", area, Operator.EQUAL));
        sub3.addPredicate(new Triplet("Type", "SensorReading", Operator.EQUAL));
        sub3.addPredicate(new Triplet("CO", Float.NEGATIVE_INFINITY, Operator.GREATER_OR_EQUAL));
        qosMB.subscribe(sub3);

        TripletSubscription sub4 = new TripletSubscription(-1, System.currentTimeMillis());
        sub4.addPredicate(new Triplet("Area", area, Operator.EQUAL));
        sub4.addPredicate(new Triplet("Type", "SensorReading", Operator.EQUAL));
        sub4.addPredicate(new Triplet("SO2", Float.NEGATIVE_INFINITY, Operator.GREATER_OR_EQUAL));
        qosMB.subscribe(sub4);

        TripletSubscription sub5 = new TripletSubscription(-1, System.currentTimeMillis());
        sub5.addPredicate(new Triplet("Area", area, Operator.EQUAL));
        sub5.addPredicate(new Triplet("Type", "SensorReading", Operator.EQUAL));
        sub5.addPredicate(new Triplet("NO2", Float.NEGATIVE_INFINITY, Operator.GREATER_OR_EQUAL));
        qosMB.subscribe(sub5);

    }

    private void cleanExpiredMessages() {
        HashMap<String, ArrayList<TripletSubscription>> subsInAreaToRemove = new HashMap<String, ArrayList<TripletSubscription>>();
        ArrayList<String> sensorsToRemove = new ArrayList<String>();

        log.writeToLog("--------------------------------------", true);
        log.writeToLog("Searching for expired subscriptions in the system", true);

        //remove expired subscriptions	
        log.writeToLog("All subscriptions by areas: " + subscriptionsInArea, true);
        for (String area : subscriptionsInArea.keySet()) {
            for (TripletSubscription sub : subscriptionsInArea.get(area)) {
                if (!sub.isValid()) {
                    //put this sub in the list of subs in this area that will be removed later
                    if (!subsInAreaToRemove.containsKey(area)) {
                        ArrayList<TripletSubscription> tempSubs = new ArrayList<TripletSubscription>();
                        tempSubs.add(sub);
                        subsInAreaToRemove.put(area, tempSubs);
                    } else {
                        subsInAreaToRemove.get(area).add(sub);
                    }
                }
            }
        }

        //remove all expired subs for all areas and all candidates for expired sub
        for (String a : subsInAreaToRemove.keySet()) {
            for (TripletSubscription sub : subsInAreaToRemove.get(a)) {
                removeSubscriptionInArea(sub);
            }
        }

        log.writeToLog("After removing expired subscriptions by areas: " + subscriptionsInArea, true);
        log.writeToLog("--------------------------------------", true);

        log.writeToLog("Searching for expired sensor announcements in the system", true);
        //remove expired sensor announcements
        for (String sensorID : announcedSensors.keySet()) {
            if (!announcedSensors.get(sensorID).isValid()) {
                sensorsToRemove.add(sensorID);
            }
        }
        for (String sensorID : sensorsToRemove) {
            removeAnnouncedSensorInArea(announcedSensors.get(sensorID));
        }
        log.writeToLog("Remaining announced sensors in the system: " + announcedSensors, true);
        log.writeToLog("--------------------------------------", true);
    }

    private void calculateAverageSensorReadings() {
        log.writeToLog("--------------------------------------", true);
        log.writeToLog("Calculate average sensor readings in last 30 minutes and delete publications that fell out from time window", true);

        HashMap<String, ArrayList<HashtablePublication>> pubsInAreaToRemove = new HashMap<String, ArrayList<HashtablePublication>>();

        synchronized (mutexPublicationsInArea) {
            for (String area : publicationsInArea.keySet()) {
                for (HashtablePublication p : publicationsInArea.get(area)) {
                    //delete all publications older than 30 minutes
                    if (System.currentTimeMillis() - p.getStartTime() >= 1800000) {
                        if (!pubsInAreaToRemove.containsKey(area)) {
                            ArrayList<HashtablePublication> tempPubs = new ArrayList<HashtablePublication>();
                            tempPubs.add(p);
                            pubsInAreaToRemove.put(area, tempPubs);
                        } else {
                            pubsInAreaToRemove.get(area).add(p);
                        }
                    }
                }
            }
        }
        synchronized (mutexPublicationsInArea) {
            for (String a : pubsInAreaToRemove.keySet()) {
                publicationsInArea.get(a).removeAll(pubsInAreaToRemove.get(a));
                if (publicationsInArea.get(a).isEmpty()) {
                    publicationsInArea.remove(a);
                }
            }
        }
        synchronized (mutexPublicationsInArea) {
            for (String a : publicationsInArea.keySet()) {
                log.writeToLog("Average readings for area " + a + ": ", true);
                qosMB.publish(qosLogic.calculateAverageSensorReadings(a, publicationsInArea.get(a)));
            }
        }

        log.writeToLog("--------------------------------------", true);
    }

    private void registerVirtualSensor(String area) throws IOException, InterruptedException {
        LatLonPoint llpoint = MGRSPoint.MGRStoLL(new MGRSPoint(area));
        double lat = llpoint.getLatitude();
        double lng = llpoint.getLongitude();
        VirtualSensor vs = new VirtualSensor(area, wrapperPort, lat, lng, this.sensorParameters, this.sensorTypes, this.lsmProperty, this.lsmUnit, this.gsnAddress);
        vs.createAndRegister();
        virtualSensors.put(area, wrapperPort);
        wrapperPort++;
    }

    private void sendToXGSN(Integer port, HashtablePublication sensorPublication) {
        //send publication to x-gsn listening on defined port
        try {
        String gsnIP = this.gsnAddress.split(":")[0];
        DatagramSocket socket = new DatagramSocket();
        ByteArrayOutputStream baseOut = new ByteArrayOutputStream(64 * 1000); //64KB, max for IP packet
        baseOut.reset();
        ObjectOutputStream oos = new ObjectOutputStream(baseOut); //has to write a new header each time
        oos.writeObject(sensorPublication.getProperties());
        oos.flush();
        //IP address of DeliveryService has to be Loopback (same maschine)
        DatagramPacket packet = new DatagramPacket(baseOut.toByteArray(), baseOut.size(),InetAddress.getByName(gsnIP), port);
        socket.send(packet);
        } catch (Exception e) {
            log.writeToLog("Exception occured while sending the UDP package to the GSN: " + e);
        }
    }

    private void startTimer() {
        Timer time = new Timer();
        CleanExpiredMessagesTask clean = new CleanExpiredMessagesTask();
        AverageSensorReadingsTask average = new AverageSensorReadingsTask();
        //schedule cleaning of expired messages every 5 minutes, start after 5 minutes
        time.scheduleAtFixedRate(clean, 300000, 300000);
        //schedule calculating average values every 15 minutes, start immediately
        time.scheduleAtFixedRate(average, 0, 900000);
    }

    private class CleanExpiredMessagesTask extends TimerTask {

        public void run() {
            cleanExpiredMessages();
        }
    }

    private class AverageSensorReadingsTask extends TimerTask {

        public void run() {
            calculateAverageSensorReadings();
        }
    }

}
