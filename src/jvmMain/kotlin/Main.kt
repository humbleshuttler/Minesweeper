import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App(gameBoard: GameBoard) {
  // Create a list of mutable state booleans to store the text visibility for each button
  val isButtonHidden = remember { mutableStateListOf<Boolean>().apply { repeat(81) { add(true) } } }
  LazyVerticalGrid(
      columns = GridCells.Fixed(9),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 50.dp)) {
        items(81) { index -> CellAsButton(index, gameBoard, isButtonHidden) }
      }
}

@Composable
fun CellAsButton(index: Int, gameBoard: GameBoard, isButtonHidden: SnapshotStateList<Boolean>) {
  Button(
      onClick = {
        val status = gameBoard.reveal(Pair(index / 9, index % 9))
        when (status) {
          GameBoard.GameStatus.NOOP -> println("nothing to do")
          GameBoard.GameStatus.RENDER_BOARD_AND_GAME_OVER -> {
            println("User loose! Game over")
            for (i in 0 until 81) {
              isButtonHidden[i] = gameBoard.getCell(i / 9, i % 9).hidden
            }
          }
          GameBoard.GameStatus.RENDER_BOARD -> {
            for (i in 0 until 81) {
              isButtonHidden[i] = gameBoard.getCell(i / 9, i % 9).hidden
            }
          }
          GameBoard.GameStatus.USER_WINS -> {
            println("Congratualtions! you win!!!")
          }
        }
      },
      shape = RoundedCornerShape(1.dp),
      modifier =
          Modifier.aspectRatio(1f)
              .clickable(enabled = true, onClick = { println("print from modifier") }),
      colors =
          ButtonDefaults.buttonColors(
              backgroundColor = Color.Gray,
              contentColor = Color.Black,
          )) {
        Text(
            if (!isButtonHidden[index]) gameBoard.getCell(index / 9, index % 9).value.toString()
            else "")
      }
}

@Composable
fun CellWithIcon(src: String, alt: String) {
  Text("Flag")
  //    Image(
  //        painter = loadImage(src),
  //        contentDescription = alt,
  //        modifier = Modifier.fillMaxSize().padding(4.dp)
  //    )
}

@Composable
fun Flag() {
  CellWithIcon(src = "assets/flag.png", alt = "Flag")
}

fun main() = application {
  val gameBoard: GameBoard = GameBoard(9, 9, 10)
  Window(onCloseRequest = ::exitApplication, title = "Minesweeper") {
    MaterialTheme { App(gameBoard) }
  }
}
