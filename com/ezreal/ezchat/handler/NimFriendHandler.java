package com.ezreal.ezchat.handler;


import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.FriendServiceObserve;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.friend.model.FriendChangedNotify;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wudeng on 2017/8/30.
 */

public class NimFriendHandler {
    // NimFriendHandler 类用于处理云信好友相关的操作

    private static final String TAG = NimFriendHandler.class.getSimpleName();
    // TAG 用于标识日志信息，通常用于调试和错误追踪

    private static NimFriendHandler instance;
    // 一个静态的 NimFriendHandler 实例，典型的单例模式

    private List<String> mFriendAccounts;
    // 存储好友账号的列表

    private List<NimUserInfo> mFriendInfos;
    // 存储好友信息的列表

    private List<Friend> mFriends;
    // 存储 Friend 对象的列表，表示好友关系

    private OnFriendUpdateListener mUpdateListener;
    // 用于监听好友列表的更新操作的监听器

//    这段代码定义了一个 NimFriendHandler 类，用于处理云信（Nim）中的好友相关操作。以下是对这段代码的详细解释：
//
//    NimFriendHandler 类是用于处理云信好友相关操作的类。
//    TAG 是一个字符串，通常用于标识日志信息，以便在调试和错误追踪时使用。
//    instance 是 NimFriendHandler 类的一个静态实例，通常用于实现单例模式，确保只有一个 NimFriendHandler 的实例存在。
//    mFriendAccounts 是一个存储好友账号的字符串列表。
//    mFriendInfos 是一个存储好友信息的 NimUserInfo 对象列表。
//    mFriends 是一个存储 Friend 对象的列表，表示好友关系。
//    mUpdateListener 是一个监听器，用于监听好友列表的更新操作。通常，它会定义一些回调方法，以便在好友列表发生变化时通知相关部分。
//    这段代码主要定义了 NimFriendHandler 类的属性，这些属性用于管理和维护云信中的好友信息。在实际应用中，这个类将会有更多的方法和功能，用于执行添加好友、删除好友、获取好友列表等操作。

    public static NimFriendHandler getInstance() {
        // 静态方法，用于获取 NimFriendHandler 的单例实例

        if (instance == null) {
            // 如果 instance 变量为 null，表示还没有创建实例
            synchronized (NimFriendHandler.class) {
                // 进入同步块，确保只有一个线程可以执行下面的代码

                if (instance == null) {
                    // 再次检查 instance 是否为 null，以防其他线程已经创建了实例
                    instance = new NimFriendHandler();
                    // 创建 NimFriendHandler 的实例
                }
            }
        }

        return instance;
        // 返回 NimFriendHandler 的单例实例
    }
//    这段代码是一个单例模式的经典实现方式。getInstance 方法用于获取 NimFriendHandler 的唯一实例。如果实例还没有被创建，它会在同步块内检查，以确保只有一个线程可以创建实例。这种方式可以避免多线程环境下创建多个实例，确保全局唯一性。单例模式通常用于管理全局状态或资源，以确保只有一个实例对其进行管理和访问。

    /**
     * 初始化好友列表工具类
     * 根据账户获取好友列表
     * 同步本地数据库的好友账户数据
     */
    public void init() {
        // 初始化方法

        // 初始化存储好友账号的列表
        mFriendAccounts = new ArrayList<>();
        // 初始化存储好友信息的列表
        mFriendInfos = new ArrayList<>();
        // 初始化存储好友对象的列表
        mFriends = new ArrayList<>();

        // 初始化好友列表更新监听
        NIMClient.getService(FriendServiceObserve.class)
                .observeFriendChangedNotify(new Observer<FriendChangedNotify>() {
                    @Override
                    public void onEvent(FriendChangedNotify notify) {
                        // 当好友列表发生变化时，执行以下操作

                        // 调用 loadFriendData 方法重新加载好友数据
                        loadFriendData();
                    }
                }, true);

        // 调用 loadFriendData 方法，初次加载好友数据
        loadFriendData();
    }
//    这段代码是一个 init 方法，用于初始化一些变量和监听器。在这段代码中：
//
//    mFriendAccounts 是一个存储好友账号的列表。
//    mFriendInfos 是一个存储好友信息的列表。
//    mFriends 是一个存储好友对象的列表。
//    接下来，代码通过以下步骤初始化好友列表的更新监听：
//
//    使用 NIM 客户端的 observeFriendChangedNotify 方法添加一个监听器，用于监视好友列表的变化。当好友列表发生变化时，将调用监听器中的 onEvent 方法。
//    在 onEvent 方法中，当监听到好友列表变化通知 (FriendChangedNotify) 时，会调用 loadFriendData 方法，重新加载好友数据。
//    最后，通过调用 loadFriendData 方法，初次加载好友数据。这个方法会在监听到好友列表变化时以及初始化时都被调用，确保好友数据的正确性。这是一个常见的操作，通常在应用中需要及时更新好友列表信息。

    public List<String> getFriendAccounts() {
        return mFriendAccounts;
    }
//    这个方法用于获取好友账号的列表。mFriendAccounts 是一个私有成员变量，它存储了用户的好友账号信息。通过调用这个方法，可以获得该列表，使其可被外部类访问。
    public List<NimUserInfo> getFriendInfos() {
        return mFriendInfos;
    }
//    这个方法用于获取好友信息的列表。mFriendInfos 是一个私有成员变量，它存储了用户的好友信息，通常包括好友的昵称、头像等信息。通过调用这个方法，可以获得该列表，使其可被外部类访问。
    public List<Friend> getFriends() {
        return mFriends;
    }
//    这个方法用于获取好友对象的列表。mFriends 是一个私有成员变量，它存储了用户的好友对象，通常包括好友的信息、状态等。通过调用这个方法，可以获得该列表，使其可被外部类访问。
    /**
     * 读取账户好友列表数据
     */
    private void loadFriendData() {
        // 清空之前的好友数据，以准备加载新数据
        mFriendAccounts.clear();

        // 获取所有好友的账号列表
        List<String> friendAccounts = NIMClient.getService(FriendService.class).getFriendAccounts();

        // 如果好友账号列表为空，直接返回
        if (friendAccounts == null || friendAccounts.isEmpty()) {
            return;
        }

        // 将获取到的好友账号列表添加到 mFriendAccounts 中
        mFriendAccounts.addAll(friendAccounts);

        // 清空之前的好友数据
        mFriends.clear();

        // 遍历好友账号列表，获取每个好友的详细信息
        Friend friend;
        for (String account : mFriendAccounts) {
            friend = NIMClient.getService(FriendService.class).getFriendByAccount(account);
            mFriends.add(friend);
        }

        // 清空之前的好友信息数据
        mFriendInfos.clear();

        // 获取好友的详细用户信息
        List<NimUserInfo> userInfoList = NIMClient.getService(UserService.class)
                .getUserInfoList(mFriendAccounts);
        // 将用户信息列表添加到 mFriendInfos 中
        mFriendInfos.addAll(userInfoList);

        // 更新用户界面中的好友信息，通过 mUpdateListener 接口通知外部
        if (mUpdateListener != null){
            mUpdateListener.friendUpdate();
        }
    }
//    这段代码执行了加载好友数据的操作。首先，它清空了之前的好友数据，然后通过 NIM 客户端的服务获取了好友的账号列表，如果列表为空就返回，否则将获取到的好友账号列表添加到 mFriendAccounts 集合中。接着，它清空之前的好友数据集合 mFriends，并通过遍历好友账号列表获取每个好友的详细信息，将这些好友信息添加到 mFriends 集合中。接下来，它清空之前的好友信息数据集合 mFriendInfos，然后获取好友的详细用户信息，将用户信息列表添加到 mFriendInfos 集合中。最后，通过 mUpdateListener 接口通知外部，即更新用户界面中的好友信息。这段代码用于在应用中加载和更新好友数据，以便在用户界面上显示好友的信息。

    /**
     * 设置好友列表更新监听
     * @param listener listener
     */
    public void setUpdateListener(OnFriendUpdateListener listener) {
        // 用于设置更新好友监听器
        this.mUpdateListener = listener;
        // 将传入的 listener 赋值给类内部的 mUpdateListener
    }
//    这段代码定义了一个方法 setUpdateListener，用于设置更新好友的监听器。这个方法的参数 listener 是一个实现了 OnFriendUpdateListener 接口的对象，通常是一个用于监听好友更新事件的类的实例。通过调用这个方法，你可以将一个监听器对象与当前的类关联起来，以便在好友更新事件发生时，执行监听器中定义的操作。这是一种回调模式，通常用于事件驱动的编程，以实现类之间的松散耦合，使代码更具可维护性和扩展性。

    /**
     * 检查该账户是否为我的好友
     *
     * @param account 待检查账户
     * @return true 如果对方是好友，否则返回 false
     */
    public boolean CheckIsMyFriend(String account) {
        // 检查是否为我的好友
        return NIMClient.getService(FriendService.class).isMyFriend(account);
        // 使用云信SDK的 FriendService 类来判断给定的账号是否是当前用户的好友
    }

    public void syncFriendInfo(List<String> accounts) {
        // 同步好友信息
        NIMClient.getService(UserService.class).fetchUserInfo(accounts);
        // 使用云信SDK的 UserService 类来获取一组账号对应的用户信息
    }

    public interface OnFriendUpdateListener {
        // 定义了一个接口 OnFriendUpdateListener
        void friendUpdate();
        // 接口中有一个抽象方法 friendUpdate，用于在实现这个接口的类中处理好友更新事件
    }
//    这段代码包括三个方法和一个接口：
//
//    CheckIsMyFriend(String account) 方法用于检查给定的账号是否是当前用户的好友。它使用了云信SDK中的 FriendService 类，通过 isMyFriend(account) 方法来判断。
//
//    syncFriendInfo(List<String> accounts) 方法用于同步一组账号对应的用户信息。它使用了云信SDK中的 UserService 类，通过 fetchUserInfo(accounts) 方法来获取用户信息。
//
//    OnFriendUpdateListener 是一个接口，定义了一个抽象方法 friendUpdate，该方法用于在实现这个接口的类中处理好友更新事件。通常，你可以创建一个实现了这个接口的类，然后实现 friendUpdate 方法以定义在好友更新时的行为。这是一种回调机制，用于监听好友相关事件。
//
//    这些方法和接口通常用于处理即时通讯应用中的好友管理和信息同步。
}
