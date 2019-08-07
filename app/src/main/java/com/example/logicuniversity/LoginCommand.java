package com.example.logicuniversity;

public class LoginCommand {
    protected AsyncLogin.IServerResponse callback;
    protected String endPt;
    protected User data;

    LoginCommand(AsyncLogin.IServerResponse callback, String endPt, User data) {
        this.callback = callback;
        this.endPt = endPt;
        this.data = data;
    }
}