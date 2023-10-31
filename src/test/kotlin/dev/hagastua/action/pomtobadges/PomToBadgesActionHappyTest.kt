package dev.hagastua.action.pomtobadges

import dev.hagastua.action.pomtobadges.testdata.MockInputsInitializerJson
import io.quarkus.test.junit.QuarkusTestProfile
import io.quarkus.test.junit.TestProfile
import io.quarkus.test.junit.main.Launch
import io.quarkus.test.junit.main.QuarkusMainTest
import java.io.File
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@QuarkusMainTest
@TestProfile(PomToBadgesActionHappyTest.PomToBadgesActionTestProfile::class)
class PomToBadgesActionHappyTest {

  @Test
  @Launch
  fun `bla bla`() {
    assertThat(File("target/kotlin.svg")).exists()
    assertThat(File("target/assertj.svg")).exists()
    assertThat(File("target/quarkus.svg")).exists()
    assertThat(File("target/quarkusaction.svg")).exists()
  }

  class PomToBadgesActionTestProfile : QuarkusTestProfile {
    override fun getEnabledAlternatives(): MutableSet<Class<*>> {
      return mutableSetOf(MockInputsInitializerJson::class.java)
    }
  }
}
