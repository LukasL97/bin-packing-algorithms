package models.problem

import models.algorithm.LocalSearch
import models.algorithm.SolutionHandler

class Knapsack(
  weights: Seq[Double],
  values: Seq[Double],
  maxWeight: Double
) {

  require(weights.length == values.length)

  case class KnapsackObject(value: Double, weight: Double)

  object KnapsackSolutionHandler extends SolutionHandler[KnapsackSolution] {

    override val startSolution: KnapsackSolution = KnapsackSolution(Seq.fill(weights.length)(false))

    private def isFeasible(solution: KnapsackSolution): Boolean = solution.getWeight <= maxWeight

    override def getNeighborhood(solution: KnapsackSolution): Set[KnapsackSolution] =
      solution.contains.zipWithIndex.map {
        case (doesContain, index) =>
          solution.contains.updated(index, !doesContain)
      }.map(KnapsackSolution)
        .filter(isFeasible)
        .toSet

    override def evaluate(solution: KnapsackSolution): BigDecimal = {
      val overallValue = (solution.contains zip values).collect { case (doesContain, value) if doesContain => value }.sum
      -overallValue
    }
  }

  case class KnapsackSolution(contains: Seq[Boolean]) {

    def getWeight: Double = (contains zip weights).collect { case (doesContain, weight) if doesContain => weight }.sum

    override def toString: String = {
      val columnWidths = (weights zip values).map {
        case (weight, value) => Set(weight, value).map(_.toString.length).max
      }
      val weightLine = "weights | " + weights
        .map(_.toString)
        .zipWithIndex
        .map {
          case (weight, index) => " " * (columnWidths(index) - weight.length) + weight
        }
        .mkString(" ") + " | " + getWeight
      val valueLine = "values  | " + values
        .map(_.toString)
        .zipWithIndex
        .map {
          case (value, index) => " " * (columnWidths(index) - value.length) + value
        }
        .mkString(" ") + " | " + -KnapsackSolutionHandler.evaluate(this)
      val containsLine = "          " + contains.zipWithIndex.map {
        case (doesContain, index) if doesContain => " " * (columnWidths(index) - 1) + "x"
        case (_, index) => " " * columnWidths(index)
      }.mkString(" ") + " | "
      Seq(weightLine, valueLine, containsLine).mkString("\n")
    }
  }

  val localSearch = new LocalSearch[KnapsackSolution](KnapsackSolutionHandler)

}

object Knapsack {

  val problem = new Knapsack(
    weights = Seq(2, 3, 5, 7, 1, 4, 1),
    values = Seq(10, 5, 15, 7, 6, 18, 3),
    maxWeight = 15
  )

  def main(args: Array[String]): Unit = {
    problem.localSearch.run(100, (solution, step, finished) => println(solution.toString + "\n\n"))
  }
}
