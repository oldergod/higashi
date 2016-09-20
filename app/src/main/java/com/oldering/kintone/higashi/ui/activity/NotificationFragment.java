package com.oldering.kintone.higashi.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.oldering.kintone.higashi.HigashiApplication;
import com.oldering.kintone.higashi.R;
import com.oldering.kintone.higashi.api.slash.AuthApi;
import com.oldering.kintone.higashi.datamodel.INotificationModel;
import com.oldering.kintone.higashi.exception.KeyStoreMacInvalidException;
import com.oldering.kintone.higashi.model.Account;
import com.oldering.kintone.higashi.model.NotificationM;
import com.oldering.kintone.higashi.model.NotificationsWrapper;
import com.oldering.kintone.higashi.ui.adapter.NotificationAdapter;
import com.oldering.kintone.higashi.ui.adapter.NotificationPagerAdapter;
import com.oldering.kintone.higashi.ui.viewmodel.NotificationViewModel;
import com.oldering.kintone.higashi.util.AccountStore;

import java.util.ArrayList;

import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.makeramen.roundedimageview.RoundedDrawable.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class NotificationFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView recyclerView;

    private CompositeSubscription subscription;
    private NotificationViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    public Context context;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NotificationFragment newInstance(int sectionNumber) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new NotificationViewModel(getDataModel());
    }

    @Override
    public void onResume() {
        super.onResume();
        bind();
    }

    @Override
    public void onPause() {
        super.onPause();
        unBind();
    }

    private void bind() {
        subscription = new CompositeSubscription();
        subscription.add(getNotificationsSubscription());
    }

    Subscription getNotificationsSubscription() {
        final Single<NotificationsWrapper> notificationsSingle;
        switch (sectionNumber()) {
            case NotificationPagerAdapter.TO_ME:
                notificationsSingle = viewModel.getMentionNotifications();
                break;
            case NotificationPagerAdapter.ALL:
                notificationsSingle = viewModel.getAllNotifications();
                break;
            case NotificationPagerAdapter.FLAGGED:
                notificationsSingle = viewModel.getFlagNotifications();
                break;
            default:
                throw new RuntimeException("What section is that " + sectionNumber());
        }
        return notificationsSingle.subscribe(new SingleSubscriber<NotificationsWrapper>() {
            @Override
            public void onSuccess(NotificationsWrapper notifications) {
                appendNotifications(notifications);
                resetSwipeRefreshLayout();
            }

            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "onError: " + error.toString());
                retry();
            }
        });
    }

    void retry() {
        // let's try to login and go
        try {
            AuthApi.attemptLogin(AccountStore.loadAccount(), new SingleSubscriber<Account>() {
                @Override
                public void onSuccess(Account value) {
                    Log.d(TAG, "onSuccess: CONNARD");
                    getNotificationsSubscription();
                }

                @Override
                public void onError(Throwable error) {
                    Log.d(TAG, "onError: CONNARD");
                    error.printStackTrace();
                    Toast.makeText(getActivity(), "PROBLEME", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                }
            });
        } catch (KeyStoreMacInvalidException e1) {
            e1.printStackTrace();
        }
    }

    void navigateToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void unBind() {
        subscription.unsubscribe();
    }

    int sectionNumber() {
        return getArguments().getInt(ARG_SECTION_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: savedInstanceState is " + (savedInstanceState == null ? "null" : "exists!"));
        View rootView = inflater.inflate(R.layout.notifications_scrolling, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.notificationsListView);
        recyclerView.setTag("recyclerViewAt" + getArguments().getInt(ARG_SECTION_NUMBER));

        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new NotificationAdapter(getContext(), new ArrayList<NotificationM>()));
        }

        // use a linear layout manager
        if (recyclerView.getLayoutManager() == null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.notificationsSwiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotificationsSubscription();
            }
        });

        return rootView;
    }

    void resetSwipeRefreshLayout() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void appendNotifications(NotificationsWrapper notificationsWrapper) {
        RecyclerView.Adapter adapter = new NotificationAdapter(getContext(), notificationsWrapper.getNotifications());
        // TODO(benoit) should we just add ntf to the adapter and trigger the callback?
        recyclerView.setAdapter(adapter);
    }

    @NonNull
    private INotificationModel getDataModel() {
        return HigashiApplication.getInstance().getNotificationModel();
    }
}