package com.example.community.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component("jdbcInitData")
public class JdbcInitData implements InitData {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void init() {
        List<Long> memberIds = initMember();
        List<Long> categories = initCategory();
        initBoard(memberIds.get(0), categories.get(0));
        System.out.println("========JdbcInitData=======");
    }


    private List<Long> initMember() {
        String insertMemberSQL = "INSERT INTO member (email, password, name, age) VALUES (?, ?, ?, ?)";

        List<Long> memberIds = new ArrayList<>();
        IntStream.rangeClosed(1, INSERT_MEMBER_COUNT).forEach(n -> {
            KeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(insertMemberSQL, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, "test" + n + "@test.com");
                    statement.setString(2, "testPassword123!@#");
                    statement.setString(3, "test" + n);
                    statement.setString(4, "20");
                    return statement;
                }
            }, holder);
            Long memberId = holder.getKey().longValue();
            memberIds.add(memberId);
        });

        return memberIds;
    }


    private List<Long> initCategory() {
        String insertCategorySQL = "INSERT INTO category (name, depth) VALUES (?, ?)";
        List<Long> categories = new ArrayList<>();

        IntStream.rangeClosed(1, INSERT_MEMBER_COUNT).forEach(n -> {
            KeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(insertCategorySQL, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, "category");
                    statement.setString(2, "0");
                    return statement;
                }
            }, holder);
            Long categoryId = holder.getKey().longValue();
            categories.add(categoryId);
        });


        return categories;
    }

    private void initBoard(Long memberId, Long categoryId) {
        int onceInsertCount = INSERT_BOARD_COUNT / 4;

        StringBuilder sb = new StringBuilder("INSERT INTO board (title, content, member_id, category_id, hit_cnt, like_count) VALUES ");
        IntStream.rangeClosed(1, onceInsertCount).forEach(n -> {
            sb.append(n == onceInsertCount ? "(?, ?, ?, ?, ?, ?); " : "(?, ?, ?, ?, ?, ?), ");
        });
        String insertBoardSQL = sb.toString();

        int paramCnt = 6;
        String memberIdStr = String.valueOf(memberId);
        String categoryIdStr = String.valueOf(categoryId);

        IntStream.rangeClosed(1, INSERT_BOARD_COUNT/onceInsertCount ).forEach(idx -> {
            KeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(insertBoardSQL, Statement.RETURN_GENERATED_KEYS);

                    IntStream.rangeClosed(1, INSERT_BOARD_COUNT).forEach(n -> {
                        int baseIndex = (n - 1) * paramCnt;
                        int number = onceInsertCount * idx + n;
                        try {
                            statement.setString(baseIndex + 1, "title" + number);
                            statement.setString(baseIndex + 2, "content" + number);
                            statement.setString(baseIndex + 3, memberIdStr);
                            statement.setString(baseIndex + 4, categoryIdStr);
                            statement.setString(baseIndex + 5, "0");
                            statement.setString(baseIndex + 6, "0");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    return statement;
                }
            }, holder);
            holder.getKey().longValue();
        });
    }
}
