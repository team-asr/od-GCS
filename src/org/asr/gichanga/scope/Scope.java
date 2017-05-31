package org.asr.gichanga.scope;

import java.awt.*;
import javax.swing.*;

public class Scope extends JPanel {
	double f(double x) {
        return (Math.cos(x / 5) + Math.sin(x / 7) + 2) * getSize().height / 4;
    }

    @Override
    public void paint(Graphics g) {
    	//g.drawLine(arg0, arg1, arg2, arg3);
        for (int x = 0; x < getSize().width; x++) {
            g.drawLine(x, (int) f(x), x + 1, (int) f(x + 1));
        }
    }

    static public void main(String args[]){
    	JFrame frame = new JFrame();
    	frame.setSize(200,200);
    	frame.getContentPane().add(new Scope());
    	frame.setVisible(true);
    }
}
