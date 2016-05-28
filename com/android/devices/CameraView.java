/**
 * CameraView.java
 *
 * Copyright (c) 2016 Tomohiro IKEDA (Korilakkuma)
 * Released under the MIT license
 */

package com.android.devices;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.MotionEvent;

public class CameraView extends SurfaceView {

    private Camera camera = null;

    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setup();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setup();
    }

    public CameraView(Context context) {
        super(context);
        this.setup();
    }

    private void setup() {
        SurfaceHolder holder = this.getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                CameraView.this.camera = Camera.open(0);

                try {
                    CameraView.this.camera.setPreviewDisplay(holder);
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                CameraView.this.camera.stopPreview();

                Parameters params = CameraView.this.camera.getParameters();

                // Size size = params.getSupportedPreviewSizes().get(0);
                // params.setPreviewSize(size.width, size.height);

                params.setPreviewSize(width, height);

                CameraView.this.camera.setParameters(params);

                CameraView.this.camera.startPreview();
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                CameraView.this.camera.release();
                CameraView.this.camera = null;
            }

        });

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.camera.takePicture(
                    new ShutterCallback() {
                        @Override
                        public void onShutter() {

                        }
                    },
                    null,
                    new PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            try {
                                FileOutputStream fos = new FileOutputStream("/mnt/sdcard/sample.jpg");

                                fos.write(data);
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            camera.startPreview();
                        }
                    }
            );
        }

        return true;
    }
}
