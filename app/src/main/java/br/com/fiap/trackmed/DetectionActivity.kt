package br.com.fiap.trackmed

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.view.View
import android.text.format.DateFormat
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detection.*
import org.altbeacon.beacon.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.round

class DetectionActivity : AppCompatActivity(), BeaconConsumer {

    private val AGENT_ID: String = "nome@email.com"

    private var beaconManager: BeaconManager? = null
    private val region: Region = Region("TrackMedRegion", null, null, null)

    private val PERMISSION_REQUEST_COARSE_LOCATION  = 1

    private val MIN_SECONDS_TO_RECORD = 2
    private val MAX_DISTANCE_FOR_DETECTION = 10
    private val MAX_DISTANCE_FOR_RECORD = 2

    private var lastSeenDateTime: Date = Date()
    private var lastSeenBeaconId: String = ""
    private var lastSeenBeaconDistance: Double = 0.0
    private var lastBeaconIdRecorded: String = ""

    private var detectionFlag: Boolean = false

    companion object {



        var db: DatabaseManager? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detection)

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1)
        }

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

            btnClose.setOnClickListener {
                finishAffinity()
            }

            btnHistory.setOnClickListener {
                val intentHistory = Intent(this, HistoryActivity::class.java)
                intentHistory.putExtra("AGENT_ID", AGENT_ID)
                startActivity(intentHistory)
            }
        }

        db = DatabaseManager(this, "TrackHistory")

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
    }


    override fun onDestroy() {
        super.onDestroy()
        db!!.close()
        beaconManager!!.unbind(this)
    }

    override fun onBeaconServiceConnect() {
        beaconManager!!.addRangeNotifier(object : RangeNotifier {
            override fun didRangeBeaconsInRegion(beacons: Collection<Beacon>, region: Region) {
                if (beacons.isNotEmpty()) {

                    val firstBeacon: Beacon? = beacons.filter { it.id2.toString().substring(2,4) == BeaconUtils.TRACKMED_BEACON }.minBy { it.distance }
                    if (firstBeacon != null && !detectionFlag) {
                        runOnUiThread {
                            updateDetection(firstBeacon)
                        }
                    }
                } else {
                    setBeaconImageInvisible()

                }
            }
        })
        try {
            beaconManager!!.startRangingBeaconsInRegion(region)
        } catch (e: RemoteException) {
        }
    }

    fun updateDetection(beacon: Beacon) {

        detectionFlag = true

        if (beacon.id1.toString() != lastSeenBeaconId) {
            lastSeenBeaconId = beacon.id1.toString()
            lastSeenDateTime = Date()
            lastSeenBeaconDistance = beacon.distance * 100 / 100

            if (lastSeenBeaconDistance <= MAX_DISTANCE_FOR_DETECTION) {
                setBeaconImageVisible(beacon)
            } else {
                setBeaconImageInvisible()
            }
        } else {

            if (beacon.id1.toString() == lastSeenBeaconId && lastSeenBeaconId != lastBeaconIdRecorded) {

                if (beacon.distance <= MAX_DISTANCE_FOR_RECORD &&
                    TimeUnit.MILLISECONDS.toSeconds(Date().time - lastSeenDateTime.time) >= MIN_SECONDS_TO_RECORD) {

                    lastBeaconIdRecorded = beacon.id1.toString()
                    db!!.insertTrack(AGENT_ID, lastSeenBeaconId, beacon.id2.toString().substring(4, 6), TimeUnit.MILLISECONDS.toSeconds(Date().time))
                    Log.d("TM", "Gravou " + lastBeaconIdRecorded)
                } else {
                    lastSeenBeaconId = ""
                }
            }
        }

        textLastSeenDateTime.text = DateFormat.getDateFormat(this).format(lastSeenDateTime) + " " +
                                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(lastSeenDateTime)
        detectionFlag = false
    }

    private fun setBeaconImageInvisible() {
        imageBeacon.text = getString(BeaconUtils.getBeaconTypeImage(""))
        frameBeaconData.visibility = View.INVISIBLE
    }

    private fun setBeaconImageVisible(beacon: Beacon) {
        val beaconType: String = beacon.id2.toString().substring(4, 6)

        imageBeacon.text = getString(BeaconUtils.getBeaconTypeImage(beaconType))
        textDistance.text = (round(beacon.distance * 100)/100).toString()
        textBeaconId.text = BeaconUtils.getBeaconTypeDescription(this, beaconType)

        frameBeaconData.visibility = View.VISIBLE
    }

}