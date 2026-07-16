package com.advice.glitch
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.createBitmap

class ChromaticAberrationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private var offset = 0f
    private val paint = Paint()

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private val redColorMatrix = ColorMatrix(
        floatArrayOf(
            1f, 0f, 0f, 0f, 0f, // red
            0f, 0f, 0f, 0f, 0f, // green
            0f, 0f, 0f, 0f, 0f, // blue
            0f, 0f, 0f, 1f, 0f // alpha
        )
    )
    private val greenColorMatrix = ColorMatrix(
        floatArrayOf(
            0f, 0f, 0f, 0f, 0f, // red
            0f, 1f, 0f, 0f, 0f, // green
            0f, 0f, 0f, 0f, 0f, // blue
            0f, 0f, 0f, 1f, 0f // alpha
        )
    )
    private val blueColorMatrix = ColorMatrix(
        floatArrayOf(
            0f, 0f, 0f, 0f, 0f, // red
            0f, 0f, 0f, 0f, 0f, // green
            0f, 0f, 1f, 0f, 0f, // blue
            0f, 0f, 0f, 1f, 0f // alpha
        )
    )

    private val redColorFilter = ColorMatrixColorFilter(redColorMatrix)
    private val greenColorFilter = ColorMatrixColorFilter(greenColorMatrix)
    private val blueColorFilter = ColorMatrixColorFilter(blueColorMatrix)

    private val addXfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
    private val drawRect = Rect()
    private val filterPaint = Paint()

    private val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo_clean)

    private var redBitmap: Bitmap? = null
    private var greenBitmap: Bitmap? = null
    private var blueBitmap: Bitmap? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            drawRect.set(0, 0, w, h)
            updateFilteredBitmaps(w, h)
        }
    }

    private fun updateFilteredBitmaps(w: Int, h: Int) {
        // Recycle old bitmaps
        redBitmap?.recycle()
        greenBitmap?.recycle()
        blueBitmap?.recycle()

        // Create new bitmaps filtered with color matrices
        redBitmap = createFilteredBitmap(w, h, redColorFilter)
        greenBitmap = createFilteredBitmap(w, h, greenColorFilter)
        blueBitmap = createFilteredBitmap(w, h, blueColorFilter)
    }

    private fun createFilteredBitmap(w: Int, h: Int, colorFilter: ColorFilter): Bitmap {
        val bitmap = createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        filterPaint.colorFilter = colorFilter
        canvas.drawBitmap(this.bitmap, null, drawRect, filterPaint)
        return bitmap
    }

    fun setOffset(offset: Float) {
        this.offset = offset
        invalidate() // Redraw the view with the new offset
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val red = redBitmap
        val green = greenBitmap
        val blue = blueBitmap

        if (red == null || green == null || blue == null) {
            return
        }

        // Enable blending mode
        paint.xfermode = addXfermode

        // Draw red component with offset
        paint.colorFilter = redColorFilter
        canvas.drawBitmap(red, offset, 0f, paint)
        // Draw green component with no offset
        paint.colorFilter = greenColorFilter
        canvas.drawBitmap(green, 0f, 0f, paint)
        // Draw blue component with negative offset
        paint.colorFilter = blueColorFilter
        canvas.drawBitmap(blue, -offset, 0f, paint)

        // Reset xfermode
        paint.xfermode = null

//        // Add scan lines
//        val scanLineSpacing = 10  // Adjust this value to change the spacing between scan lines
//        paint.color = android.graphics.Color.WHITE
//        for (i in 0 until height step scanLineSpacing) {
//            canvas.drawLine(0f, i.toFloat(), width.toFloat(), i.toFloat(), paint)
//        }

//        // Add periodic glitch effect
//        val glitchPeriod = 20  // Adjust this value to change the period of the glitch effect
//        if (System.currentTimeMillis() / 1000 % glitchPeriod < glitchPeriod / 2) {
//            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
//        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        redBitmap?.recycle()
        greenBitmap?.recycle()
        blueBitmap?.recycle()

        redBitmap = null
        greenBitmap = null
        blueBitmap = null
    }
}
