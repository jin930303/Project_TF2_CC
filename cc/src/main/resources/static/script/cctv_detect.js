    document.addEventListener("DOMContentLoaded", function() {
        const broker = "ws://localhost:9001"; // WebSocket 방식 (포트 확인 필요)
        const topic = "/camera/objects";

        const client = mqtt.connect(broker);

        client.on("connect", () => {
            console.log("Connected to broker");
            client.subscribe(topic, (err) => {
                if (!err) {
                    console.log(`Subscribed to topic: ${topic}`);
                }
            });
        });

        client.on("message", (topic, message) => {
            try {
                const payload = JSON.parse(message.toString());
                const base64Image = payload.image;
                const img = document.getElementById("cctv_view");
                img.src = `data:image/jpg;base64,${base64Image}`;
            } catch (e) {
                console.error("Failed to parse message: ", e);
            }
        });

        client.on("error", (error) => {
            console.error("Connection error: ", error);
        });

        client.on("close", () => {
            console.log("Disconnected from broker");
        });
    });
