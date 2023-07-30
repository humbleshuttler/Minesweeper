import java.time.Instant
import java.util.Random

class GameBoard(val r: Int, val c: Int, private val mines: Int) {
  private val cells = Array(r) { Array(c) { Cell(true, 0) } }
  private val random: Random = Random(Instant.now().toEpochMilli())

  init {
    for (i in 1..mines) {
      var randomCell: Pair<Int, Int> = Pair(-1, -1)
      while (true) {
        randomCell = getRandomCell(r, c)
        if (cells[randomCell.first][randomCell.second].value == 0) {
          break
        }
      }
      cells[randomCell.first][randomCell.second] = Cell(value = -1)
    }
    for (i in 0 until r) {
      for (j in 0 until c) {
        if (cells[i][j].containsMine()) {
          continue
        }
        cells[i][j] = Cell(true, getSurroundingMines(i, j))
      }
    }
  }

  fun getCell(row: Int, col: Int) = cells[row][col]

  fun reveal(coordinate: Pair<Int, Int>): GameStatus {
    if (!getCell(coordinate.first, coordinate.second).hidden) {
      // the cell is already revealed; take no action
      // may be change the button to not clickable; if possible
      return GameStatus.NOOP
    }
    if (getCell(coordinate.first, coordinate.second).containsMine()) {
      cells[coordinate.first][coordinate.second].unhide()
      // reveal all mines
      for (i in 0 until r) {
        for (j in 0 until c) {
          if (cells[i][j].hidden && cells[i][j].containsMine()) {
            cells[i][j].unhide()
          }
        }
      }
      // return game end
      return GameStatus.RENDER_BOARD_AND_GAME_OVER
    }
    if (getCell(coordinate.first, coordinate.second).value != 0) {
      getCell(coordinate.first, coordinate.second).unhide()
      return GameStatus.RENDER_BOARD
    }
    // this is an empty cell, perform reveal expansion
    revealRecursive(coordinate)
    return GameStatus.RENDER_BOARD
  }

  fun revealRecursive(coordinate: Pair<Int, Int>) {
    if (coordinate.first !in 0 until r ||
        coordinate.second !in 0 until c ||
        !(getCell(coordinate.first, coordinate.second).hidden)) {
      return
    }
    getCell(coordinate.first, coordinate.second).unhide()
    if (getCell(coordinate.first, coordinate.second).value > 0) {
      return
    }
    // empty cell
    for (i in dir.indices) {
      revealRecursive(Pair(coordinate.first + dir[i].first, coordinate.second + dir[i].second))
    }
  }

  private fun getSurroundingMines(x: Int, y: Int): Int {
    var total = 0
    for (i in dir.indices) {
      if ((x + dir[i].first in 0 until r) &&
          (y + dir[i].second in 0 until c) &&
          cells[x + dir[i].first][y + dir[i].second].containsMine()) {
        total++
      }
    }
    return total
  }

  private fun getRandomCell(r: Int, c: Int): Pair<Int, Int> {
    return Pair(random.nextInt(r), random.nextInt(c))
  }

  private companion object {
    val dir: Array<Pair<Int, Int>> =
        arrayOf(
            Pair(-1, -1),
            Pair(-1, 0),
            Pair(-1, 1),
            Pair(0, -1),
            Pair(0, 1),
            Pair(1, -1),
            Pair(1, 0),
            Pair(1, 1))
  }

  enum class GameStatus {
    NOOP,
    RENDER_BOARD_AND_GAME_OVER,
    USER_WINS,
    RENDER_BOARD
  }
}

/** 0,0 0,1 0,2 1,0 1,1 1,2 2,0 2,1 2,2 */
