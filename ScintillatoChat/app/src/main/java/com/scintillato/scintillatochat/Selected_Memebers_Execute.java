package com.scintillato.scintillatochat;



import com.scintillato.scintillatochat.selected_members_temp.SelectedMembersTemp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract.Columns;
import android.util.Log;

public class Selected_Memebers_Execute extends SQLiteOpenHelper {

	public static final int database_version=2;
	public String CREATE_QUERY_MEMBERS_TEMP="CREATE TABLE IF NOT EXISTS "+SelectedMembersTemp.TABLE_NAME+"("+SelectedMembersTemp.NAME+" TEXT,"+SelectedMembersTemp.NUMBER+" TEXT);";//+ContactsRegisteredInfo.PROFILE_PIC+" TEXT);";

	public Selected_Memebers_Execute(Context context,String number) {
		super(context, SelectedMembersTemp.DATABASE_NAME+number, null, database_version);
		Log.d("status","Database Created"+SelectedMembersTemp.DATABASE_NAME+number);

		// TODO Auto-generated constructor stub
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	     
	     db.execSQL("DROP TABLE IF EXISTS " + SelectedMembersTemp.TABLE_NAME);

	     onCreate(db);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL(CREATE_QUERY_MEMBERS_TEMP);

		Log.d("status","Table Created"+SelectedMembersTemp.TABLE_NAME);
	}
	
	
	Cursor getinfo_selected_members_temp(Selected_Memebers_Execute obj)
	{
		SQLiteDatabase SQ=obj.getReadableDatabase();
		String[] coloumns={SelectedMembersTemp.NUMBER,SelectedMembersTemp.NAME/*,ContactsUnregisteredInfo.PROFILE_PIC*/};
		Cursor cr=SQ.query(SelectedMembersTemp.TABLE_NAME, coloumns, null, null, null, null,null);
		return cr;
	}

	public void putinfo_selected_members_temp(Selected_Memebers_Execute obj,String number,String name/*,String profile_pic */)
	{
		SQLiteDatabase SQ =obj.getWritableDatabase();
		ContentValues cv= new ContentValues();
		cv.put(SelectedMembersTemp.NUMBER, number);
		cv.put(SelectedMembersTemp.NAME, name);
		
	//	cv.put(ContactsUnregisteredInfo.PROFILE_PIC, profile_pic);
		long k=SQ.insert(SelectedMembersTemp.TABLE_NAME,null,cv);
		Log.d("statis","one row inserted"+SelectedMembersTemp.TABLE_NAME);
	}
	public void delete_selected_members_temp()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from "+ SelectedMembersTemp.TABLE_NAME);
		Log.d("database", "deleted "+SelectedMembersTemp.TABLE_NAME);
	}
	public boolean delete_selected_members_temp_row(String number) 
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int x;
		
		x=db.delete(SelectedMembersTemp.TABLE_NAME,SelectedMembersTemp.NUMBER+"=?",new String[]{number});
		return x>0;
	}
}
