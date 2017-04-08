package polley;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.Gender;
import com.amazonaws.services.polly.model.LanguageCode;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;
import com.amazonaws.services.polly.model.VoiceId;

import java.io.IOException;
import java.net.URL;

import call.ai.com.callsecretary.CallSecretaryApplication;

/**
 * Created by 李东 on 2017/4/9.
 */

public class PolleyUtils {

   private static final String TAG = "PolleyUtils";
    private static final String COGNITO_POOL_ID = "us-east-1:a16c3d73-380e-4a04-b1a7-1dda5077674e";
    private static final Regions MY_REGION = Regions.US_EAST_1;
    CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonPollyPresigningClient client;
    private Voice voice;
    MediaPlayer mediaPlayer;

    private static PolleyUtils mInstance = new PolleyUtils();

    private PolleyUtils() {
        initVoice();
        initPollyClient();
        setupNewMediaPlayer();
    }

    private void initVoice(){
        voice=new Voice();
        voice.setGender(Gender.Female);
        voice.setId(VoiceId.Joanna);
        voice.setLanguageCode(LanguageCode.EnUS);
        voice.setLanguageName("US English");
        voice.setName("Joanna");
    }

    public static PolleyUtils getInstance() {
        return mInstance;
    }


    void initPollyClient() {
        // Initialize the Amazon Cognito credentials provider.
        credentialsProvider = new CognitoCachingCredentialsProvider(
                CallSecretaryApplication.getContext(),
                COGNITO_POOL_ID,
                MY_REGION
        );

        // Create a client that supports generation of presigned URLs.
        client = new AmazonPollyPresigningClient(credentialsProvider);
    }

    void setupNewMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                setupNewMediaPlayer();
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }


    public void readText(String string){

        Voice selectedVoice = voice;

        String textToRead = string;

        // Use voice's sample text if user hasn't provided any text to read.
        if (textToRead.trim().isEmpty()) {
            return;
        }

        // Create speech synthesis request.
        SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                new SynthesizeSpeechPresignRequest()
                        // Set text to synthesize.
                        .withText(textToRead)
                        // Set voice selected by the user.
                        .withVoiceId(selectedVoice.getId())
                        // Set format to MP3.
                        .withOutputFormat(OutputFormat.Mp3);

        // Get the presigned URL for synthesized speech audio stream.
        URL presignedSynthesizeSpeechUrl =
                client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);

        Log.i(TAG, "Playing speech from presigned URL: " + presignedSynthesizeSpeechUrl);

        // Create a media player to play the synthesized audio stream.
        if (mediaPlayer.isPlaying()) {
            setupNewMediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            // Set media player's data source to previously obtained URL.
            mediaPlayer.setDataSource(presignedSynthesizeSpeechUrl.toString());
        } catch (IOException e) {
            Log.e(TAG, "Unable to set data source for the media player! " + e.getMessage());
        }

        // Start the playback asynchronously (since the data source is a network stream).
        mediaPlayer.prepareAsync();
    }

}
