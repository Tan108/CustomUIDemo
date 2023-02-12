package com.tan.scanviewlayout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class ScanBarcodeLayoutView : View {
    private var mCenterRectangleColor = 0
    private var mLeftTopAngleColor = 0
    private var mRightTopAngleColor = 0
    private var mBottomLeftAngleColor = 0
    private var mBottomRightAngleColor = 0
    private var mAngleStokeWidth = 0f
    private var mLeftTopAngleHeightAtYSide = 0f
    private var mLeftTopAngleHeightAtXSide = 0f
    private var mRightTopAngleHeightAtXSide = 0f
    private var mRightTopAngleHeightAtYSide = 0f
    private var mRightBottomAngleHeightAtXSide = 0f
    private var mRightBottomAngleHeightAtYSide = 0f
    private var mLeftBottomAngleHeightAtXSide = 0f
    private var mLeftBottomAngleHeightAtYSide = 0f
    private var mSpaceBetweenCenterRectangleAndCornerAngleHorizontally = 0f
    private var mSpaceBetweenCenterRectangleAndCornerAngleVertically = 0f
    private var mPaint: Paint? = null
    private val padding = 0f
    private val size = 0f
    private val width = 0f
    private val height = 0f
    private val halfScreenSize = 0f
    private val rectLeft = 0f
    private val rectTop = 0f
    private val rectRight = 0f
    private val rectBottom = 0f
    private var mWidth = 0f
    private val path: Path? = null
    private val length = 0f
    private var mMovingLineProgress = 0f
    private var mLineMovingDown = true
    private val mAnimated = true
    private val mUpAndDownSeconds = 3.5f
    private var mMovingLinePaint: Paint? = null
    private val mMovingLineWidth = 5f
    private val mMovingLineColor = Color.parseColor("#FFB01D")
    private var pos = 0
    private var lastTick: Long = 0

    constructor(context: Context?) : super(context) {
        init(null)
        startMovingLineAnimation()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
        startMovingLineAnimation()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
        startMovingLineAnimation()
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScanBarcodeLayoutView)
        mCenterRectangleColor = typedArray.getColor(
            R.styleable.ScanBarcodeLayoutView_centerRectangleColor,
            Color.parseColor("#F5F5F5")
        )
        mAngleStokeWidth = typedArray.getFloat(R.styleable.ScanBarcodeLayoutView_AngleWidth, 9f)
        mRightTopAngleColor = typedArray.getColor(
            R.styleable.ScanBarcodeLayoutView_topRightAngleColor,
            Color.parseColor("#464646")
        )
        mLeftTopAngleColor = typedArray.getColor(
            R.styleable.ScanBarcodeLayoutView_topLeftAngleColor,
            Color.parseColor("#FDE1DE")
        )
        mBottomLeftAngleColor = typedArray.getColor(
            R.styleable.ScanBarcodeLayoutView_bottonLeftAngleColor,
            Color.parseColor("#A9A9A9")
        )
        mBottomRightAngleColor = typedArray.getColor(
            R.styleable.ScanBarcodeLayoutView_bottomRightAngleColor,
            Color.parseColor("#F0F0F0")
        )
        mLeftTopAngleHeightAtXSide =
            typedArray.getFloat(R.styleable.ScanBarcodeLayoutView_LeftTopAngleHeightAtXSide, 100f)
        mLeftTopAngleHeightAtYSide =
            typedArray.getFloat(R.styleable.ScanBarcodeLayoutView_LeftTopAngleHeightAtYSide, 100f)
        mRightTopAngleHeightAtXSide =
            typedArray.getFloat(R.styleable.ScanBarcodeLayoutView_RightTopAngleHeightAtXSide, 100f)
        mRightTopAngleHeightAtYSide =
            typedArray.getFloat(R.styleable.ScanBarcodeLayoutView_RightTopAngleHeightAtYSide, 100f)
        mLeftBottomAngleHeightAtXSide = typedArray.getFloat(
            R.styleable.ScanBarcodeLayoutView_LeftBottomAngleHeightAtXSide,
            100f
        )
        mLeftBottomAngleHeightAtYSide = typedArray.getFloat(
            R.styleable.ScanBarcodeLayoutView_LeftBottomAngleHeightAtYSide,
            100f
        )
        mRightBottomAngleHeightAtXSide = typedArray.getFloat(
            R.styleable.ScanBarcodeLayoutView_RightBottomAngleHeightAtXSide,
            100f
        )
        mRightBottomAngleHeightAtYSide = typedArray.getFloat(
            R.styleable.ScanBarcodeLayoutView_RightBottomAngleHeightAtYSide,
            100f
        )
        mSpaceBetweenCenterRectangleAndCornerAngleHorizontally = typedArray.getFloat(
            R.styleable.ScanBarcodeLayoutView_SpaceBetweenCenterRectangleAndCornerAngleHorizontally,
            15f
        )
        mSpaceBetweenCenterRectangleAndCornerAngleVertically = typedArray.getFloat(
            R.styleable.ScanBarcodeLayoutView_SpaceBetweenCenterRectangleAndCornerAngleVertically,
            15f
        )
        typedArray.recycle()
        pos = 0
        lastTick = 0
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.strokeWidth = mAngleStokeWidth
        mPaint!!.style = Paint.Style.FILL
        mMovingLinePaint = Paint(0)
        mMovingLinePaint!!.color = mMovingLineColor
        mMovingLinePaint!!.style = Paint.Style.STROKE
        mMovingLinePaint!!.strokeWidth = mMovingLineWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mWidth = getWidth().toFloat()
        val mHeight = getHeight().toFloat()
        val mPaddingTop = paddingTop.toFloat()
        val mPaddingBottom = paddingBottom.toFloat()
        val mPaddingRight = paddingRight.toFloat()
        val mPaddingLeft = paddingLeft.toFloat()
        val rectLeft = 1 + mPaddingLeft
        val rectRight = mWidth - mPaddingRight
        val rectTop = 1 + mPaddingTop
        val rectBottom = mHeight - mPaddingBottom
        mPaint!!.color = mCenterRectangleColor
        canvas.drawRect(
            rectLeft + mAngleStokeWidth / 2 + mSpaceBetweenCenterRectangleAndCornerAngleHorizontally,
            rectTop + mAngleStokeWidth / 2 + mSpaceBetweenCenterRectangleAndCornerAngleVertically,
            rectRight - mAngleStokeWidth / 2 - mSpaceBetweenCenterRectangleAndCornerAngleHorizontally,
            rectBottom - mAngleStokeWidth / 2 - mSpaceBetweenCenterRectangleAndCornerAngleVertically,
            mPaint!!
        )
        mPaint!!.color = mLeftTopAngleColor
        canvas.drawLine(
            rectLeft,
            rectTop + mAngleStokeWidth / 2,
            mLeftTopAngleHeightAtXSide + mPaddingLeft,
            rectTop + mAngleStokeWidth / 2,
            mPaint!!
        )
        canvas.drawLine(
            rectLeft + mAngleStokeWidth / 2,
            rectTop,
            rectLeft + mAngleStokeWidth / 2,
            rectTop + mLeftTopAngleHeightAtYSide,
            mPaint!!
        )
        mPaint!!.color = mRightTopAngleColor
        canvas.drawLine(
            rectLeft - mPaddingLeft + (rectRight - mRightTopAngleHeightAtXSide),
            rectTop + mAngleStokeWidth / 2,
            rectRight,
            rectTop + mAngleStokeWidth / 2,
            mPaint!!
        )
        canvas.drawLine(
            rectRight - mAngleStokeWidth / 2,
            rectTop,
            rectRight - mAngleStokeWidth / 2,
            rectTop + mRightTopAngleHeightAtYSide,
            mPaint!!
        )
        mPaint!!.color = mBottomLeftAngleColor
        canvas.drawLine(
            rectLeft + mAngleStokeWidth / 2,
            rectBottom - mLeftBottomAngleHeightAtYSide, rectLeft + mAngleStokeWidth / 2, rectBottom,
            mPaint!!
        )
        canvas.drawLine(
            rectLeft,
            rectBottom - mAngleStokeWidth / 2,
            rectLeft + mLeftBottomAngleHeightAtXSide,
            rectBottom - mAngleStokeWidth / 2,
            mPaint!!
        )
        mPaint!!.color = mBottomRightAngleColor
        canvas.drawLine(
            rectRight - mAngleStokeWidth / 2,
            rectBottom - mRightBottomAngleHeightAtYSide,
            rectRight - mAngleStokeWidth / 2,
            rectBottom,
            mPaint!!
        )
        canvas.drawLine(
            rectRight - mRightBottomAngleHeightAtXSide,
            rectBottom - mAngleStokeWidth / 2,
            rectRight,
            rectBottom - mAngleStokeWidth / 2,
            mPaint!!
        )

        // Draw the moving line
        if (mAnimated) {
            val lineAreaHeight = mHeight - 10
            var relativeY = mMovingLineProgress * lineAreaHeight
            if (!mLineMovingDown) {
                relativeY = lineAreaHeight - relativeY
            }
            val lineY = rectTop + mAngleStokeWidth / 2 + relativeY
            canvas.drawLine(
                rectLeft + 20, lineY,
                rectRight - 20, lineY, mMovingLinePaint!!
            )
        }
    }

    private fun startMovingLineAnimation() {
        // Set up the animation of the moving line
        val animation = ValueAnimator.ofFloat(0f, 1f)
        animation.interpolator = LinearInterpolator()
        animation.duration = (mUpAndDownSeconds / 2 * 1000).toLong()
        animation.addUpdateListener { valueAnimator ->
            mMovingLineProgress = valueAnimator.animatedValue as Float
            invalidate()
        }

        // Reverse the animation when it's finished
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mLineMovingDown = !mLineMovingDown
                startMovingLineAnimation()
            }
        })
        animation.start()
    }
}
