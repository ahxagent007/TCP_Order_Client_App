package com.dexian.tcporderclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    String IP = "192.168.0.104";
    int PORT = 6969;

    private Timer timer;

    infoData infoData;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_settings = findViewById(R.id.btn_settings);
        LV_itemAvailable = findViewById(R.id.LV_itemAvailable);
        ET_tableNo = findViewById(R.id.ET_tableNo);

        // Create a new instance of Gson
        Gson gson = new Gson();


        OrderList orderList[] = new OrderList[5];
        ItemList itemList[] = new ItemList[10];

        orderList[0] = new OrderList("Burger", 10, "Table#05");
        orderList[1] = new OrderList("Chefs Special", 50, "Table#71");
        orderList[2] = new OrderList("Chicken Msala Curry", 16, "Table#12");
        orderList[3] = new OrderList("Chowmeen", 19, "Table#06");
        orderList[4] = new OrderList("Chicken Burger", 32, "Table#08");

        itemList[0] = new ItemList("Burger", 100);
        itemList[1] = new ItemList("Chefs Special", 600);
        itemList[2] = new ItemList("Chicken Msala Curry", 150);
        itemList[3] = new ItemList("Chowmeen", 1000);
        itemList[4] = new ItemList("Chicken Burger", 150);

        itemList[5] = new ItemList("Burger", 100);
        itemList[6] = new ItemList("Chefs Special", 600);
        itemList[7] = new ItemList("Chicken Msala Curry", 150);
        itemList[8] = new ItemList("Chowmeen", 1000);
        itemList[9] = new ItemList("Chicken Burger", 150);

        infoData = new infoData(orderList, itemList);

        String sampleJson = gson.toJson(infoData);
        Log.i("XIAN", "sampleJson = " + sampleJson);



        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackgroundTask backgroundTask = new BackgroundTask();
                backgroundTask.execute("{data:'dadasdd', data2:'dadasdsad'}");

            }
        });


        timer = new Timer();
        //Set the schedule function and rate
        timer.scheduleAtFixedRate(new TimerTask() {

              @Override
              public void run() {

              }

          },
        //Set how long before to start calling the TimerTask (in milliseconds)
        0,
        //Set the amount of time between each execution (in milliseconds)
        1000);


        adapter = new CustomAdapter(getApplicationContext(), infoData.getItemList());
        LV_itemAvailable.setAdapter(adapter);

        /*
        //Start Server

        Thread thread = new Thread(new PersonalServer());
        thread.start();

        */

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        timer.cancel();
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        Socket socket;
        DataOutputStream dataOutputStream;

        @Override
        protected String doInBackground(String... msg) {

            String data = msg[0];

            try {
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

                    /*new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                        }
                    });*/

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                        }
                    });



                }


            } catch (IOException e) {
                Log.i("XIAN", "ERROR PersonalServer : "+e);
                e.printStackTrace();
            }
        }
    }

    public class CustomAdapter extends BaseAdapter {
        private Context context;
        private ItemList[] itemList;

        private TextView TV_itemName, TV_itemPrice;
        private EditText ET_itemQuantity;
        Button btn_order;

        public CustomAdapter(Context context, ItemList[] itemList) {
            this.context = context;
            this.itemList = itemList;
        }
        @Override
        public int getCount() {
            return itemList.length;
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
        public View getView(int position, View view, ViewGroup parent) {
            view = LayoutInflater.from(context).inflate(R.layout.single_item_list, parent, false);

            //add data to UI
            TV_itemName = view.findViewById(R.id.TV_itemName);
            TV_itemPrice = view.findViewById(R.id.TV_itemPrice);
            ET_itemQuantity = view.findViewById(R.id.ET_itemQuantity);
            btn_order = view.findViewById(R.id.btn_order);

            TV_itemName.setText(itemList[position].getItemName());
            TV_itemPrice.setText(""+itemList[position].getItemPrice());

            return view;
        }
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
