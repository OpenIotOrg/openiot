package org.openiot.qos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.util.LogWriter;

public class QoSLogic {
	
    private volatile HashMap<String, ArrayList<Double>> sensorBattery = new HashMap<String, ArrayList<Double>>();
    private volatile HashMap<String, Integer> sensorPriority = new HashMap<String, Integer>();
    private LogWriter log;

    private double highBatteryLevel;
    private double lowBatteryLevel;
    private int numOfActiveSensors;

	public QoSLogic(LogWriter log, int initialNumOfActiveSensors, double initialHighBatteryLevel, double initialLowBatteryLevel){
		this.log = log;
		this.highBatteryLevel = initialHighBatteryLevel;
		this.lowBatteryLevel = initialLowBatteryLevel;
                this.numOfActiveSensors = initialNumOfActiveSensors;
	}
		
	public void setBatteryLevels (double hBatteryLevel, double lBatteryLevel){
		this.highBatteryLevel = hBatteryLevel;
		this.lowBatteryLevel = lBatteryLevel;
	}
	
	public void setNumberOfActiveSensors (int numOfActiveSensors){
		this.numOfActiveSensors = numOfActiveSensors;
	}
	
	public Set<String> findBestSensors(Set<String> activeSensors, Set<String> possibleSensors) {
		
		Set<String> bestK = new HashSet<String>();
		Map<String, Integer> unsortedSensorPriorityMap = new HashMap<String, Integer>();
		String sensor;
		Set<String> sensorsToRemove = new HashSet<String>();
		
		if(possibleSensors!=null){
			if(numOfActiveSensors != 0){
				if (activeSensors.size()<numOfActiveSensors){
				
					//check reputation for every possible sensor
					for(String s : possibleSensors){
						if(sensorPriority.containsKey(s))
							unsortedSensorPriorityMap.put(s, sensorPriority.get(s));					
					}
					
					@SuppressWarnings("unchecked")
					Map<String, Integer> sortedMap = sortByValue(unsortedSensorPriorityMap);
					ArrayList<String> newSensors = new ArrayList<String>();
					for(String s : sortedMap.keySet()){
						newSensors.add(s);
					}
					for(int i=0; i<(numOfActiveSensors-activeSensors.size());i++){
						if(newSensors.size()-i>0 && sensorPriority.get(newSensors.get(i))!=3){
							bestK.add(newSensors.get(i));
						}
					}
					if (activeSensors.size()==0 && bestK.size()==0)
						bestK.add(newSensors.get(0));
					
					//if there is already enough active sensors check if some of active has low battery and try to replace it with better sensor 
					for(String s : activeSensors){
						if(sensorPriority.containsKey(s) && sensorPriority.get(s)==3){
							sensor = searchBetterSensor(possibleSensors, bestK);
							if (sensor!=null){
								bestK.add(sensor);
								sensorsToRemove.add(s);
							}else if(sensor==null && possibleSensors.size()==1)
							log.writeToLog("Active sensor that needs to be replaced : "+s+" Replacement sensor: "+sensor, true);
						}					
					}
					for (String s : sensorsToRemove){
						activeSensors.remove(s);
					}
					bestK.addAll(activeSensors);				
				}else if (activeSensors.size() == numOfActiveSensors){
				
					//if there is already enough active sensors check if some of active has low battery and try to replace it with better sensor 
					for(String s : activeSensors){
						if(sensorPriority.containsKey(s) && sensorPriority.get(s)==3){
							sensor = searchBetterSensor(possibleSensors, bestK);
							if (sensor!=null){
								bestK.add(sensor);
								sensorsToRemove.add(s);
							}
							log.writeToLog("Active sensor that needs to be replaced : "+s+" Replacement sensor: "+sensor, true);
						}					
					}
					for (String s : sensorsToRemove){
						activeSensors.remove(s);
					}
					bestK.addAll(activeSensors);
				}else if ((activeSensors.size() - numOfActiveSensors) > 0){
					System.out.println("Treba ugasit neke senzore");
					//nadji senzore koji se gase
					for(String s : activeSensors){
						if(sensorPriority.containsKey(s))
							unsortedSensorPriorityMap.put(s, sensorPriority.get(s));					
					}
					for(String s : possibleSensors){
						if(sensorPriority.containsKey(s))
							unsortedSensorPriorityMap.put(s, sensorPriority.get(s));					
					}
					
					@SuppressWarnings("unchecked")
					Map<String, Integer> sortedMap = sortByValue(unsortedSensorPriorityMap);
					ArrayList<String> newSensors = new ArrayList<String>();
					for(String s : sortedMap.keySet()){
						newSensors.add(s);
					}
					
					for (int i=0; i<numOfActiveSensors;i++){
						bestK.add(newSensors.get(i));
						
					}
					System.out.println(bestK);
				}
			}
		}else {
			if(numOfActiveSensors==0){
				
			}else if ((activeSensors.size() - numOfActiveSensors) > 0){
				System.out.println("Treba ugasit neke senzore");
				//nadji senzore koji se gase
				for(String s : activeSensors){
					if(sensorPriority.containsKey(s))
						unsortedSensorPriorityMap.put(s, sensorPriority.get(s));					
				}				
				@SuppressWarnings("unchecked")
				Map<String, Integer> sortedMap = sortByValue(unsortedSensorPriorityMap);
				ArrayList<String> newSensors = new ArrayList<String>();
				for(String s : sortedMap.keySet()){
					newSensors.add(s);
				}
				
				for (int i=0; i<numOfActiveSensors;i++){
					bestK.add(newSensors.get(i));
					
				}
				System.out.println(bestK);
			}else
				bestK.addAll(activeSensors);
		}
		return bestK;
	}

	private String searchBetterSensor(Set<String> possibleSensors, Set<String> bestK) {		
		String betterSensor = null;
		
		for(String s : possibleSensors){
			if(!bestK.contains(s) && sensorPriority.get(s)==1){				
				betterSensor=s;
				break;
			}
			else if(!bestK.contains(s) && sensorPriority.get(s)==2 && betterSensor == null)
				betterSensor=s;
			else if(bestK.contains(s) && possibleSensors.size()==1 && betterSensor == null)
				betterSensor=s;
		}
		return betterSensor;
		
	}

	public boolean deactivateSensor(Subscription sub, Set<String> activeSensors, String newSensorID) {
		//ako ne smije bit aktivnih senzora ugasi ga
		if (numOfActiveSensors==0)
			return true;
		//ako treba 1 aktivan i do sad nema aktivnih upali ga kakav god bio
		else if (numOfActiveSensors==1 && activeSensors.size()<numOfActiveSensors)
			return false;
		//ako treba 1 aktivan i vec ima aktivnih ugasi ga
		else if (numOfActiveSensors==1 && activeSensors.size()>=numOfActiveSensors)
			return true;
		//ako ima vec aktivnih, al ih nije dosta al kandidat ima losu bateriju ugasi ga
		else if (numOfActiveSensors>1 && activeSensors.size()>=1 && activeSensors.size()<numOfActiveSensors && sensorPriority.get(newSensorID)==3)
			return true;
		//ako ima vec aktivnih, al ih nije dosta a kandidat ima dobru bateriju upali ga
		else if (numOfActiveSensors>1 && activeSensors.size()>=1&& activeSensors.size()<numOfActiveSensors && (sensorPriority.get(newSensorID)==2 || sensorPriority.get(newSensorID)==1))
			return false;
		//ako nema aktivnih upali ga kakv god bio
		else if(numOfActiveSensors>1 && activeSensors.size()==0)
			return false;
		else 
			return true;
		
	}
	
	public Set<String> newActiveSensors(Set<String> activeSensors, String newSensorID) {
		Set<String> set = new HashSet<String>();
		set.add(newSensorID);
		Set<String> sensors = findBestSensors(activeSensors,set);
		
		
		return sensors;
	}

	public String activateBestCandidate(Set<String> setOfCandidates) {
		String sensor;
		
		Map<String, Integer> unsortedSensorPriorityMap = new HashMap<String, Integer>();
		for(String s :  setOfCandidates){
			if(sensorBattery.containsKey(s))
				unsortedSensorPriorityMap.put(s, sensorPriority.get(s));
		}
		
		
		@SuppressWarnings("unchecked")
		Map<String, Integer> sortedMap = sortByValue(unsortedSensorPriorityMap);ArrayList<String> newSensors = new ArrayList<String>();
		for(String s : sortedMap.keySet()){
			newSensors.add(s);
		}		
		sensor = newSensors.get(0);
		log.writeToLog("Sensor that is going to be activated : "+sensor, true);
		return sensor;
	}

	
	public void setBattery(HashtablePublication sensorAnnouncement) {
		
		ArrayList<Double> battery = new ArrayList<Double>();
		battery.add(new Double((Short)sensorAnnouncement.getProperties().get("BatteryS")));
		battery.add(new Double((Float)sensorAnnouncement.getProperties().get("BatteryMP")));
		sensorBattery.put((String)sensorAnnouncement.getProperties().get("SensorID"), battery);
		
		if(((Short)sensorAnnouncement.getProperties().get("BatteryS")).doubleValue() > highBatteryLevel && ((Float)sensorAnnouncement.getProperties().get("BatteryMP")).doubleValue()  > highBatteryLevel){
			sensorPriority.put((String)sensorAnnouncement.getProperties().get("SensorID"),1);
		}else if(((Short)sensorAnnouncement.getProperties().get("BatteryS")).doubleValue()  > lowBatteryLevel && ((Float)sensorAnnouncement.getProperties().get("BatteryMP")).doubleValue()  > lowBatteryLevel){
			sensorPriority.put((String)sensorAnnouncement.getProperties().get("SensorID"),2);
		}else 
			sensorPriority.put((String)sensorAnnouncement.getProperties().get("SensorID"),3);
		
		log.writeToLog("Sensor battery: "+sensorBattery.get((String)sensorAnnouncement.getProperties().get("SensorID")), true);
		log.writeToLog("Priority level: "+sensorPriority.get((String)sensorAnnouncement.getProperties().get("SensorID")), true);
	}
	
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map sortByValue(Map unsortMap) {
		 List list = new LinkedList(unsortMap.entrySet());
 
		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});
 
		// put sorted list into map again
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public HashtablePublication calculateAverageSensorReadings(String area, Set<HashtablePublication> publicationsInArea) {
		
		ArrayList<Object> temp = new ArrayList<Object>();
		ArrayList<Object> humid = new ArrayList<Object>();
		ArrayList<Object> pressure = new ArrayList<Object>();
		ArrayList<Object> co = new ArrayList<Object>();
		ArrayList<Object> no2 = new ArrayList<Object>();
		ArrayList<Object> so2 = new ArrayList<Object>();
		
		for(HashtablePublication pubInArea : publicationsInArea){
			for(String property : pubInArea.getProperties().keySet()){
				if (property.equals("Temperature"))
					temp.add(pubInArea.getProperties().get(property));
				else if (property.equals("Humidity"))
					humid.add(pubInArea.getProperties().get(property));
				else if (property.equals("Pressure"))
					pressure.add(pubInArea.getProperties().get(property));
				else if (property.equals("CO"))
					co.add(pubInArea.getProperties().get(property));
				else if (property.equals("NO2"))
					no2.add(pubInArea.getProperties().get(property));
				else if (property.equals("SO2"))
					so2.add(pubInArea.getProperties().get(property));
			}			
		}
		
		HashtablePublication pub = new HashtablePublication (System.currentTimeMillis()+900000, System.currentTimeMillis());
		pub.setProperty("Timestamp", pub.getStartTime());
		pub.setProperty("Type", "AverageReading");
		pub.setProperty("Area", area);
					
		Double tempValue = average(temp);
		if (tempValue!=Double.POSITIVE_INFINITY && tempValue!=Double.NEGATIVE_INFINITY)
			pub.setProperty("Temperature", tempValue);
		
		Double humidValue = average(humid);
		if (humidValue!=Double.POSITIVE_INFINITY && humidValue!=Double.NEGATIVE_INFINITY)
			pub.setProperty("Humidity", humidValue.intValue());
		
		Double pressureValue = average(pressure);
		if (pressureValue!=Double.POSITIVE_INFINITY && pressureValue!=Double.NEGATIVE_INFINITY)
			pub.setProperty("Pressure", pressureValue.intValue());
		
		Double coValue = average(co);
		if (coValue!=Double.POSITIVE_INFINITY && coValue!=Double.NEGATIVE_INFINITY)
			pub.setProperty("CO", coValue);
			
		Double no2Value = average(no2);
		if (no2Value!=Double.POSITIVE_INFINITY && no2Value!=Double.NEGATIVE_INFINITY)
			pub.setProperty("NO2", no2Value);
		
		Double so2Value = average(so2);
		if (so2Value!=Double.POSITIVE_INFINITY && so2Value!=Double.NEGATIVE_INFINITY)
			pub.setProperty("SO2", so2Value);
		
		return pub;
	}

	private Double average(ArrayList<Object> list) {
		Double average=Double.MIN_VALUE;
		
		for (Object o : list){
			if ( o instanceof Double)
				average+=(Double)o;	
			else if (o instanceof Float)
				average+=((Float)o).doubleValue();
			else if (o instanceof Long)
				average+=((Long)o).doubleValue();
			else if (o instanceof Integer)
				average+=((Integer)o).doubleValue();
			else if (o instanceof Short)
				average += ((Short) o).doubleValue();
		}
		
		return average/list.size();
	}

	
}
