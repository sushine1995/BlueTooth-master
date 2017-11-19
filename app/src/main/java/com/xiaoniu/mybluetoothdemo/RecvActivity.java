package com.xiaoniu.mybluetoothdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hi on 2017/10/21.
 */

public class RecvActivity extends Activity {
    private final int BUFFER_SIZE = 20*1024;
    private ScrollView root;
    private Context context;
    private BTApplication mApp;
    private ArrayList<LinearLayout> linearList;
    private List<Integer> mBuffer;             //缓冲区链表
    private static final int MSG_NEW_DATA = 3;
    private int[] iColor = new int[101];         //当i为0时，字体为黑色，奇数时为白色

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (BTApplication) this.getApplication();
        mBuffer = new ArrayList<Integer>();
        initView();
        new ReciveData().start();

    }

    private void initView() {
        linearList = new ArrayList<>();
        root = new ScrollView(this);
        root.setFillViewport(true);
        setContentView(root, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout content = new LinearLayout(root.getContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setBackgroundColor(Color.WHITE);
        root.addView(content);
        for (int i = 0; i < 101; i++) {
            LinearLayout line = new LinearLayout(content.getContext());
            //LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,46);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            line.setOrientation(LinearLayout.HORIZONTAL);
            linearList.add(line);
            content.addView(line, btnParams);
        }
    }

    int ilenBuf_old;
    int ilenBuf_new;
    int ifirst = 1;

    //按下返回时，仍然保留当前活动
    @Override
    public  boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode== KeyEvent.KEYCODE_BACK)
        {
            //true对所有活动生效，否则只对主活动生效
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_NEW_DATA:
                    synchronized (mBuffer) {
                        byte buf[] = new byte[82000];
                        int buf2 [] = new int[mBuffer.size()];

                        if (ifirst == 1) {
                            ilenBuf_old = 0;
                        } else if (ifirst == 0) {
                            ilenBuf_old = ilenBuf_new;
                        }
                        ilenBuf_new = mBuffer.size();
                        for (int j = ilenBuf_old; j < ilenBuf_new; j++) {
                            buf[j-ilenBuf_old] = mBuffer.get(j).byteValue();
                            buf2[j-ilenBuf_old] = mBuffer.get(j);
                        }
                        updateMajiang(buf2,buf,ilenBuf_new-ilenBuf_old);

                        if (ifirst == 0) {
                            ilenBuf_old = ilenBuf_new;
                        }
                        ifirst = 0;
                    }
                    //1113
                    mBuffer.clear();
                    ilenBuf_old=ilenBuf_new=0;
                    break;
            }
        }
    };


    private void updateMajiang(final int[] intData, final byte[] buf, final int intDataLen) {
        final int[] intarray = Arrays.copyOf(intData, intDataLen);
        runOnUiThread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {

                LinearLayout line = linearList.get(0);
                int j = 0;
                boolean check = false;
                line.removeAllViews();

                for (int i = 0; i < intDataLen; i++) {
                    LinearLayout.LayoutParams bParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    if (intarray[i] == 0xFE) {
                        j = i;
                        if (intDataLen-j<41)            //当剩余报文长度小于41时，抛弃
                            break;
                        check = cRcCheck(buf,j);
                        if (check) {
                            if (intarray[i + 1] < 101) {
                                line = linearList.get(intarray[i + 1]);
                                line.removeAllViews();
                                TextView textView = new TextView(line.getContext());

                                //textView.setMinimumWidth(100);
                                textView.setText(Integer.toString(intarray[i + 1]));
                                iColor[intarray[i+1]] = 1-iColor[intarray[i+1]];            //默认为0，刷新数据时在0和1变换，用于更改字体的颜色
                                if (iColor[intarray[i+1]] == 0)
                                    textView.setTextColor(getResources().getColor(R.color.red));
                                textView.setGravity(Gravity.CENTER);
                                //LinearLayout.LayoutParams bParamsText = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2);
                                bParams = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT,2);
                                //bParams.setMargins(0, 0, 0, 0);
                                line.addView(textView,bParams);
                                i++;
                                i++;            //从数据开始
                            }
                        }
                    }

                    //1113修改，避免出现错乱-短数据时出现数组越界
                    else if (check ) {
                        if(i < j + intarray[j+2]+3) {
                            if (intarray[j + 1] < 101) {
                                int majiangID = JudgeUtil.getImage(intarray[i]);
                                ImageView item = new ImageView(line.getContext());
                                item.setImageDrawable(getResources().getDrawable(majiangID));
                                bParams.setMargins(0, 0, 0, 0);
                                line.addView(item, bParams);
                            }
                        }
                        else if (i == j + intarray[j+2]+3)
                            check = false;
                    }
                }

            }
        });
    }
    private  boolean cRcCheck(byte[] data, int curPos){
        byte high;
        byte low;
        int temp;
        int wcrc=0xffff;
        for(int  i=curPos;i<(curPos+39);i++){
            temp=data[i];
            if(temp<0)  temp+=256;
            temp&=0xff;
            wcrc^=temp;
            for(int j =0;j<8;j++){
                if((wcrc&0x0001) ==0x0001)
                    wcrc=(wcrc>>1)^0xA001;
                else wcrc>>=1;
            }
        }

        int crcShort=(short)wcrc&0xffff;
        high = (byte)((crcShort&0xff00)>>8);
        low = (byte)(crcShort&0x00ff);
        if(data[curPos+40]==high &&data[curPos+39]==low) //比较
            return true;
        else return false;
    }

    private class ReciveData extends Thread {

        @Override
        public void run() {

            byte[] buffer = new byte [BUFFER_SIZE];
            int bytes=0;
            while (true) {
                try {
//                    while ( (bytes = mApp.conncet.inputStream.read(buffer) )!= -1) {        //当收到一组数据时，跳出while，并提交处理
//                        for (int i = 0; i < bytes; i++) {
//                            mBuffer.add(buffer[i] & 0xFF);
//                        }
//                        try {
//                            Thread.currentThread();
//                            sleep(50);//阻断0.05秒
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        if (mApp.conncet.inputStream.available() == 0)
//                            break;
//                    }
//                    mHandler.sendEmptyMessage(MSG_NEW_DATA);
                     while (true) {
//                        // Read from the InputStream
                         bytes = mApp.conncet.inputStream.read(buffer);
                         synchronized (mBuffer) {
                             for (int i = 0; i < bytes; i++) {
                                 mBuffer.add(buffer[i] & 0xFF);
                             }
                         }
                             try {
                                 Thread.currentThread();
                                 sleep(50);//阻断0.05秒
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }
                             if (mApp.conncet.inputStream.available() == 0)
                                 break;
                         }

                         mHandler.sendEmptyMessage(MSG_NEW_DATA);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

        }
    }
}
