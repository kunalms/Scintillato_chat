package com.scintillato.scintillatochat;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.BaseColumns;

public class Group_Database {
	
		public Group_Database() {
			// TODO Auto-generated constructor stub
		}
		
		public static abstract class GroupDatabase implements BaseColumns
		{
			public static final String GROUP_NAME="group_name";
			public static final String TOPIC="topic";
			public static final String DESCRIPTION="description";
			public static final String CREATE_DATE="create_date";
			public static final String MEMBER_COUNT="member_count";
			public static final String DATABASE_NAME="chat";
			public static final String GROUP_IMAGE="group_image";
			public static final String TABLE_NAME="groups";
			public static final String GROUP_ID="group_id";
			public static final String STATUS="status"; // may be public or private
        }
}

