package com.example.imdibil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.imdibil.components.GoldButton
import com.example.imdibil.ui.theme.TestTheme
import org.json.JSONObject

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Login()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login() {

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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            onValueChange = { password.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier
            .padding(40.dp, 0.dp, 40.dp, 0.dp)
            .width(350.dp)) {
            GoldButton(text = "Войти", onClick = {
                login(username, password, context, incorrect)
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

fun login(username: MutableState<TextFieldValue>, password: MutableState<TextFieldValue>, context: Context, incorrect: MutableTransitionState<Boolean>) {
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
//            Log.d("MyLog", response)
            val token = context.getSharedPreferences("token_access", 0)
            token.edit().putString("token_access",response).apply()
            token.edit().putString("login",username.value.text).apply()
            context.startActivity(Intent(context, MainActivity::class.java))


        },
        {
            password.value = TextFieldValue();
            incorrect.targetState = true
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}

