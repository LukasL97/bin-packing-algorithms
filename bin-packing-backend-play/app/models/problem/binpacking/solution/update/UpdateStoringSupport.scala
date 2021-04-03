package models.problem.binpacking.solution.update

trait UpdateStoringSupport[A <: UpdateStoringSupport[A]] {

  val update: Update

  def setUpdate(update: Update): A
}
