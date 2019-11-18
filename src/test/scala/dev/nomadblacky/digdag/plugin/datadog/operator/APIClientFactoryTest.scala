package dev.nomadblacky.digdag.plugin.datadog.operator

import org.scalatest.FunSpec
import org.scalatest.prop.Tables
import scaladog.api.DatadogSite
import scaladog.api.events.EventsAPIClient

import scala.language.reflectiveCalls

class APIClientFactoryTest extends FunSpec with TestUtils with Tables {

  class APIClientFactoryForTest(override protected val env: Map[String, String])
      extends APIClientFactory[EventsAPIClient] {
    override protected def newClient(apiKey: String, appKey: String, site: DatadogSite): EventsAPIClient = ???
  }

  def fixture(env: Map[String, String], _secrets: Map[String, String]) = new {
    val factory = new APIClientFactoryForTest(env)
    val secrets = new SecretProviderForTest(_secrets)
  }

  describe("lookupApiKey") {
    it("returns an API key when set the DATADOG_API_KEY environment variable") {
      val fx = fixture(env = Map("DATADOG_API_KEY" -> "ENV_KEY"), _secrets = Map.empty)
      assert(fx.factory.lookupApiKey(fx.secrets) === Right("ENV_KEY"))
    }

    it("returns an API key when set the datadog.api_key secret") {
      val fx = fixture(env = Map.empty, _secrets = Map("datadog.api_key" -> "SEC_KEY"))
      assert(fx.factory.lookupApiKey(fx.secrets) === Right("SEC_KEY"))
    }

    it("returns an API key in secrets when set the environment variable and the secret") {
      val fx = fixture(env = Map("DATADOG_API_KEY" -> "ENV_KEY"), _secrets = Map("datadog.api_key" -> "SEC_KEY"))
      assert(fx.factory.lookupApiKey(fx.secrets) == Right("SEC_KEY"))
    }
  }

}
