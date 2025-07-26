package com.example.lab1.entities;

import javax.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id = -1;

    @Column(name = "name")
    private String name;

    @Column(name = "wins")
    private int wins;

    public Player() {
    }

    public Player(String name, int wins) {
        this.name = name;
        this.wins = wins;
    }

    public Player(int id, String name, int wins) {
        this.id = id;
        this.name = name;
        this.wins = wins;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", wins=" + wins +
                '}';
    }
}
