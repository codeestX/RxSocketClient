package moe.codeest.rxsocketclientdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import kotlin.text.Charsets;
import moe.codeest.rxsocketclient.RxSocketClient;
import moe.codeest.rxsocketclient.SocketClient;
import moe.codeest.rxsocketclient.SocketSubscriber;
import moe.codeest.rxsocketclient.meta.SocketConfig;
import moe.codeest.rxsocketclient.meta.SocketOption;
import moe.codeest.rxsocketclient.meta.ThreadStrategy;

/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/7/9
 * @description:
 */
public class JavaActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "JavaActivity";

    private static final String IP = "192.168.1.101";
    private static final int PORT = 8080;
    private static final byte[] HEART_BEAT = {1, 3, 4};
    private static final byte[] HEAD = {1, 2};
    private static final byte[] TAIL = {5, 7};

    private static final byte[] MESSAGE = {0, 1, 3};
    private static final String MESSAGE_STR = "TEST";

    private SocketClient mClient;
    private Disposable ref;
    private Button btnSend;
    private Button btnConnect;
    private Button btnDisConnect;
    private TextView tvReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnDisConnect = (Button) findViewById(R.id.btn_disconnect);
        tvReceive = (TextView) findViewById(R.id.tv_receive);
        btnSend.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnDisConnect.setOnClickListener(this);
    }

    private void connect() {
        //init
        mClient = RxSocketClient
                .create(new SocketConfig.Builder()
                        .setIp(IP)
                        .setPort(PORT)
                        .setCharset(Charsets.UTF_8)
                        .setThreadStrategy(ThreadStrategy.ASYNC)
                        .setTimeout(30 * 1000)
                        .build())
                .option(new SocketOption.Builder()
                        .setHeartBeat(HEART_BEAT, 60 * 1000)
                        .setHead(HEAD)
                        .setTail(TAIL)
                        .build());

        //connect
        ref = mClient.connect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SocketSubscriber() {
                    @Override
                    public void onConnected() {
                        Log.e(TAG, "onConnected");
                    }

                    @Override
                    public void onDisconnected() {
                        Log.e(TAG, "onDisconnected");
                        //re-connect
//                        connect();
                    }

                    @Override
                    public void onResponse(@NotNull byte[] data) {
                        Log.e(TAG, Arrays.toString(data));
                        tvReceive.setText(Arrays.toString(data));
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                //send bytes
                mClient.sendData(MESSAGE);

                //or send string
//                mClient.sendData(MESSAGE_STR);
                break;

            case R.id.btn_connect:
                //connect
                connect();
                break;

            case R.id.btn_disconnect:
                //disconnect
                mClient.disconnect();

                //or disconnect
//                ref.dispose();
                break;
        }
    }
}
