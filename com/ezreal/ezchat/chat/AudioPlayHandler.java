package com.ezreal.ezchat.chat;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.ezreal.ezchat.R;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 播放音频附件，并通过更新界面实现动画效果
 * Created by wudeng on 2017/11/2.
 */

public class AudioPlayHandler {

    //语音动画控制器
    private Timer mTimer = null;
    //语音动画控制任务
    private TimerTask mTimerTask = null;
    //记录语音动画图片索引
    private int index = 0;
    //根据 index 更换动画
    private AudioAnimHandler mAudioAnimHandler = null;

    /**
     * 播放音频动画
     */
    public void startAudioAnim(ImageView imageView, boolean isLeft) {
        // 开始语音播放动画

        // 先停止之前的动画
        stopAnimTimer();

        // 创建一个 AudioAnimHandler 处理语音动画的显示
        mAudioAnimHandler = new AudioAnimHandler(imageView, isLeft);

        // 创建一个 TimerTask，用于定期切换语音播放动画的帧
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                // 发送消息给 mAudioAnimHandler，用于更新动画帧
                mAudioAnimHandler.obtainMessage(index).sendToTarget();
                index = (index + 1) % 3; // 在 3 帧之间循环切换
            }
        };

        mTimer = new Timer();
        // 每半秒更新一次界面
        mTimer.schedule(mTimerTask, 0, 500);
    }
//    这段代码用于启动一个语音播放的动画，具体说明如下：
//
//    stopAnimTimer(): 这个方法用于停止之前正在运行的动画，确保只有一个动画在播放。
//    mAudioAnimHandler: 这是一个处理语音动画显示的处理器，用于切换不同的动画帧。
//    mTimerTask: 这是一个定时任务，它在指定的时间间隔内执行一次任务，用于更新语音播放动画的帧。
//    mTimer: 这是一个计时器，用于调度 mTimerTask 的执行。
//    index: 用于跟踪当前应该显示哪一帧的语音播放动画。
//    通过定时任务和处理器，此方法创建了一个可循环播放的语音动画，每隔 0.5 秒切换到下一帧，直到停止。这通常用于在聊天应用中，模拟语音消息的播放动画效果。

    /**
     * 停止语音播放动画
     */
    public void stopAnimTimer() {
        // 停止音频动画的定时器

        if (mTimer != null) {
            mTimer.cancel();
            // 如果 mTimer 不为 null，则取消计时器
            mTimer = null;
            // 置 mTimer 为 null
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            // 如果 mTimerTask 不为 null，则取消计时任务
            mTimerTask = null;
            // 置 mTimerTask 为 null
        }

        // 将上一个语音消息界面复位
        if (mAudioAnimHandler != null) {
            mAudioAnimHandler.obtainMessage(3).sendToTarget();
            // 如果 mAudioAnimHandler 不为 null，则发送消息给它，用于复位上一个语音消息的界面状态
        }
    }
//    这段代码定义了一个方法 stopAnimTimer()，用于停止音频消息的动画定时器。在方法内部：
//
//    首先，它检查 mTimer 和 mTimerTask 是否为非空（即不为 null），如果它们不为 null，则取消它们以停止定时器和计时任务。
//
//    最后，如果 mAudioAnimHandler 不为 null，它将通过 mAudioAnimHandler 发送消息，用于复位上一个语音消息的界面状态。
//
//    这段代码主要用于管理音频消息的动画定时器，当不再需要播放或处理音频消息时，可以调用此方法来停止动画和计时。

    private static class AudioAnimHandler extends Handler {
        // 创建 AudioAnimHandler 内部类，继承自 Handler

        private final ImageView mIvAudio;
        // 一个用于显示音频动画的 ImageView 对象
        private final boolean isLeft;
        // 一个标志，指示音频动画的位置是否在左边

        private int[] mLeftIndex = {
                R.mipmap.sound_left_1, R.mipmap.sound_left_2, R.mipmap.sound_left_3
        };
        // 存储左边位置音频动画资源的数组

        private int[] mRightIndex = {
                R.mipmap.sound_right_1, R.mipmap.sound_right_2, R.mipmap.sound_right_3
        };
        // 存储右边位置音频动画资源的数组

        AudioAnimHandler(ImageView imageView, boolean isLeft) {
            // 构造函数，接受一个 ImageView 和一个布尔值作为参数
            this.mIvAudio = imageView;
            // 初始化 mIvAudio，指定用于显示音频动画的 ImageView
            this.isLeft = isLeft;
            // 初始化 isLeft，指示音频动画的位置是否在左边
        }
//        这段代码定义了一个名为 AudioAnimHandler 的内部类，它继承自 Handler 类。AudioAnimHandler 用于处理音频动画的相关操作。在这个类中，有两个数组 mLeftIndex 和 mRightIndex，它们分别存储了左边位置和右边位置的音频动画资源的引用（R.mipmap）。这个类的构造函数接受一个 ImageView 和一个布尔值作为参数，分别用于设置用于显示音频动画的 ImageView 对象和指示音频动画位置的标志。
//
//        这个类的主要作用是协助控制音频动画的显示，具体的逻辑可能包括根据不同状态切换动画资源、控制动画的播放和停止等。根据项目的具体需求，AudioAnimHandler 可能会有不同的实现，但通常用于管理与音频动画相关的 UI 操作。

        @Override
        public void handleMessage(Message msg) {
            // 重写父类的 handleMessage 方法
            super.handleMessage(msg);
            // 调用父类的 handleMessage 方法以处理消息

            switch (msg.what) {
                // 检查消息的类型，这里使用 msg.what 来判断
                case 0:
                    // 当消息类型是 0 时
                    mIvAudio.setImageResource(isLeft ? mLeftIndex[0] : mRightIndex[0]);
                    // 根据 isLeft 的值选择设置 ImageView 图片资源为 mLeftIndex[0] 或 mRightIndex[0]
                    break;
                case 1:
                    // 当消息类型是 1 时
                    mIvAudio.setImageResource(isLeft ? mLeftIndex[1] : mRightIndex[1]);
                    // 根据 isLeft 的值选择设置 ImageView 图片资源为 mLeftIndex[1] 或 mRightIndex[1]
                    break;
                case 2:
                    // 当消息类型是 2 时
                    mIvAudio.setImageResource(isLeft ? mLeftIndex[2] : mRightIndex[2]);
                    // 根据 isLeft 的值选择设置 ImageView 图片资源为 mLeftIndex[2] 或 mRightIndex[2]
                    break;
                default:
                    // 如果消息类型不是上述的任何一个
                    mIvAudio.setImageResource(isLeft ? R.mipmap.sound_left_0 : R.mipmap.sound_right_0);
                    // 根据 isLeft 的值选择设置 ImageView 图片资源为 R.mipmap.sound_left_0 或 R.mipmap.sound_right_0
                    removeCallbacks(null);
                    // 移除所有的消息回调
                    break;
            }
        }
//        这段代码展示了一个用于处理消息的方法，通常在 Android 中用于更新 UI。它在不同的消息类型下设置了一个 ImageView 的图片资源。这是一个通过 switch-case 语句来处理消息类型的示例。当 msg.what 等于 0、1、2 时，分别设置 ImageView 的图片资源为 mLeftIndex 或 mRightIndex 数组中的不同值。如果消息类型不是上述的任何一个，它将设置默认的图片资源，同时移除所有的消息回调。这个方法的实际用途是用于控制播放声音的动画效果，当不同的声音状态（消息类型）发生时，更新相应的动画效果。
    }

}
