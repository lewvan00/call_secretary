package com.singun.media.audio;

import com.amazon.blueshift.bluefront.android.vad.AbstractVAD;
import com.amazonaws.mobileconnectors.lex.interactionkit.internal.audio.AudioRecorder;
import com.amazonaws.mobileconnectors.lex.interactionkit.internal.audio.encoder.AudioEncoder;
import com.amazonaws.mobileconnectors.lex.interactionkit.internal.vad.DnnVAD;
import com.amazonaws.mobileconnectors.lex.interactionkit.internal.vad.VoiceActivityDetector;
import com.amazonaws.mobileconnectors.lex.interactionkit.internal.vad.config.DnnVADConfig;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by singun on 2017/3/8 0008.
 */

public class PcmReader {
    private PlayStateListener mListener;

    private boolean mReleased;
    private int mPrimePlaySize = 0;
    private byte[] mCacheData;
    private boolean mIsReading;

    private PipedInputStream mConsumerStream;
    private PipedOutputStream mProducerStream;

    private final AudioEncoder mAudioEncoder;
    private final VoiceActivityDetector mVAD;

    private VoiceActivityDetector.VADState state;
    private VoiceActivityDetector.VADState currentState;

    public PcmReader(int primePlaySize, AudioEncoder audioEncoder) {
        init(primePlaySize);
        mAudioEncoder = audioEncoder;
        mVAD = new DnnVAD(AudioRecorder.DEFAULT_SAMPLE_RATE, new DnnVADConfig());
    }

    private void init(int primePlaySize) {
        mPrimePlaySize = primePlaySize;
        mCacheData = new byte[mPrimePlaySize];
    }

    public void release() {
        mReleased = true;
    }

    public void startReading(String filePath) {
        if (mIsReading) {
            return;
        }
        mIsReading = true;
        AudioReadThread thread = new AudioReadThread(filePath);
        thread.start();
    }

    public void stop() {
        if (!mIsReading) {
            return;
        }
        mIsReading = false;
    }

    public boolean isReading() {
        return mIsReading;
    }

    public void setPlayStateListener(PlayStateListener listener) {
        mListener = listener;
    }

    public InputStream getConsumerStream() {
        return mConsumerStream;
    }

    protected void postAudioRecordingProcessing(final int numSamplesRead,
                                                final short[] buffer) {
        if (numSamplesRead > 0) {
            // Process the samples through the VAD and get current VAD state.
            currentState = mVAD.processSamples(buffer, numSamplesRead);

            // Encode audio for sending to service.
            final byte[] compressedBuffer = mAudioEncoder.encode(buffer, numSamplesRead);

            try {
                mProducerStream.write(compressedBuffer, 0, compressedBuffer.length);
                mProducerStream.flush();
            } catch (final IOException e) {
            }
        }
    }

    private class AudioReadThread extends Thread {
        private InputStream mInputStream;
        private String mFilePath;

        public AudioReadThread(String filePath) {
            super("AudioPlayThread");
            mFilePath = filePath;
//            setPriority();
        }

        @Override
        public void run() {
            readStart();
            while(!mReleased) {
                try {
                    int size = mInputStream.read(mCacheData, 0, mPrimePlaySize);
                    if (size <= 0) {
                        break;
                    }
                    int length = size / 2;
                    short[] shorts = new short[length];
                    ByteBuffer.wrap(mCacheData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
                    postAudioRecordingProcessing(length, shorts);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            readComplete();
        }

        private void readStart() {
            File file = new File(mFilePath);
            openAudioFile(file);
        }

        private void openAudioFile(File audioFile) {
            if (mInputStream != null) {
                closeAudioFile();
            }
            try {
                FileInputStream inputStream = new FileInputStream(audioFile);
                mInputStream = new DataInputStream(new BufferedInputStream(inputStream));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            mProducerStream = new PipedOutputStream();
            try {
                mConsumerStream = new PipedInputStream(mProducerStream, mPrimePlaySize);
            } catch (final IOException e) {
            }
        }

        private void readComplete() {
//            closeAudioFile();
            mIsReading = false;

            if (mListener != null) {
                mListener.onPlayComplete();
            }
        }

        private void closeAudioFile() {
            if (mInputStream == null) {
                return;
            }
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mInputStream = null;
        }
    }

    public interface PlayStateListener {
        void onPlayComplete();
    }
}
