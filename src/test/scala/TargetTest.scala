import java.security.MessageDigest

import org.scalatest.{FeatureSpec, Matchers}

class TargetTest extends FeatureSpec with Matchers {
  feature("Extract bucket name from datasets file") {
    def md5(plain: String) = {
      MessageDigest.getInstance("MD5").digest(plain.getBytes).map("%02x".format(_)).mkString
    }

    scenario("one dataset in one study") {
      Target.bucketName("/bhctestZone/bhcbio/research_studies/332_foo/datasets/abc.csv") shouldBe "robocop-" + md5("332_foo")
    }
  }
}
