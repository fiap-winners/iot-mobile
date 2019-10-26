package br.com.fiap.trackmed

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detection.*
import org.altbeacon.beacon.*
import java.util.*
import java.util.concurrent.TimeUnit

class DetectionActivity : AppCompatActivity(), BeaconConsumer {

    private var beaconManager: BeaconManager? = null
    private val PERMISSION_REQUEST_COARSE_LOCATION = 1

    private val TRACKMED_BEACON = "aa"
    private val TYPE_BED = "00"
    private val TYPE_FOUNTAIN = "01"
    private val TYPE_HYGIENIZING_DISPENSER = "02"
    private val TYPE_BALCONY = "03"
    private val TYPE_VENDING_MACHINE = "04"
    private val TYPE_COMPUTER = "05"
    private val TYPE_CABINET = "06"
    private val TYPE_LOCKERS = "07"
    private val TYPE_BULLETIN_BOARD = "08"
    private val TYPE_TIME_ATTENDANCE_REGISTER = "09"
    private val TYPE_MACHINE_01 = "0a"
    private val TYPE_MACHINE_02 = "0b"
    private val TYPE_MACHINE_03 = "0c"

    private val MIN_SECONDS_TO_RECORD = 2
    private val MAX_DISTANCE_TO_SHOW_DETECTION = 10

    private var lastSeenDateTime: Date = Date()
    private var lastSeenBeaconId: String = ""
    private var lastSeenBeaconDistance: Double = 0.0
    private var lastBeaconIdRecorded: String = ""

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
                    val firstBeacon: Beacon? = beacons.filter { it.id2.toString().substring(2,4) == TRACKMED_BEACON }.minBy { it.distance }
                    if (firstBeacon != null) {
                        runOnUiThread {
                            updateDistance(firstBeacon!!)
                        }
                    }
                }
            }
        })
        try {
            beaconManager!!.startRangingBeaconsInRegion(Region("TrackMedRegion", null, null, null))
        } catch (e: RemoteException) {
        }
    }

    fun updateDistance(beacon: Beacon) {

        if (beacon.id1.toString() != lastSeenBeaconId) {
            lastSeenBeaconId = beacon.id1.toString()
            lastSeenDateTime = Date()
            lastSeenBeaconDistance = beacon.distance

            if (lastSeenBeaconDistance > MAX_DISTANCE_TO_SHOW_DETECTION) {
                imageBeacon.visibility = View.INVISIBLE
                textDistance.text = "nada"
                textBeaconId.text = "nada"
            } else {
                imageBeacon.text = getString(getBeaconImage(beacon.id2.toString().substring(4, 6)))
                imageBeacon.visibility = View.VISIBLE
                textDistance.text = beacon.distance.toString()
                textBeaconId.text = beacon.id2.toString().substring(4, 6)
            }
        } else {

            if (lastSeenBeaconId == lastBeaconIdRecorded) {
                if (beacon.distance >= MAX_DISTANCE_TO_SHOW_DETECTION &&
                    TimeUnit.MILLISECONDS.toSeconds(Date().time - lastSeenDateTime.time) < MIN_SECONDS_TO_RECORD
                ) {

                    // Persistir
                    lastBeaconIdRecorded = beacon.id1.toString()
                } else {
                    lastSeenBeaconId = ""
                }
            }
        }

        textLastSeenDateTime.text = lastSeenDateTime.toString()
    }

    fun getBeaconImage(beaconType: String): Int {
        return when(beaconType) {
            TYPE_BED -> R.string.fa_procedures_solid
            TYPE_FOUNTAIN -> R.string.fa_water_solid
            TYPE_HYGIENIZING_DISPENSER -> R.string.fa_hand_holding_solid
            TYPE_BALCONY -> R.string.fa_clinic_medical_solid
            TYPE_VENDING_MACHINE -> R.string.fa_store_solid
            TYPE_COMPUTER -> R.string.fa_laptop_medical_solid
            TYPE_CABINET -> R.string.fa_archive_solid
            TYPE_LOCKERS -> R.string.fa_adn
            TYPE_BULLETIN_BOARD -> R.string.fa_chalkboard_solid
            TYPE_TIME_ATTENDANCE_REGISTER -> R.string.fa_user_clock_solid
            TYPE_MACHINE_01 -> R.string.fa_syringe_solid
            TYPE_MACHINE_02 -> R.string.fa_charging_station_solid
            TYPE_MACHINE_03 -> R.string.fa_atom_solid
            else -> R.string.fa_ban_solid
        }
    }

}