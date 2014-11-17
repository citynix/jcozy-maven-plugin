package com.citynix.tools.db;

public interface TablesCreator {

    void setDataBaseCredentials(String driver, String url, String username, String password);

    void create();
}
