package kyulab.fileservice.repository;

import kyulab.fileservice.entity.GroupImg;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupImgRepository extends ReactiveMongoRepository<GroupImg, String> {
}
