package dev.hagastua.action.pomtobadges

import dev.hagastua.action.pomtobadges.MavenPomUtils.buildKnownDependenciesFromMaven
import io.quarkiverse.githubaction.Action
import io.quarkiverse.githubaction.Inputs
import io.quarkus.logging.Log
import java.io.File
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.silentsoft.badge4j.Badge
import org.silentsoft.badge4j.Style

open class PomToBadgesAction {

  @Action
  fun action(inputs: Inputs) {

    try {
      // We get the json file with the dependencies to find versions for
      val jsonSpec = File(inputs.getRequired("spec"))
      // Deserialize the json file into an object list
      val dependencyList = createDependencyList(jsonSpec)
      // We get the pom file to use for finding dependency versions
      val pomFile = File(inputs.getRequired("pomFile"))
      // The curated list contains the dependencies we can find in the pom with versions
      val curatedList =
          curateDependencyList(buildKnownDependenciesFromMaven(pomFile), dependencyList)
      // The versioned list contain the populated dependency spec objects with version information
      val versionedList = createVersionedList(dependencyList, curatedList)
      // For each dependency, create a badge and write it to the specified file name, with the
      // specified style
      versionedList.forEach {
        println("Generating badge for : $it")
        File(it.savePath)
            .writeText(
                Badge.builder()
                    .style(Style.nameOf(it.style))
                    .message(it.version)
                    .label(it.badgeName)
                    .labelColor(it.color)
                    .color(it.versionColor)
                    .build())
      }
    } catch (e: IllegalStateException) {
      Log.error("${e.message}")
      throw e
    }
  }

  private fun createVersionedList(
      dependencyList: List<DependencySpec>,
      curatedList: List<DependencySpec>
  ): List<DependencySpec> {

    val versionedList = mutableListOf<DependencySpec>()
    dependencyList.forEach { dependency ->
      curatedList.forEach { curated ->
        if (curated.groupId == dependency.groupId && curated.artifactId == dependency.artifactId) {
          versionedList.add(dependency.copy(version = curated.version))
        }
      }
    }
    return versionedList
  }

  private fun curateDependencyList(
      resolvedDependencies: List<DependencySpec>,
      dependencyList: List<DependencySpec>
  ): List<DependencySpec> {
    return resolvedDependencies.filter { p ->
      dependencyList.any { it.groupId == p.groupId && it.artifactId == p.artifactId }
    }
  }

  @OptIn(ExperimentalSerializationApi::class)
  private fun createDependencyList(jsonSpec: File): List<DependencySpec> {

    return Json.decodeFromStream<DependencySpecs>(jsonSpec.inputStream()).dependencies

    //    val dependencyList = mutableListOf<DependencySpec>()
    //    dependencyList.addAll(
    //        listOf(
    //            DependencySpec(
    //                groupId = "org.jetbrains.kotlin",
    //                artifactId = "kotlin-maven-plugin",
    //                badgeName = "Kotlin",
    //                color = "red",
    //                savePath = "badges/kotlin.svg"),
    //            DependencySpec(
    //                groupId = "io.quarkus.platform",
    //                artifactId = "quarkus-bom",
    //                badgeName = "Quarkus",
    //                color = "blue",
    //                savePath = "badges/quarkus.svg"),
    //            DependencySpec(
    //                groupId = "io.quarkiverse.githubaction",
    //                artifactId = "quarkus-github-action",
    //                badgeName = "Quarkus Github Action",
    //                color = "green",
    //                savePath = "badges/quarkusaction.svg"),
    //            DependencySpec(
    //                groupId = "org.assertj",
    //                artifactId = "assertj-core",
    //                badgeName = "AssertJ",
    //                color = "yellow",
    //                savePath = "badges/assertj.svg"),
    //        ))
    //    return dependencyList
  }
}
