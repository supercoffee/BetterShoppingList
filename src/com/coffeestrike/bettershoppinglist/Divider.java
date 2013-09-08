package com.coffeestrike.bettershoppinglist;

public class Divider extends Item {

	public Divider(String description) {
		super(description);
	}
	
	@Override
	public boolean isDivider(){
		return true;
	}

}
