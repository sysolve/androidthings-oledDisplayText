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

package com.sysolve.androidthings.oledscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.contrib.driver.ssd1306.BitmapHelper;
import com.google.android.things.contrib.driver.ssd1306.Ssd1306;
import com.sysolve.androidthings.utils.BoardSpec;

import java.io.IOException;

/**
 * Activity that tests the Ssd1306 display.
 */
public class OledScreen {
    private static final String TAG = "OledScreen";
    private Ssd1306 mScreen;

    Context context;

    public OledScreen(Context context) {
        this.context = context;
        try {
            mScreen = new Ssd1306(BoardSpec.getI2cBus());
        } catch (IOException e) {
            Log.e(TAG, "Error while opening screen", e);
            throw new RuntimeException(e);
        }
        Log.d(TAG, "OLED screen activity created");
    }

    public void close() {
        // Close the device.
        try {
            mScreen.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing SSD1306", e);
        } finally {
            mScreen = null;
        }
    }

    public void clearPixels() {
        mScreen.clearPixels();
        printX = 0;
        printY = 0;
        lineHeight = 0;
    }

    int printX;
    int printY;
    int lineHeight;

    public void printLn() {
        printX = 0;
        printY += lineHeight;
        lineHeight = 0;
    }

    public void printMatrix(byte[][] matrix) {
        int h = matrix.length;
        int w = 0;
        if (matrix.length>0) w = matrix[0].length;
        if (printX+w > mScreen.getLcdWidth()) {
            //需要换行
            printLn();
        }
        if (printY+h > mScreen.getLcdHeight()) {
            //需要清屏
            try {
                //先显示出来，等待1秒后清屏
                mScreen.show();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clearPixels();
        }
        drawMatrix(matrix, printX, printY);
        printX += w;
        lineHeight = Math.max(lineHeight, h);
    }

    public void drawMatrix(byte[][] matrix, int x, int y) {
         for (int i=0;i<matrix.length;++i) {
            byte[] b = matrix[i];
            for (int j=0;j<b.length;++j) {
                int xx = x+j;
                int yy = y+i;
                if (xx<mScreen.getLcdWidth() && yy<mScreen.getLcdHeight())
                    mScreen.setPixel(xx, yy, b[j] == 1);
            }
        }
    }

    public void drawDigital3x5(int d, int x, int y) {
        byte[][] digitalDotMatrix = Digital3x5.DIGITALS[d];
        for (int i=0;i<digitalDotMatrix.length;++i) {
            byte[] b = digitalDotMatrix[i];
            for (int j=0;j<b.length;++j) {
                int xx = x+j;
                int yy = y+i;
                if (xx<mScreen.getLcdWidth() && yy<mScreen.getLcdHeight())
                    mScreen.setPixel(xx, yy, b[j] == 1);
            }
        }
    }

    public void drawDigital3x5(int d, int x, int y, int scale) {
        byte[][] digitalDotMatrix = Digital3x5.DIGITALS[d];
        for (int i=0;i<digitalDotMatrix.length;++i) {
            byte[] b = digitalDotMatrix[i];
            for (int j=0;j<b.length;++j) {
                for (int k=0;k<scale;++k) {
                    for (int l=0;l<scale;++l) {
                        int xx = x + j * scale + k;
                        int yy = y + i * scale + l;
                        if (xx<mScreen.getLcdWidth() && yy<mScreen.getLcdHeight())
                            mScreen.setPixel(xx, yy, b[j] == 1);
                    }
                }
            }
        }
    }

    /**
     * Draws a BMP in one of three positions.
     */
    public void drawBitmap(Bitmap bitmap, int xOffset, int yOffset) {
        drawBitmap(bitmap, xOffset, yOffset,false);
    }

    public void drawBitmap(Bitmap bitmap, int xOffset, int yOffset, boolean drawWhite) {
        BitmapHelper.setBmpData(mScreen, xOffset, yOffset, bitmap, drawWhite);
    }

    public void show() {
        try {
            mScreen.show();
        } catch (IOException e) {
            Log.e(TAG, "Error while show on screen", e);
            throw new RuntimeException(e);
        }
    }


}
