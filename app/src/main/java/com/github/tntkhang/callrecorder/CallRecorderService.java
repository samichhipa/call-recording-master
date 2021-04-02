package com.github.tntkhang.callrecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.tntkhang.utils.Constants;

import vn.nextlogix.tntkhang.R;

public class CallRecorderService extends Service {

    MediaRecorder recorder;
    static final String TAGS = "tntkhang";
    private static final String CHANNEL_ID_MAIN = "my_channel_017";
    private boolean isStartRecordSuccess = true;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_MAIN, "Recording...", NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            channel.setShowBadge(false);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_MAIN)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Recording...")
                .setSound(null)
                .build();
        startForeground(6, notification);
        recorder = new MediaRecorder();
        recorder.reset();

        String phoneNumber = intent.getStringExtra(Constants.Prefs.PHONE_CALL_NUMBER);
        String outputPath = intent.getStringExtra(Constants.Prefs.CALL_RECORD_PATH);
        Log.d(TAGS, "Phone number in service: " + phoneNumber);

//            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
//            } else if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            } else {
//                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
//            }

        recorder.setAudioSamplingRate(48000);
        recorder.setAudioEncodingBitRate(96000);


        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
//        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

//        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        recorder.setOutputFile(outputPath);

        try {
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            isStartRecordSuccess = false;
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        if (isStartRecordSuccess) {
            try {
                if (recorder != null) {
                    recorder.stop();
                    recorder.reset();
                    recorder.release();
                    recorder = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAGS, "onDestroy: " + "Recording stopped");
        }
    }
}
