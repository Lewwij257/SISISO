package com.example.sisiso

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import simplex_solvers.SimplexTable

class SolutionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_solution)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvSpecial = findViewById<TextView>(R.id.text_special)

        try{
            val tvSolutionArray = findViewById<TextView>(R.id.text_solution_array)
            val tvSolution = findViewById<TextView>(R.id.text_solution)

            val jsonSimplexTable = intent.getStringExtra("simplexTable")
            val simplexTable: SimplexTable = Gson().fromJson(jsonSimplexTable, SimplexTable::class.java)

            tvSolutionArray.text = simplexTable.getSolutionArray().contentToString()
            tvSolution.text = simplexTable.getSolution().toString()
        }
        catch (e: Exception){
            tvSpecial.visibility = View.VISIBLE
        }
    }



}