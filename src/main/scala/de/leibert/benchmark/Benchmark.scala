package de.leibert.benchmark

import actors.Actor
import org.clapper.argot.{ArgotConverters, ArgotParser}
import ArgotConverters._
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.CountDownLatch
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics
import scala.Predef._
import dispatch._

/**
 * Simple tool for benchmarking http services
 *
 * @author Florian Leibert (flo@leibert.de)
 */

object Benchmark {
  val parser = new ArgotParser(getClass().toString(), preUsage = Some("Version 1.0, by Flo"))

  val urlStr = parser.option[String](List("url"),
    "url", "The url to perform the query on")

  val throughput = parser.option[Int](List("throughput"),
    "throughput", "The target throughput")

  val actors = parser.option[Int](List("num_actors"),
    "num_actors", "The number of actors")

  val numRequestsPerActor = parser.option[Int](List("requests_per_actor"),
    "requests_per_actor", "The number of requests per actor")

  val counter = new AtomicLong(0)

  val stats = new SynchronizedDescriptiveStatistics()

  class Connection(h: Http, fun: Function[Http, Unit], latch: CountDownLatch) extends Actor {
    def act() {
      var requests = numRequestsPerActor.value.get
      var totalNanos = 0L
      loop {
        if (requests == 0) {
          val reqPerSec = numRequestsPerActor.value.get /
            (totalNanos.toDouble / 1000.toDouble / 1000.toDouble / 1000.toDouble)
          println(reqPerSec)
          latch.countDown()
          exit()
        }
        requests -= 1
        val t1 = System.nanoTime()
        fun.apply(h)
        val t2 = System.nanoTime() - t1
        totalNanos += t2
        val millis = (t2.toDouble / 1000000.toDouble)
        stats.addValue(millis)
      }
    }
  }

  def main(args: Array[String]) = {
    try {
      parser.parse(args)
      assert(!actors.value.isEmpty, "num_actors not set")
      assert(!urlStr.value.isEmpty, "url not set")
      assert(!numRequestsPerActor.value.isEmpty, "requests per actor not set")
      val latch = new CountDownLatch(actors.value.get);
      for (i <- 0 until actors.value.get) {
        //Fire off actor
        val current = new Connection(new Http, getFun(), latch).start()
      }
      latch.await()
      println("MEAN:\t\t\t" + stats.getMean)
      println("50th:\t\t\t" + stats.getPercentile(50))
      println("90th:\t\t\t" + stats.getPercentile(90))
      println("95th:\t\t\t" + stats.getPercentile(95))
      println("99th:\t\t\t" + stats.getPercentile(99))
      println("99.9th:\t\t\t" + stats.getPercentile(99.9))
      println("99.999th:\t\t" + stats.getPercentile(99.999))
    }
  }

  def getFun(): Function[Http, Unit] = {
      return {
        h: Http =>
          val f = h(url(urlStr.value.get) as_str)
      }
  }
}
