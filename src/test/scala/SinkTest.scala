import java.io.FileNotFoundException
import java.nio.file.{Files, Paths}

import Sink.TransferItem
import org.scalatest.{FeatureSpec, Matchers}

class SinkTest extends FeatureSpec with Matchers {
  feature("Copy shopping list files locally") {
    val exampleFile = "https://upload.wikimedia.org/wikipedia/en/a/a9/Example.jpg"

    scenario("copy one existing file") {
      val downloadFolder = "src/test/resources/tmp"
      val expectedDownloadedFile = "src/test/resources/tmp/Example.jpg"
      Files.deleteIfExists(Paths.get(expectedDownloadedFile))
      val copiedFiles = Sink.copyToLocal("foo", Seq(exampleFile).par, downloadFolder, HTTPCybernetic)

      copiedFiles should contain theSameElementsAs Seq(TransferItem(exampleFile, expectedDownloadedFile))
      Files.exists(Paths.get(expectedDownloadedFile)) shouldBe true
    }

    scenario("copy non existing file") {
      val downloadFolder = "src/test/resources/tmp"
      val nonExistingFileCybernetics = new FauxCybernetic(true, true, false)

      an[FileNotFoundException] should be thrownBy Sink.copyToLocal("foo", Seq("http://foo", exampleFile).par, downloadFolder, nonExistingFileCybernetics)
    }

    scenario("copy not cloud allowed file") {
      val downloadFolder = "src/test/resources/tmp"
      val nonExistingFileCybernetics = new FauxCybernetic(true, false, true)

      an[IllegalArgumentException] should be thrownBy Sink.copyToLocal("foo", Seq("http://foo", exampleFile).par, downloadFolder, nonExistingFileCybernetics)
    }

    scenario("copy no access to file") {
      val downloadFolder = "src/test/resources/tmp"
      val nonExistingFileCybernetics = new FauxCybernetic(false, true, true)

      an[IllegalArgumentException] should be thrownBy Sink.copyToLocal("foo", Seq("http://foo", exampleFile).par, downloadFolder, nonExistingFileCybernetics)
    }
  }

  feature("Extract study name for datasets file") {
    scenario("one dataset in one study") {
      Sink.studyName("/bhctestZone/bhcbio/research_studies/332_foo/datasets/abc.csv") shouldBe "332_foo"
    }

    scenario("multiple datasets in one study") {
      Sink.studyName("/bhcbioZone/bhcbio/research_studies/123_bar/foo/abc.csv") shouldBe "123_bar"
      Sink.studyName("/bhcbioZone/bhcbio/research_studies/123_bar/foo1/abc.csv") shouldBe "123_bar"
    }
  }
}
