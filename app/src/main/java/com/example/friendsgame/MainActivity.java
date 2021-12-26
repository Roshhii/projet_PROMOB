package com.example.friendsgame;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    public String name;
    public Map<String, Integer> scoreGamers;

    public static int game, GAME_COUNT = 3;
    Button play, explanations, wifi, search;
    TextView status;
    ListView devices, ranking;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    static List<String> devicesConnected = new ArrayList<String>();
    WifiP2pDevice[] deviceArray;
    ArrayAdapter<String> adapter;

    static final int MESSAGE_READ = 1;
    ServerClass serverClass;
    ClientClass clientClass;
    static SendReceive sendReceive;

    public static int[] table_games = new int[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_WIFI_STATE", "android.permission.CHANGE_WIFI_STATE", "android.permission.CHANGE_NETWORK_STATE", "android.permission.ACCESS_NETWORK_STATE"};
        this.requestAllPermissions(permissions);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        init();
        listeners();
    }

    /*
    Récupère le message
     */
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempsMsg = new String(readBuff, 0, msg.arg1);
                    System.out.println("Message recu : " + tempsMsg);
                    try {
                        JSONObject obj = new JSONObject(tempsMsg);
                        String type = obj.getString("type");
                        String game1 = obj.getString("game1");
                        String game2 = obj.getString("game2");
                        String game3 = obj.getString("game3");
                        System.out.println("type : " + type);
                        System.out.println("Game 1 : " + game1);
                        System.out.println("Game 2 : " + game2);
                        System.out.println("Game 3 : " + game3);
                        table_games[0] = Integer.parseInt(game1);
                        table_games[1] = Integer.parseInt(game2);
                        table_games[2] = Integer.parseInt(game3);
                        switch (type) {
                            /*
                            Savoir quel jeu est lancé
                             */
                           /* case "game" :
                                switch (number) {
                                    case "1":
                                        System.out.println("dice reçu");
                                        Toast.makeText(getApplicationContext(), "dice reçu", Toast.LENGTH_LONG).show();
                                        Intent diceActivity = new Intent(getApplicationContext(), DiceGame.class);
                                        startActivity(diceActivity);
                                        finish();
                                        break;
                                    case "2":
                                        System.out.println("tap reçu");
                                        Toast.makeText(getApplicationContext(), "tap reçu", Toast.LENGTH_LONG).show();
                                        Intent tapActivity = new Intent(getApplicationContext(), TapGame.class);
                                        startActivity(tapActivity);
                                        finish();
                                        break;
                                }
                                break;*/
                            case "start" :
                                Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                                switch (game1) {
                                    case "1":
                                        System.out.println("dice reçu");
                                        Toast.makeText(getApplicationContext(), "dice reçu", Toast.LENGTH_LONG).show();
                                        startActivity(loading);
                                        finish();
                                        break;
                                    case "2":
                                        System.out.println("tap reçu");
                                        Toast.makeText(getApplicationContext(), "tap reçu", Toast.LENGTH_LONG).show();
                                        startActivity(loading);
                                        finish();
                                        break;
                                }
                                break;
                            /*
                            Faire les cas pour les jeux comme
                            case "dice"
                            case "tap"
                            etc.
                             */
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    });

    /*
    Les permissions
     */
    private void requestAllPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 80);
        }
    }

    /*
    La gestion des boutons
     */
    private void listeners() {
        explanations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent welcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(welcomeActivity);
                finish();
            }
        });
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    wifi.setBackgroundColor(Color.parseColor("#CF3030"));
                } else {
                    wifiManager.setWifiEnabled(true);
                    wifi.setBackgroundColor(Color.parseColor("#50AE28"));
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Research in progress...", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Please activate WiFi", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WifiP2pDevice device = deviceArray[position];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
                        devicesConnected.add(device.deviceName);
                        scoreGamers.put(device.deviceName, 0);
                        /*
                        Je veux changer la liste et mettre quels appareils sont connectés
                         */
                        //deviceNameArray[position] = device.deviceName + " (connected)";
                    }
                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                games();
                System.out.println("Game 1 : " + table_games[0]);
                System.out.println("Game 2 : " + table_games[1]);
                System.out.println("Game 3 : " + table_games[2]);
                System.out.println("number game : " + game);
                System.out.println("GAME_COUNT : " + GAME_COUNT);
                /*
                Faire une condition pour savoir s'il y a du monde de connecté
                Si oui, on envoie un message avec le sendReceiver
                si non, on joue tout seul en lançant un jeu aléatoire
                 */
                if (devicesConnected.size() == 0) {
                    System.out.println("Aucun device connecté");
                    Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                    startActivity(loading);
                    finish();
                } else {
                    System.out.println("Un ou plusieurs devices connectés");
                    Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                    startActivity(loading);
                    finish();
                    String intGame1 = String.valueOf(table_games[0]);
                    String intGame2 = String.valueOf(table_games[1]);
                    String intGame3 = String.valueOf(table_games[2]);
                    String msg = "{ \"type\": \"start\", \"game1\": "+ intGame1+", \"game2\": "+ intGame2 +", \"game3\": "+ intGame3+"}";
                    sendReceive.write(msg.getBytes());
                }
            }
        });
    }

    /*
    L'initialisation des composants et des outils dont on a besoin pour le WiFi P2P
     */
    private void init () {
        play = findViewById(R.id.bt_play);
        explanations = findViewById(R.id.bt_explanations);
        status = findViewById(R.id.tv_status);
        devices = findViewById(R.id.lv_devices);
        ranking = findViewById(R.id.lv_ranking);
        wifi = findViewById(R.id.bt_wifi);
        search = findViewById(R.id.bt_search);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifi.setBackgroundColor(Color.parseColor("#CF3030"));
        } else {
            wifi.setBackgroundColor(Color.parseColor("#50AE28"));
        }

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        scoreGamers = new HashMap<String, Integer>();
    }

    /*
    La liste des devices P2P à proximité du téléphone
     */
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            if (!wifiP2pDeviceList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(wifiP2pDeviceList.getDeviceList());
                deviceNameArray = new String[wifiP2pDeviceList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[wifiP2pDeviceList.getDeviceList().size()];
                int index = 0;
                for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                devices.setAdapter(adapter);
            }
            if (peers.size() == 0) {
                Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /*
    Connexion des devices
     */
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                status.setText("Status: Host");
                serverClass = new ServerClass();
                serverClass.start();
                /*
                Je veux changer la liste et mettre quels appareils sont connectés
                 */
                //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                //devices.setAdapter(adapter);
            } else if (wifiP2pInfo.groupFormed) {
                status.setText("Status: Client");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
                //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                //devices.setAdapter(adapter);
            }
            /*
            serverClass = new ServerClass();
                serverClass.start();
            } else if (wifiP2pInfo.groupFormed) {
                connectionStatus.setText("Client");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
             */
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run () {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class SendReceive extends Thread {
        private static final int MESSAGE_READ = 1;
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt) {
            socket = skt;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write (byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread {
        Socket socket;
        String hostAdd;

        public ClientClass (InetAddress hostAddress) {
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run () {
            try {
                socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int randomGame(int borneInf, int borneSup) {
        Random rand = new Random();
        int nb = rand.nextInt(borneSup - borneInf + 1) + borneInf;
        return nb;
    }

    public static int getGame() {
        return game;
    }

    public void games () {
        table_games[0] = randomGame(1,2);
        table_games[1] = randomGame(1,2);
        table_games[2] = randomGame(1,2);
    }
}
