package com.example.travelplanner10

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class EditPlaceActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private var placeId: Int = -1


    private lateinit var nameInput: TextInputEditText
    private lateinit var mapView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter
    private val activities = mutableListOf<Activity>()
    private var place: Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_place)

        database = AppDatabase.getDatabase(this)

        nameInput = findViewById(R.id.textViewPlaceName)
        mapView = findViewById(R.id.imageViewMap)
        recyclerView = findViewById(R.id.recyclerViewActivities)
        adapter = ActivityAdapter(activities) { activity ->
            deleteActivity(activity)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnAddActivity: Button = findViewById(R.id.btnAddActivity)
        val btnSave: Button = findViewById(R.id.btnSave)

        // Retrieve placeId from intent
        placeId = intent.getIntExtra("PLACE_ID", -1)


        if (placeId != -1) {
            loadPlaceDetails()
            loadActivities()
        }



        val addNewActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    loadActivities() // aggiorno tutte le activities dopo che una nuova viene aggiunta
                }
            }

        btnAddActivity.setOnClickListener {
            val intent = Intent(this, AddActivityTypeActivity::class.java)
            intent.putExtra("PLACE_ID", placeId)
            addNewActivityLauncher.launch(intent)
        }

        btnSave.setOnClickListener {
            finish()
        }
        mapView.setOnClickListener {
            place?.let { openMap(it) } // safe call per essere sicuri che non sia null
        }
    }

    private fun loadPlaceDetails() {
        GlobalScope.launch(Dispatchers.IO) {
            place = database.placeDao().getById(placeId)
            runOnUiThread {
                place?.let {
                    nameInput.setText(it.name)
                }
            }
        }
    }

    private fun loadActivities() {
        GlobalScope.launch(Dispatchers.IO) {
            val activities = database.ActivityDao().getActivitiesForPlace(placeId)
            runOnUiThread {
                adapter.updateActivities(activities)
            }
        }
    }

    private fun deleteActivity(activity: Activity) {
        GlobalScope.launch(Dispatchers.IO) {
            database.ActivityDao().delete(activity.id)
            runOnUiThread {
                adapter.deleteActivity(activity)
            }
        }
    }

    private fun openMap(place: Place) {
        val gmmIntentUri = Uri.parse("geo:${place.latitude},${place.longitude}?q=${place.latitude},${place.longitude}(${place.name})")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_ACTIVITY && resultCode == RESULT_OK) {
            GlobalScope.launch(Dispatchers.IO) {
                val activitiesList = database.ActivityDao().getActivitiesForPlace(placeId)
                runOnUiThread {
                    activities.clear()
                    activities.addAll(activitiesList)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        const val REQUEST_ADD_ACTIVITY = 1
    }

    // Classe modello per le attività
    @Entity(tableName = "activities")
    data class Activity(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "placeId") val placeId: Int,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "date") val date: String,
        @ColumnInfo(name = "description") val description: String
    )

}//





