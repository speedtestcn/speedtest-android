本文为你介绍了Android端集成SDK操作，帮助你快速集成SDK并能使用测速基本功能。

<a name="ZShsl"></a>
# 前提条件
开发前的环境要求如下表所示

| **类别** | **说明** |
| --- | --- |
| 系统版本 | 支持Android 4.4及以上 |
| CPU架构 | 支持真机架构armeabi、armeabi-v7a、arm64-v8a |
| 开发软件 | 确保使用Android Studio进行开发 |


<a name="rwrFc"></a>
# 集成SDK
<a name="yoyQZ"></a>
## 1、gradle集成

1. 添加仓库，在**project**级别的**build.gradle**文件中添加Maven引用，示例如下：
```
maven { url 'https://jitpack.io' } //如果app中已添加此处可忽略
maven {
        url 'https://repository.speedtest.cn/repository/speedtest-release/'
      }
```
添加依赖，在主**module**的**build.gradle**文件添加SDK依赖，示例如下：
```
dependencies {
     // 如果App主要针对国内用户，则依赖sdk-speedtest
     implementation 'cn.speedtest:sdk-speedtest:1.1.3'
     //如果App主要针对海外用户，则依赖sdk-speedtest-foreign
     implementation 'com.juqing.speedtest:sdk-speedtest-foreign:1.0.5'
}
```
<a name="Od9HU"></a>
## 2、下载离线aar包进行集成

1. 你需要下载SDK，下载链接请参见[SDK下载](https://b.speedtest.cn/speedtest-sdk)。解压后的文件需导入到Android Studio工程libs文件下，文件类型和路径如下表所示。

| 文件名称 | 文件路径 |
| --- | --- |
| speedtest_cn_sdk_1.1.3.aar | /app/libs/ |


2. 在项目的/app/build.gradle文件中，添加如下行：
```java
dependencies {   
        ...   
    //依赖的网络测速SDK  
    implementation files('libs\\speedtest-cn-sdk_1.1.3.aar')
}
```

3. 添加所需的第三方包

1）在**project**级别的**build.gradle**文件中添加Maven引用，示例如下：
```
maven { url 'https://jitpack.io' } //如果app中已添加此处可忽略
```
2）添加依赖
```
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

//reflection
implementation 'com.github.tiann:FreeReflection:3.1.0'

//OAID
implementation 'com.github.gzu-liyujiang:Android_CN_OAID:4.2.4'
```

<a name="VVclD"></a>
## 防止代码混淆
如果你启用了proguard代码混淆，需要在proguard-rules.pro文件中，添加`-keep`类的配置，这样可以防止部分实体类被混淆。
```java
 #↓↓↓↓↓↓↓实体类↓↓↓↓↓↓↓
-dontoptimize
-keep class com.speedtest.lib_api.http.bean.** { *; }
-keep class * implements com.speedtest.lib_bean.IBean {
  *;
}
```
<a name="z0TsY"></a>
## Android P/Android 9.0版本适配
由于 Android P 限制了明文流量的网络请求，非加密的流量请求都会被系统禁止掉，所以需要再进行适配。适配方案如下： 在 res 下新建一个 xml 目录，然后创建一个xml文件，命名为network_security_config.xml ，该文件内容如下：
```java
<?xml version="1.0" encoding="utf-8"?>
            <network-security-config>
                <base-config cleartextTrafficPermitted="true" />
            </network-security-config>
```

然后在 AndroidManifest.xml application 标签内应用上面的xml配置：
```java
android:networkSecurityConfig="@xml/network_security_config"
```
<a name="qI6In"></a>
# 功能使用
<a name="jjhaf"></a>
### 权限申请
功能使用前需要申请所需要的权限，以保证功能可以正常使用
| 权限 |
| --- |
| ACCESS_FINE_LOCATION |
| ACCESS_COARSE_LOCATION |
| READ_PHONE_STATE |
### SDK初始化
执行初始化需要使用开发者申请应用得到 `appId` 和 `key`，在Application或者主Activity中加入以下(推荐在Application中加入以下初始化代码，初始化不会执行任何耗时操作，不用担心影响App启动速度：
```java
SpeedtestInterface.init(this, SDKConfig.APP_ID, SDKConfig.APP_KEY);
```
> `appId` 和 `key`是申请的应用的唯一标识，[点击获取](#)


<a name="5XgqD"></a>
### 获取全局对象
SpeedInterface初始化，获取全局SpeedInterface对象：
```java
  SpeedInterface.getSDK(Context context)
```

<a name="ZQ3KI"></a>
### 测速接口定义
测速接口startSpeedTest调用内部会执行Ping、下载以及上传，执行获取的数据通过回调返回给接口调用者，接口定义如下：
```java
public void startSpeedTest(
    final PingCallback pingCallback,
    final SpeedtestCallback speedDownloadCallback,
    final SpeedtestCallback speedUploadCallback,
    final SpeedUnit unit) {
    ...
}
```
<a name="7BfOR"></a>
### 测速接口调用
```java
SpeedInterface.getSDK(context).startSpeedTest(
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
    public void onError(SdkThrowable throwable) {
        LogUtil.d("Ping Occur Error:---------------------"+"\n");
    }
    @Override
    public void onPckLoss(double pckLoss) {
        LogUtil.d("Ping pckLoss Value：" + pckLoss +"\n");
    }
    @Override
    public void onProcessState(SpeedtestState speedtestState) {
        LogUtil.d("ProcessState Value：" + speedtestState +"\n");
   }
};
```
测速过程状态对应描述信息如下:

| SpeedtestState | 描述 |
| --- | --- |
| SpeedtestState.SPEEDTEST_STATUS_AUTH | 权限校验 |
| SpeedtestState.SPEEDTEST_STATUS_SELECT_NODE | 选择节点 |
| SpeedtestState.SPEEDTEST_STATUS_PING | PING |
| SpeedtestState.SPEEDTEST_STATUS_DOWNLOAD | 下载测速 |
| SpeedtestState.SPEEDTEST_STATUS_UPLOAD | 上传测速 |
| SpeedtestState.SPEEDTEST_STATUS_END | 测速结束 |

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
    public void onError(SdkThrowable throwable) {
        LogUtil.d("SpeedTest Download Occur Error:---------------------"+"\n");
    }
};
```
> 上传与下载回调方法一样，只是参数顺序下载在前，上传在后，第四个参数为测速量程，SpeedUnit枚举类包含Mbitps、MBps、KBps；


<a name="VEh6H"></a>
### 获取测速节点列表
测速节点列表接口参数包括请求的页数（page）和回调callback，根据节点距离当前位置的距离进行升序排列，具体使用见下；
```java
//接口定义
/**
   * @Description : 获取节点列表接口
   * @Params : page, callback
   */
public void getNodeList(int page, final GetNodeListCallback callback) {
  ...
  }
// 调用示例
SpeedInterface.getSDK(context).getNodeList(1, new GetNodeListCallback() {
  // 根据分页数返回对应节点，每页默认15个节点
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
      private String distance;//距离
      public int getId() {
          return id;
      }
      public String getSponsor() {
          return sponsor;
      }
      ...
  }
```
<a name="gufmf"></a>
### 设置为当前选中节点
从测速节点列表中选择了某一个测速节点，需要将其设置为当前选择的节点：
```java
//接口调用，通过设置节点id进行配置
SpeedInterface.getSDK(context).setSelectNode(mNodeListBean.getId);
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

<a name="gufmf"></a>
### 获取Ip&运营商信息
通过getIpLocation接口，在GetIpInfoCallback回调获取Ip&运营商信息：
```java
//接口定义
/**
* @Description : 获取IP位置信息
* @Params : callback
*/
public void getIpLocation(final GetIpInfoCallback callback) {
  ...
}
//接口调用示例
SpeedInterface.getSDK(context).getIpLocation(new GetIpInfoCallback() {
  @Override
  public void onResult(LocationInfoBean locationInfoBean) {
    ...
  }
  @Override
  public void onError(SdkThrowable sdkThrowable) {
    ...
  }
});
```
```java
//Ip信息对象
  public class NodeListBean {
    private String ip; //外部ip
    private String country; //国家
    private String countryCode; //国家码
    private String province; //省
    private String city; //市
    private String district; //区
    private String isp; //运营商信息
    ...
  }
```
### 获取测速过程信息
通过getSpeedExtraData接口，在GetSpeedExtraCallback回调获取测速过程信息：
```java
//接口定义
/**
* @Description : 获取测速过程信息
* @Params : callback
*/
public void getIpLocation(final GetIpInfoCallback callback) {
  ...
}
//接口调用示例
SpeedInterface.getSDK(context).getSpeedExtraData(new GetSpeedExtraCallback() {
  @Override
  public void onResult(SpeedExtraData speedExtraData) {
    ...
  }
  @Override
  public void onError(SdkThrowable sdkThrowable) {
    ...
  }
});
```
```java
//测速过程信息对象
public class NodeListBean {
   private float downloadCrest; //下载速率峰值Mbps
   private float uploadCrest; //上传速率峰值Mbps
   private long minPing; //闲时时延最小值
   private long maxPing; //闲时时延最大值
   private long busyDownloadPing; //忙时时延（下载）
   private long busyUploadPing; //忙时时延（上传）
   private long busyDownloadPingMin; //忙时时延（下载）最小值
   private long busyUploadPingMin; //忙时时延（上传）最小值
   private long busyDownloadPingMax; //忙时时延（下载）最大值
   private long busyUploadPingMax; //忙时时延（上传）最大值
   private float busyDownloadJitter; //忙时抖动（下载）
   private float busyUploadJitter; //忙时抖动（上传）
    ...
}
```

<a name="c9u8x"></a>
### 释放资源
abort() 停止所有测速相关请求，释放资源：
```java
SpeedInterface.getSDK(context).abort();
```

<a name="Jn7ZJ"></a>
### 错误回调
返回码汇总，ping、下载和上传每个阶段的错误会回调到相应的onError方法中，根据回调的SdkThrowable可以大致判断错误原因，SdkThrowable定义如下：
```
public class SdkThrowable extends Throwable {
  public int code; //错误码
  public String msg;  //错误描述信息
}
```
错误码对应描述信息如下:

| code | 异常情况 | 描述 |
| --- | --- | --- |
| 10000 | 正在校验中 | 权限正在校验中 |
| 10010 | 网络断开 | 网络断开 |
| 10030 | 请求失败 | 请求失败 |
| 20010 | 获取测速节点失败 | 获取测速节点失败 |
| 20020 | 接口频繁调用 | 测速接口调用频率太高，3s内启动一次 |
| 20030 | 没有使用权限 | 没有使用权限 |
| 40601 | 请确认你的appId或Key是否正确 | 请确认你的appId或Key是否正确 |
| 40602 | 产品不存在 | 产品不存在 |
| 40603 | 测速次数不足 | 测速次数不足 |
| 40604 | 到期无法使用 | 到期无法使用 |
| 40610 | 仅限（省）地区使用 | 仅限（省）地区使用 |
| 40611 | 仅限（省市）地区使用 | 仅限（省市）地区使用 |
| 40612 | 仅限（运营商）用户使用 | 仅限（运营商）用户使用 |
| 40613 | 仅限XX.XX域名使用 | 仅限XX.XX域名使用 |
| 40614 | 仅限XX类型CPU使用 | 仅限XX类型CPU使用 |


<a name="2c6XJ"></a>
## Demo下载
示例Demo可前往github下载：[https://github.com/speedtestcn/speedtest-android](https://github.com/speedtestcn/speedtest-android)
