package no.uio.ifi.in2000.team47.rocketboy.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team47.rocketboy.data.db.dao.WeatherSettingsDao
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData

/**
 * Android Room Database giving access to (multiple) different DAOs (database-objects)
 * --> Change version number if you make changes to Database Schema, current migration
 * method set to DESTROY, change this if keeping data between code updates becomes important.
 */
@Database(entities = [WeatherSettingsData::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherSettingsDao(): WeatherSettingsDao
}

object DatabaseProvider {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "rocket_database"
                    ).fallbackToDestructiveMigration(false)
                .build().also {INSTANCE = it}
            instance
        }
    }
}