package com.example.minesweeper.ui.screen

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minesweeper.R
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle


@Composable
fun MinesweeperScreen(
    modifier: Modifier = Modifier,
    viewModel: MinesweeperViewModel = viewModel()
) {

    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var gameState by remember { mutableStateOf(viewModel.gameState) }

    LaunchedEffect(viewModel.gameState) {
        gameState = viewModel.gameState
    }

    if (gameState != GameState.PLAY) {
        showDialog = true
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (gameState == GameState.WIN) stringResource(R.string.you_win) else stringResource(R.string.you_lose)) },
            text = { Text(if (gameState == GameState.WIN) stringResource(R.string.congratulations) else stringResource(R.string.try_again)) },
            confirmButton = {
                Button(onClick = {
                    gameState = GameState.PLAY
                    viewModel.resetGame()
                    showDialog = false
                }) {
                    Text(stringResource(R.string.reset))
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(stringResource(R.string.minesweeper), fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text(
            text = stringResource(R.string.flags_left) +
                    viewModel.remainingFlags,
                    fontStyle = FontStyle.Italic)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.flag_mode))
            Checkbox(
                checked = viewModel.gameMode == GameMode.FLAG,
                onCheckedChange = { isChecked ->
                    viewModel.gameMode = if (isChecked) GameMode.FLAG else GameMode.TRY
                }
            )
        }

        MinesweeperBoard(
            grid = viewModel.grid,
            onBoardCellClicked = { cell ->
                viewModel.onCellClicked(cell)
            }
        )
        Button(
            onClick = {
                viewModel.resetGame()
            }
        ) {
            Text(stringResource(R.string.reset))
        }
    }
}

@Composable
fun MinesweeperBoard(
    grid: Array<Array<Cell>>,
    onBoardCellClicked: (Cell) -> Unit
) {
    val cell: ImageBitmap = ImageBitmap.imageResource(R.drawable.cell)
    val mine: ImageBitmap = ImageBitmap.imageResource(R.drawable.mine)
    val flag: ImageBitmap = ImageBitmap.imageResource(R.drawable.flag)
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier
        .fillMaxWidth(0.8f)
        .aspectRatio(1f)
        .pointerInput(key1 = Unit) {

            detectTapGestures { offset ->
                Log.d("TAG", "MinesweeperScreen: ${offset.x}, ${offset.y} ")

                val row = (offset.x / (size.height / 5)).toInt()
                val col = (offset.y / (size.width / 5)).toInt()

                if (row in 0..4 && col in 0..4) {
                    onBoardCellClicked(grid[row][col])
                }
            }
        }

    ) {

        val gridSize = size.minDimension
        val cellSize = gridSize / 5


        for (i in 0..4) {
            for (j in 0..4) {
                val xOffset = i * cellSize.toInt()
                val yOffset = j * cellSize.toInt()
                drawImage(
                    image = cell,
                    srcOffset = IntOffset(0, 0),
                    dstOffset = IntOffset(xOffset, yOffset),
                    srcSize = IntSize(cell.width, cell.height),
                    dstSize = IntSize(cellSize.toInt(), cellSize.toInt())
                )
            }
        }


        for (i in 0..5) {
            drawLine(
                color = Color.Black,
                strokeWidth = 8f,
                pathEffect = PathEffect.cornerPathEffect(4f),
                start = androidx.compose.ui.geometry.Offset(cellSize * i, 0f),
                end = androidx.compose.ui.geometry.Offset(cellSize * i, gridSize)
            )
            drawLine(
                color = Color.Black,
                strokeWidth = 8f,

                start = androidx.compose.ui.geometry.Offset(0f, cellSize * i),
                end = androidx.compose.ui.geometry.Offset(gridSize, cellSize * i),
            )
        }

        for (i in 0..4) {
            for (j in 0..4) {
                val cell = grid[i][j]
                if (cell.isFlagged) {
                    drawImage(
                        image = flag,
                        srcOffset = IntOffset(0, 0),
                        dstOffset = IntOffset(i * cellSize.toInt(), j * cellSize.toInt()),
                        srcSize = IntSize(flag.width, flag.height),
                        dstSize = IntSize(cellSize.toInt(), cellSize.toInt())
                    )
                } else if (cell.isRevealed) {
                    if (cell.hasMine) {
                        drawImage(
                            image = mine,
                            srcOffset = IntOffset(0, 0),
                            dstOffset = IntOffset(i * cellSize.toInt(), j * cellSize.toInt()),
                            srcSize = IntSize(mine.width, mine.height),
                            dstSize = IntSize(cellSize.toInt(), cellSize.toInt())
                        )
                    } else if (cell.adjacentMines >= 0) {
                        val style = TextStyle.Default.copy(
                            color = Color.Black,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                            val measuredText =
                                textMeasurer.measure(
                                    cell.adjacentMines.toString(),
                                    style = style
                                )

                            drawText(
                                textMeasurer, cell.adjacentMines.toString(), Offset(
                                    i * cellSize + cellSize / 2 - measuredText.size.width / 2,
                                    j * cellSize + cellSize / 2 - measuredText.size.height / 2
                                ), style = style

                            )
                        }
                    }

                }
            }
        }
    }


