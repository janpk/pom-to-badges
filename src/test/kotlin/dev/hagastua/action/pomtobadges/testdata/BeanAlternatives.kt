package dev.hagastua.action.pomtobadges.testdata

import io.quarkiverse.githubaction.Inputs
import io.quarkiverse.githubaction.InputsInitializer
import io.quarkiverse.githubaction.testing.DefaultTestInputs
import jakarta.enterprise.inject.Alternative
import jakarta.inject.Singleton

const val TEST_SVG = "target/test.svg"

@Alternative
@Singleton
class MockInputsInitializerJson : InputsInitializer {
  override fun createInputs(): Inputs {
    return DefaultTestInputs(
        mapOf<String, String>(
            Pair("pomFile", "src/test/resources/test-pom.xml"),
            Pair("spec", "src/test/resources/test-dependencies-spec.json"),
        ))
  }
}
