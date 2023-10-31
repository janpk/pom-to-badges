package dev.hagastua.action.pomtobadges

import kotlinx.serialization.Serializable

@Serializable
data class DependencySpec(
    val groupId: String,
    val artifactId: String,
    val version: String? = null,
    val badgeName: String,
    val savePath: String,
    val color: String = "#007ec6",
    val versionColor: String = "#9f9f9f",
    val style: String = "flat",
)
