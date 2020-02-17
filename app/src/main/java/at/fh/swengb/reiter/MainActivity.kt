package at.fh.swengb.reiter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {
companion object{
    val TOKEN = "TOKEN"
}
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)

        if(preferences.getString(TOKEN,null)== null){
            Log.e("Token","Token not found")
        }
        else{
            val intent = Intent(this, NoteListActivity::class.java)
            startActivity(intent)
        }

        login_button.setOnClickListener {
            if(login_username.text.toString().isNotEmpty() and login_password.text.toString().isNotEmpty()) {
                val authentic =
                    AuthRequest(login_username.text.toString(), login_password.text.toString())
                    login(authentic,
                    success = {

                        preferences.edit().putString(TOKEN, it.token).apply()

                        val intent = Intent(this, NoteListActivity::class.java)

                        startActivity(intent)

                    },
                    error = {
                        Log.e("Error", it)
                    }
                )
            }
            else {
                Toast.makeText(this, this.getString(R.string.login_info_missing_message) , Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login (
        request: AuthRequest,

        success: (response: AuthResponse) -> Unit,

        error: (errorMessage: String) -> Unit) {
        NoteApi.retrofitService.login(request).enqueue(object: retrofit2.Callback<AuthResponse>{
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                error("Login was not possible.")
            }

            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val resB = response.body()
                if (response.isSuccessful && resB != null) {
                    success(resB)
                }
                else {
                    error("Error.")
                }

            }
        }
        )
    }
}