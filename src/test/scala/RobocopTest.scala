import java.io.FileNotFoundException
import java.nio.file.{Files, Paths}

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
    scenario("copy one file") {
      val downloadFolder = "src/test/resources/tmp"
      val expectedDownloadedFile = "src/test/resources/tmp/Example.jpg"
      Files.deleteIfExists(Paths.get(expectedDownloadedFile))
      val copiedFiles = Robocop.copyToLocal(Seq("https://upload.wikimedia.org/wikipedia/en/a/a9/Example.jpg").par, downloadFolder)

      copiedFiles should contain theSameElementsAs Seq(expectedDownloadedFile)
      Files.exists(Paths.get(expectedDownloadedFile)) shouldBe true
    }
  }
}
