package call.ai.com.callsecretary;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.lex.interactionkit.Response;
import com.amazonaws.mobileconnectors.lex.interactionkit.continuations.LexServiceContinuation;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.AudioPlaybackListener;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.InteractionListener;
import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceView;
import com.amazonaws.services.lexrts.model.PostContentResult;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import lex.InteractiveVoiceUtils;
import lex.SerializablePostContentResult;

/**
 * Created by lewvan on 2017/3/27.
 */

public class PhoneCallReceiver extends BroadcastReceiver implements AudioPlaybackListener, InteractionListener, InteractiveVoiceView.InteractiveVoiceListener {

    public PhoneCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            //dialing
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
            connectServerWithTCPSocket(context);
            if (tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
                //接通
                InteractiveVoiceUtils.getInstance().start(this, this, this);
            } else if (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                //挂断
                InteractiveVoiceUtils.getInstance().finish();
            }
        }
    }

    ObjectOutputStream outputStream;
    protected void connectServerWithTCPSocket(final Context context) {
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
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void sendMsgToSocket(final Object object) {
        if (outputStream != null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        outputStream.writeObject(object);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    private  void sendMsgToSocket(final byte[] msgBytes) {
        new Thread() {
            @Override
            public void run() {
                try {
                    outputStream.write(msgBytes, 0, msgBytes.length);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onAudioPlaybackStarted() {

    }

    @Override
    public void onAudioPlayBackCompleted() {

    }

    @Override
    public void onAudioPlaybackError(Exception e) {

    }

    @Override
    public void onReadyForFulfillment(Response response) {

    }

    @Override
    public void promptUserToRespond(Response response, LexServiceContinuation continuation) {

    }

    @Override
    public void onInteractionError(Response response, Exception e) {

    }

    @Override
    public void dialogReadyForFulfillment(Map<String, String> slots, String intent) {

    }

    @Override
    public void onResponse(Response response) {
        Log.d("liufan", "onResponse");
        SerializablePostContentResult result = new SerializablePostContentResult();
        PostContentResult realResult = response.getResult();
        result.setRealResult(realResult);
        sendMsgToSocket(result);
    }

    @Override
    public void onError(String responseText, Exception e) {

    }
}
