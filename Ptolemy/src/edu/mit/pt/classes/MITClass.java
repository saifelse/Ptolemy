package edu.mit.pt.classes;

import java.util.List;

import edu.mit.pt.data.Place;

public class MITClass {
	private String id;
	private String term;
	private String name;
	private Place place;

	private MITClass(String id, String term, String name, Place place) {
		this.id = id;
		this.term = term;
		this.name = name;
		this.place = place;
	}
	
	public String getId() {
		return id;
	}

	public String getTerm() {
		return term;
	}

	public String getName() {
		return name;
	}

	public Place getPlace() {
		return place;
	}

	private static boolean addOrReplaceClass(MITClass mitClass) {
		return false;
	}

	private static boolean removeClass(String id) {
		return false;
	}

	public static MITClass getClass(String search) {
		return null;
	}

	public static List<MITClass> getClasses(String search) {
		return null;
	}
}