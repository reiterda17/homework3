package at.fh.swengb.reiter


import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object NoteRepository {


    fun getNotes(token: String, lastSync: Long, success:(noteResponse: NotesResponse)->Unit,error:(errorMessage:String)->Unit){
        NoteApi.retrofitService.notes(token, lastSync).enqueue(object : Callback<NotesResponse>{


            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                error("Failed")
            }


            override fun onResponse(call: Call<NotesResponse>, response: Response<NotesResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("Error")
                }
            }
        })
    }


    fun uploadNote (token: String, note2Upload: Note, success: (note: Note)->Unit, error: (errorMessage: String)->Unit){
        NoteApi.retrofitService.addOrUpdateNote(token, note2Upload).enqueue(object : Callback<Note>{


            override fun onFailure(call: Call<Note>, t: Throwable) {
                error("The call failed! " + t.localizedMessage)
            }


            override fun onResponse(call: Call<Note>, response: Response<Note>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("Ups something went wrong xD " + response.message())
                }
            }
        })
    }



    fun nAdd(context: Context, newNote: Note) {
        val db = NoteDatabase.getDatabase(context)
        db.NoteDao.insert(newNote)
    }


    fun nID (context: Context, id: String):Note {
        val db = NoteDatabase.getDatabase(context)
        return db.NoteDao.findNoteById(id)
    }


    fun ngetAll (context: Context):List<Note> {
        val db = NoteDatabase.getDatabase(context)
        return db.NoteDao.ngetAll()
    }


    fun clearDB (context: Context) {
        val db = NoteDatabase.getDatabase(context)
        return db.NoteDao.deleteAllNotes()
    }
}