package com.example.algomauniversityapp

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Scanner

class EnrollStudentActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase
    private lateinit var studentSpinner: Spinner
    private lateinit var courseSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrol_student)

        db = openDatabase()

        studentSpinner = findViewById(R.id.student_spinner)
        courseSpinner = findViewById(R.id.course_spinner)

        // Suppose you have a function getStudents() and getCourses() that return List<String> of student names and course names
        val students = getStudents()
        val courses = getCourses()

// Create an ArrayAdapter using the string array and a default spinner layout
        val studentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, students)
        val courseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courses)

// Specify the layout to use when the list of choices appears
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Apply the adapter to the spinner
        studentSpinner.adapter = studentAdapter
        courseSpinner.adapter = courseAdapter

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
                val selectedStudentName = studentSpinner.selectedItem.toString()
                val selectedStudentId = getStudentIdByName(selectedStudentName)
                val selectedCourse = courseSpinner.selectedItem.toString()

                if (selectedStudentId != null && selectedCourse.isNotEmpty()) {
                    if (isStudentEnrolled(selectedStudentId, selectedCourse)) {
                        Toast.makeText(this, "Student is already enrolled in the selected course", Toast.LENGTH_SHORT).show()
                    } else {
                        val sqlInsert = "INSERT INTO enrolment(student, crn) VALUES(?, ?)"
                        db.execSQL(sqlInsert, arrayOf(selectedStudentId, selectedCourse))
                        Toast.makeText(this, "Student enrolled!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please select both student and course", Toast.LENGTH_SHORT).show()
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

    @SuppressLint("Range")
    private fun getStudentIdByName(studentName: String): String? {
        val query = "SELECT id FROM students WHERE name = ?"
        val cursor = db.rawQuery(query, arrayOf(studentName))

        val studentId: String? = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex("id"))
        } else {
            null
        }

        cursor.close()
        return studentId
    }


    private fun isStudentEnrolled(student: String, course: String): Boolean {
        // Return true if the student is already enrolled, otherwise return false
        val query = "SELECT * FROM enrolment WHERE student = '$student' AND crn = '$course'"
        val cursor = db.rawQuery(query, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }


    @SuppressLint("Range")
    private fun getStudents(): List<String> {
        val students = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT name FROM students", null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex("name"))
                students.add(name)
            } while (cursor.moveToNext())
        }

        cursor.close()

        return students
    }

    @SuppressLint("Range")
    private fun getCourses(): List<String> {
        val courses = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT crn FROM courses", null)

        if (cursor.moveToFirst()) {
            do {
                val courseName = cursor.getString(cursor.getColumnIndex("crn"))
                courses.add(courseName)
            } while (cursor.moveToNext())
        }

        cursor.close()

        return courses
    }

}
