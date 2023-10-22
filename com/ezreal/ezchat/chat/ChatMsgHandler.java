package com.ezreal.ezchat.chat;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.amap.api.services.core.LatLonPoint;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.suntek.commonlibrary.utils.TextUtils;

import java.io.File;
import java.util.List;

/**
 * 聊天工具类
 * Created by wudeng on 2017/9/13.
 */

public class ChatMsgHandler {
    // 定义名为 ChatMsgHandler 的类

    private static final String TAG = ChatMsgHandler.class.getSimpleName();
    // 定义一个 TAG 常量，用于在日志中标识该类

    private static final int ONE_QUERY_LIMIT = 20;
    // 定义一个常量 ONE_QUERY_LIMIT，用于表示一次查询消息的限制数

    private static final long TEN_MINUTE = 1000 * 60 * 10;
    // 定义一个常量 TEN_MINUTE，表示十分钟的毫秒数

    private Context mContext;
    // 声明一个 Context 类型的成员变量 mContext，用于处理 Android 应用的上下文信息

    private ChatSession mChatSession;
    // 声明一个 ChatSession 类型的成员变量 mChatSession，用于处理聊天会话信息
//    这段代码定义了一个名为 ChatMsgHandler 的 Java 类。该类似乎是用于处理聊天消息的逻辑。它包含了一些常量、成员变量和构造函数。以下是对这些元素的详细说明：
//
//    TAG 是一个字符串常量，通常在日志中用于标识该类。这有助于调试和定位问题。
//
//    ONE_QUERY_LIMIT 和 TEN_MINUTE 都是常量，ONE_QUERY_LIMIT 表示一次查询消息的限制数，TEN_MINUTE 表示十分钟的毫秒数。这些常量用于规定一些操作的限制和时间间隔。
//
//    mContext 是一个 Context 类型的成员变量，通常用于访问 Android 应用的上下文信息，如资源、系统服务等。
//
//    mChatSession 是一个 ChatSession 类型的成员变量，可能用于处理聊天会话相关的信息。
//
//    该类还可能包括其他方法和逻辑，用于处理聊天消息的发送、接收、存储等操作，但在提供的代码中没有显示出这些部分的内容。

    public ChatMsgHandler(Context context, ChatSession session) {
        // 构造函数，用于初始化 ChatMsgHandler 类的实例
        mContext = context;
        // 传入的上下文对象，用于后续在这个类中访问 Android 应用的上下文信息
        mChatSession = session;
        // 传入的 ChatSession 对象，用于处理聊天会话的相关操作
    }
//    这段代码定义了一个构造函数 ChatMsgHandler，它用于创建 ChatMsgHandler 类的实例。构造函数接受两个参数：context 和 session。context 是 Android 应用的上下文对象，用于获取应用的资源和执行其他应用级别的操作。session 是 ChatSession 对象，用于管理和处理聊天会话的相关操作。
//
//    构造函数的目的是将传入的参数分配给类的成员变量，以便在后续的方法中使用它们。这种方式允许在类的不同方法中访问这些参数，以执行与聊天消息处理相关的任务。

    /**
     * 发送文本消息
     *
     * @param text 文本
     */
    public IMMessage createTextMessage(String text) {
        // 创建文本消息的方法

        // 使用 MessageBuilder 类的 createTextMessage 方法创建一个文本消息
        // 参数1: mChatSession.getChatAccount() - 聊天会话中的对方账号或 ID，表示消息的接收者或发送者
        // 参数2: mChatSession.getSessionType() - 会话类型，通常是单聊、群聊等
        // 参数3: text - 要发送的文本消息内容

        return MessageBuilder.createTextMessage(mChatSession.getChatAccount(),
                mChatSession.getSessionType(), text);

        // 返回创建的文本消息
    }
//    这段代码是一个方法，用于创建文本消息。在这里，它使用了一个叫做 MessageBuilder 的类中的 createTextMessage 方法来构建一个文本消息。这个方法需要提供接收者或发送者的账号信息、会话类型以及要发送的文本消息内容作为参数。当调用这个方法时，将返回一个表示文本消息的 IMMessage 对象，可以用于发送或处理消息。这通常用于即时通讯应用程序中，以便用户可以发送文本消息给其他用户。

    /**
     * 发送图片消息
     *
     * @param path 图片路径
     */
    public IMMessage createImageMessage(String path) {
        // 创建一个名为 createImageMessage 的方法，用于生成图片消息
        return MessageBuilder.createImageMessage(mChatSession.getSessionId(),
                mChatSession.getSessionType(), new File(path));
        // 调用 MessageBuilder 的 createImageMessage 方法来创建一个图片消息，并将生成的消息返回
        // 参数说明：
        // - mChatSession.getSessionId()：获取当前聊天会话的会话 ID，通常用于确定消息发送的目标
        // - mChatSession.getSessionType()：获取当前聊天会话的会话类型，例如单聊、群聊等
        // - new File(path)：将图片的文件路径传递给 createImageMessage 方法，以便构建消息
    }
//    这段代码定义了一个名为 createImageMessage 的方法，它用于创建一个聊天应用中的图片消息。该方法接受一个字符串参数 path，这是图片文件的路径。方法内部调用 MessageBuilder.createImageMessage 来生成一个图片消息，同时传入了当前聊天会话的会话 ID 和会话类型以及图片文件的路径。这个方法主要用于将用户选择的图片文件转化为聊天消息的形式，以便在应用中发送图片消息。

    /**
     * 发送语音消息
     *
     * @param path 语音文件路径
     * @param time 录音时间 ms
     */
    public IMMessage createAudioMessage(String path, long time) {
        // 创建一个音频消息
        // 参数 'path' 是音频文件的路径
        // 参数 'time' 是音频的时长

        return MessageBuilder.createAudioMessage(
                mChatSession.getSessionId(),
                mChatSession.getSessionType(),
                new File(path),
                time
        );
    }
//    这段代码中的方法用于创建一个音频消息，通常用于聊天应用中的消息发送。以下是参数的详细解释：
//
//    path: 音频文件的路径。这个路径应该指向存储在设备上的音频文件，例如录音文件。
//    time: 音频的时长，通常以毫秒为单位表示。
//    方法使用了MessageBuilder类的createAudioMessage静态方法，该方法根据提供的参数构建一个音频消息。这个消息可以被发送到聊天会话中，以便其他用户可以接收和播放。
//
//    在这段代码中，mChatSession 变量（不在提供的代码中）用于确定消息所属的聊天会话。这个方法创建的消息将被发送到 mChatSession 所表示的聊天中。

    /**
     * 发送视频消息
     *
     * @param path 视频文件路径
     */
    public IMMessage createVideoMessage(String path) {
        // 创建一个视频消息
        File file = new File(path);
        // 从给定路径创建一个文件对象，该路径指向要发送的视频文件

        MediaPlayer player = MediaPlayer.create(mContext, Uri.fromFile(file));
        // 创建一个 MediaPlayer 对象，用于读取视频文件的信息
        // mContext 是上下文对象，用于创建 MediaPlayer
        // Uri.fromFile(file) 将文件对象转换为 Uri 以便 MediaPlayer 加载视频

        int duration = player.getDuration();
        // 获取视频的时长（毫秒）

        int height = player.getVideoHeight();
        // 获取视频的高度

        int width = player.getVideoWidth();
        // 获取视频的宽度

        return MessageBuilder.createVideoMessage(mChatSession.getSessionId(),
                mChatSession.getSessionType(), file, duration, width, height, null);
        // 使用消息构建器创建视频消息
        // mChatSession.getSessionId() 获取当前聊天会话的 ID
        // mChatSession.getSessionType() 获取当前聊天会话的类型
        // file 是视频文件
        // duration 是视频时长
        // width 和 height 是视频的宽度和高度
        // null 是可选的参数，可以用于传递额外的信息
    }
//    这段代码定义了一个方法 createVideoMessage，它用于创建一个 IM（即即时消息）中的视频消息。这个方法接受一个文件路径作为参数，该路径指向要发送的视频文件。它会读取视频文件的信息，如时长、宽度和高度，然后使用消息构建器 MessageBuilder.createVideoMessage 创建一个视频消息，并返回该消息。
//
//    这个方法主要用于在即时通讯应用中创建视频消息，以便将视频发送给其他用户。视频消息通常包括视频文件的路径、时长、宽度和高度等信息，这些信息可以在接收端用于播放和显示视频。


    /**
     * 创建地理位置消息
     *
     * @param latLonPoint 位置点
     * @param address     地址描述
     */
    public IMMessage createLocMessage(LatLonPoint latLonPoint, String address) {
        // 创建地理位置消息
        // 参数 latLonPoint 是包含纬度和经度信息的地理坐标点
        // 参数 address 是地理位置的地址信息

        return MessageBuilder.createLocationMessage(
                mChatSession.getSessionId(),
                mChatSession.getSessionType(),
                latLonPoint.getLatitude(),
                latLonPoint.getLongitude(),
                address
        );
        // 调用 MessageBuilder 类的 createLocationMessage 方法来创建地理位置消息。
        // 传入的参数包括会话的 sessionId 和 sessionType，以及地理坐标的纬度、经度和地址信息。
    }
//    这段代码定义了一个用于创建地理位置消息的方法 createLocMessage。这个方法接受地理坐标点 latLonPoint 和地址信息 address 作为参数，然后使用 MessageBuilder 类的 createLocationMessage 方法创建一个地理位置消息。这个地理位置消息通常会包含纬度、经度和地址等信息，用于在聊天应用中共享地理位置。这是一个在聊天应用中处理地理位置信息的常见操作。

    /**
     * 加载历史消息记录
     *
     * @param anchorMessage 锚点消息
     * @param listener      加载回调
     */
    public void loadMessage(final IMMessage anchorMessage, final OnLoadMsgListener listener) {
        // 加载消息方法，接受一个锚点消息和一个消息加载监听器作为参数

        NIMClient.getService(MsgService.class).queryMessageListEx(anchorMessage,
                        QueryDirectionEnum.QUERY_OLD, ONE_QUERY_LIMIT, true)
                // 使用 NIMClient 的 MsgService 获取消息服务实例，然后调用 queryMessageListEx 方法
                // 参数 anchorMessage：锚点消息，即从哪条消息开始加载更多消息
                // QueryDirectionEnum.QUERY_OLD：查询方向，表示向旧消息查询
                // ONE_QUERY_LIMIT：一次查询的消息数量限制
                // true：是否包括锚点消息

                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    // 设置请求回调监听器，该回调监听器处理加载消息的结果

                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        // 请求结果回调方法，返回 code（响应码）、result（消息列表）和 exception（异常）

                        if (exception != null) {
                            listener.loadFail(exception.getMessage());
                            // 如果有异常，调用监听器的 loadFail 方法通知加载失败，同时传递异常信息
                            return;
                        }
                        if (code != 200) {
                            listener.loadFail("code:" + code);
                            // 如果响应码不是200（成功），调用监听器的 loadFail 方法通知加载失败，并提供错误信息
                            return;
                        }

                        listener.loadSuccess(result, anchorMessage);
                        // 如果加载成功，调用监听器的 loadSuccess 方法通知加载成功，并传递消息列表和锚点消息
                    }
                });
    }
//    这段代码定义了一个 loadMessage 方法，用于加载IM消息列表。方法首先使用 NIMClient.getService(MsgService.class) 获取NIM消息服务实例，然后使用 queryMessageListEx 方法查询消息。查询包括锚点消息之前的旧消息，每次查询的消息数量由 ONE_QUERY_LIMIT 指定。请求的结果通过设置的回调监听器处理，根据结果的不同情况，通过回调通知加载成功或失败。

    /**
     * 处理历史消息记录，如果两条消息之间相隔大于 TEN_MINUTE,则需要在两条之间新增时间点文本消息
     *
     * @param messages      历史消息列表
     * @param anchorMessage 锚点消息
     * @return 处理完成后的消息列表
     */
    public List<IMMessage> dealLoadMessage(List<IMMessage> messages, IMMessage anchorMessage) {
        // 处理加载的消息列表

        // 获取最后一条消息
        IMMessage lastMsg = messages.get(messages.size() - 1);

        // 如果锚点消息和最后一条消息的时间差大于等于 TEN_MINUTE（常量），则插入一条时间消息
        if (anchorMessage.getTime() - lastMsg.getTime() >= TEN_MINUTE) {
            messages.add(messages.size() - 1, createTimeMessage(lastMsg));
        }

        // 从倒数第二条消息开始向前遍历
        for (int i = messages.size() - 2; i > 0; i--) {
            // 检查当前消息和前一条消息是否有有效的消息 UUID
            if (!TextUtils.isEmpty(messages.get(i).getUuid()) &&
                    !TextUtils.isEmpty(messages.get(i-1).getUuid())) {
                // 如果当前消息和前一条消息的时间差大于等于 TEN_MINUTE（常量），则插入一条时间消息
                if (messages.get(i).getTime() - messages.get(i-1).getTime() >= TEN_MINUTE) {
                    messages.add(i , createTimeMessage(messages.get(i)));
                }
            }
        }

        // 返回处理后的消息列表
        return messages;
    }
//    这段代码定义了一个方法 dealLoadMessage 用于处理加载的消息列表。在聊天应用中，通常需要将消息按时间间隔显示，以提高可读性。这个方法首先检查锚点消息和最后一条消息之间的时间差，如果时间差大于等于 TEN_MINUTE 常量（可能是十分钟的时间间隔），则会插入一条时间消息，以确保消息按时间顺序正确显示。
//
//    接着，它从倒数第二条消息开始向前遍历消息列表，检查相邻的消息之间的时间间隔。如果时间间隔大于等于 TEN_MINUTE，它也会插入一条时间消息。
//
//    这个方法用于在加载聊天历史消息时，确保消息以时间间隔为单位正确显示，以提高用户体验。


    public IMMessage createTimeMessage(IMMessage message) {
        // 创建一个时间消息

        return MessageBuilder.createEmptyMessage(message.getSessionId(),
                message.getSessionType(), message.getTime());
        // 利用MessageBuilder类创建一个空消息，其中包含了消息的会话ID（sessionId）、会话类型（sessionType）和消息的时间（time）
    }
//    这段代码定义了一个方法 createTimeMessage，用于创建一个时间消息（Time Message）。它接受一个 IMMessage 对象作为参数，这个对象包含了会话信息、会话类型和时间戳。在方法内部，使用 MessageBuilder 类的 createEmptyMessage 方法，根据传入的参数来创建一个空消息，实质上是在指定的会话中添加一个时间戳消息，通常用于在聊天界面中显示消息的发送时间。这个方法用于在即时通讯应用中管理消息的时间戳，以便用户能够了解消息的发送时间。

    public interface OnLoadMsgListener {
        // 定义了一个接口 OnLoadMsgListener

        void loadSuccess(List<IMMessage> messages, IMMessage anchorMessage);
        // 接口中的抽象方法 loadSuccess 用于在实现这个接口的类中处理消息加载成功的情况。
        // 它接受两参数：List<IMMessage> messages，表示加载成功的消息列表；
        // IMMessage anchorMessage，表示锚定的消息，通常用于分页加载消息。

        void loadFail(String message);
        // 接口中的抽象方法 loadFail 用于在实现这个接口的类中处理消息加载失败的情况。
        // 它接受一个参数 String message，表示加载失败的错误消息。
    }
//    这段代码定义了一个接口 OnLoadMsgListener，这个接口包含两个抽象方法，用于处理消息加载的成功和失败情况。通常，在应用中，你可以创建实现了这个接口的类，然后实现这两个方法来处理消息加载的结果。当消息加载成功时，loadSuccess 方法将传递加载的消息列表和锚定的消息。当消息加载失败时，loadFail 方法将传递一个描述加载失败的错误消息。这种模式可用于处理即时通讯应用中的消息加载和展示。
}
