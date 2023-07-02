package com.example.test
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.swipeable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.test.components.BottomNavigationMenu
import com.example.test.models.Movie
import com.example.test.models.Rate
import com.example.test.models.User
import com.example.test.ui.theme.Gold
import com.example.test.ui.theme.Green
import com.example.test.ui.theme.MainDark
import com.example.test.ui.theme.Red
import com.example.test.ui.theme.TestTheme
import org.json.JSONObject
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalUriHandler
import com.example.test.components.RateBar
import com.example.test.components.Screen

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val nav = rememberNavController()
            val context = LocalContext.current
            val title = remember {
                mutableStateOf("")
            }
            val topBarDisabled = remember {
                mutableStateOf(false)
            }

//            val token = LocalContext.current.getSharedPreferences("token_access", 0)
//            Log.d("MyLog", token.getString("token_access", Context.ACCOUNT_SERVICE).toString())
//            if(token.contains("token_access"))
//            {
//              context.startActivity(Intent(context, LoginActivity::class.java))
//            }
            LaunchedEffect(nav)
            {
                nav.currentBackStackEntryFlow.collect { backStackEntry ->
                    // You can map the title based on the route using:
                    title.value = getTitleByRoute(context, backStackEntry.destination.route)
                    topBarDisabled.value = backStackEntry.destination.route == "course/{id}"
                }
            }
            TestTheme {
                Scaffold(
                    bottomBar = { BottomNavigationMenu(navController = nav) },
//                    topBar = {
//                        if (!topBarDisabled.value) {
//                            TopAppBar(backgroundColor = MaterialTheme.colorScheme.background)
//                            {
//                                Text(
//                                    title.value,
//                                    fontSize = 22.sp,
//                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier.fillMaxWidth()
//                                )
//
//                            }
//                        }
//                    },
                    content = {padding ->
                        Column(modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()) {
                            NavGraph(navHostController = nav)
                        }

                    }
                )
            }
        }
    }


    private fun getTitleByRoute(context: Context, route: String?): String {
        return when (route) {
            "catalog" -> "Каталог"
            "notifications" -> "Уведомления"
            "profile" -> "Профиль"
            "study" -> "Обучение"
            // other cases
            else -> "Прочее"
        }
    }
}


private fun getMeetings(response: String): List<Movie>{
    if (response.isEmpty()) return listOf()
    val list = ArrayList<Movie>()
    val mainObject = JSONObject(response).getJSONArray("data")

    for (i in 0 until mainObject.length()){ //mainObject.length()
        val item = mainObject[i] as JSONObject
        try {

            val exps = arrayListOf<Rate>();
            val rates = item.getJSONArray("rates");
            for (j in 0 until rates.length())
            {
                val rate = rates[j] as JSONObject
                try {
                    exps.add(
                        Rate(
                            rate.getInt("rate"),
                            User(
                                rate.getInt("id"),
                                rate.getString("name"),
                                "https://imdibil.ru/uploads/"+rate.getString("avatar")
                            )
                        )
                    )
                } catch (e: Exception)
                {
                    Log.d("MyLog", e.toString())
                }
            }
            val poss = item.getJSONObject("positions")







            list.add(
                Movie(
                    item.getInt("id_meet"),
                    item.getString("name_m"),
                    item.getString("original"),
                    item.getInt("year_of_cr"),
                    item.getInt("duration"),
                    "комедия, драма, мелодрама",
                    item.getString("name_d"),
                    exps,
                    item.getString("poster"),
                    item.getDouble("rating"),
                    item.getDouble("rating_kp"),
                    item.getDouble("our_rate"),
                    item.getString("url"),
                    listOf(poss.getInt("imdb"),poss.getInt("kp"),poss.getInt("imdibil"))

                    )
            )
        } catch (e: Exception)
        {
            Log.d("MyLog", e.toString())
        }
    }
    return list
}


private fun getData(page: Int, context: Context, movies: MutableState<List<Movie>>)
{

    val url = "https://imdibil.ru/api/getMeetingList.php" +
            ""
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
                response ->

            val list = getMeetings(response)
            movies.value = list

        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Index(navController: NavHostController)
{
    val courses = remember {
        mutableStateOf(listOf<Movie>())
    }

    val listState: LazyListState = rememberLazyListState()
    val i = 0;
    getData(2, LocalContext.current, courses)
//    Button(onClick = { /*TODO*/ }, modifier = Modifier.height(20.dp)) {
//        Text(text = "IMDIBIL")
//    }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(), state = listState,
        flingBehavior = rememberSnapFlingBehavior(listState)

    ) {
        itemsIndexed(
            courses.value
        ) { index, item ->

            MeetingCard(movie = item, navController)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
//@Preview(showBackground = true)
@Composable
fun MeetingCard(
    movie: Movie = Movie(
        1,
        "В джазе только девушки",
        "Some Like It Hot",
        1959,
        119,
        "мелодрама, комедия",
        "Билли Уайлдер",
        listOf(
            Rate(9 , User(1, "DanilKochura", "https://imdibil.ru/uploads/DanilKochura.jpg"))
        ),
        "https://imdibil.ru/image/Some Like It Hot.jpg",
        8.2,
        8.5,
        8.1,
        "sdsdsdsds"

    ), navController: NavHostController)
{
    val rates = movie.exp_rates.chunked(2)
    val (showDialog, setShowDialog) =  remember { mutableStateOf(false) }
    val visible = remember {
        MutableTransitionState(false).apply {
            targetState = false // start the animation immediately
        }
    }
    val rating =  remember { mutableStateOf(0) }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MainDark,
            contentColor = Color.White
        ),

        modifier = Modifier
            .fillMaxHeight()
            .width(370.dp)
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ){


        Surface( modifier = Modifier
            .height(500.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = { },
                onLongClick = {
                    visible.targetState = !visible.targetState
                },
            ))
        {
            val uriHandler = LocalUriHandler.current

            val alpha: Float by animateFloatAsState(if (!visible.targetState) 1f else 0.1f)
            val ctx = LocalContext.current;
            val rated = false
            val token = ctx.getSharedPreferences("token_access", 0)
//            token.edit().clear().apply()
            val login = token.getString("login", Context.ACCOUNT_SERVICE).toString()
            AsyncImage(model = movie.image, contentDescription = "poster", contentScale = ContentScale.Crop, modifier = Modifier
                .fillMaxSize()
                .background(MainDark), alpha = alpha)
            AnimatedVisibility(
                visibleState = visible,

            ) {
                var rated = remember {
                    mutableStateOf(false)
                }

                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                   LazyColumn(
                       modifier = Modifier
                           .fillMaxWidth()

                   ) {
                       itemsIndexed(
                           rates
                       ) { index, pair ->
                           LazyRow (
                               Modifier
                                   .fillMaxSize()
                                   .padding(10.dp),horizontalArrangement = Arrangement.SpaceBetween, ){
                               itemsIndexed(
                                   pair
                               ) {
                                       index, item ->
//                                   Log.d("MyLog", item.author?.name.toString()+" "+login)
                                   if(item.author?.name.toString() === login)
                                   {
                                       Log.d("MyLog", "CHANGED")
                                       rated.value = true;
                                   }
                                   Row (Modifier.fillMaxWidth(0.5f), verticalAlignment = Alignment.CenterVertically){
                                       AsyncImage(model = item.author?.avatar, contentDescription = movie.name,
                                           Modifier
                                               .width(80.dp)
                                               .height(80.dp)
                                               .clip(
                                                   RoundedCornerShape(50)
                                               ), contentScale = ContentScale.Crop)
                                       Text(text = item.rate.toString(), color = getColor(item.rate.toDouble()), fontWeight = FontWeight(800), modifier = Modifier.padding(start = 10.dp), fontSize = 25.sp)
                                   }
                               }
                           }
                       }
                   }
                    Log.d("MyLog", rated.value.toString())
                  if(!rated.value)
                  {
                      IconButton(onClick = {
                          setShowDialog(true)
                      }, modifier = Modifier.border(2.dp, Gold, RoundedCornerShape(50))) {
                          Icon(Icons.Default.Add, contentDescription = "add", modifier = Modifier.size(50.dp), tint = Gold)
                      }
                  }
                   DialogRate(showDialog, setShowDialog, context = ctx, rating, movie = movie.id)
               }
            }
        }

//        Image(painter = painterResource(id = R.drawable.jazz), contentDescription = movie.name, modifier = Modifier.height(500.dp).fillMaxWidth())
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween)
            {
                Text(text = movie.name, color = Gold, fontSize = 22.sp, fontWeight = FontWeight(600) , modifier = Modifier.fillMaxWidth(0.7f), maxLines = 1)
                Text(text = "#"+movie.id.toString(), fontSize = 22.sp, color = Color.Gray)


            }
            Text(text = "Год: "+movie.year, fontSize = 18.sp)
            Text(text = "Режиссер: "+movie.director, fontSize = 18.sp)
            Text(text = "Длительность: "+movie.duration, fontSize = 18.sp)
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Image(
                        painter = painterResource(id = R.drawable.imdb),
                        contentDescription = "imdb",
                        Modifier
                            .height(40.dp)
                            .padding(horizontal = 5.dp)
                        )
                    Text(text = movie.imdb.toString(), color = getColor(movie.imdb))
                    if(movie.positions !== null)
                    {
                        Text(text = movie.positions[0].toString(), color = Color.Gray)
                    }
                }
                val uriHandler = LocalUriHandler.current

                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource(id = R.drawable.kp),
                        contentDescription = "imdb",
                        Modifier
                            .height(40.dp)
                            .padding(horizontal = 5.dp)
                            .clickable { uriHandler.openUri(movie.url) })
                    Text(text = movie.kp.toString(), color = getColor(movie.kp))
                    if(movie.positions !== null)
                    {
                        Text(text = movie.positions[1].toString(), color = Color.Gray)
                    }

                }
                if(movie.our !== null)
                {
                    Column (horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.logogo),
                            contentDescription = "imdb",
                            Modifier
                                .height(40.dp)
                                .padding(horizontal = 5.dp))
                        Text(text = movie.our.toString(), color = getColor(movie.our))
                        if(movie.positions !== null)
                        {
                            Text(text = movie.positions[2].toString(), color = Color.Gray)
                        }
                    }
                }
            }

        }
    }
}

fun getColor(rate: Double): Color {
    return if (rate >= 7.0)
    {
        Green
    } else if(rate < 5.0)
    {
        Red
    } else
    {
        Color.Gray
    }
}

@Composable
fun DialogRate(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, context: Context, rate: MutableState<Int>, movie: Int) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text("Оценить")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Change the state to close the dialog
                        if(rate.value != 0)
                        {
                            addRate(rate, getToken(context = context), context = context, movie = movie)
                        }
                        setShowDialog(false)
                    },
                ) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Change the state to close the dialog
                        setShowDialog(false)
                    },
                ) {
                    Text("Закрыть")
                }
            },
            text = {
                Screen(context, rate)

            },
        )
    }
}

private fun addRate(rating: MutableState<Int>, token: String, context: Context, movie: Int)
{

    val url = "https://imdibil.ru/api/rate.php?" +
            "token="+token + "&rating=" + rating.value + "&movie=" + movie
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
                response ->

            val json = JSONObject(response)
            Toast.makeText(context, json.getString("text"), Toast.LENGTH_SHORT).show()
        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}


fun getToken(context: Context): String
{
    val token = context.getSharedPreferences("token_access", 0)
    if(!token.contains("token_access"))
    {
        context.startActivity(Intent(context, LoginActivity::class.java))
    }
    return token.getString("token_access", Context.ACCOUNT_SERVICE).toString()
}