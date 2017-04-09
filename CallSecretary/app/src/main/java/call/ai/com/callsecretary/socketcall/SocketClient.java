package call.ai.com.callsecretary.socketcall;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.floating.FloatingWindowsService;
import call.ai.com.callsecretary.lex.InteractiveVoiceUtils;
import call.ai.com.callsecretary.utils.CallSecretaryApplication;
import call.ai.com.callsecretary.utils.CommonSharedPref;
import lex.SerializablePostContentResult;

/**
 * Created by lewvan on 2017/4/8.
 */

public class SocketClient {
    private static SocketClient sInstance = new SocketClient();
    Socket socket;
    ObjectOutputStream outputStream;
    InputStream inputStream;
    Handler mMainHandler;
    InteractiveVoiceUtils mVoiceUtil;

    public static SocketClient getInstance() {
        return sInstance;
    }

    private SocketClient(){

    }

    public void init(Context context, Handler mainHandler, InteractiveVoiceUtils voiceUtil) {
        mMainHandler = mainHandler;
        mVoiceUtil = voiceUtil;
        connectServerWithTCPSocket(context, mainHandler);
    }

    protected void connectServerWithTCPSocket(final Context context, final Handler mainHandler) {
        new Thread(){
            @Override
            public void run() {
                try {
                    String serviceIp = CommonSharedPref.getInstance(context).getServiceIp();
                    Log.d("liufan", "service ip = " + serviceIp);
                    socket = new Socket(serviceIp, 1989);
                    inputStream = socket.getInputStream();
                    outputStream = new ObjectOutputStream(socket.getOutputStream());

                    sendCallToSocket();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
//                    showToast("bind UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
//                    showToast("bind IOException");
                }
            }
        }.start();
    }

    private void showToast(final String text) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(CallSecretaryApplication.getContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendCallToSocket() {
        if (outputStream != null) {
            try {
                SerializablePostContentResult result = new SerializablePostContentResult();
                result.setState(SerializablePostContentResult.STATE_CALL);

                outputStream.writeObject(result);
                outputStream.flush();
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        FloatingWindowsService.getServiceInstance().ringConnect();
                    }
                });
//                showToast("call success");
                Log.d("liufan", "call success!");

                while(true) {
                    byte[] bytes = new byte[3];
                    int count = inputStream.read(bytes, 0, 3);
                    if (count <= 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    } else if (count == 3) {
                        if (bytes[0] == 'a' - 'a' &&
                                bytes[1] == 'c' - 'a' &&
                                bytes[2] == 'k' - 'a') {

                            onPhoneReceived();
                        } else if (bytes[0] == 'o' - 'a' &&
                                bytes[1] == 'f' - 'a' &&
                                bytes[2] == 'f' - 'a') {
                            onPhoneROff();
                            closeSocket();
                            break;
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
//                showToast("call IOException : " + e);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        FloatingWindowsService.getServiceInstance().hideFloatingWindows();
                    }
                });
            }
        }
    }

    private void onPhoneReceived() {
        showToast("remote listen");
        Log.d("liufan", "remote listen!");

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Context context = CallSecretaryApplication.getContext();
                FloatingWindowsService floatingWindowsService = FloatingWindowsService.getServiceInstance();
                floatingWindowsService.setIsServer(false);
                floatingWindowsService.showFloatingWindows(context.getString(R.string.float_title_call));
            }
        });
    }

    private void onPhoneROff() {
        showToast("remote off");
        Log.d("liufan", "remote off!");

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                FloatingWindowsService floatingWindowsService = FloatingWindowsService.getServiceInstance();
                floatingWindowsService.hideFloatingWindows();
            }
        });
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    public void ringoffFromSocket() {
        if (outputStream != null) {
            try {
                SerializablePostContentResult result = new SerializablePostContentResult();
                result.setState(SerializablePostContentResult.STATE_RINGOFF);

                outputStream.writeObject(result);
                outputStream.flush();

                showToast("ringoff success");
                Log.d("liufan", "ringoff success!");

            } catch (IOException e) {
                e.printStackTrace();
                showToast("ringoff IOException : " + e);
            }
        }
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
                        mVoiceUtil.onAudioPlaybackError(e);
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
