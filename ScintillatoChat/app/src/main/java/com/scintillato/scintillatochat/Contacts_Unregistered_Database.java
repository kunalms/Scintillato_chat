package com.scintillato.scintillatochat;
import android.provider.BaseColumns;

public class Contacts_Unregistered_Database {
	
		public Contacts_Unregistered_Database() {
			// TODO Auto-generated constructor stub
		}
		
		public static abstract class ContactsUnregisteredInfo implements BaseColumns
		{
			public static final String NAME="name";
			public static final String PROFILE_PIC="profile_pic";
			public static final String NUMBER="phone_number";
			public static final String DATABASE_NAME="Chat";
			public static final String TABLE_NAME="contacts_unregistered";
		}
		
	}

