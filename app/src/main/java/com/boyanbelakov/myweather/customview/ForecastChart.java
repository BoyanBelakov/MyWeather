package com.boyanbelakov.myweather.customview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.boyanbelakov.myweather.R;
import com.boyanbelakov.myweather.customview.graphics.CircleHolder;
import com.boyanbelakov.myweather.customview.graphics.LineHolder;
import com.boyanbelakov.myweather.customview.graphics.TextHolder;
import com.boyanbelakov.myweather.data.Forecast;
import com.boyanbelakov.myweather.util.WeatherConditions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class ForecastChart extends View {
    private static final long ANIMATION_DURATION_MILLIS = 600;

    private static final int DEF_MAX_DATA_LENGTH = 5;
    private static final int DEF_MAX_TEMPERATURE_COLOR = 0xffffbb33;
    private static final int DEF_MIN_TEMPERATURE_COLOR = 0xff33b5e5;
    private static final int DEF_TEXT_COLOR = Color.WHITE;
    private static final int DEF_GRID_COLOR = Color.LTGRAY;

    private static final float[] TICK_UNIT_DEFAULTS = {1.0E-10f, 2.5E-10f,
            5.0E-10f, 1.0E-9f, 2.5E-9f, 5.0E-9f, 1.0E-8f, 2.5E-8f, 5.0E-8f,
            1.0E-7f, 2.5E-7f, 5.0E-7f, 1.0E-6f, 2.5E-6f, 5.0E-6f, 1.0E-5f,
            2.5E-5f, 5.0E-5f, 1.0E-4f, 2.5E-4f, 5.0E-4f, 0.0010f, 0.0025f,
            0.0050f, 0.01f, 0.025f, 0.05f, 0.1f, 0.25f, 0.5f, 1.0f, 2.5f, 5.0f,
            10.0f, 25.0f, 50.0f, 100.0f, 250.0f, 500.0f, 1000.0f, 2500.0f,
            5000.0f, 10000.0f, 25000.0f, 50000.0f, 100000.0f, 250000.0f,
            500000.0f, 1000000.0f, 2500000.0f, 5000000.0f, 1.0E7f, 2.5E7f,
            5.0E7f, 1.0E8f, 2.5E8f, 5.0E8f, 1.0E9f, 2.5E9f, 5.0E9f, 1.0E10f,
            2.5E10f, 5.0E10f, 1.0E11f, 2.5E11f, 5.0E11f, 1.0E12f, 2.5E12f,
            5.0E12f};

    private Forecast[] mData;
    private int mDataLength;

    private boolean mLayoutDone;
    private float[] mAxisValTickMarks, mAxisLabelTickMarks;

    private int mMaxDataLength;
    private float mAxisLabelGap;
    private float mLabelSize;
    private float mIconSize;
    private float mTemperatureSize;
    private float mGridStroke;
    private float mCircleRadius;
    private float mTemperatureLineStroke;

    private int mGridColor;
    private int mMaxTemperatureColor;
    private int mMinTemperatureColor;
    private int mTextColor;

    private final float mDensity;
    private final int mGridVLines;
    private final int mGridHLines;

    private final TextPaint mIconPaint;
    private final TextPaint mLabelPaint;
    private final TextPaint mTemperaturePaint;
    private final Paint mPaint;
    // graphics data
    private final LineHolder[] mGridBuffer;
    private final TextHolder[] mLabelBuffer;
    private final TextHolder[] mIconBuffer;
    private final CircleHolder[] mMinTemperatureCircleBuffer;
    private final CircleHolder[] mMaxTemperatureCircleBuffer;
    private final TextHolder[] mMinTemperatureTextBuffer;
    private final TextHolder[] mMaxTemperatureTextBuffer;

    private final Calendar mCalendar = Calendar.getInstance();

    public ForecastChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ForecastChart, 0, 0);
        try {
            mGridColor = a.getColor(R.styleable.ForecastChart_gridColor, Color.LTGRAY);
            mMaxTemperatureColor = a.getColor(R.styleable.ForecastChart_maxTemperatureColor, 0);
            mMinTemperatureColor = a.getColor(R.styleable.ForecastChart_minTemperatureColor, 0);
            mGridStroke = a.getDimension(R.styleable.ForecastChart_gridStroke, 0);
            mTemperatureLineStroke = a.getDimension(R.styleable.ForecastChart_temperatureLineStroke, 0);
            mCircleRadius = a.getDimension(R.styleable.ForecastChart_circleRadius, 0);
            mTemperatureSize = a.getDimension(R.styleable.ForecastChart_temperatureSize, 0);
            mLabelSize = a.getDimension(R.styleable.ForecastChart_labelSize, 0);
            mIconSize = a.getDimension(R.styleable.ForecastChart_iconSize, 0);
            mAxisLabelGap = a.getDimension(R.styleable.ForecastChart_axisLabelGap, 0);
            mMaxDataLength =  a.getInteger(R.styleable.ForecastChart_maxDataLength, 0);
        } finally {
            a.recycle();
        }

        if (mGridColor == 0) mGridColor = DEF_GRID_COLOR;
        if (mMaxTemperatureColor == 0) mMaxTemperatureColor = DEF_MAX_TEMPERATURE_COLOR;
        if (mMinTemperatureColor == 0) mMinTemperatureColor = DEF_MIN_TEMPERATURE_COLOR;
        if (mTextColor == 0) mTextColor = DEF_TEXT_COLOR;
        if (mMaxDataLength == 0) mMaxDataLength = DEF_MAX_DATA_LENGTH;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mDensity = metrics.density;
        float scaledDensity = metrics.scaledDensity;

        //if (mGridStroke == 0) {/*ok*/}
        if (mTemperatureLineStroke == 0) mTemperatureLineStroke = 2 * mDensity;
        if (mCircleRadius == 0) mCircleRadius = 4 * mDensity;
        if (mTemperatureSize == 0) mTemperatureSize = 12 * scaledDensity;
        if (mLabelSize == 0) mLabelSize = 14 * scaledDensity;
        if (mIconSize == 0) mIconSize = 16 * scaledDensity;
        if (mAxisLabelGap == 0) mAxisLabelGap = 4 * mDensity;

        mGridHLines = 3;
        mGridVLines = mMaxDataLength + 1;
        int gridLines = mGridHLines + mGridVLines;
        mGridBuffer = new LineHolder[gridLines];
        for (int i = 0; i < gridLines; i++) {
            mGridBuffer[i] = new LineHolder();
        }

        mLabelBuffer = new TextHolder[mMaxDataLength];
        mIconBuffer = new TextHolder[mMaxDataLength];
        mMinTemperatureCircleBuffer = new CircleHolder[mMaxDataLength];
        mMaxTemperatureCircleBuffer = new CircleHolder[mMaxDataLength];
        mMinTemperatureTextBuffer = new TextHolder[mMaxDataLength];
        mMaxTemperatureTextBuffer = new TextHolder[mMaxDataLength];

        for (int i = 0; i < mMaxDataLength; i++) {
            mLabelBuffer[i] = new TextHolder();
            mIconBuffer[i] = new TextHolder();
            mMinTemperatureCircleBuffer[i] = new CircleHolder();
            mMaxTemperatureCircleBuffer[i] = new CircleHolder();
            mMinTemperatureTextBuffer[i] = new TextHolder();
            mMaxTemperatureTextBuffer[i] = new TextHolder();
        }

        mLabelPaint = new TextPaint();
        mLabelPaint.setColor(mTextColor);
        mLabelPaint.setTextSize(mLabelSize);
        mLabelPaint.setTextAlign(Paint.Align.CENTER);
        mLabelPaint.setAntiAlias(true);

        mIconPaint = new TextPaint();
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/weather.ttf");
        mIconPaint.setTypeface(font);
        mIconPaint.setColor(mTextColor);
        mIconPaint.setTextSize(mIconSize);
        mIconPaint.setTextAlign(Paint.Align.CENTER);
        mIconPaint.setAntiAlias(true);

        mTemperaturePaint = new TextPaint();
        mTemperaturePaint.setColor(mTextColor);
        mTemperaturePaint.setTextSize(mTemperatureSize);
        mTemperaturePaint.setTextAlign(Paint.Align.CENTER);
        mTemperaturePaint.setAntiAlias(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public void setData(Forecast[] data) {
        mData = data;
        mDataLength = (data.length > mMaxDataLength) ? mMaxDataLength : data.length;

        if (mLayoutDone && mDataLength != 0) {
            updateLabels();
            updateIcons();
            updateMinTemperature();
            updateMaxTemperature();

            startAnimation();
        }
        invalidate();
    }

    private void startAnimation(){
        float start1 = getPaddingTop() + mAxisValTickMarks[1];
        List<Animator> maxAnimators = createAnimators(mMaxTemperatureCircleBuffer, start1);

        float start2 = getPaddingTop() + mAxisValTickMarks[2];
        List<Animator> minAnimators = createAnimators(mMinTemperatureCircleBuffer, start2);

        ValueAnimator refreshAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        refreshAnimator.setDuration(ANIMATION_DURATION_MILLIS);
        refreshAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator a) {
                invalidate();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(minAnimators);
        animatorSet.playTogether(maxAnimators);
        animatorSet.playTogether(refreshAnimator);
        animatorSet.start();
    }

    private List<Animator> createAnimators(CircleHolder[] buffer, float startY){
        List<Animator> animators = new ArrayList<>(mDataLength);

        for (int i = 0; i < mDataLength; i++) {
            CircleHolder c = buffer[i];
            float endY = c.getCy();
            c.setCy(startY);

            ObjectAnimator a = new ObjectAnimator();
            a.setPropertyName("cy");
            a.setFloatValues(startY, endY);
            a.setTarget(c);
            a.setDuration(ANIMATION_DURATION_MILLIS);

            animators.add(a);
        }

        return animators;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int padL = getPaddingLeft();
        int padR = getPaddingRight();
        int padT = getPaddingTop();
        int padB = getPaddingBottom();

        float labelAscent = -mLabelPaint.ascent();
        float labelDescent = mLabelPaint.descent();
        float iconAscent = -mIconPaint.ascent();
        float iconDescent = mIconPaint.descent();

        int axisValLength = (int) (h - (padT + mAxisLabelGap + labelAscent + labelDescent +
                iconAscent + iconDescent + padB));
        int axisLabelLength = w - (padL + padR);

        mAxisValTickMarks = getTickMarks(axisValLength, mGridHLines);
        mAxisLabelTickMarks = getTickMarks(axisLabelLength, mGridVLines + mMaxDataLength);

        mLayoutDone = true;
        
        updateGrid();
        if (mDataLength != 0){
            updateLabels();
            updateIcons();
            updateMinTemperature();
            updateMaxTemperature();
        }
    }

    private List<Integer> minValues() {
        List<Integer> result = new ArrayList<>(mDataLength);
        for (int i = 0; i < mDataLength; i++) {
            Forecast f = mData[i];
            result.add(f.getLow());
        }
        return result;
    }

    private List<Integer> maxValues() {
        List<Integer> result = new ArrayList<>(mDataLength);
        for (int i = 0; i < mDataLength; i++) {
            Forecast f = mData[i];
            result.add(f.getHigh());
        }
        return result;
    }

    private void updateMaxTemperature() {
        List<Integer> values = maxValues();
        float originY = getPaddingTop() + mAxisValTickMarks[1];
        float axisLength = mAxisValTickMarks[1];
        updateTemperature(values, mMaxTemperatureCircleBuffer, mMaxTemperatureTextBuffer, originY,
                axisLength);
    }

    private void updateMinTemperature() {
        List<Integer> values = minValues();
        float originY = getPaddingTop() + mAxisValTickMarks[2];
        float axisLength = mAxisValTickMarks[2] - mAxisValTickMarks[1];
        updateTemperature(values, mMinTemperatureCircleBuffer, mMinTemperatureTextBuffer, originY,
                axisLength);
    }

    private void updateTemperature(List<Integer> values, CircleHolder[] circleBuffer,
                                   TextHolder[] textBuffer, float originY, float axisLength){
        int minVal = Collections.min(values);
        int maxVal = Collections.max(values);
        float[] autoRange = autoRange(minVal, maxVal);

        float minTemp = autoRange[0];
        float maxTemp = autoRange[1];
        float tickUnit = autoRange[2];
        List<Float> valAxis = calculateValAxis(minTemp, maxTemp, tickUnit);

        float temperatureAscent = -mTemperaturePaint.ascent();
        float temperatureDescent = mTemperaturePaint.descent();
        axisLength -= (2 * mCircleRadius + temperatureAscent + temperatureDescent);

        float[] valAxisTickMarks = getTickMarks(axisLength, valAxis.size());

        int zeroIndex = valAxis.indexOf(0.0f);
        float negLength = valAxisTickMarks[zeroIndex];
        float posLength = axisLength - negLength;

        originY -= negLength;
        int padL = getPaddingLeft();

        for (int i = 0, j = 1; i < mDataLength; i++, j += 2) {
            CircleHolder c = circleBuffer[i];
            float x = padL + mAxisLabelTickMarks[j];
            c.setCx(x);

            float y;
            int val = values.get(i);
            if (val > 0){
                y =  originY - (val / maxTemp) * posLength;
            } else if (val < 0){
                y = originY + (val / minTemp) * negLength;
            } else {
                y = originY;
            }
            c.setCy(y);

            TextHolder t = textBuffer[i];
            t.setX(x);
            t.setY(y - mCircleRadius - temperatureDescent);
            String s = formatTemp(val);
            t.setText(s);
        }
    }

    private String formatTemp(int temp) {
        return temp + " \u00b0";
    }

    private List<Float> calculateValAxis(float min, float max, float tickUnit) {
        List<Float> valAxis = new ArrayList<>();
        for (float i = min; i <= max; i += tickUnit) {
            valAxis.add(i);
        }

        return valAxis;
    }

    private void updateIcons() {
        float labelAscent = -mLabelPaint.ascent();
        float labelDescent = mLabelPaint.descent();
        float iconAscent = -mIconPaint.ascent();

        float y = getPaddingTop() + mAxisValTickMarks[2] + mAxisLabelGap + labelAscent +
                labelDescent + iconAscent;
        float padL = getPaddingLeft();

        for (int i = 0, j = 1; i < mDataLength; i++, j += 2) {
            TextHolder icon = mIconBuffer[i];
            icon.setX(padL + mAxisLabelTickMarks[j]);
            icon.setY(y);

            int code = mData[i].getCode();
            String text = WeatherConditions.fromCode(code).getIcon();
            icon.setText(text);
        }
    }

    private void updateLabels() {
        float labelAscent = -mLabelPaint.ascent();
        float y = getPaddingTop() + mAxisValTickMarks[2] + mAxisLabelGap + labelAscent;
        float padL = getPaddingLeft();

        for (int i = 0, j = 1; i < mDataLength; i++, j+= 2) {
            TextHolder label = mLabelBuffer[i];
            label.setX(padL + mAxisLabelTickMarks[j]);
            label.setY(y);

            String text = getLabel(i);
            float tickUnit = mAxisLabelTickMarks[i + 2] - mAxisLabelTickMarks[i];
            text = TextUtils.ellipsize(text, mLabelPaint, tickUnit, TextUtils.TruncateAt.END)
                    .toString();
            label.setText(text);
        }
    }

    private void updateGrid() {
        int padL = getPaddingLeft();
        int padR = getPaddingRight();
        int padT = getPaddingTop();
        int w = getWidth();

        int gridIndex = 0;
        // update hLines
        for (int i = 0; i < mGridHLines; i++, gridIndex++) {
            LineHolder l = mGridBuffer[gridIndex];
            float y = padT + mAxisValTickMarks[i];
            l.setStartX(padL);
            l.setStartY(y);
            l.setStopX(w - padR);
            l.setStopY(y);
        }
        // update vLines
        float stopY = padT + mAxisValTickMarks[2];
        for (int i = 0, j = 0; i < mGridVLines; i++, j +=2, gridIndex++) {
            LineHolder l = mGridBuffer[gridIndex];
            float x = padL + mAxisLabelTickMarks[j];
            l.setStartX(x);
            l.setStartY(padT);
            l.setStopX(x);
            l.setStopY(stopY);
        }
    }

    private float[] getTickMarks(float axisLength, int numTicks){
        float[] result = new float[numTicks];
        int lastIndex = numTicks - 1;

        for (int i = 0; i < numTicks; i++) {
            result[i] = ((float) i / lastIndex) * axisLength;
        }

        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = measureWidth(widthMeasureSpec);
        int h = measureHeight(heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = prefWidth();
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = prefHeight();
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }

        return result;
    }

    private int prefWidth() {
        float maxTextWidth = 48 * mDensity;
        // find maxTextWidth
        for (int i = 0; i < mDataLength; i++) {
            String label = getLabel(i);
            float labelWidth = mLabelPaint.measureText(label);
            if (labelWidth > maxTextWidth) {
                maxTextWidth = labelWidth;
            }

            Forecast f = mData[i];
            int code = f.getCode();
            String icon = WeatherConditions.fromCode(code).getIcon();
            float iconWidth = mIconPaint.measureText(icon);
            if (iconWidth > maxTextWidth) {
                maxTextWidth = iconWidth;
            }

            String minTemp = formatTemp(f.getLow());
            float minTempWidth = mTemperaturePaint.measureText(minTemp);
            if (minTempWidth > maxTextWidth){
                maxTextWidth = minTempWidth;
            }

            String maxTemp = formatTemp(f.getHigh());
            float maxTempWidth = mTemperaturePaint.measureText(maxTemp);
            if (maxTempWidth > maxTextWidth){
                maxTextWidth = maxTempWidth;
            }
        }

        float labelGap = 4 * mDensity;
        float axisLabelLength = mGridStroke * mGridVLines + maxTextWidth * mMaxDataLength +
                labelGap * 2 * mMaxDataLength;
        int ceil = (int)(Math.ceil(axisLabelLength / (float) mMaxDataLength));
        int axisLabelWidthRounded = ceil * mMaxDataLength;

        return getPaddingLeft() + axisLabelWidthRounded + getPaddingRight();
    }

    private String getLabel(int index) {
        Date date = mData[index].getDate();
        if (date != null){
            mCalendar.setTime(date);
            return ""+ mCalendar.get(Calendar.DAY_OF_MONTH);
        }

        return "";
    }

    private int prefHeight() {
        float labelAscent = -mLabelPaint.ascent();
        float labelDescent = mLabelPaint.descent();
        float iconAscent = -mIconPaint.ascent();
        float iconDescent = mIconPaint.descent();

        float axisValLength = mGridStroke * mGridHLines + 100 * mDensity;

        return (int) Math.ceil(getPaddingTop() + axisValLength + mAxisLabelGap + labelAscent +
                labelDescent + iconAscent + iconDescent + getPaddingBottom());
    }

    private float[] autoRange(float minValue, float maxValue) {
        // check zero into range
        if (maxValue < 0) {
            maxValue = 0;
        } else if (minValue > 0) {
            minValue = 0;
        }
        float range = maxValue - minValue;
        if (range == 0) {
            return new float[]{0.0f, 100.0f, 10.0f};
        }
        // pad min and max by 2%
        float paddedRange = range * 1.02f;
        float padding = (paddedRange - range) / 2;

        float paddedMin = minValue - padding;
        float paddedMax = maxValue + padding;

        if (paddedMin < 0 && minValue == 0) {
            paddedMin = 0;
        }
        if ((paddedMax > 0 && maxValue == 0)) {
            paddedMax = 0;
        }

        float tickUnit = paddedRange / (float) 10;
        // search for the best tick unit that fits
        float tickUnitRounded = tickUnit;
        for (float tickUnitDefault : TICK_UNIT_DEFAULTS) {
            if (tickUnitDefault > tickUnit) {
                tickUnitRounded = tickUnitDefault;
                break;
            }
        }
        // move min and max to nearest tick mark
        int floor = (int) Math.floor(paddedMin / tickUnitRounded);
        float minRounded = floor * tickUnitRounded;
        int ceil = (int) Math.ceil(paddedMax / tickUnitRounded);
        float maxRounded = ceil * tickUnitRounded;

        return new float[]{minRounded, maxRounded, tickUnitRounded};
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw grid
        mPaint.setStrokeWidth(mGridStroke);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mGridColor);
        for (LineHolder l : mGridBuffer){
            canvas.drawLine(l.getStartX(), l.getStartY(), l.getStopX(), l.getStopY(), mPaint);
        }

        if (mDataLength == 0) return;

        // draw min temperature circles
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mMinTemperatureColor);
        for (int i = 0; i < mDataLength; i++) {
            CircleHolder minTemp = mMinTemperatureCircleBuffer[i];
            canvas.drawCircle(minTemp.getCx(), minTemp.getCy(), mCircleRadius, mPaint);
        }
        // draw max temperature circles
        mPaint.setColor(mMaxTemperatureColor);
        for (int i = 0; i < mDataLength; i++) {
            CircleHolder maxTemp = mMaxTemperatureCircleBuffer[i];
            canvas.drawCircle(maxTemp.getCx(), maxTemp.getCy(), mCircleRadius, mPaint);
        }
        // draw min temperature lines
        mPaint.setStrokeWidth(mTemperatureLineStroke);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mMinTemperatureColor);
        for (int i = 0; i < mDataLength - 1; i++) {
            CircleHolder start = mMinTemperatureCircleBuffer[i];
            CircleHolder end = mMinTemperatureCircleBuffer[i + 1];
            canvas.drawLine(start.getCx(), start.getCy(), end.getCx(), end.getCy(), mPaint);
        }
        // draw max temperature lines
        mPaint.setColor(mMaxTemperatureColor);
        for (int i = 0; i < mDataLength - 1; i++) {
            CircleHolder start = mMaxTemperatureCircleBuffer[i];
            CircleHolder end = mMaxTemperatureCircleBuffer[i + 1];
            canvas.drawLine(start.getCx(), start.getCy(), end.getCx(), end.getCy(), mPaint);
        }
        // draw min temperature text
        for (int i = 0; i < mDataLength; i++) {
            TextHolder t = mMinTemperatureTextBuffer[i];
            canvas.drawText(t.getText(), t.getX(), t.getY(), mTemperaturePaint);
        }
        // draw max temperature text
        for (int i = 0; i < mDataLength; i++) {
            TextHolder t = mMaxTemperatureTextBuffer[i];
            canvas.drawText(t.getText(), t.getX(), t.getY(), mTemperaturePaint);
        }
        // draw labels and icons
        for (int i = 0; i < mDataLength; i++) {
            TextHolder label = mLabelBuffer[i];
            canvas.drawText(label.getText(), label.getX(), label.getY(), mLabelPaint);

            TextHolder icon = mIconBuffer[i];
            canvas.drawText(icon.getText(), icon.getX(), icon.getY(), mIconPaint);
        }
    }
}
