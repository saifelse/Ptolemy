package edu.mit.pt.data;

import java.util.HashMap;

import edu.mit.pt.classes.MITClassTable;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class PlacesContentProvider extends ContentProvider {
	private PtolemyOpenHelper db;
	private static final String AUTHORITY = "edu.mit.pt.data.placescontentprovider";
	private static final HashMap<String, String> columnMap = PlacesTable
			.buildColumnMap();
	private static final HashMap<String, String> classesColumnMap = MITClassTable
			.buildColumnMap();

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
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLiteDatabase database = db.getWritableDatabase();
		database.beginTransaction();
		for (ContentValues v : values) {
			database.insert(PlacesTable.PLACES_TABLE_NAME, null, v);
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		return values.length;
	}

	@Override
	public boolean onCreate() {
		db = PtolemyDBOpenHelperSingleton.getPtolemyDBOpenHelper(getContext());
		return false;
	}

	private Cursor queryClasses(String query, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MITClassTable.CLASSES_TABLE_NAME);

		queryBuilder.setProjectionMap(classesColumnMap);
		// queryBuilder.appendWhere(PlacesTable.PLACES_TABLE_NAME + " MATCH '" +
		// query + "'");
		queryBuilder.appendWhere(MITClassTable.COLUMN_MITID + " LIKE '" + query
				+ "%'");

		SQLiteDatabase database = db.getReadableDatabase();
		Cursor cursor = queryBuilder.query(database, projection, selection,
				selectionArgs, null, null, sortOrder);
		return cursor;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		// checkColumns(projection);

		queryBuilder.setTables(PlacesTable.PLACES_TABLE_NAME);

		String query = uri.getLastPathSegment();
		queryBuilder.setProjectionMap(columnMap);
		// queryBuilder.appendWhere(PlacesTable.PLACES_TABLE_NAME + " MATCH '" +
		// query + "'");
		queryBuilder.appendWhere(PlacesTable.COLUMN_NAME + " LIKE '" + query
				+ "%'");

		SQLiteDatabase database = db.getReadableDatabase();
		Cursor cursor = queryBuilder.query(database, projection, selection,
				selectionArgs, null, null, sortOrder);

		Cursor classesCursor = queryClasses(query, projection, selection, selectionArgs, sortOrder);
	
		Cursor[] cursorArray = new Cursor[] {cursor, classesCursor};
		MergeCursor mergedCursor = new MergeCursor(cursorArray);
		// Make sure that potential listeners are getting notified
		mergedCursor.setNotificationUri(getContext().getContentResolver(), uri);

		return mergedCursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
