package com.ezreal.ezchat.fragment;

import android.content.Intent;

import android.view.View;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ezreal.ezchat.R;
import com.ezreal.ezchat.activity.FriendInfoActivity;
import com.ezreal.ezchat.activity.CheckNotifyListActivity;
import com.ezreal.ezchat.activity.SearchUserActivity;
import com.ezreal.ezchat.handler.NimFriendHandler;
import com.ezreal.ezchat.handler.NimFriendHandler.OnFriendUpdateListener;
import com.ezreal.ezchat.handler.NimSysMsgHandler;
import com.ezreal.ezchat.handler.NimSysMsgHandler.SystemMessageListener;
import com.javonlee.dragpointview.OnPointDragListener;
import com.javonlee.dragpointview.view.AbsDragPointView;
import com.javonlee.dragpointview.view.DragPointView;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.suntek.commonlibrary.adapter.OnItemClickListener;
import com.suntek.commonlibrary.adapter.RViewHolder;
import com.suntek.commonlibrary.adapter.RecycleViewAdapter;
import com.suntek.commonlibrary.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wudeng on 2017/8/28.
 */

public class ContractFragment extends BaseFragment {
    // 创建名为 ContractFragment 的类，它继承自 BaseFragment

    public static final int REQUEST_CHECK_NOTI = 0x5000;
    // 声明一个整数常量 REQUEST_CHECK_NOTI，用于标识通知请求

    @BindView(R.id.rcv_friend)
    RecyclerView mRecyclerView;
    // 使用注解 @BindView 标记一个 RecyclerView 控件变量 mRecyclerView

    @BindView(R.id.dpv_unread_msg)
    DragPointView mDragPointView;
    // 使用注解 @BindView 标记一个 DragPointView 控件变量 mDragPointView

    private List<NimUserInfo> mFriendList;
    // 创建一个泛型为 NimUserInfo 的列表 mFriendList，用于存储用户信息

    private RecycleViewAdapter<NimUserInfo> mViewAdapter;
    // 创建一个泛型为 NimUserInfo 的适配器 mViewAdapter，用于处理 RecyclerView 中的数据

//    这段代码定义了一个名为 ContractFragment 的类，该类继承自 BaseFragment。它包含了以下成员：
//
//    REQUEST_CHECK_NOTI：一个整数常量，通常用于标识通知请求。这是一个公共的、静态的常量，通常用于作为标志。
//
//    mRecyclerView：一个 RecyclerView 控件，使用注解 @BindView 标记，用于在布局中显示好友列表。@BindView 注解用于与布局中的控件绑定。
//
//    mDragPointView：一个 DragPointView 控件，同样使用注解 @BindView 标记。它可能用于显示未读消息数量或提醒图标。
//
//    mFriendList：一个泛型为 NimUserInfo 的列表，用于存储好友（用户）信息。
//
//    mViewAdapter：一个泛型为 NimUserInfo 的适配器，通常用于在 RecyclerView 中管理和显示用户数据。适配器通常用于将数据与视图绑定，以便在列表或网格中显示用户信息。

    @Override
    public int setLayoutID() {
//        这是一个公有（public）方法，返回一个整数（int）类型的值。
        return R.layout.fragment_contract;
//        这个方法的主体，返回了一个整数值，即 R.layout.fragment_contract。
    }

    @Override
    public void initView(View rootView) {
        // 重写基类的 initView 方法，用于初始化视图
        ButterKnife.bind(this, rootView);
        // 使用 ButterKnife 绑定视图和类中的成员变量
        mFriendList = new ArrayList<>();
        // 创建一个 ArrayList 用于存储朋友列表数据
        mViewAdapter = new RecycleViewAdapter<NimUserInfo>(getContext(), mFriendList) {
            // 创建一个 RecyclerView 适配器 RecycleViewAdapter，用于填充 RecyclerView 的数据
            @Override
            public int setItemLayoutId(int position) {
//                这是一个公共方法，通常用于自定义的适配器类中。该方法接受一个整数参数 position，表示列表中的项的位置。
                return R.layout.item_friend;
//                这行代码表示在特定位置（position）设置列表项的布局资源文件的标识。
            }
//            这段代码的主要作用是返回特定位置的列表项的布局资源标识符。这通常在自定义适配器类中使用，其中根据位置动态设置不同的布局文件，以根据数据来呈现不同的列表项外观。
            @Override
            public void bindView(RViewHolder holder, int position) {
                // 重写 bindView 方法，在这里定义了如何绑定数据到 ViewHolder
                NimUserInfo item = mFriendList.get(position);
                // 获取指定位置的 NimUserInfo 对象，通常是一个好友信息或联系人信息
                holder.setImageByUrl(getContext(), R.id.iv_head_picture, item.getAvatar(), R.mipmap.bg_img_defalut);
                // 使用 RViewHolder 对象的 setImageByUrl 方法，加载并显示用户头像，这可能是通过 URL 获取的图片
                holder.setText(R.id.tv_friend_nick, item.getName());
                // 使用 RViewHolder 对象的 setText 方法，设置显示在 TextView 上的文本内容，通常是联系人的名称
            }
//            这段代码的作用是将好友或联系人列表中的数据与视图绑定，从而在列表视图中显示这些数据。
        };
        mViewAdapter.setItemClickListener(new OnItemClickListener() {
            // 设置列表项点击事件监听器
            @Override
            public void onItemClick(RViewHolder holder, int position) {
                // 当列表项被点击时，触发这个方法，其中position是被点击项的位置

                // 创建一个意图，用于启动 FriendInfoActivity
                Intent intent = new Intent(getContext(), FriendInfoActivity.class);

                // 将 NimUserInfo 对象作为额外数据添加到意图中
                intent.putExtra("NimUserInfo", mFriendList.get(position));

                // 在意图中添加一个名为 "FLAG" 的额外标志，用于指示 FriendInfoActivity 显示好友信息
                intent.putExtra("FLAG", FriendInfoActivity.FLAG_SHOW_FRIEND);

                // 启动 FriendInfoActivity
                startActivity(intent);
            }
        });
//        这段代码设置了一个列表项点击事件监听器，该监听器在用户点击列表中的项时被触发。当某个列表项被点击时，将执行监听器内的 onItemClick 方法。在这个方法中，会创建一个意图（Intent）用于启动 FriendInfoActivity，并将一些额外的数据添加到意图中，包括一个 NimUserInfo 对象和一个名为 "FLAG" 的标志。这些数据可能会在 FriendInfoActivity 中用于显示好友信息。
//
//        这种操作允许你在列表项点击时执行特定的操作，例如，打开另一个活动并显示有关所点击项的信息。这是一种常见的用户界面交互模式，用于导航和呈现应用程序中的不同信息。

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
// 设置RecyclerView的布局管理器为LinearLayoutManager，用于控制列表项的排列方式。这里使用的是默认的垂直排列。

        mRecyclerView.setAdapter(mViewAdapter);
// 将RecyclerView与适配器 mViewAdapter 关联，适配器负责将数据与视图绑定，以便在RecyclerView中显示数据。


        mDragPointView.setOnPointDragListener(new OnPointDragListener() {
            // 通过设置一个 OnPointDragListener 的实例来监听拖动点的相关事件
            @Override
            public void onRemoveStart(AbsDragPointView view) {
                // 当开始移除拖动点时，这个方法会被调用
                // 参数 view 是触发了移除操作的拖动点视图
                // 在这个方法中，你可以执行一些逻辑来处理开始移除操作
            }

            @Override
            public void onRemoveEnd(AbsDragPointView view) {
                // 当完成移除拖动点时，这个方法会被调用
                // 参数 view 是触发了移除操作的拖动点视图
                // 在这个方法中，你可以执行一些逻辑来处理完成移除操作
            }

            @Override
            public void onRecovery(AbsDragPointView view) {
                // 当拖动点恢复到原始位置时，这个方法会被调用
                // 参数 view 是触发了恢复操作的拖动点视图
                // 在这个方法中，你可以执行一些逻辑来处理恢复操作
            }
        });
//        这段代码使用了 setOnPointDragListener 方法来设置拖动点视图的拖动监听器，这个监听器是一个匿名内部类，实现了 OnPointDragListener 接口。这个接口定义了三个抽象方法，分别用于处理拖动点开始移除、拖动点完成移除和拖动点恢复到原始位置的事件。

        NimSysMsgHandler.getInstance().setMessageListener(new SystemMessageListener() {
            // 使用 NimSysMsgHandler 获取实例，并设置消息监听器

            @Override
            public void addFriendNotify() {
                // 当接收到添加好友通知时执行以下操作
                updateUnReadMsgView(1);
                // 更新未读消息视图，例如增加未读消息计数
            }
        });

        NimFriendHandler.getInstance().setUpdateListener(new OnFriendUpdateListener() {
            // 使用 NimFriendHandler 获取实例，并设置好友更新监听器

            @Override
            public void friendUpdate() {
                // 当好友更新时执行以下操作
                loadFriendList();
                // 加载好友列表，可能是刷新好友列表的操作
            }
        });
//        这段代码使用了NIM SDK（网易云信 SDK）的相关类来设置系统消息和好友更新的监听器。

        loadFriendList();

    }

    private void loadFriendList() {
        // 清空当前的好友列表，以便重新加载
        mFriendList.clear();

        // 从 NimFriendHandler 实例获取好友信息，并添加到好友列表
        mFriendList.addAll(NimFriendHandler.getInstance().getFriendInfos());

        // 通知适配器数据集发生变化，以便刷新界面显示
        mViewAdapter.notifyDataSetChanged();
    }
//    这段代码定义了一个方法 loadFriendList，用于加载好友列表。在方法内部，首先清空了当前的好友列表 mFriendList，然后从 NimFriendHandler 实例中获取好友信息，将其添加到好友列表中。最后，调用 mViewAdapter 的 notifyDataSetChanged 方法来通知适配器数据集发生了变化，以便更新界面显示。

    /**
     * 更新(若为当前为隐藏则显示)未读验证消息提醒数
     * @param newMsg 新消息数目
     */
    public void updateUnReadMsgView(int newMsg) {
        // 定义一个方法，用于更新未读消息视图

        if (mDragPointView.getVisibility() == View.VISIBLE) {
            // 如果未读消息视图可见

            int msg = Integer.parseInt(mDragPointView.getText().toString());
            // 从未读消息视图中获取当前的消息数，将文本内容解析为整数

            mDragPointView.setText(String.valueOf(msg + newMsg));
            // 更新未读消息视图的文本内容，将原有消息数和新消息数相加，然后转换为字符串

        } else {
            // 如果未读消息视图不可见

            mDragPointView.setText(String.valueOf(newMsg));
            // 直接设置未读消息视图的文本内容为新消息数，并将未读消息视图设置为可见状态
            mDragPointView.setVisibility(View.VISIBLE);
        }
    }
//    这段代码是一个用于更新未读消息视图的方法。方法接受一个整数参数 newMsg，表示新消息的数量。它的作用是根据当前未读消息视图的状态，更新或初始化显示未读消息数量。

    /**
     * 在进入消息提醒列表后，隐藏未读消息提醒
     */
    public void hindUnReadMsgView() {
        // 定义了一个名为 hindUnReadMsgView 的方法
        mDragPointView.setVisibility(View.GONE);
        // 设置 mDragPointView 视图的可见性为 GONE，即隐藏这个视图
    }
//    这段代码是一个方法，名为 hindUnReadMsgView。这个方法用于隐藏 mDragPointView 视图，通过将它的可见性设置为 View.GONE 来实现。在Android开发中，View.GONE 表示视图不可见且不占用布局空间，即在界面上隐藏该视图。这个方法的主要作用是在需要时隐藏未读消息的提示视图，以提供更好的用户体验。

    @OnClick(R.id.layout_add_friend)
    public void addFriend() {
        // 用注解 @OnClick 标记了这个方法，表示它是一个点击事件处理方法，与指定的视图 R.id.layout_add_friend 相关联。

        startActivity(new Intent(getContext(), SearchUserActivity.class));
        // 当 R.id.layout_add_friend 的视图被点击时，会执行这个方法。
        // 这个方法的作用是启动一个新的活动（Activity），即跳转到 SearchUserActivity。
        // getContext() 用于获取当前上下文，通常在 Android 中用于获取 Activity 的上下文。
    }
//    这段代码使用了注解 @OnClick(R.id.layout_add_friend)，它告诉应用当点击一个具有 R.id.layout_add_friend 资源标识的视图时，要执行名为 addFriend 的方法。在 addFriend 方法内，它创建一个新的意图（Intent），用于启动 SearchUserActivity 活动，这样当用户点击 layout_add_friend 视图时，会跳转到搜索用户的界面。这种方式通过 ButterKnife 库简化了视图点击事件的处理。

    @OnClick(R.id.layout_msg_notify)
// 通过 ButterKnife 注解标记一个点击事件，当 R.id.layout_msg_notify 控件被点击时触发下面的方法
    public void openMsgNotifyActivity() {
        // 定义一个名为 openMsgNotifyActivity 的方法，用于处理点击事件
        hindUnReadMsgView();
        // 调用 hindUnReadMsgView 方法，可能是用于隐藏未读消息视图
        startActivityForResult(new Intent(getContext(), CheckNotifyListActivity.class), REQUEST_CHECK_NOTI);
        // 启动一个新的 Activity，通常是 CheckNotifyListActivity，用于查看通知列表，并传入 REQUEST_CHECK_NOTI 作为请求码
    }
//    这段代码使用了注解 @OnClick 来标记一个点击事件，当与 R.id.layout_msg_notify 对应的控件被点击时，会触发 openMsgNotifyActivity 方法。这个方法的主要目的是启动 CheckNotifyListActivity 来查看通知列表，同时还调用了 hindUnReadMsgView 方法，可能用于在打开通知列表前隐藏未读消息的视图。通过 startActivityForResult 启动一个新的 Activity，传入 REQUEST_CHECK_NOTI 作为请求码，以便在目标 Activity 中识别和处理结果。这是 Android 中常见的处理点击事件并启动新 Activity 的模式。

    @OnClick(R.id.layout_group_chat)
    public void openGroupListActivity() {
        // 使用 ButterKnife 注解，表示当视图中 id 为 R.id.layout_group_chat 的元素被点击时，将执行该方法。
        // 这是一个点击事件处理方法。

        // 在该方法内部，你可以添加代码来处理点击 id 为 R.id.layout_group_chat 的视图元素的事件。
        // 例如，可以在这个方法中启动一个新的 Activity 或者执行其他操作。
    }
//    这段代码使用 ButterKnife 注解，特别是 @OnClick 注解，来标记一个方法，以便在用户点击视图中指定 id 的元素时执行该方法。在这里，@OnClick(R.id.layout_group_chat) 表示当布局中的元素 id 为 layout_group_chat 的视图被点击时，将触发 openGroupListActivity 方法。

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 调用基类的方法，以确保正确处理 Activity 结果

        if (requestCode == REQUEST_CHECK_NOTI) {
            // 检查请求码是否为特定的值 REQUEST_CHECK_NOTI
            if (resultCode == CheckNotifyListActivity.RESULT_HAVE_CHANGE) {
                // 如果结果码是特定的值 RESULT_HAVE_CHANGE
                loadFriendList();
                // 调用 loadFriendList 方法来加载好友列表
            }
        }
    }
//    这段代码是在 onActivityResult 方法中，用于处理从其他活动返回的结果。在 Android 开发中，当你启动另一个活动并等待它返回结果时，通常会使用 startActivityForResult 方法，该方法会传递一个请求码 (requestCode)。在这个方法中，首先调用了基类 super.onActivityResult 方法以确保正确处理结果。然后，它检查 requestCode 是否等于一个特定的值 REQUEST_CHECK_NOTI，以确保它是从特定的活动返回的。如果结果码 resultCode 也等于 CheckNotifyListActivity.RESULT_HAVE_CHANGE，则表示从 CheckNotifyListActivity 返回的结果表明发生了变化，因此它调用 loadFriendList() 方法以重新加载好友列表。这是一种用于处理返回结果并触发适当操作的常见方式，尤其在应用中需要在不同活动之间传递数据或状态时。
}
