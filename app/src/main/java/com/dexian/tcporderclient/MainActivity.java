package com.dexian.tcporderclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {


    Button btn_settings;
    EditText ET_tableNo;
    ListView LV_itemAvailable;

    String IP = "192.168.0.100";
    int PORT = 6969;

    private Timer timer;

    InfoData infoData;
    CustomAdapter adapter;

    Gson gson;

    List<OrderList> orderList = new ArrayList<OrderList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IP = getIP();

        btn_settings = findViewById(R.id.btn_settings);
        LV_itemAvailable = findViewById(R.id.LV_itemAvailable);
        ET_tableNo = findViewById(R.id.ET_tableNo);

        // Create a new instance of Gson
        gson = new Gson();



        List<ItemList> itemList = new ArrayList<ItemList>();

        infoData = new InfoData(orderList, itemList);


        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.setting_dialog);
                //dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                final EditText ET_IP = dialog.findViewById(R.id.ET_IP);
                Button btn_save = dialog.findViewById(R.id.btn_save);

                ET_IP.setText(getIP());


                // if button is clicked, close the custom dialog
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!ET_IP.getText().toString().equals("")){

                            IP = ET_IP.getText().toString();

                            setIP(IP);

                            dialog.dismiss();

                        }else{
                            Toast.makeText(getApplicationContext(), "IP missing", Toast.LENGTH_LONG).show();
                        }



                    }
                });


                dialog.setCancelable(true);
                dialog.getWindow().setLayout(((getWidth(getApplicationContext()) / 100) * 90), ((getHeight(getApplicationContext()) / 100) * 50));
                dialog.getWindow().setGravity(Gravity.CENTER);

                dialog.show();


            }
        });


        timer = new Timer();
        //Set the schedule function and rate
        timer.scheduleAtFixedRate(new TimerTask() {

              @Override
              public void run() {
                 syncData();
              }

          },
        //Set how long before to start calling the TimerTask (in milliseconds)
        0,
        //Set the amount of time between each execution (in milliseconds)
        5000);


        adapter = new CustomAdapter(getApplicationContext(), infoData.getItemList());
        LV_itemAvailable.setAdapter(adapter);


        //Start Server
        Thread thread = new Thread(new PersonalServer());
        thread.start();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        timer.cancel();
    }

    private void syncData(){
        String infoDataJson = gson.toJson(infoData);

        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(infoDataJson);

        IP = getIP();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //adapter.notifyDataSetChanged();
                adapter = new CustomAdapter(getApplicationContext(), infoData.getItemList());
                LV_itemAvailable.setAdapter(adapter);
            }
        });


        Log.i("XIAN", "infoDataJson = " + infoDataJson);

    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        Socket socket;
        DataOutputStream dataOutputStream;

        @Override
        protected String doInBackground(String... msg) {

            String data = msg[0];

            try {
                Log.i("XIAN", "SENDING DATA Client");
                socket = new Socket(IP, PORT);

                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(data);


                dataOutputStream.close();
                socket.close();

            } catch (IOException e) {
                Log.i("XIAN", "ERROR ** BackgroundTask : "+e);
                e.printStackTrace();
            }

            return null;
        }
    }

    class PersonalServer implements Runnable{

        ServerSocket serverSocket;
        Socket socket;
        DataInputStream dataInputStream;

        String data;


        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(PORT);

                Log.i("XIAN", "Waiting for client DATA");

                while (true){
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    data = dataInputStream.readUTF();


                    /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                        }
                    });*/

                    infoData = gson.fromJson(data, InfoData.class);
                    infoData.setOrderList(orderList);

                    Log.i("XIAN", "CLIENT INFODATA RECEIVE : "+data);

                }


            } catch (IOException e) {
                Log.i("XIAN", "ERROR PersonalServer : "+e);
                e.printStackTrace();
            }
        }
    }

    public class CustomAdapter extends BaseAdapter {
        private Context context;
        private List<ItemList> itemList;

        private TextView TV_itemName, TV_itemPrice;
        Button btn_order;

        public CustomAdapter(Context context, List<ItemList> itemList) {
            this.context = context;
            this.itemList = itemList;
        }
        @Override
        public int getCount() {
            return itemList.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            view = LayoutInflater.from(context).inflate(R.layout.single_item_list, parent, false);


            //add data to UI
            TV_itemName = view.findViewById(R.id.TV_itemName);
            TV_itemPrice = view.findViewById(R.id.TV_itemPrice);
            btn_order = view.findViewById(R.id.btn_order);

            TV_itemName.setText(itemList.get(position).getItemName());
            TV_itemPrice.setText(""+itemList.get(position).getItemPrice());

            btn_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // custom dialog
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.quantity);
                    //dialog.setTitle("Title...");

                    // set the custom dialog components - text, image and button
                    TextView TV_confirmName =  dialog.findViewById(R.id.TV_confirmName);
                    final EditText ET_itemQunatity = dialog.findViewById(R.id.ET_itemQunatity);
                    Button btn_confirmORder = dialog.findViewById(R.id.btn_confirmORder);

                    TV_confirmName.setText("Android custom dialog example!");


                   // if button is clicked, close the custom dialog
                    btn_confirmORder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {



                            if(!ET_itemQunatity.getText().toString().equals("") && !ET_tableNo.getText().toString().equals("")){

                                Log.i("XIAN", "Q = "+ET_itemQunatity.getText().toString()+" T = "+ET_tableNo.getText().toString());

                                int quantity = Integer.parseInt(ET_itemQunatity.getText().toString());
                                int table = Integer.parseInt(ET_tableNo.getText().toString());

                                addOrder(position, quantity, table);

                                dialog.dismiss();


                            }else{
                                Toast.makeText(getApplicationContext(), "Quantity or Table missing", Toast.LENGTH_LONG).show();
                            }



                        }
                    });


                    dialog.setCancelable(true);
                    dialog.getWindow().setLayout(((getWidth(context) / 100) * 90), ((getHeight(context) / 100) * 50));
                    dialog.getWindow().setGravity(Gravity.CENTER);

                    dialog.show();

                }
            });


            return view;
        }
    }

    private void addOrder(int id, int qaunt, int table){

        infoData.getOrderList().add(new OrderList(infoData.getItemList().get(id).getItemName(), qaunt, table));
        Toast.makeText(getApplicationContext(), "ORDER DONE", Toast.LENGTH_LONG).show();

        orderList = infoData.getOrderList();

        syncData();

    }

    public static int getWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private void setIP(String ip){
        SharedPreferences mSharedPreferences = getSharedPreferences("DATA", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString("IP",ip);

        mEditor.apply();
    }

    private String getIP(){
        SharedPreferences mSharedPreferences = getSharedPreferences("DATA", MODE_PRIVATE);
        String ip = mSharedPreferences.getString("IP","192.168.0.100");

        return ip;
    }

    /*
     // Create a new instance of Gson
        Gson gson = new Gson();

        // Convert numbers array into JSON string.
        String numbersJson = gson.toJson(numbers);

        // Convert strings array into JSON string
        String daysJson = gson.toJson(days);
        System.out.println("numbersJson = " + numbersJson);
        System.out.println("daysJson = " + daysJson);

        // Convert from JSON string to a primitive array of int.
        int[] fibonacci = gson.fromJson(numbersJson, int[].class);
        for (int number : fibonacci) {
            System.out.print(number + " ");
        }
        System.out.println("");

        // Convert from JSON string to a string array.
        String[] weekDays = gson.fromJson(daysJson, String[].class);
        for (String weekDay : weekDays) {
            System.out.print(weekDay + " ");
        }
        System.out.println("");

        // Converting multidimensional array into JSON
        int[][] data = {{1, 2, 3}, {3, 4, 5}, {4, 5, 6}};
        String json = gson.toJson(data);
        System.out.println("Data = " + json);

        // Convert JSON string into multidimensional array of int.
        int[][] dataMap = gson.fromJson(json, int[][].class);
        for (int[] i : dataMap) {
            for (int j : i) {
                System.out.print(j + " ");
            }
            System.out.println("");
        }
    */


}
