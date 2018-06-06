var slyaksocket = function (topics) {
    var stompClient = Stomp.over(new SockJS('/websocket'));
    var connected = false;
    return {
        connect: function (callback) {
            stompClient.connect({}, function (frame) {
                connected = true;
                console.log('Connected: ' + frame);
                topics.forEach(function (topic, index, arr) {
                    stompClient.subscribe(topic.name, function (greeting) {
                        window[topic.callback](greeting);
                    });
                });
                if (callback) {
                    callback();
                }
            });
        },
        send: function (destination, message) {
            if (this.isConnected()) {
                message = typeof message == 'string' ? message : JSON.stringify(message);
                //destination userNameAndPwd message
                stompClient.send(destination, {}, message);
            } else {
                alert("not connected yet! pls add this to connect callback!")
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