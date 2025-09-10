package org.example.web;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.function.Function;

public class UsersPage {

    public static Post GetUserModulePontuation() {
        return new Post(postHandlerUserModulePontuation);
    }

    public static Post SetUserModulePontuation() {
        return new Post(postHandlerSetUserModulePontuation);
    }

    public static Post RemoveCurseFromUser() {
        return new Post(postHandlerRemoveCurseFromUser);
    }


    private static final Function<JSONObject, JSONObject> postHandlerUserModulePontuation = (JSONObject requestJson) -> {
        String username = requestJson.getString("username");
        String curseName = requestJson.getString("curseName");

        JSONArray modulePontuation = new JSONArray();

        Users.User user = Users.GetUser(username);
        for (Users.User.Curse curse : user.curses) {
            if (curse.name.equals(curseName)) {
                for (Users.User.Curse.Modulo modulo : curse.modulos) {
                    JSONObject obj = new JSONObject();
                    obj.put("name", modulo.name);
                    obj.put("grade", modulo.grade);
                    modulePontuation.put(obj);
                }
            }
        }

        JSONObject resp = new JSONObject();
        resp.put("success", true);
        resp.put("modulePontuation", modulePontuation);
        return resp;
    };


    private static final Function<JSONObject, JSONObject> postHandlerSetUserModulePontuation = (JSONObject requestJson) -> {
        String username = requestJson.getString("username");
        String curseName = requestJson.getString("curseName").replace("%20", " ");
        String moduleName = requestJson.getString("moduleName").replace("%20", " ");
        float grade = requestJson.getFloat("grade");

        Users.GetUser(username).SetGrade(curseName, moduleName, grade);

        JSONObject resp = new JSONObject();
        resp.put("success", true);
        return resp;
    };


    private static final Function<JSONObject, JSONObject> postHandlerRemoveCurseFromUser = (JSONObject requestJson) -> {
        String username = requestJson.getString("username");
        String curseName = requestJson.getString("curseName").replace("%20", " ");

        Users.User user = Users.GetUser(username);

        System.out.println("User: " + user);
        System.out.println("CurseName: " + curseName);
        System.out.println("username: " + username);

        if (user != null) {
            user.RemoveCurse(curseName);
        }
        else {
            JSONObject resp = new JSONObject();
            resp.put("success", false);
            return resp;
        }

        JSONObject resp = new JSONObject();
        resp.put("success", true);
        System.out.println("Success removing curse.");
        return resp;
    };
}
