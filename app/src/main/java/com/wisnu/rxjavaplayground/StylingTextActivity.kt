package com.wisnu.rxjavaplayground

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_styling_text.*
import java.util.regex.Pattern

/**
 * Created by wisnu on 11/26/17.
 */
class StylingTextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_styling_text)

        styled_field.textChanges()
            .map { it.toString() }
            .doOnNext { Log.d("TAG - ", "$it") }
            .filter { it.contains("*") }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                applyStyledText(styled_field.text.toString(), styled_field.text)
            }

    }

    private fun applyStyledText(fullText: String,
                                messageEditorSpannable: Spannable) {
        removeAllSpan(fullText, messageEditorSpannable)
        applyBoldSpan(fullText, messageEditorSpannable)
    }

    private fun removeAllSpan(fullText: String,
                              messageEditorSpannable: Spannable) {
        val colorSpans = messageEditorSpannable.getSpans(0, fullText.length, ForegroundColorSpan::class.java)
        val styledSpans = messageEditorSpannable.getSpans(0, fullText.length, StyleSpan::class.java)
        colorSpans.forEach { messageEditorSpannable.removeSpan(it) }
        styledSpans.forEach { messageEditorSpannable.removeSpan(it) }
    }

    private fun applyBoldSpan(fullText: String,
                              messageEditorSpannable: Spannable) {
        val markdownStarPattern = "\\*(?=[^\\s*])(.*?)([^\\s*])\\*"
        val pattern = Pattern.compile(markdownStarPattern)
        val matcher = pattern.matcher(fullText)

        val positionStartMatch = ArrayList<Int>()
        val positionEndMatch = ArrayList<Int>()
        while (matcher.find()) {
            positionStartMatch.add(matcher.start())
            positionEndMatch.add(matcher.end())
        }

        for (i in positionEndMatch.indices) {
            messageEditorSpannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), positionStartMatch[i], positionStartMatch[i] + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            messageEditorSpannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), positionEndMatch[i] - 1, positionEndMatch[i], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            messageEditorSpannable.setSpan(StyleSpan(Typeface.BOLD), positionStartMatch[i] + 1, positionEndMatch[i] - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}