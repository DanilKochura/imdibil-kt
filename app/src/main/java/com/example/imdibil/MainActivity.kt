package com.example.imdibil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.imdibil.components.BottomNavigationMenu
import com.example.imdibil.models.Movie
import com.example.imdibil.ui.theme.TestTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject


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
fun postMoviesDataUsingVolley(movies: List<String>, context: Context, token: String? = getToken(context)) {
    // url to post our data
    val url = "https://imdibil.ru/api/getMovie.php"
//        loadingPB.setVisibility(View.VISIBLE)

    // creating a new variable for our request queue
    val queue = Volley.newRequestQueue(context)

    // on below line we are calling a string
    // request method to post the data to our API
    // in this we are calling a post method.
    val request: StringRequest = object : StringRequest(
        Method.POST, url,
        Response.Listener<String?> { response ->
            // inside on response method we are
            // hiding our progress bar
            // and setting data to edit text as empty
//                loadingPB.setVisibility(View.GONE)
//                nameEdt.setText("")
//                jobEdt.setText("")

            // on below line we are displaying a success toast message.
            Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
            try {
                // on below line we are parsing the response
                // to json object to extract data from it.
                val respObj = JSONObject(response)

                // below are the strings which we
                // extract from our json object.
//                    val name = respObj.getString("name")
//                    val job = respObj.getString("job")

                // on below line we are setting this string s to our text view.
//                    responseTV.setText("Name : $name\nJob : $job")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error -> // method to handle errors.
            Toast.makeText(
                context,
                "Fail to get response = $error",
                Toast.LENGTH_SHORT
            ).show()
        }) {
        override fun getParams(): Map<String, String> {
            // below line we are creating a map for
            // storing our values in key and value pair.
            val params: MutableMap<String, String> = HashMap()

            // on below line we are passing our key
            // and value pair to our parameters.
            params["movies0"] = movies[0]
            params["movies1"] = movies[1]
            params["movies2"] = movies[2]
            params["token"] = token.toString()

            // at last we are
            // returning our params.
            return params
        }
    }
    // below line is to make
    // a json object request.
    queue.add(request)
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