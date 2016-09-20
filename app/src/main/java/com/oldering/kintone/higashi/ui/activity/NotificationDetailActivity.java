package com.oldering.kintone.higashi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.oldering.kintone.higashi.HigashiApplication;
import com.oldering.kintone.higashi.R;
import com.oldering.kintone.higashi.datamodel.INotificationModel;
import com.oldering.kintone.higashi.model.Notification;
import com.oldering.kintone.higashi.model.NotificationContainer;
import com.oldering.kintone.higashi.model.User;
import com.oldering.kintone.higashi.ui.viewmodel.NotificationViewModel;
import com.oldering.kintone.higashi.util.DateUtils;
import com.squareup.picasso.Picasso;

import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class NotificationDetailActivity extends BaseActivity {
    private static final String TAG = "NotificationDetailActiv";

    ImageView publisherIcon;
    TextView publisherName;
    TextView body;
    TextView date;
    TextView module;

    long notificationId;

    PublishSubject<User> publisherIconClickSubject;

    private CompositeSubscription subscription;
    private NotificationViewModel viewModel;
    private Picasso picasso;

    @Override
    protected void onResume() {
        super.onResume();
        bind();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unBind();
    }

    private void bind() {
        subscription = new CompositeSubscription();
        // TODO(benoit) set this notification Id
        subscription.add(getNotificationSubscription());
        subscription.add(getPublisherIconClickSubscription());
    }

    private Subscription getNotificationSubscription() {
        return viewModel
                .getNotification(notificationId)
                .subscribe(new SingleSubscriber<NotificationContainer>() {
                    @Override
                    public void onSuccess(NotificationContainer notification) {
                        setNotification(notification);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(NotificationDetailActivity.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Subscription getPublisherIconClickSubscription() {
        return publisherIconClickSubject.subscribe(new Subscriber<User>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(User user) {
                // lets show the icon in big
                Toast.makeText(NotificationDetailActivity.this, "Clicking the icon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unBind() {
        subscription.unsubscribe();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        notificationId = intent.getLongExtra("notificationId", -1L);
        if (notificationId < 0) {
            Toast.makeText(this, "no notification id so closing", Toast.LENGTH_SHORT).show();
            finish();
        }

        viewModel = new NotificationViewModel(getDataModel());

        publisherIconClickSubject = PublishSubject.create();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        this.picasso = new Picasso.Builder(getApplicationContext())
                .downloader(new OkHttp3Downloader(HigashiApplication.getInstance().getOkHttpClient()))
                .build();
    }

    @Override
    void inflateViews() {
        setContentView(R.layout.activity_notification_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        publisherIcon = (ImageView) findViewById(R.id.notificationPublisherIcon);
        publisherName = (TextView) findViewById(R.id.notificationPublisherName);
        body = (TextView) findViewById(R.id.notificationBody);
        date = (TextView) findViewById(R.id.notificationDate);
        module = (TextView) findViewById(R.id.notificationModule);
    }

    @Override
    void fillViews() {

    }

    void setNotification(@NonNull NotificationContainer notificationContainer) {
// TODO(benoit) publisher icon
        Notification notification = notificationContainer.getItem();
        final User user = notificationContainer.getSender();
        publisherName.setText(user.getName());
        body.setText(notification.getContent().getMessage().getText());
        date.setText(DateUtils.format(notification.getSentTime()));
        module.setText(notification.getContent().getTitle().getText());

        picasso
                .load(user.getPhoto().getSize_96_r())
                .placeholder(R.drawable.ic_account_box)
                .error(R.drawable.ic_close)
                .into(publisherIcon);

        publisherIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publisherIconClickSubject.onNext(user);
            }
        });
    }

    @NonNull
    private INotificationModel getDataModel() {
        return HigashiApplication.getInstance().getNotificationModel();
    }
}
