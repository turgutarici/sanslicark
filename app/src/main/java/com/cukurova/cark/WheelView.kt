package com.cukurova.cark

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.random.Random

class WheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var prizes: List<String> = emptyList()
    
    // Genişletilmiş ve canlı renk paleti
    private val colors = listOf(
        Color.parseColor("#6b38d4"), // Mor (Primary)
        Color.parseColor("#fd761a"), // Turuncu
        Color.parseColor("#008378"), // Turkuaz
        Color.parseColor("#E91E63"), // Pembe
        Color.parseColor("#3F51B5"), // İndigo
        Color.parseColor("#4CAF50"), // Yeşil
        Color.parseColor("#FFC107"), // Amber
        Color.parseColor("#2196F3"), // Mavi
        Color.parseColor("#9C27B0"), // Deep Purple
        Color.parseColor("#FF5722"), // Deep Orange
        Color.parseColor("#00BCD4"), // Cyan
        Color.parseColor("#8BC34A")  // Light Green
    )

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val rectF = RectF()
    private var rotationAngle = 0f
    private var lastHapticAngle = 0f

    fun setPrizes(newPrizes: List<String>) {
        prizes = newPrizes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (prizes.isEmpty()) return

        val size = minOf(width, height).toFloat()
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = size / 2f

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        val sweepAngle = 360f / prizes.size

        canvas.save()
        canvas.rotate(rotationAngle, centerX, centerY)

        for (i in prizes.indices) {
            // Renk seçimi: Eğer son dilim ile ilk dilim aynı renge denk gelirse bir sonrakini seç
            var colorIndex = i % colors.size
            if (i == prizes.size - 1 && prizes.size > 1 && colorIndex == 0) {
                colorIndex = (colorIndex + 1) % colors.size
            }
            
            arcPaint.color = colors[colorIndex]
            canvas.drawArc(rectF, i * sweepAngle, sweepAngle, true, arcPaint)

            // Metin çizimi
            canvas.save()
            val textRotation = i * sweepAngle + (sweepAngle / 2)
            canvas.rotate(textRotation, centerX, centerY)
            canvas.drawText(prizes[i], centerX + radius / 1.5f, centerY + 15f, textPaint)
            canvas.restore()
        }

        canvas.restore()

        // Orta Pim
        val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        canvas.drawCircle(centerX, centerY, 30f, pinPaint)
        pinPaint.color = Color.parseColor("#6b38d4")
        canvas.drawCircle(centerX, centerY, 15f, pinPaint)
    }

    fun spin(targetAngle: Float, onFinished: (Int) -> Unit) {
        val extraRotations = Random.nextInt(5, 11)
        val totalRotation = (360f * extraRotations) + targetAngle
        
        val startRotation = rotationAngle
        lastHapticAngle = 0f

        val animator = ValueAnimator.ofFloat(0f, totalRotation)
        animator.duration = Random.nextLong(3500, 5501)
        animator.interpolator = DecelerateInterpolator()
        
        val sweepAngle = 360f / prizes.size

        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            rotationAngle = (startRotation + animatedValue) % 360
            
            if (animatedValue - lastHapticAngle >= sweepAngle) {
                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                lastHapticAngle = animatedValue
            }
            
            invalidate()
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                var angleAtPointer = (270f - rotationAngle)
                while (angleAtPointer < 0) angleAtPointer += 360f
                
                val index = (angleAtPointer / sweepAngle).toInt() % prizes.size
                onFinished(index)
            }
        })
        
        animator.start()
    }
}