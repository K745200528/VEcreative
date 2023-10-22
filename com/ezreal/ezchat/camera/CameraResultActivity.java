package com.ezreal.ezchat.camera;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ezreal.ezchat.R;
import com.suntek.commonlibrary.utils.ImageUtils;
import com.suntek.commonlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 结果展示界面
 * Created by wudeng on 2017/9/15.
 */

public class CameraResultActivity extends AppCompatActivity {

    private static final String TAG = CameraResultActivity.class.getSimpleName();
    // TAG 用于在调试时标识这个 Activity 的名称，通常用于日志记录

    public static final int FLAG_SHOW_IMG = 0x100;
    public static final int FLAG_SHOW_VIDEO = 0x101;
    // 常量 FLAG_SHOW_IMG 和 FLAG_SHOW_VIDEO 用于标识显示图像或视频的标志

    public static final int RESULT_OK = 0x200;
    public static final int RESULT_CANCEL = 0x201;
    public static final int RESULT_RESET = 0x202;
    // 常量 RESULT_OK, RESULT_CANCEL 和 RESULT_RESET 用于标识结果的标志，通常用于与其他活动进行通信

    @BindView(R.id.video_view)
    VideoView mVideoView;
    @BindView(R.id.image_view)
    ImageView mImageView;
    // 使用 ButterKnife 库注解的方式，获取布局文件中的 VideoView 和 ImageView 控件
//    这段代码定义了一个名为 CameraResultActivity 的 Android 活动（Activity）。活动是 Android 应用中的基本组件，用于用户界面和互动。这个活动继承自 AppCompatActivity 类，用于提供应用的基本界面和交互功能。
//
//    在这个活动中，首先定义了一个 TAG 常量，用于在日志中标识这个活动的名称。接下来，定义了一些常量，如 FLAG_SHOW_IMG 和 FLAG_SHOW_VIDEO，用于标识显示图像或视频的标志。还定义了一些常量，如 RESULT_OK、RESULT_CANCEL 和 RESULT_RESET，用于标识结果的标志，通常用于与其他活动进行通信。
//
//    使用 @BindView 注解方式获取了布局文件中的 VideoView 和 ImageView 控件，这是通过 ButterKnife 库来实现的，它简化了 Android 中的视图绑定操作，提高了代码的可读性和简洁性。这两个控件用于显示图像或视频。

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 调用父类的 onCreate 方法，进行基本的 Activity 初始化

        // 全屏幕
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏窗口的标题栏

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置窗口标记，以全屏显示应用

        setContentView(R.layout.activity_camera_result);
        // 使用指定的布局文件来设置当前 Activity 的界面

        ButterKnife.bind(this);
        // 使用 ButterKnife 进行视图绑定操作

        init();
        // 调用 init() 方法来执行额外的初始化操作
    }
//    这段代码是 Android 应用中的一个 Activity 的生命周期方法 onCreate。在 onCreate 方法内，通常进行了如下操作：
//
//    调用父类的 onCreate 方法：super.onCreate(savedInstanceState);，这是为了确保调用父类的相应方法以初始化 Activity。
//
//    设置全屏显示：通过 requestWindowFeature 隐藏标题栏，然后使用 getWindow().setFlags() 设置全屏标志，以确保应用以全屏模式运行。
//
//    设置布局：通过 setContentView(R.layout.activity_camera_result); 使用指定的布局文件来设置当前 Activity 的用户界面。
//
//    视图绑定：ButterKnife.bind(this); 使用 ButterKnife 框架进行视图绑定，以便在后续代码中方便地访问和操作视图组件。
//
//    最后，调用 init() 方法，执行额外的初始化操作，这个方法在后续可能包含了其他的初始化步骤。
//
//    这些操作通常在 Activity 创建时执行，以准备显示用户界面和其他相关的初始化工作。

    private void init() {
        // 初始化方法

        // 设置视频循环播放
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 视频播放完成时的监听器
                mp.start(); // 重新开始播放
                mp.setLooping(true); // 设置循环播放
            }
        });

        Intent intent = getIntent(); // 获取启动该Activity的Intent
        int flags = intent.getIntExtra("FLAG", FLAG_SHOW_IMG);
        // 从Intent中获取整数型参数，如果没有该参数则使用默认值FLAG_SHOW_IMG

        if (flags == FLAG_SHOW_IMG) {
            mImageView.setVisibility(View.VISIBLE); // 设置ImageView可见
            mVideoView.setVisibility(View.GONE); // 设置VideoView不可见
            showImg(intent); // 显示图片内容
        } else if (flags == FLAG_SHOW_VIDEO) {
            mVideoView.setVisibility(View.VISIBLE); // 设置VideoView可见
            mImageView.setVisibility(View.GONE); // 设置ImageView不可见
            showVideo(intent); // 显示视频内容
        }
    }
//    这段代码是一个 init 方法，用于初始化界面和控制视频的播放。首先，通过设置 setOnCompletionListener 监听器，实现了视频循环播放，当视频播放完成后会重新开始并循环播放。
//
//    然后，通过获取启动该Activity的Intent，检查传递的参数 FLAG，根据不同的标志值来决定显示图片或视频内容。如果 FLAG 的值为 FLAG_SHOW_IMG，则设置ImageView可见，VideoView不可见，并调用 showImg(intent) 来显示图片。如果 FLAG 的值为 FLAG_SHOW_VIDEO，则设置VideoView可见，ImageView不可见，并调用 showVideo(intent) 来显示视频。
//
//    这段代码允许根据传递的参数来决定显示不同的媒体内容，并实现视频的循环播放功能。

    private void showImg(Intent intent) {
        // 显示图片
        ImageUtils.setImageByFile(this, mImageView,
                intent.getStringExtra("imagePath"), R.mipmap.bg_img_defalut);
        // 调用 ImageUtils 工具类的方法，根据传入的路径显示图片在 mImageView 控件上
    }

    private void showVideo(Intent intent) {
        // 显示视频
        try {
            mVideoView.setVideoPath(intent.getStringExtra("videoPath"));
            // 设置 VideoView 控件的视频路径
            mVideoView.start();
            // 播放视频
            mVideoView.requestFocus();
            // 获取焦点
        } catch (Exception e) {
            // 捕获异常
            ToastUtils.showMessage(this, "播放出错:" + e.getMessage());
            // 显示出错消息
            this.setResult(RESULT_CANCEL);
            // 设置结果为取消
            this.finish();
            // 结束当前活动
        }
    }
//    这段代码包含了两个方法：showImg 和 showVideo，用于显示图片和视频。showImg 方法使用 ImageUtils 工具类来加载图片并在 mImageView 控件中显示。showVideo 方法则在 mVideoView 控件中播放视频，同时处理了可能出现的异常情况，如无法播放。在异常情况下，它会显示错误消息、设置结果为取消，并结束当前活动。这些方法通常在用户需要查看图片或视频时被调用，例如从其他活动接收到相关意图后展示相关内容。

    @OnClick(R.id.iv_ok)
    public void okClick() {
        // 注解 @OnClick 表明这个方法是一个点击事件的处理方法，它会在指定的 View 被点击时被调用。

        release();
        // 调用 release() 方法，释放资源或执行相关操作。

        this.setResult(RESULT_OK);
        // 设置返回结果为 RESULT_OK，通常用于标识操作成功。

        this.finish();
        // 关闭当前 Activity 或页面。
    }
//    这段代码定义了一个方法 okClick，它使用了 @OnClick 注解，表明这是一个点击事件的处理方法，该方法用于处理某个 View（在这里是 R.id.iv_ok 对应的 View）的点击事件。在方法内部，执行了一系列操作：
//
//    release(): 调用了 release() 方法，这可能是释放资源、清理操作、或执行其他必要的功能。
//
//            this.setResult(RESULT_OK): 设置了当前页面的返回结果为 RESULT_OK，这通常用于标识一个操作成功执行。
//
//            this.finish(): 调用了 finish() 方法，关闭当前的 Activity 或页面，返回到上一个 Activity 或页面。
//
//    这种结构常见于处理用户点击事件，其中你可以在点击某个按钮或视图后执行特定的操作，例如保存数据、提交表单、或者关闭当前页面。

    @OnClick(R.id.iv_reset)
    public void resetClick() {
        // 当名为 "iv_reset" 的视图被点击时，触发这个方法
        release();
        // 调用 release() 方法，释放资源或执行相关清理操作
        this.setResult(RESULT_RESET);
        // 设置当前活动的结果代码为 RESULT_RESET，这通常用于指示“重置”操作
        this.finish();
        // 关闭当前的活动，即关闭当前界面或返回到前一个界面
    }
//    这段代码是一个 Android 应用中的事件处理代码。当名为 "iv_reset" 的视图被点击时（通常是一个图像视图），resetClick() 方法会被触发执行。在该方法内，首先调用 release() 方法来释放资源或执行相关清理操作，然后通过 setResult(RESULT_RESET) 设置当前活动的结果代码为 RESULT_RESET。最后，通过 finish() 方法关闭当前的活动，即关闭当前界面或返回到前一个界面。

    private void release() {
        // 定义了一个名为 "release" 的私有方法
        if (mVideoView.isPlaying()) {
            // 如果视频正在播放
            mVideoView.stopPlayback();
            // 停止视频播放
        }
    }
//    这段代码定义了一个名为 release 的私有方法。在方法中，首先检查 mVideoView 是否正在播放视频，如果正在播放，就使用 stopPlayback 方法停止视频的播放。这个方法的目的是释放或停止视频播放，以确保资源得到正确的释放，同时也可以在需要时停止正在播放的视频，如在切换或关闭视频播放页面时。这有助于提高应用程序的性能和资源管理。

    @Override
    protected void onResume() {
        super.onResume();
        // 覆盖父类的 onResume 方法，进行特定的操作

        if (mVideoView.getVisibility() == View.VISIBLE){
            // 检查视频视图的可见性
            mVideoView.resume();
            // 如果视频视图可见，调用其 resume 方法，恢复播放视频
        }
    }
//    这段代码是在 Android 应用中覆盖了 onResume 方法。onResume 方法是 Android 生命周期中的一个重要部分，当一个 Activity 进入前台（可见）时，系统会调用该方法。在这个方法中，通常可以执行应用需要的一些操作，例如恢复正在播放的视频。

    @Override
    protected void onPause() {
        super.onPause();
        // 覆盖（重写）了 Activity 的 onPause 方法
        // 在 Activity 生命周期中，当 Activity 即将失去焦点时系统会调用这个方法，例如当用户切换到其他应用或返回桌面时。

        if (mVideoView.getVisibility() == View.VISIBLE) {
            // 检查 VideoView 控件是否可见
            // mVideoView 是一个 VideoView 控件，通常用于播放视频。
            // 这段代码在确保 VideoView 控件可见的情况下执行下面的操作。
            mVideoView.pause();
            // 暂停视频的播放
        }
    }
//    这段代码是在 Android 应用的生命周期中的 onPause 方法中进行的操作。当应用即将失去焦点（例如用户切换到其他应用或返回到设备主屏幕）时，系统会调用 onPause 方法。在这个方法中，首先调用了父类的 super.onPause() 方法，然后检查 mVideoView 控件是否可见，如果可见，就暂停视频的播放。

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 重写按键按下事件的处理方法

        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            // 检查事件的动作是否为按键按下且按下的键是否是返回键

            release();
            // 执行 release() 方法，可能是释放资源的操作

            this.setResult(RESULT_CANCEL);
            // 设置当前活动的结果代码为 RESULT_CANCEL

            this.finish();
            // 结束当前活动

            return true;
            // 返回 true 表示按下返回键事件已被处理
        }

        // 如果不是返回键按下事件，或者事件未被处理，交给父类处理
        return super.onKeyDown(keyCode, event);
    }
//    这段代码重写了 onKeyDown 方法，该方法用于处理按键按下事件。在这个特定情况下，主要检查是否按下了返回键。如果按下了返回键，会执行一系列操作，包括释放资源、设置结果代码为 RESULT_CANCEL，并关闭当前活动。最后，返回 true 表示按下返回键事件已被处理，否则会将事件传递给父类以继续默认处理。通常，这种用法用于自定义返回键行为，以确保在返回键按下时执行特定的操作，而不仅仅是返回上一个界面。
}
