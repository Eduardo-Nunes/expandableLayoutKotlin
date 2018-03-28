package com.nunes.eduardo.expandablelayout

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text.text = getString(R.string.lorem_ipsilum)

        moreSynopsisButtonView.setOnClickListener {
            moreSynopsisButtonView.visibility = INVISIBLE
            expandableLayout.toggle {isExpanded ->
                if (isExpanded){
                    moreSynopsisButtonView.text = "menos"
                }else{
                    moreSynopsisButtonView.text = "mais"
                }
                moreSynopsisButtonView.visibility = VISIBLE

            }
        }

    }
}
