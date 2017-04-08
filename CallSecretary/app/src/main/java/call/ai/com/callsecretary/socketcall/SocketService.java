package call.ai.com.callsecretary.socketcall;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.lex.interactionkit.InteractionClient;
import com.amazonaws.services.lexrts.model.PostContentResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import call.ai.com.callsecretary.lex.InteractiveVoiceUtils;
import call.ai.com.callsecretary.polley.PolleyUtils;
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
                            if (contentResult != null) {
                                switch (contentResult.getState()) {
                                    case SerializablePostContentResult.STATE_RESPONSE:
                                        handleVoiceResponse(contentResult);
                                        break;
                                    case SerializablePostContentResult.STATE_CALL:
                                        handleCall(socket);
                                        break;
                                }
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

    private void handleVoiceResponse(SerializablePostContentResult contentResult) {
        //get audio stream;
        Log.d("liufan", "receive audio result = " + contentResult);
        PostContentResult postContentResult = new PostContentResult();
        postContentResult.setAudioStream(new ByteArrayInputStream(contentResult.getAudioBytes()));
        postContentResult.setMessage(contentResult.getMessage());
        postContentResult.setInputTranscript(contentResult.getInputTranscript());

        InteractionClient client = InteractiveVoiceUtils.getInstance().getClient();
        client.setNeedPlayback(true);
        client.processSocketResponse(postContentResult);
    }

    private void handleCall(final Socket socket) throws IOException {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SocketService.this, "call received, auto answering", Toast.LENGTH_SHORT).show();
            }
        });


        if (isAutoAnswer()) {
            callbackAck(socket);
        } else {
            PolleyUtils.getInstance().readText("Fuck you! Answer the phone! ");
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                    }

                    callbackAck(socket);
                }
            }.start();
        }

    }

    private boolean isAutoAnswer() {
        return true;
    }

    private void callbackAck(Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            byte[] ackData = new byte[3];
            ackData[0] = 'a' - 'a';
            ackData[1] = 'c' - 'a';
            ackData[2] = 'k' - 'a';
            outputStream.write(ackData);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
