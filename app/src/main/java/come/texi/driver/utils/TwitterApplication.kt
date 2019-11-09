package come.texi.driver.utils

import android.app.Application

import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import come.texi.driver.DropLocationActivity
import come.texi.driver.repository.LocationHistoryRepository
import come.texi.driver.room.Appdatabase

import io.fabric.sdk.android.Fabric
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 * Created by techintegrity on 08/07/16.
 */
class TwitterApplication : Application(),KodeinAware {
    override val kodein= Kodein.lazy {
        import(androidXModule(this@TwitterApplication))
//        bind() from singleton { Appdatabase(instance()) }
        bind() from singleton { LocationHistoryRepository(instance()) }
    }

    override fun onCreate() {

        super.onCreate()
        val authConfig = TwitterAuthConfig("qARyLKqVjnbJ69aDKuCsiOBfo", "bJGtMyN2v0DXqBJPt5AHQkdSUglsRLwBhxhv2Nto4aiP5hY1VD")
        Fabric.with(this, Twitter(authConfig))
    }
}
