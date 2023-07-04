package com.example.imdibil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.imdibil.components.BottomNavigationMenu
import com.example.imdibil.ui.theme.TestTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            analytics = Firebase.analytics

            val nav = rememberNavController()
            val context = LocalContext.current
            val title = remember {
                mutableStateOf("")
            }
            val topBarDisabled = remember {
                mutableStateOf(false)
            }
            val loaded = remember {
                MutableTransitionState(true).apply {
                    targetState = true // start the animation immediately
                }
            }



            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("MyLog", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                sendToken(token, context)
            })

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

    private fun sendToken(token: String?, context: Context) {
        var url = "https://imdibil.ru/api/setToken.php?" +
                "token="+token

        val access = getToken(context)

        if(access !== null)
        {
            url=url+"&access="+access
        }
        val queue = Volley.newRequestQueue(context)
        val sRequest = StringRequest(
            Request.Method.GET,
            url,
            {
                    response ->


            },
            {
                Log.d("MyLog", "VolleyError: $it")
            }
        )
        queue.add(sRequest)
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



fun getToken(context: Context): String
{
    val token = context.getSharedPreferences("token_access", 0)
    if(!token.contains("token_access"))
    {
        context.startActivity(Intent(context, LoginActivity::class.java))
    }
    return token.getString("token_access", Context.ACCOUNT_SERVICE).toString()
}