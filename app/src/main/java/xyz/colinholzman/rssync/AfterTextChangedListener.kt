package xyz.colinholzman.rssync

import android.text.Editable
import android.text.TextWatcher

class AfterTextChangedListener(val listener: (String)->Unit) : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {
        listener.invoke(p0!!.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //nothing
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //nothing
    }
}