package de.envisia

import scala.concurrent.Future

trait Request[A] {

  def getJobAttributes(f: A): List[String]
  def validate(f: A): Unit
  def sendDocument(f: A): Unit
  def CreateJob(f: A): Unit
  def checkSuccess(f: A): Future[Boolean]
  def cancelJob(f: A): Unit

}
