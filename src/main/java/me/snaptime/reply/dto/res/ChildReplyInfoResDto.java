package me.snaptime.reply.dto.res;

import lombok.Builder;
import me.snaptime.reply.domain.ChildReply;


@Builder
public record ChildReplyInfoResDto(

        String writerLoginId,
        String writerUserName,
        String writerProfilePhotoURL,
        String content,
        String tagUserLoginId,
        String tagUserName,
        Long parentReplyId,
        Long childReplyId,
        String timeAgo
) {

    public static ChildReplyInfoResDto toDto(ChildReply childReply, String profilePhotoURL, String timeAgo){
        if(childReply.getReplyTagUser() == null){
            return ChildReplyInfoResDto.builder()
                    .writerLoginId(childReply.getUser().getLoginId())
                    .writerProfilePhotoURL(profilePhotoURL)
                    .writerUserName(childReply.getUser().getName())
                    .content(childReply.getContent())
                    .parentReplyId(childReply.getParentReply().getParentReplyId())
                    .childReplyId(childReply.getChildReplyId())
                    .timeAgo(timeAgo)
                    .build();
        }
        return ChildReplyInfoResDto.builder()
                .writerLoginId(childReply.getUser().getLoginId())
                .writerProfilePhotoURL(profilePhotoURL)
                .writerUserName(childReply.getUser().getName())
                .content(childReply.getContent())
                .tagUserLoginId(childReply.getReplyTagUser().getLoginId())
                .tagUserName(childReply.getReplyTagUser().getName())
                .parentReplyId(childReply.getParentReply().getParentReplyId())
                .childReplyId(childReply.getChildReplyId())
                .timeAgo(timeAgo)
                .build();
    }
}
