/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gichanga;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.util.*;
import gov.nasa.worldwind.util.layertree.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.ogc.kml.*;
import gov.nasa.worldwind.ogc.kml.impl.KMLController;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.util.*;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwindx.examples.util.*;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.URL;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;
import gov.nasa.worldwind.geom.LatLon;

/**
 * An example application that allows the user to import a KML or KMZ file as a layer. The contents of the file are
 * displayed in a feature tree. Click on KML features in the tree to navigate the view to the feature. Clicking on
 * features on the globe will open an info balloon for the feature, if the feature provides a description. Use the File
 * menu to open a document from a local file or from a URL.
 *
 * @author tag
 * @version $Id: KMLViewer.java 1 2011-07-16 23:22:47Z dcollins $
 */
 public class WorldWindPanel extends JPanel {
        protected LayerTree layerTree;
        protected RenderableLayer hiddenLayer;

        protected HotSpotController hotSpotController;
        protected BalloonController balloonController;
		
		 protected WorldWindowGLCanvas wwd;
        protected StatusBar statusBar;
        protected ToolTipController toolTipController;
        protected HighlightController highlightController;
		protected WorldWindPanel wwjPanel;
		
        protected LayerPanel layerPanel;
        protected StatisticsPanel statsPanel;
		private Dimension canvasSize = new Dimension(800, 600);
		
		private int lastTabIndex = -1;
		private final JTabbedPane tabbedPane = new JTabbedPane();
		private TerrainProfileLayer profile = new TerrainProfileLayer();
		private PropertyChangeListener measureToolListener = new MeasureToolListener();
		
		protected void initialize(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel){
            this.wwd = this.createWorldWindow();
            this.wwd.setPreferredSize(canvasSize);

            // Create the default model as described in the current worldwind properties.
            Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
            this.wwd.setModel(m);

            // Setup a select listener for the worldmap click-and-go feature
            this.wwd.addSelectListener(new ClickAndGoSelectListener(this.getWwd(), WorldMapLayer.class));

            this.add(this.wwd, BorderLayout.CENTER);
            if (includeStatusBar)
            {
                this.statusBar = new StatusBar();
                this.add(statusBar, BorderLayout.PAGE_END);
                this.statusBar.setEventSource(wwd);
            }

            // Add controllers to manage highlighting and tool tips.
            this.toolTipController = new ToolTipController(this.getWwd(), AVKey.DISPLAY_NAME, null);
            this.highlightController = new HighlightController(this.getWwd(), SelectEvent.ROLLOVER);
			
            
            this.wwjPanel.setPreferredSize(canvasSize);

            // Put the pieces together.
           // this.add(wwjPanel, BorderLayout.CENTER);
            if (includeLayerPanel)
            {
                this.layerPanel = new LayerPanel(this.wwjPanel.getWwd(), null);
                this.add(this.layerPanel, BorderLayout.WEST);
            }

            if (includeStatsPanel || System.getProperty("gov.nasa.worldwind.showStatistics") != null)
            {
                this.statsPanel = new StatisticsPanel(this.wwjPanel.getWwd(), new Dimension(250, canvasSize.height));
                this.add(this.statsPanel, BorderLayout.EAST);
            }

            // Create and install the view controls layer and register a controller for it with the World Window.
            ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
            insertBeforeCompass(getWwd(), viewControlsLayer);
            this.getWwd().addSelectListener(new ViewControlsSelectListener(this.getWwd(), viewControlsLayer));

            // Register a rendering exception listener that's notified when exceptions occur during rendering.
            this.wwjPanel.getWwd().addRenderingExceptionListener(new RenderingExceptionListener()
            {
                public void exceptionThrown(Throwable t)
                {
                    if (t instanceof WWAbsentRequirementException)
                    {
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

            // Search the layer list for layers that are also select listeners and register them with the World
            // Window. This enables interactive layers to be included without specific knowledge of them here.
            for (Layer layer : this.wwjPanel.getWwd().getModel().getLayers())
            {
                if (layer instanceof SelectListener)
                {
                    this.getWwd().addSelectListener((SelectListener) layer);
                }
            }

            // Center the application on the screen.
            WWUtil.alignComponent(null, this, AVKey.CENTER);
            //this.setResizable(true);
        }

        public WorldWindPanel(double lon, double lat, double alt, int heading, int pitch) {
			super(new BorderLayout());	
			Configuration.setValue(AVKey.INITIAL_LATITUDE, lon);
			Configuration.setValue(AVKey.INITIAL_LONGITUDE, lat);
			Configuration.setValue(AVKey.INITIAL_ALTITUDE, alt);
			Configuration.setValue(AVKey.INITIAL_HEADING, heading);
			Configuration.setValue(AVKey.INITIAL_PITCH, pitch);
			buildWwjPanel();
		}
		
		public WorldWindPanel() {
			super(new BorderLayout());	
			buildWwjPanel();
		}
		
		private void buildWwjPanel () {
			this.wwjPanel = this;
            this.initialize(true, false, false); // Don't include the layer panel; we're using the on-screen layer tree.

            // Add the on-screen layer tree, refreshing model with the WorldWindow's current layer list. We
            // intentionally refresh the tree's model before adding the layer that contains the tree itself. This
            // prevents the tree's layer from being displayed in the tree itself.
            this.layerTree = new LayerTree(new Offset(20d, 160d, AVKey.PIXELS, AVKey.INSET_PIXELS));
            this.layerTree.getModel().refresh(this.getWwd().getModel().getLayers());

            // Set up a layer to display the on-screen layer tree in the WorldWindow. This layer is not displayed in
            // the layer tree's model. Doing so would enable the user to hide the layer tree display with no way of
            // bringing it back.
            this.hiddenLayer = new RenderableLayer();
            this.hiddenLayer.addRenderable(this.layerTree);
            this.getWwd().getModel().getLayers().add(this.hiddenLayer);

            // Add a controller to handle input events on the layer selector and on browser balloons.
            this.hotSpotController = new HotSpotController(this.getWwd());

           

            // Add a controller to display balloons when placemarks are clicked. We override the method addDocumentLayer
            // so that loading a KML document by clicking a KML balloon link displays an entry in the on-screen layer
            // tree.
            this.balloonController = new BalloonController(this.getWwd())
            {
                @Override
                protected void addDocumentLayer(KMLRoot document)
                {
                   // addKMLLayer(document);
                }
            };

            // Give the KML app controller a reference to the BalloonController so that the app controller can open
            // KML feature balloons when feature's are selected in the on-screen layer tree.
            
            // Size the World Window to take up the space typically used by the layer panel.
			
			// Add terrain profile layer
            profile.setEventSource(getWwd());
            profile.setFollow(TerrainProfileLayer.FOLLOW_PATH);
            profile.setShowProfileLine(false);
            insertBeforePlacenames(getWwd(), profile);
			
			tabbedPane.add(new JPanel());
            tabbedPane.setTitleAt(0, "+");
            tabbedPane.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent changeEvent)
                {
                    if (tabbedPane.getSelectedIndex() == 0)
                    {
                        // Add new measure tool in a tab when '+' selected
                        MeasureTool measureTool = new MeasureTool(getWwd());
                        measureTool.setController(new MeasureToolController());
                        tabbedPane.add(new MeasureToolPanel(getWwd(), measureTool));
                        tabbedPane.setTitleAt(tabbedPane.getTabCount() - 1, "" + (tabbedPane.getTabCount() - 1));
                        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                        switchMeasureTool();
                    }
                    else
                    {
                        switchMeasureTool();
                    }
                }
            });

            // Add measure tool control panel to tabbed pane
            MeasureTool measureTool = new MeasureTool(this.getWwd());
            measureTool.setController(new MeasureToolController());
            tabbedPane.add(new MeasureToolPanel(this.getWwd(), measureTool));
            tabbedPane.setTitleAt(1, "1");
            tabbedPane.setSelectedIndex(1);
            switchMeasureTool();

            WorldWindPanel.this.add(tabbedPane, BorderLayout.WEST);
			
			
            Dimension size = new Dimension(1400, 800);
            this.setPreferredSize(size);
            
            WWUtil.alignComponent(null, this, AVKey.CENTER);
        }
    

	 private class MeasureToolListener implements PropertyChangeListener
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                // Measure shape position list changed - update terrain profile
                if (event.getPropertyName().equals(MeasureTool.EVENT_POSITION_ADD)
                        || event.getPropertyName().equals(MeasureTool.EVENT_POSITION_REMOVE)
                        || event.getPropertyName().equals(MeasureTool.EVENT_POSITION_REPLACE))
                {
                    updateProfile(((MeasureTool)event.getSource()));
                }
            }
        }

        private void switchMeasureTool()
        {
            // Disarm last measure tool when changing tab and switching tool
            if (lastTabIndex != -1)
            {
                MeasureTool mt = ((MeasureToolPanel)tabbedPane.getComponentAt(lastTabIndex)).getMeasureTool();
                mt.setArmed(false);
                mt.removePropertyChangeListener(measureToolListener);
            }
            // Update terrain profile from current measure tool
            lastTabIndex = tabbedPane.getSelectedIndex();
            MeasureTool mt = ((MeasureToolPanel)tabbedPane.getComponentAt(lastTabIndex)).getMeasureTool();
            mt.addPropertyChangeListener(measureToolListener);
            updateProfile(mt);
        }

        private void updateProfile(MeasureTool mt)
        {
            ArrayList<? extends LatLon> positions = mt.getPositions();
            if (positions != null && positions.size() > 1)
            {
                profile.setPathPositions(positions);
                profile.setEnabled(true);
            }
            else
                profile.setEnabled(false);
            
            getWwd().redraw();
        }
		
   	 public Dimension getCanvasSize()
        {
            return canvasSize;
        }

        public WorldWindPanel getWwjPanel()
        {
            return wwjPanel;
        }

         public WorldWindowGLCanvas getWwd()
        {
            return wwd;
        }

        public StatusBar getStatusBar()
        {
            return this.wwjPanel.getStatusBar();
        }

        public LayerPanel getLayerPanel()
        {
            return layerPanel;
        }

        public StatisticsPanel getStatsPanel()
        {
            return statsPanel;
        }

        public void setToolTipController(ToolTipController controller)
        {
            if (this.wwjPanel.toolTipController != null)
                this.wwjPanel.toolTipController.dispose();

            this.wwjPanel.toolTipController = controller;
        }

        public void setHighlightController(HighlightController controller)
        {
            if (this.wwjPanel.highlightController != null)
                this.wwjPanel.highlightController.dispose();

            this.wwjPanel.highlightController = controller;
        }
    

    public static void insertBeforeCompass(WorldWindow wwd, Layer layer) {
        // Insert the layer into the layer list just before the compass.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof CompassLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }

    public static void insertBeforePlacenames(WorldWindow wwd, Layer layer)
    {
        // Insert the layer into the layer list just before the placenames.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof PlaceNameLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }

    public static void insertAfterPlacenames(WorldWindow wwd, Layer layer)
    {
        // Insert the layer into the layer list just after the placenames.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof PlaceNameLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition + 1, layer);
    }

    public static void insertBeforeLayerName(WorldWindow wwd, Layer layer, String targetName)
    {
        // Insert the layer into the layer list just before the target layer.
        int targetPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l.getName().indexOf(targetName) != -1)
            {
                targetPosition = layers.indexOf(l);
                break;
            }
        }
        layers.add(targetPosition, layer);
    }

    static {
        System.setProperty("java.net.useSystemProxies", "true");
        if (Configuration.isMacOS()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "World Wind Application");
            System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
            System.setProperty("apple.awt.brushMetalLook", "true");
        }
        else if (Configuration.isWindowsOS()) {
            System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing
        }
    }
	
	protected WorldWindowGLCanvas createWorldWindow() {
            return new WorldWindowGLCanvas();
    }
	
	public void destroyInstance(){
		wwd.shutdown();
	}

    public static void main(String[] args) {
        JFrame frame = new JFrame();
		WorldWindPanel worldWindPanel = new WorldWindPanel(36.59979, -1.20566, 20000e3, 0, 0);
		frame.getContentPane().add(worldWindPanel);
		frame.setSize(1000,800);
		frame.setVisible(true);
    }
}
