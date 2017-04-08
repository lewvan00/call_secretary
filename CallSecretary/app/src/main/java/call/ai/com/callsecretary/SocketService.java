package call.ai.com.callsecretary;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.services.lexrts.model.PostContentResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import call.ai.com.callsecretary.lex.InteractiveVoiceUtils;
import lex.SerializablePostContentResult;


/**
 * Created by lewvan on 2017/4/8.
 */

public class SocketService extends Service {
    Handler mMainHandler;
    ServerSocket serverSocket = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //启动socket
        mMainHandler = new Handler();
        startSocketService();
    }

    public void startSocketService() {
        new Thread() {
            @Override
            public void run() {
                // 声明一个ServerSocket对象
                try {
                    // 创建一个ServerSocket对象，并让这个Socket在1989端口监听
                    serverSocket = new ServerSocket(1989);
                    // 调用ServerSocket的accept()方法，接受客户端所发送的请求，
                    // 如果客户端没有发送数据，那么该线程就停滞不继续
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SocketService.this, "socket listening", Toast.LENGTH_SHORT).show();
                        }
                    });
                    while (true) {
                        Socket socket = serverSocket.accept();
                        // 从Socket当中得到InputStream对象
                        InputStream inputStream = socket.getInputStream();
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        try {
                            SerializablePostContentResult contentResult = (SerializablePostContentResult) objectInputStream.readObject();
                            Log.d("liufan", "receive result = " + contentResult);
                            if (contentResult != null && contentResult.getState() == SerializablePostContentResult.STATE_RESPONSE) {
                                //get audio stream;
                                Log.d("liufan", "receive audio result = " + contentResult);
                                PostContentResult postContentResult = new PostContentResult();
                                postContentResult.setAudioStream(inputStream);
                                postContentResult.setMessage(contentResult.getMessage());
                                postContentResult.setInputTranscript(contentResult.getInputTranscript());
                                InteractiveVoiceUtils.getInstance().processAudioResponse(postContentResult);

                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            Log.d("liufan", "receive result = ClassNotFoundException ---- " + e);
                        }
//                        final byte buffer[] = new byte[1024 * 4];
//                        int temp = 0;
//                        // 从InputStream当中读取客户端所发送的数据
//                        while ((temp = inputStream.read(buffer)) != -1) {
//                            final int finalTemp = temp;
//                            mMainHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(SocketService.this, new String(buffer, 0, finalTemp), Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("liufan", "socket service exception : " + e);
                } finally {
                    try {
                        if (serverSocket != null) {
                            serverSocket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
