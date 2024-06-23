package com.example.sisiso

import adapters.EquationsSystemAdapter
import adapters.XOrderHorizontalAdapter
import adapters.XOrderVerticalAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import simplex_solvers.DefaultSimplexSolver
import simplex_solvers.SimplexTable

class DefaultSimplexActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_default_simplex)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gridXOrderHorizontal = findViewById<GridView>(R.id.grid_x_order_horizontal)
        val gridXOrderVertical = findViewById<GridView>(R.id.grid_x_order_vertical)
        val gridEquationsSystem = findViewById<GridView>(R.id.grid_equations_system)

        val btnRestartActivity = findViewById<Button>(R.id.button_restart_solve)
        val btnCalculate = findViewById<Button>(R.id.button_calculate)
        val btnPreviousStep = findViewById<Button>(R.id.button_previous_step)
        val btnNextStep = findViewById<Button>(R.id.button_next_step)

        val tvTextInstruction = findViewById<TextView>(R.id.text_instruction)

        val jsonSimplexTable = intent.getStringExtra("simplexTable")
        val simplexTable: SimplexTable = Gson().fromJson(jsonSimplexTable, SimplexTable::class.java)

        val stringEquationsSystem = simplexTable.stringSimplexTable!!
        val doubleEquationsSystem = simplexTable.doubleSimplexTable!!
        val xOrderArray = simplexTable.xOrderArray!!
        val originProblem = simplexTable.stringOriginProblem!!
        val ordinaryFractionState = simplexTable.ordinaryFractionState!!

        val simplexTablesList = ArrayList<SimplexTable>()
        var currentSimplexTableIndex = 0
        simplexTablesList.add(simplexTable.deepCopy())


        gridXOrderHorizontal.adapter =
            XOrderHorizontalAdapter(this, stringEquationsSystem.first().size - 1)
        gridXOrderHorizontal.numColumns = stringEquationsSystem.first().size

        gridXOrderVertical.adapter = XOrderVerticalAdapter(this, stringEquationsSystem.size)
        gridXOrderVertical.numColumns = 1

        gridEquationsSystem.adapter = EquationsSystemAdapter(
            this,
            stringEquationsSystem.first().size,
            stringEquationsSystem.size,
            ordinaryFractionState,
            true
        )
        gridEquationsSystem.numColumns = stringEquationsSystem.first().size

        (gridXOrderHorizontal.adapter as XOrderHorizontalAdapter).setXOrder(simplexTable.xOrderArray!![0])
        (gridXOrderVertical.adapter as XOrderVerticalAdapter).setXOrder(simplexTable.xOrderArray!![1])
        (gridEquationsSystem.adapter as EquationsSystemAdapter).setMatrix(simplexTable.stringSimplexTable!!)

        btnRestartActivity.setOnClickListener {

        }

        btnCalculate.setOnClickListener {

            val (selectedXIndex, selectedYIndex) = (gridEquationsSystem.adapter as EquationsSystemAdapter).getSelectedCellXYPosition()

            if (selectedXIndex < 0 || selectedYIndex < 0){
                tvTextInstruction.text = getString(R.string.please_select_support_item_before_simplex_step)
                return@setOnClickListener
            }

            if (currentSimplexTableIndex != simplexTablesList.size-1){
                simplexTablesList.subList(currentSimplexTableIndex+1, simplexTablesList.size).clear()
                btnNextStep.background = AppCompatResources.getDrawable(this, R.drawable.btn_next_grey)

            }

            val newSimplexTable = DefaultSimplexSolver.simplexSingleStep(simplexTable, selectedXIndex, selectedYIndex)
            simplexTablesList.add(newSimplexTable.deepCopy())

            (gridEquationsSystem.adapter as EquationsSystemAdapter).setMatrix(newSimplexTable.stringSimplexTable!!)
            (gridEquationsSystem.adapter as EquationsSystemAdapter).resetSelectedPosition()
            (gridXOrderHorizontal.adapter as XOrderHorizontalAdapter).setXOrder(newSimplexTable.xOrderArray!![0])
            (gridXOrderVertical.adapter as XOrderVerticalAdapter).setXOrder(newSimplexTable.xOrderArray!![1])

            if (DefaultSimplexSolver.checkIfUnsolvableSimplexTable(newSimplexTable.doubleSimplexTable!!)){
                tvTextInstruction.text = getString(R.string.simplex_table_is_now_unsolvable_click_next_to_restart)
                btnCalculate.setOnClickListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            if (DefaultSimplexSolver.checkIfSolvedSimplexTable(newSimplexTable.doubleSimplexTable!!)){
                tvTextInstruction.text = getString(R.string.simplex_table_is_solved_click_next_to_see_solve)
                btnCalculate.setOnClickListener {
                    val intent = Intent(this, SolutionActivity::class.java)
                    intent.putExtra("simplexTable", Gson().toJson(newSimplexTable))
                    startActivity(intent)
                    finish()
                }
            }
        }

        btnNextStep.setOnClickListener {
            if (currentSimplexTableIndex < simplexTablesList.size-1){
                currentSimplexTableIndex += 1
                val currentSimplexTable = simplexTablesList[currentSimplexTableIndex]
                (gridEquationsSystem.adapter as EquationsSystemAdapter).setMatrix(currentSimplexTable.stringSimplexTable!!)
                (gridXOrderHorizontal.adapter as XOrderHorizontalAdapter).setXOrder(currentSimplexTable.xOrderArray!![0])
                (gridXOrderVertical.adapter as XOrderVerticalAdapter).setXOrder(currentSimplexTable.xOrderArray!![1])
            }
            if (currentSimplexTableIndex+1==simplexTablesList.size){
                btnNextStep.background = AppCompatResources.getDrawable(this, R.drawable.btn_next_grey)
            }
            else{
                btnNextStep.background = AppCompatResources.getDrawable(this, R.drawable.btn_next)
            }
            if (currentSimplexTableIndex-1!=-1){
                btnPreviousStep.background = AppCompatResources.getDrawable(this, R.drawable.btn_previous_grey)
            }
            else{
                btnPreviousStep.background = AppCompatResources.getDrawable(this, R.drawable.btn_previous)
            }
        }

        btnPreviousStep.setOnClickListener {
            if (currentSimplexTableIndex > 0){
                currentSimplexTableIndex -= 1
                val currentSimplexTable = simplexTablesList[currentSimplexTableIndex]
                (gridEquationsSystem.adapter as EquationsSystemAdapter).setMatrix(currentSimplexTable.stringSimplexTable!!)
                (gridXOrderHorizontal.adapter as XOrderHorizontalAdapter).setXOrder(currentSimplexTable.xOrderArray!![0])
                (gridXOrderVertical.adapter as XOrderVerticalAdapter).setXOrder(currentSimplexTable.xOrderArray!![1])

                if(currentSimplexTableIndex == 0){
                    btnPreviousStep.background = AppCompatResources.getDrawable(this, R.drawable.btn_previous_grey)
                }
                else{
                    btnPreviousStep.background = AppCompatResources.getDrawable(this, R.drawable.btn_previous)
                }
                if (currentSimplexTableIndex+1!=simplexTablesList.size-1){
                    btnNextStep.background = AppCompatResources.getDrawable(this, R.drawable.btn_next)
                }
                else{
                    btnNextStep.background = AppCompatResources.getDrawable(this, R.drawable.btn_next_grey)

                }
            }
        }

        if (DefaultSimplexSolver.checkIfSolvedSimplexTable(simplexTable.doubleSimplexTable!!)) {
            tvTextInstruction.text = getString(R.string.simplex_table_is_solved_click_next_to_see_solve)
            btnCalculate.setOnClickListener {
                val intent = Intent(this, SolutionActivity::class.java)
                intent.putExtra("simplexTable", Gson().toJson(simplexTable))
                startActivity(intent)
                finish()
            }
        }
    }
}

