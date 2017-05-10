package com.scintillato.scintillatochat;



import com.scintillato.scintillatochat.Contacts_Registered_Database.ContactsRegisteredInfo;
import com.scintillato.scintillatochat.Contacts_Unregistered_Database.ContactsUnregisteredInfo;
import com.scintillato.scintillatochat.Blocked_Database.BlockedDatabase;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract.Columns;
import android.util.Log;

public class Contacts_Unregistered_Execute extends SQLiteOpenHelper {

	public static final int database_version=2;
	public String CREATE_QUERY_UNREGISTERED="CREATE TABLE IF NOT EXISTS "+ContactsUnregisteredInfo.TABLE_NAME+"("+ContactsUnregisteredInfo.NAME+" TEXT,"+ContactsUnregisteredInfo.NUMBER+" TEXT UNIQUE);";
	public String CREATE_QUERY_REGISTERED="CREATE TABLE IF NOT EXISTS "+ContactsRegisteredInfo.TABLE_NAME+"("+ContactsRegisteredInfo.NAME+" TEXT,"+ContactsRegisteredInfo.NUMBER+" TEXT UNIQUE,"+ContactsRegisteredInfo.PROFILE_PIC+" TEXT);";
	public String CREATE_QUERY_BLOCKED="CREATE TABLE IF NOT EXISTS "+BlockedDatabase.TABLE_NAME+"("+BlockedDatabase.NUMBER+" TEXT UNIQUE);";
	public Contacts_Unregistered_Execute(Context context,String number) {
		super(context, ContactsUnregisteredInfo.DATABASE_NAME+number, null, database_version);

		Log.d("status","Database Created"+ContactsUnregisteredInfo.DATABASE_NAME+number);

		// TODO Auto-generated constructor stub
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	     db.execSQL("DROP TABLE IF EXISTS " + ContactsUnregisteredInfo.TABLE_NAME);
	     db.execSQL("DROP TABLE IF EXISTS " + ContactsRegisteredInfo.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + BlockedDatabase.TABLE_NAME);
	     onCreate(db);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_QUERY_UNREGISTERED);
		db.execSQL(CREATE_QUERY_REGISTERED);
		db.execSQL(CREATE_QUERY_BLOCKED);
		Log.d("status","Table Created"+ContactsUnregisteredInfo.TABLE_NAME);
	}
	
	Cursor getinfo_unreg(Contacts_Unregistered_Execute obj)
	{
		SQLiteDatabase SQ=obj.getReadableDatabase();
		String[] coloumns={ContactsUnregisteredInfo.NAME,ContactsUnregisteredInfo.NUMBER/*,ContactsUnregisteredInfo.PROFILE_PIC*/};
		Cursor cr=SQ.query(ContactsUnregisteredInfo.TABLE_NAME, coloumns, null, null, null, null,null);
		return cr;
	}

	public void putinfo_unreg(Contacts_Unregistered_Execute obj,String name,String number/*,String profile_pic */)
	{
		SQLiteDatabase SQ =obj.getWritableDatabase();
		ContentValues cv= new ContentValues();
		cv.put(ContactsUnregisteredInfo.NAME,name);
		cv.put(ContactsUnregisteredInfo.NUMBER, number);
	//	cv.put(ContactsUnregisteredInfo.PROFILE_PIC, profile_pic);
		long k=SQ.insert(ContactsUnregisteredInfo.TABLE_NAME,null,cv);
		Log.d("statis","one row inserted"+ContactsUnregisteredInfo.TABLE_NAME);
	}
	public void delete_unregistered(String number)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from "+ ContactsUnregisteredInfo.TABLE_NAME+" where "+ContactsUnregisteredInfo.NUMBER+"="+number);
		Log.d("database", "deleted "+number+ContactsUnregisteredInfo.TABLE_NAME);
	}
	public void delete_registered_number(String number)
	{
		SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ContactsRegisteredInfo.TABLE_NAME, ContactsRegisteredInfo.NUMBER+"=?", new String[]{number});
		Log.d("database", "deleted "+number+ContactsRegisteredInfo.TABLE_NAME);
	}
	public void delete_unreg()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from "+ ContactsUnregisteredInfo.TABLE_NAME);
		Log.d("database", "deleted"+ContactsUnregisteredInfo.TABLE_NAME);
	}
	Cursor getinfo_reg(Contacts_Unregistered_Execute obj)
	{
		SQLiteDatabase SQ=obj.getReadableDatabase();
		String[] coloumns={ContactsRegisteredInfo.NAME,ContactsRegisteredInfo.NUMBER,ContactsRegisteredInfo.PROFILE_PIC};
		Cursor cr=SQ.query(ContactsRegisteredInfo.TABLE_NAME, coloumns, null, null, null, null,null);
		return cr;
	}

	public String putinfo_reg(Contacts_Unregistered_Execute obj,String name,String number/*,String profile_pic */)
	{
		SQLiteDatabase SQ =obj.getWritableDatabase();
		ContentValues cv= new ContentValues();
		cv.put(ContactsRegisteredInfo.NAME,name);
		cv.put(ContactsRegisteredInfo.NUMBER, number);
	//	cv.put(ContactsRegisteredInfo.PROFILE_PIC, profile_pic);
		long k=SQ.insert(ContactsRegisteredInfo.TABLE_NAME,null,cv);
		Log.d("statis","one row inserted"+ContactsRegisteredInfo.TABLE_NAME);
		return k+"";
	}
    void update_number_registered(Contacts_Unregistered_Execute obj,String number,String name)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ContactsRegisteredInfo.NAME,name);
        SQ.update(ContactsRegisteredInfo.TABLE_NAME, cv,ContactsRegisteredInfo.NUMBER+"=?", new String[]{number});
    }
	void update_profile_pic_registered(Contacts_Unregistered_Execute obj,String image,String number)
	{
		SQLiteDatabase SQ =obj.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(ContactsRegisteredInfo.PROFILE_PIC,image);
		SQ.update(ContactsRegisteredInfo.TABLE_NAME, cv,ContactsRegisteredInfo.NUMBER+"=?", new String[]{number});
	}
	public void delete_reg()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from "+ ContactsRegisteredInfo.TABLE_NAME);
		Log.d("database", "deleted "+ContactsRegisteredInfo.TABLE_NAME);

	}

	String number_exists(Contacts_Unregistered_Execute obj,String number)
	{
		SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={ContactsRegisteredInfo.NAME};
		     Cursor cr=SQ.query(ContactsRegisteredInfo.TABLE_NAME, coloumns, ContactsRegisteredInfo.NUMBER+"=?",new String[]{number}, null, null, null,null);
		Log.d("useruser_num",number);

		if(cr.getCount()>0)
		{
			cr.moveToFirst();
			return "1";
		}
		else
		{
			Log.d("useruser_id","here");
			return "0";
		}
	}
    String get_name_message_table(Contacts_Unregistered_Execute obj,String number)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={ContactsRegisteredInfo.NAME};
		Cursor cr=SQ.query(ContactsRegisteredInfo.TABLE_NAME, coloumns, ContactsRegisteredInfo.NUMBER+"=?",new String[]{number}, null, null, null,"1");
		if(cr.getCount()>0)
		{
			cr.moveToFirst();
			do {
				return cr.getString(0);
			}while (cr.moveToFirst());
		}
		return number;
    }
    Cursor getinfo_blocked(Contacts_Unregistered_Execute obj)
    {
        SQLiteDatabase SQ=obj.getReadableDatabase();
        String[] coloumns={BlockedDatabase.NUMBER};
        Cursor cr=SQ.query(BlockedDatabase.TABLE_NAME, coloumns, null, null, null, null,null);
        return cr;
    }

    public void putinfo_blocked(Contacts_Unregistered_Execute obj,String number/*,String profile_pic */)
    {
        SQLiteDatabase SQ =obj.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(BlockedDatabase.NUMBER,number);
        long k=SQ.insert(BlockedDatabase.TABLE_NAME,null,cv);
        Log.d("statis","one row inserted"+BlockedDatabase.TABLE_NAME);
    }
    public void delete_blocked(String number)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ BlockedDatabase.TABLE_NAME+" where "+BlockedDatabase.NUMBER+"="+number);
        Log.d("database", "deleted "+number+BlockedDatabase.TABLE_NAME);
    }



}
