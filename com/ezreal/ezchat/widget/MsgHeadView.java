package com.ezreal.ezchat.widget;

import android.animation.ValueAnimator;
import android.content.Context;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ezreal.ezchat.R;


/**
 * Created by wudeng on 2017/9/4.
 */

// 创建自定义消息头视图(MsgHeadView)，它继承自LinearLayout
public class MsgHeadView extends LinearLayout {

    // 定义一个常量用于日志标签
    private static final String TAG = MsgHeadView.class.getSimpleName();

    // 布局中的容器视图，用于包装消息头
    private LinearLayout mContainer;

    // 头部视图的高度
    private int mHeadHeight;
//    这段代码定义了一个名为MsgHeadView的自定义视图类，它用于显示聊天消息的头部信息。在类中有一个LinearLayout（mContainer）用于包装头部信息，还有一个整数属性（mHeadHeight）用于表示头部的高度。这个自定义视图将用于聊天消息的展示，允许自定义头部信息的显示方式。

    // 无参数构造函数
    public MsgHeadView(Context context) {
        // 调用带参数的构造函数，传递 null 作为 AttributeSet
        this(context, null);
    }

    // 带 AttributeSet 参数的构造函数
    public MsgHeadView(Context context, @Nullable AttributeSet attrs) {
        // 调用带 AttributeSet 和 defStyleAttr 参数的构造函数，使用默认的 defStyleAttr 值
        this(context, attrs, 0);
    }

    // 带 AttributeSet 和 defStyleAttr 参数的构造函数
    public MsgHeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        // 调用父类的构造函数
        super(context, attrs, defStyleAttr);

        // 初始化视图
        init();
    }
//    这是一个自定义 Android View 的构造函数的重载。通常，一个自定义 View 会有多个构造函数以适应不同的参数配置。这里，构造函数分别接受不同数量和类型的参数。
//
//    MsgHeadView(Context context)：无参数构造函数，通常在代码中创建视图时使用，会调用带默认参数的构造函数。
//
//    MsgHeadView(Context context, @Nullable AttributeSet attrs)：带 AttributeSet 参数的构造函数，通常在 XML 布局中定义视图时使用。
//
//    MsgHeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)：带 AttributeSet 和 defStyleAttr 参数的构造函数，通常也在 XML 布局中定义视图时使用。这些参数允许您指定视图的属性和样式。
//
//    每个构造函数都会在初始化时调用 init() 方法来执行额外的初始化工作。这种构造函数重载允许视图在不同的上下文和属性设置下进行创建，以满足不同的使用场景。

    // 初始化方法
    private void init() {
        // 从布局文件msg_head_view中充气一个LinearLayout，并赋值给mContainer
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.msg_head_view, null);

        // 创建一个布局参数LayoutParams，设置宽度为MATCH_PARENT，高度为WRAP_CONTENT，且无边距
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);

        // 设置当前View的布局参数为lp
        this.setLayoutParams(lp);

        // 设置当前View的内边距为0
        this.setPadding(0, 0, 0, 0);

        // 向当前View中添加mContainer作为子View，并设置布局参数
        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));

        // 设置当前View的重心为底部
        setGravity(Gravity.BOTTOM);

        // 创建MeasureSpec用于测量View的高度，UNSPECIFIED表示未指定
        int height = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        // 创建MeasureSpec用于测量View的宽度，UNSPECIFIED表示未指定
        int width = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        // 测量mContainer的宽高
        mContainer.measure(width, height);

        // 获取测量后的mContainer高度，即mHeadHeight
        mHeadHeight = mContainer.getMeasuredHeight();
    }
//    这段代码用于初始化一个自定义的视图。它包括以下步骤：
//
//    从布局文件 msg_head_view 中充气一个 LinearLayout 并赋值给 mContainer。
//    创建布局参数 LayoutParams，将当前视图的宽度设置为 MATCH_PARENT，高度设置为 WRAP_CONTENT，并将边距设置为0。
//    设置当前视图的内边距为0。
//    向当前视图中添加 mContainer 作为子视图，并应用布局参数。
//    设置当前视图的重心为底部（Gravity.BOTTOM）。
//    创建 MeasureSpec 以用于测量视图的高度，UNSPECIFIED 表示未指定。
//    创建 MeasureSpec 以用于测量视图的宽度，同样也是 UNSPECIFIED。
//    测量 mContainer 的宽度和高度。
//    获取测量后的 mContainer 高度，将其赋值给 mHeadHeight。
//    这段代码主要用于初始化一个可滑动的头部视图。

    // 获取头像视图的高度
    public int getHeadHeight(){
        return mHeadHeight;
    }
//    这段代码是一个简单的getter方法，用于获取头像视图的高度。在调用该方法时，它会返回存储在 mHeadHeight 变量中的值。这个方法用于获取头像高度的信息，可能在布局中的其他地方用于确定布局或视图的尺寸。

    // 创建一个平滑滚动方法，用于调整视图高度以实现平滑的滚动效果
    private void smoothScrollTo(int destHeight) {
        // 创建值动画，从当前视图高度到目标高度进行过渡
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);

        // 设置动画持续时间为300毫秒并开始动画
        animator.setDuration(300).start();

        // 添加值动画更新监听器，用于在动画进行过程中更新视图高度
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 设置当前视图高度为动画的当前值
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });

        // 启动动画
        animator.start();
    }
//    这段代码定义了一个方法，用于平滑滚动视图的高度。它通过创建一个值动画来过渡当前视图高度和目标高度。动画持续时间为300毫秒，动画更新监听器用于在动画过程中更新视图的高度。此方法可用于实现平滑的滚动效果，例如在输入框获得焦点时自动滚动以确保输入框可见。

    // 设置控件可见高度的方法
    public void setVisibleHeight(int height) {
        // 确保高度不小于0
        if (height < 0) height = 0;

        // 获取控件的布局参数
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();

        // 设置布局参数的高度
        lp.height = height;

        // 应用新的布局参数
        mContainer.setLayoutParams(lp);
    }

    // 获取控件可见高度的方法
    public int getVisibleHeight() {
        // 获取控件的布局参数
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();

        // 返回布局参数中的高度值
        return lp.height;
    }
//    这两个方法用于设置和获取控件的可见高度。在 setVisibleHeight 方法中，首先确保传入的高度不小于0，然后获取控件的布局参数，将高度设置为指定值，最后将新的布局参数应用到控件上。getVisibleHeight 方法用于获取当前控件的可见高度，它获取控件的布局参数中的高度值并返回。这种方法通常用于动态更改控件的可见高度，例如在用户界面中实现下拉刷新功能时。

}
