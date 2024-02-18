package com.encore.logeat.like.service;

import static com.encore.logeat.common.redis.CacheNames.POST;

import com.encore.logeat.common.dto.ResponseDto;
import com.encore.logeat.like.Repository.LikeRepository;
import com.encore.logeat.like.domain.Like;
import com.encore.logeat.post.domain.Post;
import com.encore.logeat.post.repository.PostRepository;
import com.encore.logeat.user.domain.User;
import com.encore.logeat.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository, UserRepository userRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    @PreAuthorize("hasAuthority('USER')")
    @CacheEvict(cacheNames = POST, key = "#id")
    public ResponseDto postLike(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("post not found"));

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String[] split = name.split(":");
        long userId = Long.parseLong(split[0]);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user not found"));

        List<Like> likeList = likeRepository.findLikesByUserIdAndPostId(userId, id);
        ResponseDto responseDto;

        if (likeList.isEmpty()) {
            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);
            responseDto = ResponseDto.builder()
                    .httpStatus(HttpStatus.OK)
                    .message(user.getNickname() + " success like it on the post")
                    .result(HttpStatus.OK)
                    .build();
            post.addLikeCount();

        } else {
            Like like = likeList.get(0);
            likeRepository.delete(like);
            responseDto = ResponseDto.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("like delete")
                    .result(HttpStatus.OK)
                    .build();
            post.reduceLikeCount();
        }
        return responseDto;
    }
}
