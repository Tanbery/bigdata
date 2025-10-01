package com.datagen;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import com.util.MyApp;

import java.net.ServerSocket;

public class DataGenFromRnd {
	public static void main(String[] args) throws IOException {

		DataGenFromRnd.genRandom(MyApp.portNumber);

	}

	public static void genRandom(int portNumber) throws IOException {
		ServerSocket listener = new ServerSocket(portNumber);
		try {
			Socket socket = listener.accept();
			System.out.println("Got new connection: " + socket.toString());
			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				Random rand = new Random();
				int count = 0;
				while (true) {
					String s = "" + System.currentTimeMillis() + "," + rand.nextInt(100);
					System.out.println(s);
					/* <timestamp>,<random-number> */
					out.println(s);
					Thread.sleep(50);

					if (count > 10) {
						Thread.sleep(10000);
						count = 0;
					}
					count++;

				}

			} finally {
				socket.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			listener.close();
		}
	}

}
