package call.ai.com.callsecretary.lex;

import android.content.Context;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.lex.interactionkit.InteractionClient;
import com.amazonaws.mobileconnectors.lex.interactionkit.Response;
import com.amazonaws.mobileconnectors.lex.interactionkit.config.InteractionConfig;
import com.amazonaws.mobileconnectors.lex.interactionkit.continuations.LexServiceContinuation;
import com.amazonaws.mobileconnectors.lex.interactionkit.exceptions.InvalidParameterException;
import com.amazonaws.mobileconnectors.lex.interactionkit.exceptions.MaxSpeechTimeOutException;
import com.amazonaws.mobileconnectors.lex.interactionkit.exceptions.NoSpeechTimeOutException;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.AudioPlaybackListener;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.InteractionListener;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.MicrophoneListener;
import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceView;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexrts.model.DialogState;
import com.amazonaws.services.lexrts.model.PostContentResult;

import java.util.HashMap;
import java.util.Map;

import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.utils.CallSecretaryApplication;

/**
 * Created by 李东 on 2017/4/8.
 */

public class InteractiveVoiceUtils implements InteractionListener, AudioPlaybackListener, MicrophoneListener {

    private final static String TAG = "InteractiveVoiceUtils";
    private static final String INTERACTION_VOICE_VIEW_USER_AGENT = "VOICE_BUTTON";
    private final Context context;
    private InteractiveVoiceView.InteractiveVoiceListener voiceListener;
    protected int state;
    private Regions awsRegion;
    private AWSCredentialsProvider credentialsProvider;
    private InteractionConfig interactionConfig;
    private InteractionClient lexInteractionClient;
    private LexServiceContinuation continuation;
    private boolean shouldInitialize;
    private Map<String, String> sessionAttributes;
    private final ClientConfiguration clientConfiguration;

    protected final static int STATE_NOT_READY = 0;
    protected final static int STATE_READY = 1;
    protected final static int STATE_LISTENING = 2;
    protected final static int STATE_AUDIO_PLAYBACK = 3;
    protected final static int STATE_AWAITING_RESPONSE = 4;

    private InteractiveVoiceUtils() {
        this.context = CallSecretaryApplication.getContext();
        this.shouldInitialize = true;
        this.voiceListener = null;
        this.state = STATE_READY;

        // for setting the user agent
        clientConfiguration = new ClientConfiguration();
        clientConfiguration.setUserAgent(INTERACTION_VOICE_VIEW_USER_AGENT);
    }

    public static InteractiveVoiceUtils getInstance() {
        return new InteractiveVoiceUtils();
    }

    public InteractionClient getClient() {
        return lexInteractionClient;
    }

    public void setCredentialProvider(AWSCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        shouldInitialize = true;
    }

    public void setInteractionConfig(InteractionConfig interactionConfig) {
        this.interactionConfig = interactionConfig;
        shouldInitialize = true;
    }

    public void setSessionAttributes(Map<String, String> sessionAttributes) {
        this.sessionAttributes = sessionAttributes;
    }

    public void setVoiceListener(InteractiveVoiceView.InteractiveVoiceListener voiceListener) {
        this.voiceListener = voiceListener;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = Regions.fromName(awsRegion) ;
    }

    public void start(InteractiveVoiceView.InteractiveVoiceListener voiceListener,
                      AudioPlaybackListener audioPlaybackListener,
                      InteractionListener interactionListener) {
        if (shouldInitialize) {
            init(voiceListener, audioPlaybackListener, interactionListener);
        }

        if (sessionAttributes == null) {
            sessionAttributes = new HashMap<String, String>();
        }
        startListening(sessionAttributes);
    }

    public void finish() {
        if (lexInteractionClient != null) {
            lexInteractionClient.cancel();
            sessionAttributes.clear();
        }
        state = STATE_READY;
    }

    private void startListening(Map<String, String> sessionParameters) {
        state = STATE_LISTENING;
        lexInteractionClient.audioInForAudioOut(sessionParameters);
    }

    private void init(InteractiveVoiceView.InteractiveVoiceListener voiceListener,
                      AudioPlaybackListener audioPlaybackListener,
                      InteractionListener interactionListener) {
        CognitoCredentialsProvider cognitoCredentialsProvider= new CognitoCredentialsProvider(
                context.getResources().getString(R.string.identity_id_test),
                Regions.fromName(context.getResources().getString(R.string.aws_region)));
        InteractionClient lexInteractionClient = new InteractionClient(context,
                cognitoCredentialsProvider,
                Regions.US_EAST_1,
                context.getResources().getString(R.string.bot_name),
                context.getResources().getString(R.string.bot_alias));
        lexInteractionClient.setAudioPlaybackListener(audioPlaybackListener);
        lexInteractionClient.setInteractionListener(interactionListener);
        setVoiceListener(voiceListener);
        setCredentialProvider(cognitoCredentialsProvider);
        setInteractionConfig(
                new InteractionConfig(context.getResources().getString(R.string.bot_name),
                        context.getResources().getString(R.string.bot_alias)));
        setAwsRegion(context.getResources().getString(R.string.aws_region));

        state = STATE_READY;
        validateAppData();
        createInteractionClient();
        shouldInitialize = false;
    }

    public void processAudioResponse(PostContentResult result) {
        lexInteractionClient.processSocketResponse(result);
    }

    protected void validateAppData() {
        if (interactionConfig == null) {
            throw new InvalidParameterException(
                    "Interaction config is not set");
        } else {
            if (interactionConfig.getBotName() == null || interactionConfig.getBotName().isEmpty()) {
                throw new InvalidParameterException(
                        "Bot name is not set");
            }

            if (interactionConfig.getBotAlias() == null || interactionConfig.getBotAlias().isEmpty()) {
                throw new InvalidParameterException(
                        "Bot alias is not set");
            }
        }

        if (awsRegion == null) {
            throw new InvalidParameterException(
                    "AWS Region is not set");
        }
    }

    protected void createInteractionClient() {
        // Release system resources assigned to an earlier instance of the client.
        if (lexInteractionClient != null) {
            lexInteractionClient.cancel();
        }

        lexInteractionClient = new InteractionClient(
                context,
                credentialsProvider,
                awsRegion,
                interactionConfig,
                clientConfiguration);

        lexInteractionClient.setAudioPlaybackListener(this);
        lexInteractionClient.setInteractionListener(this);
        lexInteractionClient.setMicrophoneListener(this);
    }

    protected void reset() {
        if (lexInteractionClient != null) {
            lexInteractionClient.cancel();
        }
        state = STATE_READY;
        sessionAttributes = null;
    }


    @Override
    public void onAudioPlaybackStarted() {
        state = STATE_AUDIO_PLAYBACK;
    }

    @Override
    public void onAudioPlayBackCompleted() {
        if (state != STATE_NOT_READY) {
            if (this.continuation != null) {
                state = STATE_LISTENING;
                continuation.continueWithCurrentMode();
            } else {
                // Cannot continue, must start new dialog.
                state = STATE_READY;
            }
        }
    }

    @Override
    public void onAudioPlaybackError(Exception e) {
        Log.e(TAG, "InteractiveVoiceViewAdapter: Audio playback failed", e);
        state = STATE_READY;
    }

    @Override
    public void readyForRecording() {

    }

    @Override
    public void startedRecording() {

    }

    @Override
    public void onRecordingEnd() {
        if (state == STATE_NOT_READY) {
            return;
        }

        if (state == STATE_LISTENING) {
            state = STATE_AWAITING_RESPONSE;
        } else {
            state = STATE_READY;
        }
    }

    @Override
    public void onSoundLevelChanged(double soundLevel) {

    }

    @Override
    public void onMicrophoneError(Exception e) {
        if (e instanceof NoSpeechTimeOutException) {
            Log.e(TAG, "InteractiveVoiceViewAdapter: Failed to detect speech", e);
            state = STATE_READY;
        } else if (e instanceof MaxSpeechTimeOutException) {
            Log.e(TAG, "InteractiveVoiceViewAdapter: Speech time out", e);
        }
    }

    @Override
    public void onReadyForFulfillment(Response response) {
        state = STATE_READY;
        continuation = null;

        if (voiceListener != null) {
            voiceListener.dialogReadyForFulfillment(response.getSlots(), response.getIntentName());
        }
    }

    @Override
    public void promptUserToRespond(Response response, LexServiceContinuation continuation) {
        if (response == null) {
            Log.e(TAG, "InteractiveVoiceViewAdapter: Received null response from Amazon Lex bot");
        }

        if (DialogState.Fulfilled.toString().equals(response.getDialogState())) {
            // The request has been fulfilled, the bot is ready for a new dialog.
            state = STATE_READY;
            this.continuation = null;
        } else {
            this.continuation = continuation;
        }

        if (voiceListener != null) {
            voiceListener.onResponse(response);
        }
    }

    @Override
    public void onInteractionError(Response response, Exception e) {
        if (state != STATE_AUDIO_PLAYBACK) {
            state = STATE_READY;
        }
        continuation = null;

        if (voiceListener != null) {
            if (response != null) {
                voiceListener.onError(response.getTextResponse(), e);
            } else {
                voiceListener.onError("Error from Bot", e);
            }
        }
    }
}
