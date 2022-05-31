package com.jiace.apm.until

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.jiace.apm.Application
import kotlin.Exception

/**
 * @author  by Mrw_v @date on 2018/11/29
 *
 */

object LocationHelper {

    /** 定位是否有效 */
    var valid = false

    /** 经度 */
    var gpsLongitude = 0.0

    /** 纬度*/
    var gpsLatitude = 0.0

    var mCurrentLocation: Location? = null

    private lateinit var mCustomLocationThread: CustomLocationThread

    fun startLocation() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(CustomLocationThread.ACTION_LOCATION_CHANGED)
        intentFilter.addAction(CustomLocationThread.ACTION_EXCEPTION)
        Application.get().registerReceiver(mLocationBroadcast, intentFilter)
        mCustomLocationThread = CustomLocationThread()
        mCustomLocationThread.start()
    }

    fun stopLocation() {
        try {
            mCustomLocationThread.cancel()
            Application.get().unregisterReceiver(mLocationBroadcast)
        } catch (e: Exception) {

        }
    }

    /** 获取当前定位 */
    fun getCurrentLocation(): Location? {
        if (valid) {
            stopLocation()
            return mCurrentLocation
        }
        return null
    }

    private val mLocationBroadcast = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            when (action) {
                CustomLocationThread.ACTION_LOCATION_CHANGED -> {
                    valid = true
                    gpsLongitude = intent.getDoubleExtra("gpsLongitude",0.0)
                    gpsLatitude = intent.getDoubleExtra("gpsLatitude",0.0)
                    mCurrentLocation = intent.getParcelableExtra("location")
                }

                CustomLocationThread.ACTION_EXCEPTION -> {
                    valid = false
                }
            }
        }
    }
}


class CustomLocationThread() : Thread("LocationThread") {

    companion object {
        const val ACTION_LOCATION_CHANGED = "com.rs.anchors.LOCATION_CHANGED"
        const val ACTION_EXCEPTION = "com.rs.connection.EXCEPTION"
        const val ERROR_PERMISSION_LOCATION = -1000
        const val MSG_START_LOCATION = 1
        const val MSG_QUIT = 2
    }

    private var mIsStarted = false
    private var mLocationManager: LocationManager = Application.get().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var mLocalBroadcastManager: LocalBroadcastManager = LocalBroadcastManager.getInstance(
        Application.get())
    private var mLocationListener: RsLocationListener = RsLocationListener(mLocalBroadcastManager)
    private var mHandler: Handler? = null

    override fun run() {
        Looper.prepare()
        mHandler = Handler(Looper.myLooper()!!) {
            when (it.what) {
                MSG_START_LOCATION -> {
                    checkAndStartLocation()
                    it.target.sendEmptyMessageDelayed(MSG_START_LOCATION, 10 * 1000)
                    true
                }

                MSG_QUIT -> {
                    stopAndQuit()
                    true
                }

                else -> false
            }
        }
        mHandler?.sendEmptyMessage(MSG_START_LOCATION)
        Looper.loop()
    }

    private fun stopAndQuit() {
        if (mIsStarted) {
            mLocationManager.removeUpdates(mLocationListener)
            val looper = Looper.myLooper()
            looper?.quit()
            mIsStarted = false
            mHandler = null
        }
    }

    fun cancel() {
        try {
            mHandler?.sendEmptyMessage(MSG_QUIT)
            join(2000)
        } catch (ignore: Exception) {

        }
    }

    fun getLocation() = mLocationListener.getLocation()

    private fun checkAndStartLocation() {
        if (!mIsStarted) {
            if (ContextCompat.checkSelfPermission(Application.get(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    Application.get(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val providers = mLocationManager.allProviders
                if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        10000.toLong(),
                        10.toFloat(),
                        mLocationListener,
                        Looper.myLooper()
                    )
                    mIsStarted = true
                }

                if (providers.contains(LocationManager.GPS_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        10000.toLong(),
                        10.toFloat(),
                        mLocationListener,
                        Looper.myLooper()
                    )
                    mIsStarted = true
                }
            }
        }

        if (!mIsStarted) {
            val intent = Intent(ACTION_EXCEPTION)
            intent.putExtra("errorCode", ERROR_PERMISSION_LOCATION)
            mLocalBroadcastManager.sendBroadcast(intent)
        }
    }

    private class RsLocationListener(val mLocalBroadcastManager: LocalBroadcastManager) : LocationListener {
        private var mPreLocation: Location? = null

        fun getLocation() = mPreLocation

        override fun onLocationChanged(location: Location) {
            if (location == null)
                return
            if (isBetterLocation(location, mPreLocation)) {
                mPreLocation = location
            }
            val intent = Intent(ACTION_LOCATION_CHANGED)
            intent.putExtra("gpsLongitude", mPreLocation!!.longitude)
            intent.putExtra("gpsLatitude", mPreLocation!!.latitude)
            intent.putExtra("accuracy", mPreLocation!!.accuracy)
            intent.putExtra("location",mPreLocation)
            mLocalBroadcastManager.sendBroadcast(intent)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}

        private fun isBetterLocation(newLocation: Location, currentLocation: Location?): Boolean {
            if (currentLocation == null)
                return true

            val timeDelta = newLocation.time - currentLocation.time
            if (timeDelta > 60 * 1000)
                return true
            else if (timeDelta < -6 * 1000)
                return false

            val accuracyDelta = (newLocation.accuracy - currentLocation.accuracy).toInt()

            return if (accuracyDelta < 0)
                true
            else if (timeDelta > 0 && accuracyDelta == 0)
                true
            else
                (timeDelta > 0) && (accuracyDelta < 100) && isSameProvider(
                    newLocation.provider,
                    currentLocation.provider
                )
        }

        private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
            if (provider1 == null)
                return provider2 == null
            return provider1 == provider2

        }
    }
}