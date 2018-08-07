import com.amazonaws.services.s3.transfer._
import com.amazonaws.{AmazonClientException, AmazonServiceException}
import com.typesafe.scalalogging.LazyLogging

object TransferManagerProgress extends LazyLogging { // waits for the transfer to complete, catching any exceptions that occur.

  def waitForCompletion(transfer: Transfer): Unit = {
    try
      transfer.waitForCompletion()
    catch {
      case e: AmazonServiceException =>
        logger.error("Amazon service error: " + e.getMessage, e)
        System.exit(1)
      case e: AmazonClientException =>
        logger.error("Amazon client error: " + e.getMessage, e)
        System.exit(1)
      case e: InterruptedException =>
        logger.error("Transfer interrupted: " + e.getMessage, e)
        System.exit(1)
    }
  }

  // Prints progress while waiting for the transfer to finish.
  def showTransferProgress(xfer: Transfer): Unit = {
    // print the transfer's human-readable description
    logger.info(xfer.getDescription)
    // print an empty progress bar...
    printProgressBar(0.0)
    // update the progress bar while the xfer is ongoing.
    do {
      try
        Thread.sleep(100)
      catch {
        case _: InterruptedException =>
          return
      }
      // Note: so_far and total aren't used, they're just for
      // documentation purposes.
      val progress = xfer.getProgress
      val so_far = progress.getBytesTransferred
      val total = progress.getTotalBytesToTransfer
      val pct = progress.getPercentTransferred
      printProgressBar(pct)
    } while ( {
      !xfer.isDone
    })
    // print the final state of the transfer.
    val xfer_state = xfer.getState
    logger.info(": " + xfer_state)
  }

  // prints a simple text progressbar: [#####     ]
  def printProgressBar(pct: Double): Unit = { // if bar_size changes, then change erase_bar (in eraseProgressBar) to
    // match.
    val bar_size = 40
    val empty_bar = "                                        "
    val filled_bar = "########################################"
    val amt_full = (bar_size * (pct / 100.0)).toInt
    logger.info("  [%s%s]".format(filled_bar.substring(0, amt_full), empty_bar.substring(0, bar_size - amt_full)))
  }
}
