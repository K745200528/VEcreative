package com.ezreal.ezchat.chat;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.suntek.commonlibrary.adapter.RViewHolder;

/**
 * Created by wudeng on 2017/8/30.
 */

public interface OnItemLongClickListener {
    // 定义了一个接口 OnItemLongClickListener
    void onItemLongClick(RViewHolder holder, IMMessage message);
    // 接口中有一个抽象方法 onItemLongClick，用于在实现这个接口的类中处理列表项的长按事件
}
//    这段代码定义了一个接口 OnItemLongClickListener，这个接口有一个抽象方法 onItemLongClick，用于在实现这个接口的类中处理列表项的长按事件。通常，你可以创建一个实现了 OnItemLongClickListener 接口的类，然后实现 onItemLongClick 方法以定义长按事件的行为。在列表或其他视图中，当某个项被长按时，可以调用实现了这个接口的类中的 onItemLongClick 方法来执行相关操作。这是一种常见的事件处理机制，用于处理用户在应用中的交互行为。
