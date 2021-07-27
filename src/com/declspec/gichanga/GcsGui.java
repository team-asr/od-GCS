package com.declspec.gichanga;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Panel;
import eu.hansolo.steelseries.extras.Horizon;
import eu.hansolo.steelseries.gauges.Radar;
import eu.hansolo.steelseries.extras.LightBulb;
import eu.hansolo.steelseries.extras.Clock;
import javax.swing.JTabbedPane;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import eu.hansolo.steelseries.gauges.RadialCounter;
import eu.hansolo.steelseries.gauges.DisplaySingle;
import java.awt.Font;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import eu.hansolo.steelseries.tools.LcdColor;
import java.awt.Color;

public class GcsGui extends JFrame
		implements ActionListener, ChangeListener, AircaftTrackingSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	static private GcsGui gcsGui = null;
	private WorldWindPanel missionPlanner = new WorldWindPanel();
	// private JPanel missionPlanner = new JPanel();

	static protected GcsGui getGui() {
		return gcsGui;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gcsGui = new GcsGui();
					Runtime.getRuntime().addShutdownHook(gcsGui.new ShutdownHookThread());
					gcsGui.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	class ShutdownHookThread extends Thread {
		/**
		 * Ni vizuri kuhifadhi chaguo la mtumizi, ama?
		 * 
		 * @since 1.0.0.0
		 * @author gichangA
		 */
		public void run() {
			if (getGui().synchroniseProperties) {
				String prjSource = getGui().getProjectSource();
				String mplabSource = getGui().getMPLABDirectory();
				Resources.getResources().saveResources(new String[] { "udb_prj_source", "mplab_directory" },
						new String[] { prjSource, mplabSource });
			}
		}
	}

	/**
	 * Create the frame.
	 */
	public GcsGui() {
		setTitle("gichangA\'s Ground Control System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1472, 961);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, "name_1761643972904");

		//JPanel missionPlanner = new JPanel();
		missionPlanner.addAircraftTrackingSelectionListener(this);
		tabbedPane.addTab("UDB Mission Planner (Logo Editor)", null, missionPlanner, null);

		JLabel lblNewLabel_2 = new JLabel("New label");
		lblNewLabel_2.setIcon(new ImageIcon("C:\\Users\\mwaura_m\\workspace\\GCS\\wwj.png"));
		// missionPlanner.add(lblNewLabel_2);
		// panel_7.add((wwjPanel);

		tabbedPane.addTab("UDB Options Editor", null, udbOptionsEditor, null);
		udbOptionsEditor.setLayout(new CardLayout(0, 0));
		JSplitPane udbOptionsSplitView = new JSplitPane();
		udbOptionsSplitView.setDividerLocation(900);
		JPanel udbOptionsEditorcommands = new JPanel();
		udbOptionsEditor.add(udbOptionsSplitView, "name_422828649896718");
		udbOptionsSplitView.add(udbOptionsEditorcommands, JSplitPane.RIGHT);
		udbOptionsEditorcommands.setLayout(null);
		btnLoad.setBounds(10, 11, 89, 23);
		udbOptionsEditorcommands.add(btnLoad);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Output Window", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 76, 415, 429);
		udbOptionsEditorcommands.add(panel);
		panel.setLayout(new CardLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, "name_1599524314803");
		textPaneCommandResults.setText("Output from PIC3 Compiler & Debugger");
		textPaneCommandResults.setForeground(Color.WHITE);
		textPaneCommandResults.setBackground(Color.GRAY);
		textPaneCommandResults.setEditable(false);

		scrollPane.setViewportView(textPaneCommandResults);
		btnCompile.setBounds(336, 504, 89, 23);
		btnCompile.addActionListener(this);
		udbOptionsEditorcommands.add(btnCompile);

		btnSave.setBounds(10, 45, 89, 23);
		btnSave.addActionListener(this);
		udbOptionsEditorcommands.add(btnSave);
		udbPicOptions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Project Settings",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		udbPicOptions.setBounds(10, 555, 415, 176);

		udbOptionsEditorcommands.add(udbPicOptions);
		udbPicOptions.setLayout(null);

		JLabel lblSourceDirectory = new JLabel("Source File");
		lblSourceDirectory.setBounds(10, 27, 102, 14);
		udbPicOptions.add(lblSourceDirectory);

		txtProjectSourceFile = new JTextField();
		txtProjectSourceFile.setEditable(false);
		txtProjectSourceFile.setBounds(114, 24, 291, 20);
		udbPicOptions.add(txtProjectSourceFile);
		txtProjectSourceFile.setColumns(10);

		JLabel lblMplabcDirectory = new JLabel("MPLABC30 Directory");
		lblMplabcDirectory.setBounds(10, 52, 102, 20);
		udbPicOptions.add(lblMplabcDirectory);

		txtMPLABDirectory = new JTextField();
		txtMPLABDirectory.setEditable(false);
		txtMPLABDirectory.setColumns(10);
		txtMPLABDirectory.setBounds(114, 52, 291, 20);
		udbPicOptions.add(txtMPLABDirectory);

		btnSelectSource.addActionListener(this);
		btnSelectSource.setBounds(284, 83, 121, 23);
		udbPicOptions.add(btnSelectSource);

		btnSelectMplab.addActionListener(this);
		btnSelectMplab.setBounds(284, 120, 121, 23);
		udbPicOptions.add(btnSelectMplab);

		JPanel panel_12 = new JPanel();
		panel_12.setBorder(new TitledBorder(null, "Backups", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_12.setBounds(109, 11, 316, 66);
		udbOptionsEditorcommands.add(panel_12);
		panel_12.setLayout(null);

		chckbxMakeBackup.setBounds(30, 19, 219, 23);
		chckbxMakeBackup.addActionListener(this);
		panel_12.add(chckbxMakeBackup);

		chckbxMakeBackupBefore.setEnabled(false);
		chckbxMakeBackupBefore.addActionListener(this);
		chckbxMakeBackupBefore.setBounds(30, 36, 219, 23);
		panel_12.add(chckbxMakeBackupBefore);
		btnLoad.addActionListener(this);

		uDbOptionsTab.addTab("Main Option Settings", null, jplMainOptionsTab, null);
		jplMainOptionsTab.setLayout(null);

		chckbxCameraStabalized.setBounds(27, 472, 148, 23);
		jplMainOptionsTab.add(chckbxCameraStabalized);

		chckbxUseMagnetometer.setBounds(27, 498, 148, 23);
		jplMainOptionsTab.add(chckbxUseMagnetometer);

		chckbxNoRadio.setBounds(26, 524, 142, 23);
		jplMainOptionsTab.add(chckbxNoRadio);

		JPanel jplPPMSettings = new JPanel();
		jplPPMSettings.setLayout(null);
		jplPPMSettings
				.setBorder(new TitledBorder(null, "PPM Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplPPMSettings.setBounds(178, 478, 165, 131);
		jplMainOptionsTab.add(jplPPMSettings);

		chckbxPPMEnable.setBounds(6, 18, 85, 23);
		jplPPMSettings.add(chckbxPPMEnable);

		JLabel label_6 = new JLabel("Number of Inputs");
		label_6.setBounds(16, 48, 104, 14);
		jplPPMSettings.add(label_6);

		txtPPMNumberOfInputs = new JTextField();
		txtPPMNumberOfInputs.setText("8");
		txtPPMNumberOfInputs.setColumns(10);
		txtPPMNumberOfInputs.setBounds(130, 45, 32, 20);
		jplPPMSettings.add(txtPPMNumberOfInputs);

		chckbxPPMSignalInverted.setBounds(6, 69, 129, 23);
		jplPPMSettings.add(chckbxPPMSignalInverted);

		chckbxPMMAltOutputPins.setBounds(6, 95, 114, 23);
		jplPPMSettings.add(chckbxPMMAltOutputPins);

		JPanel jplHovering = new JPanel();
		jplHovering.setLayout(null);
		jplHovering.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Hovering",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplHovering.setBounds(178, 397, 148, 70);
		jplMainOptionsTab.add(jplHovering);

		chckbxHoveringStabalized.setBounds(6, 18, 147, 23);
		jplHovering.add(chckbxHoveringStabalized);

		chckbxNHoveringWaypoint.setBounds(6, 43, 130, 23);
		jplHovering.add(chckbxNHoveringWaypoint);

		JPanel jplInvertedFlight = new JPanel();
		jplInvertedFlight.setLayout(null);
		jplInvertedFlight.setBorder(
				new TitledBorder(null, "Inverted flight", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplInvertedFlight.setBounds(20, 397, 148, 68);
		jplMainOptionsTab.add(jplInvertedFlight);

		chckbxInvFlightStabalized.setBounds(6, 18, 147, 23);
		jplInvertedFlight.add(chckbxInvFlightStabalized);

		chckbxInvFlightWaypoint.setBounds(6, 43, 130, 23);
		jplInvertedFlight.add(chckbxInvFlightWaypoint);

		JPanel jplSpeedControl = new JPanel();
		jplSpeedControl.setLayout(null);
		jplSpeedControl
				.setBorder(new TitledBorder(null, "Speed Control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplSpeedControl.setBounds(20, 265, 232, 131);
		jplMainOptionsTab.add(jplSpeedControl);

		chckbxSpeedControlEnable.setBounds(6, 20, 85, 23);
		jplSpeedControl.add(chckbxSpeedControlEnable);

		sldrSpeedControl.setValue(100);
		sldrSpeedControl.setToolTipText("Speed (M/S)");
		sldrSpeedControl.setPaintTicks(true);
		sldrSpeedControl.setPaintLabels(true);
		sldrSpeedControl.setMinorTickSpacing(10);
		sldrSpeedControl.setMaximum(300);
		sldrSpeedControl.setMajorTickSpacing(50);
		sldrSpeedControl.setEnabled(false);
		sldrSpeedControl.setBounds(16, 48, 200, 72);
		jplSpeedControl.add(sldrSpeedControl);

		txtIndicatedSpeed = new JTextField();
		txtIndicatedSpeed.setEditable(false);
		txtIndicatedSpeed.setText("100");
		txtIndicatedSpeed.setBounds(178, 21, 44, 20);
		jplSpeedControl.add(txtIndicatedSpeed);
		txtIndicatedSpeed.setColumns(10);

		JPanel jplRacingMode = new JPanel();
		jplRacingMode.setLayout(null);
		jplRacingMode.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Racing Mode",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplRacingMode.setBounds(254, 265, 232, 131);
		jplMainOptionsTab.add(jplRacingMode);

		chckbxRacingModeEnable.setBounds(6, 20, 85, 23);
		jplRacingMode.add(chckbxRacingModeEnable);

		sldRacingModeSpeed.setValue(100);
		sldRacingModeSpeed.setToolTipText("Speed (M/S)");
		sldRacingModeSpeed.setPaintTicks(true);
		sldRacingModeSpeed.setPaintLabels(true);
		sldRacingModeSpeed.setMinorTickSpacing(1);
		sldRacingModeSpeed.setMaximum(10);
		sldRacingModeSpeed.setMajorTickSpacing(10);
		sldRacingModeSpeed.setEnabled(false);
		sldRacingModeSpeed.setBounds(16, 48, 200, 72);
		jplRacingMode.add(sldRacingModeSpeed);

		txtIndiacatedSpeed_Racing = new JTextField();
		txtIndiacatedSpeed_Racing.setText("10");
		txtIndiacatedSpeed_Racing.setEditable(false);
		txtIndiacatedSpeed_Racing.setColumns(10);
		txtIndiacatedSpeed_Racing.setBounds(172, 21, 44, 20);
		jplRacingMode.add(txtIndiacatedSpeed_Racing);

		JPanel panel_6 = new JPanel();
		panel_6.setLayout(null);
		panel_6.setBorder(new TitledBorder(null, "Altitude Hold", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_6.setBounds(602, 309, 255, 376);
		jplMainOptionsTab.add(panel_6);

		JLabel label_10 = new JLabel("Stabalized");
		label_10.setBounds(10, 34, 61, 14);
		panel_6.add(label_10);

		cboAltHoldStabalized.setBounds(66, 34, 132, 20);
		cboAltHoldStabalized.setModel(new DefaultComboBoxModel(altitudeHoldModes));
		panel_6.add(cboAltHoldStabalized);

		JLabel label_15 = new JLabel("Waypoint");
		label_15.setBounds(10, 59, 46, 14);
		panel_6.add(label_15);

		cboAltHoldWaypoint.setBounds(66, 65, 132, 20);
		cboAltHoldWaypoint.setModel(new DefaultComboBoxModel(altitudeHoldModes));
		panel_6.add(cboAltHoldWaypoint);

		JLabel lblHeighttargetmin = new JLabel("HEIGHT_TARGET_MIN");
		lblHeighttargetmin.setBounds(10, 96, 119, 14);
		panel_6.add(lblHeighttargetmin);

		txtHEIGHT_TARGET_MIN = new JTextField();
		txtHEIGHT_TARGET_MIN.setText("25.0");
		txtHEIGHT_TARGET_MIN.setBounds(192, 96, 46, 20);
		panel_6.add(txtHEIGHT_TARGET_MIN);
		txtHEIGHT_TARGET_MIN.setColumns(10);

		JLabel lblHeighttargetmax = new JLabel("HEIGHT_TARGET_MAX");
		lblHeighttargetmax.setBounds(10, 130, 119, 14);
		panel_6.add(lblHeighttargetmax);

		txtHEIGHT_TARGET_MAX = new JTextField();
		txtHEIGHT_TARGET_MAX.setText("100.0");
		txtHEIGHT_TARGET_MAX.setBounds(192, 127, 46, 20);
		panel_6.add(txtHEIGHT_TARGET_MAX);
		txtHEIGHT_TARGET_MAX.setColumns(10);

		JLabel lblHeightmargin = new JLabel("HEIGHT_MARGIN");
		lblHeightmargin.setBounds(10, 164, 119, 14);
		panel_6.add(lblHeightmargin);

		txtHEIGHT_MARGIN = new JTextField();
		txtHEIGHT_MARGIN.setText("10");
		txtHEIGHT_MARGIN.setBounds(192, 158, 46, 20);
		panel_6.add(txtHEIGHT_MARGIN);
		txtHEIGHT_MARGIN.setColumns(10);

		JLabel lblAltholdthrottlemin = new JLabel("ALT_HOLD_THROTTLE_MIN");
		lblAltholdthrottlemin.setBounds(10, 200, 171, 14);
		panel_6.add(lblAltholdthrottlemin);

		txtALT_HOLD_THROTTLE_MIN = new JTextField();
		txtALT_HOLD_THROTTLE_MIN.setText("0.35");
		txtALT_HOLD_THROTTLE_MIN.setBounds(192, 197, 46, 20);
		panel_6.add(txtALT_HOLD_THROTTLE_MIN);
		txtALT_HOLD_THROTTLE_MIN.setColumns(10);

		JLabel lblAltholdthrottlemax = new JLabel("ALT_HOLD_THROTTLE_MAX");
		lblAltholdthrottlemax.setBounds(10, 234, 147, 14);
		panel_6.add(lblAltholdthrottlemax);

		txtALT_HOLD_THROTTLE_MAX = new JTextField();
		txtALT_HOLD_THROTTLE_MAX.setText("1.0");
		txtALT_HOLD_THROTTLE_MAX.setBounds(192, 225, 46, 20);
		panel_6.add(txtALT_HOLD_THROTTLE_MAX);
		txtALT_HOLD_THROTTLE_MAX.setColumns(10);

		JLabel lblAltholdpitchmin = new JLabel("ALT_HOLD_PITCH_MIN");
		lblAltholdpitchmin.setBounds(10, 265, 132, 14);
		panel_6.add(lblAltholdpitchmin);

		txtALT_HOLD_PITCH_MIN = new JTextField();
		txtALT_HOLD_PITCH_MIN.setText("-15.0");
		txtALT_HOLD_PITCH_MIN.setBounds(192, 256, 46, 20);
		panel_6.add(txtALT_HOLD_PITCH_MIN);
		txtALT_HOLD_PITCH_MIN.setColumns(10);

		JLabel lblAltholdpitchmax = new JLabel("ALT_HOLD_PITCH_MAX");
		lblAltholdpitchmax.setBounds(10, 303, 132, 14);
		panel_6.add(lblAltholdpitchmax);

		txtALT_HOLD_PITCH_MAX = new JTextField();
		txtALT_HOLD_PITCH_MAX.setText("15.0");
		txtALT_HOLD_PITCH_MAX.setBounds(191, 300, 47, 20);
		panel_6.add(txtALT_HOLD_PITCH_MAX);
		txtALT_HOLD_PITCH_MAX.setColumns(10);

		JLabel lblAltholdpitchhigh = new JLabel("ALT_HOLD_PITCH_HIGH");
		lblAltholdpitchhigh.setBounds(10, 345, 132, 14);
		panel_6.add(lblAltholdpitchhigh);

		txtALT_HOLD_PITCH_HIGH = new JTextField();
		txtALT_HOLD_PITCH_HIGH.setText("-15.0");
		txtALT_HOLD_PITCH_HIGH.setBounds(192, 331, 46, 20);
		panel_6.add(txtALT_HOLD_PITCH_HIGH);
		txtALT_HOLD_PITCH_HIGH.setColumns(10);

		JPanel panel_8 = new JPanel();
		panel_8.setLayout(null);
		panel_8.setBorder(new TitledBorder(null, "Navigation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_8.setBounds(27, 152, 109, 78);
		jplMainOptionsTab.add(panel_8);

		chckbxNavAileron.setBounds(6, 18, 82, 23);
		panel_8.add(chckbxNavAileron);

		chckbxNavRudder.setBounds(6, 44, 82, 23);
		panel_8.add(chckbxNavRudder);

		JPanel jplStabalization = new JPanel();
		jplStabalization.setLayout(null);
		jplStabalization
				.setBorder(new TitledBorder(null, "Stabalization", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplStabalization.setBounds(368, 11, 213, 160);
		jplMainOptionsTab.add(jplStabalization);

		JPanel jplStabRoll = new JPanel();
		jplStabRoll.setLayout(null);
		jplStabRoll.setBorder(new TitledBorder(null, "Roll", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplStabRoll.setBounds(10, 22, 193, 54);
		jplStabalization.add(jplStabRoll);

		chckbxStabalizationRollRudder.setBounds(6, 21, 82, 23);
		jplStabRoll.add(chckbxStabalizationRollRudder);

		chckbxStabalizationRollAilerons.setBounds(67, 21, 120, 23);
		jplStabRoll.add(chckbxStabalizationRollAilerons);

		JPanel jplStabYaw = new JPanel();
		jplStabYaw.setLayout(null);
		jplStabYaw.setBorder(new TitledBorder(null, "Yaw", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplStabYaw.setBounds(10, 95, 176, 54);
		jplStabalization.add(jplStabYaw);

		chckbxStabalizationYawRudder.setBounds(6, 21, 82, 23);
		jplStabYaw.add(chckbxStabalizationYawRudder);

		chckbxStabalizationYawAilerons.setBounds(69, 21, 101, 23);
		jplStabYaw.add(chckbxStabalizationYawAilerons);

		chckbxStabalizationPitch.setBounds(10, 71, 97, 23);
		jplStabalization.add(chckbxStabalizationPitch);

		JLabel label_16 = new JLabel("Board Type");
		label_16.setToolTipText("");
		label_16.setBounds(61, 14, 85, 14);
		jplMainOptionsTab.add(label_16);

		JLabel label_17 = new JLabel("Board Orientation");
		label_17.setToolTipText("Select Board type");
		label_17.setBounds(26, 39, 142, 14);
		jplMainOptionsTab.add(label_17);

		JLabel label_18 = new JLabel("Airframe Type");
		label_18.setBounds(46, 64, 81, 14);
		jplMainOptionsTab.add(label_18);

		JLabel label_19 = new JLabel("GPS Type");
		label_19.setBounds(73, 98, 46, 14);
		jplMainOptionsTab.add(label_19);

		cboBoardType.setModel(new DefaultComboBoxModel(boardTypes));
		cboBoardType.setToolTipText("Select Board Type");
		cboBoardType.setBounds(127, 11, 125, 20);
		jplMainOptionsTab.add(cboBoardType);

		cboBoardOrientation.setToolTipText("Change the Board Orientation");
		cboBoardOrientation.setModel(new DefaultComboBoxModel(boardOrientation));
		cboBoardOrientation.setBounds(127, 39, 184, 20);
		jplMainOptionsTab.add(cboBoardOrientation);

		cboAirframeType.setToolTipText("Select Airframe type");
		cboAirframeType.setModel(new DefaultComboBoxModel(airframeType));
		cboAirframeType.setBounds(127, 64, 184, 20);
		jplMainOptionsTab.add(cboAirframeType);

		cboGpsType.setToolTipText("Select GPS Type");
		cboGpsType.setModel(new DefaultComboBoxModel(gpsType));
		cboGpsType.setBounds(127, 95, 109, 20);
		jplMainOptionsTab.add(cboGpsType);

		JLabel label_47 = new JLabel("Serial Output Format");
		label_47.setBounds(591, 11, 114, 14);
		jplMainOptionsTab.add(label_47);

		JLabel label_48 = new JLabel("Mavlink SYSID");
		label_48.setBounds(614, 50, 89, 23);
		jplMainOptionsTab.add(label_48);

		cboMavSerialOutputFormat.setBounds(704, 11, 142, 20);
		cboMavSerialOutputFormat.setModel(new DefaultComboBoxModel(mavlinkOutputFormat));
		jplMainOptionsTab.add(cboMavSerialOutputFormat);
		sldMavSYSID.setMaximum(255);
		sldMavSYSID.setMinorTickSpacing(5);
		sldMavSYSID.setPaintLabels(true);
		sldMavSYSID.setPaintTicks(true);

		sldMavSYSID.setBounds(704, 50, 172, 23);
		jplMainOptionsTab.add(sldMavSYSID);

		chckbxWingGainAdjustment.setBounds(27, 547, 165, 23);
		jplMainOptionsTab.add(chckbxWingGainAdjustment);

		chckbxUseOsd.setBounds(27, 573, 97, 23);
		jplMainOptionsTab.add(chckbxUseOsd);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Vehicle and Pilot Identification", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panel_4.setBounds(602, 98, 282, 142);
		jplMainOptionsTab.add(panel_4);
		panel_4.setLayout(null);

		JLabel lblVehicleModelName = new JLabel("Vehicle Model Name");
		lblVehicleModelName.setBounds(10, 24, 192, 14);
		panel_4.add(lblVehicleModelName);

		txtVEHICLE_MODEL_NAME = new JTextField();
		txtVEHICLE_MODEL_NAME.setBounds(132, 15, 140, 20);
		panel_4.add(txtVEHICLE_MODEL_NAME);
		txtVEHICLE_MODEL_NAME.setColumns(10);

		JLabel lblVehicleRegistration = new JLabel("Vehicle Registration");
		lblVehicleRegistration.setBounds(10, 49, 112, 14);
		panel_4.add(lblVehicleRegistration);

		txtVEHICLE_REGISTRATION = new JTextField();
		txtVEHICLE_REGISTRATION.setBounds(132, 43, 140, 20);
		panel_4.add(txtVEHICLE_REGISTRATION);
		txtVEHICLE_REGISTRATION.setColumns(10);

		JLabel lblLeadPilot = new JLabel("Lead Pilot");
		lblLeadPilot.setBounds(10, 74, 46, 14);
		panel_4.add(lblLeadPilot);

		txtLEAD_PILOT = new JTextField();
		txtLEAD_PILOT.setBounds(132, 74, 140, 20);
		panel_4.add(txtLEAD_PILOT);
		txtLEAD_PILOT.setColumns(10);

		JLabel lblDiyDronesUrl = new JLabel("Diy Drones URL");
		lblDiyDronesUrl.setBounds(10, 111, 101, 14);
		panel_4.add(lblDiyDronesUrl);

		txtDIY_DRONES_URL = new JTextField();
		txtDIY_DRONES_URL.setBounds(132, 105, 140, 20);
		panel_4.add(txtDIY_DRONES_URL);
		txtDIY_DRONES_URL.setColumns(10);

		chckbxEnableVtol.setBounds(122, 122, 97, 23);
		jplMainOptionsTab.add(chckbxEnableVtol);

		chckbxRecordfreestackspace.setBounds(27, 628, 209, 23);
		jplMainOptionsTab.add(chckbxRecordfreestackspace);

		JLabel lblFlightplantype = new JLabel("FLIGHT_PLAN_TYPE");
		lblFlightplantype.setBounds(605, 256, 135, 14);
		jplMainOptionsTab.add(lblFlightplantype);

		cboFLIGHT_PLAN_TYPE.setBounds(750, 253, 107, 20);
		cboFLIGHT_PLAN_TYPE.setModel(new DefaultComboBoxModel(flightPlanType));
		jplMainOptionsTab.add(cboFLIGHT_PLAN_TYPE);

		JPanel panel_11 = new JPanel();
		panel_11.setBorder(new TitledBorder(null, "Hardware In the Loop Simulation", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panel_11.setBounds(150, 164, 193, 83);
		jplMainOptionsTab.add(panel_11);
		panel_11.setLayout(null);

		chckbxHilsimEnable.setBounds(6, 22, 97, 23);
		panel_11.add(chckbxHilsimEnable);

		JLabel lblHilsimbaud = new JLabel("HILSIM_BAUD");
		lblHilsimbaud.setBounds(6, 52, 97, 14);
		panel_11.add(lblHilsimbaud);

		cboHILSIM_BAUD.setBounds(84, 49, 99, 20);
		cboHILSIM_BAUD.setModel(new DefaultComboBoxModel(baudRate));
		panel_11.add(cboHILSIM_BAUD);

		JLabel lblRtlpitchdown = new JLabel("RTL_PITCH_DOWN");
		lblRtlpitchdown.setBounds(602, 281, 138, 14);
		jplMainOptionsTab.add(lblRtlpitchdown);

		txtRTL_PITCH_DOWN = new JTextField();
		txtRTL_PITCH_DOWN.setText("0.0");
		txtRTL_PITCH_DOWN.setBounds(750, 284, 46, 20);
		jplMainOptionsTab.add(txtRTL_PITCH_DOWN);
		txtRTL_PITCH_DOWN.setColumns(10);

		JPanel jplThreshholdSettings = new JPanel();
		uDbOptionsTab.addTab("Failsafe Threshold and Mode Switch", null, jplThreshholdSettings, null);
		jplThreshholdSettings.setLayout(null);

		JPanel jplModeSwitchThres = new JPanel();
		jplModeSwitchThres.setLayout(null);
		jplModeSwitchThres.setBorder(
				new TitledBorder(null, "Mode Switch Threshold", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplModeSwitchThres.setBounds(10, 11, 232, 180);
		jplThreshholdSettings.add(jplModeSwitchThres);

		dplModeSwitchThresLOW.setLcdValue(2600.0);
		dplModeSwitchThresLOW.setLcdUnitString("");
		dplModeSwitchThresLOW.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dplModeSwitchThresLOW.setBounds(48, 15, 93, 37);
		jplModeSwitchThres.add(dplModeSwitchThresLOW);

		sldModeSwitchThresLOW.setValue(2600);
		sldModeSwitchThresLOW.setPaintTicks(true);
		sldModeSwitchThresLOW.setMinorTickSpacing(100);
		sldModeSwitchThresLOW.setMinimum(600);
		sldModeSwitchThresLOW.setMaximum(9800);
		sldModeSwitchThresLOW.setMajorTickSpacing(1000);
		sldModeSwitchThresLOW.setBounds(10, 58, 200, 37);
		jplModeSwitchThres.add(sldModeSwitchThresLOW);

		JLabel label_1 = new JLabel("LOW");
		label_1.setBounds(20, 33, 46, 14);
		jplModeSwitchThres.add(label_1);

		sldModeSwitchThresHIGH.setValue(2600);
		sldModeSwitchThresHIGH.setPaintTicks(true);
		sldModeSwitchThresHIGH.setMinorTickSpacing(100);
		sldModeSwitchThresHIGH.setMinimum(600);
		sldModeSwitchThresHIGH.setMaximum(9800);
		sldModeSwitchThresHIGH.setMajorTickSpacing(1000);
		sldModeSwitchThresHIGH.setBounds(10, 135, 200, 37);
		jplModeSwitchThres.add(sldModeSwitchThresHIGH);

		dplModeSwitchThresHIGH.setLcdValue(3400.0);
		dplModeSwitchThresHIGH.setLcdUnitString("");
		dplModeSwitchThresHIGH.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dplModeSwitchThresHIGH.setBounds(48, 95, 93, 37);
		jplModeSwitchThres.add(dplModeSwitchThresHIGH);

		JLabel label_9 = new JLabel("HIGH");
		label_9.setBounds(20, 110, 46, 14);
		jplModeSwitchThres.add(label_9);

		chbxModeSwitch2PositionSwitch.setBounds(14, 189, 196, 23);
		jplThreshholdSettings.add(chbxModeSwitch2PositionSwitch);

		JPanel jplFailsafeInputChannel = new JPanel();
		jplFailsafeInputChannel.setLayout(null);
		jplFailsafeInputChannel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"Failsafe Input Channel", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplFailsafeInputChannel.setBounds(252, 11, 294, 263);
		jplThreshholdSettings.add(jplFailsafeInputChannel);

		dplFailsafeInputChannelLOW.setLcdValue(2600.0);
		dplFailsafeInputChannelLOW.setLcdUnitString("");
		dplFailsafeInputChannelLOW.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dplFailsafeInputChannelLOW.setBounds(48, 15, 93, 37);
		jplFailsafeInputChannel.add(dplFailsafeInputChannelLOW);

		sldFailsafeInputChannelLOW.setValue(2600);
		sldFailsafeInputChannelLOW.setPaintTicks(true);
		sldFailsafeInputChannelLOW.setMinorTickSpacing(100);
		sldFailsafeInputChannelLOW.setMinimum(600);
		sldFailsafeInputChannelLOW.setMaximum(9800);
		sldFailsafeInputChannelLOW.setMajorTickSpacing(1000);
		sldFailsafeInputChannelLOW.setBounds(10, 58, 200, 37);
		jplFailsafeInputChannel.add(sldFailsafeInputChannelLOW);

		JLabel label_49 = new JLabel("LOW");
		label_49.setBounds(20, 33, 46, 14);
		jplFailsafeInputChannel.add(label_49);

		sldFailsafeInputChannelHIGH.setValue(2600);
		sldFailsafeInputChannelHIGH.setPaintTicks(true);
		sldFailsafeInputChannelHIGH.setMinorTickSpacing(100);
		sldFailsafeInputChannelHIGH.setMinimum(600);
		sldFailsafeInputChannelHIGH.setMaximum(9800);
		sldFailsafeInputChannelHIGH.setMajorTickSpacing(1000);
		sldFailsafeInputChannelHIGH.setBounds(10, 135, 200, 37);
		jplFailsafeInputChannel.add(sldFailsafeInputChannelHIGH);

		dplFailsafeInputChannelHIGH.setLcdValue(3400.0);
		dplFailsafeInputChannelHIGH.setLcdUnitString("");
		dplFailsafeInputChannelHIGH.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dplFailsafeInputChannelHIGH.setBounds(48, 95, 93, 37);
		jplFailsafeInputChannel.add(dplFailsafeInputChannelHIGH);

		JLabel label_50 = new JLabel("HIGH");
		label_50.setBounds(20, 110, 46, 14);
		jplFailsafeInputChannel.add(label_50);

		JLabel lblFailsafeInputChannel = new JLabel("Failsafe Input Channel");
		lblFailsafeInputChannel.setBounds(20, 186, 121, 14);
		jplFailsafeInputChannel.add(lblFailsafeInputChannel);

		cboFailsafeInputChannel.setBounds(153, 183, 131, 20);
		cboFailsafeInputChannel.setModel(new DefaultComboBoxModel(inputChannels));
		jplFailsafeInputChannel.add(cboFailsafeInputChannel);

		chbxFailasfeHold.setBounds(20, 230, 97, 23);
		jplFailsafeInputChannel.add(chbxFailasfeHold);

		JLabel lblFailsafeType = new JLabel("Failsafe Type");
		lblFailsafeType.setBounds(20, 209, 121, 14);
		jplFailsafeInputChannel.add(lblFailsafeType);

		cboFailsafeType.setModel(new DefaultComboBoxModel(failsafeTypes));
		cboFailsafeType.setBounds(153, 206, 131, 20);
		jplFailsafeInputChannel.add(cboFailsafeType);

		JPanel jplIoPanel = new JPanel();
		uDbOptionsTab.addTab("I\\O Option Setting", null, jplIoPanel, null);
		jplIoPanel.setLayout(null);

		JPanel jplInputConfig = new JPanel();
		jplInputConfig.setLayout(null);
		jplInputConfig.setBorder(
				new TitledBorder(null, "Input Configuration", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplInputConfig.setBounds(10, 11, 307, 521);
		jplIoPanel.add(jplInputConfig);

		JLabel label = new JLabel("Number of Inputs");
		label.setBounds(10, 27, 104, 14);
		jplInputConfig.add(label);

		JLabel label_20 = new JLabel("Throttle");
		label_20.setBounds(86, 58, 46, 14);
		jplInputConfig.add(label_20);

		JLabel label_21 = new JLabel("Aileron");
		label_21.setBounds(86, 87, 46, 14);
		jplInputConfig.add(label_21);

		JLabel label_22 = new JLabel("Elevator");
		label_22.setBounds(76, 112, 56, 14);
		jplInputConfig.add(label_22);

		JLabel label_23 = new JLabel("Rudder");
		label_23.setBounds(86, 137, 46, 14);
		jplInputConfig.add(label_23);

		JLabel label_24 = new JLabel("Mode Switch");
		label_24.setBounds(55, 159, 79, 14);
		jplInputConfig.add(label_24);

		JLabel label_25 = new JLabel("Camera Pitch");
		label_25.setBounds(53, 187, 79, 14);
		jplInputConfig.add(label_25);

		JLabel label_26 = new JLabel("Camera Yaw");
		label_26.setBounds(53, 212, 79, 14);
		jplInputConfig.add(label_26);

		JLabel label_27 = new JLabel("Camera Mode");
		label_27.setBounds(27, 237, 104, 14);
		jplInputConfig.add(label_27);

		JLabel label_28 = new JLabel("OSD Mode Switch");
		label_28.setBounds(37, 262, 146, 14);
		jplInputConfig.add(label_28);

		JLabel label_29 = new JLabel("Passthrough A");
		label_29.setBounds(53, 296, 79, 14);
		jplInputConfig.add(label_29);

		JLabel label_30 = new JLabel("Passthrough B");
		label_30.setBounds(46, 321, 86, 14);
		jplInputConfig.add(label_30);

		JLabel label_31 = new JLabel("Passthrough C");
		label_31.setBounds(46, 346, 86, 14);
		jplInputConfig.add(label_31);

		JLabel label_32 = new JLabel("Passthrough D");
		label_32.setBounds(46, 371, 86, 14);
		jplInputConfig.add(label_32);

		cboInputConfigThrottle.setBounds(134, 55, 154, 20);
		cboInputConfigThrottle.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigThrottle);

		cboInputConfigAileron.setBounds(134, 84, 154, 20);
		cboInputConfigAileron.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigAileron);

		cboInputConfigElevator.setBounds(134, 109, 154, 20);
		cboInputConfigElevator.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigElevator);

		cboInputConfigRudder.setBounds(134, 134, 154, 20);
		cboInputConfigRudder.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigRudder);

		cboInputConfigModeSwitch.setBounds(134, 159, 154, 20);
		cboInputConfigModeSwitch.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigModeSwitch);

		cboInputConfigCameraPitch.setBounds(134, 184, 154, 20);
		cboInputConfigCameraPitch.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigCameraPitch);

		cboInputConfigCameraYaw.setBounds(134, 209, 154, 20);
		cboInputConfigCameraYaw.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigCameraYaw);

		cboInputConfigCameraMode.setBounds(134, 234, 154, 20);
		cboInputConfigCameraMode.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigCameraMode);

		cboInputConfigOSDModeSwitch.setBounds(134, 262, 154, 20);
		cboInputConfigOSDModeSwitch.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigOSDModeSwitch);

		cboInputConfigPassthroughA.setBounds(134, 287, 154, 20);
		cboInputConfigPassthroughA.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigPassthroughA);

		cboInputConfigPassthroughB.setBounds(134, 312, 154, 20);
		cboInputConfigPassthroughB.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigPassthroughB);

		cboInputConfigPassthroughC.setBounds(134, 337, 154, 20);
		cboInputConfigPassthroughC.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigPassthroughC);

		cboInputConfigPassthroughD.setBounds(134, 368, 154, 20);
		cboInputConfigPassthroughD.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigPassthroughD);

		cboInputConfigNumberOfInputs.setBounds(225, 24, 63, 20);
		cboInputConfigNumberOfInputs.setModel(new DefaultComboBoxModel(noOfinputs));
		jplInputConfig.add(cboInputConfigNumberOfInputs);

		JLabel label_33 = new JLabel("LOGO A");
		label_33.setBounds(76, 396, 79, 14);
		jplInputConfig.add(label_33);

		JLabel label_34 = new JLabel("LOGO B");
		label_34.setBounds(76, 421, 86, 14);
		jplInputConfig.add(label_34);

		JLabel label_35 = new JLabel("LOGO C");
		label_35.setBounds(76, 446, 86, 14);
		jplInputConfig.add(label_35);

		JLabel label_36 = new JLabel("LOGO D");
		label_36.setBounds(76, 480, 86, 14);
		jplInputConfig.add(label_36);

		cboInputConfigLOGOD.setBounds(134, 477, 154, 20);
		cboInputConfigLOGOD.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigLOGOD);

		cboInputConfigLOGOC.setBounds(134, 446, 154, 20);
		cboInputConfigLOGOC.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigLOGOC);

		cboInputConfigLOGOB.setBounds(134, 421, 154, 20);
		cboInputConfigLOGOB.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigLOGOB);

		cboInputConfigLOGOA.setBounds(134, 396, 154, 20);
		cboInputConfigLOGOA.setModel(new DefaultComboBoxModel(channelTypes));
		jplInputConfig.add(cboInputConfigLOGOA);

		JPanel jplOutputConfig = new JPanel();
		jplOutputConfig.setLayout(null);
		jplOutputConfig.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Output Configuration",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplOutputConfig.setBounds(331, 11, 307, 400);
		jplIoPanel.add(jplOutputConfig);

		JLabel label_2 = new JLabel("Number of Outputs");
		label_2.setBounds(10, 27, 104, 14);
		jplOutputConfig.add(label_2);

		JLabel label_3 = new JLabel("Throttle");
		label_3.setBounds(86, 58, 46, 14);
		jplOutputConfig.add(label_3);

		JLabel label_4 = new JLabel("Aileron");
		label_4.setBounds(86, 87, 46, 14);
		jplOutputConfig.add(label_4);

		JLabel label_5 = new JLabel("Elevator");
		label_5.setBounds(76, 112, 56, 14);
		jplOutputConfig.add(label_5);

		JLabel label_7 = new JLabel("Rudder");
		label_7.setBounds(86, 137, 46, 14);
		jplOutputConfig.add(label_7);

		JLabel label_8 = new JLabel("Aileron, Secondary");
		label_8.setBounds(23, 159, 111, 14);
		jplOutputConfig.add(label_8);

		JLabel label_11 = new JLabel("Camera Pitch");
		label_11.setBounds(53, 187, 79, 14);
		jplOutputConfig.add(label_11);

		JLabel label_12 = new JLabel("Camera Yaw");
		label_12.setBounds(53, 212, 79, 14);
		jplOutputConfig.add(label_12);

		JLabel label_13 = new JLabel("Trigger Output");
		label_13.setBounds(37, 262, 146, 14);
		jplOutputConfig.add(label_13);

		JLabel label_14 = new JLabel("Passthrough A");
		label_14.setBounds(53, 296, 79, 14);
		jplOutputConfig.add(label_14);

		JLabel label_37 = new JLabel("Passthrough B");
		label_37.setBounds(46, 321, 86, 14);
		jplOutputConfig.add(label_37);

		JLabel label_38 = new JLabel("Passthrough C");
		label_38.setBounds(46, 346, 86, 14);
		jplOutputConfig.add(label_38);

		JLabel label_39 = new JLabel("Passthrough D");
		label_39.setBounds(46, 371, 86, 14);
		jplOutputConfig.add(label_39);

		cboOutputConfigThrottle.setBounds(134, 55, 154, 20);
		cboOutputConfigThrottle.setModel(new DefaultComboBoxModel(channelTypes));
		jplOutputConfig.add(cboOutputConfigThrottle);

		cboOutputConfigAileron.setBounds(134, 84, 154, 20);
		cboOutputConfigAileron.setModel(new DefaultComboBoxModel(channelTypes));
		jplOutputConfig.add(cboOutputConfigAileron);

		cboOutputConfigElevator.setBounds(134, 109, 154, 20);
		cboOutputConfigElevator.setModel(new DefaultComboBoxModel(channelTypes));
		jplOutputConfig.add(cboOutputConfigElevator);

		cboOutputConfigRudder.setBounds(134, 134, 154, 20);
		cboOutputConfigRudder.setModel(new DefaultComboBoxModel(channelTypes));
		jplOutputConfig.add(cboOutputConfigRudder);

		cboOutputConfigSecondaryAileron.setBounds(134, 159, 154, 20);
		jplOutputConfig.add(cboOutputConfigSecondaryAileron);

		cboOutputConfigCameraPitch.setBounds(134, 184, 154, 20);
		cboOutputConfigCameraPitch.setModel(new DefaultComboBoxModel(channelTypes));
		jplOutputConfig.add(cboOutputConfigCameraPitch);

		cboOutputConfigCameraYaw.setBounds(134, 209, 154, 20);
		cboOutputConfigCameraYaw.setModel(new DefaultComboBoxModel(channelTypes));
		jplOutputConfig.add(cboOutputConfigCameraYaw);

		cboOutputConfigTriggerOutput.setBounds(134, 262, 154, 20);
		cboOutputConfigTriggerOutput.setModel(new DefaultComboBoxModel(channelTypes));
		jplOutputConfig.add(cboOutputConfigTriggerOutput);

		cboOutputConfigPassthroughA.setBounds(134, 287, 154, 20);
		cboOutputConfigPassthroughA.setModel(new DefaultComboBoxModel(channelTypes));
		jplOutputConfig.add(cboOutputConfigPassthroughA);

		cboOutputConfigPassthroughB.setBounds(134, 312, 154, 20);
		cboOutputConfigPassthroughB.setModel(new DefaultComboBoxModel(channelTypes));
		jplOutputConfig.add(cboOutputConfigPassthroughB);

		cboOutputConfigPassthroughC.setBounds(134, 337, 154, 20);
		cboOutputConfigPassthroughC.setModel(new DefaultComboBoxModel(channelTypes));
		jplOutputConfig.add(cboOutputConfigPassthroughC);

		cboOutputConfigPassthroughD.setBounds(134, 368, 154, 20);
		cboOutputConfigPassthroughD.setModel(new DefaultComboBoxModel(channelTypes));
		jplOutputConfig.add(cboOutputConfigPassthroughD);

		cboOutputConfigNumberOfOutputs.setBounds(232, 24, 56, 20);
		cboOutputConfigNumberOfOutputs.setModel(new DefaultComboBoxModel(noOfinputs));
		jplOutputConfig.add(cboOutputConfigNumberOfOutputs);

		JPanel jplServoReserving = new JPanel();
		jplServoReserving.setLayout(null);
		jplServoReserving.setBorder(
				new TitledBorder(null, "Servo Reversing", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplServoReserving.setBounds(648, 11, 249, 282);
		jplIoPanel.add(jplServoReserving);

		JLabel label_40 = new JLabel("Aileron");
		label_40.setBounds(47, 22, 46, 14);
		jplServoReserving.add(label_40);

		cboServoRevAileron.setBounds(103, 19, 100, 20);
		cboServoRevAileron.setModel(new DefaultComboBoxModel(hardwareSwitches));
		jplServoReserving.add(cboServoRevAileron);

		JLabel label_41 = new JLabel("Elevator");
		label_41.setBounds(47, 50, 46, 14);
		jplServoReserving.add(label_41);

		cboServoRevElevator.setBounds(103, 47, 102, 20);
		cboServoRevElevator.setModel(new DefaultComboBoxModel(hardwareSwitches));
		jplServoReserving.add(cboServoRevElevator);

		JLabel label_42 = new JLabel("Secondary Aileron");
		label_42.setBounds(10, 101, 123, 14);
		jplServoReserving.add(label_42);

		cboServoRevSecondaryAileron.setBounds(103, 95, 102, 20);
		cboServoRevSecondaryAileron.setModel(new DefaultComboBoxModel(hardwareSwitches));
		jplServoReserving.add(cboServoRevSecondaryAileron);

		JLabel label_43 = new JLabel("Throttle");
		label_43.setBounds(47, 126, 46, 14);
		jplServoReserving.add(label_43);

		cboServoRevThrottle.setBounds(103, 123, 100, 20);
		cboServoRevThrottle.setModel(new DefaultComboBoxModel(hardwareSwitches));
		jplServoReserving.add(cboServoRevThrottle);

		JLabel label_44 = new JLabel("Camera Pitch");
		label_44.setBounds(20, 157, 69, 14);
		jplServoReserving.add(label_44);

		cboServoRevCameraPitch.setBounds(103, 154, 100, 20);
		cboServoRevCameraPitch.setModel(new DefaultComboBoxModel(hardwareSwitches));
		jplServoReserving.add(cboServoRevCameraPitch);

		JLabel label_45 = new JLabel("Camera Yaw");
		label_45.setBounds(30, 188, 79, 14);
		jplServoReserving.add(label_45);

		cboServoRevCameraYaw.setBounds(103, 185, 100, 20);
		cboServoRevCameraYaw.setModel(new DefaultComboBoxModel(hardwareSwitches));
		jplServoReserving.add(cboServoRevCameraYaw);

		chbxServoRevElevons.setBounds(20, 226, 196, 23);
		jplServoReserving.add(chbxServoRevElevons);

		JLabel label_46 = new JLabel("Rudder");
		label_46.setBounds(45, 73, 46, 14);
		jplServoReserving.add(label_46);

		cboServoRevRudder.setBounds(101, 70, 102, 20);
		cboServoRevRudder.setModel(new DefaultComboBoxModel(hardwareSwitches));
		jplServoReserving.add(cboServoRevRudder);

		JPanel jplAnalogInputs = new JPanel();
		jplAnalogInputs.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Analog Inputs",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplAnalogInputs.setBounds(10, 533, 307, 148);
		jplIoPanel.add(jplAnalogInputs);
		jplAnalogInputs.setLayout(null);

		JLabel lblNumberOfAnalog = new JLabel("Number Of Analog Inputs");
		lblNumberOfAnalog.setBounds(10, 22, 138, 14);
		jplAnalogInputs.add(lblNumberOfAnalog);

		cboNoOfAnalogInputs.setBounds(215, 19, 57, 20);
		cboNoOfAnalogInputs.setModel(new DefaultComboBoxModel(noOfAnaloginputs));
		jplAnalogInputs.add(cboNoOfAnalogInputs);

		JLabel lblNewLabel = new JLabel("Current Input Channel");
		lblNewLabel.setBounds(20, 47, 121, 14);
		jplAnalogInputs.add(lblNewLabel);

		JLabel lblVoltageInputChannel = new JLabel("Voltage Input Channel");
		lblVoltageInputChannel.setBounds(24, 91, 107, 14);
		jplAnalogInputs.add(lblVoltageInputChannel);

		JLabel lblRssiInputChannel = new JLabel("RSSI Input Channel");
		lblRssiInputChannel.setBounds(10, 116, 121, 14);
		jplAnalogInputs.add(lblRssiInputChannel);

		cboAnalogCurrentInputChannel.setBounds(141, 47, 131, 20);
		cboAnalogCurrentInputChannel.setModel(new DefaultComboBoxModel(channelTypes));
		jplAnalogInputs.add(cboAnalogCurrentInputChannel);

		cboAnalogVoltageInputChannel.setBounds(141, 82, 131, 20);
		cboAnalogVoltageInputChannel.setModel(new DefaultComboBoxModel(channelTypes));
		jplAnalogInputs.add(cboAnalogVoltageInputChannel);

		cboAnalogRSSIInputChannel.setBounds(141, 113, 131, 20);
		cboAnalogRSSIInputChannel.setModel(new DefaultComboBoxModel(channelTypes));
		jplAnalogInputs.add(cboAnalogRSSIInputChannel);

		JPanel jplRSSI = new JPanel();
		jplRSSI.setBorder(new TitledBorder(null, "RSSI - RC Receiver signal strength", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		jplRSSI.setBounds(331, 414, 307, 267);
		jplIoPanel.add(jplRSSI);
		jplRSSI.setLayout(null);

		dlsRSSIMinVoltage.setLcdValue(0.5);
		dlsRSSIMinVoltage.setLcdUnitString("");
		dlsRSSIMinVoltage.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dlsRSSIMinVoltage.setBounds(116, 26, 93, 37);
		jplRSSI.add(dlsRSSIMinVoltage);

		dlsRSSIMaxVoltage.setLcdValue(3.3);
		dlsRSSIMaxVoltage.setLcdUnitString("");
		dlsRSSIMaxVoltage.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dlsRSSIMaxVoltage.setBounds(116, 126, 93, 37);
		jplRSSI.add(dlsRSSIMaxVoltage);

		sdrRSSIMaxVoltage.setValue(1);
		sdrRSSIMaxVoltage.setPaintTicks(true);
		sdrRSSIMaxVoltage.setMinorTickSpacing(1);
		sdrRSSIMaxVoltage.setMinimum(1);
		sdrRSSIMaxVoltage.setMaximum(10);
		sdrRSSIMaxVoltage.setMajorTickSpacing(2);
		sdrRSSIMaxVoltage.setBounds(23, 163, 200, 37);
		jplRSSI.add(sdrRSSIMaxVoltage);

		JLabel lblMaxSignalVoltage = new JLabel("MAX Signal Voltage");
		lblMaxSignalVoltage.setBounds(23, 138, 104, 14);
		jplRSSI.add(lblMaxSignalVoltage);

		JLabel lblMinSignalVoltage = new JLabel("MIN Signal Voltage");
		lblMinSignalVoltage.setBounds(22, 38, 93, 14);
		jplRSSI.add(lblMinSignalVoltage);

		sdrRSSIMinVoltage.setValue(1);
		sdrRSSIMinVoltage.setPaintTicks(true);
		sdrRSSIMinVoltage.setMinorTickSpacing(1);
		sdrRSSIMinVoltage.setMinimum(1);
		sdrRSSIMinVoltage.setMaximum(10);
		sdrRSSIMinVoltage.setMajorTickSpacing(2);
		sdrRSSIMinVoltage.setBounds(23, 74, 200, 37);
		jplRSSI.add(sdrRSSIMinVoltage);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Trigger Action", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(648, 304, 236, 377);
		jplIoPanel.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblTriggerType = new JLabel("Trigger Type");
		lblTriggerType.setBounds(10, 14, 80, 14);
		panel_1.add(lblTriggerType);

		JLabel lblTriggerAction = new JLabel("Trigger Action");
		lblTriggerAction.setBounds(10, 47, 93, 14);
		panel_1.add(lblTriggerAction);

		JLabel lblServoLow = new JLabel("Servo LOW");
		lblServoLow.setBounds(10, 84, 93, 14);
		panel_1.add(lblServoLow);

		JLabel lblServoHigh = new JLabel("Servo HIGH");
		lblServoHigh.setBounds(16, 166, 69, 14);
		panel_1.add(lblServoHigh);

		JLabel lblPulseDuration = new JLabel("Pulse Duration");
		lblPulseDuration.setBounds(15, 241, 75, 14);
		panel_1.add(lblPulseDuration);

		JLabel lblRepeatDuration = new JLabel("Repeat Period");
		lblRepeatDuration.setBounds(16, 314, 74, 14);
		panel_1.add(lblRepeatDuration);

		cboTriggerType.setBounds(100, 11, 126, 20);
		cboTriggerType.setModel(new DefaultComboBoxModel(triggerTypes));
		panel_1.add(cboTriggerType);

		cboTriggerAction.setBounds(100, 44, 126, 20);
		cboTriggerAction.setModel(new DefaultComboBoxModel(triggerAction));
		panel_1.add(cboTriggerAction);

		dslTRIGGER_SERVO_LOW.setLcdValue(2600.0);
		dslTRIGGER_SERVO_LOW.setLcdUnitString("");
		dslTRIGGER_SERVO_LOW.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dslTRIGGER_SERVO_LOW.setBounds(100, 72, 93, 37);
		panel_1.add(dslTRIGGER_SERVO_LOW);

		dslTRIGGER_SERVO_HIGH.setLcdValue(2600.0);
		dslTRIGGER_SERVO_HIGH.setLcdUnitString("");
		dslTRIGGER_SERVO_HIGH.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dslTRIGGER_SERVO_HIGH.setBounds(100, 150, 93, 37);
		panel_1.add(dslTRIGGER_SERVO_HIGH);

		dslTRIGGER_PULSE_DURATION.setLcdValue(2600.0);
		dslTRIGGER_PULSE_DURATION.setLcdUnitString("");
		dslTRIGGER_PULSE_DURATION.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dslTRIGGER_PULSE_DURATION.setBounds(100, 229, 93, 37);
		panel_1.add(dslTRIGGER_PULSE_DURATION);

		dslTRIGGER_REPEAT_PERIOD.setLcdValue(2600.0);
		dslTRIGGER_REPEAT_PERIOD.setLcdUnitString("");
		dslTRIGGER_REPEAT_PERIOD.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dslTRIGGER_REPEAT_PERIOD.setBounds(100, 300, 93, 37);
		panel_1.add(dslTRIGGER_REPEAT_PERIOD);

		sldTRIGGER_SERVO_LOW.setValue(2600);
		sldTRIGGER_SERVO_LOW.setPaintTicks(true);
		sldTRIGGER_SERVO_LOW.setMinorTickSpacing(100);
		sldTRIGGER_SERVO_LOW.setMinimum(600);
		sldTRIGGER_SERVO_LOW.setMaximum(9800);
		sldTRIGGER_SERVO_LOW.setMajorTickSpacing(1000);
		sldTRIGGER_SERVO_LOW.setBounds(10, 109, 200, 37);
		panel_1.add(sldTRIGGER_SERVO_LOW);

		sldTRIGGER_SERVO_HIGH.setValue(2600);
		sldTRIGGER_SERVO_HIGH.setPaintTicks(true);
		sldTRIGGER_SERVO_HIGH.setMinorTickSpacing(100);
		sldTRIGGER_SERVO_HIGH.setMinimum(600);
		sldTRIGGER_SERVO_HIGH.setMaximum(9800);
		sldTRIGGER_SERVO_HIGH.setMajorTickSpacing(1000);
		sldTRIGGER_SERVO_HIGH.setBounds(10, 191, 200, 37);
		panel_1.add(sldTRIGGER_SERVO_HIGH);

		sldTRIGGER_PULSE_DURATION.setValue(2600);
		sldTRIGGER_PULSE_DURATION.setPaintTicks(true);
		sldTRIGGER_PULSE_DURATION.setMinorTickSpacing(100);
		sldTRIGGER_PULSE_DURATION.setMinimum(600);
		sldTRIGGER_PULSE_DURATION.setMaximum(9800);
		sldTRIGGER_PULSE_DURATION.setMajorTickSpacing(1000);
		sldTRIGGER_PULSE_DURATION.setBounds(10, 266, 200, 37);
		panel_1.add(sldTRIGGER_PULSE_DURATION);

		sldTRIGGER_REPEAT_PERIOD.setValue(2600);
		sldTRIGGER_REPEAT_PERIOD.setPaintTicks(true);
		sldTRIGGER_REPEAT_PERIOD.setMinorTickSpacing(100);
		sldTRIGGER_REPEAT_PERIOD.setMinimum(600);
		sldTRIGGER_REPEAT_PERIOD.setMaximum(9800);
		sldTRIGGER_REPEAT_PERIOD.setMajorTickSpacing(1000);
		sldTRIGGER_REPEAT_PERIOD.setBounds(10, 340, 200, 37);
		panel_1.add(sldTRIGGER_REPEAT_PERIOD);

		JPanel jplCompilationTab = new JPanel();
		uDbOptionsTab.addTab("Control Gains, Camera Stabilization and Targeting", null, jplCompilationTab, null);
		jplCompilationTab.setLayout(null);

		JLabel lblServosat = new JLabel("ServoSAT");
		lblServosat.setBounds(10, 23, 61, 14);
		jplCompilationTab.add(lblServosat);

		txtSERVOSAT = new JTextField();
		txtSERVOSAT.setText("1.0");
		txtSERVOSAT.setBounds(71, 20, 33, 20);
		jplCompilationTab.add(txtSERVOSAT);
		txtSERVOSAT.setColumns(10);

		JPanel jplRoll = new JPanel();
		jplRoll.setBorder(new TitledBorder(null, "Aileron/Roll Control Gains", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		jplRoll.setBounds(10, 48, 165, 159);
		jplCompilationTab.add(jplRoll);
		jplRoll.setLayout(null);

		JLabel lblRollkp = new JLabel("ROLLKP");
		lblRollkp.setBounds(10, 21, 46, 14);
		jplRoll.add(lblRollkp);

		txtROLLKP = new JTextField();
		txtROLLKP.setText("0.20");
		txtROLLKP.setBounds(58, 18, 46, 20);
		jplRoll.add(txtROLLKP);
		txtROLLKP.setColumns(10);

		JLabel lblRollkd = new JLabel("ROLLKD");
		lblRollkd.setBounds(10, 46, 46, 14);
		jplRoll.add(lblRollkd);

		txtROLLKD = new JTextField();
		txtROLLKD.setText("0.05");
		txtROLLKD.setBounds(58, 43, 46, 20);
		jplRoll.add(txtROLLKD);
		txtROLLKD.setColumns(10);

		JLabel lblYawkpaileron = new JLabel("YAWKP_AILERON");
		lblYawkpaileron.setBounds(10, 72, 94, 14);
		jplRoll.add(lblYawkpaileron);

		txtYAWKP_AILERON = new JTextField();
		txtYAWKP_AILERON.setText("0.10");
		txtYAWKP_AILERON.setBounds(110, 69, 35, 20);
		jplRoll.add(txtYAWKP_AILERON);
		txtYAWKP_AILERON.setColumns(10);

		JLabel lblYawkdaileron = new JLabel("YAWKD_AILERON");
		lblYawkdaileron.setBounds(10, 97, 86, 14);
		jplRoll.add(lblYawkdaileron);

		txtYAWKD_AILERON = new JTextField();
		txtYAWKD_AILERON.setText("0.05");
		txtYAWKD_AILERON.setBounds(110, 100, 35, 20);
		jplRoll.add(txtYAWKD_AILERON);
		txtYAWKD_AILERON.setColumns(10);

		JLabel lblAileronboost = new JLabel("AILERON_BOOST");
		lblAileronboost.setBounds(10, 126, 86, 14);
		jplRoll.add(lblAileronboost);

		txtAILERON_BOOST = new JTextField();
		txtAILERON_BOOST.setText("1");
		txtAILERON_BOOST.setBounds(109, 123, 36, 20);
		jplRoll.add(txtAILERON_BOOST);
		txtAILERON_BOOST.setColumns(10);

		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Elevator/Pitch Control Gains",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(10, 218, 198, 159);
		jplCompilationTab.add(panel_2);

		JLabel lblPitchgain = new JLabel("PITCHGAIN");
		lblPitchgain.setBounds(10, 21, 79, 14);
		panel_2.add(lblPitchgain);

		txtPITCHGAIN = new JTextField();
		txtPITCHGAIN.setText("0.10");
		txtPITCHGAIN.setColumns(10);
		txtPITCHGAIN.setBounds(139, 15, 35, 20);
		panel_2.add(txtPITCHGAIN);

		JLabel lblPitchkd = new JLabel("PITCHKD");
		lblPitchkd.setBounds(10, 46, 79, 14);
		panel_2.add(lblPitchkd);

		txtPITCHKD = new JTextField();
		txtPITCHKD.setText("0.04");
		txtPITCHKD.setColumns(10);
		txtPITCHKD.setBounds(139, 40, 35, 20);
		panel_2.add(txtPITCHKD);

		JLabel lblRudderelevmix = new JLabel("RUDDER_ELEV_MIX");
		lblRudderelevmix.setBounds(10, 72, 94, 14);
		panel_2.add(lblRudderelevmix);

		txtRUDDER_ELEV_MIX = new JTextField();
		txtRUDDER_ELEV_MIX.setText("0.20");
		txtRUDDER_ELEV_MIX.setColumns(10);
		txtRUDDER_ELEV_MIX.setBounds(139, 66, 35, 20);
		panel_2.add(txtRUDDER_ELEV_MIX);

		JLabel lblRollelevmix = new JLabel("ROLL_ELEV_MIX");
		lblRollelevmix.setBounds(10, 103, 86, 14);
		panel_2.add(lblRollelevmix);

		txtROLL_ELEV_MIX = new JTextField();
		txtROLL_ELEV_MIX.setText("0.05");
		txtROLL_ELEV_MIX.setColumns(10);
		txtROLL_ELEV_MIX.setBounds(139, 97, 35, 20);
		panel_2.add(txtROLL_ELEV_MIX);

		JLabel lblElevatorboost = new JLabel("ELEVATOR_BOOST");
		lblElevatorboost.setBounds(10, 134, 118, 14);
		panel_2.add(lblElevatorboost);

		txtELEVATOR_BOOST = new JTextField();
		txtELEVATOR_BOOST.setText("0.50");
		txtELEVATOR_BOOST.setColumns(10);
		txtELEVATOR_BOOST.setBounds(138, 131, 36, 20);
		panel_2.add(txtELEVATOR_BOOST);

		txtINVERTED_NEUTRAL_PITCH = new JTextField();
		txtINVERTED_NEUTRAL_PITCH.setText("8");
		txtINVERTED_NEUTRAL_PITCH.setBounds(186, 399, 33, 14);
		jplCompilationTab.add(txtINVERTED_NEUTRAL_PITCH);
		txtINVERTED_NEUTRAL_PITCH.setColumns(10);

		JLabel lblInvertedneutralpitch = new JLabel("INVERTED_NEUTRAL_PITCH");
		lblInvertedneutralpitch.setBounds(10, 399, 165, 14);
		jplCompilationTab.add(lblInvertedneutralpitch);

		JPanel panel_3 = new JPanel();
		panel_3.setLayout(null);
		panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Rudder/Yaw Control Gains",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(10, 429, 247, 193);
		jplCompilationTab.add(panel_3);

		JLabel lblYawkprudder = new JLabel("YAWKP_RUDDER");
		lblYawkprudder.setBounds(10, 21, 94, 14);
		panel_3.add(lblYawkprudder);

		txtYAWKP_RUDDER = new JTextField();
		txtYAWKP_RUDDER.setText("0.05");
		txtYAWKP_RUDDER.setColumns(10);
		txtYAWKP_RUDDER.setBounds(181, 15, 35, 20);
		panel_3.add(txtYAWKP_RUDDER);

		JLabel lblYawkdrudder = new JLabel("YAWKD_RUDDER");
		lblYawkdrudder.setBounds(10, 46, 94, 14);
		panel_3.add(lblYawkdrudder);

		txtYAWKD_RUDDER = new JTextField();
		txtYAWKD_RUDDER.setText("0.05");
		txtYAWKD_RUDDER.setColumns(10);
		txtYAWKD_RUDDER.setBounds(181, 40, 35, 20);
		panel_3.add(txtYAWKD_RUDDER);

		JLabel lblRollkprudder = new JLabel("ROLLKP_RUDDER");
		lblRollkprudder.setBounds(10, 72, 94, 14);
		panel_3.add(lblRollkprudder);

		txtROLLKP_RUDDER = new JTextField();
		txtROLLKP_RUDDER.setText("0.06");
		txtROLLKP_RUDDER.setColumns(10);
		txtROLLKP_RUDDER.setBounds(181, 66, 35, 20);
		panel_3.add(txtROLLKP_RUDDER);

		JLabel lblRollkdrudder = new JLabel("ROLLKD_RUDDER");
		lblRollkdrudder.setBounds(10, 97, 86, 14);
		panel_3.add(lblRollkdrudder);

		txtROLLKD_RUDDER = new JTextField();
		txtROLLKD_RUDDER.setText("0.05");
		txtROLLKD_RUDDER.setColumns(10);
		txtROLLKD_RUDDER.setBounds(181, 97, 35, 20);
		panel_3.add(txtROLLKD_RUDDER);

		JLabel lblManualaileronruddermix = new JLabel("MANUAL_AILERON_RUDDER_MIX");
		lblManualaileronruddermix.setBounds(10, 126, 171, 14);
		panel_3.add(lblManualaileronruddermix);

		txtMANUAL_AILERON_RUDDER_MIX = new JTextField();
		txtMANUAL_AILERON_RUDDER_MIX.setText("0.00");
		txtMANUAL_AILERON_RUDDER_MIX.setColumns(10);
		txtMANUAL_AILERON_RUDDER_MIX.setBounds(181, 123, 36, 20);
		panel_3.add(txtMANUAL_AILERON_RUDDER_MIX);

		JLabel lblRudderboost = new JLabel("RUDDER_BOOST");
		lblRudderboost.setBounds(10, 154, 94, 14);
		panel_3.add(lblRudderboost);

		txtRUDDER_BOOST = new JTextField();
		txtRUDDER_BOOST.setText("1.00");
		txtRUDDER_BOOST.setColumns(10);
		txtRUDDER_BOOST.setBounds(180, 148, 36, 20);
		panel_3.add(txtRUDDER_BOOST);

		JPanel jplHoverGains = new JPanel();
		jplHoverGains.setLayout(null);
		jplHoverGains.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Gains for Hovering",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jplHoverGains.setBounds(279, 48, 247, 305);
		jplCompilationTab.add(jplHoverGains);

		JLabel lblHoverrollkp = new JLabel("HOVER_ROLLKP");
		lblHoverrollkp.setBounds(10, 21, 94, 14);
		jplHoverGains.add(lblHoverrollkp);

		txtHOVER_ROLLKP = new JTextField();
		txtHOVER_ROLLKP.setText("0.05");
		txtHOVER_ROLLKP.setColumns(10);
		txtHOVER_ROLLKP.setBounds(181, 15, 35, 20);
		jplHoverGains.add(txtHOVER_ROLLKP);

		JLabel lblHoverrollkd = new JLabel("HOVER_ROLLKD");
		lblHoverrollkd.setBounds(10, 46, 94, 14);
		jplHoverGains.add(lblHoverrollkd);

		txtHOVER_ROLLKD = new JTextField();
		txtHOVER_ROLLKD.setText("0.05");
		txtHOVER_ROLLKD.setColumns(10);
		txtHOVER_ROLLKD.setBounds(181, 40, 35, 20);
		jplHoverGains.add(txtHOVER_ROLLKD);

		JLabel lblHoverpitchgain = new JLabel("HOVER_PITCHGAIN");
		lblHoverpitchgain.setBounds(10, 72, 124, 14);
		jplHoverGains.add(lblHoverpitchgain);

		txtHOVER_PITCHGAIN = new JTextField();
		txtHOVER_PITCHGAIN.setText("0.20");
		txtHOVER_PITCHGAIN.setColumns(10);
		txtHOVER_PITCHGAIN.setBounds(181, 66, 35, 20);
		jplHoverGains.add(txtHOVER_PITCHGAIN);

		JLabel lblHoverpitchkd = new JLabel("HOVER_PITCHKD");
		lblHoverpitchkd.setBounds(10, 97, 86, 14);
		jplHoverGains.add(lblHoverpitchkd);

		txtHOVER_PITCHKD = new JTextField();
		txtHOVER_PITCHKD.setText("0.25");
		txtHOVER_PITCHKD.setColumns(10);
		txtHOVER_PITCHKD.setBounds(181, 97, 35, 20);
		jplHoverGains.add(txtHOVER_PITCHKD);

		JLabel lblHoverpitchoffset = new JLabel("HOVER_PITCH_OFFSET");
		lblHoverpitchoffset.setBounds(10, 126, 171, 14);
		jplHoverGains.add(lblHoverpitchoffset);

		txtHOVER_PITCH_OFFSET = new JTextField();
		txtHOVER_PITCH_OFFSET.setText("0.00");
		txtHOVER_PITCH_OFFSET.setColumns(10);
		txtHOVER_PITCH_OFFSET.setBounds(181, 123, 36, 20);
		jplHoverGains.add(txtHOVER_PITCH_OFFSET);

		JLabel lblHoveryawkp = new JLabel("HOVER_YAWKP");
		lblHoveryawkp.setBounds(10, 154, 94, 14);
		jplHoverGains.add(lblHoveryawkp);

		txtHOVER_YAWKP = new JTextField();
		txtHOVER_YAWKP.setText("0.20");
		txtHOVER_YAWKP.setColumns(10);
		txtHOVER_YAWKP.setBounds(180, 148, 36, 20);
		jplHoverGains.add(txtHOVER_YAWKP);

		JLabel lblHovernavmaxpitchradius = new JLabel("HOVER_NAV_MAX_PITCH_RADIUS");
		lblHovernavmaxpitchradius.setBounds(10, 267, 171, 14);
		jplHoverGains.add(lblHovernavmaxpitchradius);

		JLabel lblHoverpitchtowardswp = new JLabel("HOVER_PITCH_TOWARDS_WP");
		lblHoverpitchtowardswp.setBounds(10, 239, 171, 14);
		jplHoverGains.add(lblHoverpitchtowardswp);

		JLabel lblHoveryawoffset = new JLabel("HOVER_YAW_OFFSET");
		lblHoveryawoffset.setBounds(10, 210, 124, 14);
		jplHoverGains.add(lblHoveryawoffset);

		JLabel lblHoveryawkd = new JLabel("HOVER_YAWKD");
		lblHoveryawkd.setBounds(10, 185, 124, 14);
		jplHoverGains.add(lblHoveryawkd);

		txtHOVER_YAWKD = new JTextField();
		txtHOVER_YAWKD.setText("0.25");
		txtHOVER_YAWKD.setColumns(10);
		txtHOVER_YAWKD.setBounds(181, 179, 35, 20);
		jplHoverGains.add(txtHOVER_YAWKD);

		txtHOVER_YAW_OFFSET = new JTextField();
		txtHOVER_YAW_OFFSET.setText("0.0");
		txtHOVER_YAW_OFFSET.setColumns(10);
		txtHOVER_YAW_OFFSET.setBounds(181, 210, 35, 20);
		jplHoverGains.add(txtHOVER_YAW_OFFSET);

		txtHOVER_PITCH_TOWARDS_WP = new JTextField();
		txtHOVER_PITCH_TOWARDS_WP.setText("30.0");
		txtHOVER_PITCH_TOWARDS_WP.setColumns(10);
		txtHOVER_PITCH_TOWARDS_WP.setBounds(181, 236, 36, 20);
		jplHoverGains.add(txtHOVER_PITCH_TOWARDS_WP);

		txtHOVER_NAV_MAX_PITCH_RADIUS = new JTextField();
		txtHOVER_NAV_MAX_PITCH_RADIUS.setText("20.0");
		txtHOVER_NAV_MAX_PITCH_RADIUS.setColumns(10);
		txtHOVER_NAV_MAX_PITCH_RADIUS.setBounds(180, 261, 36, 20);
		jplHoverGains.add(txtHOVER_NAV_MAX_PITCH_RADIUS);

		JPanel panel_5 = new JPanel();
		panel_5.setLayout(null);
		panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Control of camera modes",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_5.setBounds(279, 378, 247, 180);
		jplCompilationTab.add(panel_5);

		dplCameraModeThresholdLOW.setLcdValue(2600.0);
		dplCameraModeThresholdLOW.setLcdUnitString("");
		dplCameraModeThresholdLOW.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dplCameraModeThresholdLOW.setBounds(48, 15, 93, 37);
		panel_5.add(dplCameraModeThresholdLOW);

		sdlCameraModeThresholdLOW.setValue(2600);
		sdlCameraModeThresholdLOW.setPaintTicks(true);
		sdlCameraModeThresholdLOW.setMinorTickSpacing(100);
		sdlCameraModeThresholdLOW.setMinimum(600);
		sdlCameraModeThresholdLOW.setMaximum(9800);
		sdlCameraModeThresholdLOW.setMajorTickSpacing(1000);
		sdlCameraModeThresholdLOW.setBounds(10, 58, 200, 37);
		panel_5.add(sdlCameraModeThresholdLOW);

		JLabel label_51 = new JLabel("LOW");
		label_51.setBounds(20, 33, 46, 14);
		panel_5.add(label_51);

		sdlCameraModeThresholdHIGH.setValue(2600);
		sdlCameraModeThresholdHIGH.setPaintTicks(true);
		sdlCameraModeThresholdHIGH.setMinorTickSpacing(100);
		sdlCameraModeThresholdHIGH.setMinimum(600);
		sdlCameraModeThresholdHIGH.setMaximum(9800);
		sdlCameraModeThresholdHIGH.setMajorTickSpacing(1000);
		sdlCameraModeThresholdHIGH.setBounds(10, 135, 200, 37);
		panel_5.add(sdlCameraModeThresholdHIGH);

		dplCameraModeThresholdHIGH.setLcdValue(3400.0);
		dplCameraModeThresholdHIGH.setLcdUnitString("");
		dplCameraModeThresholdHIGH.setCustomLcdUnitFont(new Font("Verdana", Font.BOLD, 12));
		dplCameraModeThresholdHIGH.setBounds(48, 95, 93, 37);
		panel_5.add(dplCameraModeThresholdHIGH);

		JLabel label_52 = new JLabel("HIGH");
		label_52.setBounds(20, 110, 46, 14);
		panel_5.add(label_52);

		JLabel lblCamtanpitchinstabilizedmode = new JLabel("CAM_TAN_PITCH_IN_STABILIZED_MODE");
		lblCamtanpitchinstabilizedmode.setBounds(279, 569, 225, 14);
		jplCompilationTab.add(lblCamtanpitchinstabilizedmode);

		txtCAM_TAN_PITCH_IN_STABILIZED_MODE = new JTextField();
		txtCAM_TAN_PITCH_IN_STABILIZED_MODE.setText("1433");
		txtCAM_TAN_PITCH_IN_STABILIZED_MODE.setBounds(508, 569, 41, 14);
		jplCompilationTab.add(txtCAM_TAN_PITCH_IN_STABILIZED_MODE);
		txtCAM_TAN_PITCH_IN_STABILIZED_MODE.setColumns(10);

		JLabel lblCamyawinstabilizedmode = new JLabel("CAM_YAW_IN_STABILIZED_MODE");
		lblCamyawinstabilizedmode.setBounds(283, 587, 198, 14);
		jplCompilationTab.add(lblCamyawinstabilizedmode);

		txtCAM_YAW_IN_STABILIZED_MODE = new JTextField();
		txtCAM_YAW_IN_STABILIZED_MODE.setText("0");
		txtCAM_YAW_IN_STABILIZED_MODE.setBounds(508, 584, 41, 17);
		jplCompilationTab.add(txtCAM_YAW_IN_STABILIZED_MODE);
		txtCAM_YAW_IN_STABILIZED_MODE.setColumns(10);

		JPanel panel_7 = new JPanel();
		panel_7.setLayout(null);
		panel_7.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Camera Pitch",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_7.setBounds(536, 48, 238, 159);
		jplCompilationTab.add(panel_7);

		JLabel lblCampitchservothrow = new JLabel("CAM_PITCH_SERVO_THROW");
		lblCampitchservothrow.setBounds(10, 24, 154, 14);
		panel_7.add(lblCampitchservothrow);

		txtCAM_PITCH_SERVO_THROW = new JTextField();
		txtCAM_PITCH_SERVO_THROW.setText("95");
		txtCAM_PITCH_SERVO_THROW.setColumns(10);
		txtCAM_PITCH_SERVO_THROW.setBounds(181, 24, 35, 20);
		panel_7.add(txtCAM_PITCH_SERVO_THROW);

		JLabel lblCampitchservomax = new JLabel("CAM_PITCH_SERVO_MAX");
		lblCampitchservomax.setBounds(10, 55, 133, 14);
		panel_7.add(lblCampitchservomax);

		txtCAM_PITCH_SERVO_MAX = new JTextField();
		txtCAM_PITCH_SERVO_MAX.setText("85");
		txtCAM_PITCH_SERVO_MAX.setColumns(10);
		txtCAM_PITCH_SERVO_MAX.setBounds(181, 49, 35, 20);
		panel_7.add(txtCAM_PITCH_SERVO_MAX);

		JLabel lblCampitchservomin = new JLabel("CAM_PITCH_SERVO_MIN");
		lblCampitchservomin.setBounds(10, 81, 154, 14);
		panel_7.add(lblCampitchservomin);

		txtCAM_PITCH_SERVO_MIN = new JTextField();
		txtCAM_PITCH_SERVO_MIN.setText("-22");
		txtCAM_PITCH_SERVO_MIN.setColumns(10);
		txtCAM_PITCH_SERVO_MIN.setBounds(181, 75, 35, 20);
		panel_7.add(txtCAM_PITCH_SERVO_MIN);

		JLabel lblCampitchoffsetcentred = new JLabel("CAM_PITCH_OFFSET_CENTRED");
		lblCampitchoffsetcentred.setBounds(10, 109, 154, 14);
		panel_7.add(lblCampitchoffsetcentred);

		txtCAM_PITCH_OFFSET_CENTRED = new JTextField();
		txtCAM_PITCH_OFFSET_CENTRED.setText("38");
		txtCAM_PITCH_OFFSET_CENTRED.setColumns(10);
		txtCAM_PITCH_OFFSET_CENTRED.setBounds(181, 106, 35, 20);
		panel_7.add(txtCAM_PITCH_OFFSET_CENTRED);

		JPanel panel_9 = new JPanel();
		panel_9.setLayout(null);
		panel_9.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Camera Yaw",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_9.setBounds(536, 218, 238, 159);
		jplCompilationTab.add(panel_9);

		JLabel lblCamyawservothrow = new JLabel("CAM_YAW_SERVO_THROW");
		lblCamyawservothrow.setBounds(10, 24, 154, 14);
		panel_9.add(lblCamyawservothrow);

		txtCAM_YAW_SERVO_THROW = new JTextField();
		txtCAM_YAW_SERVO_THROW.setText("350");
		txtCAM_YAW_SERVO_THROW.setColumns(10);
		txtCAM_YAW_SERVO_THROW.setBounds(181, 24, 35, 20);
		panel_9.add(txtCAM_YAW_SERVO_THROW);

		JLabel lblCamyawservomax = new JLabel("CAM_YAW_SERVO_MAX");
		lblCamyawservomax.setBounds(10, 55, 133, 14);
		panel_9.add(lblCamyawservomax);

		txtCAM_YAW_SERVO_MAX = new JTextField();
		txtCAM_YAW_SERVO_MAX.setText("130");
		txtCAM_YAW_SERVO_MAX.setColumns(10);
		txtCAM_YAW_SERVO_MAX.setBounds(181, 49, 35, 20);
		panel_9.add(txtCAM_YAW_SERVO_MAX);

		JLabel lblCamyawservomin = new JLabel("CAM_YAW_SERVO_MIN");
		lblCamyawservomin.setBounds(10, 81, 154, 14);
		panel_9.add(lblCamyawservomin);

		txtCAM_YAW_SERVO_MIN = new JTextField();
		txtCAM_YAW_SERVO_MIN.setText("-130");
		txtCAM_YAW_SERVO_MIN.setColumns(10);
		txtCAM_YAW_SERVO_MIN.setBounds(181, 75, 35, 20);
		panel_9.add(txtCAM_YAW_SERVO_MIN);

		JLabel lblCamyawoffsetcentred = new JLabel("CAM_YAW_OFFSET_CENTRED");
		lblCamyawoffsetcentred.setBounds(10, 109, 154, 14);
		panel_9.add(lblCamyawoffsetcentred);

		txtCAM_YAW_OFFSET_CENTRED = new JTextField();
		txtCAM_YAW_OFFSET_CENTRED.setText("11");
		txtCAM_YAW_OFFSET_CENTRED.setColumns(10);
		txtCAM_YAW_OFFSET_CENTRED.setBounds(181, 106, 35, 20);
		panel_9.add(txtCAM_YAW_OFFSET_CENTRED);

		JPanel panel_10 = new JPanel();
		panel_10.setLayout(null);
		panel_10.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Camera Testing",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_10.setBounds(536, 386, 238, 110);
		jplCompilationTab.add(panel_10);

		JLabel lblCamtestingoveride = new JLabel("CAM_TESTING_OVERIDE");
		lblCamtestingoveride.setBounds(10, 24, 154, 14);
		panel_10.add(lblCamtestingoveride);

		txtCAM_TESTING_OVERIDE = new JTextField();
		txtCAM_TESTING_OVERIDE.setText("0");
		txtCAM_TESTING_OVERIDE.setColumns(10);
		txtCAM_TESTING_OVERIDE.setBounds(181, 24, 35, 20);
		panel_10.add(txtCAM_TESTING_OVERIDE);

		JLabel lblCamtestingyawangle = new JLabel("CAM_TESTING_YAW_ANGLE");
		lblCamtestingyawangle.setBounds(10, 55, 161, 14);
		panel_10.add(lblCamtestingyawangle);

		txtCAM_TESTING_YAW_ANGLE = new JTextField();
		txtCAM_TESTING_YAW_ANGLE.setText("90");
		txtCAM_TESTING_YAW_ANGLE.setColumns(10);
		txtCAM_TESTING_YAW_ANGLE.setBounds(181, 49, 35, 20);
		panel_10.add(txtCAM_TESTING_YAW_ANGLE);

		JLabel lblCamtestingpitchangle = new JLabel("CAM_TESTING_PITCH_ANGLE");
		lblCamtestingpitchangle.setBounds(10, 81, 154, 14);
		panel_10.add(lblCamtestingpitchangle);

		txtCAM_TESTING_PITCH_ANGLE = new JTextField();
		txtCAM_TESTING_PITCH_ANGLE.setText("90");
		txtCAM_TESTING_PITCH_ANGLE.setColumns(10);
		txtCAM_TESTING_PITCH_ANGLE.setBounds(181, 75, 35, 20);
		panel_10.add(txtCAM_TESTING_PITCH_ANGLE);

		chckbxCamuseexternaltargetdata.setBounds(531, 513, 238, 23);
		jplCompilationTab.add(chckbxCamuseexternaltargetdata);
		uDbOptionsTab.setSize(new Dimension(400, 200));
		loadDefaults();
		udbOptionsSplitView.add(uDbOptionsTab, JSplitPane.LEFT);
	}

	private void loadDefaults() {
		txtMPLABDirectory.setText(Resources.getResources().getMPLABDirectory());
		txtProjectSourceFile.setText(Resources.getResources().getProjectDirectory());
		chckbxMakeBackup.setSelected(Resources.getResources().canBackupDaily());
		chckbxMakeBackupBefore.setSelected(Resources.getResources().canBackupBeforeSave());
		chckbxMakeBackupBefore.setEnabled(Resources.getResources().canBackupBeforeSave());
	}

	private String channelTypes[] = new String[] { "CHANNEL_UNUSED", "CHANNEL_1", "CHANNEL_2", "CHANNEL_3", "CHANNEL_4",
			"CHANNEL_5" };
	private String inputChannels[] = new String[] { "THROTTLE_INPUT_CHANNEL", "AILERON_INPUT_CHANNEL",
			"ELEVATOR_INPUT_CHANNEL", "RUDDER_INPUT_CHANNEL", "MODE_SWITCH_INPUT_CHANNEL" };
	private String failsafeTypes[] = new String[] { "FAILSAFE_RTL", "FAILSAFE_MAIN_FLIGHTPLAN" };
	private String boardTypes[] = new String[] { "GREEN_BOARD", "RED_BOARD", "UDB3_BOARD", "UDB4_BOARD",
			"AUAV1_BOARD" };
	private String boardOrientation[] = new String[] { "ORIENTATION_FORWARDS", "ORIENTATION_BACKWARDS",
			"ORIENTATION_INVERTED", "ORIENTATION_FLIPPED", "ORIENTATION_YAWCW", "ORIENTATION_YAWCCW" };
	private String airframeType[] = new String[] { "AIRFRAME_STANDARD", "AIRFRAME_VTAIL", "AIRFRAME_DELTA" };
	private String gpsType[] = new String[] { "GPS_STD", "GPS_UBX_2HZ", "GPS_UBX_4HZ", "GPS_MTEK" };
	private String hardwareSwitches[] = new String[] { "HW_SWITCH_1", "HW_SWITCH_2", "HW_SWITCH_3", "0", "1" };
	private String noOfinputs[] = new String[] { "1", "2", "3", "4", "5", "6", "7", "8" };
	private String noOfAnaloginputs[] = new String[] { "0", "1", "2", "3" };
	private String[] altitudeHoldModes = new String[] { "AH_NONE", "AH_FULL", "AH_PITCH_ONLY" };
	private String[] mavlinkOutputFormat = new String[] { "SERIAL_NONE", "SERIAL_DEBUG", "SERIAL_ARDUSTATION",
			"SERIAL_UDB", "SERIAL_UDB_EXTRA", "SERIAL_MAVLINK", "SERIAL_CAM_TRACK", "SERIAL_OSD_REMZIBI" };
	private String[] flightPlanType = new String[] { "FP_WAYPOINTS", "FP_LOGO" };
	private String[] baudRate = new String[] { "300", "2400", "9600", "14400", "28800", "38400", "57600", "152000" };
	private String[] triggerTypes = new String[] { "TRIGGER_TYPE_NONE", "TRIGGER_TYPE_SERVO", "TRIGGER_TYPE_DIGITAL" };
	private String[] triggerAction = new String[] { "TRIGGER_PULSE_HIGH", "TRIGGER_PULSE_LOW", "TRIGGER_TOGGLE",
			"TRIGGER_REPEATING" };

	private final JPanel udbOptionsEditor = new JPanel();
	private final JTabbedPane uDbOptionsTab = new JTabbedPane(JTabbedPane.TOP);
	private final JPanel jplMainOptionsTab = new JPanel();
	private JTextField txtPPMNumberOfInputs;
	static private boolean synchroniseProperties = false;
	static private boolean isOptionsLoaded = false;

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == chckbxSpeedControlEnable) {
			if (chckbxSpeedControlEnable.isSelected()) {
				sldrSpeedControl.setEnabled(true);
				debug("Speed enabled");
			} else {
				sldrSpeedControl.setEnabled(false);
				debug("Speed disabled");
			}
		} else if (e.getSource() == chckbxMakeBackup) {
			if (chckbxMakeBackup.isSelected()) {
				chckbxMakeBackupBefore.setEnabled(true);
			} else {
				chckbxMakeBackupBefore.setEnabled(false);
				chckbxMakeBackupBefore.setSelected(false);
			}
		} else if (e.getSource() == chckbxRacingModeEnable) {
			if (chckbxRacingModeEnable.isSelected()) {
				sldRacingModeSpeed.setEnabled(true);
				debug("Racing Mode enabled");
			} else {
				sldRacingModeSpeed.setEnabled(false);
				debug("Racing Mode disabled");
			}
		}

		else if (e.getSource() == btnLoad) {
			loadOptionsFile();
			isOptionsLoaded = true;
		} else if (e.getSource() == btnSave) {
			if (!isOptionsLoaded) {
				JOptionPane.showMessageDialog(this, "Please load an options file using the load button first",
						"UDB4-Tool", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			Resources.getResources().saveResources("todays_count", Resources.getResources().getDailyCount());
			serializeOptionsFile();
		} else if (e.getSource() == btnCompile) {
			if (!isOptionsLoaded) {
				JOptionPane.showMessageDialog(this, "Please load an options file using the load button first",
						"UDB4-Tool", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			buildCode();
		} else if (e.getSource() == btnSelectSource) {
			chooseDirectory(PROJECT_FILE_DIALOGUE);
		} else if (e.getSource() == btnSelectMplab) {
			chooseDirectory(COMPILER_DIRECTORY_DIALOGUE);
		} 
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == sldFailsafeInputChannelHIGH)
			dplFailsafeInputChannelHIGH.setLcdValue((double) sldFailsafeInputChannelHIGH.getValue());
		else if (e.getSource() == sldFailsafeInputChannelLOW)
			dplFailsafeInputChannelLOW.setLcdValue((double) sldFailsafeInputChannelLOW.getValue());
	}

	private void debug(String message) {
		System.out.println(new java.util.Date() + ": " + message);
	}

	protected void loadOptionsFile() {
		// Select file using a menu
		// Load file
		String selectedFile = Resources.getResources().getUDBOptionsFile(), lineFeed = null, parameter = "", value = "";
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(selectedFile))));
			while ((lineFeed = reader.readLine()) != null) {
				if (lineFeed.startsWith("//"))
					continue;
				if (lineFeed.startsWith("#define")) {
					if (lineFeed.contains("\"")) {
						parameter = lineFeed.substring(lineFeed.indexOf("#define ") + 8, lineFeed.indexOf("\"")).trim();
						value = lineFeed.substring(lineFeed.indexOf("\""), lineFeed.lastIndexOf("\"") + 1).trim();
					} else {
						try {
							parameter = lineFeed.substring(lineFeed.indexOf("#define ") + 8, lineFeed.indexOf("\t"))
									.trim();
							value = lineFeed.substring(lineFeed.indexOf("\t")).trim();
						} catch (Exception e) {
							parameter = lineFeed.substring(lineFeed.indexOf("#define ") + 8).trim();
							value = "";
						}
					}
					if (value.contains("//") && !value.contains("http"))
						value = value.substring(0, value.indexOf("/")).trim();
					// debug(parameter + "," + value);
					processParameters(parameter, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void processWindowEvent(WindowEvent we) {
		if (we.getID() == we.WINDOW_CLOSING) {
			missionPlanner.destroyInstance();
			System.out.println("/n/nxxxxxxxxxxxxxxxxxxxxxxxx see you later xxxxxxxxxxxxxxxxxxxxx... gichangA/n/n/n");
			System.exit(0x000);
		}
	}

	protected void processParameters(String parameter, String value) {
		switch (parameter) {
		case "BOARD_TYPE": {
			cboBoardType.getModel().setSelectedItem(value);
			debug(parameter + "," + value);
			break;
		}
		case "BOARD_ORIENTATION": {
			cboBoardOrientation.getModel().setSelectedItem(value);
			break;
		}
		case "AIRFRAME_TYPE": {
			cboAirframeType.getModel().setSelectedItem(value);
			break;
		}
		case "GPS_TYPE": {
			cboGpsType.getModel().setSelectedItem(value);
			break;
		}
		case "ROLL_STABILIZATION_AILERONS": {
			chckbxStabalizationRollAilerons.setSelected(getBoolean(value));
			break;
		}
		case "ROLL_STABILIZATION_RUDDER": {
			chckbxStabalizationRollRudder.setSelected(getBoolean(value));
			break;
		}
		case "PITCH_STABILIZATION": {
			chckbxStabalizationPitch.setSelected(getBoolean(value));
			break;
		}
		case "YAW_STABILIZATION_RUDDER": {
			chckbxStabalizationYawRudder.setSelected(getBoolean(value));
			break;
		}
		case "YAW_STABILIZATION_AILERON": {
			chckbxStabalizationYawAilerons.setSelected(getBoolean(value));
			break;
		}
		case "AILERON_NAVIGATION": {
			chckbxNavAileron.setSelected(getBoolean(value));
			break;
		}
		case "RUDDER_NAVIGATION": {
			chckbxNavRudder.setSelected(getBoolean(value));
			break;
		}
		case "WIND_GAIN_ADJUSTMENT": {
			chckbxWingGainAdjustment.setSelected(getBoolean(value));
			break;
		}
		case "ALTITUDEHOLD_STABILIZED": {
			cboAltHoldStabalized.getModel().setSelectedItem(value);
			break;
		}
		case "ALTITUDEHOLD_WAYPOINT": {
			cboAltHoldWaypoint.getModel().setSelectedItem(value);
			break;
		}
		case "SPEED_CONTROL": {
			chckbxSpeedControlEnable.setSelected(getBoolean(value));
			break;
		}
		case "DESIRED_SPEED": {
			sldrSpeedControl.setValue((int) Double.parseDouble(value));
			break;
		}
		case "INVERTED_FLIGHT_STABILIZED_MODE": {
			chckbxInvFlightStabalized.setSelected(getBoolean(value));
			break;
		}
		case "INVERTED_FLIGHT_WAYPOINT_MODE": {
			chckbxInvFlightWaypoint.setSelected(getBoolean(value));
			break;
		}
		case "HOVERING_STABILIZED_MODE": {
			chckbxHoveringStabalized.setSelected(getBoolean(value));
			break;
		}
		case "HOVERING_WAYPOINT_MODE": {
			chckbxNHoveringWaypoint.setSelected(getBoolean(value));
			break;
		}
		case "USE_CAMERA_STABILIZATION": {
			chckbxCameraStabalized.setSelected(getBoolean(value));
			break;
		}
		case "MAG_YAW_DRIFT": {
			chckbxUseMagnetometer.setSelected(getBoolean(value));
			break;
		}
		case "RACING_MODE": {
			chckbxRacingModeEnable.setSelected(getBoolean(value));
			break;
		}
		case "RACING_MODE_WP_THROTTLE": {
			sldRacingModeSpeed.setValue((int) Double.parseDouble(value));
			break;
		}
		case "NORADIO": {
			chckbxNoRadio.setSelected(getBoolean(value));
			break;
		}
		case "USE_PPM_INPUT": {
			chckbxPPMEnable.setSelected(getBoolean(value));
			break;
		}
		case "PPM_NUMBER_OF_CHANNELS": {
			txtPPMNumberOfInputs.setText(value);
			break;
		}
		case "PPM_SIGNAL_INVERTED": {
			chckbxPPMSignalInverted.setSelected(getBoolean(value));
			break;
		}
		case "PPM_ALT_OUTPUT_PINS": {
			chckbxPMMAltOutputPins.setSelected(getBoolean(value));
			break;
		}
		case "NUM_INPUTS": {
			cboInputConfigNumberOfInputs.getModel().setSelectedItem(value);
			break;
		}
		case "THROTTLE_INPUT_CHANNEL": {
			cboInputConfigThrottle.getModel().setSelectedItem(value);
			break;
		}
		case "AILERON_INPUT_CHANNEL": {
			cboInputConfigAileron.getModel().setSelectedItem(value);
			break;
		}
		case "ELEVATOR_INPUT_CHANNEL": {
			cboInputConfigElevator.getModel().setSelectedItem(value);
			break;
		}
		case "RUDDER_INPUT_CHANNEL": {
			cboInputConfigRudder.getModel().setSelectedItem(value);
			break;
		}
		case "MODE_SWITCH_INPUT_CHANNEL": {
			cboInputConfigModeSwitch.getModel().setSelectedItem(value);
			break;
		}
		case "CAMERA_PITCH_INPUT_CHANNEL": {
			cboInputConfigCameraPitch.getModel().setSelectedItem(value);
			break;
		}
		case "CAMERA_YAW_INPUT_CHANNEL": {
			cboInputConfigCameraYaw.getModel().setSelectedItem(value);
			break;
		}
		case "CAMERA_MODE_INPUT_CHANNEL": {
			cboInputConfigCameraMode.getModel().setSelectedItem(value);
			break;
		}
		case "OSD_MODE_SWITCH_INPUT_CHANNEL": {
			cboInputConfigOSDModeSwitch.getModel().setSelectedItem(value);
			break;
		}
		case "PASSTHROUGH_A_INPUT_CHANNEL": {
			cboInputConfigPassthroughA.getModel().setSelectedItem(value);
			break;
		}
		case "PASSTHROUGH_B_INPUT_CHANNEL": {
			cboInputConfigPassthroughB.getModel().setSelectedItem(value);
			break;
		}
		case "PASSTHROUGH_C_INPUT_CHANNEL": {
			cboInputConfigPassthroughC.getModel().setSelectedItem(value);
			break;
		}
		case "PASSTHROUGH_D_INPUT_CHANNEL": {
			cboInputConfigPassthroughD.getModel().setSelectedItem(value);
			break;
		}
		case "LOGO_A_INPUT_CHANNEL": {
			cboInputConfigLOGOA.getModel().setSelectedItem(value);
			break;
		}
		case "LOGO_B_INPUT_CHANNEL": {
			cboInputConfigLOGOB.getModel().setSelectedItem(value);
			break;
		}
		case "LOGO_C_INPUT_CHANNEL": {
			cboInputConfigLOGOC.getModel().setSelectedItem(value);
			break;
		}
		case "LOGO_D_INPUT_CHANNEL": {
			cboInputConfigLOGOD.getModel().setSelectedItem(value);
			break;
		}
		case "NUM_OUTPUTS": {
			cboOutputConfigNumberOfOutputs.getModel().setSelectedItem(value);
			break;
		}
		case "THROTTLE_OUTPUT_CHANNEL": {
			cboOutputConfigThrottle.getModel().setSelectedItem(value);
			break;
		}
		case "AILERON_OUTPUT_CHANNEL": {
			cboOutputConfigAileron.getModel().setSelectedItem(value);
			break;
		}
		case "ELEVATOR_OUTPUT_CHANNEL": {
			cboOutputConfigElevator.getModel().setSelectedItem(value);
			break;
		}
		case "RUDDER_OUTPUT_CHANNEL": {
			cboOutputConfigRudder.getModel().setSelectedItem(value);
			break;
		}
		case "AILERON_SECONDARY_OUTPUT_CHANNEL": {
			cboOutputConfigSecondaryAileron.getModel().setSelectedItem(value);
			break;
		}
		case "CAMERA_PITCH_OUTPUT_CHANNEL": {
			cboOutputConfigCameraPitch.getModel().setSelectedItem(value);
			break;
		}
		case "CAMERA_YAW_OUTPUT_CHANNEL": {
			cboOutputConfigCameraYaw.getModel().setSelectedItem(value);
			break;
		}
		case "TRIGGER_OUTPUT_CHANNEL": {
			cboOutputConfigTriggerOutput.getModel().setSelectedItem(value);
			break;
		}
		case "PASSTHROUGH_A_OUTPUT_CHANNEL": {
			cboOutputConfigPassthroughA.getModel().setSelectedItem(value);
			break;
		}
		case "PASSTHROUGH_B_OUTPUT_CHANNEL": {
			cboOutputConfigPassthroughB.getModel().setSelectedItem(value);
			break;
		}
		case "PASSTHROUGH_C_OUTPUT_CHANNEL": {
			cboOutputConfigPassthroughC.getModel().setSelectedItem(value);
			break;
		}
		case "PASSTHROUGH_D_OUTPUT_CHANNEL": {
			cboOutputConfigPassthroughD.getModel().setSelectedItem(value);
			break;
		}
		case "AILERON_CHANNEL_REVERSED": {
			cboServoRevAileron.getModel().setSelectedItem(value);
			break;
		}
		case "ELEVATOR_CHANNEL_REVERSED": {
			cboServoRevElevator.getModel().setSelectedItem(value);
			break;
		}
		case "RUDDER_CHANNEL_REVERSED": {
			cboServoRevRudder.getModel().setSelectedItem(value);
			break;
		}
		case "AILERON_SECONDARY_CHANNEL_REVERSED": {
			cboServoRevAileron.getModel().setSelectedItem(value);
			break;
		}
		case "THROTTLE_CHANNEL_REVERSED": {
			cboServoRevThrottle.getModel().setSelectedItem(value);
			break;
		}
		case "CAMERA_PITCH_CHANNEL_REVERSED": {
			cboServoRevCameraPitch.getModel().setSelectedItem(value);
			break;
		}
		case "CAMERA_YAW_CHANNEL_REVERSED": {
			cboServoRevCameraYaw.getModel().setSelectedItem(value);
			break;
		}
		case "ELEVON_VTAIL_SURFACES_REVERSED": {
			chbxServoRevElevons.setSelected(getBoolean(value));
			break;
		}
		case "MODE_SWITCH_THRESHOLD_LOW": {
			dplModeSwitchThresLOW.setLcdValue(Double.valueOf(value));
			sldModeSwitchThresLOW.setValue((int) Double.parseDouble(value));
			break;
		}
		case "MODE_SWITCH_THRESHOLD_HIGH": {
			dplModeSwitchThresHIGH.setLcdValue(Double.valueOf(value));
			sldModeSwitchThresHIGH.setValue((int) Double.parseDouble(value));
			break;
		}
		case "MODE_SWITCH_TWO_POSITION": {
			chbxModeSwitch2PositionSwitch.setSelected(getBoolean(value));
			break;
		}
		case "FAILSAFE_INPUT_CHANNEL": {
			cboFailsafeInputChannel.getModel().setSelectedItem(value);
			break;
		}
		case "FAILSAFE_INPUT_MIN": {
			sldFailsafeInputChannelLOW.setValue((int) Double.parseDouble(value));
			dplFailsafeInputChannelLOW.setLcdValue(Double.valueOf(value));
			break;
		}
		case "FAILSAFE_INPUT_MAX": {
			sldFailsafeInputChannelHIGH.setValue((int) Double.parseDouble(value));
			dplModeSwitchThresHIGH.setLcdValue(Double.valueOf(value));
			break;
		}
		case "FAILSAFE_TYPE": {
			cboFailsafeType.getModel().setSelectedItem(value);
			break;
		}
		case "FAILSAFE_HOLD": {
			chbxFailasfeHold.setSelected(getBoolean(value));
			break;
		}
		case "SERIAL_OUTPUT_FORMAT": {
			cboMavSerialOutputFormat.getModel().setSelectedItem(value);
			break;
		}
		case "MAVLINK_SYSID": {
			sldMavSYSID.setValue((int) Double.parseDouble(value));
			break;
		}
		case "USE_OSD": {
			chckbxUseOsd.setSelected(getBoolean(value));
			break;
		}
		case "NUM_ANALOG_INPUTS": {
			cboNoOfAnalogInputs.getModel().setSelectedItem(value);
			break;
		}
		case "ANALOG_CURRENT_INPUT_CHANNEL": {
			cboAnalogCurrentInputChannel.getModel().setSelectedItem(value);
			break;
		}
		case "ANALOG_VOLTAGE_INPUT_CHANNEL": {
			cboAnalogVoltageInputChannel.getModel().setSelectedItem(value);
			break;
		}
		case "ANALOG_RSSI_INPUT_CHANNEL": {
			cboAnalogRSSIInputChannel.getModel().setSelectedItem(value);
			break;
		}
		case "RSSI_MIN_SIGNAL_VOLTAGE": {
			sdrRSSIMinVoltage.setValue((int) (Math.round(Double.valueOf(value))));
			dlsRSSIMinVoltage.setLcdValue(Double.valueOf(value));
			break;
		}
		case "RSSI_MAX_SIGNAL_VOLTAGE": {
			sdrRSSIMaxVoltage.setValue((int) (Math.round(Double.valueOf(value))));
			dlsRSSIMaxVoltage.setLcdValue(Double.valueOf(value));
			break;
		}
		case "TRIGGER_TYPE": {
			cboTriggerType.getModel().setSelectedItem(value);
			break;
		}
		case "TRIGGER_ACTION": {
			cboTriggerAction.getModel().setSelectedItem(value);
			break;
		}
		case "TRIGGER_SERVO_LOW": {
			sldTRIGGER_SERVO_LOW.setValue((int) (Math.round(Double.valueOf(value))));
			dslTRIGGER_SERVO_LOW.setLcdValue(Double.valueOf(value));
			break;
		}
		case "TRIGGER_SERVO_HIGH": {
			sldTRIGGER_SERVO_HIGH.setValue((int) (Math.round(Double.valueOf(value))));
			dslTRIGGER_SERVO_HIGH.setLcdValue(Double.valueOf(value));
			break;
		}
		case "TRIGGER_PULSE_DURATION": {
			sldTRIGGER_PULSE_DURATION.setValue((int) (Math.round(Double.valueOf(value))));
			dslTRIGGER_PULSE_DURATION.setLcdValue(Double.valueOf(value));
			break;
		}
		case "TRIGGER_REPEAT_PERIOD": {
			sldTRIGGER_REPEAT_PERIOD.setValue((int) (Math.round(Double.valueOf(value))));
			dslTRIGGER_REPEAT_PERIOD.setLcdValue(Double.valueOf(value));
			break;
		}
		case "SERVOSAT": {
			txtSERVOSAT.setText(value);
			break;
		}
		case "ROLLKP": {
			txtROLLKP.setText(value);
			break;
		}
		case "ROLLKD": {
			txtROLLKD.setText(value);
			break;
		}
		case "YAWKP_AILERON": {
			txtYAWKP_AILERON.setText(value);
			break;
		}
		case "YAWKD_AILERON": {
			txtYAWKD_AILERON.setText(value);
			break;
		}
		case "AILERON_BOOST": {
			txtAILERON_BOOST.setText(value);
			break;
		}
		case "PITCHGAIN": {
			txtPITCHGAIN.setText(value);
			break;
		}
		case "PITCHKD": {
			txtPITCHKD.setText(value);
			break;
		}
		case "RUDDER_ELEV_MIX": {
			txtRUDDER_ELEV_MIX.setText(value);
			break;
		}
		case "ROLL_ELEV_MIX": {
			txtROLL_ELEV_MIX.setText(value);
			break;
		}
		case "ELEVATOR_BOOST": {
			txtELEVATOR_BOOST.setText(value);
			break;
		}
		case "INVERTED_NEUTRAL_PITCH": {
			txtINVERTED_NEUTRAL_PITCH.setText(value);
			break;
		}
		case "YAWKP_RUDDER": {
			txtYAWKP_RUDDER.setText(value);
			break;
		}
		case "YAWKD_RUDDER": {
			txtYAWKD_RUDDER.setText(value);
			break;
		}
		case "ROLLKP_RUDDER": {
			txtROLLKP_RUDDER.setText(value);
			break;
		}
		case "ROLLKD_RUDDER": {
			txtROLLKD_RUDDER.setText(value);
			break;
		}
		case "MANUAL_AILERON_RUDDER_MIX": {
			txtMANUAL_AILERON_RUDDER_MIX.setText(value);
			break;
		}
		case "RUDDER_BOOST": {
			txtRUDDER_BOOST.setText(value);
			break;
		}
		case "HOVER_ROLLKP": {
			txtHOVER_ROLLKP.setText(value);
			break;
		}
		case "HOVER_ROLLKD": {
			txtHOVER_ROLLKD.setText(value);
			break;
		}
		case "HOVER_PITCHGAIN": {
			txtHOVER_PITCHGAIN.setText(value);
			break;
		}
		case "HOVER_PITCHKD": {
			txtHOVER_PITCHKD.setText(value);
			break;
		}
		case "HOVER_PITCH_OFFSET": {
			txtHOVER_PITCH_OFFSET.setText(value);
			break;
		}
		case "HOVER_YAWKP": {
			txtHOVER_YAWKP.setText(value);
			break;
		}
		case "HOVER_YAWKD": {
			txtHOVER_YAWKD.setText(value);
			break;
		}
		case "HOVER_YAW_OFFSET": {
			txtHOVER_YAW_OFFSET.setText(value);
			break;
		}
		case "HOVER_PITCH_TOWARDS_WP": {
			txtHOVER_PITCH_TOWARDS_WP.setText(value);
			break;
		}
		case "HOVER_NAV_MAX_PITCH_RADIUS": {
			txtHOVER_NAV_MAX_PITCH_RADIUS.setText(value);
			break;
		}
		case "CAMERA_MODE_THRESHOLD_LOW": {
			sdlCameraModeThresholdLOW.setValue((int) (Math.round(Double.valueOf(value))));
			dplCameraModeThresholdLOW.setLcdValue(Double.valueOf(value));
			break;
		}
		case "CAMERA_MODE_THRESHOLD_HIGH": {
			sdlCameraModeThresholdHIGH.setValue((int) (Math.round(Double.valueOf(value))));
			dplCameraModeThresholdHIGH.setLcdValue(Double.valueOf(value));
			break;
		}
		case "CAM_TAN_PITCH_IN_STABILIZED_MODE": {
			txtCAM_TAN_PITCH_IN_STABILIZED_MODE.setText(value);
			break;
		}
		case "CAM_YAW_IN_STABILIZED_MODE": {
			txtCAM_YAW_IN_STABILIZED_MODE.setText(value);
			break;
		}
		case "CAM_PITCH_SERVO_THROW": {
			txtCAM_PITCH_SERVO_THROW.setText(value);
			break;
		}
		case "CAM_PITCH_SERVO_MAX": {
			txtCAM_PITCH_SERVO_MAX.setText(value);
			break;
		}
		case "CAM_PITCH_SERVO_MIN": {
			txtCAM_PITCH_SERVO_MIN.setText(value);
			break;
		}
		case "CAM_PITCH_OFFSET_CENTRED": {
			txtCAM_PITCH_OFFSET_CENTRED.setText(value);
			break;
		}
		case "CAM_YAW_SERVO_THROW": {
			txtCAM_YAW_SERVO_THROW.setText(value);
			break;
		}
		case "CAM_YAW_SERVO_MAX": {
			txtCAM_YAW_SERVO_MAX.setText(value);
			break;
		}
		case "CAM_YAW_SERVO_MIN": {
			txtCAM_YAW_SERVO_MIN.setText(value);
			break;
		}
		case "CAM_YAW_OFFSET_CENTRED": {
			txtCAM_YAW_OFFSET_CENTRED.setText(value);
			break;
		}
		case "CAM_TESTING_OVERIDE": {
			txtCAM_TESTING_OVERIDE.setText(value);
			break;
		}

		case "CAM_TESTING_YAW_ANGLE": {
			txtCAM_TESTING_YAW_ANGLE.setText(value);
			break;
		}

		case "CAM_TESTING_PITCH_ANGLE": {
			txtCAM_TESTING_PITCH_ANGLE.setText(value);
			break;
		}
		case "CAM_USE_EXTERNAL_TARGET_DATA": {
			chckbxCamuseexternaltargetdata.setSelected(getBoolean(value));
			break;
		}
		case "HEIGHT_TARGET_MIN": {
			txtHEIGHT_TARGET_MIN.setText(value);
			break;
		}
		case "HEIGHT_TARGET_MAX": {
			txtHEIGHT_TARGET_MAX.setText(value);
			break;
		}
		case "HEIGHT_MARGIN": {
			txtHEIGHT_MARGIN.setText(value);
			break;
		}
		case "ALT_HOLD_THROTTLE_MIN": {
			txtALT_HOLD_THROTTLE_MIN.setText(value);
			break;
		}
		case "ALT_HOLD_THROTTLE_MAX": {
			txtALT_HOLD_THROTTLE_MAX.setText(value);
			break;
		}
		case "ALT_HOLD_PITCH_MIN": {
			txtALT_HOLD_PITCH_MIN.setText(value);
			break;
		}
		case "ALT_HOLD_PITCH_MAX": {
			txtALT_HOLD_PITCH_MAX.setText(value);
			break;
		}
		case "ALT_HOLD_PITCH_HIGH": {
			txtALT_HOLD_PITCH_HIGH.setText(value);
			break;
		}
		case "RTL_PITCH_DOWN": {
			txtRTL_PITCH_DOWN.setText(value);
			break;
		}
		case " HILSIM": {
			cboHILSIM_BAUD.getModel().setSelectedItem(value);
			break;
		}
		case "HILSIM_BAUD": {
			chckbxHilsimEnable.setSelected(getBoolean(value));
			break;
		}
		case "FLIGHT_PLAN_TYPE": {
			cboFLIGHT_PLAN_TYPE.getModel().setSelectedItem(value);
			break;
		}
		case "RECORD_FREE_STACK_SPACE": {
			chckbxRecordfreestackspace.setSelected(getBoolean(value));
			break;
		}

		case "ID_VEHICLE_MODEL_NAME": {
			txtVEHICLE_MODEL_NAME.setText(value);
			break;
		}
		case "ID_VEHICLE_REGISTRATION": {
			txtVEHICLE_REGISTRATION.setText(value);
			break;
		}
		case "ID_LEAD_PILOT": {
			txtLEAD_PILOT.setText(value);
			break;
		}
		case "ID_DIY_DRONES_URL": {
			txtDIY_DRONES_URL.setText(value);
			break;
		}
		}
		repaint();
	}

	private boolean getBoolean(String value) {
		return value == "1" ? true : false;
	}

	static private final int PROJECT_FILE_DIALOGUE = 0, COMPILER_DIRECTORY_DIALOGUE = 1;

	protected void chooseDirectory(int purposeOfDirectory) {
		final JFileChooser fileChooser = new JFileChooser();
		if (purposeOfDirectory == PROJECT_FILE_DIALOGUE) {
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("MPLAB Project File", "mcp"));
		} else if (purposeOfDirectory == COMPILER_DIRECTORY_DIALOGUE)
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		try {
			int status = fileChooser.showOpenDialog(this);
			if (status == JFileChooser.APPROVE_OPTION) {
				synchroniseProperties = true;
				if (purposeOfDirectory == PROJECT_FILE_DIALOGUE)
					txtProjectSourceFile.setText(fileChooser.getSelectedFile().toString());
				else if (purposeOfDirectory == COMPILER_DIRECTORY_DIALOGUE)
					txtMPLABDirectory.setText(fileChooser.getSelectedFile().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String getProjectSource() {
		return txtProjectSourceFile.getText();
	}

	protected String getMPLABDirectory() {
		return txtMPLABDirectory.getText();
	}

	protected void buildCode() {
		try {
			String sourceFile = null, buildString = "-mcpu=33FJ256GP710A ", runString = "";
			UdbMcpFile source = new UdbMcpFile(Resources.getResources().getUDBProjectFile());
			Iterator<String> sourceFiles = source.getSourceFiles();
			Runtime runtime = Runtime.getRuntime();

			while (sourceFiles.hasNext()) {
				sourceFile = sourceFiles.next();
				runString = "\"" + Resources.getResources().getMPLABDirectory() + "/bin/pic30-gcc.exe\" -mcpu="
						+ source.getDevice() + " " + source.getToolSettings() + " " + sourceFile + " -o"
						+ source.getBuildDirectory() + "\\" + sourceFile + " " + source.getIncludeDirectory() + " "
						+ source.getToolTrailer();
				System.out.println(runString);
				runtime.exec(runString, null, new File(Resources.getResources().getProjectDirectory()));
			}
			File folder = new File(Resources.getResources().getProjectDirectory() + source.getBuildDirectory());
			File[] files = folder.listFiles();
			for (int a = 0; a < files.length; a++)
				buildString += "\"build\\udb4\\" + files[a].getName() + "\" ";
			buildString += "\"" + source.getLibDsp() + "\" -o\"MatrixPilot-udb4.cof\" -legacy-libc -Wl,-L\""
					+ source.getLibDirectory() + "\",-Tp" + source.getDevice() + ".gld,--defsym=__MPLAB_BUILD=1,-Map=\""
					+ this.getBoardType() + ".map\",--report-mem";
			// "C:\Program Files\Microchip\mplabc30\v3.31\bin\pic30-bin2hex.exe"
			// "G:\matrixpilot\gentlenav\MatrixPilot\MatrixPilot-udb4.cof"
			System.out.println(buildString);
			// Process compilationProcess =
			// Runtime.getRuntime().exec(Resources.getResources().getMPLABDirectory()
			// + "/bin/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getBoardType() {
		String boardType = null;
		switch (cboBoardType.getModel().getSelectedItem().toString()) {
		case "GREEN_BOARD": {
			break;
		}
		case "RED_BOARD": {
			break;
		}
		case "UDB3_BOARD": {
			boardType = "MatrixPilot";
			break;
		}
		case "UDB4_BOARD": {
			boardType = "MatrixPilot-udb4";
			break;
		}
		case "AUAV1_BOARD": {
			break;
		}
		}
		return boardType;
	}
	/*
	 * protected void processWindowEvent(WindowEvent we){ if
	 * (we.getID()==WindowEvent.WINDOW_CLOSING){ String prjSource =
	 * getGui().getProjectSource(); String mplabSource =
	 * getGui().getMPLABDirectory(); Resources.getResources().saveResources(new
	 * String[]{prjSource}, new String[]{mplabSource}); System.exit(0x0); } }
	 */

	/****
	 * Moved these definations here so that they are accessible to functions
	 * outside the GUI creation function
	 */
	private JCheckBox chckbxCameraStabalized = new JCheckBox("Camera Stabalization");
	private JCheckBox chckbxUseMagnetometer = new JCheckBox("Use Magnetometer");
	private JCheckBox chckbxNoRadio = new JCheckBox("No Radio");
	private JCheckBox chckbxPPMEnable = new JCheckBox("Enable");
	private JCheckBox chckbxPPMSignalInverted = new JCheckBox("Signal Inverted");
	private JCheckBox chckbxPMMAltOutputPins = new JCheckBox("Alt output pins");
	private JCheckBox chckbxHoveringStabalized = new JCheckBox("Stabilized Mode");
	private JCheckBox chckbxNHoveringWaypoint = new JCheckBox("Waypoint Mode");
	private JCheckBox chckbxInvFlightStabalized = new JCheckBox("Stabilized Mode");
	private JCheckBox chckbxInvFlightWaypoint = new JCheckBox("Waypoint Mode");
	private JCheckBox chckbxSpeedControlEnable = new JCheckBox("Enable");
	private JSlider sldrSpeedControl = new JSlider();
	private JCheckBox chckbxRacingModeEnable = new JCheckBox("Enable");
	private JSlider sldRacingModeSpeed = new JSlider();
	private JComboBox cboAltHoldStabalized = new JComboBox();
	private JComboBox cboAltHoldWaypoint = new JComboBox();
	private JCheckBox chckbxNavAileron = new JCheckBox("Aileron");
	private JCheckBox chckbxNavRudder = new JCheckBox("Rudder");
	private JCheckBox chckbxStabalizationRollRudder = new JCheckBox("Rudder");
	private JCheckBox chckbxStabalizationRollAilerons = new JCheckBox("Ailerons");
	private JCheckBox chckbxStabalizationYawRudder = new JCheckBox("Rudder");
	private JCheckBox chckbxStabalizationYawAilerons = new JCheckBox("Ailerons");
	private JCheckBox chckbxStabalizationPitch = new JCheckBox("Pitch");
	private JComboBox cboBoardType = new JComboBox();
	private JComboBox cboBoardOrientation = new JComboBox();
	private JComboBox cboAirframeType = new JComboBox();
	private JComboBox cboGpsType = new JComboBox();
	private JComboBox cboMavSerialOutputFormat = new JComboBox();
	private JSlider sldMavSYSID = new JSlider();
	private JButton btnLoad = new JButton("Load");
	private JCheckBox chckbxWingGainAdjustment = new JCheckBox("Wind Gain Adjustment");
	private JComboBox cboInputConfigThrottle = new JComboBox();
	private JComboBox cboInputConfigAileron = new JComboBox();
	private JComboBox cboInputConfigElevator = new JComboBox();
	private JComboBox cboInputConfigRudder = new JComboBox();
	private JComboBox cboInputConfigModeSwitch = new JComboBox();
	private JComboBox cboInputConfigCameraPitch = new JComboBox();
	private JComboBox cboInputConfigCameraYaw = new JComboBox();
	private JComboBox cboInputConfigCameraMode = new JComboBox();
	private JComboBox cboInputConfigOSDModeSwitch = new JComboBox();
	private JComboBox cboInputConfigPassthroughA = new JComboBox();
	private JComboBox cboInputConfigPassthroughB = new JComboBox();
	private JComboBox cboInputConfigPassthroughC = new JComboBox();
	private JComboBox cboInputConfigPassthroughD = new JComboBox();
	private JComboBox cboInputConfigNumberOfInputs = new JComboBox();
	private JComboBox cboInputConfigLOGOD = new JComboBox();
	private JComboBox cboInputConfigLOGOC = new JComboBox();
	private JComboBox cboInputConfigLOGOA = new JComboBox();
	private JComboBox cboInputConfigLOGOB = new JComboBox();
	private JComboBox cboOutputConfigThrottle = new JComboBox();
	private JComboBox cboOutputConfigAileron = new JComboBox();
	private JComboBox cboOutputConfigElevator = new JComboBox();
	private JComboBox cboOutputConfigRudder = new JComboBox();
	private JComboBox cboOutputConfigCameraPitch = new JComboBox();
	private JComboBox cboOutputConfigCameraYaw = new JComboBox();
	private JComboBox cboOutputConfigTriggerOutput = new JComboBox();
	private JComboBox cboOutputConfigPassthroughA = new JComboBox();
	private JComboBox cboOutputConfigPassthroughB = new JComboBox();
	private JComboBox cboOutputConfigPassthroughC = new JComboBox();
	private JComboBox cboOutputConfigPassthroughD = new JComboBox();
	private JComboBox cboOutputConfigNumberOfOutputs = new JComboBox();
	private JComboBox cboServoRevAileron = new JComboBox();
	private JComboBox cboServoRevElevator = new JComboBox();
	private JComboBox cboServoRevSecondaryAileron = new JComboBox();
	private JComboBox cboServoRevThrottle = new JComboBox();
	private JComboBox cboServoRevCameraPitch = new JComboBox();
	private JComboBox cboServoRevCameraYaw = new JComboBox();
	private JCheckBox chbxServoRevElevons = new JCheckBox("Elevon - VTail Surfaces Reversed");
	private JComboBox cboServoRevRudder = new JComboBox();
	private DisplaySingle dplModeSwitchThresLOW = new DisplaySingle();
	private JSlider sldModeSwitchThresLOW = new JSlider();
	private JSlider sldModeSwitchThresHIGH = new JSlider();
	private DisplaySingle dplModeSwitchThresHIGH = new DisplaySingle();
	private JCheckBox chbxModeSwitch2PositionSwitch = new JCheckBox("Mode Switch 2 Position switch");
	private DisplaySingle dplFailsafeInputChannelLOW = new DisplaySingle();
	private JSlider sldFailsafeInputChannelLOW = new JSlider();
	private JSlider sldFailsafeInputChannelHIGH = new JSlider();
	private DisplaySingle dplFailsafeInputChannelHIGH = new DisplaySingle();
	private JComboBox cboFailsafeInputChannel = new JComboBox();
	private JCheckBox chbxFailasfeHold = new JCheckBox("Failsafe Hold");
	private JTextPane textPaneCommandResults = new JTextPane();
	private JButton btnCompile = new JButton("Compile");
	private JComboBox cboOutputConfigSecondaryAileron = new JComboBox();
	private JComboBox cboFailsafeType = new JComboBox();
	private final JPanel udbPicOptions = new JPanel();
	private JTextField txtProjectSourceFile;
	private JTextField txtMPLABDirectory;
	private JButton btnSelectSource = new JButton("Select Source");
	private JButton btnSelectMplab = new JButton("Select MPLAB");
	private JCheckBox chckbxUseOsd = new JCheckBox("Use OSD");
	private JTextField txtSERVOSAT;
	private JTextField txtROLLKP;
	private JTextField txtROLLKD;
	private JTextField txtYAWKP_AILERON;
	private JTextField txtYAWKD_AILERON;
	private JTextField txtAILERON_BOOST;
	private JTextField txtPITCHGAIN;
	private JTextField txtPITCHKD;
	private JTextField txtRUDDER_ELEV_MIX;
	private JTextField txtROLL_ELEV_MIX;
	private JTextField txtELEVATOR_BOOST;
	private JTextField txtINVERTED_NEUTRAL_PITCH;
	private JTextField txtYAWKP_RUDDER;
	private JTextField txtYAWKD_RUDDER;
	private JTextField txtROLLKP_RUDDER;
	private JTextField txtROLLKD_RUDDER;
	private JTextField txtMANUAL_AILERON_RUDDER_MIX;
	private JTextField txtRUDDER_BOOST;
	private JTextField txtHOVER_ROLLKP;
	private JTextField txtHOVER_ROLLKD;
	private JTextField txtHOVER_PITCHGAIN;
	private JTextField txtHOVER_PITCHKD;
	private JTextField txtHOVER_PITCH_OFFSET;
	private JTextField txtHOVER_YAWKP;
	private JTextField txtHOVER_YAWKD;
	private JTextField txtHOVER_YAW_OFFSET;
	private JTextField txtHOVER_PITCH_TOWARDS_WP;
	private JTextField txtHOVER_NAV_MAX_PITCH_RADIUS;
	private JTextField txtVEHICLE_MODEL_NAME;
	private JTextField txtVEHICLE_REGISTRATION;
	private JTextField txtLEAD_PILOT;
	private JTextField txtDIY_DRONES_URL;
	private JTextField txtCAM_TAN_PITCH_IN_STABILIZED_MODE;
	private JTextField txtCAM_YAW_IN_STABILIZED_MODE;
	private JTextField txtCAM_PITCH_SERVO_THROW;
	private JTextField txtCAM_PITCH_SERVO_MAX;
	private JTextField txtCAM_PITCH_SERVO_MIN;
	private JTextField txtCAM_PITCH_OFFSET_CENTRED;
	private JTextField txtCAM_YAW_SERVO_THROW;
	private JTextField txtCAM_YAW_SERVO_MAX;
	private JTextField txtCAM_YAW_SERVO_MIN;
	private JTextField txtCAM_YAW_OFFSET_CENTRED;
	private JTextField txtCAM_TESTING_OVERIDE;
	private JTextField txtCAM_TESTING_YAW_ANGLE;
	private JTextField txtCAM_TESTING_PITCH_ANGLE;
	private JTextField txtRTL_PITCH_DOWN;
	private JTextField txtHEIGHT_TARGET_MIN;
	private JTextField txtHEIGHT_TARGET_MAX;
	private JTextField txtHEIGHT_MARGIN;
	private JTextField txtALT_HOLD_THROTTLE_MIN;
	private JTextField txtALT_HOLD_THROTTLE_MAX;
	private JTextField txtALT_HOLD_PITCH_MIN;
	private JTextField txtALT_HOLD_PITCH_MAX;
	private JTextField txtALT_HOLD_PITCH_HIGH;
	private JComboBox cboAnalogCurrentInputChannel = new JComboBox();
	private JComboBox cboAnalogVoltageInputChannel = new JComboBox();
	private JComboBox cboAnalogRSSIInputChannel = new JComboBox();
	private DisplaySingle dlsRSSIMinVoltage = new DisplaySingle();
	private DisplaySingle dlsRSSIMaxVoltage = new DisplaySingle();
	private JSlider sdrRSSIMaxVoltage = new JSlider();
	private JSlider sdrRSSIMinVoltage = new JSlider();
	private JComboBox cboTriggerType = new JComboBox();
	private JComboBox cboTriggerAction = new JComboBox();
	private JSlider sldTRIGGER_PULSE_DURATION = new JSlider();
	private JSlider sldTRIGGER_REPEAT_PERIOD = new JSlider();
	private JSlider sldTRIGGER_SERVO_HIGH = new JSlider();
	private JSlider sldTRIGGER_SERVO_LOW = new JSlider();
	private DisplaySingle dslTRIGGER_REPEAT_PERIOD = new DisplaySingle();
	private DisplaySingle dslTRIGGER_PULSE_DURATION = new DisplaySingle();
	private DisplaySingle dslTRIGGER_SERVO_HIGH = new DisplaySingle();
	private DisplaySingle dslTRIGGER_SERVO_LOW = new DisplaySingle();
	private DisplaySingle dplCameraModeThresholdHIGH = new DisplaySingle();
	private JSlider sdlCameraModeThresholdHIGH = new JSlider();
	private JSlider sdlCameraModeThresholdLOW = new JSlider();
	private JCheckBox chckbxMakeBackup = new JCheckBox("Make Backup (Once per day)");
	private JCheckBox chckbxMakeBackupBefore = new JCheckBox("Make backup before each save");
	private DisplaySingle dplCameraModeThresholdLOW = new DisplaySingle();
	private JCheckBox chckbxCamuseexternaltargetdata = new JCheckBox("Cam_Use_External_Target_Data");
	private JComboBox cboFLIGHT_PLAN_TYPE = new JComboBox();
	private JCheckBox chckbxRecordfreestackspace = new JCheckBox("RECORD_FREE_STACK_SPACE");
	private JCheckBox chckbxEnableVtol = new JCheckBox("Enable VTOL");
	private JComboBox cboHILSIM_BAUD = new JComboBox();
	private JCheckBox chckbxHilsimEnable = new JCheckBox("HILSIM Enable");
	private JButton btnSave = new JButton("Save");
	private JComboBox cboNoOfAnalogInputs = new JComboBox();
	private JTextField txtIndicatedSpeed;
	private JTextField txtIndiacatedSpeed_Racing;

	private void serializeOptionsFile() {
		String output = "// This file is part of MatrixPilot.\n" + "//\n"
				+ "//    http://code.google.com/p/gentlenav/\n" + "//\n" + "// Copyright 2009-2011 MatrixPilot Team\n"
				+ "// See the AUTHORS.TXT file for a list of authors of MatrixPilot.\n" + "//\n"
				+ "// MatrixPilot is free software: you can redistribute it and/or modify\n"
				+ "// it under the terms of the GNU General Public License as published by\n"
				+ "// the Free Software Foundation, either version 3 of the License, or\n"
				+ "// (at your option) any later version.\n" + "//\n"
				+ "// MatrixPilot is distributed in the hope that it will be useful,\n"
				+ "// but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
				+ "// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
				+ "// GNU General Public License for more details.\n" + "//\n"
				+ "// You should have received a copy of the GNU General Public License\n"
				+ "// along with MatrixPilot.  If not, see <http://www.gnu.org/licenses/>.\n" + "\n" + "\n"
				+ "////////////////////////////////////////////////////////////////////////////////\n"
				+ "// options.h\n" + "// Bill Premerlani's UAV Dev Board\n" + "// \n"
				+ "// This file includes all of the user-configuration for this firmware,\n"
				+ "// with the exception of waypoints, which live in the waypoints.h file.\n" + "// \n"
				+ "// Note that there is a small but growing library of preset options.h files for\n"
				+ "// specific planes located in the MatrixPilot/example-options-files directory.\n"
				+ "// You can use one of those files by replacing this file with that one.\n" + "\n" + "\n"
				+ "////////////////////////////////////////////////////////////////////////////////\n"
				+ "// Set Up Board Type\n"
				+ "// GREEN_BOARD - Board is green and includes 2 vertical gyro daugter-boards.\n"
				+ "// RED_BOARD   - Board is red, and includes 2 vertical gyro daugter-boards.\n"
				+ "// UDB3_BOARD  - Board is red, and includes a single, flat, multi-gyro daugter-board.\n"
				+ "// UDB4_BOARD  - Board is red, has 8 inputs, 8 output and no gyro daughter-board.\n"
				+ "// AUAV1_BOARD - Nick Arsov's UDB3 clone, version one\n"
				+ "// See the MatrixPilot wiki for more details on different UDB boards.\n"
				+ "// If building for the UDB4, use the MatrixPilot-udb4.mcw project workspace.\n";
		output += "#define BOARD_TYPE" + "\t\t\t" + cboBoardType.getModel().getSelectedItem() + "\n";
		output += "                                                                                                          \n"
				+ "                                                                                                          \n"
				+ "////////////////////////////////////////////////////////////////////////////////                          \n"
				+ "// Use board orientation to change the mounting direction of the board.                                   \n"
				+ "// Note: For UDB3 and older versions of UDB, Y arrow points to the front, GPS connector is on the front.  \n"
				+ "//       For UDB4, X arrow points to the front, GPS connectors are on the front.                          \n"
				+ "// The following 6 orientations have the board parallel with the ground.                                  \n"
				+ "// ORIENTATION_FORWARDS:  Component-side up,   GPS connector front                                        \n"
				+ "// ORIENTATION_BACKWARDS: Component-side up,   GPS connector back                                         \n"
				+ "// ORIENTATION_INVERTED:  Component-side down, GPS connector front                                        \n"
				+ "// ORIENTATION_FLIPPED:   Component-side down, GPS connector back                                         \n"
				+ "// ORIENTATION_YAWCW:     Component-side up,   GPS connector to the right                                 \n"
				+ "// ORIENTATION_YAWCCW:    Component-side up,   GPS connector to the left                                  \n"
				+ "//                                                                                                        \n"
				+ "// The following 2 orientations are \"knife edge\" mountings                                                \n"
				+ "// ORIENTATION_ROLLCW: Rick's picture #9, board rolled 90 degrees clockwise,                              \n"
				+ "//		from point of view of the pilot                                                                    \n"
				+ "// ORIENTATION_ROLLCW180: Rick's pitcure #11, board rolled 90 degrees clockwise,                          \n"
				+ "//		from point of view of the pilot, then rotate the board 180 around the Z axis of the plane,         \n"
				+ "//		so that the GPS connector points toward the tail of the plane                                      \n";
		output += "#define BOARD_ORIENTATION" + "\t\t\t" + cboBoardOrientation.getModel().getSelectedItem() + "\n";
		output += "#define AIRFRAME_TYPE" + "\t\t\t" + cboAirframeType.getModel().getSelectedItem() + "\n";
		output += "#define GPS_TYPE" + "\t\t\t" + cboGpsType.getModel().getSelectedItem() + "\n";
		output += "#define ROLL_STABILIZATION_AILERONS" + "\t\t\t" + chckbxStabalizationRollAilerons.isSelected()
				+ "\n";
		output += "#define ROLL_STABILIZATION_RUDDER" + "\t\t\t" + chckbxStabalizationRollRudder.isSelected() + "\n";
		output += "#define PITCH_STABILIZATION" + "\t\t\t" + chckbxStabalizationPitch.isSelected() + "\n";
		output += "#define YAW_STABILIZATION_RUDDER" + "\t\t\t" + chckbxStabalizationYawRudder.isSelected() + "\n";
		output += "#define YAW_STABILIZATION_AILERON" + "\t\t\t" + chckbxStabalizationYawAilerons.isSelected() + "\n";
		output += "#define AILERON_NAVIGATION" + "\t\t\t" + chckbxNavAileron.isSelected() + "\n";
		output += "#define RUDDER_NAVIGATION" + "\t\t\t" + chckbxNavRudder.isSelected() + "\n";
		output += "#define WIND_GAIN_ADJUSTMENT" + "\t\t\t" + chckbxWingGainAdjustment.isSelected() + "\n";
		output += "#define ALTITUDEHOLD_STABILIZED" + "\t\t\t" + cboAltHoldStabalized.getModel().getSelectedItem()
				+ "\n";
		output += "#define ALTITUDEHOLD_WAYPOINT" + "\t\t\t" + cboAltHoldWaypoint.getModel().getSelectedItem() + "\n";
		output += "#define SPEED_CONTROL" + "\t\t\t" + chckbxSpeedControlEnable.isSelected() + "\n";
		output += "#define INVERTED_FLIGHT_STABILIZED_MODE" + "\t\t\t" + chckbxInvFlightStabalized.isSelected() + "\n";
		output += "#define INVERTED_FLIGHT_WAYPOINT_MODE" + "\t\t\t" + chckbxInvFlightWaypoint.isSelected() + "\n";
		output += "#define HOVERING_STABILIZED_MODE" + "\t\t\t" + chckbxHoveringStabalized.isSelected() + "\n";
		output += "#define HOVERING_WAYPOINT_MODE" + "\t\t\t" + chckbxNHoveringWaypoint.isSelected() + "\n";
		output += "#define USE_CAMERA_STABILIZATION" + "\t\t\t" + chckbxCameraStabalized.isSelected() + "\n";
		output += "#define MAG_YAW_DRIFT" + "\t\t\t" + chckbxUseMagnetometer.isSelected() + "\n";
		output += "#define RACING_MODE" + "\t\t\t" + chckbxRacingModeEnable.isSelected() + "\n";
		output += "#define NORADIO" + "\t\t\t" + chckbxNoRadio.isSelected() + "\n";
		output += "#define USE_PPM_INPUT" + "\t\t\t" + chckbxPPMEnable.isSelected() + "\n";
		output += "#define PPM_NUMBER_OF_CHANNELS" + "\t\t\t" + txtPPMNumberOfInputs.getText() + "\n";
		output += "#define PPM_SIGNAL_INVERTED" + "\t\t\t" + chckbxPPMSignalInverted.isSelected() + "\n";
		output += "#define PPM_ALT_OUTPUT_PINS" + "\t\t\t" + chckbxPMMAltOutputPins.isSelected() + "\n";
		output += "#define NUM_INPUTS" + "\t\t\t" + cboInputConfigNumberOfInputs.getModel().getSelectedItem() + "\n";
		output += "#define THROTTLE_INPUT_CHANNEL" + "\t\t\t" + cboInputConfigThrottle.getModel().getSelectedItem()
				+ "\n";
		output += "#define AILERON_INPUT_CHANNEL" + "\t\t\t" + cboInputConfigAileron.getModel().getSelectedItem()
				+ "\n";
		output += "#define ELEVATOR_INPUT_CHANNEL" + "\t\t\t" + cboInputConfigElevator.getModel().getSelectedItem()
				+ "\n";
		output += "#define RUDDER_INPUT_CHANNEL" + "\t\t\t" + cboInputConfigRudder.getModel().getSelectedItem() + "\n";
		output += "#define MODE_SWITCH_INPUT_CHANNEL" + "\t\t\t" + cboInputConfigModeSwitch.getModel().getSelectedItem()
				+ "\n";
		output += "#define CAMERA_PITCH_INPUT_CHANNEL" + "\t\t\t"
				+ cboInputConfigCameraPitch.getModel().getSelectedItem() + "\n";
		output += "#define CAMERA_YAW_INPUT_CHANNEL" + "\t\t\t" + cboInputConfigCameraYaw.getModel().getSelectedItem()
				+ "\n";
		output += "#define CAMERA_MODE_INPUT_CHANNEL" + "\t\t\t" + cboInputConfigCameraMode.getModel().getSelectedItem()
				+ "\n";
		output += "#define OSD_MODE_SWITCH_INPUT_CHANNEL" + "\t\t\t"
				+ cboInputConfigOSDModeSwitch.getModel().getSelectedItem() + "\n";
		output += "#define PASSTHROUGH_A_INPUT_CHANNEL" + "\t\t\t"
				+ cboInputConfigPassthroughA.getModel().getSelectedItem() + "\n";
		output += "#define PASSTHROUGH_B_INPUT_CHANNEL" + "\t\t\t"
				+ cboInputConfigPassthroughB.getModel().getSelectedItem() + "\n";
		output += "#define PASSTHROUGH_C_INPUT_CHANNEL" + "\t\t\t"
				+ cboInputConfigPassthroughC.getModel().getSelectedItem() + "\n";
		output += "#define PASSTHROUGH_D_INPUT_CHANNEL" + "\t\t\t"
				+ cboInputConfigPassthroughD.getModel().getSelectedItem() + "\n";
		output += "#define LOGO_A_INPUT_CHANNEL" + "\t\t\t" + cboInputConfigLOGOA.getModel().getSelectedItem() + "\n";
		output += "#define LOGO_B_INPUT_CHANNEL" + "\t\t\t" + cboInputConfigLOGOB.getModel().getSelectedItem() + "\n";
		output += "#define LOGO_C_INPUT_CHANNEL" + "\t\t\t" + cboInputConfigLOGOC.getModel().getSelectedItem() + "\n";
		output += "#define LOGO_D_INPUT_CHANNEL" + "\t\t\t" + cboInputConfigLOGOD.getModel().getSelectedItem() + "\n";
		output += "#define NUM_OUTPUTS" + "\t\t\t" + cboOutputConfigNumberOfOutputs.getModel().getSelectedItem() + "\n";
		output += "#define THROTTLE_OUTPUT_CHANNEL" + "\t\t\t" + cboOutputConfigThrottle.getModel().getSelectedItem()
				+ "\n";
		output += "#define AILERON_OUTPUT_CHANNEL" + "\t\t\t" + cboOutputConfigAileron.getModel().getSelectedItem()
				+ "\n";
		output += "#define ELEVATOR_OUTPUT_CHANNEL" + "\t\t\t" + cboOutputConfigElevator.getModel().getSelectedItem()
				+ "\n";
		output += "#define RUDDER_OUTPUT_CHANNEL" + "\t\t\t" + cboOutputConfigRudder.getModel().getSelectedItem()
				+ "\n";
		output += "#define AILERON_SECONDARY_OUTPUT_CHANNEL" + "\t\t\t"
				+ cboOutputConfigSecondaryAileron.getModel().getSelectedItem() + "\n";
		output += "#define CAMERA_PITCH_OUTPUT_CHANNEL" + "\t\t\t"
				+ cboOutputConfigCameraPitch.getModel().getSelectedItem() + "\n";
		output += "#define CAMERA_YAW_OUTPUT_CHANNEL" + "\t\t\t" + cboOutputConfigCameraYaw.getModel().getSelectedItem()
				+ "\n";
		output += "#define TRIGGER_OUTPUT_CHANNEL" + "\t\t\t"
				+ cboOutputConfigTriggerOutput.getModel().getSelectedItem() + "\n";
		output += "#define PASSTHROUGH_A_OUTPUT_CHANNEL" + "\t\t\t"
				+ cboOutputConfigPassthroughA.getModel().getSelectedItem() + "\n";
		output += "#define PASSTHROUGH_B_OUTPUT_CHANNEL" + "\t\t\t"
				+ cboOutputConfigPassthroughB.getModel().getSelectedItem() + "\n";
		output += "#define PASSTHROUGH_C_OUTPUT_CHANNEL" + "\t\t\t"
				+ cboOutputConfigPassthroughC.getModel().getSelectedItem() + "\n";
		output += "#define PASSTHROUGH_D_OUTPUT_CHANNEL" + "\t\t\t"
				+ cboOutputConfigPassthroughD.getModel().getSelectedItem() + "\n";
		output += "#define AILERON_CHANNEL_REVERSED" + "\t\t\t" + cboServoRevAileron.getModel().getSelectedItem()
				+ "\n";
		output += "#define ELEVATOR_CHANNEL_REVERSED" + "\t\t\t" + cboServoRevElevator.getModel().getSelectedItem()
				+ "\n";
		output += "#define RUDDER_CHANNEL_REVERSED" + "\t\t\t" + cboServoRevRudder.getModel().getSelectedItem() + "\n";
		output += "#define AILERON_SECONDARY_CHANNEL_REVERSED" + "\t\t\t"
				+ cboServoRevAileron.getModel().getSelectedItem() + "\n";
		output += "#define THROTTLE_CHANNEL_REVERSED" + "\t\t\t" + cboServoRevThrottle.getModel().getSelectedItem()
				+ "\n";
		output += "#define CAMERA_PITCH_CHANNEL_REVERSED" + "\t\t\t"
				+ cboServoRevCameraPitch.getModel().getSelectedItem() + "\n";
		output += "#define CAMERA_YAW_CHANNEL_REVERSED" + "\t\t\t" + cboServoRevCameraYaw.getModel().getSelectedItem()
				+ "\n";
		output += "#define ELEVON_VTAIL_SURFACES_REVERSED" + "\t\t\t" + chbxServoRevElevons.isSelected() + "\n";
		output += "#define MODE_SWITCH_THRESHOLD_LOW" + "\t\t\t" + dplModeSwitchThresLOW.getLcdValue() + "\n";
		output += "#define MODE_SWITCH_THRESHOLD_HIGH" + "\t\t\t" + dplModeSwitchThresHIGH.getLcdValue() + "\n";
		output += "#define MODE_SWITCH_TWO_POSITION" + "\t\t\t" + chbxModeSwitch2PositionSwitch.isSelected() + "\n";
		output += "#define FAILSAFE_INPUT_CHANNEL" + "\t\t\t" + cboFailsafeInputChannel.getModel().getSelectedItem()
				+ "\n";
		output += "#define FAILSAFE_INPUT_MIN" + "\t\t\t" + sldFailsafeInputChannelLOW.getValue() + "\n";
		output += "#define FAILSAFE_INPUT_MAX" + "\t\t\t" + sldFailsafeInputChannelHIGH.getValue() + "\n";
		output += "#define FAILSAFE_TYPE" + "\t\t\t" + cboFailsafeType.getModel().getSelectedItem() + "\n";
		output += "#define FAILSAFE_HOLD" + "\t\t\t" + chbxFailasfeHold.isSelected() + "\n";
		output += "#define SERIAL_OUTPUT_FORMAT" + "\t\t\t" + cboMavSerialOutputFormat.getModel().getSelectedItem()
				+ "\n";
		output += "#define MAVLINK_SYSID" + "\t\t\t" + sldMavSYSID.getValue() + "\n";
		output += "#define USE_OSD" + "\t\t\t" + chckbxUseOsd.isSelected() + "\n";
		output += "#define NUM_ANALOG_INPUTS" + "\t\t\t" + sldMavSYSID.getValue() + "\n";
		output += "#define ANALOG_CURRENT_INPUT_CHANNEL" + "\t\t\t"
				+ cboAnalogCurrentInputChannel.getModel().getSelectedItem() + "\n";
		output += "#define ANALOG_VOLTAGE_INPUT_CHANNEL" + "\t\t\t"
				+ cboAnalogVoltageInputChannel.getModel().getSelectedItem() + "\n";
		output += "#define ANALOG_RSSI_INPUT_CHANNEL" + "\t\t\t"
				+ cboAnalogRSSIInputChannel.getModel().getSelectedItem() + "\n";
		output += "#define RSSI_MIN_SIGNAL_VOLTAGE" + "\t\t\t" + dlsRSSIMinVoltage.getLcdValue() + "\n";
		output += "#define RSSI_MAX_SIGNAL_VOLTAGE" + "\t\t\t" + dlsRSSIMaxVoltage.getLcdValue() + "\n";
		output += "#define TRIGGER_TYPE" + "\t\t\t" + cboTriggerType.getModel().getSelectedItem() + "\n";
		output += "#define TRIGGER_ACTION" + "\t\t\t" + cboTriggerAction.getModel().getSelectedItem() + "\n";
		output += "#define TRIGGER_SERVO_LOW" + "\t\t\t" + dslTRIGGER_SERVO_LOW.getLcdValue() + "\n";
		output += "#define TRIGGER_SERVO_HIGH" + "\t\t\t" + dslTRIGGER_SERVO_HIGH.getLcdValue() + "\n";
		output += "#define TRIGGER_PULSE_DURATION" + "\t\t\t" + dslTRIGGER_PULSE_DURATION.getLcdValue() + "\n";
		output += "#define TRIGGER_REPEAT_PERIOD" + "\t\t\t" + dslTRIGGER_REPEAT_PERIOD.getLcdValue() + "\n";
		output += "#define SERVOSAT" + "\t\t\t" + txtSERVOSAT.getText() + "\n";
		output += "#define ROLLKP" + "\t\t\t" + txtROLLKP.getText() + "\n";
		output += "#define ROLLKD" + "\t\t\t" + txtROLLKD.getText() + "\n";
		output += "#define YAWKP_AILERON" + "\t\t\t" + txtYAWKP_AILERON.getText() + "\n";
		output += "#define YAWKD_AILERON" + "\t\t\t" + txtYAWKD_AILERON.getText() + "\n";
		output += "#define AILERON_BOOST" + "\t\t\t" + txtAILERON_BOOST.getText() + "\n";
		output += "#define PITCHGAIN" + "\t\t\t" + txtPITCHGAIN.getText() + "\n";
		output += "#define PITCHKD" + "\t\t\t" + txtPITCHKD.getText() + "\n";
		output += "#define RUDDER_ELEV_MIX" + "\t\t\t" + txtRUDDER_ELEV_MIX.getText() + "\n";
		output += "#define ROLL_ELEV_MIX" + "\t\t\t" + txtROLL_ELEV_MIX.getText() + "\n";
		output += "#define ELEVATOR_BOOST" + "\t\t\t" + txtELEVATOR_BOOST.getText() + "\n";
		output += "#define INVERTED_NEUTRAL_PITCH" + "\t\t\t" + txtINVERTED_NEUTRAL_PITCH.getText() + "\n";
		output += "#define YAWKP_RUDDER" + "\t\t\t" + txtYAWKP_RUDDER.getText() + "\n";
		output += "#define YAWKD_RUDDER" + "\t\t\t" + txtYAWKD_RUDDER.getText() + "\n";
		output += "#define ROLLKP_RUDDER" + "\t\t\t" + txtROLLKP_RUDDER.getText() + "\n";
		output += "#define ROLLKD_RUDDER" + "\t\t\t" + txtROLLKD_RUDDER.getText() + "\n";
		output += "#define MANUAL_AILERON_RUDDER_MIX" + "\t\t\t" + txtMANUAL_AILERON_RUDDER_MIX.getText() + "\n";
		output += "#define RUDDER_BOOST" + "\t\t\t" + txtRUDDER_BOOST.getText() + "\n";
		output += "#define HOVER_ROLLKP" + "\t\t\t" + txtHOVER_ROLLKP.getText() + "\n";
		output += "#define HOVER_ROLLKD" + "\t\t\t" + txtHOVER_ROLLKD.getText() + "\n";
		output += "#define HOVER_PITCHGAIN" + "\t\t\t" + txtHOVER_PITCHGAIN.getText() + "\n";
		output += "#define HOVER_PITCHKD" + "\t\t\t" + txtHOVER_PITCHKD.getText() + "\n";
		output += "#define HOVER_PITCH_OFFSET" + "\t\t\t" + txtHOVER_PITCH_OFFSET.getText() + "\n";
		output += "#define HOVER_YAWKP" + "\t\t\t" + txtHOVER_YAWKP.getText() + "\n";
		output += "#define HOVER_YAWKD" + "\t\t\t" + txtHOVER_YAWKD.getText() + "\n";
		output += "#define HOVER_YAW_OFFSET" + "\t\t\t" + txtHOVER_YAW_OFFSET.getText() + "\n";
		output += "#define HOVER_PITCH_TOWARDS_WP" + "\t\t\t" + txtHOVER_PITCH_TOWARDS_WP.getText() + "\n";
		output += "#define HOVER_NAV_MAX_PITCH_RADIUS" + "\t\t\t" + txtHOVER_NAV_MAX_PITCH_RADIUS.getText() + "\n";
		output += "#define CAMERA_MODE_THRESHOLD_LOW" + "\t\t\t" + dplCameraModeThresholdLOW.getLcdValue() + "\n";
		output += "#define CAMERA_MODE_THRESHOLD_HIGH" + "\t\t\t" + dplCameraModeThresholdHIGH.getLcdValue() + "\n";
		output += "#define CAM_TAN_PITCH_IN_STABILIZED_MODE" + "\t\t\t" + txtCAM_TAN_PITCH_IN_STABILIZED_MODE.getText()
				+ "\n";
		output += "#define CAM_YAW_IN_STABILIZED_MODE" + "\t\t\t" + txtCAM_YAW_IN_STABILIZED_MODE.getText() + "\n";
		output += "#define CAM_PITCH_SERVO_THROW" + "\t\t\t" + txtCAM_PITCH_SERVO_THROW.getText() + "\n";
		output += "#define CAM_PITCH_SERVO_MAX" + "\t\t\t" + txtCAM_PITCH_SERVO_MAX.getText() + "\n";
		output += "#define CAM_PITCH_SERVO_MIN" + "\t\t\t" + txtCAM_PITCH_SERVO_MIN.getText() + "\n";
		output += "#define CAM_PITCH_OFFSET_CENTRED" + "\t\t\t" + txtCAM_PITCH_OFFSET_CENTRED.getText() + "\n";
		output += "#define CAM_YAW_SERVO_THROW" + "\t\t\t" + txtCAM_YAW_SERVO_THROW.getText() + "\n";
		output += "#define CAM_YAW_SERVO_MAX" + "\t\t\t" + txtCAM_YAW_SERVO_MAX.getText() + "\n";
		output += "#define CAM_YAW_SERVO_MIN" + "\t\t\t" + txtCAM_YAW_SERVO_MIN.getText() + "\n";
		output += "#define CAM_YAW_OFFSET_CENTRED" + "\t\t\t" + txtCAM_YAW_OFFSET_CENTRED.getText() + "\n";
		output += "#define CAM_TESTING_OVERIDE" + "\t\t\t" + txtCAM_TESTING_OVERIDE.getText() + "\n";
		output += "#define CAM_TESTING_YAW_ANGLE" + "\t\t\t" + txtCAM_TESTING_YAW_ANGLE.getText() + "\n";
		output += "#define CAM_TESTING_PITCH_ANGLE" + "\t\t\t" + txtCAM_TESTING_PITCH_ANGLE.getText() + "\n";
		output += "#define CAM_USE_EXTERNAL_TARGET_DATA" + "\t\t\t" + chckbxCamuseexternaltargetdata.isSelected()
				+ "\n";
		output += "#define HEIGHT_TARGET_MIN" + "\t\t\t" + txtHEIGHT_TARGET_MIN.getText() + "\n";
		output += "#define HEIGHT_TARGET_MAX" + "\t\t\t" + txtHEIGHT_TARGET_MAX.getText() + "\n";
		output += "#define HEIGHT_MARGIN" + "\t\t\t" + txtHEIGHT_MARGIN.getText() + "\n";
		output += "#define ALT_HOLD_THROTTLE_MIN" + "\t\t\t" + txtALT_HOLD_THROTTLE_MIN.getText() + "\n";
		output += "#define ALT_HOLD_THROTTLE_MAX" + "\t\t\t" + txtALT_HOLD_THROTTLE_MAX.getText() + "\n";
		output += "#define ALT_HOLD_PITCH_MIN" + "\t\t\t" + txtALT_HOLD_PITCH_MIN.getText() + "\n";
		output += "#define ALT_HOLD_PITCH_MAX" + "\t\t\t" + txtALT_HOLD_PITCH_MAX.getText() + "\n";
		output += "#define ALT_HOLD_PITCH_HIGH" + "\t\t\t" + txtALT_HOLD_PITCH_HIGH.getText() + "\n";
		output += "#define RTL_PITCH_DOWN" + "\t\t\t" + txtRTL_PITCH_DOWN.getText() + "\n";
		output += "#define  HILSIM" + "\t\t\t" + cboHILSIM_BAUD.getModel().getSelectedItem() + "\n";
		output += "#define HILSIM_BAUD" + "\t\t\t" + chckbxHilsimEnable.isSelected() + "\n";
		output += "#define FLIGHT_PLAN_TYPE" + "\t\t\t" + cboFLIGHT_PLAN_TYPE.getModel().getSelectedItem() + "\n";
		output += "#define RECORD_FREE_STACK_SPACE" + "\t\t\t" + chckbxRecordfreestackspace.isSelected() + "\n";
		output += "#define ID_VEHICLE_MODEL_NAME" + "\t\t\t" + txtVEHICLE_MODEL_NAME.getText() + "\n";
		output += "#define ID_VEHICLE_REGISTRATION" + "\t\t\t" + txtVEHICLE_REGISTRATION.getText() + "\n";
		output += "#define ID_LEAD_PILOT" + "\t\t\t" + txtLEAD_PILOT.getText() + "\n";
		output += "#define ID_LEAD_PILOT" + "\t\t\t" + txtLEAD_PILOT.getText() + "\n";
		if (chckbxEnableVtol.isSelected())
			output += "#define INITIALIZE_VERTICAL";
		else
			output += "// #define INITIALIZE_VERTICAL";
		output += "\n";
		try {
			this.makeBackup();
			new FileOutputStream(new File(Resources.getResources().getUDBOptionsFile()))
					.write(output.replaceAll("false", "0").replaceAll("true", "1").getBytes());
			JOptionPane.showMessageDialog(this,
					"File saved! A timestamped backup has been created in the MatrixPilot directory", "UDB4-Tool",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "UDB4-Tool", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void makeBackup() throws Exception {
		if (!this.canMakeBackup())
			return;
		File backup = new File(Resources.getResources().getProjectDirectory() + "options_backup");
		if (!backup.exists())
			backup.mkdir();
		backup = null;
		SimpleDateFormat fileNameFormat = new SimpleDateFormat();
		fileNameFormat.applyPattern("yyyy_MM_dd\'T\'HH_mm_ss.SSSZ");
		String fileName = fileNameFormat.format(new Date()) + "options.h";
		String buf = null;
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File(Resources.getResources().getUDBOptionsFile()))));
		PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(
						new File(Resources.getResources().getProjectDirectory() + "options_backup/" + fileName))),
				true);
		while ((buf = reader.readLine()) != null) {
			writer.println(buf);
		}
		// Eeeh, murogoti wa giko, tafadhali ruta wira!
		writer.close();
		writer = null;
		reader.close();
		reader = null;
		fileName = null;
		fileNameFormat = null;
	}

	protected boolean canMakeBackup() {
		boolean makeBackup = false;
		if (chckbxMakeBackup.isSelected()) {
			if (chckbxMakeBackupBefore.isSelected())
				makeBackup = true;
			else if (Integer.parseInt(Resources.getResources().getBackupCount()) <= 0) {
				makeBackup = true;
			} else if (Integer.parseInt(Resources.getResources().getBackupCount()) > 0)
				makeBackup = false;
		}
		return makeBackup;
	}

	static private SBSMonitor sbsMonitor = null;

	@Override
	public void sbsTrackingSelectionEvent(boolean isSelected) {
		System.out.println("Wewe: " + isSelected);
		try {
			if (isSelected) {
				sbsMonitor = new SBSMonitor();
				sbsMonitor.startSBSMonitor();
				
			} else {
				sbsMonitor.stopSBSMonitor();
				Thread.sleep(500);
				sbsMonitor = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class UdbMcpFile {
	private String device = null, toolSettings = "-x c -c", includeDirectory = null,
			toolTrailer = "-g -Wall -mlarge-code -legacy-libc", buildDirectory = null, libDirectory = null,
			libDsp = null;

	private Vector<String> sourceFiles = new Vector();

	protected String getDevice() {
		return device;
	}

	protected String getToolTrailer() {
		return toolTrailer;
	}

	protected String getToolSettings() {
		return toolSettings;
	}

	protected String getIncludeDirectory() {
		return includeDirectory;
	}

	protected String getLibDirectory() {
		return libDirectory;
	}

	protected Iterator<String> getSourceFiles() {
		return sourceFiles.iterator();
	}

	protected String getBuildDirectory() {
		return buildDirectory;
	}

	protected String getLibDsp() {
		return libDsp;
	}

	protected UdbMcpFile(String mcpFile) {
		String buf = null;
		boolean isFileInfo = false;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(mcpFile)));
			while ((buf = reader.readLine()) != null) {
				if (buf.contains("FILE_INFO")) {
					isFileInfo = true;
					continue;
				}
				if (isFileInfo) {
					if (buf.contains("]")) {
						isFileInfo = false;
						continue;
					}
					if (buf.contains("file_")) {
						if (buf.contains("libdsp-coff")) {
							libDsp = buf.substring(buf.indexOf("=") + 1);
							continue;
						}
						sourceFiles.add(buf.substring(buf.indexOf("=") + 1));
						continue;
					}
					continue;
				}
				if (buf.contains("device"))
					device = buf.substring(12);
				if (buf.contains("dir_inc"))
					includeDirectory = "-I\"" + buf.substring(8) + "\" -I\".\"";
				if (buf.contains("dir_tmp"))
					buildDirectory = buf.substring(8);
				if (buf.contains("dir_lib"))
					libDirectory = buf.substring(8);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
