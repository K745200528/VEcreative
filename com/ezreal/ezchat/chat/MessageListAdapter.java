package com.ezreal.ezchat.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ezreal.emojilibrary.EmojiUtils;
import com.ezreal.ezchat.R;

import com.ezreal.ezchat.activity.FriendInfoActivity;

import com.joooonho.SelectableRoundedImageView;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.suntek.commonlibrary.utils.ImageUtils;
import com.suntek.commonlibrary.utils.TextUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 聊天界面 聊天记录列表 adapter
 * Created by wudeng on 2017/9/13.
 */

public class MessageListAdapter extends RecyclerView.Adapter<RViewHolder> {
    // 创建 MessageListAdapter 类，继承自 RecyclerView.Adapter 类，泛型参数是 RViewHolder

    private static final int MSG_TEXT_L = 0x20000;
    private static final int MSG_IMG_L = 0x20001;
    private static final int MSG_AUDIO_L = 0x20002;
    private static final int MSG_VIDEO_L = 0x20003;
    private static final int MSG_LOC_L = 0x20004;

    private static final int MSG_TEXT_R = 0x30000;
    private static final int MSG_IMG_R = 0x30001;
    private static final int MSG_AUDIO_R = 0x30002;
    private static final int MSG_VIDEO_R = 0x30003;
    private static final int MSG_LOC_R = 0x30004;
    // 上述代码定义了一系列的整数常量，它们用于标识不同类型的消息布局。
    // 例如，MSG_TEXT_L 代表左侧文本消息，MSG_IMG_L 代表左侧图片消息，以此类推。

    private Context mContext;
    private LayoutInflater mInflater;
    private List<IMMessage> mMessageList;
    private SimpleDateFormat mDateFormat;
    private ChatSession mChatSession;
    private ChatUtils mChatUtils;
    private OnItemClickListener mItemClickListener;
    // 这些变量用于在 MessageListAdapter 类中存储相关信息和处理消息列表的显示。

//    这段代码定义了一个名为 MessageListAdapter 的类，它继承自 RecyclerView.Adapter 类，泛型参数为 RViewHolder。MessageListAdapter 类的目的是用于显示聊天消息列表，它包含了一系列常量，用于标识不同类型的消息布局。
//
//    常量 MSG_TEXT_L, MSG_IMG_L, MSG_AUDIO_L, MSG_VIDEO_L, MSG_LOC_L 用于标识左侧消息的不同类型，如文本消息、图片消息等。
//    常量 MSG_TEXT_R, MSG_IMG_R, MSG_AUDIO_R, MSG_VIDEO_R, MSG_LOC_R 用于标识右侧消息的不同类型。
//    Context 变量 mContext 用于存储上下文信息。
//    LayoutInflater 变量 mInflater 用于创建布局视图。
//    List<IMMessage> 变量 mMessageList 用于存储消息列表数据。
//    SimpleDateFormat 变量 mDateFormat 用于格式化日期。
//    ChatSession 变量 mChatSession 和 ChatUtils 变量 mChatUtils 用于处理聊天会话和相关功能。
//    OnItemClickListener 变量 mItemClickListener 用于处理列表项点击事件。
//    MessageListAdapter 类是一个自定义的适配器类，用于在 RecyclerView 中显示聊天消息列表。

    public MessageListAdapter(Context context, List<IMMessage> messages, ChatSession session) {
        // 构造函数，接受三个参数：上下文、消息列表、和聊天会话
        mContext = context;
        // 存储上下文，通常用于访问应用的资源和环境
        mInflater = LayoutInflater.from(context);
        // 获取用于加载布局资源的 LayoutInflater 对象
        mChatUtils = new ChatUtils(context);
        // 创建用于聊天的辅助工具类的实例
        mMessageList = messages;
        // 存储消息列表，即聊天中的消息
        mChatSession = session;
        // 存储聊天会话，通常包括与谁聊天以及聊天的其他属性
        mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        // 创建一个日期格式化对象，用于将日期和时间格式化为指定的格式
    }
//    这段代码是一个构造函数，用于创建 MessageListAdapter 的实例。MessageListAdapter 是一个用于在聊天应用中显示消息列表的适配器。构造函数接受三个参数：
//
//    context：上下文对象，通常用于获取应用的资源和访问应用的环境。
//    messages：消息列表，包含了聊天中的所有消息。
//    session：聊天会话对象，用于存储与聊天相关的信息，例如聊天对象等。
//    在构造函数中，这些参数的值被存储在类的成员变量中，以便在适配器的其他方法中使用。例如，mContext 存储了上下文对象，mMessageList 存储了消息列表，mChatSession 存储了聊天会话信息，mDateFormat 存储了日期格式化对象，用于将日期和时间格式化为指定的格式。这样，这些数据可以在适配器的其他方法中使用，以便正确地渲染和显示聊天消息。

    @Override
    public int getItemViewType(int position) {
        // 重写 getItemViewType 方法
        if (mMessageList.get(position - 1).getUuid() == null) {
            // 检查前一个消息的 UUID 是否为 null，如果是，表示这是一个时间项
            return R.layout.item_msg_list_time;
            // 返回时间项的布局 ID
        } else {
            // 如果前一个消息的 UUID 不为 null
            return getViewLayoutId(getMsgViewType(mMessageList.get(position - 1).getDirect(),
                    mMessageList.get(position - 1).getMsgType()));
            // 调用 getViewLayoutId 方法来获取消息项的布局 ID
        }
    }
//    这段代码是 RecyclerView 或类似的列表视图适配器中的 getItemViewType 方法的重写。它用于决定特定位置的列表项应该使用哪种视图类型。

    @Override
    public RViewHolder onCreateViewHolder(ViewGroup parent, int layoutId) {
        // 重写 RecyclerView.Adapter 中的 onCreateViewHolder 方法

        // 使用布局填充器从 layoutId 创建一个新的 View 对象
        View view = mInflater.inflate(layoutId, parent, false);

        // 创建并返回一个 RViewHolder 对象，将上下文 mContext 和新的 View 作为参数传入
        return new RViewHolder(mContext, view);
    }
//    这段代码是 onCreateViewHolder 方法的重写。在 RecyclerView 中，RecyclerView.Adapter 的主要职责之一是创建新的 ViewHolder 对象，以便在列表中显示数据项。这个方法在需要新的 ViewHolder 时被调用，通常在 RecyclerView 的初始设置中使用。
//
//    @Override: 这个注解表示这是一个方法覆盖


    @Override
    public void onBindViewHolder(RViewHolder holder, int position) {
        // 当 RecyclerView 显示列表项时，会回调这个方法，用于绑定数据到列表项上

        if (mMessageList.get(position - 1).getUuid() == null) {
            // 检查消息对象是否有 UUID，通常用于标记时间戳而不是实际消息
            String time = mDateFormat.format(new Date(mMessageList.get(position - 1).getTime()));
            // 格式化时间戳，将时间戳转换为人类可读的时间格式
            holder.setText(R.id.tv_msg_time, time);
            // 将格式化后的时间显示在对应的视图中
        } else {
            // 如果消息对象有 UUID，表示这是一条实际的消息
            bindMsgView(holder, mMessageList.get(position - 1));
            // 使用自定义的方法 bindMsgView 绑定消息内容到列表项视图
        }
    }
//    这段代码是在 RecyclerView 的适配器中的 onBindViewHolder 方法。在这个方法中，你根据列表项的位置 position 获取相应的消息对象，并检查这个消息对象是否有 UUID。如果没有 UUID，则表示这是一个时间戳而不是实际消息，因此会格式化时间戳并将其显示在列表项上。如果有 UUID，则表示这是一条实际的消息，将使用 bindMsgView 方法将消息内容绑定到列表项的视图上。这是一个常见的 RecyclerView 适配器中的数据绑定逻辑。

    @Override
    public int getItemCount() {
        // 重写了 RecyclerView.Adapter 中的 getItemCount 方法
        return mMessageList.size();
        // 返回消息列表中的项数，通常用于确定要显示多少个列表项
    }
//    这段代码是一个重写方法，它实现了 RecyclerView.Adapter 类中的 getItemCount 方法。该方法的主要目的是返回消息列表（mMessageList）中的项数，以便 RecyclerView 知道需要显示多少个列表项。在这里，返回的值是消息列表的大小，即列表中的消息项数量。RecyclerView 将使用这个数量来决定有多少个列表项需要在屏幕上进行绘制。

    private void bindMsgView(final RViewHolder holder, final IMMessage message) {
        // 绑定消息视图，用于显示聊天消息

        ImageView headView = holder.getImageView(R.id.iv_head_picture);
        // 获取头像视图

        if (message.getDirect() == MsgDirectionEnum.In) {
            // 如果消息是接收到的消息（In 表示接收）

            ImageUtils.setImageByUrl(mContext, headView, mChatSession.getChatInfo().getAvatar(),
                    R.mipmap.app_logo);
            // 使用 ImageUtils 类的 setImageByUrl 方法来加载头像图片，设置到头像视图中
            // 这里根据 mChatSession 获取聊天会话信息，包括对方的头像，然后设置到头像视图中
            // 如果加载失败，使用 R.mipmap.app_logo 作为默认头像

            // 设置好友头像点击事件--打开好友信息界面
            headView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 当视图被点击时触发的事件处理函数
                    Intent intent = new Intent(mContext, FriendInfoActivity.class);
                    // 创建一个新的意图（Intent）用于启动 FriendInfoActivity 类
                    intent.putExtra("NimUserInfo", mChatSession.getChatInfo());
                    // 向意图中添加附加数据，键名为 "NimUserInfo"，值是 mChatSession.getChatInfo() 的结果
                    intent.putExtra("FLAG", FriendInfoActivity.FLAG_SHOW_FRIEND);
                    // 再次向意图中添加附加数据，键名为 "FLAG"，值是 FriendInfoActivity.FLAG_SHOW_FRIEND
                    mContext.startActivity(intent);
                    // 启动 FriendInfoActivity，将创建的意图传递给它，启动新的活动
                }
//                这段代码是一个点击事件处理函数，通常用于响应视图元素的点击事件。在这里，当点击事件发生时，将创建一个新的意图（Intent）用于启动 FriendInfoActivity 类。这个意图包含了一些额外的信息，如 "NimUserInfo" 和 "FLAG"。接着，通过 mContext.startActivity(intent) 启动了 FriendInfoActivity，并传递了这个意图作为参数.
            });

        } else {
            ImageUtils.setImageByUrl(mContext, headView, mChatSession.getMyInfo().getAvatar(),
                    R.mipmap.app_logo);
        }

        // 根据消息状态和附件传输状态决定是否显示progress bar
        if (mChatUtils.isTransferring(message)) {
            holder.setVisible(R.id.progress_status, true);
        } else {
            holder.setVisible(R.id.progress_status, false);
        }

        // 根据类型绑定数据
        int viewType = getMsgViewType(message.getDirect(), message.getMsgType());
        switch (viewType) {

            // 文本
            case MSG_TEXT_L:
            case MSG_TEXT_R:
                // 根据消息类型 MSG_TEXT_L 或 MSG_TEXT_R 找到对应的布局中的 TextView
                TextView textView = holder.getTextView(R.id.tv_chat_msg);

                // 通过 EmojiUtils 将消息内容中的文本表情转换成实际的表情图像
                // mContext 是上下文对象，message 是包含消息内容的对象，textView.getTextSize() 返回文本尺寸
                // 这个转换将文本消息中的特定文本表情替换为对应的图像，以便在聊天界面中显示表情
                textView.setText(EmojiUtils.text2Emoji(mContext, message.getContent(), textView.getTextSize()));

                textView.setOnClickListener(new View.OnClickListener() {
                    // 为 TextView 控件设置点击监听器
                    @Override
                    public void onClick(View v) {
                        // 当 TextView 被点击时触发的事件处理方法
                        if (mItemClickListener != null) {
                            // 检查是否设置了列表项点击监听器
                            mItemClickListener.onItemClick(holder, message);
                            // 如果监听器不为空，调用 onItemClick 方法，传递 holder 和 message 参数
                        }
                    }
                });

                break;

            // 图像
            case MSG_IMG_L:
            case MSG_IMG_R:
                // 当消息类型为左侧或右侧的图片消息时执行以下代码
                ImageAttachment imageAttachment = (ImageAttachment) message.getAttachment();
                // 从消息对象中获取图片附件
                final SelectableRoundedImageView imageView = (SelectableRoundedImageView)
                        holder.getImageView(R.id.iv_msg_img);
                // 通过控件持有者获取图像视图的引用
                Bitmap bitmap = mChatUtils.getBitmap(imageAttachment);
                // 使用 ChatUtils 类的 getBitmap 方法获取图像的 Bitmap 对象
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    // 如果 Bitmap 对象不为空，将它设置为图像视图的位图
                } else {
                    imageView.setImageResource(R.mipmap.bg_img_defalut);
                    // 否则，设置默认图像
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 为图像视图设置点击事件监听器
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(holder, message);
                            // 如果监听器不为空，在图像视图被点击时触发 onItemClick 方法
                        }
                    }
                });
                break;
//            这段代码处理消息的图片附件，包括左侧和右侧的情况。根据消息类型，它获取消息的图片附件，并将图片显示在对应的图像视图中。如果成功获取到图片的 Bitmap 对象，就将它设置为图像视图的位图；否则，将显示一个默认的图像。

            // 音频
            case MSG_AUDIO_L:
            case MSG_AUDIO_R:
                // 当消息类型是左侧或右侧的音频消息时执行以下操作
                AudioAttachment audioAttachment = (AudioAttachment) message.getAttachment();
                // 从消息中获取音频附件
                holder.setText(R.id.tv_audio_time, mChatUtils.getAudioTime(audioAttachment.getDuration()));
                // 在相应的视图组件中显示音频时长
                RelativeLayout layout = holder.getReltiveLayout(R.id.layout_audio_msg);
                mChatUtils.setAudioLayoutWidth(layout, audioAttachment.getDuration());
                // 设置音频消息的布局宽度，根据音频时长动态调整

                holder.getReltiveLayout(R.id.layout_audio_msg)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mItemClickListener != null) {
                                    mItemClickListener.onItemClick(holder, message);
                                }
                            }
                        });
                // 为音频消息的布局添加点击事件监听器，以便在点击时触发相应的操作
                break;
//            这段代码处理了两种消息类型：左侧（MSG_AUDIO_L）和右侧（MSG_AUDIO_R）的音频消息。对于这两种消息类型，它执行以下操作：
//
//            从消息中获取音频附件，以便获取音频时长。
//            在对应的视图组件中显示音频消息的时长。
//            动态调整音频消息的布局宽度，以便根据音频时长适当调整宽度。
//            为音频消息的布局添加点击事件监听器，以便在用户点击音频消息时触发相应的操作。这可能是播放音频的功能，因为点击音频消息通常是为了播放声音。
//            这段代码是一个用于呈现音频消息的片段，它负责将音频消息显示在聊天界面上，并添加相应的点击事件处理。

            // 视频
            case MSG_VIDEO_L:
            case MSG_VIDEO_R:
                // 当消息类型为左侧或右侧的视频消息时执行以下代码
                VideoAttachment videoAttachment = (VideoAttachment) message.getAttachment();
                // 从消息对象中获取视频附件信息
                Bitmap videoCover = mChatUtils.getVideoCover(videoAttachment);
                // 使用 ChatUtils 工具类获取视频封面的 Bitmap
                SelectableRoundedImageView roundedImageView =
                        (SelectableRoundedImageView) holder.getImageView(R.id.iv_video_cover);
                // 获取视频封面的 ImageView 对象
                if (videoCover != null) {
                    // 如果视频封面不为空
                    roundedImageView.setImageBitmap(videoCover);
                    // 将视频封面设置为 ImageView 的图片
                } else {
                    roundedImageView.setImageResource(R.mipmap.bg_img_defalut);
                    // 否则，将默认图片设置为 ImageView 的图片
                }
                ImageView play = holder.getImageView(R.id.iv_btn_play);
                // 获取播放按钮的 ImageView 对象
                if (mChatUtils.isTransferring(message)) {
                    // 如果视频正在传输中
                    play.setVisibility(View.GONE);
                    // 隐藏播放按钮
                } else {
                    play.setVisibility(View.VISIBLE);
                    // 否则，显示播放按钮
                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mItemClickListener != null) {
                                mItemClickListener.onItemClick(holder, message);
                            }
                        }
                    });
                    // 为播放按钮设置点击监听器，在点击时触发回调
                }
                holder.setText(R.id.tv_video_time, mChatUtils.getVideoTime(videoAttachment.getDuration()));
                // 设置显示视频时长的 TextView 的文本内容
                break;
//            这段代码是一个 switch-case 语句块，用于根据消息类型（MSG_VIDEO_L 或 MSG_VIDEO_R）来处理不同类型的视频消息。具体操作包括获取视频封面、设置封面图片、处理播放按钮的显示或隐藏等。
//
//                如果消息类型是视频消息，首先从消息中获取视频附件的信息。
//                然后，使用 ChatUtils 工具类中的 getVideoCover 方法获取视频封面的 Bitmap，如果封面不为空，将其设置为 roundedImageView（封面显示的控件）的图片，否则设置默认图片。
//                接下来，根据消息的传输状态，控制播放按钮 play 的可见性。如果视频正在传输中，隐藏播放按钮，否则显示播放按钮。
//                最后，为播放按钮设置点击监听器，在点击时触发 onItemClick 回调，通知监听器处理播放操作。
//                最后一行代码设置显示视频时长的 TextView 的文本内容，使用了 ChatUtils 工具类中的 getVideoTime 方法。

            // 位置
            case MSG_LOC_L:
                // 处理消息类型为左侧位置消息
            case MSG_LOC_R:
                // 处理消息类型为右侧位置消息
                LocationAttachment locationAttachment = (LocationAttachment) message.getAttachment();
                // 从消息对象中获取位置附件
                holder.setText(R.id.tv_loc_address, locationAttachment.getAddress());
                // 在 ViewHolder 中找到对应的 TextView 控件，并设置其文本为位置信息
                holder.getTextView(R.id.tv_show_loc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 设置点击事件监听器，当用户点击这个控件时触发
                        if (mItemClickListener != null) {
                            // 检查是否设置了点击事件监听器
                            mItemClickListener.onItemClick(holder, message);
                            // 调用点击事件监听器的 onItemClick 方法，传递 ViewHolder 和消息对象作为参数
                        }
                    }
                });
                break;
//            这段代码在一个消息处理的逻辑中，根据消息的类型（MSG_LOC_L 表示左侧位置消息，MSG_LOC_R 表示右侧位置消息），来执行相应的操作。主要涉及到以下几点：
//
//            LocationAttachment locationAttachment = (LocationAttachment) message.getAttachment();：从消息对象中获取位置附件，这个附件包含了位置信息的相关数据。
//
//            holder.setText(R.id.tv_loc_address, locationAttachment.getAddress());：在消息列表的 ViewHolder 中找到用于显示位置信息的 TextView 控件，然后将位置信息设置为该控件的文本内容。
//
//            holder.getTextView(R.id.tv_show_loc).setOnClickListener(new View.OnClickListener() { ... });：为一个点击控件设置点击事件监听器。当用户点击这个控件时，触发监听器中的 onClick 方法。在这个方法中，检查是否设置了 mItemClickListener，如果设置了，就调用 mItemClickListener 的 onItemClick 方法，将 ViewHolder 和消息对象作为参数传递，以便在点击事件发生时执行相应的操作。
//
//            这段代码的主要作用是在聊天应用中显示位置消息，并处理用户点击位置消息时的交互。

        }
    }


    private int getViewLayoutId(int viewType) {
        // 根据视图类型（viewType）返回对应的布局资源ID
        switch (viewType) {
            // 收到的消息
            case MSG_TEXT_L:
                return R.layout.item_msg_text_left;
// 当消息类型为 MSG_TEXT_L（左侧文本消息）时，返回用于显示左侧文本消息的布局资源 R.layout.item_msg_text_left。

            case MSG_IMG_L:
                return R.layout.item_msg_img_left;
// 当消息类型为 MSG_IMG_L（左侧图片消息）时，返回用于显示左侧图片消息的布局资源 R.layout.item_msg_img_left。

            case MSG_AUDIO_L:
                return R.layout.item_msg_audio_left;
// 当消息类型为 MSG_AUDIO_L（左侧音频消息）时，返回用于显示左侧音频消息的布局资源 R.layout.item_msg_audio_left。

            case MSG_VIDEO_L:
                return R.layout.item_msg_video_left;
// 当消息类型为 MSG_VIDEO_L（左侧视频消息）时，返回用于显示左侧视频消息的布局资源 R.layout.item_msg_video_left。

            case MSG_LOC_L:
                return R.layout.item_msg_loc_left;
// 当消息类型为 MSG_LOC_L（左侧位置消息）时，返回用于显示左侧位置消息的布局资源 R.layout.item_msg_loc_left。


            // 发出的消息
            case MSG_TEXT_R:
                // 当消息类型是 MSG_TEXT_R，即右侧文本消息
                return R.layout.item_msg_text_right;
            // 返回右侧文本消息的布局文件

            case MSG_IMG_R:
                // 当消息类型是 MSG_IMG_R，即右侧图片消息
                return R.layout.item_msg_img_right;
            // 返回右侧图片消息的布局文件

            case MSG_AUDIO_R:
                // 当消息类型是 MSG_AUDIO_R，即右侧音频消息
                return R.layout.item_msg_audio_right;
            // 返回右侧音频消息的布局文件

            case MSG_VIDEO_R:
                // 当消息类型是 MSG_VIDEO_R，即右侧视频消息
                return R.layout.item_msg_video_right;
            // 返回右侧视频消息的布局文件

            case MSG_LOC_R:
                // 当消息类型是 MSG_LOC_R，即右侧位置消息
                return R.layout.item_msg_loc_right;
            // 返回右侧位置消息的布局文件


            // 其他消息
            default:
                return R.layout.item_msg_list_time;
        }
    }
//    这段代码定义了一个名为 getViewLayoutId 的方法，它接受一个 viewType 参数，并根据这个参数返回对应的布局资源ID。这个方法通常用于在适配器中，根据不同类型的消息，为每个消息项设置不同的布局。
//
//    viewType 可能代表不同种类的消息，例如文本消息、图片消息、音频消息等。根据不同的 viewType，该方法将返回相应的布局资源ID，这些布局资源用于在列表或视图中渲染消息项。这是一种常见的做法，用于根据数据的类型动态加载不同的布局，以实现适配器中的多种消息类型的显示。

    private int getMsgViewType(MsgDirectionEnum direct, MsgTypeEnum type) {

        // 收到的消息，头像显示在 left
        if (direct == MsgDirectionEnum.In) {
            // 如果消息是接收到的消息
            if (type == MsgTypeEnum.text) {
                // 如果消息类型是文本消息
                return MSG_TEXT_L;
            } else if (type == MsgTypeEnum.image) {
                // 如果消息类型是图片消息
                return MSG_IMG_L;
            } else if (type == MsgTypeEnum.audio) {
                // 如果消息类型是语音消息
                return MSG_AUDIO_L;
            } else if (type == MsgTypeEnum.video) {
                // 如果消息类型是视频消息
                return MSG_VIDEO_L;
            } else if (type == MsgTypeEnum.location) {
                // 如果消息类型是位置消息
                return MSG_LOC_L;
            } else {
                // 如果消息类型不是上述任何一种
                return 0;
            }
        } else { // 发出的消息，头像显示在右边
            // 在这里根据消息的类型（type）来返回对应的消息布局资源ID
            if (type == MsgTypeEnum.text) {
                // 如果消息类型是文本消息，返回右侧文本消息布局资源ID
                return MSG_TEXT_R;
            } else if (type == MsgTypeEnum.image) {
                // 如果消息类型是图片消息，返回右侧图片消息布局资源ID
                return MSG_IMG_R;
            } else if (type == MsgTypeEnum.audio) {
                // 如果消息类型是音频消息，返回右侧音频消息布局资源ID
                return MSG_AUDIO_R;
            } else if (type == MsgTypeEnum.video) {
                // 如果消息类型是视频消息，返回右侧视频消息布局资源ID
                return MSG_VIDEO_R;
            } else if (type == MsgTypeEnum.location) {
                // 如果消息类型是位置消息，返回右侧位置消息布局资源ID
                return MSG_LOC_R;
            } else {
                // 如果消息类型不是上述任何一种，返回0或其他默认值
                return 0;
            }
        }
    }
//    这段代码中的 getMsgViewType 方法用于确定消息的布局类型，即消息显示在左侧还是右侧，并根据消息的类型返回相应的布局资源ID。在一个聊天界面中，不同类型的消息（文本、图片、语音等）需要使用不同的布局来呈现，左侧通常是接收到的消息，右侧是发送出去的消息，这个方法根据消息的方向和类型来决定使用哪种布局。

    public void setOnItemClickListener(OnItemClickListener listener) {
        // 定义一个公共方法 setOnItemClickListener，用于设置列表项点击监听器
        this.mItemClickListener = listener;
        // 将传入的监听器对象赋值给成员变量 mItemClickListener
    }
//    这段代码定义了一个公共方法 setOnItemClickListener，它的作用是用于设置列表项的点击监听器。

}
