package com.dqqdo.dochart.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.dqqdo.dobase.DoLog;
import com.dqqdo.dobase.DoToast;
import com.dqqdo.dochart.data.ChartValueBean;

import java.util.ArrayList;

/**
 * 柱状图定义组件
 * 作者：duqingquan
 * 时间：2017/2/20 15:27
 */
public class BarChartView extends View implements View.OnTouchListener{

    // 绘图使用的画笔对象
    Paint mPaint;

    // 当前组件宽高.
    private int width, height;

    // 主绘制区域的边距控制
    private float left, top, right, bottom;
    // 圆心坐标
    private float[] center = new float[2];


    private ArrayList<Float> percents = new ArrayList<>();

    private ArrayList<RectF> rectFs = new ArrayList<>();


    // 每个绘制单元的颜色
    private ArrayList<Integer> colors = new ArrayList<>();

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {

        // 初始化组件相关
        initView();
        // 初始化绘制相关
        initPaint();
        // 初始化数据相关
        initData();

    }

    // 对应的数据实体类
    private ArrayList<ChartValueBean> beans = new ArrayList<>();

    private int dataNum = 0;
    private double maxDataValue = 0;

    private void initData() {

        // 模拟假数据
        ChartValueBean valueBean1 = new ChartValueBean();
        valueBean1.setName("百度贴吧");
        valueBean1.setValue(35);
        beans.add(valueBean1);


        ChartValueBean valueBean2 = new ChartValueBean();
        valueBean2.setName("QQ空间");
        valueBean2.setValue(166);
        beans.add(valueBean2);


        ChartValueBean valueBean3 = new ChartValueBean();
        valueBean3.setName("新浪微博");
        valueBean3.setValue(115);
        beans.add(valueBean3);


        ChartValueBean valueBean4 = new ChartValueBean();
        valueBean4.setName("微信");
        valueBean4.setValue(135);
        beans.add(valueBean4);


        ChartValueBean valueBean5 = new ChartValueBean();
        valueBean5.setName("FaceBook");
        valueBean5.setValue(155);
        beans.add(valueBean5);


        dataNum = beans.size();


        for (ChartValueBean bean: beans) {
            double beanValue = bean.getValue();
            if(beanValue > maxDataValue){
                maxDataValue = beanValue;
            }
        }

        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.GRAY);
        colors.add(Color.GREEN);




    }





    /**
     * 初始化组件相关
     */
    private void initView(){
        this.setOnTouchListener(this);
    }

    private Paint textPaint;

    private void initPaint() {

        mPaint = new Paint();
        mPaint.setColor(Color.RED);

        textPaint = new Paint();
        textPaint.setTextSize(26);
        textPaint.setColor(Color.GREEN);

        pathEffect = new DashPathEffect(new float[]{1,5},1.0f);

    }

    // 点击时的单元下标
    private int downIndex = -1;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:

                float eventX = event.getX();
                float eventY = event.getY();

                int rectNum = rectFs.size();
                for(int i = 0;i < rectNum;i++){
                    RectF rectF = rectFs.get(i);
                    if(rectF.contains(eventX,eventY)){
                        downIndex = i;
                        break;
                    }
                }

                break;


            case MotionEvent.ACTION_UP:

                if(downIndex > -1){
                    String name = beans.get(downIndex).getName();
                    DoToast.shortToast(this.getContext(),name);
                    downIndex = -1;
                }

                break;

        }


        return true;
    }

    // 绘图有效区域半径
    private float halfContentWidth = 0f;
    // 有效区域宽度
    private float contentWidth = 0f;
    // 有效区域高度
    private float contentHeight = 0f;
    // 绘制的有效区域 矩形对象
    private RectF bigRect = new RectF();

    // 每个单元之间的间隔
    private float perUnitSpace = 100;
    // 每个单元的宽度
    private float perUnitWidth = 50;
    // 步长
    private float perNumHeight = 3;

    // 当前刻度
    private float currentMark = -1;
    // 最大的展示刻度
    private float maxContentMark;

    // 每单位代表的数值
    private float perUnitYValue = 0;
    // 每个单元代表的高度
    private float perUnitYHeight = 0;
    // 数值单元数量
    private final int yLineNum = 5;


    @Override
    protected void onDraw(Canvas canvas) {

        // 准备数据
        width = getWidth();
        height = getHeight();

        mPaint.reset();
        mPaint.setColor(Color.BLACK);

        center[0] = width / 2;
        center[1] = height / 2;

        halfContentWidth = width / 3;
        left = center[0] - halfContentWidth;
        top = center[1] - halfContentWidth;
        right = center[0] + halfContentWidth;
        bottom = center[1] + halfContentWidth;

        contentWidth = halfContentWidth * 2;
        contentHeight = contentWidth;
        maxContentMark = (float) (contentHeight * 0.8);

        bigRect.set(left, top, right, bottom);

        float perPixelValue = (float) (maxDataValue / maxContentMark);
        perUnitYHeight = contentHeight / 5;
        perUnitYValue = perUnitYHeight * perPixelValue;


        // 计算步长，宽度等信息
        perUnitWidth = contentWidth / (dataNum * 2 + 1);
        perUnitSpace = perUnitWidth;

        perNumHeight = (float) (maxContentMark / maxDataValue);

        if(currentMark == -1){
            currentMark = maxContentMark;
        }

        // 绘制背景
        canvas.drawRect(bigRect,mPaint);

        // 绘制坐标轴
        drawAxis(canvas);

        mPaint.reset();

        rectFs.clear();
        int dataSize = beans.size();


        for(int i = 0; i < dataSize ;i++){


            RectF rectF = new RectF();

            ChartValueBean valueBean = beans.get(i);
            double beanValue = valueBean.getValue();

            float valueTop;
            float valueLeft;
            float valueRight;
            float valueBottom;

            if(i == downIndex){


                valueTop = (float) (top + contentHeight - beanValue * perNumHeight + currentMark);

                valueTop -= 10;
                valueLeft = left + (perUnitSpace + perUnitWidth) * i + perUnitSpace;
                valueLeft -= 5;
                valueRight = valueLeft + perUnitWidth;
                valueRight += 10;
                valueBottom = bottom;

                rectF.set(valueLeft,valueTop ,valueRight ,valueBottom);

            }else {

                valueTop = (float) (top + contentHeight - beanValue * perNumHeight  + currentMark);
                valueLeft = left + (perUnitSpace + perUnitWidth) * i + perUnitSpace;
                valueRight = valueLeft + perUnitWidth;
                valueBottom = bottom;

                rectF.set(valueLeft,valueTop,valueRight ,valueBottom);

            }

            rectFs.add(rectF);

            int unitColor = colors.get(i);
            mPaint.setColor(unitColor);
            canvas.drawRect(rectF,mPaint);

            if(currentMark == 0){
                // 绘制描述文字
                drawDescText(canvas,valueBean,valueLeft,valueTop);
            }


        }

        // 播放动画的步长
        currentMark -= 3;
        // 动画刻度的极限控制
        if(currentMark < 0){
            currentMark = 0;
        }


        // 更新界面
        invalidate();

    }


    PathEffect pathEffect;



    /**
     * 绘制坐标轴
     * @param canvas 画板
     */
    private void drawAxis(Canvas canvas){

        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(10);

        canvas.drawLine(left,bottom,left,top - 50,mPaint);
        canvas.drawLine(left,bottom,right + 50,bottom,mPaint);

        mPaint.setTextSize(25);
        mPaint.setTextSkewX((float) -0.5);
        mPaint.setColor(Color.WHITE);

        for(int i = 0;i < dataNum;i++){
            String xDesc = beans.get(i).getName();
            float textLength = mPaint.measureText(xDesc);
            float unitDescX = left + (i + 1) * (perUnitSpace + perUnitWidth) -  perUnitWidth / 2 - textLength / 2;
            canvas.drawText(xDesc,unitDescX,bottom + 50,mPaint);
        }

        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(5);
        mPaint.setPathEffect(pathEffect);
        mPaint.setStyle(Paint.Style.STROKE);

        for(int i = 1; i <= yLineNum;i++){
            DoLog.d("perUnitYHeight  ===  " + perUnitYHeight);
            float lineY = bottom - i * perUnitYHeight;

//            canvas.drawLine(left,lineY,right,lineY,mPaint);
            Path linePath = new Path();
            linePath.moveTo(left,lineY);
            linePath.lineTo(right,lineY);
            //linePath.close();
            canvas.drawPath(linePath,mPaint);

            float descValue = i * perUnitYValue;
            String descValueStr = descValue + "(单位)";
            float textLength = textPaint.measureText(descValueStr);
            canvas.drawText(descValueStr,left - textLength,lineY,textPaint);

        }



    }


    /**
     * 绘制单元描述文字的函数实现
     * @param canvas
     * @param valueBean
     * @param valueLeft
     * @param valueTop
     */
    private void drawDescText(Canvas canvas,ChartValueBean valueBean,float valueLeft,float valueTop){

        String desc = valueBean.getName() + valueBean.getValue();
        float textX = valueLeft;
        float descTextLength = textPaint.measureText(desc);
        textX -= descTextLength / 2;
        textX += perUnitWidth / 2;

        float textY = valueTop - 30;

        canvas.drawText(desc,textX,textY,textPaint);
    }

}
