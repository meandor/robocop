import java.io.FileNotFoundException

import com.typesafe.scalalogging.LazyLogging

import scala.collection.parallel.ParSeq
import scala.io.Source
import scala.language.postfixOps

object Robocop extends LazyLogging {

  type ShoppingList = ParSeq[String]

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

        val tmpFolder = s"./tmp/${System.currentTimeMillis()}"
        val localShoppingListItems = Sink.copyToLocal(syncingUser, shoppingList, tmpFolder, HTTPCybernetic)


      } catch {
        case _: FileNotFoundException => logger.warn("The specified shoppingList could not be found/accessed")
        case e: Throwable => logger.error(e.getMessage)
      }
    }
  }
}
