package com.example.instagramclone.domain.post.infrastructure;

import com.example.instagramclone.domain.post.api.LikeStatusResponse;
import com.example.instagramclone.domain.post.api.PostImageResponse;
import com.example.instagramclone.domain.post.api.PostResponse;
import com.example.instagramclone.domain.post.domain.Post;
import com.example.instagramclone.domain.post.domain.PostImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Post 도메인 MapStruct 매퍼.
 *
 * [componentModel = "spring"]
 * 컴파일 시 PostMapperImpl을 생성하고 스프링 Bean으로 등록합니다.
 * → PostService에서 생성자 주입으로 바로 사용 가능
 *
 * [./gradlew compileJava 후 확인할 것]
 * build/generated/sources/annotationProcessor/.../PostMapperImpl.java
 */
@Mapper(componentModel = "spring")
public interface PostMapper {

    /**
     * PostImage → PostImageResponse
     * imgOrder(엔티티) → imageOrder(DTO) 이름이 달라 @Mapping 명시 필요.
     * 나머지 id, imageUrl은 이름이 같으므로 자동 매핑됩니다.
     */
    @Mapping(source = "imgOrder", target = "imageOrder")
    PostImageResponse toImageResponse(PostImage postImage);

    /**
     * List<PostImage> → List<PostImageResponse>
     * 위 toImageResponse()를 참고하여 MapStruct가 구현체를 자동 생성합니다.
     */
    List<PostImageResponse> toImageResponses(List<PostImage> postImages);

    /**
     * Post + List<PostImage> → PostResponse
     *
     * writer.username, writer.profileImageUrl 처럼 중첩 객체 접근이 필요하고,
     * likeStatus·commentCount 같은 고정값도 있어 default 메서드로 직접 조립합니다.
     * toImageResponses()를 재사용하여 이미지 리스트 변환도 위임합니다.
     */
    default PostResponse toResponse(Post post, List<PostImage> images) {
        
        if (post == null) {
            return null;
        }
        
        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getWriter().getUsername(),
                post.getWriter().getProfileImageUrl(),
                toImageResponses(images),
                post.getCreatedAt(),
                LikeStatusResponse.empty(),
                0
        );
    }
}
