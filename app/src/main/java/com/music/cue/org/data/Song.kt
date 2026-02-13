package com.music.cue.org.data

import android.os.Parcel
import android.os.Parcelable

data class Song(
    val id: Long,
    val title: String?,
    val artist: String?,
    val data: String,
    val albumId: Long
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(title)
        dest.writeString(artist)
        dest.writeString(data)
        dest.writeLong(albumId)
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(
                parcel.readLong(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString()!!,
                parcel.readLong()
            )
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}
