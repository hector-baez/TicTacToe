package com.hbaez.tictactoe

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TicTacToe(
    onPlayerWin: (Player) -> Unit,
    onNewRound: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var isGameRunning by remember {
        mutableStateOf(true)
    }
    var currPlayer by remember {
        mutableStateOf<Player>(Player.X)
    }
    var gameState by remember {
        mutableStateOf(emptyGameState())
    }
    var animations = remember {
        emptyAnimations()
    }
    var squareTapped by remember {
        mutableStateOf(-1)
    }

    Canvas(
        modifier = Modifier
            .size(300.dp)
            .padding(10.dp)
            .pointerInput(true) {
                detectTapGestures {
                    if(!isGameRunning) {
                        return@detectTapGestures
                    }
                    squareTapped = getSquare(it.x, it.y, size)
                    Log.println(Log.DEBUG, "tapped square", squareTapped.toString())
//                    Log.println(Log.DEBUG, "tapped square", "${it.x}...${it.y}")
//                    Log.println(Log.DEBUG, "canvas center", "${center.x}...${center.y}")
                    if(squareTapped != -1){
                        currPlayer = !currPlayer
                    }
                    when (squareTapped){
                        1 -> {
                            if(gameState[0][0] == 'E') {
                                gameState = updateGameState(gameState, 0, 0, currPlayer.symbol)
                                scope.animateFloatToOne(animations[0][0])
                            }
                        }
                        2 -> {
                            if(gameState[1][0] == 'E') {
                                gameState = updateGameState(gameState, 1, 0, currPlayer.symbol)
                                scope.animateFloatToOne(animations[1][0])
                            }
                        }
                        3 -> {
                            if(gameState[2][0] == 'E') {
                                gameState = updateGameState(gameState, 2, 0, currPlayer.symbol)
                                scope.animateFloatToOne(animations[2][0])
                            }
                        }
                        4 -> {
                            if(gameState[0][1] == 'E') {
                                gameState = updateGameState(gameState, 0, 1, currPlayer.symbol)
                                scope.animateFloatToOne(animations[0][1])
                            }
                        }
                        5 -> {
                            if(gameState[1][1] == 'E') {
                                gameState = updateGameState(gameState, 1, 1, currPlayer.symbol)
                                scope.animateFloatToOne(animations[1][1])
                            }
                        }
                        6 -> {
                            if(gameState[2][1] == 'E') {
                                gameState = updateGameState(gameState, 2, 1, currPlayer.symbol)
                                scope.animateFloatToOne(animations[2][1])
                            }
                        }
                        7 -> {
                            if(gameState[0][2] == 'E') {
                                gameState = updateGameState(gameState, 0, 2, currPlayer.symbol)
                                scope.animateFloatToOne(animations[0][2])
                            }
                        }
                        8 -> {
                            if(gameState[1][2] == 'E') {
                                gameState = updateGameState(gameState, 1, 2, currPlayer.symbol)
                                scope.animateFloatToOne(animations[1][2])
                            }
                        }
                        9 -> {
                            if(gameState[2][2] == 'E') {
                                gameState = updateGameState(gameState, 2, 2, currPlayer.symbol)
                                scope.animateFloatToOne(animations[2][2])
                            }
                        }
                    }
                    val isFieldFull = gameState.all { row ->
                        row.all { char -> char != 'E' }
                    }
                    val didXWin = didPlayerWin(gameState, Player.X)
                    val didOWin = didPlayerWin(gameState, Player.O)
                    if(didXWin){
                        onPlayerWin(Player.X)
                    } else if (didOWin) {
                        onPlayerWin(Player.O)
                    }
                    if(isFieldFull || didXWin || didOWin) {
                        scope.launch {
                            isGameRunning = false
                            delay(5000L)
                            isGameRunning = true
                            gameState = emptyGameState()
                            animations = emptyAnimations()
                            onNewRound()
                        }
                    }
                }
            }
    ) {
        // draw horizontal
        drawLine(
            color = Color.Black,
            start = Offset(size.width / 3f, 0f),
            end = Offset(size.width / 3f, size.height),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(2 * size.width / 3f, 0f),
            end = Offset(2 * size.width / 3f, size.height),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round
        )
        // draw vertical
        drawLine(
            color = Color.Black,
            start = Offset(0f, size.width / 3f),
            end = Offset(size.height, size.width / 3f),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black,
            start = Offset(0f, 2 * size.width / 3f),
            end = Offset(size.height, 2 * size.width / 3f),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // draw each played square
        gameState.forEachIndexed { i, row ->
            row.forEachIndexed { j, symbol ->
                if (symbol == Player.X.symbol) {
                    val path1 = Path().apply {
                        moveTo(
                            i * size.width / 3f + size.width / 6f - 50f,
                            j * size.height / 3f + size.height / 6f - 50f
                        )
                        lineTo(
                            i * size.width / 3f + size.width / 6f + 50f,
                            j * size.height / 3f + size.height / 6f + 50f
                        )
                    }
                    val path2 = Path().apply {
                        moveTo(
                            i * size.width / 3f + size.width / 6f - 50f,
                            j * size.height / 3f + size.height / 6f + 50f
                        )
                        lineTo(
                            i * size.width / 3f + size.width / 6f + 50f,
                            j * size.height / 3f + size.height / 6f - 50f,
                        )
                    }
                    val outPath1 = Path()
                    PathMeasure().apply {
                        setPath(path1, false)
                        getSegment(0f, animations[i][j].value * length, outPath1)
                    }
                    val outPath2 = Path()
                    PathMeasure().apply {
                        setPath(path2, false)
                        getSegment(0f, animations[i][j].value * length, outPath2)
                    }
                    drawPath(
                        path = outPath1,
                        color = Color.Blue,
                        style = Stroke(
                            width = 5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                    drawPath(
                        path = outPath2,
                        color = Color.Blue,
                        style = Stroke(
                            width = 5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                } else if (symbol == Player.O.symbol) {
                    drawArc(
                        color = Color.Green,
                        startAngle = 0f,
                        sweepAngle = animations[i][j].value * 360f,
                        useCenter = false,
                        topLeft = Offset(i * size.width / 3f + size.width / 6f - 50f, j * size.height / 3f + size.height / 6f - 50f),
                        size = Size(100f, 100f),
                        style = Stroke(
                            width = 5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
        }
    }
}

fun getSquare(X: Float, Y: Float, size: IntSize): Int{

    if(X in 0f..(size.width.toFloat()) && Y in 0f..(size.height.toFloat())){
        when (X) {
            in 0f..(size.width / 3f) -> {
                return when (Y) {
                    in 0f..(size.height / 3f) -> {
                        1
                    }
                    in (size.height / 3f)..( 2 * size.height / 3f) -> {
                        4
                    }
                    else -> 7
                }
            }
            in (size.width / 3f)..(2 * size.width / 3f) -> {
                return when (Y) {
                    in 0f..(size.height / 3f) -> {
                        2
                    }
                    in (size.height / 3f)..(2 * size.height / 3f) -> {
                        5
                    }
                    else -> 8
                }
            }
            else -> {
                return when (Y) {
                    in 0f..(size.height / 3f) -> {
                        3
                    }
                    in (size.height / 3f)..(2 * size.width / 3f) -> {
                        6
                    }
                    else -> 9
                }
            }
        }
    } else return -1
}

private fun emptyAnimations(): ArrayList<ArrayList<Animatable<Float, AnimationVector1D>>> {
    val arrayList = arrayListOf<ArrayList<Animatable<Float, AnimationVector1D>>>()
    for (i in 0..2) {
        arrayList.add(arrayListOf())
        for (j in 0..2) {
            arrayList[i].add(Animatable(0f))
        }
    }
    return arrayList
}

private fun emptyGameState(): Array<CharArray> {
    return arrayOf(
        charArrayOf('E', 'E', 'E'),
        charArrayOf('E', 'E', 'E'),
        charArrayOf('E', 'E', 'E'),
    )
}

private fun updateGameState(gameState: Array<CharArray>, i: Int, j: Int, symbol: Char): Array<CharArray> {
    val arrayCopy = gameState.copyOf()
    arrayCopy[i][j] = symbol
    return arrayCopy
}

private fun CoroutineScope.animateFloatToOne(animatable: Animatable<Float, AnimationVector1D>) {
    launch {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 500
            )
        )
    }
}

private fun didPlayerWin(gameState: Array<CharArray>, player: Player): Boolean {
    val firstRowFull = gameState[0][0] == gameState[1][0] &&
            gameState[1][0] == gameState[2][0] && gameState[0][0] == player.symbol
    val secondRowFull = gameState[0][1] == gameState[1][1] &&
            gameState[1][1] == gameState[2][1] && gameState[0][1] == player.symbol
    val thirdRowFull = gameState[0][2] == gameState[1][2] &&
            gameState[1][2] == gameState[2][2] && gameState[0][2] == player.symbol

    val firstColFull = gameState[0][0] == gameState[0][1] &&
            gameState[0][1] == gameState[0][2] && gameState[0][0] == player.symbol
    val secondColFull = gameState[1][0] == gameState[1][1] &&
            gameState[1][1] == gameState[1][2] && gameState[1][0] == player.symbol
    val thirdColFull = gameState[2][0] == gameState[2][1] &&
            gameState[2][1] == gameState[2][2] && gameState[2][0] == player.symbol

    val firstDiagonalFull = gameState[0][0] == gameState[1][1] &&
            gameState[1][1] == gameState[2][2] && gameState[0][0] == player.symbol
    val secondDiagonalFull = gameState[0][2] == gameState[1][1] &&
            gameState[1][1] == gameState[2][0] && gameState[0][2] == player.symbol

    return firstRowFull || secondRowFull || thirdRowFull || firstColFull ||
            secondColFull || thirdColFull || firstDiagonalFull || secondDiagonalFull
}