package com.example.ac2_laberinto

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import com.example.ac2_laberinto.ui.theme.AC2LaberintoTheme
import kotlinx.coroutines.delay
import kotlin.math.abs
import androidx.compose.foundation.layout.Box


class Circle(val x: Float, val y: Float, val r: Float)

class Rectangle(val x: Float, val y: Float, val w: Float, val h: Float)


class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var x by mutableFloatStateOf(0f)
    private var y by mutableFloatStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf("title") }

            AC2LaberintoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {

                            "title" -> TitleScreen(
                                onStart = { currentScreen = "game" },
                                onExit = { this@MainActivity.finish() }
                            )

                            "game" -> SimpleGame(
                                modifier = Modifier.fillMaxSize(),
                                x = x,
                                y = y,
                                onWin = { currentScreen = "victory" }
                            )

                            "victory" -> VictoryScreen(
                                onBackToMenu = { currentScreen = "title" }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        x = event.values[0]
        y = event.values[1]
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}


@Composable
fun SimpleGame(modifier: Modifier, x: Float, y: Float, onWin: () -> Unit ) {

    // 1 = pared, 0 = camino, 2 = meta
    val grid = arrayOf(
        intArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 0, 0, 0, 1, 0, 0, 0, 2, 1),
        intArrayOf(1, 0, 1, 0, 1, 0, 1, 1, 1, 1),
        intArrayOf(1, 0, 1, 0, 0, 0, 1, 0, 0, 1),
        intArrayOf(1, 0, 1, 1, 1, 1, 1, 0, 1, 1),
        intArrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 0, 1),
        intArrayOf(1, 0, 0, 0, 1, 0, 0, 0, 0, 1),
        intArrayOf(1, 0, 1, 0, 1, 0, 1, 1, 0, 1),
        intArrayOf(1, 0, 1, 0, 1, 0, 0, 1, 0, 1),
        intArrayOf(1, 0, 1, 0, 0, 0, 0, 1, 0, 1),
        intArrayOf(1, 0, 1, 1, 1, 1, 1, 0, 1, 1),
        intArrayOf(1, 0, 0, 0, 0, 0, 0, 0, 1, 1),
        intArrayOf(1, 0, 1, 1, 1, 1, 0, 0, 1, 1),
        intArrayOf(1, 0, 1, 0, 0, 0, 1, 0, 0, 1),
        intArrayOf(1, 1, 1, 0, 0, 0, 0, 1, 0, 1),
        intArrayOf(1, 0, 1, 0, 1, 1, 1, 1, 0, 1),
        intArrayOf(1, 0, 1, 0, 0, 0, 0, 0, 0, 1),
        intArrayOf(1, 0, 1, 0, 1, 1, 1, 1, 0, 1),
        intArrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
    )

    val radius = 30f
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var circleX by remember { mutableFloatStateOf(0f) }
    var circleY by remember { mutableFloatStateOf(0f) }
    var started by remember { mutableStateOf(false) }
    var win by remember { mutableStateOf(false) }

    // Para que el bucle vea siempre el último valor del sensor
    val currentX by rememberUpdatedState(x)
    val currentY by rememberUpdatedState(y)

    LaunchedEffect(Unit) {

        while (true) {

            delay(16)
            if (canvasSize == Size.Zero || win) continue

            //tamaño bloques
            val cell = minOf(canvasSize.width / grid[0].size, canvasSize.height / grid.size)
            val offX = (canvasSize.width - cell * grid[0].size) / 2f
            val offY = (canvasSize.height - cell * grid.size) / 2f

            //posocion spawneo
            if (!started) {
                circleX = offX + 1.5f * cell
                circleY = offY + 19.5f * cell
                started = true
            }

            //direccion y velocidad de la pelota
            val velocidad = 1f
            val newX = circleX - currentX * velocidad
            val newY = circleY + currentY * velocidad

            //movimiento x
            val circuloEnNuevaX = Circle(newX, circleY, radius)
            var chocaX = false
            for (i in grid.indices) {
                for (j in grid[i].indices) {
                    if (grid[i][j] == 1) {
                        val pared = Rectangle(offX + j * cell, offY + i * cell, cell, cell)
                        if (intersects(circuloEnNuevaX, pared)) chocaX = true
                    }
                }
            }
            if (!chocaX) circleX = newX

            //movimiento y
            val circuloEnNuevaY = Circle(circleX, newY, radius)
            var chocaY = false
            for (i in grid.indices) {
                for (j in grid[i].indices) {
                    if (grid[i][j] == 1) {
                        val pared = Rectangle(offX + j * cell, offY + i * cell, cell, cell)
                        if (intersects(circuloEnNuevaY, pared)) chocaY = true
                    }
                }
            }
            if (!chocaY) circleY = newY

            //comprovar si ha tocado la meta
            val circuloActual = Circle(circleX, circleY, radius)
            for (i in grid.indices) {
                for (j in grid[i].indices) {
                    if (grid[i][j] == 2) {
                        val meta = Rectangle(offX + j * cell, offY + i * cell, cell, cell)
                        if (intersects(circuloActual, meta)) {
                            win = true
                            onWin()
                        }
                    }
                }
            }
        }
    }

    //print del mapa y la bola
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
    ) {
        val cell = minOf(size.width / grid[0].size, size.height / grid.size)
        val offsetX = (size.width - cell * grid[0].size) / 2f
        val offsetY = (size.height - cell * grid.size) / 2f

        for (i in grid.indices) {
            for (j in grid[i].indices) {
                val topLeft = Offset(offsetX + j * cell, offsetY + i * cell)
                when (grid[i][j]) {
                    1 -> drawRect(Color.Black, topLeft, Size(cell, cell))
                    2 -> drawRect(Color.Green, topLeft, Size(cell, cell))
                }
            }
        }

        drawCircle(Color.Red, radius, Offset(circleX, circleY))
    }
}

//deteccion de colision

fun intersects(circle: Circle, rect: Rectangle): Boolean {
    val halfW = rect.w / 2
    val halfH = rect.h / 2

    // 1. Distancia del centro del círculo al centro del rectángulo
    val distX = abs(circle.x - rect.x - halfW)
    val distY = abs(circle.y - rect.y - halfH)

    // 2. Demasiado lejos en X o en Y: no se tocan
    if (distX > halfW + circle.r) return false
    if (distY > halfH + circle.r) return false

    // 3. El centro está dentro de la franja del rectángulo en un eje: se tocan
    if (distX <= halfW) return true
    if (distY <= halfH) return true

    // 4. Caso de la esquina: Pitágoras
    val dx = distX - halfW
    val dy = distY - halfH
    return dx * dx + dy * dy <= circle.r * circle.r
}