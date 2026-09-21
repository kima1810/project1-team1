package org.half.model;

import com.williamcallahan.tui4j.compat.bubbletea.Message;

public record RouteModel(Route destination, Object payload) implements Message {
    public enum Route {
        WELCOME,
        SIGN_IN,
        REGISTER,
        ACCOUNT_SELECTION,
        ACCOUNT_CREATION,
        MAIN_MENU,
        EXIT
    }
}