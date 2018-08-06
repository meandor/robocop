import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

object Robocop extends LazyLogging {

  def parseShoppingList(shoppingListFilePath: String): Seq[String] = {
    Source.fromFile(shoppingListFilePath).getLines.filter(line => line != "").toSeq
  }

  def main(args: Array[String]): Unit = {
    val arguments = args.toSeq
    if (arguments.size != 2) {
      print("Usage: robocop <copyingUser> <shoppingList>")
    } else {
      val syncingUser = arguments.head
      val toBeSyncedFiles = parseShoppingList(arguments.last)

      logger.info(s"Starting syncing as user: $syncingUser")
      logger.info(s"Syncing files: " + toBeSyncedFiles.mkString("\n"))
    }
  }
}
