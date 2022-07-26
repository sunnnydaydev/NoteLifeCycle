# LifeCycle

Jetpack中的lifeCycle、viewModel、LiveData属于生命周期感知组件，这些组件能够很好的感知Activity/Fragment的生命周期变化更
有助于我们编写有条理、精简、易维护的代码。接下来看下第一个声明周期感知组件lifeCycle

###### 1、为啥要引入lifeCycle

首先看一个例子:

```kotlin
/**
 * Create by SunnyDay /07/20 21:29:31
 */
class MyLocationListener(private val context: Context,private val callback:(Location)->Unit) {

    fun start() {
        // connect to system location service
        // get current Location
        // feedback
        callback.invoke(Location(50F,50F))
    }

    fun stop() {
        // disconnect from system location service
    }

   data class Location(val x:Float,val y:Float)
}
```

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var myLocationListener: MyLocationListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //init and register callback
        myLocationListener = MyLocationListener(this){
            // todo update ui
            it.x
            it.y
        }
    }

    override fun onStart() {
        super.onStart()
        myLocationListener.start()
    }

    override fun onStop() {
        super.onStop()
        myLocationListener.stop()
    }
}
```

上述的代码，经过分析还是存在一些弊端的：

- Activity的生命周期回调中（onCreate/onStop/onDestroy）可能需要管理很多如上组件（MyLocationListener）。导致放置大量代码难以维护。
- 无法保证组件会在 Activity 或 Fragment 停止之前启动。正常来说activityA跳转ActivityB时生命周期为：activityB onStart 然后activityA onStop。假如activityB onStart有耗时操作时，可能会出现activityA onStop先结束情况。
- 在一个Activity中去感知它的生命周期非常简单，而如果要在一个非Activity的类中去感知Activity的生命周期，应该怎么办呢？我们就要像上面那样在Activity中做些逻辑处理。

lifeCycle优势： lifeCycle就是解决上述问题的。它可以让任何一个类都能轻松感知到Activity的生命周期，同时又不需要在Activity中编写大量的逻辑处理。

###### 2、依赖的引入

[官方文档](https://developer.android.google.cn/jetpack/androidx/releases/lifecycle#declaring_dependencies)

其实安卓支持库26.1.0开始已经默认引入了生命周期感知组件，若是我们想添加其他的依赖或者是修改使用的版本可以选取如下添加。

```groovy
    dependencies {
        val lifecycle_version = "2.6.0-alpha01"
        val arch_version = "2.1.0"

        // ViewModel
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
        // LiveData
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
        // Lifecycles only (without ViewModel or LiveData)
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")

        // Saved state module for ViewModel
        implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")
        // Annotation processor
        kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
    
        // alternately - if using Java8, use the following instead of lifecycle-compiler
        implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")

        // optional - helpers for implementing LifecycleOwner in a Service
        implementation("androidx.lifecycle:lifecycle-service:$lifecycle_version")
        // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
        implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")
        // optional - ReactiveStreams support for LiveData
        implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle_version")
        // ViewModel utilities for Compose
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    }
```
###### 3、简单使用#使用lifecycle解耦系统组件与普通组件

上面通过伪代码举了一个Location的栗子，接下来使用lifecycle改写下：

（1）定义

定义实现类，实现LifecycleObserver接口，这样我们自定义的实现类就能感知Activity生命周期了。

```kotlin
/**
 * Create by SunnyDay /07/21 21:01:08
 */
class MyLocationListenerWithLifecycle(
    private val context: Context,
    private val callback:(Location)->Unit
):LifecycleObserver {
    companion object{
      const val tag = "Lifecycle"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        Log.d(tag,"onStart")
        // connect to system location service
        // get current Location
        // feedback
        callback.invoke(Location(50F,50F))
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        Log.d(tag,"onStop")
        // disconnect from system location service
    }

    data class Location(val x:Float,val y:Float)
}
```

（2）使用

向Activity注册下监听即可

```kotlin
class SecActivity : AppCompatActivity() {
    
    private  val mLocationListenerWithLifecycle: MyLocationListenerWithLifecycle by lazy {
        MyLocationListenerWithLifecycle(this){
            // todo update ui
           it.x
           it.y
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sec)
        lifecycle.addObserver(mLocationListenerWithLifecycle)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mLocationListenerWithLifecycle)
    }
}

```
随着Activity生命周期的切换，这会打印出不同的log，可见相对之前来说简洁了些，不用在activity中不同生命周期里面去注册了。使用lifeCycle直接解耦了组件。
也即解耦了普通组件MyLocationListenerWithLifecycle与activity这个系统组件。

 (3) 小结

lifeCycle的使用也十分简单，一般分为三步：首先定义类实现LifecycleObserver，然后使用@OnLifecycleEvent注解给普通组件的方法绑定
到系统组件的对应生命周期上即可。最后为系统组件（lifeCycleOwner）注册普通组件（Observer）即可。


OnLifecycleEvent注解参数字段有很多，一般都对应这系统组件的生命周期的：

```kotlin
/**
 * Create by SunnyDay /07/20 22:08:38
 */
class MyObserver : LifecycleObserver {
    companion object {
        const val TAG = "MyObserver"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun activityCreate() {
        Log.i(TAG, "onCreate")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun activityStart() {
        Log.i(TAG, "onStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun activityResume() {
        Log.i(TAG, "onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun activityStop() {
        Log.i(TAG, "onStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun activityDestroy() {
        Log.i(TAG, "onDestroy")
    }

}
```

```kotlin
class SecActivity : AppCompatActivity() {
    private  val myObserver: MyObserver by lazy {
        MyObserver()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sec)
        lifecycle.addObserver(myObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(myObserver)
    }
}
```


###### 4、使用lifeCycleService解耦Service与组件

上面了解到可以使用lifeCycle解耦Activity与普通组件，这里我们可以使用lifeCycleService解耦Service与普通组件。

其实使用和Activity差不多，activity默认是LifecycleOwner，而Service不是所以Service要添加依赖。

```groovy
    def lifecycle_version = "2.6.0-alpha01"
    implementation("androidx.lifecycle:lifecycle-service:$lifecycle_version")
```

其他的使用流程就和activity差不多了

```kotlin
/***
 * Activity的父类ComponentActivity实现了LifecycleOwner接口，service默认没实现，所以我们应该添加依赖库继承LifecycleService
 */
class LocationService  : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService","onCreate")
        lifecycle.addObserver(MyLocationListenerWithLifecycle(this){
            it.x
            it.y
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LocationService","onDestroy")
    }
}
```

观察者直接复用之前写过的吧~

```kotlin
class MyLocationListenerWithLifecycle(
    private val context: Context,
    private val callback:(Location)->Unit
):LifecycleObserver {
    companion object{
      const val tag = "Lifecycle"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun start() {
        Log.d(tag,"onStart")
        // connect to system location service
        // get current Location
        // feedback
        callback.invoke(Location(50F,50F))
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        Log.d(tag,"onStop")
        // disconnect from system location service
    }

    data class Location(val x:Float,val y:Float)
}
```

剩下的就是service的开启关闭了，简单模拟下~

```kotlin
class ServiceActivity : AppCompatActivity() {
   private val mIntent by lazy {
       Intent(this,LocationService::class.java)
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)
        startService(mIntent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        stopService(mIntent)
    }
}
```

###### 5、使用

ProcessLifecycleOwner 监听应用程序生命周期,其实用法和Activity，Service的使用差不多。只是ProcessLifeCycleOwner监听的
是整个app的生命周期，与Activity数量无关。再者就是ProcessLifeCycleOwner#Lifecycle.Event.ON_DESTROY永远不会调用，ProcessLifeCycleOwner#Lifecycle.Event.ON_CREATE
只会调用一次。

```groovy
    def lifecycle_version = "2.6.0-alpha01"
    implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")
```

```kotlin
/**
 * Create by SunnyDay /07/20 22:08:38
 */
class AppObserver : LifecycleObserver {
    companion object {
        const val TAG = "AppObserver"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun activityCreate() {
        Log.i(TAG, "onCreate")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun activityStart() {
        Log.i(TAG, "onStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun activityResume() {
        Log.i(TAG, "onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun activityStop() {
        Log.i(TAG, "onStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun activityDestroy() {
        Log.i(TAG, "onDestroy")
    }

}
```

```kotlin
/**
 * Create by SunnyDay /08/11 21:06:31
 */
class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppObserver())
    }
}
```

app就一个activity进行的测试，开启app
```kotlin
2022-08-11 21:19:03.662 2743-2743/com.example.notelifecycle I/AppObserver: onCreate
2022-08-11 21:19:03.750 2743-2743/com.example.notelifecycle I/AppObserver: onStart
2022-08-11 21:19:03.754 2743-2743/com.example.notelifecycle I/AppObserver: onResume
```

关闭app

```kotlin
2022-08-11 21:20:32.547 2743-2743/com.example.notelifecycle I/AppObserver: onStop
```

再次打开（app进程还未死亡）
```kotlin
2022-08-11 21:21:05.771 2743-2743/com.example.notelifecycle I/AppObserver: onStart
2022-08-11 21:21:05.774 2743-2743/com.example.notelifecycle I/AppObserver: onResume
```

[官方文档](https://developer.android.google.cn/topic/libraries/architecture/lifecycle)