package com.br.ifal.hobbyhub.enums

enum class MusicSearchScreenTypeEnum(val displayName: String, val apiValue: String) {
    NAME(displayName = "Nome", apiValue = "track"),
    ALBUM(displayName = "Álbum", apiValue = "album"),
    ARTIST(displayName = "Artista", apiValue = "artist");
}