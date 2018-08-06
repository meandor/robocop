class FauxCybernetic(hasAccessFlag: Boolean, isCloudAllowedFlag: Boolean, fileExistsFlag: Boolean) extends Cybernetics {
  
  override def downloadFile(url: String, tmpFolder: String): String = {
    println(url)
    url
  }

  override def hasAccess(user: String, target: Any): Boolean = hasAccessFlag

  override def isCloudAllowed(target: Any): Boolean = isCloudAllowedFlag

  override def fileExists(target: Any): Boolean = fileExistsFlag
}
