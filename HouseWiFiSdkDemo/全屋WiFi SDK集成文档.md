本文为你介绍了Android端集成SDK操作，帮助你快速集成SDK并能使用全屋WiFi基本功能。

sdk合规使用指引：https://b.speedtest.cn/compliance/

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
     implementation 'cn.speedtest:sdk-housewifi:1.0.3'
}
```
<a name="Od9HU"></a>
## 2、下载离线aar包进行集成

1. 你需要下载SDK，下载链接请参见[SDK下载](https://b.speedtest.cn/speedtest-sdk)。解压后的文件需导入到Android Studio工程libs文件下，文件类型和路径如下表所示。

| 文件名称 | 文件路径 |
| --- | --- |
| sdk-housewifi-1.0.3.aar | /app/libs/ |


2. 在项目的/app/build.gradle文件中，添加如下行：
```java
dependencies {   
        ...   
    //依赖的全屋WiFiSDK  
    implementation files('libs\\sdk-housewifi-1.0.3.aar')
}
```

3. 添加所需的第三方包

1）在**project**级别的**build.gradle**文件中添加Maven引用，示例如下：
```
maven { url 'https://jitpack.io' } //如果app中已添加此处可忽略
```
2）添加依赖
```
//recyclerview 如果app中已添加此处可忽略
implementation 'androidx.recyclerview:recyclerview:1.1.0'

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

//glide
implementation 'com.github.bumptech.glide:glide:4.11.0'

//smarttablayout
implementation 'com.ogaclejapan.smarttablayout:library:2.0.0@aar'
implementation 'com.ogaclejapan.smarttablayout:utils-v4:2.0.0@aar'

//flexbox
implementation 'com.google.android:flexbox:1.0.0'

//PickerView
implementation 'com.contrarywind:Android-PickerView:4.1.9'

//cropper
implementation 'com.theartofdev.edmodo:android-image-cropper:2.8.+'

//Luban
implementation 'top.zibin:Luban:1.1.8'
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
-keep class * implements com.contrarywind.interfaces.IPickerViewData {*;}
-keep class com.speedtest.housewifi_sdk.housewifi.recognize.JsonBean$* {*;}
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
### SDK初始化
执行初始化需要使用开发者申请应用得到 `appId` 和 `key`，在Application或者主Activity中加入以下(推荐在Application中加入以下初始化代码，初始化不会执行任何耗时操作，不用担心影响App启动速度：
```java
SpeedtestInterface.init(this, SDKConfig.APP_ID, SDKConfig.APP_KEY);
```
> `appId` 和 `key`是申请的应用的唯一标识，[点击获取](#)


### 布局引用
在Activity的布局文件中添加以下代码
```java
<fragment
        android:id="@+id/fragment"
        class="com.speedtest.housewifi_sdk.housewifi.help.HouseWifiHelpFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

<a name="2c6XJ"></a>
## Demo下载
示例Demo可前往github下载：[https://github.com/speedtestcn/speedtest-android](https://github.com/speedtestcn/speedtest-android)
