package com.example.imdibil.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisConfig
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.AccessibilityConfig
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
import com.example.imdibil.R
import com.example.imdibil.models.User
import com.example.imdibil.models.UserRates
import com.example.imdibil.ui.theme.Gold
import com.example.imdibil.ui.theme.MainDark
import com.example.imdibil.ui.theme.Orange
import com.example.imdibil.viewModel.StatisticsViewModel
import com.example.imdibil.viewModel.getStatistycs
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotTest(viewModel: StatisticsViewModel = androidx.lifecycle.viewmodel.compose.viewModel())
{
    val labels = remember {
        mutableStateOf(listOf("Апокалипсис сегодня",
            "Завтрак у Тиффани",
            "Римские каникулы",
            "Жизнь других",
            "Лобстер",
            "Полночь в Париже",
            "Убийца",
            "Убить пересмешника",
            "Призрак в доспехах",
            "Адамовы яблоки",
            "Американский психопат",
            "На грани",
            "Достучаться до небес",
            "Укрась прощальное утро цветами обещания",
            "Залечь на дно в Брюгге",
            "Реквием по мечте",
            "Святая Гора",
            "Форма голоса",
            "Общество мертвых поэтов",
            "Остров сокровищ",
            "Еще по одной",
            "Сладкая жизнь",
            "Охота",
            "Бьютифул",
            "Капитан Фантастик",
            "Отец",
            "Господин Никто",
            "Психо",
            "Ванильное небо",
            "В джазе только девушки",
            "Душа",
            "Таксист",
            "Братья Блюз",
            "Молодость",
            "Нелюбовь",
            "Враг", "Cнайпер"))
    }
    val pointsData: ArrayList<Point> =
        arrayListOf()
    val pointsDataKP: ArrayList<Point> =
        arrayListOf()
    val pointsDataIMDB: ArrayList<Point> =
        arrayListOf()
    var max = -1f;
    var min = 11f;
    val coroutineScope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val act = ctx as Activity
    val portrait = remember {
        mutableStateOf(false)
    }
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val loaded = remember {
        MutableTransitionState(true).apply {
            targetState = true // start the animation immediately
        }
    }
    val lits = listOf(
        "7.5",
        "7",
        "8.8",
        "7.8",
        "6.6",
        "7.8",
        "6.9",
        "7.6",
        "7.4",
        "7.4",
        "7.1",
        "6.6",
        "7.7",
        "7.2",
        "7.7",
        "8",
        "3.4",
        "8.7",
        "7.9",
        "6.9",
        "7.2",
        "5.3",
        "7.6",
        "6.6",
        "6.9",
        "7.6",
        "6.4",
        "7.6",
        "6.6",
        "8.1",
        "8.2",
        "7.4",
        "7.3",
        "6.9",
        "5.9",
        "5.6",
        "6.2"
    )
    for ( i in lits.indices)
    {
        pointsDataKP.add(Point((i+1).toFloat(), lits[i].toFloat()))
    }
    if(viewModel.data.value.isEmpty())
    {
        getStatistycs(LocalContext.current, viewModel.data, viewModel.dataPrepared, movies = labels,  loaded, loaded)
    }

    var list = arrayListOf(Line(
        dataPoints = listOf(Point(0f, 10f), Point(0f, 0f)),
        LineStyle(color = MainDark, width = 0f, alpha = 0f),

        ))
    Log.d("MyLog", viewModel.dataPrepared.value.joinToString("|"))
    for(i in viewModel.dataPrepared.value.indices)
    {
        val item = viewModel.dataPrepared.value[i] as UserRates
        Log.d("MyLog", item.toString())

        val dots = arrayListOf<Point>()
        var color = MainDark
        if(item.name == "IMDB")
        {
            color = Color.Yellow
        } else if(item.name == "Кинопоиск")
        {
            color = Orange
        } else if( item.name == "Оценка сообщества")
        {
            color = Gold
        }
        for (j in item.rates.indices)
        {
            dots.add(Point(j.toFloat(), item.rates[j]?.toFloat() ?: 0f))
        }
        Log.d("MyLog", item.name+" "+item.rates[0])

        list.add(Line(
            dataPoints = dots,
            LineStyle(color = color),
            IntersectionPoint(color = color),
            SelectionHighlightPoint(color = color),
            ShadowUnderLine(color = color),
            SelectionHighlightPopUp(backgroundColor = color, labelColor = Color.White)
        ))
    }
    list = list
     val color = remember {
         mutableStateOf(MainDark)
     }
    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .axisLabelAngle(5f)
        .axisLabelFontSize(10.sp)
        .backgroundColor(MainDark)
        .axisLineColor(Gold)
        .axisLabelColor(Color.LightGray)
        .steps(labels.value.size -1)
        .axisConfig(AxisConfig(true, true,  15.dp))

        .labelData { i -> labels.value[i] }
        .labelAndAxisLinePadding(15.dp).bottomPadding(10.dp)
        .shouldDrawAxisLineTillEnd(true)
        .build()

    val yAxisData = AxisData.Builder()
        .backgroundColor(MainDark)
        .axisLabelColor(Color.LightGray)
        .axisLineColor(Gold)
        .steps(10)
        .labelAndAxisLinePadding(15.dp).bottomPadding(10.dp)
        .labelData { i ->
            i.toString()
        }.build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = list

        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = MainDark, lineWidth = 0.dp),
        backgroundColor = MainDark,
        paddingRight = 0.dp, bottomPadding = 50.dp,
        accessibilityConfig = AccessibilityConfig("Test", popUpTopRightButtonDescription = "test1")
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(color.value)) {

        LineChart(
            modifier =
            Modifier
                .fillMaxSize(),
            lineChartData = lineChartData
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Row {
                IconButton(onClick = {

                    if(!portrait.value)
                    {
                        ctx.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                    } else
                    {
                        ctx.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                    portrait.value = !portrait.value
                }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "", tint = Color.White, modifier = Modifier.padding(10.dp))
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                }) {
                    Icon(
                        Icons.Default.Settings, "", tint = Color.White
                    )
                }
            }
        }
    }
    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContent = {
            val imdb = remember {
                mutableStateOf(true)
            }
            val kp = remember {
                mutableStateOf(true)
            }
            val avg = remember {
                mutableStateOf(true)
            }
            val dist = remember {
                mutableStateOf(true)
            }
            Column (modifier = Modifier.padding(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter_svgrepo_com),
                        contentDescription = "",
                        Modifier
                            .size(30.dp)
                            .padding(end = 7.dp)
                    )
                    androidx.compose.material.Text(text = "Фильтр", fontSize = 20.sp)
                }
                Divider(modifier = Modifier.padding(10.dp), thickness = 2.dp)


                androidx.compose.material.Text(text = "Показывать:")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = imdb.value,
                        onCheckedChange = { checked_ ->
                            imdb.value = checked_
                        }, colors = CheckboxDefaults.colors(checkedColor = Gold),
                        modifier = Modifier.scale(1.3f)
                    )
                    androidx.compose.material.Text(
                        modifier = Modifier.padding(start = 2.dp),
                        text = "IMDB",
                        fontSize = 22.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = kp.value,
                        onCheckedChange = { checked_ ->
                            kp.value = checked_
                        },
                        colors = CheckboxDefaults.colors(checkedColor = Gold),
                        modifier = Modifier.scale(1.3f)

                    )

                    androidx.compose.material.Text(
                        modifier = Modifier.padding(start = 2.dp),
                        text = "Кинопоиск",
                        fontSize = 22.sp
                    )
                }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = avg.value,
                            onCheckedChange = { checked_ ->
                                avg.value = checked_
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Gold),
                            modifier = Modifier.scale(1.3f)

                        )

                        androidx.compose.material.Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text = "Оценка сообщества",
                            fontSize = 22.sp
                        )
                    }
                    Button(
                        onClick = {
                            color.value = Gold
                            val data = arrayListOf<UserRates>()
                            for (i in viewModel.data.value) {
                                if (i.name == "IMDB" && imdb.value) {
                                    Log.d("MyLog", "IMDB ADDDED")
                                    data.add(i)
                                } else if (i.name == "Кинопоиск" && kp.value) {
                                    Log.d("MyLog", "KP ADDDED")
                                    data.add(i)
                                } else if (i.name == "Оценка сообщества" && avg.value) {
                                    Log.d("MyLog", "AVG ADDDED")
                                    data.add(i)
                                }
                                Log.d("MyLog", i.name + kp.value.toString() + imdb.value.toString())
                            }
                            viewModel.dataPrepared.value = data
                            coroutineScope.launch { bottomSheetState.hide() }


                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MainDark,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        androidx.compose.material.Text(text = "Показать")
                    }
            }
        },
        sheetState = bottomSheetState,
    ) {

    }
}