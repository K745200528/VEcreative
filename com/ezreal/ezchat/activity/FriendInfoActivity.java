package com.ezreal.ezchat.activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ezreal.ezchat.R;
import com.joooonho.SelectableRoundedImageView;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.suntek.commonlibrary.utils.ImageUtils;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wudeng on 2017/8/28.
 */

public class FriendInfoActivity extends BaseActivity {

    // FriendInfoActivity 类，负责显示朋友的详细信息

    // 常量，标识添加朋友
    public static final int FLAG_ADD_FRIEND = 10001;

    // 常量，标识展示朋友
    public static final int FLAG_SHOW_FRIEND = 10002;

    // 使用 ButterKnife 注解的控件，用于显示朋友信息的头像
    @BindView(R.id.iv_head_picture)
    SelectableRoundedImageView mHeadImg;

    // 使用 ButterKnife 注解的控件，用于显示朋友的性别图标
    @BindView(R.id.iv_person_sex)
    ImageView mIvPersonSex;

    // 使用 ButterKnife 注解的控件，用于显示朋友的备注
    @BindView(R.id.tv_remark)
    TextView mTvRemark;

    // 使用 ButterKnife 注解的控件，用于显示朋友的账号
    @BindView(R.id.tv_account)
    TextView mTvAccount;

    // 使用 ButterKnife 注解的控件，用于显示朋友的昵称
    @BindView(R.id.tv_nike)
    TextView mTvNike;

    // 使用 ButterKnife 注解的控件，用于将朋友添加到通讯录
    @BindView(R.id.tv_add_to_contract)
    TextView mTvAdd2Contract;

    // 使用 ButterKnife 注解的控件，用于开始聊天
    @BindView(R.id.tv_start_chat)
    TextView mTvStartChat;

    // 使用 ButterKnife 注解的控件，用于发起视频聊天
    @BindView(R.id.tv_video_chat)
    TextView mTvVideoChat;
//    这段代码定义了 FriendInfoActivity 类，该类用于显示朋友的详细信息。它包含了一些常量标识以及一些使用 ButterKnife 注解的控件，这些控件用于显示朋友的头像、性别、备注、账号、昵称以及一些操作按钮，如将朋友添加到通讯录、开始聊天和发起视频聊天等。

    // 创建一个 NimUserInfo 对象用于存储用户信息
    private NimUserInfo mNimUserInfo;

    // 在 Activity 创建时调用的方法
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.app_blue_color); // 设置状态栏颜色
        setContentView(R.layout.activity_friend_info); // 设置界面布局
        setTitleBar("详细资料", true, true); // 设置标题栏的标题为"详细资料"，并显示返回按钮和更多操作按钮
        ButterKnife.bind(this); // 使用 ButterKnife 绑定视图元素
        bindViewByIntent(); // 通过 Intent 绑定视图
    }
//    这段代码是在一个 Activity 中的 onCreate 方法中，用于初始化用户界面。它包括设置状态栏颜色、设置布局文件、设置标题栏的标题并显示返回按钮和更多操作按钮，然后使用 ButterKnife 绑定视图元素。最后，它调用 bindViewByIntent 方法，该方法可能用于通过 Intent 传递的数据来绑定视图的具体内容。


    private void bindViewByIntent() {
        // 通过意图获取传递的数据
        Intent intent = getIntent();

// 获取传递的标志值，默认为FLAG_ADD_FRIEND
        int flag = intent.getIntExtra("FLAG", FLAG_ADD_FRIEND);

// 根据标志值选择不同的操作
        if (flag == FLAG_ADD_FRIEND) {
            // 如果标志是添加好友，则显示添加到通讯录按钮，隐藏开始聊天和视频聊天按钮
            mTvAdd2Contract.setVisibility(View.VISIBLE);
            mTvStartChat.setVisibility(View.GONE);
            mTvVideoChat.setVisibility(View.GONE);
        } else if (flag == FLAG_SHOW_FRIEND) {
            // 如果标志是显示好友，则隐藏添加到通讯录按钮，显示开始聊天和视频聊天按钮
            mTvAdd2Contract.setVisibility(View.GONE);
            mTvStartChat.setVisibility(View.VISIBLE);
            mTvVideoChat.setVisibility(View.VISIBLE);
        }

// 通过意图获取传递的NimUserInfo对象
        mNimUserInfo = (NimUserInfo) intent.getSerializableExtra("NimUserInfo");

// 如果NimUserInfo对象不为空，更新界面数据
        if (mNimUserInfo != null) {
            // 设置头像
            ImageUtils.setImageByUrl(this, mHeadImg, mNimUserInfo.getAvatar(), R.mipmap.app_logo);

            // 根据性别设置性别图标
            if (mNimUserInfo.getGenderEnum() == GenderEnum.FEMALE) {
                mIvPersonSex.setImageResource(R.mipmap.ic_woman);
            } else if (mNimUserInfo.getGenderEnum() == GenderEnum.MALE) {
                mIvPersonSex.setImageResource(R.mipmap.ic_man);
            }

            // 设置账号
            mTvAccount.setText(mNimUserInfo.getAccount());

            // 设置昵称
            mTvNike.setText(mNimUserInfo.getName());

            // 设置备注
            String remark = mNimUserInfo.getName();
            Map<String, Object> extensionMap = mNimUserInfo.getExtensionMap();
            if (extensionMap != null && extensionMap.containsKey("remark")) {
                remark = extensionMap.get("remark").toString();
            }
            mTvRemark.setText(remark);
        }

    }
//    这段代码主要处理了两个方面的功能：
//
//    通过检查从意图中传递的标志值来决定在界面上显示哪些按钮。如果标志是FLAG_ADD_FRIEND，显示“添加到通讯录”按钮并隐藏“开始聊天”和“视频聊天”按钮。如果标志是FLAG_SHOW_FRIEND，隐藏“添加到通讯录”按钮并显示“开始聊天”和“视频聊天”按钮。

//    通过意图获取传递的NimUserInfo对象，该对象包含有关用户的信息，包括头像、性别、账号、昵称和备注。根据这些信息，更新界面上的相关视图。如果用户具有备注信息，则显示备注，否则显示昵称。

    /**
     * 设置备注信息
     */
    // 用注解 @OnClick 表明这是一个点击事件的处理方法，指定了 R.id.tv_set_remark 表明这个方法会在对应的视图被点击时触发。
    public void setRemark() {
        // 此方法是在用户点击对应视图时触发的事件处理方法。它的用途应该是设置或编辑备注信息。
        // 但在提供的代码中，方法体是空的，没有包含具体的设置备注的逻辑。
        // 在实际应用中，你需要添加代码来处理设置备注的逻辑，例如弹出对话框或切换到备注编辑界面。
    }
//    这段代码使用了 ButterKnife 注解库中的 @OnClick 注解，它将 setRemark 方法与一个具体的视图（在这里是 R.id.tv_set_remark）关联起来。当用户点击与此注解关联的视图时，setRemark 方法将被调用，执行其中定义的操作。在这个示例中，setRemark 方法还没有包含具体的操作逻辑，因此需要根据你的需求来添加相应的操作。通常，这种方法用于处理用户界面的点击事件。

    /**
     * 添加好友
     */
    // 当用户点击 R.id.tv_add_to_contract 视图时触发的方法
    @OnClick(R.id.tv_add_to_contract)
    public void add2Contract() {
        // 创建一个新的意图，用于启动 RequestFriendActivity 类
        Intent intent = new Intent(this, RequestFriendActivity.class);

        // 将当前用户的账户信息作为额外数据添加到意图中
        intent.putExtra("account", mNimUserInfo.getAccount());

        // 启动 RequestFriendActivity
        startActivity(intent);
    }
//    这段代码是一个点击事件处理方法，当用户点击 R.id.tv_add_to_contract 视图时触发。它创建一个意图以启动 RequestFriendActivity 类，并将当前用户的账户信息作为额外数据添加到意图中，然后启动新的活动。这通常用于添加用户到联系人列表。

    /**
     * 跳转至聊天界面
     */
    // 注解，指示下面的方法响应视图上 R.id.tv_start_chat 控件的点击事件
    @OnClick(R.id.tv_start_chat)
    public void startChat() {
        // 创建一个意图，用于启动 P2PChatActivity 类
        Intent intent = new Intent(this, P2PChatActivity.class);

        // 将 NimUserInfo 对象作为额外数据添加到意图中，以便在聊天界面中使用
        intent.putExtra("NimUserInfo", mNimUserInfo);

        // 启动 ChatActivity
        startActivity(intent);
    }
//    这段代码是一个点击事件处理方法，当用户单击具有 R.id.tv_start_chat 控件的视图时，将触发此方法。该方法首先创建一个意图，以启动 P2PChatActivity 类。然后，它将包含 NimUserInfo 对象的数据添加到意图中，以便在聊天界面中使用。最后，它启动 ChatActivity，将用户导航到聊天界面。

    /**
     * 跳转到视频聊天界面
     */
    // 当用户点击视图上的 "tv_video_chat" 元素时触发的方法
    @OnClick(R.id.tv_video_chat)
    public void startVideoChat() {

    }

    // 当用户点击视图上的 "iv_back_btn" 元素时触发的方法
    @OnClick(R.id.iv_back_btn)
    public void backClick() {
        // 关闭当前活动（Activity）
        this.finish();
    }
//    这段代码使用了 ButterKnife 库，@OnClick 注解用于给指定的视图元素设置点击事件监听器。当用户点击 "tv_video_chat" 元素时，startVideoChat() 方法将被调用，但此处方法为空，没有具体操作。同样，当用户点击 "iv_back_btn" 元素时，backClick() 方法将被调用，这个方法会关闭当前的活动。
}