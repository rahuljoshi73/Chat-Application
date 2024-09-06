let stompClient = null;

function setConnected(connected) {
    // $("#connect").prop("disabled", connected);
    // $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    const socket = new SockJS("/stomp-endpoint");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log("Connected: " + frame);

        // Subscribe to the user-specific destination for private messages
        stompClient.subscribe("queue/mess/" + getChatIdFromUrl(), function (message) {
            showGreeting(JSON.parse(message.body));
        });

    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");

    // Redirect to localhost:8080/user/main
    window.location.href = "http://localhost:8080/user/main";
}

function sendPrivateMessage(chatId, message) {
    const privateMessage = {
        message: message
    };

    stompClient.send("/app/chat/" + chatId, {}, JSON.stringify(privateMessage));
}

function sendName() {
    const message = getSenderName() + ":" + $("#chat").prop("value");


    if (getChatIdFromUrl() && message) {
        sendPrivateMessage(getChatIdFromUrl(), message);
        $("#chat").val("");
    }
}


    function getSenderName() {
        const senderNameElement = document.getElementById("senderName");
        return senderNameElement.getAttribute("data-sendername");
    }

function showGreeting(message) {
    console.log(message);

    $("#conversation").append("<tr><td>" + message + "</td></tr>");// Update the message format

}


function getChatIdFromUrl() {
        const currentURL = window.location.href;
        const pathSegments = currentURL.split('/');

        // Assuming the chat ID is located at a specific index in the URL path
        const chatIdIndex = pathSegments.indexOf("chatRoom") + 1;
        return pathSegments[chatIdIndex];
    }

    $(document).ready(function () {
        connect();

        $("form").on("submit", function (e) {
            e.preventDefault();
        });

        // $( "#connect" ).click(function() { connect(); });
        $("#disconnectButton").click(function () {
            disconnect();
        });
        $("#send").click(function () {
            sendName();
        });
    });

