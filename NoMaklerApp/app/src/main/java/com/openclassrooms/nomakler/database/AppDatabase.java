package com.openclassrooms.nomakler.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;

import com.openclassrooms.nomakler.models.Property;

@Database(entities = {Property.class}, version = 2)

@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PropertyDao propertyDao();
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(final SupportSQLiteDatabase db) {
            db.execSQL("ADD COLUMN number TEXT");
        }
    };

}