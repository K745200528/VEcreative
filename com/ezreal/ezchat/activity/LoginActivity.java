package com.ezreal.ezchat.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ezreal.ezchat.ChatApplication;
import com.ezreal.ezchat.R;
import com.ezreal.ezchat.handler.NimUserHandler;
import com.ezreal.ezchat.http.NimClientHandle;
import com.ezreal.ezchat.http.OnRegisterListener;
import com.ezreal.ezchat.utils.Constant;
import com.ezreal.ezchat.utils.ConvertUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.suntek.commonlibrary.utils.PermissionUtils;
import com.suntek.commonlibrary.utils.SharedPreferencesUtil;
import com.suntek.commonlibrary.utils.SystemUtils;
import com.suntek.commonlibrary.utils.TextUtils;
import com.suntek.commonlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wudeng on 2017/8/24.
 */

// 创建一个名为LoginActivity的类，继承自BaseActivity
public class LoginActivity extends BaseActivity {

    // 使用ButterKnife注解绑定的布局元素
    // 使用ButterKnife库的注解，将XML布局中的UI元素与对应的Java变量进行绑定

    // 绑定登录界面的布局RelativeLayout
    @BindView(R.id.layout_login)
    RelativeLayout mLayoutLogin;

    // 绑定注册界面的布局RelativeLayout
    @BindView(R.id.layout_register)
    RelativeLayout mLayoutRegister;

    // 绑定登录界面的账户输入框EditText
    @BindView(R.id.et_user_account)
    EditText mEtLoginAccount;

    // 绑定登录界面的密码输入框EditText
    @BindView(R.id.et_pass_word)
    EditText mEtLoginPassword;

    // 绑定注册界面的注册按钮FloatingActionButton
    @BindView(R.id.fab_register)
    FloatingActionButton mBtnRegister;

    // 绑定注册界面的账户输入框EditText
    @BindView(R.id.et_register_account)
    EditText mEtRegisterAccount;

    // 绑定注册界面的名称输入框EditText
    @BindView(R.id.et_register_name)
    EditText mEtRegisterName;

    // 绑定注册界面的密码输入框EditText
    @BindView(R.id.et_register_pass)
    EditText mEtRegisterPass;

    // 绑定注册界面的确认密码输入框EditText
    @BindView(R.id.et_register_confirm_pass)
    EditText mEtConfirmPass;

//    这段代码定义了一个名为LoginActivity的类，它是一个Android活动。在该活动中，使用ButterKnife库的@BindView注解来绑定XML布局文件中的视图元素。这些视图元素包括登录和注册的布局容器、用户账号和密码输入框、注册按钮、以及注册时的账号、姓名、密码和确认密码输入框。这些绑定的视图元素将在后续的代码中用于用户界面的交互和数据输入。

    // 创建一个包含基本权限的字符串数组
    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            // 写入外部存储的权限，用于保存或读取文件
            Manifest.permission.CAMERA,
            // 相机权限，用于拍摄照片或录制视频
            Manifest.permission.READ_PHONE_STATE,
            // 读取电话状态的权限，用于识别设备的电话号码
            Manifest.permission.RECORD_AUDIO,
            // 录音权限，用于录制声音或语音
            Manifest.permission.ACCESS_COARSE_LOCATION,
            // 粗略定位权限，用于获取大致位置信息
            Manifest.permission.ACCESS_FINE_LOCATION
            // 精确定位权限，用于获取精确位置信息
    };
//    这段代码定义了一个包含基本权限的字符串数组。这些权限用于控制应用程序的不同功能，例如读写存储、使用相机、访问电话状态、录音以及定位。你可以使用这些权限数组来请求用户授予应用所需的权限，以确保应用能够正常运行。这些权限字符串对应于AndroidManifest.xml文件中的权限声明，允许或拒绝这些权限将影响应用的功能。
// 定义一个权限请求码
private static final int PERMISSION_REQUEST_CODE = 100001;

    // 声明一个登录任务的可中止未来对象
    private AbortableFuture<LoginInfo> mLoginFuture;

    // 声明一个标志，指示用户是否已登录
    private static boolean isLogin = false;

    // 记录按下返回键的次数
    private static int mKeyBackCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置该活动的内容视图为指定的布局
        setContentView(R.layout.activity_login);

        // 使用ButterKnife绑定视图元素
        ButterKnife.bind(this);

        // 为登录密码编辑框设置触摸监听器
        mEtLoginPassword.setOnTouchListener(new MyTouchListener());

        // 初始化权限
        initPermission();

        // 从本地存储中获取用户的帐户和令牌，并在对应的编辑框中填充
        // 从 SharedPreferences 中获取用户 ID
        String id = SharedPreferencesUtil.getStringSharedPreferences(this, Constant.LOCAL_LOGIN_TABLE, Constant.LOCAL_USER_ACCOUNT);

// 从 SharedPreferences 中获取用户令牌（Token）
        String token = SharedPreferencesUtil.getStringSharedPreferences(this, Constant.LOCAL_LOGIN_TABLE, Constant.LOCAL_USER_TOKEN);

// 检查是否成功获取用户 ID
        if (!TextUtils.isEmpty(id)) {
            // 如果 ID 不为空，将其设置为登录账号的文本
            mEtLoginAccount.setText(id);

            // 进一步检查是否成功获取用户令牌
            if (!TextUtils.isEmpty(token)) {
                // 如果令牌不为空，将其设置为登录密码的文本
                mEtLoginPassword.setText(token);
            }
        }

    }
//    这段代码中包括了一些关于活动初始化、权限请求、本地存储和视图绑定的内容。它们的主要目的是初始化登录活动的UI，包括用户帐户和密码的填充，设置权限请求代码，以及初始化一个用于登录的未来对象和一些标志。还设置了一个触摸监听器，以便用户可以点击登录密码编辑框。

    /**
     * 检查，申请权限
     */
    // 初始化权限请求
    private void initPermission() {
        // 检查当前 Android 版本是否大于等于 Marshmallow (Android 6.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 使用工具方法检查是否已经获得所需的权限
            boolean has = PermissionUtils.checkPermissions(this, BASIC_PERMISSIONS);

            // 如果没有获得权限
            if (!has) {
                // 请求所需的权限
                PermissionUtils.requestPermissions(this, PERMISSION_REQUEST_CODE, BASIC_PERMISSIONS);
            }
        }
    }
//    这段代码的主要作用是在 Android 6.0（Marshmallow）及更高版本中检查应用是否已经获得了一组基本权限。如果没有获得这些权限，它会请求用户授权这些权限。这是 Android 运行时权限处理的一种方式，确保应用在需要敏感权限时可以请求并获得这些权限以执行特定操作。

    /**
     * 跳转到权限设置页面返回后再次检查
     */
    // 当Activity重新启动时，该方法会被调用
    @Override
    protected void onRestart() {
        super.onRestart();
        initPermission();
    }
//    这段代码是Android中的Activity生命周期方法，它会在Activity从"暂停"状态重新变为"运行"状态时被调用。在这里，我们调用了initPermission方法。
//
//            super.onRestart();是用来调用父类的onRestart方法，以确保执行了父类的必要操作。
//
//    initPermission()是一个用于初始化权限的自定义方法。在Activity重新启动时，可能需要重新检查和请求一些需要的权限。这个方法的具体内容和实现可能需要根据你的应用的需求来编写。

    /**
     * 权限授予结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 检查请求码是否为所需的权限请求码
        if (requestCode == PERMISSION_REQUEST_CODE) {

            // 调用 PermissionUtils 类中的方法处理权限请求结果
            PermissionUtils.dealPermissionResult(LoginActivity.this, permissions, grantResults,
                    new PermissionUtils.RequestPermissionCallBack() {
                        @Override
                        public void onGrant(String... permissions) {
                            // 处理已授予的权限，可以在这里编写相关逻辑
                        }

                        @Override
                        public void onDenied(String... permissions) {
                            // 处理被拒绝的权限，可以在这里编写相关逻辑
                        }

                        @Override
                        public void onDeniedAndNeverAsk(String... permissions) {
                            // 处理被拒绝并选择不再询问的权限，可以在这里编写相关逻辑
                        }
                    });
        }
    }
//    这段代码是在 Android 权限请求的回调方法中，用于处理用户对权限请求的响应。它首先检查请求码是否与所需的权限请求码相匹配，然后调用 PermissionUtils.dealPermissionResult 方法来处理权限请求结果。根据用户的响应（授予、拒绝或拒绝且不再询问），你可以在相应的回调方法中编写相关逻辑来处理权限请求结果。


    @OnClick(R.id.tv_btn_login)
    public void login() {
        // 获取输入的账号和密码
        String account = mEtLoginAccount.getText().toString().trim();
        String pass = mEtLoginPassword.getText().toString().trim();

        // 检查是否输入为空的情况
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pass)) {
            ToastUtils.showMessage(this, "账号或密码为空~");
            return;
        }

        // 创建登录请求的回调函数
        RequestCallback<LoginInfo> callBack = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                isLogin = false;
                // 保存登录信息
                saveLoginInfo(loginInfo);
                // 转入主页面
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onFailed(int code) {
                // 标记登录状态为失败
                isLogin = false;

                // 显示登录失败的消息，包括错误码转换为人类可读的文本
                ToastUtils.showMessage(LoginActivity.this, "登录失败：" + ConvertUtils.code2String(code));
            }

            @Override
            public void onException(Throwable exception) {
                // 标记用户没有成功登录
                isLogin = false;

                // 显示登录出错信息
                ToastUtils.showMessage(LoginActivity.this, "登录出错：" + exception.getMessage());
            }
        };

        // 创建登录信息对象
        LoginInfo loginInfo = new LoginInfo(account, pass);

        // 发起登录请求
        mLoginFuture = NIMClient.getService(AuthService.class).login(loginInfo);
        isLogin = true;
        mLoginFuture.setCallback(callBack);
    }
//    这段代码实现了登录按钮的点击事件处理。它首先获取用户在账号和密码输入框中输入的内容。然后，它检查是否有输入为空的情况。如果输入为空，会显示一个提示消息。
//
//    接下来，代码创建一个登录请求的回调函数，该函数处理登录成功、登录失败和登录异常的情况。在成功登录后，它会保存登录信息，并跳转到主页面。
//
//    最后，它创建一个 LoginInfo 对象，包含用户输入的账号和密码，并使用 NIM SDK 的 AuthService 发起登录请求。在登录请求发起后，代码设置 isLogin 为 true，表示正在登录，并将回调函数设置给登录请求。

    @OnClick(R.id.btn_register)
    public void register(){
        // 在方法内部获取用户在输入框中输入的帐户、姓名、密码和确认密码
        String account = mEtRegisterAccount.getText().toString().trim();
        String name = mEtRegisterName.getText().toString().trim();
        String pass = mEtRegisterPass.getText().toString().trim();
        String confirmPass = mEtConfirmPass.getText().toString().trim();

// 检查输入是否为空，如果为空则显示提示信息并返回
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(name) || TextUtils.isEmpty(pass)){
            Toast.makeText(this,"请将信息填写完整",Toast.LENGTH_SHORT).show();
            return;
        }

// 检查确认密码是否为空或与密码不符，如果是则显示提示信息并返回
        if (TextUtils.isEmpty(confirmPass) || !confirmPass.equals(pass)){
            ToastUtils.showMessage(this,"确认密码为空或与密码不符");
            return;
        }

// 调用 NimClientHandle 的 register 方法进行注册
        NimClientHandle.getInstance().register(account, pass, name, new OnRegisterListener() {
            @Override
            public void onSuccess() {
                // 注册成功时显示成功提示，设置登录帐户和密码，并隐藏注册布局
                ToastUtils.showMessage(LoginActivity.this,"注册成功");
                mEtLoginAccount.setText(account);
                mEtLoginPassword.setText(pass);
                hindRegisterLayout();
            }

            @Override
            public void onFailed(String message) {
                // 注册失败时显示失败提示信息
                ToastUtils.showMessage(LoginActivity.this,"注册失败:" + message);
            }
        });
    }
//    这段代码用于处理用户注册操作。首先，它从输入框中获取用户输入的帐户、姓名、密码和确认密码。然后，它检查这些输入是否为空以及确认密码是否与密码匹配。如果检测到任何问题，它会显示相应的提示信息并返回。如果输入有效，它将调用 NimClientHandle 的 register 方法进行注册。注册成功后，它会显示成功提示，设置登录帐户和密码，并隐藏注册布局。如果注册失败，它会显示失败提示信息。

    @OnClick(R.id.fab_register)
    public void openRegisterLayout() {

        // 按钮移动到屏幕中央
        int[] origin = getCenterCoord(mBtnRegister);

        // 计算按钮需要移动到屏幕中央的距离
        // 获取设备屏幕宽度和高度的一半，用于计算屏幕中心
        int[] distance = {
                getResources().getDisplayMetrics().widthPixels / 2, // 将屏幕宽度除以2，得到水平中心位置
                getResources().getDisplayMetrics().heightPixels / 2 // 将屏幕高度除以2，得到垂直中心位置
        };

// 计算X轴和Y轴上的位移（以像素为单位）
        int changeX = distance[0] - origin[0]; // 计算水平位移，即中心位置减去原始位置的X坐标
        int changeY = distance[1] - origin[1]; // 计算垂直位移，即中心位置减去原始位置的Y坐标


        // 创建X轴平移动画
        ObjectAnimator translationX = ObjectAnimator.ofFloat(mBtnRegister, "translationX", 0, changeX);

        // 创建Y轴平移动画
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mBtnRegister, "translationY", 0, changeY);

        // 创建透明度动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBtnRegister, "alpha", 1, 0.5f);

        // 创建动画集合，将上述动画组合在一起
        // 创建一个动画集合，用于组合多个动画
        AnimatorSet animatorSet = new AnimatorSet();

// 设置动画集合的持续时间，这里为300毫秒
        animatorSet.setDuration(300);

// 设置动画集合的插值器，这里使用加速减速插值器，让动画速度在开始和结束时减速
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

// 将多个动画同时播放在一起
// 'translationX'、'translationY'、'alpha' 是之前创建的动画对象
        animatorSet.play(translationX).with(translationY).with(alpha);


        // 监听动画的开始和结束事件
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // 动画开始时执行的操作
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束时执行的操作
                showRegisterLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // 动画被取消时执行的操作
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // 动画重复时执行的操作
            }
        });

        // 启动动画集合
        animatorSet.start();
    }
//    这段代码的作用是创建一个开场动画，将注册按钮移动到屏幕中央并逐渐减小按钮的透明度，然后在动画结束后显示注册界面。

    // 显示注册布局
    // 显示注册布局的方法
    private void showRegisterLayout() {

        // 计算注册布局的对角线长度
        double hypot = Math.hypot(mLayoutRegister.getWidth(), mLayoutRegister.getHeight);

        // 计算注册布局中心的屏幕坐标
        int[] center = {
                getResources().getDisplayMetrics().widthPixels / 2,
                getResources().getDisplayMetrics().heightPixels / 2
        };

        // 创建圆形揭示动画
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(mLayoutRegister,
                center[0], center[1], 0, (int) hypot);

        // 添加动画监听器
        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // 动画开始时，将注册布局设置为可见
                mLayoutRegister.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束时的操作
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // 动画取消时的操作
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // 动画重复时的操作
            }
        });

        // 设置动画持续时间
        circularReveal.setDuration(300);

        // 设置动画的插值器，这里使用加速减速插值器
        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());

        // 启动动画
        circularReveal.start();
    }
//    这段代码的目的是在用户点击注册按钮时以圆形揭示的方式显示注册布局。它通过计算注册布局的对角线长度和屏幕中心的坐标来创建揭示动画。在动画开始时，将注册布局设置为可见，以便用户能够看到它。然后，设置动画的持续时间和插值器，并启动动画。

    @OnClick(R.id.fab_close)
    // 取消注册的操作
    public void cancelRegister() {
        // 调用隐藏注册布局的方法
        hindRegisterLayout();
    }
//    这段代码用于取消注册操作。在 cancelRegister 方法内部，它调用了 hindRegisterLayout 方法来隐藏注册布局，从而取消了注册。

    private void hindRegisterLayout(){

        // 清空数据
        mEtRegisterAccount.getText().clear();
        mEtRegisterName.getText().clear();
        mEtRegisterPass.getText().clear();
        mEtConfirmPass.getText().clear();

        // 计算半径，以便执行视图隐藏动画
        double hypot = Math.hypot(mLayoutRegister.getWidth(), mLayoutRegister.getHeight);

        // 获取屏幕中心坐标
        int[] center = {getResources().getDisplayMetrics().widthPixels / 2,
                getResources().getDisplayMetrics().heightPixels / 2};

        // 创建视图展开动画
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(mLayoutRegister,
                center[0], center[1], (int) hypot, 0);

        // 设置视图隐藏动画的监听器
        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // 动画开始时执行的操作
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束时执行的操作

                // 隐藏注册布局
                mLayoutRegister.setVisibility(View.INVISIBLE);
                // 重置注册按钮状态
                resetRegisterButton();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // 动画被取消时执行的操作
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // 动画重复时执行的操作
            }
        });

        // 设置动画持续时间
        circularReveal.setDuration(300);
        // 设置动画的插值器，这里使用线性插值器
        circularReveal.setInterpolator(new LinearInterpolator());
        // 启动动画
        circularReveal.start();
    }
//    这段代码的目的是隐藏注册表单的视图，通过执行一个圆形揭示动画（circular reveal animation）实现。该动画从一个指定半径开始逐渐展开，最终覆盖整个视图，从而隐藏了注册表单。动画结束后，重置了注册按钮的状态。

    private void resetRegisterButton() {
        // 创建水平方向平移动画，将注册按钮返回到初始位置
        ObjectAnimator translationX = ObjectAnimator.ofFloat(mBtnRegister, "translationX",
                mBtnRegister.getTranslationX(), 0);

// 创建垂直方向平移动画，将注册按钮返回到初始位置
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mBtnRegister, "translationY",
                mBtnRegister.getTranslationY(), 0);

// 创建透明度变化动画，将注册按钮逐渐恢复到不透明
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBtnRegister, "alpha", 0.5f, 1);

// 创建一个动画集合，包含上述三个动画
        AnimatorSet animatorSet = new AnimatorSet();

// 设置动画的持续时间为300毫秒
        animatorSet.setDuration(300);

// 设置动画的插值器，这里使用线性插值器
        animatorSet.setInterpolator(new LinearInterpolator());

// 同时播放平移和透明度动画
        animatorSet.play(translationX).with(translationY).with(alpha);

// 为动画集合添加监听器，监听动画的不同阶段
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // 动画开始时执行的操作
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束时执行的操作
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // 动画被取消时执行的操作
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // 动画重复播放时执行的操作
            }
        });

// 启动动画集合
        animatorSet.start();
    }
//    这段代码的目的是创建一个动画效果，将注册按钮恢复到原始位置，并使其透明度从0.5逐渐变为1。它使用了属性动画框架，包括水平和垂直平移动画以及透明度变化动画。最后，通过监听器来处理动画的各个阶段，例如动画开始、结束、取消和重复。

    private int[] getCenterCoord(View view) {
        // 创建一个整数数组 leftTop 来保存左上角坐标
        int[] leftTop = {-1, -1};

// 获取视图在窗口中的左上角坐标
        view.getLocationInWindow(leftTop);

// 计算视图中心的 X 坐标
        int centerX = view.getWidth() / 2 + leftTop[0];

// 计算视图中心的 Y 坐标
        int centerY = view.getHeight() / 2 + leftTop[1];

// 返回一个包含中心坐标的整数数组
        return new int[]{centerX, centerY};
    }
//    这段代码的目的是获取给定视图的中心坐标。首先，它创建一个名为 leftTop 的整数数组来存储视图的左上角坐标。然后，使用 getLocationInWindow() 方法获取视图在窗口中的左上角坐标。接下来，通过将视图的宽度和高度除以2，计算出视图中心的 X 和 Y 坐标。最后，它将这两个坐标存储在一个整数数组中并返回。这可以用于定位视图的中心点，通常用于实现与触摸事件或动画相关的功能。

    @Override
    // 当按下硬件按键时触发该方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 检测是否按下的是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 如果当前处于登录状态
            if (isLogin) {
                // 终止登录的 Future（异步任务）
                mLoginFuture.abort();
            } else {
                // 如果不处于登录状态，执行返回操作检查
                backDoubleExit();
            }
            // 返回 true，表示已处理按键事件
            return true;
        }
        // 如果按下的不是返回键，使用默认的按键处理方法
        return super.onKeyDown(keyCode, event);
    }
//    这段代码在 Android 应用中，用于处理用户按下硬件返回键的操作。如果用户按下返回键，它首先检查当前是否处于登录状态。如果是，它会终止登录的 Future，通常用于取消正在进行的登录操作。如果不处于登录状态，它会执行一个双击返回操作（backDoubleExit），用于确认用户是否要退出应用。最后，无论是终止登录还是执行返回操作，该方法会返回 true，表示已经处理了按键事件。如果按下的不是返回键，它会使用默认的按键处理方法。

    private void backDoubleExit() {
        // 用户按下返回键的次数的跟踪器，用于实现双击返回退出应用的功能
        mKeyBackCount++;

// 检查按下返回键的次数
        if (mKeyBackCount == 1) {
            // 如果是第一次按下，显示一个提示消息
            ToastUtils.showMessage(LoginActivity.this, "再点一次退出程序~~");
        } else if (mKeyBackCount == 2) {
            // 如果是第二次按下，调用 ChatApplication 的 AppExit 方法来退出应用
            ChatApplication.getInstance().AppExit();
        }
    }
//    这段代码用于在用户双击返回键时退出应用。它通过追踪用户按下返回键的次数来实现这一功能。如果用户第一次按下返回键，将显示一个提示消息告诉用户再次按下返回键以退出应用。如果用户第二次按下返回键，将调用 ChatApplication 的 AppExit 方法来退出应用。

    // 保存登录信息到SharedPreferences中
    private void saveLoginInfo(LoginInfo info) {
        // 使用SharedPreferencesUtil将账户信息保存到本地
        SharedPreferencesUtil.setStringSharedPreferences(this, Constant.LOCAL_LOGIN_TABLE,
                Constant.LOCAL_USER_ACCOUNT, info.getAccount());
        // 使用SharedPreferencesUtil将令牌信息保存到本地
        SharedPreferencesUtil.setStringSharedPreferences(this, Constant.LOCAL_LOGIN_TABLE,
                Constant.LOCAL_USER_TOKEN, info.getToken());
        // 设置NimUserHandler实例的当前账户
        NimUserHandler.getInstance().setMyAccount(info.getAccount());
    }
//    这段代码用于保存用户的登录信息，包括账户名和令牌，以便在应用程序的其他部分进行使用。它使用SharedPreferencesUtil工具类将这些信息保存到本地的SharedPreferences中，以便稍后检索和使用。最后，它通过NimUserHandler设置当前用户的账户信息。

    // 创建一个自定义的触摸监听器类，实现了View.OnTouchListener接口
    private class MyTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // 检测触摸事件是否是抬起动作
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.post(() -> {
                    // 获取键盘的高度
                    int keyBoardHeight = SystemUtils.getKeyBoardHeight(LoginActivity.this);
                    if (keyBoardHeight != 0) {
                        // 如果键盘高度不为零，将键盘高度存储在共享首选项中
                        SharedPreferencesUtil.setIntSharedPreferences(LoginActivity.this,
                                Constant.OPTION_TABLE, Constant.OPTION_KEYBOARD_HEIGHT, keyBoardHeight);
                    }
                });
            }
            // 执行点击事件，以确保正确处理点击操作
            v.performClick();
            // 返回 false，以允许事件继续传递
            return false;
        }
    }
//    这段代码创建了一个自定义的触摸监听器类，用于处理与键盘高度相关的逻辑。当用户在屏幕上抬起手指时（ACTION_UP事件），它会获取键盘的高度并将其存储在共享首选项中。这通常用于保存键盘高度以便在应用中正确调整界面布局。

}
