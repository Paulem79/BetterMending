package net.paulem.btm.libs.k3kdude;

import com.google.gson.JsonObject;
import lombok.*;
import net.paulem.btm.BetterMending;

import javax.net.ssl.HttpsURLConnection;
import javax.validation.constraints.*;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Builder
@AllArgsConstructor
public class DiscordWebhook {

    // Cosmetic Details
    @Getter @Setter @Size(max = 2048) private String content;

    // Sender details
    @Getter @NotBlank private String username;

    // Delivery Details
    @Getter @NotBlank private String sendTo;

    @SneakyThrows
    public boolean send() {
        if(!BetterMending.instance.getConfig().getBoolean("crashlogSending", true)) {
            return false;
        }

        JsonObject message = new JsonObject();

        message.addProperty("username", this.username);
        message.addProperty("content", this.content);

        URL url = new URL(this.sendTo);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Supreme-Webhook-Savag3life");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        @Cleanup OutputStream stream = connection.getOutputStream();
        stream.write(message.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();

        connection.getInputStream().close();
        connection.disconnect();
        return true;
    }
}