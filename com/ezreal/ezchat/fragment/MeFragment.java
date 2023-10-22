package com.ezreal.ezchat.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.ezreal.ezchat.R;
import com.ezreal.ezchat.activity.AccountInfoActivity;
import com.ezreal.ezchat.activity.ChangePassActivity;
import com.ezreal.ezchat.activity.LoginActivity;
import com.ezreal.ezchat.bean.LocalAccountBean;
import com.ezreal.ezchat.handler.NimUserHandler;
import com.ezreal.ezchat.handler.NimUserHandler.OnInfoUpdateListener;
import com.joooonho.SelectableRoundedImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.suntek.commonlibrary.utils.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wudeng on 2017/8/28.
 */

public class MeFragment extends BaseFragment {
    // 创建 MeFragment 类，它继承自 BaseFragment，用于展示“我的”页面的内容。

    @BindView(R.id.iv_head_picture)
    SelectableRoundedImageView mHeadView;
    // 使用 ButterKnife 注解的方式，将一个 ImageView 组件与代码中的 mHeadView 变量绑定。

    @BindView(R.id.tv_user_name)
    TextView mTvName;
    // 使用 ButterKnife 注解的方式，将一个 TextView 组件与代码中的 mTvName 变量绑定。

    @BindView(R.id.tv_account)
    TextView mTvAccount;
    // 使用 ButterKnife 注解的方式，将一个 TextView 组件与代码中的 mTvAccount 变量绑定。
//    这段代码定义了一个名为 MeFragment 的类，它继承自 BaseFragment 类，用于展示用户的个人信息。在这个类中，使用 ButterKnife 库的注解方式，将布局文件中的 ImageView（用户头像）、TextView（用户名）和TextView（账户信息）分别与类中的 mHeadView、mTvName 和 mTvAccount 变量进行了绑定。
//
//    这些绑定后，你可以通过这些变量访问和操作这些 UI 组件，以便在 MeFragment 中显示用户的头像、用户名和账户信息等内容。这是 Android 开发中的一种常见做法，简化了 UI 元素的管理和操作。

    @Override
    public int setLayoutID() {
        // 返回要加载的布局资源的 ID
        return R.layout.fragment_me;
        // 这里指定了要加载的 Fragment 的布局资源文件
    }

    @Override
    public void initView(View rootView) {
        // 在这个方法中进行界面初始化和视图绑定的操作
        ButterKnife.bind(this, rootView);
        // 使用 ButterKnife 库绑定当前 Fragment 中的视图，以便更方便地访问和操作视图元素

        NimUserHandler.getInstance().setUpdateListeners(new OnInfoUpdateListener() {
            @Override
            public void myInfoUpdate() {
                // 当个人信息更新时，触发这个回调方法
                showOrRefreshView();
                // 调用 showOrRefreshView 方法来显示或刷新视图
            }
        });

        showOrRefreshView();
        // 初始时，显示或刷新视图
    }
//    这段代码是 Android 中的一个片段，通常是一个 Fragment。在这里，你有两个重要的方法：
//
//    setLayoutID(): 这个方法返回要加载的布局资源的 ID。在这里，返回了 R.layout.fragment_me，即加载名为 "fragment_me" 的布局文件。
//
//    initView(View rootView): 这个方法在视图初始化时调用。在其中，使用 ButterKnife 库来绑定视图元素，以便可以更方便地访问和操作它们。然后，设置了一个监听器，当个人信息更新时，会触发 myInfoUpdate 方法，并在该方法内调用 showOrRefreshView 来显示或刷新视图。
//
//    这段代码的目的是在 Fragment 初始化时设置布局并处理视图的初始化工作。它还监听个人信息的更新，以确保在信息变化时更新视图。


    @OnClick(R.id.layout_account)
// 这行代码使用 ButterKnife 注解，指示在点击 R.id.layout_account 元素时执行下面定义的方法。
    public void openAccountInfo() {
        // 这是一个公有方法，将在点击 R.id.layout_account 元素时被调用。
        Intent intent = new Intent(getContext(), AccountInfoActivity.class);
        // 创建一个新的 Intent 对象，用于启动 AccountInfoActivity 类。
        startActivity(intent);
        // 启动 AccountInfoActivity，跳转到用户账户信息页面。
    }
//    这段代码使用了 ButterKnife 库的注解 @OnClick 来设置一个点击事件监听器。当与指定 ID (R.id.layout_account) 关联的视图元素被点击时，将执行 openAccountInfo 方法。在 openAccountInfo 方法中，首先创建了一个 Intent 对象，该对象用于启动 AccountInfoActivity 类，然后通过 startActivity 启动了这个新的活动，从而实现了在点击特定视图元素时跳转到用户账户信息页面的功能。这是一种常见的方式，用于处理 Android 应用中的 UI 点击事件和导航。


    @OnClick(R.id.tv_logout)
// 使用 ButterKnife 注解，表示这个方法与布局中 id 为 "tv_logout" 的视图的点击事件关联
    public void logout(){
        // 这是一个处理注销操作的方法

        NIMClient.getService(AuthService.class).logout();
        // 使用 NIMClient 的 AuthService 类提供的方法来执行注销操作，退出登录
        // 这通常会清除用户的登录状态和相关信息

        Intent intent = new Intent(getContext(), LoginActivity.class);
        // 创建一个新的 Intent 对象，用于启动登录页面 (LoginActivity)

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // 设置 Intent 标志，清除现有任务栈中的所有活动，确保用户注销后无法返回到之前的界面

        startActivity(intent);
        // 启动 LoginActivity，即跳转到登录界面
    }
//    这段代码使用了 ButterKnife 框架的注解 @OnClick(R.id.tv_logout)，将 logout 方法与布局中 id 为 "tv_logout" 的视图的点击事件关联。当 "tv_logout" 被点击时，将触发 logout 方法。
//
//    在 logout 方法内部，执行了以下操作：
//
//    使用 NIMClient 的 AuthService 类提供的方法 logout，这是 IM (Instant Messaging) SDK 的一部分，用于执行注销操作，即用户退出登录。
//
//    创建一个新的 Intent 对象，用于启动登录页面 (LoginActivity)。
//
//    设置 Intent 标志 (Intent.FLAG_ACTIVITY_CLEAR_TASK)，以确保在用户注销后清除现有任务栈中的所有活动，这样用户无法返回到之前的界面。
//
//    最后，通过 startActivity 方法启动登录页面，将用户重定向到登录界面，实现了用户注销登录的功能。

    @OnClick(R.id.layout_change_pass)
    public void updatePass() {
        // 当用户点击具有 R.id.layout_change_pass 标识的视图时，将执行这个方法
        Intent intent = new Intent(getContext(), ChangePassActivity.class);
        // 创建一个 Intent，用于启动 ChangePassActivity（应该是一个修改密码的活动）
        startActivity(intent);
        // 启动由上述 Intent 指定的活动，通常是切换到修改密码页面
    }
//    这段代码是使用 ButterKnife 库的注解功能实现的。@OnClick(R.id.layout_change_pass) 表示当具有 R.id.layout_change_pass 标识的视图被点击时，将触发 updatePass 方法。
//
//    在 updatePass 方法中，首先创建了一个 Intent 对象，它被用来启动 ChangePassActivity（假定它是一个修改密码的活动）。然后，通过 startActivity(intent) 启动了这个指定的活动，从而使用户能够切换到修改密码页面。
//
//    这个代码片段是一个典型的事件处理程序，用于在用户与应用程序界面互动时执行相应的操作。

    private void showOrRefreshView() {
        // 定义一个名为 showOrRefreshView 的方法，用于显示或刷新视图。

        LocalAccountBean accountBean = NimUserHandler.getInstance().getLocalAccount();
        // 获取本地用户账户信息，通常包括头像、昵称、账号等。

        if (accountBean != null) {
            // 如果账户信息不为空（即存在有效的账户信息），则执行下面的操作。

            ImageUtils.setImageByUrl(getContext(), mHeadView, accountBean.getHeadImgUrl(), R.mipmap.app_logo);
            // 使用 ImageUtils 类中的 setImageByUrl 方法，通过 URL 加载用户头像，并将头像设置到 mHeadView 控件中。
            // getContext() 是获取当前上下文的方法，用于访问应用资源和设置视图。
            // accountBean.getHeadImgUrl() 获取用户头像的 URL。
            // R.mipmap.app_logo 是默认头像的资源 ID，在加载头像失败时使用。

            mTvName.setText(accountBean.getNick());
            // 设置 mTvName 控件的文本为用户的昵称。

            mTvAccount.setText(accountBean.getAccount());
            // 设置 mTvAccount 控件的文本为用户的账号。
        }
    }
//    这段代码是一个方法 showOrRefreshView，其目的是在界面上显示或刷新用户的信息，包括用户头像、昵称和账号。它首先获取本地用户账户信息，检查是否为null（即是否有有效账户信息）。如果存在有效账户信息，它会使用 ImageUtils 类中的 setImageByUrl 方法加载用户头像，然后设置昵称和账号文本到相应的视图控件中。这种方法通常在用户个人信息页面或者用户界面中调用，以确保用户信息的正确显示和更新。
}
