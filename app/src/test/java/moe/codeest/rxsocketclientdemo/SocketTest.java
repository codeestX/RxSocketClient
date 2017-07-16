package moe.codeest.rxsocketclientdemo;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import java.net.URISyntaxException;
import java.util.Arrays;

import moe.codeest.rxsocketclient.SocketSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/7/12
 * @description:
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SocketTest {

    private JavaActivity activity;
    private Button btnConnect, btnDisConnect, btnSend;
    private TextView tvReceive;

    @Before
    public void setUp() throws URISyntaxException {
        ShadowLog.stream = System.out;
        activity = Robolectric.setupActivity(JavaActivity.class);
        btnConnect = (Button) activity.findViewById(R.id.btn_connect);
        btnDisConnect = (Button) activity.findViewById(R.id.btn_disconnect);
        btnSend = (Button) activity.findViewById(R.id.btn_send);
        tvReceive = (TextView) activity.findViewById(R.id.tv_receive);
    }

    @Test
    public void testActivity() {
        assertNotNull(activity);
        assertEquals("RxSocketClientDemo", activity.getTitle());
    }

    @Test
    public void testCallback(){
        assertNotNull(activity);
        TestSocketSubscriber subscriber = new TestSocketSubscriber();
        activity.connect(subscriber);
        subscriber.onConnected();
        assertEquals("onConnected", ShadowToast.getTextOfLatestToast());
        subscriber.onResponse(new byte[]{1, 2, 3});
        assertEquals("[1, 2, 3]", tvReceive.getText());
        subscriber.onDisconnected();
        assertEquals("onDisConnected", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testUI(){
        assertNotNull(btnConnect);
        assertNotNull(btnDisConnect);
        btnConnect.performClick();
        btnDisConnect.performClick();
    }

    public class TestSocketSubscriber extends SocketSubscriber {

        @Override
        public void onConnected() {
            Toast.makeText(activity, "onConnected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected() {
            Toast.makeText(activity, "onDisConnected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(@NotNull byte[] data) {
            tvReceive.setText(Arrays.toString(data));
        }
    }

}