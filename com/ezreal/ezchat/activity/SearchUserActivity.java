package com.ezreal.ezchat.activity;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.ezreal.ezchat.R;
import com.ezreal.ezchat.handler.NimUserHandler;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.suntek.commonlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 根据账户搜索用户
 * Created by wudeng on 2017/8/28.
 */

// 引入所需的类和库
public class SearchUserActivity extends BaseActivity {
    // 声明用户帐号输入框
    @BindView(R.id.et_user_account)
    EditText mEtUserAccount;

    // 声明用于标识此活动的 TAG
    private static final String TAG = SearchUserActivity.class.getSimpleName();

    // 声明用于处理请求回调的对象，该回调将返回用户信息列表
    private RequestCallback<List<NimUserInfo>> mRequestCallback;
//    这段代码是一个 Android 活动的声明，用于搜索用户。它包含了一个用户帐号输入框、一个 TAG 用于标识活动，以及一个用于处理请求回调的对象。此回调用于处理返回的用户信息列表。这些变量和对象用于与用户界面和后端数据进行交互，以实现用户搜索功能。

    // 重写Activity的onCreate方法
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 调用父类的onCreate方法
        super.onCreate(savedInstanceState);

        // 设置状态栏颜色
        setStatusBarColor(R.color.app_blue_color);

        // 设置当前Activity的布局
        setContentView(R.layout.activity_search_user);

        // 设置标题栏的文本为"添加朋友"，并显示返回按钮
        setTitleBar("添加朋友", true, false);

        // 使用ButterKnife绑定视图元素
        ButterKnife.bind(this);

        // 初始化回调方法
        initCallBack();
    }
//    这段代码是Android应用的Activity的onCreate方法。在这个方法中：
//
//            super.onCreate(savedInstanceState) 调用了父类onCreate方法，用于初始化Activity。
//
//    setStatusBarColor(R.color.app_blue_color) 设置状态栏的颜色。
//
//    setContentView(R.layout.activity_search_user) 设置当前Activity使用的布局文件。
//
//    setTitleBar("添加朋友", true, false) 设置标题栏的文本为"添加朋友"，并显示返回按钮。
//
//            ButterKnife.bind(this) 使用ButterKnife库绑定视图元素，这可以简化视图元素的操作。
//
//    initCallBack() 初始化回调方法，用于处理用户交互等操作。

    // 初始化请求回调接口
    private void initCallBack(){
        // 创建一个 RequestCallback 对象，参数为泛型类型为 List<NimUserInfo>
        mRequestCallback = new RequestCallback<List<NimUserInfo>>() {
            // 请求成功时触发的方法，参数是请求返回的数据列表
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                // 如果返回的数据列表为空
                if (param.isEmpty()){
                    // 显示提示消息，说明没有找到匹配的用户
                    ToastUtils.showMessage(SearchUserActivity.this,"查无此人喔，请检查后再试~~");
                    return;
                }
                // 创建一个意图对象，用于跳转到好友信息界面
                Intent intent = new Intent(SearchUserActivity.this,FriendInfoActivity.class);
                intent.putExtra("FLAG",FriendInfoActivity.FLAG_ADD_FRIEND);
                intent.putExtra("NimUserInfo",param.get(0)); // 将找到的用户信息传递给好友信息界面
                startActivity(intent); // 启动好友信息界面
            }

            // 请求失败时触发的方法，参数是失败的状态码
            @Override
            public void onFailed(int code) {
                // 显示提示消息，说明搜索失败并显示失败的状态码
                ToastUtils.showMessage(SearchUserActivity.this,"搜索失败，返回码：" + code);
            }

            // 请求发生异常时触发的方法，参数是抛出的异常
            @Override
            public void onException(Throwable exception) {
                // 显示提示消息，说明搜索出错并显示异常消息
                ToastUtils.showMessage(SearchUserActivity.this,"搜索出错：" + exception.getMessage());
            }
        };
    }
//    这段代码的主要目的是初始化一个请求回调接口对象 mRequestCallback，该接口定义了在请求成功、失败和发生异常时的处理逻辑。在请求成功时，它会跳转到好友信息界面并传递用户信息。如果没有找到匹配的用户，它会显示一条消息。在请求失败或发生异常时，它也会显示相应的消息。

    // 当搜索按钮（控件ID为R.id.iv_search）被点击时，执行搜索好友的操作
    @OnClick(R.id.iv_search)
    public void searchFriend() {
        // 从输入框获取用户输入的账号并去除首尾空格
        String account = mEtUserAccount.getText().toString().trim();

        // 检查账号是否为空
        if (TextUtils.isEmpty(account)) {
            // 如果为空，显示提示消息并返回
            ToastUtils.showMessage(SearchUserActivity.this, "账号不能为空");
            return;
        }

        // 检查用户是否尝试添加自己为好友
        if (account.equals(NimUserHandler.getInstance().getMyAccount())) {
            // 如果是自己，显示提示消息并返回
            ToastUtils.showMessage(SearchUserActivity.this, "不能添加自己为好友");
            return;
        }

        // 创建一个包含用户账号的列表
        List<String> accounts = new ArrayList<>(1);
        accounts.add(account);

        // 通过NIMClient获取UserService并执行fetchUserInfo方法来获取用户信息
        NIMClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(mRequestCallback);
    }
//    这段代码定义了一个方法，当搜索按钮被点击时，它会获取用户输入的账号，然后进行一系列检查，包括账号非空和不添加自己为好友。最后，它使用NIM SDK的UserService来获取用户信息，该操作可能涉及网络请求，并通过回调函数mRequestCallback来处理结果。

    // 当返回按钮图标 (R.id.iv_back_btn) 被点击时执行以下操作
    @OnClick(R.id.iv_back_btn)
    public void backOnClick(){
        // 结束当前活动 (Activity) 并返回上一个活动 (Activity)
        finish();
    }
//    这段代码是一个点击事件处理方法，当用户点击返回按钮图标时，它会调用 finish() 方法来结束当前的活动 (Activity) 并返回到上一个活动。这是一种常见的用于返回上一个屏幕的操作。

}
