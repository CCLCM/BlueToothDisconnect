package com.zhidao.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Method;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBtAdapter;
    private BluetoothA2dpSink mBluetoothA2dp;
    private BluetoothHeadsetClient mHeadset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取 BluetoothAdapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        //获取 ProfileProxy
        mBtAdapter.getProfileProxy(this, mListener,  10);
        mBtAdapter.getProfileProxy(this, mListener, 16);

    }

    /**
     * Button按钮响应
     * @param view
     */
    public void DisConnect(View view) {
        Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice device :bondedDevices) {
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.d("test", "disConnectA2dp " + device.getAddress() + "  " + device.getName() );

                disConnectA2dp(device);
                disConnectHead(device);
            }
        }
    }

    /**
     * BluetoothProfile 回调监听
     */
    private BluetoothProfile.ServiceListener mListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int profile) {
            // TODO 注意:  BluetoothA2dpSink Android 5.1  profile 是 10 ,  Android 7.1  profile 是11
            // TODO 注意区分Android 版本
            if (profile == 10) {
                mBluetoothA2dp = null;
            }

            if (profile == 16) {
                mHeadset = null;
            }
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            // TODO 注意:  BluetoothA2dpSink Android 5.1  profile 是 10 ,  Android 7.1  profile 是11
            // TODO 注意区分Android 版本
            Log.d("test", "onServiceConnected " + proxy.getClass().getSimpleName() + "  profile " + profile );
            if (profile == 10) {
                mBluetoothA2dp = (BluetoothA2dpSink) proxy; //转换
            }
            if (profile == 16) {
                mHeadset = (BluetoothHeadsetClient) proxy;
            }
        }
    };

    /**
     * 断开音频链接
     * @param device 断开的设备
     */
    public void disConnectA2dp(BluetoothDevice device) {
        if (mBluetoothA2dp == null) {
            Log.d("test", " disConnectA2dp " +  " mBluetoothA2dp is null return!!!");
            return;
        }
        try {
            //BluetoothA2dpSink （hide的），断开连接。
            Method connectMethod = BluetoothA2dpSink.class.getMethod("disconnect",
                    BluetoothDevice.class);
            connectMethod.invoke(mBluetoothA2dp, device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开音频链接
     * @param device 断开的设备
     */
    public void disConnectHead(BluetoothDevice device) {
        if (mHeadset == null) {
            Log.d("test", "disConnectHead " +  " mHeadset is null return!!!");
            return;
        }
        mHeadset.disconnect(device);
    }



}
