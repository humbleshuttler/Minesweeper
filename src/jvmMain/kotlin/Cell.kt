/**
 * A value of -1 means the cell has a mine.
 */
data class Cell(var hidden: Boolean = true, var value: Int) {
  fun unhide() {
    this.hidden = false
  }

  fun containsMine(): Boolean {
    return value == -1
  }
}
