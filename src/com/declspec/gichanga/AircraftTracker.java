package com.declspec.gichanga;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class AircraftTracker {
	private final Map<Long,Aircraft> aircraftMap = new HashMap<Long,Aircraft>();
	
	private long millisecondsTimeout = 20 * 1000;
	private long lastCleanupTime = -1;

	public void handleSBSMessage(String message) {
		SBSMessage msg = new SBSMessage(message);
		long flightId = msg.getFlightId();
		
		Aircraft aircraft = aircraftMap.get(flightId);
		if (aircraft == null) {
			aircraft = new Aircraft(flightId);
			aircraftMap.put(flightId, aircraft);
			WorldWindPanel.getWorldWindPanel().addAircraftLayer(aircraft);
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXX Created path for: " + flightId);
		}
		aircraft.handleSBSMessage(msg);
		System.out.println(new java.util.Date() + ": " + aircraft + "," + msg.getTrack());
		if (msg.isPositionMessage()){
			WorldWindPanel.getWorldWindPanel().updateAircraftLayer(aircraft);
		}
		checkCleanup();
	}

	public Aircraft[] getAircrafts() {
		Collection aircrafts = aircraftMap.values();
		return (Aircraft[]) aircrafts.toArray(new Aircraft[aircrafts.size()]);
	}

	private void cleanup() {
		Aircraft[] aircrafts = getAircrafts();
		long now = System.currentTimeMillis();
		for (int i = 0; i < aircrafts.length; i++) {
			Aircraft a = aircrafts[i];
			if ((now - a.getLastUpdated()) > millisecondsTimeout) {
				WorldWindPanel.getWorldWindPanel().removeAircraft(a);
				aircraftMap.remove(a.getFlightID());
			}
		}
	}

	private void checkCleanup() {
		if (lastCleanupTime == -1 || System.currentTimeMillis() - lastCleanupTime > millisecondsTimeout / 4) {
			cleanup();
			lastCleanupTime = System.currentTimeMillis();
		}
	}

	public long getMillisecondsTimeout() {
		return millisecondsTimeout;
	}

	public void setMillisecondsTimeout(long millisecondsTimeout) {
		this.millisecondsTimeout = millisecondsTimeout;
	}
}
