package com.ezreal.ezchat.camera;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.graphics.PointF;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.ezreal.ezchat.R;
import com.ezreal.ezchat.utils.Constant;

import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Grid;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.controls.VideoCodec;
import com.otaliastudios.cameraview.size.SizeSelectors;
import com.otaliastudios.cameraview.video.encoding.MediaEncoderEngine;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wudeng on 2017/9/14.
 */

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    // 定义 CameraActivity 类，它继承自 AppCompatActivity 类，并实现了 View.OnClickListener 接口
    private static final String TAG = CameraActivity.class.getSimpleName();
    // 定义常量 TAG，用于记录日志，TAG 值为 CameraActivity 类的简单名称

    public static final int RESULT_IMAGE = 0x5001;
    // 定义常量 RESULT_IMAGE，用于表示返回结果的标识，通常用于区分返回的结果类型
    public static final int RESULT_VIDEO = 0x5002;
    // 定义常量 RESULT_VIDEO，用于表示返回结果的标识，通常用于区分返回的结果类型

    private static final int REQUEST_SHOW_IMG = 0x301;
    // 定义常量 REQUEST_SHOW_IMG，用于表示启动活动时的请求码，通常用于识别返回的结果是从哪个活动返回的
    private static final int REQUEST_SHOW_VIDEO = 0x302;
    // 定义常量 REQUEST_SHOW_VIDEO，用于表示启动活动时的请求码，通常用于识别返回的结果是从哪个活动返回的

//    这段代码定义了 CameraActivity 类，它是一个活动（Activity），继承自 AppCompatActivity 类，并实现了 View.OnClickListener 接口。在这个类中，还定义了一些常量，如 TAG 用于记录日志的标签，RESULT_IMAGE 和 RESULT_VIDEO 用于标识返回结果的类型，以及 REQUEST_SHOW_IMG 和 REQUEST_SHOW_VIDEO 用于启动其他活动时的请求码。这些常量通常用于活动之间的通信和识别不同的操作或结果。

    @BindView(R.id.camera_view)
    CameraView mCameraView;
// 使用 ButterKnife 库的注解 @BindView 将 XML 布局中的 CameraView 组件与 Java 代码中的 mCameraView 成员变量关联。

    @BindView(R.id.iv_flash_status)
    ImageView mIvFlash;
// 使用 ButterKnife 注解将 XML 布局中的 ImageView 组件与 Java 代码中的 mIvFlash 成员变量关联。

    @BindView(R.id.pre_record_time)
    NumberProgressBar mProgressBar;
// 使用 ButterKnife 注解将 XML 布局中的 NumberProgressBar 组件与 Java 代码中的 mProgressBar 成员变量关联。

    @BindView(R.id.iv_camera_btn)
    ImageView mIvCamera;
// 使用 ButterKnife 注解将 XML 布局中的 ImageView 组件与 Java 代码中的 mIvCamera 成员变量关联。

    @BindView(R.id.iv_exit)
    ImageView mIvExit;
// 使用 ButterKnife 注解将 XML 布局中的 ImageView 组件与 Java 代码中的 mIvExit 成员变量关联。

    @BindView(R.id.iv_change)
    ImageView mIvChange;
// 使用 ButterKnife 注解将 XML 布局中的 ImageView 组件与 Java 代码中的 mIvChange 成员变量关联。

    @BindView(R.id.tv_tip)
    TextView mTvTip;
// 使用 ButterKnife 注解将 XML 布局中的 TextView 组件与 Java 代码中的 mTvTip 成员变量关联。

//    这段代码是使用 ButterKnife 库的注解 @BindView 来将 XML 布局文件中的不同视图组件与 Java 代码中的成员变量进行关联。这样，在 Java 代码中就可以直接访问和操作这些视图组件，而不需要手动查找和设置它们。这种方式提高了代码的可读性和简洁性，同时减少了样板代码。
//
//    每个注解 @BindView 后面跟着一个成员变量的声明，其中 @BindView(R.id.xxx) 中的 R.id.xxx 是 XML 布局文件中相应视图组件的资源标识符。当 ButterKnife.bind(this) 方法被调用时，ButterKnife 将会在布局中查找对应资源标识符的视图组件，并将它们与成员变量建立关联，使这些成员变量可以直接操作布局中的视图。

    private static final Flash[] FLASH_OPTIONS = {
            Flash.OFF,
//            Flash.OFF 表示关闭闪光灯，不使用闪光。
            Flash.ON,
//            Flash.ON 表示打开闪光灯，强制启用闪光。
            Flash.AUTO
//            Flash.AUTO 表示自动模式，相机会根据光线条件自动决定是否使用闪光。
    };
//    这段代码定义了一个名为 FLASH_OPTIONS 的静态常量数组。这个数组包含了三个枚举值 Flash.OFF、Flash.ON 和 Flash.AUTO。这通常用于在相机应用中设置闪光灯选项。

    private static final Facing[] FACE_OPTIONS = {
//            Facing 是一个枚举类型，它定义了摄像头的朝向，通常包括前置和后置两种选项。
            Facing.BACK,
//            Facing.BACK 表示使用后置摄像头，它通常用于拍摄主要照片或视频。
            Facing.FRONT
//            Facing.FRONT 表示使用前置摄像头，通常用于自拍或视频通话。
    };
//    这段代码创建了一个名为 FACE_OPTIONS 的静态常量数组，用于存储摄像头朝向的选项。这个数组包含了两个 Facing 枚举值：Facing.BACK 和 Facing.FRONT。这通常用于应用程序中的相机功能，其中你可以让用户选择使用前置摄像头或后置摄像头拍摄照片或视频。

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_off,
//            R.drawable.ic_flash_off：代表闪光灯关闭的图标。
            R.drawable.ic_flash_on,
//            R.drawable.ic_flash_on：代表闪光灯开启的图标。
            R.drawable.ic_flash_auto,
//            R.drawable.ic_flash_auto：代表闪光灯自动模式的图标。
    };
//    这段代码定义了一个名为 FLASH_ICONS 的常量数组，其中包含了三个整数值，这些整数值对应了不同的闪光灯模式图标。这些图标通常用于相机应用或拍照功能中，以表示当前选择的闪光灯模式。

    private int mCurrentFlash = 0;
// 当前闪光灯模式的状态，通常用整数值表示不同状态，0 可能表示关闭闪光灯，1 表示开启闪光灯，等等。

    private int mCurrentFace = 0;
// 当前面部识别模式的状态，也通常用整数值表示不同状态，0 可能表示没有启用面部识别，1 表示启用面部识别，等等。

    private boolean isRecording;
// 一个布尔值，用于标识是否正在进行视频录制，true 表示正在录制，false 表示没有录制。

    private int mRecordTime;
// 记录已录制的视频时长的变量，通常用整数表示，单位可能是秒。

    private String mVideoPath;
// 存储当前录制的视频的文件路径，通常是一个字符串，指向文件的位置。

    private String mImagePath;
// 存储当前截取的图像的文件路径，通常是一个字符串，指向文件的位置。

    private static final int MSG_UPDATE_TIME = 0x1001;
// 一个常量，用于标识消息，通常作为消息处理的唯一标识。在这里，0x1001 被用于表示更新时间的消息。

//    这段代码声明了一些成员变量，它们用于跟踪和控制相机应用的不同状态和功能：
//
//    mCurrentFlash 和 mCurrentFace 是整数变量，分别用于跟踪当前的闪光灯和面部识别模式的状态。这些变量通常在应用中用于切换和显示相应的功能。
//
//    isRecording 是一个布尔变量，用于标识是否正在录制视频。如果为 true，表示正在录制；如果为 false，表示没有录制。
//
//    mRecordTime 是一个整数，通常用于记录已录制的视频时长，以秒为单位。
//
//    mVideoPath 和 mImagePath 是字符串，分别表示当前录制的视频文件路径和截取的图像文件路径。
//
//    MSG_UPDATE_TIME 是一个常量，通常用于标识消息。在这里，它被定义为 0x1001，可以用于唯一标识一个特定类型的消息，例如用于更新时间的消息。

    private Runnable mRecordRunnable = new Runnable() {
        // 创建一个新的 Runnable 对象 mRecordRunnable
        @Override
        public void run() {
            // 实现了 Runnable 接口的 run 方法
            while (isRecording) {
                // 当正在录音时执行以下操作
                try {
                    Thread.sleep(100);
                    // 使当前线程休眠 100 毫秒
                    mRecordTime += 1;
                    // 增加录音时间（以秒为单位）
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIME);
                    // 通过 mHandler 发送一条消息，消息标识为 MSG_UPDATE_TIME
                } catch (InterruptedException e) {
                    // 处理 InterruptedException 异常
                    e.printStackTrace();
                }
            }
        }
    };
//    这段代码定义了一个名为 mRecordRunnable 的 Runnable 对象。Runnable 是一个接口，用于在新线程中执行代码。这个 mRecordRunnable 被用于在后台线程中计时并发送更新时间的消息。

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        // 创建一个名为 mHandler 的 Handler 对象，同时使用 @SuppressLint("HandlerLeak") 注解忽略内存泄漏警告

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 处理消息，这是 Handler 的核心方法，用于接收和处理消息

            switch (msg.what) {
                case MSG_UPDATE_TIME:
                    // 检查消息的标识符是否为 MSG_UPDATE_TIME
                    mProgressBar.setProgress(mRecordTime);
                    // 更新进度条的进度，通常用于显示录音时的时间进度
                    if (mRecordTime > 100) {
                        stopRecord();
                    }
                    // 如果录音时间超过 100（这里的 100 很可能是一个时间限制），则停止录音
                    break;
            }
        }
    };
//    这段代码创建了一个名为 mHandler 的 Handler 对象，用于在 Android 应用中处理消息和更新用户界面。Handler 通常用于在不同线程之间传递和处理消息。

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 调用父类的 onCreate 方法，执行默认的初始化

        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 请求窗口没有标题，并且将窗口设置为全屏显示
        setContentView(R.layout.activity_camera);
        // 设置当前视图的内容为指定的布局文件

        ButterKnife.bind(this);
        // 使用 ButterKnife 绑定视图元素

        mCameraView.setVideoCodec(VideoCodec.H_264);
        // 设置相机视图的视频编解码器为 H.264
        mCameraView.setGrid(Grid.DRAW_PHI);
        // 设置相机视图上的网格类型
        mCameraView.setPlaySounds(false);
        // 设置是否播放声音
        mCameraView.setVideoBitRate(48000);
        // 设置视频比特率

        initListener();
        // 调用自定义的方法 initListener()，用于初始化事件监听器
    }
//    这段代码是在 Android Activity 的 onCreate 方法中执行的。它完成了以下操作：
//
//    隐藏标题栏并将应用窗口设置为全屏显示。
//    将当前视图的内容设置为一个指定的布局文件（R.layout.activity_camera），即将该布局文件显示在屏幕上。
//    使用 ButterKnife 库将当前视图绑定到该 Activity，以便更方便地访问视图元素。
//    针对 CameraView 控件，设置了视频编解码器为 H.264，绘制网格类型，是否播放声音以及视频比特率。
//    调用 initListener 方法，用于初始化事件监听器。
//    这段代码通常是在 Activity 创建时执行的，用于初始化界面、设置视图元素和事件监听。


    @SuppressLint("ClickableViewAccessibility")
// 忽略针对点击辅助功能的警告，通常是为了告知代码审查工具不要关注点击事件的可访问性问题。

    private void initListener() {
        // 初始化监听器方法

        mIvCamera.setOnClickListener(this);
        // 为相机图标设置点击监听器，当图标被点击时，将触发 `this`（当前活动）的点击事件处理方法。

        mIvFlash.setOnClickListener(this);
        // 同上，为闪光灯图标设置点击监听器。

        mIvExit.setOnClickListener(this);
        // 同上，为退出图标设置点击监听器。

        mIvChange.setOnClickListener(this);
        // 同上，为切换摄像头图标设置点击监听器。

        mIvCamera.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 当相机图标长按时触发的监听器
                startRecord();
                // 调用 `startRecord` 方法开始录像
                return true;
                // 返回 `true` 表示消耗了长按事件，不会触发短按事件
            }
        });

        mIvCamera.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 相机图标的触摸事件监听器
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 当触摸事件为按下时
                        break;
                    case MotionEvent.ACTION_UP:
                        // 当触摸事件为抬起时
                        if (isRecording) {
                            stopRecord();
                        }
                        // 如果正在录像，调用 `stopRecord` 方法停止录像
                        break;
                }
                return false;
                // 返回 `false`，以便其他触摸事件可以处理
            }
        });

        mCameraView.addCameraListener(new CameraListener() {
            // 为相机视图添加相机监听器
            @Override
            public void onCameraOpened(@NonNull CameraOptions options) {
                super.onCameraOpened(options);
                // 相机打开时触发的事件，可以在这里添加处理逻辑
            }

            @Override
            public void onCameraClosed() {
                super.onCameraClosed();
                // 相机关闭时触发的事件，可以在这里添加处理逻辑
                Log.i(TAG, "Camera Close");
            }

            @Override
            public void onCameraError(@NonNull CameraException exception) {
                super.onCameraError(exception);
                // 相机出现错误时触发的事件，可以在这里添加处理逻辑
                Log.e(TAG, "Camera Exception: " + exception.getMessage());
                Toast.makeText(CameraActivity.this,
                        "抱歉，相机出了点问题，请稍后重试", Toast.LENGTH_LONG).show();
                if (isRecording) {
                    startRecord();
                }
                // 如果正在录像，重新开始录像
            }

            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
                // 拍照完成后触发的事件
                mImagePath = Constant.APP_CACHE_IMAGE
                        + File.separator + System.currentTimeMillis() + ".jpeg";
                result.toFile(new File(mImagePath), new FileCallback() {
                    @Override
                    public void onFileReady(File file) {
                        // 当文件准备好后，会执行这个回调方法

                        Intent intent = new Intent(CameraActivity.this, CameraResultActivity.class);
                        // 创建一个意图，用于启动 CameraResultActivity 这个活动

                        intent.putExtra("imagePath", mImagePath);
                        // 将图片路径 mImagePath 作为额外数据添加到意图中，以便在 CameraResultActivity 中使用

                        intent.putExtra("FLAG", CameraResultActivity.FLAG_SHOW_IMG);
                        // 将一个标志 FLAG_SHOW_IMG 作为额外数据添加到意图中，以告知 CameraResultActivity 显示图片

                        startActivityForResult(intent, REQUEST_SHOW_IMG);
                        // 启动 CameraResultActivity，并等待其返回结果
                    }
                });
            }

            @Override
            public void onVideoTaken(@NonNull VideoResult result) {
                super.onVideoTaken(result);
                // 录像完成后触发的事件
                Intent intent = new Intent(CameraActivity.this, CameraResultActivity.class);
// 创建一个新的 Intent 对象，用于从 CameraActivity 启动 CameraResultActivity。

                intent.putExtra("videoPath", mVideoPath);
// 向 Intent 中添加一个额外的信息，名为 "videoPath"，并将 mVideoPath 的值传递给它。
// 这通常用于传递数据或参数到目标 Activity。

                intent.putExtra("FLAG", CameraResultActivity.FLAG_SHOW_VIDEO);
// 向 Intent 中添加另一个额外的信息，名为 "FLAG"，并将 CameraResultActivity 类的 FLAG_SHOW_VIDEO 常量值传递给它。
// 这也是为了在目标 Activity 中进行不同的操作或控制。

                startActivityForResult(intent, REQUEST_SHOW_VIDEO);
// 启动 CameraResultActivity 并期望在它完成后接收结果。这个结果将在 onActivityResult 方法中处理。
// REQUEST_SHOW_VIDEO 是一个请求码，用于标识启动的活动以及在结果处理中识别来自 CameraResultActivity 的响应。

            }

            // 其他相机事件监听方法可以在这里添加
//            这段代码初始化了各种监听器以处理摄像功能。通过设置点击监听器和长按监听器，可以实现拍照、闪光灯控制、录像等功能。同时，还设置了摄像机事件监听器，用于处理摄像机的各种事件，例如摄像机开启、关闭、拍照、录像等事件。

            @Override
            public void onVideoRecordingStart() {
                // 重写父类的 onVideoRecordingStart 方法
                super.onVideoRecordingStart();
                // 调用父类的 onVideoRecordingStart 方法，执行默认的操作
                Log.i(TAG, "onVideoRecordingStart");
                // 使用日志记录方法执行，以便调试和追踪应用的执行流程
            }
//            这段代码重写了某个父类（或接口）的 onVideoRecordingStart 方法。在这个重写的方法内部，首先调用了父类的 onVideoRecordingStart 方法，以执行默认的操作。然后，使用 Log.i 方法记录了一条信息，该信息被标记为 "onVideoRecordingStart"，并被打印到应用的日志中。

            @Override
            public void onVideoRecordingEnd() {
                super.onVideoRecordingEnd();
//                调用了基类的 onVideoRecordingEnd 方法，这是为了确保基类中的相关操作得以执行。
                Log.i(TAG,"onVideoRecordingEnd");
//                输出一条信息日志，记录视频录制结束的事件。这通常用于调试和记录应用程序中的事件，以便更容易跟踪和排查问题。
            }
//            这段代码位于一个类中，该类可能是一个继承了某个父类的子类，同时实现了视频录制结束时的操作。在实际应用中，你可以根据自己的需求在 onVideoRecordingEnd 方法中添加具体的逻辑来处理视频录制结束的情况。

            @Override
            public void onOrientationChanged(int orientation) {
                // 重写父类的 onOrientationChanged 方法
                super.onOrientationChanged(orientation);
                // 调用父类的 onOrientationChanged 方法
            }
//            这段代码是一个方法重写，重写了父类的 onOrientationChanged 方法。在这个重写的方法中，使用 super.onOrientationChanged(orientation) 调用了父类的相同方法。

            @Override
            public void onAutoFocusStart(PointF point) {
//                方法的声明。它指定了方法名称、参数列表和返回类型。
                super.onAutoFocusStart(point);
//                调用了父类的 onAutoFocusStart 方法，以确保执行父类的逻辑。
            }
//            这段代码是一个方法重写（@Override 标识的方法覆盖了父类或接口的方法）。这个方法 onAutoFocusStart 用于处理相机自动对焦功能开始的事件。

            @Override
            public void onAutoFocusEnd(boolean successful, PointF point) {
                super.onAutoFocusEnd(successful, point);
//                uccessful 表示自动对焦是否成功，point 可能是成功对焦的焦点坐标。
            }
//            方法的主要用途在于处理自动对焦结束时的操作。

            @Override
            public void onZoomChanged(float newValue,float[] bounds, PointF[] fingers) {
//                这是一个方法的定义，接受三个参数：newValue，bounds，和 fingers。这个方法用于处理缩放变化的事件。
                super.onZoomChanged(newValue, bounds, fingers);
//                这行代码调用了父类（超类）的 onZoomChanged 方法，以便执行与缩放事件相关的默认操作。
            }
//            这段代码的主要用途是在特定的缩放事件发生时，执行一些特定的操作。在子类中，你可以根据需要扩展这个方法，添加自定义的缩放行为。这通常用于与用户界面交互中的缩放操作，例如图片缩放或地图缩放等。

            @Override
            public void onExposureCorrectionChanged(float newValue, float[] bounds, PointF[] fingers) {
                // 当曝光补偿值发生变化时，将会触发这个方法
                super.onExposureCorrectionChanged(newValue, bounds, fingers);
                // 调用基类的相同方法，以确保正常处理曝光补偿的变化事件
            }
//            这段代码是一个方法覆盖（override），它重写了父类（或接口）中的 onExposureCorrectionChanged 方法。通常情况下，这种重写是为了在子类中实现特定的行为，以响应事件或处理数据。
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 覆盖父类的 onActivityResult 方法，处理子活动返回的结果

        if (resultCode == CameraResultActivity.RESULT_OK) {
            // 如果操作结果为 RESULT_OK（通常表示成功）
            if (requestCode == REQUEST_SHOW_IMG) {
                // 如果请求码指示为显示图片
                Intent intent = new Intent();
                intent.putExtra("imagePath", mImagePath);
                // 创建一个带有图片路径的意图
                this.setResult(RESULT_IMAGE, intent);
                // 设置结果码为 RESULT_IMAGE，将带有图片路径的意图返回给调用方
                this.finish();
                // 结束当前活动
            } else if (requestCode == REQUEST_SHOW_VIDEO) {
                // 如果请求码指示为显示视频
                Intent intent = new Intent();
                intent.putExtra("videoPath", mVideoPath);
                // 创建一个带有视频路径的意图
                this.setResult(RESULT_VIDEO, intent);
                // 设置结果码为 RESULT_VIDEO，将带有视频路径的意图返回给调用方
                this.finish();
                // 结束当前活动
            }

        } else if (resultCode == CameraResultActivity.RESULT_RESET) {
            // 如果操作结果为 RESULT_RESET（通常表示重置）
            if (requestCode == REQUEST_SHOW_VIDEO) {
                // 如果请求码指示为显示视频
                new File(mVideoPath).delete();
                // 删除视频文件
            }
        } else if (resultCode == CameraResultActivity.RESULT_CANCEL) {
            // 如果操作结果为 RESULT_CANCEL（通常表示取消）
            if (requestCode == REQUEST_SHOW_VIDEO) {
                // 如果请求码指示为显示视频
                new File(mVideoPath).delete();
                // 删除视频文件
            }
            this.finish();
            // 结束当前活动
        }
    }
//    这段代码覆盖了 onActivityResult 方法，用于处理从其他活动返回的结果。通常，Android应用中的不同活动（Activity）之间需要传递数据或者接收操作的结果。当一个子活动完成其任务后，会调用 setResult 方法来设置结果码和传递数据，然后通过 finish 方法关闭自身。在这段代码中，根据返回的结果码和请求码来执行相应的操作。如果结果为 RESULT_OK，表示操作成功，根据请求码分别将图片路径或视频路径放入意图中返回给调用方。如果结果为 RESULT_RESET，通常表示重置，这里删除视频文件。如果结果为 RESULT_CANCEL，通常表示取消，同样删除视频文件并结束当前活动。这个方法用于处理子活动的回调，以便在主活动中执行相应的操作。

    @Override
    public void onClick(View v) {
        // 当视图被点击时触发的回调方法

        switch (v.getId()) {
            // 根据点击的视图的ID进行不同的处理
            case R.id.iv_camera_btn:
                // 如果点击的是拍照按钮
                mCameraView.takePicture();
                // 调用相机视图的拍照方法
                break;
            case R.id.iv_flash_status:
                // 如果点击的是闪光灯状态按钮
                mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                // 切换闪光灯模式，根据数组 FLASH_OPTIONS 中的选项，循环切换
                mIvFlash.setBackgroundResource(FLASH_ICONS[mCurrentFlash]);
                // 更新闪光灯按钮的图标
                mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
                // 设置相机视图的闪光灯模式
                break;
            case R.id.iv_change:
                // 如果点击的是切换摄像头按钮
                mCurrentFace = (mCurrentFace + 1) % 2;
                // 切换摄像头，根据数组 FACE_OPTIONS 中的选项，循环切换
                mCameraView.setFacing(FACE_OPTIONS[mCurrentFace]);
                // 设置相机视图的摄像头
                break;
            case R.id.iv_exit:
                // 如果点击的是退出按钮
                finish();
                // 关闭当前活动页面
                break;
        }
    }
//    这段代码实现了一个 View.OnClickListener 接口中的 onClick 方法，用于处理视图的点击事件。在这个方法中，通过 switch 语句根据点击的视图的 ID 执行不同的操作：
//
//    R.id.iv_camera_btn：点击拍照按钮，调用相机视图的 takePicture 方法来拍照。
//    R.id.iv_flash_status：点击闪光灯状态按钮，切换闪光灯模式，更新按钮图标，并设置相机视图的闪光灯模式。
//    R.id.iv_change：点击切换摄像头按钮，切换摄像头，设置相机视图的摄像头。
//    R.id.iv_exit：点击退出按钮，关闭当前活动页面（Activity）。
//    这个方法用于响应用户的交互操作，根据不同的按钮点击执行相应的操作。

    private void startRecord() {
        // 启动录制功能的方法
        // 生成视频文件的保存路径，通常以当前时间命名，确保唯一性
        mVideoPath = Constant.APP_CACHE_VIDEO + File.separator +  System.currentTimeMillis() + ".mp4";
        // 创建一个文件对象，用于保存视频
        File file = new File(mVideoPath);
        // 设置录制状态为正在录制
        isRecording = true;
        // 设置摄像头视图为视频模式
        mCameraView.setMode(Mode.VIDEO);
        // 调用摄像头视图的 takeVideo 方法开始录制视频，将视频保存到指定文件中
        mCameraView.takeVideo(file);
        // 设置进度条的进度为0
        mProgressBar.setProgress(0);
        // 显示进度条
        mProgressBar.setVisibility(View.VISIBLE);
        // 隐藏提示文本
        mTvTip.setVisibility(View.INVISIBLE);
        // 更改拍摄按钮图标为录制状态
        mIvCamera.setImageResource(R.mipmap.record);
        // 启动一个新线程来处理录制视频的逻辑
        new Thread(mRecordRunnable).start();
    }
//    这段代码是一个方法 startRecord，它用于启动录制功能。在这个方法中：
//
//    mVideoPath 用于保存视频文件的路径，它通常以当前时间命名以确保唯一性。
//    File 对象 file 被创建，用于保存录制的视频。
//    isRecording 被设置为 true，表示当前处于录制状态。
//    mCameraView 设置为视频模式，用于录制视频。
//            mCameraView.takeVideo(file) 启动摄像头开始录制视频，视频会保存到 file 中。
//    进度条 mProgressBar 的进度被设置为0，然后显示出来。
//    提示文本 mTvTip 被隐藏。
//    拍摄按钮 mIvCamera 的图标被更改为录制状态图标。
//    最后，启动一个新线程 mRecordRunnable 来处理录制视频的逻辑。
//    这段代码用于启动录制视频功能，当用户点击录制按钮时，将调用这个方法来开始录制视频。


    private void stopRecord() {
        // 停止录制视频
        mCameraView.stopVideo();

        // 切换相机预览模式为拍照模式
        mCameraView.setMode(Mode.PICTURE);

        // 重置录制时间
        mRecordTime = 0;

        // 设置录制状态为未录制
        isRecording = false;

        // 更改拍摄按钮图标为拍照状态
        mIvCamera.setImageResource(R.mipmap.capture);

        // 重置进度条的进度
        mProgressBar.setProgress(0);

        // 显示提示文本
        mTvTip.setVisibility(View.VISIBLE);

        // 隐藏进度条
        mProgressBar.setVisibility(View.INVISIBLE);
    }
//    这段代码定义了一个名为 stopRecord 的方法。该方法的作用是停止录制视频，将相机视图切换到拍照模式，并重置相关状态和UI元素。

    @Override
    protected void onResume() {
//        这是 onResume 方法的定义，它在 Activity 生命周期中表示当 Activity 即将可见时会被调用。
        super.onResume();
//        调用父类（Activity）的 onResume 方法，以确保它的标准操作也被执行。
        mCameraView.open();
//        调用 mCameraView 的 open 方法，用于打开摄像头或启动相机功能。
    }
//    这段代码表示当 Activity 进入 onResume 生命周期阶段时，它会调用 mCameraView 的 open 方法，以准备摄像头并启动相机功能，以便用户可以拍摄或查看图像。

    @Override
    protected void onPause() {
        // 重写 Activity 的 onPause 方法
        mCameraView.close();
        // 调用 CameraView 的 close 方法
        super.onPause();
        // 调用基类的 onPause 方法
    }
//    这段代码是在 Android Activity 类中重写的 onPause 方法。在方法内部，有以下操作：
//
//            mCameraView.close();: 这一行代码调用了 mCameraView 的 close 方法。这可能是一个自定义的相机视图或摄像头操作类的实例，用于释放相机资源或关闭摄像头。在 onPause 中关闭摄像头是一种良好的做法，以确保在应用不可见时释放摄像头资源，以避免资源泄漏。
//
//            super.onPause();: 这一行代码调用了基类的 onPause 方法，以确保 Activity 的其他标准生命周期事件得以处理。这是在自定义 onPause 方法中的通用做法，以维护标准的 Activity 生命周期。
//
//    总之，这段代码在 Activity 的 onPause 方法中关闭了相机或释放了相机资源，然后调用了基类 onPause 方法以继续处理其他相关的生命周期事件。这是为了确保应用在不可见或失去焦点时能够正确地管理相机资源。

    @Override
    protected void onDestroy() {
        // 当 Activity 即将被销毁时触发的方法
        mHandler.removeCallbacksAndMessages(null);
        // 移除与 Handler 相关的所有未执行的回调和消息，以避免在 Activity 销毁后可能导致的内存泄漏。
        super.onDestroy();
        // 调用父类的 onDestroy 方法，以确保释放相关资源和执行必要的清理操作。
        mCameraView.destroy();
        // 销毁相机预览视图，释放相机资源以及相关的资源，以避免内存泄漏。
    }
//    这段代码是 Android Activity 生命周期中的 onDestroy 方法的重写。onDestroy 方法在 Activity 即将被销毁时调用，通常在该方法中执行一些资源释放和清理操作，以确保应用程序在退出或销毁该 Activity 时不会出现问题。

}
