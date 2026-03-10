package com.example.instagramclone.domain.post.infrastructure;

import com.example.instagramclone.domain.post.api.PostImageResponse;
import com.example.instagramclone.domain.post.api.PostResponse;
import com.example.instagramclone.domain.post.domain.Post;
import com.example.instagramclone.domain.post.domain.PostImage;

import java.util.List;

// TODO: 1. MapStruct가 이 인터페이스의 구현체를 자동 생성하도록 아래 애노테이션을 추가하세요.
//         @Mapper(componentModel = "spring")
//
//         [componentModel = "spring" 의 의미]
//         생성된 구현체(PostMapperImpl)를 스프링 Bean으로 등록합니다.
//         덕분에 PostService에서 @Autowired / 생성자 주입으로 바로 사용할 수 있습니다.
//
//         [./gradlew compileJava 후 확인할 것]
//         build/generated/sources/annotationProcessor/.../PostMapperImpl.java 가 생성되는지 확인하세요.
//         "여러분이 손으로 짜던 그 매핑 코드를 컴파일러가 대신 짜줍니다!"
public interface PostMapper {

    // TODO: 2. PostImage → PostImageResponse 매핑 메서드를 선언하세요.
    //         필드명이 동일한 것은 @Mapping 없이도 자동으로 매핑됩니다.
    //
    //         [PostImage 필드]   →   [PostImageResponse 필드]
    //          id                →    id
    //          imageUrl          →    imageUrl
    //          imgOrder          →    imageOrder   ←  이름이 다릅니다! @Mapping 필요
    //
    //         힌트:
    //         @Mapping(source = "imgOrder", target = "imageOrder")
    //         PostImageResponse toImageResponse(PostImage postImage);
    PostImageResponse toImageResponse(PostImage postImage);

    // TODO: 3. List<PostImage> → List<PostImageResponse> 매핑 메서드를 선언하세요.
    //         MapStruct는 위 toImageResponse() 메서드를 참고하여 리스트 변환 코드를 자동 생성합니다.
    //         별도 구현 없이 시그니처 선언만으로 동작합니다!
    //
    //         힌트: List<PostImageResponse> toImageResponses(List<PostImage> postImages);
    List<PostImageResponse> toImageResponses(List<PostImage> postImages);

    // TODO: 4. Post + List<PostImage> → PostResponse 변환 메서드를 작성하세요.
    //
    //         [필드 매핑 분석]
    //         Post 필드              →  PostResponse 필드
    //          id                   →  id             (동일, 자동 매핑)
    //          content              →  content        (동일, 자동 매핑)
    //          writer.username      →  username       ← ⚠️ 중첩 객체! @Mapping 필요
    //          writer.profileImageUrl → profileImageUrl ← ⚠️ 중첩 객체! @Mapping 필요
    //          createdAt            →  createdAt      (동일, 자동 매핑)
    //          -                    →  likeStatus     ← ⚠️ 고정값! expression 또는 @AfterMapping 필요
    //          -                    →  commentCount   ← ⚠️ 고정값(0)! 동일
    //          -                    →  images         ← ⚠️ 별도 파라미터! @Mapping 필요
    //
    //         [접근 방법 - default 메서드 활용]
    //         MapStruct에서 복잡한 매핑은 인터페이스의 default 메서드로 직접 구현할 수 있습니다.
    //
    //         default PostResponse toResponse(Post post, List<PostImage> images) {
    //             return new PostResponse(
    //                 post.getId(),
    //                 post.getContent(),
    //                 post.getWriter().getUsername(),
    //                 post.getWriter().getProfileImageUrl(),
    //                 toImageResponses(images),  ← TODO 3 에서 선언한 메서드 재사용!
    //                 post.getCreatedAt(),
    //                 LikeStatusResponse.empty(),
    //                 0
    //             );
    //         }
    //
    //         [PostService 리팩토링 후 Before / After 비교]
    //         Before: return PostResponse.of(post, postImages);   // PostResponse에 직접 매핑 로직
    //         After:  return postMapper.toResponse(post, postImages); // 매퍼에 위임!

    // TODO: 위 가이드를 참고하여 default 메서드로 구현하세요.
    default PostResponse toResponse(Post post, List<PostImage> images) {
        // TODO: 구현하세요.
        return null;
    }
}
