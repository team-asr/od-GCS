package com.declspec.gichanga;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class SBSMessage {
	private static final double METERS_PER_FOOT = 0.3048;
	
	public static final int FIELD_INDEX_MESSAGE_TYPE = 0;
	public static final String MESSAGE_TYPE_TRANSMISSION_MESSAGE = "MSG";

	public static final int FIELD_INDEX_Transmission_Type = 1; 
	public static final int FIELD_INDEX_SessionID = 2; 
	public static final int FIELD_INDEX_AircraftID = 3;
	public static final int FIELD_INDEX_HexIdent  = 4;
	public static final int FIELD_INDEX_FlightID = 5;
	public static final int FIELD_INDEX_Time_message_generated = 6; 
	public static final int FIELD_INDEX_Time_message_logged = 8;
	public static final int FIELD_INDEX_Callsign = 10;
	public static final int FIELD_INDEX_Altitude = 11;
	public static final int FIELD_INDEX_GroundSpeed = 12;
	public static final int FIELD_INDEX_Track = 13;
	public static final int FIELD_INDEX_Lat = 14;
	public static final int FIELD_INDEX_Long = 15;
	public static final int FIELD_INDEX_VerticalRate = 16; 
	public static final int FIELD_INDEX_Squawk = 17;
	public static final int FIELD_INDEX_Alert = 18;
	public static final int FIELD_INDEX_Emergency = 19; 
	public static final int FIELD_INDEX_SPI = 20;
	public static final int FIELD_INDEX_IsOnGround = 21; 

	
	public static final int TRANSMISSION_TYPE_IDMessage = 1; 
	public static final int TRANSMISSION_TYPE_SurfacePositionMessage = 2;
	public static final int TRANSMISSION_TYPE_AirbornePositionMessage = 3;
	public static final int TRANSMISSION_TYPE_AirborneVelocityMessage = 4;
	public static final int TRANSMISSION_TYPE_SurveillanceAltMessage = 5;
	public static final int TRANSMISSION_TYPE_SurveillanceIDMessage = 6;
	public static final int TRANSMISSION_TYPE_AirToAirMessage = 7;
	public static final int TRANSMISSION_TYPE_AllCallReply = 8;

	private static Calendar GMT_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss.SSS", Locale.ENGLISH);
	static {DATE_FORMAT.setCalendar(GMT_CALENDAR);}

	
	private final String[] fields;

	public SBSMessage(String message) {
		String delim = ",";
		StringTokenizer tok = new StringTokenizer(message, delim, true);
		List fieldList = new ArrayList();
		boolean lastTokenWasDelimiter = false;
		while(tok.hasMoreTokens()) {
			String token = tok.nextToken();
			if(token.equals(delim)) {
				if(lastTokenWasDelimiter) {
					fieldList.add("");
				}
				lastTokenWasDelimiter = true;
			} else {
				fieldList.add(token);
				lastTokenWasDelimiter = false;
			}
		}
		fields = (String[])fieldList.toArray(new String[fieldList.size()]);
	}
	
	public String getField(int index) {
		return fields[index];
	}
	
	public double getDoubleField(int index) {
		return Double.parseDouble(getField(index));
	}

	public int getIntField(int index) {
		return Integer.parseInt(getField(index));
	}
	
	public boolean isTransmissionMessage() {
		return getField(FIELD_INDEX_MESSAGE_TYPE).equals(MESSAGE_TYPE_TRANSMISSION_MESSAGE);
	}
	
	public long getFlightId() {
		return Long.parseLong(getField(FIELD_INDEX_FlightID));
	}
	
	public float getTrack() {
		float heading = 0;
		try{
			if (getField(FIELD_INDEX_Track) != null && getField(FIELD_INDEX_Track).length() > 0) 
				heading = Float.valueOf(getField(FIELD_INDEX_Track));
		}catch(Exception e){}
		return heading;
	}
	
	public String getAircraftId() {
		return getField(FIELD_INDEX_AircraftID);
	}
	
	public boolean isPositionMessage() {
		if(isTransmissionMessage()) {
			int transType = getIntField(FIELD_INDEX_Transmission_Type);
			return transType == TRANSMISSION_TYPE_AirbornePositionMessage || transType == TRANSMISSION_TYPE_SurfacePositionMessage;
		}
		return false;
	}
	
	public boolean isIdMessage() {
		if(isTransmissionMessage()) {
			int transType = getIntField(FIELD_INDEX_Transmission_Type);
			return transType == TRANSMISSION_TYPE_IDMessage;
		}
		return false;
	}

	public double getLongitude() {
		return getDoubleField(FIELD_INDEX_Long);
	}
	
	public double getLatitude() {
		return getDoubleField(FIELD_INDEX_Lat);
	}

	/**
	 * @return altitude in meters
	 */
	public double getAltitude() {
		return getDoubleField(FIELD_INDEX_Altitude) * METERS_PER_FOOT;
	}
	
	public Date getDateField(int index) {
		try {
			return DATE_FORMAT.parse(getField(index) + " " + getField(index + 1));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Date getMessageGenerated() {
		return getDateField(FIELD_INDEX_Time_message_generated);
	}

	public Date getMessageLogged() {
		return getDateField(FIELD_INDEX_Time_message_logged);
	}
	
	public String getCallsign() {
		return getField(FIELD_INDEX_Callsign);
	}
	
	public String getHexIdent() {
		return getField(FIELD_INDEX_HexIdent);
	}


}
