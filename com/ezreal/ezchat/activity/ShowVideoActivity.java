package com.ezreal.ezchat.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.ezreal.ezchat.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.model.AttachmentProgress;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.suntek.commonlibrary.utils.TextUtils;
import com.suntek.commonlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by wudeng on 2017/10/30.
 */

// 创建 ShowVideoActivity 类，该类继承自 BaseActivity，用于显示视频。
public class ShowVideoActivity extends BaseActivity {

    // 定义一个 TAG 常量，用于在日志中标识该 Activity 的名称。
    private static final String TAG = ShowImageActivity.class.getSimpleName();

    // 使用 ButterKnife 库的 @BindView 注解，将布局中的控件与对应的成员变量绑定。
    @BindView(R.id.video_view)
    VideoView mVideoView; // 视频播放控件
    @BindView(R.id.tv_show_progress)
    TextView mTvProgress; // 显示播放进度的文本控件
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar; // 进度条控件
//    这段代码是一个 Android Activity 类，用于显示视频。它初始化了视频播放控件 mVideoView、显示播放进度的文本控件 mTvProgress 以及进度条控件 mProgressBar。此外，它还创建了一个 TAG 常量用于在日志中标识该 Activity 的名称。

    // 当前持续时间，初始化为0
    private int mCurrentDur = 0;

    // IM 消息对象，用于存储即时通讯消息
    private IMMessage mIMMessage;

    // 附件传输进度观察者
    private Observer<AttachmentProgress> mProgressObserver;

    // 标志是否正在下载中，初始化为false
    private boolean downloading = false;
//    这段代码声明了几个变量：
//
//    mCurrentDur 用于存储当前的持续时间，通常在处理音频或视频播放等功能中使用。
//
//    mIMMessage 是 IM（即时通讯）消息对象，通常用于存储即时通讯中的消息内容。
//
//    mProgressObserver 是一个观察者对象，用于观察附件传输的进度变化。这通常在消息附件下载或上传时使用，以跟踪传输的进度。
//
//    downloading 是一个布尔值，用于标志是否当前正在进行下载操作，初始化为 false，通常用于控制下载操作的状态。

    // 创建 Handler 对象，用于处理消息，使用 @SuppressLint 注解忽略潜在的内存泄漏警告
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // 当接收到消息时进行处理
            if (downloading && msg.what == 0x300) {
                // 如果正在下载且消息标识为 0x300

                String path = ((VideoAttachment) mIMMessage.getAttachment()).getPath();
                // 获取视频附件的路径

                if (!TextUtils.isEmpty(path)) {
                    // 如果路径不为空

                    downloading = false;
                    // 设置下载状态为 false

                    mTvProgress.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    // 隐藏下载进度文本和进度条

                    mVideoView.setMediaController(new MediaController(ShowVideoActivity.this));
                    // 设置视频视图的媒体控制器，允许用户控制视频播放

                    mVideoView.setVideoPath(path);
                    // 设置视频视图的视频路径

                    mVideoView.start();
                    // 开始播放视频

                    mVideoView.requestFocus();
                    // 获取视频焦点，以便用户可以交互

                } else {
                    // 如果路径为空

                    mHandler.sendEmptyMessageAtTime(0x300, 1000);
                    // 延迟 1 秒后再次发送消息，用于检查路径是否就绪
                }
            }
        }
    };
//    这段代码中，创建了一个 Handler 对象，用于在消息处理中更新视频播放。该 Handler 用于异步处理下载和播放视频，包括检查视频路径是否准备就绪，并在路径准备就绪后开始播放视频。当 downloading 为 true 且消息的标识为 0x300 时，表示视频下载正在进行。如果视频路径不为空，那么将设置下载状态为 false，隐藏下载进度文本和进度条，设置媒体控制器以允许用户控制视频播放，并开始播放视频。如果视频路径为空，则等待 1 秒后再次发送消息以检查路径是否准备就绪。

    // 当活动被创建时调用
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 隐藏窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 设置窗口标志，使应用全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 设置该活动的内容视图为指定的布局文件
        setContentView(R.layout.activity_show_video);

        // 使用ButterKnife绑定视图
        ButterKnife.bind(this);

        // 创建一个观察者用于监控附件下载进度
        mProgressObserver = new Observer<AttachmentProgress>() {
            @Override
            public void onEvent(AttachmentProgress progress) {
                // 如果正在下载并且进度对应当前消息的UUID
                if (downloading && progress.getUuid().equals(mIMMessage.getUuid())) {
                    // 计算下载进度百分比
                    int present = (int) (progress.getTransferred() / (progress.getTotal() * 1.0f) * 100.0f);
                    String text = "下载中…… " + String.valueOf(present) + "%";
                    // 更新下载进度文本视图
                    mTvProgress.setText(text);

                    if (present > 60) {
                        // 如果下载进度大于60%，则通过Handler发送消息
                        // 注意：mProgressObserver不一定会回调到100%下载，所以通过检查路径是否存在来判断
                        mHandler.sendEmptyMessageAtTime(0x300, 1000);
                    }
                }
            }
        };

        // 初始化视频
        initVideo();
    }
//    这段代码用于配置和初始化活动的界面和一些操作。其中，它设置了全屏显示，绑定了视图元素，创建了一个用于监控附件下载进度的观察者，以及初始化了视频的相关设置。

    // 当活动恢复时触发的方法
    @Override
    protected void onResume() {
        super.onResume();

        // 如果当前播放时间大于0，说明之前有暂停或切换操作，将视频跳转到当前时间点
        if (mCurrentDur > 0) {
            mVideoView.seekTo(mCurrentDur);
        } else {
            // 否则从开始位置开始播放
            mVideoView.seekTo(0);
        }

        // 恢复视频播放
        mVideoView.resume();

        // 注册消息服务观察者，用于监视附件上传进度
        NIMClient.getService(MsgServiceObserve.class)
                .observeAttachmentProgress(mProgressObserver, true);
    }
//    这段代码是在 Android 活动（Activity）的 onResume 方法中执行的。它主要完成以下操作：
//
//    通过 seekTo 方法，检查 mCurrentDur 变量的值，如果大于 0，表示之前进行了暂停或跳转操作，将视频播放器跳转到 mCurrentDur 所代表的时间点；如果等于或小于 0，将视频播放器跳转到开始位置。
//
//    通过 resume 方法恢复视频播放，继续播放之前暂停的视频。
//
//    注册一个消息服务观察者（mProgressObserver），用于监视附件（通常是视频附件）上传的进度，以便及时更新附件上传进度的信息。
//
//    这段代码主要处理视频播放的继续和附件上传进度的观察。

    // 当 Activity 暂停时调用
    @Override
    protected void onPause() {
        super.onPause();

        // 检查视频是否正在播放
        if (mVideoView.isPlaying()) {
            // 暂停视频播放
            mVideoView.pause();

            // 记录当前播放进度，以便在恢复播放时继续
            mCurrentDur = mVideoView.getCurrentPosition();
        }
    }
//    这段代码是在 Android Activity 生命周期中的onPause方法中，用于处理在暂停 Activity 时的操作。在这里，它首先调用父类的onPause方法以确保完成必要的暂停操作。

//    接下来，它检查视频是否正在播放（mVideoView.isPlaying()），如果是，则暂停视频播放（mVideoView.pause()）。同时，它记录了当前的播放进度（mVideoView.getCurrentPosition()），以便在稍后恢复播放时从同一位置继续播放。这通常在用户切换到其他应用或关闭当前应用时用于保存视频播放状态。

    // 当 Activity 销毁时调用
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 停止视频播放
        mVideoView.stopPlayback();

        // 取消对附件上传和下载进度的观察
        // NIMClient 是网易云信 SDK 的客户端类，getService(MsgServiceObserve.class) 用于获取消息服务观察者
        // observeAttachmentProgress 用于监听附件（如图片、音视频文件）的上传和下载进度
        // mProgressObserver 是一个进度观察者对象
        NIMClient.getService(MsgServiceObserve.class).observeAttachmentProgress(mProgressObserver, false);
    }
//    这段代码用于处理在 Activity 销毁时的清理工作。首先，它停止了正在播放的视频，然后取消了对附件上传和下载进度的观察。这通常用于释放资源、停止后台任务或取消不再需要的观察者。

    // 初始化视频播放
    private void initVideo() {
        // 从Intent中获取序列化的IMMessage对象
        mIMMessage = (IMMessage) getIntent().getSerializableExtra("IMMessage");

        // 如果IMMessage对象为空，显示提示信息并结束当前Activity
        if (mIMMessage == null) {
            ToastUtils.showMessage(this, "视频无法播放，请重试~");
            finish();
            return;
        }

        // 从IMMessage对象获取视频附件
        VideoAttachment attachment = (VideoAttachment) mIMMessage.getAttachment();

        // 如果视频附件为空，显示提示信息并结束当前Activity
        if (attachment == null) {
            ToastUtils.showMessage(this, "视频附件为空，无法播放!");
            finish();
            return;
        }

        // 获取视频文件的路径
        String path = attachment.getPath();

        // 如果视频文件路径为空，显示提示对话框询问用户是否下载视频附件
        if (TextUtils.isEmpty(path)) {
            new AlertDialog.Builder(this)
                    .setTitle("视频未下载或者地址已失效，是否下载？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 用户点击了对话框的确定按钮后触发的事件
                            dialog.dismiss(); // 关闭对话框
                            downloadVideo(); // 调用 downloadVideo() 方法下载视频附件
                        }
                    })

                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // 关闭对话框
                            finish(); // 结束当前的 Activity
                        }

                    }).setCancelable(false).show();
            return;
        }

        // 设置视频播放完成监听器，当视频播放完成时自动回到视频开始位置
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 当媒体播放器播放完成了一次媒体文件时，会调用这个方法。

                mp.seekTo(0);
                // 这行代码将媒体播放器的播放位置（播放头）设置到媒体文件的起始位置，实现重新播放该媒体文件。
                // 通常，这是当媒体文件播放结束后自动重新播放时使用的方法。
            }

        });

        // 设置媒体控制器，以便用户控制播放进度等
        mVideoView.setMediaController(new MediaController(this));

        // 设置视频路径并开始播放
        mVideoView.setVideoPath(path);
        mVideoView.start();
        mVideoView.requestFocus();
    }
//    这段代码用于初始化视频播放功能。首先，它从Intent中获取序列化的IMMessage对象，然后检查该对象是否为空。接着，它获取视频附件，再次检查附件是否为空。如果视频文件路径为空，它会显示一个对话框询问用户是否下载视频附件。如果用户同意下载，将调用downloadVideo()方法进行下载。
//
//    然后，代码设置视频播放的一些属性，包括在视频播放完成后自动回到视频开始位置、设置媒体控制器以供用户控制播放进度等，最后设置视频的路径并开始播放。

    // 启动视频下载
    private void downloadVideo() {
        downloading = true;
        mTvProgress.setVisibility(View.VISIBLE);

        // 通过 NIMClient 获取消息服务，再调用 downloadAttachment 方法来下载附件
        NIMClient.getService(MsgService.class)
                .downloadAttachment(mIMMessage, false);

        // 显示下载进度条
        mProgressBar.setVisibility(View.VISIBLE);
    }
//    这段代码的目的是启动视频下载操作。首先，将 downloading 标志设置为 true，表示下载正在进行中。然后，将下载进度文本视图 mTvProgress 设置为可见。接着，通过 NIMClient 获取消息服务，使用 downloadAttachment 方法下载消息附件，其中 mIMMessage 是要下载附件的消息，false 表示不显示下载进度。最后，设置下载进度条 mProgressBar 为可见，用于显示下载进度。这段代码主要用于触发和控制视频下载的过程。

}
