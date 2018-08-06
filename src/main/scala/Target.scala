import java.io.File
import java.security.MessageDigest

import Robocop.ShoppingList
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}

object Target {

  val bucketPrefix: String = "robocop-"
  val s3: AmazonS3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).build()

  def bucketName(filePath: String): String = {
    val study = Sink.studyName(filePath)
    bucketPrefix +
      MessageDigest.getInstance("MD5").digest(study.getBytes).map("%02x".format(_)).mkString
  }

//  def uploadFileToS3(localFilePath: String, originalFilePath: String) = {
//    val localFile = new File(localFilePath)
//    val localFileBucket = bucketName()
//    val transferManager = TransferManagerBuilder.standard().build()
//
//    try {
//      MultipleFileUpload xfer = transferManager.uploadFileList(bucket_name,
//        key_prefix, new File("."), files);
//      XferMgrProgress.showTransferProgress(xfer);
//      XferMgrProgress.waitForCompletion(xfer);
//    } catch (AmazonServiceException e) {
//      System.err.println(e.getErrorMessage());
//      System.exit(1);
//    }
//  }
}
