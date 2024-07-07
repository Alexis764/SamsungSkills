package co.com.samsungskills.core.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReadSitesDocument @Inject constructor(@ApplicationContext private val context: Context) {

    operator fun invoke(): List<SiteModelGson> {
        val gson = Gson()
        val sitesListJson = context.assets.open("sites.json")
            .bufferedReader()
            .use { it.readText() }

        return gson.fromJson(
            sitesListJson,
            object : TypeToken<List<SiteModelGson>>() {}.type
        )
    }

}