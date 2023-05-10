package com.safra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.safra.R;
import com.safra.models.NotificationItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.safra.utilities.Common.DATE_FORMAT;

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.NotificationViewHolder> {

    private final Context context;
    private final List<NotificationItem> notificationList;
    private final OnItemClickListener listener;

    private final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public interface OnItemClickListener{
        void onClick(NotificationItem item, int position);
    }

    public NotificationRecyclerAdapter(Context context, List<NotificationItem> notificationList,
                                       OnItemClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bindView(notificationList.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder{
        ImageView notificationImage;
        TextView notificationTitle, notificationDate;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationImage = itemView.findViewById(R.id.ivNotificationImage);
            notificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            notificationDate = itemView.findViewById(R.id.tvDate);
        }

        public void bindView(NotificationItem item) {
            notificationTitle.setText(item.getNotificationTitle());

            switch ((int) item.getModuleId()){
                case 2:
                    Glide.with(context).load(R.drawable.ic_forms).into(notificationImage);
                    break;
                case 3:
                    Glide.with(context).load(R.drawable.ic_tasks).into(notificationImage);
                    break;
            }

            notificationDate.setText(sdf.format(new Date(item.getNotificationDate())));

            itemView.setOnClickListener(v -> listener.onClick(item, getAbsoluteAdapterPosition()));
        }
    }

}
