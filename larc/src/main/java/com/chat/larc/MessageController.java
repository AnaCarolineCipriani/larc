package com.chat.larc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST que gerencia as requisições HTTPs relacionadas às mensagens.
 * 
 * @author Ana
 * @author Luigi
 * @author Rodrigo
 * @author Rossana
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

	@Autowired
	private MessageClientService messageClientService;

	@GetMapping("/start")
	public void iniciaRecebimentoMensagens() {
		messageClientService.startMessages();
	}

	@GetMapping("/users")
	public List<Map<String, String>> getUsuariosAtivos() {
		List<Usuario> usuariosAtivos = messageClientService.getUsuariosAtivos();

		if (usuariosAtivos != null) {
			return usuariosAtivos.stream()
					.map(user -> Map.of("userId", user.getUserid(), 
							"userName", user.getUsername(), 
							"wins", user.getWins()))
					.collect(Collectors.toList());
		}
		return null;
	}

	@PostMapping("/send")
	public String enviaMensagem(@RequestParam String userId, @RequestParam String mensagem) {
		try {
			messageClientService.sendMessage(userId, mensagem);
			return "Mensagem enviada";
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "Falha ao enviar a mensagem";
		}
	}
}
