var slyaksocket = function (topics) {
    var stompClient = Stomp.over(new SockJS('/websocket'));
    var connected = false;
    return {
        connect: function () {
            stompClient.connect({}, function (frame) {
                connected = true;
                console.log('Connected: ' + frame);
                topics.forEach(function (topic, index, arr) {
                    stompClient.subscribe(topic.name, function (greeting) {
                        window[topic.callback](greeting);
                    });
                });
            });
        },
        send: function (destination, message) {
            if (this.isConnected()) {
                message = typeof message == 'string' ? message : JSON.stringify(message);
                stompClient.send(destination, message);
            }
        },

        disconnect: function () {
            if (stompClient !== null) {
                stompClient.disconnect();
            }
        },
        isConnected: function () {
            return connected;
        }
    }
};