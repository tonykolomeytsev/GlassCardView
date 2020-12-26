package kekmech.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kekmech.glasscardview.GlassCardView
import kekmech.glasscardview.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        animate1()
    }

    private fun animate1() {
        findViewById<GlassCardView>(R.id.gcv).animate()
            .setStartDelay(500L)
            .translationY(-200f)
            .setDuration(1500L)
            .withEndAction { animate2() }
            .start()

    }
    private fun animate2() {
        findViewById<GlassCardView>(R.id.gcv).animate()
            .setStartDelay(500L)
            .translationY(+200f)
            .setDuration(1500L)
            .withEndAction { animate1() }
            .start()
    }
}