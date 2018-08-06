import java.io.File
import java.security.MessageDigest

import Sink.TransferItem
import com.amazonaws.AmazonServiceException
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.transfer.{TransferManager, TransferManagerBuilder}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.typesafe.scalalogging.LazyLogging

object Target extends LazyLogging {

  val bucketPrefix: String = "robocop-"
  val s3: AmazonS3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).build()
  val multipartThreshold: Long = 100 * 1024 * 1025

  def bucketName(filePath: String): String = {
    val study = Sink.studyName(filePath)
    bucketPrefix +
      MessageDigest.getInstance("MD5").digest(study.getBytes).map("%02x".format(_)).mkString
  }

  def uploadFileToS3(transferItem: TransferItem): Boolean = {
    val localFile = new File(transferItem.localPath)
    val targetBucket = bucketName(transferItem.originalPath)

    if (!s3.doesBucketExistV2(targetBucket)) {
      throw new IllegalArgumentException(s"Bucket ($targetBucket) does not exist")
    }

    val key = transferItem.localPath.split("/").last
    val transferManager: TransferManager = TransferManagerBuilder.standard().withS3Client(s3).withMultipartUploadThreshold(multipartThreshold).build()

    try {
      val transfer = transferManager.upload(targetBucket, key, localFile)
      TransferManagerProgress.showTransferProgress(transfer)
      TransferManagerProgress.waitForCompletion(transfer)
      true
    } catch {
      case ex: AmazonServiceException =>
        logger.error(ex.getErrorMessage)
        false
      case ex: Throwable =>
        logger.error(ex.getMessage, ex)
        false
    }
  }
}
