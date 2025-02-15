package com.example.test.respositories;

import com.example.test.models.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostRespo extends JpaRepository<Posts, Long> {
    // Tìm kiếm bài viết có tiêu đề chứa từ khóa 'title', không phân biệt chữ hoa chữ thường
    List<Posts> findByTitleContainingIgnoreCaseAndStatus(String title,Posts.PostStatus status);//viet dung de tim kiem

    //dung de duyet bai viet
    List<Posts>findByStatus(Posts.PostStatus status);


    List<Posts> findByCategoryIdAndStatus(Long categoryId, Posts.PostStatus status);

    List<Posts>findByUserId(Long UserId);


    @Transactional  //Đảm bảo rằng thao tác được thực hiện trong một giao dịch.
    @Modifying //Chỉ định rằng đây là một truy vấn sửa đổi dữ liệu (DELETE/UPDATE).
    @Query("DELETE FROM Posts c WHERE c.post_id = :categoryId") //Sử dụng JPQL để thực hiện xóa tất cả các Image có postId tương ứng.
    void deleteByCategoryId(Long categoryId);

}

