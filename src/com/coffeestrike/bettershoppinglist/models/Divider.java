package com.coffeestrike.bettershoppinglist.models;

import java.util.UUID;


/**
 * @author Benjamin Daschel
 * Special class to represent a divider in a list of items.
 * All instances of this class will share the same UUID.
 *
 */
public class Divider extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5783563470482249183L;

	public Divider() {
		super(null);
		mId = UUID.fromString("bc8ef74a-944c-4a61-a3aa-3f9eb7654032");
	}
	
	@Override
	public boolean isDivider(){
		return true;
	}

}
