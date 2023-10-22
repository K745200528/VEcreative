package com.ezreal.ezchat.activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.ezreal.ezchat.R;
import com.ezreal.ezchat.bean.LocationPoint;
import com.suntek.commonlibrary.adapter.OnItemClickListener;
import com.suntek.commonlibrary.adapter.RViewHolder;
import com.suntek.commonlibrary.adapter.RecycleViewAdapter;
import com.suntek.commonlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择位置，发送位置信息
 * Created by wudeng on 2017/9/18.
 */

// 导入必要的类和接口
public class SelectLocActivity extends BaseActivity implements AMapLocationListener,
        PoiSearch.OnPoiSearchListener, AMap.OnCameraChangeListener {

    // 定义一个标签用于日志
    private static final String TAG = SelectLocActivity.class.getSimpleName();

    // 使用ButterKnife绑定地图视图
    @BindView(R.id.map_view)
    MapView mMapView;
    // 使用ButterKnife绑定地点列表的RecyclerView
    @BindView(R.id.rcv_poi_list)
    RecyclerView mRecyclerView;
    // 使用ButterKnife绑定返回按钮的ImageView
    @BindView(R.id.iv_back_btn)
    ImageView mIvBack;
    // 使用ButterKnife绑定搜索按钮的ImageView
    @BindView(R.id.iv_search)
    ImageView mIvSearch;
    // 使用ButterKnife绑定标题的TextView
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    // 使用ButterKnife绑定发送按钮的TextView
    @BindView(R.id.tv_btn_send)
    TextView mTvSend;
    // 使用ButterKnife绑定进度条的ProgressBar
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    // 使用ButterKnife绑定选中点的ImageView
    @BindView(R.id.iv_selected_point)
    ImageView mIvCenter;
//    这段代码声明了一个名为SelectLocActivity的类，它继承自BaseActivity并实现了AMapLocationListener、PoiSearch.OnPoiSearchListener和AMap.OnCameraChangeListener这些接口。这个类用于显示地图以供用户选择位置，它包括地图视图、地点列表、按钮和进度条等元素的绑定，以便在界面中进行操作。

    // 创建高德地图对象
    private AMap mAMap;

    // 创建高德定位客户端
    private AMapLocationClient mLocationClient;

    // 创建高德定位结果对象
    private AMapLocation mLocation;

    // 创建地图中心点的经纬度对象
    private LatLng mCenterPoint;

    // 创建标记选项对象，用于在地图上添加标记
    private MarkerOptions mLocationMarker;

    // 创建线性布局管理器，用于管理 RecyclerView 的布局
    private LinearLayoutManager mLayoutManager;

    // 创建存储地点坐标的列表
    private List<LocationPoint> mPointList;

    // 创建 RecyclerView 适配器，用于显示地点列表
    private RecycleViewAdapter<LocationPoint> mAdapter;

    // 初始化当前选中的地点索引，默认为 -1（表示未选中任何地点）
    private int mCurrentSelect = -1;

    // 设置地图的缩放级别，默认为 16.0f
    private float mZoomLevel = 16.0f;
//    这段代码的目的是声明一些变量和对象，用于管理和显示地图、定位信息以及地点列表。它包括了高德地图对象、定位客户端、定位结果对象、地图中心点坐标、地图标记选项、布局管理器、地点坐标列表、RecyclerView 适配器和一些其他配置参数。这些变量和对象在地图应用中用于显示地图、管理定位信息、以及展示地点信息。

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置状态栏颜色
        setStatusBarColor(R.color.app_blue_color);

        // 设置当前活动的布局文件
        setContentView(R.layout.activity_select_location);

        // 使用ButterKnife绑定视图
        ButterKnife.bind(this);

        // 创建地图视图并在其中执行生命周期方法
        mMapView.onCreate(savedInstanceState);

        // 初始化兴趣点（POI）列表
        initPoiList();

        // 初始化地图
        initMap();
    }
//    这段代码在 Android 活动的 onCreate 方法中执行以下操作：
//
//    调用父类的 onCreate 方法。
//    设置状态栏的颜色。
//    使用指定的布局文件设置当前活动的用户界面。
//    使用 ButterKnife 库绑定视图。
//    创建一个地图视图并调用其 onCreate 方法，以处理地图视图的生命周期。
//    调用 initPoiList 方法来初始化兴趣点（POI）列表。
//    调用 initMap 方法来初始化地图。

    // 初始化地点列表
    private void initPoiList(){
        // 创建线性布局管理器用于 RecyclerView
        mLayoutManager = new LinearLayoutManager(this);

        // 将线性布局管理器设置给 RecyclerView
        mRecyclerView.setLayoutManager(mLayoutManager);

        // 初始化地点列表
        mPointList = new ArrayList<>();

        // 创建适配器并设置其布局
        mAdapter = new RecycleViewAdapter<LocationPoint>(this, mPointList) {
            @Override
            public int setItemLayoutId(int position) {
                return R.layout.item_round_poi;
            }

            @Override
            public void bindView(RViewHolder holder, int position) {
                LocationPoint point = mPointList.get(position);

                // 设置地点名称和地址
                holder.setText(R.id.tv_poi_name, point.getName());
                holder.setText(R.id.tv_poi_address, point.getAddress());

                // 根据是否选中设置可见性
                if (point.isSelected()) {
                    holder.getImageView(R.id.iv_selected).setVisibility(View.VISIBLE);
                } else {
                    holder.getImageView(R.id.iv_selected).setVisibility(View.INVISIBLE);
                }
            }
        };
        // 设置适配器的点击监听器
        mAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RViewHolder holder, int position) {

                // 清除原来已选的
                mPointList.get(mCurrentSelect).setSelected(false);
                mAdapter.notifyItemChanged(mCurrentSelect);

                // 更新当前已选点
                mCurrentSelect = position;
                mPointList.get(position).setSelected(true);
                mAdapter.notifyItemChanged(position);

                // 刷新地图
                LatLonPoint point = mPointList.get(position).getPoint();
                setMapCenter(point.getLatitude(),point.getLongitude());
            }
        });
// 将适配器设置给 RecyclerView
        mRecyclerView.setAdapter(mAdapter);
    }
//    这段代码的主要目的是初始化一个包含地点列表的 RecyclerView。初始化包括设置 RecyclerView 的布局管理器、创建适配器并绑定数据，以及设置点击监听器以响应用户对地点的点击。根据用户的选择，可视化选中的地点。最后，根据点击的地点在地图上设置中心。

    // 初始化地图
    private void initMap(){

        // 获取地图实例
        mAMap = mMapView.getMap();

        // 设置地图的中心位置，这里初始化为广州市中心
        setMapCenter(23.13023, 113.253171);

        // 监听地图中心位置的变化
        mAMap.setOnCameraChangeListener(this);

        // 设置缩放控件
        UiSettings settings = mAMap.getUiSettings();
        settings.setZoomGesturesEnabled(true);

        // 禁用“我的位置”按钮
        settings.setMyLocationButtonEnabled(false);

        // 禁用缩放控件
        settings.setZoomControlsEnabled(false);

        // 创建定位客户端
        mLocationClient = new AMapLocationClient(this);

        // 配置定位选项
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setGpsFirst(false);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 单次定位
        option.setOnceLocation(true);

        // 应用配置到定位客户端
        mLocationClient.setLocationOption(option);

        // 设置定位监听器
        mLocationClient.setLocationListener(this);

        // 启动定位
        mLocationClient.startLocation();
    }
//    这段代码用于初始化地图，并配置地图的一些设置，以便在应用中使用。主要包括以下功能：
//
//    获取地图实例 (mAMap = mMapView.getMap();)，以便在应用中控制和显示地图。
//    设置地图的中心位置，这里初始化为广州市中心 (setMapCenter(23.13023, 113.253171);)。
//    监听地图中心位置的变化 (mAMap.setOnCameraChangeListener(this);)，以便在地图中心发生变化时执行相应的操作。
//    配置地图的缩放控件和禁用“我的位置”按钮以及缩放控件。
//    创建并配置定位客户端 (AMapLocationClient)，设置定位选项，包括定位模式和定位方式。
//    设置定位监听器 (mLocationClient.setLocationListener(this);)，以便获取定位信息。
//    启动定位服务 (mLocationClient.startLocation();)，开始定位操作。
//    这段代码的目的是在地图上显示位置信息，实现地图的初始化和定位功能。

    /**地图镜头被移动*****/

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        // 此方法是OnCameraChangeListener接口的回调方法
        // 当地图的摄像机位置发生变化时，将调用这个方法

        // 参数cameraPosition包含有关新摄像机位置的信息，如目标、缩放级别和倾斜度

        // 在此方法中，你可以处理地图摄像机位置变化的逻辑
        // 但是在这个示例中，方法体为空，没有具体的逻辑操作
        // 如果需要在摄像机位置变化时执行特定操作，可以在这个方法中编写相应的代码
    }
//    这段代码是Android应用中的一个方法，它实现了OnCameraChangeListener接口中的onCameraChange方法。当地图的摄像机位置发生变化时，这个方法会被调用。在这个示例中，该方法还没有具体的操作，它仅是一个占位符，如果需要在摄像机位置变化时执行特定的操作，你可以在这个方法中添加相应的代码逻辑。

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        // 当摄像头状态变化结束时触发的回调函数

        // 获取摄像头缩放级别
        mZoomLevel = cameraPosition.zoom;

        // 判断是否已经定位成功
        if (mLocation != null){
            // 若移动后，与原中心点距离超过10米，刷新 POI 列表

            // 计算新的地图中心点与原中心点之间的距离
            float distance = AMapUtils.
                    calculateLineDistance(mCenterPoint, cameraPosition.target);

            // 如果距离超过10米，执行以下操作
            if (distance >= 10.0f){
                // 重新设置地图中心点
                setMapCenter(cameraPosition.target.latitude, cameraPosition.target.longitude);

                // 搜索周围的兴趣点（POI）并刷新列表
                searchRoundPoi(new LatLonPoint(cameraPosition.target.latitude,
                        cameraPosition.target.longitude));
            }
        }
    }
//    这段代码是在地图摄像头状态变化结束后触发的回调函数。它用于检测地图的缩放级别和中心点，如果移动后与原中心点距离超过10米，则会刷新附近的兴趣点（POI）列表。这可以确保在地图浏览时，当用户浏览到新区域时会更新POI数据以供显示。


    /**我的位置发生移动**/
    @Override
    public void onLocationChanged(AMapLocation location) {
        // 检查位置对象是否为null
        if (location != null) {
            // 检查位置获取是否成功，错误码为0表示成功
            if (location.getErrorCode() == 0) {
                // 保存当前定位点的位置信息
                mLocation = location;
                // 将地图中心设置为当前定位点的经纬度
                setMapCenter(location.getLatitude(), location.getLongitude());
                // 在地图上显示位置点
                drawLocationPoint();

                // 搜索并显示周边POI（Point of Interest）点列表
                searchRoundPoi(new LatLonPoint(location.getLatitude(), location.getLongitude()));
            }
        }
    }
//    这段代码是一个Android地图应用中的回调方法，当设备的位置发生变化时调用。它的作用是获取设备当前的位置信息，并在地图上显示该位置点。具体的功能包括：
//
//    检查location对象是否为空，确保已成功获取位置信息。
//
//    检查location对象中的错误码，如果为0表示定位成功，继续处理。
//
//    将当前的定位信息保存到mLocation对象中。
//
//    将地图的中心设置为当前位置的经度和纬度，以确保地图视图显示在当前位置。
//
//    在地图上绘制当前位置点，以便用户能够看到自己的位置。
//
//    调用searchRoundPoi方法搜索并显示周边POI点列表，searchRoundPoi方法的参数是当前位置的经纬度信息。这可以用于查找附近的地点兴趣点。

    private void setMapCenter(double latitude, double longitude) {
        // 设置地图中心点坐标，根据传入的纬度和经度
        mCenterPoint = new LatLng(latitude, longitude);

// 创建一个用于移动地图视图的相机更新对象
// 这里使用`CameraUpdateFactory.newLatLngZoom`来指定新的中心点和缩放级别
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCenterPoint, mZoomLevel);

// 使用相机更新对象来平滑地将地图移动到新的中心点
        mAMap.animateCamera(cameraUpdate);

    }
//    这段代码的目的是在地图上设置新的中心点，然后平滑地将地图视图移动到该中心点位置。首先，通过传入的经度和纬度创建一个LatLng对象，表示新的中心点坐标。接下来，通过CameraUpdateFactory.newLatLngZoom方法创建一个相机更新对象，指定了新的中心点和缩放级别。最后，使用animateCamera方法将地图平滑地移动到指定的中心点位置。

    private void drawLocationPoint(){
        // 创建一个名为 drawLocationPoint 的方法，用于在地图上绘制用户当前位置的点。

// 创建一个 MarkerOptions 对象 mLocationMarker，用于配置位置标记的各种属性。
        mLocationMarker = new MarkerOptions();

// 从资源文件中获取一个 BitmapDescriptor 对象 descriptor，用于表示位置标记的图标。
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_loc);

// 设置 mLocationMarker 的图标为上面获取的图标 descriptor。
        mLocationMarker.icon(descriptor);

// 设置 mLocationMarker 的位置为预定义的中心点 mCenterPoint。
        mLocationMarker.position(mCenterPoint);

// 将 mLocationMarker 添加到 AMap 对象（高德地图）上，从而在地图上显示位置标记。
        mAMap.addMarker(mLocationMarker);

    }
//    这段代码的目的是在地图上绘制用户的当前位置标记。它首先创建一个 MarkerOptions 对象，然后设置该标记的图标为从资源文件中获取的图标（通常是用户位置的图标），最后指定标记的位置并将其添加到地图上。

    private void searchRoundPoi(LatLonPoint point) {
        // 显示进度条
        mProgressBar.setVisibility(View.VISIBLE);

        // 清除原有数据
        mPointList.clear();
        mAdapter.notifyDataSetChanged();

        // 创建一个 PoiSearch 查询对象，设置关键字、类型、和区域（城市代码）
        PoiSearch.Query query = new PoiSearch.Query("", "", mLocation.getCityCode());
        query.setPageNum(1);
        query.setPageSize(50);

        // 创建一个 PoiSearch 对象，设置查询参数和搜索范围（1000米以内）
        PoiSearch search = new PoiSearch(this, query);
        PoiSearch.SearchBound bound = new PoiSearch.SearchBound(point, 1000);
        search.setBound(bound);

        // 设置 PoiSearch 监听器为当前活动（this），并异步执行 POI 搜索
        search.setOnPoiSearchListener(this);
        search.searchPOIAsyn();
    }
//    这段代码的目的是执行一个地理位置的周边兴趣点（POI）搜索。它首先显示一个进度条以指示搜索正在进行，然后清除先前的搜索结果。接下来，它创建一个 PoiSearch 查询对象，设置关键字、类型和城市区域，然后创建一个 PoiSearch 对象，设置查询参数和搜索范围。最后，它将当前活动设置为 PoiSearch 监听器，并启动异步 POI 搜索。一旦搜索完成，将通过 PoiSearchListener 处理搜索结果。

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (!poiResult.getPois().isEmpty()) {
            // 如果POI搜索结果非空

            // 创建一个用于存储位置点的列表
            List<LocationPoint> points = new ArrayList<>(poiResult.getPois().size());
            LocationPoint point;

            // 遍历POI搜索结果中的每个POI项
            for (PoiItem poiItem : poiResult.getPois()) {
                // 创建一个新的位置点对象
                point = new LocationPoint();

                // 设置位置点的ID
                point.setId(poiItem.getPoiId());

                // 设置位置点的名称
                point.setName(poiItem.getTitle());

                // 设置位置点的经纬度坐标
                point.setPoint(poiItem.getLatLonPoint());

                // 设置位置点未选中状态
                point.setSelected(false);

                // 构建地址字符串，包括省份、城市、区域和详情
                String address = poiItem.getProvinceName()
                        + poiItem.getCityName()
                        + poiItem.getAdName()
                        + poiItem.getSnippet();

                // 设置位置点的地址
                point.setAddress(address);

                // 将位置点添加到列表中
                points.add(point);
            }

            mCurrentSelect = 0;
            // 默认选择第一项
            points.get(0).setSelected(true);

            // 将所有位置点添加到位置点列表中
            mPointList.addAll(points);

            // 通知适配器数据已更改
            mAdapter.notifyDataSetChanged();

            // 隐藏进度条
            mProgressBar.setVisibility(View.GONE);
        }
    }
//    这段代码处理POI（兴趣点）搜索结果。当POI搜索返回非空结果时，它将每个POI项提取并构建成一个LocationPoint对象，并将它们添加到mPointList列表中。然后，默认选择第一个位置点，通知适配器数据已更改以更新UI，并隐藏进度条。这通常用于地图应用中的位置搜索功能。

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        // 当兴趣点搜索完成时触发的回调方法
        // 这里可以添加处理搜索结果的逻辑，但在这个示例中，该方法没有具体实现
    }

    @OnClick(R.id.iv_back_btn)
    public void backOnClick(){
        // 当返回按钮被点击时触发的方法
        this.finish(); // 结束当前 Activity
    }

    @OnClick(R.id.tv_btn_send)
    public void send(){
        // 当发送按钮被点击时触发的方法
        if (!mPointList.isEmpty() && mCurrentSelect != -1){
            // 检查位置列表非空且已选择了位置
            Intent intent = new Intent();
            intent.putExtra("location",mPointList.get(mCurrentSelect).getPoint());
            intent.putExtra("address",mPointList.get(mCurrentSelect).getAddress());
            // 创建一个意图，附带位置和地址信息
            setResult(RESULT_OK,intent); // 设置结果代码和附带的数据
            finish(); // 结束当前 Activity
        } else {
            ToastUtils.showMessage(this,"位置获取失败，请稍后再试~");
            // 在没有选中位置或位置列表为空时显示提示消息
        }
    }
//    这段代码包含了三个方法：
//
//    onPoiItemSearched: 这是兴趣点搜索完成后触发的回调方法。在这个示例中，它并没有具体的实现，但通常用于处理兴趣点搜索的结果。
//
//    backOnClick: 当返回按钮被点击时触发的方法。它结束当前的 Activity。
//
//    send: 当发送按钮被点击时触发的方法。它检查位置列表是否非空且是否已选择了位置。如果满足条件，它创建一个包含位置和地址信息的意图，设置结果代码，并结束当前 Activity。如果不满足条件，它显示一个提示消息。

    // 在Activity的生命周期中的onResume()方法中
    @Override
    protected void onResume() {
        super.onResume();

        // 调用MapView的onResume()方法
        mMapView.onResume();
    }
//    这段代码的目的是在Android应用的生命周期中的onResume()方法中，通过调用mMapView的onResume()方法来恢复地图的显示。这通常用于确保地图在应用返回前台时能够正确刷新和显示。

    @Override
    protected void onPause() {
        super.onPause();
        // 调用父类的暂停方法，执行通用的暂停操作。

        mMapView.onPause();
        // 暂停地图视图，释放相关资源以减少内存占用。

        if (mLocationClient.isStarted()){
            mLocationClient.stopLocation();
        }
        // 如果定位客户端已启动，则停止位置信息的获取。
    }
//    这段代码是在 Android 应用中的生命周期方法 onPause 中的重写代码。在此方法中，执行以下操作：
//
//    首先，调用父类的 onPause 方法，以确保执行一些通用的暂停操作，这些操作可能包括停止动画或处理其他暂停相关的任务。
//
//    然后，调用 mMapView.onPause() 方法，暂停地图视图的运行，释放相关资源，从而降低应用的内存占用。
//
//    最后，检查定位客户端 mLocationClient 是否已经启动，如果是，则调用 mLocationClient.stopLocation() 方法停止获取位置信息。这可能是应用中的定位功能，确保在暂停时停止定位以节省电量和资源。
//
//    总之，该代码段用于在用户暂停应用或离开地图页面时执行必要的资源释放和操作以提高应用的性能和资源管理。

    // 当 Activity 销毁时，执行以下操作
    @Override
    protected void onDestroy() {
        super.onDestroy(); // 调用父类的 onDestroy 方法以执行必要的清理工作

        mMapView.onDestroy(); // 销毁地图视图，释放与地图相关的资源
        mLocationClient.onDestroy(); // 销毁定位客户端，释放与定位相关的资源
    }
//    这段代码位于 onDestroy 方法中，用于在 Android Activity 被销毁时进行资源清理。具体操作如下：
//
//            super.onDestroy() 调用父类 onDestroy 方法以执行父类可能需要的清理操作。
//
//            mMapView.onDestroy() 是针对地图的清理操作。如果应用中使用了地图控件（如百度地图、高德地图等），需要在 Activity 销毁时调用 onDestroy 方法，以确保地图资源得到释放。
//
//            mLocationClient.onDestroy() 是针对定位服务的清理操作。如果应用中使用了定位服务，通常需要在 Activity 销毁时调用 onDestroy 方法，以释放与定位相关的资源。
//
//    这是良好的编程实践，以确保在不再需要的资源时将其释放，以避免内存泄漏和性能问题。

}
