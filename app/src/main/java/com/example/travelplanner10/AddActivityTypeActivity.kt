package com.example.travelplanner10


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddActivityTypeActivity : AppCompatActivity() {

    private var placeId: Int = -1
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_activity_type)

        database = AppDatabase.getDatabase(this)

        placeId = intent.getIntExtra("PLACE_ID", -1)

        findViewById<Button>(R.id.btnAddRestaurant).setOnClickListener {
            addActivity("Restaurant")
        }

        findViewById<Button>(R.id.btnAddAttraction).setOnClickListener {
            addActivity("Attraction")
        }

        findViewById<Button>(R.id.btnAddMuseum).setOnClickListener {
            addActivity("Museum")
        }

        findViewById<Button>(R.id.btnAddHiking).setOnClickListener {
            addActivity("Hiking")
        }

        findViewById<Button>(R.id.btnAddShopping).setOnClickListener {
            addActivity("Shopping")
        }

        // si possono aggiungere altri onClickListener per aggiungere altre attività

    }

    private fun addActivity(type: String) {
        val activity = EditPlaceActivity.Activity(
            id = 0,
            placeId = placeId,
            name = "$type Activity",
            date = "2024-07-10", // Placeholder date, replace with actual date logic
            description = "Description of $type"
        )

        GlobalScope.launch(Dispatchers.IO) {
            database.ActivityDao().insert(activity)
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }
    }
}
