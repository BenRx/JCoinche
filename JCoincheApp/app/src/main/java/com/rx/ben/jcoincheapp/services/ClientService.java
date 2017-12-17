package com.rx.ben.jcoincheapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.rx.ben.jcoincheapp.BaseApplication;
import com.rx.ben.jcoincheapp.controllers.ClientController;
import com.rx.ben.jcoincheapp.interfaces.IClientControllerListener;
import com.rx.ben.jcoincheapp.models.CoincheRequest;
import com.rx.ben.jcoincheapp.models.Lobby;
import com.rx.ben.jcoincheapp.models.Room;

import java.util.ArrayList;

public class ClientService extends Service {

    public static final String ACTION_START_CONNECTION = "ACTION_START_CONNECTION";
    public static final String ACTION_STOP_CONNECTION = "ACTION_STOP_CONNECTION";
    public static final String ACTION_SEND_NICKNAME = "ACTION_SEND_NICKNAME";

    public static final String KEY_CONNEXION_HOST = "KEY_CONNECTION_HOST";
    public static final String KEY_CONNEXION_PORT = "KEY_CONNECTION_PORT";
    public static final String KEY_CONNEXION_NICK = "KEY_CONNEXION_NICK";

    public static final String KEY_LOGGED_NAME = "KEY_LOGGED_NAME";
    public static final String KEY_LOGGED_ROOMS_NAME = "KEY_LOGGED_ROOMS_NAME";

    public static final String STATUS_CONNEXION_ERROR = "STATUS_CONNEXION_ERROR";
    public static final String STATUS_CLIENT_CONNECTED = "STATUS_CLIENT_CONNECTED";
    public static final String STATUS_CLIENT_DISCONNECTED = "STATUS_CLIENT_DISCONNECTED";
    public static final String STATUS_CLIENT_LOGGED = "STATUS_CLIENT_LOGGED";
    public static final String STATUS_NICKNAME_ERROR = "STATUS_NICKNAME_ERROR";

    private ClientController mClientController;

    private IClientControllerListener mClientControllerListener = new IClientControllerListener() {

        @Override
        public void onConnectionError() {
            sendStatusUpdate(STATUS_CONNEXION_ERROR);
        }

        @Override
        public void onClientConnected() {
            sendStatusUpdate(STATUS_CLIENT_CONNECTED);
        }

        @Override
        public void onClientDisconnected() {
            sendStatusUpdate(STATUS_CLIENT_DISCONNECTED);
        }

        @Override
        public void onClientLogged(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Lobby) {
                Lobby lobby = (Lobby) coincheRequest.mPacket;
                ArrayList<String> rooms = new ArrayList<>();
                for (Room room : lobby.mRooms) {
                    rooms.add(room.mName);
                }
                sendLoggedStatusUpdate(mClientController.getNickName(), rooms);
            }
        }

        @Override
        public void onNickAlreadyUsedError() {
            sendStatusUpdate(STATUS_NICKNAME_ERROR);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopService(startId);
            return START_NOT_STICKY;
        }
        Log.d("ClientService", "GloveService.onStartCommand: action = " + intent.getAction() + ", flags = " + flags + ", startId = " + startId);
        switch (intent.getAction()) {
            case ACTION_START_CONNECTION:
                String host = intent.getStringExtra(KEY_CONNEXION_HOST);
                int port = intent.getIntExtra(KEY_CONNEXION_PORT, -1);
                if (host != null && port != -1) {
                    mClientController = new ClientController(mClientControllerListener, host, port);
                    mClientController.start();
                }
                break;
            case ACTION_STOP_CONNECTION:
                mClientController.stop();
                break;
            case ACTION_SEND_NICKNAME:
                String nick = intent.getStringExtra(KEY_CONNEXION_NICK);
                if (nick != null) {
                    mClientController.setNickName(nick);
                    mClientController.sendRequest(mClientController.getKryoClientController().getConnection(), 200, nick);
                }
                break;
            default:
                Log.e("ClientService", "Unexpected intent " + intent.getAction());
                stopService(startId);
                return START_NOT_STICKY;

        }
        return START_STICKY;
    }

    private void sendLoggedStatusUpdate(String nick, ArrayList<String> rooms) {
        Intent intent = new Intent(STATUS_CLIENT_LOGGED);

        intent.putExtra(KEY_LOGGED_NAME, nick);
        intent.putExtra(KEY_LOGGED_ROOMS_NAME, rooms);
        LocalBroadcastManager.getInstance(BaseApplication.getAppContext()).sendBroadcast(intent);
    }

    private void sendStatusUpdate(String status) {
        Intent intent = new Intent(status);
        LocalBroadcastManager.getInstance(BaseApplication.getAppContext()).sendBroadcast(intent);
    }

    private void stopService(int startId) {
        Log.d("ClientService", "Stopping service ...");
        stopForeground(true);
        stopSelf(startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
