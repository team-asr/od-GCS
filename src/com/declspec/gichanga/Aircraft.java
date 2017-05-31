package com.declspec.gichanga;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Renderable;

public class Aircraft {
	final private long flightId;
	private String hexCode = "";
	private Date logged;
	private String callsign = null;
	
	private CustomRenderableLayer controlPointsLayer;
	private CustomRenderableLayer shapeLayer;
	
	private long lastUpdated;
	
	private ArrayList<Position> positions = new ArrayList<Position>();
	
	private ArrayList<Renderable> controlPoints = new ArrayList<Renderable>();
	
	protected ArrayList<Renderable> getControlPoints(){
		return this.controlPoints;
	} 

	protected void addControlPoint(String id, Position position, String key, Object value) {
		ControlPoint controlPoint = new ControlPoint(id, position, WorldWindPanel.CONTROL_POINTS_ATTRIBUTES);
		controlPoint.setValue(key, value);

		this.controlPoints.add(controlPoint);	
	}
	
	protected Aircraft(long flightId) {
		this.flightId = flightId;
		this.controlPointsLayer = new CustomRenderableLayer();
		this.shapeLayer = new CustomRenderableLayer();
	}
	
	
	public Date getLogged() {
		return logged;
	}

	protected void setHexCode(String hexCode){this.hexCode = hexCode;}
	
	public void handleSBSMessage(SBSMessage msg) {
		if(msg.isPositionMessage()) {
			Position pos = Position.fromDegrees(msg.getLatitude(), msg.getLongitude(), msg.getAltitude());
			positions.add(0, pos);
			logged = msg.getMessageLogged();
			lastUpdated = System.currentTimeMillis();
			String callSign = null;
			if ((callSign = msg.getCallsign()) != null)
				if (callSign.length() > 0)
				callsign = callSign.trim();
		} 
		if(msg.isIdMessage()) {
			callsign = msg.getCallsign();
			lastUpdated = System.currentTimeMillis();
		}
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public String getCallsign() {
		return callsign;
	}

	public boolean hasCallsign() {
		return callsign != null;
	}

	public boolean hasPositions() {
		return positions.size() > 0;
	}
	
	public Position[] getPositions() {
		return (Position[])positions.toArray(new Position[positions.size()]);
	}
	
	public Position getCurrentPosition() {
		return (Position)positions.get(0); 
	}
	
	public ArrayList<Position> getPosition() {
		return positions; 
	}
	
	protected String getHexCode(){return this.hexCode;}
	
	@Override
	public String toString(){
		String toStr = "";
		if (this.hasCallsign())
			toStr = this.getCallsign();
		if (this.hasPositions()){
			toStr += "; " + this.getCurrentPosition(); 
		}
		return toStr;
	}
	
	protected long getFlightID(){return this.flightId;}


	protected CustomRenderableLayer getShapeLayer() {
		return this.shapeLayer;
	}

	protected CustomRenderableLayer getControlPointsLayer() {
		return this.controlPointsLayer;
	}
}
