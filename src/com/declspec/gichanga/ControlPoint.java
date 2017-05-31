package com.declspec.gichanga;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;

public class ControlPoint extends GlobeAnnotation {

	public ControlPoint(String id, Position position, AnnotationAttributes attributes) {
		super(id, position, attributes);
		this.setAltitudeMode(gov.nasa.worldwind.WorldWind.ABSOLUTE);
	}

}
