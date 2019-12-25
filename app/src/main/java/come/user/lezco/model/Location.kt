package come.user.lezco.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity
data class Location(
    @Expose
    @ColumnInfo(name = "address")
    var address:String,
    @ColumnInfo(name = "fulladdress")
    var fulladdress:String
)
{
    @PrimaryKey(autoGenerate = true)
    var id:Int=0
}