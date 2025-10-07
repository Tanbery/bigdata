package com.udemy.datagen;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.udemy.util.MyApp;

public class DataGenFromFile {

	public static void main(String[] args) throws IOException {

		// DataGenFromFile.genFromFile("avg", MyApp.portNumber);
		DataGenFromFile.genFromFile("avg", MyApp.portNumber);
	}

	public static void genFromFile(String filename, int portNumber) throws IOException {
		ServerSocket listener = new ServerSocket(portNumber);
		try {
			Socket socket = listener.accept();
			System.out.println("Got new connection: " + socket.toString());

			ArrayList<String> text = MyApp.readFile(filename);

			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

				for (int i = 0; i < text.size(); i++) {
					System.out.println(text.get(i));
					out.println(text.get(i));
					Thread.sleep(50);
					if (i % 10 == 0)
						Thread.sleep(200);
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
