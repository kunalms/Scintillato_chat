package com.scintillato.scintillatochat;
import android.provider.BaseColumns;

public class Recent_Chats {

    public Recent_Chats() {
        // TODO Auto-generated constructor stub

    }
    public static abstract class RecentChatsInfo implements BaseColumns
    {
        public static final String GROUP_ID="group_id";
        public static final String OPPOSITE_PERSON_NUMBER="opposite_person_number";
        public static final String FLAG="flag";//0 for group and 1 for single
        public static final String LAST_UPDATED="last_updated";
        public static final String SENDER="sender";
        public static final String DATABASE_NAME="chat";
        public static final String TABLE_NAME="recent_chats";
      //  public static final String MESSAGE_UNREAD_COUNT="message_unread_count";
    }

}

