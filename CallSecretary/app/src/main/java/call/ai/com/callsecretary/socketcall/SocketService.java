package call.ai.com.callsecretary.socketcall;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.services.lexrts.model.PostContentResult;

import java.io.ByteArrayInputStream;
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
                try {
                    serverSocket = new ServerSocket(1989);
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SocketService.this, "socket listening", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Socket socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    while (true) {
                        try {
                            SerializablePostContentResult contentResult = (SerializablePostContentResult) objectInputStream.readObject();
                            if (contentResult != null && contentResult.getState() == SerializablePostContentResult.STATE_RESPONSE) {
                                //get audio stream;
                                Log.d("liufan", "receive audio result = " + contentResult);
                                PostContentResult postContentResult = new PostContentResult();
                                postContentResult.setAudioStream(new ByteArrayInputStream(contentResult.getAudioBytes()));
                                postContentResult.setMessage(contentResult.getMessage());
                                postContentResult.setInputTranscript(contentResult.getInputTranscript());
                                InteractiveVoiceUtils.getInstance().processAudioResponse(postContentResult);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            Log.d("liufan", "receive result = ClassNotFoundException ---- " + e);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("liufan", "socket service exception : " + e);
                } finally {
                    Log.d("liufan", "finally");
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
