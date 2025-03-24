package com.example.minesweeper.ui.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random


enum class GameMode {
    TRY, FLAG
}

enum class GameState {
    PLAY, LOSE, WIN
}

data class Cell(
    val row: Int,
    val col: Int,
    var hasMine: Boolean = false,
    var isRevealed: Boolean = false,
    var adjacentMines: Int = 0,
    var isFlagged: Boolean = false
)


class MinesweeperViewModel : ViewModel() {

    var grid by mutableStateOf(Array(5) { Array(5) { Cell(it, it) } })
    var gameMode by mutableStateOf(GameMode.TRY)
    var gameState by mutableStateOf(GameState.PLAY)
    var remainingFlags by mutableStateOf(3)

    init {
        resetGame()
    }

    fun placeMines() {
        val random = Random
        var minesPlaced = 0
        while (minesPlaced < 3) {
            val row = random.nextInt(5)
            val col = random.nextInt(5)
            if (!grid[row][col].hasMine) {
                grid[row][col].hasMine = true
                minesPlaced++
            }
        }
    }

    fun calculateAdjacentMines() {
        for (r in 0..4) {
            for (c in 0..4) {
                if (grid[r][c].hasMine) continue
                var adjacentMines = 0
                for (i in -1..1) {
                    for (j in -1..1) {
                        if (r + i in 0..4 && c + j in 0..4 && grid[r + i][c + j].hasMine) {
                            adjacentMines++
                        }
                    }
                }
                grid[r][c].adjacentMines = adjacentMines
            }
        }
    }


    fun revealCell(row: Int, col: Int) {
        val newGrid = grid.copyOf()
        if (newGrid[row][col].isRevealed || newGrid[row][col].isFlagged) return

        if (newGrid[row][col].hasMine) {
            newGrid[row][col].isRevealed = true
            gameState = GameState.LOSE
        } else {
            newGrid[row][col].isRevealed = true
            grid = newGrid
        }
        if (checkWin()) {
            gameState = GameState.WIN
        }
    }

    fun flagCell(row: Int, col: Int) {
        val newGrid = grid.copyOf()
        if (newGrid[row][col].isRevealed) return

        if (remainingFlags == 0 && !newGrid[row][col].isFlagged) return

        newGrid[row][col].isFlagged = !newGrid[row][col].isFlagged

        if (newGrid[row][col].isFlagged) {
            remainingFlags--
        } else {
            remainingFlags++
        }

        if (checkWin()) {
            gameState = GameState.WIN
        }
    }

    fun onCellClicked(cell: Cell) {
        if (gameState != GameState.PLAY || cell.isRevealed) return
        if (cell.isFlagged) !cell.isFlagged
        when (gameMode) {
            GameMode.TRY -> revealCell(cell.row, cell.col)
            GameMode.FLAG -> flagCell(cell.row, cell.col)
        }
    }


    fun resetGame() {
        grid = Array(5) { row ->
            Array(5) { col ->
                Cell(row, col) }}
        placeMines()
        calculateAdjacentMines()
        gameState = GameState.PLAY
        gameMode = GameMode.TRY
        remainingFlags = 3
    }

    fun checkWin() : Boolean {
        var flagCounter = 0
        var mineCounter = 0
        var allNonMineCellsRevealed = true

        for (row in 0..4) {
            for (col in 0 .. 4) {
                val cell = grid[row][col]
                if (cell.hasMine && cell.isFlagged) {
                    flagCounter++
                }
                if (cell.hasMine) {
                    mineCounter++
                }
                if (!cell.hasMine && !cell.isRevealed) {
                    allNonMineCellsRevealed = false
                }

            }
        }
        return flagCounter == mineCounter || allNonMineCellsRevealed
    }

}