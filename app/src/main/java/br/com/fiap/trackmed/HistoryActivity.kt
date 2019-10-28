package br.com.fiap.trackmed

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.activity_history.*
import android.text.format.DateFormat
import android.util.TypedValue
import info.androidhive.fontawesome.FontCache
import info.androidhive.fontawesome.FontTextView
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private val agentId: String = "nome@email.com"
    private var historyList: Cursor = DetectionActivity.db!!.queryTrack(agentId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_history)

        while (historyList.moveToNext()) {

            val linearLayout1 = LinearLayout(this)
            val linearLayout2 = LinearLayout(this)
            linearLayout1.orientation = LinearLayout.VERTICAL
            linearLayout2.orientation = LinearLayout.HORIZONTAL

            val textView1 = TextView(this)
            textView1.text = "Id: " + historyList.getString(historyList.getColumnIndex("BeaconId")) + " - " +
                    DateFormat.getDateFormat(this).format(
                        Date(historyList.getString(historyList.getColumnIndex("DetectionTime")).toLong()*1000L)) + " " +
                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(
                        historyList.getString(historyList.getColumnIndex("DetectionTime")).toLong()*1000L)

            val textView2 = TextView(this)
            textView2.text = BeaconUtils.getBeaconTypeDescription(this, historyList.getString(historyList.getColumnIndex("BeaconType")))
            textView2.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)
            val textLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // CardView width
                LinearLayout.LayoutParams.WRAP_CONTENT // CardView height
            )
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)
            textLayoutParams.setMargins(18, 0, 0, 0)
            textView2.layoutParams = textLayoutParams

            val textView3 = FontTextView(this)
            textView3.typeface = FontCache.get(this, FontCache.FA_FONT_SOLID)
            textView3.text = getString(BeaconUtils.getBeaconTypeImage(historyList.getString(historyList.getColumnIndex("BeaconType"))))
            textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)

            val cardView = CardView(this)

            cardView.radius = 10F
            cardView.setContentPadding(8, 8, 8, 8)
            cardView.setCardBackgroundColor(Color.WHITE)
            cardView.cardElevation = 30F
            cardView.maxCardElevation = 30F
            cardView.setContentPadding(15, 10, 15, 10)

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // CardView width
                LinearLayout.LayoutParams.WRAP_CONTENT // CardView height
            )
            layoutParams.setMargins(18, 18, 18, 18)
            cardView.layoutParams = layoutParams
            cardView.id = View.generateViewId()


            linearLayout2.addView(textView3)
            linearLayout2.addView(textView2)

            linearLayout1.addView(textView1)
            linearLayout1.addView(linearLayout2)

            cardView.addView(linearLayout1)
            listHistoryDetected.addView(cardView)

            val constraintSet = ConstraintSet()
            constraintSet.clone(listHistoryDetected)

            if (historyList.position == 0) {
                constraintSet.connect(
                    cardView.id,
                    ConstraintSet.TOP,
                    scrollView.id,
                    ConstraintSet.TOP,
                    18
                )
            } else {
                constraintSet.connect(
                    cardView.id,
                    ConstraintSet.TOP,
                    listHistoryDetected.getChildAt(historyList.position - 1).id,
                    ConstraintSet.BOTTOM,
                    38
                )
            }
            constraintSet.connect(
                cardView.id,
                ConstraintSet.LEFT,
                listHistoryDetected.id,
                ConstraintSet.RIGHT,
                20
            )
            constraintSet.connect(
                cardView.id,
                ConstraintSet.RIGHT,
                listHistoryDetected.id,
                ConstraintSet.LEFT,
                20
            )

            constraintSet.applyTo(listHistoryDetected)
        }
        historyList.close()
    }
}