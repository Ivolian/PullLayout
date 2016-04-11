package ivolianer.pulllayout;

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
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                // 滑动距离
                float dy = e.getY() - lastY;
                // 新的偏移量
                int newOffset = (int) (offset + dy);
                changeOffset(newOffset);
                break;
            case MotionEvent.ACTION_UP:
                changeOffset(0);
                break;
        }
        lastY = e.getY();
        return true;
    }

    private void changeOffset(int offset) {
        this.offset = offset;
        // 会导致 onLayout 的调用
        requestLayout();
    }


//    newOffset = Math.min(300, newOffset);
//    newOffset = Math.max(0,newOffset);
}
