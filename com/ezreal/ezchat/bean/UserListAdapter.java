package com.ezreal.ezchat.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ezreal.ezchat.R;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

/**
 * Created by wudeng on 2017/11/10.
 */

// 用户列表适配器
public class UserListAdapter extends BaseAdapter {

    private Context mContext;  // 上下文
    private List<NimUserInfo> mUserList;  // 用户信息列表
    private LayoutInflater mInflater;  // 布局解析器
    private OnItemClickListener mOnItemClickListener;  // 点击监听器

    public UserListAdapter(Context context, List<NimUserInfo> users) {
        mContext = context;  // 初始化上下文
        mUserList = users;  // 初始化用户信息列表
        mInflater = LayoutInflater.from(context);  // 初始化布局解析器
    }

    @Override
    public int getCount() {
        return mUserList != null ? mUserList.size() : 0;  // 获取列表项数量
    }

    @Override
    public NimUserInfo getItem(int position) {
        return mUserList.get(position);  // 获取指定位置的用户信息
    }

    @Override
    public long getItemId(int position) {
        return position;  // 获取指定位置的ID
    }
//这段代码定义了一个用户列表适配器，用于显示用户信息的列表。它包括了适配器的构造函数，获取列表项数量、获取指定位置的用户信息以及获取指定位置的ID的方法。适配器用于将用户数据绑定到列表视图中

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // 如果 convertView 为空，表示当前列表项尚未被创建，需要初始化 ViewHolder
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_user, parent, false);
            holder.imageView = convertView.findViewById(R.id.image_view);
            convertView.setTag(holder);
        } else {
            // 如果 convertView 不为空，表示当前列表项已经被创建，可以直接获取 ViewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        // 使用 Glide 库加载用户头像图片到 ImageView 控件
        Glide.with(mContext)
                .load(mUserList.get(position).getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.mipmap.bg_img_defalut)
                .into(holder.imageView);

        // 为列表项设置点击监听器，以便处理点击事件
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 当列表项被点击时，检查是否设置了点击监听器（mOnItemClickListener）
                if (mOnItemClickListener != null) {
                    // 如果监听器存在，调用 itemClick 方法，通知点击事件发生，并传递点击位置（position）
                    mOnItemClickListener.itemClick(position);
                }
            }
        });

        return convertView;
    }
//    这段代码是一个自定义的 getView 方法，通常用于 Android 中的 Adapter 类中。它用于在列表视图中显示用户信息，例如用户头像。下面是代码的主要用途和方法解释：
//
//    getView 方法：这是 Android 中 Adapter 类的一个重要方法，用于渲染和显示列表中的每个项目。
//
//    ViewHolder 类：这是一个自定义的内部类，用于保存列表项中的视图控件的引用，以便在需要时重用。这有助于提高列表滚动性能。
//
//    检查 convertView 是否为 null：如果 convertView 为空，表示当前列表项尚未被创建，因此需要初始化 ViewHolder 并创建列表项。
//
//    使用 Glide 加载用户头像：这里使用 Glide 图片加载库将用户的头像图片加载到 ImageView 控件中。如果加载失败，它会显示一个默认图片。
//
//    设置列表项点击监听器：当用户点击列表项时，会触发一个点击事件。如果设置了监听器 mOnItemClickListener，则调用其 itemClick 方法通知点击事件发生，同时传递点击位置（position）。
//
//    返回 convertView：最后，返回更新后的 convertView 作为当前列表项的视图。这样，系统就可以将其添加到列表视图中。
//
//    这段代码通常在 BaseAdapter 或 ArrayAdapter 的子类中使用，用于定义如何在列表视图中显示数据。

    // 创建一个私有内部类 ViewHolder
    private class ViewHolder {
        ImageView imageView;
    }
//    这段代码定义了一个名为 ViewHolder 的私有内部类。ViewHolder 类通常用于在 Android 中作为缓存，以提高 ListView 或 RecyclerView 等列表视图的性能。在这里，ViewHolder 包含一个 ImageView 类型的成员变量。这样可以在列表项视图中存储一个 ImageView 控件的引用，以便稍后的操作，如设置图像或其他视图属性。

    public void setOnItemClickListener(OnItemClickListener listener) {
        // 设置点击事件监听器
        this.mOnItemClickListener = listener;
    }
//    这段代码定义了一个公有方法 setOnItemClickListener 用于设置一个点击事件监听器。这个方法的目的是允许外部代码设置一个监听器，以便在特定项被点击时执行相应的操作。通常，你可以在列表适配器或其他 UI 组件中使用此方法，将点击事件监听器传递给内部的列表项，以响应用户的点击操作。

    public interface OnItemClickListener {
        // 定义了一个接口 OnItemClickListener
        void itemClick(int position);
        // 接口中有一个抽象方法 itemClick，用于在实现这个接口的类中处理列表项点击事件
    }
//    这段代码定义了一个接口 OnItemClickListener，这个接口有一个抽象方法 itemClick，用于在实现这个接口的类中处理列表项的点击事件。通常，你可以创建一个实现了 OnItemClickListener 接口的类，然后实现 itemClick 方法以定义点击事件的行为。在列表或其他视图中，当某个项被点击时，可以调用实现了这个接口的类中的 itemClick 方法来执行相关操作。这是一种常见的事件处理机制，用于处理用户在应用中的交互行为。
}
