package come.texi.driver.repository

import androidx.lifecycle.LiveData
import come.texi.driver.model.Location
import come.texi.driver.room.Appdatabase
import come.texi.driver.room.LocationDao

class LocationHistoryRepository (private val db:Appdatabase)
{
//    val alllocation=locationDao.getLocation()
   suspend fun getAllLocation( ):LiveData<List<Location>>
    {
        return db.getlocationDao().getLocation()
    }

    suspend fun saveLocation( location: Location)
    {
        return db.getlocationDao().saveLocation(location)
    }
}