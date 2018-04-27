var slyaksocket = function (topics) {
    var stompClient = Stomp.over(new SockJS('/websocket'));
    var failedMsg = [];
    var connected = false;
    return {
        reSend: function () {
            var _this = this;
            var _send = _this.send;
            var msg;
            while ((msg = failedMsg.shift()) != undefined) {
                _send.call(_this, msg.destination, msg.message);
            }
        },
        connect: function () {
            var _this = this;
            var _reSend = _this.reSend;
            stompClient.connect({"user": "sdfasf"}, function (frame) {
                connected = true;
                console.log('Connected: ' + frame);
                topics.forEach(function (topic, index, arr) {
                    stompClient.subscribe(topic.name, function (greeting) {
                        window[topic.callback](greeting);
                    });
                });
                setTimeout(function () {
                    _reSend.call(_this);
                }, 500)
            });
        },
        send: function (destination, message, headers) {
            console.log(headers);
            if (this.isConnected()) {
                message = typeof message == 'string' ? message : JSON.stringify(message);
                stompClient.send(destination, headers, message);
            } else {
                //store
                failedMsg.push({destination: destination, message: message})
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