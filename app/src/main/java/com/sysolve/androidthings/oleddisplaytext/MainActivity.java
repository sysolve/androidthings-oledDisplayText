/*
 * @author Ray, ray@sysolve.com
 * Copyright 2018, Sysolve IoT Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sysolve.androidthings.oleddisplaytext;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;

import com.sysolve.androidthings.oledscreen.Font16;
import com.sysolve.androidthings.oledscreen.OledScreen;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    OledScreen oledScreen;
    Font16 font16;

    Handler handler = new Handler();

    public String getIP(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address))
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oledScreen = new OledScreen(this);

        oledScreen.clearPixels();
        oledScreen.show();

        font16 = new Font16(MainActivity.this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //显示数字（使用一个自定义的3x5的数字点阵）
                oledScreen.clearPixels();
                displayScaledDigitals();
                oledScreen.show();

                sleep(1000);

                //显示图片
                oledScreen.clearPixels();
                oledScreen.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.flower), 0, 0, true); //只显示白色的像素
                oledScreen.show();

                sleep(1000);

                //显示图片（只显示白色/判断Alpha通道值）
                oledScreen.clearPixels();
                oledScreen.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.android), 0, 0, true);   //只显示白色的像素
                oledScreen.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.android), 64, 0, false);   //判断Alpha通道 >GRADIENT_CUTOFF(170)显示
                oledScreen.show();

                sleep(1000);

                //显示一个中文字
                byte[][] b = font16.resolveString("我");
                oledScreen.clearPixels();
                oledScreen.drawMatrix(b, 0, 0);
                oledScreen.show();

                sleep(1000);

                //显示文本信息
                oledScreen.clearPixels();
                printText("Android Things使用OLED显示文字的例子");
                sleep(2000);

                printText("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                printText("!@#$%^&*():;");
                printText("abcdefghijklmnopqrstuvwxyz\n");

                String ip = getIP();
                if (ip!=null)
                    printText(ip);
                else
                    printText("没有获得IP地址");
                oledScreen.printLn();
                sleep(1000);

                printText("Android物联网\n请关注\n智能产品设计开发\n微信公众号");
            }
        }, 100);
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printText(String text) {
        for (int i=0;i<text.length();++i) {
            //依次获取字符串中的字符
            String t = text.substring(i, i+1);
            if ("\n".equals(t))
                oledScreen.printLn();   //换行
            else
                oledScreen.printMatrix(font16.resolveString(t));    //获取字符并打印
        }
        oledScreen.show();
    }

    public void displayScaledDigitals() {
        for (int i=0;i<10;++i) {
            oledScreen.drawDigital3x5(i, i * 4, 0);
        }

        for (int i=0;i<10;++i) {
            oledScreen.drawDigital3x5(i, i * 4 * 2, 6, 2);
        }

        for (int i=0;i<10;++i) {
            oledScreen.drawDigital3x5(i, i * 4 * 3, 17, 3);
        }

        for (int i=0;i<10;++i) {
            oledScreen.drawDigital3x5(i, i * 4 * 4, 33, 4);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        oledScreen.close();
    }

}
