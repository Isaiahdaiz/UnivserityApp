package com.example.algomauniversityapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Scanner

class AddCourseActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase
    private lateinit var crnEditText: EditText
    private lateinit var nameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        db = openDatabase()

        crnEditText = findViewById(R.id.course_crn_input)
        nameEditText = findViewById(R.id.course_name_input)
    }

    private fun openDatabase(): SQLiteDatabase {
        val db = this.openOrCreateDatabase("assignment4database", Context.MODE_PRIVATE, null)
        val sqlFile = Scanner(resources.openRawResource(R.raw.assignment4database))
        while(sqlFile.hasNextLine()) {
            val line = StringBuilder(sqlFile.nextLine())
            while(sqlFile.hasNextLine() && !line.contains(";")) {
                line.append(sqlFile.nextLine())
            }
            db.execSQL(line.toString())
        }
        return db
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save_cancel_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                val crn = crnEditText.text.toString().trim()
                val title = nameEditText.text.toString().trim()

                if (crn.isEmpty() || title.isEmpty()) {
                    Toast.makeText(this, "Please enter CRN and title", Toast.LENGTH_SHORT).show()
                } else if (isCrnExists(crn)) {
                    Toast.makeText(this, "CRN already exists", Toast.LENGTH_SHORT).show()
                } else if (crn.length != 10) {
                    Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
                } else {
                    val sqlInsert = "INSERT INTO courses(crn, title) VALUES('$crn', '$title')"
                    db.execSQL(sqlInsert)
                    Toast.makeText(this, "Course saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }

                true
            }
            R.id.action_cancel -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isCrnExists(crn: String): Boolean {
        // Return true if the CRN exists, otherwise return false
        val query = "SELECT crn FROM courses WHERE crn = '$crn'"
        val cursor = db.rawQuery(query, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

}
