package com.example.calculator

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import com.example.calculater.R

class MainActivity : AppCompatActivity() {
    private var firstNumber = ""
    private var currentNumber = ""
    private var currentOperator = ""
    private var result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configura a janela para exibir layout sem limites
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        val tvResult = findViewById<TextView>(R.id.tvResult)
        val tvFormula = findViewById<TextView>(R.id.tvFormula)
        val mainLayout = findViewById<GridLayout>(R.id.layoutMain)

        // Aplicar insets de tela ao layout principal
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar listeners para todos os botões no layout
        mainLayout.children.filterIsInstance<Button>().forEach { button ->
            button.setOnClickListener {
                val buttonText = button.text.toString()
                when {
                    buttonText.matches(Regex("[0-9]")) -> handleNumberInput(buttonText, tvResult)
                    buttonText.matches(Regex("[+\\-*/]")) -> handleOperatorInput(buttonText, tvResult)
                    buttonText == "=" -> handleEqualInput(tvResult, tvFormula)
                    buttonText == "." -> handleDecimalInput(tvResult)
                    buttonText.equals("clear", ignoreCase = true) -> handleClearInput(tvResult, tvFormula)
                }
            }
        }
    }

    private fun handleNumberInput(buttonText: String, tvResult: TextView) {
        if (currentOperator.isEmpty()) {
            firstNumber += buttonText
            tvResult.text = firstNumber
        } else {
            currentNumber += buttonText
            tvResult.text = currentNumber
        }
    }

    private fun handleOperatorInput(buttonText: String, tvResult: TextView) {
        if (firstNumber.isNotEmpty() && currentNumber.isEmpty()) {
            currentOperator = buttonText
            tvResult.text = "0"
        }
    }

    private fun handleEqualInput(tvResult: TextView, tvFormula: TextView) {
        if (firstNumber.isNotEmpty() && currentNumber.isNotEmpty() && currentOperator.isNotEmpty()) {
            tvFormula.text = "$firstNumber $currentOperator $currentNumber"
            result = evaluateExpression(firstNumber, currentNumber, currentOperator)
            firstNumber = result
            currentNumber = ""
            currentOperator = ""
            tvResult.text = result
        }
    }

    private fun handleDecimalInput(tvResult: TextView) {
        if (currentOperator.isEmpty()) {
            if (!firstNumber.contains(".")) {
                firstNumber += if (firstNumber.isEmpty()) "0." else "."
                tvResult.text = firstNumber
            }
        } else {
            if (!currentNumber.contains(".")) {
                currentNumber += if (currentNumber.isEmpty()) "0." else "."
                tvResult.text = currentNumber
            }
        }
    }

    private fun handleClearInput(tvResult: TextView, tvFormula: TextView) {
        firstNumber = ""
        currentNumber = ""
        currentOperator = ""
        result = ""
        tvResult.text = "0"
        tvFormula.text = ""
    }

    private fun evaluateExpression(firstNumber: String, secondNumber: String, operator: String): String {
        val num1 = firstNumber.toDoubleOrNull() ?: return ""
        val num2 = secondNumber.toDoubleOrNull() ?: return ""
        return when (operator) {
            "+" -> (num1 + num2).toString()
            "-" -> (num1 - num2).toString()
            "*" -> (num1 * num2).toString()
            "/" -> if (num2 != 0.0) (num1 / num2).toString() else "Error"
            else -> ""
        }
    }
}
