package com.nunes.eduardo.expandablelayout

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.transitionseverywhere.*
import com.transitionseverywhere.Transition.TransitionListener
import java.lang.Integer.MAX_VALUE

internal const val DEFAULT_MIN_HEIGHT: Int = 0
internal const val DEFAULT_TARGET_HEIGHT: Int = 0
internal const val DEFAULT_MAX_COLLAPSE_LINES: Int = 3

class ExpandableLayout : ConstraintLayout {
    private var targetHeight: Int = DEFAULT_TARGET_HEIGHT
    private var collapseMinHeight: Int = DEFAULT_MIN_HEIGHT
    private var isExpanded: Boolean = true
    private lateinit var childTextView: TextView

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        attrs?.let { attributes ->
            initAttrs(context, attributes)
        }
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.expandable_layout)
        isExpanded = attr.getBoolean(R.styleable.expandable_layout_expand_isExpanded, true)
        collapseMinHeight = attr.getDimensionPixelSize(R.styleable.expandable_layout_collapse_min_height, DEFAULT_MIN_HEIGHT)
        attr.recycle()
    }

    override fun onViewAdded(view: View?) {
        if (view is TextView) {
            childTextView = view
            view.post {
                targetHeight = view.measuredHeight
                setCollapseAttr()
                isExpanded = false
            }
        }

        super.onViewAdded(view)
    }

    private fun setCollapseAttr() {
        childTextView.ellipsize = TextUtils.TruncateAt.END
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            collapseMinHeight = childTextView.lineHeight * childTextView.minLines + childTextView.lineHeight/4
            childTextView.maxLines = childTextView.minLines
        } else {
            collapseMinHeight = childTextView.lineHeight * DEFAULT_MAX_COLLAPSE_LINES + childTextView.lineHeight/4
            childTextView.maxLines = DEFAULT_MAX_COLLAPSE_LINES
        }
    }

    fun toggle(callback: ((Boolean) -> Unit)? = null) {
        if (isExpanded) {
            collapse(callback)
        } else {
            expand(callback)
        }
    }

    private fun expand(callback: ((Boolean) -> Unit)? = null) {
        childTextView.maxLines = MAX_VALUE
        childTextView.ellipsize = null

        val anim = ChangeBounds()

        anim.addListener(onEndAnimationListener({
            callback?.invoke(isExpanded)
        }))

        TransitionManager.beginDelayedTransition(this, anim)

        layoutParams.height = targetHeight

        requestLayout()

        isExpanded = true
    }

    private fun collapse(callback: ((Boolean) -> Unit)? = null) {
        val anim = ChangeBounds()
        anim.addListener(onEndAnimationListener({
            setCollapseAttr()
            callback?.invoke(isExpanded)
        }))

        TransitionManager.beginDelayedTransition(this, anim)

        layoutParams.height = collapseMinHeight

        requestLayout()

        isExpanded = false
    }

    private inline fun onEndAnimationListener(
            crossinline onEnd: (transition: Transition?) -> Unit
    ) = object : TransitionListener {
        override fun onTransitionResume(transition: Transition?) {}

        override fun onTransitionPause(transition: Transition?) {}

        override fun onTransitionCancel(transition: Transition?) {}

        override fun onTransitionStart(transition: Transition?) {}

        override fun onTransitionEnd(transition: Transition?) = onEnd(transition)
    }
}

