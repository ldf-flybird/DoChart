package com.dqqdo.dochart.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.dqqdo.dobase.DoToast;
import com.dqqdo.dochart.data.ChartValueBean;

import java.util.ArrayList;

/**
 * 绘制饼状图的自定义组件
 * 作者：duqingquan
 * 时间：2017/2/14 14:36
 */
public class PieChartView extends View implements View.OnTouchListener{

    // 绘图使用的画笔对象
    Paint mPaint;
    // 当前组件宽高.
    private int width, height;

    private RectF bigRect = new RectF();

    // 主绘制区域的边距控制
    private float left, top, right, bottom;
    // 圆心坐标
    private float[] center = new float[2];
    // 每次绘制圆 的绘制进度
    private float mSweep = 0;

    // 绘制单元下标
    private int index = 0;
    // 每次绘制圆的 开始弧度数值
    private float startPercent = 0;
    // 当前正在绘制单元百分比，如果动画已经绘制完完成，则为0.
    private float nowUnitPercent = 0;

    // 每个绘制单元的百分比
    private ArrayList<Float> percents = new ArrayList<>();
    // 每个绘制单元的颜色
    private ArrayList<Integer> colors = new ArrayList<>();

    // 单元下标最大值
    private int maxIndex;

    // 近圆点坐标
    private float[][] cicleLineF ;
    // 近圆点转折处坐标
    private float[][] cicleLineT ;
    // 分割线切点坐标
    private float[][] cicleLineStart ;

    /**
     * 文字底部  靠近圆环 短线的 长度
     */
    private float textLineByCircleW = 60;

    /**
     * 绘制动画，每次绘制的圆环的弧度长
     */
    private static final float SWEEP_INC = 5;


    private int downIndex = -1;

    private int upIndex = -1;



    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int eventType = event.getAction();

        switch (eventType){
            case MotionEvent.ACTION_DOWN:

                downIndex = getCoorPos(event.getX(),event.getY());

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                upIndex = getCoorPos(event.getX(),event.getY());

                if (downIndex == upIndex && upIndex >= 0){
                    // 给出提示
                    String name = beans.get(upIndex).getName();
                    DoToast.shortToast(getContext(),"name === " + name);

                }


                downIndex = -1;

                break;
        }

        return true;
    }


    /**
     * 获取坐标对应的单元下标
     * @return
     */
    private int getCoorPos(float x,float y){

        // 第一步，判断当前点是否位于圆内
        double absX = (int) Math.abs((center[0] - x));
        double absY = (int) Math.abs((center[1] - y));

        double result =  Math.hypot(absX,absY);
        if(result > fr){
            // 大于半径，在圆外不作处理
            return -1;
        }

        // 小于半径，在圆内部，进一步判断角度
        double num = 0;
        double degrees = 0;
        int quadrant = quadrant(x,y);
        if(quadrant == 1){
            num = Math.atan2(absY,absX);
            degrees =  Math.toDegrees(num);
        }else if(quadrant == 2){
            num = Math.atan2(absX,absY);
            degrees = 90 + Math.toDegrees(num);
        }else if(quadrant == 3){
            num = Math.atan2(absX,absY);
            degrees = 270 -  Math.toDegrees(num);
        }else if(quadrant == 4){
            num = Math.atan2(absY,absX);
            degrees = 360 -  Math.toDegrees(num);
        }



        float totalDegrees = 0;
        for(int i = 0;i < percents.size();i++){
            float nowDegrees = percents.get(i);
            totalDegrees += nowDegrees;
            if(degrees <= totalDegrees){
               return i;
            }
        }

        return -1;

    }

    /**
     * 返回象限 这里返回的是几何象限，不是数学象限
     * @param x 坐标x
     * @param y 坐标y
     * @return 象限名称
     */
    private int quadrant(float x,float y){

        if(x > center[0]){
            if(y > center[1]){
                return 1;
            }else{
                return 4;
            }
        }else{
            if(y > center[1]){
                return 2;
            }else{
                return 3;
            }
        }

    }




    // 对应的数据实体类
    private ArrayList<ChartValueBean> beans = new ArrayList<>();


    private void init() {

        // 初始化组件相关
        initView();
        // 初始化绘制相关
        initPaint();
        // 初始化数据相关
        initData();

    }

    /**
     * 初始化组件相关
     */
    private void initView(){
        this.setOnTouchListener(this);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
    }


    private void initData() {

        // 模拟假数据
        ChartValueBean ChartValueBean1 = new ChartValueBean();
        ChartValueBean1.setName("百度贴吧");
        ChartValueBean1.setValue(35);
        beans.add(ChartValueBean1);


        ChartValueBean ChartValueBean2 = new ChartValueBean();
        ChartValueBean2.setName("QQ空间");
        ChartValueBean2.setValue(166);
        beans.add(ChartValueBean2);


        ChartValueBean ChartValueBean3 = new ChartValueBean();
        ChartValueBean3.setName("新浪微博");
        ChartValueBean3.setValue(115);
        beans.add(ChartValueBean3);


        ChartValueBean ChartValueBean4 = new ChartValueBean();
        ChartValueBean4.setName("微信");
        ChartValueBean4.setValue(135);
        beans.add(ChartValueBean4);


        ChartValueBean ChartValueBean5 = new ChartValueBean();
        ChartValueBean5.setName("FaceBook");
        ChartValueBean5.setValue(155);
        beans.add(ChartValueBean5);


        calcPercent();

        cicleLineF = new float[percents.size()][2];
        cicleLineT = new float[percents.size()][2];
        cicleLineStart  = new float[percents.size()][2];


    }





    /**
     * 计算各元素百分比
     */
    private void calcPercent() {

        double total = 0;
        for (ChartValueBean ChartValueBean : beans) {
            total += ChartValueBean.getValue();
        }

        float availableDegree = 360;


        for (ChartValueBean ChartValueBean : beans) {
            double perValue = ChartValueBean.getValue();
            percents.add((float) ((perValue * availableDegree / total)));
        }

        maxIndex = percents.size();

        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.GRAY);
        colors.add(Color.GREEN);


        mPaintLine = new Paint();
        mPaintLine.setStyle(Paint.Style.FILL);
        mPaintLine.setStrokeWidth(5);
        mPaintLine.setColor(Color.WHITE);
        mPaintLine.setTextAlign(Paint.Align.CENTER);
        mPaintLine.setTextSize(30);


    }

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private float pr = 0f;
    private float fr = 0f;
    private float tr = 0f;





    @Override
    public void onDraw(Canvas canvas) {

        // 准备数据
        width = getWidth();
        height = getHeight();

        mPaint.setXfermode(null);


        center[0] = width / 2;
        center[1] = height / 2;

        fr = width / 4.5f;
        left = center[0] - fr;
        top = center[1] - fr;
        right = center[0] + fr;
        bottom = center[1] + fr;

        pr = (right - left)/2;

        bigRect.set(left, top, right, bottom);


        // 判断是否当前单元下标位置是否有已经绘制过的区域
        float circle = 0;

        int percentNum = percents.size();


        for (int i = 0; i < percentNum; i++) {

            // 计算相关数据
            if(index < percentNum){


                nowUnitPercent = percents.get(i);
                circle += nowUnitPercent / 2;

                // 绘制单元内部圆半径
                cicleLineF[i][0] = center[0] + (float)(fr * Math.cos(circle * Math.PI/180));
                cicleLineF[i][1] = center[1] + (float)(fr * Math.sin(circle * Math.PI/180));

                // 虚拟外圈圆半径
                tr = pr + textLineByCircleW;
                cicleLineT[i][0] = center[0] + (float) (tr * Math.cos(circle * Math.PI/180));
                cicleLineT[i][1] = center[1] + (float) (tr * Math.sin(circle * Math.PI/180));

                // 刻度转回起点度数
                circle -= nowUnitPercent/2;
                circle += nowUnitPercent;

                // 计算分割线切点
                cicleLineStart[i][0] = center[0] + (float)(fr * Math.cos(circle * Math.PI/180));
                cicleLineStart[i][1] = center[1] + (float)(fr * Math.sin(circle * Math.PI/180));


            }else{
                nowUnitPercent = 0;
            }


            // 进行绘制
            if (i < index) {

                // 已经绘制过的部分
                float postPercent = percents.get(i);

                // 已经超过当前绘制进度，则绘制整体单元，并继续绘制下一个单元动画
                mPaint.setColor(colors.get(i));
                canvas.drawArc(bigRect, startPercent, postPercent, true, mPaint);

                startPercent += postPercent;

                // 绘制完成，绘制描述信息
                drawLine(canvas,i);



            } else {

                mPaint.setColor(colors.get(index));

                if (mSweep <= (nowUnitPercent)) {

                    // 尚未绘制完毕当前单元，则继续按照进度绘制动画
                    canvas.drawArc(bigRect, startPercent, mSweep, true, mPaint);
                    mSweep += 1;

                }else{
                    break;
                }
            }

        }


        int leftDownEffect = 0;
        int rightDownEffect = 0;

        if(downIndex == 0){
            leftDownEffect = beans.size() - 1;
            rightDownEffect = 0;
        }else{
            leftDownEffect = downIndex - 1;
            rightDownEffect = downIndex;
        }



        // 绘制分割线，因为要绘制在最上层，所有要在每帧最后绘制
        for (int i = 0; i < percentNum; i++) {
            // 计算相关数据
            if(i < index){

                if(index == -1){
                    drawSplitLine(canvas,cicleLineStart[i][0],cicleLineStart[i][1],-1);
                }else{

                    if(i == leftDownEffect || i == rightDownEffect){
                        // 增加点击效果
                        drawSplitLine(canvas,cicleLineStart[i][0],cicleLineStart[i][1],colors.get(downIndex));
                    }else{
                        drawSplitLine(canvas,cicleLineStart[i][0],cicleLineStart[i][1],-1);
                    }

                }




            }
        }




        // 调整数据，并判断数据有效性
        if (mSweep > nowUnitPercent) {
            // 绘制单元下标增加
            // 已经超过了限制
            if(index >= maxIndex){
                index = maxIndex;
            }else{
                index += 1;
            }
            // 单元内部绘制动画角度重置
            mSweep = 1;

        }

        startPercent = 0;

        mPaint.setColor(Color.WHITE);
        // 绘制圆心
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(center[0],center[1],80,mPaint);


        // 通知界面更新
        invalidate();


    }

    /**
     * 绘制单元之间的分割线
     * @param canvas
     * @param stopX
     * @param stopY
     */
    private void drawSplitLine(Canvas canvas,float stopX,float stopY,int writeColor){


        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(20);

        if(writeColor == -1){
            linePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }else{
            linePaint.setColor(writeColor);
        }


        canvas.drawLine(center[0],center[1],stopX,stopY,linePaint);

    }





    /**
     * 带圆点线上  圆点的半径
     */
    private float textLineCircleR = 6f;
    private Paint mPaintLine;

    /**
     * 文字底部  带圆点线的  长度
     */
    private float textLineWidth = 170;

    private void drawLine(Canvas canvas,int index){

        float fx = cicleLineF[index][0];
        float fy = cicleLineF[index][1];
        float tx = cicleLineT[index][0];
        float ty = cicleLineT[index][1];


        if(percents.get(index) == 0){
            return;
        }

        canvas.drawCircle(fx, fy, textLineCircleR, mPaintLine);
        canvas.drawLine(fx,fy,tx,ty,mPaintLine);
        float temp;
        if(tx > center[0]){
            temp = textLineWidth;
        }else{
            temp = -textLineWidth;
        }
        canvas.drawLine(tx,ty,tx+temp,ty, mPaintLine);
        canvas.drawCircle(tx + temp, ty, textLineCircleR, mPaintLine);
        // 绘制单元描述文字
        ChartValueBean nowBean = beans.get(index);
        canvas.drawText(nowBean.getName() + "-" +nowBean.getValue(),tx + temp / 2,ty - 20,mPaintLine);

    }



}
