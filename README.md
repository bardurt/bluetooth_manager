# Bluetooth Manager


### Add Library to your Android Project

Step 1. Add the JitPack repository to your build file

```
allprojects {
		repositories {
			//...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add it in your root build.gradle at the end of repositories:

</br>

Step 2. Add the dependency
```
dependencies {
	        implementation 'com.github.bardurt:bluetooth_manager:Tag'
	}
```
Tag should be the latest version number 1.0.1 etc

</br>

### How to use

In your MainActivity onCreate, create a new instance and pass it a Activity Context

```
 private lateinit var bluetoothModule: BluetoothModule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothModule = BluetoothModule(this)
    }
    
```


call BluetoothModule.start() to show the bluetooth dialog, the module will request permission if requried.
```
fun showBluetoothSettings() {
    bluetoothModule.start()
}
```

In your MainActivity onDestroy, stop the module to release resources
```
override fun onDestroy() {
    super.onDestroy()
    bluetoothModule.stop()
}
```
