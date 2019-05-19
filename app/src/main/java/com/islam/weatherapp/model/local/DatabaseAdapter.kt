package com.islam.weatherapp.model.local

import android.content.Context
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.google.gson.Gson
import com.islam.weatherapp.entities.City
import com.islam.weatherapp.entities.Coordinates
import com.islam.weatherapp.entities.FavoriteCityId
import io.reactivex.Flowable
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


private const val DATABASE_NAME = "DatabaseGateway.db"

@Database(entities = [(City::class), (FavoriteCityId::class)], version = 1, exportSchema = false)
@TypeConverters(CoordinatesConverter::class)
abstract class DatabaseAdapter : RoomDatabase() {
    abstract val citiesTable: CitiesTable
    abstract val favoriteCityIdsTable: FavoriteCityIdsTable

    companion object {
        @Volatile
        private var INSTANCE: DatabaseAdapter? = null

        fun getDataBase(context: Context): DatabaseAdapter {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance: DatabaseAdapter = copyDatabaseFromAssets(
                    context
                )
                    .let { Room.databaseBuilder(context, DatabaseAdapter::class.java,
                        DATABASE_NAME
                    ) }
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

private fun copyDatabaseFromAssets(context: Context) =
    context.getDatabasePath(DATABASE_NAME)
        ?.takeUnless { it.exists() }
        ?.let { copy(context, it) }


private fun copy(context: Context, databaseFile: File) {
    databaseFile.parentFile.mkdirs()
    context.assets.open(DATABASE_NAME)
        .use { copyByteArray(databaseFile, it) }

}

private fun copyByteArray(databaseFile: File, assetsInputStream: InputStream) {
    FileOutputStream(databaseFile)
        .use { it.write(byteArray(assetsInputStream)) }
}

private fun byteArray(assetsFileInputStream: InputStream) =
    ByteArray(assetsFileInputStream.available())
        .also { assetsFileInputStream.read(it) }

@Dao
interface CitiesTable {

    @Query("select * from City where City.name like :cityName")
    fun queryCityByName(cityName: String): Flowable<List<City>>

    @Query("select * from City ORDER BY RANDOM() limit 5")
    fun queryFiveCities(): Flowable<List<City>>

    @Query("select count(*) from City ")
    fun queryCitiesCount(): Single<Int>

    @Query("select * from City where City.id in (:ids)")
    fun queryCitiesByIds(ids: List<Long>): Flowable<List<City>>

    @Insert(onConflict = REPLACE)
    fun insertCityList(cities: List<City>)

    @Insert(onConflict = REPLACE)
    fun insertCity(city: City)


}


@Dao
interface FavoriteCityIdsTable {

    @Query("select * from FavoriteCityId")
    fun queryFavoriteCityIds(): Flowable<List<FavoriteCityId>>

    @Insert(onConflict = REPLACE)
    fun insertFavoriteCityId(favoriteCityId: FavoriteCityId)

    @Delete
    fun deleteFavoriteCityId(favoriteCityId: FavoriteCityId)
}

class CoordinatesConverter {

    @TypeConverter
    fun fromCoordinates(coordinates: Coordinates) = Gson().toJson(coordinates)!!


    @TypeConverter
    fun fromString(value: String) = Gson().fromJson<Coordinates>(value, Coordinates::class.java)!!
}
