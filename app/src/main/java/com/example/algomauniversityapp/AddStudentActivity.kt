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

class AddStudentActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase
    private lateinit var idEditText: EditText
    private lateinit var nameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        db = openDatabase()

        idEditText = findViewById(R.id.student_id_input)
        nameEditText = findViewById(R.id.student_name_input)
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
                val id = idEditText.text.toString().trim()
                val name = nameEditText.text.toString().trim()

                if (id.isEmpty() || name.isEmpty()) {
                    Toast.makeText(this, "Please enter ID and name", Toast.LENGTH_SHORT).show()
                } else if (isIdExists(id)) {
                    Toast.makeText(this, "ID already exists", Toast.LENGTH_SHORT).show()
                } else if (id.length != 9) {
                    Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
                } else {
                    val sqlInsert = "INSERT INTO students(id, name) VALUES('$id', '$name')"
                    db.execSQL(sqlInsert)
                    Toast.makeText(this, "Student saved!", Toast.LENGTH_SHORT).show()
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

    private fun isIdExists(id: String): Boolean {
        // Return true if the ID exists, otherwise return false
        val query = "SELECT id FROM students WHERE id = '$id'"
        val cursor = db.rawQuery(query, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }


}
