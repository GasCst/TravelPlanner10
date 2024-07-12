package com.example.travelplanner10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter1: PlaceAdapter
    private lateinit var database: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Aggiungi questa activity al ActivityCollector
        ActivityCollector.addActivity(this)

        // button per aggiungere un nuovo itinerario (  place sarebbe l'itinerario ... )
        val btnAddPlace: Button = findViewById(R.id.btnAddPlace)
        btnAddPlace.setOnClickListener {
            val intent = Intent(this, AddPlaceActivity::class.java)
            startActivity(intent)
        }

        // inizializzo il database e il recyclerView
        database = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // inizializzo l'adapter con una mutableList vuota

        adapter1 = PlaceAdapter(
            mutableListOf(),
            { placeId -> deletePlace(placeId) },
            { placeId -> val intent = Intent(this, EditPlaceActivity::class.java)
            intent.putExtra("PLACE_ID", placeId)
            startActivity(intent)
            })
        recyclerView.adapter = adapter1



        GlobalScope.launch(Dispatchers.IO) {
            val places = database.placeDao().getAll()
            runOnUiThread {
                adapter1.updatePlaces(places)
            }
        }


        val logoutButton: Button = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            // Codice per eseguire il logout , che richiama finishall e riporta alla LoginActivity
            ActivityCollector.finishAll()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun deletePlace(placeId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            database.placeDao().deleteById(placeId)
            val filteredPlaces = adapter1.places.filter { it.id != placeId } // Assuming places is accessible in adapter
            runOnUiThread {
                adapter1.updatePlaces(filteredPlaces)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // ogni volta che riprende l'activity aggiorno
        loadPlaces()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    private fun loadPlaces() {
        GlobalScope.launch(Dispatchers.Main) {
            val places = database.placeDao().getAll()
            adapter1.updatePlaces(places)
        }
    }
}




