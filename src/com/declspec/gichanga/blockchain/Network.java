package com.declspec.gichanga.blockchain;
import java.net.*;
import java.io.*;
import java.util.*;

public class Network implements BlocksAdded {
	
	Vector<Block> networkedBlocks = new Vector();

	static private Network network = null;
	
	protected Network() {
		network = new Network();
		
	}
	
	public Network getNetwork() {
		if (network == null)
			new Network();
		return network;
	}
	
	protected void pump(Block block) {
		networkedBlocks.add(block);
		Iterator<Block> looper = networkedBlocks.iterator();
		while (looper.hasNext()) {
			looper.next()
		}
	}

	@Override
	public void blockAddedListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void blockRemovedListener() {
		// TODO Auto-generated method stub
		
	}
	

}
