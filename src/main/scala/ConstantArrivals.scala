import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import scala.math.random

object ConstantArrivals {
	def main(args: Array[String]) {
		val conf = new SparkConf().setMaster("local[1]").setAppName("Spark Pi")
		println("*** got conf ***")
		val spark = new SparkContext(conf)
		println("*** got spark context ***")
		val slices = if (args.length > 0) args(0).toInt else 2
		val n = math.min(100000L * slices, Int.MaxValue).toInt
		val count = spark.parallelize(1 until n, slices).map { i =>
      val x = random * 2 - 1
      val y = random * 2 - 1
      if (x*x + y*y < 1) 1 else 0
    }.reduce(_ + _)
    println("Pi is roughly " + 4.0 * count / n)
    spark.stop()
	}
}


