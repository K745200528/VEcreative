package com.ezreal.ezchat.activity;

import android.content.Intent;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ezreal.ezchat.R;
import com.ezreal.ezchat.http.NimClientHandle;
import com.ezreal.ezchat.http.OnRegisterListener;
import com.ezreal.ezchat.utils.Constant;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.suntek.commonlibrary.utils.SharedPreferencesUtil;
import com.suntek.commonlibrary.utils.TextUtils;
import com.suntek.commonlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 更新密码
 * Created by wudeng on 2019/02/19.
 */

// 创建一个类，用于处理密码修改的活动
public class ChangePassActivity extends BaseActivity {

    // 绑定布局文件中的编辑文本视图，用于输入账号、密码和确认密码
    @BindView(R.id.et_account)
    EditText mEtAccount; // 输入账号
    @BindView(R.id.et_pass_word)
    EditText mEtPass; // 输入密码
    @BindView(R.id.et_confirm_pass)
    EditText mEtConfirmPass; // 输入确认密码
//    这段代码定义了一个名为 ChangePassActivity 的类，它继承自 BaseActivity 类。该活动用于处理密码修改操作，其中包括输入账号、密码和确认密码。在布局文件中，这些输入字段使用 @BindView 注解与 XML 布局中的对应视图控件关联。这样，开发人员可以在活动中轻松访问这些输入字段并执行密码修改相关的操作。

    @Override
    // 当活动被创建时调用
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置状态栏颜色为应用蓝色
        setStatusBarColor(R.color.app_blue_color);

        // 将布局文件设置为该活动的内容视图
        setContentView(R.layout.activity_change_pass);

        // 设置标题栏标题为“更新密码”，显示返回按钮，隐藏右侧按钮
        setTitleBar("更新密码", true, false);

        // 使用ButterKnife绑定视图和控件
        ButterKnife.bind(this);
    }
//    这段代码是Android活动的onCreate方法。它的目的是在活动创建时进行一些初始化设置，包括设置状态栏颜色、加载布局、设置标题栏、以及使用ButterKnife库绑定视图和控件。这些步骤确保了在活动启动时界面的正确显示和用户界面的初始化。

    @OnClick(R.id.btn_update)
    public void register() {
        // 获取用户输入的帐号
        String account = mEtAccount.getText().toString().trim();

        // 获取用户输入的密码
        final String pass = mEtPass.getText().toString().trim();

        // 获取用户输入的确认密码
        String confirmPass = mEtConfirmPass.getText().toString().trim();

        // 检查确认密码是否为空或与密码不匹配
        if (TextUtils.isEmpty(confirmPass) || !confirmPass.equals(pass)) {
            // 显示提示消息并返回
            ToastUtils.showMessage(this, "确认密码为空或与密码不符");
            return;
        }

        // 调用NimClientHandle实例的updateToken方法进行注册
        NimClientHandle.getInstance().updateToken(account, pass, new OnRegisterListener() {
            @Override
            public void onSuccess() {
                // 注册成功后的处理
                ToastUtils.showMessage(ChangePassActivity.this, "更新成功");

                // 重新登录
                // 保存用户的新密码
                SharedPreferencesUtil.setStringSharedPreferences(ChangePassActivity.this,
                        Constant.LOCAL_LOGIN_TABLE, Constant.LOCAL_USER_TOKEN, pass);

                // 注销当前NIM服务
                NIMClient.getService(AuthService.class).logout();

                // 启动登录界面
                // 创建一个意图对象，用于从 ChangePassActivity 到 LoginActivity 的切换
                Intent intent = new Intent(ChangePassActivity.this, LoginActivity.class);

// 设置意图的标志，以便在启动 LoginActivity 后将 ChangePassActivity 清除出任务栈
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

// 启动 LoginActivity，关闭 ChangePassActivity
                startActivity(intent);
            }

            @Override
            public void onFailed(String message) {
                // 注册失败后的处理
                ToastUtils.showMessage(ChangePassActivity.this, "注册失败:" + message);
            }
        });
    }
//    这段代码用于进行用户注册操作。首先，它获取用户输入的帐号、密码以及确认密码。然后，它检查确认密码是否为空或与密码不匹配，如果不匹配就显示提示消息并返回。

    @OnClick(R.id.iv_back_btn)
    // 点击返回按钮的处理方法
    public void clickBack(){
        // 结束当前活动
        finish();
    }
//    这段代码定义了一个用于处理返回按钮点击事件的方法。在方法内部，调用finish()函数来结束当前活动，将用户返回到上一个活动或界面。通常，这个方法用于实现“返回”或“取消”按钮的功能，以退出当前页面或操作。
}
