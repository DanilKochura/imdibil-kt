package com.example.imdibil

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.imdibil.components.GoldText
import com.example.imdibil.models.Movie
import com.example.imdibil.models.Third
import com.example.imdibil.viewModel.MainViewModel
import org.json.JSONArray
import org.json.JSONObject
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Thirds(navHostController: NavHostController, ctx: Context, mainViewModel: MainViewModel) {

    val loaded = remember {
        MutableTransitionState(true).apply {
            targetState = true // start the animation immediately
        }
    }
    val error = remember {
        MutableTransitionState(false).apply {
            targetState = false // start the animation immediately
        }
    }
    AnimatedVisibility(visibleState = loaded) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
    }
    val listState: LazyListState = rememberLazyListState()
    val i = 0;
    AnimatedVisibility(visibleState = error) {
        ErrorScreen()
    }
    if(mainViewModel.thirds.value.isEmpty())
    {
        getThirdsRaw(ctx, mainViewModel.thirds, loaded, error)
    }

    val sort = remember {
        mutableStateOf(0)
    }
    LazyColumn(Modifier.fillMaxSize()) {
        itemsIndexed(mainViewModel.thirds.value)
        {
                it, item ->

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                itemsIndexed(
                    item.movies
                ) { index, movie ->

                    MeetingCard(movie = movie, navHostController, item.selected)
                }
            }
        }
    }

}
private fun getThirds(response: String): List<Third>{
    if (response.isEmpty()) return listOf()
    val list = ArrayList<Third>()
    val mainObject = JSONObject(response).getJSONArray("data")

    for (i in 0 until mainObject.length()){ //mainObject.length()
        val third = mainObject[i] as JSONArray
        val thirdList = ArrayList<Movie>()
        for(j in 0 until 3)
        {
            val item = third[j] as JSONObject
            try {

                thirdList.add(
                    Movie(
                        item.getInt("id_m"),
                        item.getString("name_m"),
                        item.getString("original"),
                        item.getInt("year_of_cr"),
                        item.getInt("duration"),
                        "комедия, драма, мелодрама",
                        item.getString("name_d"),
                        listOf(),
                        item.getString("poster"),
                        item.getDouble("rating"),
                        item.getDouble("rating_kp"),
                        if (item.has("our_rate") && !item.isNull("our_rate")) {
                            item.getDouble("our_rate")
                        } else
                        {
                            null
                        },
                        item.getString("url"),
//                        listOf(poss.getInt("imdb"),poss.getInt("kp"),poss.getInt("imdibil")),
                        null,
                        if (item.has("id_e") && !item.isNull("id_e")) {
                            item.getInt("id_e")
                        } else
                        {
                            null
                        },
                        item.getString("description")
                    )

                )


            } catch (e: Exception)
            {
                Log.d("MyLog", e.toString())
            }
        }
        val item = third[0] as JSONObject

        val author = item.getString("name")
        val selected = if (item.has("selected") && !item.isNull("selected")) {
            item.getInt("selected")
        } else
        {
            0
        }

        list.add(Third(thirdList, author = author, selected = selected))

    }
    return list
}


private fun getThirdsRaw(
    context: Context,
    thirds: MutableState<List<Third>>,
    loaded: MutableTransitionState<Boolean>,
    error: MutableTransitionState<Boolean>
)
{

    val url = "https://imdibil.ru/api/getThirds.php"
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
                response ->

            val list = getThirds(response)
            if(list.isEmpty())
            {
                error.targetState = true
            }
            thirds.value = list
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
