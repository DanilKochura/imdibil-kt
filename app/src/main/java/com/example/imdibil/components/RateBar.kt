package com.example.test.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.MeetingCard
import com.example.test.ui.theme.Gold
import com.example.test.ui.theme.MainDark


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RateBar(context: Context = LocalContext.current, rate: MutableState<Int>)
{

    val startColor = remember {
        listOf(mutableStateOf(MainDark),mutableStateOf(MainDark),mutableStateOf(MainDark),mutableStateOf(MainDark),mutableStateOf(MainDark),mutableStateOf(MainDark),mutableStateOf(MainDark),mutableStateOf(MainDark),mutableStateOf(MainDark),mutableStateOf(MainDark))
    }

    val listState: LazyListState = rememberLazyListState()
    val list = listOf(1,2,3,4,5,6,7,8,9,10)
    LazyRow(
        modifier = Modifier.width(400.dp), state = listState,
        flingBehavior = rememberSnapFlingBehavior(listState)

    ) {
        itemsIndexed(list)
        {
            index, it ->
            IconButton(onClick =
            {
                rate.value = it
                for(i in 0 .. 9)
                {
                    if(i <= index)
                    {
                        startColor[i].value = Gold
                    } else
                    {
                        startColor[i].value = MainDark
                    }
                }

            }, Modifier.size(60.dp)) {
                Icon(imageVector = Icons.Default.Star, tint = startColor[index].value, contentDescription = it.toString(), modifier = Modifier.size(60.dp))
                Text(it.toString(), color = Color.White)

            }
        }
    }
}


@Composable
fun test()
{
    IconButton(onClick = { /*TODO*/ }, Modifier.fillMaxSize()) {
        Icon(imageVector = Icons.Default.Star, contentDescription = "sfgsfg", modifier = Modifier.size(95.dp))
        Text(text = "1", color = Color.White)
    }
}

@Composable
fun Screen(context: Context = LocalContext.current, rate: MutableState<Int>)
{
    Column(modifier = Modifier
        .width(500.dp)
        .horizontalScroll(rememberScrollState())) {
        RateBar(context, rate)
    }
}