# R11-Android-SDK
## 项目各个模块支持gradle引入
**rbridge的导入**
```SHELL
implementation 'org.rainboweleven:rbridge:$versionName'
```
查看各模块最新$versionName版本到：https://jcenter.bintray.com/org/rainboweleven


#Rainboweleven Android

**使用指南（mac电脑）：**
* 1、下载源代码：[Rainboweleven安卓源码（最新分支）](https://github.com/chenshaomou/R11-Android-SDK/tree/development)，如果装了git，可以用git命令：git clone https://github.com/chenshaomou/R11-Android-SDK

**2、电脑安装java环境**
* 2-1、到[ java官网 ](http://www.oracle.com/technetwork/java/javase/downloads/index.html)下载JDK
![下载JDK](https://git.oschina.net/uploads/images/2017/0616/234934_ace215d6_703215.png "下载JDK")
* 2-2、下载后安装![安装JDK](https://git.oschina.net/uploads/images/2017/0616/235038_f7883897_703215.png "JDK")
* 2-3、到终端查看下配置是否生效：![到终端查看下配置是否生效](https://git.oschina.net/uploads/images/2017/0617/001945_274b8d07_703215.png "到终端查看下配置是否生效")

**3、电脑配置android SDK环境**
* 3-1、下载android-sdk-macosx.zip
* 3-2、将android-sdk-macosx.zip解压到一个指定目录
* 3-3、修改目录Rainboweleven/local.properties中的sdk.dir=为SDK解压的SDK目录路径

**4、电脑配置gradle编译环境**
* 4-1、到这里：[gradle官方下载地址](http://services.gradle.org/distributions)，选择4.1版本，即：[http://services.gradle.org/distributions/gradle-4.1-all.zip](http://services.gradle.org/distributions/gradle-4.1-all.zip)

* 4-2、下载完成解压到一个指定目录，然后到mac电脑用户名下找到.bash_profile文件，没有则新建一个，配置gradle环境变量
![配置gradle环境变量](https://git.oschina.net/uploads/images/2017/0617/001722_2a468f9e_703215.png "配置gradle环境变量")
* 4-3、到终端查看下配置是否生效：![到终端查看下配置是否生效](https://git.oschina.net/uploads/images/2017/0617/002417_b64d24ca_703215.png "到终端查看下配置是否生效")

**5、进入android项目目录（Rainboweleven文件夹中）**
* 5-1、运行调试版编译命令：gradle clean assembleDebug，测试版本编译命令：gradle clean assembleFortest，或发布版编译命令：gradle clean assembleRelease
* 5-2、生成的apk位于：Rainboweleven/app/build/outputs/apk

**6、修改编译目录**
* 6-1、修改打包html项目路径，修改文件Rainboweleven/gradle.properties中的字段htmlInputPath（注：路径不要含有中文命名）
* 6-2、修改输出目录路径，修改文件Rainboweleven/gradle.properties中的字段apkOutputPath


SDK文档和iOS项目参考：https://github.com/chenshaomou/R11-iOS-SDK


**JsBridge的使用：**
**loadLocalURL（加载本地页面）**
```SHELL
JsBridge.getInstance().loadLocalURL(webView, path, null);
```
**loadRemoteURL（加载远程页面）**
```SHELL
JsBridge.getInstance().loadRemoteURL(webView, path, null);
```

**register（原生插件注册给JS用）**
```SHELL
// 注册获取APP版本信息插件
AppInfoPlugin appInfoPlugin = new AppInfoPlugin(this);
JsBridge.getInstance().register(webView, AppInfoPlugin.MODULE_NAME, AppInfoPlugin.METHOD_VERSION, appInfoPlugin);
```

**call（执行JS上的插件/方法）**
```SHELL
JsBridge.getInstance().call(webView, null, "testNavCall", params, new OnCallJsResultListener() {
@Override
public void onCallJsResult(String result) {
// 执行结果
Log.e("wlf", "执行结果：result：" + result);
}
});
```

**on（监听整个系统的事件(含H5的事件)）**
```SHELL
JsBridge.getInstance().on(context, "domLoadFinish", new EventObserver() {
@Override
public void onObserver(String eventName, String params) {
// H5加载完成，隐藏loading框
}
});
```

**off（解除监听整个系统的事件(含H5的事件)）**
```SHELL
JsBridge.getInstance().on(context, "domLoadFinish", new EventObserver() {
@Override
public void onObserver(String eventName, String params) {
// H5加载完成，隐藏loading框
}
});
```

**send（发送一个事件给整个系统的事件(含H5的事件)）**
```SHELL
JsBridge.getInstance().send(context, "onPayFinish", "{'orderNO':'11931398'}");
```



