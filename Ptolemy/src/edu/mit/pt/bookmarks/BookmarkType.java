package edu.mit.pt.bookmarks;

public enum BookmarkType {
	LECTURE			("Lecture", "LEC"),
	RECITATION		("Recitation", "REC"),
	OFFICE_HOURS	("Office hours", "OH"),
	OTHER			("None of the above", ">");
	
	private final String fullName;
	private final String shortName;
	
	BookmarkType(String fullName, String shortName) {
		this.fullName = fullName;
		this.shortName = shortName;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public String toString() {
		return getFullName();
	}
}