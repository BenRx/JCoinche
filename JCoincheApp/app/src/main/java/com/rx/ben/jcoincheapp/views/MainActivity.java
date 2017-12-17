package com.rx.ben.jcoincheapp.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.rx.ben.jcoincheapp.R;
import com.rx.ben.jcoincheapp.databinding.ActivityMainBinding;
import com.rx.ben.jcoincheapp.services.ClientService;
import com.rx.ben.jcoincheapp.views.adapters.RecyclerContentAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding mBinding;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                Log.e("MainActivity", "NotificationCenterFragment.mBroadcastReceiver.onReceive(): no intent received");
                return;
            }
            switch (intent.getAction()) {
                case ClientService.STATUS_CONNEXION_ERROR:
                    Log.i("MainActivity", "Connexion Error");
                    mBinding.setConnecting(false);
                    mBinding.setIsConnected(false);
                    mBinding.setIsNickState(false);
                    mBinding.setIsLobbyState(false);
                    Toast.makeText(getApplicationContext(), "Unable to join the specified host", Toast.LENGTH_SHORT).show();
                    break;
                case ClientService.STATUS_CLIENT_CONNECTED:
                    Log.i("MainActivity", "Connected");
                    mBinding.setConnecting(false);
                    mBinding.setIsConnected(true);
                    mBinding.setIsNickState(true);
                    Toast.makeText(getApplicationContext(), "You're now connected", Toast.LENGTH_SHORT).show();
                    break;
                case ClientService.STATUS_CLIENT_DISCONNECTED:
                    Log.i("MainActivity", "Disconnected");
                    mBinding.setConnecting(false);
                    mBinding.setIsConnected(false);
                    mBinding.setIsNickState(false);
                    mBinding.setIsLobbyState(false);
                    Toast.makeText(getApplicationContext(), "You're now disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case ClientService.STATUS_CLIENT_LOGGED:
                    Log.i("MainActivity", "Logged");
                    if (intent.hasExtra(ClientService.KEY_LOGGED_NAME) && intent.hasExtra(ClientService.KEY_LOGGED_ROOMS_NAME)) {
                        mBinding.setIsNickState(false);
                        mBinding.setIsLobbyState(true);
                        String nickname = intent.getStringExtra(ClientService.KEY_LOGGED_NAME);
                        String welcomeMsg = "Welcome " + nickname;
                        mBinding.welcomeMessage.setText(welcomeMsg);

                        ArrayList<String> rooms = intent.getStringArrayListExtra(ClientService.KEY_LOGGED_ROOMS_NAME);

                        mBinding.roomsRecycler.setAdapter(new RecyclerContentAdapter(rooms));

                        Toast.makeText(getApplicationContext(), "You're now logged", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ClientService.STATUS_NICKNAME_ERROR:
                    Toast.makeText(getApplicationContext(), "This nickname is already used", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Log.e("MainActivity", "Unexpected intent action " + intent.getAction());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mBinding.setClickListener(this);

        mBinding.setConnecting(false);
        mBinding.setIsConnected(false);
        mBinding.setIsNickState(false);
        mBinding.setIsLobbyState(false);

        mBinding.roomsRecycler.setLayoutManager(new LinearLayoutManager(this));

        IntentFilter serviceActionFilter = new IntentFilter();
        serviceActionFilter.addAction(ClientService.STATUS_CONNEXION_ERROR);
        serviceActionFilter.addAction(ClientService.STATUS_CLIENT_CONNECTED);
        serviceActionFilter.addAction(ClientService.STATUS_CLIENT_DISCONNECTED);
        serviceActionFilter.addAction(ClientService.STATUS_CLIENT_LOGGED);
        serviceActionFilter.addAction(ClientService.STATUS_NICKNAME_ERROR);

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, serviceActionFilter);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            if (view.getId() == R.id.connect_button) {
                intentServerConnection();
            } else if (view.getId() == R.id.nick_button) {
                sendNickName();
            }
        }
    }

    private void intentServerConnection() {
        if (!mBinding.getIsConnected()) {
            if (mBinding.portEdit.getText() != null && mBinding.hostEdit.getText() != null) {
                String host = mBinding.hostEdit.getText().toString();
                Integer port = 0;
                try {
                    port = Integer.parseInt(mBinding.portEdit.getText().toString());
                } catch (Exception e) {
                    Log.e("MainActivity", "Unable to parse port : " + e.getMessage());
                    return;
                }
                Log.d("MainActivity", "Host : " + host + " Port : " + port);

                Intent intent = new Intent(this, ClientService.class);
                intent.putExtra(ClientService.KEY_CONNEXION_HOST, host);
                intent.putExtra(ClientService.KEY_CONNEXION_PORT, port);
                intent.setAction(ClientService.ACTION_START_CONNECTION);
                startService(intent);
                mBinding.setConnecting(true);
            }
        } else {
            Intent intent = new Intent(this, ClientService.class);
            intent.setAction(ClientService.ACTION_STOP_CONNECTION);
            startService(intent);
        }
    }

    private void sendNickName() {
        if (mBinding.nickEdit.getText() != null) {
            String nickname = mBinding.nickEdit.getText().toString();
            Intent intent = new Intent(this, ClientService.class);
            intent.putExtra(ClientService.KEY_CONNEXION_NICK, nickname);
            intent.setAction(ClientService.ACTION_SEND_NICKNAME);
            startService(intent);
        }
    }
}
