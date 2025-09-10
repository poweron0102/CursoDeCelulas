package org.example.web;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

public class getCurses extends Post {

    private getCurses() {
        super(postHandlerHome);
    }

    public static Post GetCursesHome() {
        return new Post(postHandlerHome);
    }

    public static Post GetCurse() {
        return new Post(postHandlerCurse);
    }

    public static Post GetModule() {
        return new Post(postHandlerModule);
    }

    private static final Function<JSONObject, JSONObject> postHandlerHome = (JSONObject requestJson) -> {
        JSONObject responseJson = new JSONObject();
        System.out.println("Received getCurses request: " + requestJson);
        responseJson.put("status", "ok");
        JSONArray cursos = new JSONArray();

        // read the files in "WebServer/Cursos" and append them to the responseJson
        try {
            File[] files = new File("WebServer/Cursos").listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    System.out.println("Reading file: " + file.getName());
                    CurseParser curseParser = new CurseParser(file);
                    cursos.put(curseParser.GetHomeHtml());
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } finally {
            responseJson.put("cursos", cursos);
            System.out.println("Sending: " + responseJson);
            return responseJson;
        }
    };

    private static final Function<JSONObject, JSONObject> postHandlerCurse = (JSONObject requestJson) -> {
        String curso = requestJson.getString("name");

        JSONObject responseJson = new JSONObject();
        responseJson.put("status", "ok");

        try {
            CurseParser curseParser = new CurseParser(new File("WebServer/Cursos/" + curso + ".html"));
            responseJson.put("curso", curseParser.GetCurseHtml());
            responseJson.put("modulos", curseParser.GetModulesNames());
        } catch (IOException e) {
            System.out.println("Error: " + e);
            throw new RuntimeException(e);
        } finally {
            System.out.println("Sending: " + responseJson);
            return responseJson;
        }
    };

    /// first return Module, then the questions
    private static String[] GetModuleHtml(String modulo, String curso) throws IOException {
        File file = new File("WebServer/Cursos/" + curso + ".html");
        //System.out.println("Reading file: \n" + Files.readString(file.toPath()));
        StringBuilder moduleResult = new StringBuilder();
        StringBuilder questionResult = new StringBuilder();
        ArrayList<String> questions = new ArrayList<>();
        boolean inModule = false;
        boolean inModuleDelay = false;
        boolean inQuestion = false;
        boolean analyzeName = false;
        modulo = modulo.replace("%20", " ");
        System.out.println("Modulo: " + modulo);

        for (String line : Objects.requireNonNull(Files.readString(file.toPath()).split("\n"))) {
            if (inModuleDelay) {
                inModule = true;
                inModuleDelay = false;
            }
            if (analyzeName) {
                if (line.strip().equals(modulo.strip())) {
                    inModuleDelay = true;
                }
                analyzeName = false;
            }
            if (line.contains("<!--@modulo@-->")) {
                inModule = false;
                analyzeName = true;
            }
            if (line.contains("<!--@questao@-->")) {
                if (inQuestion) {
                    inQuestion = false;
                    if (inModule) {
                        String r = questionResult.toString();
                        questionResult = new StringBuilder();
                        if (!r.isEmpty()) {
                            questions.add(r);
                        }
                    }
                }
                else {
                    inQuestion = true;
                }
            }

            System.out.println("inModule: " + inModule + " inQuestion: " + inQuestion + " analyzeName: " + analyzeName + " line: " + line);

            if (inModule && !inQuestion) {
                moduleResult.append(line).append("\n");
            }
            if (inModule && inQuestion) {
                questionResult.append(line).append("\n");
            }
        }

        ArrayList<String> result = new ArrayList<>();
        result.add(moduleResult.toString());
        result.addAll(questions);
        return result.toArray(new String[0]);
    }

    private static final Function<JSONObject, JSONObject> postHandlerModule = (JSONObject requestJson) -> {
        String curso = requestJson.getString("curseName");
        String modulo = requestJson.getString("moduleName");

        System.out.println("Received getModule request: " + requestJson);
        System.out.println("Curso: " + curso + " Modulo: " + modulo);

        JSONObject responseJson = new JSONObject();
        responseJson.put("status", "ok");

        try {
            String[] moduleHtml = GetModuleHtml(modulo, curso);
            responseJson.put("module", moduleHtml[0]);
            JSONArray questions = new JSONArray();
            for (int i = 1; i < moduleHtml.length; i++) {
                questions.put(moduleHtml[i]);
            }
            responseJson.put("questions", questions);
        } catch (IOException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        } finally {
            System.out.println("Sending: " + responseJson);
            return responseJson;
        }
    };
}
