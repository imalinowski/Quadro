package com.malinowski.quadro;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import androidx.annotation.RequiresApi;

class Client extends Thread {

    static int[] ip = {192,168,1,186};//IP для подключения
    static int port = 8888;//порт для подключения

    private String message = null;
    private  Socket server;
    private OutputStream out;
    static boolean isConnected = false;
    @RequiresApi
    public void run() {
        try
        {
            //здесь устанавливается соединение с сервером
            MainActivity.info.setText("Trying to connect...");
            InetAddress ipAdress = InetAddress.getByName(ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3]);//IP для подключения
            server = new Socket(ipAdress,port);

            //out = new PrintWriter(server.getOutputStream(), true);
            out = new DataOutputStream(server.getOutputStream());
            //BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            InputStream in = new DataInputStream(server.getInputStream());

            isConnected = true;
            MainActivity.info.setText("Connection success!");
            MainActivity.point.setImageResource(R.drawable.green_point);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer;
            byte[] result;
            //прием данных происходит в байтах
            while(server.isConnected() && isConnected && MainActivity.client == this) {
                baos.reset();
                //при приеме картинки первым остсылается ее размер, чтобы знать какого объема последующий пакет
                byte[] sizeAr = new byte[4];
                in.read(sizeAr);
                int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                buffer = new byte[size];
                baos.write(buffer, 0 , in.read(buffer));
                result = baos.toByteArray();
                //message = Arrays.toString(result);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                //полученные байты конвертируются в изображение и если исходые данные представляли картинку - она будет установлена
                //в ImageView по центру основной активити
                Bitmap decodedByte = BitmapFactory.decodeByteArray(result, 0, result.length,options);
                MainActivity.image.setImageBitmap(decodedByte);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    message = new String(result, StandardCharsets.UTF_8);
                }

                MainActivity.info.setText("server :" + message);

            }
        }
        catch(Throwable cause){
            Log.e("Error","Error in Client Thread! : " + cause.getMessage());
        }
        try {
            if(server != null && server.isConnected())
                server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainActivity.point.setImageResource(R.drawable.red_point);
        isConnected = false;
    }

    //посылка рысканье,газ,крен,тангаж,режим
    void send(int yaw, int throttle,int pitch, int roll, int mode){
        final byte[] bytedata = new byte[6];

        bytedata[0] = (byte)0xFF;
        bytedata[1] = (byte)yaw;
        bytedata[2] = (byte)throttle;
        bytedata[3] = (byte)pitch;
        bytedata[4] = (byte)roll;
        bytedata[5] = (byte)mode;

        Thread myThread = new Thread(
                new Runnable(){
                    public void run(){
                        try {
                            out.write(bytedata);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        myThread.start();

    }
}
