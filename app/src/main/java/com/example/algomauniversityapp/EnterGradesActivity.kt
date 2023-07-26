package com.example.algomauniversityapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class EnterGradesActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase
    private lateinit var courseCrnEditText: EditText
    private lateinit var fetchStudentsButton: Button
    private lateinit var gradesLinearLayout: LinearLayout
    private lateinit var courseAdapter: ArrayAdapter<String>
    private lateinit var courses: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_grades)

        db = openDatabase()

        courseCrnEditText = findViewById(R.id.course_crn_input)
        fetchStudentsButton = findViewById(R.id.fetch_students_button)
        gradesLinearLayout = findViewById(R.id.grades_linear_layout)

        fetchStudentsButton.setOnClickListener {
            fetchStudentsForCourse()
        }

        courses = getCourses()
        courseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courses)
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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

    private fun fetchStudentsForCourse() {
        // capitalize text before queuing sql
        var selectedCourse = courseCrnEditText.text.toString().trim().uppercase(Locale.ROOT)
        courseCrnEditText.setText(selectedCourse)

        if (isCourseExists(selectedCourse)) {
            val students = fetchStudentsFromDatabase(selectedCourse)
            createGradeEditTexts(students)
        } else {
            Toast.makeText(this, "Course does not exist", Toast.LENGTH_SHORT).show()
        }
    }


    private fun isCourseExists(crn: String): Boolean {
        val query = "SELECT crn FROM courses WHERE crn = ?"
        val cursor = db.rawQuery(query, arrayOf(crn))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    private fun createGradeEditTexts(students: List<Pair<Int, String>>) {
        gradesLinearLayout.removeAllViews()

        for (student in students) {
            val gradeEditText = EditText(this)
            gradeEditText.hint = "Enter grade for ${student.second}"
            gradeEditText.setText(fetchGradeFromDatabase(student.first))
            gradesLinearLayout.addView(gradeEditText)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save_cancel_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveGradesToDatabase()
                Toast.makeText(this, "Grades saved!", Toast.LENGTH_SHORT).show()
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
    @SuppressLint("Range")
    private fun fetchGradeFromDatabase(studentId: Int): String {
        val cursor = db.rawQuery("SELECT grade FROM grades WHERE student = ?", arrayOf(studentId.toString()))
        val grade = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex("grade"))
        } else {
            ""
        }
        cursor.close()
        return grade
    }

    @SuppressLint("Range")
    private fun fetchStudentsFromDatabase(course: String): List<Pair<Int, String>> {
        val students = mutableListOf<Pair<Int, String>>()
        val query = "SELECT students.id, students.name " +
                "FROM enrolment " +
                "JOIN students ON enrolment.student = students.id " +
                "WHERE enrolment.crn = ?"

        val cursor = db.rawQuery(query, arrayOf(course))

        if (cursor.moveToFirst()) {
            do {
                val studentId = cursor.getInt(cursor.getColumnIndex("id"))
                val studentName = cursor.getString(cursor.getColumnIndex("name"))
                students.add(Pair(studentId, studentName))
            } while (cursor.moveToNext())
        }

        cursor.close()

        return students
    }



    private fun saveGradesToDatabase() {
        val selectedCourse = courseCrnEditText.text.toString()

        for (i in 0 until gradesLinearLayout.childCount) {
            val view = gradesLinearLayout.getChildAt(i)
            if (view is EditText) {
                val studentGrade = view.text.toString()
                val studentName = view.hint.toString().removePrefix("Enter grade for ")
                saveGradeToDatabase(selectedCourse, studentName, studentGrade)
            }
        }
    }

    private fun saveGradeToDatabase(course: String, studentName: String, grade: String) {
        val values = ContentValues()
        values.put("crn", course)
        values.put("student", studentName)
        values.put("grade", grade)

        db.insertWithOnConflict("grades", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

}
