package me.snaptime.reply.dto.res;

import lombok.Builder;
import me.snaptime.reply.domain.ChildReply;


@Builder
public record ChildReplyInfoResDto(

        String writerEmail,
        String writerUserName,
        String writerProfilePhotoURL,
        String content,
        String tagUserEmail,
        String tagUserName,
        Long parentReplyId,
        Long childReplyId,
        String timeAgo
) {

    public static ChildReplyInfoResDto toDto(ChildReply childReply, String profilePhotoURL, String timeAgo){
        if(childReply.getReplyTagUser() == null){
            return ChildReplyInfoResDto.builder()
                    .writerEmail(childReply.getUser().getEmail())
                    .writerProfilePhotoURL(profilePhotoURL)
                    .writerUserName(childReply.getUser().getName())
                    .content(childReply.getContent())
                    .parentReplyId(childReply.getParentReply().getParentReplyId())
                    .childReplyId(childReply.getChildReplyId())
                    .timeAgo(timeAgo)
                    .build();
        }
        return ChildReplyInfoResDto.builder()
                .writerEmail(childReply.getUser().getEmail())
                .writerProfilePhotoURL(profilePhotoURL)
                .writerUserName(childReply.getUser().getName())
                .content(childReply.getContent())
                .tagUserEmail(childReply.getReplyTagUser().getEmail())
                .tagUserName(childReply.getReplyTagUser().getName())
                .parentReplyId(childReply.getParentReply().getParentReplyId())
                .childReplyId(childReply.getChildReplyId())
                .timeAgo(timeAgo)
                .build();
    }
}
