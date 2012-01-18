package edu.mit.pt.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class PlacesContentProvider extends ContentProvider {
	private PlacesOpenHelper db;
	private static final String AUTHORITY = "edu.mit.pt.data.placescontentprovider";

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase database = db.getWritableDatabase();
		long id = database.insert(PlacesTable.PLACES_TABLE_NAME, null, values);
		return Uri.parse("/" + id);
	}

	@Override
	public boolean onCreate() {
		db = new PlacesOpenHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		//checkColumns(projection);
		
		queryBuilder.setTables(PlacesTable.PLACES_TABLE_NAME);
		
		String query = uri.getLastPathSegment();
		System.out.println("QUERY is " + query);
		queryBuilder.appendWhere(PlacesTable.PLACES_TABLE_NAME + " MATCH '" + query + "'");
		
		SQLiteDatabase database = db.getReadableDatabase();
		Cursor cursor = queryBuilder.query(database, projection, selection,
				selectionArgs, null, null, sortOrder);
				
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
