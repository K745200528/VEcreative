package com.ezreal.ezchat.widget;

import android.app.Activity;
import android.content.Context;

import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ezreal.audiorecordbutton.AudioRecordButton;
import com.ezreal.emojilibrary.EmojiBean;
import com.ezreal.emojilibrary.EmojiUtils;
import com.ezreal.emojilibrary.ExpressLayout;
import com.ezreal.ezchat.R;
import com.ezreal.ezchat.utils.Constant;
import com.suntek.commonlibrary.utils.SharedPreferencesUtil;
import com.suntek.commonlibrary.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * 聊天界面 输入相关组合布局
 * Created by wudeng on 2017/11/2.
 */

// ChatInputLayout 是一个自定义的布局类，用于处理聊天输入界面的各种元素。

public class ChatInputLayout extends RelativeLayout {
    // 使用 ButterKnife 框架，@BindView 注解用于绑定视图控件。

    // 输入类型图标
    @BindView(R.id.iv_input_type)
    ImageView mIvInputType;
    // 聊天消息输入框
    @BindView(R.id.et_chat_message)
    EditText mEtInput;
    // 录音按钮
    @BindView(R.id.btn_audio_record)
    AudioRecordButton mBtnAudioRecord;
    // 表情图标
    @BindView(R.id.iv_expression)
    ImageView mIvExpress;
    // 更多操作图标
    @BindView(R.id.iv_more)
    ImageView mIvMore;
    // 发送按钮
    @BindView(R.id.tv_btn_send)
    TextView mBtnSend;

    // 扩展布局
    @BindView(R.id.layout_extension)
    RelativeLayout mExtensionLayout;
    // 表情布局
    @BindView(R.id.layout_express)
    ExpressLayout mExpressLayout;
//    这段代码定义了一个自定义的聊天输入布局 ChatInputLayout，包括了聊天输入界面的各种元素。使用 ButterKnife 框架的 @BindView 注解将视图控件与 Java 代码绑定在一起，以便在代码中进行操作和监听。这些元素包括输入类型图标、文本输入框、录音按钮、表情图标、更多操作图标、发送按钮，以及扩展布局和表情布局。通过这些元素，用户可以在聊天应用中输入文本消息、发送语音消息、切换输入类型、浏览表情、打开更多操作选项等。

    // 输入法管理器
    private InputMethodManager mInputManager;
    // 输入布局监听器
    private OnInputLayoutListener mLayoutListener;
    // 当前活动（Activity）
    private Activity mActivity;
    // 内容视图
    private View mContentView;
//    这段代码定义了一些变量，它们用于在 Android 应用程序中处理输入法的显示和隐藏。让我们逐行解释它们的用途：
//
//    mInputManager 是一个 InputMethodManager 类型的变量，它用于管理输入法的显示和隐藏。
//
//    mLayoutListener 是一个自定义的监听器，它允许你监听输入法的显示和隐藏事件以执行特定的操作。
//
//    mActivity 是当前所在的活动（Activity）的引用，通常用于在活动中执行与输入法相关的操作。
//
//    mContentView 是活动的内容视图（View），它表示活动的主要界面布局。这可以用于在输入法弹出时调整界面的布局以防止被遮挡。
//
//    这些变量通常用于在 Android 应用中管理输入法的显示和隐藏，以及对界面布局进行必要的调整。

    // 构造函数，用于创建 ChatInputLayout 的实例
    public ChatInputLayout(Context context) {
        // 调用带一个参数的构造函数，传递上下文对象
        this(context, null);
    }

    // 构造函数，用于创建 ChatInputLayout 的实例
    public ChatInputLayout(Context context, @Nullable AttributeSet attrs) {
        // 调用带两个参数的构造函数，传递上下文对象和属性集合
        this(context, attrs, 0);
    }
//    这两个构造函数用于创建 ChatInputLayout 的实例。构造函数是类的一部分，用于在创建对象时初始化对象的状态。在这两个构造函数中，第一个构造函数调用第二个构造函数，第二个构造函数再调用第三个构造函数。
//
//    ChatInputLayout(Context context): 这是一个构造函数，接受一个 Context 类型的参数。Context 是 Android 应用程序的全局信息接口，它提供了应用程序的全局资源和服务的访问能力。这个构造函数通常用于创建一个 ChatInputLayout 的实例，但不传递任何属性。
//
//    ChatInputLayout(Context context, @Nullable AttributeSet attrs): 这也是一个构造函数，接受两个参数。第一个参数是 Context，第二个参数是 AttributeSet。AttributeSet 包含了一个视图的属性集合，这些属性可以在 XML 布局文件中定义。这个构造函数通常用于创建 ChatInputLayout 的实例，并考虑到可能的属性设置。
//
//    这些构造函数的主要目的是允许在不同的情况下创建 ChatInputLayout 实例，以满足不同的需求。这种构造函数重载允许您使用不同的参数来创建对象。

    // 构造函数，初始化 ChatInputLayout
    public ChatInputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 通过布局文件填充当前的 ChatInputLayout
        View root = LayoutInflater.from(context).inflate(R.layout.layout_chat_input, this, true);

        // 使用 ButterKnife 绑定视图，将 XML 布局与 Java 代码关联
        ButterKnife.bind(root);

        // 获取输入法管理器的实例，用于控制键盘的显示和隐藏
        mInputManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);

        // 初始化监听器，用于响应用户的输入和操作
        initListener();
    }
//    这段代码是 ChatInputLayout 类的构造函数。它用于初始化 ChatInputLayout，包括以下步骤：
//
//    调用父类的构造函数，传递了上下文、属性集合以及样式参数。
//    使用布局填充器（LayoutInflater）从布局文件（R.layout.layout_chat_input）中填充 ChatInputLayout 的内容，并将其添加到当前的 ChatInputLayout。
//    使用 ButterKnife 进行视图绑定，这样可以方便地访问 XML 布局中的视图。
//    获取系统的输入法管理器（InputMethodManager）实例，这个管理器用于控制键盘的显示和隐藏。
//    调用 initListener() 方法，用于初始化各种监听器，以响应用户的输入和操作。

    /**
     * 根据Activity和内容区域，设置隐藏显示时的控件高度
     */
    // 绑定输入布局
    // 绑定输入布局
    public void bindInputLayout(Activity activity, View contentView) {
        mActivity = activity;  // 将传入的 Activity 对象赋值给类的 mActivity 成员变量，以便后续使用。
        mContentView = contentView;  // 将传入的 View 对象赋值给类的 mContentView 成员变量，用于处理输入布局的显示和隐藏。

    // 从 SharedPreferences 中获取保存的键盘高度，默认为 787 像素
        int keyboardHeight = SharedPreferencesUtil.getIntSharedPreferences(activity, Constant.OPTION_TABLE, Constant.OPTION_KEYBOARD_HEIGHT);
        if (keyboardHeight == 0) {
            keyboardHeight = 787; // 默认键盘高度
        }

        // 获取表情布局的布局参数
        ViewGroup.LayoutParams layoutParams = mExpressLayout.getLayoutParams();

// 设置表情布局的高度为键盘高度
        layoutParams.height = keyboardHeight;

// 应用设置后的布局参数到表情布局
        mExpressLayout.setLayoutParams(layoutParams);

        // 获取扩展布局的布局参数
        layoutParams = mExtensionLayout.getLayoutParams();

// 设置扩展布局的高度为键盘的高度
        layoutParams.height = keyboardHeight;

// 应用更新后的布局参数，以改变扩展布局的高度
        mExtensionLayout.setLayoutParams(layoutParams);

    }
//    这段代码用于绑定输入布局，根据保存在SharedPreferences中的键盘高度，动态设置表情布局（mExpressLayout）和扩展布局（mExtensionLayout）的高度，以适应键盘的高度。如果没有保存的键盘高度信息，则使用默认高度 787 像素。

    private void initListener() {
        // 文本输入框触摸监听
        mEtInput.setOnTouchListener(new MyOnTouchListener());

        // 初始化音频录制按钮
        mBtnAudioRecord.init(Constant.APP_CACHE_AUDIO);

// 设置音频录制按钮的录制监听器
        mBtnAudioRecord.setRecordingListener(new AudioRecordButton.OnRecordingListener() {
            @Override
            public void recordFinish(String audioFilePath, long recordTime) {
                if (mLayoutListener != null) {
                    // 当录制完成时，调用布局监听器的音频录制完成方法，传递音频文件路径和录制时长
                    mLayoutListener.audioRecordFinish(audioFilePath, recordTime);
                }
            }

            @Override
            public void recordError(String message) {
                if (mLayoutListener != null) {
                    // 当录制出现错误时，调用布局监听器的音频录制错误方法，传递错误信息
                    mLayoutListener.audioRecordError(message);
                }
            }
        });
//        这段代码的目的是初始化音频录制按钮，并设置录制监听器。mBtnAudioRecord 是一个音频录制按钮对象，通过 init 方法初始化，并指定音频文件缓存路径。然后，通过 setRecordingListener 方法设置了一个录制监听器，该监听器在录制完成和出现错误时执行相应的回调操作。如果 mLayoutListener 不为空，它将调用布局监听器的方法，将音频文件路径、录制时长或错误信息传递给相应的回调方法。这通常用于处理音频录制的结果和错误信息。

        // 文本输入框输入监听
        // 添加文本变化监听器
        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 当文本变化前的回调，可以在文本变化前执行一些操作
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当文本正在变化时的回调
                if (s.toString().length() > 0) {
                    // 如果输入文本的长度大于 0
                    mIvMore.setVisibility(View.GONE);
                    // 隐藏 "更多" 图标
                    mBtnSend.setVisibility(View.VISIBLE);
                    // 显示发送按钮
                } else {
                    // 如果输入文本的长度为 0
                    mIvMore.setVisibility(View.VISIBLE);
                    // 显示 "更多" 图标
                    mBtnSend.setVisibility(View.GONE);
                    // 隐藏发送按钮
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 当文本变化后的回调，可以在文本变化后执行一些操作
            }
        });
//        这段代码的目的是在mEtInput（一个文本输入框）中添加一个文本变化监听器。当用户在输入框中输入文字时，该监听器会监测文本的变化。根据文本的长度，它会在 "更多" 图标和发送按钮之间进行切换可见性。如果文本长度大于 0，"更多" 图标将被隐藏，发送按钮将显示；如果文本长度为 0，"更多" 图标将显示，发送按钮将被隐藏。这有助于用户交互，根据输入情况动态显示或隐藏相关控件。

        mExpressLayout.setOnExpressSelListener(new ExpressLayout.OnExpressSelListener() {
            @Override
            public void onEmojiSelect(EmojiBean emojiBean) {
                // 当用户选择了表情时触发的回调

                // 获取当前光标位置
                int curPosition = mEtInput.getSelectionStart();
                // 创建一个字符串构建器，用于修改输入框中的文本
                StringBuilder sb = new StringBuilder(mEtInput.getText().toString());
                // 在光标位置插入选中的表情文本
                sb.insert(curPosition, emojiBean.getEmojiName());

                // 特殊文字处理，将表情等转换为可显示的图片
                SpannableString spannableString = EmojiUtils.text2Emoji(getContext(),
                        sb.toString(), mEtInput.getTextSize());
                mEtInput.setText(spannableString);

                // 将光标设置到新增完表情的右侧
                mEtInput.setSelection(curPosition + emojiBean.getEmojiName().length());
            }

            @Override
            public void onEmojiDelete() {
                // 当用户点击删除表情按钮时触发的回调

                // 调用系统的删除操作，模拟删除键按下
                mEtInput.dispatchKeyEvent(new KeyEvent(
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            }
        });
//        这段代码用于处理表情选择与删除操作。setOnExpressSelListener 方法设置了一个表情选择监听器，当用户点击表情时，会触发 onEmojiSelect 方法，将选中的表情插入到输入框中，并处理文本中的特殊字符，将其转换为表情图片。当用户点击删除表情按钮时，会触发 onEmojiDelete 方法，模拟按下删除键，实现删除操作。这个监听器用于处理用户与表情输入框的交互操作。
    }

    @Override
//    这个注解表示该方法是覆盖父类方法的。
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        这是 onSizeChanged 方法的签名，用于监测视图大小的变化。
        super.onSizeChanged(w, h, oldw, oldh);
//        这行代码调用了父类（View的父类）的 onSizeChanged 方法，确保了视图大小的正确处理。
        if (h > oldh){
//            这个条件语句检查新的高度 h 是否大于旧的高度 oldh，以确定视图的高度是否增加。
            if (mLayoutListener != null){
//                这行代码检查是否存在一个非空的 mLayoutListener 对象，以避免空指针异常。
                mLayoutListener.exLayoutShow();
//                如果满足条件，调用 mLayoutListener 的 exLayoutShow 方法，这个方法用于通知监听器（或回调）视图布局的变化。
            }
        }
//        这段代码的目的是在视图大小变化时，通过触发监听器的回调方法 exLayoutShow 来通知外部代码视图布局的变化。通常，这种方法用于处理布局的变化，例如，当键盘弹出或收起时，可以在该方法内触发相应的操作以调整布局。
    }

    /************ 按钮点击事件 ***********/

    // 点击输入类型按钮的处理方法
    @OnClick(R.id.iv_input_type)
    public void clickInputTypeBtn() {
        if (isSoftInputShow()) {
            // 如果软键盘已经显示，则隐藏软键盘
            hideSoftInput();
        } else if (mExtensionLayout.isShown()) {
            // 如果扩展布局已经显示，则隐藏扩展布局
            mExtensionLayout.setVisibility(GONE);
        } else if (mExpressLayout.isShown()) {
            // 如果表情布局已经显示，则隐藏表情布局
            mExpressLayout.setVisibility(GONE);
            mIvExpress.setImageResource(R.mipmap.expression);
        }

        if (mEtInput.isShown()) {
            // 如果文本输入框已经显示，则切换到语音输入
            mEtInput.setVisibility(GONE);
            mBtnAudioRecord.setVisibility(VISIBLE);
            mIvInputType.setImageResource(R.mipmap.keyboard);
        } else {
            // 如果文本输入框未显示，则切换到文本输入并显示软键盘
            mBtnAudioRecord.setVisibility(GONE);
            mEtInput.setVisibility(VISIBLE);
            mIvInputType.setImageResource(R.mipmap.sound);
            showSoftInput();
        }
    }
//    这段代码处理了点击输入类型按钮的操作。根据当前界面状态，它可以执行以下操作：
//
//    如果软键盘已经显示，它会隐藏软键盘。
//    如果扩展布局（例如附件、图片等功能的布局）已经显示，它会隐藏扩展布局。
//    如果表情布局已经显示，它会隐藏表情布局。
//    然后，它会检查文本输入框的状态：
//
//    如果文本输入框已经显示，它将切换到语音输入（隐藏文本输入框，显示语音输入按钮）。
//    如果文本输入框未显示，它将切换到文本输入（隐藏语音输入按钮，显示文本输入框），并显示软键盘。

    // 点击"更多"按钮的响应方法
    @OnClick(R.id.iv_more)
    public void clickMoreBtn() {
        if (isSoftInputShow()) {
            // 如果软键盘已显示
            lockContentHeight();   // 锁定内容高度
            hideSoftInput();       // 隐藏软键盘
            mExtensionLayout.setVisibility(VISIBLE);   // 显示输入法扩展布局
            unLockContentHeight(); // 解锁内容高度
        } else if (mExpressLayout.isShown()) {
            // 如果表情布局已显示
            lockContentHeight();   // 锁定内容高度
            mExpressLayout.setVisibility(GONE);        // 隐藏表情布局
            mIvExpress.setImageResource(R.mipmap.expression); // 设置"表情"按钮图标
            mExtensionLayout.setVisibility(VISIBLE);    // 显示输入法扩展布局
            unLockContentHeight(); // 解锁内容高度
        } else if (mExtensionLayout.isShown()) {
            // 如果输入法扩展布局已显示
            lockContentHeight();   // 锁定内容高度
            mExtensionLayout.setVisibility(GONE); // 隐藏输入法扩展布局
            showSoftInput();       // 显示软键盘
            unLockContentHeight(); // 解锁内容高度
        } else {
            // 如果以上条件都不满足
            mExtensionLayout.setVisibility(VISIBLE); // 直接显示输入法扩展布局
        }
    }
//    这段代码响应"更多"按钮的点击事件，根据当前界面状态执行相应操作。首先，检查软键盘是否显示，如果显示则隐藏软键盘并显示输入法扩展布局；如果表情布局已显示，则隐藏表情布局并显示输入法扩展布局；如果输入法扩展布局已显示，则隐藏输入法扩展布局并显示软键盘；如果以上条件都不满足，直接显示输入法扩展布局。这段代码用于控制输入法和输入法扩展布局的显示和隐藏。

    @OnClick(R.id.iv_expression)
    public void clickExpressBtn() {
        // 点击表情按钮触发的事件

        if (mBtnAudioRecord.isShown()) {
            // 如果录音按钮可见

            // 隐藏录音按钮
            mBtnAudioRecord.setVisibility(GONE);

            // 切换输入类型图标为声音图标
            mIvInputType.setImageResource(R.mipmap.sound);

            // 显示文本输入框
            mEtInput.setVisibility(VISIBLE);
        }


        // 切换操作：如果扩展布局（如发送图片、文件等功能）可见，则执行相应的切换操作
        if (mExtensionLayout.isShown()) {
            lockContentHeight();  // 锁定内容高度，以防止界面抖动
            mExtensionLayout.setVisibility(GONE);  // 隐藏扩展布局
            mExpressLayout.setVisibility(VISIBLE);  // 显示表情布局
            unLockContentHeight();  // 解锁内容高度
        } else if (mExpressLayout.isShown()) {
            lockContentHeight();
            mExpressLayout.setVisibility(GONE);  // 隐藏表情布局
            mIvExpress.setImageResource(R.mipmap.expression);  // 设置表情按钮图标为“笑脸”图标
            showSoftInput();  // 显示软键盘
            unLockContentHeight();
        } else if (isSoftInputShow()) {
            lockContentHeight();
            hideSoftInput();  // 隐藏软键盘
            mExpressLayout.setVisibility(VISIBLE);  // 显示表情布局
            mIvExpress.setImageResource(R.mipmap.keyboard);  // 设置表情按钮图标为“键盘”图标
            unLockContentHeight();
        } else {
            mExpressLayout.setVisibility(VISIBLE);  // 无扩展布局或软键盘，直接显示表情布局
            mIvExpress.setImageResource(R.mipmap.keyboard);  // 设置表情按钮图标为“键盘”图标
        }
    }
//    这段代码处理了在点击表情按钮时触发的事件，用于切换输入法方式，包括从语音输入切换到文本输入，显示或隐藏扩展布局（如发送图片、文件等功能），以及显示或隐藏表情布局。其中，通过一些条件判断，根据不同的情况执行相应的操作，如显示或隐藏软键盘等。

    // 创建一个触摸事件监听器类
    private class MyOnTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // 当触摸事件为按下事件时
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mExpressLayout.isShown()) {
                    // 如果表情布局可见
                    lockContentHeight(); // 锁定内容高度
                    mExpressLayout.setVisibility(GONE); // 隐藏表情布局
                    mIvExpress.setImageResource(R.mipmap.expression); // 设置表情按钮图标
                    showSoftInput(); // 显示软键盘
                    unLockContentHeight(); // 解锁内容高度
                } else if (mExtensionLayout.isShown()) {
                    // 如果扩展布局可见
                    lockContentHeight(); // 锁定内容高度
                    mExtensionLayout.setVisibility(GONE); // 隐藏扩展布局
                    showSoftInput(); // 显示软键盘
                    unLockContentHeight(); // 解锁内容高度
                } else {
                    // 如果两者都不可见
                    showSoftInput(); // 直接显示软键盘
                }
                return true; // 处理触摸事件，不传递给下一层
            }
            return false; // 不处理其他触摸事件
        }
    }
//    这段代码定义了一个触摸事件监听器 MyOnTouchListener。当触摸事件为按下事件（MotionEvent.ACTION_DOWN）时，根据当前界面的状态（是否显示表情布局或扩展布局），采取不同的操作：
//
//    如果表情布局可见，首先锁定内容高度，然后隐藏表情布局，将表情按钮图标切换为表情图标，显示软键盘，最后解锁内容高度。
//
//    如果扩展布局可见，同样锁定内容高度，隐藏扩展布局，显示软键盘，解锁内容高度。
//
//    如果两者都不可见，直接显示软键盘。
//
//    最后，无论哪种情况，return true; 用于表示已处理触摸事件，不传递给下一层处理；return false; 表示不处理其他触摸事件。


    // 当点击 R.id.tv_btn_send 控件时执行此方法
    @OnClick(R.id.tv_btn_send)
    public void sendTextMessage() {
        // 从输入框中获取文本
        String text = mEtInput.getText().toString();

        // 清空输入框
        mEtInput.getText().clear();

        // 检查是否设置了布局监听器 mLayoutListener
        if (mLayoutListener != null) {
            // 调用布局监听器的 sendBtnClick 方法，并传递文本作为参数
            mLayoutListener.sendBtnClick(text);
        }
    }
//    这段代码是一个点击事件处理方法，当 R.id.tv_btn_send 控件被点击时，它会执行。首先，它从输入框 mEtInput 中获取文本，然后清空输入框内容。接着，它检查是否设置了布局监听器 mLayoutListener。如果设置了监听器，它会调用监听器的 sendBtnClick 方法，并将获取的文本作为参数传递给监听器，以便在用户点击发送按钮时执行相应的操作。


    @OnClick(R.id.layout_image)
    public void selectPhoto() {
        // 当用户点击对应布局（R.id.layout_image）时触发的方法。

        if (mLayoutListener != null) {
            // 检查是否设置了监听器对象 mLayoutListener。

            mLayoutListener.photoBtnClick();
            // 调用监听器的 photoBtnClick 方法，通知监听器用户点击了“选择照片”的操作。
        }
    }
//    这段代码是一个点击事件处理方法。当用户点击具有 R.id.layout_image 标识的布局时，会执行此方法。首先，它检查是否存在 mLayoutListener 监听器对象，以确保已设置监听器。如果监听器存在，它调用监听器的 photoBtnClick 方法，这表示用户点击了用于选择照片的按钮。这是一种在 Android 应用中处理按钮点击事件的常见方式，通过监听器模式来实现事件处理。

    // 点击事件处理方法
    @OnClick(R.id.layout_location)
    public void selectLocation() {
        if (mLayoutListener != null) {
            // 检查监听器是否存在
            mLayoutListener.locationBtnClick();
            // 调用监听器的位置按钮点击回调方法
        }
    }
//    这段代码是一个点击事件处理方法，当用户点击与 R.id.layout_location 关联的视图时，会触发此方法。在方法中，首先检查 mLayoutListener 是否存在，如果存在，它会调用监听器中的 locationBtnClick 回调方法。这是一种常见的模式，用于处理按钮点击事件并通知其他部分的应用程序以执行相关操作。这种模式通过接口和回调方法实现了组件之间的通信。

    // 当点击 R.id.layout_camera 布局时触发的方法
    @OnClick(R.id.layout_camera)
    public void startCamera() {
        // 检查 mLayoutListener 是否为空
        if (mLayoutListener != null) {
            // 调用 mLayoutListener 的 cameraBtnClick 方法
            mLayoutListener.cameraBtnClick();
        }
    }
//    这段代码是一个点击事件处理方法，当用户点击具有 R.id.layout_camera ID 的布局时，会执行 startCamera() 方法。该方法首先检查 mLayoutListener 对象是否为空，然后调用 mLayoutListener 的 cameraBtnClick() 方法。这种方法通常用于在用户点击按钮或布局时触发相应的操作。

    /**
     * 锁定内容 View 的高度，解决闪屏问题
     */
    private void lockContentHeight() {
        // 锁定内容视图的高度，以便它不再随内容的变化而变化

// 获取内容视图的布局参数
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContentView.getLayoutParams();

// 将内容视图的高度设置为当前的高度，即锁定当前的高度
        layoutParams.height = mContentView.getHeight();

// 将内容视图的权重设置为0.0f，这将阻止内容视图的自动大小更改
        layoutParams.weight = 0.0f;

    }
//    这段代码用于锁定内容视图的高度，通常在需要禁用内容视图的大小自适应时使用。通过将内容视图的布局参数的高度设置为当前高度，并将权重设置为0.0f，可以防止内容视图自动调整大小以适应内容的变化。这可能用于特定用户界面需求，如在特定情况下禁用滚动。
    /**
     * 释放内容 View 高度
     */
    // 解锁内容高度的方法
    private void unLockContentHeight() {
        // 在200毫秒后执行以下任务
        mEtInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 获取内容视图的布局参数
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                        mContentView.getLayoutParams();

                // 重置内容视图的高度为0
                layoutParams.height = 0;

                // 重新分配布局参数的权重为1.0，以支持自动调整高度
                layoutParams.weight = 1.0f;
            }
        }, 200); // 等待200毫秒再执行以确保正确调整高度
    }
//    这段代码的目的是解锁内容高度，通常在键盘隐藏后执行。它将内容视图的高度重置为0，并将布局参数的权重设置为1.0，以支持内容的自动调整高度。这是为了确保当键盘被隐藏后，内容能够重新占据适当的空间。

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    // 显示输入法软键盘
    private void showSoftInput() {
        // 将输入焦点设置到输入框
        mEtInput.requestFocus();

        // 使用 post 方法在消息队列中异步执行显示软键盘的任务
        mEtInput.post(new Runnable() {
            @Override
            public void run() {
                // 显示输入法软键盘，参数0表示没有附加选项
                mInputManager.showSoftInput(mEtInput, 0);
            }
        });
    }
//    这段代码的目的是在用户需要输入时显示输入法软键盘。首先，它将焦点设置到输入框 mEtInput，然后使用 post 方法异步执行显示软键盘的任务，以确保在视图已准备好时再显示软键盘。最后，mInputManager.showSoftInput(mEtInput, 0) 用于实际显示输入法软键盘。

    /**
     * 隐藏软件盘
     */
    // 隐藏软键盘
    private void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEtInput.getWindowToken(), 0);
    }

    // 检查软键盘是否显示
    private boolean isSoftInputShow() {
        // 通过检查键盘的高度是否为0来确定软键盘是否显示
        return SystemUtils.getKeyBoardHeight(mActivity) != 0;
    }

    // 设置输入布局监听器
    public void setLayoutListener(OnInputLayoutListener layoutListener) {
        this.mLayoutListener = layoutListener;
    }
//    这些方法用于处理软键盘的显示和隐藏以及为输入布局设置监听器。其中：
//
//    hideSoftInput() 方法用于隐藏软键盘。
//    isSoftInputShow() 方法用于检查软键盘是否显示，它通过检查键盘的高度是否为0来确定软键盘的显示状态。
//    setLayoutListener() 方法用于设置输入布局监听器，以便在输入布局的状态发生变化时执行相应的操作。

    /**
     * 隐藏所有已显示的布局（键盘，表情，扩展）
     */
    // 隐藏悬浮布局的方法
    public void hideOverView() {
        // 如果软键盘正在显示，隐藏软键盘
        if (isSoftInputShow()) {
            hideSoftInput();
        }

        // 如果表情布局正在显示，隐藏表情布局，同时设置图标为表情图标
        if (mExpressLayout.isShown()) {
            mExpressLayout.setVisibility(GONE);
            mIvExpress.setImageResource(R.mipmap.expression);
        }

        // 如果扩展布局正在显示，隐藏扩展布局
        if (mExtensionLayout.isShown()) {
            mExtensionLayout.setVisibility(GONE);
        }
    }
//    这段代码定义了一个用于隐藏悬浮布局的方法。它首先检查是否软键盘正在显示，如果是，则隐藏软键盘。接着，它检查是否表情布局正在显示，如果是，则隐藏表情布局并切换表情图标。最后，它检查是否扩展布局正在显示，如果是，则隐藏扩展布局。这个方法通常在用户想要关闭这些布局时调用，以提供更好的用户体验。

    public interface OnInputLayoutListener {
        // 当用户点击发送按钮时触发，传递文本消息
        void sendBtnClick(String textMessage);

        // 当用户点击图片按钮时触发
        void photoBtnClick();

        // 当用户点击位置按钮时触发
        void locationBtnClick();

        // 当用户点击相机按钮时触发
        void cameraBtnClick();

        // 当用户完成音频录制后触发，传递音频文件路径和录制时间
        void audioRecordFinish(String audioFilePath, long recordTime);

        // 当音频录制出现错误时触发，传递错误消息
        void audioRecordError(String message);

        // 当扩展布局显示时触发
        void exLayoutShow();
    }
//    这个接口定义了一组回调方法，用于处理用户在输入布局中的不同操作。每个方法对应不同的用户操作，如点击发送按钮、图片按钮、位置按钮、相机按钮，以及处理音频录制的完成和错误情况。此外，还有一个方法用于在扩展布局显示时触发。实现该接口的类可以根据需要处理这些操作。
}
