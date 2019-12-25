package come.user.lezco.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import come.user.lezco.model.Location

@Database(entities = arrayOf(Location::class),
    version = 1)
public abstract class Appdatabase:RoomDatabase() {

        abstract fun getlocationDao():LocationDao
//        abstract fun  deletelocationDao():LocationDao
    companion object
    {
            //Singleton Prevent multiple instance of database opening at the same time.
        @Volatile
        private var INSTANCE:Appdatabase?=null

        fun getDatabase(context: Context):Appdatabase
        {
            val tempInstance= INSTANCE
            if(tempInstance!=null)
                return tempInstance
            synchronized(this)
            {
                val instance=Room.databaseBuilder(
                    context.applicationContext,
                    Appdatabase::class.java,
                    "Appdatabase"
                ).build()
                INSTANCE=instance
                return instance
            }
        }

    }
}