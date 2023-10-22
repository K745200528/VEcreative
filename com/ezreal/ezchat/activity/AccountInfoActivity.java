package com.ezreal.ezchat.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.ezreal.ezchat.R;
import com.ezreal.ezchat.bean.LocalAccountBean;
import com.ezreal.ezchat.handler.NimUserHandler;
import com.ezreal.ezchat.utils.Constant;
import com.ezreal.timeselectview.CityPickerView;
import com.ezreal.timeselectview.TimePickerView;
import com.joooonho.SelectableRoundedImageView;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.suntek.commonlibrary.utils.ImageUtils;
import com.suntek.commonlibrary.utils.TextUtils;
import com.suntek.commonlibrary.utils.ToastUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * 账号信息详情页
 * Created by wudeng on 2017/8/31.
 */

public class AccountInfoActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    // 创建一个名为 AccountInfoActivity 的类，它扩展了 BaseActivity 类，并实现了 View.OnClickListener 和 View.OnTouchListener 接口。

    private static final String TAG = AccountInfoActivity.class.getSimpleName();
    // 创建一个名为 TAG 的常量字符串，用于在日志中标识当前 Activity 类的名称。

    @BindView(R.id.layout_head)
    RelativeLayout mLayoutHead;
    // 使用 ButterKnife 注解，将 layout_head 布局文件中的 RelativeLayout 绑定到 mLayoutHead 变量。这是用户头像的布局。

    @BindView(R.id.iv_head_picture)
    SelectableRoundedImageView mIvHead;
    // 使用 ButterKnife 注解，将 iv_head_picture 控件（ImageView）绑定到 mIvHead 变量。这是用户头像的 ImageView。

    @BindView(R.id.tv_account)
    TextView mTvAccount;
    // 使用 ButterKnife 注解，将 tv_account 控件（TextView）绑定到 mTvAccount 变量。这是用于显示用户账号的 TextView。

    @BindView(R.id.et_account_nick)
    EditText mEtNick;
    // 使用 ButterKnife 注解，将 et_account_nick 控件（EditText）绑定到 mEtNick 变量。这是用于输入用户昵称的 EditText。

    @BindView(R.id.tv_account_sex)
    TextView mTvSex;
    // 使用 ButterKnife 注解，将 tv_account_sex 控件（TextView）绑定到 mTvSex 变量。这是用于显示用户性别的 TextView。

    @BindView(R.id.tv_account_birth)
    TextView mTvBirthDay;
    // 使用 ButterKnife 注解，将 tv_account_birth 控件（TextView）绑定到 mTvBirthDay 变量。这是用于显示用户生日的 TextView。

    @BindView(R.id.tv_account_location)
    TextView mTvLocation;
    // 使用 ButterKnife 注解，将 tv_account_location 控件（TextView）绑定到 mTvLocation 变量。这是用于显示用户位置的 TextView。

    @BindView(R.id.et_account_signature)
    EditText mEtSignature;
    // 使用 ButterKnife 注解，将 et_account_signature 控件（EditText）绑定到 mEtSignature 变量。这是用于输入用户个性签名的 EditText。

    // 个人信息
    private LocalAccountBean mAccountBean;
// 创建一个名为 mAccountBean 的对象，类型为 LocalAccountBean，用于存储用户账户信息。

    // 头像本地路径
    private String mHeadImgPath = "";
// 创建一个名为 mHeadImgPath 的字符串变量，用于存储用户头像的本地文件路径。初始为空字符串。

    // 获取图像请求码
    private static final int SELECT_PHOTO = 30000;
    private static final int TAKE_PHOTO = 30001;
// 创建两个静态常量整数 SELECT_PHOTO 和 TAKE_PHOTO，分别用于表示选择照片和拍照的请求码。

    // 信息是否有被更新
    private boolean haveAccountChange = false;
// 创建一个名为 haveAccountChange 的布尔变量，用于表示用户信息是否有被更新。初始值为 false。

    // 是否处于编辑状态
    private boolean isEditor;
// 创建一个名为 isEditor 的布尔变量，用于表示用户是否处于编辑状态。

    // 输入服务，用于显示键盘
    private InputMethodManager mInputMethodManager;
// 创建一个名为 mInputMethodManager 的对象，类型为 InputMethodManager，用于管理输入方法和键盘的显示。

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 调用基类的 onCreate 方法，执行基本的 Activity 初始化。

        setStatusBarColor(R.color.app_blue_color);
        // 设置状态栏的颜色为应用的蓝色。这是一个自定义方法。

        setContentView(R.layout.activity_account_info);
        // 设置当前 Activity 使用的布局文件为 "activity_account_info.xml"。

        setTitleBar("个人信息", true, true);
        // 设置标题栏的标题为 "个人信息"，并启用返回按钮和保存按钮。这是一个自定义方法。

        ButterKnife.bind(this);
        // 使用 ButterKnife 库来绑定视图，将 XML 布局中的视图关联到对应的变量上。

        showData();
        // 调用 showData 方法，用于显示用户数据。

        init();
        // 调用 init 方法，用于初始化操作。
    }
//    这段代码位于 onCreate 方法中，用于在 Activity 创建时执行一系列初始化和设置操作，包括设置状态栏颜色、关联布局文件、设置标题栏、绑定视图、显示用户数据以及执行其他初始化操作。

    // 显示数据
    private void showData() {
        // 获取本地账户信息
        mAccountBean = NimUserHandler.getInstance().getLocalAccount();
// 获取本地账户信息
        // 检查是否获取到了本地账户信息
        if (mAccountBean != null) {
            // 检查是否获取到了本地账户信息
            ImageUtils.setImageByFile(this, mIvHead, mAccountBean.getHeadImgUrl(), R.mipmap.bg_img_defalut);
// 使用 ImageUtils 设置头像图片

            mTvAccount.setText(mAccountBean.getAccount());

            // 设置文本视图 mTvAccount 的文本为账户名
            mEtNick.setText(mAccountBean.getNick());
// 设置编辑框 mEtNick 的文本为用户昵称

            if (mAccountBean.getGenderEnum() == GenderEnum.FEMALE) {
                // 如果用户的性别是女性
                mTvSex.setText("女");
                // 设置文本视图 mTvSex 的文本为 "女"
            } else if (mAccountBean.getGenderEnum() == GenderEnum.MALE) {
                // 如果用户的性别是男性
                mTvSex.setText("男");
                // 设置文本视图 mTvSex 的文本为 "男"
            } else {
                // 如果用户的性别既不是女性也不是男性，通常表示性别未公开（保密）
                mTvSex.setText("保密");
                // 设置文本视图 mTvSex 的文本为 "保密"
            }
// 根据性别设置文本视图 mTvSex 的文本，可能为 "女"、"男" 或 "保密"


            mEtSignature.setText(mAccountBean.getSignature());

            // 设置编辑框 mEtSignature 的文本为用户签名
            String birthday = mAccountBean.getBirthDay();

            // 获取用户生日
            if (TextUtils.isEmpty(birthday)) {
                // 检查用户生日是否为空（null或空字符串）

                // 如果生日为空，将文本视图 mTvBirthDay 的文本设置为 "未设置"
                mTvBirthDay.setText("未设置");
            } else {
                // 如果生日不为空

                // 设置文本视图 mTvBirthDay 的文本为用户的生日
                mTvBirthDay.setText(birthday);
            }

            // 根据用户生日是否为空设置文本视图 mTvBirthDay 的文本
            String location = mAccountBean.getLocation();

            // 获取用户位置信息
            if (TextUtils.isEmpty(location)) {
                // 使用TextUtils.isEmpty检查位置信息是否为空
                // 如果位置信息为空，即用户没有设置位置信息
                mTvLocation.setText("未设置");
                // 在文本视图 mTvLocation 中显示文本 "未设置"，表示用户没有设置位置信息
            } else {
                // 如果位置信息不为空，即用户已设置位置信息
                mTvLocation.setText(location);
                // 在文本视图 mTvLocation 中显示用户设置的位置信息
            }

            // 根据用户位置信息是否为空设置文本视图 mTvLocation 的文本
        }
    }
//    这段代码用于在界面上显示用户的个人信息，包括头像、账户名、昵称、性别、签名、生日和位置信息。

    private void init() {
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // 获取输入法管理器的实例，用于控制键盘的显示和隐藏

        // 以下是对各个视图组件设置点击或触摸事件监听器，以响应用户的交互行为
        mLayoutHead.setOnClickListener(this);

        // 当点击 mLayoutHead 视图时，执行点击事件处理
        mTvSex.setOnClickListener(this);

        // 当点击 mTvSex 视图时，执行点击事件处理
        mTvBirthDay.setOnClickListener(this);

        // 当点击 mTvBirthDay 视图时，执行点击事件处理
        mTvLocation.setOnClickListener(this);

        // 当点击 mTvLocation 视图时，执行点击事件处理
        mIvBack.setOnClickListener(this);

        // 标题栏中的返回按钮，点击时执行点击事件处理
        mIvMenu.setOnClickListener(this);

        // 标题栏中的菜单按钮，点击时执行点击事件处理
        mEtNick.setOnTouchListener(this);

        // 当触摸 mEtNick 编辑框时，执行触摸事件处理
        mEtSignature.setOnTouchListener(this);

        // 当触摸 mEtSignature 编辑框时，执行触摸事件处理
        finishEdit();
        // 结束编辑，将界面初始化为非编辑状态
    }
//    这段代码的主要目的是初始化界面的各种交互操作，包括点击和触摸事件监听器的设置，以及初始化编辑状态。

    @Override
    public void onClick(View v) {
        // 根据点击的 View ID 执行不同的操作
        switch (v.getId()) {
            case R.id.layout_head:
                // 如果点击的是头像布局，执行设置头像操作
                setHeadImg();
                break;
            case R.id.tv_account_sex:
                // 如果点击的是性别文本视图，执行设置性别操作
                setSex();
                break;
            case R.id.tv_account_location:
                // 如果点击的是位置文本视图，执行设置位置操作
                setLocation();
                break;
            case R.id.tv_account_birth:
                // 如果点击的是生日文本视图，执行设置生日操作
                setBirthday();
                break;
            case R.id.iv_back_btn:
                // 如果点击的是返回按钮图标，结束当前界面
                this.finish();
                break;
            case R.id.iv_menu_btn:
                if (isEditor) {
                    // 如果处于编辑状态，执行结束编辑操作
                    finishEdit();
                } else {
                    // 如果不处于编辑状态，执行开始编辑操作
                    startEdit();
                }
                break;
        }
    }
//    这段代码是一个点击事件的监听器，它根据用户点击的视图的 ID 来执行不同的操作。例如，如果用户点击头像布局 (layout_head)，则执行设置头像 (setHeadImg) 的操作。同样，对于其他视图 ID，也会执行相应的操作，包括设置性别、位置、生日，结束编辑或开始编辑。这样可以根据用户的点击动作响应不同的界面交互需求。

    // EditText 获取焦点并将光标移动到末尾
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 检查是否处于编辑状态
        if (isEditor) {
            // 如果正在编辑
            // 根据触发事件的视图 ID 进行不同的处理
            if (v.getId() == R.id.et_account_nick) {
                // 如果触发事件的视图是昵称编辑框
                mEtNick.requestFocus();
                // 请求焦点，使昵称编辑框获取焦点
                mEtNick.setSelection(mEtNick.getText().length());
                // 将光标移到昵称文本的末尾
                mInputMethodManager.showSoftInput(mEtNick, 0);
                // 显示软键盘以允许用户输入昵称
            } else if (v.getId() == R.id.et_account_signature) {
                // 如果触发事件的视图是签名编辑框
                mEtSignature.requestFocus();
                // 请求焦点，使签名编辑框获取焦点
                mEtSignature.setSelection(mEtSignature.getText().length());
                // 将光标移到签名文本的末尾
                mInputMethodManager.showSoftInput(mEtSignature, 0);
                // 显示软键盘以允许用户输入签名
            }
            return true;
            // 返回 true 表示事件已处理
        }
        return false;
        // 如果不处于编辑状态，返回 false
    }
//    该方法的主要目的是处理触摸事件，以便在编辑状态下，当用户触摸昵称编辑框或签名编辑框时，请求焦点，设置光标位置，并显示软键盘以便用户编辑。如果不处于编辑状态，将返回 false，表示事件未被处理。
    /**
     * 启动编辑
     */
    private void startEdit() {
        mIvMenu.setImageResource(R.mipmap.done);
        // 设置菜单图标为 "完成" 图标
        // 可点击
        mLayoutHead.setClickable(true); // 点击头像部分
        mTvSex.setClickable(true);      // 点击性别信息
        mTvLocation.setClickable(true); // 点击位置信息
        mTvBirthDay.setClickable(true); // 点击生日信息
        // 可编辑
        mEtNick.setFocusable(true);             // 允许编辑昵称
        mEtNick.setFocusableInTouchMode(true);   // 允许在触摸模式下编辑昵称
        mEtSignature.setFocusable(true);        // 允许编辑签名
        mEtSignature.setFocusableInTouchMode(true); // 允许在触摸模式下编辑签名
        isEditor = true;
        // 将编辑状态标志设置为 true
    }
//    这段代码的目的是启用用户信息的编辑功能，它改变了一些 UI 元素的行为，使它们变得可点击和可编辑。具体地：
//
//            mIvMenu.setImageResource(R.mipmap.done); 设置右上角菜单图标为 "完成" 图标，表示编辑状态。
//            mLayoutHead.setClickable(true); 允许用户点击头像部分。
//            mTvSex.setClickable(true);, mTvLocation.setClickable(true);, mTvBirthDay.setClickable(true); 允许用户点击性别、位置、生日信息。
//            mEtNick.setFocusable(true);, mEtNick.setFocusableInTouchMode(true); 允许用户编辑昵称。
//            mEtSignature.setFocusable(true);, mEtSignature.setFocusableInTouchMode(true); 允许用户编辑签名。
//    最后，将 isEditor 标志设置为 true，表示已进入编辑状态。

    /**
     * 结束编辑，判断是否有修改，决定是否同步缓存数据
     */
    private void finishEdit() {
        // 检查用户昵称是否有更改
        if (!mEtNick.getText().toString().equals(mAccountBean.getNick())) {
            // 如果用户昵称发生更改，更新 mAccountBean 中的昵称字段
            mAccountBean.setNick(mEtNick.getText().toString());
            // 设置 haveAccountChange 为 true，表示账户信息已更改
            haveAccountChange = true;
        }

        // 检查用户签名是否有更改
        if (!mEtSignature.getText().toString().equals(mAccountBean.getSignature())) {
            // 如果用户签名发生更改，更新 mAccountBean 中的签名字段
            mAccountBean.setSignature(mEtSignature.getText().toString());
            // 设置 haveAccountChange 为 true，表示账户信息已更改
            haveAccountChange = true;
        }

        // 如果账户信息有更改
        if (haveAccountChange) {
            // 更新本地缓存中的用户账户信息
            NimUserHandler.getInstance().setLocalAccount(mAccountBean);
            // 通知 NimUserHandler 处理将更改同步到服务器
            NimUserHandler.getInstance().syncChange2Service();
            // 重置 haveAccountChange，以便将来的更改能够触发同步
            haveAccountChange = false;
        }

        mIvMenu.setImageResource(R.mipmap.editor);
// 设置一个图像资源给 ImageView mIvMenu，通常用于指示编辑操作。
        // 不可点击
        mLayoutHead.setClickable(false);
// 设置布局 mLayoutHead 为不可点击状态，这将禁用与该布局的交互。

        mTvSex.setClickable(false);
// 设置文本视图 mTvSex 为不可点击状态，禁止用户点击该视图。

        mTvLocation.setClickable(false);
// 设置文本视图 mTvLocation 为不可点击状态，禁止用户点击该视图。

        mTvBirthDay.setClickable(false);
// 设置文本视图 mTvBirthDay 为不可点击状态，禁止用户点击该视图。

        // 不可编辑
        mEtNick.setFocusable(false);
// 禁用编辑框 mEtNick 的焦点，使其不可编辑。

        mEtNick.setFocusableInTouchMode(false);
// 禁用编辑框 mEtNick 的触摸模式焦点，也使其不可编辑。

        mEtSignature.setFocusable(false);
// 禁用编辑框 mEtSignature 的焦点，使其不可编辑。

        mEtSignature.setFocusableInTouchMode(false);
// 禁用编辑框 mEtSignature 的触摸模式焦点，同样使其不可编辑。

        isEditor = false;
        // 将布尔变量 isEditor 设置为 false，表示不处于编辑状态。
    }
//    这段代码似乎用于禁用用户界面的编辑功能，即在用户信息显示状态下，用户不能编辑或点击一些元素。

    /**
     * 设置性别
     */
    private void setSex(){
        final int[] selected = new int[1];
        // 创建一个整型数组 selected，用于存储用户选择的性别索引

        if (mAccountBean.getGenderEnum() == GenderEnum.MALE) {
            selected[0] = 0;
        } else if (mAccountBean.getGenderEnum() == GenderEnum.FEMALE) {
            selected[0] = 1;
        } else {
            selected[0] = 2;
        }
        // 根据用户的性别设置 selected[0] 的值，0 表示男性，1 表示女性，2 表示保密

        final String[] items = new String[]{"男", "女", "保密"};
        // 创建字符串数组 items，包含了性别选项 "男"、"女" 和 "保密"

        new AlertDialog.Builder(this)
                // 创建一个新的 AlertDialog.Builder 对话框
                .setTitle("性别")
                // 设置对话框的标题为 "性别"
                .setSingleChoiceItems(items, selected[0], new DialogInterface.OnClickListener() {
                    // 使用单选列表项构建对话框，选择项由 items 数组提供
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 当用户在对话框中做出选择后执行的点击事件

                        if (which != selected[0]) {
                            // 检查用户是否做出了与之前选择不同的选择

                            if (which == 0) {
                                // 如果用户选择的是第一个选项，代表性别为男性
                                mAccountBean.setGenderEnum(GenderEnum.MALE);
                                // 设置用户的性别为男性
                                mTvSex.setText("男");
                                // 更新性别显示在界面上
                            } else if (which == 1) {
                                // 如果用户选择的是第二个选项，代表性别为女性
                                mAccountBean.setGenderEnum(GenderEnum.FEMALE);
                                // 设置用户的性别为女性
                                mTvSex.setText("女");
                                // 更新性别显示在界面上
                            } else {
                                // 用户选择的是其他选项，代表性别为保密
                                mAccountBean.setGenderEnum(GenderEnum.UNKNOWN);
                                // 设置用户的性别为保密
                                mTvSex.setText("保密");
                                // 更新性别显示在界面上
                            }
                            // 用户更新了个人信息，将标志位设为 true
                            haveAccountChange = true;
                        }
                        dialog.dismiss();
                        // 关闭对话框
                    }
//                    这段代码用于允许用户选择性别，通过创建一个警告对话框，用户可以从 "男"、"女" 和 "保密" 中选择一个选项。选择后，会根据用户的选择更新性别信息，并标记用户信息已被修改。

                    /**
     * 设置生日
     */private void setBirthday() {
         // 创建一个视图用于选择生日日期
         View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_birthday, null);

         // 创建一个对话框以显示生日选择视图
         final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

         // 获取日期选择器视图
         TimePickerView timePickerView = (TimePickerView) view.findViewById(R.id.date_picker);

         // 为日期选择器设置日期选中监听器
         timePickerView.setSelectedListener(new TimePickerView.OnDateSelectedListener() {
             @Override
             public void selectedDate(int year, int month, int day) {
                 // 将年、月、日转换为字符串
                 String yearString = String.valueOf(year);
                 String monthString = String.valueOf(month);
                 String dayString = String.valueOf(day);

                 // 根据需要，在月份和日期前添加零以确保格式正确
                 if (monthString.length() == 1){
                     monthString = "0" + monthString;
                 }
                 if (dayString.length() == 1){
                     dayString = "0" + dayString;
                 }

                 // 创建格式化的生日字符串
                 String birthday = String.format("%s-%s-%s", yearString, monthString, dayString);
// 检查用户选择的生日是否与当前显示的生日文本不同
                 if (!birthday.equals(mTvBirthDay.getText().toString())) {
                     // 如果不同，将用户的生日信息设置为新的生日
                     mAccountBean.setBirthDay(birthday);

                     // 更新界面上显示的生日文本
                     mTvBirthDay.setText(birthday);

                     // 设置标志以指示用户信息已更改
                     haveAccountChange = true;
                 }

                 // 检查生日是否与之前的不同，如果是则更新数据
                 if (!birthday.equals(mTvBirthDay.getText().toString())) {
                     mAccountBean.setBirthDay(birthday);
                     mTvBirthDay.setText(birthday);
                     haveAccountChange = true;
                 }

                 // 隐藏生日选择对话框
                 dialog.dismiss();
             }
         });

         // 显示生日选择对话框
         dialog.show();
     }
//                    这段代码的目的是创建一个视图以供用户选择生日日期，当用户在生日文本视图上单击时显示对话框。用户选择日期后，将日期格式化为字符串并与先前选择的日期进行比较，如果不同则更新用户的生日信息。最后，隐藏对话框。

    /**
     * 设置地区
     */
    private void setLocation(){
        // 创建一个视图用于选择用户所在的地理位置
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_location, null);

// 创建一个对话框以显示地理位置选择视图
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

// 获取城市选择器视图
        CityPickerView cityPickerView = (CityPickerView) view.findViewById(R.id.city_picker);

// 为城市选择器设置城市选中监听器
        cityPickerView.setCitySelectedListener(new CityPickerView.OnCitySelectedListener() {
            @Override
            public void citySelected(String province, String city) {
                // 将选中的省份和城市合并为地理位置字符串
                String location = province + "/" + city;

                // 检查地理位置是否与之前的不同，如果是则更新用户的地理位置信息
                // 获取用户输入的位置信息并存储在变量 "location" 中
                if (!location.equals(mTvLocation.getText().toString())) {
                    // 检查用户输入的位置信息是否与之前存储的不同
                    // 如果不同，执行以下操作：

                    // 1. 更新用户账户数据中的位置信息
                    mAccountBean.setLocation(location);

                    // 2. 更新显示用户位置信息的 TextView，将新的位置信息显示在界面上
                    mTvLocation.setText(location);

                    // 3. 标记用户信息已更改（将 "haveAccountChange" 设置为 true）
                    haveAccountChange = true;
                }


                // 隐藏地理位置选择对话框
                dialog.dismiss();
            }
        });

// 显示地理位置选择对话框
        dialog.show();
    }
//                    这段代码的目的是创建一个视图，用于选择用户所在的地理位置，当用户在地理位置文本视图上单击时显示对话框。用户选择省份和城市后，将它们合并为地理位置字符串，并与先前选择的地理位置进行比较，如果不同则更新用户的地理位置信息。最后，隐藏对话框。

    /**
     * 设置头像，拍照或选择照片
     */
    private void setHeadImg() {
        // 创建一个视图以供用户选择头像设置方式
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_head_img, null);

// 创建一个警报对话框以显示头像设置方式选择视图
        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();

// 获取“拍照”文本视图
        TextView take = (TextView) view.findViewById(R.id.tv_take_photo);

// 获取“选择图片”文本视图
        TextView select = (TextView) view.findViewById(R.id.tv_select_img);

// 为“拍照”选项设置单击监听器
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭头像设置方式选择对话框
                alertDialog.dismiss();

                try {
                    // 启动相机应用以拍摄照片
                    // 创建一个意图，用于启动相机应用
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

// 设置图像的保存路径，使用用户账号作为文件名
                    mHeadImgPath = Constant.APP_CACHE_PATH + File.separator + "image"
                            + File.separator + mAccountBean.getAccount() + ".jpg";

// 创建一个 URI 对象，指向图像文件
                    Uri uri = Uri.fromFile(new File(mHeadImgPath));

// 将 URI 作为额外输出传递给相机应用，以便它将拍摄的照片保存到指定路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);


                    // 启动相机活动以拍摄照片
                    startActivityForResult(intent, TAKE_PHOTO);
                } catch (Exception e) {
                    // 捕获可能的异常，并显示错误消息
                    ToastUtils.showMessage(AccountInfoActivity.this, "启动相机出错！请重试");
                    e.printStackTrace();
                }
            }
        });

// 为“选择图片”选项设置单击监听器
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭头像设置方式选择对话框
                alertDialog.dismiss();

                // 启动图像选择器以选择头像图片
                // 创建一个意图，用于从媒体存储中选择图片
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

// 设置意图类型为所有图片类型
                intent.setType("image/*");

// 启动带有选择器的意图，使用户能够选择图片
// "选择头像图片" 是选择器的标题
// SELECT_PHOTO 是请求码，用于识别选择图片操作
                startActivityForResult(Intent.createChooser(intent, "选择头像图片"), SELECT_PHOTO);
            }
        });

// 显示头像设置方式选择对话框
        alertDialog.show();
    }
//   这段代码的目的是创建一个对话框，以供用户选择设置头像的方式，包括拍照和从相册中选择。根据用户的选择，启动相应的操作（拍照或选择图片）。


    @Override
    // 当 Activity 接收到从其他 Activity 返回的结果时触发此方法
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 检查返回结果是否成功
        if (resultCode == RESULT_OK) {

            // 根据请求码判断是拍照还是选择照片
            if (requestCode == TAKE_PHOTO) {
                // 如果是拍照请求，处理拍照结果
                dealTakePhotoResult();
            } else if (requestCode == SELECT_PHOTO) {
                // 如果是选择照片请求，获取选中照片的本地路径并处理结果
                // 获取头像图片的文件路径
                mHeadImgPath = ImageUtils.getFilePathFromUri(AccountInfoActivity.this, data.getData());

            // 调用处理拍照结果的方法
                dealTakePhotoResult();

            }
        }
    }
//   这段代码处理在 AccountInfoActivity 中拍照或选择照片后的返回结果。首先，检查返回的结果是否成功（resultCode == RESULT_OK）。然后，根据请求码（requestCode）来确定是拍照请求（TAKE_PHOTO）还是选择照片请求（SELECT_PHOTO）。根据不同的请求码，调用相应的处理方法。如果是选择照片请求，还会获取选中照片的本地路径。

                    /**
     * 处理拍照回传数据
     */
     // 处理拍照结果
     private void dealTakePhotoResult() {
         // 使用RxJava Flowable创建一个数据流，将拍照的图片路径传递给下一个步骤
         Flowable.just(mHeadImgPath)
                 .map(new Function<String, Bitmap>() {
                     @Override
                     public Bitmap apply(String path) throws Exception {
                         // 调整旋转角度，压缩

                         // 获取图像的旋转角度
                         int bitmapDegree = ImageUtils.getBitmapDegree(mHeadImgPath);

                         // 从文件中获取位图，限制宽度为600像素，高度为400像素
                         Bitmap bitmap = ImageUtils.getBitmapFromFile(mHeadImgPath, 600, 400);

                         // 根据旋转角度旋转位图
                         bitmap = ImageUtils.rotateBitmapByDegree(bitmap, bitmapDegree);

                         // 保存位图为JPEG格式
                         ImageUtils.saveBitmap2Jpg(bitmap, path);

                         return bitmap;
                     }
                 })
                 .subscribeOn(Schedulers.io()) // 在I/O线程中执行
                 .observeOn(AndroidSchedulers.mainThread()) // 在主线程中观察结果
                 .subscribe(new Consumer<Bitmap>() {
                     @Override
                     public void accept(Bitmap bitmap) throws Exception {
                         // 显示，记录更新，同步至网易云服务器

                         if (bitmap != null) {
                             // 如果位图不为空，上传至服务器
                             uploadHeadImg(bitmap);
                         }
                     }
                 });
     }
//  这段代码用于处理拍摄照片后的结果。首先，它创建一个Flowable，该Flowable包含拍照后保存的图像文件的路径。然后，使用map操作执行以下操作：
//  获取图像的旋转角度，以便在处理之前校正图像的方向。
//  从文件中获取图像，并限制其宽度为600像素，高度为400像素。
//  根据旋转角度旋转图像。
//  保存图像为JPEG格式。
//  接下来，使用RxJava的线程调度功能：
//      subscribeOn(Schedulers.io()): 在I/O线程中执行上述处理。
//      observeOn(AndroidSchedulers.mainThread()): 在主线程中观察处理结果。
//  最后，使用Consumer接口观察结果。如果处理后的图像不为空，它将调用uploadHeadImg(bitmap)方法将图像上传到服务器。
    /**
     * 将头像数据上传至网易云服务器存储，获取服务器返回URL
     */
    // 上传用户头像
    private void uploadHeadImg(final Bitmap bitmap) {
        // 使用 NIMClient 获取 NosService 服务，用于上传头像
        AbortableFuture<String> upload = NIMClient.getService(NosService.class)
                .upload(new File(mHeadImgPath), "image/ipeg");

        // 设置回调函数来处理上传结果
        upload.setCallback(new RequestCallback() {
            @Override
            public void onSuccess(Object param) {
                Log.e(TAG, "uploadHeadImg onSuccess url = " + param.toString());
                mIvHead.setImageBitmap(bitmap);
                // 将新的头像设置到 ImageView 中
                // 保存图片本地路径和服务器路径
                mAccountBean.setHeadImgUrl(param.toString());
                // 将新头像的服务器路径存储到用户信息中
                haveAccountChange = true;
            }

            @Override
            public void onFailed(int code) {
                Log.e(TAG, "uploadHeadImg onFailed code " + code);
                ToastUtils.showMessage(AccountInfoActivity.this,
                        "修改失败，头像上传失败，code:" + code);
                // 如果上传失败，显示错误消息
            }

            @Override
            public void onException(Throwable exception) {
                Log.e(TAG, "uploadHeadImg onException message " + exception.getMessage());
                ToastUtils.showMessage(AccountInfoActivity.this,
                        "修改失败,图像上传出错:" + exception.getMessage());
                // 如果发生异常，显示异常消息
            }
        });
    }
//   这段代码的目的是上传用户的头像图像。它使用 NIM SDK 中的 NosService 服务来执行上传操作。上传成功后，将新的头像设置到 ImageView 中，并将新头像的服务器路径保存到用户信息中，以便后续使用。如果上传失败或出现异常，将显示相应的错误消息。
}
