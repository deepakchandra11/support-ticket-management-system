package com.example.tickets.web.dto;

import com.example.tickets.domain.Comment;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String body,
        String author,
        Instant createdAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getBody(),
                comment.getAuthor(),
                comment.getCreatedAt()
        );
    }
}
