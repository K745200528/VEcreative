package com.ezreal.ezchat.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.suntek.commonlibrary.utils.ImageUtils;
import com.suntek.commonlibrary.utils.TextUtils;

/**
 * Created by wudeng on 2017/11/1.
 */

public class ChatUtils {

    // 上下文对象
    private Context mContext;

    // 用于缓存位图的 LruCache
    private LruCache<String, Bitmap> mLruCache;
//    这段代码定义了一个名为 ChatUtils 的类，它似乎与聊天相关的实用工具有关。

    public ChatUtils(Context context) {
        mContext = context;
        // 初始化 ChatUtils 类，接受一个 Context 对象作为参数。

        // 获取应用最大可用内存，取1/8作为缓存内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        // 计算可用内存的 1/8 作为图片缓存的内存上限。

        // 初始化图片缓存
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            // 创建一个 LruCache 实例，用于在内存中缓存位图数据。

            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 重写 sizeOf 方法，用于确定缓存中每个项的大小，以便控制缓存的大小。
                return value.getRowBytes() * value.getHeight();
                // 返回图片所占内存的大小，通过位图的行字节数和高度相乘来计算。
            }
        };
    }
//    这段代码是 ChatUtils 类的构造函数。它初始化了一个内存缓存对象 mLruCache，用于缓存位图数据，以提高应用程序的性能和加载速度。首先，它获取了应用程序可用的最大内存大小，然后将这个大小除以8，以得到用于缓存的内存大小。接着，它使用 LruCache 类创建了一个图片缓存，LruCache 是 Android 提供的内存缓存类，用于按照最近最少使用（LRU）的规则来管理缓存。然后，重写了 sizeOf 方法，该方法用于确定缓存中每个项的大小，这里使用了位图的行字节数和高度的乘积来计算。这个缓存会在应用中用于存储聊天中的图片，以便快速加载和显示，减少了图片的重复下载和加载，从而提高了应用的性能。


    public boolean isTransferring(IMMessage message) {
        // 定义一个名为 isTransferring 的方法，用于判断消息是否正在传输中
        return message.getStatus() == MsgStatusEnum.sending ||
                // 如果消息状态为 "sending"，表示消息正在发送
                (message.getAttachment() != null && message.getAttachStatus() == AttachStatusEnum.transferring);
        // 或者，如果消息的附件不为空且附件状态为 "transferring"，表示消息的附件正在传输
    }
//    这段代码定义了一个方法 isTransferring，用于检查给定的 IMMessage 对象是否处于传输中的状态。

    public Bitmap getBitmap(ImageAttachment attachment) {
        // 获取附件的图片
        // 这个方法用于从 ImageAttachment 对象中获取并返回 Bitmap 图像。

        // 优先显示缩略图，但是要限制宽高
        // 如果有缩略图可用，首先检查缓存中是否已经存在该缩略图，如果没有，则创建一个限制宽高的缩略图，并将其加入缓存。
        if (!TextUtils.isEmpty(attachment.getThumbPath())) {
            // 获取缩略图路径
            Bitmap bitmap = mLruCache.get(attachment.getThumbPath());
            // 从内存缓存中尝试获取该缩略图
            if (bitmap == null){
                // 如果缓存中没有，说明该缩略图还没有被加载
                bitmap = ImageUtils.getBitmapFromFile(attachment.getThumbPath(), 400, 180);
                // 创建限制宽高的缩略图
                mLruCache.put(attachment.getThumbPath(), bitmap);
                // 将缩略图加入缓存，以便下次快速获取
            }
            return bitmap;
            // 返回获取到的缩略图
        }

        // 缩略图不存在的情况下显示原图，但是要限制宽高
        // 如果没有可用的缩略图，则尝试获取原始图像，同样，限制宽高并缓存图像。
        if (!TextUtils.isEmpty(attachment.getPath())) {
            // 获取原始图像的路径
            Bitmap bitmap = mLruCache.get(attachment.getPath());
            // 尝试从缓存中获取图像
            if (bitmap == null){
                // 如果缓存中没有，说明原始图像还没有被加载
                bitmap = ImageUtils.getBitmapFromFile(attachment.getPath(), 400, 180);
                // 创建限制宽高的原始图像
                mLruCache.put(attachment.getPath(), bitmap);
                // 将原始图像加入缓存，以便下次快速获取
            }
            return bitmap;
            // 返回获取到的原始图像
        }

        // 如果没有缩略图和原始图像，则返回空
        return null;
        // 返回 null，表示没有可用图像
    }
//    这段代码是一个方法，用于从 ImageAttachment 对象中获取 Bitmap 图像。它首先尝试获取缩略图，如果缩略图存在，则限制宽度和高度，然后将其加入内存缓存。如果缩略图不可用，它会尝试获取原始图像，同样限制宽度和高度，并将其加入内存缓存。如果缩略图和原始图像都不可用，它将返回 null。
//
//    这种方法适用于在应用中显示图片附件，首先尝试加载较小的缩略图，以提高性能和加载速度，如果没有缩略图，则加载较大的原始图像。缓存的使用有助于减少对文件系统的重复访问，提高了加载速度。


    public String getAudioTime(long duration) {
        // 定义一个方法，用于将音频时长（以毫秒为单位）转换为字符串
        return String.valueOf(duration / 1000.0) + "‘";
        // 将毫秒时长除以1000，得到以秒为单位的时长，并将其转换为字符串，然后附加上一个单引号，表示音频时长，最后返回这个字符串
    }
//    这段代码定义了一个用于将音频时长从毫秒转换为字符串表示的方法 getAudioTime。在这个方法中，duration 是音频的时长，以毫秒为单位。方法首先将毫秒时长除以1000，以得到以秒为单位的时长。然后，它将这个时长转换为字符串，附加一个单引号（'）表示音频时长，并最终返回这个字符串。
//
//    这个方法可以用于在用户界面中显示音频的时长，以便用户了解音频的持续时间。通常，它在音频播放器或录音应用中使用，用于显示音频文件的时长信息。


    public void setAudioLayoutWidth(RelativeLayout layout, long duration) {
        // 设置音频布局的宽度

        float perSecondWidth = 4.0f;
        // 每秒的宽度，以像素为单位
        float second = duration / 1000.0f;
        // 音频的时长，以毫秒为单位
        float width = second * perSecondWidth;
        // 计算出的布局宽度，以像素为单位

        if (width < 60) {
            width = 60.0f;
        } else if (width > 240) {
            width = 240.0f;
        }
        // 确保布局宽度在一定的范围内，例如最小宽度为60像素，最大宽度为240像素

        int dpWidth = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width,
                mContext.getResources().getDisplayMetrics()));
        // 将计算出的宽度从像素转换为设备独立像素（dp）

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        // 获取音频布局的参数
        params.width = dpWidth;
        // 设置布局的宽度为计算后的宽度
        layout.setLayoutParams(params);
        // 应用设置后的参数到布局
    }
//    这段代码定义了一个方法 setAudioLayoutWidth，用于设置音频布局的宽度。传入的参数包括一个 RelativeLayout（音频布局）和音频的持续时间 duration。
//
//    首先，通过 perSecondWidth 定义每秒的宽度（以像素为单位），然后根据音频的持续时间计算出布局的宽度（以像素为单位），并确保它在一定的范围内（60像素到240像素之间）。接下来，通过 TypedValue 将像素转换为设备独立像素（dp），然后将这个宽度应用到音频布局的参数中，从而设置音频布局的宽度。
//
//    这个方法主要用于动态设置音频波形图的宽度，以适应不同长度的音频片段。


    public Bitmap getVideoCover(VideoAttachment attachment) {
        // 获取视频封面的方法，该方法用于根据给定的VideoAttachment获取视频封面的Bitmap。

        if (!TextUtils.isEmpty(attachment.getThumbPath())) {
            // 如果VideoAttachment中包含缩略图路径

            Bitmap bitmap = mLruCache.get(attachment.getThumbPath());
            // 从LRU缓存中尝试获取缩略图的Bitmap

            if (bitmap == null) {
                // 如果LRU缓存中没有对应的Bitmap

                bitmap = ImageUtils.getBitmapFromFile(attachment.getThumbPath(), 400, 180);
                // 从文件中读取缩略图，并设置其宽高为400x180像素

                mLruCache.put(attachment.getThumbPath(), bitmap);
                // 将获取到的Bitmap放入LRU缓存，以便下次快速访问

            }
            return bitmap;
            // 返回获取到的Bitmap
        }

        if (!TextUtils.isEmpty(attachment.getPath())) {
            // 如果VideoAttachment中包含视频路径

            Bitmap bitmap = mLruCache.get(attachment.getPath());
            // 从LRU缓存中尝试获取视频封面的Bitmap

            if (bitmap == null) {
                // 如果LRU缓存中没有对应的Bitmap

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(attachment.getPath());
                // 创建一个MediaMetadataRetriever用于获取视频信息

                bitmap = retriever.getFrameAtTime();
                // 从视频中获取一帧作为封面

                mLruCache.put(attachment.getPath(), bitmap);
                // 将获取到的Bitmap放入LRU缓存，以便下次快速访问

            }
            return bitmap;
            // 返回获取到的视频封面的Bitmap
        }

        return null;
        // 如果VideoAttachment中既没有缩略图路径，也没有视频路径，则返回null
    }
//    这段代码定义了一个方法 getVideoCover，用于获取视频封面的Bitmap。它接受一个 VideoAttachment 参数，该对象包含视频的相关信息。在方法内部，它首先检查是否存在缩略图路径，如果存在则尝试从LRU缓存中获取缩略图的Bitmap，如果缓存中没有，则从文件中读取并放入缓存。如果缩略图路径不存在，则检查是否存在视频路径，如果存在则从LRU缓存中获取视频封面的Bitmap，如果缓存中没有，则使用 MediaMetadataRetriever 从视频中获取一帧作为封面图像，同时也将其放入缓存。最后，如果既没有缩略图路径，也没有视频路径，则返回null。这个方法可以用于在应用中显示视频的封面图像。

    public String getVideoTime(long duration) {
        // 这个方法用于将视频时长（以毫秒为单位）转换为格式化的时间字符串，如“0:05”（表示5秒）。

        // 将视频时长从毫秒转换为秒
        int second = (int) (duration / 1000);

        if (second < 10) {
            // 如果视频时长小于10秒，将其格式化为“0:0X”形式，其中X是秒数
            return "0:0" + String.valueOf(second);
        } else {
            // 如果视频时长大于等于10秒，返回固定的时间字符串“0:10”，表示最大10秒的视频。
            return "0:10";
        }
    }
//    这段代码是一个辅助函数，用于将视频的时长（以毫秒为单位）转化为格式化的时间字符串，以分钟和秒的形式表示视频时长。在方法内部，视频时长首先从毫秒转换为秒，并然后将其格式化为0:0X或0:10的字符串，具体格式取决于视频时长是否小于10秒。这个函数通常在视频播放或显示时用于显示视频时长。

}
