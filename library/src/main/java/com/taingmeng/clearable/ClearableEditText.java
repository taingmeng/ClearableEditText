package com.taingmeng.clearable;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by taingmeng on 1/4/16.
 */
public class ClearableEditText extends EditText {

    protected final static int EXTRA_TAPPABLE_AREA = 20;

    @DrawableRes
    protected int iconClear = R.drawable.icon_clear;

    protected int tintColor;

    protected ColorStateList tintStateListColor;

    protected Rect rBounds;

    protected Drawable drawableClear;

    protected boolean isRTL;

    protected OnClearListener onClearListener;

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public ClearableEditText(Context context) {
        super(context, null);
    }

    private void init(AttributeSet attrs, int defStyle){
        isRTL = isRTLLanguage();

        if(attrs != null){
            TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ClearableEditText, defStyle, 0);
            try {
                iconClear = styledAttributes.getResourceId(R.styleable.ClearableEditText_cet_drawable, iconClear);
                tintColor = styledAttributes.getColor(R.styleable.ClearableEditText_cet_tint, tintColor);
                tintStateListColor = styledAttributes.getColorStateList(R.styleable.ClearableEditText_cet_tintStateList);
            }
            finally {
                styledAttributes.recycle();
            }

        }
        drawableClear = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), iconClear)).mutate();

        if (tintColor != 0) {
            DrawableCompat.setTint(drawableClear, tintColor);

        }
        if(tintStateListColor != null){
            DrawableCompat.setTintList(drawableClear, tintStateListColor);
        }

        showClearIcon(getText().length() != 0);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP && drawableClear!=null)
        {
            final Rect bounds = drawableClear.getBounds();
            final int x = (int) event.getX();
            int iconXRect = isRTL? getLeft() + bounds.width() + EXTRA_TAPPABLE_AREA :
                    getRight() - bounds.width() - EXTRA_TAPPABLE_AREA;


            if ((isRTL? x<= iconXRect : x >= iconXRect)) {
                this.setText("");
                // prevent keyboard from coming up
                event.setAction(MotionEvent.ACTION_CANCEL);

                if(onClearListener != null){
                    onClearListener.onCleared(this);
                }
            }


        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable
    {
        drawableClear = null;
        rBounds = null;
        super.finalize();
    }

    private boolean isRTLLanguage() {
        // as getLayoutDirection was introduced in API 17, under 17 we default to LTR
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
            return false;
        }
        Configuration config = getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {

        showClearIcon(text.length() != 0);

        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    private void showClearIcon(boolean show){
        if(drawableClear == null){
            return;
        }

        if(show){
            setCompoundDrawablesWithIntrinsicBounds(isRTL ? drawableClear : null, null, isRTL ? null : drawableClear, null);
        }
        else {
            setCompoundDrawables(null, null, null, null);
        }
    }

    public Drawable getDrawableClear() {
        return drawableClear;
    }

    public interface OnClearListener{
        public void onCleared(EditText editText);
    }

    public void setOnClearListener(OnClearListener onClearListener) {
        this.onClearListener = onClearListener;
    }
}

