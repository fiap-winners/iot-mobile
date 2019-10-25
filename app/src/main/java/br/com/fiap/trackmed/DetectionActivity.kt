package br.com.fiap.trackmed

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detection.*
import org.altbeacon.beacon.*
import java.text.DecimalFormat
import kotlin.math.roundToLong

class DetectionActivity : AppCompatActivity(), BeaconConsumer {

    private var beaconManager: BeaconManager? = null
    private val PERMISSION_REQUEST_COARSE_LOCATION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("This app needs location access")
                builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        PERMISSION_REQUEST_COARSE_LOCATION)
                }
                builder.show()
            }
        }

        setContentView(R.layout.activity_detection)

        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT))
        beaconManager!!.bind(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_COARSE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d(TAG, "coarse location permission granted")
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
                return
            }
        }
/*
        companion object {
            protected val TAG = "DetectionActitity"
        }
*/
    }


    override fun onDestroy() {
        super.onDestroy()
        beaconManager!!.unbind(this)
    }

    override fun onBeaconServiceConnect() {
        beaconManager!!.addRangeNotifier(object : RangeNotifier {
            override fun didRangeBeaconsInRegion(beacons: Collection<Beacon>, region: Region) {
                if (beacons.isNotEmpty()) {

//                    val firstBeacon = beacons.iterator().next()
                    val firstBeacon = beacons.minBy { it.distance }
                    runOnUiThread {
                        updateDistance(firstBeacon!!)
                    }
                }
            }
        })
        try {
            beaconManager!!.startRangingBeaconsInRegion(Region("Hostpital", null, null, null))
        } catch (e: RemoteException) {
        }
    }

    fun updateDistance(beacon: Beacon) {

        if (beacon.distance > 10.0) {
            imageBeacon.visibility = View.INVISIBLE
            textDistance.text = "nada"
            textBeaconId.text = "nada"
        } else {
            imageBeacon.visibility = View.VISIBLE
            textDistance.text = beacon.distance.toString()
            textBeaconId.text = beacon.toString()
        }
    }
}