const broker = 'ws://192.168.0.221:9001';
const topic = '/cctv/objects';

const client = mqtt.connect(broker);

client.on("connect", () => {
    console.log("Connected to broker");
    client.subscribe(topic, (err) => {
        if(!err) {
            console.log(`Subscribed to topic: ${topic}`);
        }
    })
})

client.on("message", (topic, message) => {
    try {
        const payload = JSON.parse(message.toString());
        const base64Image = payload.image;
        const img = document.getElementById("cctv_detect_view");
        img.src = `data:image/jpg;base64,${base64Image}`;
    }
    catch(e) {
        console.error(`Failed to parse message: ` ,e);
    }
})

client.on("error", (error) => {
    console.error("connection error : " ,error);
})

client.on("close", ()=> {
    console.log("Disconnected from broker");
})