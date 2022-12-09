package com.declspec.gichanga;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

public class DataProcessAndUpload extends JPanel implements ActionListener, ListSelectionListener {
	private JButton btnImportImages = new JButton("Import Images");
	private JButton btnExportMapImages = new JButton("Export Map Images");
	private JButton btnExecute = new JButton("Execute");
	
	private JRadioButton rdbtnSingleTask = new JRadioButton("Single Processing");
	private JRadioButton rdbtnMultiprocessing = new JRadioButton("Multiprocessing");
	
	private JSplitPane splitPane = new JSplitPane();
	private JSplitPane mainSplitPane = new JSplitPane();
	private JScrollPane scrollPane = null;
	private JScrollPane imgViewer = null;
	private JList displayList = null;
	/**
	 * Create the panel.
	 */
	public DataProcessAndUpload() {
		setLayout(new BorderLayout(0, 0));
		
		add(mainSplitPane);
		mainSplitPane.setDividerLocation(250);
		
		JPanel jplImageProcessor = new JPanel();
		jplImageProcessor.setLayout(null);
		jplImageProcessor.setBorder(new TitledBorder(null, "Image Processor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainSplitPane.setLeftComponent(jplImageProcessor);
		
		btnImportImages.addActionListener(this);
		btnImportImages.setBounds(10, 22, 164, 23);
		jplImageProcessor.add(btnImportImages);
		
		
		btnExportMapImages.setBounds(10, 56, 164, 23);
		jplImageProcessor.add(btnExportMapImages);
		
		JPanel jpnProcessing = new JPanel();
		jpnProcessing.setBorder(new TitledBorder(null, "Execute Processing Engines", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jpnProcessing.setBounds(10, 96, 229, 386);
		jplImageProcessor.add(jpnProcessing);
		jpnProcessing.setLayout(null);
		
		btnExecute.addActionListener(this);
		btnExecute.setMnemonic('E');
		btnExecute.setToolTipText("Execute Image processing commands.");
		btnExecute.setBounds(6, 307, 89, 23);
		jpnProcessing.add(btnExecute);
		
		JCheckBox chckbxOrthomosaics = new JCheckBox("Orthomosaics");
		chckbxOrthomosaics.setToolTipText("Georeferenced, orthorectified maps");
		chckbxOrthomosaics.setBounds(6, 19, 155, 23);
		jpnProcessing.add(chckbxOrthomosaics);
		
		JCheckBox chckbxPointClouds = new JCheckBox("Point Clouds");
		chckbxPointClouds.setToolTipText("Georeferenced, filtered and classified dense point clouds");
		chckbxPointClouds.setMnemonic('P');
		chckbxPointClouds.setBounds(6, 45, 99, 23);
		jpnProcessing.add(chckbxPointClouds);
		
		JCheckBox chckbxElevationModels = new JCheckBox("Elevation Models");
		chckbxElevationModels.setToolTipText("Georeferenced digital elevation models (DSMs and DTMs)");
		chckbxElevationModels.setBounds(6, 71, 189, 23);
		jpnProcessing.add(chckbxElevationModels);
		
		JCheckBox chckbx3DModels = new JCheckBox("3D Models");
		chckbx3DModels.setToolTipText("Textured 3D models in .OBJ format");
		chckbx3DModels.setBounds(6, 97, 99, 23);
		jpnProcessing.add(chckbx3DModels);
		
		JCheckBox chckbxMeasurements = new JCheckBox("Measurements");
		chckbxMeasurements.setToolTipText("Make volume and area measurements with ease, track stockpiles");
		chckbxMeasurements.setMnemonic('M');
		chckbxMeasurements.setBounds(6, 123, 170, 23);
		jpnProcessing.add(chckbxMeasurements);
		
		JCheckBox chckbxPlantHealth = new JCheckBox("Plant Health");
		chckbxPlantHealth.setToolTipText("Easily compute NDVI, VARI, GNDVI and many other Indexes");
		chckbxPlantHealth.setMnemonic('P');
		chckbxPlantHealth.setBounds(6, 149, 99, 23);
		jpnProcessing.add(chckbxPlantHealth);
		
		JCheckBox chckbxGCPs = new JCheckBox("Ground Control Points (GCPs)");
		chckbxGCPs.setToolTipText("Create and use GCPs for additiona accuracy.");
		chckbxGCPs.setBounds(6, 175, 217, 23);
		jpnProcessing.add(chckbxGCPs);
		
		rdbtnSingleTask.setMnemonic('S');
		rdbtnSingleTask.setBounds(6, 241, 155, 23);
		rdbtnSingleTask.addActionListener(this);
		jpnProcessing.add(rdbtnSingleTask);
		
		rdbtnMultiprocessing.setSelected(true);
		rdbtnMultiprocessing.setMnemonic('u');
		rdbtnMultiprocessing.setBounds(6, 261, 170, 23);
		rdbtnMultiprocessing.addActionListener(this);
		jpnProcessing.add(rdbtnMultiprocessing);
		
		JButton btnAddNew = new JButton("Add New Processing Engine");
		btnAddNew.setMnemonic('A');
		btnAddNew.setBounds(6, 333, 189, 23);
		jpnProcessing.add(btnAddNew);
		
		JCheckBox chckbxMultispectral = new JCheckBox("Multispectral");
		chckbxMultispectral.setToolTipText("Process Multispectral Images");
		chckbxMultispectral.setBounds(6, 287, 170, 23);
		jpnProcessing.add(chckbxMultispectral);
		
		JCheckBox chckbxContours = new JCheckBox("Contours");
		chckbxContours.setToolTipText("Preview and export elevation contours to AutoCAD, Shapefile, Geopackage.");
		chckbxContours.setBounds(6, 201, 99, 23);
		jpnProcessing.add(chckbxContours);
		
		JPanel jplImageList = new JPanel();
		mainSplitPane.setRightComponent(jplImageList);
		jplImageList.setLayout(new BorderLayout(0, 0));
		
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		jplImageList.add(splitPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Image Viewer", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		image.setVisible(true);
		
		imgViewer = new JScrollPane(image);
		imgViewer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		imgViewer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		imgViewer.setViewportBorder(new LineBorder(Color.BLUE));
		panel.add(imgViewer, BorderLayout.CENTER);
		/*System.out.println(
				new File("res/orthomosaic.png").getAbsolutePath()
				);
		System.exit(ABORT);*/
	    scrollPane = new JScrollPane();
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    scrollPane.setViewportBorder(new LineBorder(Color.BLUE));
	    splitPane.setLeftComponent(scrollPane);
		splitPane.setDividerLocation(450);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()== btnImportImages) { 
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int returnValue = jfc.showOpenDialog(DataProcessAndUpload.this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
			    File selectedFile = jfc.getSelectedFile();
			    // Display selected file in console
			    displayList = new JList(selectedFile.listFiles());
			    splitPane.setLeftComponent(null);
			    
			    displayList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		        displayList.setCellRenderer(new FileCellRenderer());
		        displayList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
		        displayList.setName("displayList");
		        displayList.addListSelectionListener(this);
		        //displayList.setVisibleRowCount(-1);
		        scrollPane = new JScrollPane(displayList);
		        splitPane.setLeftComponent(scrollPane);
			} else 
			    System.out.println("No File Selected!");
			
		}else if (e.getSource()== btnExecute) { 
			
		}else if (e.getSource()== rdbtnMultiprocessing) { 
			if (rdbtnMultiprocessing.isSelected())
				rdbtnSingleTask.setSelected(false);
		}else if (e.getSource()== rdbtnSingleTask) { 
			if (rdbtnSingleTask.isSelected())
				rdbtnMultiprocessing.setSelected(false);
		}
	}
		
	
	/*private void populateScrollpane(File folder) {
		File files[] = folder.listFiles(); 
		String fileName = "";
		if (files == null) {
			fileName = "" + folder.getName();
			JLabel img = new JLabel(new ImageIcon(new File("res/images.png").getAbsolutePath()));
			img.setText(fileName);
			img.setVisible(true);
        	scrollPane.getViewport().add(img);
            System.out.println(fileName);
		} else {
        for (File file : files) {
            if (file.isDirectory()) {
                populateScrollpane(file); // Calls same method again.
            } else {
            	fileName = "" + file.getName();
    			JLabel img = new JLabel(new ImageIcon(new File("res/images.png").getAbsolutePath()));
    			img.setText(fileName);
    			img.setVisible(true);
            	scrollPane.getViewport().add(img);
                System.out.println(fileName);                
            }
        }
		}
    }*/
	
	static private boolean selected = false;
	static private String selectedFile = "";
	static private JLabel image = new JLabel();

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == displayList) {
			if (!selected) {
				selectedFile = displayList.getSelectedValue().toString();
				image.setIcon(new ImageIcon(selectedFile));		
				selected = true;
				System.out.println(selectedFile);
				selectedFile = "";
			} else
				selected = false;
		}
	}
}
