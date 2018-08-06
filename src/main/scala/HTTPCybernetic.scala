import java.io.File

import com.typesafe.scalalogging.LazyLogging

import scala.language.postfixOps

object HTTPCybernetic extends Cybernetics with LazyLogging {
  def download(url: String, filename: String) = {
    import java.net.URL

    import sys.process._
    try {
      new URL(url) #> new File(filename) !!
    } catch {
      case ex: Throwable =>
        logger.error("tmp folder does not exist", ex)
        System.exit(1)
    }
  }

  def downloadFile(url: String, tmpFolder: String): String = {
    val tmpFolderFile = new File(tmpFolder)
    tmpFolderFile.mkdirs()
    val filename = tmpFolder + "/" + url.split("/").last
    logger.debug(s"downloading file $filename")
    download(url, filename)
    filename
  }

  def hasAccess(user: String, target: Any): Boolean = {
    true
  }

  def isCloudAllowed(target: Any): Boolean = {
    true
  }

  def fileExists(target: Any): Boolean = {
    true
  }
}
