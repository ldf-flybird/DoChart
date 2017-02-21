package com.dqqdo.dobase;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * 所以直接继承 FragmentActivity 的类都应该继承该类。
 */
public abstract class BaseActivity extends FragmentActivity {

    private Handler mMainHandler;//主线程的 Handler。
    private Handler mWorkHandler;//工作线程的 Handler。
    HandlerThread mHandlerThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音

        setContentView(getContentView());

        initView();

        initData();

    }

    public Handler getMainHandler() {
        if (mMainHandler==null){
            mMainHandler = new Handler(getMainLooper());
        }
        return mMainHandler;
    }


    public Handler getWorkHandler() {
        if (mWorkHandler==null){
            mHandlerThread = new HandlerThread("TrainingClient");
            mHandlerThread.start();
            mWorkHandler = new Handler(mHandlerThread.getLooper());
        }
        return mWorkHandler;
    }


    /**
     * 初始化 View。
     */
    protected abstract void initView();

    /**
     * 对 View 进行数据填充。
     */
    protected abstract void initData();

    /**
     * 设置 Activity 布局文件。
     *
     * @return 布局文件。
     */
    protected abstract int getContentView();


    /**
     * 该方法用来代替 findViewById 方法。
     *
     * @param resId view Id。
     * @param <T>   范型。
     * @return View。
     */
    protected <T extends View> T getViewById(int resId) {
        return (T) this.findViewById(resId);
    }

    /**
     * 该方法用来代替 findViewById 方法。
     * @param resId view Id。
     * @param <T>   范型。
     * @return View。
     */
    protected <T extends Fragment> T getFragmentById(int resId) {
        return (T) this.getSupportFragmentManager().findFragmentById(resId);
    }

}
