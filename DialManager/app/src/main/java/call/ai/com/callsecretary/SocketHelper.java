package call.ai.com.callsecretary;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import lex.InteractiveVoiceUtils;

/**
 * Created by lewvan on 2017/4/8.
 */

public class SocketHelper {
    private static SocketHelper sInstance = new SocketHelper();
    ObjectOutputStream outputStream;
    Handler mMainHandler;

    public static SocketHelper getInstance() {
        return sInstance;
    }

    private SocketHelper(){

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
                try {// 创建一个Socket对象，并指定服务端的IP及端口号
                    String serviceIp = CommonSharedPref.getInstance(context).getServiceIp();
                    Log.d("liufan", "service ip = " + serviceIp);
                    socket = new Socket(serviceIp, 1989);
                    // 获取Socket的OutputStream对象用于发送数据。
//            OutputStream outputStream = socket.getOutputStream();
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
//                    String sendMsg = "hello world";
//                    byte[] msgByte = sendMsg.getBytes();
//                    // 把数据写入到OuputStream对象中
//                    outputStream.write(msgByte, 0, msgByte.length);
//                    // 发送读取的数据到服务端
//                    outputStream.flush();

                    /** 或创建一个报文，使用BufferedWriter写入,看你的需求 **/
//          String socketData = "[2143213;21343fjks;213]";
//          BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
//                  socket.getOutputStream()));
//          writer.write(socketData.replace("\n", " ") + "\n");
//          writer.flush();
                    /************************************************/
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
