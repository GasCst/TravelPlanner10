package com.example.travelplanner10

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Place::class, EditPlaceActivity.Activity::class], version = 5, exportSchema = false )
abstract class AppDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
    abstract fun ActivityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // nell'utltima migration aggiungo latitudine e longitudine al db
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS places_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                image TEXT NOT NULL,
                date TEXT NOT NULL,
                startTime TEXT NOT NULL,
                endTime TEXT NOT NULL,
                latitude REAL NOT NULL DEFAULT 0.0,
                longitude REAL NOT NULL DEFAULT 0.0
            )
        """)

                // controllo se esiste la vecchia table
                val cursor = database.query("SELECT name FROM sqlite_master WHERE type='table' AND name='places'")
                val tableExists = cursor.count > 0
                cursor.close()

                if (tableExists) {
                    // copio i dati della vecchia table nella nuova
                    database.execSQL("""
                INSERT INTO places_new (id, name, description, image, date, startTime, endTime, latitude, longitude)
                SELECT id, name, description, image, date, startTime, endTime, 0.0, 0.0
                FROM places
            """)

                    // elimino la vecchia table
                    database.execSQL("DROP TABLE places")
                }

                // rinomino la nuova table con il nome della vecchia
                database.execSQL("ALTER TABLE places_new RENAME TO places")

                // creo la table activities
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS activities (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                placeId INTEGER NOT NULL,
                name TEXT NOT NULL,
                date TEXT NOT NULL,
                description TEXT NOT NULL,
                FOREIGN KEY(placeId) REFERENCES places(id) ON DELETE CASCADE
            )
        """)
            }
        }



        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "place_database"
                )
                    .addMigrations(MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
