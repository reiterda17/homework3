package at.fh.swengb.reiter

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_edit_note.*

class AddEditNoteActivity : AppCompatActivity() {

    companion object {

            val EXTRA_ADDED_OR_EDITED_RESULT = "ADD_OR_EDITED_RESULT"
            val TOKEN = "TOKEN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        val add: String? = intent.getStringExtra(NoteListActivity.NOTEID)

        if(add != null){
            val note:Note? = NoteRepository.nID(this, add)
            if(note != null) {
                add_edit_title.setText(note.title)
                add_edit_text.setText(note.text)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item?.itemId) {
            R.id.savenote -> {

                val add: String? = intent.getStringExtra(NoteListActivity.NOTEID)

                val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)

                val token = preferences.getString(TOKEN, null)

                if (
                    (add != null) &&
                    (add_edit_text.text.toString().isNotEmpty() || add_edit_title.text.toString().isNotEmpty()) &&
                    (token != null))

                {
                    val entry = Note(add, add_edit_title.text.toString(), add_edit_text.text.toString(), true)
                    NoteRepository.nAdd(this, entry)
                    NoteRepository.uploadNote(
                        token,
                        entry,
                        success = {
                        NoteRepository.nAdd(this, it)
                    },

                        error = {
                        Log.e("Upload", it)

                    })

                    val resultIntent = intent
                    resultIntent.putExtra(EXTRA_ADDED_OR_EDITED_RESULT, "ADDED")
                    Log.e("ADD_NOTE", "Note successfully added")
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                else {
                    Toast.makeText(this, this.getString(R.string.fill_message) , Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
