package com.declspec.gichanga;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.JScrollPane;
import javax.swing.JDesktopPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Component;
import javax.swing.ScrollPaneConstants;
import javax.swing.JCheckBox;

public class MapViewer extends JPanel implements ListSelectionListener {

	static private JLabel image = new JLabel();
	private JList displayList = null;
	private JScrollPane scrollPane = null;
	
	public MapViewer() {
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane jspMapView = new JSplitPane();
		jspMapView.setDividerLocation(250);
		add(jspMapView);
		
		JPanel jplCommandPanel = new JPanel();
		jspMapView.setLeftComponent(jplCommandPanel);
		jplCommandPanel.setLayout(null);
		
		JPanel jplMapView = new JPanel();
		jspMapView.setRightComponent(jplMapView);
		jplMapView.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane(image);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Projects List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 11, 229, 233);
		jplCommandPanel.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JButton btnExportData = new JButton("Export");
		btnExportData.setMnemonic('E');
		btnExportData.setBounds(23, 412, 89, 23);
		jplCommandPanel.add(btnExportData);
		
		displayList = new JList();
		this.loadProjects();
		JScrollPane jspCommandPane = new JScrollPane(displayList);
		jspCommandPane.setViewportBorder(new LineBorder(new Color(0, 0, 255), 1, true));
		jspCommandPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jspCommandPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel.add(jspCommandPane, BorderLayout.CENTER);
		
		JCheckBox chckbxMeasurements = new JCheckBox("Measurements");
		chckbxMeasurements.setToolTipText("Make volume and area measurements with ease, track stockpiles");
		chckbxMeasurements.setMnemonic('M');
		chckbxMeasurements.setBounds(10, 277, 170, 23);
		jplCommandPanel.add(chckbxMeasurements);
		
		JCheckBox chckbxPlantHealth = new JCheckBox("Plant Health");
		chckbxPlantHealth.setToolTipText("Easily compute NDVI, VARI, GNDVI and many other Indexes");
		chckbxPlantHealth.setMnemonic('P');
		chckbxPlantHealth.setBounds(10, 303, 99, 23);
		jplCommandPanel.add(chckbxPlantHealth);
		
		JCheckBox chckbxGCPs = new JCheckBox("Ground Control Points (GCPs)");
		chckbxGCPs.setToolTipText("Create and use GCPs for additiona accuracy.");
		chckbxGCPs.setBounds(10, 329, 217, 23);
		jplCommandPanel.add(chckbxGCPs);
		
		JCheckBox chckbxContours = new JCheckBox("Contours");
		chckbxContours.setToolTipText("Preview and export elevation contours to AutoCAD, Shapefile, Geopackage.");
		chckbxContours.setBounds(10, 355, 99, 23);
		jplCommandPanel.add(chckbxContours);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportBorder(new LineBorder(Color.BLUE));
		jplMapView.add(scrollPane);
		
	}
	
	private void loadProjects() {
		try {
			File selectedFile = new File("C:\\usr\\asr\\od-GCS\\projects");
		    displayList = new JList(selectedFile.listFiles());
		    
		    displayList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
	        displayList.setCellRenderer(new FileCellRenderer());
	        displayList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
	        displayList.setName("displayList");
	        displayList.addListSelectionListener(this);
	        
	        //displayList.setVisibleRowCount(-1);
		}catch(Exception e) {e.printStackTrace();}
	}
	
	static private boolean selected = false;

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == displayList) {
			if (!selected) {
				String selectedPrj = displayList.getSelectedValue().toString();
				System.out.println(selectedPrj);
				image.setIcon(new ImageIcon(selectedPrj));
				selected = true;
				selectedPrj = "";
			} else
				selected = false;
		}
	}
}
