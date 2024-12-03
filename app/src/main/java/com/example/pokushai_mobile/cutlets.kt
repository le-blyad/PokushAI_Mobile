package com.example.pokushai_mobile

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageButton
import kotlin.math.roundToInt

class cutlets : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cutlets)

        //Ингредиенты
        val portions = 5

        val pointOne = 50f
        val pointTwo = 50f
        val pointThree = 0.2f
        val pointFour = 3.0f
        val pointFive = 0.2f
        val pointSix = 1.0f
        var portionsAdditionally = portions


        val textPortions = findViewById<TextView>(R.id.textPortions)
        textPortions.text = "$portionsAdditionally"

        val buttonDecrease = findViewById<Button>(R.id.buttonDecrease)
        buttonDecrease.setOnClickListener {

            if (portionsAdditionally > 1) {
                portionsAdditionally--
            } else {
                portionsAdditionally = 1
            }

            val pointValues = listOf(pointOne, pointTwo, pointThree, pointFour, pointFive, pointSix)
            val pointViews = listOf(R.id.pointOneText, R.id.pointTwoText, R.id.pointThreeText, R.id.pointFourText, R.id.pointFiveText, R.id.pointSixText)

            for (i in pointValues.indices) {
                val pointValueAdditionally = pointValues[i] * portionsAdditionally
                val pointTextView = findViewById<TextView>(pointViews[i])
                val pointValueAdditionallyRounded = pointValueAdditionally.roundToInt()
                pointTextView.text = "$pointValueAdditionallyRounded"
            }

            textPortions.text = "$portionsAdditionally"
        }

        val buttonIncrease = findViewById<Button>(R.id.buttonIncrease)
        buttonIncrease.setOnClickListener {

            portionsAdditionally++

            val pointValues = listOf(pointOne, pointTwo, pointThree, pointFour, pointFive, pointSix)
            val pointViews = listOf(R.id.pointOneText, R.id.pointTwoText, R.id.pointThreeText, R.id.pointFourText, R.id.pointFiveText, R.id.pointSixText)

            for (i in pointValues.indices) {

                val pointValueAdditionally = pointValues[i] * portionsAdditionally
                val pointTextView = findViewById<TextView>(pointViews[i])
                val pointValueAdditionallyRounded = pointValueAdditionally.roundToInt()
                pointTextView.text = "$pointValueAdditionallyRounded"
            }

            textPortions.text = "$portionsAdditionally"
        }

        val pointOneAdditionally = pointOne*portionsAdditionally
        val pointOneText = findViewById<TextView>(R.id.pointOneText)
        val pointOneAdditionallyRounded = pointOneAdditionally.roundToInt()
        pointOneText.text = "$pointOneAdditionallyRounded"

        val pointTwoAdditionally = pointTwo*portionsAdditionally
        val pointTwoText = findViewById<TextView>(R.id.pointTwoText)
        val pointTwoAdditionallyRounded = pointTwoAdditionally.roundToInt()
        pointTwoText.text = "$pointTwoAdditionallyRounded"

        val pointThreeAdditionally = pointThree*portionsAdditionally
        val pointThreeText = findViewById<TextView>(R.id.pointThreeText)
        val pointThreeAdditionallyRounded = pointThreeAdditionally.roundToInt()
        pointThreeText.text = "$pointThreeAdditionallyRounded"

        val pointFourAdditionally = pointFour*portionsAdditionally
        val pointFourText = findViewById<TextView>(R.id.pointFourText)
        val pointFourAdditionallyRounded = pointFourAdditionally.roundToInt()
        pointFourText.text = "$pointFourAdditionallyRounded"

        val pointFiveAdditionally = pointFive*portionsAdditionally
        val pointFiveText = findViewById<TextView>(R.id.pointFiveText)
        val pointFiveAdditionallyRounded = pointFiveAdditionally.roundToInt()
        pointFiveText.text = "$pointFiveAdditionallyRounded"

        val pointSixAdditionally = pointSix*portionsAdditionally
        val pointSixText = findViewById<TextView>(R.id.pointSixText)
        val pointSixAdditionallyRounded = pointSixAdditionally.roundToInt()
        pointSixText.text = "$pointSixAdditionallyRounded"



        //Кнопка назад

        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
    buttonBack.setOnClickListener {
        onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

        val layout: LinearLayout = findViewById(R.id.allSteps)
        val topMenu: LinearLayout = findViewById(R.id.topMenu)
        val layoutTimer: LinearLayout = findViewById(R.id.timerBackground)
        val timerCurrentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        // Проверяем текущую тему приложения
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Темная тема
            layout.setBackgroundResource(R.drawable.shape_dark)
            topMenu.setBackgroundResource(R.drawable.bottom_menu_dark)
            layoutTimer.setBackgroundResource(R.drawable.timerbackgrounddark)
        } else {
            // Светлая тема
            layout.setBackgroundResource(R.drawable.shape_light)
            topMenu.setBackgroundResource(R.drawable.bottom_menu_light)
            layoutTimer.setBackgroundResource(R.drawable.timerbackgroundlight)
        }



        // Энергитическая ценность
        val textViewCalories = findViewById<TextView>(R.id.textViewCalories)
        textViewCalories.text = "Калории\n305\nККАЛ"

        val textViewSquirrels = findViewById<TextView>(R.id.textViewSquirrels)
        textViewSquirrels.text = "Белки\n25\nграмм"

        val textViewFats = findViewById<TextView>(R.id.textViewFats)
        textViewFats.text = "Жиры\n20\nграмм"

        val textViewCarbohydrates = findViewById<TextView>(R.id.textViewCarbohydrates)
        textViewCarbohydrates.text = "Углеводы\n3\nграмм"

        //Таймер

        val timerTextView = findViewById<TextView>(R.id.timerTextView)
        val buttonTimerStart = findViewById<Button>(R.id.buttonTimerStart)
        lateinit var timer: CountDownTimer

        var isTimerRunning = false
        var timeLeftInMillis: Long = 600000

        fun updateTimer() {
            val seconds = (timeLeftInMillis / 1000) % 60
            val minutes = (timeLeftInMillis / (1000 * 60)) % 60
            val hours = (timeLeftInMillis / (1000 * 60 * 60)) % 24
            val timeLeft = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            timerTextView.text = timeLeft
        }

        fun startTimer() {
            timer = object : CountDownTimer(timeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    updateTimer()
                }

                override fun onFinish() {
                    timerTextView.text = "00:00:00"
                    timeLeftInMillis = 600000
                }
            }
            timer.start()
            isTimerRunning = true
        }

        fun pauseTimer() {
            timer.cancel()
            isTimerRunning = false
        }

        buttonTimerStart.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        val buttonTimerRestart = findViewById<Button>(R.id.buttonTimerRestart)
        buttonTimerRestart.setOnClickListener {
            if (isTimerRunning) {
                timer.cancel()  // Остановить текущий таймер, если он работает
            }
            timeLeftInMillis = 600000  // Сбросить время до начального значения
            updateTimer()  // Обновить отображение времени на экране
            isTimerRunning = false  // Установить флаг запущенности таймера в false
        }
    }
}