package com.coffeestrike.bettershoppinglist.models;

public class UnitsOfMeasure {
	
	private static String [] sUnits = {
			"Each",
			"Dozen",
			"Gallon",
		};


	public static String[] getUnits(){
		return sUnits;
	}
	
	public static void setUnits(String[] units){
		
		if(units != null && units.length > 0){
			sUnits = units;
		}
		
	}
}
