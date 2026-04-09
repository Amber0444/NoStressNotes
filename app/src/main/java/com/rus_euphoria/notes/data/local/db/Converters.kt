package com.rus_euphoria.notes.data.local.db

import androidx.room.TypeConverter
import com.rus_euphoria.notes.model.Importance
import java.util.Date

class Converters {

    @TypeConverter
    fun importanceToString(value: Importance): String = value.name

    @TypeConverter
    fun stringToImportance(value: String): Importance = Importance.valueOf(value)

    @TypeConverter
    fun dateToLong(value: Date?): Long? = value?.time

    @TypeConverter
    fun longToDate(value: Long?): Date? = value?.let { Date(it) }
}
