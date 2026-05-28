package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: NoteRepository) : ViewModel() {

    val notes: StateFlow<List<Note>> = repository.allNotes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // Calculator State
    private val _calcText = MutableStateFlow("")
    val calcText: StateFlow<String> = _calcText.asStateFlow()
    
    fun onCalcKeyPress(key: String) {
        when (key) {
            "C" -> _calcText.value = ""
            "DEL" -> _calcText.value = _calcText.value.dropLast(1)
            "=" -> evaluateCalc()
            else -> _calcText.value += key
        }
    }
    
    private fun evaluateCalc() {
        try {
            val result = evaluateMathExpression(_calcText.value)
            if (result == result.toLong().toDouble()) {
                _calcText.value = result.toLong().toString()
            } else {
                _calcText.value = result.toString()
            }
        } catch (e: Exception) {
             _calcText.value = "Erreur"
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            repository.insert(Note(title = title, content = content))
        }
    }
    
    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
}

// Simple evaluator for the calculator
class MathEvaluator(private val str: String) {
    private var pos = -1
    private var ch = 0

    fun evaluate(): Double {
        nextChar()
        val x = parseExpression()
        if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
        return x
    }

    private fun nextChar() {
        ch = if (++pos < str.length) str[pos].code else -1
    }

    private fun eat(charToEat: Int): Boolean {
        while (ch == ' '.code) nextChar()
        if (ch == charToEat) {
            nextChar()
            return true
        }
        return false
    }

    private fun parseFactor(): Double {
        if (eat('+'.code)) return parseFactor()
        if (eat('-'.code)) return -parseFactor()
        var x: Double
        val startPos = pos
        if (eat('('.code)) {
            x = parseExpression()
            eat(')'.code)
        } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
            while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
            x = str.substring(startPos, pos).toDouble()
        } else {
            throw RuntimeException("Unexpected: " + ch.toChar())
        }
        return x
    }

    private fun parseTerm(): Double {
        var x = parseFactor()
        while (true) {
            if (eat('*'.code)) x *= parseFactor()
            else if (eat('/'.code)) x /= parseFactor()
            else return x
        }
    }

    private fun parseExpression(): Double {
        var x = parseTerm()
        while (true) {
            if (eat('+'.code)) x += parseTerm()
            else if (eat('-'.code)) x -= parseTerm()
            else return x
        }
    }
}

fun evaluateMathExpression(str: String): Double {
    return MathEvaluator(str.replace("×", "*").replace("÷", "/")).evaluate()
}
