package com.ezreal.ezchat.http;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;


import androidx.annotation.NonNull;

import com.ezreal.ezchat.ChatApplication;
import com.ezreal.ezchat.utils.CheckSumUtils;
import com.ezreal.ezchat.utils.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by wudeng on 2017/8/24.
 */

public class NimClientHandle {

    private static final String TAG = NimClientHandle.class.getSimpleName();
    // 定义了一个常量 TAG，用于标识日志输出的类名

    private String APP_SERVER_BASE_URL = "https://api.netease.im/nimserver/";
    // 设置了常量字符串 APP_SERVER_BASE_URL，表示云信服务器的基础URL

    private String mAppServerUserCreate = "user/create.action";
    private String mAppServerUserUpdate = "user/update.action";
    private String mAppServerTokenUpdate = "user/refreshToken.action";
    private String mAppServerUserInfo = "user/getUinfos.action";
    private String mAppServerUserInfoUpdate = "user/updateUinfo.action";
    // 设置了多个常量字符串，每个字符串表示不同的云信服务器API请求的具体路径

    private static NimClientHandle instance;
    // 定义了一个静态的 NimClientHandle 类型变量 instance，用于在程序中获取 NimClientHandle 的实例

    private OkHttpClient mOkHttpClient;
    // 定义了一个 OkHttp 客户端变量 mOkHttpClient，用于进行网络请求
//    这段代码定义了一个 NimClientHandle 类，该类可能是与云信（Netease IM）服务器通信的客户端处理类。其中包含了一系列常量，用于表示云信服务器不同API请求的路径。还有一个 OkHttp 客户端 mOkHttpClient，用于执行网络请求。这个类似乎是一个网络请求工具类，用于处理与云信服务器相关的请求。

    public static NimClientHandle getInstance() {
        // 获取单例的实例方法，确保只有一个实例

        if (instance == null) {
            // 如果实例为空，表示还没有创建

            synchronized (NimClientHandle.class) {
                // 进入同步块，确保多线程环境下只有一个线程能进入

                if (instance == null) {
                    // 再次检查实例是否为空，因为在进入同步块前可能其他线程已经创建了实例

                    instance = new NimClientHandle();
                    // 创建一个新的实例
                }
            }
        }

        return instance;
        // 返回实例
    }
//    这段代码是一个获取单例（Singleton）实例的方法。它使用了双重检查锁定（Double-Checked Locking）来确保在多线程环境下只有一个实例被创建。

    private NimClientHandle() {
        // 构造函数，创建 NimClientHandle 类的实例
        initApi();
        // 调用 initApi() 方法进行初始化
    }

    private void initApi() {
        // 初始化 API 的方法
        mOkHttpClient = new OkHttpClient();
        // 创建一个 OkHttpClient 的实例，通常用于网络请求
    }
//    这段代码定义了一个名为 NimClientHandle 的类，该类包含了构造函数和一个私有方法 initApi。构造函数在创建类的实例时会自动被调用，而 initApi 方法用于初始化类的成员变量 mOkHttpClient。OkHttpClient 是 OkHttp 库的一部分，通常用于进行网络请求和与服务器通信。
//
//    通过构造函数调用 initApi 方法，确保在创建 NimClientHandle 对象时，相关的初始化工作也会得以执行，如创建一个 OkHttpClient 实例。这有助于确保该类的成员变量在对象创建时具有适当的初始状态。

    public void register(String account, String token, String name, final OnRegisterListener listener) {
        // 这是一个用户注册的方法，用于向服务器注册新用户。

        final RequestBody body = new FormBody.Builder()
                .add("accid", account)
                .add("token", token)
                .add("name", name)
                .build();
        // 创建一个请求体，包含用户的账号、令牌和姓名。

        Request request = new Request.Builder()
                .url(APP_SERVER_BASE_URL + mAppServerUserCreate)
                .headers(createHeaders())
                .post(body)
                .build();
        // 创建一个HTTP POST请求，包含了用户注册所需的信息和服务器的地址。

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            // 使用OkHttp库发送HTTP请求，这里是异步操作，即请求将在后台线程中执行，回调方法将在请求完成后调用。

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 当请求失败时调用此方法，通常在网络问题或服务器不可达时触发。

                listener.onFailed(e.getMessage());
                // 调用注册监听器的 onFailed 方法，将失败的原因传递给监听器。
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 当请求成功响应时调用此方法，response 包含了服务器的响应数据。

                if (listener != null) {
                    if (response.code() == 200) {
                        // 如果服务器响应状态码为200，表示成功，通常用于表示请求成功。

                        Gson gson = new Gson();
                        // 创建 Gson 对象，用于将 JSON 数据解析成 Java 对象。

                        RegisterResp resp = gson.fromJson(response.body().string(),
                                new TypeToken<RegisterResp>(){}.getType());
                        // 解析服务器响应的 JSON 数据成一个 RegisterResp 对象，TypeToken 用于处理泛型。

                        if (resp == null){
                            listener.onFailed("解析返回数据失败" + response.body().string());
                            // 如果解析失败，通知监听器解析错误。
                            return;
                        }

                        if (resp.getCode() != 200){
                            listener.onFailed(resp.getDesc());
                            // 如果注册不成功，通知监听器注册失败，传递失败原因。
                            return;
                        }

                        listener.onSuccess();
                        // 如果注册成功，通知监听器注册成功。
                    } else {
                        listener.onFailed(response.message());
                        // 如果响应状态码不为200，通常表示请求失败，通知监听器失败原因。
                    }
                }
            }
        });
    }
//    这段代码是一个用户注册方法，它执行以下操作：
//
//    创建一个包含用户账号、令牌和姓名的请求体。
//    创建一个HTTP POST请求，包含请求体和服务器地址。
//    使用OkHttp库发送异步HTTP请求。
//    在请求响应成功时，解析服务器响应的JSON数据并通知注册成功。
//    在请求失败时，通知注册失败或出现的错误。

    public void updateToken(String account, String pass, final OnRegisterListener listener) {
        // 定义了一个名为 "updateToken" 的方法，用于更新用户的令牌，该方法接受三个参数：account（帐号）、pass（密码）和一个注册监听器。

        // 创建请求体（RequestBody）用于POST请求
        final RequestBody body = new FormBody.Builder()
                .add("accid", account)
                .add("token", pass)
                .build();
        // 创建一个表单请求体，添加了账号和令牌信息。

        // 创建HTTP请求
        Request request = new Request.Builder()
                .url(APP_SERVER_BASE_URL + mAppServerUserUpdate)
                .headers(createHeaders())
                .post(body)
                .build();
        // 创建一个HTTP请求对象，设置请求URL、请求头（headers）、请求方法（POST）以及请求体。

        // 使用OkHttp的异步请求方式
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onFailed(e.getMessage());
            }
            // 处理请求失败的情况，调用注册监听器的 onFailed 方法，传递错误信息。

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (listener != null) {
                    if (response.code() == 200) {
                        // 当HTTP响应码为200时，表示请求成功
                        Gson gson = new Gson();
                        // 创建Gson对象，用于解析JSON数据
                        RegisterResp resp = gson.fromJson(response.body().string(),
                                new TypeToken<RegisterResp>(){}.getType());
                        // 解析JSON响应并将其映射为 RegisterResp 类型的对象

                        if (resp == null) {
                            listener.onFailed("解析返回数据失败" + response.body().string());
                            return;
                        }
                        // 如果解析结果为空，调用监听器的 onFailed 方法，传递错误信息。

                        if (resp.getCode() != 200) {
                            listener.onFailed(resp.getDesc());
                            return;
                        }
                        // 如果返回的状态码不为200，调用监听器的 onFailed 方法，传递错误信息。

                        listener.onSuccess();
                        // 若一切正常，调用监听器的 onSuccess 方法，表示注册成功。
                    } else {
                        listener.onFailed(response.message());
                    }
                    // 如果HTTP响应码不为200，调用监听器的 onFailed 方法，传递响应消息中的错误信息。
                }
            }
            // 处理HTTP响应的回调方法
        });
    }
//    这段代码定义了一个方法 updateToken，用于向服务器发送更新用户令牌的请求。在方法内部，首先创建一个表单请求体，添加了用户帐号和令牌信息。然后，创建了一个HTTP请求对象，设置了请求的URL、请求头、请求方法（POST）和请求体。接下来，使用OkHttp的异步请求方式发送HTTP请求，当请求完成后，会触发 onResponse 或 onFailure 方法，用于处理请求的成功或失败情况。
//
//    在 onResponse 方法中，首先检查HTTP响应码，如果为200，表示请求成功，然后使用Gson库解析JSON响应，并检查响应中的状态码。如果一切正常，调用注册监听器的 onSuccess 方法，表示令牌更新成功。如果HTTP响应码不为200，或者解析出错，调用 onFailed 方法，传递相应的错误信息。
//
//    这个方法的主要目的是与服务器通信，更新用户的令牌信息，并根据服务器响应处理注册的结果。

    /**
     * 生成访问 NIM  APP-SERVICE 所要求的 HEADER
     *
     * @return headers ,in OK HTTP3
     */
    private Headers createHeaders() {
        // 创建一个名为 createHeaders 的私有方法

        String nonce = CheckSumUtils.getNonce();
        // 生成一个随机数 nonce，用于安全性，非常数的值

        String time = String.valueOf(System.currentTimeMillis() / 1000L);
        // 获取当前时间戳，并将其转换为秒

        return new Headers.Builder()
                // 创建一个请求头 Headers.Builder

                .add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                // 添加一个请求头字段 "Content-Type"，设置请求的内容类型为 "application/x-www-form-urlencoded;charset=utf-8"

                .add("AppKey", readAppKey())
                // 添加一个请求头字段 "AppKey"，并调用 readAppKey 方法来获取应用密钥

                .add("Nonce", nonce)
                // 添加一个请求头字段 "Nonce"，设置为之前生成的随机数 nonce

                .add("CurTime", time)
                // 添加一个请求头字段 "CurTime"，设置为当前的时间戳

                .add("CheckSum", CheckSumUtils.getCheckSum(Constant.APP_SECURY, nonce, time))
                // 添加一个请求头字段 "CheckSum"，并调用 CheckSumUtils.getCheckSum 方法来计算校验和

                .build();
        // 构建并返回请求头对象
    }
//    这段代码定义了一个 createHeaders 方法，该方法用于创建 HTTP 请求的头部信息（Headers）。头部信息通常包含了请求的元数据，如内容类型、应用密钥、随机数（nonce）、时间戳（timestamp）以及校验和（CheckSum）等。

    /**
     * 读取存储于manifest文件下的 APP KEY
     *
     * @return APP key
     */
    private String readAppKey() {
        // 定义一个用于读取 AppKey 的方法
        try {
            // 尝试执行以下代码块
            ApplicationInfo appInfo = ChatApplication.getInstance().getPackageManager()
                    .getApplicationInfo(ChatApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            // 获取 ApplicationInfo 对象，其中包含了应用程序的信息，包括元数据
            if (appInfo != null) {
                // 如果 ApplicationInfo 不为 null，表示找到了应用信息
                return appInfo.metaData.getString("com.netease.nim.appKey");
                // 从应用元数据中获取名为 "com.netease.nim.appKey" 的字符串值，通常这是 App 的唯一标识
            }
        } catch (Exception e) {
            // 捕获可能抛出的异常
            e.printStackTrace();
            // 打印异常堆栈信息，通常用于调试和错误追踪
            return "";
            // 出现异常时，返回空字符串表示获取 AppKey 失败
        }
        return "";
        // 如果没有异常，同样返回空字符串，表示获取 AppKey 失败
    }
//    这段代码定义了一个 readAppKey 方法，用于获取应用程序的 AppKey。在 Android 应用程序中，AppKey 通常是用于标识应用程序的唯一字符串，特别是在使用第三方服务或库时，如云服务或 IM（即时通讯）服务。这个方法首先尝试获取应用程序的元数据（metadata），然后从中提取名为 "com.netease.nim.appKey" 的字符串值，这个值通常是 App 的标识。
//
//    注意，由于可能会出现异常，如找不到元数据或其他错误，所以在方法内使用了异常处理，以便在出现问题时返回一个空字符串表示获取 AppKey 失败。这个方法一般用于初始化 IM 或其他需要 AppKey 的服务。

}
