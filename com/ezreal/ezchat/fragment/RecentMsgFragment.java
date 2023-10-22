package com.ezreal.ezchat.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ezreal.ezchat.R;
import com.ezreal.ezchat.activity.P2PChatActivity;
import com.ezreal.ezchat.bean.RecentContactBean;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.suntek.commonlibrary.adapter.OnItemClickListener;
import com.suntek.commonlibrary.adapter.RViewHolder;
import com.suntek.commonlibrary.adapter.RecycleViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by wudeng on 2017/8/28.
 */

public class RecentMsgFragment extends BaseFragment {

    private static final String TAG = RecentMsgFragment.class.getSimpleName();
    // 声明一个常量字符串 TAG，用于标识当前类

    private RecyclerView mRecyclerView;
    // 用于显示消息列表的 RecyclerView

    private List<RecentContactBean> mContactList;
    // 存储最近联系人的数据的列表

    private RecycleViewAdapter<RecentContactBean> mViewAdapter;
    // 用于将数据绑定到 RecyclerView 的适配器

    private Observer<List<RecentContact>> mObserver;
    // 观察最近消息列表的变化的观察者

    private SimpleDateFormat mDateFormat;
    // 日期格式化工具
//    这段代码定义了一个名为 RecentMsgFragment 的类，它继承自 BaseFragment。以下是每个变量的用途：
//
//    TAG 是一个字符串常量，通常用于在日志或调试信息中标识当前类。
//    mRecyclerView 是一个 RecyclerView 对象，用于显示消息列表。
//    mContactList 是一个存储 RecentContactBean 对象的列表，用于存储最近联系人的数据。
//    mViewAdapter 是一个 RecycleViewAdapter 对象，用于将数据绑定到 RecyclerView。
//    mObserver 是一个观察 RecentContact 列表变化的观察者，通常用于实时更新 UI。
//    mDateFormat 是一个日期格式化工具，用于将日期转换成指定格式的字符串。
//    这些变量在 RecentMsgFragment 类中用于管理和展示最近消息的列表，并确保它们在视图中正确显示。


    @Override
    public int setLayoutID() {
//        这是一个公有方法，它返回一个整数类型的值。
        return R.layout.fragment_message;
//        在这行代码中，方法返回一个整数值，该值指定了当前类或片段将使用的布局资源文件，即 R.layout.fragment_message。这个布局文件包含了关于界面的定义，如视图控件的排列和外观。
    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public void initView(View rootView) {
        // 初始化视图方法，在该方法中对视图进行初始化和设置

        mRecyclerView = rootView.findViewById(R.id.rcv_message_list);
        // 找到布局中的 RecyclerView 控件并将其实例化

        mDateFormat = new SimpleDateFormat("HH:mm");
        // 创建一个时间格式化对象，用于将时间格式化为"HH:mm"的形式

        initRecyclerView();
        // 调用自定义的初始化 RecyclerView 的方法，设置 RecyclerView 的布局和适配器等

        initListener();
        // 调用自定义的初始化监听器的方法，设置用户界面的交互事件的监听

        loadRecentList();
        // 调用自定义的加载最近消息列表的方法，通常用于加载聊天应用中的最近对话列表
    }
//    这段代码是一个 Android Activity 或 Fragment 类中的 initView 方法。在该方法中，进行了如下操作：
//
//    使用 @SuppressLint("SimpleDateFormat") 注解，可能是用于告诉代码分析工具或编译器不要对该方法中使用 SimpleDateFormat 的情况发出警告。
//
//    mRecyclerView 是一个 RecyclerView 控件的实例，通过 rootView 找到布局文件中具有 R.id.rcv_message_list ID 的 RecyclerView 控件。
//
//    mDateFormat 是一个 SimpleDateFormat 对象，用于格式化时间。这里的格式为 "HH:mm"，表示时:分。
//
//    initRecyclerView() 方法用于初始化 RecyclerView 控件，包括设置其布局和适配器。
//
//    initListener() 方法用于初始化监听器，通常是为视图元素设置事件监听器，以响应用户的交互操作。
//
//    loadRecentList() 方法用于加载最近消息列表，这可能涉及到从数据源获取数据，并将数据展示在 RecyclerView 中，通常用于聊天应用等场景。


    private void initRecyclerView() {
        // 初始化 RecyclerView，设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        // 创建一个存储联系人数据的列表
        mContactList = new ArrayList<>();

        // 将布局管理器设置给 RecyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        // 创建 RecyclerView 适配器 mViewAdapter，使用泛型 RecentContactBean
        mViewAdapter = new RecycleViewAdapter<RecentContactBean>(getContext(), mContactList) {
            @Override
            public int setItemLayoutId(int position) {
                // 设置每个项的布局文件
                return R.layout.item_recent_msg;
            }

            @Override
            public void bindView(RViewHolder holder, int position) {
                // 在适配器中绑定视图数据
                RecentContactBean contactBean = mContactList.get(position);
                UserInfo userInfo = contactBean.getUserInfo();
                if (userInfo != null) {
                    // 如果有用户信息，设置用户头像和名称
                    mContactList.get(position).setUserInfo(userInfo);
                    // 更新联系人列表中的用户信息
                    holder.setImageByUrl(getContext(), R.id.iv_head_picture, contactBean.getUserInfo().getAvatar(), R.mipmap.bg_img_defalut);
                    // 使用 holder 对象设置列表项的头像图片，从用户信息中获取头像 URL，如果不存在则使用默认图片
                    holder.setText(R.id.tv_recent_name, contactBean.getUserInfo().getName());
                    // 使用 holder 对象设置列表项的文本内容，显示用户名称
                } else {
                    // 如果没有用户信息，设置默认头像和联系人 ID
                    holder.setImageResource(R.id.iv_head_picture, R.mipmap.app_logo);
                    // 使用 holder 对象设置列表项的头像图片为默认图标
                    holder.setText(R.id.tv_recent_name, contactBean.getRecentContact().getContactId());
                    // 使用 holder 对象设置列表项的文本内容，显示联系人 ID
                }
                holder.setText(R.id.tv_recent_content, contactBean.getRecentContact().getContent());
// 通过传递的视图 ID（R.id.tv_recent_content），在 RecyclerView 的 ViewHolder 中找到对应的 TextView 并设置文本内容。
// contactBean.getRecentContact().getContent() 用于获取联系人（contactBean）最近的消息内容，并将其设置到对应的 TextView 中。

                String time = mDateFormat.format(new Date(contactBean.getRecentContact().getTime()));
// 创建一个格式化日期的字符串，使用 mDateFormat 对象将时间戳转换为可读的日期格式。

                holder.setText(R.id.tv_recent_time, time);
// 同样，通过传递的视图 ID（R.id.tv_recent_time），在 RecyclerView 的 ViewHolder 中找到对应的 TextView 并设置日期时间字符串。
// 这段代码的目的是将联系人的最近消息内容和时间显示在 RecyclerView 中的相应视图上。

            }
        };

        // 设置 RecyclerView 项点击事件
        mViewAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RViewHolder holder, int position) {
                // 当列表项被点击时执行以下代码

                RecentContactBean contactBean = mContactList.get(position);
                // 获取列表中指定位置的 RecentContactBean 对象

                Intent intent;
                // 声明一个意图对象

                if (contactBean.getRecentContact().getSessionType() == SessionTypeEnum.P2P) {
                    // 如果该联系的会话类型是 P2P (点对点)，即一对一私聊

                    intent = new Intent(getContext(), P2PChatActivity.class);
                    // 创建一个新的意图，将 P2PChatActivity 类作为要启动的活动

                    intent.putExtra("NimUserInfo", contactBean.getUserInfo());
                    // 将 contactBean 中的用户信息作为额外数据添加到意图中

                    startActivity(intent);
                    // 启动指定的活动，即启动一对一聊天活动
                }
            }
        });

        // 设置 RecyclerView 的适配器
        mRecyclerView.setAdapter(mViewAdapter);
    }
//    这段代码主要实现了初始化 RecyclerView 和相关的适配器，以用于显示联系人列表的信息。通过 initRecyclerView() 方法，RecyclerView 的布局管理器、适配器以及点击事件都被设置。适配器使用 RecycleViewAdapter 并根据数据源绑定各个视图项的数据。当用户点击列表项时，根据会话类型不同，会跳转到不同的聊天活动。

    private void initListener() {
        // 初始化监听器
        mObserver = new Observer<List<RecentContact>>() {
            @Override
            public void onEvent(List<RecentContact> recentContacts) {
                // 当最近联系人列表有更新时触发的事件处理

                Log.e(TAG, "Observer RecentContact size = " + recentContacts.size());
                // 打印日志，输出最近联系人列表的大小

                if (mContactList.isEmpty()) {
                    // 如果联系人列表为空
                    List<RecentContactBean> contactBeans = createContactBeans(recentContacts);
                    // 利用最新的最近联系人数据创建联系人列表项
                    mContactList.addAll(contactBeans);
                    // 将新创建的联系人项添加到联系人列表
                    mViewAdapter.notifyDataSetChanged();
                    // 通知适配器数据已更新
                    return;
                }

                for (RecentContact contact : recentContacts) {
                    // 遍历最新的最近联系人列表
                    refreshRecentList(contact);
                    // 更新已存在的联系人列表项
                }
            }
        };
    }
//    这段代码是一个方法 initListener 的定义，该方法初始化了一个观察者对象 mObserver，并定义了观察者的 onEvent 方法，用于处理最近联系人列表的更新事件。具体解释如下：
//
//    initListener() 方法初始化了一个观察者对象 mObserver，这个观察者用于监听最近联系人列表的变化。
//
//    在 onEvent 方法中，当最近联系人列表有更新时，会触发这个方法，参数 recentContacts 是最新的最近联系人列表。
//
//    日志输出了最近联系人列表的大小，方便调试。
//
//    如果联系人列表为空（mContactList），则将最新的最近联系人数据转化为联系人列表项并添加到联系人列表 mContactList 中。
//
//    最后，使用适配器的 notifyDataSetChanged 方法通知适配器数据已更新，从而刷新界面。
//
//    如果联系人列表不为空，会遍历最新的最近联系人列表，逐一刷新已存在的联系人列表项。
//
//    这段代码的主要功能是监听最近联系人列表的变化并更新界面，确保联系人信息保持最新。

    private void refreshRecentList(RecentContact contact) {
        // 这是一个用于刷新最近联系人列表的方法，其中参数 contact 是最新的联系人会话。

        for (int i = 0; i < mContactList.size(); i++) {
            // 遍历当前的最近联系人列表。
            RecentContactBean bean = mContactList.get(i);

            if (bean.getRecentContact().getContactId().equals(contact.getContactId())) {
                // 如果找到了已经在列表中的联系人会话。

                bean.setRecentContact(contact);
                // 更新这个已存在的联系人会话对象。

                mViewAdapter.notifyItemChanged(i);
                // 通知适配器更新列表中这一项的数据。

                break;
            }

            if (i == mContactList.size() - 1) {
                // 如果遍历到了列表的最后一项，但仍未找到匹配的联系人。

                RecentContactBean newBean = new RecentContactBean();
                // 创建一个新的联系人会话对象。

                newBean.setRecentContact(contact);
                // 设置新对象的联系人会话为传入的 contact。

                NimUserInfo userInfo = getUserInfoByAccount(contact.getContactId());
                // 获取联系人的用户信息。

                if (userInfo != null) {
                    newBean.setUserInfo(userInfo);
                    // 如果用户信息已存在，设置新对象的用户信息。
                } else {
                    List<String> a = new ArrayList<>();
                    a.add(contact.getContactId());
                    getUserInfoRemote(a);
                    // 如果用户信息不存在，尝试从远程获取用户信息。
                }

                mContactList.add(0, newBean);
                // 将新的联系人会话对象添加到列表的开头，以保证它成为最近的一项。

                mViewAdapter.notifyItemInserted(0);
                // 通知适配器插入了一个新的列表项。

                break;
            }
        }
    }
//    这段代码的作用是用于刷新最近联系人列表。首先，它遍历当前的最近联系人列表，尝试查找是否有与传入的 contact 匹配的联系人会话。如果找到匹配的联系人，它会更新该联系人的信息。如果没有找到匹配的联系人，它会创建一个新的联系人会话对象，并将其添加到列表的开头，然后通知适配器插入了一个新的列表项。
//
//    这个方法的目的是确保最近联系人列表的内容与最新的联系人信息保持同步，并且按照最近联系时间的顺序显示。这是一种常见的用于维护联系人列表的操作。

    @Override
    public void onResume() {
        super.onResume();
        // 调用父类的 onResume 方法，执行父类的逻辑

        Log.e(TAG, "onResume");
        // 打印一个带有 TAG 为 "onResume" 的日志信息，通常用于调试

        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(mObserver, true);
        // 使用 NIMClient 获取消息服务，通过 MsgServiceObserve 类的 observeRecentContact 方法来注册观察者 mObserver，
        // 并启用这个观察者，用于观察最近联系人列表的变化
    }
//    这段代码是 Android 中的生命周期方法 onResume 的重写。在该方法中，首先调用了父类的 onResume 方法，执行了父类的一些逻辑。然后，通过 Log.e 方法记录了一条日志，以便开发者进行调试和追踪应用的运行情况。最后，使用 NIM（网易云信）的消息服务框架，注册了一个观察者 mObserver，并启用了这个观察者，以观察最近联系人列表的变化。
//
//    这段代码主要用于在 Activity 的生命周期中做一些初始化和注册观察者的工作，确保在界面恢复到前台时，能够正常响应最近联系人列表的变化。

    @Override
    public void onPause() {
        super.onPause();
        // 调用父类的 onPause 方法，以确保执行默认的暂停操作
        Log.e(TAG, "onPause");
        // 使用 Android 的 Log 类记录一条消息，通常用于调试目的，这里记录了 "onPause"
        // 表示应用在进入暂停状态时会执行这个方法
    }
//    这段代码是一个方法覆盖（Override），它覆盖了父类的 onPause 方法。当 Android 应用进入暂停状态时，系统会自动调用这个方法。在这里，开发者在 onPause 方法中添加了一个日志记录，以便跟踪应用的生命周期事件。这种记录对于调试和了解应用的运行状态非常有用。
//
//    在方法的开头，通过 super.onPause() 调用了父类的 onPause 方法，这是因为通常情况下，需要确保执行父类的操作，然后再进行自定义的操作。最后，使用 Log 类记录了一个日志消息，以 "onPause" 为内容，以便在开发和调试过程中查看。

    private void loadRecentList() {
        // 加载最近联系人列表的方法

        // 使用 NIMClient 的 MsgService 获取最近联系人
        NIMClient.getService(MsgService.class).queryRecentContacts()
                .setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
                    @Override
                    public void onResult(int code, List<RecentContact> result, Throwable exception) {
                        // 当加载完成后会执行这个回调方法

                        if (exception != null) {
                            // 如果加载过程中出现异常，记录错误信息并返回
                            Log.e(TAG, "loadRecentList exception = " + exception.getMessage());
                            return;
                        }

                        if (code != 200) {
                            // 如果加载操作返回了不是 200 的错误码，记录错误信息并返回
                            Log.e(TAG, "loadRecentList error code = " + code);
                            return;
                        }

                        // 如果加载成功，输出最近联系人的数量
                        Log.e(TAG, "loadRecentList size = " + result.size());

                        // 转换最近联系人的数据为自定义 RecentContactBean 类的列表
                        List<RecentContactBean> contactBeans = createContactBeans(result);

                        // 清空当前联系人列表，然后添加新的联系人数据
                        mContactList.clear();
                        mContactList.addAll(contactBeans);

                        // 通知视图适配器数据已更改
                        mViewAdapter.notifyDataSetChanged();
                    }
                });
    }
//    这段代码是一个方法，它用于从 NIM（NetEase IM SDK）中加载最近联系人列表。以下是代码的主要步骤和注释：
//
//            NIMClient.getService(MsgService.class)：通过 NIMClient 获取消息服务 MsgService 实例，用于操作消息相关的功能。
//
//    queryRecentContacts()：使用 MsgService 提供的方法查询最近联系人，这将返回一个最近联系人的列表。
//
//    setCallback(new RequestCallbackWrapper<List<RecentContact>>() { ... }：设置一个回调函数，当查询操作完成后，会调用这个回调函数。
//
//    onResult(int code, List<RecentContact> result, Throwable exception)：在回调中处理查询结果，包括错误情况。如果发生异常，会记录错误信息并返回。如果返回的错误码不是 200，同样会记录错误信息并返回。
//
//            Log.e(TAG, "loadRecentList size = " + result.size())：如果查询成功，输出最近联系人列表的大小。
//
//    List<RecentContactBean> contactBeans = createContactBeans(result)：将 SDK 返回的 RecentContact 列表转换为应用中自定义的 RecentContactBean 列表。
//
//            mContactList.clear()：清空应用中保存最近联系人数据的列表。
//
//            mContactList.addAll(contactBeans)：将新的最近联系人数据添加到应用的列表中。
//
//            mViewAdapter.notifyDataSetChanged()：通知视图适配器数据已更改，以便刷新UI。
//
//    这段代码的主要目的是加载和刷新最近联系人列表，以确保应用中的最近联系人信息保持最新。

    private List<RecentContactBean> createContactBeans(List<RecentContact> recentContacts) {
        // 创建一个方法，用于将 RecentContact 列表转换成 RecentContactBean 列表

        List<String> accounts = new ArrayList<>();
        // 创建一个字符串列表 accounts，用于存储联系人的账号

        List<RecentContactBean> beanList = new ArrayList<>();
        // 创建一个 RecentContactBean 列表 beanList，用于存储转换后的数据

        RecentContactBean bean;
        // 创建一个 RecentContactBean 对象 bean，用于暂存每个 RecentContact 的信息

        for (RecentContact contact : recentContacts) {
            // 遍历 recentContacts 列表中的每个 RecentContact

            bean = new RecentContactBean();
            // 创建一个 RecentContactBean 对象 bean 用于封装 RecentContact 的信息

            bean.setRecentContact(contact);
            // 将当前 RecentContact 对象与 RecentContactBean 关联

            NimUserInfo userInfo = getUserInfoByAccount(contact.getContactId());
            // 调用 getUserInfoByAccount 方法根据联系人账号获取用户信息

            if (userInfo != null) {
                bean.setUserInfo(userInfo);
                // 如果获取到用户信息，将其设置到 RecentContactBean 中
            } else {
                accounts.add(contact.getContactId());
                // 如果未获取到用户信息，将联系人的账号添加到 accounts 列表
            }

            beanList.add(bean);
            // 将当前处理好的 RecentContactBean 添加到 beanList 列表
        }

        if (!accounts.isEmpty()) {
            getUserInfoRemote(accounts);
            // 如果 accounts 列表不为空，说明有未获取用户信息的账号，调用 getUserInfoRemote 方法远程获取用户信息
        }

        return beanList;
        // 返回处理好的 RecentContactBean 列表
    }
//    这段代码定义了一个方法 createContactBeans，用于将 RecentContact 列表转换为 RecentContactBean 列表。在这个方法中，首先创建了两个列表 accounts 和 beanList，分别用于存储联系人账号和转换后的数据。然后，通过循环遍历 recentContacts 列表中的每个 RecentContact 对象，将其转换成 RecentContactBean，并尝试获取对应的用户信息。如果获取到用户信息，将其设置到 RecentContactBean 中，否则将该联系人的账号添加到 accounts 列表中。最后，如果 accounts 列表不为空，说明有未获取用户信息的账号，将调用 getUserInfoRemote 方法来远程获取用户信息。最终返回处理好的 beanList 列表。这个方法用于构建最近联系人列表，通常在聊天应用中用于展示用户的最近聊天记录。

    private NimUserInfo getUserInfoByAccount(String account) {
        // 创建一个方法，用于根据账号获取用户信息

        return NIMClient.getService(UserService.class).getUserInfo(account);
        // 使用 NIMClient 获取用户服务，然后调用 getUserInfo 方法，传入账号参数，以获取与该账号关联的用户信息。
    }
//    这段代码中的方法 getUserInfoByAccount 用于根据给定的账号获取用户信息。首先，它通过 NIMClient.getService(UserService.class) 获取了 NIM 用户服务的实例，然后调用 getUserInfo 方法，将账号作为参数传递。这个方法会根据提供的账号查询并返回与该账号相关的用户信息。这通常用于获取用户的详细信息，例如昵称、头像、性别等。


    private void getUserInfoRemote(List<String> accounts) {
        // 创建一个方法 getUserInfoRemote，接受一个 String 列表作为参数

        NIMClient.getService(UserService.class).fetchUserInfo(accounts)
                // 使用 NIMClient 的 UserService 获取用户信息服务，调用 fetchUserInfo 方法，传入要查询的用户账号列表 accounts

                .setCallback(new RequestCallback<List<NimUserInfo>>() {
                    // 调用 setCallback 方法，为异步请求设置回调接口，这里使用了 RequestCallback 的泛型，表示请求结果会返回 NimUserInfo 类型的数据

                    @Override
                    public void onSuccess(List<NimUserInfo> param) {
                        // 请求成功时执行的回调方法
                        updateView(param);
                        // 调用 updateView 方法，将获取的用户信息数据传递给该方法处理
                    }

                    @Override
                    public void onFailed(int code) {
                        // 请求失败时执行的回调方法
                        // 处理请求失败的逻辑，code 可能包含了错误信息
                    }

                    @Override
                    public void onException(Throwable exception) {
                        // 请求发生异常时执行的回调方法
                        // 处理请求异常的逻辑，exception 包含了异常信息
                    }
                });
    }
//    这段代码定义了一个名为 getUserInfoRemote 的方法，它用于从云端获取用户信息。在方法中，首先使用 NIMClient 的 UserService 获取用户信息服务，然后调用 fetchUserInfo 方法传入用户账号列表 accounts。接着，通过 setCallback 方法为异步请求设置回调，这个回调是一个匿名内部类，用于处理请求的成功、失败和异常情况。在成功时，会调用 updateView 方法，将获取的用户信息传递给该方法进行处理。在失败和异常时，可以根据具体情况进行适当的错误处理。这种异步请求的处理方式常用于与云服务交互，以确保不会阻塞应用程序的主线程。

    private void updateView(List<NimUserInfo> param) {
        // 定义一个用于更新视图的方法，接受一个 NimUserInfo 列表作为参数

        boolean isUpdate = false;
        // 创建一个布尔变量 isUpdate，用于标记是否需要更新视图

        for (NimUserInfo userInfo : param) {
            // 遍历传入的 NimUserInfo 列表

            for (RecentContactBean bean : mContactList) {
                // 遍历当前已有的 RecentContactBean 列表

                if (userInfo.getAccount().equals(bean.getRecentContact().getContactId())) {
                    // 如果传入的 NimUserInfo 对象的帐号与当前 RecentContactBean 的联系人 ID 匹配

                    bean.setUserInfo(userInfo);
                    // 更新 RecentContactBean 对象中的用户信息

                    isUpdate = true;
                    // 设置更新标志为 true
                }
            }
        }

        if (isUpdate && mViewAdapter != null) {
            // 如果需要更新且适配器不为 null

            mViewAdapter.notifyDataSetChanged();
            // 通知适配器数据已更改，以触发列表视图的刷新
        }
    }
//    这段代码是一个用于更新视图的方法。它接受一个 NimUserInfo 列表作为参数，遍历这个列表，查找是否有匹配的联系人并更新相应的 RecentContactBean 对象。如果有更新，它会通知适配器更新列表视图。
//
//    param 是传入的 NimUserInfo 列表，包含了用户信息。
//    isUpdate 是一个布尔变量，用于标记是否需要更新视图。
//    通过嵌套的循环遍历 param 和 mContactList，找到匹配的联系人并更新相应的 RecentContactBean 对象。
//    如果有更新并且适配器 mViewAdapter 不为 null，就调用 notifyDataSetChanged 方法通知适配器更新列表视图。
}
