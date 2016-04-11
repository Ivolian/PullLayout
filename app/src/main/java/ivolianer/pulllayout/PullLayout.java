package ivolianer.pulllayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


public class PullLayout extends ViewGroup {

    public PullLayout(Context context) {
        super(context);
    }

    public PullLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 总偏移量
    int offset = 0;

    View header;

    View content;

    @Override
    protected void onFinishInflate() {
        // 获取 header 和 content
        header = getChildAt(0);
        content = getChildAt(1);
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 默认处理
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        Log.e("result", "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("result", "PullLayout: " + l + " " + t + " " + r + " " + b + " ");
        layoutHeaderView();
        layoutContentView();
    }

    private void layoutHeaderView() {
        final int left = 0;
        final int top = offset - header.getMeasuredHeight();
        final int right = left + header.getMeasuredWidth();
        final int bottom = top + header.getMeasuredHeight();
        header.layout(left, top, right, bottom);
        Log.e("result", "header: " + left + " " + top + " " + right + " " + bottom + " ");
    }

    private void layoutContentView() {
        final int left = 0;
        final int top = offset;
        final int right = left + content.getMeasuredWidth();
        final int bottom = top + content.getMeasuredHeight();
        content.layout(left, top, right, bottom);
        Log.e("result", "content: " + left + " " + top + " " + right + " " + bottom + " ");
    }


    //

    float lastY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {

        // 执行动画过程，屏蔽所有事件
        if (animating) {
            return false;
        }

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 把事件分发下去，但始终消费 DOWN 事件
                super.dispatchTouchEvent(e);
                break;
            case MotionEvent.ACTION_MOVE:
                // 滑动距离
                float dy = e.getY() - lastY;
                // 阻力
                dy = dy / 2;
                // 最难的地方，谁来处理滑动事件
                if (offset > 0 || offset == 0 && dy > 0 && content.getScrollY() == 0) {
                    selfHandleMoveEvent(dy);
                } else {
                    return contentHandleMoveEvent(e);
                }
                break;
            case MotionEvent.ACTION_UP:
                // 把事件分发下去，但始终消费 UP 事件
                super.dispatchTouchEvent(e);
                if (offset > 280) {
                    doYourLoadingAnimation();
                } else {
                    clearOffset();
                }
                break;
        }
        lastY = e.getY();
        return true;
    }

    private void selfHandleMoveEvent(float dy) {
        int newOffset = (int) (offset + dy);
        newOffset = checkOffsetRange(newOffset);
        changeOffset(newOffset);
    }

    private boolean contentHandleMoveEvent(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    private void changeOffset(int offset) {
        this.offset = offset;
        // 会导致 onLayout 的调用
        requestLayout();
    }

    private int checkOffsetRange(int newOffset) {
        newOffset = Math.min(300, newOffset);
        newOffset = Math.max(0, newOffset);
        return newOffset;
    }

    //

    boolean animating = false;

    private void doYourLoadingAnimation() {
        // 缩放动画
        ValueAnimator animator = ValueAnimator.ofFloat(0.9f, 1.1f, 0.9f, 1.1f, 0.9f, 1.1f, 0.9f, 1.1f, 1);
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float scale = (Float) animator.getAnimatedValue();
                header.setScaleX(scale);
                if (1 == animator.getAnimatedFraction()) {
                    animating = false;
                    clearOffset();
                }
            }
        });
        animator.start();
        animating = true;
    }

    private void clearOffset() {
        ValueAnimator animator = ValueAnimator.ofInt(offset, 0);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int currentOffset = (Integer) animator.getAnimatedValue();
                changeOffset(currentOffset);
                if (1 == animator.getAnimatedFraction()) {
                    animating = false;
                }
            }
        });
        animator.start();
        animating = true;
    }


//    newOffset = Math.min(300, newOffset);
//    newOffset = Math.max(0,newOffset);
}
