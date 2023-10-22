package com.ezreal.ezchat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ezreal.ezchat.R;
import com.ezreal.ezchat.bean.AddFriendNotify;
import com.ezreal.ezchat.handler.NimFriendHandler;
import com.ezreal.ezchat.handler.NimUserHandler;
import com.ezreal.ezchat.handler.NimSysMsgHandler;
import com.ezreal.ezchat.handler.NimSysMsgHandler.SystemMessageListener;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.suntek.commonlibrary.adapter.OnItemClickListener;
import com.suntek.commonlibrary.adapter.OnItemLongClickListener;
import com.suntek.commonlibrary.adapter.RViewHolder;
import com.suntek.commonlibrary.adapter.RecycleViewAdapter;
import com.suntek.commonlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wudeng on 2017/8/29.
 */

public class CheckNotifyListActivity extends BaseActivity {

    // 创建名为 CheckNotifyListActivity 的类，该类继承 BaseActivity。

    // 定义一个整数常量 RESULT_HAVE_CHANGE，用于标记结果为数据已更改。
    public static final int RESULT_HAVE_CHANGE = 0x4000;

    // 定义一个整数常量 LOAD_MESSAGE_COUNT，用于设置加载通知消息的数量。
    private static final int LOAD_MESSAGE_COUNT = 500;

    // 使用 ButterKnife 框架绑定视图组件，将 RecyclerView 控件与代码中的 mRecyclerView 变量绑定。
    @BindView(R.id.rcv_notify_list)
    RecyclerView mRecyclerView;

    // 创建一个名为 mNotifyInfoList 的列表，用于存储添加好友通知信息。
    private List<AddFriendNotify> mNotifyInfoList;

    // 创建一个 RecyclerView 适配器，名为 mAdapter，用于管理 RecyclerView 中的通知数据。
    private RecycleViewAdapter<AddFriendNotify> mAdapter;

    // 创建一个布尔变量 haveChange，用于跟踪通知列表是否发生更改。
    private boolean haveChange = false;
//    这段代码定义了一个名为 CheckNotifyListActivity 的类，该类用于管理添加好友通知列表。它还包括了一些常量，用于标记结果和设置加载消息的数量。使用 ButterKnife 框架绑定了一个 RecyclerView 控件和一些变量，用于存储通知信息和管理 RecyclerView 的适配器。最后，定义了一个布尔变量，用于跟踪通知列表是否发生更改。
    @Override

    // 在Activity创建时调用
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置状态栏颜色为app中定义的蓝色
        setStatusBarColor(R.color.app_blue_color);

        // 设置Activity的内容视图为activity_check_notify.xml
        setContentView(R.layout.activity_check_notify);

        // 设置标题栏文本为“验证提醒”，并显示返回按钮和操作按钮
        setTitleBar("验证提醒", true, true);

        // 使用ButterKnife绑定视图控件
        ButterKnife.bind(this);

        // 初始化Activity
        init();
    }
//    这段代码是Android应用中的Activity生命周期方法 onCreate。它的目的是在Activity创建时进行一些初始化操作。在这里的具体操作包括：
//
//    调用 super.onCreate(savedInstanceState)，以确保Activity的基本初始化得以执行。
//
//    调用 setStatusBarColor(R.color.app_blue_color)，设置状态栏的颜色为应用定义的蓝色。
//
//    调用 setContentView(R.layout.activity_check_notify)，设置Activity的内容视图为指定的XML布局文件 activity_check_notify.xml。
//
//    调用 setTitleBar("验证提醒", true, true)，设置标题栏的文本为“验证提醒”，并显示返回按钮和操作按钮。
//
//    调用 ButterKnife.bind(this)，使用ButterKnife库绑定视图控件，以便在后续代码中直接访问这些控件。
//
//    最后，调用 init() 方法，执行其他与Activity的初始化相关的操作。

    private void init() {
        // 初始化通知信息列表
        mNotifyInfoList = new ArrayList<>();

// 创建适配器并关联通知信息列表
        mAdapter = new RecycleViewAdapter<AddFriendNotify>(this, mNotifyInfoList) {
            @Override
            public int setItemLayoutId(int position) {
                // 返回指定位置的通知项使用的布局资源 ID
                return R.layout.item_check_notify;
            }
//            这段代码的目的是初始化通知信息列表和与之关联的适配器。RecycleViewAdapter 是一个用于显示 RecyclerView 中数据的自定义适配器。在这里，创建了一个 mNotifyInfoList 列表来存储通知信息，然后创建了一个适配器 mAdapter 并指定了通知项使用的布局资源 ID，这里使用 R.layout.item_check_notify。适配器用于将数据与布局资源关联，以在 RecyclerView 中显示通知信息。

            @Override
            public void bindView(RViewHolder holder, final int position) {
                // 获取指定位置的好友通知项
                final AddFriendNotify item = mNotifyInfoList.get(position);

                // 获取通知消息
                SystemMessage message = item.getMessage();

                // 获取通知消息关联的用户信息
                NimUserInfo userInfo = item.getUserInfo();

                if (userInfo != null) {
                    // 如果用户信息不为空，使用用户信息的头像
                    holder.setImageByUrl(CheckNotifyListActivity.this, R.id.iv_head_picture, userInfo.getAvatar(), R.mipmap.bg_img_defalut);
                    holder.setText(R.id.tv_name, userInfo.getName());
                } else {
                    // 如果用户信息为空，使用消息的发送者帐号作为名字
                    holder.setImageResource(R.id.iv_head_picture, R.mipmap.bg_img_defalut);
                    holder.setText(R.id.tv_name, message.getFromAccount());
                }

                // 设置通知内容
                holder.setText(R.id.tv_content, message.getContent());
//                这段代码是一个 bindView 方法的实现，它用于将通知数据绑定到视图控件上，主要是好友通知列表的每一项。在这里，首先获取指定位置的通知项，然后根据通知的消息内容以及关联的用户信息设置头像、用户名和通知内容。如果没有相关的用户信息，就使用消息的发送者帐号作为名字。最后，将这些数据显示在对应的视图控件上。

                // 获取 "tv_status" TextView 视图对象
            TextView tv_status = holder.getTextView(R.id.tv_status);

// 获取 "tv_agree" TextView 视图对象
                TextView tv_agree = holder.getTextView(R.id.tv_do_agree);

// 获取 "tv_refuse" TextView 视图对象
                TextView tv_refuse = holder.getTextView(R.id.tv_do_refuse);

// 根据消息的状态设置视图的可见性和文本内容
                if (message.getStatus() == SystemMessageStatus.declined) {
                    // 如果消息状态为 "declined"（拒绝）
                    tv_agree.setVisibility(View.GONE);  // 隐藏同意按钮
                    tv_refuse.setVisibility(View.GONE);  // 隐藏拒绝按钮
                    tv_status.setText("已拒绝");  // 设置状态文本为 "已拒绝"
                    tv_status.setVisibility(View.VISIBLE);  // 显示状态文本
                } else if (message.getStatus() == SystemMessageStatus.passed) {
                    // 如果消息状态为 "passed"（已同意）
                    tv_agree.setVisibility(View.GONE);  // 隐藏同意按钮
                    tv_refuse.setVisibility(View.GONE);  // 隐藏拒绝按钮
                    tv_status.setText("已同意");  // 设置状态文本为 "已同意"
                    tv_status.setVisibility(View.VISIBLE);  // 显示状态文本
                } else if (message.getStatus() == SystemMessageStatus.init) {
                    // 如果消息状态为 "init"（初始状态）
                    tv_status.setText("");  // 清空状态文本
                    tv_status.setVisibility(View.GONE);  // 隐藏状态文本
                    tv_agree.setVisibility(View.VISIBLE);  // 显示同意按钮
                    tv_refuse.setVisibility(View.VISIBLE);  // 显示拒绝按钮
                }
//                这段代码的作用是根据系统消息的状态，控制相关视图（TextView）的可见性和文本内容。如果消息状态是"declined"（拒绝）或"passed"（已同意），则显示相应的状态文本并隐藏同意和拒绝按钮。如果消息状态是"init"（初始状态），则显示同意和拒绝按钮，同时隐藏状态文本。
                // 为 TextView tv_agree 添加点击事件监听器
                tv_agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 在点击时处理请求，其中
                        // item.getMessage().getFromAccount() 用于获取请求来自哪个账户
                        // position 通常表示列表中的位置或索引
                        // true 表示同意请求，这取决于实际应用逻辑
                        dealRequest(item.getMessage().getFromAccount(), position, true);
                    }
                });
//                这段代码的目的是为名为 tv_agree 的 TextView 控件添加一个点击事件监听器。当用户单击该 TextView 时，会调用 dealRequest 方法来处理请求。item.getMessage().getFromAccount() 获取请求的发送方账户，position 通常表示该项在列表中的位置，而 true 可能表示同意请求的操作，但具体含义取决于实际应用逻辑。

                // 设置一个点击事件监听器，当拒绝按钮被点击时执行以下操作
                tv_refuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 调用dealRequest方法，该方法用于处理请求
                        // 参数1: 通过item.getMessage().getFromAccount()获取请求来源的帐户
                        // 参数2: 通过position获取项目的位置
                        // 参数3: 传递false表示拒绝请求
                        dealRequest(item.getMessage().getFromAccount(), position, false);
                    }
                });
//                这段代码创建了一个点击事件监听器，当拒绝按钮 (tv_refuse) 被点击时触发。在监听器的onClick方法中，它调用了dealRequest方法，该方法用于处理特定请求。item.getMessage().getFromAccount()获取了请求的来源帐户，position表示请求的项目位置，而false作为参数表示拒绝请求。这段代码通过监听按钮点击来实现用户拒绝请求的功能。
            }
        };
        // 设置适配器的长按点击监听器，用于处理长按事件
        mAdapter.setItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(RViewHolder holder, int position) {
                // 处理长按事件，调用 ignoreRequest 方法
                ignoreRequest(position);
            }
        });

// 设置适配器的点击监听器，用于处理普通点击事件
        mAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RViewHolder holder, int position) {
                // 处理普通点击事件，调用 showAccountInfo 方法
                showAccountInfo(position);
            }
        });
//        这段代码的目的是在适配器中设置长按点击监听器和普通点击监听器。在长按事件触发时，将调用 ignoreRequest(position) 方法，而在普通点击事件触发时，将调用 showAccountInfo(position) 方法。这允许用户与列表项进行不同的操作，例如长按以忽略请求，普通点击以查看帐户信息。

        // 设置RecyclerView的布局管理器，这里使用LinearLayoutManager，表示列表项垂直排列
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

// 为RecyclerView设置适配器，mAdapter是用于填充RecyclerView的数据适配器
        mRecyclerView.setAdapter(mAdapter);

        // Tool Bar
        // 设置"返回"图标的点击监听器，当点击该图标时，执行finish()方法关闭当前活动
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        这段代码用于配置RecyclerView、设置适配器，以及为"返回"图标添加点击事件监听器。RecyclerView用于显示列表数据，布局管理器决定了列表项的排列方式，适配器负责提供数据给RecyclerView展示。"返回"图标的点击事件监听器使其在被点击时执行finish()方法，用于关闭当前活动。

        // 初始化好友添加通知监听，收到信息后刷新页面
        // 设置系统消息监听器，这里使用匿名内部类来实现接口SystemMessageListener
        NimSysMsgHandler.getInstance().setMessageListener(new SystemMessageListener() {
            @Override
            public void addFriendNotify() {
                // 当接收到好友添加通知时，调用loadAddFriendNotify()方法处理
                loadAddFriendNotify();
            }
        });

// 加载好友添加通知
        loadAddFriendNotify();
    }
//    这段代码的主要作用是设置系统消息监听器，以便在接收到好友添加通知时触发addFriendNotify()回调方法。然后，立即调用loadAddFriendNotify()方法来处理这些通知。如果系统消息中有新的好友添加通知，addFriendNotify()将被调用，通知将在loadAddFriendNotify()方法中被加载和处理。

    /**
     * 读取好友添加通知数据
     */
    private void loadAddFriendNotify() {
        // 加载添加好友的通知消息

// 创建一个类型列表以存储系统消息类型
        List<SystemMessageType> types = new ArrayList<>();

// 向类型列表添加一个类型，这里是添加好友的系统消息类型
        types.add(SystemMessageType.AddFriend);

        // 获取“添加朋友”消息列表
        List<SystemMessage> msgList = NIMClient.getService(SystemMessageService.class)
                .querySystemMessageByTypeBlock(types, 0, LOAD_MESSAGE_COUNT);

        // 根据对方账户，获取账户信息，构建显示 item 数据
        AddFriendNotify notify;
        List<AddFriendNotify> notifyInfoList = new ArrayList<>();
        for (SystemMessage message : msgList) {
            // 若用户已选择忽略这条消息，则跳过不显示
            if (message.getStatus() == SystemMessageStatus.ignored) {
                continue;
            }
            NimUserInfo userInfo = NimUserHandler.getInstance().getUserInfo();
            //若获取不到该条记录的账户数据，也跳过不显示
            if (userInfo == null) {
                return;
            }

            // 创建一个 AddFriendNotify 实例
            notify = new AddFriendNotify();

// 设置通知的消息内容
            notify.setMessage(message);

// 使用 NimFriendHandler 检查消息发送者是否是已经是我的好友
            notify.setMyFriend(NimFriendHandler.getInstance().CheckIsMyFriend(message.getFromAccount()));

// 设置通知的用户信息
            notify.setUserInfo(userInfo);

// 将该通知添加到通知信息列表中
            notifyInfoList.add(notify);

        }

        // 刷新界面
        // 清空通知信息列表
        mNotifyInfoList.clear();

// 将新的通知信息列表添加到现有列表中
        mNotifyInfoList.addAll(notifyInfoList);

// 通知适配器数据已更改，以便刷新视图
        mAdapter.notifyDataSetChanged();


        // 将“添加朋友”消息至为已读
        NIMClient.getService(SystemMessageService.class)
                .resetSystemMessageUnreadCountByType(types);
    }
//    这段代码的主要作用是加载并显示“添加好友”的通知消息。它首先创建一个用于存储系统消息类型的列表，然后使用NIM SDK查询符合“添加好友”类型的系统消息列表。接下来，它遍历消息列表，检查每个消息是否已被忽略，如果没有被忽略，就创建一个AddFriendNotify实例，设置通知的消息内容、发送者是否已是好友以及用户信息，并将通知添加到通知信息列表中。最后，它清空旧的通知信息列表，将新的通知信息列表添加到列表中，通知适配器数据已更改，以便刷新界面，并将“添加好友”消息标记为已读。
    /**
     * 处理好友请求
     *
     * @param account  对方账户
     * @param position 列表位置
     * @param agree    是否同意
     */
    // 处理好友请求
    private void dealRequest(String account, int position, final boolean agree) {
        // 获取系统消息
        final SystemMessage message = mNotifyInfoList.get(position).getMessage();

        // 获取好友服务
        NIMClient.getService(FriendService.class)
                .ackAddFriendRequest(account, agree)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        SystemMessageStatus status;

                        // 如果同意请求，设置状态为已通过，否则设置状态为已拒绝
                        if (agree) {
                            status = SystemMessageStatus.passed;
                        } else {
                            status = SystemMessageStatus.declined;
                        }

                        // 设置系统消息的状态
                        NIMClient.getService(SystemMessageService.class)
                                .setSystemMessageStatus(message.getMessageId(), status);

                        // 更新消息的状态
                        message.setStatus(status);

                        // 通知适配器数据已更改
                        mAdapter.notifyDataSetChanged();

                        // 标记已有变更
                        haveChange = true;
                    }

                    @Override
                    public void onFailed(int code) {
                        // 处理失败时显示消息
                        ToastUtils.showMessage(CheckNotifyListActivity.this, "处理失败,code:" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        // 处理异常时显示消息
                        ToastUtils.showMessage(CheckNotifyListActivity.this, "处理出错:" + exception.getMessage());
                    }
                });
    }
//    这段代码用于处理好友请求。具体流程包括接受请求、拒绝请求，更新系统消息的状态，并更新列表的显示。如果处理成功，则将系统消息的状态设置为已通过或已拒绝，然后通知适配器数据已更改，以反映更改的状态。如果处理失败或出现异常，会显示相应的提示消息。

    /**
     * 删除并忽略该条记录
     * @param position 所在位置
     */
    // 创建一个忽略请求的方法
    private void ignoreRequest(final int position) {
        // 创建一个警告对话框，询问用户是否删除该记录
        new AlertDialog.Builder(this)
                .setMessage("是否删除该条记录？")
                .setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取要忽略的系统消息
                        SystemMessage message = mNotifyInfoList.get(position).getMessage();

                        // 设置系统消息的状态为已忽略
                        NIMClient.getService(SystemMessageService.class)
                                .setSystemMessageStatus(message.getMessageId(),
                                        SystemMessageStatus.ignored);

                        // 从通知信息列表中移除该记录
                        mNotifyInfoList.remove(position);

                        // 通知适配器数据已更改
                        mAdapter.notifyDataSetChanged();

                        // 关闭对话框
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户选择取消，关闭对话框
                        dialog.dismiss();
                    }
                }).show();
    }
//    这段代码的目的是创建一个名为 ignoreRequest 的方法，用于处理忽略请求操作。当用户在应用中执行忽略请求操作时，会弹出一个对话框，询问用户是否确定删除该记录。如果用户选择"确定"，则会设置相应的系统消息状态为已忽略，从通知信息列表中移除该记录，并通知适配器数据已更改，最后关闭对话框。如果用户选择"取消"，则只是关闭对话框。这段代码使用 Android 的对话框（AlertDialog）来实现用户交互。

    // 显示用户帐户信息
    private void showAccountInfo(int position) {
        // 获取通知信息对象
        AddFriendNotify notifyInfo = mNotifyInfoList.get(position);

        // 创建一个意图用于跳转到FriendInfoActivity
        Intent intent = new Intent(this, FriendInfoActivity.class);

        // 将NimUserInfo对象作为额外数据传递给FriendInfoActivity
        intent.putExtra("NimUserInfo", notifyInfo.getUserInfo());

        // 检查是否是我的好友，根据情况设置不同的标志以在FriendInfoActivity中显示适当的内容
        if (notifyInfo.isMyFriend()) {
            intent.putExtra("FLAG", FriendInfoActivity.FLAG_SHOW_FRIEND);
        } else {
            intent.putExtra("FLAG", FriendInfoActivity.FLAG_ADD_FRIEND);
        }

        // 启动FriendInfoActivity以显示帐户信息
        startActivity(intent);
    }
//    这段代码用于显示用户的帐户信息。它首先获取了通知信息对象，然后创建了一个用于跳转到用户信息界面的意图。根据用户是否是你的好友，它设置了不同的标志，以便FriendInfoActivity知道应该显示哪种类型的信息。最后，通过启动意图来显示用户的帐户信息。

    @Override
    // 当 Activity 销毁时执行
    protected void onDestroy() {
        super.onDestroy();

        // 如果 haveChange 为 true，表示信息已被更改
        if (haveChange){
            haveChange = false;

            // 设置 Activity 结果为 RESULT_HAVE_CHANGE
            this.setResult(RESULT_HAVE_CHANGE);
        }
    }
//    这段代码是在 Android Activity 的 onDestroy 方法中，用于在 Activity 销毁时检查是否有信息被更改。如果 haveChange 变量为 true，表示信息已经发生更改，它将设置当前 Activity 的结果为 RESULT_HAVE_CHANGE，以便在其他地方可以根据这个结果来执行相应的操作。
}
