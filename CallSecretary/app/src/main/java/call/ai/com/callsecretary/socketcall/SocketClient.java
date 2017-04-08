package call.ai.com.callsecretary.socketcall;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import call.ai.com.callsecretary.lex.InteractiveVoiceUtils;
import call.ai.com.callsecretary.utils.CallSecretaryApplication;
import call.ai.com.callsecretary.utils.CommonSharedPref;

/**
 * Created by lewvan on 2017/4/8.
 */

public class SocketClient {
    private static SocketClient sInstance = new SocketClient();
    ObjectOutputStream outputStream;
    Handler mMainHandler;

    public static SocketClient getInstance() {
        return sInstance;
    }

    private SocketClient(){

    }

    public void init(Context context, Handler mainHandler) {
        mMainHandler = mainHandler;
        connectServerWithTCPSocket(context, mainHandler);
    }

    protected void connectServerWithTCPSocket(final Context context, final Handler mainHandler) {
        new Thread(){
            @Override
            public void run() {
                Socket socket;
                try {
                    String serviceIp = CommonSharedPref.getInstance(context).getServiceIp();
                    Log.d("liufan", "service ip = " + serviceIp);
                    socket = new Socket(serviceIp, 1989);
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    Log.d("liufan", "bind success");
                    showToast("bind success");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    showToast("bind UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("bind IOException");
                }
            }
        }.start();
    }

    private void showToast(final String text) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CallSecretaryApplication.getContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendMsgToSocket (final Object object) {
        if (outputStream != null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        outputStream.writeObject(object);
                        outputStream.flush();
                        showToast("send bytes success");
                        Log.d("liufan", "send bytes success!");
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("send IOException : " + e);
                        InteractiveVoiceUtils.getInstance().onAudioPlaybackError(e);
                    }
                }
            }.start();
        }
    }

    public  void sendMsgToSocket(final byte[] msgBytes) {
        if (outputStream != null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        outputStream.write(msgBytes, 0, msgBytes.length);
                        outputStream.flush();
                        showToast("send bytes success");
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("send IOException : " + e);
                    }
                }
            }.start();
        }
    }

}
