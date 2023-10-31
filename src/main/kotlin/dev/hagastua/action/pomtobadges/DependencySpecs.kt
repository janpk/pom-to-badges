package dev.hagastua.action.pomtobadges

import kotlinx.serialization.Serializable

@Serializable data class DependencySpecs(val dependencies: List<DependencySpec>)
