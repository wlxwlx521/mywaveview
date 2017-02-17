package car.com.wlc.waterview.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlc on 2017/2/16.
 */

public class WaveView extends View {
    private Matrix mShaderMatrix;
    private Paint mViewPaint;
    private Paint mBorderPaint;
    private Paint mTextPaint;

    /**
     * +------------------------+
     * |<--wave length-> 波长        |______
     * |   /\          |   /\   |  |
     * |  /  \         |  /  \  | amplitude 波峰
     * | /    \        | /    \ |  |
     * |/      \       |/      \|__|____
     * |        \      /        |  |
     * |         \    /         |  |
     * |          \  /          |  |
     * |           \/           | water level
     * |                        |  |
     * |                        |  |
     * +------------------------+__|____
     */
    private final float DEFAULT_AMPLITUDE_RATIO = 0.05f;
    private final float DEFAULT_WAVE_LEVEL_RATIO = 0.05f;
    private final float DEFAULT_WAVE_LENGTH_RATIO = 0.05f;
    private final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;

    public static final int DEFAULT_BEHIND_WAVE_COLOR = Color.parseColor("#28FFFFFF");
    public static final int DEFAULT_FRONT_WAVE_COLOR = Color.parseColor("#3CFFFFFF");
    //if true ,the shader will display the wave
    private boolean mShowWave;

    // shader containing repeated waves
    private BitmapShader mWaveShader;
    private float mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO;
    private float mWaveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO;
    private float mWaveLevelRadio = DEFAULT_WAVE_LEVEL_RATIO;
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;
    private int mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR;
    private int mFrontWaveColor = DEFAULT_FRONT_WAVE_COLOR;
    private int mTextColor = Color.parseColor("#3F51B5");
    private int mTextsize = 41;
    private int mBoderColor = Color.parseColor("#28FFFFFF");
    private int mBorderWidth = 6;
    private float waveHeigth;

    private int width;
    private int heigth;
    private double mDefaultAngularFrequency;
    private float mDefaultAmplitude;
    private float mDefaultWaterLevel;
    private int mDefaultWaveLength;
    private String contentText = "hello";
    private AnimatorSet mAnimatorSet;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mShaderMatrix = new Matrix();
        mViewPaint = new Paint();
        mViewPaint.setAntiAlias(true);
        //外圆
        if (mBorderPaint == null) {
            mBorderPaint = new Paint();
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setColor(mBoderColor);
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }
        //字体颜色
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextsize);
    }

    public float getWaveHeigth() {
        return waveHeigth;
    }

    public void setWaveHeigth(float waveHeigth) {
        this.waveHeigth = waveHeigth;
    }

    public void setCurrentText(String content, int contentColor, int contentSize) {
        this.contentText = content;
        this.mTextsize = contentSize;
        this.mTextColor = contentColor;
    }

    public boolean ismShowWave() {
        return mShowWave;
    }

    public void setmShowWave(boolean mShowWave) {
        this.mShowWave = mShowWave;
    }

    public float getmAmplitudeRatio() {
        return mAmplitudeRatio;
    }

    public void setmAmplitudeRatio(float mAmplitudeRatio) {
        this.mAmplitudeRatio = mAmplitudeRatio;
        invalidate();
    }

    public float getmWaveLengthRatio() {
        return mWaveLengthRatio;
    }

    public void setmWaveLengthRatio(float mWaveLengthRatio) {
        this.mWaveLengthRatio = mWaveLengthRatio;
        invalidate();
    }

    public float getmWaveLevelRadio() {
        return mWaveLevelRadio;
    }

    public void setmWaveLevelRadio(float mWaveLevelRadio) {
        this.mWaveLevelRadio = mWaveLevelRadio;
        invalidate();
    }

    public float getmWaveShiftRatio() {
        return mWaveShiftRatio;
    }

    public void setmWaveShiftRatio(float mWaveShiftRatio) {

        this.mWaveShiftRatio = mWaveShiftRatio;
        invalidate();
    }

    public int getmBehindWaveColor() {
        return mBehindWaveColor;
    }

    public void setmBehindWaveColor(int mBehindWaveColor) {
        this.mBehindWaveColor = mBehindWaveColor;
        invalidate();
    }

    public int getmFrontWaveColor() {
        return mFrontWaveColor;
    }

    public void setmFrontWaveColor(int mFrontWaveColor) {
        this.mFrontWaveColor = mFrontWaveColor;
        invalidate();
    }

    public int getmBoderColor() {
        return mBoderColor;
    }

    public void setmBoderColor(int mBoderColor) {
        this.mBoderColor = mBoderColor;
        invalidate();
    }

    public int getmBorderWidth() {
        return mBorderWidth;
    }

    public void setmBorderWidth(int mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
        invalidate();
    }

    public void setWaveColor(int behindWaveColor, int frontWaveColor) {
        mBehindWaveColor = behindWaveColor;
        mFrontWaveColor = frontWaveColor;
        mWaveShader = null;
        createShader();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        heigth = getMeasuredHeight();
    }

    /*
    *  Create the shader with default waves which repeat horizontally, and clamp vertically
    * */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createShader();
    }

    private void createShader() {

        mDefaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / getWidth();
        mDefaultAmplitude = getHeight() * DEFAULT_AMPLITUDE_RATIO;
        mDefaultWaterLevel = getHeight() * DEFAULT_WAVE_LEVEL_RATIO;
        mDefaultWaveLength = getWidth();
        //根据水波图的大小创建一个画布
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setStrokeWidth(2);

        //Draw default waves into the bitmap
        //
        //  y=Asin(ωx+φ)+h
        final int endX = getWidth() + 1;
        final int endY = getHeight() + 1;
        float[] waveY = new float[endX];
        //画前 一个水波
        wavePaint.setColor(mBehindWaveColor);
        for (int beginX = 0; beginX < endX; beginX++) {
            double wx = beginX * mDefaultAngularFrequency;
            float beginY = (float) mDefaultWaterLevel + mDefaultAmplitude;
            canvas.drawLine(beginX, beginY, beginX, endY, wavePaint);
            waveY[beginX] = beginY;
        }
        //画 后一个水波
        wavePaint.setColor(mFrontWaveColor);
        final int wave2Shift = (int) mDefaultWaveLength / 4;
        for (int beginX = 0; beginX < endX; beginX++) {
            canvas.drawLine(beginX, waveY[beginX + wave2Shift] % endX, beginX, endY, wavePaint);
        }
        mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        mViewPaint.setShader(mWaveShader);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //modify paint shader according

        if (mShowWave && mWaveShader != null) {
            // first call after mShowWave, assign it to our paint
            if (mViewPaint.getShader() == null) {
                mViewPaint.setShader(mWaveShader);
            }

            //根据 波长和波峰缩放模型
            // 它的大小是根据（ 波长的宽 和 波峰的高）波来决定的
            mShaderMatrix.setScale(mWaveLengthRatio / DEFAULT_WAVE_LENGTH_RATIO, mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO, 0
                    , mDefaultWaterLevel);
            // 根据 mWaveShiftRatio and mWaterLevelRatio 移动shader
            // 开始得位置是（mWaveShiftRatio for x, mWaterLevelRatio for y）
            mShaderMatrix.postTranslate(mWaveShiftRatio * getWidth(), (DEFAULT_WAVE_LEVEL_RATIO - mWaveLengthRatio) * getHeight());
            // 再一次是=使shader 重置
            mWaveShader.setLocalMatrix(mShaderMatrix);
            //画字
            canvas.drawText(contentText, width / 2, heigth / 2, mTextPaint);
            //画边界
            float borderWidth = mBorderPaint == null ? 0f : mBorderPaint.getStrokeWidth();
            if (borderWidth > 0) {
                canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, (getWidth() - borderWidth) / 2f - 1f, mBorderPaint);
            }
            float radius = getWidth() / 2f - borderWidth;
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, mViewPaint);
        } else {
            mViewPaint.setShader(null);
        }
    }

    private void initAnimation() {
        List<Animator> animations = new ArrayList<>();
        //waveShift
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(this, "waveLevelRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(1000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        animations.add(waveShiftAnim);

        //waveLevel
        ObjectAnimator waveLleveltAnim = ObjectAnimator.ofFloat(this, "waveLevelRatio", 0f, waveHeigth);
        waveLleveltAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveLleveltAnim.setDuration(5000);
        waveLleveltAnim.setInterpolator(new DecelerateInterpolator());
        animations.add(waveLleveltAnim);

        //amplitude
        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(this, "waveLevelRatio", 0.0001f, 0.05f);
        amplitudeAnim.setRepeatCount(ValueAnimator.INFINITE);
        amplitudeAnim.setRepeatMode(ValueAnimator.REVERSE);
        amplitudeAnim.setDuration(5000);
        amplitudeAnim.setInterpolator(new LinearInterpolator());
        animations.add(amplitudeAnim);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animations);

    }

    public void start() {
        this.setmShowWave(true);
        if (mAnimatorSet == null) {
            mAnimatorSet.start();
        }
    }

    public void cancel() {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
        }
    }
}























