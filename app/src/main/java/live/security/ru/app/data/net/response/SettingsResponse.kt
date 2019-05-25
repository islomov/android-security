package ru.security.live.data.net.response

/**
 * @author sardor
 */

data class SettingsResponse(val mapSettings: MapSettings)

data class MapSettings(val tilesUri: String)