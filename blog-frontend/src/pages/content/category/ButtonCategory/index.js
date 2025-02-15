import axios from "axios";
import { useEffect, useState } from "react";
import { Container,Col,Row,Card} from "react-bootstrap";
import { useParams ,Link} from "react-router-dom";
import routes from "~/config";
import classNames from "classnames/bind";
import style from "./cateDel.module.scss"

const cx=classNames.bind(style);




function CategoryDetails() {

    const [data,setData]=useState([]);
    const [categories, setCategories] = useState([]); 
    const { categoryId } = useParams(); // Lấy categoryName từ URL


    useEffect(() => {
        axios
          .get(`https://api.winde.site/api/posts/category/${categoryId}`)
          .then(async (response) => {
            const CategoryDetails = response.data;
      
            // Lấy thêm thông tin chi tiết cho mỗi bài đăng
            const postsWithDetails = await Promise.all(
              CategoryDetails.map(async (post) => {
                const postId = post.post_id; // Lấy postId từ bài đăng
                let username = "Anonymous";
                let images = [];
      
                // Bước 2: Lấy thông tin bài đăng từ postId
                try {
                  const postResponse = await axios.get(
                    `https://api.winde.site/api/posts/${postId}`
                  );
                  const postData = postResponse.data;
                  // console.log(postData)

                  // Bước 3: Lấy thông tin user từ userId trong post
                  try {
                    const userResponse = await axios.get(
                      `https://api.winde.site/api/user/${postData.userId}`

                    );
                    username = userResponse.data.username || "Anonymous";
                    // console.log(postData); // Xem cấu trúc của postData
                    // console.log(postData.userId); // Kiểm tra xem userId có đúng không
                    

                  } catch (error) {
                    console.error("Error fetching user data:", error);
                  }

      
                  // Bước 4: Lấy ảnh của bài đăng
                  try {
                    const imagesResponse = await axios.get(`https://api.winde.site/api/image/post/${postId}`);
                    images = imagesResponse.data || [];
                  } catch (error) {
                    if (error.response?.status !== 404) {
                      console.error('Lỗi khác:', error);
                    }
                  }
                  
      
                  // Trả về bài đăng với tất cả thông tin
                  return {
                    ...postData,
                    username,
                    images,
                  };
                } catch (error) {
                  console.error("Error fetching post data:", error);
                  return { ...post, username, images }; // Nếu có lỗi, trả về thông tin mặc định
                }
              })
            );
      
            setData(postsWithDetails); // Cập nhật dữ liệu vào state
          })
          .catch((error) => {
            console.error("Error fetching category data:", error);
          });
      }, [categoryId]); // Trigger khi categoryName thay đổi

      useEffect(()=>{
        axios.get(
          `https://api.winde.site/api/category/${categoryId}`
        ).then((res)=>{
          setCategories(res.data)
          // console.log(res.data)
        })
      },[categoryId])
   
    return (
      <div className={cx("p-5")}>
        <Container>
          {/* {categories.map((cate) => (
            <h1 key={cate.category_id} className={cx("title-cate", "py-5")}>
              {cate.categoryName}
            </h1>
          ))} */}
          <Link className={cx("btn-return")} as={Link} to={routes.home} >Trở lại</Link>
          <h1 className={cx("title-cate",)}>
              {categories.categoryName}
          </h1>
            <Row className="g-4">
                {/* Kiểm tra nếu có dữ liệu bài viết */}
                {data.length > 0 ? (
                    data.map((post) => (
                        <Col key={post.post_id} xl={3} lg={4} md={6} sm={12}>
                            <Link to={routes.postDetail.replace(':id', post.post_id)}>
                                <Card className={cx("custom-card")}>
                                    {post.images && post.images.length > 0 ? (
                                        <Card.Img
                                            className={cx("img-content")}
                                            src={`https://api.winde.site/api/image/post/${post.post_id}`} // Sử dụng ảnh đầu tiên
                                            alt={post.title}
                                        />
                                        ) : (
                                        <Card.Img
                                            className={cx("img-content")}
                                            src="https://media.istockphoto.com/id/1224500457/vi/anh/n%E1%BB%81n-t%E1%BA%A3ng-c%C3%B4ng-ngh%E1%BB%87-tr%E1%BB%ABu-t%C6%B0%E1%BB%A3ng-m%C3%A3-l%E1%BA%ADp-tr%C3%ACnh-c%E1%BB%A7a-nh%C3%A0-ph%C3%A1t-tri%E1%BB%83n-ph%E1%BA%A7n-m%E1%BB%81m-v%C3%A0-k%E1%BB%8Bch-b%E1%BA%A3n-m%C3%A1y-t%C3%ADnh.jpg?s=612x612&w=0&k=20&c=492Izyb2fyCZfeBOiFxUnxeoMTOH8STWSFa9NJ2WWns="
                                            alt="blog"
                                        />
                                        )}

                                    <Card.Body>
                                        <Card.Title className={cx("title-content")}>{post.title}</Card.Title>
                                        <div className={cx("d-flex justify-content-between")}>
                                            <Card.Text className={cx("text-secondary")}>
                                            {post.username}
                                            </Card.Text>
                                            <Card.Text className={cx("text-secondary")}>{post.created_at}</Card.Text>
                                        </div>
                                    </Card.Body>
                                </Card>
                            </Link>
                        </Col>
                    ))
                ) : (
                    <p>Không có bài viết hãy là người tạo bài viết đầu tiên.</p>
                )}
            </Row>
        </Container>
      </div>


    );
}

export default CategoryDetails;