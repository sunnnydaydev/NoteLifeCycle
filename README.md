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

lifeCycle优势： lifeCycle就是解决上述问题的。它可以让任何一个类都能轻松感知到Activity/Fragment的生命周期，同时又不需要在Activity/Fragment中编写大量的逻辑处理。

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
###### 3、简单实用#在一个类中监听Activity的生命周期

（1）定义

定义实现类，实现LifecycleObserver接口即可，这样我们自定义的实现类就能感知Activity生命周期了。

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

（2）使用

向Activity注册下监听即可

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
随着Activity生命周期的切换，这会打印出不同的log，可见相对之前来说简洁了些，不用在activity中不同生命周期里面去注册了。

###### 4、

liveData
viewModel
3、lifeCycle
4、生命周期感知组件综合总结

[官方文档](https://developer.android.google.cn/topic/libraries/architecture/lifecycle)