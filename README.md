[![Build status](https://ci.appveyor.com/api/projects/status/odbi1gqfas4x5uq6?svg=true)](https://ci.appveyor.com/project/zy4/akka-ipp)

Introduction to akka-ipp
=========================

This library allows for basic tcp queries by targeting the [IPP/2.0](https://en.wikipedia.org/wiki/Internet_Printing_Protocol) specifications. 
There are still many features which are missing but it can already be used to query 

* a printer's capabilities
* submit a job
* query the status of the job
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
import de.envisia.akka.ipp.services.IPPClient
import de.envisia.akka.ipp.Response
import de.envisia.akka.ipp.model.IppConfig

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
val config = IppConfig("192.168.123.123", WELL_KNOWN_PORT, "print", Some("username"), 2.seconds)

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
