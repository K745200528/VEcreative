package com.ezreal.ezchat.widget;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 聊天记录列表
 * Created by wudeng on 2017/9/4.
 */

// 自定义消息显示的 RecyclerView 类
public class MsgRecyclerView extends RecyclerView {

    // 定义头部视图的类型为 100001
    public static final int VIEW_TYPE_HEADER = 100001;

    // 定义触发刷新的偏移比率
    private final static float OFFSET_RADIO = 1.8f;

    // 加载监听器接口，用于监听加载事件
    public OnLoadingListener mLoadingListener;

    // 消息视图适配器
    private MsgViewAdapter mMsgViewAdapter;

    // 是否正在加载中
    private boolean isLoading = false;

    // 上次触摸事件的 Y 坐标位置
    private float mLastY = -1;

    // 消息头部视图
    private MsgHeadView mHeadView;

    // 线性布局管理器
    private LinearLayoutManager mLayoutManager;

    // 数据观察者对象，用于观察数据变化
    private final RecyclerView.AdapterDataObserver mDataObserver = new DataObserver();
//    这段代码定义了一个自定义的 RecyclerView 类 MsgRecyclerView，用于显示消息。它包括以下功能：
//
//    定义了一个头部视图的类型为 VIEW_TYPE_HEADER，以便在适配器中使用。
//    定义了一个触发刷新的偏移比率 OFFSET_RADIO，用于确定什么时候触发刷新。
//    包含一个加载监听器接口 mLoadingListener，用于监听加载事件。
//    拥有一个消息视图适配器 mMsgViewAdapter，用于管理消息的显示。
//    包括一个标志位 isLoading，表示是否正在加载中。
//    记录了上次触摸事件的 Y 坐标位置 mLastY，用于判断用户滑动的方向。
//    包括一个消息头部视图 mHeadView，用于显示下拉刷新的头部视图。
//    使用线性布局管理器 mLayoutManager 来布局消息列表。
//    最后，定义了一个数据观察者对象 mDataObserver，用于观察数据变化并更新 UI。

    // 构造函数，初始化 MsgRecyclerView 的实例，使用默认的样式和属性。
    public MsgRecyclerView(Context context) {
        this(context, null);
    }

    // 构造函数，初始化 MsgRecyclerView 的实例，可设置属性。
    public MsgRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // 构造函数，初始化 MsgRecyclerView 的实例，设置样式和属性。
    public MsgRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        // 调用父类构造函数来完成基础的 RecyclerView 初始化工作。
        super(context, attrs, defStyle);

        // 调用 init 方法，执行其他自定义初始化工作。
        init();
    }
//    这段代码包括 MsgRecyclerView 的三个构造函数。每个构造函数用于初始化 MsgRecyclerView 的实例，但具有不同的参数集。它们是重载的构造函数，可以根据需要选择使用。所有这些构造函数最终都会调用 init() 方法，该方法用于执行自定义的初始化工作，例如设置 RecyclerView 的布局管理器、适配器和样式等。

    // 初始化方法
    private void init() {
        // 创建一个线性布局管理器
        mLayoutManager = new LinearLayoutManager(getContext());
        // 将线性布局管理器设置给当前视图
        setLayoutManager(mLayoutManager);

        // 创建一个消息头部视图
        mHeadView = new MsgHeadView(getContext());
    }

    // 隐藏头部视图的方法
    public void hideHeadView() {
        // 将头部视图的可见高度设置为0，即隐藏头部视图
        mHeadView.setVisibleHeight(0);
        // 将 isLoading 标志设为 false
        isLoading = false;
    }
//    这段代码是一个自定义控件或视图的初始化和隐藏头部视图的方法。在初始化方法中，首先创建一个线性布局管理器 mLayoutManager 并将其设置为当前视图的布局管理器。然后，创建一个名为 mHeadView 的 MsgHeadView 实例，它代表消息列表的头部视图。
//
//    在隐藏头部视图的方法中，将 mHeadView 的可见高度设置为0，以实现隐藏头部视图的效果。还将 isLoading 标志设置为 false，这可能用于跟踪是否正在加载更多数据。

    // 重写 setAdapter 方法
    @Override
    public void setAdapter(Adapter adapter) {
        // 创建 MsgViewAdapter 并将传入的 Adapter 包装其中
        mMsgViewAdapter = new MsgViewAdapter(adapter);

        // 调用父类的 setAdapter 方法，实际设置 MsgViewAdapter 为适配器
        super.setAdapter(mMsgViewAdapter);

        // 注册 Adapter 数据观察者以监听数据变化
        adapter.registerAdapterDataObserver(mDataObserver);

        // 手动调用 onChanged 方法，以确保数据得到正确初始化
        mDataObserver.onChanged();
    }

    // 重写 getAdapter 方法
    @Override
    public Adapter getAdapter() {
        // 返回 MsgViewAdapter 中包装的原始 Adapter
        return mMsgViewAdapter.getOriginalAdapter();
    }
//    这段代码主要是对 RecyclerView 的 Adapter 进行包装，以便更好地处理聊天消息的显示。setAdapter 方法创建一个 MsgViewAdapter 并将传入的 Adapter 包装其中，然后调用 super.setAdapter(mMsgViewAdapter) 来设置 MsgViewAdapter 为 RecyclerView 的适配器。同时，它还注册了一个数据观察者 mDataObserver 并手动触发了 onChanged 方法来确保数据得到正确初始化。 getAdapter 方法用于获取 MsgViewAdapter 包装的原始 Adapter。

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下时坐标
                mLastY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算手指滑动距离，并更新当前 Y 值
                final float deltaY = e.getRawY() - mLastY;
                mLastY = e.getRawY();
                // 若当前处于列表最上方，且headView 当前显示高度小于完整高度2倍，则更新 headView 的显示高度
                if (mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0
                        && (deltaY > 0) && !isLoading
                        && mHeadView.getVisibleHeight() <= mHeadView.getHeadHeight() * 2) {
                    // 如果在顶部并且是下滑操作，更新头部视图的高度
                    mHeadView.setVisibleHeight((int) (deltaY / OFFSET_RADIO + mHeadView.getVisibleHeight()));
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastY = -1;
                // 如果 headView 显示高度大于原始高度，则刷新消息列表
                if (mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    // 检查是否在列表顶部

                    if (!isLoading && mHeadView.getVisibleHeight() > mHeadView.getHeadHeight()) {
                        // 如果没有在加载并且头部视图的高度大于刷新的高度
                        if (mLoadingListener != null) {
                            // 如果有加载监听器，执行加载操作
                            mLoadingListener.loadPreMessage();
                            isLoading = true;
                        }
                        mHeadView.setVisibleHeight(mHeadView.getHeadHeight());
                    }else {
                        // 否则，隐藏headView
                        hideHeadView();
                    }
                }
                break;
        }
        // 调用父类的触摸事件处理
        return super.onTouchEvent(e);
    }
//    这段代码是一个onTouchEvent方法，用于处理用户在列表上的触摸事件。它跟踪用户的触摸操作，以实现下拉刷新功能。根据不同的触摸事件（按下、滑动、抬起），它执行不同的操作，如更新头部视图的高度、触发加载更多操作等。它允许用户通过下拉手势来刷新列表数据。

    // 设置加载监听器，用于在某些事件发生时执行操作
    public void setLoadingListener(OnLoadingListener loadingListener) {
        // 将传递进来的监听器对象赋给类内部的 mLoadingListener 变量
        mLoadingListener = loadingListener;
    }

    // 定义一个加载监听器接口
    public interface OnLoadingListener {
        // 声明一个方法，用于加载之前的消息
        void loadPreMessage();
    }
//    这段代码的目的是为一个类设置加载监听器，以便在需要加载之前的消息时执行相关操作。通过设置监听器，可以将加载之前消息的操作委托给实现了 OnLoadingListener 接口的类，并在适当的时候调用 loadPreMessage 方法。

    // 创建一个内部类 DataObserver，该类扩展了 RecyclerView.AdapterDataObserver 类
    private class DataObserver extends RecyclerView.AdapterDataObserver {
        // 当数据集发生改变时调用
        @Override
        public void onChanged() {
            // 检查消息视图适配器是否为空，如果不为空，则通知适配器数据已更改
            if (mMsgViewAdapter != null) {
                mMsgViewAdapter.notifyDataSetChanged();
            }
        }

        // 当有项目范围插入时调用
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            // 通知适配器有项目范围插入
            mMsgViewAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        // 当有项目范围更改时调用
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            // 通知适配器有项目范围更改
            mMsgViewAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        // 当有项目范围更改时调用，可以包括有效负载
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            // 通知适配器有项目范围更改，可以传递有效负载
            mMsgViewAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        // 当有项目范围移除时调用
        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            // 通知适配器有项目范围移除
            mMsgViewAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        // 当有项目范围移动时调用
        @Override
        public void onItemRangeMoved(int fromPosition, toPosition, int itemCount) {
            // 通知适配器有项目范围移动
            mMsgViewAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }
//    这段代码创建了一个内部类 DataObserver，用于监听 RecyclerView.AdapterDataObserver 中的数据集更改事件，并通过适配器通知 RecyclerView 进行更新。每个方法对应不同类型的数据更改事件，例如数据集整体改变、项目范围插入、项目范围更改等。在每个方法中，它会检查消息视图适配器是否为空，然后通知适配器进行相应的更新。这有助于确保 RecyclerView 中的数据与适配器保持同步。

    // 创建一个名为 MsgViewAdapter 的内部类，它继承自 RecyclerView.Adapter<ViewHolder>
    private class MsgViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        // 声明一个适配器对象
        private RecyclerView.Adapter mAdapter;

        // 构造函数，接受一个外部适配器作为参数
        public MsgViewAdapter(RecyclerView.Adapter adapter) {
            this.mAdapter = adapter;
        }

        // 获取原始适配器的方法
        public RecyclerView.Adapter getOriginalAdapter() {
            return this.mAdapter;
        }
//        这段代码定义了一个名为 MsgViewAdapter 的内部适配器类，该类继承自 RecyclerView.Adapter<RecyclerView.ViewHolder>。这个内部适配器接受一个外部适配器作为参数，并提供了一个方法 getOriginalAdapter() 用于获取原始的外部适配器。这通常用于包装或扩展外部适配器的功能。

        // 重写获取列表项类型的方法
        @Override
        public int getItemViewType(int position) {
            // 如果位置为0，表示为头部视图
            if (position == 0) {
                return VIEW_TYPE_HEADER;
            }
            // 否则，调用底层适配器的方法获取该位置的列表项类型
            return mAdapter.getItemViewType(position);
        }

        // 重写创建列表项视图的方法
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 如果视图类型是头部，返回包装了头部视图的SimpleViewHolder
            if (viewType == VIEW_TYPE_HEADER) {
                return new SimpleViewHolder(mHeadView);
            }
            // 否则，调用底层适配器的方法根据视图类型创建列表项视图
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        // 重写绑定列表项视图的方法
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // 如果位置为0，表示头部，不执行绑定操作
            if (position == 0) {
                return;
            }
            // 否则，调用底层适配器的方法根据位置绑定列表项视图
            mAdapter.onBindViewHolder(holder, position);
        }
//        这段代码是RecyclerView适配器中的关键方法，主要用于实现包含头部视图的适配器。getItemViewType用于确定特定位置的列表项类型，如果位置为0，表示头部视图；否则，它调用底层适配器的getItemViewType来获取列表项类型。onCreateViewHolder用于创建视图持有者，如果视图类型是头部，它返回包装了头部视图的SimpleViewHolder；否则，它调用底层适配器的onCreateViewHolder来创建列表项视图。onBindViewHolder用于绑定列表项视图，如果位置为0，表示头部，不执行绑定操作；否则，它调用底层适配器的onBindViewHolder来根据位置绑定列表项视图。

        @Override
        public int getItemCount() {
            // 返回项目总数，包括一个额外的项
            return mAdapter.getItemCount() + 1;
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            // 当视图从窗口分离时，通知子适配器
            mAdapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            // 当视图被回收时，通知子适配器
            mAdapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
            // 当视图无法被回收时，通知子适配器
            return mAdapter.onFailedToRecycleView(holder);
        }
//        这些方法是一个 RecyclerView.Adapter 的回调方法，通常由 RecyclerView 使用。这个类可能是一个包装类，它实际上是一个内部子适配器的代理。例如，getItemCount() 方法返回的项目总数比子适配器多1，这意味着它可能包括一个额外的项。其他方法则负责将事件传递给子适配器，以便执行适当的操作。

        // 重写方法，用于取消适配器数据观察者的注册
        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mAdapter.unregisterAdapterDataObserver(observer);
        }

        // 重写方法，用于注册适配器数据观察者
        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mAdapter.registerAdapterDataObserver(observer);
        }

        // 定义了一个名为SimpleViewHolder的内部类，继承自RecyclerView.ViewHolder
        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            // SimpleViewHolder的构造函数，接受一个View作为参数
            SimpleViewHolder(View itemView) {
                // 调用父类RecyclerView.ViewHolder的构造函数，将传入的View与ViewHolder关联
                super(itemView);
            }
        }
//        这段代码是一个RecyclerView的适配器类，它重写了unregisterAdapterDataObserver和registerAdapterDataObserver方法，允许注册和取消注册适配器数据观察者。此适配器还定义了一个内部类SimpleViewHolder，它用于为RecyclerView的每个项目创建一个简单的视图持有者。这个内部类扩展了RecyclerView.ViewHolder，接受一个View参数，并使用它来初始化父ViewHolder。
    }
}
