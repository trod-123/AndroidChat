package com.thirdarm.chat.ui;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thirdarm.chat.MmsSms.MmsSmsHelper;
import com.thirdarm.chat.R;
import com.thirdarm.chat.utils.Utils;

/**
 * Created by TROD on 20180310.
 */

public class MessagingAdapter extends RecyclerView.Adapter<MessagingAdapter.MessagingVH> {

    private Context mContext;
    private Cursor mCursor;

    public static final String SMS_TAG = "sms";
    public static final String MMS_TAG = "mms";

    public MessagingAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MessagingVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.message_item, parent, false);
        return new MessagingVH(view);
    }

    @Override
    public void onBindViewHolder(MessagingVH holder, int position) {
        if (mCursor.moveToPosition(position)) {
            String messageType =
                    mCursor.getString(mCursor.getColumnIndex("m_type"));
            if ("application/vnd.wap.multipart.related".equals(messageType)) {
                // message is MMS
                bindMMSView(holder, position);
            } else {
                // message is SMS
                bindSMSView(holder, position);
            }
        }
    }

    // TODO: Set MMS content
    private void bindMMSView(MessagingVH holder, int position) {
        String senderName = "WAAHAHAHAHA";
        String time =
                Utils.convertMillisToReadableDateTime(mCursor.getLong(mCursor.getColumnIndex("normalized_date")));
        String body = "WAHAHAHAHA";

        holder.bindMms(senderName, time, body, -1);
    }

    // TODO: Set HTML compat for clicking hyperlinks
    private void bindSMSView(MessagingVH holder, int position) {
        String senderName =
                MmsSmsHelper.getReadableAddressString(mContext,
                        new String[]{mCursor.getString(mCursor.getColumnIndex("address"))},
                        holder, true);
        String time =
                Utils.convertMillisToReadableDateTime(
                        mCursor.getLong(mCursor.getColumnIndex("normalized_date")));
        String body =
                mCursor.getString(mCursor.getColumnIndex("body"));

        int type =
                mCursor.getInt(mCursor.getColumnIndex("type"));

        // if the message was in the outbox, then the user is the sender
        if (type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX ||
                type == Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT) {
            senderName = "You";
        }

        holder.bindSms(senderName, time, body);
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public class MessagingVH extends RecyclerView.ViewHolder
            implements MmsSmsHelper.ReadableAddressCallback {

        private TextView tv_name;
        private TextView tv_time;
        private TextView tv_body;
        private ImageView iv_image;

        public MessagingVH(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.message_item_sender_name_textview);
            tv_time = (TextView) itemView.findViewById(R.id.message_item_time_textview);
            tv_body = (TextView) itemView.findViewById(R.id.message_item_body_textview);
            iv_image = (ImageView) itemView.findViewById(R.id.message_item_image);
        }

        public void bindSms(String senderName, String time, String body) {
            tv_name.setText(senderName);
            tv_time.setText(time);
            tv_body.setText(body);

            itemView.setTag(SMS_TAG);
        }

        public void bindMms(String senderName, String time, String body, int imageId) {
            tv_name.setText(senderName);
            tv_time.setText(time);
            tv_body.setText(body);

            //iv_image.setImageResource(imageId);

            itemView.setTag(MMS_TAG);
        }

        @Override
        public void returnReadableAddress(String result) {
            tv_name.setText(result);
        }
    }
}