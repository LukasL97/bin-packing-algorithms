package models.problem.binpacking.utils

object PairBuildingUtil {

  def buildPairs[A](elements: Iterable[A]): Seq[(A, A)] = {
    val elementsSeq = elements.toSeq
    elementsSeq.zipWithIndex.flatMap {
      case (elementA, index) => elementsSeq.take(index).map(elementB => (elementA, elementB))
    }
  }

}
