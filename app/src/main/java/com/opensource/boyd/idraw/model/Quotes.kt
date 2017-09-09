package com.opensource.boyd.idraw.model

import android.content.Context
import com.opensource.boyd.idraw.R
import java.util.*

/**
 * Created by Boyd on 9/8/2017.
 */
class Quotes(context: Context) {

    private var quotes : List<String> = listOf(
            context.getString(R.string.quote_1),
            context.getString(R.string.quote_2),
            context.getString(R.string.quote_3),
            context.getString(R.string.quote_4),
            context.getString(R.string.quote_5)
    )

    fun getQuote() : String {
        var random = Random()
        return quotes[random.nextInt(0..5)]
    }

    fun Random.nextInt(range: IntRange) : Int {
        return range.start + nextInt(range.last - range.start)
    }
}