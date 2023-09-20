package com.example.imdibil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.GradientDrawable.Orientation
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.yml.charts.axis.AxisConfig
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.axis.Gravity
import co.yml.charts.common.model.AccessibilityConfig
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import coil.compose.AsyncImage
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.imdibil.components.GoldButton
import com.example.imdibil.components.GoldText
import com.example.imdibil.models.Movie
import com.example.imdibil.models.Rate
import com.example.imdibil.models.User
import com.example.imdibil.screens.NotTest
import com.example.imdibil.ui.theme.Gold
import com.example.imdibil.ui.theme.MainDark
import com.example.imdibil.ui.theme.Orange
import com.example.imdibil.viewModel.MainViewModel
import com.example.imdibil.viewModel.StatisticsViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject


@Composable
fun NavGraph(
    navHostController: NavHostController, mainViewModel: MainViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val token = LocalContext.current.getSharedPreferences("token_access", 0)
    if(!token.contains("token_access"))
    {
        ctx.startActivity(Intent(ctx, LoginActivity::class.java))
    }
    val t = getToken(ctx)
    NavHost(navController = navHostController, startDestination = "homeHf,"){
        composable("profile"){
            Profile(navHostController, mainViewModel)

        }
        composable("news"){
            Thirds(navHostController, ctx, mainViewModel)
        }
        composable("notifications"){
            NotTest()
        }
        composable("home?scroll={scroll}",
            arguments = listOf(navArgument("scroll") { defaultValue = 0 })){
                backStackEntry ->
                Index(navController = navHostController,
                    backStackEntry.arguments?.getString("scroll")?.toInt() ?: 0
                )


        //            Index(navController = navHostController);
        }

        composable(
            route =     "course/{id}",
            arguments = listOf(navArgument("id")
            {
                type = NavType.IntType
            })
        ){
            val id = it.arguments?.getInt("id")
            if(id != null)
            {
//                CoursePage(id = id, navController = navHostController)
            }
        }
    }
}



@Composable
fun Profile(navController: NavHostController, mainViewModel: MainViewModel)
{
    val movies = remember {
        mutableStateOf(arrayListOf<Movie>())
    }
    val context = LocalContext.current
    val user = remember {
        mutableStateOf(User(0, "", "", 0.0, 0))
    }
    val amount = remember {
        mutableStateOf(0)
    }
    val userRates = remember {
        mutableStateOf(listOf<Rate>())
    }
    val openDialog = remember { mutableStateOf(false) }

     getUser(3, context, user, userRates, navController, amount);
    Column(
        Modifier
            .fillMaxWidth()
            .background(color = MainDark)) {
        Row (
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .padding(10.dp)){
            AsyncImage(model = user.value.avatar, contentDescription = "dsdfsdf",
                Modifier

                    .padding(5.dp)
                    .size(120.dp)
                    .clip(RoundedCornerShape(50))
            )
            Column (verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    GoldText(user.value.name, 28.sp, 600)
                }
               Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                   Column(horizontalAlignment = Alignment.CenterHorizontally) {
                       Text(text = user.value.avgRate!!.toString(), color = getColor(user.value.avgRate!!) , fontSize = 25.sp, fontWeight = FontWeight(600))
                        Text(text = "Ср. оценка", color = Color.LightGray, fontSize = 12.sp)
                   }
                   Column (horizontalAlignment = Alignment.CenterHorizontally) {
                       Text(text = user.value.amountOfMeetings.toString()+"/"+amount.value.toString(), fontSize = 25.sp, color = Color.White)
                       Text(text = "Кол-во встреч", color = Color.LightGray, fontSize = 12.sp)

                   }
               }

            }
        }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                title = { Text(text = "Подтверждение действия") },
                text = { Text("Вы действительно хотите выйти из аккаунта?") },
                confirmButton = {
                    Button(
                        onClick = {
                            val token = context.getSharedPreferences("token_access", 0)
                            token.edit().clear().apply()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                            openDialog.value = false }
                    ) {
                        Text("Да", fontSize = 22.sp)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { openDialog.value = false }
                    ) {
                        Text("Отмена", fontSize = 22.sp)
                    }
                }

            )
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .clip(
                RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )) {
            if (user.value.avgRate !== null)
            {
                Row (horizontalArrangement = Arrangement.SpaceEvenly){
//                Text(text = "Средняя оценка: ")

                }
            }

            DialogAddMovie(mainViewModel.showDialog.value, mainViewModel::setShowDialog, context = context, movies)

            LazyColumn(Modifier.fillMaxWidth()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))

                }
                itemsIndexed(
                    userRates.value
                ) { index, rate ->
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .shadow(2.dp, RoundedCornerShape(10.dp))
                        , colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ))
                    {
                        Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box (modifier = Modifier.fillMaxWidth(0.1f)){
                                AsyncImage(model = rate.movie_poster, contentDescription = "", modifier = Modifier
                                    .height(50.dp)
                                    .align(
                                        Alignment.TopStart
                                    ))

                            }
                           Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                               Text(text = rate.movie_name!!, color = Color.Black, modifier = Modifier.fillMaxWidth(0.6f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                               Text(text = rate.rate.toString(), style = getTextStyle( rate.rate.toDouble()), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                           }
                        }
                    }

                }
                item {
//                    Column() {
//
//                       Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
//                           Button(onClick = {
//                               openDialog.value = true
//                           }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//                               modifier = Modifier.height(35.dp)
//                           ) {
//                               Text(text = "Выход")
//                           }
//                       }
//                    }
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }

        }


    }
}

//









@Composable
fun InDev(){
    val token = LocalContext.current.getSharedPreferences("token", 0)
    if(token.contains("token"))
    {
        Log.d("MyLog", token.getString("token", "kt").toString())
    } else
    {

        token.edit().putString("token","token").apply()
    }
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Row (verticalAlignment = Alignment.CenterVertically){
            Icon(painter = painterResource(id = R.drawable.devel), contentDescription = "dev", modifier = Modifier.padding(5.dp))
            Text(text = "Страница находится в разработке: ")
        }

    }
}




fun getUser(id: Int, context: Context, mutableState: MutableState<User>, rates : MutableState<List<Rate>>, navController: NavHostController, amount: MutableState<Int>)
{
    val token = context.getSharedPreferences("token_access", 0)

    val tok =  token.getString("token_access", Context.ACCOUNT_SERVICE).toString()
//    if (tok == "token")
//    {
//        navController.navigate("login")
//    }
    val url = "https://imdibil.ru/api/profile.php?token=" + tok
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
                response ->
            try {
                val mainObject = JSONObject(response).getJSONObject("data")
//            Log.d("MyLog", mainObject.getDouble("module").toString())
                val ratesObj = mainObject.getJSONArray("rates")

                mutableState.value = User(1, mainObject.getString("name"), "https://imdibil.ru/uploads/"+mainObject.getString("avatar"), mainObject.getDouble("module"), mainObject.getInt("amount"))
                val rateArray = arrayListOf<Rate>()
                amount.value = JSONObject(response).getInt("count")
                for (i in 0 until  ratesObj.length()-1)
                {
                    val item = ratesObj[i] as JSONObject
                    rateArray.add(Rate(item.getInt("rate"), null, item.getString("name_m"), item.getString("poster")
                    ))
                }
                rates.value = rateArray
            } catch (e: Exception)
            {
                Log.d("MyLog", e.toString())
            }
        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAddMovie(showDialog: Boolean, setShowDialogG: (Boolean) -> Unit, context: Context, movies: MutableState<ArrayList<Movie>>) {
    val username = remember { mutableStateOf(arrayListOf(TextFieldValue(), TextFieldValue(),TextFieldValue())) }
    val movs = remember {
        mutableStateOf(listOf("", "",""))
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text("Добавить тройку")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val texts = listOf(username.value[0].text,username.value[1].text,username.value[2].text)
                        postMoviesDataUsingVolley(texts, context)
                        // Change the state to close the dialog

//                        setShowDialog(false)
                    },
                ) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Change the state to close the dialog
                        setShowDialogG(false)
                    },
                ) {
                    Text("Закрыть")
                }
            },
            text = {
                Column {
                    TextField(value = username.value[0],
                        onValueChange = { username.value[0] = it }, maxLines = 1, label = { Text(text = "Ссылка на КП 1") },)
                    TextField(value = username.value[1],
                        onValueChange = { username.value[1] = it }, maxLines = 1, label = { Text(text = "Ссылка на КП 2") },)
                    TextField(value = username.value[2],
                        onValueChange = { username.value[2] = it }, maxLines = 1, label = { Text(text = "Ссылка на КП 3") },)
//                    LazyRow(Modifier.fillMaxWidth()) {
//                        itemsIndexed(movs.value)
//                        {
//                            ite, item ->
//                            AsyncImage(model = item, contentDescription = "", modifier = Modifier.width(90.dp))
//                        }
//                    }
                }

            },
        )
    }
}
//
private fun findOrGetMovie(
    token: String,
    context: Context,
    kp: ArrayList<TextFieldValue>,
    movies: MutableState<ArrayList<Movie>>,
)
{

    val url = "https://imdibil.ru/api/getMovie.php?" +
            "token="+token + "&mov1=" + kp[0].text
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
                response ->
            Log.d("MyLog", response)
            Toast.makeText(context, response, Toast.LENGTH_SHORT).show()

            val json = JSONObject(response)
            movies.value = arrayListOf(Movie(0,json.getString("name"), "",0,0,"","", listOf(), json.getString("poster"), 0.0, 0.0,0.0, ""))
        },
        {
            Log.d("MyLog", "VolleyError: $it")

        }
    )
    queue.add(sRequest)
}

