package com.example.travelplanner10

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class LocationSearchActivity : AppCompatActivity() {

    private lateinit var suggestionsAdapter: SuggestionsAdapter
    private val locationSuggestions = mutableListOf<GeoName>()

    private lateinit var geoNamesService: GeoNamesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)

        val searchBar : EditText = findViewById(R.id.search_bar)

        // quando viene premuto il tasto enter
        searchBar.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val manualLocation = textView.text.toString()
                val resultIntent = Intent().apply {
                    putExtra("selectedLocation", manualLocation)
                    putExtra("latitude", 0.0) // 0.0 default
                    putExtra("longitude", 0.0)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
                true
            } else {
                false
            }
        }

        // inizializzo la suggestions list e  l'adapter
        suggestionsAdapter = SuggestionsAdapter(locationSuggestions) { selectedGeoName ->
            val resultIntent = Intent().apply {
                putExtra("selectedLocation", selectedGeoName.name)
                putExtra("latitude", selectedGeoName.lat)
                putExtra("longitude", selectedGeoName.lng)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // inizializzo il RecyclerView
        val suggestions_recycler_view: RecyclerView = findViewById(R.id.suggestions_recycler_view)
        suggestions_recycler_view.layoutManager = LinearLayoutManager(this)
        suggestions_recycler_view.adapter = suggestionsAdapter

        // uso  TextWatcher collegato alla search_bar per aggiornare i suggerimenti
        val search_bar: EditText = findViewById(R.id.search_bar)
        search_bar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            //uso soltanto quando il testo cambia.
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSuggestions(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })



        // inzializzo Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.geonames.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        geoNamesService = retrofit.create(GeoNamesService::class.java)


    }

    private fun updateSuggestions(query: String) {
        if (query.isEmpty()) {
            locationSuggestions.clear()
            suggestionsAdapter.notifyDataSetChanged()
            return
        }

        Log.d("LocationSearchActivity", "Searching for locations with query: $query")

        val call = geoNamesService.searchLocations(query, 10, "gaspare")
        call.enqueue(object : Callback<GeoNamesResponse> {
            override fun onResponse(call: Call<GeoNamesResponse>, response: Response<GeoNamesResponse>) {
                Log.d("LocationSearchActivity", "Response: $response")
                if (response.isSuccessful) {
                    locationSuggestions.clear()
                    val locations = response.body()?.geonames ?: emptyList()
                    locationSuggestions.addAll(locations)
                    suggestionsAdapter.notifyDataSetChanged()
                }else {
                    Log.e("LocationSearchActivity", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GeoNamesResponse>, t: Throwable) {
                // quando l'API fallisce
                Log.e("LocationSearchActivity", "API call failed", t)
                locationSuggestions.clear()
                suggestionsAdapter.notifyDataSetChanged()
            }
        })
    }

}
