package sparkarrivals;

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import java.util.concurrent.CountDownLatch
import org.apache.spark.scheduler
import org.apache.spark.scheduler.TaskSchedulerImpl
import org.apache.spark.scheduler._
import scala.util.Random

import scala.math.random

object RandomReducer {
  

  /**
   * sbt "run-main RandomReducer 4 10000"
   * sbt "run-main RandomReducer 4 100000 1000"
   */
  def main(args: Array[String]) {
	  //val conf = new SparkConf().setMaster("local[2]").setAppName("RandomReducer")
	  val conf = new SparkConf().setAppName("RandomReducer")
	  println("*** got conf ***")
		val spark = new SparkContext(conf)
		println("*** got spark context ***")
		
		
		// give the system a little time for the executors to start... 30 seconds?
		// this is stupid b/c the full set of executors actually take less tha 1s to start
		print("Waiting a moment to let the executors start...")
		Thread sleep 1000*5
		println("done waiting!\n")
		
	  val numPartitions = if (args.length > 0) args(0).toInt else 1
	  val recordsPerPartition = if (args.length > 1) args(1).toInt else 1000000
	  val numKeys = if (args.length > 2) args(2).toInt else 1000
	  
		val distData = spark.parallelize(Seq[Int](), numPartitions)
  		.mapPartitions { _ => {
	  		(1 to recordsPerPartition).map{_ => (Random.nextInt(numKeys), 1)}.iterator
		  }}
	  //distData.take(100).foreach(println)

	  // force the data to be computed and cached across the cluster
	  println("persist distData...")
	  distData.persist()
	  //distData.take(2).foreach(println)
	  //distData.reduce((a,b) => (0,0))
	  distData.reduceByKey((a,b) => 0).take(1)
	  println("done!")

	  
	  // the idea was that by computing different things in the reduce, the
	  // optimizastion of the repeated reduces wouldn't be very good, but
	  // it is the same as if we computed the same thing three times.  Maybe
	  // the shuffle operation only has to be done once.  Or once the datas are
	  // sorted out by keys on each executor it doesn't have to be done again?
	  println("reducing by key 1 ...")
	  val summedData = distData.mapValues(x => Random.nextInt()).reduceByKey((a,b) => a + b)
	  //summedData.take(100).foreach(println)
	  println(summedData.count())

	  println("reducing by key 2 ...")
	  val summedData2 = distData.mapValues(x => Random.nextInt()).reduceByKey((a,b) => a + 2*b)
	  //summedData.take(100).foreach(println)
	  println(summedData2.count())

	  println("reducing by key 3 ...")
	  val summedData3 = distData.mapValues(x => Random.nextInt()).reduceByKey((a,b) => 2*a + b)
	  //summedData.take(100).foreach(println)
	  println(summedData3.count())

	  // ===============================---------------------
	  println("now jumble the keys too ...")
	  println("reducing by key 4 ...")
	  val summedData4 = distData.values.map(x => (Random.nextInt(numKeys), Random.nextInt())).reduceByKey((a,b) => a + b)
	  //summedData.take(100).foreach(println)
	  println(summedData4.count())

	  println("reducing by key 5 ...")
	  val summedData5 = distData.values.map(x => (Random.nextInt(numKeys), Random.nextInt())).reduceByKey((a,b) => a + 2*b)
	  //summedData.take(100).foreach(println)
	  println(summedData5.count())

	  println("reducing by key 6 ...")
	  val summedData6 = distData.values.map(x => (Random.nextInt(numKeys), Random.nextInt())).reduceByKey((a,b) => 2*a + b)
	  //summedData.take(100).foreach(println)
	  println(summedData6.count())
	  
	  // ===============================---------------------
	  println("now jumble the keys and do groupByKey ...")
	  println("grouping by key 7 ...")
	  val summedData7 = distData.values.map(x => (Random.nextInt(numKeys), Random.nextInt())).groupByKey(numPartitions)
	  println(summedData7.count())

	  println("grouping by key 8 ...")
	  val summedData8 = distData.values.map(x => (Random.nextInt(numKeys), Random.nextInt())).groupByKey(numPartitions)
	  println(summedData8.count())

	  println("grouping by key 9 ...")
	  val summedData9 = distData.values.map(x => (Random.nextInt(numKeys), Random.nextInt())).groupByKey(numPartitions)
	  println(summedData9.count())
	  
	  // ===============================---------------------
	  println("repeatedly do groupByKey on cached data...")
	  val summedData10 = distData.values.map(x => (Random.nextInt(numKeys), Random.nextInt()))
	  summedData10.persist()
	  println("grouping by key 10 ...")
	  println(summedData10.groupByKey(numPartitions).count())

	  println("grouping by key 11 ...")
	  println(summedData10.groupByKey(numPartitions).count())

	  println("grouping by key 12 ...")
	  println(summedData10.groupByKey(numPartitions).count())
	  
	  //println("making 
	  //distData.map(x => (Random.nextInt(numKeys), x))
	  
	  
		println("*** FINISHED!! ***")
		
		Thread sleep 1000*5

		println("*** stopping spark ***")
		spark.stop()
  }
}


