package com.checkin.app.checkin.Session.ActiveSession.Chat;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel.EVENT_CONCERN_TYPE;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SessionChatActivity extends AppCompatActivity implements ActiveSessionChatAdapter.SessionChatInteraction {
    public static final String KEY_SERVICE_TYPE = "session_chat.service";

    @BindView(R.id.bottom_expand_menu)
    ViewGroup bottomExpandedMenu;
    @BindView(R.id.rv_active_session_chat)
    RecyclerView rvSessionChat;
    @BindView(R.id.et_chat_msg)
    EditText etMessage;
    @BindView(R.id.btn_chat_send_msg)
    ImageView btnSendMsg;
    @BindView(R.id.ll_call_waiter_button)
    View vCallWaiter;
    @BindView(R.id.ll_table_cleaning_button)
    View vCleanTable;
    @BindView(R.id.ll_refill_glass_button)
    View vRefillGlass;
    @BindView(R.id.refresh_session_chat)
    SwipeRefreshLayout refreshSessionChat;

    private ActiveSessionChatAdapter mChatAdapter;
    private ActiveSessionChatViewModel mViewModel;

    private int concernTag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session_chat);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionChatViewModel.class);

        setupUi();

        mViewModel.getSessionChat().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                mChatAdapter.setData(resource.data);
                refreshSessionChat.setRefreshing(false);
            } else if (resource.status == Resource.Status.LOADING) {
                refreshSessionChat.setRefreshing(true);
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    Utils.toast(this, "Sent!");
                    resetMessageState();
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Utils.toast(this, resource.message);
                }
            }
        });

        mViewModel.fetchSessionChat();
    }

    private void setupUi() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white);
        }

        rvSessionChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        mChatAdapter = new ActiveSessionChatAdapter(null, this);
        rvSessionChat.setAdapter(mChatAdapter);

        refreshSessionChat.setOnRefreshListener(() -> mViewModel.updateResults());

        int serviceTag = getIntent().getIntExtra(KEY_SERVICE_TYPE, EVENT_REQUEST_SERVICE_TYPE.SERVICE_NONE.tag);
        if (serviceTag == EVENT_REQUEST_SERVICE_TYPE.SERVICE_CALL_WAITER.tag)
            vCallWaiter.performClick();
        else if (serviceTag == EVENT_REQUEST_SERVICE_TYPE.SERVICE_CLEAN_TABLE.tag)
            vCleanTable.performClick();
        else if (serviceTag == EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY.tag)
            vRefillGlass.performClick();

        invalidateOptionsMenu();
    }

    private void resetMessageState() {
        etMessage.setText("");
        etMessage.setHint(getResources().getString(R.string.title_how_can_we_assist_you));
        btnSendMsg.setTag(null);
    }

    public void setMessage(String msg) {
        etMessage.setText(msg);
        etMessage.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @OnClick(R.id.ll_call_waiter_button)
    public void clickCallWaiter() {
        setMessage(getResources().getString(R.string.msg_call_waiter));
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_CALL_WAITER);
    }

    @OnClick(R.id.ll_table_cleaning_button)
    public void clickTableClean() {
        setMessage(getResources().getString(R.string.msg_clean_table));
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_CLEAN_TABLE);
    }

    @OnClick(R.id.ll_refill_glass_button)
    public void clickRefillGlass() {
        setMessage(getResources().getString(R.string.msg_refill_glass));
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
    }

    @OnClick(R.id.im_expand_bottom_menu)
    public void onExpandMenu() {
        bottomExpandedMenu.setVisibility(View.VISIBLE);
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        bottomExpandedMenu.startAnimation(bottomUp);
    }

    @OnClick(R.id.im_collapse_bottom_menu)
    public void onCollapseMenu() {
        bottomExpandedMenu.setVisibility(View.GONE);
        Animation upDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        bottomExpandedMenu.startAnimation(upDown);
    }

    @OnClick(R.id.btn_chat_send_msg)
    public void sendMsg() {
        String msg = etMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(msg)) {
            if (concernTag != 0 && mChatAdapter.getSelectedEvent() != null) {
                mViewModel.raiseConcern(concernTag, msg, mChatAdapter.getSelectedEvent().getPk());
            } else {
                if (btnSendMsg.getTag() == null)
                    btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_NONE);
                mViewModel.sendMessage(msg, btnSendMsg.getTag());
            }
        } else {
            Utils.toast(this, "Please enter the message.");
        }
    }

    @OnClick(R.id.ll_napkin_container_button)
    public void clickNapkin() {
        setMessage("Napkins required");
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
    }

    @OnClick(R.id.ll_extra_plates_container_button)
    public void clickExtraPlates() {
        setMessage("Bring extra plates");
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
    }

    @OnClick(R.id.ll_salt_container_button)
    public void clickSalt() {
        setMessage("Bring salt");
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
    }

    @OnClick(R.id.ll_sauce_container_button)
    public void clickSauce() {
        setMessage("Bring sauce");
        btnSendMsg.setTag(EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delay:
                concernTag = EVENT_CONCERN_TYPE.CONCERN_DELAY.tag;
                setMessage("");
                return true;
            case R.id.menu_quality:
                concernTag = EVENT_CONCERN_TYPE.CONCERN_QUALITY.tag;
                setMessage("");
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return mChatAdapter.getSelectedEvent() != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_active_session_chat, menu);
        return true;
    }

    @Override
    public void onSelectionChange(@Nullable SessionChatModel chatModel) {
        invalidateOptionsMenu();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mChatAdapter.getSelectedEvent() != null) {
            mChatAdapter.resetSelectedEvent();
        } else super.onBackPressed();
    }
}
