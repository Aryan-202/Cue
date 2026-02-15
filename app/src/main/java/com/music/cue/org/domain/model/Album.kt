package com.music.cue.org.domain.model

class Album (
    val id: Long,
    val title: String,
    val artistName: String,
    val coverArt: String?,
    val songCount: Int = 0,
    val releaseYear: Int?,
    val duration: Long = 0L,
)