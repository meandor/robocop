import java.io.FileNotFoundException
import java.nio.file.{Files, Paths}
import java.security.MessageDigest

import org.scalatest.{FeatureSpec, Matchers}

class RobocopTest extends FeatureSpec with Matchers {
  feature("Load shopping list content") {
    scenario("shopping list file with 4 items") {
      Robocop.parseShoppingList("src/test/resources/shoppingList.txt") should contain theSameElementsAs
        Seq("foo", "bar", "http://foo.bar", "foo")
    }

    scenario("shopping list file does not exist") {
      an[FileNotFoundException] should be thrownBy Robocop.parseShoppingList("src/test/resources/foo")
    }
  }

  feature("Copy shopping list files locally") {
    scenario("copy one existing file") {
      val downloadFolder = "src/test/resources/tmp"
      val expectedDownloadedFile = "src/test/resources/tmp/Example.jpg"
      Files.deleteIfExists(Paths.get(expectedDownloadedFile))
      val copiedFiles = Robocop.copyToLocal("foo", Seq("https://upload.wikimedia.org/wikipedia/en/a/a9/Example.jpg").par, downloadFolder, HTTPCybernetic)

      copiedFiles should contain theSameElementsAs Seq(expectedDownloadedFile)
      Files.exists(Paths.get(expectedDownloadedFile)) shouldBe true
    }

    scenario("copy non existing file") {
      val downloadFolder = "src/test/resources/tmp"
      val nonExistingFileCybernetics = new FauxCybernetic(true, true, false)

      an[FileNotFoundException] should be thrownBy Robocop.copyToLocal("foo", Seq("http://foo", "https://upload.wikimedia.org/wikipedia/en/a/a9/Example.jpg").par, downloadFolder, nonExistingFileCybernetics)
    }

    scenario("copy not cloud allowed file") {
      val downloadFolder = "src/test/resources/tmp"
      val nonExistingFileCybernetics = new FauxCybernetic(true, false, true)

      an[IllegalArgumentException] should be thrownBy Robocop.copyToLocal("foo", Seq("http://foo", "https://upload.wikimedia.org/wikipedia/en/a/a9/Example.jpg").par, downloadFolder, nonExistingFileCybernetics)
    }

    scenario("copy no access to file") {
      val downloadFolder = "src/test/resources/tmp"
      val nonExistingFileCybernetics = new FauxCybernetic(false, true, true)

      an[IllegalArgumentException] should be thrownBy Robocop.copyToLocal("foo", Seq("http://foo", "https://upload.wikimedia.org/wikipedia/en/a/a9/Example.jpg").par, downloadFolder, nonExistingFileCybernetics)
    }
  }

  feature("Extract study name for datasets file") {
    scenario("one dataset in one study") {
      Robocop.studyName("/bhctestZone/bhcbio/research_studies/332_foo/datasets/abc.csv") shouldBe "332_foo"
    }

    scenario("multiple datasets in one study") {
      Robocop.studyName("/bhcbioZone/bhcbio/research_studies/123_bar/foo/abc.csv") shouldBe "123_bar"
      Robocop.studyName("/bhcbioZone/bhcbio/research_studies/123_bar/foo1/abc.csv") shouldBe "123_bar"
    }
  }

  feature("Extract bucket name from datasets file") {
    def md5(plain: String) = {
      MessageDigest.getInstance("MD5").digest(plain.getBytes).map("%02x".format(_)).mkString
    }

    scenario("one dataset in one study") {
      Robocop.bucketName("/bhctestZone/bhcbio/research_studies/332_foo/datasets/abc.csv") shouldBe "robocop-" + md5("332_foo")
    }
  }
}
