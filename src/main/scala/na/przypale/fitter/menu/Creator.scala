package na.przypale.fitter.menu

import scala.collection.SortedMap

object Creator {
  val mainMenu = new Menu(SortedMap(
    1 -> Action("Create user", () => {}))
  )
}
