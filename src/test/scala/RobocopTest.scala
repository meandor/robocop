import java.io.FileNotFoundException

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
}
