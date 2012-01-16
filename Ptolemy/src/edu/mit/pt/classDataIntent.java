package edu.mit.pt;

import java.util.List;

import android.content.Intent;

public class classDataIntent extends Intent {
	final static String CLASSES = "classes";
	public classDataIntent(List<String> classes){
		super();
		this.putExtra(CLASSES, classes.toArray(new String[]{}));
	}
}
