package com.declspec.gichanga;

import gov.nasa.worldwind.geom.Angle;

/*
Copyright (C) 2001, 2008 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.TerrainProfileLayer;
import gov.nasa.worldwind.layers.Earth.MSVirtualEarthLayer;
import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;
//import gov.nasa.worldwindx.examples.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import gov.nasa.worldwindx.examples.LayerPanel;
import gov.nasa.worldwindx.examples.util.*;
import gov.nasa.worldwind.util.layertree.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.ogc.kml.*;
import gov.nasa.worldwind.ogc.kml.impl.KMLController;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.markers.BasicMarker;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.BasicMarkerShape;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwind.util.*;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.*;

import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;

import javax.swing.filechooser.*;
import javax.xml.stream.XMLStreamException;

import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.URL;

/**
 * Example usage of MeasureTool to draw a shape on the globe and measure length,
 * area, etc. Click the "New" button, and then click and drag on the globe to
 * define a shape. The panel on the left shows the shape's measurement.
 *
 * @author Patrick Murris
 * @version $Id: WorldWindPanel.java 1 2011-07-16 23:22:47Z dcollins $
 * @see gov.nasa.worldwind.util.measure.MeasureTool
 * @see gov.nasa.worldwind.util.measure.MeasureToolController
 * @see MeasureToolPanel
 */
public class WorldWindPanel extends JPanel {
	private int lastTabIndex = -1;
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private TerrainProfileLayer profile = new TerrainProfileLayer();
	private PropertyChangeListener measureToolListener = new MeasureToolListener();

	
	private CustomRenderableLayer layer;
	
	private Polyline line;
	

	private Color lineColor = Color.YELLOW;
	private Color fillColor = new Color(.6f, .6f, .4f, .5f);
	private double lineWidth = 2;

	protected LayerTree layerTree;
	private RenderableLayer hiddenLayer = null;

	private MarkerLayer aircraftLayer;

	private static WorldWindPanel wwPanel = null;
	static final protected AnnotationAttributes CONTROL_POINTS_ATTRIBUTES = new AnnotationAttributes();;

	private WorldWindowGLCanvas wwd;
	private StatusBar statusBar;
	private WorldWindPanel wwjPanel;

	private LayerPanel layerPanel;
	private StatisticsPanel statsPanel;
	private Dimension canvasSize = new Dimension(800, 600);

	static ArrayList<Marker> aircraftMarkers = null;

	private Vector<AircaftTrackingSelectionListener> aircraftTrackingSelectionListeners = new Vector<AircaftTrackingSelectionListener>();

	private void initialize(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel) {
		this.wwd = this.createWorldWindow();
		this.wwd.setPreferredSize(canvasSize);

		
		// Define an 8x8 square centered on the screen point
		this.CONTROL_POINTS_ATTRIBUTES.setFrameShape(AVKey.SHAPE_CIRCLE);
		this.CONTROL_POINTS_ATTRIBUTES.setLeader(AVKey.SHAPE_NONE);
		this.CONTROL_POINTS_ATTRIBUTES.setAdjustWidthToText(AVKey.SIZE_FIXED);
		this.CONTROL_POINTS_ATTRIBUTES.setSize(new Dimension(8, 8));
		this.CONTROL_POINTS_ATTRIBUTES.setDrawOffset(new Point(0, -4));
		this.CONTROL_POINTS_ATTRIBUTES.setInsets(new Insets(0, 0, 0, 0));
		this.CONTROL_POINTS_ATTRIBUTES.setBorderWidth(0.5);
		this.CONTROL_POINTS_ATTRIBUTES.setCornerRadius(1);
		this.CONTROL_POINTS_ATTRIBUTES.setBackgroundColor(Color.BLUE); // Normal
																		// color
		this.CONTROL_POINTS_ATTRIBUTES.setTextColor(Color.GREEN); // Highlighted
																// color
		this.CONTROL_POINTS_ATTRIBUTES.setHighlightScale(1.2);
		this.CONTROL_POINTS_ATTRIBUTES.setDistanceMaxScale(1); // No distance
																// scaling
		this.CONTROL_POINTS_ATTRIBUTES.setDistanceMinScale(1);
		this.CONTROL_POINTS_ATTRIBUTES.setDistanceMinOpacity(1);
		
		Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		this.wwd.setModel(m);
		
		this.wwd.addSelectListener(new ClickAndGoSelectListener(this.getWwd(), WorldMapLayer.class));
		this.add(this.wwd, BorderLayout.CENTER);
		
		if (includeStatusBar) {
			this.statusBar = new StatusBar();
			this.add(statusBar, BorderLayout.PAGE_END);
			this.statusBar.setEventSource(wwd);
		}


		this.wwjPanel.setPreferredSize(canvasSize);

		// Put the pieces together.
		// this.add(wwjPanel, BorderLayout.CENTER);
		if (includeLayerPanel) {
			this.layerPanel = new LayerPanel(this.wwjPanel.getWwd());
			this.add(this.layerPanel, BorderLayout.WEST);
		}

		if (includeStatsPanel || System.getProperty("gov.nasa.worldwind.showStatistics") != null) {
			this.statsPanel = new StatisticsPanel(this.wwjPanel.getWwd(), new Dimension(250, canvasSize.height));
			this.add(this.statsPanel, BorderLayout.EAST);
		}
		
		ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
		insertBeforeCompass(getWwd(), viewControlsLayer);
		this.getWwd().addSelectListener(new ViewControlsSelectListener(this.getWwd(), viewControlsLayer));

		// Register a rendering exception listener that's notified when
		// exceptions occur during rendering.
		this.wwjPanel.getWwd().addRenderingExceptionListener(new RenderingExceptionListener() {
			public void exceptionThrown(Throwable t) {
				if (t instanceof WWAbsentRequirementException) {
					String message = "Computer does not meet minimum graphics requirements.\n";
					message += "Please install up-to-date graphics driver and try again.\n";
					message += "Reason: " + t.getMessage() + "\n";
					message += "This program will end when you press OK.";

					JOptionPane.showMessageDialog(WorldWindPanel.this, message, "Unable to Start Program",
							JOptionPane.ERROR_MESSAGE);
					System.exit(-1);
				}
			}
		});

		// Search the layer list for layers that are also select listeners and
		// register them with the World
		// Window. This enables interactive layers to be included without
		// specific knowledge of them here.
		for (Layer layer : this.wwjPanel.getWwd().getModel().getLayers()) {
			if (layer instanceof SelectListener) {
				this.getWwd().addSelectListener((SelectListener) layer);
			}
		}

		// Center the application on the screen.
		WWUtil.alignComponent(null, this, AVKey.CENTER);
		// this.setResizable(true);
	}

	protected WorldWindPanel() {
		super(new BorderLayout());
		buildWwjPanel();
		// Add terrain profile layer
		profile.setEventSource(getWwd());
		profile.setFollow(TerrainProfileLayer.FOLLOW_PATH);
		profile.setShowProfileLine(false);
		insertBeforePlacenames(getWwd(), profile);
		
		MeasureTool measureTool = new MeasureTool(this.getWwd());
		measureTool.setController(new MeasureToolController());
		tabbedPane.add(new MeasureToolPanel(this.getWwd(), measureTool));
		tabbedPane.setTitleAt(0, "Waypoint[s] Editor");
		tabbedPane.setSelectedIndex(0);
		this.switchMeasureTool();
		this.add(tabbedPane, BorderLayout.WEST);
		wwPanel = this;
	}

	private void buildWwjPanel() {
		this.wwjPanel = this;
		this.initialize(true, true, false); // Don't include the layer panel;
											
		this.layerTree = new LayerTree(new Offset(20d, 160d, AVKey.PIXELS, AVKey.INSET_PIXELS));
		this.layerTree.getModel().refresh(this.getWwd().getModel().getLayers());

	
		this.hiddenLayer = new RenderableLayer();
		
		this.aircraftLayer = new MarkerLayer();
		aircraftMarkers = new ArrayList<Marker>();
		this.hiddenLayer.addRenderable(this.layerTree);		
		this.getWwd().getModel().getLayers().add(this.hiddenLayer);

		this.layer = new CustomRenderableLayer();

		
        this.wwd.getModel().getLayers().add(this.layer);

		Dimension size = new Dimension(1400, 800);
		this.setPreferredSize(size);

		WWUtil.alignComponent(null, this, AVKey.CENTER);
	}

	private class MeasureToolListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			// Measure shape position list changed - update terrain profile
			if (event.getPropertyName().equals(MeasureTool.EVENT_POSITION_ADD)
					|| event.getPropertyName().equals(MeasureTool.EVENT_POSITION_REMOVE)
					|| event.getPropertyName().equals(MeasureTool.EVENT_POSITION_REPLACE)) {
				updateProfile(((MeasureTool) event.getSource()));
			}
		}
	}

	private void switchMeasureTool() {
		// Disarm last measure tool when changing tab and switching tool
		if (lastTabIndex != -1) {
			MeasureTool mt = ((MeasureToolPanel) tabbedPane.getComponentAt(lastTabIndex)).getMeasureTool();
			mt.setArmed(false);
			mt.removePropertyChangeListener(measureToolListener);
		}
		// Update terrain profile from current measure tool
		lastTabIndex = tabbedPane.getSelectedIndex();
		MeasureTool mt = ((MeasureToolPanel) tabbedPane.getComponentAt(lastTabIndex)).getMeasureTool();
		mt.addPropertyChangeListener(measureToolListener);
		updateProfile(mt);
	}

	private void updateProfile(MeasureTool mt) {
		ArrayList<? extends LatLon> positions = mt.getPositions();
		if (positions != null && positions.size() > 1) {
			profile.setPathPositions(positions);
			profile.setEnabled(true);
		} else
			profile.setEnabled(false);

		getWwd().redraw();
	}

	private WorldWindowGLCanvas getWwd() {
		return wwd;
	}

	private void insertBeforeCompass(WorldWindow wwd, Layer layer) {
		// Insert the layer into the layer list just before the compass.
		int compassPosition = 0;
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers) {
			if (l instanceof CompassLayer)
				compassPosition = layers.indexOf(l);
		}
		layers.add(compassPosition, layer);
	}

	private void insertBeforePlacenames(WorldWindow wwd, Layer layer) {
		// Insert the layer into the layer list just before the placenames.
		int compassPosition = 0;
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers) {
			if (l instanceof PlaceNameLayer)
				compassPosition = layers.indexOf(l);
		}
		layers.add(compassPosition, layer);
	}

	static {
		System.setProperty("java.net.useSystemProxies", "true");
		if (Configuration.isMacOS()) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "World Wind Application");
			System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
			System.setProperty("apple.awt.brushMetalLook", "true");
		} else if (Configuration.isWindowsOS()) {
			System.setProperty("sun.awt.noerasebackground", "true"); 
		}
	}

	private WorldWindowGLCanvas createWorldWindow() {
		return new WorldWindowGLCanvas();
	}

	protected void destroyInstance() {
		wwd.shutdown();
	}

	protected void fireSBSTrackingEvent(boolean isSelected) {
		Iterator<AircaftTrackingSelectionListener> e = aircraftTrackingSelectionListeners.iterator();
		while (e.hasNext())
			e.next().sbsTrackingSelectionEvent(isSelected);
	}

	protected void addAircraftTrackingSelectionListener(AircaftTrackingSelectionListener listeners) {
		this.aircraftTrackingSelectionListeners.add(listeners);
	}

	static protected WorldWindPanel getWorldWindPanel() {
		return wwPanel;
	}

	/*	
	 * protected void updateAircraftLayer(Aircraft aircraft) { //
	 * this.aircraftLayer Position pos = aircraft.getCurrentPosition(); Marker
	 * marker = new BasicMarker(
	 * gov.nasa.worldwind.geom.Position.fromDegrees(pos.getLatitude(),
	 * pos.getLongitude(), pos.getAltitude()), new
	 * BasicMarkerAttributes(Material.GRAY, BasicMarkerShape.HEADING_ARROW, 1d,
	 * 10, 5)); marker.setPosition(
	 * gov.nasa.worldwind.geom.Position.fromDegrees(pos.getLatitude(),
	 * pos.getLongitude(), pos.getAltitude()));
	 * marker.setHeading(Angle.fromDegrees(pos.getHeading()));
	 * aircraftMarkers.add(marker); this.getWwd().redraw(); }
	 */

	/*
	 * protected void addAircraftPath(Aircraft aircraft) { Path path = new
	 * Path(pathPositions); ShapeAttributes attrs = new BasicShapeAttributes();
	 * attrs.setOutlineMaterial(new Material(WWUtil.makeRandomColor(null)));
	 * attrs.setOutlineWidth(2d); path.setAttributes(attrs);
	 * path.setValue("flightID", aircraft.getFlightID()); path.setVisible(true);
	 * path.setAltitudeMode(WorldWind.ABSOLUTE);
	 * path.setPathType(AVKey.GREAT_CIRCLE);
	 * aircraftPathLayer.addRenderable(path); this.getWwd().redraw(); }
	 */
	
	protected void addAircraftLayer(Aircraft aircraft){
		this.layer.addRenderable(aircraft.getShapeLayer());          // add shape layer to render layer
        this.layer.addRenderable(aircraft.getControlPointsLayer());  // add control points layer to render layer
	}

	protected void updateAircraftLayer(Aircraft aircraft) {
		aircraft.addControlPoint(aircraft.getHexCode(),aircraft.getCurrentPosition(),	"HEXIdent", aircraft.getHexCode());
		aircraft.getControlPointsLayer().setRenderables(aircraft.getControlPoints());

		this.updateMeasureShape(aircraft);
		//this.addAircraftBeacon(currentPosition, aircraft);
		this.getWwd().redraw();
	}

	

	private void updateMeasureShape(Aircraft aircraft) {
		if (aircraft.getPosition().size() > 1 && this.line == null) {
			// Init polyline
			this.line = new Polyline();
			this.line.setFollowTerrain(false);
			this.line.setLineWidth(this.lineWidth);
			this.line.setColor(this.lineColor);
			this.line.setPathType(Polyline.LINEAR);
			// this.line.setNumSubsegments(this.followTerrain ? 10 : 1);
			aircraft.getShapeLayer().addRenderable(this.line);
		}
		if (aircraft.getPosition().size() < 2 && this.line != null) {
			// Remove line if less then 2 positions
			aircraft.getShapeLayer().removeRenderable(this.line);
			this.line = null;
		}
		// Update current line
		if (aircraft.getPosition().size() > 1 && this.line != null){
			this.line.setPositions(aircraft.getPosition());
		}

		/*
		 * if (this.surfaceShape != null) { // Remove surface shape if necessary
		 * this.shapeLayer.removeRenderable(this.surfaceShape);
		 * this.surfaceShape = null; }
		 */

	}
	
	/*private void addAircraftBeacon(gov.nasa.worldwind.geom.Position position, Aircraft aircraft) {
		PointPlacemark pp = new PointPlacemark(position);
		pp.setLabelText(aircraft.getHexCode());
		pp.setValue(AVKey.DISPLAY_NAME, aircraft.getCallsign() + "," + aircraft.getFlightID() );
		pp.setLineEnabled(false);
		PointPlacemarkAttributes attrs = new PointPlacemarkAttributes();
		//attrs.setImageAddress("gov/nasa/worldwindx/examples/images/audioicon-64.png");
		attrs.setImageColor(new Color(1f, 1f, 1f, 0.6f));
		attrs.setScale(0.6);
		// attrs.setImageOffset(new Offset(19d, 8d, AVKey.PIXELS,
		// AVKey.PIXELS));
		attrs.setLabelOffset(new Offset(0.9d, 0.6d, AVKey.FRACTION, AVKey.FRACTION));
		pp.setAttributes(attrs);
		this.shapeLayer.addRenderable(pp);
	}*/
     

	protected void removeAircraft(Aircraft aircraft) {
		 this.layer.removeRenderable(aircraft.getShapeLayer());          // add shape layer to render layer
	     this.layer.removeRenderable(aircraft.getControlPointsLayer());
	     aircraft.getControlPoints().clear();
	     this.getWwd().redraw();
	}
}
