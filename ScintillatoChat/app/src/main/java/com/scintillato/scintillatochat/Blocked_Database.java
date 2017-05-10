package com.scintillato.scintillatochat;
import android.provider.BaseColumns;

public class Blocked_Database {

		public Blocked_Database() {
			// TODO Auto-generated constructor stub
		}
		
		public static abstract class BlockedDatabase implements BaseColumns
		{
			public static final String NUMBER="phone_number";
			public static final String DATABASE_NAME="Chat";
			public static final String TABLE_NAME="blocked";
		}
	}

