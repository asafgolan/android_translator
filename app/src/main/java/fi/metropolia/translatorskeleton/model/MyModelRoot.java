package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 23.3.16.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author peterh
 */
public class MyModelRoot {
    private UserData data;

    private MyModelRoot() {
        data = new UserData();
    }

    public static MyModelRoot getInstance() {
        return MyModelRootHolder.INSTANCE;
    }

    private static class MyModelRootHolder {

        private static final MyModelRoot INSTANCE = new MyModelRoot();
    }

    public UserData getUserData() {
        return data;
    }

    public void setUserData(UserData u) {
        this.data = u;
    }
}
