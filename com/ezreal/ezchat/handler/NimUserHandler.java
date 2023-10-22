package com.ezreal.ezchat.handler;

import android.util.Log;

import com.ezreal.ezchat.bean.LocalAccountBean;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.UserServiceObserve;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.suntek.commonlibrary.utils.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wudeng on 2017/8/30.
 */

public class NimUserHandler {
    // 定义了一个类 NimUserHandler，处理云信用户信息的管理

    private static final String TAG = NimUserHandler.class.getSimpleName();
    // 定义一个常量字符串 TAG，用于在日志中标识该类
    private static NimUserHandler instance;
    // 声明一个静态的 NimUserHandler 实例，用于实现单例模式
    private String mMyAccount;
    // 用户的账号信息
    private NimUserInfo mUserInfo;
    // 云信用户信息
    private LocalAccountBean mLocalAccount;
    // 本地账户信息
    private List<OnInfoUpdateListener> mUpdateListeners;
    // 信息更新监听器列表
//    这个类的主要作用是管理用户信息，包括云信的用户信息和本地的账户信息。它还提供了方法来添加和通知信息更新的监听器。

    public static NimUserHandler getInstance() {
        // 静态方法，用于获取 NimUserHandler 的实例
        if (instance == null) {
            // 如果实例尚未创建
            synchronized (NimUserHandler.class) {
                // 使用 synchronized 块来保证多线程安全
                if (instance == null) {
                    // 再次检查实例是否为 null，以防止其他线程已经创建实例
                    instance = new NimUserHandler();
                    // 创建 NimUserHandler 的新实例
                }
            }
        }
        return instance;
        // 返回 NimUserHandler 的实例
    }
//    这段代码实现了一个单例模式的 getInstance 方法，用于获取 NimUserHandler 类的唯一实例。单例模式确保一个类只有一个实例，并提供一个全局访问点以便获取该实例。

    public void init() {
        // 初始化方法

        // 获取当前用户的信息，通常是登录用户的信息
        mUserInfo = NIMClient.getService(UserService.class).getUserInfo(mMyAccount);

        // 创建一个用于保存监听器的 ArrayList
        mUpdateListeners = new ArrayList<>();

        // 初始化监听，观察用户信息的更新
        NIMClient.getService(UserServiceObserve.class)
                .observeUserInfoUpdate(new Observer<List<NimUserInfo>>() {
                    @Override
                    public void onEvent(List<NimUserInfo> userInfoList) {
                        // 当用户信息更新时触发的事件
                        NimUserInfo userInfo = userInfoList.get(0);
                        // 获取更新的用户信息
                        Log.e(TAG,"UserInfoUpdate" + userInfo.toString());
                        // 输出用户信息的日志
                    }
                }, true);
        // 使用 NIM SDK 提供的服务观察用户信息的更新，并添加一个观察者用于监听这些更新，true 参数表示需要主动拉取用户信息。
    }
//    这段代码定义了一个名为 init 的方法。该方法主要用于初始化用户信息和设置一个用户信息更新监听器，以便在用户信息更新时执行特定的操作。

    /**
     * 从服务器账户数据到本地数据库
     */
    public void fetchAccountInfo() {
        // 创建一个用于存储账户的列表
        List<String> accounts = new ArrayList<>();
        // 向列表中添加当前用户的账号
        accounts.add(mMyAccount);

        // 通过 NIMClient 获取 UserService 实例，用于处理用户信息
        NIMClient.getService(UserService.class).fetchUserInfo(accounts)
//        setCallback(new RequestCallback<List<NimUserInfo>>() { ... });: 设置一个回调函数，用于处理获取用户信息的结果。
                .setCallback(new RequestCallback<List<NimUserInfo>>() {
                    @Override
                    public void onSuccess(List<NimUserInfo> param) {
                        Log.e(TAG, "fetchAccountInfo onSuccess");
                        // 请求成功，获取用户信息
                        mUserInfo = param.get(0);
                        // 同步成功后，通知所有的信息更新监听器
                        for (OnInfoUpdateListener listener : mUpdateListeners) {
                            listener.myInfoUpdate();
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.e(TAG, "fetchAccountInfo onFailed code " + code);
                        // 请求失败，记录失败的错误码
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Log.e(TAG, "fetchAccountInfo onException message " + exception.getMessage());
                        // 请求发生异常，记录异常信息
                    }
                });
    }
//    这段代码的主要作用是获取用户的账户信息，以及在获取成功后通知相关的信息更新监听器。

    public void syncChange2Service() {
        // 同步本地数据到云端服务器的方法
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>();
        // 创建一个映射用于存储用户信息字段和值

        if (!TextUtils.isEmpty(mLocalAccount.getHeadImgUrl())) {
            fields.put(UserInfoFieldEnum.AVATAR, mLocalAccount.getHeadImgUrl());
        }
        // 如果本地账户的头像URL非空，将头像URL添加到字段映射中

        if (!TextUtils.isEmpty(mLocalAccount.getBirthDay())) {
            fields.put(UserInfoFieldEnum.BIRTHDAY, mLocalAccount.getBirthDay());
        }
        // 如果本地账户的生日非空，将生日添加到字段映射中

        if (!TextUtils.isEmpty(mLocalAccount.getLocation())) {
            fields.put(UserInfoFieldEnum.EXTEND, mLocalAccount.getLocation());
        }
        // 如果本地账户的位置信息非空，将位置信息添加到字段映射中

        if (!TextUtils.isEmpty(mLocalAccount.getSignature())) {
            fields.put(UserInfoFieldEnum.SIGNATURE, mLocalAccount.getSignature());
        }
        // 如果本地账户的个性签名非空，将个性签名添加到字段映射中

        fields.put(UserInfoFieldEnum.Name, mLocalAccount.getNick());
        // 将本地账户的昵称添加到字段映射中

        fields.put(UserInfoFieldEnum.GENDER, mLocalAccount.getGenderEnum().getValue());
        // 将本地账户的性别信息添加到字段映射中

        NIMClient.getService(UserService.class).updateUserInfo(fields)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        Log.e(TAG, "syncChange2Service onSuccess");
                        // 同步成功后的操作：记录日志，并更新本地数据库

                        fetchAccountInfo();
                        // 获取最新账户信息
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.e(TAG, "syncChange2Service onFailed code " + code);
                        // 同步失败后的操作：记录失败日志，可能需要后续处理

                        // TODO 同步失败应当后台服务上传
                        // 可以在这里添加将同步失败的数据上传至后台的逻辑
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Log.e(TAG, "syncChange2Service onException message " + exception.getMessage());
                        // 异常情况下的处理，通常也需要记录日志
                    }
                });
        // 使用云信 SDK 提供的 UserService 更新用户信息，将字段映射传递给云端
    }
//    这段代码实现了一个用于将本地用户信息同步到云端服务器的方法 syncChange2Service。首先，它创建一个映射 fields，用于存储用户信息字段和相应的值。然后，根据本地账户的不同属性（头像、生日、位置、个性签名、昵称和性别），将相应的字段和值添加到 fields 映射中。
//
//    接下来，使用云信（NIM）的 UserService 提供的 updateUserInfo 方法将 fields 映射传递给云端服务器，以更新用户信息。在此之后，通过回调函数处理操作的结果，包括成功、失败和异常情况。成功时，记录成功日志并更新本地数据库；失败时，记录失败日志并执行可能需要的后续处理，例如将同步失败的数据上传至后台；异常情况下，记录异常信息。
//
//    这段代码用于实现用户信息的同步功能，确保用户在本地应用中的个人信息与云端服务器保持同步。

    public LocalAccountBean getLocalAccount() {
        // 创建一个新的 LocalAccountBean 实例
        mLocalAccount = new LocalAccountBean();

        // 从 mUserInfo 对象中获取各种个人信息并设置到 mLocalAccount 实例中
        mLocalAccount.setAccount(mUserInfo.getAccount());
        // 设置用户帐号
        mLocalAccount.setHeadImgUrl(mUserInfo.getAvatar());
        // 设置用户头像 URL
        mLocalAccount.setBirthDay(mUserInfo.getBirthday());
        // 设置用户生日
        mLocalAccount.setNick(mUserInfo.getName());
        // 设置用户昵称
        mLocalAccount.setSignature(mUserInfo.getSignature());
        // 设置用户签名
        mLocalAccount.setGenderEnum(mUserInfo.getGenderEnum());
        // 设置用户性别

        // 从 mUserInfo 对象中获取用户扩展信息
        String extension = mUserInfo.getExtension();
        if (!TextUtils.isEmpty(extension)){
            // 如果扩展信息不为空，设置到 mLocalAccount 实例中的位置属性
            mLocalAccount.setLocation(extension);
        }

        // 返回包含用户信息的 mLocalAccount 实例
        return mLocalAccount;
    }
//    这段代码是一个方法，它用于从 mUserInfo 对象中提取用户信息，并将这些信息填充到一个新的 mLocalAccount 对象中。这个方法允许你获取本地的用户信息以便在应用中使用。首先，它创建一个 mLocalAccount 实例。然后，通过调用 mUserInfo 对象的不同方法，获取用户的帐号、头像 URL、生日、昵称、签名和性别信息，并将这些信息设置到 mLocalAccount 对象中。最后，它检查用户的扩展信息是否为空，如果不为空，则设置到 mLocalAccount 对象的位置属性中。最终，返回 mLocalAccount 对象，其中包含了用户的各种信息。


    public void setLocalAccount(LocalAccountBean account) {
        // 设置本地用户账户信息
        this.mLocalAccount = account;
    }

    public String getMyAccount() {
        // 获取我的账户信息
        return mMyAccount;
    }

    public void setMyAccount(String account) {
        // 设置我的账户信息
        mMyAccount = account;
    }

    public NimUserInfo getUserInfo() {
        // 获取用户信息
        return mUserInfo;
    }

    public void setUpdateListeners(OnInfoUpdateListener listeners) {
        // 设置信息更新监听器
        mUpdateListeners.add(listeners);
    }

    public interface OnInfoUpdateListener {
        // 定义信息更新监听器接口
        void myInfoUpdate();
    }
//    这段代码定义了一个类，其中包括设置和获取本地用户账户信息的方法，以及设置和获取我的账户信息的方法。此外，还定义了一个 NimUserInfo 用户信息对象的获取方法，用于获取用户信息。最后，它包含了一个信息更新监听器接口 OnInfoUpdateListener，其中有一个抽象方法 myInfoUpdate 用于在信息更新时通知监听器的实现类。这些方法和接口通常用于处理用户账户信息的管理和更新。
}
