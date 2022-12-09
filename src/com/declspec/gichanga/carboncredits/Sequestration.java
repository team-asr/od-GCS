package com.declspec.gichanga.carboncredits;


/**
 * 
 * @author moses gichanga
 * 
 * Brainstorming ideas along sequestration: Long term monitoring of vegetative ecosystems for
 * 1. Threat Identification
 * 2. Growth of vegetation to track carbon capture
 * 3. Addition of a blockchain wallet for carbon credits for smallholder farmers
 *
 */
public abstract class Sequestration {
	public abstract void initialize();
	abstract public void setVegetationType();
	abstract protected Carbon getAreaCarbon() throws NoCarbonSetException;
	
}

class Carbon{}
class NoCarbonSetException extends Exception{

	/**
	 * SerialVersionUID for serialization
	 * @author moses gichanga
	 */
	private static final long serialVersionUID = -2727630265450827217L;
	
}
