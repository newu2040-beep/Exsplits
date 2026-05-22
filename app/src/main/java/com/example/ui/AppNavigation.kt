package com.example.ui

import kotlinx.serialization.Serializable

@Serializable object HomeRoute
@Serializable object CreateRoute
@Serializable data class DetailRoute(val id: String)
@Serializable object SettingsRoute
