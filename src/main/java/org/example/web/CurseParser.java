package org.example.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CurseParser {
    public String name;
    private String[] file;

    public CurseParser(File file) throws IOException {
        this.file = Files.readString(file.toPath()).split("\n");
        this.name = file.getName();
    }

    public String GetHomeHtml() {
        StringBuilder result = new StringBuilder();
        boolean shouldAdd = false;
        for (String line : file) {
            System.out.println("Should add: " + shouldAdd + " line: " + line);
            if (line.contains("<!--@home@-->")) {
                shouldAdd = !shouldAdd;
            }
            if (shouldAdd) {
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }

    public String GetCurseHtml() {
        StringBuilder result = new StringBuilder();
        boolean shouldAdd = false;
        for (String line : file) {
            if (line.contains("<!--@curso@-->")) {
                shouldAdd = !shouldAdd;
            }
            if (shouldAdd) {
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }

    public String[] GetModulesHtml() {
        StringBuilder module = new StringBuilder();
        ArrayList<String> result = new ArrayList<>();
        boolean shouldAdd = false;
        boolean skip = false;

        for (String line : file) {
            if (skip) {
                skip = false;
                continue;
            }
            if (line.contains("<!--@modulo@-->")) {
                shouldAdd = !shouldAdd;
                if (!shouldAdd) {
                    result.add(module.toString());
                    module = new StringBuilder();
                }
                else {
                    skip = true; // skip the next line because it is the module name
                }
            }
            if (shouldAdd) {
                module.append(line).append("\n");
            }
        }

        return result.toArray(new String[0]);
    }

    public static String[] GetQuestionsHtml(String module) {
        StringBuilder question = new StringBuilder();
        ArrayList<String> result = new ArrayList<>();
        boolean shouldAdd = false;

        for (String line : module.split("\n")) {
            if (line.contains("<!--@questao@-->")) {
                shouldAdd = !shouldAdd;
                if (!shouldAdd) {
                    result.add(question.toString());
                    question = new StringBuilder();
                }
            }
            if (shouldAdd) {
                question.append(line).append("\n");
            }
        }

        return result.toArray(new String[0]);
    }

    public String[] GetModulesNames() {
        List<String> result = new ArrayList<>();
        boolean shouldAdd = false;
        int cont = 0;

        for (String line : file) {
            if (shouldAdd) {
                if (cont % 2 == 1) {
                    result.add(line);
                }
                shouldAdd = false;
            }
            if (line.contains("<!--@modulo@-->")) {
                shouldAdd = true;
                cont++;
            }
        }

        return result.toArray(new String[0]);
    }
}
