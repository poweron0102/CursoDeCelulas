package org.example.web;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

public class Admin {

    public static Post GetUsersList() {
        return new Post(postHandlerUsersList);
    }

    public static Post RemoveUser() {
        return new Post(postHandlerRemoveUser);
    }

    public static Post PromoteUser() {
        return new Post(postHandlerPromoteUser);
    }

    public static Post DemoteUser() {
        return new Post(postHandlerDemoteUser);
    }

    public static Post AddCourse() {
        return new Post(postHandlerAddCourse);
    }


    private static final Function<JSONObject, JSONObject> postHandlerUsersList = (JSONObject requestJson) -> {
        JSONArray users = new JSONArray();
        for (Users.User user : Users.users) {
            JSONObject userJson = new JSONObject();
            userJson.put("username", user.username);
            userJson.put("isAdmin", user.isAdmin);
            users.put(userJson);
        }
        JSONObject resp = new JSONObject();
        resp.put("success", true);
        resp.put("users", users);
        return resp;
    };

    private static final Function<JSONObject, JSONObject> postHandlerRemoveUser = (JSONObject requestJson) -> {
        String username = requestJson.getString("username");
        System.out.println("Removing user: " + username);
        Users.RemoveUser(Users.GetUser(username));

        return new JSONObject("{\"success\": true}");
    };

    private static final Function<JSONObject, JSONObject> postHandlerPromoteUser = (JSONObject requestJson) -> {
        String username = requestJson.getString("username");
        System.out.println("Promoting user: " + username);

        if (Users.IsThisUserHere(username)) {
            Users.GetUser(username).Promote();
        }

        return new JSONObject("{\"success\": true}");
    };

    private static final Function<JSONObject, JSONObject> postHandlerDemoteUser = (JSONObject requestJson) -> {
        String username = requestJson.getString("username");
        System.out.println("Demoting user: " + username);

        if (Users.IsThisUserHere(username)) {
            Users.GetUser(username).Demote();
        }

        return new JSONObject("{\"success\": true}");
    };

    private static final Function<JSONObject, JSONObject> postHandlerAddCourse = (JSONObject requestJson) -> {
        String fileName = requestJson.getString("fileName");
        String fileContent = requestJson.getString("fileContent");

        File file = new File("WebServer/Cursos/" + fileName);
        System.out.println("Adding course: " + fileName + "\nPath: " + file.getAbsolutePath());
        System.out.println("Content: " + fileContent);
        if (file.exists()) {
            System.out.println("File already exists");
            return new JSONObject("{\"success\": false}");
        }

        try {
            // Write content to file
            for (String line : fileContent.split("\n")) {
                try (FileWriter writer = new FileWriter(file, true)) {
                    writer.write(line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONObject("{\"success\": false}");
        }

        return new JSONObject("{\"success\": true}");
    };
}
