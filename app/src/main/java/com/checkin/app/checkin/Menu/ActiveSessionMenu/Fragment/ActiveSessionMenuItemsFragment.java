package com.checkin.app.checkin.Menu.ActiveSessionMenu.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Menu.MenuItemInteraction;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Menu.ActiveSessionMenu.Adapter.ActiveSessionMenuItemAdapter;
import com.checkin.app.checkin.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ActiveSessionMenuItemsFragment extends Fragment implements ActiveSessionMenuItemAdapter.OnItemInteractionListener {
    @BindView(R.id.rv_menu_items)
    RecyclerView rvMenuItems;
    private Unbinder unbinder;
    private ActiveSessionMenuItemAdapter mAdapter;
    @Nullable
    private MenuItemInteraction mListener;

    public ActiveSessionMenuItemsFragment() {
    }

    public static ActiveSessionMenuItemsFragment newInstance(List<MenuItemModel> menuItems, MenuItemInteraction listener, boolean isSessionActive) {
        ActiveSessionMenuItemsFragment fragment = new ActiveSessionMenuItemsFragment();
        fragment.mAdapter = new ActiveSessionMenuItemAdapter(menuItems, fragment, isSessionActive);
        fragment.mListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_menu_items, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvMenuItems.setAdapter(mAdapter);
        rvMenuItems.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onItemAdded(MenuItemModel item) {
        if (mListener != null) {
            return mListener.onMenuItemAdded(item);
        }
        return false;
    }

    @Override
    public boolean onItemLongPress(MenuItemModel item) {
        if (mListener != null) {
            mListener.onMenuItemShowInfo(item);
        }
        return true;
    }

    @Override
    public boolean onItemChanged(MenuItemModel item, int count) {
        if (mListener != null) {
            return mListener.onMenuItemChanged(item, count);
        }
        return false;
    }

    @Override
    public int orderedItemCount(MenuItemModel item) {
        if (mListener != null) {
            return mListener.getItemOrderedCount(item);
        }
        return 0;
    }
}
