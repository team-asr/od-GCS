package com.declspec.gichanga;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import eu.hansolo.steelseries.gauges.Compass;
import eu.hansolo.steelseries.gauges.Altimeter;
import eu.hansolo.steelseries.gauges.DisplayMulti;
import eu.hansolo.steelseries.gauges.DigitalRadial;
import eu.hansolo.steelseries.gauges.Level;

public class ImuVisualization extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImuVisualization frame = new ImuVisualization();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ImuVisualization() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 619, 367);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Compass compass = new Compass();
		compass.setBounds(33, 11, 200, 200);
		contentPane.add(compass);
		
		Altimeter altimeter = new Altimeter();
		altimeter.setBounds(267, 11, 200, 200);
		contentPane.add(altimeter);
		
		DisplayMulti displayMulti = new DisplayMulti();
		displayMulti.setBounds(33, 240, 128, 64);
		contentPane.add(displayMulti);
		
		DigitalRadial digitalRadial = new DigitalRadial();
		digitalRadial.setBounds(421, 48, 200, 200);
		contentPane.add(digitalRadial);
		
		Level level = new Level();
		level.setBounds(140, 157, 200, 200);
		contentPane.add(level);
	}
}
