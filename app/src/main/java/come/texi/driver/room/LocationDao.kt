package come.texi.driver.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import come.texi.driver.model.Location

@Dao
interface LocationDao {
    @Query("select * from Location")
    fun getLocation():LiveData<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocation(location: Location)
}