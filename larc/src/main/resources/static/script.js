document.addEventListener("DOMContentLoaded", () => {
    fetchUsers();
    startReceivingMessages();
    setInterval(fetchUsers, 6000);
});

let usuarios = {};

async function fetchUsers() {
    try {
        let response = await fetch("/messages/users");
        let users = await response.json();
        let userList = document.getElementById("userList");

        userList.innerHTML = ""; // limpa a lista antes de atualizar
        users.forEach(user => {
        
        	usuarios[user.userId] = user.userName;
        	
            let primeiraLetra = user.userName.charAt(0).toUpperCase();
            let winIcon = '<img src="images/win.svg" alt="Vitórias" style="width: 20px; height: 20px; vertical-align: middle; margin-left: 5px;">';
            let div = document.createElement("div");
            div.className = "contact";
            div.innerHTML = `
                <div class="contact-icon">${primeiraLetra}</div>
                <div class="contact-text">${user.userName} ${winIcon} ${user.wins}</div>
                <input type="checkbox" name="user" value="${user.userId}" class="contact-checkbox">
            `;
            userList.appendChild(div);
        });
    } catch (error) {
        console.error("Erro ao buscar usuários: ", error);
    }
}

async function startReceivingMessages() {
    try {
        await fetch("/messages/start");
        console.log("Recebendo mensagens");
    } catch (error) {
        console.error("Erro ao receber mensagens: ", error);
    }
}

async function receiveMessage() {
    try {
        let response = await fetch("/messages/start");
        
        const data = await response.json();
        
        let message = usuarios[data.userId] + data.message;
        let messageArea = document.getElementById("messageArea");
        let p = document.createElement("p");
        p.textContent = message;
        messageArea.appendChild(p);
        messageArea.scrollTop = messageArea.scrollHeight;
    } catch (error) {
        console.error("Erro ao receber mensagens: ", error);
    }
}

async function sendMessage() {
    let messageInput = document.getElementById("messageInput");
    let message = messageInput.value.trim();
    let idUsuarioSelecionado = Array.from(document.querySelectorAll('input[name="user"]:checked')).map(cb => cb.value)[0];

    if (message) {

        try {
            let userId = idUsuarioSelecionado || "0";
            await fetch(`/messages/send?userId=${userId}&mensagem=${encodeURIComponent(message)}`, {
                method: "POST"
            });

		
            let messageArea = document.getElementById("messageArea");
            let enviadoPara = userId === "0" ? "Todos" : usuarios[userId];
            confirmaEnvio(messageArea, message, enviadoPara);
            messageArea.scrollTop = messageArea.scrollHeight;
            messageInput.value = "";
            
        } catch (error) {
            console.error("Erro ao enviar mensagem: ", error);
        }
    }
}

function confirmaEnvio(elemento, message, userName) {
    const messageDiv = document.createElement("div");
    let p = document.createElement("p");
    p.classList.add("message-enviada");
    p.innerHTML = `${message}`;

    const statusEnvio = document.createElement("div");
    statusEnvio.className = "status-icon-container";

    const statusIcon = document.createElement("img");
    statusIcon.src = "images/enviada.svg";
    statusIcon.className = "status-icon";
    statusIcon.alt = "Mensagem enviada";

    const tooltip = document.createElement("div");
    tooltip.className = "status-icon-tooltip";
    tooltip.textContent = `Enviado para ${userName}`;

    statusEnvio.appendChild(statusIcon);
    statusEnvio.appendChild(tooltip);
    messageDiv.appendChild(statusEnvio);
	
    p.appendChild(messageDiv);
	elemento.appendChild(p);
}
