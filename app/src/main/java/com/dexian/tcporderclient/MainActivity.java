package com.dexian.tcporderclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {


    Button btn_settings;
    EditText ET_tableNo;
    ListView LV_itemAvailable;

    String IP = "192.168.0.104";
    int PORT = 6969;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_settings = findViewById(R.id.btn_settings);

        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackgroundTask backgroundTask = new BackgroundTask();
                backgroundTask.execute("{data:'dadasdd', data2:'dadasdsad'}");

            }
        });

        /*
        //Start Server

        Thread thread = new Thread(new PersonalServer());
        thread.start();

        */


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




}
