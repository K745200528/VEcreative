package com.ezreal.ezchat.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 横向 ListView
 * Created by wudeng on 2017/11/10.
 */

public class HorizonListView extends AdapterView<ListAdapter> {
    // 水平列表视图的基类，用于显示横向的数据。

    protected ListAdapter mAdapter;
    // 用于提供数据的适配器。

    private int mLeftViewIndex = -1;
    private int mRightViewIndex = 0;
    // 左边和右边的视图索引，用于控制视图的布局。

    protected int mCurrentX;
    protected int mNextX;
    // 当前的X坐标和下一个X坐标，用于跟踪视图的滚动。

    private int mMaxX = Integer.MAX_VALUE;
    // 最大的X坐标，用于限制滚动范围。

    private int mDisplayOffset = 0;
    // 显示的偏移量，用于控制视图的显示位置。

    protected Scroller mScroller;
    // 用于处理滚动的Scroller。

    private GestureDetector mGesture;
    // 用于处理手势事件的GestureDetector。

    private Queue<View> mRemovedViewQueue = new LinkedList<View>();
    // 存储被移除的视图的队列，以便重用。

    private OnItemSelectedListener mOnItemSelected;
    private OnItemClickListener mOnItemClicked;
    private OnItemLongClickListener mOnItemLongClicked;
    // 用于监听视图项选中、点击和长点击事件的回调接口。

    private boolean mDataChanged = false;
    // 标记数据是否发生了变化。
//    这段代码定义了一个名为HorizonListView的类，它是一个自定义的水平列表视图。它包含了用于显示横向数据的相关属性和方法，以及用于处理事件和回调的接口。这个类可以用于创建水平滚动的视图，其中包含可横向滚动的数据项。

    // 构造函数，用于创建 HorizonListView 的实例
    public HorizonListView(Context context) {
        super(context);
    }

    // 构造函数，用于创建 HorizonListView 的实例，同时传递属性集合
    public HorizonListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 构造函数，用于创建 HorizonListView 的实例，同时传递属性集合和样式
    public HorizonListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
//    这些构造函数是用来创建 HorizonListView 的实例的。HorizonListView 是 Android 中的一个自定义视图（View），这些构造函数提供了不同的方式来初始化这个自定义视图。第一个构造函数用于在代码中直接创建 HorizonListView 的实例，第二个构造函数用于在 XML 布局文件中声明 HorizonListView 时，同时传递自定义属性集合。最后一个构造函数还允许传递样式（style）信息，以定制视图的外观和行为。

    private synchronized void initView() {
        // 初始化视图，主要用于设置初始状态和参数

// 左侧视图索引初始化为-1
        mLeftViewIndex = -1;

// 右侧视图索引初始化为0
        mRightViewIndex = 0;

// 显示偏移初始化为0
        mDisplayOffset = 0;

// 当前X坐标初始化为0
        mCurrentX = 0;

// 下一个X坐标初始化为0
        mNextX = 0;

// 最大X坐标初始化为Integer类型的最大值
        mMaxX = Integer.MAX_VALUE;

// 创建滚动器Scroller实例，用于处理滑动效果
        mScroller = new Scroller(getContext());

// 创建手势检测器GestureDetector实例，用于处理触摸手势
        mGesture = new GestureDetector(getContext(), mOnGesture);
    }
//    这段代码的作用是初始化自定义视图的各种参数和工具，包括视图的索引、偏移量、当前和下一个X坐标，最大X坐标，滚动器和手势检测器。这些参数和工具用于处理视图的滑动效果和触摸手势。

    @Override
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        // 设置下拉列表项被选中时的监听器
        mOnItemSelected = listener;
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        // 设置下拉列表项被单击时的监听器
        mOnItemClicked = listener;
    }

    @Override
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        // 设置下拉列表项被长按时的监听器
        mOnItemLongClicked = listener;
    }
//    这段代码是用于设置下拉列表控件（Spinner）的事件监听器。通过这些监听器，你可以在用户与下拉列表交互时执行特定的操作。这些方法允许你分别设置下拉列表项被选中、单击和长按时的监听器。

    // 创建一个 DataSetObserver 对象用于监听数据集的变化
    private DataSetObserver mDataObserver = new DataSetObserver() {

        // 当数据集发生变化时触发的方法
        @Override
        public void onChanged() {
            synchronized (HorizonListView.this) {
                mDataChanged = true;
            }
            // 通知视图需要重绘
            invalidate();
            // 请求重新布局视图
            requestLayout();
        }

        // 当数据集无效时触发的方法
        @Override
        public void onInvalidated() {
            // 重置数据集
            reset();
            // 通知视图需要重绘
            invalidate();
            // 请求重新布局视图
            requestLayout();
        }
    };
//    这段代码定义了一个 DataSetObserver 对象，用于监听数据集的变化。onChanged 方法在数据集发生变化时被调用，通知视图需要重绘和重新布局。onInvalidated 方法在数据集无效时被调用，也会通知视图需要重绘和重新布局。这通常用于在数据集发生变化时更新视图。

    @Override
    public ListAdapter getAdapter() {
        // 返回当前用于此 Spinner 的适配器
        return mAdapter;
    }

    @Override
    public View getSelectedView() {
        // 返回当前选择的视图，通常用于显示当前选择项
        return null;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            // 如果之前已经有一个适配器，取消其数据集观察器
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }

        // 设置新的适配器
        mAdapter = adapter;

        // 注册新适配器的数据集观察器
        mAdapter.registerDataSetObserver(mDataObserver);

        // 重置 Spinner，通常用于刷新视图
        reset();
    }
//    这些方法与 Android Spinner 控件相关。getAdapter 用于获取当前的适配器，getSelectedView 通常用于获取当前选择的视图（这里没有实现返回值），setAdapter 用于设置一个新的适配器，并在切换适配器时进行必要的数据集观察器的注册和注销。 Spinner 是一个常用的 UI 控件，用于显示下拉列表供用户选择。

    // 重置布局
    private synchronized void reset() {
        // 初始化视图
        initView();

        // 移除布局中的所有子视图
        removeAllViewsInLayout();

        // 请求重新布局
        requestLayout();
    }

    // 设置选中项的方法
    @Override
    public void setSelection(int position) {
        // 这个方法是空的，没有具体的实现
    }

    // 添加子视图并测量尺寸
    private void addAndMeasureChild(final View child, int viewPos) {
        // 获取子视图的布局参数，如果没有则创建默认的布局参数
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        // 在布局中添加子视图
        addViewInLayout(child, viewPos, params, true);

        // 测量子视图的尺寸
        child.measure(
                MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST)
        );
    }
//    这段代码定义了一个自定义视图的重置方法 reset() 以及添加和测量子视图的方法 addAndMeasureChild(). reset() 用于在重新加载视图时初始化视图、清除已有的子视图并请求重新布局。setSelection(int position) 方法用于设置选中项，但是在这里是一个空方法，没有具体的实现。addAndMeasureChild() 方法用于将子视图添加到布局中并测量子视图的尺寸。这对于自定义视图的管理和渲染非常重要。


    @Override
    protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 如果适配器为空，返回
        if (mAdapter == null) {
            return;
        }

        // 如果数据已更改
        if (mDataChanged) {
            int oldCurrentX = mCurrentX;

            // 初始化视图，清空已有视图
            initView();
            removeAllViewsInLayout();
            mNextX = oldCurrentX;
            mDataChanged = false;
        }

        // 如果滚动器正在计算滚动位置
        if (mScroller.computeScrollOffset()) {
            mNextX = mScroller.getCurrX();
        }

        // 处理边界情况，确保不滚动超出内容的左右边界
        if (mNextX <= 0) {
            mNextX = 0;
            mScroller.forceFinished(true);
        }
        if (mNextX >= mMaxX) {
            mNextX = mMaxX;
            mScroller.forceFinished(true);
        }

        // 计算当前滚动位置与下一个滚动位置的差
        int dx = mCurrentX - mNextX;

        // 移除不可见的项
        removeNonVisibleItems(dx);

        // 填充列表
        fillList(dx);

        // 调整列表项的位置
        positionItems(dx);

        // 更新当前滚动位置
        mCurrentX = mNextX;

        // 如果滚动还未结束，请求重新布局
        if (!mScroller.isFinished()) {
            post(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                }
            });
        }
    }
//    这段代码是自定义的滚动视图的 onLayout 方法。它处理滚动列表的布局和视图重用。在滚动时，它检查当前布局是否已更改、计算滚动位置、处理边界情况，调整视图位置以及在滚动未结束时请求重新布局。这是一个自定义视图的关键部分，用于实现水平滚动的列表。

    // 填充列表的方法，该方法用于将可见区域的数据项填充到RecyclerView
    private void fillList(final int dx) {
        int edge = 0;
        // 获取列表中最后一个可见子视图
        View child = getChildAt(getChildCount() - 1);
        if (child != null) {
            // 获取该子视图的右边缘坐标
            edge = child.getRight();
        }
        // 向右填充列表，根据 dx 参数的正负确定是向左还是向右填充
        fillListRight(edge, dx);

        edge = 0;
        // 获取列表中第一个可见子视图
        child = getChildAt(0);
        if (child != null) {
            // 获取该子视图的左边缘坐标
            edge = child.getLeft();
        }
        // 向左填充列表，根据 dx 参数的正负确定是向左还是向右填充
        fillListLeft(edge, dx);
    }
//    这段代码的主要目的是在RecyclerView中填充数据项以显示在屏幕上。它根据滚动方向（由 dx 参数确定）来填充列表。首先，它获取最后一个可见子视图的右边缘坐标，然后调用 fillListRight 方法向右填充数据项。接下来，它获取第一个可见子视图的左边缘坐标，然后调用 fillListLeft 方法向左填充数据项。这样可以确保在滚动时动态填充和回收视图，以实现平滑的滚动效果。

    // 填充显示在右边的视图项，根据右边界和水平滚动距离

    private void fillListRight(int rightEdge, final int dx) {
        // 当滚动右边界 + 水平滚动距离 小于视图的宽度并且右侧视图索引小于适配器的项数时执行
        while (rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {
            // 获取适配器中的视图项
            View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);

            // 向视图中添加并测量子视图
            addAndMeasureChild(child, -1);

            // 更新右边界
            rightEdge += child.getMeasuredWidth();

            // 如果右侧视图索引达到适配器项数减一，更新最大水平滚动距离 mMaxX
            if (mRightViewIndex == mAdapter.getCount() - 1) {
                mMaxX = mCurrentX + rightEdge - getWidth();
            }

            // 如果 mMaxX 小于零，则将其设为零
            if (mMaxX < 0) {
                mMaxX = 0;
            }

            // 增加右侧视图索引
            mRightViewIndex++;
        }
    }
//    这段代码的目的是填充显示在可滚动视图右侧的子视图。它根据给定的右边界和水平滚动距离（dx）来决定应该添加哪些子视图。如果当前滚动位置没有显示所有的项，它会动态添加子视图，以确保用户能够向右滚动查看更多内容。

    // 用于填充左侧的子视图，从右向左滚动时使用
    private void fillListLeft(int leftEdge, final int dx) {
        while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
            // 获取要显示的子视图，如果有可重用的视图，则从队列中获取，否则从适配器获取
            View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
            // 将子视图添加到列表并测量它
            addAndMeasureChild(child, 0);
            // 调整 leftEdge 和 mDisplayOffset 以适应新添加的子视图
            leftEdge -= child.getMeasuredWidth();
            mLeftViewIndex--;
            mDisplayOffset -= child.getMeasuredWidth();
        }
    }
//    这段代码用于在水平滚动视图中填充左侧的子视图。具体的操作包括：
//
//    leftEdge 表示左边缘的位置，dx 表示滚动的偏移量。
//    进入 while 循环，它的目的是循环遍历左侧的子视图，直到占据的空间超出左边缘或者已经到达列表的最左边。
//    通过适配器 mAdapter 获取子视图。如果有可重用的子视图，从 mRemovedViewQueue 中取出，否则从适配器中获取。
//    调用 addAndMeasureChild 方法将子视图添加到水平滚动视图中并对其进行测量。
//    更新 leftEdge，减去当前子视图的宽度，以适应下一个子视图。
//    减小 mLeftViewIndex，表示当前左侧子视图的索引。
//    调整 mDisplayOffset，以考虑添加的子视图的宽度。
//    这段代码的作用是确保在滚动时，始终有足够的子视图填充到左侧以支持平滑的滚动效果。

    private void removeNonVisibleItems(final int dx) {
        // 该方法用于移除不可见的视图项，根据传入的偏移量（dx）来判断视图项是否可见。

// 获取第一个子视图
        View child = getChildAt(0);

// 当前子视图不为空并且位于视图左侧且在可见区域之外时执行以下操作
        while (child != null && child.getRight() + dx <= 0) {
            // 增加显示偏移量，用于跟踪已移除的视图项的总宽度
            mDisplayOffset += child.getMeasuredWidth();

            // 将子视图添加到已移除视图队列中
            mRemovedViewQueue.offer(child);

            // 从布局中移除子视图
            removeViewInLayout(child);

            // 更新左侧视图项的索引
            mLeftViewIndex++;

            // 获取下一个子视图，继续判断是否需要移除
            child = getChildAt(0);
        }

// 获取最后一个子视图
        child = getChildAt(getChildCount() - 1);

// 当前子视图不为空并且位于视图右侧且在可见区域之外时执行以下操作
        while (child != null && child.getLeft() + dx >= getWidth()) {
            // 将子视图添加到已移除视图队列中
            mRemovedViewQueue.offer(child);

            // 从布局中移除子视图
            removeViewInLayout(child);

            // 更新右侧视图项的索引
            mRightViewIndex--;

            // 获取下一个子视图，继续判断是否需要移除
            child = getChildAt(getChildCount() - 1);
        }
    }
//    这段代码的作用是根据传入的偏移量（dx），移除不可见的视图项。在水平滚动的视图中，当视图项的右侧边界在可见区域之外时，将其移除。这是一个回收机制，有助于优化视图的性能，以保持可滚动视图中仅包含可见的项目。

    // 调整子视图项的位置以响应水平滚动
    private void positionItems(final int dx) {
        if (getChildCount() > 0) {
            // 增加滚动偏移量
            mDisplayOffset += dx;

            // 左边界为偏移量
            int left = mDisplayOffset;

            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                int childWidth = child.getMeasuredWidth();

                // 设置子视图的位置，确定左、上、右、底边界
                child.layout(left, 0, left + childWidth, child.getMeasuredHeight());

                // 更新左边界以放置下一个子视图
                left += childWidth + child.getPaddingRight();
            }
        }
    }
//    这段代码的目的是在水平滚动时调整子视图项的位置，以响应用户的滚动操作。它增加了一个滚动偏移量 dx，然后遍历子视图，为每个子视图计算新的位置并使用 layout 方法设置其位置。这实现了水平滚动效果，确保子视图在滚动过程中正确显示。

    // 同步方法，用于滚动视图到指定的位置
    public synchronized void scrollTo(int x) {
        // 使用 Scroller 对象开始滚动到指定的 x 坐标
        mScroller.startScroll(mNextX, 0, x - mNextX, 0);
        // 请求重新布局视图
        requestLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 调用父类的事件分发方法，获取是否已处理
        boolean handled = super.dispatchTouchEvent(ev);
        // 将手势识别器处理触摸事件，并更新 handled
        handled |= mGesture.onTouchEvent(ev);
        // 返回处理情况
        return handled;
    }
//    这段代码是一个自定义视图或控件的方法和重写的方法。主要功能包括：
//
//    scrollTo(int x): 这是一个同步方法，用于滚动视图到指定的 x 坐标。它使用 Scroller 对象实现平滑的滚动效果，然后请求重新布局视图。
//
//    dispatchTouchEvent(MotionEvent ev): 这是一个重写的方法，用于处理触摸事件的分发。首先，它调用了父类的 dispatchTouchEvent 方法来获取是否已处理事件。然后，它通过手势识别器（mGesture）处理触摸事件，并更新 handled 变量以表示事件是否已处理。最后，它返回 handled 以指示事件处理情况。
//
//    这些方法通常用于创建自定义视图，以便实现特定的滚动和触摸行为。

    protected boolean onFling(MotionEvent e1, MotionEvent e2,
                              float velocityX, float velocityY) {
        // 在HorizonListView内部同步执行以下代码块
        synchronized (HorizonListView.this) {
            // 使用Scroller实现滑动效果，fling方法会根据指定的速度和边界值计算滚动位置
            // mNextX 是视图的当前滚动位置，0表示在水平方向上没有垂直滚动
            // -velocityX 表示水平方向上的速度，正数表示向左滚动，负数表示向右滚动
            // mMaxX 是水平方向上的最大滚动边界
            mScroller.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
        }
        // 通知布局请求重新布置子视图
        requestLayout();

        // 返回true表示已经处理了此事件
        return true;
    }

    protected boolean onDown(MotionEvent e) {
        // 强制结束Scroller动画，这样可以立即停止任何正在进行的滚动
        mScroller.forceFinished(true);

        // 返回true表示已经处理了此事件
        return true;
    }
//    这段代码包含两个触摸手势处理方法，onFling和onDown。onFling方法用于处理快速滑动手势，通常用于实现滚动效果。它使用Scroller类来模拟滚动动画，并根据用户的滑动速度进行计算。onDown方法用于处理按下手势，它在用户按下屏幕时立即结束任何正在进行的滚动。
//
//    在onFling方法中，首先通过synchronized块确保在HorizonListView内部同步执行代码。然后，使用Scroller的fling方法设置滚动动画，包括当前滚动位置mNextX、水平方向速度-velocityX以及水平滚动的边界mMaxX。最后，通过调用requestLayout()通知布局重新布置子视图，从而触发滚动效果。
//
//    在onDown方法中，通过调用mScroller的forceFinished(true)方法立即结束Scroller的动画，以停止任何正在进行的滚动。返回true表示已处理此事件。

    private GestureDetector.OnGestureListener mOnGesture =
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return HorizonListView.this.onDown(e);
                }

                // 当用户触摸按下事件发生时调用
                // 参数 MotionEvent e 代表触摸事件

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    return HorizonListView.this.onFling(e1, e2, velocityX, velocityY);
                }

                // 当用户进行快速滑动操作（Fling）时调用
                // 参数 MotionEvent e1 和 e2 代表滑动的起始和结束事件
                // 参数 float velocityX 和 velocityY 代表在X和Y方向上的滑动速度

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                        float distanceX, float distanceY) {
                    synchronized (HorizonListView.this) {
                        mNextX += (int) distanceX;
                    }
                    // 在水平列表视图上滚动时更新下一个X坐标位置
                    requestLayout();
                    // 请求重新布局视图
                    return true;
                }

                // 当用户进行滚动操作时调用
                // 参数 MotionEvent e1 和 e2 代表滚动的起始和结束事件
                // 参数 float distanceX 和 distanceY 代表滚动的距离

//                这段代码定义了一个 OnGestureListener 接口的实现，用于处理与手势相关的操作。它包括以下几个方法：
//                onDown(MotionEvent e)：当用户触摸按下事件发生时调用，返回一个布尔值。
//                onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)：当用户进行快速滑动操作（Fling）时调用，返回一个布尔值。
//                onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)：当用户进行滚动操作时调用，返回一个布尔值。
//                这些方法用于处理用户在水平列表视图上的手势操作，包括点击、滑动、快速滑动等，以便在视图中进行相应的处理和更新。

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    // 遍历水平滚动列表的子视图
                    for (int i = 0; i < getChildCount(); i++) {
                        View child = getChildAt(i);

                        // 检查触摸事件是否发生在子视图内
                        if (isEventWithinView(e, child)) {
                            // 如果有定义点击事件监听器
                            if (mOnItemClicked != null) {
                                // 调用点击事件监听器的onItemClick方法，传递点击的子视图、索引和关联的项目ID
                                mOnItemClicked.onItemClick(HorizonListView.this,
                                        child, mLeftViewIndex + 1 + i,
                                        mAdapter.getItemId(mLeftViewIndex + 1 + i));
                            }

                            // 如果有定义项目选择事件监听器
                            if (mOnItemSelected != null) {
                                // 调用项目选择事件监听器的onItemSelected方法，传递点击的子视图、索引和关联的项目ID
                                mOnItemSelected.onItemSelected(HorizonListView.this,
                                        child, mLeftViewIndex + 1 + i,
                                        mAdapter.getItemId(mLeftViewIndex + 1 + i));
                            }
                            // 终止循环
                            break;
                        }
                    }
                    // 返回true表示触摸事件已被处理
                    return true;
                }
//                这段代码是在 HorizonListView 的 onSingleTapConfirmed 方法中，用于处理单击事件。首先，它遍历 HorizonListView 的子视图以检查触摸事件是否发生在哪个子视图内。如果找到了匹配的子视图，然后检查是否设置了点击事件监听器 (mOnItemClicked) 和项目选择事件监听器 (mOnItemSelected)。如果监听器存在，将分别触发相应的事件，并传递相关信息，如点击的子视图、索引和关联的项目ID。最后，返回 true 表示触摸事件已被处理。

                @Override
                public void onLongPress(MotionEvent e) {
                    // 获取子视图的数量
                    int childCount = getChildCount();

                    // 遍历子视图
                    for (int i = 0; i < childCount; i++) {
                        View child = getChildAt(i);

                        // 检查触摸事件是否在当前子视图内
                        if (isEventWithinView(e, child)) {

                            // 如果长按事件发生在子视图内，且已设置了长按事件监听器
                            if (mOnItemLongClicked != null) {

                                // 通知监听器发生了长按事件，提供了水平滚动视图、子视图、子视图在适配器中的位置和子视图的ID
                                mOnItemLongClicked.onItemLongClick(HorizonListView.this, child,
                                        mLeftViewIndex + 1 + i,
                                        mAdapter.getItemId(mLeftViewIndex + 1 + i));
                            }
                            break;
                        }
                    }
                }
//                这段代码处理HorizonListView（水平滚动视图）的长按事件。首先，它获取HorizonListView中子视图的数量，然后遍历每个子视图。对于每个子视图，它检查触摸事件是否发生在子视图内，这是通过isEventWithinView(e, child)方法来判断的。如果触摸事件确实发生在子视图内，它将通知已注册的mOnItemLongClicked（长按事件监听器）。
//
//                mOnItemLongClicked 被通知时，会提供水平滚动视图（HorizonListView）、当前子视图、当前子视图在适配器中的位置和子视图的ID，允许你在长按事件时执行自定义操作。最后，通过break语句退出循环，因为长按事件只能在一个子视图上触发一次。

                // 检查事件是否在给定的视图内
                private boolean isEventWithinView(MotionEvent e, View child) {
                    // 创建一个矩形对象用于表示视图的位置和大小
                    Rect viewRect = new Rect();

                    // 获取子视图在屏幕上的位置坐标
                    int[] childPosition = new int[2];
                    child.getLocationOnScreen(childPosition);

                    // 计算视图的左、右、上和下边界
                    // 获取子视图的左边界位置
                    int left = childPosition[0];

// 计算子视图的右边界位置，通过将左边界位置与子视图的宽度相加得到
                    int right = left + child.getWidth();

// 获取子视图的顶部位置
                    int top = childPosition[1];

// 计算子视图的底部位置，通过将顶部位置与子视图的高度相加得到
                    int bottom = top + child.getHeight();


                    // 设置矩形对象的边界
                    viewRect.set(left, top, right, bottom);

                    // 检查事件的原始坐标是否在矩形内部
                    return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
                }
//                这段代码用于检查给定的 MotionEvent 事件是否发生在指定的 View 内。它首先获取了视图在屏幕上的位置坐标，然后计算视图的左、右、上和下边界。最后，它使用 contains 方法检查事件的原始坐标是否在计算的矩形内，如果是，则返回 true，表示事件在视图内。这个方法通常用于处理触摸事件，以确定用户是否点击了特定的视图。
            };

}
