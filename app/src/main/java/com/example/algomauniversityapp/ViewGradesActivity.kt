package com.example.algomauniversityapp

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ViewGradesActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase
    private lateinit var courseCrnEditText: EditText
    private lateinit var fetchGradesButton: Button
    private lateinit var gradesLinearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_grades)

        db = openDatabase()

        courseCrnEditText = findViewById(R.id.course_crn_input)
        fetchGradesButton = findViewById(R.id.fetch_grades_button)
        gradesLinearLayout = findViewById(R.id.grades_linear_layout)

        fetchGradesButton.setOnClickListener {
            fetchGradesForCourse()
        }
    }

    private fun openDatabase(): SQLiteDatabase {
        val db = this.openOrCreateDatabase("assignment4database", Context.MODE_PRIVATE, null)
        val sqlFile = Scanner(resources.openRawResource(R.raw.assignment4database))
        while (sqlFile.hasNextLine()) {
            val line = StringBuilder(sqlFile.nextLine())
            while (sqlFile.hasNextLine() && !line.contains(";")) {
                line.append(sqlFile.nextLine())
            }
            db.execSQL(line.toString())
        }
        return db
    }

    private fun fetchGradesForCourse() {
        var crn = courseCrnEditText.text.toString()

        // Capitalize the input
        crn = crn.uppercase(Locale.getDefault())
        courseCrnEditText.setText(crn)

        if (isCourseExists(crn)) {
            val grades = fetchGradesFromDatabase(crn)
            displayGrades(grades)
        } else {
            Toast.makeText(this, "Course does not exists", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("Range")
    private fun isCourseExists(crn: String): Boolean {
        val cursor = db.rawQuery("SELECT crn FROM courses WHERE crn = ?", arrayOf(crn))
        val courseExists = cursor.count > 0
        cursor.close()
        return courseExists
    }


    private fun displayGrades(grades: List<Pair<String, String>>) {
        gradesLinearLayout.removeAllViews()

        val textSize = 16 // Set the desired text size in pixels

        for (grade in grades) {
            val studentName = grade.first
            val studentGrade = grade.second

            val gradeTextView = TextView(this)
            gradeTextView.text = "$studentName: $studentGrade"
            gradeTextView.textSize = textSize.toFloat()
            gradeTextView.setTextColor(Color.BLACK)

            gradesLinearLayout.addView(gradeTextView)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cancel -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("Range")
    private fun fetchGradesFromDatabase(crn: String): List<Pair<String, String>> {
        val grades = mutableListOf<Pair<String, String>>()
        val cursor = db.rawQuery("SELECT student, grade FROM grades WHERE crn = ?", arrayOf(crn))

        if (cursor.moveToFirst()) {
            do {
                val studentName = cursor.getString(cursor.getColumnIndex("student"))
                val studentGrade = cursor.getString(cursor.getColumnIndex("grade"))
                grades.add(Pair(studentName, studentGrade))
            } while (cursor.moveToNext())
        }

        cursor.close()

        return grades
    }
}
