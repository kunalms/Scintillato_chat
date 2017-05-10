package com.scintillato.scintillatochat;
import android.provider.BaseColumns;

public class Message_Database_Unsend {

    public Message_Database_Unsend() {
        // TODO Auto-generated constructor stub
    }
    public static abstract class MessageUnsendSingleInfo implements BaseColumns
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
        public static final String DATABASE_NAME="chat";
        public static final String TABLE_NAME="message_unsend_single";
    }

    public static abstract class MessageUnsendGroupInfo implements BaseColumns
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
        public static final String DATABASE_NAME="chat";
        public static final String TABLE_NAME="message_unsend_group";
    }
}

