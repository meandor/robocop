import java.io.FileNotFoundException
import java.security.MessageDigest

import com.typesafe.scalalogging.LazyLogging

import scala.collection.parallel.ParSeq
import scala.io.Source
import scala.language.postfixOps

object Robocop extends LazyLogging {

  type ShoppingList = ParSeq[String]

  val bucketPrefix = "robocop-"

  def studyName(filePath: String): String = {
    val pattern = "^.*_studies/(\\d+_\\w+)/.*$".r
    val pattern(study) = filePath
    study
  }

  def bucketName(filePath: String): String = {
    val study = studyName(filePath)
    bucketPrefix +
      MessageDigest.getInstance("MD5").digest(study.getBytes).map("%02x".format(_)).mkString
  }

  def copyToLocal(user: String, shoppingList: ShoppingList, tmpFolder: String, adapter: Cybernetics): ParSeq[String] = {
    shoppingList.map(shoppingListItem => {
      if (!adapter.fileExists(shoppingListItem)) {
        throw new FileNotFoundException("File was not found")
      }

      if (!adapter.hasAccess(user, shoppingListItem)) {
        throw new IllegalArgumentException("User has no access to that data")
      }

      if (!adapter.isCloudAllowed(shoppingListItem)) {
        throw new IllegalArgumentException("Dataset not allowed to be transferred to cloud")
      }

      adapter.downloadFile(shoppingListItem, tmpFolder)
    })
  }

  def parseShoppingList(shoppingListFilePath: String): ShoppingList = {
    Source.fromFile(shoppingListFilePath).getLines.filter(line => line != "").toSeq.par
  }

  def main(args: Array[String]): Unit = {
    val arguments = args.toSeq
    if (arguments.size != 2) {
      logger.warn("Usage: robocop <copyingUser> <shoppingList>")
    } else {
      try {
        val syncingUser = arguments.head
        val shoppingList = parseShoppingList(arguments.last)

        logger.info(s"Starting syncing as user: $syncingUser")
        logger.info(s"Syncing files: " + shoppingList.mkString("\n"))

        val localShoppingListItems = copyToLocal(syncingUser, shoppingList, s"./tmp/${System.currentTimeMillis()}", HTTPCybernetic)


      } catch {
        case _: FileNotFoundException => logger.warn("The specified shoppingList could not be found/accessed")
        case e: Throwable => logger.error(e.getMessage)
      }
    }
  }
}
