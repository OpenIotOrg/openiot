package org.openiot.gsn.dynamicSensorControl;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.openiot.gsn.metadata.LSM.LSMRepository;
import org.openiot.gsn.utils.PropertiesReader;

public class TaskTimer extends Timer {

	private static volatile TaskTimer instance = new TaskTimer();

	private TaskTimer() {

	}

	public static TaskTimer getInstance() {
		return instance;
	}

	public void startTimer() {
		String property = PropertiesReader
				.readProperty(LSMRepository.LSM_CONFIG_PROPERTIES_FILE,
						"dynamicControlPeriod").trim().toLowerCase();

		int minutes = Integer.parseInt(property);
		long period = TimeUnit.MINUTES.toMillis(minutes);

		schedule(DynamicControlTask.getInstance(), 0, period);
	}
}
