package es.uma.quiziosity.utils

import android.content.Context
import android.graphics.*
import kotlin.random.Random

object TextDrawable {
    fun getAvatarBitmap(context: Context, text: String, sizeDp: Int): Bitmap {
        val sizePx = dpToPx(context, sizeDp)
        val backgroundColor = getRandomColor()
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = backgroundColor
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), paint)
        paint.color = Color.WHITE
        paint.textSize = sizePx / 2f
        paint.textAlign = Paint.Align.CENTER
        val xPos = canvas.width / 2
        val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)
        canvas.drawText(text, xPos.toFloat(), yPos, paint)

        // Create a circular bitmap
        val output = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvasOutput = Canvas(output)
        val paintOutput = Paint()
        paintOutput.isAntiAlias = true
        canvasOutput.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, paintOutput)
        paintOutput.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvasOutput.drawBitmap(bitmap, 0f, 0f, paintOutput)

        return output
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun getRandomColor(): Int {
        val random = Random.Default
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        // Ensure the color is not white, so the text is visible
        return if (red > 200 && green > 200 && blue > 200) {
            getRandomColor()
        } else {
            Color.rgb(red, green, blue)
        }
    }
}