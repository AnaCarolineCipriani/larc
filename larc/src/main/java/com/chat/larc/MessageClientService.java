package com.chat.larc;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Serviço que instancia {@link MessageClient} e chama seus métodos
 * 
 * @author Ana
 * @author Luigi
 * @author Rodrigo
 * @author Rossana
 */
@Service
public class MessageClientService {

    private MessageClient messageClient;
    private List<Usuario> usuariosAtivos;
    
    private boolean teste = false;
    private List<String> mensagensTeste;

    public MessageClientService() {
        String serverAddress = "larc.inf.furb.br";
        int tcpPort = 1012;
        int udpPort = 1011;
        String userId = "4732";
        String password = "ihuyt";
        
        messageClient = new MessageClient(serverAddress, tcpPort, udpPort, userId, password);
        mensagensTeste = new ArrayList<>();
        usuariosAtivos = new ArrayList<>();
        startKeepAlive();
        
        setModoTeste();
    }

    public List<Usuario> getUsuariosAtivos() {
    	setUsuariosAtivos();
        return usuariosAtivos;
    }
    
    public void setUsuariosAtivos() {
    	List<Usuario> usuarios = messageClient.getUsers();
    	if (usuarios != null
    			&& !usuarios.isEmpty()) {
    		this.usuariosAtivos = usuarios;
    	}
	}
    
    public void setModoTeste() {
    	teste = true;
    	usuariosAtivos.add(new Usuario("yfdmf", "Rossana Rocha da Silva", "0"));
    	usuariosAtivos.add(new Usuario("wcchs", "Rodrigo Kapulka Franco", "0"));
    	usuariosAtivos.add(new Usuario("nywfg", "Luigi Garcia Marchetti", "0"));
    }

    private void startKeepAlive() {
        messageClient.checkUsers();
    }

	public void startMessages() {
		new Thread(() -> messageClient.checkMessages()).start();
	}

    public void sendMessage(String userId, String mensagem) throws IOException {
        if (teste) {
        	mensagensTeste.add(mensagem);
        } else {
            messageClient.sendMessage(userId, mensagem);
        }
    }

}
