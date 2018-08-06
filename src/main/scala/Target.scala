import java.security.MessageDigest

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}

object Target {

  val bucketPrefix: String = "robocop-"
  val s3: AmazonS3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).build()

  def bucketName(filePath: String): String = {
    val study = Sink.studyName(filePath)
    bucketPrefix +
      MessageDigest.getInstance("MD5").digest(study.getBytes).map("%02x".format(_)).mkString
  }
}
