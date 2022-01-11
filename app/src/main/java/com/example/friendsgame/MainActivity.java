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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.example.friendsgame.data.User;
import com.example.friendsgame.other.SharedPref;
import com.example.friendsgame.temporary.FinishedScreen;
import com.example.friendsgame.temporary.LoadingScreen;
import com.example.friendsgame.temporary.VictoryScreen;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    public static String myName;
    public static int myScore = 0;
    public static Map<String, Integer> scoreGamers, stateGamers;
    public static ArrayList<String> listGamers;
    public static boolean finished = false;

    public static int game, GAME_COUNT = 3;
    Button btPlay;
    CardView cvWifi, cvExplanations, cvSearch, cvLogout, cvPractice;
    TextView tvStatus, tvWifi, tvHello;

    public ListView devices;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    static List<String> devicesConnected = new ArrayList<String>();
    WifiP2pDevice[] deviceArray;
    public static String[] rankingGamers;
    public ArrayAdapter<String> adapter;
    public static ArrayAdapter<String> adapterRanking;

    static final int MESSAGE_READ = 1;
    ServerClass serverClass;
    ClientClass clientClass;
    static SendReceive sendReceive;

    public static int[] table_games = new int[3]; //new int[6] à la fin

    SharedPref sharedPref;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_WIFI_STATE", "android.permission.CHANGE_WIFI_STATE", "android.permission.CHANGE_NETWORK_STATE", "android.permission.ACCESS_NETWORK_STATE"};
        this.requestAllPermissions(permissions);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sharedPref = SharedPref.getInstance();
        user = sharedPref.getUser(this);
        myName = user.getUsername();

        System.out.println("myName : " + myName);

        init();
        listeners();
    }

    /*
    Récupère le message
     */
    Handler handler = new Handler(new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.N)
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
                        switch (type) {
                            //Le jeu démarre, on récupère les différents jeux
                            case "start":
                                String game1 = obj.getString("game1");
                                String game2 = obj.getString("game2");
                                String game3 = obj.getString("game3");
                                /*
                                String game4 = obj.getString("game4");
                                String game5 = obj.getString("game5");
                                String game6 = obj.getString("game6");
                                */
                                System.out.println("Game 1 : " + game1);
                                System.out.println("Game 2 : " + game2);
                                System.out.println("Game 3 : " + game3);
                                /*
                                System.out.println("Game 4 : " + game4);
                                System.out.println("Game 5 : " + game5);
                                System.out.println("Game 6 : " + game6);
                                */
                                table_games[0] = Integer.parseInt(game1);
                                table_games[1] = Integer.parseInt(game2);
                                table_games[2] = Integer.parseInt(game3);
                                /*
                                table_games[3] = Integer.parseInt(game3);
                                table_games[4] = Integer.parseInt(game4);
                                table_games[5] = Integer.parseInt(game5);
                                */
                                Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                                startActivity(loading);
                                break;
                            //On récupère notre nom
                            case "gamer":
                                String str = obj.getString("name");
                                if (!(scoreGamers.containsKey(str) && stateGamers.containsKey(str))) {
                                    String name = "{ \"type\": \"gamer\", \"name\": "+ myName+"}";
                                    sendReceive.write(name.getBytes());
                                    scoreGamers.put(str, 0);
                                    stateGamers.put(str, 0);
                                    listGamers.add(str);
                                    System.out.println("Name reçu : " + str);
                                    System.out.println("Gamers : " + stateGamers.toString());
                                }
                                break;
                            //Le score des joueurs
                            case "game":
                                String gamer = obj.getString("name");
                                int score = Integer.parseInt(obj.getString("score"));
                                System.out.println("Score/ "+ gamer +" : " + score);
                                scoreGamers.replace(gamer, (scoreGamers.get(gamer)+score));
                                System.out.println("Score Gamers : " + scoreGamers.toString());
                                break;
                            case "finished":
                                String gamerFinished = obj.getString("name");
                                stateGamers.replace(gamerFinished, 1);
                                int finalScore = Integer.parseInt(obj.getString("score"));
                                scoreGamers.replace(gamerFinished, (scoreGamers.get(gamerFinished)+finalScore));
                                System.out.println("Score Gamers : " + scoreGamers.toString());
                                if (allFinished() && finished ) {
                                    determineRanking(getApplicationContext());
                                    Intent finished = new Intent(getApplicationContext(), FinishedScreen.class);
                                    startActivity(finished);
                                    finish();
                                }
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
        cvExplanations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent welcomeActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(welcomeActivity);
                finish();
            }
        });
        cvWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    tvWifi.setTextColor(Color.parseColor("#CF3030"));
                } else {
                    wifiManager.setWifiEnabled(true);
                    tvWifi.setTextColor(Color.parseColor("#50AE28"));
                }
            }
        });
        cvSearch.setOnClickListener(new View.OnClickListener() {
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
                        /*
                        scoreGamers.put(device.deviceName, 0);
                        stateGamers.put(device.deviceName, 0);
                        nameGamers.put(device.deviceName, "");
                         */
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
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateGames();
                System.out.println("Game 1 : " + table_games[0]);
                System.out.println("Game 2 : " + table_games[1]);
                System.out.println("Game 3 : " + table_games[2]);
                System.out.println("number game : " + game);
                System.out.println("GAME_COUNT : " + GAME_COUNT);
                if (devicesConnected.size() == 0) {
                    System.out.println("Aucun device connecté");
                    Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                    startActivity(loading);
                    finish();
                } else {
                    String msg = "{ \"type\": \"gamer\", \"name\": "+ myName+"}";
                    sendReceive.write(msg.getBytes());
                    System.out.println("Un ou plusieurs devices connectés");
                    String intGame1 = String.valueOf(table_games[0]);
                    String intGame2 = String.valueOf(table_games[1]);
                    String intGame3 = String.valueOf(table_games[2]);
                    msg = "{ \"name\": "+myName+", \"type\": \"start\", \"game1\": "+ intGame1+", \"game2\": "+ intGame2 +", \"game3\": "+ intGame3+"}";
                    //\"game4\": "+ intGame4+"\"game5\": "+ intGame5+"\"game6\": "+ intGame6+"}";
                    sendReceive.write(msg.getBytes());
                    Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                    startActivity(loading);
                    finish();
                }
            }
        });

        cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref.clearSharedPref(MainActivity.this);
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cvPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PracticeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /*
    L'initialisation des composants et des outils dont on a besoin pour le WiFi P2P
     */
    private void init () {

        btPlay = findViewById(R.id.bt_play);
        cvExplanations = findViewById(R.id.cv_explanations);
        tvStatus = findViewById(R.id.tv_status);
        devices = findViewById(R.id.lv_devices);
        cvWifi = findViewById(R.id.cv_wifi);
        cvSearch = findViewById(R.id.cv_search);
        cvLogout = findViewById(R.id.cv_logout);
        cvPractice = findViewById(R.id.cv_practice);

        tvHello = findViewById(R.id.tv_hello);
        tvHello.setText("Hello, " + user.getUsername());

        tvWifi = findViewById(R.id.tv_wifi);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            tvWifi.setTextColor(Color.parseColor("#50AE28"));
        } else {
            tvWifi.setTextColor(Color.parseColor("#CF3030"));
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
        stateGamers = new HashMap<String, Integer>();

        listGamers = new ArrayList<String>();
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
                tvStatus.setText("Status: Host");
                serverClass = new ServerClass();
                serverClass.start();
                /*
                Je veux changer la liste et mettre quels appareils sont connectés
                 */
                //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                //devices.setAdapter(adapter);
            } else if (wifiP2pInfo.groupFormed) {
                tvStatus.setText("Status: Client");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
                //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                //devices.setAdapter(adapter);
            }
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

    public void generateGames() {
        table_games[0] = randomGame(1,5);
        /*table_games[1] = tableGamesRandom();
        table_games[2] = tableGamesRandom();*/
        table_games[1] = randomGame(1,5);
        table_games[2] = randomGame(1,5);
    }

    public boolean tableGamesContains(int game) {
        for (int tmp : table_games) {
            if (tmp == game) {
                return true;
            }
        }
        return false;
    }

    public int tableGamesRandom () {
        int tmp = randomGame(1, 5);
        while (tableGamesContains(tmp)) {
            tmp = randomGame(1, 5);
        }
        return tmp;
    }

    /*
    True : perdu
    False : gagné
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean determineWinner () {
        Iterator it = scoreGamers.keySet().iterator();
        int winner = scoreGamers.get(it.next());
        int tmp;
        while (it.hasNext()) {
            tmp = scoreGamers.get(it.next());
            if (winner < tmp) {
                winner = tmp;
            }
        }
        return myScore < winner;
        /*
        if (myScore < winner) {
            return getSingleKeyFromValue(scoreGamers, winner);
        } else {
            return myName;
        }
         */
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayAdapter<String> determineRanking(Context context) {
        scoreGamers.put(myName, myScore);
        List<Integer> list = new ArrayList<Integer>(scoreGamers.values());
        Collections.sort(list);
        System.out.println("List : " + list);
        int index = 0;
        rankingGamers = new String[list.size()];
        ListIterator li = list.listIterator(list.size());
        // Iterate in reverse.
        while(li.hasPrevious()) {
            int tmp = (int) li.previous();
            rankingGamers[index] = getSingleKeyFromValue(scoreGamers, tmp) + " (Score: " + tmp + ")";
            index++;
        }
        System.out.println("Ranking:");
        for (String str : rankingGamers) {
            System.out.println(str);
        }

        adapterRanking = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, rankingGamers);
        return adapterRanking;
    }

    public static boolean allFinished () {
        for (String gamer : stateGamers.keySet()) {
            if (stateGamers.get(gamer) == 0) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getSingleKeyFromValue(Map<String, Integer> map, int value) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void reset () {
        myScore = 0;
        GAME_COUNT = 3;
        stateGamers.clear();
        scoreGamers.clear();
    }



}
