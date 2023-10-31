package dev.hagastua.action.pomtobadges

import io.quarkus.logging.Log
import java.io.File
import java.io.FileReader
import java.util.*
import org.apache.maven.model.Dependency
import org.apache.maven.model.Model
import org.apache.maven.model.Plugin
import org.apache.maven.model.io.xpp3.MavenXpp3Reader

object MavenPomUtils {

  fun buildKnownDependenciesFromMaven(pomFile: File): List<DependencySpec> {

    val mavenReader = MavenXpp3Reader()
    val dependencySpecs = mutableListOf<DependencySpec>()
    try {
      val reader = FileReader(pomFile)
      val model = mavenReader.read(reader)
      dependencySpecs.addAll(resolveFromDependencies(model))
      dependencySpecs.addAll(resolveFromDependencyManagement(model))
      dependencySpecs.addAll(resolveFromPlugins(model))
      dependencySpecs.addAll(resolveFromPluginManagement(model))
    } catch (e: Exception) {
      Log.error("Error parsing pom : $pomFile")
      Log.error(e)
    }
    return dependencySpecs
  }

  private fun resolveDependency(dependency: Dependency, properties: Properties): DependencySpec {

    val groupId =
        if (dependency.groupId.contains("\${")) {
          resolveFromMavenProperties(
              dependency.groupId.substring(2, dependency.groupId.lastIndexOf("}")), properties)
        } else {
          dependency.groupId
        }
    val artifactId: String =
        if (dependency.artifactId.contains("\${")) {
          resolveFromMavenProperties(
              dependency.artifactId.substring(2, dependency.artifactId.lastIndexOf("}")),
              properties)
        } else {
          dependency.artifactId
        }
    val version: String =
        if (null != dependency.version && dependency.version.contains("\${")) {
          resolveFromMavenProperties(
              dependency.version.substring(2, dependency.version.lastIndexOf("}")), properties)
        } else {
          dependency.version ?: ""
        }
    return DependencySpec(
        groupId = groupId,
        artifactId = artifactId,
        version = version,
        badgeName = "",
        color = "",
        savePath = "")
  }

  private fun resolvePlugin(plugin: Plugin, properties: Properties): DependencySpec {
    val groupId =
        if (plugin.groupId.contains("\${")) {
          resolveFromMavenProperties(
              plugin.groupId.substring(2, plugin.groupId.lastIndexOf("}")), properties)
        } else {
          plugin.groupId
        }
    val artifactId =
        if (plugin.artifactId.contains("\${")) {
          resolveFromMavenProperties(
              plugin.artifactId.substring(2, plugin.artifactId.lastIndexOf("}")), properties)
        } else {
          plugin.artifactId
        }
    val version =
        if (plugin.version.contains("\${")) {
          resolveFromMavenProperties(
              plugin.version.substring(2, plugin.version.lastIndexOf("}")), properties)
        } else {
          plugin.version ?: ""
        }
    return DependencySpec(
        groupId = groupId,
        artifactId = artifactId,
        version = version,
        badgeName = "",
        color = "",
        savePath = "")
  }

  fun resolveFromDependencies(model: Model): List<DependencySpec> {
    val dependencySpecs = mutableListOf<DependencySpec>()

    if (null != model.dependencies) {
      model.dependencies.forEach { dependencySpecs.add(resolveDependency(it, model.properties)) }
    }
    return dependencySpecs
  }

  fun resolveFromDependencyManagement(model: Model): List<DependencySpec> {
    val dependencySpecs = mutableListOf<DependencySpec>()
    if (null != model.dependencyManagement) {
      model.dependencyManagement.dependencies.forEach {
        dependencySpecs.add(resolveDependency(it, model.properties))
      }
    }
    return dependencySpecs
  }

  fun resolveFromPluginManagement(model: Model): List<DependencySpec> {
    val dependencySpecs = mutableListOf<DependencySpec>()

    if (null != model.build.pluginManagement) {
      model.build.pluginManagement.plugins.forEach {
        dependencySpecs.add(resolvePlugin(it, model.properties))
      }
    }
    return dependencySpecs
  }

  fun resolveFromPlugins(model: Model): List<DependencySpec> {
    val dependencySpecs = mutableListOf<DependencySpec>()
    if (null != model.build.plugins) {
      model.build.plugins.forEach { dependencySpecs.add(resolvePlugin(it, model.properties)) }
    }
    return dependencySpecs
  }

  private fun resolveFromMavenProperties(propertyName: String, properties: Properties): String {
    val property = properties[propertyName]
    if (null != property) {
      return property.toString()
    } else {
      println("Unable to resolve value for : $propertyName")
    }
    return ""
  }
}
