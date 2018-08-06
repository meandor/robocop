import java.io.File

import scala.language.postfixOps

object HTTPCybernetic extends Cybernetics {
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
