package org.example.web;

import org.json.JSONObject;

import java.util.function.Function;

public class Login {

    public static Post GetLogin() {
        return new Post(postHandlerLogin);
    }

    public static Post GetSignUp() {
        return new Post(postHandlerSignUp);
    }

    private static final Function<JSONObject, JSONObject> postHandlerLogin = (JSONObject requestJson) -> {
        String username = requestJson.getString("username");
        String password = requestJson.getString("password");

        JSONObject responseJson = new JSONObject();

        if (Users.IsThisUserHere(username)) {
            Users.User user = Users.GetUser(username);
            responseJson.put("loginError", !user.password.equals(password));
            responseJson.put("isAdmin", user.isAdmin);
        } else {
            responseJson.put("loginError", true);
        }

        return responseJson;
    };

    private static final Function<JSONObject, JSONObject> postHandlerSignUp = (JSONObject requestJson) -> {
        String username = requestJson.getString("username");
        String password = requestJson.getString("password");

        JSONObject responseJson = new JSONObject();

        if (Users.IsThisUserHere(username)) {
            responseJson.put("signUpError", true);
        } else {
            responseJson.put("signUpError", false);
            Users.AddUser(new Users.User(username, password));
        }

        return responseJson;
    };
}
