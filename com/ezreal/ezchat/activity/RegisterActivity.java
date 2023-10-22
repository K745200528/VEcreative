package com.ezreal.ezchat.activity;

import android.os.Bundle;

import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ezreal.ezchat.R;
import com.ezreal.ezchat.http.NimClientHandle;
import com.ezreal.ezchat.http.OnRegisterListener;
import com.suntek.commonlibrary.utils.TextUtils;
import com.suntek.commonlibrary.utils.ToastUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册账户
 * Created by wudeng on 2017/8/24.
 */

public class RegisterActivity extends BaseActivity {
// 注册界面的 Activity，继承自基础 BaseActivity 类

    // 使用 ButterKnife 绑定 UI 元素到对应的变量上
    // 用注解 @BindView 标记并初始化一个名为 mEtAccount 的 EditText 视图
    @BindView(R.id.et_account)
    EditText mEtAccount;

    // 用注解 @BindView 标记并初始化一个名为 mEtName 的 EditText 视图
    @BindView(R.id.et_user_name)
    EditText mEtName;

    // 用注解 @BindView 标记并初始化一个名为 mEtPass 的 EditText 视图
    @BindView(R.id.et_pass_word)
    EditText mEtPass;

    // 用注解 @BindView 标记并初始化一个名为 mEtConfirmPass 的 EditText 视图
    @BindView(R.id.et_confirm_pass)
    EditText mEtConfirmPass;

//    这段代码定义了一个注册界面的Activity，通过ButterKnife库将EditText控件绑定到变量上，以便后续在代码中访问和操作这些UI元素。这些EditText分别用于输入账户、用户名、密码和确认密码的信息。

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在父类中执行相应操作

        setStatusBarColor(R.color.app_blue_color);
        // 设置状态栏颜色为应用蓝色

        setContentView(R.layout.activity_register);
        // 设置当前视图的内容为注册页面的布局

        setTitleBar("注册", true, false);
        // 设置标题栏，标题为 "注册"，具有返回按钮但没有保存按钮

        ButterKnife.bind(this);
        // 使用ButterKnife绑定视图与布局
    }
//    这段代码是在onCreate生命周期中的方法，用于配置和初始化活动。以下是对每一行代码的解释：
//
//    @Override: 这是Java注解，表示我们重写了父类的方法。
//    protected void onCreate(@Nullable Bundle savedInstanceState): 这是活动的onCreate方法的开始。它执行了父类的onCreate方法，传递了任何先前保存的状态数据（如用户界面的状态）。
//    setStatusBarColor(R.color.app_blue_color): 这个方法设置状态栏的颜色为应用的蓝色。
//    setContentView(R.layout.activity_register): 这一行代码将当前的视图内容设置为注册页面的布局，使用户可以看到和与该界面交互。
//    setTitleBar("注册", true, false): 这行代码用于设置标题栏的显示。标题被设置为 "注册"，具有返回按钮但没有保存按钮。
//            ButterKnife.bind(this): 这行代码使用ButterKnife库将当前活动的视图与布局相关联，以便可以轻松访问和操作视图中的元素。

    // 当注册按钮 (R.id.btn_register) 被点击时执行这个方法
    @OnClick(R.id.btn_register)
    public void register() {
        // 从相关的文本字段中获取输入的账号、名字、密码和确认密码
        String account = mEtAccount.getText().toString().trim();
        String name = mEtName.getText().toString().trim();
        String pass = mEtPass.getText().toString().trim();
        String confirmPass = mEtConfirmPass.getText().toString().trim();

        // 检查是否有字段为空
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(name) || TextUtils.isEmpty(pass)) {
            // 如果有字段为空，显示提示消息并结束方法
            Toast.makeText(this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查确认密码是否为空，且与密码是否匹配
        if (TextUtils.isEmpty(confirmPass) || !confirmPass.equals(pass)) {
            // 如果确认密码为空或与密码不匹配，显示相应的提示消息并结束方法
            ToastUtils.showMessage(this, "确认密码为空或与密码不符");
            return;
        }

        // 调用 NimClientHandle 的 register 方法进行用户注册
        NimClientHandle.getInstance().register(account, pass, name, new OnRegisterListener() {
            @Override
            public void onSuccess() {
                // 注册成功时显示成功消息，然后结束当前活动
                ToastUtils.showMessage(RegisterActivity.this, "注册成功");
                finish();
            }

            @Override
            public void onFailed(String message) {
                // 注册失败时显示失败消息，携带失败原因，然后结束当前活动
                ToastUtils.showMessage(RegisterActivity.this, "注册失败:" + message);
            }
        });
    }
//    这段代码是一个点击事件处理程序，当用户点击注册按钮时，它会从输入框中提取账号、名字、密码和确认密码，并检查它们的有效性。如果信息不完整或确认密码不匹配密码，它将显示相应的提示消息。如果信息有效，它将使用 NimClientHandle 中的 register 方法尝试注册用户，并根据注册结果显示成功或失败的消息。

    // 设置点击事件处理方法，当点击视图 R.id.iv_back_btn 时触发
    @OnClick(R.id.iv_back_btn)
    public void clickBack() {
        finish(); // 调用 finish() 方法来结束当前 Activity，实现返回上一个界面
    }
//    这段代码是通过 ButterKnife 库的注解 @OnClick 来为指定的视图（R.id.iv_back_btn）设置点击事件处理方法。当用户点击 iv_back_btn 视图时，clickBack 方法会被触发。在 clickBack 方法内部，finish() 方法被调用，用于结束当前的 Activity，实现返回上一个界面。

}
