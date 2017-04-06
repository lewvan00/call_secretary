package call.ai.com.callsecretary.recorder;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;

import omrecorder.AudioChunk;
import omrecorder.AudioSource;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;
import omrecorder.WriteAction;

/**
 * Created by zhangwx on 2017/3/30.
 */

public class CallRecorder implements PullTransport.OnAudioChunkPulledListener, Recorder.OnSilenceListener {

    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int AUDIO_FREQUENCY = 16000;//Hz

    private File mFile = null;
    private Recorder mRecorder = null;

    public CallRecorder() {
        mFile = new File(Environment.getExternalStorageDirectory(), "callRecoding.wav");
        mRecorder = OmRecorder.wav(
                new PullTransport.Noise(getAudioSource(), this, new WriteAction.Default(), this, 100),
                mFile);
    }

    @Override
    public void onAudioChunkPulled(AudioChunk audioChunk) {
        //do nothing
    }

    @Override
    public void onSilence(long silenceTime) {
        //do nothing
    }

    private AudioSource getAudioSource() {
        return new AudioSource.Smart(
                AUDIO_SOURCE,
                AUDIO_ENCODING,
                AUDIO_CHANNEL,
                AUDIO_FREQUENCY);
    }

    public void startRecording() {
        if (mRecorder != null) {
            mRecorder.startRecording();
        }
    }

    public void stopRecoding() {
        if (mRecorder != null) {
            mRecorder.startRecording();
        }
    }

    public void pauseRecording() {
        if (mRecorder != null) {
            mRecorder.pauseRecording();
        }
    }

    public void resumeRecording() {
        if (mRecorder != null) {
            mRecorder.resumeRecording();
        }
    }

    public File getRecordFile() {
        return mFile;
    }
}
