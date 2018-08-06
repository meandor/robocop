trait Cybernetics {
  def downloadFile(url: String, tmpFolder: String): String

  def hasAccess(user: String, target: Any): Boolean

  def isCloudAllowed(target: Any): Boolean

  def fileExists(target: Any): Boolean
}
