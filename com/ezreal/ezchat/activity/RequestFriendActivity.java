package com.ezreal.ezchat.activity;

import android.os.Bundle;

import android.widget.EditText;

import androidx.annotation.Nullable;

import com.ezreal.ezchat.R;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.suntek.commonlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wudeng on 2017/8/29.
 */

// 创建 RequestFriendActivity 类，该类继承自 BaseActivity
public class RequestFriendActivity extends BaseActivity {

    // 声明一个 EditText 用于输入好友请求信息
    @BindView(R.id.et_request_msg)
    EditText mEtRequestMsg;

    // 创建一个请求好友的回调接口，用于处理请求结果
    private RequestCallback<Void> mRequestCallback;
//    这段代码定义了一个名为 RequestFriendActivity 的类，它是一个活动（Activity）并继承了 BaseActivity 类。在这个活动中，用户可以在 mEtRequestMsg 的 EditText 视图中输入好友请求信息。还声明了一个 mRequestCallback，用于处理请求好友操作的回调结果。这个活动可能涉及到发送好友请求并处理请求结果。

    // 覆盖父类的方法，在 Activity 创建时调用
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.app_blue_color);
        // 设置状态栏颜色
        setContentView(R.layout.activity_request_friend);
        // 设置当前 Activity 的布局文件
        setTitleBar("添加好友", true, false);
        // 设置标题栏的标题为 "添加好友"，显示返回按钮但不显示更多选项按钮
        ButterKnife.bind(this);
        // 使用 ButterKnife 绑定当前 Activity，以简化视图绑定操作
        initCallBack();
        // 初始化回调方法
    }
//    这段代码是在创建 Activity 时执行的方法。它完成了以下操作：
//
//    设置状态栏的颜色为蓝色。
//    设置当前 Activity 使用的布局文件为 "activity_request_friend"。
//    设置标题栏的标题为 "添加好友"，并显示返回按钮，但不显示更多选项按钮。
//    使用 ButterKnife 进行视图绑定，以方便后续对视图的操作。
//    调用 initCallBack() 方法，用于初始化回调方法或其他操作。

    // 初始化回调函数
    private void initCallBack() {
        mRequestCallback = new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                if (exception != null) {
                    // 如果出现异常，显示错误消息
                    ToastUtils.showMessage(RequestFriendActivity.this, "请求出错，请重试：" + exception.getMessage());
                } else {
                    if (code == 200) {
                        // 如果返回状态码为200，表示请求已成功
                        ToastUtils.showMessage(RequestFriendActivity.this, "请求已发出~");
                        finish();  // 关闭当前活动
                    } else {
                        // 如果返回状态码不是200，显示异常消息和状态码
                        ToastUtils.showMessage(RequestFriendActivity.this, "请求异常，请重试，异常代码：" + code);
                    }
                }
            }
        };
    }
//    这段代码用于初始化一个回调函数，这个回调函数用于处理发送好友请求的结果。如果请求出现异常，它将显示错误消息。如果请求成功（状态码为200），则显示请求成功的消息并关闭当前活动。如果请求不成功，它将显示异常消息和状态码。这个回调函数可以用于处理与服务器的通信结果，以便提供适当的用户反馈。

    // 注解，指示这个方法应该在R.id.tv_send_request视图被点击时执行
    @OnClick(R.id.tv_send_request)
    public void sendRequest() {
        // 从Intent中获取传递过来的"account"信息
        String account = getIntent().getStringExtra("account");

        // 初始化消息字符串
        String msg = "";

        // 检查输入的请求消息是否为空，如果不为空，去除首尾空格
        if (mEtRequestMsg.getText() != null) {
            msg = mEtRequestMsg.getText().toString().trim();
        }

        // 设置请求类型为"VERIFY_REQUEST"，用于向好友发送验证请求
        VerifyType type = VerifyType.VERIFY_REQUEST;

        // 使用NIMClient获取好友服务，并添加好友
        InvocationFuture<Void> addFriend = NIMClient.getService(FriendService.class)
                .addFriend(new AddFriendData(account, type, msg));

        // 设置请求回调处理
        addFriend.setCallback(mRequestCallback);
    }
//    这段代码用于处理用户点击发送好友请求按钮的事件。首先，它获取从Intent中传递的"account"信息，然后从输入框获取用户输入的请求消息。接下来，它设置请求类型为"VERIFY_REQUEST"，表示这是一个好友验证请求。然后，使用NIMClient获取好友服务，并创建一个添加好友的操作。最后，它设置请求的回调处理，用于处理添加好友请求的结果。

}
