package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {
    // Moved the following variables from the GameFragment.kt file
    var score = 0

    private var _currentWordTryCount = 0
    val currentWordTryCount: Int
        get() = _currentWordTryCount

    private var _incorrectWordCount = 0
    val incorrectWordCount: Int
        get() = _incorrectWordCount

    private var _currentWordCount = 0
    val currentWordCount: Int
        get() = _currentWordCount
    private lateinit var _currentScrambledWord: String
    val currentScrambledWord: String
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
            _currentScrambledWord = String(tempWord)
            ++_currentWordCount
            _currentWordTryCount = 0
            wordsList.add(currentWord)
        }
    }

    /*
     * Updates currentWord and _currentScrambledWord with the new scrambled word.
     */
    fun rescrambleWord() {
        val newScrambledWord = currentScrambledWord.toCharArray()
        newScrambledWord.shuffle()
        while (newScrambledWord.toString().equals(currentWord, false)) {
            newScrambledWord.shuffle()
        }
        _currentScrambledWord = String(newScrambledWord)
    }

    /*
     * Progressively updates each letter of _currentScrambled word to the corresponding letter in the currentWord.
     * From first to last letter, whilst keeping the latter letters.
     * For hinting purposes.
     */
    fun hintWord() {
        val scrambledWord = currentScrambledWord.toCharArray()
        for (i in currentWord.indices) {
            // what does currentWord.indices look like? -> 0, 1, 2, 3, 4, 5
            if (scrambledWord[i] != currentWord[i]) {
                // what does currentWord[i] look like? -> "a", "n", "i", "m", "a", "l"
                scrambledWord[i] = currentWord[i]
                //nonHintScrambledCharacters = scrambleWord.substring(i + 1)
                break
            }
        }
        _currentScrambledWord = String(scrambledWord)
    }

    /*
     * Returns true if the current word count is less than MAX_NO_OF_WORDS.
     * Updates the next word.
     * Helper Methode
     */
    fun nextWord(): Boolean {
        return if (_currentWordCount < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    /*
     * Returns true if the incorrectWordCount is less than MAX_NO_OF_INCORRECT_WORDS.
     */
    fun nextTry(): Boolean {
        ++_currentWordTryCount
        return _incorrectWordCount < MAX_NO_OF_INCORRECT_WORDS
    }

    /*
     * Increases the score by SCORE_INCREASE.
     */
    private fun increaseScore() {
        // if score = 20
        if (_currentWordTryCount == FIRST_TRY) {
            score += BONUS
        }
        score += SCORE_INCREASE
        // score = 20 + 2 + 20 = 42
    }

    /*
     * Decreases the score by SCORE_DECREASE.
     */
    private fun decreaseScore() {
        score -= SCORE_DECREASE
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
        } else
            decreaseScore()
            ++_incorrectWordCount
        return false
    }

    /*
     * Re-initializes the game data to restart the game.
     */
    fun reinitializeData() {
        _currentWordCount = 0
        score = 0
        _incorrectWordCount = 0
        _currentWordTryCount = 0
        wordsList.clear()
        getNextWord()
    }

    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }


    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }

}