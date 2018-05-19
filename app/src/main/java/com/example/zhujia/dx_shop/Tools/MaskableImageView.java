package com.example.zhujia.dx_shop.Tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by DXSW5 on 2017/6/27.
 */

public class MaskableImageView extends ImageView {

    public MaskableImageView(Context context) {
        super(context);
    }
    public MaskableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MaskableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.setColorFilter(0x33000000);
                //重写触摸事件的方法,当按钮被点击的时候
                mOnClickListener.onClick();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.setColorFilter(null);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 定义点击的接口
     */
    public interface OnClickListener {
        void onClick();
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener (OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

}