package com.example.imdibil.viewModel

import android.R.attr.password
import android.accounts.AccountManager.KEY_PASSWORD
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.imdibil.models.Movie
import com.example.imdibil.models.Rate
import com.example.imdibil.models.Third
import com.example.imdibil.models.User
import com.example.imdibil.models.UserRates
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Array


class StatisticsViewModel
        (
        ): ViewModel()
{

        val data = mutableStateOf(listOf<UserRates>())
        val dataPrepared = mutableStateOf(listOf<UserRates>())
        val loaded = mutableStateOf(false)

        val error = mutableStateOf(false)
        val moviesList = mutableStateOf(listOf<String>())
}

fun getStatistycs(
        context: Context,
        rates: MutableState<List<UserRates>>,
        ratesPrepared: MutableState<List<UserRates>>,
        loaded: MutableTransitionState<Boolean>,
        error: MutableTransitionState<Boolean>

)
{

        val url = "https://imdibil.ru/api/getStats.php"

        val queue = Volley.newRequestQueue(context)
        val sRequest = StringRequest(
                Request.Method.GET,
                url,
                {
                                response ->

                        val list = getStats(response)
                        if(list.isEmpty())
                        {
                                error.targetState = true
                        }
                        rates.value = list
                        ratesPrepared.value = list
                        loaded.targetState = false

                },
                {
                        error.targetState = true
                        loaded.targetState = false

                        Log.d("MyLog", "VolleyError: $it")
                }
        )
        queue.add(sRequest)
}

fun getStats(response: String): List<UserRates>
{
        if (response.isEmpty()) return listOf()
        val list = ArrayList<UserRates>()
        var mainObject1 = JSONObject.NULL
        try {
                mainObject1 = JSONObject(response)
        } catch (e: Exception)
        {
                Log.d("MyLog", e.message.toString())
                return listOf()
        }
        val dataObject = mainObject1.getJSONObject("data") as JSONObject

        val imdb = dataObject.getJSONObject("imdb") as JSONObject

        val kp = dataObject.getJSONObject("kp") as JSONObject

        val avg = dataObject.getJSONObject("avg") as JSONObject

        val movies = dataObject.getJSONArray("movies") as JSONArray

        val users = dataObject.getJSONArray("user") as JSONArray



       parsUserRates(list, imdb)
       parsUserRates(list, kp)
       parsUserRates(list, avg)
        val  tmp = arrayListOf<String>()
        for (i in 0 until movies.length())
        {
                tmp.add(movies[i].toString())
        }




        return list
}

fun parsUserRates(list: ArrayList<UserRates>, item: JSONObject): ArrayList<UserRates> {
        val tmp = arrayListOf<Float?>()
        val ratesL = item.getJSONArray("data")
        for(i in 0 until  ratesL.length())
        {
                Log.d("MyLog", ratesL[i].toString())
                val item = ratesL[i] as String
                tmp.add(item.toFloatOrNull())
        }
        list.add(
                UserRates(
                        item.getString("name"),
                        tmp
                )
        )
        return list
}