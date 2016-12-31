package fitter.menu

case class ActionIntId(id: Int) extends ActionId {
  override def matches(command: String): Boolean = command equals id.toString
}
