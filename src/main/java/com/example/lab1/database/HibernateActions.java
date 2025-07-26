package com.example.lab1.database;

import com.example.lab1.entities.Player;
import com.example.lab1.model.PlayerGameInfo;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.stream.Collectors;

public class HibernateActions {
    public static List<Player> getPlayers() {
        Session session = HibernateSessionFactoryUtil
                .getSessionFactory().openSession();
        List<Player> players = session.createQuery("From Player", Player.class).list();
        session.close();
        return players;
    }

    public static void addNotExistingUsersToDB(List<PlayerGameInfo> players) {
        if (players == null || players.isEmpty()) {
            return;
        }
        List<String> playerNames = players.stream()
                .map(PlayerGameInfo::getName)
                .collect(Collectors.toList());
        Transaction transaction = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            List<String> existingNames;
            try {
                transaction = session.beginTransaction();
                existingNames = session.createQuery(
                                "SELECT p.name FROM Player p WHERE p.name IN :names", String.class)
                        .setParameter("names", playerNames)
                        .getResultList();
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                throw e;
            }
            List<Player> newPlayers = players.stream()
                    .filter(p -> !existingNames.contains(p.getName()))
                    .map(p -> new Player(p.getName(), 0))
                    .toList();
            if (newPlayers.isEmpty()) {
                return;
            }
            transaction = session.beginTransaction();
            for (Player player : newPlayers) {
                session.persist(player);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error adding players: " + e.getMessage());
        }
    }


    public static void updateDB(String name) {
        if (name == null || name.isEmpty()) {
            System.err.println("Player name cannot be null or empty");
            return;
        }
        Transaction transaction = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Player player = session.createQuery("FROM Player WHERE name = :name", Player.class)
                    .setParameter("name", name)
                    .uniqueResult();
            if (player != null) {
                player.setWins(player.getWins() + 1);
                session.update(player);
            } else {
                System.err.println("Player not found: " + name);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Failed to update player wins: " + e.getMessage());
        }
    }
}