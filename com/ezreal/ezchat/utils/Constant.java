package com.ezreal.ezchat.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by wudeng on 2017/8/24.
 */

public class Constant {
    // 定义了一个常量类 Constant

    public static final String APP_SECURY = "a8ab0eefb250";
    // 定义了一个公共的常量 APP_SECURY 并初始化为 "a8ab0eefb250"
//    这段代码定义了一个常量类 Constant，其中包含一个公共的常量 APP_SECURY。常量通常用于存储不会改变的数值或字符串，以便在整个应用程序中共享使用。在这里，APP_SECURY 常量的值是 "a8ab0eefb250"，并且标记为公共的，因此其他类可以访问并使用这个常量。这可以用于存储应用程序的一些配置信息或密钥，以便在代码的不同部分中重复使用，同时保持可维护性和一致性。

    /**
     * SharePreference 相关
     */
    public static final String LOCAL_LOGIN_TABLE = "LOGIN_INFO";
// 定义一个字符串常量 LOCAL_LOGIN_TABLE，用于表示本地登录信息存储的表名。

    public static final String LOCAL_USER_ACCOUNT = "USER_ACOUNT";
// 定义一个字符串常量 LOCAL_USER_ACCOUNT，用于表示本地用户账户信息的键名。

    public static final String LOCAL_USER_TOKEN = "USER_TOKEN";
// 定义一个字符串常量 LOCAL_USER_TOKEN，用于表示本地用户令牌信息的键名。

    public static final String OPTION_TABLE = "OPTION_TABLE";
// 定义一个字符串常量 OPTION_TABLE，用于表示选项信息存储的表名。

    public static final String OPTION_KEYBOARD_HEIGHT = "OPTION_KEYBOARD_HEIGHT";
// 定义一个字符串常量 OPTION_KEYBOARD_HEIGHT，用于表示保存键盘高度选项的键名。

//    这段代码定义了一些字符串常量，用于表示数据库表名和键名。通常，这些常量用于访问和管理本地存储的信息。例如，LOCAL_LOGIN_TABLE 可能用于指定存储登录信息的数据库表名称，LOCAL_USER_ACCOUNT 可能用于指定本地存储的用户账户信息的键名。这些常量的使用可以提高代码的可维护性，因为它们可以在整个应用中重复使用，而不需要多次输入相同的字符串。在代码的其他部分，这些常量可以用作键名，以从本地存储中获取或存储相应的信息。

    /**
     * APP 缓存文件夹根目录
     */
    public static final String APP_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "EzChat";
// 声明一个常量字符串 APP_CACHE_PATH，用于表示应用的缓存根目录路径。
// Environment.getExternalStorageDirectory() 返回外部存储的根目录路径，通常是内置存储或SD卡。
// File.separator 用于添加目录分隔符，以确保路径分隔符符合系统规范。

    public static final String APP_CACHE_AUDIO = Constant.APP_CACHE_PATH + File.separator + "audio";
// 声明一个常量字符串 APP_CACHE_AUDIO，表示存储音频缓存的子目录路径，连接到根目录路径。

    public static final String APP_CACHE_IMAGE = Constant.APP_CACHE_PATH + File.separator + "image";
// 声明一个常量字符串 APP_CACHE_IMAGE，表示存储图像缓存的子目录路径，连接到根目录路径。

    public static final String APP_CACHE_VIDEO = Constant.APP_CACHE_PATH + File.separator + "video";
// 声明一个常量字符串 APP_CACHE_VIDEO，表示存储视频缓存的子目录路径，连接到根目录路径。
//    这段代码定义了一些字符串常量，用于表示应用的不同类型文件的缓存路径。通常，Android应用会将不同类型的文件，如音频、图像和视频等，存储在不同的目录下，以便更好地管理和访问这些文件。这些常量定义了缓存根目录路径以及与之相关的子目录路径，以便在应用中使用这些路径来存储和检索特定类型的文件。这有助于使应用的文件存储结构更有组织性，并提高了文件管理的效率。

}
