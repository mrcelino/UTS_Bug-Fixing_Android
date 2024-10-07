package com.example.soal_uts_bug_fixing

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import android.text.format.DateFormat
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import com.example.soal_uts_bug_fixing.databinding.ActivityAppointmentBinding
import com.example.soal_uts_bug_fixing.databinding.DialogExitBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AppointmentActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogExit.DialogListener {
    private lateinit var binding: ActivityAppointmentBinding

    companion object {
        const val EXTRA_TELEFON = "extra_phone"
        const val EXTRA_ALAMAT = "extra_alamat"
        const val EXTRA_TIPE = "extra_tipe"
        const val EXTRA_TANGGAL = "extra_tanggal"
        const val EXTRA_WAKTU = "extra_waktu"
    }

    private lateinit var dateInput: String
    private lateinit var timeInput: String
    private lateinit var tipePertemuan: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dateInput = ""
        timeInput = ""
        tipePertemuan = ""

        with(binding) {
            // diubah sesuai button nya di xml
            kalenderTxt.setOnClickListener {
                val datePicker = DatePicker()
                datePicker.show(supportFragmentManager, "datePicker")
            }

            timerTxt.setOnClickListener {
                val timePicker = TimePicker()
                timePicker.show(supportFragmentManager, "timePicker")
            }

            submitBtn.setOnClickListener {
                if (fieldNotEmpty()) {
                    val dialog = DialogExit()
                    dialog.show(supportFragmentManager, "DialogExit")
                } else {
                    Toast.makeText(this@AppointmentActivity, "MASIH ADA KOLOM YANG KOSONG", Toast.LENGTH_SHORT).show()
                }
            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioButton1 -> {
                        tipePertemuan = radioButton1.text.toString()
                    }
                    R.id.radioButton2 -> {
                        tipePertemuan = radioButton2.text.toString()
                    }
                }
                inputLayout.visibility = if (tipePertemuan == "Online") View.GONE else View.VISIBLE
            }
        }
    }

    // mengubah ini
    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        dateInput = dateFormat.format(calendar.time)
        binding.kalenderTxt.text = dateInput
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        timeInput = String.format("%02d:%02d", hourOfDay, minute)
        binding.timerTxt.text = timeInput
    }

    // mengubah result ini
    override fun onDialogResult(result: Boolean) {
        val nama = intent.getStringExtra(FormActivity.EXTRA_NAMA)
        val identitas = intent.getStringExtra(FormActivity.EXTRA_IDENTITAS)
        val gender = intent.getStringExtra(FormActivity.EXTRA_GENDER)

        if (result) {
            val intentToResult = Intent(this@AppointmentActivity, ResultActivity::class.java).apply {
                putExtra(EXTRA_TELEFON, binding.kontakEdt.text.toString())
                putExtra(EXTRA_TANGGAL, binding.kalenderTxt.text.toString())
                putExtra(EXTRA_WAKTU, binding.timerTxt.text.toString())
                putExtra(EXTRA_TIPE, tipePertemuan)
                putExtra(FormActivity.EXTRA_NAMA, nama)
                putExtra(FormActivity.EXTRA_IDENTITAS, identitas)
                putExtra(FormActivity.EXTRA_GENDER, gender)
                if (tipePertemuan == "Offline") {
                    putExtra(EXTRA_ALAMAT, binding.lokasiEdt.text.toString())
                }
            }
            startActivity(intentToResult)
        }
    }

    private fun fieldNotEmpty(): Boolean {
        with(binding) {
            if (kontakEdt.text.toString().isNotEmpty() && tipePertemuan.isNotEmpty() && timeInput.isNotEmpty() && dateInput.isNotEmpty()) {
                return if (tipePertemuan == "Offline") lokasiEdt.text.toString().isNotEmpty() else true
            }
            return false
        }
    }
}


// mengisi date picker
class DatePicker : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireActivity(), activity as DatePickerDialog.OnDateSetListener, year, month, day)
    }
}


// mengisi date picker
class TimePicker : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(
            activity,
            activity as TimePickerDialog.OnTimeSetListener,
            hour,
            minute,
            DateFormat.is24HourFormat(activity)
        )
    }
}

// exit dialog
class DialogExit : DialogFragment() {
    interface DialogListener {
        fun onDialogResult(result: Boolean)
    }

    private lateinit var listener: DialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? DialogListener
            ?: throw ClassCastException("$context must implement DialogListener")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val binding = DialogExitBinding.inflate(inflater)

        with(binding) {
            yesBtn.setOnClickListener {
                listener.onDialogResult(true)
                dismiss()
            }
            noBtn.setOnClickListener {
                listener.onDialogResult(false)
                dismiss()
            }
        }
        builder.setView(binding.root)
        return builder.create()
    }
}
