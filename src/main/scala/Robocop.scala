import java.io.FileNotFoundException

import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

object Robocop extends LazyLogging {

  def parseShoppingList(shoppingListFilePath: String): Seq[String] = {
    Source.fromFile(shoppingListFilePath).getLines.filter(line => line != "").toSeq
  }

  def main(args: Array[String]): Unit = {
    val arguments = args.toSeq
    if (arguments.size != 2) {
      logger.warn("Usage: robocop <copyingUser> <shoppingList>")
    } else {
      try {
        val syncingUser = arguments.head
        val toBeSyncedFiles = parseShoppingList(arguments.last)

        logger.info(s"Starting syncing as user: $syncingUser")
        logger.info(s"Syncing files: " + toBeSyncedFiles.mkString("\n"))
      } catch {
        case e: FileNotFoundException => logger.warn("The specified shoppingList could not be found/accessed")
        case e: Throwable => logger.error(e.getMessage)
      }
    }
  }
}
