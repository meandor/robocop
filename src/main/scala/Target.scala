import java.security.MessageDigest

object Target {
  val bucketPrefix = "robocop-"

  def bucketName(filePath: String): String = {
    val study = Sink.studyName(filePath)
    bucketPrefix +
      MessageDigest.getInstance("MD5").digest(study.getBytes).map("%02x".format(_)).mkString
  }
}
