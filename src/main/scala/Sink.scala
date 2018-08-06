import java.io.FileNotFoundException

import Robocop.ShoppingList

import scala.collection.parallel.ParSeq

object Sink {

  def studyName(filePath: String): String = {
    val pattern = "^.*_studies/(\\d+_\\w+)/.*$".r
    val pattern(study) = filePath
    study
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
}
