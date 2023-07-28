package com.example.imdibil

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.imdibil.components.GoldButton
import com.example.imdibil.components.GoldText
import com.example.imdibil.models.Movie
import com.example.imdibil.models.Rate
import com.example.imdibil.models.User
import com.example.imdibil.ui.theme.Gold
import com.example.imdibil.ui.theme.MainDark
import org.json.JSONObject


@Composable
fun NavGraph(
    navHostController: NavHostController
) {
    val ctx = LocalContext.current
    val token = LocalContext.current.getSharedPreferences("token_access", 0)
    if(!token.contains("token_access"))
    {
        ctx.startActivity(Intent(ctx, LoginActivity::class.java))
    }
    val t = getToken(ctx)
    NavHost(navController = navHostController, startDestination = "home"){
        composable("profile"){
            Profile(navHostController)

        }
        composable("news"){
            Thirds(navHostController, ctx)
        }
        composable("notifications"){
            InDev()
        }
        composable("home"){
                Index(navController = navHostController)


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
fun Profile(navController: NavHostController)
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
    val (showDialog, setShowDialog) =  remember { mutableStateOf(false) }
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
                       Text(text = user.value.amountOfMeetings.minus(1).toString()+"/"+amount.value.toString(), fontSize = 25.sp, color = Color.White)
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
            Spacer(modifier = Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = {setShowDialog(true)}, colors = ButtonDefaults.buttonColors(Gold), shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp) ) {
                    Text(text = "Добавить тройку")
                }

//                Button(onClick = {
//                    openDialog.value = true
//                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//                    modifier = Modifier.height(50.dp)
//                ) {
//                    Text(text = "Выход")
//                }
            }
            DialogAddMovie(showDialog, setShowDialog, context = context, movies)
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(Modifier.fillMaxWidth()) {
                itemsIndexed(
                    userRates.value
                ) { index, rate ->
                    Row (horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        Text(text = rate.movie_name!!, modifier = Modifier.fillMaxWidth(0.6f))
                        Text(text = rate.rate.toString(), style = getTextStyle( rate.rate.toDouble()), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
            Column() {

                Button(onClick = {
                    openDialog.value = true
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.height(35.dp)
                ) {
                    Text(text = "Выход")
                }
            }
        }


    }
}

//






@Composable
fun Notifications()
{
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "InDev: Notifications")

    }
}



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
                    rateArray.add(Rate(item.getInt("rate"), null, item.getString("name_m")
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
fun DialogAddMovie(showDialog: Boolean, setShowDialog: (Boolean) -> Unit, context: Context, movies: MutableState<ArrayList<Movie>>) {
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
                        setShowDialog(false)
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

