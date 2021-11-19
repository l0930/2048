package com.example.a2048.Model;

/**
 * @author lmw
 */
public class User {
    private String account;
    private int score;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public User(String account, int score) {
        this.account = account;
        this.score = score;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", score=" + score +
                '}';
    }
}
