package edu.mit.pt.maps;

import java.util.HashMap;
import java.util.Map;

import edu.mit.pt.data.PlaceType;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class PlaceFilterButton extends ToggleButton {
	private PlaceType placeType;
	private static Map<PlaceType, PlaceFilterButton> placeMap = new HashMap<PlaceType, PlaceFilterButton>();
	
	public static PlaceFilterButton getPlaceFilterButton(PlaceType placeType){
		if(placeMap.containsKey(placeType)){
			return placeMap.get(placeType);
		}
		return null;
	}
	// Note: Doesn't prevent place type from being registered multiple times.
	public static void registerPlaceType(PlaceType placeType, PlaceFilterButton placeFilterButton){
		placeFilterButton.setPlaceType(placeType);
		placeMap.put(placeType, placeFilterButton);
	}
	public PlaceFilterButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public PlaceFilterButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public PlaceFilterButton(Context context) {
		super(context);
	}
	public PlaceType getPlaceType(){
		return placeType;
	}
	public void setPlaceType(PlaceType placeType){
		this.placeType = placeType;
	}

}
