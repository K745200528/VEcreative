package com.ezreal.ezchat.http;

/**
 * Created by wudeng on 2017/8/25.
 */

public interface OnRegisterListener {
    // 定义了一个接口 OnRegisterListener

    void onSuccess();
    // 声明了一个无参数的抽象方法 onSuccess，用于在注册成功时被调用。

    void onFailed(String message);
    // 声明了一个带有一个字符串参数的抽象方法 onFailed，用于在注册失败时被调用，并传递一个错误信息作为参数。
}
//    这段代码定义了一个接口 OnRegisterListener，这个接口用于处理用户注册操作的结果。它包含两个抽象方法，分别是 onSuccess 和 onFailed。
//
//        onSuccess() 方法用于在用户注册成功时被调用。通常，当用户成功注册后，实现了这个接口的类中的 onSuccess() 方法将执行相应的操作，例如显示注册成功的消息或者导航到其他界面。
//
//        onFailed(String message) 方法用于在用户注册失败时被调用，它接收一个字符串参数 message，该参数通常包含了注册失败的具体原因或错误消息。实现了这个接口的类可以根据 message 参数来采取适当的措施，例如显示注册失败的消息或者提示用户解决问题。
//
//        这样的接口通常用于与用户注册功能相关的操作，允许开发者定义注册成功和失败时的行为，以便更好地处理用户注册的结果。
