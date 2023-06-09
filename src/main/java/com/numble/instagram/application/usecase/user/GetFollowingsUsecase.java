package com.numble.instagram.application.usecase.user;

import com.numble.instagram.domain.follow.entity.Follow;
import com.numble.instagram.domain.follow.service.FollowReadService;
import com.numble.instagram.domain.user.entity.User;
import com.numble.instagram.domain.user.service.UserReadService;
import com.numble.instagram.dto.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GetFollowingsUsecase {

    private final UserReadService userReadService;
    private final FollowReadService followReadService;

    public List<UserResponse> execute(Long userId) {
        User user = userReadService.getUser(userId);
        return followReadService.getFollowingsFollow(user.getId()).stream()
                .map(Follow::getToUser)
                .map(UserResponse::from)
                .sorted(Comparator.comparing(UserResponse::nickname))
                .toList();
    }
}
