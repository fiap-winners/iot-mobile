package br.com.fiap.trackmed

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager(context: Context, name: String?) : SQLiteOpenHelper(context, name, null, 1) {

    override fun onCreate(p0: SQLiteDatabase?) {

        p0?.execSQL("CREATE TABLE TrackHistory(" +
                                      "AgentId VARCHAR(30)," +
                                      "BeaconId VARCHAR(20)," +
                                      "BeaconType VARCHAR(2)," +
                                      "DetectionTime REAL);"
        )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

        p0?.execSQL("DROP TABLE IF EXISTS TrackHistory")

        p0?.execSQL("CREATE TABLE TrackHistory(" +
                                      "AgentId VARCHAR(30)," +
                                      "BeaconId VARCHAR(20)," +
                                      "BeaconType VARCHAR(2)," +
                                      "DetectionTime REAL);"
        )
    }

    fun insertTrack(AgenId: String, BeaconId: String, BeaconType: String, DetectionTime: Long) {

        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put("AgentId", AgenId)
        cv.put("BeaconId", BeaconId)
        cv.put("BeaconType", BeaconType)
        cv.put("DetectionTime", DetectionTime)

        db.insert("TrachHistory", null, cv)
    }

    fun queryTrack(agentId: String): Cursor {

        val db = this.readableDatabase
        val cur = db.rawQuery("SELECT AgentId, BeaconId, BeaconType, DetectionTime FROM TrachHistory WHERE agentId = ?", Array<String>(1) {agentId })

        return cur
    }
}