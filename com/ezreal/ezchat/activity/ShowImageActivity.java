package com.ezreal.ezchat.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bm.library.PhotoView;
import com.ezreal.ezchat.R;
import com.ezreal.ezchat.utils.ConvertUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.model.AttachmentProgress;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.suntek.commonlibrary.utils.TextUtils;
import com.suntek.commonlibrary.utils.ToastUtils;
import com.suntek.commonlibrary.widget.PinchImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wudeng on 2017/10/20.
 */

// 创建一个名为 "ShowImageActivity" 的 Java 类
public class ShowImageActivity extends BaseActivity {

    // 创建一个 TAG 常量，用于标识这个 Activity 类
    private static final String TAG = ShowImageActivity.class.getSimpleName();

    // 使用 ButterKnife 绑定视图元素
    // 使用ButterKnife注解绑定的方式，将XML布局中指定ID的ImageView和TextView控件与对应的Java代码中的变量绑定
// 这样可以方便地在Java代码中操作这些控件，而不需要手动查找和初始化它们
    @BindView(R.id.image_view)
    PhotoView mIvImage; // 将指定ID为R.id.image_view的ImageView绑定到变量mIvImage上

    @BindView(R.id.tv_show_big)
    TextView mTvShowBig; // 将指定ID为R.id.tv_show_big的TextView绑定到变量mTvShowBig上


    // 创建 IM 消息对象
    private IMMessage mMessage;

    // 创建观察者对象，用于监听附件下载进度
    private Observer<AttachmentProgress> mProgressObserver;

    // 创建一个标志，表示是否正在下载中
    private boolean downloading = false;
//    这段代码是一个 Android Activity 类，用于显示图像。它包括一些视图元素、IM 消息对象以及一个附件下载进度的观察者。下面是它的一些用途：
//
//    ShowImageActivity 类用于显示图像的 Activity。
//
//    TAG 常量是用于标识这个 Activity 类的标签。
//
//    @BindView 注解用于将 XML 布局中的视图元素与 Java 代码中的成员变量关联起来。
//
//    mIvImage 是用于显示图像的 PhotoView 视图。
//
//    mTvShowBig 是用于显示图像放大选项的 TextView 视图。
//
//    mMessage 是一个 IM 消息对象，用于包含图像数据。
//
//    mProgressObserver 是用于监听附件下载进度的观察者。
//
//    downloading 是一个标志，用于表示是否正在下载图像附件。

    // 创建一个 Handler 对象，使用 @SuppressLint 注解忽略潜在的内存泄漏警告
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // 检查是否正在下载图片，并且消息的标识为 0x300
            if (downloading && msg.what == 0x300){
                // 获取图片附件的本地路径
                String path = ((ImageAttachment) mMessage.getAttachment()).getPath();

                // 如果本地路径不为空
                if (!TextUtils.isEmpty(path)){
                    downloading =  false;
                    mTvShowBig.setVisibility(View.GONE); // 隐藏“查看大图”视图

                    // 从本地路径解码并创建位图
                    Bitmap bitmap = BitmapFactory.decodeFile(path);

                    // 如果位图不为空，将其设置为 ImageView 中显示
                    if (bitmap != null) {
                        mIvImage.setImageBitmap(bitmap);
                    }
                } else {
                    // 如果本地路径为空，1秒后再次发送消息 0x300，进行轮询下载
                    mHandler.sendEmptyMessageAtTime(0x300,1000);
                }
            }
        }
    };
//    这段代码创建了一个 Handler 对象，用于处理消息。当消息的标识为0x300时，它会检查是否正在下载图片，然后获取图片附件的本地路径。如果本地路径不为空，它会将下载标志设置为false，隐藏“查看大图”视图，然后从本地路径解码图片并将其显示在 ImageView 中。如果本地路径为空，它会在1秒后再次发送消息0x300，以进行轮询下载图片。这通常用于异步下载图片并在下载完成后显示它们。

    // 在创建活动时执行的方法
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 隐藏窗口的标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 设置窗口标志，使窗口全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 设置当前活动使用的布局
        setContentView(R.layout.activity_show_image);

        // 使用ButterKnife绑定视图控件
        ButterKnife.bind(this);

        // 创建一个用于监视附件下载进度的观察者
        mProgressObserver = new Observer<AttachmentProgress>() {
            @Override
            public void onEvent(AttachmentProgress progress) {
                // 检查是否正在下载，并且进度的 UUID 与消息的 UUID 匹配
                if (downloading && progress.getUuid().equals(mMessage.getUuid())) {
                    // 计算下载进度百分比
                    int present = (int) (progress.getTransferred() /
                            (progress.getTotal() * 1.0f) * 100.0f);

                    // 创建下载进度文本
                    String text = "下载中…… " + String.valueOf(present) + "%";
                    mTvShowBig.setText(text);

                    // 检查下载进度是否达到 60%，如果是则延迟 1 秒后发送消息
                    if (present >= 60) {
                        mHandler.sendEmptyMessageAtTime(0x300, 1000);
                    }
                }
            }
        };

        // 初始化显示图片的方法
        initImage();
    }
//    这段代码是在创建活动时执行的，它的主要功能包括：
//
//    隐藏窗口的标题，以确保全屏显示。
//    设置窗口标志，使窗口全屏显示。
//    设置当前活动使用的布局，即R.layout.activity_show_image。
//    使用ButterKnife库绑定视图控件，简化视图操作。
//    创建一个观察者（mProgressObserver），用于监视附件下载的进度。
//    在mProgressObserver的onEvent方法中，处理附件下载的进度信息，更新下载进度的文本，并根据下载进度发送消息。
//    最后，调用initImage方法，初始化显示图片。

    // 当 Activity 进入 onResume 状态时
    @Override
    protected void onResume() {
        super.onResume();
        // 通过 NIMClient 获取消息服务观察者，并观察附件上传进度（消息中的附件如图片、音频等的上传进度）
        NIMClient.getService(MsgServiceObserve.class)
                .observeAttachmentProgress(mProgressObserver, true);
    }
//    这段代码的作用是在 Activity 进入 onResume 状态时，启用消息服务的附件上传进度观察者。这可以用于监控消息附件的上传进度，通常用于显示进度条或其他与附件上传相关的用户界面。在这里，mProgressObserver 是用于处理附件上传进度的观察者对象，true 参数表示启用该观察者。

    // 覆盖基类的销毁方法
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 获取 NIM 客户端服务中的消息服务观察器
        MsgServiceObserve msgServiceObserve = NIMClient.getService(MsgServiceObserve.class);

        // 停止观察附件传输进度，传递 mProgressObserver 参数表示停止观察这一进度
        msgServiceObserve.observeAttachmentProgress(mProgressObserver, false);
    }
//    这段代码在 Activity 销毁时停止观察附件传输进度。它通过获取 NIM（网易云信）客户端服务中的消息服务观察器来实现。observeAttachmentProgress 方法用于观察附件传输的进度，传递 mProgressObserver 参数表示停止观察这一进度，false 表示停止观察。这通常用于资源的释放和取消监听以防止内存泄漏。

    // 初始化图片显示组件
    private void initImage() {
        mIvImage.enable();
        mIvImage.setMaxScale(4);

        // 从传递的Intent中获取IM消息对象
        mMessage = (IMMessage) getIntent().getSerializableExtra("IMMessage");

        // 隐藏查看原图的文本视图
        mTvShowBig.setVisibility(View.GONE);

        // 如果IM消息为空，显示错误提示并结束活动
        if (mMessage == null) {
            ToastUtils.showMessage(this, "图片无法显示，请重试~");
            finish();
            return;
        }

        // 获取原始图片路径、缩略图路径和文件大小
        String path = ((ImageAttachment) mMessage.getAttachment()).getPath();
        String thumbPath = ((ImageAttachment) mMessage.getAttachment()).getThumbPath();
        long size = ((ImageAttachment) mMessage.getAttachment()).getSize();

        // 如果原图已经下载并传输，显示原图
        if (mMessage.getAttachStatus() == AttachStatusEnum.transferred
                && !TextUtils.isEmpty(path)) {
            // 从文件路径解码原图并显示在ImageView中
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                mIvImage.setImageBitmap(bitmap);
            } else {
                // 如果解码失败，显示错误提示并结束活动
                ToastUtils.showMessage(this, "原图下载/显示失败，请重试~");
                finish();
            }
        } else {
            // 否则，显示缩略图
            if (!TextUtils.isEmpty(thumbPath)) {
                // 从文件路径解码缩略图并显示在ImageView中
                Bitmap bitmap = BitmapFactory.decodeFile(thumbPath);
                if (bitmap != null) {
                    mIvImage.setImageBitmap(bitmap);
                }
            }

            // 格式化文件大小以显示在查看原图的文本视图上
            String sizeString = ConvertUtils.getSizeString(size);
            String text = "查看原图 (" + sizeString + ")";
            mTvShowBig.setText(text);
            mTvShowBig.setVisibility(View.VISIBLE);
        }
    }
//    这段代码的主要功能是初始化图片显示，它通过传递的IM消息对象来显示原始图片或缩略图。如果原图已经下载并传输，将解码并显示原图；否则，显示缩略图以及一个“查看原图”的链接。如果IM消息为空，将显示错误提示并结束活动。

    // 当用户点击具有ID "tv_show_big" 的视图时执行此方法
    @OnClick(R.id.tv_show_big)
    public void showBigImage() {
        // 设置标志以指示正在下载大图像
        downloading = true;
        // 更新文本视图以显示下载中的状态
        mTvShowBig.setText("下载中……");

        // 获取 NIMClient 中的消息服务(MsgService) 并下载消息的附件（大图像），并指定不开启后台下载
        NIMClient.getService(MsgService.class).downloadAttachment(mMessage, false);

        // 设置文本视图为不可点击，以防止多次触发下载操作
        mTvShowBig.setClickable(false);
    }
//    这段代码是一个点击事件处理方法。当用户点击一个ID为 "tv_show_big" 的视图（通常是一个按钮）时，会执行此方法。该方法的主要目的是下载消息的附件（大图像）。在开始下载之前，它将设置一个标志以指示下载正在进行中，更新文本视图以显示下载中的状态，然后使用NIM SDK中的downloadAttachment方法下载附件（在本例中是大图像），并禁止再次点击"tv_show_big"按钮。

}

