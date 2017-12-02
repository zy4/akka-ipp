[![Build Status](https://travis-ci.org/zy4/akka-ipp.svg?branch=master)](https://travis-ci.org/zy4/akka-ipp)
[![Build status](https://ci.appveyor.com/api/projects/status/odbi1gqfas4x5uq6?svg=true)](https://ci.appveyor.com/project/zy4/akka-ipp)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3cc32ff259cb4e68a178ac895290a944)](https://www.codacy.com/app/zy4/akka-ipp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=zy4/akka-ipp&amp;utm_campaign=Badge_Grade)

Introduction to akka-ipp
=========================

This library allows for basic tcp queries by targeting the [IPP/2.0](https://en.wikipedia.org/wiki/Internet_Printing_Protocol) specifications. 
There are still many features which are missing but akka-ipp can already be used to 

* query a printer's capabilities
* submit a job
* query the status of a job
* cancel jobs
* do some polling on the job state

If your printer speaks IPP, then you can use this library in your Scala code and just print without any driver.

Installation
============

We suggest that you clone this repository and publish it locally.

`sbt publishLocal`

After that, you can use it in your sbt by adding the following dependency:

`libraryDependencies += "de.envisia.akka" %% "akka-ipp" % "0.0.3"`


Usage
=====

Here is a simple usage example:

```scala
import de.envisia.akka.ipp.attributes.Attributes.WELL_KNOWN_PORT
import de.envisia.akka.ipp.IPPClient
import de.envisia.akka.ipp.Response
import de.envisia.akka.ipp.IPPConfig

import akka.actor.ActorSystem
import akka.stream.{ ActorMaterializer, Materializer }
import akka.http.scaladsl.Http
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration._

implicit val actorSystem: ActorSystem = ActorSystem()
implicit val mat: Materializer = ActorMaterializer()
implicit val executionContext: ExecutionContext = actorSystem.dispatcher

val http = Http()
val client = new IPPClient(http)(mat, executionContext)
val config = IPPConfig("192.168.123.123", WELL_KNOWN_PORT, "print", Some("username"), 2.seconds) // all params except ip are optional

// Getting the printer attributes

val attrs: Future[Response.GetPrinterAttributesResponse] 
  = client.printerAttributes(config) // returns the Future of a response object

// Printing

import akka.stream.scaladsl.FileIO
import akka.util.ByteString
import java.nio.file.{ Files, Paths }

val x: ByteString = ByteString(Files.readAllBytes(Paths.get("examples/pdf-sample.pdf")))
val printJob = client.printJob(x, config)  // returns the Future of a PrintJob response

// Getting the Job Attributes

val jobAttrs: Future[Response.GetJobAttributesResponse] 
  = client.getJobAttributes(42, config) // returns the response data as a Scala Future

// Polling

val jobData: Future[Response.JobData] = client.poll(42, config) // polls the state of some job by job id

// Cancelling a job

val resp: Future[Response.CancelJobResponse] = client.cancelJob(42, config)

```



Usage with Dependency Injection (Guice) in PLAY
===============================================
You can also use the library via DI, for example from your PLAY app.

```scala

@Singleton
class HttpExtProvider @Inject()(
  applicationLifecycle: ApplicationLifecycle
)(implicit actorSystem: ActorSystem, val executionContext: ExecutionContext, val mat: Materializer)
  extends Provider[HttpExt] {

  override lazy val get: HttpExt = {
    val innerHttp = Http()
    applicationLifecycle.addStopHook(() => innerHttp.shutdownAllConnectionPools())
    innerHttp
  }

}

@Singleton
class IppClientProvider @Inject()(
    http: HttpExt,
    applicationLifecycle: ApplicationLifecycle
)(implicit val executionContext: ExecutionContext, val mat: Materializer)
    extends Provider[IPPClient] {

  override lazy val get: IPPClient = {
    val innerClient = new IPPClient(http)
    applicationLifecycle.addStopHook(() => innerClient.shutdown())
    innerClient
  }

}


```

Now you can just bind the IPPClient to the Provider:

```scala

class YourModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    // IPP Printing Client
    bind(classOf[IPPClient]).toProvider(classOf[IppClientProvider])
    // Akka HttpExt
    bind(classOf[HttpExt]).toProvider(classOf[HttpExtProvider])
  }
}

```