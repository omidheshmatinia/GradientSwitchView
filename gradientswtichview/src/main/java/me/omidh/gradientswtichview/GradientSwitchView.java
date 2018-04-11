package me.omidh.gradientswtichview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class GradientSwitchView extends FrameLayout {

    private String beginText = "Hello";
    private String endText = "GoodBye";
    private SwitchStates currentState = SwitchStates.Begin;
    private int viewHeight = 40;
    private int textColor = Color.BLACK;
    private Integer beginColor;
    private Integer middleColor;
    private Integer endColor;
    private int circleColor = Color.GRAY;
    private int solidBackgroundColor = Color.GRAY; // Color.parseColor("#2C465C");
    private int circleIconColor = Color.WHITE;
    private int textSize = (int)convertDipToPixels(8);
    private int icon = R.drawable.ic_arrow_forward_vector;
    private float[] cornerRadius =new float[]{0,0,0,0,0,0,0,0};
    private ImageView circle;
    private FrameLayout gradientLayout;
    private TextView textView;
    private float lastCircleRotation = 0;
    private int animDuration = 300;
    private Boolean changeTextWithAnim = true;

    public GradientSwitchView(Context context) {
        super(context);
        init(null);
    }

    public GradientSwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GradientSwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GradientSwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private float convertDipToPixels(float dips) {
        return (dips * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
        makeView();
    }

    private void makeView(){
        float cornerRadiusDp = convertDipToPixels(viewHeight/2);
        cornerRadius = new float[]{cornerRadiusDp,cornerRadiusDp,cornerRadiusDp,cornerRadiusDp,cornerRadiusDp,cornerRadiusDp,cornerRadiusDp,cornerRadiusDp};
        textView= new TextView(getContext());
        textView.setTextSize(textSize);
        textView.setText(beginText);
        textView.setTextColor(textColor);
//        LayoutParams textLayoutParams = new LayoutParams(tv.getMeasuredWidth(),tv.getMeasuredHeight());
        textView.setPadding(4,4,4,4);
        textView.setGravity(Gravity.CENTER);
//        tv.setLayoutParams(textLayoutParams);


        circle= new ImageView(getContext());
        LayoutParams circleLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        circleLp.height = viewHeight;
        circleLp.width = viewHeight;
        circleLp.gravity = Gravity.LEFT;
        circle.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int imageInnerPadding = (int)convertDipToPixels(8);
        circle.setPadding(imageInnerPadding,imageInnerPadding,imageInnerPadding,imageInnerPadding);
        circle.setLayoutParams(circleLp);
        circle.setBackground(getCircleDrawable(circleColor,viewHeight));
        circle.setImageResource(icon);
        circle.setColorFilter(circleIconColor);
        circle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(currentState==SwitchStates.Begin){
                    moveToEnd();
                } else {
                    moveToBegin();
                }
            }
        });

        gradientLayout = new FrameLayout(getContext());
        LayoutParams gradientLp = new LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT);
        gradientLp.gravity = Gravity.LEFT;
        gradientLayout.setBackground(getGradientDrawable());
        gradientLayout.setLayoutParams(gradientLp);

        this.addView(gradientLayout);
        this.addView(textView);
        this.addView(circle);
        this.setBackground(getSolidBackgroundDrawable(solidBackgroundColor));
    }
    private void init(AttributeSet attrs){
        fillParams(attrs);
    }

    public Boolean isSwitchOn(){
        if(currentState==SwitchStates.Begin)
            return false;
        return true;
    }

    private void fillParams(AttributeSet attrs) {
        setLayerType(LAYER_TYPE_HARDWARE, null);
        if(attrs!=null){
            //if(!isInEditMode()) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GradientSwitchView);
            animDuration = a.getInteger(R.styleable.GradientSwitchView_gsw_animDuration,animDuration);
            beginText = a.getString(R.styleable.GradientSwitchView_gsw_beginText);
            endText = a.getString(R.styleable.GradientSwitchView_gsw_endText);
            solidBackgroundColor = a.getColor(R.styleable.GradientSwitchView_gsw_solidBackgroundColor,solidBackgroundColor);
            textColor = a.getColor(R.styleable.GradientSwitchView_gsw_textColor,textColor);
            if(a.hasValue(R.styleable.GradientSwitchView_gsw_beginGradientColor))
                beginColor =  a.getColor(R.styleable.GradientSwitchView_gsw_beginGradientColor,Color.GRAY);
            if(a.hasValue(R.styleable.GradientSwitchView_gsw_middleGradientColor))
                middleColor =  a.getColor(R.styleable.GradientSwitchView_gsw_middleGradientColor,Color.GRAY);
            if(a.hasValue(R.styleable.GradientSwitchView_gsw_endGradientColor))
                endColor =  a.getColor(R.styleable.GradientSwitchView_gsw_endGradientColor,Color.GRAY);
            circleColor =  a.getColor(R.styleable.GradientSwitchView_gsw_circleColor,circleColor);
            circleIconColor =  a.getColor(R.styleable.GradientSwitchView_gsw_circleIconColor,circleIconColor);
            textSize=a.getDimensionPixelSize(R.styleable.GradientSwitchView_gsw_textSize,textSize);
            icon = a.getResourceId(R.styleable.GradientSwitchView_gsw_circleIcon, icon);
            changeTextWithAnim = a.getBoolean(R.styleable.GradientSwitchView_gsw_changeTextWithAnim, changeTextWithAnim);
//
//            mNextFocusIds[0] = a.getResourceId(R.styleable.RippleValidatorEditText_android_nextFocusDown, 0);
//            mNextFocusIds[1] = a.getResourceId(R.styleable.RippleValidatorEditText_android_nextFocusLeft, 0);
//            mNextFocusIds[2] = a.getResourceId(R.styleable.RippleValidatorEditText_android_nextFocusUp, 0);
//            mNextFocusIds[3] = a.getResourceId(R.styleable.RippleValidatorEditText_android_nextFocusRight, 0);
//            mNextFocusIds[4] = a.getResourceId(R.styleable.RippleValidatorEditText_android_nextFocusForward, 0);
//            mImeOptions = a.getInt(R.styleable.RippleValidatorEditText_android_imeOptions, 0);
//            mHelperAnimation = a.getResourceId(R.styleable.RippleValidatorEditText_rve_helperAnimation,R.anim.fade_in_slide_right);
//            mHintColor = a.getColor(R.styleable.RippleValidatorEditText_android_textColorHint, mHintColor);
//            mBackgroundColor = a.getColor(R.styleable.RippleValidatorEditText_rve_backgroundColor, mBackgroundColor);
//            mAutoValidate = a.getBoolean(R.styleable.RippleValidatorEditText_rve_validateOnFocusLost,mAutoValidate);
//            mHintText = a.getString(R.styleable.RippleValidatorEditText_rve_hint);
//            mEditTextGravity = a.getInteger(R.styleable.RippleValidatorEditText_rve_editTextGravity,mEditTextGravity);
//            mHelperTextGravity = a.getInteger(R.styleable.RippleValidatorEditText_rve_helperTextGravity,mHelperTextGravity);
//            String typeface=a.getString(R.styleable.RippleValidatorEditText_rve_font);
//            if(typeface!=null){
//                mTypeFace = Typeface.createFromAsset(getContext().getAssets(),typeface);
//            }
//            mStrokeWidth = a.getDimensionPixelOffset(R.styleable.RippleValidatorEditText_rve_strokeWidth,mStrokeWidth);
//            mEditTextSize=a.getDimensionPixelSize(R.styleable.RippleValidatorEditText_rve_editTextSize,(int) convertDipToPixels(mEditTextSize));
//            mHelperTextSize=a.getDimensionPixelSize(R.styleable.RippleValidatorEditText_rve_helperTextSize,(int)convertDipToPixels(mHelperTextSize));
//            //corner radius
//            float topRight = a.getDimension(R.styleable.RippleValidatorEditText_rve_topRightCornerRadius,0);
//            float topLeft = a.getDimension(R.styleable.RippleValidatorEditText_rve_topLeftCornerRadius,0);
//            float BottomRight = a.getDimension(R.styleable.RippleValidatorEditText_rve_bottomRightCornerRadius,0);
//            float BottomLeft = a.getDimension(R.styleable.RippleValidatorEditText_rve_bottomLeftCornerRadius,0);
//            mCornerRadius = new float[]{topLeft,topLeft,topRight,topRight,BottomRight,BottomRight,BottomLeft,BottomLeft};
//            // colors
//            mNormalColor = a.getColor(R.styleable.RippleValidatorEditText_rve_normalColor,mNormalColor);
//            mErrorColor = a.getColor(R.styleable.RippleValidatorEditText_rve_errorColor,mErrorColor);
//            mTypingColor = a.getColor(R.styleable.RippleValidatorEditText_rve_typingColor,mTypingColor);
//            mEditTextColor = a.getColor(R.styleable.RippleValidatorEditText_rve_editTextColor,mEditTextColor);
//            mValidColor = a.getColor(R.styleable.RippleValidatorEditText_rve_validColor,mValidColor);

            a.recycle();
            //}
        }
    }

    private void animateCircle(int begin,int end,AnimatorListenerAdapter listener)
    {
        ValueAnimator animator = ValueAnimator.ofInt(begin,end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int place = (int) animation.getAnimatedValue();
                circle.setX(place);
                changeGradientWidth(place+circle.getWidth());
            }
        });
        animator.addListener(listener);
        animator.setDuration(animDuration);
        animator.start();
    }

    private void moveToBegin() {
        animateCircle((int)circle.getX(),0,new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                currentState = SwitchStates.Begin;
                changeTextWithAnim(changeTextWithAnim,beginText);
            }
        });
        rotateCircle(lastCircleRotation,0);
    }

    private void rotateCircle(float beginDegree,float endDegree){
        ValueAnimator animator = ValueAnimator.ofFloat(beginDegree,endDegree);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float rotation = (float) animation.getAnimatedValue();
                Log.i("rotateCircle","rotation = "+rotation);
                circle.setRotation(rotation);
                lastCircleRotation = rotation;
            }
        });
        animator.setDuration(animDuration);
        animator.start();
    }

    private void moveToEnd() {
        animateCircle((int)circle.getX(),getWidth()-circle.getWidth(),new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                currentState = SwitchStates.End;
                changeTextWithAnim(changeTextWithAnim,endText);
            }
        });
        rotateCircle(lastCircleRotation,180);
    }

    private void changeGradientWidth(int width) {
        LayoutParams gradientLp =(FrameLayout.LayoutParams) gradientLayout.getLayoutParams();
        gradientLp.width = width;
        gradientLayout.setLayoutParams(gradientLp);
    }

    private Drawable getCircleDrawable(@ColorInt int color,int size){
        GradientDrawable shape =  new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setSize(size,size);
        shape.setColor(color);
        return shape;
    }

    private void changeTextWithAnim(Boolean showAnim,String text){
        textView.setText(text);
        if(showAnim){
            ValueAnimator anim = ValueAnimator.ofFloat(0,1);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    textView.setAlpha((float)animation.getAnimatedValue());
                }
            });
            anim.setDuration(200);
            anim.start();
        }
    }

    private Drawable getGradientDrawable(){
        int size = 0;
        if(beginColor!=null)
            size++;
        if(middleColor!=null)
            size++;
        if(endColor!=null)
            size++;
        int[] colors = new int[size];
        int index = 0;
        if(beginColor!=null) {
            colors[index] = beginColor;
            index++;
        }
        if(middleColor!=null) {
            colors[index] = middleColor;
            index++;
        }
        if(endColor!=null) {
            colors[index] = endColor;
            index++;
        }
        GradientDrawable shape =  new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        ((GradientDrawable)shape.mutate()).setCornerRadii( cornerRadius );
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setSize(getMeasuredWidth(),getMeasuredHeight());
        shape.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        return shape;
    }

    private Drawable getSolidBackgroundDrawable(@ColorInt int color){
        GradientDrawable shape =  new GradientDrawable();
        ((GradientDrawable)shape.mutate()).setCornerRadii( cornerRadius );
        shape.setColor(color);
        return shape;
    }
}
