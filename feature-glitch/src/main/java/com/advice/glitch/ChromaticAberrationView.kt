
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.advice.glitch.R

@Composable
fun ChromaticAberrationComposeView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var offset by remember { mutableStateOf(0f) }

    Column(
        modifier
//            .padding(32.dp)
//            .background(Color.Yellow)
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    ) {
        AndroidView(
            modifier = Modifier
//                .background(Color.Black)
                .fillMaxSize(),
            factory = { ctx ->
                // Initialization of your custom Android View goes here
                ChromaticAberrationView(ctx).apply {
                    // Perform any configuration you need on the view instance
                    setOffset(offset)
                }
            },
            update = { view ->
                // Whenever the state changes, update the view
                view.setOffset(offset)
            }
        )

        Slider(
            value = offset,
            onValueChange = { offset = it },
            valueRange = 0f..100f,
            steps = 100
        )
    }
}

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
            1f, 0f, 0f, 0f, 0f,  // red
            0f, 0f, 0f, 0f, 0f,  // green
            0f, 0f, 0f, 0f, 0f,  // blue
            0f, 0f, 0f, 1f, 0f   // alpha
        )
    )
    private val greenColorMatrix = ColorMatrix(
        floatArrayOf(
            0f, 0f, 0f, 0f, 0f,  // red
            0f, 1f, 0f, 0f, 0f,  // green
            0f, 0f, 0f, 0f, 0f,  // blue
            0f, 0f, 0f, 1f, 0f   // alpha
        )
    )
    private val blueColorMatrix = ColorMatrix(
        floatArrayOf(
            0f, 0f, 0f, 0f, 0f,  // red
            0f, 0f, 0f, 0f, 0f,  // green
            0f, 0f, 1f, 0f, 0f,  // blue
            0f, 0f, 0f, 1f, 0f   // alpha
        )
    )

    private val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo_clean)

    private var redBitmap: Bitmap? = null
    private var greenBitmap: Bitmap? = null
    private var blueBitmap: Bitmap? = null

    private fun updateFilteredBitmaps() {
        // Create new bitmaps only when necessary
        if (redBitmap == null || redBitmap!!.width != width || redBitmap!!.height != height) {
            // Recycle old bitmaps
            redBitmap?.recycle()
            greenBitmap?.recycle()
            blueBitmap?.recycle()

            // Create new bitmaps filtered with color matrices
            redBitmap = createFilteredBitmap(redColorMatrix)
            greenBitmap = createFilteredBitmap(greenColorMatrix)
            blueBitmap = createFilteredBitmap(blueColorMatrix)
        }
    }

    private fun createFilteredBitmap(colorMatrix: ColorMatrix): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
        canvas.drawBitmap(this.bitmap, null, Rect(0, 0, width, height), paint)
        return bitmap
    }

    fun setOffset(offset: Float) {
        this.offset = offset
        invalidate()  // Redraw the view with the new offset
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Enable blending mode
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)

        updateFilteredBitmaps()

        // Draw red component with offset
        paint.colorFilter = ColorMatrixColorFilter(redColorMatrix)
        canvas.drawBitmap(redBitmap!!, offset, 0f, paint)
        // Draw green component with no offset
        paint.colorFilter = ColorMatrixColorFilter(greenColorMatrix)
        canvas.drawBitmap(greenBitmap!!, 0f, 0f, paint)
        // Draw blue component with negative offset
        paint.colorFilter = ColorMatrixColorFilter(blueColorMatrix)
        canvas.drawBitmap(blueBitmap!!, -offset, 0f, paint)

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
