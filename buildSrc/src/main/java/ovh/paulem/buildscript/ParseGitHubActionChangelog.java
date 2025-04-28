package ovh.paulem.buildscript;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ParseGitHubActionChangelog {

    public static String getChangelog() throws Throwable {
        final String path = System.getenv("GITHUB_EVENT_RAW_PATH");
        if (path == null || StringUtils.isBlank(path)) return "No changelog was specified. ";
        final JsonObject jsonObject = new Gson().fromJson(new String(Files.readAllBytes(Paths.get(path))), JsonObject.class);

        StringBuilder builder = new StringBuilder();
        builder.append("This version is uploaded automatically by GitHub Actions.  \n\n")
                .append("Changelog:  \n");
        @Nullable final JsonArray commits = jsonObject.getAsJsonArray("commits");
        if (commits == null || commits.isEmpty()) {
            builder.append("Changes available on Github. \n");
        } else {
            for (JsonElement commit : commits) {
                JsonObject object = commit.getAsJsonObject();
                builder.append("- ");
                builder.append('[').append(object.get("id").getAsString(), 0, 8).append(']')
                        .append('(').append(object.get("url").getAsString()).append(')');
                builder.append(' ');
                builder.append(object.get("message").getAsString().split("\n")[0]);
                builder.append(" - ");
                builder.append(object.get("author").getAsJsonObject().get("name").getAsString());
                builder.append("  ").append('\n');
            }
        }
        return builder.toString();
    }

}
