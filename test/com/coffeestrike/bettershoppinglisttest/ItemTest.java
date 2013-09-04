package com.coffeestrike.bettershoppinglisttest;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.coffeestrike.bettershoppinglist.Item;


@RunWith(RobolectricTestRunner.class)
public class ItemTest {
	Item mItem;
	
	@Before
	public void setUp(){
		mItem = new Item("");
	}
	
	@Test
	public void testConstructors(){
		assertNotNull(mItem.getUnitOfMeasure());
	}

	@Test
	public void testToJSON() throws Exception{
		JSONObject json = mItem.toJSON();
		assertNotNull(json);
	}

	@Test
	public void testEqualsObject() throws Exception{
//		fail("Not yet implemented");
	}

}
