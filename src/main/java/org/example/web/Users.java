package org.example.web;

import java.io.*;

public class Users {

    private static final String path = "usuários.urs";

    public static class User {

        public static class Curse {
            public String name;
            public Modulo[] modulos;

            public static class Modulo {
                public String name;
                public float grade;

                public Modulo(String name) {
                    this.name = name;
                    this.grade = -1;
                }

                public Modulo() {
                    this.name = "";
                    this.grade = -1;
                }
            }

            public Curse(String name) {
                this.name = name;
                this.modulos = new Modulo[0];
            }
            public Curse() {
                this.name = "";
                this.modulos = new Modulo[0];
            }
        }

        public String username;
        public String password;
        public boolean isAdmin;

        Curse[] curses;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
            this.isAdmin = false;
            this.curses = new Curse[0];
        }

        public void AddCurse(Curse curse) {
            Curse[] newCurses = new Curse[curses.length + 1];
            System.arraycopy(curses, 0, newCurses, 0, curses.length);
            newCurses[curses.length] = curse;
            curses = newCurses;
        }

        public void RemoveCurse(String curseName) {
            for (Curse curse : curses) {
                if (curse.name.strip().equals(curseName.strip())) {
                    Curse[] newCurses = new Curse[curses.length - 1];
                    int j = 0;
                    for (Curse c : curses) {
                        if (c != curse) {
                            newCurses[j] = c;
                            j++;
                        }
                    }
                    curses = newCurses;
                    return;
                }
            }
        }

        public void Promote() {
            isAdmin = true;
        }

        public void Demote() {
            isAdmin = false;
        }

        public void SetGrade(String curseName, String moduloName, float grade) {
            for (Curse curse : curses) {
                if (curse.name.equals(curseName)) {
                    for (Curse.Modulo modulo : curse.modulos) {
                        if (modulo.name.equals(moduloName)) {
                            modulo.grade = grade;
                            return;
                        }
                    }
                }
            }

            Curse curse = new Curse(curseName);
            Curse.Modulo modulo = new Curse.Modulo(moduloName);
            modulo.grade = grade;
            curse.modulos = new Curse.Modulo[] {modulo};
            AddCurse(curse);
        }

        public float GetGrade(String curseName, String moduloName) {
            for (Curse curse : curses) {
                if (curse.name.equals(curseName)) {
                    for (Curse.Modulo modulo : curse.modulos) {
                        if (modulo.name.equals(moduloName)) {
                            return modulo.grade;
                        }
                    }
                }
            }
            return -1;
        }

        /// First Curse and lest Module
        public String[] GetWorseModule() {
            String[] worstModule = new String[2];
            float worstGrade = 11;
            for (Curse curse : curses) {
                for (Curse.Modulo modulo : curse.modulos) {
                    if (modulo.grade < worstGrade && modulo.grade != -1) {
                        worstGrade = modulo.grade;
                        worstModule[0] = curse.name;
                        worstModule[1] = modulo.name;
                    }
                }
            }

            return worstModule;
        }
    }


    public static User[] users = new User[0];

    public static void AddUser(User user) {
        User[] newUsers = new User[users.length + 1];
        System.arraycopy(users, 0, newUsers, 0, users.length);
        newUsers[users.length] = user;
        users = newUsers;
    }

    public static void RemoveUser(User user) {
        User[] newUsers = new User[users.length - 1];
        int j = 0;
        boolean found = false;
        for (User us : users) {
            if (us != user) {
                newUsers[j] = us;
                j++;
                found = true;
            }
        }
        if (found) {
            users = newUsers;
        }
    }

    public static User GetUser(String username) {
        for (User user : users) {
            if (user.username.strip().equals(username.strip())) {
                return user;
            }
        }
        return null;
    }

    public static boolean CheckUser(String username, String password) {
        for (User user : users) {
            if (user.username.strip().equals(username.strip()) && user.password.equals(password)) {
                return true;
            }
        }
        return false;
    }

    public static boolean IsThisUserHere(String username) {
        for (User user : users) {
            if (user.username.strip().equals(username.strip())) {
                return true;
            }
        }
        return false;
    }

    public static void Save() throws IOException {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeInt(users.length);
            for (User user : users) {
                oos.writeObject(user.username);
                oos.writeObject(user.password);
                oos.writeBoolean(user.isAdmin);
                oos.writeInt(user.curses.length);
                for (User.Curse curse : user.curses) {
                    oos.writeObject(curse.name);
                    oos.writeInt(curse.modulos.length);
                    for (User.Curse.Modulo modulo : curse.modulos) {
                        oos.writeObject(modulo.name);
                        oos.writeFloat(modulo.grade);
                    }
                }
            }
        }
    }

    public static void Load() {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            int usersLength = ois.readInt();
            users = new User[usersLength];
            for (int i = 0; i < usersLength; i++) {
                User user = new User((String) ois.readObject(), (String) ois.readObject());
                user.isAdmin = ois.readBoolean();
                int cursesLength = ois.readInt();
                user.curses = new User.Curse[cursesLength];
                for (int j = 0; j < cursesLength; j++) {
                    User.Curse curse = new User.Curse();
                    curse.name = (String) ois.readObject();
                    int modulosLength = ois.readInt();
                    curse.modulos = new User.Curse.Modulo[modulosLength];
                    for (int k = 0; k < modulosLength; k++) {
                        User.Curse.Modulo modulo = new User.Curse.Modulo();
                        modulo.name = (String) ois.readObject();
                        modulo.grade = ois.readFloat();
                        curse.modulos[k] = modulo;
                    }
                    user.curses[j] = curse;
                }
                users[i] = user;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Testes
    public static void main(String[] args) {

        // save test
        User user = new User("admin", "admin");
        user.isAdmin = true;
        AddUser(user);
        User user2 = new User("user", "user");
        AddUser(user2);
        User user3 = new User("user2", "user2");
        AddUser(user3);

        user2.SetGrade("curso1", "modulo1", 10);
        user2.SetGrade("curso1", "modulo2", 9);
        user2.SetGrade("curso2", "modulo1", 8);
        user3.SetGrade("curso1", "modulo1", 7);
        user3.SetGrade("curso1", "modulo2", 6);

        try {
            Save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // load test
        Load();

        System.out.println(user2.GetGrade("curso1", "modulo1"));
        System.out.println(user2.GetGrade("curso1", "modulo2"));

        for (User u : users) {
            System.out.println(u.username + " " + u.password + " " + u.isAdmin);
        }
    }
}
