package com.music.cue.org.domain.model

import java.util.Date

class Playlist (
    val id: Long,
    val title: String?,
    val icon: String?,
    val songCount: Int = 0,
    val songs: List<Song> = emptyList(),
    val songsIds: List<Long> = emptyList(),
    val createdAt: Date,
    val updatedAt: Date,
    val deletedAt: Date?
)


