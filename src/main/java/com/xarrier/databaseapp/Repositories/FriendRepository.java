package com.xarrier.databaseapp.Repositories;

import com.xarrier.databaseapp.Entities.Friend;
import com.xarrier.databaseapp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findByUser(User user);

    boolean existsByUserAndFriend(User user, User friend);
}
