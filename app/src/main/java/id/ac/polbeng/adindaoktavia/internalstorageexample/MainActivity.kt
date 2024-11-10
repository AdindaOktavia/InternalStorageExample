package id.ac.polbeng.adindaoktavia.internalstorageexample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import id.ac.polbeng.adindaoktavia.internalstorageexample.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val FILE_NAME = "rpl.txt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Button click listener to open SecondActivity
        binding.btnSaveText.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }

    // Save data to file in background thread
    private fun saveData() {
        Thread {
            try {
                val out = openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
                out.use {
                    out.write(binding.etInputText.text.toString().toByteArray())
                }
                runOnUiThread {
                    Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
                }
            } catch (ioe: IOException) {
                Log.w(TAG, "Error while saving $FILE_NAME : $ioe")
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        saveData() // Save data when activity is paused
    }

    override fun onResume() {
        super.onResume()
        loadData() // Load data when activity is resumed
    }

    // Load data from file in background thread
    private fun loadData() {
        Thread {
            try {
                val input = openFileInput(FILE_NAME)
                input.use {
                    val buffer = StringBuilder()
                    var bytesRead = input.read()
                    while (bytesRead != -1) {
                        buffer.append(bytesRead.toChar())
                        bytesRead = input.read()
                    }
                    runOnUiThread {
                        binding.etInputText.setText(buffer.toString()) // Display the loaded data
                    }
                }
            } catch (fnf: FileNotFoundException) {
                Log.w(TAG, "File not found, occurs only once")
            } catch (ioe: IOException) {
                Log.w(TAG, "IOException : $ioe")
            }
        }.start()
    }
}
