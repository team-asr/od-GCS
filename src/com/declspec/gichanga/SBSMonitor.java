package com.declspec.gichanga;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.Timer;

import gov.nasa.worldwind.geom.Position;

public class SBSMonitor extends Thread {
	private static final int DEFAULT_AIRCRAFT_TIMEOUT_SECONDS = 20;
	private static final String PROPERTY_AIRCRAFT_TIMEOUT_SECONDS = "earthgate.aircraft.timeout.seconds";
	private static final int DEFAULT_SERVERPORT = 8080;
	private static final String PROPERTY_SERVERPORT = "earthgate.serverport";
	private static final int DEFAULT_SBSPORT = 30003;
	private static final String PROPERTY_SBSPORT = "earthgate.sbsport";
	private static final String DEFAULT_SBSHOST = "localhost";
	private static final String PROPERTY_SBSHOST = "earthgate.sbshost";
	private static Calendar GMT_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	private static SimpleDateFormat HTTP_RESPONSE_DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz",
			Locale.ENGLISH);

	static {
		HTTP_RESPONSE_DATE_FORMAT.setCalendar(GMT_CALENDAR);
	}

	private Socket sbsSocket;
	private BufferedReader sbsReader;
	static private boolean isRunning = false;

	static final private AircraftTracker tracker = new AircraftTracker();

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	protected void startSBSMonitor() throws UnknownHostException, IOException {
		isRunning = true;
		this.start();
	}
	
	

	protected SBSMonitor() throws UnknownHostException, IOException {
		String sbsHost = System.getProperty(PROPERTY_SBSHOST, DEFAULT_SBSHOST);
		int sbsPort = Integer.getInteger(PROPERTY_SBSPORT, DEFAULT_SBSPORT).intValue();
		int serverPort = Integer.getInteger(PROPERTY_SERVERPORT, DEFAULT_SERVERPORT).intValue();
		int aircraftMillisecondsTimeout = Integer
				.getInteger(PROPERTY_AIRCRAFT_TIMEOUT_SECONDS, DEFAULT_AIRCRAFT_TIMEOUT_SECONDS).intValue() * 1000;

		tracker.setMillisecondsTimeout(aircraftMillisecondsTimeout);

		sbsSocket = new Socket(sbsHost, sbsPort);
		sbsReader = new BufferedReader(new InputStreamReader(sbsSocket.getInputStream()));
		System.out.println("Listening to BaseStation on " + sbsHost + ":" + sbsPort);
		;
		
		System.out.println("Tracking aircrafts with a timeout of " + aircraftMillisecondsTimeout / 1000 + " seconds");
	}

	public void run() {
		while (isRunning) {
			try {
				String message = sbsReader.readLine();
				// System.out.println(message);
				tracker.handleSBSMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

		protected void stopSBSMonitor() {
		isRunning = false;
	}

}
