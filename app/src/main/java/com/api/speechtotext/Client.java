package com.api.speechtotext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends Thread
{
    private int serverPort = 5051;
    private String serverIP = "100.107.53.197";
    private Socket clientSocket = null;
    private ReentrantLock lock;
    private Condition condition;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean run = true;
    private boolean wait = true;
    String message = "";

    Client()
    {
        lock = new ReentrantLock();
        condition = lock.newCondition();

    }

    private void connect()
    {
        if(clientSocket == null)
        {
            try
            {
                clientSocket = new Socket(serverIP, serverPort);

                out = new DataOutputStream(clientSocket.getOutputStream());
                //in = new DataInputStream(clientSocket.getInputStream());
            }
            catch (IOException e)
            {
                System.out.println("Eccezione");
                e.printStackTrace();
            }
        }
    }

    public void send(String message)
    {
        try {
            out.writeUTF(message);
            out.flush();
            out.close();
            //out.writeBytes(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection()
    {
        if(clientSocket != null)
        {
            try
            {
                out.close();
                clientSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {this.run = false;}
    public void setMessage(String message) {this.message = message;}

    public void setWait(boolean wait)
    {
        if(wait == true)
        {
            this.wait = wait;
        }
        else {
            this.wait = false;
            //notifyAll();
        }

    }

    @Override
    public void run()
    {
        connect();
        lock.lock();
        while(run)
        {
            send(message);
            while(wait)
            {
                System.out.println("Wait="+wait);
                try
                {
                    condition.await();
                    System.out.println("sono in wait");
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        send("!DISCONNECT!");
        lock.unlock();
    }
}
