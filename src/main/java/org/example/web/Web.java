package org.example.web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Web {
    public static HttpServer SERVER;

    public static void main(String[] args) throws Exception {
        Users.Load();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Users.Save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 7689;
        SERVER = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        SERVER.createContext("/", new Page("WebServer/home.html"));
        SERVER.createContext("/common.css", new Page("WebServer/common.css"));
        SERVER.createContext("/common.js", new Page("WebServer/common.js"));

        SERVER.createContext("/home.js", new Page("WebServer/home.js"));
        SERVER.createContext("/curso.js", new Page("WebServer/curso.js"));
        SERVER.createContext("/modulo.js", new Page("WebServer/modulo.js"));
        SERVER.createContext("/admin.js", new Page("WebServer/admin.js"));


        //SERVER.createContext("/curse/", new Page("WebServer/curse(old).html"));

        SERVER.createContext("/getCursesHome", getCurses.GetCursesHome());
        SERVER.createContext("/getCurse", getCurses.GetCurse());
        SERVER.createContext("/getModule", getCurses.GetModule());

        SERVER.createContext("/login", Login.GetLogin());
        SERVER.createContext("/signUp", Login.GetSignUp());

        // Admin
        SERVER.createContext("/admin.html", new Page("WebServer/admin.html"));
        SERVER.createContext("/getUsers", Admin.GetUsersList());
        SERVER.createContext("/removeUser", Admin.RemoveUser());
        SERVER.createContext("/promoteUser", Admin.PromoteUser());
        SERVER.createContext("/demoteUser", Admin.DemoteUser());
        SERVER.createContext("/addCourse", Admin.AddCourse());

        // Users
        SERVER.createContext("/GetUserModulePontuation", UsersPage.GetUserModulePontuation());
        SERVER.createContext("/SetUserModulePontuation", UsersPage.SetUserModulePontuation());
        SERVER.createContext("/RemoveCurseFromUser", UsersPage.RemoveCurseFromUser());


        SERVER.setExecutor(null); // creates a default executor
        SERVER.start();

        System.out.println("Server started on port " + port);
    }
}
