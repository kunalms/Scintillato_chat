package com.scintillato.scintillatochat;
import android.provider.BaseColumns;

public class Group_Member_Database {
	
		public Group_Member_Database() {
			// TODO Auto-generated constructor stub
		}

		public static abstract class GroupMemberDatabase implements BaseColumns
		{
			public static final String GROUP_ID="group_id";
			//public static final String NAME="name";
			public static final String NUMBER="number";
			public static final String ADMIN="admin";
            public static final String RANK="rank";
			public static final String ENTER_DATE="enter_date";
			public static final String PROFILE_PIC="profile_pic";
			public static final String DATABASE_NAME="chat";
			public static final String TABLE_NAME="group_members";
		}
	}

