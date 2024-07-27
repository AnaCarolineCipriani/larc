package com.chat.larc;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Lida com a comunicação entre cliente e servidor por meio de sockets.
 * 
 * @author Ana
 * @author Luigi
 * @author Rodrigo
 * @author Rossana
 */
public class MessageClient {

	private String serverAddress;
	private int tcpPort;
	private int udpPort;
	private String userId;
	private String password;
	private ScheduledExecutorService scheduler;

	public MessageClient(String serverAddress, int tcpPort, int udpPort, String userId, String password) {
		this.serverAddress = serverAddress;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		this.userId = userId;
		this.password = password;
		this.scheduler = Executors.newScheduledThreadPool(2);
	}

	/**
	 * Inicia o keepalive para obter a lista de usuários a cada seis
	 * segundos. <br> 
	 * 
	 * Verifica a cada cinco segundos se existem novas mensagens para o
	 * usuário atual.
	 */
	public void start() {
		if (scheduler != null) {
			checkUsers();
			checkMessages();
		}
	}

	protected void checkUsers() {
		scheduler.scheduleAtFixedRate(() -> getUsers(), 0, 6, TimeUnit.SECONDS);
	}
	
	protected void checkMessages() {
		scheduler.scheduleAtFixedRate(() -> getMessage(), 0, 5, TimeUnit.SECONDS);
	}

	/**
	 * Busca os usuários criando uma conexão com o servidor e a porta
	 * TCP. <br>
	 * 
	 * out - envia os dados para o servidor através do println
	 * in - lê os dados recebidos do servidor
	 */
	protected List<Usuario> getUsers() {
		List<Usuario> usuarios = new ArrayList<>();
		
		try (Socket socket = new Socket(serverAddress, tcpPort);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

			String request = "GET USERS " + userId + ":" + password;
			out.println(request);

			String response = in.readLine();
			System.out.println("Usuários ativos: " + response);
			
			
			String[] dadosUsuario = response.split(":");
			for (int i = 0; i < dadosUsuario.length - 1; i += 3) {
	           
				if (i + 2 < dadosUsuario.length) {
	                String userid = dadosUsuario[i];
	                String username = dadosUsuario[i + 1];
	                String wins = dadosUsuario[i + 2];
	                Usuario usuario = new Usuario(userid, username, wins);
	                usuarios.add(usuario);
	            }
	        }
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return usuarios;
	}

	/**
	 * Busca por mensagens enviadas para o usuário atual por meio da conexão com o
	 * servidor e a porta TCP. <br>
	 * 
	 * out - envia os dados para o servidor através do println
	 * in - lê os dados recebidos do servidor
	 */
	protected Map<String, String> getMessage() {
		Map<String, String> messageMap = null;
		
		try (Socket socket = new Socket(serverAddress, tcpPort);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

			String request = "GET MESSAGE " + userId + ":" + password;
			out.println(request);

			String response = in.readLine();
			System.out.println("Message: " + response);
			
			String[] dadosMensagem = response.split(":");
			String userId = dadosMensagem[0];
			String mensagem = dadosMensagem[1];
			
			messageMap = Stream.of(new String[][] {
                {"userId", userId},
                {"message", mensagem},
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return messageMap;
	}

	/**
	 * Envia uma mensagem utilizando a um usuário específico utilizando a porta UDP.
	 * <br>
	 * 
	 * Cria um pacote que contém a requisição e é enviado para o servidor.
	 * 
	 * Espera dez segundos para obter uma resposta do servidor após o envio da
	 * mensagem.
	 * 
	 * @param userIdDestino
	 * @param mensagem
	 * @throws IOException
	 */
	public void sendMessage(String userIdDestino, String mensagem) throws IOException {
		try (DatagramSocket socket = new DatagramSocket()) {

			String request = "SEND MESSAGE " + this.userId + ":" + this.password + ":" + userIdDestino + ":" + mensagem;
			DatagramPacket packet = new DatagramPacket(request.getBytes(), request.length(), InetAddress.getByName(serverAddress), udpPort);
			socket.send(packet);

			byte[] responseBuffer = new byte[1024];
			DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
			socket.setSoTimeout(10000);

			try {
				socket.receive(responsePacket);
				String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
				System.out.println("Resposta recebida: " + response);
			} catch (SocketTimeoutException e) {
				System.out.println("Tempo limite de recebimento expirado.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
