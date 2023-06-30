package com.example.test

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.test.components.GoldButton
import com.example.test.components.GoldText
import com.example.test.models.Rate
import com.example.test.models.User
import com.example.test.ui.theme.Gold
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import kotlin.math.log


@Composable
fun NavGraph(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = "catalog"){
        composable("profile"){
            Profile(navHostController)
        }
        composable("study"){
            InDev()
        }
        composable("notifications"){
            InDev()
        }
        composable("catalog"){
            val token = LocalContext.current.getSharedPreferences("token_access", 0)
            if(token.contains("token_access"))
            {
                Index(navController = navHostController)
            } else
            {
                navHostController.navigate("login")
            }

        //            Index(navController = navHostController);
        }
        composable("login"){
            Login(navController = navHostController);
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
    val context = LocalContext.current
    val user = remember {
        mutableStateOf(User(0, "", ""))
    }
    val userRates = remember {
        mutableStateOf(listOf<Rate>())
    }
     getUser(3, context, user, userRates, navController);
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)) {
        AsyncImage(model = user.value.avatar, contentDescription = "dsdfsdf",
            Modifier
                .clip(
                    RoundedCornerShape(20)
                )
                .fillMaxWidth()
        )
        GoldText(user.value.name, 22.sp, 600)
        if (user.value.avgRate !== null)
        {
            Row {
                Text(text = "Средняя оценка: ")
                Text(text = user.value.avgRate!!.toString(), color = getColor(user.value.avgRate!!) )
            }
        }
        Text(text = "Количество заседаний: "+user.value.amountOfMeetings)
        GoldButton("Добавить фильм", {})

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
fun Study(){
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "InDev: Study")

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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController) {
//    Box(modifier = Modifier.fillMaxSize()) {
//        ClickableText(
//            text = AnnotatedString("Sign up here"),
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(20.dp),
//            onClick = { },
//            style = TextStyle(
//                fontSize = 14.sp,
////                fontFamily = FontFamily.Default,
//                textDecoration = TextDecoration.Underline,
//                color = Gold
//            )
//        )
//    }
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val username = remember { mutableStateOf(TextFieldValue()) }
        val password = remember { mutableStateOf(TextFieldValue()) }
        val incorrect = remember {
            MutableTransitionState(false).apply {
                targetState = false // start the animation immediately
            }
        }
        val context = LocalContext.current
        Image(painter = painterResource(id = R.drawable.logogo), contentDescription = "MainLoginLogo", Modifier.fillMaxWidth(0.7f))
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Авторизация", style = TextStyle(fontSize = 40.sp))
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Логин") },
            value = username.value,
            onValueChange = { username.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Пароль") },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier
            .padding(40.dp, 0.dp, 40.dp, 0.dp)
            .width(350.dp)) {
            GoldButton(text = "Войти", onClick = {
                login(username, password, context, incorrect, navController)
            })
        }

        Spacer(modifier = Modifier.height(20.dp))
        AnimatedVisibility(visibleState = incorrect) {
            Text(text = "Неверный логин или пароль!", color = Color.Red)

        }
//        ClickableText(
//            text = AnnotatedString("Forgot password?"),
//            onClick = { },
//            style = TextStyle(
//                fontSize = 14.sp,
////                fontFamily = FontFamily.Default
//            )
//        )
    }
}

fun login(username: MutableState<TextFieldValue>, password: MutableState<TextFieldValue>, context: Context, incorrect: MutableTransitionState<Boolean>, navController: NavHostController) {
    val jsonBody = JSONObject()
    jsonBody.put("login", username.value.text)
    jsonBody.put("password", password.value.text)
    val requestBody = jsonBody.toString()
    val url = "https://imdibil.ru/api/login.php" +
            "?login="+username.value.text+"&password="+password.value.text
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
                response ->
            Log.d("MyLog", response)
            val token = context.getSharedPreferences("token_access", 0)
            token.edit().putString("token_access",response).apply()
            navController.navigate("profile")


        },
        {
            password.value = TextFieldValue();
            incorrect.targetState = true
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}


fun getUser(id: Int, context: Context, mutableState: MutableState<User>, rates : MutableState<List<Rate>>, navController: NavHostController)
{
    val token = context.getSharedPreferences("token_access", 0)

    val tok =  token.getString("token_access", Context.ACCOUNT_SERVICE).toString()
//    if (tok == "token")
//    {
//        navController.navigate("login")
//    }
    Log.d("MyLog", tok)
    val url = "https://imdibil.ru/api/profile.php?token=" + tok
    Log.d("MyLog", url)
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
                response ->

            val mainObject = JSONObject(response).getJSONObject("data")
            val ratesObj = mainObject.getJSONArray("rates")
            mutableState.value = User(1, mainObject.getString("name"), "https://imdibil.ru/uploads/"+mainObject.getString("avatar"), mainObject.getDouble("module"), mainObject.getInt("amount"))
            val rateArray = arrayListOf<Rate>()
            for (i in 0 .. ratesObj.length())
            {
                val item = ratesObj[i] as JSONObject
                rateArray.add(Rate(item.getInt("rate"), null, item.getString("name_m")
                ))
            }
        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}



