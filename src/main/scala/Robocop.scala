import java.io.{File, FileNotFoundException}

import com.typesafe.scalalogging.LazyLogging

import scala.collection.parallel.ParSeq
import scala.io.Source
import scala.language.postfixOps

object Robocop extends LazyLogging {

  type ShoppingList = ParSeq[String]

  def download(url: String, filename: String) = {
    import java.net.URL

    import sys.process._
    new URL(url) #> new File(filename) !!
  }

  def downloadFile(url: String, tmpFolder: String): String = {
    val filename = tmpFolder + "/" + url.split("/").last
    download(url, filename)
    filename
  }

  def copyToLocal(shoppingList: ShoppingList, tmpFolder: String): ParSeq[String] = {
    shoppingList.map(shoppingListItem => downloadFile(shoppingListItem, tmpFolder))
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

        val localShoppingListItems = copyToLocal(shoppingList, s"./tmp/${System.currentTimeMillis()}")


      } catch {
        case e: FileNotFoundException => logger.warn("The specified shoppingList could not be found/accessed")
        case e: Throwable => logger.error(e.getMessage)
      }
    }
  }
}

//  val s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).build()
//
//  val buckets = s3.listBuckets()
//  System.out.println("Your Amazon S3 buckets are:")
//
//  import scala.collection.JavaConversions._
//
//  for (b <- buckets) {
//    System.out.println("* " + b.getName)
//  }
