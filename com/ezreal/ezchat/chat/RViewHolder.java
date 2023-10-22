package com.ezreal.ezchat.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;

import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


/**
 * RecyclerView View Holder
 * Created by wudeng on 2017/3/9.
 */

public class RViewHolder extends RecyclerView.ViewHolder {
    // 定义了一个名为 RViewHolder 的类，它是 RecyclerView.ViewHolder 的子类

    private SparseArray<View> mViews;
    // 使用 SparseArray 存储视图对象，可以根据视图的 ID 快速检索视图
    private View mConvertView;
    // 用于存储列表项的根视图
    private Context mContext;
    // 用于保存上下文对象，通常用于资源访问和视图操作

//    这段代码定义了一个名为 RViewHolder 的类，它是 RecyclerView.ViewHolder 的子类。这个类主要用于管理 RecyclerView 中的列表项视图。

    public RViewHolder(Context context, View itemView) {
        // 构造函数，用于创建 RViewHolder 实例
        super(itemView);
        // 调用父类的构造函数，将 itemView 传递给父类
        mContext = context;
        // 初始化成员变量 mContext，用于存储上下文信息
        mConvertView = itemView;
        // 初始化成员变量 mConvertView，用于存储视图对象
        mViews = new SparseArray<>();
        // 初始化成员变量 mViews，它是一个稀疏数组，用于存储视图控件
    }
//    这段代码定义了一个名为 RViewHolder 的类的构造函数。RViewHolder 是用于适配器模式中的 RecyclerView 或 ListView 的视图持有者。构造函数接受两个参数，一个是 Context，表示上下文，另一个是 itemView，表示视图对象。

    private <T extends View> T getView(int viewId) {
        // 定义一个泛型方法 getView，用于查找视图控件
        View view = mViews.get(viewId);
        // 从缓存中查找指定 viewId 的视图控件
        if (view == null) {
            // 如果缓存中没有找到视图
            view = mConvertView.findViewById(viewId);
            // 通过视图 ID 在当前的布局中查找视图控件
            mViews.put(viewId, view);
            // 将查找到的视图控件存入缓存以备后续使用
        }
        return (T) view;
        // 返回找到的视图，使用泛型类型 T 进行类型转换，以便在调用时返回正确的视图类型
    }
//    这段代码定义了一个泛型方法 getView，它的主要目的是在布局中查找指定 ID 的视图控件。这个方法通常在适配器等地方用于获取布局中的各种控件，以便在代码中进行操作或显示。

    public View getConvertView() {
        return mConvertView;
    }
//    这个方法返回列表项的视图对象。在 Adapter 中，通常会有多个列表项需要绘制，这个方法返回当前列表项的视图，以便对其进行填充和显示。
    public TextView getTextView(int viewId){
        return getView(viewId);
    }
//    这个方法用于获取列表项中的 TextView 视图，传入的参数 viewId 代表 TextView 的资源 ID。它通过调用 getView(viewId) 方法实现，返回指定资源 ID 的 TextView 对象。
    public ImageView getImageView(int viewId){
        return getView(viewId);
    }
//    这个方法用于获取列表项中的 ImageView 视图，传入的参数 viewId 代表 ImageView 的资源 ID。它通过调用 getView(viewId) 方法实现，返回指定资源 ID 的 ImageView 对象。
    public RelativeLayout getReltiveLayout(int viewId){
        return getView(viewId);
    }
//    这个方法用于获取列表项中的 RelativeLayout 视图，传入的参数 viewId 代表 RelativeLayout 的资源 ID。它通过调用 getView(viewId) 方法实现，返回指定资源 ID 的 RelativeLayout 对象。
    public void setText(int viewId, String text) {
        // 通过视图的 ID 和文本内容来设置 TextView 的文本

        TextView tv = getView(viewId);
        // 通过 getView 方法获取对应 ID 的 TextView 视图

        tv.setText(text);
        // 设置 TextView 的文本内容为传入的 text 字符串

    }
//    这段代码定义了一个方法 setText，用于在视图中设置指定 ID 的 TextView 的文本内容。它接受两个参数，viewId 表示要设置文本的 TextView 的资源 ID，text 是要设置的文本内容。

    public void setImageResource(int viewId, int resId) {
        // 定义一个公共方法，用于设置 ImageView 控件的资源图像
        ImageView view = getView(viewId);
        // 通过传入的 viewId 参数，获取对应的 ImageView 控件
        view.setImageResource(resId);
        // 使用传入的 resId 参数来设置 ImageView 控件的图像资源
    }
//    这段代码定义了一个公共方法 setImageResource，其目的是设置一个特定 ImageView 控件的图像资源。

    public void setImageBitmap(int viewId, Bitmap bitmap) {
        // 通过传入的视图 ID 获取对应的 ImageView 控件
        ImageView view = getView(viewId);
        // 将传入的 Bitmap 对象设置到该 ImageView 控件中
        view.setImageBitmap(bitmap);
    }

    public void setImageByUrl(Context context, int viewId, String url, int default_img_id) {
        // 通过 Glide（或类似库）加载指定 URL 对应的图片并设置到 ImageView 控件
        setImageWithGlide(context, viewId, url, default_img_id);
    }

    public void setImageByUri(Context context, int viewId, String uri, int default_img_id) {
        // 通过 Glide（或类似库）加载指定 Uri 对应的图片并设置到 ImageView 控件
        setImageWithGlide(context, viewId, uri, default_img_id);
    }

    public void setImageByFilePath(Context context, int viewId, String path, int default_img_id) {
        // 通过 Glide（或类似库）加载指定文件路径对应的图片并设置到 ImageView 控件
        setImageWithGlide(context, viewId, path, default_img_id);
    }
//    这段代码是一个帮助类中的方法集合，用于设置 ImageView 控件的图片。它包括以下几种方法：
//
//    setImageBitmap(int viewId, Bitmap bitmap): 设置指定 viewId 对应的 ImageView 控件的图片为传入的 Bitmap 对象。
//
//    setImageByUrl(Context context, int viewId, String url, int default_img_id): 通过 Glide 或类似的库加载指定 URL 对应的图片，然后设置到 viewId 对应的 ImageView 控件中。如果加载失败，将会显示默认图片，default_img_id 是默认图片的资源 ID。
//
//    setImageByUri(Context context, int viewId, String uri, int default_img_id): 类似于上一个方法，但是这里加载的是 Uri 对应的图片。
//
//    setImageByFilePath(Context context, int viewId, String path, int default_img_id): 类似于上一个方法，但是这里加载的是文件路径对应的图片。
//
//    这些方法主要用于设置 ImageView 控件的图片内容，通常在 Android 应用中用于加载和显示网络图片或本地图片。常见的使用场景是在列表项中加载不同的图片，或者在用户界面中显示用户头像等图片。

    private void setImageWithGlide(Context context, int viewId, String s, int default_img_id) {
        // 用Glide加载图片并设置到ImageView上
        // 参数:
        // - context: 上下文对象，通常是 Activity 或 Fragment 的上下文
        // - viewId: ImageView 的资源ID
        // - s: 图片的URL或本地路径
        // - default_img_id: 如果加载失败时要显示的默认图片的资源ID

        // 获取指定资源ID的ImageView
        ImageView view = getView(viewId);

        // 使用Glide库加载图片
        Glide.with(context) // 传入上下文
                .load(s) // 加载指定URL或本地路径的图片
                .asBitmap() // 将图片作为位图加载
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 使用磁盘缓存策略，缓存所有尺寸的图片
                .error(default_img_id) // 在加载失败时显示指定的默认图片
                .into(view); // 设置加载后的图片显示到指定的ImageView中
    }
//    这段代码定义了一个方法 setImageWithGlide，该方法使用了 Glide 图片加载库来加载指定 URL 或本地路径的图片，并将其显示在指定的 ImageView 控件中。方法的参数包括上下文对象 context，ImageView 的资源ID viewId，图片的URL或本地路径 s 以及加载失败时要显示的默认图片的资源ID default_img_id。

    public void setImageDrawable(int viewId, Drawable drawable) {
        // 通过传入的视图ID获取相应的ImageView控件
        ImageView view = getView(viewId);
        // 设置该ImageView的图片为传入的drawable对象
        view.setImageDrawable(drawable);
    }

    public void setBackgroundColor(int viewId, int color) {
        // 通过传入的视图ID获取相应的View控件
        View view = getView(viewId);
        // 设置该View的背景颜色为传入的color
        view.setBackgroundColor(color);
    }

    public void setBackgroundRes(int viewId, int backgroundRes) {
        // 通过传入的视图ID获取相应的View控件
        View view = getView(viewId);
        // 设置该View的背景资源为传入的backgroundRes指定的资源
        view.setBackgroundResource(backgroundRes);
    }

    public void setTextColor(int viewId, int textColor) {
        // 通过传入的视图ID获取相应的TextView控件
        TextView view = getView(viewId);
        // 设置该TextView的文本颜色为传入的textColor
        view.setTextColor(textColor);
    }

    public void setTextColorRes(int viewId, int textColorRes) {
        // 通过传入的视图ID获取相应的TextView控件
        TextView view = getView(viewId);
        // 通过资源ID获取文本颜色，并将该颜色设置给TextView
        view.setTextColor(mContext.getResources().getColor(textColorRes));
        // 这里 mContext 是一个成员变量，用于获取应用的资源
    }
//    这段代码包含了一些公用方法，用于操作视图组件的外观属性。这些方法通常用于修改控件的背景颜色、图片、文本颜色等，根据传入的视图ID来找到对应的控件，然后进行相应的设置。这是一个通用的视图操作工具，用于简化在Android应用中操作UI元素的代码。

    @SuppressLint("NewApi")
// 标记用于告知静态分析工具忽略指定的代码警告
    public void setAlpha(int viewId, float value) {
        // 定义一个方法名为 setAlpha，接受两个参数：viewId 和 value

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // 检查当前 Android 版本是否高于或等于 Honeycomb (API 11) 版本

            getView(viewId).setAlpha(value);
            // 如果当前 Android 版本高于 Honeycomb，使用 setAlpha 方法设置视图的透明度为指定的值
        } else {
            // 如果当前 Android 版本低于 Honeycomb

            // Pre-honeycomb hack to set Alpha value
            // 针对 Honeycomb 之前的版本设置 Alpha 值的技巧

            AlphaAnimation alpha = new AlphaAnimation(value, value);
            // 创建一个 AlphaAnimation 动画，用于改变透明度值

            alpha.setDuration(0);
            // 设置动画的持续时间为 0，即立即完成动画

            alpha.setFillAfter(true);
            // 设置动画完成后保持在最终状态

            getView(viewId).startAnimation(alpha);
            // 启动动画来设置视图的透明度
        }
    }
//    这段代码定义了一个方法 setAlpha，用于设置指定视图的透明度。它接受两个参数：viewId 表示视图的标识符，value 表示要设置的透明度值。
//
//    首先，通过 @SuppressLint("NewApi") 注解告知静态分析工具忽略可能出现的新 API 使用警告。
//    接下来，代码检查当前 Android 版本是否高于或等于 Honeycomb（API 11）版本。
//    如果当前 Android 版本高于 Honeycomb，它将直接使用 setAlpha 方法来设置视图的透明度。
//    如果当前 Android 版本低于 Honeycomb，它将使用一个透明度变化的 AlphaAnimation 动画来模拟设置透明度。这是一种在较旧 Android 版本上实现类似效果的技巧。
//    这段代码的主要目的是为了兼容不同 Android 版本的透明度设置，以确保应用在不同设备上具有一致的行为。


    public void setVisible(int viewId, boolean visible) {
        // 根据传入的 viewId 获取对应的视图
        View view = getView(viewId);
        // 根据传入的 boolean 参数设置视图的可见性
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        // 如果 visible 为 true，设置视图可见，否则设置为不可见
    }

    public void linkify(int viewId) {
        // 根据传入的 viewId 获取对应的 TextView 视图
        TextView view = getView(viewId);
        // 使用 Linkify 类为 TextView 中的文本添加链接
        Linkify.addLinks(view, Linkify.ALL);
        // Linkify.addLinks 方法可以自动识别文本中的链接、邮箱地址等，并将它们变为可点击的超链接
        // Linkify.ALL 表示识别所有类型的链接，包括网址、邮件地址、电话号码等
    }
//    这段代码包含两个方法，分别用于处理视图的可见性和在 TextView 中添加链接。这些方法可以帮助你在 Android 应用中控制视图的显示和为文本添加链接。setVisible 方法根据布尔参数来控制视图的可见性，linkify 方法使用 Linkify 类为 TextView 中的文本添加链接，使其变成可点击的超链接。这些方法有助于改进用户界面的交互性和可用性。

    public void setTypeface(Typeface typeface, int... viewIds) {
        // 设置指定视图的字体类型
        for (int viewId : viewIds) {
            // 遍历传入的视图 ID 列表
            TextView view = getView(viewId);
            // 获取对应的 TextView 视图
            view.setTypeface(typeface);
            // 设置视图的字体类型
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            // 添加一个字体渲染标志，以改善文本的显示效果
        }
    }

    public void setProgress(int viewId, int progress) {
        // 设置指定 ProgressBar 控件的进度
        ProgressBar view = getView(viewId);
        // 获取对应的 ProgressBar 视图
        view.setProgress(progress);
        // 设置进度
    }

    public void setProgress(int viewId, int progress, int max) {
        // 设置指定 ProgressBar 控件的进度和最大值
        ProgressBar view = getView(viewId);
        // 获取对应的 ProgressBar 视图
        view.setMax(max);
        // 设置进度条的最大值
        view.setProgress(progress);
        // 设置进度
    }

    public void setMax(int viewId, int max) {
        // 设置指定 ProgressBar 控件的最大值
        ProgressBar view = getView(viewId);
        // 获取对应的 ProgressBar 视图
        view.setMax(max);
        // 设置进度条的最大值
    }
//    这些方法用于在 Android 应用中操作视图控件。下面是对每个方法的简要解释：
//
//    setTypeface(Typeface typeface, int... viewIds)：用于设置指定视图的字体类型。Typeface 参数表示要应用的字体，而 viewIds 参数是一个可变参数列表，可以传入一个或多个视图的 ID。在方法内部，通过遍历 viewIds，获取对应的 TextView 视图，然后为这些视图设置相同的字体类型和改善文本显示效果的标志。
//
//    setProgress(int viewId, int progress)：设置指定 ProgressBar 控件的进度。方法接受一个视图 ID 和一个进度值，在方法内部，通过视图 ID 获取对应的 ProgressBar 视图，然后设置其进度。
//
//    setProgress(int viewId, int progress, int max)：设置指定 ProgressBar 控件的进度和最大值。与上一个方法类似，不过这个方法还需要传入最大值。在方法内部，设置 ProgressBar 的最大值和进度。
//
//    setMax(int viewId, int max)：设置指定 ProgressBar 控件的最大值。这个方法接受一个视图 ID 和一个最大值，然后获取对应的 ProgressBar 视图，并设置其最大值。
//
//    这些方法允许在 Android 应用中以编程方式更改视图控件的属性，例如字体类型、进度和最大值。

    public void setRating(int viewId, float rating) {
        // 设置评分
        RatingBar view = getView(viewId);
        // 通过视图 ID 获取 RatingBar 控件
        view.setRating(rating);
        // 设置 RatingBar 控件的评分值为传入的 rating 参数
    }

    public void setRating(int viewId, float rating, int max) {
        // 设置评分并指定最大值
        RatingBar view = getView(viewId);
        // 通过视图 ID 获取 RatingBar 控件
        view.setMax(max);
        // 设置 RatingBar 控件的最大值为传入的 max 参数
        view.setRating(rating);
        // 设置 RatingBar 控件的评分值为传入的 rating 参数
    }

    public void setTag(int viewId, Object tag) {
        // 设置视图的标签
        View view = getView(viewId);
        // 通过视图 ID 获取 View 控件
        view.setTag(tag);
        // 将传入的 tag 参数设置为视图的标签
    }

    public void setTag(int viewId, int key, Object tag) {
        // 设置视图的标签并指定键
        View view = getView(viewId);
        // 通过视图 ID 获取 View 控件
        view.setTag(key, tag);
        // 将传入的 tag 参数设置为视图的标签，并使用 key 参数指定标签的键
    }
//    这段代码包含了一些方法，用于在 Android 应用中对视图元素进行操作。这些方法根据视图的 ID 查找相应的视图，然后执行特定的操作：
//
//    setRating(int viewId, float rating): 这个方法设置一个 RatingBar 控件的评分值。
//
//    setRating(int viewId, float rating, int max): 这个方法设置一个 RatingBar 控件的评分值并指定最大值。
//
//    setTag(int viewId, Object tag): 这个方法设置一个视图的标签，通常用于在视图中存储一些额外的信息。
//
//    setTag(int viewId, int key, Object tag): 这个方法设置一个视图的标签并指定一个键，允许在一个视图中存储多个标签，每个标签可以通过不同的键来访问。

    public void setChecked(int viewId, boolean checked) {
        // 设置视图的选中状态
        Checkable view = (Checkable) getView(viewId);
        // 从指定的视图 ID 获取一个 Checkable 类型的视图
        view.setChecked(checked);
        // 使用传入的布尔值来设置视图的选中状态
    }

    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        // 设置视图的点击监听器
        View view = getView(viewId);
        // 从指定的视图 ID 获取一个 View 类型的视图
        view.setOnClickListener(listener);
        // 将传入的点击监听器设置给视图
    }

    public void setOnTouchListener(int viewId, View.OnTouchListener listener) {
        // 设置视图的触摸监听器
        View view = getView(viewId);
        // 从指定的视图 ID 获取一个 View 类型的视图
        view.setOnTouchListener(listener);
        // 将传入的触摸监听器设置给视图
    }

    public void setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        // 设置视图的长按监听器
        View view = getView(viewId);
        // 从指定的视图 ID 获取一个 View 类型的视图
        view.setOnLongClickListener(listener);
        // 将传入的长按监听器设置给视图
    }
//    这段代码定义了一组用于管理视图控件的辅助方法。这些方法允许你在布局中查找特定视图，然后设置它们的状态和监听器。这通常在 Android 应用中用于操作用户界面。这些方法中的每一个接受一个视图 ID 和一个适当的参数（例如，选中状态、点击监听器、触摸监听器或长按监听器），然后使用传入的参数设置指定 ID 的视图。

}
