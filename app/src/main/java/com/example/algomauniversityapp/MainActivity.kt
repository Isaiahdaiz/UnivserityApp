package com.example.algomauniversityapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var addStudentButton: Button
    private lateinit var addCourseButton: Button
    private lateinit var enrolStudentButton: Button
    private lateinit var enterGradeButton: Button
    private lateinit var viewGradesButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addStudentButton = findViewById(R.id.add_student_button)
        addCourseButton = findViewById(R.id.add_course_button)
        enrolStudentButton = findViewById(R.id.enrol_student_button)
        enterGradeButton = findViewById(R.id.enter_grade_button)
        viewGradesButton = findViewById(R.id.view_grades_button)

        addStudentButton.setOnClickListener {
            startActivity(Intent(this, AddStudentActivity::class.java))
        }

        addCourseButton.setOnClickListener {
            startActivity(Intent(this, AddCourseActivity::class.java))
        }

        enrolStudentButton.setOnClickListener {
            startActivity(Intent(this, EnrollStudentActivity::class.java))
        }

        enterGradeButton.setOnClickListener {
            startActivity(Intent(this, EnterGradesActivity::class.java))
        }

        viewGradesButton.setOnClickListener {
            startActivity(Intent(this, ViewGradesActivity::class.java))
        }
    }
}
