package de.envisia.util

import java.util.concurrent.atomic.AtomicInteger

object RequestId {

  var atomicInt = new AtomicInteger(1)
  var counter = 1

  def getAndIncrement: Int = {
    val res = counter
    counter += 1
    res
  }

}
