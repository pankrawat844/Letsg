package come.user.lezco.repository

import androidx.lifecycle.LiveData
import come.user.lezco.model.Location
import come.user.lezco.room.Appdatabase

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