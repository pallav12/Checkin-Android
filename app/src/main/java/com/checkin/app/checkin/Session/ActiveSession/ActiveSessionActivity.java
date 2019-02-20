package com.checkin.app.checkin.Session.ActiveSession;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE;
import com.checkin.app.checkin.Data.Message.MessageObjectModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Search.SearchActivity;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatActivity;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel;
import com.checkin.app.checkin.Session.Model.ActiveSessionModel;
import com.checkin.app.checkin.Session.Model.SessionCustomerModel;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveSessionActivity extends BaseActivity implements ActiveSessionMemberAdapter.SessionMemberInteraction {
    private static final String TAG = ActiveSessionActivity.class.getSimpleName();

    private static final int RC_SEARCH_MEMBER = 201;

    @BindView(R.id.rv_session_members)
    RecyclerView rvMembers;
    @BindView(R.id.tv_active_session_bill)
    TextView tvBill;
    @BindView(R.id.tv_as_waiter_name)
    TextView tvWaiterName;
    @BindView(R.id.tv_session_live_at)
    TextView tvSessionLiveAt;
    @BindView(R.id.im_as_waiter_pic)
    ImageView imWaiterPic;
    @BindView(R.id.container_as_actions_bottom)
    ViewGroup containerBottomActions;
    @BindView(R.id.fl_view_orders)
    FrameLayout flViewOrders;

    private ActiveSessionViewModel mViewModel;
    private ActiveSessionMemberAdapter mSessionMembersAdapter;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageModel message = MessageUtils.parseMessage(intent);
            if (message == null) return;

            MessageObjectModel model;
            switch (message.getType()) {
                case USER_SESSION_BILL_CHANGE:
                    ActiveSessionActivity.this.updateBill(message.getRawData().getSessionBillTotal());
                    break;
                case USER_SESSION_HOST_ASSIGNED:
                    model = message.getObject();
                    ActiveSessionActivity.this.updateHost(model.getBriefModel());
                    break;
                case USER_SESSION_MEMBER_ADD_REQUEST:
                    model = message.getActor();
                    SessionCustomerModel customer = new SessionCustomerModel(model.getPk(), model.getBriefModel(), false, false);
                    ActiveSessionActivity.this.addCustomer(customer);
                    break;
                case USER_SESSION_MEMBER_ADDED:
                    model = message.getObject();
                    SessionCustomerModel accCustomer = new SessionCustomerModel(model.getPk(), model.getBriefModel(), false, true);
                    ActiveSessionActivity.this.addCustomer(accCustomer);
                    break;
                case USER_SESSION_END:
                    Utils.navigateBackToHome(getApplicationContext());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);

        setupUi();
        setupObservers();

        mViewModel.fetchActiveSessionDetail();
    }

    private void setupObservers() {
        mViewModel.getSessionData().observe(this, resource -> {
            if (resource == null) return;
            ActiveSessionModel data = resource.data;
            switch (resource.status) {
                case SUCCESS: {
                    if (data == null)
                        return;
                    stopRefreshing();
                    mViewModel.setSessionPk(data.getPk());
                    mViewModel.setShopPk(data.getShopPk());
                    setupData(data);
                    break;
                }
                case LOADING: {
                    startRefreshing();
                    break;
                }
                default: {
                    stopRefreshing();
                    Log.e(resource.status.name(), resource.message == null ? "Null" : resource.message);
                }
            }
        });

        mViewModel.getSessionMemberUpdate().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    Utils.toast(this, "Done!");
                    if (resource.data != null)
                        mViewModel.updateUiSessionMember(Long.parseLong(resource.data.getPk()));
                    else mViewModel.updateResults();
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(this, resource.message);
                }
            }
        });
    }

    @Override
    protected void updateScreen() {
        mViewModel.updateResults();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUi() {
        rvMembers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mSessionMembersAdapter = new ActiveSessionMemberAdapter(null, this);
        rvMembers.setAdapter(mSessionMembersAdapter);

        containerBottomActions.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP)
                openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_NONE);
            return true;
        });

        initRefreshScreen(R.id.sr_active_session);
    }

    private void setupData(ActiveSessionModel data) {
        mSessionMembersAdapter.setUsers(data.getCustomers());
        tvBill.setText(data.formatBill(this));
        tvSessionLiveAt.setText(data.getRestaurant().getDisplayName());
        if (data.gethost() != null) {
            tvWaiterName.setText(data.gethost().getDisplayName());
            Utils.loadImageOrDefault(imWaiterPic, data.gethost().getDisplayPic(), R.drawable.ic_waiter);
        } else {
            tvWaiterName.setText(R.string.waiter_unassigned);
        }
    }

    private void updateBill(double bill) {
        mViewModel.updateBill(bill);
    }

    private void updateHost(BriefModel user) {
        mViewModel.updateHost(user);
    }

    private void addCustomer(SessionCustomerModel customer) {
        mViewModel.addCustomer(customer);
    }

    @OnClick(R.id.btn_active_session_menu)
    public void onListMenu() {
        SessionMenuActivity.withSession(this, mViewModel.getShopPk(), null);
    }

    @OnClick(R.id.btn_active_session_orders)
    public void onViewOrders() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_view_orders,ActiveSessionViewOrdersFragment.newInstance()).commit();
    }

    @OnClick(R.id.tv_active_session_bill)
    public void openBillDetails() {
        startActivity(new Intent(
                this, ActiveSessionInvoiceActivity.class)
                .putExtra(ActiveSessionInvoiceActivity.KEY_SESSION_PK, mViewModel.getSessionPk()));
    }

    @OnClick(R.id.ll_call_waiter_button)
    public void openChatCallWaiter() {
        openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_CALL_WAITER);
    }

    @OnClick(R.id.ll_table_cleaning_button)
    public void openChatCleanContainer() {
        openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_CLEAN_TABLE);
    }

    @OnClick(R.id.ll_refill_glass_button)
    public void openChatRefillGlass() {
        openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
    }

    @Override
    public void onUnacceptedMemberClicked(SessionCustomerModel customerModel) {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to accept/reject member request?")
                .setPositiveButton("Accept", ((dialog, which) -> mViewModel.acceptSessionMember(customerModel.getUser().getPk())))
                .setNegativeButton("Reject", ((dialog, which) -> mViewModel.removeSessionMember(customerModel.getUser().getPk())))
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SEARCH_MEMBER && resultCode == RESULT_OK) {
            long userPk = data.getLongExtra(SearchActivity.KEY_RESULT_PK, 0L);
            mViewModel.addMembers(userPk);
        }
    }

    public void openChat(SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE service) {
        Intent myIntent = new Intent(ActiveSessionActivity.this, SessionChatActivity.class);
//        ActivityOptions options = ActivityOptions.makeCustomAnimation(ActiveSessionActivity.this, R.anim.slide_up, R.anim.slide_down);
        myIntent.putExtra(SessionChatActivity.KEY_SERVICE_TYPE, service.tag);
        startActivity(myIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MESSAGE_TYPE[] types = new MESSAGE_TYPE[] {
                MESSAGE_TYPE.USER_SESSION_BILL_CHANGE, MESSAGE_TYPE.USER_SESSION_HOST_ASSIGNED,
                MESSAGE_TYPE.USER_SESSION_MEMBER_ADD_REQUEST, MESSAGE_TYPE.USER_SESSION_MEMBER_ADDED, MESSAGE_TYPE.USER_SESSION_END
        };
        MessageUtils.registerLocalReceiver(this, mReceiver, types);
        updateScreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(this, mReceiver);
    }
}
