package at.fh.swengb.reiter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_note_list.*
import java.util.*

class NoteListActivity : AppCompatActivity() {

    companion object{

        val TOKEN = "TOKEN"

        val LASTSYNC = "LASTSYNC"

        val NOTEID = "NOTEID"

        val EXTRA_ADDED_OR_EDITED_RESULT = 0
    }
    val nAdapt = NoteAdapter(){
        val int = Intent(this, AddEditNoteActivity::class.java)
        int.putExtra(NOTEID, it.id)
        startActivityForResult(int, EXTRA_ADDED_OR_EDITED_RESULT)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)

        val token = preferences.getString(TOKEN, null)

        val lastSync = preferences.getLong(LASTSYNC, 0)

        if (token != null){
            NoteRepository.getNotes(
                token,
                lastSync,

                success = {
                    it.notes.map { NoteRepository.nAdd(this, it) }
                    preferences.edit().putLong(LASTSYNC, it.lastSync).apply()
                    nAdapt.updateList(NoteRepository.ngetAll(this))
                },

                error = {
                    Log.e("Error", it)
                    nAdapt.updateList(NoteRepository.ngetAll(this))
                })

            note_recycler_view.layoutManager = StaggeredGridLayoutManager(2,1)
            note_recycler_view.adapter = nAdapt

        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item?.itemId) {

            R.id.logout -> {
                val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                preferences.edit().clear().apply()
                NoteRepository.clearDB(this)
                finish()
            true}

            R.id.newnote -> {
                val uuidString = UUID.randomUUID().toString()
                val int = Intent(this, AddEditNoteActivity::class.java)
                int.putExtra(NOTEID, uuidString)
                startActivityForResult(int, EXTRA_ADDED_OR_EDITED_RESULT)
            true}

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        Log.e("ACTIVITY_RESULT","Resulted Activity")

        if (requestCode == EXTRA_ADDED_OR_EDITED_RESULT  && resultCode == Activity.RESULT_OK){
            nAdapt.updateList(NoteRepository.ngetAll(this))
            note_recycler_view.layoutManager = StaggeredGridLayoutManager(2,1)
            note_recycler_view.adapter = nAdapt
        }
    }
}
