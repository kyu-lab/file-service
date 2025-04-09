package kyulab.fileservice.repository;

import kyulab.fileservice.entity.PostImg;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImgRepository extends ReactiveMongoRepository<PostImg, String> {
}
