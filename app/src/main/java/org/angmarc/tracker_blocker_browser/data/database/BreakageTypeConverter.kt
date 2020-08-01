package org.angmarc.tracker_blocker_browser.data.database

import androidx.room.TypeConverter

class BreakageTypeConverter {

    @TypeConverter
    fun fromBreakageType(value: BreakageType): String {
        return value.toString()
    }

    @TypeConverter
    fun toBreakageType(value: String): BreakageType {
        return BreakageType.valueOf(value)
    }
}