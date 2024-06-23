package com.example.sisiso

import adapters.EquationsSystemAdapter
import adapters.OriginProblemAdapter
import adapters.XOrderHorizontalAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import utils.InputValidator
import com.google.gson.Gson
import simplex_solvers.ArtificialBasisSolver
import simplex_solvers.DefaultSimplexSolver
import simplex_solvers.GaussianMethod
import simplex_solvers.SimplexTable
import utils.MatrixAdditionalFunctions

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etVariablesCount = findViewById<EditText>(R.id.edit_variables_count)
        val etConstraintsCount = findViewById<EditText>(R.id.edit_equations_count)
        val tvInputErrorText = findViewById<TextView>(R.id.text_build_matrix_error_text)

        val btnBuildMatrix = findViewById<Button>(R.id.button_build_matrix)
        val btnOpenSolveSettings = findViewById<Button>(R.id.button_solve_settings)
        val btnRestartActivity = findViewById<Button>(R.id.button_restart_activity)
        val btnOpenInstruction = findViewById<Button>(R.id.button_info)
        val btnAcceptMatrix = findViewById<Button>(R.id.button_accept_matrices_inputs)
        val btnOpenProgramSettings = findViewById<Button>(R.id.button_app_settings)
        val btnStart = findViewById<View>(R.id.button_start)

        val gridEquationsSystem = findViewById<GridView>(R.id.grid_equations_system)
        val gridXOrderHorizontal = findViewById<GridView>(R.id.grid_x_order_horizontal)
        val gridOriginProblem = findViewById<GridView>(R.id.grid_origin_function)
        val gridGraphicEquationsSystem = findViewById<GridView>(R.id.grid_graphic_equations_system)

        var variablesCount = 0
        var constraintsCount = 0
        var calculationsSimplexTable = Array(0){DoubleArray(0)}



        btnBuildMatrix.setOnClickListener {

            val sharedPreferences = getSharedPreferences("SolveSettings", Context.MODE_PRIVATE)
            val solveMethodState = sharedPreferences.getInt("solve_method", 0)
            val ordinaryFractionState = sharedPreferences.getBoolean("ordinary_fraction_state", false)

            variablesCount = etVariablesCount.text.toString().toInt()
            constraintsCount = etConstraintsCount.text.toString().toInt()

            if (InputValidator.isValidRowsAndColumnsInput(etVariablesCount, etConstraintsCount)){
                if (solveMethodState==2){

                }

                else{

                    gridOriginProblem.visibility = View.VISIBLE
                    gridXOrderHorizontal.visibility = View.VISIBLE
                    gridEquationsSystem.visibility = View.VISIBLE

                    gridXOrderHorizontal.adapter = XOrderHorizontalAdapter(this, variablesCount)
                    gridXOrderHorizontal.numColumns = variablesCount + 1
                    (gridXOrderHorizontal.adapter as XOrderHorizontalAdapter).setXOrder(IntArray(variablesCount) { it + 1 })

                    gridOriginProblem.adapter = OriginProblemAdapter(this, variablesCount + 1, ordinaryFractionState, false)
                    gridOriginProblem.numColumns = variablesCount + 1

                    gridEquationsSystem.adapter = EquationsSystemAdapter(this, variablesCount + 1, constraintsCount, ordinaryFractionState, false)
                    gridEquationsSystem.numColumns = variablesCount + 1

                    btnOpenSolveSettings.visibility = View.GONE
                    btnRestartActivity.visibility = View.VISIBLE
                    tvInputErrorText.visibility = View.GONE
                }
            }
            else{
                tvInputErrorText.visibility = View.VISIBLE
                tvInputErrorText.text = getString(R.string.incorrect_input)
            }
        }

        btnAcceptMatrix.setOnClickListener {

            val sharedPreferences = getSharedPreferences("SolveSettings", Context.MODE_PRIVATE)
            val preferencesEditor = getSharedPreferences("SolveSettings", Context.MODE_PRIVATE).edit()

            val ordinaryFractionState = sharedPreferences.getBoolean("ordinary_fractions_state", false)
            val solveMethodState = sharedPreferences.getInt("solve_method", 0)

            val equationsSystem = MatrixAdditionalFunctions.deepCopyTwoDimStringArray((gridEquationsSystem.adapter as EquationsSystemAdapter).getMatrix())
            var originProblem = MatrixAdditionalFunctions.deepCopyOneDimStringArray((gridOriginProblem.adapter as OriginProblemAdapter).getOriginProblem())

            val jsonEquationSystem = Gson().toJson(equationsSystem)
            val jsonOriginProblem = Gson().toJson(originProblem)
            preferencesEditor.putString("json_equation_system", jsonEquationSystem)
            preferencesEditor.putString("json_origin_problem", jsonOriginProblem)
            preferencesEditor.apply()

            if (originProblem.last()=="max"){
                val newOriginProblem = originProblem.map{
                    when(it){
                        "max" -> "min"
                        else -> ((it.toInt()*-1).toString())
                    }
                }.toTypedArray()
                (gridOriginProblem.adapter as OriginProblemAdapter).setOriginProblem(newOriginProblem)
                originProblem = newOriginProblem
            }

            try{
                when(solveMethodState){
                    0->{
                        //simple simplex
                        gridEquationsSystem.adapter = EquationsSystemAdapter(this, variablesCount+1, constraintsCount, ordinaryFractionState, true)
                        gridEquationsSystem.numColumns = variablesCount+1
                        (gridEquationsSystem.adapter as EquationsSystemAdapter).setMatrix(GaussianMethod.eliminate(equationsSystem))
                        gridOriginProblem.adapter = OriginProblemAdapter(this, variablesCount+1, ordinaryFractionState, true)
                        gridOriginProblem.numColumns = variablesCount+1
                        (gridOriginProblem.adapter as OriginProblemAdapter).setOriginProblem(originProblem)
                        calculationsSimplexTable = MatrixAdditionalFunctions.convertString2DArrayToDouble2DArray(GaussianMethod.eliminate(equationsSystem))
                    }
                    1 -> {
                        //artificial basis method
                        gridEquationsSystem.adapter = EquationsSystemAdapter(this, variablesCount+1, constraintsCount, ordinaryFractionState, true)
                        gridEquationsSystem.numColumns = variablesCount+1
                        (gridEquationsSystem.adapter as EquationsSystemAdapter).setMatrix(equationsSystem)
                        gridOriginProblem.adapter = OriginProblemAdapter(this, variablesCount+1, ordinaryFractionState, true)
                        gridOriginProblem.numColumns = variablesCount+1
                        (gridOriginProblem.adapter as OriginProblemAdapter).setOriginProblem(originProblem)
                        calculationsSimplexTable = MatrixAdditionalFunctions.convertString2DArrayToDouble2DArray(equationsSystem)
                    }
                    2 -> {
                        //graphic method
                    }
                }
                btnAcceptMatrix.visibility = View.GONE
                btnStart.visibility = View.VISIBLE
            }
            catch (e: Exception){
                tvInputErrorText.text = getString(R.string.incorrect_input)
            }
        }

        btnStart.setOnClickListener{
            val sharedPreferences = getSharedPreferences("SolveSettings", Context.MODE_PRIVATE)
            val stepByStepSolveState = sharedPreferences.getBoolean("step_by_step_state", false)
            val ordinaryFractionsState = sharedPreferences.getBoolean("ordinary_fraction_state", false)
            val solveMethodState = sharedPreferences.getInt("solve_method", 0)

            when (stepByStepSolveState to solveMethodState){
                true to 0 -> {
                    val originProblem = ((gridOriginProblem.adapter as OriginProblemAdapter).getOriginProblem()).dropLast(1).toTypedArray()
                    val preSimplexTable = (((gridEquationsSystem.adapter as EquationsSystemAdapter).getMatrix()).map { subArray -> subArray.drop(calculationsSimplexTable.size).toTypedArray() }.toTypedArray())
                    val simplexTable = SimplexTable()
                    simplexTable.doubleSimplexTable = DefaultSimplexSolver.createSimplexTable(preSimplexTable, originProblem)
                    simplexTable.stringSimplexTable = (DefaultSimplexSolver.createSimplexTable(preSimplexTable, originProblem)).map { it.map { it.toString() }.toTypedArray() }.toTypedArray()
                    simplexTable.xOrderArray = arrayOf(IntArray(calculationsSimplexTable.first().size-calculationsSimplexTable.size-1){it+calculationsSimplexTable.size+1}, IntArray(calculationsSimplexTable.size){it+1})
                    simplexTable.stringOriginProblem = (gridOriginProblem.adapter as OriginProblemAdapter).getOriginProblem()
                    simplexTable.ordinaryFractionState = ordinaryFractionsState

                    val intent = Intent(this, DefaultSimplexActivity::class.java)
                    intent.putExtra("simplexTable", Gson().toJson(simplexTable))
                    startActivity(intent)
                    finish()
                }
                true to 1-> {
                    val originProblem = ((gridOriginProblem.adapter as OriginProblemAdapter).getOriginProblem()).dropLast(1).toTypedArray()
                    val preSimplexTable = (((gridEquationsSystem.adapter as EquationsSystemAdapter).getMatrix()))
                    val simplexTable = SimplexTable()
                    simplexTable.doubleSimplexTable = MatrixAdditionalFunctions.convertString2DArrayToDouble2DArray(ArtificialBasisSolver.createSimplexTable(preSimplexTable))
                    simplexTable.stringSimplexTable = ArtificialBasisSolver.createSimplexTable(preSimplexTable)
                    simplexTable.xOrderArray = arrayOf(IntArray(calculationsSimplexTable.first().size-1){it+1}, IntArray(calculationsSimplexTable.size){it+calculationsSimplexTable.first().size})
                    simplexTable.stringOriginProblem = (gridOriginProblem.adapter as OriginProblemAdapter).getOriginProblem()
                    simplexTable.ordinaryFractionState = ordinaryFractionsState

                    val intent = Intent(this, ArtificialBasisActivity::class.java)
                    intent.putExtra("simplexTable", Gson().toJson(simplexTable))
                    startActivity(intent)
                    finish()
                }
                true to 2 -> {

                }
                false to 0 -> {
                    val originProblem = ((gridOriginProblem.adapter as OriginProblemAdapter).getOriginProblem()).dropLast(1).toTypedArray()
                    val preSimplexTable = (((gridEquationsSystem.adapter as EquationsSystemAdapter).getMatrix()).map { subArray -> subArray.drop(calculationsSimplexTable.size).toTypedArray() }.toTypedArray())
                    val simplexTable = SimplexTable()
                    simplexTable.doubleSimplexTable = DefaultSimplexSolver.createSimplexTable(preSimplexTable, originProblem)
                    simplexTable.stringSimplexTable = (DefaultSimplexSolver.createSimplexTable(preSimplexTable, originProblem)).map { it.map { it.toString() }.toTypedArray() }.toTypedArray()
                    simplexTable.xOrderArray = arrayOf(IntArray(calculationsSimplexTable.first().size-calculationsSimplexTable.size-1){it+calculationsSimplexTable.size+1}, IntArray(calculationsSimplexTable.size){it+1})
                    simplexTable.stringOriginProblem = (gridOriginProblem.adapter as OriginProblemAdapter).getOriginProblem()
                    simplexTable.ordinaryFractionState = ordinaryFractionsState
                    val resultSimplexTable = DefaultSimplexSolver.simplexAutoSolve(simplexTable)

                    val intent = Intent(this, SolutionActivity::class.java)
                    intent.putExtra("simplexTable", Gson().toJson(resultSimplexTable))
                    startActivity(intent)
                    finish()
                }
                false to 1 -> {

                }
                false to 2 -> {

                }
            }
        }


        btnOpenSolveSettings.setOnClickListener {

            val dialogWindow = Dialog(this)
            dialogWindow.setContentView(R.layout.activity_solve_settings)
            dialogWindow.show()

            val btnSaveSolveSettings =
                dialogWindow.findViewById<Button>(R.id.button_save_solve_settings)

            val sharedPreferences = getSharedPreferences("SolveSettings", Context.MODE_PRIVATE)
            val preferencesEditor = sharedPreferences.edit()

            val stepByStepState = sharedPreferences.getBoolean("step_by_step_state", false)
            val ordinaryFractionState =
                sharedPreferences.getBoolean("ordinary_fraction_state", false)
            val solveMethod = sharedPreferences.getInt("solve_method", 0)

            dialogWindow.findViewById<CheckBox>(R.id.check_step_by_step).isChecked = stepByStepState
            dialogWindow.findViewById<CheckBox>(R.id.check_ordinary_fraction).isChecked =
                ordinaryFractionState
            dialogWindow.findViewById<Spinner>(R.id.spin_solve_method).setSelection(solveMethod)

            btnSaveSolveSettings.setOnClickListener {
                preferencesEditor.putBoolean(
                    "step_by_step_state",
                    dialogWindow.findViewById<CheckBox>(R.id.check_step_by_step).isChecked
                )
                preferencesEditor.putBoolean(
                    "ordinary_fraction_state",
                    dialogWindow.findViewById<CheckBox>(R.id.check_ordinary_fraction).isChecked
                )
                preferencesEditor.putInt(
                    "solve_method",
                    dialogWindow.findViewById<Spinner>(R.id.spin_solve_method).selectedItemPosition
                )
                preferencesEditor.apply()
                Toast.makeText(applicationContext, R.string.saved, Toast.LENGTH_LONG).show()
                dialogWindow.dismiss()
            }
        }

        btnOpenInstruction.setOnClickListener {

            val dialogWindow = Dialog(this)
            dialogWindow.setContentView(R.layout.activity_main_instruction)
            dialogWindow.show()

            var instructionPage = 0

            val btnPreviousInstruction =
                dialogWindow.findViewById<Button>(R.id.button_previous_instruction)
            val btnNextInstruction = dialogWindow.findViewById<Button>(R.id.button_next_instruction)
            val btnCloseInstructionWindow = dialogWindow.findViewById<Button>(R.id.button_close)
            val imgImageInstruction =
                dialogWindow.findViewById<ImageView>(R.id.image_instruction_image)
            val tvTextInstruction = dialogWindow.findViewById<TextView>(R.id.text_instruction)

            val instructionImagesArray = arrayOf(
                AppCompatResources.getDrawable(this, R.drawable.img_main_instruction_1),
                AppCompatResources.getDrawable(this, R.drawable.img_main_instruction_2),
                AppCompatResources.getDrawable(this, R.drawable.img_main_instruction_3),
                AppCompatResources.getDrawable(this, R.drawable.img_main_instruction_4)
            )
            val instructionTextArray = arrayOf(
                getString(R.string.main_instruction_text_1),
                getString(R.string.main_instruction_text_2),
                getString(R.string.main_instruction_text_3),
                getString(R.string.main_instruction_text_4)
            )

            btnPreviousInstruction.setOnClickListener {
                if (instructionPage != 0) {
                    instructionPage--
                    imgImageInstruction.setImageDrawable(instructionImagesArray[instructionPage])
                    tvTextInstruction.text = instructionTextArray[instructionPage]
                    if (instructionPage == 0) {
                        btnPreviousInstruction.background =
                            AppCompatResources.getDrawable(this, R.drawable.btn_previous_grey)
                        btnNextInstruction.background =
                            AppCompatResources.getDrawable(this, R.drawable.btn_next)
                    } else {
                        btnPreviousInstruction.background =
                            AppCompatResources.getDrawable(this, R.drawable.btn_previous)
                        btnNextInstruction.background =
                            AppCompatResources.getDrawable(this, R.drawable.btn_next)
                    }
                }
            }
            btnNextInstruction.setOnClickListener {
                if (instructionPage != 3) {
                    instructionPage++
                    imgImageInstruction.setImageDrawable(instructionImagesArray[instructionPage])
                    tvTextInstruction.text = instructionTextArray[instructionPage]
                    if (instructionPage == 3) {
                        btnPreviousInstruction.background =
                            AppCompatResources.getDrawable(this, R.drawable.btn_previous)
                        btnNextInstruction.background =
                            AppCompatResources.getDrawable(this, R.drawable.btn_next_grey)
                    } else {
                        btnPreviousInstruction.background =
                            AppCompatResources.getDrawable(this, R.drawable.btn_previous)
                        btnNextInstruction.background =
                            AppCompatResources.getDrawable(this, R.drawable.btn_next)
                    }
                }
            }
            btnCloseInstructionWindow.setOnClickListener {
                dialogWindow.hide()
            }
        }

    }
}
