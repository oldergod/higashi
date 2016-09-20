package com.oldering.kintone.higashi.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.oldering.kintone.higashi.HigashiApplication;
import com.oldering.kintone.higashi.R;
import com.oldering.kintone.higashi.api.kintone.KintoneApi;
import com.oldering.kintone.higashi.model.Account;
import com.oldering.kintone.higashi.model.ApiResponse;
import com.oldering.kintone.higashi.model.NotificationM;
import com.oldering.kintone.higashi.ui.activity.NotificationDetailActivity;
import com.oldering.kintone.higashi.util.AccountStore;
import com.oldering.kintone.higashi.util.DateUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import okhttp3.OkHttpClient;
import rx.SingleSubscriber;

import static com.oldering.kintone.higashi.R.id.notificationActionFlag;
import static com.oldering.kintone.higashi.R.id.notificationActionLike;
import static com.oldering.kintone.higashi.R.id.notificationActionMark;
import static com.oldering.kintone.higashi.R.id.notificationActionReply;
import static com.oldering.kintone.higashi.R.id.notificationActionShare;
import static com.oldering.kintone.higashi.R.id.notificationBody;
import static com.oldering.kintone.higashi.R.id.notificationPublisherIcon;
import static com.oldering.kintone.higashi.R.id.notificationPublisherName;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationM> notifications;
    private static final String TAG = "NotificationAdapter";
    Context context;
    private Picasso picasso;
    private Transformation transformation = new RoundedTransformationBuilder()
            .borderColor(Color.WHITE)
            .borderWidthDp(2)
            .cornerRadiusDp(4)
            .oval(false)
            .build();

    public NotificationAdapter(Context context, List<NotificationM> notifications) {
        this.context = context;
        this.notifications = notifications;
        OkHttpClient okHttpClient = HigashiApplication.getInstance().getOkHttpClient();
        if (okHttpClient != null) {
            this.picasso = new Picasso.Builder(context)
//                    .indicatorsEnabled(true)
                    .downloader(new OkHttp3Downloader(okHttpClient))
                    .build();
        }
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationM notification = notifications.get(position);
        holder.notification = notification;
        holder.publisherName.setText(notification.getSenderName());
        holder.body.setText(notification.getMessage());
        holder.date.setText(DateUtils.format(notification.getSentTime()));
        // TODO(benoit) be aware of long names! maxline to 1 for now
        holder.module.setText(notification.getSubTitle() + "：" + notification.getTitle());

        if (this.picasso != null) {
            picasso
                    .load(notification.getSenderPhoto().replace("NORMAL", "SIZE_56"))
                    .transform(this.transformation)
                    .placeholder(R.drawable.ic_account_box)
                    .error(R.drawable.ic_close)
                    .into(holder.publisherIcon);
        }

        setActionState(holder, notification);
    }

    void setActionState(ViewHolder holder, NotificationM notification) {
        // flag
        holder.flagImageView.setAlpha(notification.isFlagged() ? 1f : 0.3f);

        // read
        holder.markImageView.setRotation(notification.isRead() ? 45 : 0);
        holder.markImageView.setAlpha(notification.isRead() ? 0.3f : 1f);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        NotificationM notification;
        ImageView publisherIcon;
        TextView publisherName;
        TextView body;
        TextView date;
        TextView module;
        ImageView markImageView;
        ImageView flagImageView;

        ViewHolder(View v) {
            super(v);
            publisherIcon = (ImageView) v.findViewById(R.id.notificationPublisherIcon);
            publisherName = (TextView) v.findViewById(R.id.notificationPublisherName);
            body = (TextView) v.findViewById(R.id.notificationBody);
            date = (TextView) v.findViewById(R.id.notificationDate);
            module = (TextView) v.findViewById(R.id.notificationModule);

            publisherIcon.setOnClickListener(this);
            publisherName.setOnClickListener(this);
            body.setOnClickListener(this);

            // action button
            v.findViewById(R.id.notificationActionLike).setOnClickListener(this);
            v.findViewById(R.id.notificationActionReply).setOnClickListener(this);
            v.findViewById(R.id.notificationActionShare).setOnClickListener(this);
            flagImageView = (ImageView) v.findViewById(R.id.notificationActionFlag);
            flagImageView.setOnClickListener(this);
            markImageView = (ImageView) v.findViewById(R.id.notificationActionMark);
            markImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case notificationPublisherIcon:
                    break;
                case notificationPublisherName:
                    break;
                case notificationBody:
//                    viewUrl(notification.getUrl());
                    showNotificationDetail(notification.getId());
                    break;
                case notificationActionLike:
                    break;
                case notificationActionReply:
                    break;
                case notificationActionShare:
                    break;
                case notificationActionFlag:
                    flagNotification(this, notification);
                    break;
                case notificationActionMark:
                    markNotification(this, notification);
                    break;
                default:
                    Log.w(TAG, "onClick: What is this click ! " + view);
            }
        }
    }

    private void flagNotification(final ViewHolder holder, final NotificationM notification) {
        KintoneApi.flagNotification(notification)
                .subscribe(new SingleSubscriber<ApiResponse>() {
                    @Override
                    public void onSuccess(ApiResponse response) {
                        if (response.isSuccess()) {
                            if (holder.notification.getId() == notification.getId()) {
                                notification.setFlagged(!notification.isFlagged());
                                setActionState(holder, notification);
                            }
                            for (NotificationM ntf : notifications) {
                                if (ntf.getId() == notification.getId()) {
                                    ntf.setFlagged(notification.isFlagged());
                                }
                            }
                        } else {
                            Toast.makeText(context, "2xx but not success ? " + response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(context, "エラー発生したよ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void markNotification(final ViewHolder holder, final NotificationM notification) {
        KintoneApi.markNotification(notification)
                .subscribe(new SingleSubscriber<ApiResponse>() {
                    @Override
                    public void onSuccess(ApiResponse response) {
                        if (response.isSuccess()) {
                            if (holder.notification.getId() == notification.getId()) {
                                notification.setRead(!notification.isRead());
                                setActionState(holder, notification);
                            }
                            for (NotificationM ntf : notifications) {
                                if (ntf.getId() == notification.getId()) {
                                    ntf.setRead(notification.isRead());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(context, "エラー発生したよ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void showNotificationDetail(long notificationId) {
        Intent intent = new Intent(context, NotificationDetailActivity.class);
        intent.putExtra("notificationId", notificationId);
        context.startActivity(intent);
    }

    /**
     * Cannot open kindroid. Hence opens the browser but the certificate thing
     * is once again painful. Can be used but... looking for some other solution.
     *
     * @param notificationUrl
     */
    @Deprecated
    private void viewUrl(String notificationUrl) {
        Account account = AccountStore.loadAccount();
        assert account != null;
        Uri uri = account.createUri(notificationUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}
