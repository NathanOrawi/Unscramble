package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordTryCount = MutableLiveData(0)
    val currentWordTryCount: LiveData<Int>
        get() = _currentWordTryCount

    private val _incorrectWordCount = MutableLiveData(0)
    val incorrectWordCount: LiveData<Int>
        get() = _incorrectWordCount

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount
    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    // List of words used in the game
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    /*
     * Updates currentWord and _currentScrambledWord with the next word.
     */
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()
        while (tempWord.toString().equals(currentWord, false)) {
            tempWord.shuffle()
        }
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            _currentWordTryCount.value = 0
            wordsList.add(currentWord)
        }
    }

    /*
     * Updates currentWord and _currentScrambledWord with the new scrambled word.
     * Re-scrambles the current word.
     */
    fun reScrambleWord() {
        _currentScrambledWord.value?.toCharArray()?.let { newScrambledWord ->
            newScrambledWord.shuffle()
            while (newScrambledWord.toString().equals(currentWord, false)) {
                newScrambledWord.shuffle()
            }
            _currentScrambledWord.value = String(newScrambledWord)
        }
    }

    /*
     * Progressively updates each letter of _currentScrambled word to the corresponding letter in the currentWord.
     * From first to last letter, whilst keeping the latter letters.
     * For hinting purposes.
     */
    fun hintWord() {
        currentScrambledWord.value?.toCharArray()?.let { hintWord ->
            for (i in hintWord.indices) {
                if (hintWord[i] != currentWord[i]) {
                    hintWord[i] = currentWord[i]
                    break
                }
            }
            _currentScrambledWord.value = String(hintWord)
        }
    }

    /*
     * Returns true if the current word count is less than MAX_NO_OF_WORDS.
     * Updates the next word.
     * Helper Methode
     */
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    /*
     * Returns true if the incorrectWordCount is less than MAX_NO_OF_INCORRECT_WORDS.
     */
    fun nextTry(): Boolean {
        _currentWordTryCount.value = _currentWordTryCount.value?.inc()
        return _incorrectWordCount.value!! < MAX_NO_OF_INCORRECT_WORDS
    }

    /*
     * Increases the score by SCORE_INCREASE.
     */
    private fun increaseScore() {
        // if score = 20
        if (_currentWordTryCount.value!! == FIRST_TRY) {
            _score.value = _score.value?.plus(BONUS)
        }
        _score.value = _score.value?.plus(SCORE_INCREASE)
        // score = 20 + 2 + 20 = 42
    }

    /*
     * Decreases the score by SCORE_DECREASE.
     */
    private fun decreaseScore() {
        _score.value = _score.value?.minus(SCORE_DECREASE)
    }

    /*
     * Checks if the player's word is correct.
     * If the player's word is correct, the score is increased.
     * Returns true if the player's word is correct.
     * else increments the incorrectWordCount if the player's word is incorrect.
     * Returns false if the player's word is incorrect.
     */
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        } else decreaseScore()
        _incorrectWordCount.value = _incorrectWordCount.value?.dec()
        return false
    }

    /*
     * Re-initializes the game data to restart the game.
     */
    fun reinitializeData() {
        _currentWordCount.value = 0
        _score.value = 0
        _incorrectWordCount.value = 0
        _currentWordTryCount.value = 0
        wordsList.clear()
        getNextWord()
    }

    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }
}