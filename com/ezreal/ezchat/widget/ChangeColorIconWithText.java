package com.ezreal.ezchat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.ezreal.ezchat.R;

public class ChangeColorIconWithText extends View {

	private int mColor = 0xFF45C01A; // 定义图标颜色，默认为绿色
	private Bitmap mIconBitmap; // 用于显示图标的位图
	private String mText = "消息"; // 显示的文本，默认为"消息"
	private int mTextSize = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()); // 设置文本大小，默认为 12sp

	private Canvas mCanvas; // 用于绘制的画布
	private Bitmap mBitmap; // 位图用于绘制
	private Paint mPaint; // 画笔

	private float mAlpha; // 控制图标颜色的透明度

	private Rect mIconRect; // 图标的矩形区域
	private Rect mTextBound; // 文本的矩形区域
	private Paint mTextPaint; // 绘制文本的画笔
//	这段代码定义了一个名为ChangeColorIconWithText的自定义View，用于显示带有文本的彩色图标。代码中包括了设置图标颜色、图标位图、显示文本、文本大小等属性的初始化。还有用于绘制的画布、位图和画笔。最后，还有控制图标颜色透明度的mAlpha属性以及图标和文本的矩形区域和相应的画笔。

	public ChangeColorIconWithText(Context context) {
		// 构造函数，创建ChangeColorIconWithText的实例，接受一个上下文参数。
		// 这个构造函数被其他构造函数调用，传递给它默认值 null。
		this(context, null);
	}

	public ChangeColorIconWithText(Context context, AttributeSet attrs) {
		// 构造函数，创建ChangeColorIconWithText的实例，接受上下文和属性集参数。
		// 这个构造函数被其他构造函数调用，传递给它默认值 0。
		this(context, attrs, 0);
	}
//	这两个构造函数用于创建ChangeColorIconWithText的实例。第一个构造函数接受一个上下文（Context）作为参数，而第二个构造函数接受上下文和属性集（Attributes）作为参数。这两个构造函数分别调用了另一个构造函数，传递了默认值。通常，这种构造函数重载用于为不同的创建实例的方式提供灵活性，以适应不同的使用情况和参数配置。

	/**
	 * 获取自定义属性的值
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public ChangeColorIconWithText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// 从XML属性集合中获取自定义属性
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChangeColorIconWithText);

		int n = a.getIndexCount();

		// 遍历属性集合
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
				case R.styleable.ChangeColorIconWithText_icon:
					// 从属性集合中获取图标，并将其转换为位图
					BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
					mIconBitmap = drawable.getBitmap();
					break;
				case R.styleable.ChangeColorIconWithText_color:
					// 获取颜色属性，默认值为0xFF60CAF1
					mColor = a.getColor(attr, 0xFF60CAF1);
					break;
				case R.styleable.ChangeColorIconWithText_text:
					// 获取文本属性
					mText = a.getString(attr);
					break;
				case R.styleable.ChangeColorIconWithText_text_size:
					// 获取文本大小属性，以像素为单位，默认大小为12sp
					mTextSize = (int) a.getDimension(attr, TypedValue
							.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
									getResources().getDisplayMetrics()));
					break;
			}
		}

		// 回收属性集合，以便重用
		a.recycle();

		// 初始化文本绘制相关的属性
		mTextBound = new Rect();
		mTextPaint = new Paint();
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0Xffbfbfbf); // 设置文本颜色，默认为灰色
		mTextPaint.setAntiAlias(true); // 开启抗锯齿
		mTextPaint.setDither(true); // 开启抗抖动
		mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound); // 计算文本的边界
	}
//	这段代码是一个自定义控件的构造方法，主要用于初始化控件的各种属性，包括图标、颜色、文本和文本大小。它从XML布局文件中获取自定义属性，然后根据这些属性设置控件的状态。这个构造方法允许我们在XML布局文件中使用自定义属性来配置控件的外观和行为。

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 计算图标宽度，限制在视图宽度和高度以及文本高度之间的最小值
		int iconWidth = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
				getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mTextBound.height());

		// 计算图标的位置左上角坐标
		int left = getMeasuredWidth() / 2 - iconWidth / 2;
		int top = getMeasuredHeight() / 2 - (mTextBound.height() + iconWidth) / 2;

		// 创建包围图标的矩形
		mIconRect = new Rect(left, top, left + iconWidth, top + iconWidth);
	}
//	这段代码是自定义View的onMeasure方法，用于测量视图的大小。在这里，视图测量自身的宽度和高度，并计算图标的位置和大小，以确保图标在视图中居中显示。这个方法通常与onDraw方法一起使用，用于绘制自定义视图。

	@Override
	protected void onDraw(Canvas canvas) {
		// 在自定义View的绘制过程中，会调用此方法来绘制内容。

		// 绘制原图
		canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
		// 使用Canvas的drawBitmap方法绘制位图，其中mIconBitmap是原始图像，mIconRect是绘制的位置和大小，null表示不使用画笔特效。

		// 内存去准备变色的 mBitmap
		int alpha = (int) Math.ceil(255 * mAlpha);
		// 计算一个 alpha 值，用于表示透明度，范围是0（完全透明）到255（完全不透明）。

		setupTargetBitmap(alpha);
		// 为目标位图 mBitmap 进行准备，实现颜色变化。

		// 绘制原文本
		drawSourceText(canvas, alpha);
		// 绘制原始文本。

		// 绘制变色的文本
		drawTargetText(canvas, alpha);
		// 绘制颜色变化后的文本。

		// 绘制渐变色后的图 mBitmap
		canvas.drawBitmap(mBitmap, 0, 0, null);
		// 绘制最终的图像 mBitmap 到Canvas上。
	}
//	这段代码是一个自定义View的onDraw方法，用于在View上绘制原始图像、透明度调整、颜色变化后的文本和最终图像。这通常用于实现图像渐变效果。

	// 绘制目标文本
	private void drawTargetText(Canvas canvas, int alpha) {
		// 设置文本颜色为指定颜色
		mTextPaint.setColor(mColor);
		// 设置文本透明度
		mTextPaint.setAlpha(alpha);

		// 计算文本的横坐标 x
		int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
		// 计算文本的纵坐标 y
		int y = mIconRect.bottom + mTextBound.height();

		// 在指定位置绘制文本
		canvas.drawText(mText, x, y, mTextPaint);
	}

	// 绘制源文本
	private void drawSourceText(Canvas canvas, int alpha) {
		// 设置文本颜色为灰色
		mTextPaint.setColor(0xffbfbfbf);
		// 设置文本透明度
		mTextPaint.setAlpha(255 - alpha);

		// 计算文本的横坐标 x
		int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
		// 计算文本的纵坐标 y
		int y = mIconRect.bottom + mTextBound.height();

		// 在指定位置绘制文本
		canvas.drawText(mText, x, y, mTextPaint);
	}
//	这段代码包含了两个方法，分别用于绘制目标文本和源文本。它们在 Canvas 上绘制文本，设置文本的颜色、透明度以及位置。这通常用于自定义控件中的绘制操作，例如在一个图标下面添加文本标签。

	/**
	 * 在内存中绘制可变色的Icon
	 */
	// 设置目标位图，带有指定透明度
	private void setupTargetBitmap(int alpha) {

		// 创建一个空位图，大小与视图相同
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

		// 创建绘制用的画笔，设置颜色、抗锯齿、抖动、透明度
		mPaint = new Paint();
		mPaint.setColor(mColor);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setAlpha(alpha);

		// 在画布上绘制一个纯色矩形，作为目标图像
		mCanvas.drawRect(mIconRect, mPaint);

		// 设置图像合成模式为 DST_IN，这样只绘制重合区域
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

		// 绘制原图 mIcon 到位图上，最终得到带透明度的 mIconBitmap
		mPaint.setAlpha(255);
		mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
	}
//	这段代码的主要目的是生成一个带有指定透明度的目标位图，通过使用不同的绘图方法和合成模式，将目标图像与源图像合并以获得特定效果。在这里，首先创建一个空位图，然后在这个位图上绘制一个纯色的矩形（目标图像），接着设置图像合成模式为DST_IN，这会将源图像与目标图像的重合区域绘制出来，最后将原图 mIcon 绘制在位图上，得到一个带透明度的 mIconBitmap。这个操作通常用于图形处理中，例如实现图形融合效果。

	// 用于保存实例状态的关键字，用于在Activity重建时存储和恢复数据
	private static final String INSTANCE_STATUS = "instance_status";

	// 用于存储状态中 alpha 值的关键字
	private static final String STATUS_ALPHA = "status_alpha";
//	这部分代码定义了两个常量字符串。这些字符串通常用于在Android应用程序中保存和恢复Activity的状态。INSTANCE_STATUS是用于存储实例状态的关键字，通常在Activity被销毁前将数据存储在Bundle对象中，以便在Activity重新创建时恢复状态。STATUS_ALPHA通常用于存储和恢复状态中的 alpha 值，可能是一个透明度或不透明度的值。这些常量用于在不同生命周期事件之间传递数据并维护应用程序的状态。

	// 重写 onSaveInstanceState 方法，用于保存当前状态的数据
	@Override
	protected Parcelable onSaveInstanceState() {
		// 创建一个 Bundle 对象，用于存储状态数据
		Bundle bundle = new Bundle();

		// 调用父类的 onSaveInstanceState 方法，将其返回的 Parcelable 对象存入 Bundle 中
		bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());

		// 存储当前对象中的 alpha 值到 Bundle 中
		bundle.putFloat(STATUS_ALPHA, mAlpha);

		// 返回保存了状态数据的 Bundle 对象
		return bundle;
	}
//	这段代码是在 Android 中用于保存 Activity 或 Fragment 状态的方法。在 onSaveInstanceState 中，首先创建了一个 Bundle 对象，它将用于存储状态数据。接着，调用父类的 onSaveInstanceState 方法，将父类返回的 Parcelable 对象存入 Bundle 中，以便在稍后恢复状态时使用。最后，将当前对象的 alpha 值 mAlpha 存入 Bundle 中，这样在 Activity 或 Fragment 重新创建时，可以从 Bundle 中获取 alpha 值以还原状态。
//
//	这种机制通常用于保存一些临时性状态，以确保用户不会失去数据或体验的一致性，例如在设备旋转或用户按下后退按钮时。

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		// 恢复视图的状态
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			// 从 Bundle 中获取保存的透明度值
			mAlpha = bundle.getFloat(STATUS_ALPHA);

			// 调用父类的 onRestoreInstanceState 方法来恢复实例状态
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
			return;
		}

		// 如果状态不是 Bundle 类型，使用默认的状态恢复
		super.onRestoreInstanceState(state);
	}
//	这段代码是在 Android 中用于恢复视图状态的方法。具体注释如下：
//
//	@Override: 这个注解表示下面的方法是对父类方法的覆盖（重写）。
//	protected void onRestoreInstanceState(Parcelable state): 这个方法用于恢复视图的状态。
//			if (state instanceof Bundle): 这个条件判断检查传入的状态对象是否是 Bundle 类型。
//	Bundle bundle = (Bundle) state;: 如果状态是 Bundle 类型，将其转换为 Bundle 对象。
//	mAlpha = bundle.getFloat(STATUS_ALPHA);: 从 Bundle 中获取一个名为 STATUS_ALPHA 的浮点数值，通常用于恢复透明度或自定义的状态。
//			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));: 使用父类的 onRestoreInstanceState 方法来恢复视图的实例状态。INSTANCE_STATUS 通常是一个常量用于保存视图状态的键。
//			super.onRestoreInstanceState(state);: 如果状态不是 Bundle 类型，使用默认的状态恢复，调用父类的方法。

	// 设置图标透明度
	public void setIconAlpha(float alpha) {
		this.mAlpha = alpha; // 将传入的透明度值赋给成员变量 mAlpha
		invalidateView(); // 调用方法来刷新视图以应用透明度设置
	}
//	这段代码用于设置图标的透明度。setIconAlpha 方法接受一个 alpha 参数，该参数表示图标的透明度。在该方法中，传入的透明度值被赋给类中的成员变量 mAlpha，然后调用 invalidateView 方法来刷新视图，以应用新的透明度设置。

	/**
	 * 重绘
	 */
	// 重新绘制视图
	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			// 如果当前线程是主线程，可以直接调用invalidate()来请求重绘
			invalidate();
		} else {
			// 如果当前线程不是主线程，使用postInvalidate()来在主线程中请求重绘
			postInvalidate();
		}
	}
//	这段代码的目的是重新绘制视图。invalidate() 方法通常在主线程中调用，以触发视图的重绘。然而，如果当前线程不是主线程，就不能直接调用 invalidate()，因为 Android UI 操作必须在主线程中执行。因此，使用 postInvalidate() 方法将重绘请求发送到主线程，确保在主线程中执行视图的重绘操作。这有助于避免线程相关的问题和UI更新的同步问题。

}
