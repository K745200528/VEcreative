package com.ezreal.ezchat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ezreal.ezchat.R;
import com.ezreal.ezchat.fragment.BaseFragment;
import com.ezreal.ezchat.fragment.ContractFragment;
import com.ezreal.ezchat.fragment.MeFragment;
import com.ezreal.ezchat.fragment.RecentMsgFragment;
import com.ezreal.ezchat.handler.NimFriendHandler;
import com.ezreal.ezchat.handler.NimOnlineStatusHandler;
import com.ezreal.ezchat.handler.NimSysMsgHandler;
import com.ezreal.ezchat.handler.NimUserHandler;
import com.ezreal.ezchat.widget.ChangeColorIconWithText;
import com.javonlee.dragpointview.view.DragPointView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.suntek.commonlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements OnClickListener, ViewPager.OnPageChangeListener {
    // 此类 MainActivity 继承自 BaseActivity，并实现了 OnClickListener 和 ViewPager.OnPageChangeListener 接口

    private static final String TAG = MainActivity.class.getSimpleName();
    // 创建一个用于日志标记的 TAG，通常用于调试

    @BindView(R.id.view_page)
    ViewPager mViewPager;
    // 通过注解绑定一个 ViewPager 视图

    @BindView(R.id.id_indicator_msg)
    ChangeColorIconWithText mIndicatorMsg;
    // 通过注解绑定一个 ChangeColorIconWithText 视图（用于消息页面的底部导航）

    @BindView(R.id.id_indicator_contact)
    ChangeColorIconWithText mIndicatorContract;
    // 通过注解绑定一个 ChangeColorIconWithText 视图（用于联系人页面的底部导航）

//    @BindView(R.id.id_indicator_found)
//    ChangeColorIconWithText mIndicatorFound;
    // 通过注解绑定一个 ChangeColorIconWithText 视图（用于发现页面的底部导航）

    @BindView(R.id.id_indicator_me)
    ChangeColorIconWithText mIndicatorMe;
    // 通过注解绑定一个 ChangeColorIconWithText 视图（用于个人信息页面的底部导航）

    @BindView(R.id.dpv_unread_recent_msg)
    DragPointView mDpvUnRead;
    // 通过注解绑定一个 DragPointView 视图（用于显示未读消息数的小红点）


    // 创建一个列表以容纳各个片段
    private List<BaseFragment> mFragments;

    // 创建一个列表以容纳底部标签栏的图标和文本
    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<>();

    // 创建一个用于显示最近消息的片段
    private RecentMsgFragment mMsgFragment;

    // 创建一个用于显示联系人的片段
    private ContractFragment mContractFragment;
//    private FoundFragment mFoundFragment;
    // 创建一个用于显示"我"页面的片段
    private MeFragment mMeFragment;


    @Override
    // 当活动被创建时调用
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置状态栏颜色为应用蓝色
        setStatusBarColor(R.color.app_blue_color);

        // 设置当前活动的视图为activity_main.xml布局
        setContentView(R.layout.activity_main);

        // 设置标题栏文本为应用名称，同时隐藏左侧和右侧的按钮
        setTitleBar(getString(R.string.app_name), false, false);

        // 使用ButterKnife库绑定视图元素和成员变量
        ButterKnife.bind(this);

        // 初始化视图
        initView();

        // 绑定碎片
        bindFragment();

        // 初始化处理程序
        initHandler();

    // 开启通知栏，有信息的时候通知通知
        NIMClient.toggleNotification(true);

        // 由通知栏点击进入后，用于跳转到指定的聊天界面
//        ArrayList<IMMessage> messages = (ArrayList<IMMessage>)
//                getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
//        if (messages != null && !messages.isEmpty()){
//            IMMessage message = messages.get(0);
//            NimUserInfo userInfo = NIMClient.getService(UserService.class)
//                    .getUserInfo(message.getSessionId());
//            Intent intent = new Intent(this,P2PChatActivity.class);
//            intent.putExtra("NimUserInfo",userInfo);
//            startActivity(intent);
//        }

    }

    private void initView() {
        // 初始化视图，将选项卡指示器添加到一个列表中
        mTabIndicators.add(mIndicatorMsg);
        mTabIndicators.add(mIndicatorContract);
//        mTabIndicators.add(mIndicatorFound);
        mTabIndicators.add(mIndicatorMe);
        // 为选项卡指示器添加点击事件监听器，以响应用户点击
        mIndicatorMsg.setOnClickListener(this);
        mIndicatorContract.setOnClickListener(this);
//        mIndicatorFound.setOnClickListener(this);
        mIndicatorMe.setOnClickListener(this);
        // 设置选项卡指示器 Msg 的图标透明度为 1.0f，表示该选项卡是当前选中的
        mIndicatorMsg.setIconAlpha(1.0f);
    }

    private void bindFragment() {
        // 创建一个用于存储碎片的列表
        mFragments = new ArrayList<>();

// 创建最近消息碎片实例
        mMsgFragment = new RecentMsgFragment();

// 将最近消息碎片添加到碎片列表
        mFragments.add(mMsgFragment);

// 创建联系人碎片实例
        mContractFragment = new ContractFragment();

// 将联系人碎片添加到碎片列表
        mFragments.add(mContractFragment);
// 创建发现碎片实例（在此处被注释掉，不被使用）
// mFoundFragment = new FoundFragment();
// mFragments.add(mFoundFragment);

        // 创建个人资料碎片实例
        mMeFragment = new MeFragment();

// 将个人资料碎片添加到碎片列表
        mFragments.add(mMeFragment);

        // 创建一个FragmentPagerAdapter，用于管理多个Fragment页面
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            // 获取Fragment的数量，通常与Fragment列表的大小相同
            @Override
            public int getCount() {
                return mFragments.size();
            }

            // 获取特定位置的Fragment，通常根据位置获取对应的Fragment实例
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }
        };

        // 设置ViewPager的适配器为adapter
        mViewPager.setAdapter(adapter);

// 为ViewPager添加页面变化监听器，即当前类实现了OnPageChangeListener接口
        mViewPager.addOnPageChangeListener(this);
    }

    private void initHandler() {
        // 初始化 NimOnlineStatusHandler，用于处理用户在线状态相关的功能
        NimOnlineStatusHandler.getInstance().init();

// 设置在线状态变化的监听器
        NimOnlineStatusHandler.getInstance().setStatusChangeListener(new NimOnlineStatusHandler.OnStatusChangeListener() {
            @Override
            public void requestReLogin(String message) {
                // 当需要重新登录时，弹出提示消息
                ToastUtils.showMessage(MainActivity.this, "自动登录失败或被踢出，请手动登录~");

                // 启动登录页面
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }

            @Override
            public void networkBroken() {
                // 处理网络断开的情况，这里可以添加自定义的处理逻辑
            }
        });

// 初始化 NimSysMsgHandler，用于处理系统消息相关的功能
        NimSysMsgHandler.getInstance().init();

// 初始化 NimFriendHandler，用于处理好友相关的功能
        NimFriendHandler.getInstance().init();

// 初始化 NimUserHandler，用于处理用户相关的功能
        NimUserHandler.getInstance().init();
//        这段代码主要用于初始化不同的处理器，这些处理器负责处理不同方面的功能，如用户在线状态、系统消息、好友操作等。对于在线状态处理，它设置了一个监听器，以便在需要重新登录或网络断开时进行相应的处理。其他处理器的初始化也在这里完成，以确保应用能够处理不同方面的功能。
    }

    @Override
    public void onClick(View v) {
        // 重置选项卡的状态
        resetOtherTabs();

        // 根据用户的点击选择设置相应的选项卡
        switch (v.getId()) {
            case R.id.id_indicator_msg:
                // 设置消息选项卡为当前选中状态，同时设置对应的页面为当前页面
                mIndicatorMsg.setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_contact:
                // 设置联系人选项卡为当前选中状态，同时设置对应的页面为当前页面
                mIndicatorContract.setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            // 找到以下两行代码已被注释，未被执行
            // case R.id.id_indicator_found:
            //     mIndicatorFound.setIconAlpha(1.0f);
            //     mViewPager.setCurrentItem(2, false);
            //     break;
            case R.id.id_indicator_me:
                // 设置"我"选项卡为当前选中状态，同时设置对应的页面为当前页面
                mIndicatorMe.setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
        }
    }
//    这段代码是一个点击事件处理方法，当用户点击不同的选项卡时，根据点击的选项卡设置对应的图标为选中状态，同时设置对应的页面为当前显示页面。在这个方法中，首先通过 resetOtherTabs() 方法重置其他未被点击的选项卡，然后通过 switch 语句根据用户点击的选项卡来设置选项卡的状态。已注释的两行代码表示该选项卡在注释状态，未被执行。

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 当按下某个按键时，这个方法会被触发，通常用于监听物理按键事件

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 如果按下的是返回键

            // 创建一个用于返回到桌面的意图
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);

            // 启动这个意图，将应用返回到桌面
            startActivity(home);

            // 返回 true 表示已经处理了按键事件
            return true;
        }

        // 如果按下的不是返回键，调用父类的方法处理按键事件
        return super.onKeyDown(keyCode, event);
    }
//    这段代码的目的是监听返回键的按下事件。当用户按下返回键时，将应用退回到设备的桌面。如果按下的是返回键，将创建一个特定的意图，并将其设置为返回桌面。然后，启动这个意图以退出应用，并返回 true 以表示已处理按键事件。如果按下的不是返回键，则会调用父类方法以正常处理按键事件。

    // 重置其他标签页
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            // 获取当前标签页控件，并将其图标透明度设置为0
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }
//    这段代码用于在标签页切换时将其他标签页的图标透明度重置为0。它遍历标签页列表 mTabIndicators，并通过 setIconAlpha(0) 方法将每个标签页的图标透明度设置为0，从而实现重置其他标签页的效果。这通常用于高亮显示当前选定的标签页，同时将其他标签页的图标恢复为正常状态。

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            // 检查页面是否正在滑动并且滑动位置大于0

            // 获取当前页和下一页的标签视图
            ChangeColorIconWithText left = mTabIndicators.get(position);
            ChangeColorIconWithText right = mTabIndicators.get(position + 1);

            // 根据滑动位置设置左边和右边标签的图标透明度
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }
//    这段代码是在页面滑动时调用的回调方法。如果页面正在滑动并且滑动位置大于0，它会根据滑动位置（positionOffset）来设置左边和右边标签的图标透明度，以实现页面滑动时图标颜色渐变的效果。

    @Override
    public void onPageSelected(int position) {
        // 当 ViewPager 选中页发生变化时触发的事件
        // 切换到最近联系人列表界面
        if (position == 0){
            // 能看到新消息提醒，不需要通知栏通知
            // 通过 NIMClient 获取消息服务对象，设置聊天账户和消息通知的方式
            NIMClient.getService(MsgService.class)
                    .setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
            // 这里将消息通知设置为仅在当前聊天界面时显示，不显示通知栏通知
        } else {
            // 如果不是最近联系人列表界面，即在其他聊天界面时
            // 不能看到消息提醒，需要通知栏通知

            // 通过 NIMClient 获取消息服务对象，设置聊天账户和消息通知的方式
            NIMClient.getService(MsgService.class)
                    .setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
            // 这里将消息通知设置为通过通知栏通知，不显示在聊天界面
        }
    }
//    这段代码在 ViewPager 中切换到不同的界面时，通过 NIM SDK 中的消息服务控制消息通知的显示方式。在最近联系人列表界面（position为0）时，用户可以看到新消息提醒，所以消息通知不需要通过通知栏显示；而在其他聊天界面时，用户不会看到消息提醒，因此消息通知需要通过通知栏通知的方式来显示。这可以提供更好的用户体验，根据用户当前所在的界面决定是否通过通知栏提醒。

    @Override
    public void onPageScrollStateChanged(int state) {
        // 页面滚动状态变化的回调函数

        // 参数 state 表示当前滚动状态，有三个可能的状态：
        // 1. ViewPager.SCROLL_STATE_IDLE：表示ViewPager处于空闲状态，没有滚动或拖动
        // 2. ViewPager.SCROLL_STATE_DRAGGING：表示ViewPager正在被用户拖动
        // 3. ViewPager.SCROLL_STATE_SETTLING：表示ViewPager正在自动滚动到最终位置

        // 此方法通常用于在页面滚动状态变化时执行一些操作，可以根据不同的滚动状态来处理不同的逻辑。
        // 例如，在用户开始拖动ViewPager时，可以执行某些操作，或者在自动滚动结束时执行其他操作。
        // 这是一个空方法，需要根据具体需求重写。
    }
//    这段代码是 ViewPager.OnPageChangeListener 接口的 onPageScrollStateChanged 方法的实现。该方法在页面滚动状态发生变化时被调用，通常用于处理在不同滚动状态下的逻辑。在这个示例中，方法为空，需要根据具体需求来重写。在滚动状态变化时，可以执行一些与状态相关的操作，例如在用户开始拖动ViewPager时执行某些操作或在自动滚动结束时执行其他操作。

}
