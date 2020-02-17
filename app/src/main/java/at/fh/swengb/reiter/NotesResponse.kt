package at.fh.swengb.reiter


import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class NotesResponse(val lastSync: Long, val notes: List<Note>) {
}