package com.example.data_input

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.data_input.R

class MainActivity : AppCompatActivity() {
	private lateinit var spinnerFrom: Spinner
	private lateinit var spinnerTo: Spinner
	private lateinit var editFrom: EditText
	private lateinit var editTo: EditText
	private lateinit var textRate: TextView

	private var isUpdatingFrom = false
	private var isUpdatingTo = false

	// Fixed exchange rates relative to USD (1 USD = rate * currency)
	// These are static and embedded in source code as required.
	private val ratesToUSD: Map<String, Double> = mapOf(
		"USD" to 1.0,
		"EUR" to 0.92,
		"GBP" to 0.79,
		"JPY" to 151.0,
		"CNY" to 7.2,
		"AUD" to 1.52,
		"CAD" to 1.37,
		"CHF" to 0.90,
		"SEK" to 10.8,
		"INR" to 83.0,
		"VND" to 24450.0
	)

	private val currencies: List<String> = ratesToUSD.keys.sorted()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

		spinnerFrom = findViewById(R.id.spinnerFrom)
		spinnerTo = findViewById(R.id.spinnerTo)
		editFrom = findViewById(R.id.editFrom)
		editTo = findViewById(R.id.editTo)
		textRate = findViewById(R.id.textRate)

		setupSpinners()
		setupTextWatchers()
		updateDisplayedRate()
    }

	private fun setupSpinners() {
		val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		spinnerFrom.adapter = adapter
		spinnerTo.adapter = adapter

		// Default: From USD, To EUR if available
		spinnerFrom.setSelection(currencies.indexOf("USD").coerceAtLeast(0))
		spinnerTo.setSelection(currencies.indexOf("EUR").coerceAtLeast(0))

		val onChange: (Int) -> Unit = {
			updateDisplayedRate()
			recalculateFromSource(isFromSource = true)
		}
		spinnerFrom.onItemSelectedListener = simpleListener(onChange)
		spinnerTo.onItemSelectedListener = simpleListener(onChange)
	}

	private fun setupTextWatchers() {
		editFrom.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable?) {
				if (isUpdatingFrom) return
				recalculateFromSource(isFromSource = true)
			}
		})
		editTo.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
			override fun afterTextChanged(s: Editable?) {
				if (isUpdatingTo) return
				recalculateFromSource(isFromSource = false)
			}
		})
	}

	private fun recalculateFromSource(isFromSource: Boolean) {
		val fromCode = spinnerFrom.selectedItem as String
		val toCode = spinnerTo.selectedItem as String
		val rate = getRate(fromCode, toCode)

		if (isFromSource) {
			val value = editFrom.text.toString().toDoubleOrNull()
			val result = if (value != null) value * rate else 0.0
			isUpdatingTo = true
			editTo.setText(formatNumber(result))
			isUpdatingTo = false
		} else {
			val value = editTo.text.toString().toDoubleOrNull()
			val result = if (value != null && rate != 0.0) value / rate else 0.0
			isUpdatingFrom = true
			editFrom.setText(formatNumber(result))
			isUpdatingFrom = false
		}
		updateDisplayedRate()
	}

	private fun updateDisplayedRate() {
		val fromCode = spinnerFrom.selectedItem as String
		val toCode = spinnerTo.selectedItem as String
		val rate = getRate(fromCode, toCode)
		textRate.text = "Rate: 1 $fromCode = ${formatNumber(rate)} $toCode"
	}

	private fun getRate(from: String, to: String): Double {
		val fromPerUSD = ratesToUSD[from] ?: 1.0
		val toPerUSD = ratesToUSD[to] ?: 1.0
		// Convert: amount_in_to = amount_in_from * (toPerUSD / fromPerUSD)
		return if (fromPerUSD == 0.0) 0.0 else toPerUSD / fromPerUSD
	}

	private fun formatNumber(value: Double): String {
		// Trim trailing zeros while keeping reasonable precision
		val text = String.format(java.util.Locale.US, "%.6f", value)
		return text.trimEnd('0').trimEnd('.')
	}

	private fun simpleListener(onChanged: (Int) -> Unit) =
		object : android.widget.AdapterView.OnItemSelectedListener {
			override fun onItemSelected(
				parent: android.widget.AdapterView<*>?,
				view: android.view.View?,
				position: Int,
				id: Long
			) {
				onChanged(position)
			}
			override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
		}
}