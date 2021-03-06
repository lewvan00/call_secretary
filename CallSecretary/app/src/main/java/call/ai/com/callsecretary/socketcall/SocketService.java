package call.ai.com.callsecretary.socketcall;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
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
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.floating.FloatingWindowsService;
import call.ai.com.callsecretary.lex.InteractiveVoiceUtils;
import call.ai.com.callsecretary.polley.PolleyUtils;
import call.ai.com.callsecretary.utils.CallSecretaryApplication;
import lex.SerializablePostContentResult;


/**
 * Created by lewvan on 2017/4/8.
 */

public class SocketService extends Service {
    public static final String INTENT_RINGOFF = "ringoff";

    Handler mMainHandler;
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    private MediaPlayer mMediaPlayer;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            boolean ringoff = intent.getBooleanExtra(INTENT_RINGOFF, false);
            if (ringoff && clientSocket != null) {
                new Thread() {
                    @Override
                    public void run() {
                        callbackRingoff(clientSocket);
                        clientSocket = null;
                        stopRingTone();
                    }
                }.start();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public static void callRingOff() {
        Context context = CallSecretaryApplication.getContext();
        Intent intent = new Intent(context, SocketService.class);
        intent.putExtra(INTENT_RINGOFF, true);
        context.startService(intent);
    }

    public void startSocketService() {
        new Thread() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(1989);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(SocketService.this, "socket listening", Toast.LENGTH_SHORT).show();
                    }
                });
                while (true) {
                    Socket socket = null;
                    try {
                        socket = serverSocket.accept();
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
                                        case SerializablePostContentResult.STATE_FINAL:
                                            startRingTone();
                                            break;
                                        case SerializablePostContentResult.STATE_HANGUP:
                                            handleHandUpStatus(contentResult);
                                            break;
                                        case SerializablePostContentResult.STATE_RINGOFF:
                                            handleRingoff(socket);
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
                        try {
                            if (socket != null) {
                                socket.close();
                            }
                        } catch (Exception e) {
                        }
                    }
                }

            }
        }.start();
    }

    private void handleHandUpStatus(SerializablePostContentResult contentResult) {
        PostContentResult postContentResult = new PostContentResult();
        postContentResult.setAudioStream(new ByteArrayInputStream(contentResult.getAudioBytes()));
        postContentResult.setMessage(contentResult.getMessage());
        postContentResult.setInputTranscript(contentResult.getInputTranscript());

        InteractionClient client = InteractiveVoiceUtils.getInstance().getClient();
        client.setNeedPlayback(true);
        client.processSocketResponse(postContentResult);
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startRingTone();
            }
        }, 1500);
    }

    private void handleRingoff(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
        }
        clientSocket = null;
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                FloatingWindowsService.getServiceInstance().hideFloatingWindows();
            }
        });
    }

    private void handleVoiceResponse(SerializablePostContentResult contentResult) {
        //get audio stream;
        Log.d("liufan", "receive audio result = " + contentResult);
        final PostContentResult postContentResult = new PostContentResult();
        postContentResult.setAudioStream(new ByteArrayInputStream(contentResult.getAudioBytes()));
        postContentResult.setMessage(contentResult.getMessage());
        postContentResult.setInputTranscript(contentResult.getInputTranscript());

        InteractionClient client = InteractiveVoiceUtils.getInstance().getClient();
        client.setNeedPlayback(true);
        client.processSocketResponse(postContentResult);

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                FloatingWindowsService.getServiceInstance().onResult(postContentResult);
            }
        });
    }

    private void handleCall(final Socket socket) throws IOException {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(SocketService.this, "call received, auto answering", Toast.LENGTH_SHORT).show();
            }
        });

        PolleyUtils.getInstance().readText("Hello!");
        callbackAck(socket);

        clientSocket = socket;
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                FloatingWindowsService floatingWindowsService = FloatingWindowsService.getServiceInstance();
                floatingWindowsService.setIsServer(true);
                floatingWindowsService.showFloatingWindows(getString(R.string.float_title_answer));
                floatingWindowsService.ringConnect();
            }
        });
    }

    private void callbackAck(Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            byte[] ackData = "ack".getBytes();
            outputStream.write(ackData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void callbackRingoff(Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            byte[] ackData = "off".getBytes();
            outputStream.write(ackData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startRingTone() {
        mMediaPlayer = MediaPlayer.create(this, getSystemDefultRingtoneUri());
        mMediaPlayer.setLooping(false);
        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    public void stopRingTone() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
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
