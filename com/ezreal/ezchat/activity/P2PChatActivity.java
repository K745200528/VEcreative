package com.ezreal.ezchat.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amap.api.services.core.LatLonPoint;
import com.ezreal.audiorecordbutton.AudioPlayManager;
import com.ezreal.ezchat.R;
import com.ezreal.ezchat.camera.CameraActivity;
import com.ezreal.ezchat.chat.AudioPlayHandler;
import com.ezreal.ezchat.chat.ChatMsgHandler;
import com.ezreal.ezchat.chat.ChatSession;
import com.ezreal.ezchat.chat.MessageListAdapter;
import com.ezreal.ezchat.chat.OnItemClickListener;
import com.ezreal.ezchat.chat.RViewHolder;
import com.ezreal.ezchat.handler.NimUserHandler;
import com.ezreal.ezchat.widget.ChatInputLayout;
import com.ezreal.ezchat.widget.MsgRecyclerView;
import com.ezreal.photoselector.PhotoSelectActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.AttachmentProgress;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.suntek.commonlibrary.utils.TextUtils;
import com.suntek.commonlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 点对点聊天界面
 * Created by wudeng on 2017/9/4.
 */

// 创建 P2P 单聊界面的活动类，该类扩展自 BaseActivity
public class P2PChatActivity extends BaseActivity
        implements ChatMsgHandler.OnLoadMsgListener, ChatInputLayout.OnInputLayoutListener {

    private static final String TAG = P2PChatActivity.class.getSimpleName();
    // 定义一个 TAG 常量，用于日志标识

    private static final long TEN_MINUTE = 1000 * 60 * 10;
    // 定义一个常量表示十分钟的毫秒数，用于后续的时间计算

    private static final int SELECT_PHOTO = 0x6001;
    private static final int START_CAMERA = 0x6002;
    private static final int SELECT_LOCATION = 0x6003;
    // 定义一些常量来表示不同的操作，如选择照片、启动相机和选择位置

//    这段代码定义了一个名为 P2PChatActivity 的类，它是一个聊天界面的活动。这个类实现了两个接口，ChatMsgHandler.OnLoadMsgListener 和 ChatInputLayout.OnInputLayoutListener，并定义了一些常量用于标识不同的操作。此外，还有一个 TAG 常量用于标识日志消息，以便日志记录和调试。

    // 使用 ButterKnife 注解，将布局中的 R.id.rcv_msg_list 绑定到 MsgRecyclerView 变量
    @BindView(R.id.rcv_msg_list)
    MsgRecyclerView mRecyclerView;

    // 使用 ButterKnife 注解，将布局中的 R.id.input_layout 绑定到 ChatInputLayout 变量
    @BindView(R.id.input_layout)
    ChatInputLayout mInputLayout;
//    这段代码使用 ButterKnife 库对布局文件中的两个视图进行绑定，以便在代码中可以方便地访问和操作这两个视图。@BindView 注解允许将 XML 布局文件中的特定视图与 Java 代码中的变量关联起来。在这里，R.id.rcv_msg_list 对应 MsgRecyclerView 变量，R.id.input_layout 对应 ChatInputLayout 变量。这样，开发者就可以直接通过 mRecyclerView 和 mInputLayout 来操作和修改这两个视图，而不需要手动查找和绑定它们。

    // 创建聊天消息处理器对象
    private ChatMsgHandler mChatHandler;
    // 创建聊天会话对象
    private ChatSession mChatSession;
    // 创建线性布局管理器
    private LinearLayoutManager mLayoutManager;
    // 创建消息列表
    private List<IMMessage> mMsgList;
    // 创建消息列表适配器
    private MessageListAdapter mListAdapter;
    // 创建消息接收观察者
    private Observer<List<IMMessage>> mMsgReceiveObserver;
    // 创建消息状态观察者
    private Observer<IMMessage> mMsgStatusObserver;
    // 创建附件传输进度观察者
    private Observer<AttachmentProgress> mProgressObserver;

    // 创建音频播放处理器
    private AudioPlayHandler mAudioPlayHandler;
    // 创建标志以指示是否正在播放音频
    private boolean isAudioPlay = false;
    // 存储当前播放的音频消息的ID
    private String mPlayId = "";
//    这段代码定义了一系列变量，它们用于处理聊天消息和相关功能。这包括了聊天消息的处理、消息列表的显示、消息状态的观察、附件传输进度的观察、音频播放等。这些变量用于维护和控制聊天界面的各种功能，如消息接收、消息发送、消息状态更新、音频播放等等。

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置状态栏颜色
        setStatusBarColor(R.color.app_blue_color);

        // 设置视图布局为指定的 XML 布局文件
        setContentView(R.layout.activity_p2p_chat);

        // 创建点对点聊天会话
        createChatSession();

        // 初始化标题栏
        initTitle();

        // 使用 ButterKnife 进行视图绑定
        ButterKnife.bind(this);

        // 设置输入布局的监听器
        mInputLayout.setLayoutListener(this);

        // 将输入布局绑定到 RecyclerView
        mInputLayout.bindInputLayout(this, mRecyclerView);

        // 初始化消息列表
        initMsgList();

        // 初始化监听器
        initListener();

        // 注册监听

        // 注册消息接收监听器
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(mMsgReceiveObserver, true);

        // 注册消息状态监听器
        NIMClient.getService(MsgServiceObserve.class)
                .observeMsgStatus(mMsgStatusObserver, true);

        // 注册附件上传进度监听器
        NIMClient.getService(MsgServiceObserve.class)
                .observeAttachmentProgress(mProgressObserver, true);

        // 加载历史消息数据
        loadMessage();
    }
//    这段代码是 Android Activity 的 onCreate 方法，负责初始化界面和各种监听器。具体包括：
//
//    设置状态栏颜色。
//    将视图布局设置为指定的 XML 布局文件。
//    创建点对点聊天会话。
//    初始化标题栏。
//    使用 ButterKnife 进行视图绑定。
//    设置输入布局的监听器。
//    将输入布局与 RecyclerView 绑定。
//    初始化消息列表。
//    初始化监听器。
//    注册消息接收监听器，用于监听接收到的消息。
//    注册消息状态监听器，用于监听消息状态的改变。
//    注册附件上传进度监听器，用于监听附件上传的进度。
//    加载历史消息数据。

    /**
     * 初始化 当前聊天会话
     */
    // 创建一个聊天会话
    private void createChatSession() {
        // 从Intent中获取聊天对象的NimUserInfo
        NimUserInfo chatInfo = (NimUserInfo) getIntent().getSerializableExtra("NimUserInfo");

        // 获取当前用户的NimUserInfo
        NimUserInfo myInfo = NimUserHandler.getInstance().getUserInfo();

        // 创建一个新的聊天会话对象
        mChatSession = new ChatSession();

        // 设置会话的唯一标识，这通常是聊天对象的账号
        mChatSession.setSessionId(chatInfo.getAccount());

        // 设置会话类型为点对点（P2P）聊天
        mChatSession.setSessionType(SessionTypeEnum.P2P);

        // 设置聊天会话的聊天对象账号
        mChatSession.setChatAccount(chatInfo.getAccount());

        // 设置聊天会话的当前用户账号
        mChatSession.setMyAccount(myInfo.getAccount());

        // 设置聊天会话的聊天对象信息
        mChatSession.setChatInfo(chatInfo);

        // 设置聊天会话的当前用户信息
        mChatSession.setMyInfo(myInfo);

        // 创建一个ChatMsgHandler用于处理聊天消息
        mChatHandler = new ChatMsgHandler(this, mChatSession);
    }
//    这段代码的目的是创建一个聊天会话，其中包括了聊天对象的信息、当前用户的信息以及用于处理聊天消息的处理程序。首先，它从Intent中获取了聊天对象的信息，然后获取了当前用户的信息。接着，它创建了一个ChatSession对象，设置了会话的各种属性，包括会话ID、会话类型、聊天对象账号、当前用户账号，聊天对象信息和当前用户信息。最后，它创建了一个ChatMsgHandler来处理聊天消息。

    /**
     * 初始化标题栏
     */
    // 初始化标题栏
    private void initTitle() {
        if (!TextUtils.isEmpty(mChatSession.getChatNick())) {
            // 如果聊天会话的昵称不为空，将其设置为标题，并显示返回按钮和更多菜单按钮
            setTitleBar(mChatSession.getChatNick(), true, true);
        } else {
            // 如果聊天会话的昵称为空，使用会话信息的名称作为标题，并显示返回按钮和更多菜单按钮
            setTitleBar(mChatSession.getChatInfo().getName(), true, true);
        }

        // 设置更多菜单按钮的图标为 "people" 图标
        mIvMenu.setImageResource(R.mipmap.people);

        // 为更多菜单按钮设置点击事件监听器
        mIvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在点击时显示一个简单的开发中提示
                ToastUtils.showMessage(P2PChatActivity.this,"开发中");
            }
        });
    }
//    这段代码用于初始化标题栏，根据聊天会话的信息设置标题，并添加一个更多菜单按钮。当用户点击更多菜单按钮时，它会显示一个简单的提示消息。

    /**
     * 初始化消息列表
     */
    // 初始化消息列表
    private void initMsgList() {
        // 获取消息列表的布局管理器
        mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        // 创建一个消息列表
        mMsgList = new ArrayList<>();

        // 创建消息列表适配器，并关联消息列表和当前聊天会话
        mListAdapter = new MessageListAdapter(this, mMsgList, mChatSession);

        // 设置消息列表项的点击监听器
        mListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RViewHolder holder, IMMessage message) {
                // 根据消息类型执行不同的操作
                // 根据消息的类型执行相应的操作
                switch (message.getMsgType()) {
                    case image:
                        // 如果消息类型是图片，调用showAttachOnActivity方法显示图片
                        showAttachOnActivity(ShowImageActivity.class, message);
                        break;
                    case audio:
                        // 如果消息类型是音频，调用playAudio方法播放音频
                        playAudio(holder, message);
                        break;
                    case video:
                        // 如果消息类型是视频，调用showAttachOnActivity方法显示视频
                        showAttachOnActivity(ShowVideoActivity.class, message);
                        break;
                    case location:
                        // 如果消息类型是位置信息，调用showAttachOnActivity方法显示位置信息
                        showAttachOnActivity(ShowLocActivity.class, message);
                        break;
                }
            }
        });

        // 设置消息列表的适配器
        mRecyclerView.setAdapter(mListAdapter);

        // 设置消息列表的加载监听器
        mRecyclerView.setLoadingListener(new MsgRecyclerView.OnLoadingListener() {
            @Override
            public void loadPreMessage() {
                // 加载更多消息
                loadMessage();
            }
        });
    }
//    这段代码用于初始化消息列表。它包括以下步骤：
//
//    获取消息列表的布局管理器（LinearLayoutManager）。
//    创建一个空的消息列表 mMsgList。
//    创建消息列表适配器 mListAdapter，并设置消息列表和当前聊天会话。
//    设置消息列表项的点击监听器，根据消息类型执行不同的操作，如查看图片、播放音频等。
//    设置消息列表的适配器为 mListAdapter。
//    设置消息列表的加载监听器，以便在滚动到消息列表顶部时加载更多消息。


    /*** 播放音频，并监听播放进度，更新页面动画 ***/
    public void playAudio(final RViewHolder holder, final IMMessage message) {

        // 检查是否有音频正在播放
        if (isAudioPlay) {
            // 如果正在播放，会先关闭当前播放
            // 暂停音频播放
            AudioPlayManager.pause();
            // 释放音频资源
            AudioPlayManager.release();
            // 停止音频播放的动画计时器
            mAudioPlayHandler.stopAnimTimer();
            // 标记音频播放状态为假，表示不在播放
            isAudioPlay = false;


            // 如果关闭的是自己的音频，停止执行下面的操作
            if (message.getUuid().equals(mPlayId)) {
                mPlayId = "";
                return;
            }
        }

        // 如果音频播放处理程序为空，创建一个新的处理程序
        if (mAudioPlayHandler == null) {
            mAudioPlayHandler = new AudioPlayHandler();
        }

        // 获取音频附件
        AudioAttachment audioAttachment = (AudioAttachment) message.getAttachment();
        if (audioAttachment == null || TextUtils.isEmpty(audioAttachment.getPath())) {
            // 如果音频附件为空或路径为空，显示错误消息并返回
            ToastUtils.showMessage(this, "音频附件失效，播放失败！");
            return;
        }

        // 通过 holder 获取 ImageView 实例，该 ImageView 用于显示声音图标
        final ImageView imageView = holder.getImageView(R.id.iv_audio_sound);

// 根据消息的方向（是接收的消息还是发送的消息）判断是否为左侧（接收方）
        final boolean isLeft = message.getDirect() == MsgDirectionEnum.In;


        // 使用AudioPlayManager来播放音频
        AudioPlayManager.playAudio(this, audioAttachment.getPath(),
                new AudioPlayManager.OnPlayAudioListener() {
                    @Override
                    public void onPlay() {
                        // 启动播放动画
                        // 设置音频播放标志为 true，表示正在播放音频
                        isAudioPlay = true;

// 获取消息的唯一标识，通常用于区分不同消息
                        mPlayId = message.getUuid();

// 使用音频播放处理器启动音频播放动画
// 参数 imageView 表示要显示动画的 ImageView，isLeft 表示消息是否是左侧的，用于选择动画的方向
                        mAudioPlayHandler.startAudioAnim(imageView, isLeft);

                    }

                    @Override
                    public void onComplete() {
                        // 播放完成时的操作
                        // 将音频播放状态设置为假，表示音频当前未播放
                        isAudioPlay = false;

// 清空当前播放的音频ID，用于标记没有正在播放的音频
                        mPlayId = "";

// 停止音频播放处理程序中的动画计时器，用于停止可能正在播放的音频动画
                        mAudioPlayHandler.stopAnimTimer();

                    }

                    @Override
                    public void onError(String message) {
                        // 播放出错时的操作
                        // 标识当前不在播放音频
                        isAudioPlay = false;

// 重置当前播放的音频消息ID
                        mPlayId = "";

// 停止播放动画和计时器
                        mAudioPlayHandler.stopAnimTimer();

// 显示消息提示
                        ToastUtils.showMessage(P2PChatActivity.this, message);

                    }
                });
    }
//    这段代码的目的是处理用户的音频播放操作。它首先检查是否有其他音频正在播放，如果有，会关闭当前的播放。接着，如果音频播放处理程序为空，将创建一个新的处理程序。然后，它获取音频附件，检查附件是否有效。如果音频有效，它会播放音频并启动播放动画。当播放完成时，它会执行相应的操作，包括停止播放动画。如果播放出现错误，也会显示错误消息。


    /**
     * 根据消息附件内容，跳转至相应界面显示附件
     **/
    // 在当前 Activity 上显示附加的信息
    private void showAttachOnActivity(Class<?> activity, IMMessage message) {
        // 创建一个新的 Intent 对象，用于启动指定的 Activity
        Intent intent = new Intent(this, activity);

        // 将 IMMessage 对象作为额外数据添加到 Intent 中，以便目标 Activity 可以访问该信息
        intent.putExtra("IMMessage", message);

        // 启动指定的 Activity，将 Intent 传递给它
        startActivity(intent);
    }
//    这段代码的目的是创建一个方法，该方法允许你在当前的 Activity 上启动另一个指定的 Activity 并传递一个名为 "IMMessage" 的附加消息。这可以用于在不同的 Activity 之间传递消息或数据。


    /*** 初始化各类消息监听 *****/
    private void initListener() {

        // 设置RecyclerView的触摸监听器，用于处理触摸事件
        mRecyclerView.setOnTouchListener(new MyTouchListener());

// 获取窗口的根视图，以便监听全局布局事件
        final View decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    private int previousKeyboardHeight = 0;

                    @Override
                    public void onGlobalLayout() {
                        // 获取屏幕显示区域的矩形
                        // 创建一个 Rect 对象来存储窗口可见区域的信息
                        Rect rect = new Rect();

// 获取当前窗口的装饰视图并获取其可见显示区域
                        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

// 获取整个窗口显示的高度
                        int displayHeight = rect.bottom;

// 获取装饰视图的高度
                        int height = decorView.getHeight();

// 计算键盘的高度，它是装饰视图的高度减去窗口可见区域的底部
                        int keyboardHeight = height - displayHeight;


                        // 检测键盘高度变化
                        if (previousKeyboardHeight != keyboardHeight) {
                            // 判断是否隐藏键盘，通常当键盘占据屏幕高度的80%以上时隐藏键盘
                            boolean hide = (double) displayHeight / height > 0.8;
                            if (!hide) {
                                // 当键盘不隐藏时，滚动RecyclerView以保持最新消息可见
                                mLayoutManager.scrollToPosition(mMsgList.size());
                            }
                        }
                    }
                });

        // 网易云信消息接收监听
        mMsgReceiveObserver = new Observer<List<IMMessage>>() {
            @Override
            public void onEvent(List<IMMessage> imMessages) {
                // 通过判断，决定是否添加收到消息的时间
                IMMessage imMessage = imMessages.get(0);
                // 检查消息列表是否为空
                if (mMsgList.isEmpty()) {
                    // 如果消息列表为空，检查消息的会话类型是否为点对点（P2P）
                    // 并且会话ID与当前聊天会话的聊天对象一致
                    if (imMessage.getSessionType() == SessionTypeEnum.P2P
                            && imMessage.getSessionId().equals(mChatSession.getChatAccount())) {
                        // 如果满足条件，将一条时间消息添加到消息列表中
                        mMsgList.add(mChatHandler.createTimeMessage(imMessage));
                    }
                } else {
                    // 如果消息列表不为空，获取消息列表中的最后一条消息
                    IMMessage lastMsg = mMsgList.get(mMsgList.size() - 1);

                    // 检查新消息的会话类型是否为点对点（P2P）
                    // 会话ID与当前聊天会话的聊天对象一致
                    // 并且新消息的时间与最后一条消息的时间差大于十分钟
                    if (imMessage.getSessionType() == SessionTypeEnum.P2P
                            && imMessage.getSessionId().equals(mChatSession.getChatAccount())
                            && imMessage.getTime() - lastMsg.getTime() > TEN_MINUTE) {
                        // 如果满足条件，将一条时间消息添加到消息列表中
                        mMsgList.add(mChatHandler.createTimeMessage(imMessage));
                    }
                }


                // 将收到的消息添加到列表中
                int receiveCount = 0;
                for (IMMessage message : imMessages) {
                    // 如果消息的会话类型是点对点（P2P）会话，
// 且消息的会话 ID 与当前聊天会话的账户相匹配
                    if (message.getSessionType() == SessionTypeEnum.P2P
                            && message.getSessionId().equals(mChatSession.getChatAccount())) {
                        // 将该消息添加到消息列表
                        mMsgList.add(message);
                        // 增加接收消息计数
                        receiveCount++;
                    }

                }

                if (receiveCount > 0) {
                    mListAdapter.notifyDataSetChanged();

                    // 对于整个 mListAdapter 来说,第0个 item 是 HeadView
                    // 即mMsgList的第 i 条数据，相当于mListAdapter来说是第 i+1 条
                    mLayoutManager.scrollToPosition(mMsgList.size());
                }
            }
        };
        // 网易云信消息状态监听
        mMsgStatusObserver = new Observer<IMMessage>() {
            @Override
            public void onEvent(IMMessage message) {
                // 收到消息状态更新，从后往前更新消息状态
                for (int i = mMsgList.size() - 1; i >= 0; i--) {
                    // 时间 item  UUid  为空
                    if (TextUtils.isEmpty(mMsgList.get(i).getUuid())) {
                        // 如果消息列表中的第 i 条数据的 UUID 为空，继续下一个循环
                        continue;
                    }
                    if (mMsgList.get(i).getUuid().equals(message.getUuid())) {
                        // 如果消息列表中的第 i 条数据的 UUID 与新消息的 UUID 相同
                        // 更新该消息的状态和附件状态
                        mMsgList.get(i).setStatus(message.getStatus());
                        mMsgList.get(i).setAttachStatus(message.getAttachStatus());

                        // 对于整个 mListAdapter 来说，第 0 个 item 是 HeadView
                        // 即 mMsgList 的第 i 条数据，相当于 mListAdapter 来说是第 i+1 条
                        // 通知适配器局部更新第 i+1 条消息
                        mListAdapter.notifyItemChanged(i + 1);
                        // 退出循环
                        break;
                    }
                }
            }
        };

        // 附件传输进度监听
        mProgressObserver = new Observer<AttachmentProgress>() {
            @Override
            public void onEvent(AttachmentProgress progress) {
// 在此处处理附件传输进度
            }
        };
    }
//    这段代码主要用于初始化监听器，包括触摸监听器、全局布局监听器以及消息的接收、状态和附件传输进度监听器。触摸监听器用于处理RecyclerView的触摸事件，全局布局监听器用于检测键盘高度变化并控制RecyclerView的滚动。消息的接收监听器用于处理收到的消息，状态监听器用于更新消息状态，附件传输进度监听器用于处理附件传输进度。

    /*** 消息加载与加载回调 ***/

    private void loadMessage() {
        if (mMsgList.isEmpty()) {
            // 记录为空时，以当前时间为锚点
            IMMessage anchorMessage = MessageBuilder.createEmptyMessage(mChatSession.getSessionId(),
                    mChatSession.getSessionType(), System.currentTimeMillis());
            // 使用聊天处理器加载消息
            mChatHandler.loadMessage(anchorMessage, this);
        } else {
            // 否则，以最上一条消息为锚点
            IMMessage firstMsg = mMsgList.get(0);
            if (TextUtils.isEmpty(firstMsg.getUuid())) {
                firstMsg = mMsgList.get(1);
            }
            // 使用聊天处理器加载消息
            mChatHandler.loadMessage(firstMsg, this);
        }
    }
//    这段代码用于加载聊天消息。如果消息记录为空，它创建一个空的消息以当前时间为锚点，并使用聊天处理器加载消息。如果消息记录不为空，它选择最上一条消息或下一条消息（如果第一条消息的UUID为空，可能是系统消息）作为锚点，并再次使用聊天处理器加载消息。

    @Override
    public void loadSuccess(List<IMMessage> messages, IMMessage anchorMessage) {
        // 隐藏消息列表的头部视图，通常用于隐藏下拉刷新的指示符或加载动画
        mRecyclerView.hideHeadView();

        boolean scroll = false;
        // 如果原本没有，为第一次加载，需要在加载完成后移动到最后一项
        if (mMsgList.isEmpty()) {
            scroll = true;
        }
        if (!messages.isEmpty()) {
            // 处理加载的消息，通常用于格式化和准备显示
            mMsgList.addAll(0, mChatHandler.dealLoadMessage(messages, anchorMessage));

            // 通知适配器数据已经改变
            mListAdapter.notifyDataSetChanged();
        }

        if (scroll) {
            // 如果需要滚动，将屏幕滚动到最后一条消息
            mLayoutManager.scrollToPosition(mMsgList.size());
        }
    }
//    这段代码用于处理消息加载成功后的情况。首先，它隐藏了消息列表的头部视图，通常用于隐藏下拉刷新的指示符或加载动画。接下来，它检查是否需要滚动到最后一条消息，如果消息列表为空，则设置scroll标志为true。然后，如果加载的消息不为空，它调用ChatHandler的dealLoadMessage方法处理消息，将它们添加到消息列表中，并通知适配器数据已经改变。最后，如果scroll标志为true，则将屏幕滚动到最后一条消息。

    // 复写（Override）了接口中的加载失败回调方法
    @Override
    public void loadFail(String message) {
        // 隐藏 RecyclerView 中的头部视图，可能是刷新控件
        mRecyclerView.hideHeadView();

        // 显示一个消息提示，通知用户消息加载失败并附上失败信息
        ToastUtils.showMessage(this, "消息加载失败：" + message);

        // 使用 Android 日志工具记录错误消息
        Log.e(TAG, "load message fail:" + message);
    }
//    这段代码是在接口回调方法中实现的，当加载消息失败时，它执行以下操作：
//
//    调用 mRecyclerView.hideHeadView() 隐藏 RecyclerView（可能包含下拉刷新控件）的头部视图。
//    通过 Toast 工具显示一个消息通知用户消息加载失败，消息文本包括失败信息。
//    使用 Android 的 Log 工具将错误信息记录到日志中，以便开发人员进行调试和错误追踪。

    /******** 页面跳转回调 ********/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 处理从其他活动返回的结果

        if (requestCode == SELECT_PHOTO) {
            // 如果请求代码是选择照片

            if (resultCode == RESULT_OK) {
                // 如果返回结果是成功的

                // 从返回的数据中获取图片路径数组
                String[] images = data.getStringArrayExtra("images");

                // 遍历图片路径并将每个路径发送为消息
                for (String path : images) {
                    sendMessage(mChatHandler.createImageMessage(path));
                }
            }
        } else if (requestCode == START_CAMERA) {
            // 如果请求代码是启动相机

            if (resultCode == CameraActivity.RESULT_IMAGE) {
                // 如果返回结果是图像

                // 从返回的数据中获取图像路径
                String imagePath = data.getStringExtra("imagePath");

                // 发送图像消息
                sendMessage(mChatHandler.createImageMessage(imagePath));
            } else if (resultCode == CameraActivity.RESULT_VIDEO) {
                // 如果返回结果是视频

                // 从返回的数据中获取视频路径
                String videoPath = data.getStringExtra("videoPath");

                // 发送视频消息
                sendMessage(mChatHandler.createVideoMessage(videoPath));
            }
        } else if (requestCode == SELECT_LOCATION) {
            // 如果请求代码是选择位置

            if (resultCode == RESULT_OK) {
                // 如果返回结果是成功的

                // 从返回的数据中获取地址字符串
                String address = data.getStringExtra("address");

                // 从返回的数据中获取地理位置信息
                LatLonPoint loc = data.getParcelableExtra("location");

                // 发送位置消息
                sendMessage(mChatHandler.createLocMessage(loc, address));
            }
        }
    }
//    这段代码是用于处理从其他活动返回的结果。具体来说：
//
//    通过onActivityResult方法来捕获返回的结果。
//
//    根据请求代码（requestCode）来区分不同的操作，例如选择照片、启动相机、选择位置等。
//
//    对于选择照片的情况，如果返回结果是成功的（RESULT_OK），则获取选定的图片路径数组并将每个路径发送为消息。
//
//    对于启动相机的情况，根据返回结果的类型（图像或视频），获取相应的路径，并将其发送为图像消息或视频消息。
//
//    对于选择位置的情况，如果返回结果是成功的，获取选定位置的地址和地理位置信息，并将其发送为位置消息。

    /*** 消息发送 ***/
    // 发送IM消息的方法
    private void sendMessage(IMMessage message) {

        // 如果消息列表为空或者与上一条消息时间间隔大于十分钟，添加一条时间戳消息
        if (mMsgList.isEmpty() ||
                message.getTime() - mMsgList.get(mMsgList.size() - 1).getTime() > TEN_MINUTE) {
            mMsgList.add(mChatHandler.createTimeMessage(message));
        }

        // 将新消息加入列表并刷新界面
        mMsgList.add(message);
        // 通知消息列表适配器有新消息插入，并刷新界面
        mListAdapter.notifyItemInserted(mMsgList.size());

        // 滚动到最新的消息位置，确保用户能看到最新的消息
        mLayoutManager.scrollToPosition(mMsgList.size());
        // 发送消息并监听消息发送状态
        NIMClient.getService(MsgService.class).sendMessage(message, false);
    }
//    这段代码是用于在聊天应用中发送IM消息的方法。它的主要功能包括：
//
//    检查消息列表是否为空或与上一条消息的时间间隔是否超过十分钟，如果是，添加一条时间戳消息，以分隔不同时刻的消息。
//
//    将新消息添加到消息列表（ArrayList）中。
//
//    通知消息列表适配器（mListAdapter）有新消息插入，并刷新界面，以便显示新消息。
//
//    滚动到消息列表的最后，以确保用户可以看到最新的消息。
//
//    使用NIM SDK的MsgService服务来发送消息，并将第二个参数设置为false，表示不需要监听消息的发送状态（异步发送）。
//
//    这段代码有助于处理消息的发送和在聊天界面上更新消息列表的显示。

    /*** Activity 生命周期，注册或注销各类监听 ***/

    @Override
    protected void onResume() {
        super.onResume();
        // 设置当前聊天对象，即如果为mChatPersonAccount用户的消息，则不在通知了进行通知
        NIMClient.getService(MsgService.class).setChattingAccount(mChatSession.getChatAccount(),
                mChatSession.getSessionType());
        // 设置当前聊天的对象，以便在接收到消息时不再进行通知，这通常用于将聊天界面视为活动状态。

        AudioPlayManager.resume();
        // 恢复音频播放管理器的操作。通常用于在 Activity 恢复时继续播放声音或语音消息。
    }
//    这段代码用于在活动（Activity）的 onResume 方法中执行以下操作：
//
//    设置当前的聊天对象，以便在接收到消息时不再进行通知。这是通过 NIM（网易即时通讯云）提供的服务来实现的，根据 mChatSession 中的信息，设置当前聊天对象和会话类型。
//    恢复音频播放管理器的操作。这通常用于在 Activity 恢复时继续播放声音或语音消息。
//    这是在处理聊天界面的 Activity 时，确保正确管理消息通知和音频播放的重要部分。

    @Override
    protected void onPause() {
        super.onPause();
        // 当前无聊天对象，需要通知栏提醒
        // 获取 NIM 消息服务
        MsgService msgService = NIMClient.getService(MsgService.class);

        // 设置当前聊天对象为空，这将告诉消息服务没有当前的聊天对象
        msgService.setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);

        // 暂停语音播放管理器，这可以用于停止正在播放的语音消息
        AudioPlayManager.pause();
    }
//    这段代码在活动（Activity）的 onPause 方法中执行。它的目的是在用户离开该活动时执行一些必要的操作：
//
//    通过 NIMClient 获取消息服务 MsgService 的实例，该服务用于管理即时消息。
//
//    使用 setChattingAccount 方法将当前聊天对象切换为 "无聊天对象"，这样做是为了确保在离开该界面时，用户不会错误地保留在某个聊天会话中。MsgService.MSG_CHATTING_ACCOUNT_NONE 表示无聊天对象，SessionTypeEnum.None 表示没有正在进行的聊天会话。
//
//    暂停 AudioPlayManager，它是一个用于播放语音消息的管理器。这可以确保在用户离开该活动时停止任何正在播放的语音消息，从而节省资源和提供更好的用户体验。
//
//    总之，这段代码有助于管理即时消息和语音消息的状态，以确保在用户离开活动时一切都处于正确的状态。

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销各类监听事件
        // 注销消息接收事件监听器
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(mMsgReceiveObserver, false);

        // 注销消息状态变化事件监听器
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(mMsgStatusObserver, false);

        // 注销消息附件上传/下载进度事件监听器
        NIMClient.getService(MsgServiceObserve.class).observeAttachmentProgress(mProgressObserver, true);

        // 释放音频播放管理器资源
        AudioPlayManager.release();
    }
//    这段代码在Activity销毁时进行资源和事件监听器的清理工作：
//
//            NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(mMsgReceiveObserver, false); - 取消消息接收事件监听器，该监听器通常用于接收并处理新消息。
//
//            NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(mMsgStatusObserver, false); - 取消消息状态变化事件监听器，该监听器用于处理消息发送状态的变化。
//
//            NIMClient.getService(MsgServiceObserve.class).observeAttachmentProgress(mProgressObserver, true); - 注销消息附件上传/下载进度事件监听器，用于监听消息附件上传或下载的进度。
//
//            AudioPlayManager.release(); - 释放音频播放管理器的资源，确保在Activity销毁时停止任何正在播放的音频并释放相关资源，以避免内存泄漏和资源浪费。

    /********** 输入面板事件回调 *********/

    // 当发送按钮点击时，调用该方法，将文本消息发送
    @Override
    public void sendBtnClick(String textMessage) {
        sendMessage(mChatHandler.createTextMessage(textMessage));
    }

    // 当照片按钮点击时，打开照片选择活动
    @Override
    public void photoBtnClick() {
        Intent intent = new Intent(this, PhotoSelectActivity.class);
        startActivityForResult(intent, SELECT_PHOTO);
    }

    // 当位置按钮点击时，打开位置选择活动
    @Override
    public void locationBtnClick() {
        Intent intent = new Intent(this, SelectLocActivity.class);
        startActivityForResult(intent, SELECT_LOCATION);
    }

    // 当相机按钮点击时，打开相机活动
    @Override
    public void cameraBtnClick() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, START_CAMERA);
    }

    // 当录音完成时，将音频文件路径和录制时间发送为音频消息
    @Override
    public void audioRecordFinish(String audioFilePath, long recordTime) {
        sendMessage(mChatHandler.createAudioMessage(audioFilePath, recordTime));
    }

    // 当录音出错时，显示出错消息
    @Override
    public void audioRecordError(String message) {
        ToastUtils.showMessage(this, "录音出错:" + message);
    }

    // 当扩展布局显示时，将消息列表滚动到底部
    @Override
    public void exLayoutShow() {
        mLayoutManager.scrollToPosition(mMsgList.size());
    }
//    这段代码是一个接口的实现，用于处理不同按钮的点击事件。当用户点击发送按钮，照片按钮，位置按钮，相机按钮等不同操作时，分别执行对应的操作，如发送文本消息、打开照片选择活动、打开位置选择活动、打开相机活动、发送音频消息等。

    /***  标题栏返回按钮事件 *****/

    // 注解，表示这是一个点击事件的处理方法，对应控件的ID为 R.id.iv_back_btn
    @OnClick(R.id.iv_back_btn)
    public void finishActivity() {
        // 当用户点击返回按钮图标时，关闭当前Activity
        this.finish();
    }
//    这段代码使用了注解 @OnClick 来标记一个点击事件的处理方法，该方法在用户点击一个具有 R.id.iv_back_btn ID 的控件（通常是返回按钮图标）时被调用。在方法内部，它调用了 finish() 方法，用于关闭当前的 Activity。这通常用于实现返回上一个界面或退出当前界面的功能。

    /******  消息列表触摸事件   *******/

    // 创建一个名为MyTouchListener的内部类，实现View.OnTouchListener接口
    private class MyTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // 当触摸事件发生时
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 如果是按下操作

                // 隐藏输入布局的悬浮视图
                mInputLayout.hideOverView();
            }
            // 返回false表示此触摸事件未被消耗，将被传递给其他触摸监听器
            return false;
        }
    }
//    这段代码定义了一个名为MyTouchListener的内部类，它实现了View.OnTouchListener接口。主要作用是在触摸事件发生时，如果是按下操作（MotionEvent.ACTION_DOWN），则调用mInputLayout的hideOverView方法来隐藏输入布局的悬浮视图。最后，返回false表示该触摸事件未被消耗，将被传递给其他触摸监听器。

}
