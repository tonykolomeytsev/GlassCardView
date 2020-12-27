package kekmech.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kekmech.glasscardview.GlassCardView
import kekmech.glasscardview.R
import kekmech.glasscardview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val container = findViewById<View>(R.id.container)
        val glass: GlassCardView = findViewById(R.id.glass)
        val menu: GlassCardView = findViewById(R.id.menu)
        glass.framesSourceView = container
        menu.framesSourceView = container
        findViewById<SeekBar>(R.id.seek1).setOnSeekBarChangeListener {
            glass.blurRadius = it.coerceIn(8, 25*8)
        }
        findViewById<SeekBar>(R.id.seek2).setOnSeekBarChangeListener {
            glass.opacity = it / 100f
        }
        findViewById<SeekBar>(R.id.seek3).setOnSeekBarChangeListener {
            glass.elevation = (it / 4f)
        }
        findViewById<SeekBar>(R.id.seek4).setOnSeekBarChangeListener {
            glass.cornerRadius = (it / 2f)
        }
        findViewById<Button>(R.id.button1).setOnClickListener {
            if (glass.framesSourceView == container) {
                glass.framesSourceView = glass.parent as View
            } else {
                glass.framesSourceView = container
            }
            glass.invalidate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun SeekBar.setOnSeekBarChangeListener(listener: (Int) -> Unit) {
        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                listener(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }
}