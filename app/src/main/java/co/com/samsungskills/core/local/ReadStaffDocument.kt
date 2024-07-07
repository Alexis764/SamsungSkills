package co.com.samsungskills.core.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReadStaffDocument @Inject constructor(@ApplicationContext private val context: Context) {

    operator fun invoke(): StaffModelGson {
        val gson = Gson()
        val staffJson = context.assets.open("staff.json")
            .bufferedReader()
            .use { it.readText() }

        return gson.fromJson(
            staffJson,
            object : TypeToken<StaffModelGson>() {}.type
        )
    }

}