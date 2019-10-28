package br.com.fiap.trackmed

import android.content.Context

class BeaconUtils {

    companion object {

        val TRACKMED_BEACON                 = "aa"
        val TYPE_BED                        = "00"
        val TYPE_FOUNTAIN                   = "01"
        val TYPE_HYGIENIZING_DISPENSER      = "02"
        val TYPE_BALCONY                    = "03"
        val TYPE_VENDING_MACHINE            = "04"
        val TYPE_COMPUTER                   = "05"
        val TYPE_CABINET                    = "06"
        val TYPE_LOCKERS                    = "07"
        val TYPE_BULLETIN_BOARD             = "08"
        val TYPE_TIME_ATTENDANCE_REGISTER   = "09"
        val TYPE_MACHINE_01                 = "0a"
        val TYPE_MACHINE_02                 = "0b"
        val TYPE_MACHINE_03                 = "0c"

        fun getBeaconTypeImage(beaconType: String): Int {
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

        fun getBeaconTypeDescription(context: Context, beaconType: String): String {
            return when (beaconType) {
                TYPE_BED -> context.getString(R.string.bed)
                TYPE_FOUNTAIN -> context.getString(R.string.fountain)
                TYPE_HYGIENIZING_DISPENSER -> context.getString(R.string.hygienizing_dispenser)
                TYPE_BALCONY -> context.getString(R.string.balcony)
                TYPE_VENDING_MACHINE -> context.getString(R.string.vending_machine)
                TYPE_COMPUTER -> context.getString(R.string.computer)
                TYPE_CABINET -> context.getString(R.string.cabinet)
                TYPE_LOCKERS -> context.getString(R.string.lockers)
                TYPE_BULLETIN_BOARD -> context.getString(R.string.bulletin_board)
                TYPE_TIME_ATTENDANCE_REGISTER -> context.getString(R.string.time_attendance_register)
                TYPE_MACHINE_01 -> context.getString(R.string.machine_1)
                TYPE_MACHINE_02 -> context.getString(R.string.machine_2)
                TYPE_MACHINE_03 -> context.getString(R.string.machine_3)
                else -> context.getString(R.string.not_implemented)
            }
        }
    }
}