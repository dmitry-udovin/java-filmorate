package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class User {

    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();

    private Map<Long, FriendRelation> outgoingRequests = new HashMap<>(); // кому отправлен запрос

    private Map<Long, FriendRelation> incomingRequests = new HashMap<>(); // кто отправил запрос

    public User() {

    }

    public User(final long id, final String email, final String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }


    @Data
    @AllArgsConstructor
    public static class FriendRelation {
        private Long friendId;
        private FriendStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime confirmedAt;

        public boolean isConfirmed() {
            return status == FriendStatus.CONFIRMED;
        }

    }

    public enum FriendStatus {
        UNCONFIRMED, // запрос на дружбу отправлен/получен
        CONFIRMED    // дружба подтверждена
    }

    // отправить заявку в друзья
    public void sendFriendRequest(Long friendId) {
        friends.add(friendId);
        FriendRelation relation = new FriendRelation(
                friendId,
                FriendStatus.UNCONFIRMED,
                LocalDateTime.now(),
                null
        );
        outgoingRequests.put(friendId, relation);
    }

    // принять заявку от другого пользователя
    public void acceptFriendRequest(Long requesterId) {
        FriendRelation incomingRelation = incomingRequests.get(requesterId);

        if (incomingRelation != null && incomingRelation.getStatus() == FriendStatus.UNCONFIRMED) {
            incomingRelation.setStatus(FriendStatus.CONFIRMED);
            incomingRelation.setConfirmedAt(LocalDateTime.now());

            FriendRelation outgoingRelation = new FriendRelation(
                    requesterId,
                    FriendStatus.CONFIRMED,
                    incomingRelation.getCreatedAt(),
                    LocalDateTime.now()
            );
            outgoingRequests.put(requesterId, outgoingRelation);
        } else {
            throw new IllegalStateException("Нет входящей заявки от пользователя " + requesterId);
        }
    }

    // Отклонить входящую заявку
    public void rejectFriendRequest(Long requesterId) {
        FriendRelation relation = incomingRequests.get(requesterId);
        if (relation != null && relation.getStatus() == FriendStatus.UNCONFIRMED) {
            incomingRequests.remove(requesterId);
        }
    }

    // Удалить из друзей (разорвать связь)
    public void removeFriend(Long friendId) {
        outgoingRequests.remove(friendId);
        incomingRequests.remove(friendId);
    }

    // Проверить, является ли пользователь другом (подтвержденным)
    public boolean isFriend(Long userId) {

        FriendRelation outgoing = outgoingRequests.get(userId);
        FriendRelation incoming = incomingRequests.get(userId);

        return (outgoing != null && outgoing.isConfirmed()) ||
                (incoming != null && incoming.isConfirmed());
    }

    // Получить список входящих заявок (ожидающих подтверждения)
    public Set<Long> getPendingIncomingRequests() {
        return incomingRequests.entrySet().stream()
                .filter(entry -> entry.getValue().getStatus() == FriendStatus.UNCONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    // Получить список исходящих заявок (ожидающих подтверждения)
    public Set<Long> getPendingOutgoingRequests() {
        return outgoingRequests.entrySet().stream()
                .filter(entry -> entry.getValue().getStatus() == FriendStatus.UNCONFIRMED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    // Получить всех подтвержденных друзей
    public Set<Long> getAllConfirmedFriends() {
        Set<Long> friends = new HashSet<>();

        // Добавляем подтвержденные исходящие
        outgoingRequests.entrySet().stream()
                .filter(entry -> entry.getValue().isConfirmed())
                .map(Map.Entry::getKey)
                .forEach(friends::add);

        // Добавляем подтвержденные входящие
        incomingRequests.entrySet().stream()
                .filter(entry -> entry.getValue().isConfirmed())
                .map(Map.Entry::getKey)
                .forEach(friends::add);

        return friends;
    }

}
