package com.hckst.respal.comment.domain.repository.converter;

import com.hckst.respal.comment.domain.Comment;
import com.hckst.respal.members.domain.Members;
import io.r2dbc.spi.Row;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import org.springframework.core.convert.converter.Converter;

public class CommentReadConverter implements Converter<Row, Comment> {

    @Override
    public Comment convert(Row source) {

        Members members = Members.convertByQuery(
                (Long) source.get("membersId")
                , (String) source.get("membersNickName")
                , (String) source.get("membersEmail")
                , (String) source.get("membersPicture"));
        return Comment.convertByQuery(
                (Long) source.get("id")
                , (String) source.get("content")
                , (int) source.get("xLocation")
                , (int) source.get("yLocation")
                , members
                , (String) source.get("deleteYn")
                , (ZonedDateTime) source.get("regTime"));
    }
}
