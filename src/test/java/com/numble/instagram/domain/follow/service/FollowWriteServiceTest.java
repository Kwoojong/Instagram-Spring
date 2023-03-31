package com.numble.instagram.domain.follow.service;

import com.numble.instagram.domain.follow.entity.Follow;
import com.numble.instagram.domain.follow.repository.FollowRepository;
import com.numble.instagram.domain.user.entity.User;
import com.numble.instagram.exception.badrequest.AlreadyFollowedUserException;
import com.numble.instagram.util.fixture.follow.FollowFixture;
import com.numble.instagram.util.fixture.user.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowWriteServiceTest {

    @InjectMocks
    private FollowWriteService followWriteService;

    @Mock
    private FollowRepository followRepository;

    @Test
    @DisplayName("팔로우는 완료 되어야 한다. ")
    void followTest() {
        User fromUser = UserFixture.create(1L, "user1");
        User toUser = UserFixture.create(2L, "user2");
        Follow follow = FollowFixture.create(fromUser, toUser);

        when(followRepository.save(any(Follow.class))).thenReturn(follow);

        Follow savedFollow = followWriteService.follow(fromUser, toUser);

        assertEquals(follow, savedFollow);
    }

    @Test
    @DisplayName("이미 팔로우 했으면 발생한다.")
    void follow_AlreadyFollowed() {
        User fromUser = UserFixture.create(1L, "user1");
        User toUser = UserFixture.create(2L, "user2");
        Follow alreadyFollowedFollow = FollowFixture.create(fromUser, toUser);

        when(followRepository.findByFromUserAndToUser(fromUser, toUser)).thenReturn(Optional.of(alreadyFollowedFollow));

        assertThrows(AlreadyFollowedUserException.class, () -> followWriteService.follow(fromUser, toUser));
    }
}