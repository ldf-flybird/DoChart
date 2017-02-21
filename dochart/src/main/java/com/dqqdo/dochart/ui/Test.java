package com.dqqdo.dochart.ui;

/**
 * 作者：duqingquan
 * 时间：2017/2/10 16:36
 */
public class Test {

    public static void main(String args[]){

        int a = 10;
        int b = 10;

        doJob(a,b);
        System.out.println("a = " + a);
        System.out.println("b = " + b);

    }

    private static void doJob(int a,int b){

        a = 100;
        b = 200;

    }



    class Money<X>{

    }
}
