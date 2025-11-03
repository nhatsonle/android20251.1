package com.example.data_input

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.data_input.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firstName = findViewById<EditText>(R.id.etFirstName)
        val lastName = findViewById<EditText>(R.id.etLastName)
        val genderGroup = findViewById<RadioGroup>(R.id.rgGender)
        val birthdayEt = findViewById<EditText>(R.id.etBirthday)
        val selectBirthdayBtn = findViewById<Button>(R.id.btnSelectBirthday)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val address = findViewById<EditText>(R.id.etAddress)
        val email = findViewById<EditText>(R.id.etEmail)
        val terms = findViewById<CheckBox>(R.id.cbTerms)
        val registerBtn = findViewById<Button>(R.id.btnRegister)

        val defaultFieldColor = 0xFFF5F5F5.toInt()
        val errorFieldColor = 0xFFFFCDD2.toInt() // light red

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val tempCalendar = Calendar.getInstance()

        // Toggle CalendarView visibility
        selectBirthdayBtn.setOnClickListener {
            calendarView.visibility = if (calendarView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Update chosen date into EditText and hide calendar
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            tempCalendar.set(year, month, dayOfMonth)
            birthdayEt.setText(dateFormat.format(tempCalendar.time))
            calendarView.visibility = View.GONE
        }

        // Validation on Register
        registerBtn.setOnClickListener {
            var allValid = true

            fun validateEditText(et: EditText): Boolean {
                val ok = et.text.toString().trim().isNotEmpty()
                et.setBackgroundColor(if (ok) defaultFieldColor else errorFieldColor)
                return ok
            }

            allValid = validateEditText(firstName) && allValid
            allValid = validateEditText(lastName) && allValid
            allValid = validateEditText(birthdayEt) && allValid
            allValid = validateEditText(address) && allValid
            allValid = validateEditText(email) && allValid

            // Gender required
            val genderOk = genderGroup.checkedRadioButtonId != -1
            genderGroup.setBackgroundColor(if (genderOk) 0x00000000 else errorFieldColor)
            allValid = genderOk && allValid

            // Terms must be checked (treat as required)
            val termsOk = terms.isChecked
            terms.setBackgroundColor(if (termsOk) 0x00000000 else errorFieldColor)
            allValid = termsOk && allValid

            if (allValid) {
                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}