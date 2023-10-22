package com.ezreal.ezchat.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 图片加载类
 * Created by wudeng on 2016/12/6.
 */

public class ImageLoader {
    // 定义一个名为 ImageLoader 的类

    private static ImageLoader mInstance;
    // 声明一个 ImageLoader 类的私有静态成员变量 mInstance，用于在类内部获取 ImageLoader 的单例实例
//    这段代码是 ImageLoader 类的开始部分，它定义了 ImageLoader 类并声明了一个静态的 mInstance 成员变量。通常，mInstance 用来创建 ImageLoader 类的单例实例，以确保在整个应用程序中只有一个 ImageLoader 对象，从而提高资源的利用效率和避免资源浪费。单例模式是一种设计模式，通常用于管理全局的对象，例如图像加载器、数据库连接、网络请求等，以便多个部分共享相同的实例。

    /**
     * 图片缓存对象
     */
    private LruCache<String, Bitmap> mLruCache;
// 创建了一个 LruCache（最近最少使用缓存）对象，用于在内存中缓存位图（Bitmap）对象
//这行代码创建了一个 LruCache（最近最少使用缓存）对象，它用于在内存中缓存位图（Bitmap）对象。LruCache 是 Android 中用于缓存数据的类，它采用了一种基于最近最少使用原则的缓存策略。这意味着当缓存达到一定大小限制时，它会尝试移除最长时间没有被访问的数据，以腾出空间给新的数据。
//
//    在 Android 应用程序中，位图数据通常占据大量内存。通过使用 LruCache，可以有效地管理位图的内存使用，避免内存溢出问题。这是在处理图像、图形和其他需要大量内存的数据时非常有用的工具。
//
//    通常，你会在应用中的图像加载、处理或显示代码中使用 LruCache。你可以通过 put 方法将位图添加到缓存，通过 get 方法从缓存中检索位图，以及通过适当设置缓存大小限制来控制缓存的大小。

    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
// 创建一个 ExecutorService 对象，用于管理线程池

    private static final int DEFAULT_THREAD_COUNT = 1;
// 定义默认的线程数，通常为 1，表示顺序执行

    private Type mType = Type.LIFO;
// 定义一个枚举类型的变量 mType，初始值为 Type.LIFO（后进先出）
//    这段代码主要用于线程池的初始化和管理。
//
//    mThreadPool 是一个用于管理线程的 ExecutorService 对象。它可以用来执行多个线程任务，控制线程的数量、并发性等。
//
//    DEFAULT_THREAD_COUNT 是一个整数常量，用于定义默认的线程数量。在这里，默认为 1，表示只有一个线程会被执行，即顺序执行。
//
//    mType 是一个枚举类型的变量，表示任务的执行顺序。它的初始值为 Type.LIFO，表示使用后进先出（Last In, First Out）的方式执行任务。
//
//    这段代码初始化了线程池管理对象 mThreadPool，设置了默认的线程数量和任务执行顺序方式。线程池在多线程编程中非常有用，可以管理并发执行的任务，提高程序的性能和效率。

    /**
     * 用户队列
     */
    private LinkedList<Runnable> mTaskQueue;
// 创建一个名为 mTaskQueue 的 LinkedList，用于存储 Runnable 对象的队列
//这行代码声明并创建了一个名为 mTaskQueue 的 LinkedList 对象。在这个队列中，你可以存储 Runnable 类型的对象，以便按顺序执行它们。通常，这种队列用于管理需要按顺序执行的任务或操作，比如在 Android 中处理异步任务。你可以使用 add 方法将任务添加到队列中，然后依次执行队列中的任务。这对于需要顺序执行的工作非常有用，比如在后台执行耗时任务或者按照用户请求的顺序执行操作。

    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
// 声明一个线程对象 mPoolThread

    private Handler mPoolThreadHandler;
// 声明一个处理程序对象 mPoolThreadHandler
//这段代码声明了两个成员变量 mPoolThread 和 mPoolThreadHandler，用于处理后台任务和线程间通信。在 Android 应用开发中，通常会创建一个单独的线程来执行一些耗时的任务，以免阻塞主线程，这也被称为后台线程。mPoolThread 可能是用于创建这个后台线程的线程对象。
//
//    mPoolThreadHandler 是用于与后台线程进行通信的工具。在 Android 中，UI 线程（主线程）和后台线程之间的通信必须经过消息处理，Handler 是用来处理消息的类。mPoolThreadHandler 通常会与 mPoolThread 关联，以便将任务发送到后台线程执行。
//
//    这段代码中，mPoolThread 和 mPoolThreadHandler 被声明为成员变量，以便在类的其他方法中使用，比如用于执行后台任务或处理后台线程的结果。

    /**
     * 通过信号量，同步
     */
    private Semaphore mSemaPoolThreadHandler = new Semaphore(0);
// 创建一个名为 mSemaPoolThreadHandler 的 Semaphore 实例，并初始化为 0。Semaphore 用于控制线程访问的许可。

    private Semaphore mSemaPoolThread;
// 创建一个名为 mSemaPoolThread 的 Semaphore 实例。这个实例可能需要在其他地方进行初始化，因为在这里没有初始值。

    private Handler mUIHandler;
// 创建一个名为 mUIHandler 的 Handler 实例。Handler 通常用于在 Android 中管理与用户界面（UI）相关的线程通信。
//    这段代码声明了三个变量：
//
//    mSemaPoolThreadHandler 是一个 Semaphore 实例，被初始化为 0。Semaphores 用于控制线程的访问。在这里，它可能用于等待某个资源或事件的完成，直到许可被释放。
//
//    mSemaPoolThread 是另一个 Semaphore 实例。在代码中没有给它赋初始值，可能在其他地方初始化。这个 Semaphore 可能用于线程池的管理，以控制同时执行的线程数量。
//
//    mUIHandler 是一个 Handler 实例。在 Android 中，Handler 主要用于在不同线程间发送和处理消息，特别是用于与用户界面（UI）线程的通信。这个 Handler 可能用于在后台线程中执行任务后，将结果传递给 UI 线程以更新用户界面。

    public static ImageLoader getInstance(){
        // 获取 ImageLoader 单例的方法
        if (mInstance == null){
            // 如果 mInstance 为空
            synchronized (ImageLoader.class){
                // 在同步块中，确保多线程环境下只有一个线程能够访问以下代码
                if (mInstance == null){
                    // 再次检查 mInstance 是否为空
                    mInstance = new ImageLoader(DEFAULT_THREAD_COUNT, Type.LIFO);
                    // 如果为空，创建一个 ImageLoader 实例，默认线程数和加载策略（后进先出）
                }
            }
        }
        return mInstance;
        // 返回 ImageLoader 单例
    }

    public static ImageLoader getInstance(int threadCount, Type type){
        // 获取带有自定义线程数和加载策略的 ImageLoader 单例的方法
        if (mInstance == null){
            // 如果 mInstance 为空
            synchronized (ImageLoader.class){
                // 在同步块中，确保多线程环境下只有一个线程能够访问以下代码
                if (mInstance == null){
                    // 再次检查 mInstance 是否为空
                    mInstance = new ImageLoader(threadCount, type);
                    // 如果为空，创建一个 ImageLoader 实例，带有自定义线程数和加载策略
                }
            }
        }
        return mInstance;
        // 返回 ImageLoader 单例
    }
//    这段代码定义了一个名为 ImageLoader 的类，其中包括两个获取单例对象的静态方法 getInstance，这是一种常见的单例模式实现。第一个 getInstance 方法用于获取一个默认配置的 ImageLoader 实例，它会在内部使用默认的线程数和加载策略。第二个 getInstance 方法允许你传入自定义的线程数和加载策略来获取 ImageLoader 的实例。
//
//    在每个 getInstance 方法中，都使用了双重检查锁定（Double-Checked Locking）来确保在多线程环境下只创建一个 ImageLoader 实例。如果 mInstance 为 null，则创建一个新的 ImageLoader 实例，然后返回。这有助于减少不必要的对象创建，提高性能，同时确保只有一个 ImageLoader 实例存在。
//
//    这种模式通常用于创建全局共享的对象，以便在整个应用程序中进行图片加载操作。

    private ImageLoader(int ThreadCount, Type type) {
        // ImageLoader 类的构造函数，用于创建 ImageLoader 实例
        // 接收两个参数: ThreadCount - 用于指定线程数，type - 用于指定图片加载方式的类型
        init(ThreadCount, type);
        // 调用 init 方法，完成初始化工作
    }
//    这段代码是一个构造函数，用于创建 ImageLoader 实例。构造函数通常在对象创建时被调用，这里它接受两个参数：ThreadCount 用于指定线程数，type 用于指定图片加载方式的类型。一般来说，构造函数用于初始化对象的状态和属性。在这里，它调用了 init 方法，完成了进一步的初始化工作。
//
//    通常，构造函数用于设置对象的初始状态和执行必要的准备工作。在这种情况下，ThreadCount 和 type 参数将影响 ImageLoader 对象的行为，根据这些参数的不同值，ImageLoader 可能会以不同的方式加载图像。

    /**
     * 初始化图片加载类
     * @param threadCount 用于加载图片的最大线程数
     * @param type 图片加载策略
     */
    private void init(int threadCount, Type type) {
        // 初始化方法，用于设置线程池，内存缓存等

        // 初始化轮询线程
        mPoolThread = new Thread() {
            // 创建一个新的线程 mPoolThread
            @SuppressLint("HandlerLeak")
            @Override
            public void run() {
                Looper.prepare();
                // 初始化 Looper，允许 Handler 处理消息
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        // 当接收到消息时执行以下代码块
                        // 通过线程池取出任务并执行
                        mTreadPool.execute(getTask());
                        try {
                            mSemaPoolThread.acquire();
                            // 从信号量中获取许可，如果没有可用许可，则会被阻塞直到有可用许可
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                // Handler 实例化完成后释放信号量
                mSemaPoolThreadHandler.release();
                // 释放信号量 mSemaPoolThreadHandler
                Looper.loop();
                // 开始处理消息循环
            }
        };
        mPoolThread.start();

        // 获取应用最大可用内存，取1/8作为缓存内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        // 初始化图片缓存
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 返回图片所占内存
                return value.getRowBytes() * value.getHeight();
            }
        };
        // 初始化线程池，传入最大线程数
        mTreadPool = Executors.newFixedThreadPool(threadCount);
        // 初始化任务队列
        mTaskQueue = new LinkedList<>();
        mType = type;
        // 初始化信号量
        mSemaPoolThread = new Semaphore(threadCount);
    }
//    这段代码的主要目的是初始化一个图片加载的工具。这是一个多线程的图片加载工具，主要包含以下几个部分：

//    初始化轮询线程：创建一个后台线程 mPoolThread 用于轮询任务队列并执行任务。
//    获取应用最大可用内存：通过 Runtime 类的 maxMemory 方法获取应用程序可以使用的最大内存，并计算出用于图片缓存的内存大小。
//    初始化图片缓存：使用 LruCache 类创建图片内存缓存，设置图片的大小以控制缓存大小。
//    初始化线程池：使用 Executors.newFixedThreadPool 创建一个固定大小的线程池，线程数量为传入的 threadCount。
//    初始化任务队列：创建一个任务队列 mTaskQueue 用于存储加载图片的任务。
//    设置线程池的类型 mType，可以根据需要进行设置。
//    初始化信号量 mSemaPoolThread，用于控制线程池中的线程数量。
//    这段代码的主要目的是为了初始化一个图片加载工具，它包括了线程池、内存缓存以及任务队列，用于异步加载图片并实现图片的缓存和管理。

    /**
     * 根据path得到图片，显示到imageview上
     * @param path 图片路径
     * @param imageView 显示图片的imageview
     */
    @SuppressLint("HandlerLeak")
    public void loadImage(final String path, final ImageView imageView) {
        // 将传入的路径与ImageView关联
        imageView.setTag(path);

        if (mUIHandler == null) {
            // 检查UI处理程序是否为空，如果为空，进行初始化
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 处理消息
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    // 获取消息中的数据对象 ImgBeanHolder
                    Bitmap bitmap = holder.bitmap;
                    ImageView imageview = holder.imageview;
                    String path = holder.path;
                    // 从数据对象中获取位图、图像视图和路径信息

                    if (imageview.getTag().toString().equals(path)) {
                        // 检查图像视图的标签是否匹配当前路径
                        imageview.setImageBitmap(bitmap);
                        // 如果匹配，将位图设置到图像视图中
                    }
                }
            };
        }

        Bitmap bm = getBitMapFromLruCache(path);
// 从 LruCache 中获取位图，传入图片路径作为参数

        if (bm != null) {
            // 如果从 LruCache 中成功获取了位图
            Message message = mUIHandler.obtainMessage();
            // 创建一个消息对象，用于将位图信息传递给 UI 线程
            ImgBeanHolder holder = new ImgBeanHolder();
            // 创建一个自定义的 ImgBeanHolder 对象，用于存储位图和相关信息
            holder.bitmap = bm;
            // 将获取的位图设置到 ImgBeanHolder 对象中
            holder.imageview = imageView;
            // 将 ImageView 对象设置到 ImgBeanHolder 对象中
            holder.path = path;
            // 将图片路径设置到 ImgBeanHolder 对象中
            message.obj = holder;
            // 将 ImgBeanHolder 对象设置为消息的数据
            message.sendToTarget();
            // 发送消息到 UI 线程，以通知 UI 更新显示图片
        } else {
            // 如果缓存中不存在该路径所代表的图片，则发送加载图片任务至轮询线程
            addTask(new Runnable() {
                @Override
                public void run() {
                    // 1. 得到图片要显示的宽高
                    ImageSize size = getImageViewSize(imageView);
                    // 2. 压缩图片
                    Bitmap bm = decodeSampleBitmapFromPath(path, size.height, size.width);
                    // 3. 将图片加入缓存
                    if (getBitMapFromLruCache(path) == null) {
                        mLruCache.put(path, bm);
                    }
                    // 4. 将图片发送出去
                    Message message = mUIHandler.obtainMessage();
// 创建一个消息对象 message，用于在 Android 应用程序中进行线程间的通信。

                    ImgBeanHolder holder = new ImgBeanHolder();
// 创建一个 ImgBeanHolder 对象，用于封装图像和相关信息。

                    holder.bitmap = bm;
// 将位图（Bitmap）对象 bm 设置到 ImgBeanHolder 对象的 bitmap 属性中，用于存储图像数据。

                    holder.imageview = imageView;
// 将 ImageView 对象 imageView 设置到 ImgBeanHolder 对象的 imageview 属性中，用于显示位图。

                    holder.path = path;
// 将图像路径 path 设置到 ImgBeanHolder 对象的 path 属性中，用于记录图像的文件路径。

                    message.obj = holder;
// 将包含图像信息的 ImgBeanHolder 对象设置为消息 message 的附加数据，以便在不同线程之间传递这些数据。

                    message.sendToTarget();
// 将消息 message 发送到其关联的处理程序，通常用于更新用户界面或执行其他操作。

                    // 每执行完一个任务，释放一个信号量
                    mSemaPoolThread.release();
                }
            });
        }
    }
//    这段代码定义了一个方法 loadImage，用于在ImageView中加载图片。该方法的主要功能包括：
//
//    设置ImageView的Tag，将ImageView与指定的图片路径关联。
//    如果UIHandler（用于在UI线程中处理图片加载的Handler）为null，则初始化它。
//    尝试从LRU缓存中获取图片，如果已存在则直接显示在ImageView中。
//    如果缓存中不存在图片，添加一个加载图片的任务到线程池中执行。这个任务包括获取ImageView的尺寸、压缩图片、将图片加入缓存以及将图片发送到UI线程中显示。
//    通过这个方法，可以异步加载并显示图片，同时解决了由于ImageView的复用问题而引起的图片错乱显示。

    /**
     * 根据图片需要显示的宽高得到bitmap
     * @param path 图片路径
     * @param height 显示高度
     * @param width 显示宽度
     * @return 压缩后的bitmap
     */
    private Bitmap decodeSampleBitmapFromPath(String path, int height, int width) {
        // 创建一个函数用于从文件路径解码并返回一个缩小尺寸的 Bitmap 对象

        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        // 设置 BitmapFactory.Options 中的 inJustDecodeBounds 为 true，这意味着将仅获取图像的宽度和高度信息，不加载整个图像到内存

        BitmapFactory.decodeFile(path, op);
        // 通过调用 BitmapFactory.decodeFile 方法，获取到文件的宽度和高度，这样你可以知道原图像的实际尺寸

        op.inSampleSize = calculateInSampleSize(op, height, width);
        // 计算采样率，以便加载缩小尺寸的图像

        op.inJustDecodeBounds = false;
        // 将 inJustDecodeBounds 设置回 false，这意味着之后加载整个图像

        Bitmap bitmap = BitmapFactory.decodeFile(path, op);
        // 最终加载缩小尺寸的图像

        return bitmap;
    }
//    这段代码中的函数 decodeSampleBitmapFromPath 用于从文件路径加载图像并返回一个经过缩小尺寸的 Bitmap 对象。以下是代码的详细解释：
//
//    BitmapFactory.Options 是一个用于配置图像加载的类。
//
//    op.inJustDecodeBounds = true; 设置 inJustDecodeBounds 为 true，这表示加载图像时仅获取图像的宽度和高度信息，而不加载整个图像。这是为了计算采样率而进行的操作。
//
//            BitmapFactory.decodeFile(path, op); 使用 decodeFile 方法获取文件的宽度和高度信息，并将它们保存在 op 对象中。
//
//    op.inSampleSize = calculateInSampleSize(op, height, width); 计算采样率，它决定了加载缩小尺寸的图像。calculateInSampleSize 方法的实现应该在代码中的其他位置。
//
//    op.inJustDecodeBounds = false; 设置 inJustDecodeBounds 为 false，表示接下来将加载整个图像。
//
//    Bitmap bitmap = BitmapFactory.decodeFile(path, op); 使用 decodeFile 方法加载缩小尺寸的图像。
//
//    最终，这个函数会返回一个经过缩小尺寸的 Bitmap 对象，可以在应用中使用。这种方法可以用于加载大图像并避免占用过多内存，特别是在移动设备上。

    /**
     * 根据图片实际的宽高和需要显示的宽高，计算缩略图大小
     */
    private int calculateInSampleSize(BitmapFactory.Options op, int reqHeight, int reqWidth) {
        // 定义一个方法 calculateInSampleSize，用于计算图像的采样率
        int width = op.outWidth;
        int height = op.outHeight;
        int inSampleSize = 1;

        // 图像实际宽度大于需求宽度或实际高度大于需求高度时，执行以下操作
        if (width > reqWidth || height > reqHeight) {
            // 计算宽度采样率，即实际宽度与需求宽度的比值（四舍五入取整）
            int widthRatio = Math.round(width * 1.0f / reqWidth);
            // 计算高度采样率，即实际高度与需求高度的比值（四舍五入取整）
            int heightRatio = Math.round(height * 1.0f / reqHeight);
            // 选择宽度和高度采样率中的较大值作为最终采样率
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        // 返回计算得到的采样率
        return inSampleSize;
    }
//    这段代码定义了一个方法 calculateInSampleSize，该方法用于计算图像的采样率，以便有效地加载大尺寸图像，减少内存占用。采样率是指原始图像与目标图像之间的缩放比例。在这个方法中，通过比较实际图像的宽度和高度与需求的宽度和高度，来确定合适的采样率。如果实际图像的尺寸超过需求尺寸，就计算宽度和高度的比值，然后选择较大的比值作为最终的采样率。这有助于在加载图像时减小内存占用，特别是在加载大型图像时非常有用。

    /**
     * 根据iamgeview 获取适当的宽高
     * @param imageView 用于显示图片的imageview
     * @return 图片显示大小
     */
    private ImageSize getImageViewSize(ImageView imageView) {
        // 定义一个用于获取图像尺寸的方法

        ImageSize imagesize = new ImageSize();
        // 创建一个用于存储图像尺寸的对象

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        // 获取ImageView的布局参数，这可以用来确定ImageView的尺寸

        final DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        // 获取屏幕显示的指标，如屏幕宽度和高度

        int width = imageView.getWidth();
        // 获取ImageView的实际宽度，如果图片尚未加载，这个值可能为0

        if (width <= 0) {
            width = layoutParams.width;
        }
        // 如果实际宽度仍然为0，检查ImageView在布局中声明的宽度，如果是wrap_content则也为0

        if (width <= 0) {
            width = imageView.getMaxWidth();
        }
        // 如果宽度仍然为0，检查ImageView的最大宽度

        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }
        // 如果宽度仍然为0，将其设置为屏幕宽度

        int height = imageView.getHeight();
        // 获取ImageView的实际高度，如果图片尚未加载，这个值可能为0

        if (height <= 0) {
            height = layoutParams.height;
        }
        // 如果实际高度仍然为0，检查ImageView在布局中声明的高度，如果是wrap_content则也为0

        if (height <= 0) {
            height = imageView.getMaxHeight();
        }
        // 如果高度仍然为0，检查ImageView的最大高度

        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        // 如果高度仍然为0，将其设置为屏幕高度

        imagesize.height = height;
        imagesize.width = width;
        // 将获取到的高度和宽度存储到imagesize对象中

        return imagesize;
        // 返回存储了ImageView尺寸的对象
    }
//    这段代码定义了一个用于获取ImageView尺寸的方法 getImageViewSize。它用于确定ImageView的宽度和高度，以便在加载图像时可以进行适当的调整和显示。在这个方法中，首先尝试获取ImageView的实际宽度和高度，然后检查布局参数中声明的宽度和高度，如果这些值仍然为0，就使用屏幕的宽度和高度作为默认值。最后，将这些尺寸存储在 ImageSize 对象中并返回。这个方法通常用于图像加载和显示的逻辑中，以确保图像以合适的大小呈现在ImageView中。

    /**
     * 将获取图片的任务加载到任务队列中
     * @param runnable 待执行的任务
     */
    private synchronized void addTask(Runnable runnable) {
        // 这是一个同步方法，用于向任务队列中添加一个任务，任务是一个 Runnable 对象。

        mTaskQueue.add(runnable);
        // 向任务队列（mTaskQueue）中添加一个任务（runnable）。

        try {
            // 尝试捕获异常
            if (mPoolThreadHandler == null){
                // 如果线程处理器为空
                mSemaPoolThreadHandler.acquire();
                // 获取一个信号量，这里用于等待线程处理器的初始化。
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            // 捕获可能出现的中断异常，通常在多线程编程中使用。
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
        // 向线程处理器（mPoolThreadHandler）发送一条空消息（0x110），通常用于触发线程处理器的工作。
    }
//    这段代码是一个用于管理任务队列的方法。在方法中：
//
//            mTaskQueue.add(runnable);：将一个 runnable 对象添加到任务队列 mTaskQueue 中，这个队列存储了待执行的任务。
//
//            if (mPoolThreadHandler == null)：检查线程处理器是否为 null。
//
//            mSemaPoolThreadHandler.acquire();：如果线程处理器为 null，则阻塞当前线程，等待线程处理器初始化。这里使用信号量来实现等待机制。
//
//            mPoolThreadHandler.sendEmptyMessage(0x110);：向线程处理器发送一条消息（0x110），通常用于触发线程处理器的工作，使其从任务队列中取出任务并执行。
//
//    这个方法主要用于向任务队列中添加任务并确保线程处理器准备就绪以便执行任务。

    /**
     * 根据TYPE，从任务队列中取出一个Runable
     * @return Runnable
     */
    private Runnable getTask() {
        // 定义了一个名为 getTask 的私有方法，返回一个 Runnable 对象

        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
            // 如果任务队列的类型是 FIFO（先进先出），则从任务队列的头部（第一个任务）移除并返回一个任务（Runnable 对象）
        } else if (mType == Type.LIFO) {
            return mTaskQueue.removeLast();
            // 如果任务队列的类型是 LIFO（后进先出），则从任务队列的尾部（最后一个任务）移除并返回一个任务（Runnable 对象）
        }

        return null;
        // 如果任务队列的类型既不是 FIFO 也不是 LIFO，则返回 null，表示没有任务可执行
    }
//    这段代码定义了一个名为 getTask 的方法，它用于从任务队列中获取一个任务（Runnable 对象）。根据队列的类型（FIFO 或 LIFO），它会从队列的头部或尾部获取任务。
//
//    如果队列类型是 FIFO（先进先出），则会使用 removeFirst() 方法从任务队列的头部（第一个任务）移除并返回一个任务。
//
//    如果队列类型是 LIFO（后进先出），则会使用 removeLast() 方法从任务队列的尾部（最后一个任务）移除并返回一个任务。
//
//    最后，如果队列类型既不是 FIFO 也不是 LIFO，那么返回 null，表示没有任务可执行。
//
//    这段代码通常用于任务调度器或线程池中，根据任务的执行顺序来获取下一个要执行的任务。

    /**
     * 根据path从缓存中获取图片bitmap
     * @param path 图片路径
     * @return 从缓存中取出的Bitmap
     */
    private Bitmap getBitMapFromLruCache(String path) {
        // 根据传入的路径参数从 LruCache 中获取 Bitmap 对象
        return mLruCache.get(path);
    }
//    这段代码定义了一个方法 getBitMapFromLruCache，用于从 LruCache 中获取 Bitmap 对象。它接受一个 path 参数，表示要获取 Bitmap 的路径。函数的主要作用是根据路径从 LruCache 中检索 Bitmap 对象并返回。

    // 创建一个私有内部类 ImageSize
    private class ImageSize {
        int height; // 图片高度
        int width;  // 图片宽度
    }
//    这段代码定义了一个私有内部类 ImageSize，该类具有两属性 height 和 width，用于表示图像的高度和宽度。通常，这样的类用于在程序中方便地传递和存储图像的尺寸信息。

    // 定义一个名为 ImgBeanHolder 的内部类
    private class ImgBeanHolder {
        Bitmap bitmap;         // 用于存储图片的 Bitmap 对象
        ImageView imageView;   // 用于显示图片的 ImageView 控件
        String path;           // 存储图片路径的字符串
    }
//    这段代码定义了一个名为 ImgBeanHolder 的内部类，用于封装图片信息。

    public enum Type {
        FIFO,LIFO,
    }
//    这段代码定义了一个枚举类型 Type，其中包含两个枚举值 FIFO 和 LIFO。枚举类型通常用于表示一组相关的常量或选项，这里的 Type 用于表示数据结构的不同处理方式，具体是先进先出（FIFO）还是后进先出（LIFO）。枚举值 FIFO 表示先进先出，而 LIFO 表示后进先出。在程序中，可以使用这个枚举类型来指定数据处理方式的选项。
}
