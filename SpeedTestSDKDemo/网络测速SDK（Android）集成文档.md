# Android SDK集成
本文为你介绍了Android端集成SDK操作，帮助你快速集成SDK并能使用测速基本功能。
# 前提条件
开发前的环境要求如下表所示：

| **类别** | **说明** |
| --- | --- |
| 系统版本 | 支持Android 4.1及以上 |
| CPU架构 | 支持真机架构armeabi、armeabi-v7a、arm64-v8a |
| 开发软件 | 确保使用Android Studio进行开发 |

# 集成SDK
## 添加依赖

1. 你需要**下载SDK**，下载链接请参见[SDK下载](https://b-api.speedtest.cn/sdkVersion/download?sdkName=speedSdk&os=Android&file=sdk)。解压后的文件需**导入到Android Studio工程libs文件下**，文件类型和路径如下表所示：

| **文件名称** | **文件路径** |
| --- | --- |
| speedtest-sdk.aar | /app/libs/ |

2. 在项目的 **/app/build.gradle文件**中，添加如下行：

```java
dependencies {
      ...
  //依赖的网络测速SDK
  implementation files('libs\\speedtest-cn-sdk.aar')
}
```

## 添加所需的第三方包
如果项目中已包含以下包可以不进行添加，版本号可以按照自己项目而定。

```java
	// gson
    implementation 'com.google.code.gson:gson:2.8.5'

    // okhttp
    implementation 'com.squareup.okhttp3:okhttp:3.9.1'

    // retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.3.0'

    // converter
    implementation 'com.squareup.retrofit2:converter-gson:2.3.0'

    // rx - adapter
    implementation 'com.squareup.retrofit2:adapter-rxjava2:2.3.0'

    //rx
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'
    implementation 'io.reactivex.rxjava2:rxjava:2.2.10'

    // 不是androidx依赖的添加这个
    implementation 'com.android.support:recyclerview-v7:27.0.0-alpha1'

    // 是androidx依赖的添加这个
    implementation 'androidx.recyclerview:recyclerview:1.1.0'
```

## 代码同步
点击**Sync Now**，进行代码同步。


## 防止代码混淆（可选）
如果你的应用设置了混淆配置，需要进行以下配置。在**proguard-rules.pro**文件中，添加 **-keep** 类的配置，这样可以防止混淆**AliRtcSDK**公共类名称。

```java
#↓↓↓↓↓↓↓实体类↓↓↓↓↓↓↓
-keep class com.speedmanager.speedtest_api.http.bean.** { *; }
-keep class * implements com.speedmanager.baseapp.IBean {
  public static final android.os.Parcelable$Creator *;
}
```

## Android P/Android 9.0版本适配
由于 Android P 限制了明文流量的网络请求，非加密的流量请求都会被系统禁止掉，所以需要再进行适配。适配方案
如下： 在**res**下新建一个**xml**目录，然后创建一个**xml**文件，命名为**network_security_config.xml**，该文件内容如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
  <network-security-config>
      <base-config cleartextTrafficPermitted="true" />
  </network-security-config>
```

然后在AndroidManifest.xml application标签内应用上面的xml配置：

```xml
android:networkSecurityConfig="@xml/network_security_config"
```

# 功能使用

## Android UI版
含有用户操作界面的版本。

### \- 加入测速页面

在所需页面的布局文件中加入测速页面：

```xml
<fragment
android:id="@+id/fragment"
class="cn.speedtest.speedtest_sdk.SpeedtestFragment"
android:layout_width="match_parent"
android:layout_height="match_parent" />
```

### \- 配置测速单位
测速单位为非必须配置项，默认为**Mbps**，可选值：**Mbps、KB/s、MB/s**，示例代码如下：

```xml
// unitStr取值：Mbps，MB/s，KB/s
SpeedtestInterface.setUnit(Context context, String unitStr);
```

## 高级功能

### \- 数据接收
定义广播接收器接收数据，详细数据如下表：

| **参数** | **说明** | **类型** | **可选值** | **默认值** |
| --- | --- | --- | --- | --- |
| ping | 时延（单位ms） | double | - | 0 |
| jitter | 抖动（单位ms） | double | - | 0 |
| pckLoss | 丢包（单位%） | double | - | 0 |
| upload | 上传速度 | double | - | 0 |
| download | 下载速度 | double | - | 0 |
| wifiName | wifi名称 | String | - | - |

```xml
 // 定义广播接收器接收数据
BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
  @Override
  public void onReceive(Context context, Intent intent) {
      double download = intent.getDoubleExtra("download", 0);
      double upload = intent.getDoubleExtra("upload", 0);
      double ping = intent.getDoubleExtra("ping", 0);
      double jitter = intent.getDoubleExtra("jitter", 0);
      double pckLoss = intent.getDoubleExtra("pckLoss", 0);
      String wifiName = intent.getStringExtra("wifiName");

      Log.d(TAG, "onReceive: download -> " + download);
      Log.d(TAG, "onReceive: upload -> " + upload);
      Log.d(TAG, "onReceive: ping -> " + ping);
      Log.d(TAG, "onReceive: jitter -> " + jitter);
      Log.d(TAG, "onReceive: pckLoss -> " + pckLoss);
      Log.d(TAG, "onReceive: wifiName -> " + wifiName);
  }
};
// 注册广播接收器
IntentFilter intentFilter = new IntentFilter("cn.speedtest.sdk");
registerReceiver(mBroadcastReceiver, intentFilter);
// 注销广播接收器
unregisterReceiver(mBroadcastReceiver);
```

### \- 自定义界面
在项目下的colors资源文件中定义如下资源：

```xml
顶部Logo定制：
// 参数，传入图片资源id，-1为隐藏Logo
SpeedtestInterface.setLogoResId();
<resources>
              <!--    页面背景颜色-->
  <color name="sp_color_bg_primary">#E1F5FE</color>
              <!--    弹窗背景颜色-->
  <color name="sp_bg_dialog_main">#E1F5FE</color>
              <!--    三级文字颜色-->
  <color name="sp_txt_auxiliary">#313133</color>
              <!--    二级文字颜色-->
  <color name="sp_txt_subtitle">#313133</color>
              <!--    一级文字颜色-->
  <color name="sp_txt_title">#141414</color>
              <!--    分割线颜色-->
  <color name="sp_divider">#ADAEB8</color>
              <!--    toolbar图标颜色-->
  <color name="sp_ic_toolbar_icon">#141414</color>
              <!--    活跃按钮文本颜色-->
  <color name="sp_colorAccent">#F48FB1</color>
              <!--    表盘背景颜色-->
  <color name="sp_color_dv_bg_color">#ADAEB8</color>
              <!--    主下载色-->
  <color name="sp_main_download">#F06292</color>
              <!--    主上传色-->
  <color name="sp_main_upload">#FF8A65</color>
              <!--    测速表格线颜色-->
  <color name="sp_speed_grid_color">#C5C4C4</color>
  </resources>
```

## Android 接口版
不含有用户操作界面的版本。

### \- SDK初始化
执行初始化需要使用开发者申请应用得到**appId**和**key**，在**Application**或者主**Activity**（推荐在Application）中加入以下初始化代码：

```java
SpeedtestInterface.init(this, SDKConfig.APP_ID, SDKConfig.APP_KEY);
```

**appId** 和 **key** 是申请的应用的唯一标识，[点击获取](/products/)

### \- 获取全局对象
**SpeedtestInterface**初始化，获取全局**SpeedtestInterface**对象：

```java
SpeedtestInterface.getSDK(Context context)
```

### \- 配置测速阈值（可选）
**SpeedtestInterface**配置测速阈值（**holdValue**）、是否自动重新测速（**autoDown**）、快速测速（**fastSpeed**）、是否缓存当前测速节点（**enableNodeCache**）；
- 阈值单位为**Mbps**，不设置默认为**0Mbps**，设置**holdValue**后若测速结果小于holdValue会自动切换节点，节点在下次测速时生效。
- 若同时设置**holdValue**和**autoDown**，在测速结果小于holdValue时自动切换节点并自动重新测速，自动重新测速次数最多为2次。
- 设置**fastSpeed**为**true**启用快速测速。
- 设置**enableNodeCache**为**true**启动缓存，开启后每次首选缓存的节点进行测速，请针对具体业务考虑是否开启，该功能默认关闭。

| **参数** | **说明** | **类型** | **可选值** | **默认值** |
| --- | --- | --- | --- | --- |
| holdValue | 阈值(Mbps)	 | double | - | 0 |
| autoDown | 是否自动重新测速 | boolean | true/false | false |
| fastSpeed | 快速测速 | boolean | true/false | false |
| enableNodeCache | 是否缓存当前测速节点 | boolean | true/false | false |

```java
SpeedtestInterface.getSDK(Context context)
.holdValue(double holdValue).autoDown(boolean autoDown).fastSpeed(boolean fastSpeed);
```

### \- 测速接口定义
测速接口**startSpeedTest**调用内部会执行**Ping**、下载以及上传，执行获取的数据通过回调返回给接口调用者，接口定义如下：

```java
public void startSpeedTest(
  final PingCallback pingCallback,
  final SpeedtestCallback speedDownloadCallback,
  final SpeedtestCallback speedUploadCallback,
  final SpeedUnit unit) {
      ...
  }
```

### \- 测速接口调用

```java
SpeedtestInterface.getSDK(context).startSpeedTest(
  pingCallback, downCallback, upCallback, SpeedUnit.MBps
)
```

```java
private PingCallback pingCallback = new PingCallback() {
  @Override
  public void onResult(PingResultData pingResultData) {
      LogUtil.d("Ping Result:---------------------"+"\n");
      LogUtil.d(pingResultData.toString());
  }
  @Override
  public void onError(Throwable throwable) {
      LogUtil.d("Ping Occur Error:---------------------"+"\n");
  }
  //v2.4.0新增，将丢包计算的回调单独出来
  @Override
  public void onPckLoss(double pckLoss) {
      LogUtil.d("Ping pckLoss Value:" + pckLoss +"\n");
  }
};
```

```java
private SpeedtestCallback downloadCallback = new SpeedtestCallback() {
  @Override
  public void onStart() {
      LogUtil.d("Start SpeedTest Download:---------------------"+"\n");
  }
  @Override
  public void onProcess(double mbps) {
      LogUtil.d(mbps + "\n");
  }
  @Override
  public void onEnd(double mbps) {
      LogUtil.d("End SpeedTest Download:---------------------"+"\n");
  }
  @Override
  public void onError(Throwable throwable) {
      LogUtil.d("SpeedTest Download Occur Error:---------------------"+"\n");
  }
};
```

上传与下载回调方法一样，只是参数顺序下载在前，上传在后，第四个参数为测速量程，SpeedUnit枚举类包含Mbitps、MBps、KBps；

### \- 获取测速节点列表
测速节点列表接口参数为回调**callback**，根据节点距离当前位置的距离进行升序排列，默认返回15个测速节点，
具体使用见下:

```java
//接口定义
/**
   * @Description : 获取节点列表接口
   * @Params : callback
   */
public void getNodeList(final GetNodeListCallback callback) {
  ...
  }
// 调用示例
SpeedtestInterface.getSDK(context).getNodeList(new GetNodeListCallback() {
  //请求默认返回15个节点
  @Override
  public void onResult(List<NodeListBean> listBeans) {
    if (listBeans == null || listBeans.size() == 0) {
        return;
    }
    mNodeListBeans = listBeans;
    ...
  }
```

```java
  //节点对象
  public class NodeListBean {
      private int id;//节点id
      private String sponsor;//节点名称
      private String operator;//运营商
      private String city;//城市
      public int getId() {
          return id;
      }
      public String getSponsor() {
          return sponsor;
      }
      ...
      }
```

### \- 设置为当前选中节点
从测速节点列表中选择了某一个测速节点，需要将其设置为当前选择的节点：

```java
//接口调用，通过设置节点id进行配置
SpeedtestInterface.getSDK(context).setSelectNode(mNodeListBean.getId);
//接口定义
/**
* @Description : 设置当前测速节点
* @Params :节点id
*/
public void setSelectNode(int nodeId) {
  ...
}
/**
* @Description : 获取当前测速节点
* @Params :
*/
public NodeListBean getSelectNode() {
  ...
}
```

### \- 释放资源
**abort()** 停止所有测速相关请求，释放资源：

```java
SpeedtestInterface.getSDK(context).abort();
```

### \- 错误回调
返回码汇总，因为测速启动第一步为**Ping**，所以相关错误回调在**pingCallback**的**onError**方法，通过 **throwable.getMessage()** 获取返回码。

错误回调编码规则:

| code | 异常情况 | 描述 |
| --- | --- | --- |
| 10000 | 正在校验中 | 正在校验中 |
| 10010 | 网络断开 | 网络断开 |
| 10020 | 签名异常 | 签名异常 |
| 10030 | 请求失败 | 请求失败 |
| 40601 | appId或key非法 | 请确认你的appId或key是否正确 |
| 40602 | 非法请求接口 | 没有使用权限 |
| 40603 | 当月测速次数已用完 | 你的当月测速次数已经用完，请联系测速网官方客服购买测速次数 |
| 40604 | 测速接口请求太频繁 | 3秒内只能启动一次测速 |
