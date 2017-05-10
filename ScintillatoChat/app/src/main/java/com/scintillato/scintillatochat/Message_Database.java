package com.scintillato.scintillatochat;
import android.provider.BaseColumns;

public class Message_Database {

    public Message_Database() {
        // TODO Auto-generated constructor stub
    }

    public static abstract class MessageSingleInfo implements BaseColumns
    {
        public static final String MESSAGE_ID="message_id";
        public static final String DATE_TIME="date_time";
        public static final String MESSAGE="message";
        public static final String SENDER="sender_number";
        public static final String IMAGE_LOC="image_loc";
        public static final String VIDEO_LOC="video_loc";
        public static final String STATUS="status";//if send_recieve=0, status=0,1,2(sent,delive,seen) else status=0,1(received,seen)
        public static final String RECIEVER="receiver_number";
        public static final String SEND_RECIEVE="send_recieve";// (0/1)  0-send 1-recieve
        public static final String OPPOSITE_PERSON_NUMBER="opposite_person_number";
        public static final String OPPOSITE_PERSON_MESSAGE_ID="opposite_person_message_id";
        public static final String DATABASE_NAME="chat";
        public static final String TABLE_NAME="message_single";
    }

    public static abstract class MessageGroupInfo implements BaseColumns
    {
        public static final String MESSAGE_ID="message_id";
        public static final String DATE_TIME="date_time";
        public static final String GROUP_ID="group_id";
        public static final String MESSAGE="message";
        public static final String SENDER="sender_number";
        public static final String IMAGE_LOC="image_loc";
        public static final String VIDEO_LOC="video_loc";
        public static final String STATUS="status";//if send_recieve=0, status=0,1,2(sent,delive,seen) else status=0,1(received,seen)
        public static final String SEND_RECIEVE="send_recieve";// (0/1)  0-send 1-recieve
        public static final String _MEMBER="_member";
        public static final String NEW_NAME="new_name";
        public static final String ADD="add_member";
        public static final String REMOVE="remove";
        public static final String LEFT="left";
        public static final String ICON_CHANGE="icon_change";
        public static final String NAME_CHANGE="name_change";
        public static final String DATABASE_NAME="chat";
        public static final String TABLE_NAME="message_group";

    }


}

