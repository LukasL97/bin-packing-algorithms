package demo

import kantan.csv._
import kantan.csv.ops._
import kantan.csv.rfc
import models.problem.binpacking.BinPacking
import models.problem.binpacking.BinPackingInstance
import models.problem.binpacking.greedy.BinPackingGreedy
import models.problem.binpacking.greedy.BoxClosingBinPackingGreedy
import models.problem.binpacking.greedy.candidatesupported.SizeOrderedBinPackingGreedy
import models.problem.binpacking.localsearch.BinPackingLocalSearch
import models.problem.binpacking.localsearch.RectanglePermutationBinPacking
import models.problem.binpacking.localsearch.TopLeftFirstBoxMergingBinPacking
import models.problem.binpacking.localsearch.TopLeftFirstOverlappingBinPacking
import scopt.OptionParser

import java.io.File

object DemoCli extends App {

  private val parser: OptionParser[DemoCliConfig] = new OptionParser[DemoCliConfig]("demo-cli") {
    opt[String]("configPath").action((v, c) => c.copy(demoConfigPath = v)).required().text("Path to demo config csv")
    opt[String]("outPath").action((v, c) => c.copy(outPath = v)).required().text("Path to output csv file")
    opt[Int]("timeLimit")
      .action((v, c) => c.copy(timeLimitMillis = Option(v)))
      .optional()
      .text("Local search time limit in ms")
    opt[Int]("maxSteps")
      .action((v, c) => c.copy(localSearchMaxSteps = v))
      .optional()
      .text("Local search maximum steps")
  }

  private def getTheoreticalBoxesLowerBound(instance: BinPackingInstance): Int = {
    val rectangleArea = instance.rectangles.map(_.getArea).sum
    val boxArea = instance.boxLength * instance.boxLength
    Math.ceil(rectangleArea.toDouble / boxArea).toInt
  }

  private def run(binPacking: BinPacking, localSearchMaxSteps: Int, timeLimitMillis: Option[Int]): DemoRunResult = {
    val startTime = System.currentTimeMillis()
    val solution = binPacking match {
      case binPacking: BinPackingGreedy[_] => binPacking.greedy.run()
      case binPacking: BinPackingLocalSearch[_] => binPacking.localSearch.run(localSearchMaxSteps, timeLimitMillis)
    }
    val timeUsed = (System.currentTimeMillis() - startTime).toDouble / 1000
    val boxesUsed = solution.getPlacementsPerBox.keys.max
    DemoRunResult(binPacking.getClass.getSimpleName, binPacking.instance, boxesUsed, timeUsed)
  }

  private implicit val demoConfigRowDecoder: RowDecoder[DemoConfigRow] =
    RowDecoder.decoder(0, 1, 2, 3, 4, 5, 6)(DemoConfigRow.apply)

  parser.parse(args, DemoCliConfig("", "", None, 10000)).foreach { config =>
    val file = new File(config.demoConfigPath)
    val reader = file.asCsvReader[DemoConfigRow](rfc.withHeader)

    val instances = reader.map {
      case Left(error) => throw new IllegalArgumentException(s"Parsing config failed: ${error.getMessage}")
      case Right(DemoConfigRow(numInstances, boxLength, numRectangles, minWidth, maxWidth, minHeight, maxHeight)) =>
        (1 to numInstances).map(
          _ => BinPackingInstance(boxLength, numRectangles, minWidth, maxWidth, minHeight, maxHeight)
        )
    }.toSeq.flatten

    val demoRunResults = instances.zipWithIndex.map {
      case (instance, index) =>
        println(
          s"\nExecute algorithms on instance ${index + 1}/${instances.size} ${instance.toTupleString} with lower bound ${getTheoreticalBoxesLowerBound(instance)} boxes"
        )
        val algorithms = Seq(
          new SizeOrderedBinPackingGreedy(instance),
          new BoxClosingBinPackingGreedy(instance),
          new TopLeftFirstBoxMergingBinPacking(instance),
          new RectanglePermutationBinPacking(instance),
          new TopLeftFirstOverlappingBinPacking(instance)
        )
        algorithms.map { algorithm =>
          val result = run(algorithm, config.localSearchMaxSteps, config.timeLimitMillis)
          println(s"${algorithm.getClass.getSimpleName}: ${result.boxesUsed} boxes - ${result.timeUsed} seconds")
          result
        }
    }

    val algorithmNames = demoRunResults.head.map(_.algorithm)
    val header = Seq(
      "boxLength",
      "numRectangles",
      "minWidth",
      "maxWidth",
      "minHeight",
      "maxHeight",
      "theoretical lower bound"
    ) ++ algorithmNames.flatMap(name => Seq(s"$name (boxes)", s"$name (time)"))

    val out = new File("/home/lukas/Documents/bin-packing-algorithms/bin-packing-backend-play/resources/out.csv")
    val writer = out.asCsvWriter[Seq[Double]](rfc.withHeader(header: _*))
    demoRunResults.foreach { results =>
      val instance = results.head.instance
      val instanceRow = Seq(
        instance.boxLength,
        instance.numRectangles,
        instance.minWidth,
        instance.maxWidth,
        instance.minHeight,
        instance.maxHeight,
        getTheoreticalBoxesLowerBound(instance)
      ).map(_.toDouble)
      val algorithmsRow = results.flatMap(result => Seq(result.boxesUsed, result.timeUsed))
      writer.write(instanceRow ++ algorithmsRow)
    }
    writer.close()

  }

}

case class DemoCliConfig(
  demoConfigPath: String,
  outPath: String,
  timeLimitMillis: Option[Int],
  localSearchMaxSteps: Int
)

case class DemoConfigRow(
  numInstances: Int,
  boxLength: Int,
  numRectangles: Int,
  minWidth: Int,
  maxWidth: Int,
  minHeight: Int,
  maxHeight: Int
)

case class DemoRunResult(
  algorithm: String,
  instance: BinPackingInstance,
  boxesUsed: Int,
  timeUsed: Double
)
