package call.ai.com.callsecretary;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lewvan on 2017/4/8.
 */

public class SocketService extends Service {
    Handler mMainHandler;
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
                ServerSocket serverSocket = null;
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
                        final byte buffer[] = new byte[1024 * 4];
                        int temp = 0;
                        // 从InputStream当中读取客户端所发送的数据
                        while ((temp = inputStream.read(buffer)) != -1) {
                            final int finalTemp = temp;
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SocketService.this, new String(buffer, 0, finalTemp), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
}
