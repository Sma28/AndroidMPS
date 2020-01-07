package com.example.androidmps;

import android.graphics.Bitmap;

public class CartoonFilter {

    public Bitmap getCartoonImage(Bitmap image) {

        int radius = 10;
        float colorDistance = 20f;
        int width = image.getHeight();
        int height = image.getWidth();
        //System.out.println(width + " " + height);
        Pixel[][] pixels = new Pixel[height][width];
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                //System.out.println(i + " " + j);
                int pixel = image.getPixel(i, j);
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;

                //System.out.println(r + " " + g + " " + b);
                pixels[i][j] = new Pixel();
                pixels[i][j].red = 0.299f  *r + 0.587f * g + 0.114f  *b;
                pixels[i][j].green = 0.5957f *r - 0.2744f*g - 0.3212f *b;
                pixels[i][j].blue = 0.2114f *r - 0.5226f*g + 0.3111f *b;
            }
        }

        int iterations = 0;
        float shift = 0;

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                int yc = y;
                int xc = x;
                int xcOld, ycOld;
                float YcOld, IcOld, QcOld;
                Pixel p = pixels[x][y];
                float Yc = p.red;
                float Ic = p.green;
                float Qc = p.blue;

                do {
                    xcOld = xc;
                    ycOld = yc;
                    YcOld = Yc;
                    IcOld = Ic;
                    QcOld = Qc;
                    float mx = 0;
                    float my = 0;
                    float mY = 0;
                    float mI = 0;
                    float mQ = 0;
                    int num=0;
                    int radius2 = radius * radius;
                    float colorDistance2 = colorDistance * colorDistance;
                    for (int rx=-radius; rx <= radius; rx++) {
                        int x2 = xc + rx;
                        if (x2 >= 0 && x2 < height) {
                            for (int ry=-radius; ry <= radius; ry++) {
                                int y2 = yc + ry;
                                if (y2 >= 0 && y2 < width) {
                                    if (rx*rx + ry*ry <= radius2) {
                                        Pixel p1 = pixels[x2][y2];

                                        float Y2 = p1.red;
                                        float I2 = p1.green;
                                        float Q2 = p1.blue;

                                        float dY = Yc - Y2;
                                        float dI = Ic - I2;
                                        float dQ = Qc - Q2;

                                        if (dY*dY+dI*dI+dQ*dQ <= colorDistance2) {
                                            mx += x2;
                                            my += y2;
                                            mY += Y2;
                                            mI += I2;
                                            mQ += Q2;
                                            num++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    float num_ = 1f/num;
                    Yc = mY*num_;
                    Ic = mI*num_;
                    Qc = mQ*num_;
                    xc = (int) (mx*num_+0.5);
                    yc = (int) (my*num_+0.5);
                    int dx = xc-xcOld;
                    int dy = yc-ycOld;
                    float dY = Yc-YcOld;
                    float dI = Ic-IcOld;
                    float dQ = Qc-QcOld;

                    shift = dx*dx+dy*dy+dY*dY+dI*dI+dQ*dQ;
                    iterations++;
                }
                while (shift > 3 && iterations > 100);

                int r_ = (int)(Yc + 0.9563f*Ic + 0.6210f*Qc);
                int g_ = (int)(Yc - 0.2721f*Ic - 0.6473f*Qc);
                int b_ = (int)(Yc - 1.1070f*Ic + 1.7046f*Qc);


                int pix = image.getPixel(x,y);
                //System.out.println(r_ + " " + g_ + " " + b_);
                pix = (r_<<16) | (g_<<8) | b_;
                image.setPixel(x, y, pix);

            }
        }
        return image;
    }
}
