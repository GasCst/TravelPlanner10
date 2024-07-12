package com.example.travelplanner10

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

class AddPlaceActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    private val locationSearchLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedLocation = result.data?.getStringExtra("selectedLocation")
            inputDestination.setText(selectedLocation)
            latitude = result.data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            longitude = result.data?.getDoubleExtra("longitude", 0.0) ?: 0.0
        }
    }

    private lateinit var inputDestination: TextInputEditText
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)

        database = AppDatabase.getDatabase(this)

        val Name:        TextInputEditText = findViewById(R.id.inputName)
        val Description: TextInputEditText = findViewById(R.id.inputDescription)
        val Image:       TextInputEditText = findViewById(R.id.inputImage)
        val Date:        Spinner = findViewById(R.id.spinnerDate)
        val StartTime:   Spinner = findViewById(R.id.spinnerStartTime)
        val EndTime:     Spinner = findViewById(R.id.spinnerEndTime)
        val btnSave: Button = findViewById(R.id.btnSave)
        var placeToDeleteId: Int = -1

        inputDestination = findViewById(R.id.inputDestination)


        // popolo lo spinner per selezionare la data
        val dates = ArrayList<String>()
        val calendar = Calendar.getInstance()
        for (i in 0 until 30) {
            dates.add("${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}")
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        val dateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dates)
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        Date.adapter = dateAdapter

        // popolo lo spinner per selezionare l'ora
        val times = ArrayList<String>()
        for (i in 0 until 24) {
            for (j in 0 until 60 step 15) {
                times.add(String.format("%02d:%02d", i, j))
            }
        }

        val timeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, times)
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        StartTime.adapter = timeAdapter
        EndTime.adapter = timeAdapter


        // ---------------------------------------

        /* lancio intent per LocationSearchActivity quando clicco su inputDestination che è un
          TextInputEditText  , inoltre ho fatto in modo che apra l'ntent al primo click con setOnFocusChangeListener*/
        inputDestination.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                inputDestination.clearFocus()
                val intent = Intent(this, LocationSearchActivity::class.java)
                locationSearchLauncher.launch(intent)
            }
        }



        btnSave.setOnClickListener {
            val name      = Name.text.toString()
            val description = Description.text.toString()
            val image     = Image.text.toString()
            val date      = Date.selectedItem.toString()
            val startTime = StartTime.selectedItem.toString()
            val endTime   = EndTime.selectedItem.toString()

            GlobalScope.launch(Dispatchers.IO) {
                savePlace(name,description,image,date,startTime,endTime, latitude, longitude)
            }
            GlobalScope.launch(Dispatchers.IO) {
                if (placeToDeleteId != -1) {
                    database.placeDao().deleteById(placeToDeleteId)
                }
            }
            finish()
        }
    }


    private suspend fun savePlace(name: String, description: String, image:String, date: String, startTime: String, endTime: String, latitude: Double, longitude: Double) {
        val place = Place(name = name, description = description, image = image, date = date, startTime = startTime, endTime = endTime, latitude = latitude, longitude = longitude)
        database.placeDao().insert(place)
        finish()
    }
}


