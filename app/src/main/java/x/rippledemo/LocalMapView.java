package x.rippledemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;

public class LocalMapView extends View {

    private final static Paint mPaint = new Paint();

    private int mViewWidth = 0;

    private int mViewHeight = 0;


    private int mCenterX = 0;

    private int mCenterY = 0;


    public LocalMapView(Context context) {
        this(context, null, 0);
    }

    public LocalMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initializeMapView();
    }


    public void initializeMapView() {
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas canvas) {

        mViewWidth = getWidth();
        mViewHeight = getHeight();

        mCenterX = mViewWidth / 2;
        mCenterY = mViewHeight / 2;
        /**
         * 正在请求数据动画
         */
        mRippleController.drawRipples(canvas);
    }

    /**
     * 启动正在请求数据的动画
     */
    public void startQueryingAnimation() {
        mRippleController.startRipple();
    }

    public void stopQueryingAnimation(boolean force) {
        mRippleController.stopRipple(force);
    }


    private RippleAnimController mRippleController = new RippleAnimController();

    private class RippleAnimController {

        private boolean mIsRunning = false;
        private Handler mHandler = new Handler();

        private Paint mRipplePaint = new Paint();

        private ValueAnimator mRadiusAnimator = null;

        private float mDestFloatValue = 0;
        private final ArrayList<Float> mValueQues = new ArrayList<>();

        private final static int RIPPLE_DURATION = 300;
        private final static int RIPPLE_TIME = 1500;
        private Runnable mRippleRunnable = new Runnable() {
            @Override
            public void run() {
                mRadiusAnimator.clone().start();

                if (mIsRunning) {
                    mHandler.postDelayed(this, RIPPLE_DURATION);
                }
            }
        };

        private void startRipple() {
            mForceStop = false;
            mIsRunning = true;
            mHandler.removeCallbacks(mRippleRunnable);
            mRipplePaint.setColor(Color.WHITE);

            post(new Runnable() {
                @Override
                public void run() {
                    mDestFloatValue = Math.min(getWidth(), getHeight()) / 2f;
                    mRadiusAnimator = ValueAnimator.ofFloat((35), mDestFloatValue);
                    mRadiusAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    mRadiusAnimator.setDuration(RIPPLE_TIME);
                    mRadiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            if (mForceStop) {
                                valueAnimator.cancel();
                                return;
                            }
                            synchronized (mValueQues) {
                                float value = (float) valueAnimator.getAnimatedValue();
                                mValueQues.add(value);
                            }

                            postInvalidate();
                        }
                    });

                    mRadiusAnimator.start();
                    mHandler.postDelayed(mRippleRunnable, RIPPLE_DURATION);

                }
            });

            invalidate();
        }

        private boolean mForceStop = false;

        private void stopRipple(boolean force) {
            mForceStop = force;
            if (mForceStop) {
                mValueQues.clear();
            }

            mIsRunning = false;
            mHandler.removeCallbacks(mRippleRunnable);
            invalidate();
        }

        /**
         * 画波纹
         */
        private synchronized void drawRipples(final Canvas canvas) {
            if (mForceStop) {
                return;
            }

            synchronized (mValueQues) {
                for (float value : mValueQues) {

                    mRipplePaint.setAlpha((int) ((1 - value / mDestFloatValue) * 120));
                    mRipplePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    mRipplePaint.setAntiAlias(true);
                    mRipplePaint.setStrokeWidth(2);
                    canvas.drawCircle(mCenterX, mCenterY, value, mRipplePaint);
                }

                mValueQues.clear();
            }

        }
    }

}
