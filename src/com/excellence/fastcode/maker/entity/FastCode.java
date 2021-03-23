package com.excellence.fastcode.maker.entity;

import java.util.List;

/**
 * @author Slim on 2019/2/22
 */
public class FastCode {

    /**
     * code : 5511
     * servers : [{"server_name":"","server_url":"http://mag.myott.net/c/","server_mac":""}]
     */

    private String code;
    private List<Server> servers;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public static class Server {
        /**
         * server_name :
         * server_url : http://mag.myott.net/c/
         * server_mac :
         * user_name :
         * user_password :
         */

        private String server_name;
        private String server_url;
        private String server_mac;
        private String user_name;
        private String user_password;

        public String getServer_name() {
            return server_name;
        }

        public void setServer_name(String server_name) {
            this.server_name = server_name;
        }

        public String getServer_url() {
            return server_url;
        }

        public void setServer_url(String server_url) {
            this.server_url = server_url;
        }

        public String getServer_mac() {
            return server_mac;
        }

        public void setServer_mac(String server_mac) {
            this.server_mac = server_mac;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_password() {
            return user_password;
        }

        public void setUser_password(String user_password) {
            this.user_password = user_password;
        }

        @Override
        public String toString() {
            return "Server{" +
                    "server_name='" + server_name + '\'' +
                    ", server_url='" + server_url + '\'' +
                    ", server_mac='" + server_mac + '\'' +
                    ", user_name='" + user_name + '\'' +
                    ", user_password='" + user_password + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FastCode{" +
                "code='" + code + '\'' +
                ", servers=" + servers +
                '}';
    }
}